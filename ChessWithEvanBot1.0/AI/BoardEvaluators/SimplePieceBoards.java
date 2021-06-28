package AI.BoardEvaluators;

import Boards.*;
import ChessMain.*;

public class SimplePieceBoards {
    
    private double[] WpawnBoard = {
        0,0,0,0,0,0,0,0,
        1,1,1,0,0,1,1,1,
        0,0,0,5,5,0,0,0,
        -5,0,0,10,10,0,0,-5,
        0,0,0,10,10,0,0,0,
        10,10,10,10,10,10,10,10,
        15,15,15,15,15,15,15,15,
        20,20,20,20,20,20,20,20
    };

    private double[] BPawnBoard = flipBoard(WpawnBoard);

    private double[] WRookBoard = {
        0,0,5,15,15,5,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        10,10,10,10,10,10,10,10,
        20,20,20,20,20,20,20,20,
        0,0,0,0,0,0,0,0
    };

    private double[] BRookBoard = flipBoard(WRookBoard);

    private double[] WKnightBoard = {
        0,0,0,0,0,0,0,0,
        0,0,10,10,10,10,0,0,
        0,10,20,20,20,20,10,0,
        0,10,20,30,30,20,10,0,
        0,10,20,30,30,20,10,0,
        0,10,20,20,20,20,10,0,
        0,0,10,10,10,10,0,0,
        0,0,0,0,0,0,0,0
    };

    private double[] BKnightBoard = flipBoard(WKnightBoard);

    private double[] WBishopBoard = {
        30,20,-10,0,0,-10,20,30,
        20,30,20,10,10,20,30,20,
        5,15,25,15,15,25,15,5,
        0,0,10,20,20,10,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
    };

    private double[] BBishopBoard = flipBoard(WBishopBoard);

    private double[] WQueenBoard = {
        0,0,0,10,10,0,0,0,
        0,0,0,0,0,0,0,0,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
        0,0,0,0,0,0,0,0,
    };

    private double[] BQueenBoard = flipBoard(WQueenBoard);

    private double[] WKingBoard = {
        10,15,0,0,0,0,15,10,
        -5,-5,-5,-5,-5,-5,-5,-5,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
        -10,-10,-10,-10,-10,-10,-10,-10,
    };

    private double[] BKingBoard = flipBoard(WKingBoard);

    private double[] flipBoard(double[] b){
        double[] nb= new double[64];
        if(b.length<64) {
            ChessCoordinator.printArray(b);
            System.out.println(b.length);
        }
        for(int i=0;i<64;i++){
            nb[i]=b[63-i];
        }
        return nb;
    }

    private double[][] WBoards = {WBishopBoard,WKingBoard,WKnightBoard,WpawnBoard,WQueenBoard,WRookBoard};
    private double[][] BBoards ={BBishopBoard,BKingBoard,BKnightBoard,BPawnBoard,BQueenBoard,BRookBoard};

    public double[][] getWBoards(){
        return WBoards;
    }
    public double[][] getBBoards(){
        return BBoards;
    }
}