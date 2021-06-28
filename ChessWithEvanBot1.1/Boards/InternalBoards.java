package Boards;

import java.util.ArrayList;
import java.util.HashMap;

import AI.BoardEvaluators.BoardEvaluator;
import Boards.Pieces.Bishop;
import Boards.Pieces.ChessPiece;
import Boards.Pieces.King;
import Boards.Pieces.Knight;
import Boards.Pieces.Pawn;
import Boards.Pieces.Queen;
import Boards.Pieces.Rook;
import Moves.Move;

public class InternalBoards {
    private int[] integerBoard = new int[64]; // Each Piece is a different integer Value.
    /*
     * 0->Empty 1->Black Bishop 2->Black King 3->Black Knight 4->Black Pawn 5->Black
     * Queen 6->Black Rook 7->White Bishop 8->White King 9->White Knight 10->White
     * Pawn 11->White Queen 12->White Rook
     */
    private ChessPiece[] chessPieceBoard = new ChessPiece[64]; // Allow for quick access to piece-specific
    // move-generating functions.
   
    private MoveMaker moveMaker = new MoveMaker(this); // Class for interpreting and executing int[6] moves.
    private CheckChecker checkChecker; // CheckChecker for checking check and checkmate.
    private BoardEvaluator boardEvaluator; // BoardEvaluator for ChessAI evaluation and movemaking.
    
    private Stack<int[]> movesPlayedList = new Stack<>();
    private Stack<int[]> futureMovesList = new Stack<>();
    private Stack<ChessPiece> capturedPieceStack = new Stack<>(); // Stores captured ChessPiece objects

    private int activePlayer = 1; // Flag for active player. Necessary for custom positins and loading from PGN.

    private int moveCount = 0; // For keeping track of # of positions searched by position evaluator.
    private int recursionDepth = 0; // For keeping track of search depth.


    // INITIAL SET-UP FUNCTIONS

    public InternalBoards() {
        setUpIntegerBoard();
        setUpchessPieceBoard();
        checkChecker = new CheckChecker(integerBoard, chessPieceBoard, movesPlayedList);
    }

    public InternalBoards(String FEN) {
        setUpIntegerBoardFromFEN(FEN);
        setUpchessPieceBoard();
        checkChecker = new CheckChecker(integerBoard, chessPieceBoard, movesPlayedList);
    }

    public ChessPiece makePiece(int pos, int type) {
        ChessPiece piece = null;
        if (type == 0)
            return null;
        if (type % 6 == 1)
            piece = new Bishop(pos, type);
        if (type % 6 == 2)
            piece = new King(pos, type);
        if (type % 6 == 3)
            piece = new Knight(pos, type);
        if (type % 6 == 4)
            piece = new Pawn(pos, type);
        if (type % 6 == 5)
            piece = new Queen(pos, type);
        if (type % 6 == 0)
            piece = new Rook(pos, type);
        return piece;
    }

    public void setUpIntegerBoardFromFEN(String FEN) {

        int[] pieceNums = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
        String[] pieceKeys = { "b", "k", "n", "p", "q", "r", "B", "K", "N", "P", "Q", "R" };
        HashMap<String, Integer> pieceVals = new HashMap<>();
        for (int i : pieceNums) {
            pieceVals.put(pieceKeys[i - 1], Integer.valueOf(i));
        }
        String[] SplitFEN = FEN.split("");
        int file = 7;
        int rank = 7;
        for (String s : SplitFEN) {
            if (s.equals("/")) {
                file = 7;
                rank--;
            } else if (isNumeric(s)) {
                int i = Integer.parseInt(s);
                file -= i;
            } else {
                try {
                    integerBoard[rank * 8 + file--] = pieceVals.get(s).intValue();
                    System.out.println("IntegerBoard position #" + (rank * 8 + file - 1) + " set to "
                            + pieceVals.get(s).intValue());
                } catch (Exception e) {
                    System.out
                            .println("Null Pointer Exception. Tried to add " + s + " to position " + (rank * 8 + file));
                }
            }
        }

    }

