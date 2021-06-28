package Boards.Pieces;

import Boards.*;
import Moves.*;
import java.util.ArrayList;

public class Rook extends ChessPiece {
    public Rook(int p, int t) {
        super(p, t);
    }

    public int[] generateMove(int m, int[] b, ChessPiece[] pb, Stack<int[]> mpl, CheckChecker cc) {
        return generateMove(getPosition(), m, 0, isCapture(m, b), isCheck(m, b, cc), 0);
    }

    public boolean isLegalMove(int i, int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl) {
        return (isVisible(i, position, b) && !isFriendlyPiece(i, b) && (sameFile(i, position) || sameRank(i, position))
                && !cantBecauseItsPinned(i, c) && !cantBecauseKingIsInCheck(i, c));
    }
    public boolean isCheck(int j, int[] b, CheckChecker cc){
        int enemyKing = cc.getKingIndices()[cc.getEnemy()];
        return isVisibleIgnoreKing(enemyKing, position, b) && (sameFile(enemyKing, position) || sameRank(enemyKing, position));
    }

    public boolean isCapture(int o, int[] b){
        return isEnemyPiece(o, b);
    }

    public boolean isTempo(int o, int[] b, CheckChecker cc){
        return false;
    }

    public int[] possibleMoves(int[] b, ChessPiece[] pb, CheckChecker c, Stack<int[]> mpl) {

        int size = 0;
        int k = 0;

        for (int i = 0; i < 64; i++) {
            if (isLegalMove(i, b, pb, c, mpl))
                size++;
        }
        int[] moves = new int[size];

        for (int i = 0; i < 64; i++) {
            if (isLegalMove(i, b, pb, c, mpl))
                moves[k++] = i;
        }

        return moves;
    }

    public int[] possibleMovesThreateningKing(int[] b, ChessPiece[] pb) {
        int size = 0;
        int k = 0;

        for (int i = 0; i < 64; i++) {
            if (isVisibleIgnoreKing(i, position, b) && (sameFile(i, position) || sameRank(i, position)))
                size++;
        }
        int[] moves = new int[size];

        for (int i = 0; i < 64; i++) {
            if (isVisibleIgnoreKing(i, position, b) && (sameFile(i, position) || sameRank(i, position)))
                moves[k++] = i;
        }

        return moves;
    }
}
