package TheGame.ThePlayer;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import TheGame.ThePlayer.Pieces.Bishop;
import TheGame.ThePlayer.Pieces.Castling;
import TheGame.ThePlayer.Pieces.King;
import TheGame.ThePlayer.Pieces.Knight;
import TheGame.ThePlayer.Pieces.Pawn;
import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.Pieces.Protector;
import TheGame.ThePlayer.Pieces.Queen;
import TheGame.ThePlayer.Pieces.Rook;

/**
 * Created by AHMED on 07/04/2018.
 */

public class Player implements Serializable {

    public Player(PlayerType type, int time)
    {
        this.type = type;
        this.time = time * 60;
        this.MyPieces = new ArrayList<>();
        int r = type == PlayerType.Black ? 0 : 7;
        this.MyPieces.add(new King(new Position(4, r)));
        this.MyPieces.add(new Queen(new Position(3, r)));
        this.MyPieces.add(new Rook(new Position(0, r)));
        this.MyPieces.add(new Rook(new Position(7, r)));
        this.MyPieces.add(new Bishop(new Position(2, r)));
        this.MyPieces.add(new Bishop(new Position(5, r)));
        this.MyPieces.add(new Knight(new Position(1, r)));
        this.MyPieces.add(new Knight(new Position(6, r)));
        for (int i = 0; i < 8; i++)
            this.MyPieces.add(new Pawn(new Position(i, type == PlayerType.Black ? 1 : 6), this.GetDirection()));
        this.DiscountPieces = new ArrayList<>();
        this.Check = false;
        this.CheckMat = false;
        this.ThreatPiece = null;
        this.castling = Castling.None;
    }

    // the clock time of this player, with sec
    private int time;

    public int getTime()
    {
        return time;
    }

    public boolean timeDown()
    {
        if (time > 0)
            time--;
        return (time == 0);
    }

    // <editor-fold desc="Check and CheckMat">

    // The player is in check or not
    private boolean Check;
    // The king of this player is besieged : it's mean this player is lost the game
    private boolean CheckMat;
    // The pieces(only there positions) the make threat to the king
    private Position ThreatPiece;

    public Position GetThreatPiece(){
        return this.ThreatPiece;
    }

    public void CheckTest(List<PieceMoves> discountMoves)
    {
        this.Check = false;
        this.ThreatPiece = null;
        int kingIndex = this.GetKingIndex();
        Position kingPos = MyPieces.get(kingIndex).GetPositions();
        // Check for "Check"(Threat to this player king)
        for (int i = 0; i < discountMoves.size(); i++)
            if (discountMoves.get(i).IsThreatOrAlmostToTheKing(kingPos)) {
                this.ThreatPiece = discountMoves.get(i).GetPosition();
                this.Check = true;
            }
        // Search for a protectors for the king and set there moves
        for (int i = 0; i < discountMoves.size(); i++)
            for(int j = 0; j < this.MyPieces.size(); j++) {
                Protector protector = null;
                try {
                    protector = (Protector) this.MyPieces.get(j);
                }
                catch (Exception ee){}
                if (protector != null)
                    protector.Protect(discountMoves.get(i).GetMovesIfIsProtectTheKing(this.GetPieceMoves(j, false), protector.GetPositions(), this.Check));
            }
        // Set the moves of the king
        List<List<Position>> kingListMoves = this.GetPieceMoves(kingIndex, false);
        List<List<Position>> listMoves = new ArrayList<>();
        for (int i = 0; i < kingListMoves.size(); i++){
            listMoves.add(new ArrayList<Position>());
            for (int j = 0; j < kingListMoves.get(i).size(); j++){
                int l;
                for(l = 0; l < discountMoves.size(); l++)
                    if(!this.Check || j != 1)
                        if (discountMoves.get(l).CanMoveToIt(kingListMoves.get(i).get(j), this.Check))
                            break;
                if (l == discountMoves.size())
                    listMoves.get(i).add(kingListMoves.get(i).get(j));
                else
                    break;
            }
        }
        ((King)MyPieces.get(kingIndex)).NewPossitions(listMoves);
        if (this.Check)
        {
            int countMoves = 0; // the count of moves can this player do it, if is 0 mean is CheckMat
            for (int i = 0; i < this.MyPieces.size(); i++)
                countMoves += this.MyPieces.get(i).MovesCount();
            if (countMoves == 0)
                this.CheckMat = true;
        }
    }

