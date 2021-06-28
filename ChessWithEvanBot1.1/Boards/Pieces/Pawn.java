package Boards.Pieces;

import Boards.*;
import Moves.*;
import java.util.ArrayList;

public class Pawn extends ChessPiece
{
    public Pawn(int p, int t){super(p,t);}

    public int[] generateMove(int m, int[] b, ChessPiece[] pb, Stack<int[]> mpl, CheckChecker cc)
    {
        if(isEnPassant(m,b,pb,mpl)) return generateMove(getPosition(), m, 2, true, isCheck(m, b, cc), 0);
        else if(rank(m)==0 || rank(m)==7) return generateMove(getPosition(), m, 3, isCapture(m, b), isCheck(m,b,cc), 0);
        else return generateMove(getPosition(), m, 0, isCapture(m, b), isCheck(m,b,cc), 0);
    }
    public boolean isLegalMove(int i, int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl)
    { return ((isPawnPush(i,b) || isInitialPush(i,b) || isPawnCapture(i,b) || isEnPassant(i,b,pb,mpl)) && !isFriendlyPiece(i,b) && !cantBecauseItsPinned(i,c) && !cantBecauseKingIsInCheck(i,c));}
  
    public boolean isCheck(int j, int[] b, CheckChecker cc){
        return isPawnThreat((cc.getKingIndices()[cc.getEnemy()]),j);
    }

    public boolean isCapture(int o, int[] b){
        return isEnemyPiece(o, b) || (sameDiagonal(o, position) && isEnemyPiece(rank(position)*8+file(o), b));
    }

    public boolean isTempo(int o, int[] b, CheckChecker cc){
        for(int i=o;i<64;i++){
            if (isKnightMove(i, o) && isForward(i, o) && isEnemyPiece(i, b)) return true;
        }
        return false;
    }

    public int[] possibleMoves(int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl)
    {
        int size=0; int k=0;

        for(int i=0; i<64;i++)
        {
            if(isLegalMove(i,b,pb,c,mpl)) size++;
        }
        int[] moves = new int[size];

        for(int i=0;i<64;i++)
        {
            if(isLegalMove(i,b,pb,c,mpl)) moves[k++]= i;
        }
        
        return moves;
    }

    public int[] possibleMovesThreateningKing(int[] b, ChessPiece[] pb)
    {
        int size=0; int k=0;

        for(int i=0; i<64;i++)
        {
            if(isPawnThreat(i, position)) size++;
        }
        int[] moves = new int[size];

        for(int i=0;i<64;i++)
        {
            if(isPawnThreat(i, position)) moves[k++]= i;
        }
        
        return moves;       
    }

    //extra booleans for checking pawn move legality
    public boolean isPawnPush(int o, int[] b){return (isForward(o, position) && sameFile(o, position) && isOneAway(o, position) && isEmpty(o,b));}
    public boolean isPawnThreat(int o, int j){return (isForward(o, j) && sameDiagonal(o, j) && isOneAway(o, j));}
    public boolean isPawnCapture(int o, int[] b){return (isForward(o, position) && sameDiagonal(o, position) && isOneAway(o, position) && isEnemyPiece(o,b));}
    public boolean isInitialPush(int o, int[] b){return (isForward(o, position) && (timesMoved==0) && sameFile(o, position) && isEmpty(o,b) && isEmpty((o+position)/2,b) && (distanceTo(o, position)==2));}
    public boolean isEnPassantablePawn(int o, int[] b, ChessPiece[] pb)
    {
        return (isEnemyPiece(o,b) && b[o]%6==4 && pb[o].getTimesMoved()==1 && rank(o)!=2 && rank(o)!=5);
    }
    public boolean isEnPassant(int o, int[] b, ChessPiece[] pb, Stack<int[]> mpl)
    {
        int otherPawn = rank(position)*8+file(o);
        return (isForward(o, position) && sameDiagonal(o, position) && isOneAway(o, position) && isEmpty(o,b) && isEnPassantablePawn(otherPawn,b,pb) && mpl.peek()[1]==otherPawn);
    }
}
