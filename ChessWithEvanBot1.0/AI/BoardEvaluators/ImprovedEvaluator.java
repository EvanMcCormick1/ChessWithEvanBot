package AI.BoardEvaluators;

public class ImprovedEvaluator extends BoardEvaluator{
    
    private SimplePieceBoards pieceBoards;
    private double[][] WBoards;
    private double[][] BBoards;
    public ImprovedEvaluator(){
        pieceBoards = new SimplePieceBoards();
        WBoards = pieceBoards.getWBoards();
        BBoards = pieceBoards.getBBoards();
    }

    public int evaluate(int[] o){
        
        int eval=0;
        double[] pieceVals = {325,0,315,100,950,500};
        for(int i=0;i<64;i++){
            if((o[i]-1)/6==1){
                eval+=(pieceVals[(o[i]-1)%6]+WBoards[(o[i]-1)%6][i]);
                if(isPawnThreatened(i,o)) eval -= (int)(0.1*pieceVals[(o[i]-1)%6]);
                if(pawnBlocked(i, o)){eval -= 40;}
            }
            else if(o[i]>0){
                eval-=(pieceVals[(o[i]-1)%6]+BBoards[(o[i]-1)%6][i]);
                if(isPawnThreatened(i,o)) eval += (int)(0.1*pieceVals[(o[i]-1)%6]);
                if(pawnBlocked(i, o)){ eval += 40;}
            }
        }            
        return eval;
    }

    boolean isPawnThreatened(int i, int[] b){
        boolean result = false;
        for(int o=0;o<64;o++){
            result = isKnightMove(o, i) && isForward(o, i, b) && isEnemyPiece(o, i, b) && b[o]%6==4;
        }
        return result;
    }

    boolean pawnBlocked(int i, int[] b){
        int color = (b[i]-1)/6;
        if(color == 1) return b[i]%6 == 4 && (b[i+8]-1)/6==color;
        else return b[i]%6 == 4 && b[i-8]>0 && (b[i-8]-1)/6==color;
    }
}