    public boolean IsInCheck()
    {
        return this.Check;
    }

    public boolean IsInCheckMat()
    {
        return this.CheckMat;
    }

    //Exemption of pieces from the protection of the king
    private void ExemptionPiecesFromProtectTheKing()
    {
        for(int i = 0; i < this.MyPieces.size(); i++)
            if (MyPieces.get(i) instanceof Protector)
                ((Protector)this.MyPieces.get(i)).NoLongerAProtector();
    }

    // get the moves of a piece for check
    public PieceMoves GetPieceMoves(int index, Player discountPlayer)
    {
        if (MyPieces.get(index) instanceof King)
            ((King)MyPieces.get(index)).NewPossitions(null);
        List<List<Position>> listMoves = this.GetPieceMoves(index, true);
        List<List<Position>> pieceMoves = new ArrayList<>();
        List<Integer> pathesEnd = new ArrayList<>();
        for (int i = 0; i < listMoves.size(); i++) {
            pieceMoves.add(new ArrayList<Position>());
            pathesEnd.add(-1);
            for (int j = 0; j < listMoves.get(i).size(); j++) {
                if (this.MyPieces.get(index) instanceof Pawn) {
                    if (i == 0)
                        break;
                    else /*if (PositionsCompare(listMoves.get(i).get(j)[0], listMoves.get(i).get(j)[1], discountPlayer.MyPieces))*/ {
                        pieceMoves.get(i).add(listMoves.get(i).get(j));
                        break;
                    }
                } else {
                    if (PositionsCompare(listMoves.get(i).get(j), discountPlayer.MyPieces)) {
                        pieceMoves.get(i).add(listMoves.get(i).get(j));
                        if (pathesEnd.get(i) == -1)
                            pathesEnd.set(i, j + 1);
                        else
                            break;
                    } else
                        pieceMoves.get(i).add(listMoves.get(i).get(j));
                }
            }
            if (pathesEnd.get(i) == -1)
                pathesEnd.set(i, pieceMoves.get(i).size());
        }
        return new PieceMoves((this.MyPieces.get(index) instanceof Queen || this.MyPieces.get(index) instanceof Rook || this.MyPieces.get(index) instanceof Bishop), this.MyPieces.get(index).GetPositions(), pieceMoves, pathesEnd);
    }

    // </editor-fold>

    // The type of this player
    private PlayerType type;
    public PlayerType GetType()
    {
        return this.type;
    }

    // Get the Direction of the player Up(-1) or Down(1)
    private int GetDirection()
    {
        if (this.type == PlayerType.Black)
            return 1;
        else
            return -1;
    }

    // The pieces of this player
    private List<Piece> MyPieces;
    public Piece GetPiece(int index)
    {
        if (index < 0 || index >= this.MyPieces.size())
            return null;
        else
            return this.MyPieces.get(index);
    }

    // Promotion one pawn of this player to more valuable piece
    public void Promotion(Pawn pawn, PieceType pieceType)
    {
        if (this.MyPieces.contains(pawn))
        {
            if (pieceType == PieceType.Queen)
                this.MyPieces.add(new Queen(pawn.GetPositions()));
            else if (pieceType == PieceType.Knight)
                this.MyPieces.add(new Knight(pawn.GetPositions()));
            else if (pieceType == PieceType.Rook)
                this.MyPieces.add(new Rook(pawn.GetPositions()));
            else if (pieceType == PieceType.Bishop)
                this.MyPieces.add(new Bishop(pawn.GetPositions()));
            this.MyPieces.remove(pawn);
        }
    }

