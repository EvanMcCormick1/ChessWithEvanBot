package AI;

import ChessMain.ChessCoordinator;

public class RandomMover extends ChessAI{

    public RandomMover(ChessCoordinator cc){
        super(cc);
    }

    public void makeMove(){
        parent.makeRandomMove();
    }
    
}
