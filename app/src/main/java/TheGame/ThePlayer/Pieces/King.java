package TheGame.ThePlayer.Pieces;


import android.support.annotation.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 08/04/2018.
 */

public class King extends Piece implements Serializable {
    public King(Position position)
    {
        super(position);
        this.neverMoved = true;
        ListMoves = null;
        castlingType = Castling.None;
    }

    // true if this piece never moved
    private boolean neverMoved;

    // Castling type, if is NONE is mean the king last move is not a castling
    @NonNull
    private Castling castlingType;

    public Castling IsCastling(){ return this.castlingType; }

    // list of moves can this king move to it
    private List<List<Position>> ListMoves;

    public void NewPossitions(List<List<Position>> listMoves)
    {
        this.ListMoves = listMoves;
    }

    public List<List<Position>> GetMoves()
    {
        List<List<Position>> listMoves = this.ListMoves;
        if (listMoves == null) {
            listMoves = new ArrayList<>();
            int Xdir = -1, Ydir = 0;
            boolean stop = false;
            while (!stop) {
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + Xdir, this.position.GetY() + Ydir));
                if (Ydir == 0 && this.neverMoved)
                    AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + Xdir * 2, this.position.GetY() + Ydir));
                int[] dirs = Board.ChangeDirection(Xdir, Ydir, 1);
                Xdir = dirs[0];
                Ydir = dirs[1];
                if (Xdir == -1 && Ydir == 0)
                    stop = true;
            }
        }
        return listMoves;
    }

    @Override
    public void MakeMove(Position position)
    {
        if (this.neverMoved){
            if (position.GetX() - this.position.GetX() == 2)
                this.castlingType = Castling.KingSide;
            else if (this.position.GetX() - position.GetX() == 2)
                this.castlingType = Castling.QueenSide;
            else
                this.castlingType = Castling.None;
            this.neverMoved = false;
        }
        else
            this.castlingType = Castling.None;
        super.MakeMove(position);
    }

    // get a copy of this king (new king has the same properties of this king)
    @Override
    public Piece GetCopy()
    {
        King copy = new King(position);
        copy.ListMoves = this.ListMoves;
        copy.neverMoved = this.neverMoved;
        copy.castlingType = this.castlingType;
        return copy;
    }

    // get the type of this piece
    @Override
    public PieceType GetType()
    {
        return PieceType.King;
    }

    // get the value of the king
    @Override
    public int GetValue()
    {
        return 200;
    }

    // get the evaluation of the king
    @Override
    public int GetEvaluation()
    {
        return 20000;
    }

    // get the index of a piece
    @Override
    public int GetIndex(PlayerType playerType)
    {
        return playerType == PlayerType.White ? 1 : -1;
    }

    @Override
    public int MovesCount() {
        if (this.ListMoves != null) {
            int count = 0;
            for(int i = 0; i < this.ListMoves.size(); i++)
                count += this.ListMoves.get(i).size();
            return count;
        }
        return 0;
    }
}
