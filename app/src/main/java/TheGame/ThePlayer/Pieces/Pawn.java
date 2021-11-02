package TheGame.ThePlayer.Pieces;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 08/04/2018.
 */

public class Pawn extends Protector implements Serializable {
    public Pawn(Position position, int direction)
    {
        super(position);
        this.neverMoved = true;
        this.canCapturedEnPassant = false;
        this.direction = direction;
        this.promotion = false;
    }

    // true if this piece never moved
    private boolean neverMoved;

    // True if the pawn just moved two step, that's main can be capture from another pawn
    private boolean canCapturedEnPassant;

    // Promotion this pawn to more valuable piece
    private boolean promotion;
    public boolean Promotion()
    {
        return this.promotion;
    }

    public boolean CanCapturedEnPassant()
    {
        return this.canCapturedEnPassant;
    }

    public void NotEnPassant()
    {
        this.canCapturedEnPassant = false;
    }

    @Override
    public void MakeMove(Position position)
    {
        if (this.neverMoved)
        {
            int steps = this.position.GetY() - position.GetY();
            if (this.position.GetY() < position.GetY())
                steps = position.GetY() - this.position.GetY();
            if (steps == 2)
                this.canCapturedEnPassant = true;
        }
        super.MakeMove(position);
        if (this.position.GetY() == 0 || this.position.GetY() == 7)
            this.promotion = true;
        this.neverMoved = false;
    }

    // the direction of the pawn (up / down)
    private int direction;

    public List<List<Position>> GetMoves()
    {
        List<List<Position>> listMoves = super.GetMoves();
        if (listMoves == null) {
            listMoves = new ArrayList<>();
            if (!this.promotion) {
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX(), this.position.GetY() + direction));
                if (this.neverMoved)
                    AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX(), this.position.GetY() + direction * 2));
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() + 1, this.position.GetY() + direction));
                listMoves.add(new ArrayList<Position>());
                AddNewMove(listMoves.get(listMoves.size() - 1), new Position(this.position.GetX() - 1, this.position.GetY() + direction));
            }
        }
        return listMoves;
    }

    // get the value of the pawn
    @Override
    public int GetValue()
    {
        return 1;
    }

    // get the evaluation of the pawn
    @Override
    public int GetEvaluation()
    {
        return 100;
    }

    // get the index of a piece
    @Override
    public int GetIndex(PlayerType playerType)
    {
        return 6 * (playerType == PlayerType.White ? 1 : -1);
    }

    // get the type of this piece
    @Override
    public PieceType GetType()
    {
        return PieceType.Pawn;
    }

    // get a copy of this pawn (new pawn has the same properties of this pawn)
    @Override
    public Piece GetCopy()
    {
        Pawn copy = new Pawn(position, this.direction);
        copy.Protect(super.GetMoves());
        copy.promotion = this.promotion;
        copy.canCapturedEnPassant = this.canCapturedEnPassant;
        copy.neverMoved = this.neverMoved;
        return copy;
    }
}
