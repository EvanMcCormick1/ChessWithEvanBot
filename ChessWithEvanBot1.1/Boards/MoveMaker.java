package Boards;

import Boards.*;
import Boards.Pieces.*;

public class MoveMaker {

    InternalBoards parent;

    public MoveMaker(InternalBoards p){
        parent = p;
    }

    public int[] generateMove(int s, int f, int movetype, int isCapture, int isCheck, int promotionType){
        int[] move = new int[6];
        move[0]=s;
        move[1]=f;
        move[2]=movetype;
        move[3]=isCapture;
        move[4]=isCheck;
        move[5]=promotionType;
        return move;
    }
    /*Trying to simplify the move object to increase search capacity.
    Move is now an int[].
    [0]=start square index;
    [1]=finish square index;
    [2]=movetype flag (0:normalMove, 1:Castle, 2:EnPassant, 3: PawnPromotion)
    [3]=isCapture (0:no, 1:yes)
    [4]=isCheck (0:no, 1:yes)
    [5]=which piece promoted to if pawn promotion (0:null, 1: Queen, 2:Rook, 3: Knight, 4: Bishop)
    */
    public void executeMove(int[] move){
        if(move[2]==0) executeNormalMove(move);
        else if(move[2]==1) executeCastle(move);
        else if(move[2]==2) executeEnPassant(move);
        else if (move[2]==3) executePawnPromotion(move);
    }
    
    public void reverseMove(int[] move){
        if(move[2]==0) reverseNormalMove(move);
        else if(move[2]==1) reverseCastle(move);
        else if(move[2]==2) reverseEnPassant(move);
        else if (move[2]==3) reversePawnPromotion(move);
    }

    protected void executeNormalMove(int[] move){
        int start = move[0];
        int finish = move[1];
        parent.movePiece(start, finish);
    }

    protected void reverseNormalMove(int[] move){
        int start = move[0];
        int finish = move[1];
        parent.unmovePiece(start, finish);
        if(move[3]==1) parent.replacePiece(finish);
    }

    protected void executeEnPassant(int[] move){
        int start = move[0];
        int finish = move[1];
        int pawnCaptured = (start / 8) * 8 + finish % 8;
        parent.movePiece(start, finish);
        parent.removePiece(pawnCaptured);
    }

    protected void reverseEnPassant(int[] move){
        int start = move[0];
        int finish = move[1];
        int pawnCaptured = (start / 8) * 8 + finish % 8;
        parent.replacePiece(pawnCaptured);
        parent.unmovePiece(start, finish);
    }

    protected void executeCastle(int[] move){
        int rookStart;
        int rookFinish;
        int s = move[0];
        int f = move[1];

        if (f > s)
            rookStart = (s / 8) * 8 + 7;
        else
            rookStart = (s / 8) * 8;
        rookFinish = s + (rookStart - s) / Math.abs(rookStart - s);
        parent.movePiece(s, f);
        parent.movePiece(rookStart, rookFinish);
    }

    protected void reverseCastle(int[] move){
        int rookStart;
        int rookFinish;
        int s = move[0];
        int f = move[1];
        
        if (f > s)
            rookStart = (s / 8) * 8 + 7;
        else
            rookStart = (s / 8) * 8;
        rookFinish = s + (rookStart - s) / Math.abs(rookStart - s);
        parent.unmovePiece(s, f);
        parent.unmovePiece(rookStart, rookFinish);
    }

    protected void executePawnPromotion(int[] move){
        ChessPiece newPiece = new Queen(move[1],(move[1]/32)*6+5);
        newPiece.setTimesMoved(parent.getChessPiece(move[0]).getTimesMoved()+1);

        parent.movePiece(move[0], move[1]);
        parent.promotePiece(move[1], newPiece);
    }

    protected void reversePawnPromotion(int[] move){
        parent.demotePiece(move[1]);
        parent.unmovePiece(move[0], move[1]);
        if(move[3]==1) parent.replacePiece(move[1]);
    }
}
