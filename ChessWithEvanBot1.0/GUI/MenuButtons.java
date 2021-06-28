package GUI;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import javax.swing.JOptionPane;

import ChessMain.ChessCoordinator;
import AI.*;
import AI.BoardEvaluators.*;

public class MenuButtons extends JPanel {

    // internal variables
    protected ChessCoordinator parent;
    protected JPanel undoredoMoveButtons = new JPanel();
    protected JButton undoMoveButton = new JButton();
    protected JButton redoMoveButton = new JButton();
    protected JButton flipBoard = new JButton();
    protected JButton setBoardEvaluator = new JButton();
    protected JButton setAI = new JButton();
    protected JButton loadFromFEN = new JButton();
    protected JButton setSquareColor = new JButton();

    protected JLabel isCheck = new JLabel("CHECK");

    public MenuButtons(ChessCoordinator cc) {
        parent = cc;
        setBounds(600, 200, 300, 800);
        setLayout(new FlowLayout());

        // Adding JPanels and JButtons
        add(undoredoMoveButtons);

        undoredoMoveButtons.add(undoMoveButton);
        undoMoveButton.setIcon(new ImageIcon("GUI/GenericIcons/undoMove.jpg"));
        undoMoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                undoMove();
            }
        });
        undoredoMoveButtons.add(redoMoveButton);
        redoMoveButton.setIcon(new ImageIcon("GUI/GenericIcons/redoMove.jpg"));
        redoMoveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                redoMove();
            }
        });

        add(flipBoard);
        flipBoard.setIcon(new ImageIcon("GUI/GenericIcons/flipBoard.png"));
        flipBoard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                flipBoard();
            }
        });

        add(setAI);
        setAI.setIcon(new ImageIcon("GUI/GenericIcons/setAI.png"));
        setAI.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setAI();
            }
        });

        add(setBoardEvaluator);
        setBoardEvaluator.setIcon(new ImageIcon("GUI/GenericIcons/setBoardEvaluator.png"));
        setBoardEvaluator.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setBoardEvaluator();
            }
        });

        add(loadFromFEN);
        loadFromFEN.setIcon(new ImageIcon("GUI/GenericIcons/loadFromFEN.png"));
        loadFromFEN.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadFromFEN();
            }
        });

        add(setSquareColor);
        setSquareColor.setIcon(new ImageIcon("GUI/GenericIcons/setSquareColor.png"));
        setSquareColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setSquareColor();
            }
        });

    }

    protected boolean undoMove() {
        if (parent.reverseMove())
            return true;
        else
            return false;
    }

    protected boolean redoMove() {
        if (parent.redoMove())
            return true;
        else
            return false;
    }

    protected void flipBoard() {
        parent.flipBoard();
    }

    public void setBoardEvaluator() {
        String[] possibleBE = { "Simple Evaluator", "Improved Evaluator" };
        String choice = (String) JOptionPane.showInputDialog(null,
                "Which board evaluator would you like the computer to use?", "", JOptionPane.CLOSED_OPTION, null,
                possibleBE, possibleBE[0]);

        if (choice == "Simple Evaluator")
            parent.setBoardEvaluator(new SimpleEvaluator());
        if (choice == "Improved Evaluator")
            parent.setBoardEvaluator(new ImprovedEvaluator());
    }

    public void setAI() {
        ;
        String[] possibleAI = { "Alpha Beta Tree Search", "Simple Tree Search", "PVP", "Random Mover" };
        String choice = (String) JOptionPane.showInputDialog(null,
                "Which move search engine would you like the computer to use?", "", JOptionPane.CLOSED_OPTION, null,
                possibleAI, possibleAI[0]);

        if (choice == "Alpha Beta Tree Search")
            parent.setAi(new AlphaBetaTreeSearch(parent));
        else if (choice == "Simple Tree Search")
            parent.setAi(new SimpleTreeSearch(parent));
        else if (choice == "PVP")
            parent.setAi(new PVP(parent));
        else if (choice == "Random Mover")
            parent.setAi(new RandomMover(parent));
    }

    public void setSquareColor() {
        try{
            String userInput = JOptionPane.showInputDialog("Enter square colors in the following manner: e.g.,000-000-000,000-000-000 or 256-234-3,34-54-60. numbers from left to right indicate RGB value of Light, Dark squares respectively.");
            String[] colors = userInput.split(",");
            String[] RGBV1 = colors[0].split("-");
            String[] RGBV2 = colors[1].split("-");
            int[] RGB = new int[6]; int k=0;
            for(String s:RGBV1) RGB[k++]=Integer.parseInt(s);
            for(String s:RGBV2) RGB[k++]=Integer.parseInt(s);
            Color c1 = new Color(RGB[0],RGB[1],RGB[2]);
            Color c2 = new Color (RGB[3],RGB[4],RGB[5]);
            parent.setColorLightSquares(c1);
            parent.setColorDarkSquares(c2); 
        }
        catch(Exception e){
            JOptionPane.showMessageDialog(null, "The color you entered was either an impossible RGB Value, or was formatted incorrectly. ", "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }

    public void loadFromFEN() {
        String FEN = JOptionPane.showInputDialog("Enter FEN");
        parent.setInternalBoardsFromFEN(FEN);
    }
}
