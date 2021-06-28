package GUI;

import java.awt.GridLayout;
import java.awt.Color;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import ChessMain.ChessCoordinator;

public class DisplayBoard extends JPanel {
    private String[] pieceImageNames = { "000", "BB", "BK", "BN", "BP", "BQ", "BR", "WB", "WK", "WN", "WP", "WQ",
            "WR" };
    /*
     * 0->Empty 1->Black Bishop 2->Black King 3->Black Knight 4->Black Pawn 5->Black
     * Queen 6->Black Rook 7->White Bishop 8->White King 9->White Knight 10->White
     * Pawn 11->White Queen 12->White Rook
     */
    private ImageIcon[] pieceImageIcons = new ImageIcon[13];
    private ChessSquare[] chessSquares = new ChessSquare[64];

    private int roundedSize; // Size of Board
    private int moveStart = -1; // Keeps track of last chessSquare clicked with a friendly piece in it, for
                                // movement. Click start square -> Click destination square = make move.
    private boolean isFlipped = false; // Keeps track of board orientation. false=White's View, true = Black's View.

    // CONSTRUCTOR
    public DisplayBoard(int size, ChessCoordinator cc) {
        // Set-Up
        loadPieceImageIcons();
        roundedSize = (size / 8) * 8;
        setSquares(cc);
        setBounds(0, 0, roundedSize, roundedSize);
        setLayout(new GridLayout(8, 8));
        flipBoard();
    }

    // SET-UP FUNCTIONS
    protected void loadPieceImageIcons() {
        int i = 0;
        for (String s : pieceImageNames)
            pieceImageIcons[i++] = new ImageIcon("GUI/PieceImages/" + s + ".png");
    }

    protected void setSquares(ChessCoordinator cc) {
        for (int i = 0; i < chessSquares.length; i++) {
            chessSquares[i] = new ChessSquare(i, roundedSize / 8, ((i % 2 + ((i / 8) % 2)) % 2), 0, pieceImageIcons, cc,
                    this);
            add(chessSquares[i]);
        }
    }

    // SIMPLE UPDATING FUNCTIONS
    public void setSquare(int i, int t) {
        chessSquares[i].setPiece(t);
    }

    public void setMoveStart(int moveStart) {
        this.moveStart = moveStart;
    }

    public void setColorLightSquares(Color c) {
        for(int i=0;i<64;i++){
            if(((i % 2 + ((i / 8) % 2)) % 2)==1) chessSquares[i].setColor(c);
        }
    }

    public void setColorDarkSquares(Color c) {
        for(int i=0;i<64;i++){
            if(((i % 2 + ((i / 8) % 2)) % 2)==0) chessSquares[i].setColor(c);
        }
    }

    // COMPLEX UPDATING FUNCTIONS
    public void highlightSquares(int[] arr) {
        for (int i : arr)
            chessSquares[i].highlight();
    }

    public void unhighlightSquares() {
        for (int i = 0; i < 64; i++)
            chessSquares[i].unhighlight();
    }

    public void flipBoard() {
        if (isFlipped) {
            for (int i = 63; i >= 0; i--)
                remove(chessSquares[i]);
            for (int i = 0; i < 64; i++)
                add(chessSquares[i]);
        } else {
            for (int i = 0; i < 64; i++)
                remove(chessSquares[i]);
            for (int i = 63; i >= 0; i--)
                add(chessSquares[i]);
        }
        repaint();
        revalidate();
        isFlipped = !isFlipped;
    }

    // SIMPLE RETURN FUNCTIONS
    public int getMoveStart() {
        return moveStart;
    }

    // COMPLEX RETURN FUNCTIONS

}
