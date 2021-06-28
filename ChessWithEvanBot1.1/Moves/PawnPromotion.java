package Moves;

//import javax.swing.JOptionPane;
import Boards.*;
import Boards.Pieces.*;


public class PawnPromotion extends Move
{
    protected ChessPiece newPiece;
    protected String[] possiblePromotions = {"Queen","Rook","Knight","Bishop"};

    public PawnPromotion(int s, int f, int t, ChessPiece[] pb, int[] b, boolean ic)
    {
        super(s,f,t,b, ic);
        /*String promotion = (String) JOptionPane.showInputDialog(null, "What piece would you like to promote to?", "Fuck you Danny", 
        JOptionPane.CLOSED_OPTION, null, possiblePromotions, possiblePromotions[0]
        );

        if(promotion=="Queen") newPiece = new Queen(finishSquare, (t/6)*6+5);
        else if(promotion=="Rook") newPiece = new Rook(finishSquare, (t/6)*6+6);
        else if(promotion=="Knight") newPiece = new Knight(finishSquare, (t/6)*6+3);
        else newPiece = new Bishop(finishSquare, (t/6)*6+1);*/
        newPiece = new Queen(finishSquare,(t/6)*6+5);
        newPiece.setTimesMoved(pb[s].getTimesMoved()+1);
    }

    @Override
    public void executeMove(InternalBoards ib) {
        ib.movePiece(startSquare, finishSquare);
        ib.promotePiece(finishSquare, newPiece);
    }

    @Override
    public void reverseMove(InternalBoards ib) {
        ib.demotePiece(finishSquare);
        ib.unmovePiece(startSquare, finishSquare);
        if(isCapture) ib.replacePiece(finishSquare);
    }
}
