package AI;

import ChessMain.*;

public abstract class ChessAI {

    protected ChessCoordinator parent;

    public ChessAI(ChessCoordinator cc){
        parent=cc;
    }
    public abstract void makeMove();
}
