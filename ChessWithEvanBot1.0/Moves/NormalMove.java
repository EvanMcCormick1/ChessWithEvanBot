package Moves;

import Boards.*;

public class NormalMove extends Move
{
    public NormalMove(int s, int f, int t, int[] b, boolean ic){super(s,f,t,b,ic);}

    @Override
    public void executeMove(InternalBoards ib) {
        ib.movePiece(startSquare,finishSquare);
    }

    @Override
    public void reverseMove(InternalBoards ib) {
        ib.unmovePiece(startSquare, finishSquare);
        if(isCapture) ib.replacePiece(finishSquare);
    }
}
