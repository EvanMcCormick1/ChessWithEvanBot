package GUI;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ChessMain.ChessCoordinator;

public class ChessSquare extends JPanel
{
    protected int position;
    protected int isWhite;
    protected int size;
    protected int occupyingPiece;

    protected Color color;
    protected ImageIcon[] imageIcons;
    protected JLabel occupyingPieceImage;
    protected MouseListener userInputListener;

    ChessSquare(int pos, int size, int iw, int piece, ImageIcon[] ii, ChessCoordinator cc, DisplayBoard db)
    {
        //initialize internal variables.
        imageIcons=ii;
        position = pos;
        isWhite = iw;
        color = new Color((50+200*isWhite),(10+240*isWhite),(100+50*isWhite));
        userInputListener = new MouseListener(cc, db);
        addMouseListener(userInputListener);
        
        //set visual parameters
        setLayout(null);
        setBounds(0,0,size,size);
        setBackground(color);
        setPiece(piece);

    }    

    //Square Updators
    protected void setPiece(int p)
    {
        occupyingPiece = p;
        ImageIcon piece = imageIcons[p];
        if(occupyingPieceImage != null) remove(occupyingPieceImage);
        occupyingPieceImage = new JLabel(piece, JLabel.CENTER);
        occupyingPieceImage.setBounds(size/2, size/2, piece.getIconWidth(), piece.getIconHeight());
        add(occupyingPieceImage);
        repaint(); revalidate();                                                                                                                                                                                                                                                                                                               
    }

    protected void highlight() {setBackground(new Color(250,150,150)); repaint();}
    protected void unhighlight(){setBackground(color); repaint();}
    protected void setColor(Color c) {color=c; setBackground(color); repaint();}

    private class MouseListener extends MouseAdapter
    {
        private DisplayBoard parent;
        private ChessCoordinator grandParent;

        public MouseListener(ChessCoordinator cc, DisplayBoard db){
            grandParent = cc;
            parent = db;
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            int moveStart = parent.getMoveStart();
            if(moveStart>-1 && grandParent.makeOfficialMove(moveStart,position)) //makeOfficialMove actually makes the move, and returns false if such a move fails.
            {
                parent.setMoveStart(-1);
            }
            else if (occupyingPiece>0 && (occupyingPiece-1)/6==(grandParent.getActivePlayer()))
            {
                parent.setMoveStart(position);
            };
            
        }
        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (occupyingPiece>0 && (occupyingPiece-1)/6==(grandParent.getActivePlayer()))
            {
                parent.unhighlightSquares();
                if(occupyingPiece>0) grandParent.highlightLegalMoves(position);
            }
        }

    }

}

