package Moves;

import Boards.*;

public class Castle extends Move {
    protected int rookStart;
    protected int rookFinish;

    public Castle(int s, int f, int t, int[] b, boolean ic) {
        super(s, f, t, b, ic);
        if (f > s)
            rookStart = (s / 8) * 8 + 7;
        else
            rookStart = (s / 8) * 8;
        rookFinish = s + (rookStart - s) / Math.abs(rookStart - s);
    }

    @Override
    public void executeMove(InternalBoards ib) {
        ib.movePiece(startSquare, finishSquare);
        ib.movePiece(rookStart, rookFinish);
    }

    @Override
    public void reverseMove(InternalBoards ib) {
        ib.unmovePiece(startSquare, finishSquare);
        ib.unmovePiece(rookStart, rookFinish);
    }
}
