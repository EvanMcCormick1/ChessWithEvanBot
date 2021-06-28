package Boards.Pieces;

import Boards.*;
import Moves.*;
import java.util.ArrayList;

public class King extends ChessPiece
{
    public King(int p, int t){super(p,t);}

    public int[] generateMove(int m, int[] b, ChessPiece[] pb, Stack<int[]> mpl, CheckChecker cc)
    { 
        if(isLegalCastle(m, b, pb, cc)) return generateMove(getPosition(),m, 1, false, isCheck(m,b,cc), 0);
        else return generateMove(getPosition(), m, 0 , isCapture(m, b), false, 0);
    }
    public boolean isLegalMove(int i, int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl){
        return ((isOneAway(i, position) && !isFriendlyPiece(i,b)) || isLegalCastle(i,b,pb,cc)) && !cantBecauseKingDies(i,cc);
    }
    
    public boolean isCheck(int j, int[] b, CheckChecker cc){
        int rookPos = (j+position)/2;
        int ek=cc.getKingIndices()[cc.getEnemy()];
        return distanceTo(j, position)==2 && isVisible(ek, rookPos, b) && (sameFile(ek, rookPos) || sameRank(ek, rookPos));
    }

    public boolean isCapture(int o, int[] b){
        return isEnemyPiece(o, b);
    }

    public boolean isTempo(int o, int[] b, CheckChecker cc){
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
            if((isOneAway(i, position)) || isCastle(i,b,pb)) size++;
        }
        int[] moves = new int[size];

        for(int i=0;i<64;i++)
        {
            if((isOneAway(i, position)) || isCastle(i,b,pb)) moves[k++]= i;
        }
        
        return moves;
    }

    //Special Booleans for Castling
    public boolean isLegalCastle(int o, int[] board, ChessPiece[] pb, CheckChecker cc)
    {
        int rookPos;
        if(o>position) rookPos=rank(position)*8+7;
        else rookPos=rank(position)*8;
        return(sameRank(o, position) && distanceTo(o, position)==2 && isVisible(rookPos, position, board)&& getTimesMoved()==0 &&//Checks that the castling square is 2 away from the king on the same rank, the king hasn't moved yet, and that there is space to castle.
        ((board[rookPos]-1)/6==color  && board[rookPos]!=0 && board[rookPos]%6==0 && pb[rookPos].getTimesMoved()==0))
        && !cantBecauseKingDies(position, cc); //Checks to see if there is a friendly rook which hasn't moved in the castling direction.
    }

    public boolean isCastle(int o, int[] board, ChessPiece[] pb)
    {
        int rookPos;
        if(o>position) rookPos=rank(position)*8+7;
        else rookPos=rank(position)*8;
        return(sameRank(o, position) && distanceTo(o, position)==2 && isVisible(rookPos, position, board)&& getTimesMoved()==0 &&//Checks that the castling square is 2 away from the king on the same rank, the king hasn't moved yet, and that there is space to castle.
        ((board[rookPos]-1)/6==color  && board[rookPos]!=0 && board[rookPos]%6==0 && pb[rookPos].getTimesMoved()==0)); //Checks to see if there is a friendly rook which hasn't moved in the castling direction.
    }

    //Special Booleans for check
    public boolean cantBecauseKingDies(int o, CheckChecker cc){return (cc.isThreatenedSquare(o));}
}
