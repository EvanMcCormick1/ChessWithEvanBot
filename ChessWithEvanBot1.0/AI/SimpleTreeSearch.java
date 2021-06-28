package AI;

import ChessMain.*;
import AI.BoardEvaluators.*;

public class SimpleTreeSearch extends ChessAI{
    public SimpleTreeSearch(ChessCoordinator cc){
        super(cc);
        parent.setBoardEvaluator(new SimpleEvaluator());
    }
    public void makeMove(){
        parent.simpleTreeSearch();
    }
}
