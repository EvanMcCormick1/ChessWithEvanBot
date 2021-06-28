package Boards.Pieces;

import Boards.*;
import Moves.*;
import java.util.ArrayList;

public abstract class ChessPiece 
{
    protected int position; //position in the board array
    protected int type; //the integer value representing this particular piece in the board array.
    protected int color; // White = 1, Black = 0.
    protected int timesMoved; //# of times the piece has moved.

    public ChessPiece(int p, int t)
    {
        position = p;
        type = t;
        color = (type-1)/6;
        timesMoved = 0;
    }

    //Getters for information
    public int getColor(){return color;}
    public int getType(){return type;}
    public int getPosition(){return position;}
    public int getTimesMoved(){return timesMoved;}

    //information updators
    public void incrementTimesMoved(){timesMoved++;}
    public void decrementTimesMoved(){timesMoved--;}
    public void setTimesMoved(int i){timesMoved=i;}
    public void updatePosition(int p){position = p;}

    //Abstract move generation functions
    public abstract int[] generateMove(int m, int[] b, ChessPiece[] pb, Stack<int[]> mpl, CheckChecker cc);
    public int[] generateMove(int s, int f, int movetype, boolean isCapture, boolean isCheck, int promotionType){
        int[] move = new int[6];
        move[0]=s;
        move[1]=f;
        move[2]=movetype;
        move[3]=isCapture?1:0;
        move[4]=isCheck?1:0;
        move[5]=promotionType;
        return move;
    }
    public abstract boolean isLegalMove(int o, int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl);
    public abstract boolean isCheck(int o, int[] b, CheckChecker c);
    public abstract boolean isCapture(int o, int[] b);
    public abstract boolean isTempo(int o, int[] b, CheckChecker c);
    public abstract int[] possibleMoves(int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl);
    public abstract int[] possibleMovesThreateningKing(int[] b, ChessPiece[] pb);

    //Generating all legal moves using Generate move and IsLegalMove
    public ArrayList<int[]> generateLegalMoves(int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl)
    {
        ArrayList<int[]> result = new ArrayList<>();
        int[] moves = possibleMoves(b,pb,cc,mpl);
        for(int i=0; i< moves.length;i++) result.add(generateMove(moves[i],b,pb,mpl,cc));
        return result;
    }

    public ArrayList<int[]> generateChecks(int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl){
        ArrayList<int[]> result = new ArrayList<>();
        int[] moves = possibleMoves(b,pb,cc,mpl);
        for(int i=0; i< moves.length;i++) {
            if(isCheck(moves[i],b,cc)) result.add(generateMove(moves[i],b,pb,mpl,cc));
        }
        return result;
    }

    public ArrayList<int[]> generateTempo(int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl){
        ArrayList<int[]> result = new ArrayList<>();
        int[] moves = possibleMoves(b,pb,cc,mpl);
        for(int i=0; i< moves.length;i++) {
            if(isTempo(moves[i],b,cc)) result.add(generateMove(moves[i],b,pb,mpl,cc));
        }
        return result;
    }

    public ArrayList<int[]> generateCaptures(int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl){
        ArrayList<int[]> result = new ArrayList<>();
        int[] moves = possibleMoves(b,pb,cc,mpl);
        for(int i=0; i< moves.length;i++) {
            if(isCapture(moves[i],b)) result.add(generateMove(moves[i],b,pb,mpl,cc));
        }
        return result;
    }

    public ArrayList<int[]> generateCheckCaptures(int[] b, ChessPiece[] pb, CheckChecker cc, Stack<int[]> mpl){
        ArrayList<int[]> result = new ArrayList<>();
        int[] moves = possibleMoves(b, pb, cc, mpl);
        for(int i:moves){
            if(isCapture(i,b) || isCheck(i, b, cc)) result.add(generateMove(i,b,pb,mpl,cc));
        }

        return result;
    }
    //Basic functions for determining piece movement. 
    public int rank(int piece){return piece/8;}
    public int file(int piece){return piece%8;}
    public int distanceTo(int o, int j) {return Math.max(Math.abs(rank(o)-rank(j)),Math.abs(file(o)-file(j)));}
    
