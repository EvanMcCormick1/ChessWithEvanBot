package AI.BoardEvaluators;

public class SimpleEvaluator extends BoardEvaluator{
        
    public int evaluate(int[] o){
        
        int eval=0;
        double[] pieceVals = {3,0,3,1,9,5};
        for(int i=0;i<64;i++){
            if((o[i]-1)/6==1){
                eval+=pieceVals[(o[i]-1)%6];
            }
            else if(o[i]>0){
                eval-=pieceVals[(o[i]-1)%6];
            }
        }            
        return eval;
    }
}