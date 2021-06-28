package ChessMain;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;

import javax.swing.JFrame;

import AI.AlphaBetaTreeSearch;
import AI.ChessAI;
import AI.BoardEvaluators.BoardEvaluator;
import Boards.InternalBoards;
import GUI.DisplayBoard;
import GUI.MenuButtons;
import Moves.Move;

public class ChessCoordinator extends JFrame {
    // JPanel objects for JFrame aspect of this class
    private DisplayBoard displayBoard;
    private MenuButtons menuButtons;
    private DBMouseListener userInputListener;

    //Defining private mouselistener class
    private class DBMouseListener extends MouseAdapter{

        @Override
        public void mouseExited(MouseEvent e) {
            unhighlightSquares();
        }

    }

    // Internal data structures.
    private InternalBoards internalBoards;


    // Commander Classes
    private ChessAI ai;

    // Constructor
    public ChessCoordinator() {
        //Internal Variables
        internalBoards = new InternalBoards();
        displayBoard = new DisplayBoard(500, this);
        menuButtons = new MenuButtons(this);
        userInputListener = new DBMouseListener();
        ai = new AlphaBetaTreeSearch(this);
        
        //Default Parameters
        setSize(1000,600);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Add Stuff
        add(displayBoard);
        add(menuButtons);
        addMouseListener(userInputListener);
        updateDisplayBoard();

        //Finish Up
        setVisible(true);
    }

    //GET FUNCTIONS
    public int getActivePlayer(){
        return (internalBoards.getActivePlayer());
    }


    //SET FUNCTIONS
    public void setAi(ChessAI ai) {
        this.ai = ai;
    }



    //DISPLAYBOARD UPDATING FUNCTIONS
    public void updateDisplayBoard() {
        for (int i = 0; i < 64; i++) {
            displayBoard.setSquare(i, internalBoards.getIntegerBoard(i));
        }
    }
    public void highlightLegalMoves(int i) {
        int[] pm = internalBoards.getPossibleMoves(i);
        displayBoard.highlightSquares(pm);
    }

    public void unhighlightSquares(){
        displayBoard.unhighlightSquares();
    }

    public void flipBoard(){
        displayBoard.flipBoard();
    }

    public void setFutureMove(Move move){
        internalBoards.setFutureMove(move);
    }

    public void setColorLightSquares(Color c){
        displayBoard.setColorLightSquares(c);
    }

    public void setColorDarkSquares(Color c){
        displayBoard.setColorDarkSquares(c);
    }
    
    
    // INTERNALBOARDS UPDATING FUNCTIONS
    public void setBoardEvaluator(BoardEvaluator be){
        internalBoards.setBoardEvaluator(be);
    }

    public void setInternalBoardsFromFEN(String FEN){
        internalBoards = new InternalBoards(FEN);
        updateDisplayBoard();
    }

    // COMPLEX FUNCTIONS
    public boolean makeOfficialMove(int s, int f){
        if(internalBoards.executeMove(s,f)) 
        {
            updateDisplayBoard();
            ai.makeMove();
            return true;
        }
        else return false;
    }

    public boolean reverseMove(){
        if(internalBoards.reverseOfficialMove()) {
            updateDisplayBoard();
            return true;
        }
        else return false;
    }

    public boolean redoMove(){
        if(internalBoards.redoMove()) {
            updateDisplayBoard();
            return true;
        }
        else return false;
    }

    public void makeRandomMove(){
        internalBoards.makeRandomMove();
        updateDisplayBoard();
    }

    public void simpleTreeSearch(){
        internalBoards.simpleTreeSearch(4);
        System.out.println("Positions searched: "+internalBoards.getMoveCount()+", Depth of Search: "+internalBoards.getRecursionDepth());
        internalBoards.setMoveCount(0);
        internalBoards.setRecursionDepth(0);
        updateDisplayBoard();
    }

    public void alphaBetaTreeSearch(){
        internalBoards.alphaBetaTreeSearch(2);
        System.out.println("Positions searched: "+internalBoards.getMoveCount()+", Depth of Search: "+internalBoards.getRecursionDepth());
        //printArray(internalBoards.getCheckChecker().getCheckLines());
        //printArray(internalBoards.getCheckChecker().getThreatenedSquares());
        internalBoards.setMoveCount(0);
        internalBoards.setRecursionDepth(0);
        updateDisplayBoard();
    }
    /*
     * Top-level functions to include in CC Class: -make official move 
     * -make AI move (According to various movesearch algorithms) 
     * -set AI -reset internal boards movelist Arraylist. 
     * -update displayboard with internal boards info D
     * -Highlight a set of squares corresponding to possible moves from a square.
     */

     //Tracer functions
     public static void printArray(int[] o){
        for(int i=0;i<64;i++){
            if(i%8==0) {
                System.out.println();
                System.out.println();
            }
            System.out.print(o[i]+"  ");
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }

     public static void printArray(double[] o){
        for(int i=0;i<64;i++){
            if(i%8==0) {
                System.out.println();
                System.out.println();
            }
            System.out.print(o[i]+"  ");
        }
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println();
    }
}