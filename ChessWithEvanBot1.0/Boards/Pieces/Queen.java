package Boards.Pieces;

import Boards.*;
import Moves.*;
import java.util.ArrayList;

public class Queen extends ChessPiece
{
    public Queen(int p, int t){super(p,t);}

    public Move generateMove(int m, int[] b, ChessPiece[] pb, Stack<Move> mpl, CheckChecker cc){
         return new NormalMove(getPosition(), m, getType(),b, isCheck(m,b,cc));
        }
    public boolean isLegalMove(int i, int[] b, ChessPiece[] pb, CheckChecker c, Stack<Move> mpl){
        return (isVisible(i, position, b) && !isFriendlyPiece(i,b) && !cantBecauseItsPinned(i,c) && !cantBecauseKingIsInCheck(i,c));
    }
    public boolean isCheck(int c, int[] b, CheckChecker cc){
        return isVisibleIgnoreKing(cc.getKingIndices()[cc.getEnemy()], c, b);
    }

    public boolean isCapture(int o, int[] b){
        return isEnemyPiece(o, b);
    }
  
    public int[] possibleMoves(int[] b, ChessPiece[] pb, CheckChecker c, Stack<Move> mpl)
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
            if(isVisibleIgnoreKing(i, position, b)) size++;
        }
        int[] moves = new int[size];

        for(int i=0;i<64;i++)
        {
            if(isVisibleIgnoreKing(i,position,b)) moves[k++]= i;
        }
        
        return moves;
    }
}