    //protected booleans for checking validity of moves on the board.
    protected boolean sameRank(int o, int j){ return (rank(o)==rank(j));}
    protected boolean sameFile(int o, int j){return (file(o)==file(j));}
    protected boolean sameDiagonal(int o, int i){ return (Math.abs(file(o)-file(i))==Math.abs(rank(o)-rank(i)));}
    protected boolean isADirection(int o, int j){ return sameRank(o,j) || sameFile(o,j) || sameDiagonal(o,j);}
    protected boolean isOneAway(int o, int j){return (distanceTo(o,j)==1);}
    protected boolean isEdge(int o, int j){return (rank(o)==0 || rank(o)==7 || file(o)==0 || file(o)==7);}
    protected boolean isForward(int o, int j){return(!sameRank(o,j) && ((rank(o)-rank(j))/Math.abs(rank(o)-rank(j))==(color*(2)-1)));}
    protected boolean isKnightMove(int o, int j) {return (distanceTo(o,j)==2 && !isADirection(o,j));}
    protected boolean isEmpty(int o, int[] board){return (board[o]==0);}
    protected boolean isEnemyPiece(int o, int[] board){return (board[o]>0 && ((board[o]-1)/6!=color));}
    protected boolean isFriendlyPiece(int o, int[] board){return(board[o]>0 && ((board[o]-1)/6==color));}
    protected boolean isVisible(int o, int j, int[] board)
    {
        boolean result = true;
        if(!isADirection(o,j) || j==o) return false;
        for(int i:squaresTo(o,j)) if(board[i]!=0) result = false;
        return result;
    }
    protected boolean isVisibleIgnoreKing(int o, int j, int[] board) //function for calculating squares that are in check (if the king is in a line of check, the squares that he is blocking are still threatened.)
    {
        boolean result = true;
        if(!isADirection(o,j) || j==o) return false;
        for(int i:squaresTo(o,j)) if(board[i]!=0 && board[i]!=6*((color+1)%2)+2) result = false; //the second boolean means "if the square we're checking isn't occupied by an enemy king"
        return result;
    }
    protected boolean cantBecauseItsPinned(int o, CheckChecker cc) //Pinned Pieces can't move off of the checkLine (the line they are pinned too.)
    {
        return(cc.getCheckLineValue(position)>0 && cc.getCheckLineValue(o) != cc.getCheckLineValue(position));
    }
    protected boolean cantBecauseKingIsInCheck(int o, CheckChecker checkChecker) //If the king is in check, any pieceMove must either block the check or take the checking piece. If it's a double check, than no piece moves are possible.
    {
        return((checkChecker.isCheck() && checkChecker.getCheckLineValue(o)>-1) || checkChecker.getNumCheckLines()==2);
    }
    
    protected int[] squaresTo(int o, int k) //returns the indices of all squares to a given square, if it is in a straight line from the current position.
    {
        int[] squares = new int[distanceTo(o,k)-1];
        int j=0;
        if(sameRank(o,k)){ 
            for(int i=0;i<64;i++){
                if(sameRank(i,k) && i>=Math.min(k, o) && i<=Math.max(k, o) && i!=k && i!=o) squares[j++]=i;}
            }   
        else if(sameFile(o,k)){
             for(int i=0;i<64;i++){
                 if(sameFile(i,k) && i>=Math.min(k, o) && i<=Math.max(k, o) && i!=k && i!=o) squares[j++]=i;}
             }
        else if(sameDiagonal(o,k)){ 
            for(int i=0;i<64;i++){
                 if(sameDiagonal(i,k) && sameDiagonal(o,i) && i>=Math.min(k, o) && i<=Math.max(k, o) && i!=k && i!=o) squares[j++]=i;}
            }
        else throw new RuntimeException("A piece tried to call squaresTo(o) on another piece which which wasn't in the same rank, file, or diagonal.");
        return squares;
    }
}
  