package TheGame.GameAndTurns;

import java.io.Serializable;

import TheGame.ThePlayer.Pieces.Castling;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 05/05/2018.
 */

public class Move implements Serializable {

    public Move(Position from, Position to, PieceType piece, boolean capturing, Castling castling, int time)
    {
        this.from = (from == null) ? new Position(0, 0) : from;
        this.to = to;
        this.piece = piece;
        this.capturing = capturing;
        this.castling = castling;
        this.promotion = null;
        this.check = false;
        this.checkMat = false;
        this.draw = false;
        this.time = time;
    }

    // The moved piece
    private PieceType piece;

    public PieceType getPiece() {
        return piece;
    }

    // The time with seconds this move made
    private int time;

    public int getTime() {
        return time;
    }

    // If this moved caused a capture a piece from the other player
    private boolean capturing;

    public boolean getCapturing() {
        return capturing;
    }

    // The type of castling in this move, Castling.NONE if there is no castling happened
    private Castling castling;

    public Castling getCastling() {
        return castling;
    }

    // This move caused a checkMat if true
    private boolean checkMat;

    public boolean isCheckMat() {
        return checkMat;
    }

    void setCheckMat(boolean checkMat)
    {
        this.checkMat = checkMat;
    }

    // This move caused a draw if true
    private boolean draw;

    public boolean isDraw() {
        return draw;
    }

    public void setDraw(boolean draw)
    {
        this.draw = draw;
    }

    // This move caused a check if true
    private boolean check;

    public boolean isCheck() {
        return check;
    }

    void setCheck(boolean check)
    {
        this.check = check;
    }

    // the position the piece moved from
    private Position from;

    public Position getFrom() {
        return from;
    }

    // the position the piece moved to it
    private Position to;

    public Position getTo() {
        return to;
    }

    // the type of piece paw promotion to
    private PieceType promotion;

    public PieceType getPromotion() {
        return promotion;
    }

    void setPromotion(PieceType type)
    {
        promotion = type;
    }

    // get the notation of this move
    public String GetNotation()
    {
        char[] ColumnsIndex = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'};
        String castling = (this.castling == Castling.KingSide) ? "0-0" : (this.castling == Castling.QueenSide) ? "0-0-0" : "";
        String piece = "", promotion = "", position = "", capturing = "";
        if (castling == "")
        {
            piece = getPieceSymbol(this.piece);
            promotion = (this.promotion != null) ? String.format("=%s", getPieceSymbol(this.promotion)) : "";
            capturing = this.capturing ? String.format("%cx", ColumnsIndex[from.GetX()]) : "";
            position = String.format("%s%s", ColumnsIndex[to.GetX()], 8 - to.GetY());
        }
        String check = (this.checkMat) ? "#" : (this.check) ? "+" : "";
        return String.format("%s%s%s%s%s%s", castling, piece, capturing, position, promotion, check);
    }

    private String getPieceSymbol(PieceType piece)
    {
        String symbol = "";
        switch (piece)
        {
            case King:
                symbol = "K";
                break;
            case Rook:
                symbol = "R";
                break;
            case Queen:
                symbol = "Q";
                break;
            case Bishop:
                symbol = "B";
                break;
            case Knight:
                symbol = "N";
                break;
        }
        return symbol;
    }
}