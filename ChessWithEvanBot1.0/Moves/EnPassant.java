package Moves;

import Boards.*;

public class EnPassant extends Move {
    
    int pawnCaptured;

    public EnPassant(int s, int f, int t, int[] b, boolean ic) {
        super(s, f, t, b, ic);
        pawnCaptured = (s / 8) * 8 + f % 8;
    }

    @Override
    public void executeMove(InternalBoards ib) {
        ib.movePiece(startSquare, finishSquare);
        ib.removePiece(pawnCaptured);
    }

    @Override
    public void reverseMove(InternalBoards ib) {
        ib.replacePiece(pawnCaptured);
        ib.unmovePiece(startSquare, finishSquare);
        if(isCapture) ib.replacePiece(finishSquare);
    }
}
