package TheGame.ThePlayer.Pieces;


import java.util.ArrayList;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 08/04/2018.
 */

public class Rook extends Protector {
    public Rook(Position position)
    {
        super(position);
        this.neverMoved = true;
    }

    // true if this piece never moved
    private boolean neverMoved;

    public boolean IsNeverMoved()
    {
        return this.neverMoved;
    }

    public List<List<Position>> GetMoves()
    {
        List<List<Position>> listMoves = super.GetMoves();
        if (listMoves == null) {
            listMoves = new ArrayList<>();
            listMoves.add(new ArrayList<Position>());
            int Xdir = -1, Ydir = 0, dis = 1;
            while (dis != 0) {
                if (!AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + (dis * Xdir), this.position.GetY() + (dis * Ydir)))) {
                    if (Xdir == 0 && Ydir == 1)
                        dis = -1;
                    else {
                        int[] dirs = Board.ChangeDirection(Xdir, Ydir, 2);
                        Xdir = dirs[0];
                        Ydir = dirs[1];
                        dis = 0;
                        listMoves.add(new ArrayList<Position>());
                    }
                }
                dis++;
            }
        }
        return listMoves;
    }

    @Override
    public void MakeMove(Position position)
    {
        super.MakeMove(position);
        this.neverMoved = false;
    }

    // get the type of this piece
    @Override
    public PieceType GetType()
    {
        return PieceType.Rook;
    }

    // get the value of the rook
    @Override
    public int GetValue()
    {
        return 5;
    }

    // get the evaluation of the rook
    @Override
    public int GetEvaluation()
    {
        return 500;
    }

    // get the index of a piece
    @Override
    public int GetIndex(PlayerType playerType)
    {
        return 3 * (playerType == PlayerType.White ? 1 : -1);
    }

    // get a copy of this rook (new rook has the same properties of this rook)
    @Override
    public Piece GetCopy()
    {
        Rook copy = new Rook(position);
        copy.Protect(super.GetMoves());
        copy.neverMoved = this.neverMoved;
        return copy;
    }
}
