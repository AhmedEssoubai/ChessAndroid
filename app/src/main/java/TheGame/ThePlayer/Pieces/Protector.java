package TheGame.ThePlayer.Pieces;

import android.support.annotation.CallSuper;

import java.io.Serializable;
import java.util.List;

import TheGame.Interface.Board;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 01/05/2018.
 */

public abstract class Protector extends Piece implements Serializable {
    public Protector(Position position)
    {
        super(position);
    }

    // <editor-fold desc="Protection">

    // is this piece protect the king
    private boolean protector;

    public boolean IsProtector()
    {
        return this.protector;
    }

    // get the positions can this piece move to it and stay protecting the king, equal null if this piece don't protect the king
    /*public List<List<int[]>> PositionsCanProtectFrom()
    {
        return this.positionsCanProtectFrom;
    }*/

    private List<List<Position>> positionsCanProtectFrom;

    // protect the king
    public void Protect(List<List<Position>> PositionsCanProtectFrom) {
        if (PositionsCanProtectFrom != null) {
            this.protector = true;
            positionsCanProtectFrom = PositionsCanProtectFrom;
        }
    }

    // this piece don't protect the king
    public void NoLongerAProtector()
    {
        this.protector = false;
        this.positionsCanProtectFrom = null;
    }

    // </editor-fold>

    // get the positions[x, y] of the possible moves of this piece
    @CallSuper
    public List<List<Position>> GetMoves()
    {
        return this.positionsCanProtectFrom;
    }

    // get a copy of this piece (new piece has the same properties of this piece)
    @Override
    public abstract Piece GetCopy();

    // get the count of moves can this piece do in Check
    @Override
    public int MovesCount(){
        if (this.protector) {
            int count = 0;
            for(int i = 0; i < this.positionsCanProtectFrom.size(); i++)
                count += this.positionsCanProtectFrom.get(i).size();
            return count;
        }
        return 0;
    }
}
