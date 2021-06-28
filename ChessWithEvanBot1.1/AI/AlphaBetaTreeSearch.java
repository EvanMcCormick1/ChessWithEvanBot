package AI;

import ChessMain.ChessCoordinator;
import AI.BoardEvaluators.*;

public class AlphaBetaTreeSearch extends ChessAI{
    
    public AlphaBetaTreeSearch(ChessCoordinator cc){
        super(cc);
        parent.setBoardEvaluator(new ImprovedEvaluator());
    }
    
    public void makeMove(){
        parent.alphaBetaTreeSearch();
    }
}