    public void setUpIntegerBoard() {
        integerBoard[0] = 12;
        integerBoard[1] = 9;
        integerBoard[2] = 7;
        integerBoard[3] = 8;
        integerBoard[4] = 11;
        integerBoard[5] = 7;
        integerBoard[6] = 9;
        integerBoard[7] = 12;
        for (int i = 8; i < 16; i++)
            integerBoard[i] = 10;
        for (int i = 48; i < 56; i++)
            integerBoard[i] = 4;
        integerBoard[56] = 6;
        integerBoard[57] = 3;
        integerBoard[58] = 1;
        integerBoard[59] = 2;
        integerBoard[60] = 5;
        integerBoard[61] = 1;
        integerBoard[62] = 3;
        integerBoard[63] = 6;
    }

    public void setUpchessPieceBoard() {
        for (int i = 0; i < integerBoard.length; i++)
            chessPieceBoard[i] = makePiece(i, integerBoard[i]);
    }

    // SET FUNCTIONS
    public void setMovesPlayedList(Stack<int[]> movesPlayedList) {
        this.movesPlayedList = movesPlayedList;
    }

    public void setBoardEvaluator(BoardEvaluator be) {
        boardEvaluator = be;
    }

    public void setMoveCount(int i) {
        moveCount = i;
    }

    public void setRecursionDepth(int i) {
        recursionDepth = i;
        if (i > 100)
            throw new RuntimeException("Recursion Depth is 101. We've probably hit some kind of loop.");
    }

    // COMPLEX UPDATING FUNCTIONS
    public void movePiece(int start, int finish) {
        int type = integerBoard[start];
        if (type == 0)
            throw new RuntimeException("There's no piece at coordinate " + start + "!");

        integerBoard[start] = 0;
        integerBoard[finish] = type;

        if (chessPieceBoard[finish] != null)
            capturedPieceStack.push(chessPieceBoard[finish]);
        chessPieceBoard[finish] = chessPieceBoard[start];
        chessPieceBoard[start] = null;
        chessPieceBoard[finish].incrementTimesMoved();
        chessPieceBoard[finish].updatePosition(finish);
    }

    public void unmovePiece(int start, int finish) {
        int type = integerBoard[finish];
        if (type == 0)
            throw new RuntimeException("There's no piece at coordinate " + finish + "!");

        integerBoard[start] = type;
        chessPieceBoard[start] = chessPieceBoard[finish];
        chessPieceBoard[start].decrementTimesMoved();
        chessPieceBoard[start].updatePosition(start);
        chessPieceBoard[finish] = null;
        integerBoard[finish] = 0;
    }

    public void removePiece(int pos) {
        if (integerBoard[pos] == 0)
            throw new RuntimeException("RemovePiece called on an empty square! #" + pos);
        integerBoard[pos] = 0;
        if (chessPieceBoard[pos] == null)
            throw new RuntimeException("RemovePiece called on a null chessPieceBoard square! #" + pos);
        capturedPieceStack.push(chessPieceBoard[pos]);
        chessPieceBoard[pos] = null;
    }

    public void replacePiece(int pos) {
        if (integerBoard[pos] != 0)
            throw new RuntimeException("replacePiece called on an already occupied square! #" + pos);
        if (chessPieceBoard[pos] != null)
            throw new RuntimeException("replacePiece called on an occupied chessPieceBoard square! #" + pos);
        chessPieceBoard[pos] = capturedPieceStack.pop();
        integerBoard[pos] = chessPieceBoard[pos].getType();
    }

    public void promotePiece(int pos, ChessPiece np) {
        capturedPieceStack.push(chessPieceBoard[pos]);
        chessPieceBoard[pos] = np;
        integerBoard[pos] = np.getType();
    }

    public void demotePiece(int pos) {
        chessPieceBoard[pos] = capturedPieceStack.pop();
        integerBoard[pos] = chessPieceBoard[pos].getType();
    }

