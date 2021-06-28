package AI.BoardEvaluators;

import Boards.CheckChecker;

public abstract class BoardEvaluator {

    public abstract int evaluate(int[] o, CheckChecker cc);
    
    //Basic functions for determining piece position. 
    public int rank(int piece){return piece/8;}
    public int file(int piece){return piece%8;}
    public int distanceTo(int o, int j) {return Math.max(Math.abs(rank(o)-rank(j)),Math.abs(file(o)-file(j)));}
    
    //booleans for analyzing more complex aspects of the board.
    protected boolean sameRank(int o, int j){return (rank(o)==rank(j));}
    protected boolean sameFile(int o, int j){return (file(o)==file(j));}
    protected boolean sameDiagonal(int o, int i){return (Math.abs(file(o)-file(i))==Math.abs(rank(o)-rank(i)));}
    protected boolean isADirection(int o, int j){return sameRank(o,j) || sameFile(o,j) || sameDiagonal(o,j);}
    protected boolean isOneAway(int o, int j){return (distanceTo(o,j)==1);}
    protected boolean isEdge(int o, int j){return (rank(o)==0 || rank(o)==7 || file(o)==0 || file(o)==7);}
    protected boolean isForward(int o, int j, int[] b){return(!sameRank(o,j) && ((rank(o)-rank(j))/Math.abs(rank(o)-rank(j))==(b[j]-1)/6));}
    protected boolean isKnightMove(int o, int j) {return (distanceTo(o,j)==2 && !isADirection(o,j));}
    protected boolean isEmpty(int o, int[] board){return (board[o]==0);}
    protected boolean isEnemyPiece(int o, int j, int[] board){return (board[o]>0 && ((board[o]-1)/6!=(board[j]-1)/6));}
    protected boolean isFriendlyPiece(int o, int j, int[] board){return(board[o]>0 && ((board[o]-1)/6==(board[j]-1)/6));}
    protected boolean isVisible(int o, int j, int[] board)
    {
        boolean result = true;
        if(!isADirection(o,j) || j==o) return false;
        for(int i:squaresTo(o,j)) if(board[i]!=0) result = false;
        return result;
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

