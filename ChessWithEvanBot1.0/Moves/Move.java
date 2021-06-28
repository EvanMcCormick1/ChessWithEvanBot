package Moves;

import Boards.*;

public abstract class Move {
    protected int startSquare;
    protected int finishSquare;
    protected int type;
    protected boolean isCapture = false;
    protected boolean isCheck = false;
    protected boolean isTempo = false;

    Move(int s, int f, int t, int[] b, boolean ic) {
        startSquare = s;
        finishSquare = f;
        type = t;
        if (b[f] > 0)
            isCapture = true;
        isCheck = ic;
        isTempo = isTempo(s,f,t,b);
    }

    // abstract movement functions
    public abstract void executeMove(InternalBoards ib);

    public abstract void reverseMove(InternalBoards ib);

    //Tempo Check

    public boolean isTempo(int s, int f, int t, int[] b){
        boolean result = false;
        return result;
    }

    // data getters
    public int getStart() {
        return startSquare;
    }

    public int getFinish() {
        return finishSquare;
    }

    public int gettype() {
        return type;
    }

    public boolean isCapture(){
        return isCapture;
    }

    public boolean isCheck(){
        return isCheck;
    }
}