    // The pieces this player Captured
    private List<PieceType> DiscountPieces;
    public PieceType[] GetCapturedPieces()
    {
        PieceType[] Tab = new PieceType[this.DiscountPieces.size()];
        for (int i = 0; i < Tab.length; i++)
            Tab[i] = this.DiscountPieces.get(i);
        return Tab;
    }

    // get the value of the pieces of this player
    public int PiecesValue()
    {
        int value = 0;
        for (int i = 0; i < MyPieces.size(); i++)
            value += this.MyPieces.get(i).GetValue();
        return value;
    }

    // Add new Captured pieces to this player
    public void Captured(Piece piece)
    {
        PieceType index = this.GetPieceType(piece);
        if (index != null)
            this.DiscountPieces.add(index);
    }

    // the player lose one of his chess pieces
    public void LostPiece(Piece piece)
    {
        this.MyPieces.remove(piece);
    }

    // get the king piece of this player
    public Piece GetKing()
    {
        for (int i = 0; i < this.MyPieces.size(); i++)
            if (this.MyPieces.get(i) instanceof King)
                return this.MyPieces.get(i);
        return null;
    }

    // get the index of the king piece of this player
    private int GetKingIndex()
    {
        for (int i = 0; i < this.MyPieces.size(); i++)
            if (this.MyPieces.get(i) instanceof King)
                return i;
        return -1;
    }

    // The count of player pieces
    public int GetPiecesCount()
    {
        return this.MyPieces.size();
    }

    // get the moves of one of this player piece
    public List<Position>[] GetPieceMoves(Piece piece, Player discountPlayer)
    {
        int index = -1;
        for(int i = 0; i < this.MyPieces.size(); i++)
            if (this.MyPieces.get(i) == piece)
            {
                index = i;
                break;
            }
        if (index == -1)
            return null;
        List<List<Position>> listMoves = new ArrayList<>();
        if (!this.Check || ((piece instanceof King) ? true : ((Protector)piece).IsProtector()))
            listMoves = this.GetPieceMoves(index, false);
        List<Position>[] pieceMoves = new List[]{new ArrayList<Position>(), new ArrayList<Position>()};
        for (int i = 0; i < listMoves.size(); i++)
            for (int j = 0; j < listMoves.get(i).size(); j++)
            {
                if (piece instanceof Pawn)
                {
                    if (i == 0)
                    {
                        if (!PositionsCompare(listMoves.get(i).get(j), discountPlayer.MyPieces))
                            pieceMoves[0].add(listMoves.get(i).get(j));
                        else
                            break;
                    }
                    else
                        if (PositionsCompare(listMoves.get(i).get(j), discountPlayer.MyPieces))
                        {
                            pieceMoves[1].add(listMoves.get(i).get(j));
                            break;
                        }
                        else
                            for (int k = 0; k < discountPlayer.MyPieces.size(); k++)
                                if (discountPlayer.MyPieces.get(k) instanceof Pawn)
                                    if (((Pawn) discountPlayer.MyPieces.get(k)).CanCapturedEnPassant())
                                        if (listMoves.get(i).get(j).GetX() == discountPlayer.MyPieces.get(k).GetPositions().GetX() && piece.GetPositions().GetY() == discountPlayer.MyPieces.get(k).GetPositions().GetY()){
                                            pieceMoves[1].add(listMoves.get(i).get(j));
                                            break;
                                        }
                }
                else
                {
                    boolean castling = false;
                    if (piece instanceof King)
                        if (j == 1)
                            if (CanKingCastling((i == 0) ? Castling.QueenSide : Castling.KingSide))
                                castling = true;
                            else
                                break;
                    if (PositionsCompare(listMoves.get(i).get(j), discountPlayer.MyPieces))
                    {
                        if (!castling)
                            pieceMoves[1].add(listMoves.get(i).get(j));
                        break;
                    }
                    else
                        pieceMoves[0].add(listMoves.get(i).get(j));
                }
            }
        return pieceMoves;
    }

