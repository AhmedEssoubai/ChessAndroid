package TheGame.ThePlayer.Pieces;


import java.util.ArrayList;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 08/04/2018.
 */

public class Queen extends Protector {
    public Queen(Position position)
    {
        super(position);
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
                    if (Xdir == -1 && Ydir == 1)
                        dis = -1;
                    else {
                        int[] dirs = Board.ChangeDirection(Xdir, Ydir, 1);
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

    // get the type of this piece
    @Override
    public PieceType GetType()
    {
        return PieceType.Queen;
    }

    // get the value of the queen
    @Override
    public int GetValue()
    {
        return 9;
    }

    // get the evaluation of the queen
    @Override
    public int GetEvaluation()
    {
        return 900;
    }

    // get the index of a piece
    @Override
    public int GetIndex(PlayerType playerType)
    {
        return 2 * (playerType == PlayerType.White ? 1 : -1);
    }

    // get a copy of this queen (new queen has the same properties of this queen)
    @Override
    public Piece GetCopy()
    {
        Queen copy = new Queen(position);
        copy.Protect(super.GetMoves());
        return copy;
    }
}