    public void executeMove(int[] move) {
        moveCount++;
        activePlayer = (activePlayer + 1) % 2;
        moveMaker.executeMove(move);
        movesPlayedList.push(move);
        checkChecker.update(integerBoard, chessPieceBoard, movesPlayedList);
    }

    public boolean executeMove(int s, int f) {
        if (isLegalMove(s, f)) {
            int[] move = chessPieceBoard[s].generateMove(f, integerBoard, chessPieceBoard, movesPlayedList,
                    checkChecker);
            executeMove(move);
            return true;
        } else
            return false;
    }

    public void reverseMove(int[] move) {
        moveMaker.reverseMove(move);
        activePlayer = (activePlayer + 1) % 2;
        checkChecker.update(integerBoard, chessPieceBoard, movesPlayedList);
    }

    public boolean reverseMove() {
        if (movesPlayedList.size() < 1)
            return false;
        else {
            reverseMove(movesPlayedList.pop());
            return true;
        }
    }

    public boolean reverseOfficialMove() {
        if (movesPlayedList.size() < 1)
            return false;
        else {
            int[] move = movesPlayedList.pop();
            reverseMove(move);
            futureMovesList.push(move);
            return true;
        }
    }

    public boolean redoMove() {
        if (futureMovesList.size() < 1)
            return false;
        else {
            executeMove(futureMovesList.pop());
            return true;
        }
    }

    public void setFutureMove(int[] move) {
        futureMovesList.push(move);
    }

    // GET FUNCTIONS
    public int getIntegerBoard(int i) {
        return integerBoard[i];
    }

    public int getActivePlayer() {
        // return (movesPlayedList.size() + 1) % 2;
        return activePlayer;
    }

    public int[] getPossibleMoves(int i) {
        return chessPieceBoard[i].possibleMoves(integerBoard, chessPieceBoard, checkChecker, movesPlayedList);
    }

    public int getBoardEval() {
        return boardEvaluator.evaluate(integerBoard, checkChecker);
    }

    public int getMoveCount() {
        return moveCount;
    }

    public int getRecursionDepth() {
        return recursionDepth;
    }

    public CheckChecker getCheckChecker() {
        return checkChecker;
    }

    public ChessPiece getChessPiece(int o) {
        return chessPieceBoard[o];
    }
    // COMPLEX RETURN FUNCTIONS

    public boolean isLegalMove(int s, int f) {
        return integerBoard[s] > 0
                && chessPieceBoard[s].isLegalMove(f, integerBoard, chessPieceBoard, checkChecker, movesPlayedList);
    }

    public ArrayList<int[]> allPossibleMovesOrdered() {
        ArrayList<int[]> checks = new ArrayList<>();
        ArrayList<int[]> captures = new ArrayList<>();
        ArrayList<int[]> moves = new ArrayList<>();
        int k = (int) (Math.random() * 64);
        // System.out.println(k);
        for (int i = 0; i < 64; i++) {
            int l = (i + k) % 64;
            int j = 8 * (l % 8) + l / 8;
            if (integerBoard[j] > 0 && ((integerBoard[j] - 1) / 6 == getActivePlayer())) {
                for (int[] move : chessPieceBoard[j].generateLegalMoves(integerBoard, chessPieceBoard, checkChecker,
                        movesPlayedList)) {
                    if (move[4]==1)
                        checks.add(move);
                    else if (move[3]==1)
                        captures.add(move);
                    else
                        moves.add(move);
                }
            }
        }

        checks.addAll(captures);
        checks.addAll(moves);
        return checks;
    }