    // test if there is a castling
    private boolean CanKingCastling(Castling castling){
        return GetCastlingRook(castling) != null;
    }

    // short method to get the castling rook
    private Rook GetCastlingRook(Castling castling){
        int x = (castling == Castling.KingSide) ? 7 : 0;
        for(int i = 0; i < this.MyPieces.size(); i++)
            if (this.MyPieces.get(i) instanceof Rook) {
                Rook rook = (Rook) this.MyPieces.get(i);
                if (rook.IsNeverMoved() && rook.GetPositions().GetX() == x)
                    return rook;
            }
        return null;
    }

    //The method depends on which other methods to bring movements of a piece
    private List<List<Position>> GetPieceMoves(int index, boolean ForCheck)
    {
        List<List<Position>> listMoves = MyPieces.get(index).GetMoves();
        List<List<Position>> pieceMoves = new ArrayList<>();
        for (int i = 0; i < listMoves.size(); i++)
        {
            pieceMoves.add(new ArrayList<Position>());
            for (int j = 0; j < listMoves.get(i).size(); j++)
            {
                if (!PositionsCompare(listMoves.get(i).get(j), this.MyPieces))
                    pieceMoves.get(i).add(listMoves.get(i).get(j));
                else
                {
                    if (ForCheck)
                        pieceMoves.get(i).add(listMoves.get(i).get(j));
                    break;
                }
            }
        }
        return pieceMoves;
    }

    // short method for GetPieceMoves using to compare between the positions
    private boolean PositionsCompare(Position position, List<Piece> pieceList)
    {
        for (int i = 0; i < pieceList.size(); i++)
            if (position.Equal(pieceList.get(i).GetPositions()))
                return true;
        return false;
    }

    // get the equal index of a piece
    private PieceType GetPieceType(Piece piece)
    {
        if (piece == null)
            return null;
        if (piece instanceof Pawn)
            return PieceType.Pawn;
        if (piece instanceof Knight)
            return PieceType.Knight;
        if (piece instanceof Bishop)
            return PieceType.Bishop;
        if (piece instanceof Rook)
            return PieceType.Rook;
        if (piece instanceof Queen)
            return PieceType.Queen;
        return PieceType.King;
    }

    // all paws of this player can't be captured by passing
    public void NotEnPassant()
    {
        for (int i = 0; i < this.MyPieces.size(); i++)
            if (this.MyPieces.get(i) instanceof Pawn)
                ((Pawn) this.MyPieces.get(i)).NotEnPassant();
    }

    // make the move of this player
    public boolean MakeMove(Piece piece, Position position){
        piece.MakeMove(position);
        if (piece instanceof King)
        {
            castling = ((King) piece).IsCastling();
            if (castling != Castling.None)
                GetCastlingRook(castling).MakeMove(new Position(position.GetX() + (castling == Castling.QueenSide ? 1 : -1), position.GetY()));
        }
        ExemptionPiecesFromProtectTheKing();
        if (piece instanceof Pawn)
            if (((Pawn) piece).Promotion())
                return true;
        return false;
    }

    // the type of castling happened in the last move
    private Castling castling;

    public Castling getCastling() { return castling; }

    // get a copy of this player (new player has the same properties of this player)
    public Player GetCopy()
    {
        Player copy = new Player(this.type, this.time);
        copy.Check = this.Check;
        copy.castling = this.castling;
        copy.time = this.time;
        copy.CheckMat = this.CheckMat;
        copy.MyPieces = new ArrayList<>();
        for (int i = 0; i < this.MyPieces.size(); i++)
            copy.MyPieces.add(this.MyPieces.get(i).GetCopy());
        copy.DiscountPieces.addAll(this.DiscountPieces);
        return copy;
    }
}
