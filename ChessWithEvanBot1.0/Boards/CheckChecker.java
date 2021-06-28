package Boards;

import Boards.Pieces.ChessPiece;
import Moves.Move;

public class CheckChecker {
    
    // Internal Arrays
    private int[] threatenedSquares = new int[64]; // Keeps track of squares threatened by opponent.
    private int[] checkLines = new int[64]; // Keeps track of lines of check/pin lines.
    private int[] kingIndices = new int[2]; // Keeps track of indices of friendly and enemy kings.

    private int enemy;
    private int friendly;
    private int numPinLines;
    private int numCheckLines;

    public CheckChecker(int[] b, ChessPiece[] pb, Stack<Move> mpl) {
        update(b, pb, mpl);
    }

    public void update(int[] b, ChessPiece[] pb, Stack<Move> mpl) {
        for (int i = 0; i < 64; i++) {//Initially clean the board.
            if (b[i] == 2)
                kingIndices[0] = i;
            if (b[i] == 8)
                kingIndices[1] = i;
            checkLines[i] = 0;
            threatenedSquares[i] = 0;
        }

        enemy = mpl.size() % 2;
        friendly = (enemy + 1) % 2;
        numPinLines = 0;
        numCheckLines = 0;

        for (int i = 0; i < 64; i++) {
            ChessPiece piece = pb[i];
            if (piece != null && piece.getColor() == enemy) {
                for (int j : piece.possibleMovesThreateningKing(b, pb))
                    {
                        threatenedSquares[j] = 1;
                        if(j==kingIndices[friendly] && piece.getType()%6==3){
                            checkLines[i]=-1;
                            numCheckLines++;
                        }
                    }
                if (((piece.getType() % 6 == 5 || piece.getType() % 6 == 0) && (sameRank(i) || sameFile(i)))
                        || ((piece.getType() % 6 == 5 || piece.getType() % 6 == 1) && sameDiagonal(i))) {
                    if (isPinLine(i,b)) {
                        checkLines[i] = ++numPinLines;
                        for (int j : squaresTo(i))
                            checkLines[j] = numPinLines;
                    } else if (isCheckLine(i,b)) {
                        checkLines[i] = -1;
                        for (int j : squaresTo(i))
                            checkLines[j] = -1;
                        numCheckLines++;
                    }
                }
            }
        }
        //printArray(threatenedSquares);
        //printArray(checkLines);
        //printArray(kingIndices);
    }

    // Return functions for internal variables
    public int[] getCheckLines() {
        return checkLines;
    }

    public int[] getThreatenedSquares() {
        return threatenedSquares;
    }
    
    public int[] getKingIndices() {
        return kingIndices;
    }

    public int getEnemy() {
        return enemy;
    }

    public int getFriendly() {
        return friendly;
    }

    public int getNumPinLines() {
        return numPinLines;
    }

    public int getNumCheckLines() {
        return numCheckLines;
    }

    // Complex return functions
    public boolean isCheck(){return threatenedSquares[kingIndices[friendly]]==1;}
    public boolean isThreatenedSquare(int p){return(threatenedSquares[p]==1);}
    public int getCheckLineValue(int p){return(checkLines[p]);}

    // Booleans for internal checks
    protected int rank(int piece) {
        return piece / 8;
    }

    protected int file(int piece) {
        return piece % 8;
    }

    protected int distanceTo(int o) {
        return Math.max(Math.abs(rank(o) - rank(kingIndices[friendly])),
                Math.abs(file(o) - file(kingIndices[friendly])));
    }

    protected boolean sameRank(int o) {
        return (rank(o) == rank(kingIndices[friendly]));
    }

    protected boolean sameFile(int o) {
        return (file(o) == file(kingIndices[friendly]));
    }

    protected boolean sameDiagonal(int o) {
        return sameDiagonal(o, kingIndices[friendly]);
    }

    protected boolean sameDiagonal(int o, int i) {
        return (Math.abs(file(o) - file(i)) == Math.abs(rank(o) - rank(i)));
    }

    // returns the indices of all squares between this piece and a target square, to
    // determine if the path there is empty.
    protected int[] squaresTo(int o) {
        int[] squares = new int[distanceTo(o) - 1];
        int k = 0;
        if (sameRank(o)) {
            for (int i = 0; i < 64; i++) {
                if (sameRank(i) && i >= Math.min(kingIndices[friendly], o) && i <= Math.max(kingIndices[friendly], o)
                        && i != kingIndices[friendly] && i != o)
                    squares[k++] = i;
            }
        } else if (sameFile(o)) {
            for (int i = 0; i < 64; i++) {
                if (sameFile(i) && i >= Math.min(kingIndices[friendly], o) && i <= Math.max(kingIndices[friendly], o)
                        && i != kingIndices[friendly] && i != o)
                    squares[k++] = i;
            }
        } else if (sameDiagonal(o)) {
            for (int i = 0; i < 64; i++) {
                if (sameDiagonal(i) && sameDiagonal(o, i) && i >= Math.min(kingIndices[friendly], o)
                        && i <= Math.max(kingIndices[friendly], o) && i != kingIndices[friendly] && i != o)
                    squares[k++] = i;
            }
        } else
            throw new RuntimeException(
                    "A piece tried to call squaresTo(o) on another piece which which wasn't in the same rank, file, or diagonal.");
        return squares;
    }

    // Checks how many pieces are between an enemy piece and our king. If it's one
    // piece, then it's a numPinLines, if it's 0 pieces, then it's a numCheckLines.
    protected boolean isPinLine(int o, int[] b) {
        int pieceCount = 0;
        for (int i : squaresTo(o))
            if (b[i] != 0 && (b[i]-1)/6==friendly)
                pieceCount++;
        return (pieceCount == 1);
    }

    protected boolean isCheckLine(int o, int[] b) {
        int pieceCount = 0;
        for (int i : squaresTo(o))
            if (b[i] != 0)
                pieceCount++;
        return (pieceCount == 0);
    }
}
