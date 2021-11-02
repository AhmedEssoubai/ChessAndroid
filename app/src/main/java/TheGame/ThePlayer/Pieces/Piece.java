package TheGame.ThePlayer.Pieces;

import android.support.annotation.CallSuper;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 07/04/2018.
 */

public abstract class Piece implements Serializable {
    public Piece(Position position)
    {
        this.position = position;
    }

    protected Position position;
    /*
        X : The position of the piece in column from board
        Y : The position of the piece in row from board
     */
    public void MakeMove(Position position)
    {
        this.position = position;
    }
    public Position GetPositions()
    {
        return position;
    }

    // get the positions[x, y] of the possible moves of this piece
    public abstract List<List<Position>> GetMoves();

    // Add new move
    protected boolean AddNewMove(List<Position> list, Position position)
    {
        if (!Board.OutOfBoard(position)) {
            list.add(position);
            return true;
        }
        return false;
    }

    // get a copy of this piece (new piece has the same properties of this piece)
    public abstract Piece GetCopy();

    // get the count of moves can this piece do in Check
    public abstract int MovesCount();

    // get the type of this piece
    public abstract PieceType GetType();

    // get the value of a piece
    public abstract int GetValue();

    // get the evaluation of a piece
    public abstract int GetEvaluation();

    // get the index of a piece
    public abstract int GetIndex(PlayerType playerType);

    // get the index of a piece from type
    public static int indexOf(PieceType pieceType)
    {
        int value = 0;
        switch (pieceType){
            case King:
                value = 1;
                break;
            case Queen:
                value = 2;
                break;
            case Rook:
                value = 3;
                break;
            case Knight:
                value = 4;
                break;
            case Bishop:
                value = 5;
                break;
            case Pawn:
                value = 6;
                break;
        }
        return value;
    }

    // get the type of a piece from index
    public static PieceType typeOf(int index)
    {
        PieceType[] pieces = new PieceType[]{PieceType.King, PieceType.Queen, PieceType.Rook, PieceType.Knight, PieceType.Bishop, PieceType.Pawn};
        try
        {
            return pieces[index - 1];
        }
        catch (Exception e){
            return null;
        }
    }
}