    public ArrayList<int[]> allCaptures() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for (int i = 0; i < 64; i++) {
            if (integerBoard[i] > 0 && ((integerBoard[i] - 1) / 6 == getActivePlayer())) {
                for (int[] move : chessPieceBoard[i].generateCaptures(integerBoard, chessPieceBoard, checkChecker,
                        movesPlayedList)) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public ArrayList<int[]> allChecks() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for (int i = 0; i < 64; i++) {
            if (integerBoard[i] > 0 && ((integerBoard[i] - 1) / 6 == getActivePlayer())) {
                for (int[] move : chessPieceBoard[i].generateChecks(integerBoard, chessPieceBoard, checkChecker,
                        movesPlayedList)) {
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public ArrayList<int[]> allTempo() {
        ArrayList<int[]> moves = new ArrayList<int[]>();
        for (int i = 0; i < 64; i++) {
            if (integerBoard[i] > 0 && ((integerBoard[i] - 1) / 6 == getActivePlayer())) {
                for (int[] move : chessPieceBoard[i].generateTempo(integerBoard, chessPieceBoard, checkChecker,
                movesPlayedList)){
                    moves.add(move);
                }
            }
        }
        return moves;
    }

    public ArrayList<int[]> allChecksCapturesOrdered() {
        ArrayList<int[]> checks = new ArrayList<>();
        ArrayList<int[]> captures = new ArrayList<>();

        for (int i = 0; i < 64; i++) {
            if (integerBoard[i] > 0 && ((integerBoard[i] - 1) / 6 == getActivePlayer())) {
                for (int[] move : chessPieceBoard[i].generateCheckCaptures(integerBoard, chessPieceBoard, checkChecker,
                        movesPlayedList)) {
                    if (move[4]==1)
                        checks.add(move);
                    else if (move[3]==1)
                        captures.add(move);
                }
            }
        }

        checks.addAll(captures);
        return checks;
    }

    public int numberOfPossibleMoves(){
        int total = 0;
        for (int i = 0; i < 64; i++) {
            if (integerBoard[i] > 0 && ((integerBoard[i] - 1) / 6 == getActivePlayer())) {
                total+=chessPieceBoard[i].possibleMoves(integerBoard, chessPieceBoard, checkChecker, movesPlayedList).length;
                }
            }
        return total;
    }
 
    public void makeRandomMove() {
        ArrayList<int[]> moves = allPossibleMovesOrdered();
        int i = (int) (Math.random() * (moves.size() - 1));
        executeMove(moves.get(i));
    }

    // TREESEARCH FUNCTIONS FOR AI
    public void simpleTreeSearch(int depth) {

        ArrayList<int[]> moves = allPossibleMovesOrdered();
        int index = -1;
        int bestEval;

        if (getActivePlayer() == 1) { // Maxi (White wants highest score)

            bestEval = -1000000;
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = miniMax(depth - 1, moves.get(i));
                    if (eval >= bestEval) {
                        bestEval = eval;
                        index = i;
                    }
                }
            }
        } else { // Mini (Black wants lowest score)

            bestEval = 1000000;
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = miniMax(depth - 1, moves.get(i));
                    if (eval <= bestEval) {
                        bestEval = eval;
                        index = i;
                    }
                }
            }
        }
        executeMove(moves.get(index));
    }
    
    public int miniMax(int depth, int[] move) {

        if (depth == 0) {
            executeMove(move);
            int eval = getBoardEval();
            reverseMove();
            return eval;
        }

        else {

            executeMove(move);
            ArrayList<int[]> moves = allPossibleMovesOrdered();
            int bestEval;

            if (getActivePlayer() == 1) { // Maxi (White wants highest score)
                bestEval = -1000000;
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = miniMax(depth - 1, moves.get(i));
                        if (eval >= bestEval) {
                            bestEval = eval;
                        }
                    }
                }
            } else { // Mini (Black wants lowest score)

                bestEval = 1000000;
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = miniMax(depth - 1, moves.get(i));
                        if (eval <= bestEval) {
                            bestEval = eval;
                        }
                    }
                }
            }

            reverseMove();
            return bestEval;
        }
    }


    public void alphaBetaTreeSearch(int depth) {
        int alpha = -1000000;
        int beta = 1000000;
        int bestEval;

        int index = 0;
        ArrayList<int[]> moves = allPossibleMovesOrdered();

        if (getActivePlayer() == 1) { // Maxi (White wants highest score)
            bestEval = -1000000; // This is going to represent white's best possible move eval (so it starts at
                                 // basically neg infinity)
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = alphaBetaMiniMax(depth - 1, alpha, beta, moves.get(i));
                    if (eval > bestEval) {
                        bestEval = eval;
                        alpha = eval;
                        index = i;
                    }
                }
            }
        } else { // Mini (Black wants lowest score)
            bestEval = 1000000;// This is going to represent black's best possible move eval (so it starts at
                               // basically infinity)
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = alphaBetaMiniMax(depth - 1, alpha, beta, moves.get(i));
                    System.out.println("int[] # " + i + " Eval: " + eval);
                    if (eval < bestEval) {
                        bestEval = eval;
                        beta = eval;
                        index = i;
                    }
                }
            }
        }
        if (moves.size() > 0) {
            executeMove(moves.get(index));
            System.out.println("Move Chosen: # " + index + ", " + moves.get(index));
            System.out.println("Eval: " + bestEval);
        } else {
            if (checkChecker.isCheck())
                System.out.println("Checkmate! Player wins.");
            else
                System.out.println("Stalemate! It's a draw.");
        }
    }
    
    public int alphaBetaMiniMax(int depth, int alpha, int beta, int[] move) {

        if (depth <= 0) {
            return alphaBetaChecksCaptures(3, alpha, beta, move);
        }

        else {

            executeMove(move);
            ArrayList<int[]> moves = allPossibleMovesOrdered();
            int bestEval;

            if (getActivePlayer() == 1) { // Maxi (White wants highest score)

                bestEval = -1000000;
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaMiniMax(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            } else { // Mini (Black wants lowest score)

                bestEval = 1000000;
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaMiniMax(depth - 1, alpha, beta, moves.get(i));
                        if (eval < bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval < beta) {
                            beta = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            }
            reverseMove();
            return bestEval;
        }
    }
    
    public int alphaBetaChecksCaptures(int depth, int alpha, int beta, int[] move) {
        
        if (depth <= 0) {
            return alphaBetaCaptures(depth + 4, alpha, beta, move);
        }
        
        else {
            executeMove(move);
            int bestEval = getBoardEval();
            recursionDepth = Math.max(depth, recursionDepth);
            
            if (getActivePlayer() == 1) { // Maxi (White wants highest score)
                ArrayList<int[]> moves = allChecks();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksResponse(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
                moves = allCaptures();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksCaptures(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
                moves = allTempo();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksCaptures(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
            } 
            else { // Mini (Black wants lowest score)
                ArrayList<int[]> moves = allChecks();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksResponse(depth - 1, alpha, beta, moves.get(i));
                        if (eval < bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval < beta) {
                            beta = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
                moves = allCaptures();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksCaptures(depth - 1, alpha, beta, moves.get(i));
                        if (eval < bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval < beta) {
                            beta = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
                moves = allTempo();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksCaptures(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                        break;
                    }
                }
            }
            if(checkChecker.isCheck() && numberOfPossibleMoves()==0) bestEval=(getActivePlayer()*2-1)*-1000000;
            reverseMove();
            return bestEval;
        }
    }
    
    public int alphaBetaChecksResponse(int depth, int alpha, int beta, int[] move) {
        if (depth <= 0) {
            return alphaBetaCaptures(depth + 4, alpha, beta, move);
        }

        else {
            executeMove(move);
            int bestEval = getBoardEval();
            recursionDepth = Math.max(depth, recursionDepth);
            
            if (getActivePlayer() == 1) { // Maxi (White wants highest score)
                ArrayList<int[]> moves = allPossibleMovesOrdered();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecks(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            } 
            else { // Mini (Black wants lowest score)
                ArrayList<int[]> moves = allPossibleMovesOrdered();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecks(depth - 1, alpha, beta, moves.get(i));
                        if (eval < bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval < beta) {
                            beta = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            }
            if(checkChecker.isCheck() && numberOfPossibleMoves()==0) bestEval=(getActivePlayer()*2-1)*-1000000;
            reverseMove();
            return bestEval;
        }
    }
    
    public int alphaBetaChecks(int depth, int alpha, int beta, int[] move) {
        
        if (depth <= 0) {
            return alphaBetaCaptures(depth + 4, alpha, beta, move);
        }

        else {
            executeMove(move);
            int bestEval = getBoardEval();
            recursionDepth = Math.max(depth, recursionDepth);
            
            if (getActivePlayer() == 1) { // Maxi (White wants highest score)
                ArrayList<int[]> moves = allChecks();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksResponse(depth - 1, alpha, beta, moves.get(i));
                        if (eval > bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval > alpha) {
                            alpha = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            } 
            else { // Mini (Black wants lowest score)
                ArrayList<int[]> moves = allChecks();
                if (moves.size() > 0) {
                    for (int i = 0; i < moves.size(); i++) {
                        int eval = alphaBetaChecksResponse(depth - 1, alpha, beta, moves.get(i));
                        if (eval < bestEval) {
                            bestEval = eval;
                        }
                        if (bestEval < beta) {
                            beta = bestEval;
                        }
                        if (alpha >= beta)
                            break;
                    }
                }
            }
            if(checkChecker.isCheck() && numberOfPossibleMoves()==0) bestEval=(getActivePlayer()*2-1)*-1000000;
            reverseMove();
            return bestEval;
        }
    }
    
    public int alphaBetaCaptures(int depth, int alpha, int beta, int[] move) {
        // Think of alpha as white's best alternative move, and beta as black's best
        // alternative move.
        // alpha ==-1000000; beta == 1000000; (in initial call)
        executeMove(move);

        int bestEval = getBoardEval();
        recursionDepth = Math.max(depth, recursionDepth);

        if(depth>10){
            reverseMove();
            return bestEval;
        }

        if (getActivePlayer() == 1) { // White to Play.
            if (bestEval > beta) { // If the current position is better for white than black's best guaranteed
                                   // alternative.
                reverseMove();
                return bestEval; // Just return the current board evaluation. Black's capture was unproductive,
                                 // so no point continuing the search.
            }
            ArrayList<int[]> moves = allCaptures();
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = alphaBetaCaptures(depth + 1, alpha, beta, moves.get(i)); // For each move in the list
                                                                                        // of possible captures.
                    if (eval > bestEval) {
                        bestEval = eval; // If the evaluation is better than the current best evaluation (initially the
                                         // position evaluation), set best evaluation to the one for this move.
                    }
                    if (eval > alpha) {
                        alpha = eval; // If the evaluation for this position is better than the evaluation for alpha
                                      // (white's best alternative), set alpha to the position eval.
                    }
                    if (alpha >= beta)
                        break; // If white has a move in this position that is guaranteed to be better for him
                               // than black's best alternative, stop searching. Black is guaranteed to be
                               // worse off in this branch than another, so no point continuing.
                }
            }
        } else {// Black to play.
            if (bestEval < alpha) { // If the current position is better for black than white's best alternative.
                reverseMove();
                return bestEval; // Just return board eval, continued search is pointless.
            }
            ArrayList<int[]> moves = allCaptures();
            if (moves.size() > 0) {
                for (int i = 0; i < moves.size(); i++) {
                    int eval = alphaBetaCaptures(depth + 1, alpha, beta, moves.get(i));
                    if (eval < bestEval) {
                        bestEval = eval; // If eval better than current best eval, set best eval to this eval.
                    }
                    if (eval < beta) {
                        // ChessCoordinator.printArray(integerBoard);
                        beta = eval; // If eval greater than beta (black's best alternative) set beta to this eval.
                    }
                    if (alpha >= beta)
                        break; // If black is guaranteed a move in this branch that's worse for white than
                               // white's best alternative (alpha), white won't bother with this branch. Stop
                               // searching.
                }
            }
        }

        reverseMove();
        // System.out.println("Active Player: "+getActivePlayer()+", Best eval =" +
        // bestEval);
        return bestEval;
    }

    // Static Methods for simple checks.
    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
