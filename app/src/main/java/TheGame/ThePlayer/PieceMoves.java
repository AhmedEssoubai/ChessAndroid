package TheGame.ThePlayer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by AHMED on 11/04/2018.
 */

public class PieceMoves implements Serializable {
    private boolean CanBeThreatToTheKing, ThreatToTheKing, AlmostThreatToTheKing;
    private List<List<Position>> moves;
    private List<Integer> pathesEnd;
    private Position PiecePosition;

    PieceMoves(boolean canBeThreatToTheKing, Position piecePosition, List<List<Position>> moves, List<Integer> pathesEnd)
    {
        this.CanBeThreatToTheKing = canBeThreatToTheKing;
        this.moves = moves;
        this.pathesEnd = pathesEnd;
        this.i = -1;
        this.kingPos = -1;
        this.PiecePosition = piecePosition;
        this.ThreatToTheKing = false;
        this.AlmostThreatToTheKing = false;
    }

    Position GetPosition() {return this.PiecePosition;}

    // this i and j(kingPos) just to make the test more faster
    private int i, kingPos;

    // Test if this piece is threat or almost threat to the king
    boolean IsThreatOrAlmostToTheKing(Position urKingPos){
        this.ThreatToTheKing = this.IsThreatToTheKing(urKingPos);
        if (!this.ThreatToTheKing)
            this.AlmostThreatToTheKing = this.IsAlmostThreatToTheKing(urKingPos);
        return this.ThreatToTheKing;
    }

    // test if this piece is a threat to the other player king
    private boolean IsThreatToTheKing(Position urKingPos){
        for(int i = 0; i < moves.size(); i++)
            for(int j = 0; j < pathesEnd.get(i);j++)
                if (IfTheKing(i, j, urKingPos))
                    return true;
        return false;
    }

    //Compare the one move from this piece moves with the positions of the king
    private boolean IfTheKing(int i, int j, Position urKingPos){
        if (moves.get(i).get(j).Equal(urKingPos))
        {
            this.i = i;
            this.kingPos = j;
            return true;
        }
        return false;
    }

    // test if this piece is a threat to the other player king but the king is protected by a piece
    private boolean IsAlmostThreatToTheKing(Position urKingPos){
        if (CanBeThreatToTheKing)
            for(int i = 0; i < moves.size(); i++)
                for(int j = 0; j < this.moves.get(i).size();j++)
                    if (IfTheKing(i, j, urKingPos))
                        return true;
        return false;
    }

    // get the moves of the piece that protect the king
    List<List<Position>> GetMovesIfIsProtectTheKing(List<List<Position>> protectorMoves, Position protectorMovesPosition, boolean Check){
        if(Check && this.AlmostThreatToTheKing)
            return new ArrayList<>();
        if (this.ThreatToTheKing)
            return GetPositionCanProtectFrom(protectorMoves);
        if (this.AlmostThreatToTheKing)
            for(int j = 0; j < this.kingPos;j++)
                if (moves.get(this.i).get(j).Equal(protectorMovesPosition))
                    return GetPositionCanProtectFrom(protectorMoves);
        return null;
    }

    // short method for get the position of a piece can protect from
    private List<List<Position>> GetPositionCanProtectFrom(List<List<Position>> protectorMoves){
        List<List<Position>> listMoves = new ArrayList<>();
        for(int i = 0;i < protectorMoves.size(); i++){
            listMoves.add(new ArrayList<Position>());
            for (int k = 0; k < protectorMoves.get(i).size(); k++)
                if (this.PiecePosition.Equal(protectorMoves.get(i).get(k)))
                    listMoves.get(i).add(protectorMoves.get(i).get(k));
                else
                    if (CanBeThreatToTheKing)
                        for (int l = 0; l < this.kingPos; l++)
                            if (moves.get(this.i).get(l).Equal(protectorMoves.get(i).get(k)))
                                listMoves.get(i).add(protectorMoves.get(i).get(k));
        }
        return listMoves;
    }

    // check if this piece can move to a position given
    boolean CanMoveToIt(Position position, boolean Check){
        for(int i = 0; i < moves.size(); i++)
        {
            int pathEnd = (Check) ? moves.get(i).size() : this.pathesEnd.get(i);
            for (int j = 0; j < pathEnd; j++)
                if (moves.get(i).get(j).Equal(position))
                    return true;
        }
        return false;
    }
}
