package TheGame.ThePlayer.Pieces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 08/04/2018.
 */

public class Knight extends Protector implements Serializable {
    public Knight(Position position)
    {
        super(position);
    }

    public List<List<Position>> GetMoves()
    {
        List<List<Position>> listMoves = super.GetMoves();
        if (listMoves == null) {
            listMoves = new ArrayList<>();
            int Xdir = -1, Ydir = 0;
            while (Xdir != Ydir) {
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + (Xdir * 2 + Ydir), this.position.GetY() + (Ydir * 2 + Xdir)));
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + (Xdir * 2 + Ydir * -1), this.position.GetY() + (Ydir * 2 + Xdir * -1)));
                if (Xdir == 0 && Ydir == 1) {
                    Xdir = 0;
                    Ydir = 0;
                } else {
                    int[] dirs = Board.ChangeDirection(Xdir, Ydir, 2);
                    Xdir = dirs[0];
                    Ydir = dirs[1];
                }
            }
        }
        return listMoves;
    }

    // get the type of this piece
    @Override
    public PieceType GetType()
    {
        return PieceType.Knight;
    }

    // get the value of the knight
    @Override
    public int GetValue()
    {
        return 3;
    }

    // get the evaluation of the knight
    @Override
    public int GetEvaluation()
    {
        return 320;
    }

    // get the index of a piece
    @Override
    public int GetIndex(PlayerType playerType)
    {
        return 4 * (playerType == PlayerType.White ? 1 : -1);
    }


    // get a copy of this knight (new knight has the same properties of this knight)
    @Override
    public Piece GetCopy()
    {
        Knight copy = new Knight(position);
        copy.Protect(super.GetMoves());
        return copy;
    }
}
