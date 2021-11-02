package TheGame.GameAndTurns;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import TheGame.ThePlayer.PieceMoves;
import TheGame.ThePlayer.Pieces.Bishop;
import TheGame.ThePlayer.Pieces.King;
import TheGame.ThePlayer.Pieces.Knight;
import TheGame.ThePlayer.Pieces.Pawn;
import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.Pieces.Queen;
import TheGame.ThePlayer.Pieces.Rook;
import TheGame.ThePlayer.Player;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 07/04/2018.
 */

class Turn implements Serializable {
    Player player1, player2;
    protected int turn;
    Piece pieceAboutToMove;

    Turn(int time){
        this.player1 = new Player(PlayerType.White, time);
        this.player2 = new Player(PlayerType.Black, time);
        this.turn = 1;
        this.promotion = false;
        this.draw = false;
    }

    // get possible moves of a piece and save the piece to move it later
    List<Position>[] GetPiecePlayerMoves(Piece piece)
    {
        List<Position>[] listMoves;
        if (turn == 1)
            listMoves = this.player1.GetPieceMoves(piece, this.player2);
        else
            listMoves = this.player2.GetPieceMoves(piece, this.player1);
        if (listMoves != null)
            this.pieceAboutToMove = piece;
        return listMoves;
    }

    // get the positions of the piece about to move
    Position GetPositionsPieceMoving()
    {
        return this.pieceAboutToMove.GetPositions();
    }

    // make a player move
    private Move move;
    Move MakeMove(Position position, Piece capturePiece)
    {
        if (capturePiece != null)
            if (turn == 1)
                this.ShortMethodForCapture(capturePiece, this.player2, this.player1);
            else
                this.ShortMethodForCapture(capturePiece, this.player1, this.player2);
        Position piecePosition = null;
        if (this.pieceAboutToMove != null)
        {
            piecePosition = this.pieceAboutToMove.GetPositions();
            if (getPlayerTurn().MakeMove(this.pieceAboutToMove, position)){
                this.promotion = true;
                this.promotionPawn = (Pawn) this.pieceAboutToMove;
            }
        }
        move = new Move(piecePosition, position, this.pieceAboutToMove == null ? null : this.pieceAboutToMove.GetType(), capturePiece != null, getPlayerTurn().getCastling(), (turn == 1) ? player1.getTime() : player2.getTime());
        if (turn == 1)
            this.player2.NotEnPassant();
        else
            this.player1.NotEnPassant();
        this.turn = this.turn == 1 ? 2 : 1;
        if (!this.promotion)
            callCheckTest();
        return move;
    }

    // short method for test check and save the result to the MOVE
    private void callCheckTest()
    {
        this.CheckTest();
        move.setCheck(IsThereACheck());
        move.setCheckMat(IsThereACheckMat());
        if (!move.isCheck())
        {
            Player player = getPlayerTurn();
            int movesCount = 0;
            for (int i = 0; i < player.GetPiecesCount(); i++) {
                List<Position>[] listMoves = GetPiecePlayerMoves(player.GetPiece(i));
                movesCount += listMoves[0].size() + listMoves[1].size();
            }
            if (movesCount == 0) {
                move.setDraw(true);
                draw = true;
            }
        }
    }

    // This turn is end by draw if true
    private boolean draw;

    boolean isDraw() {
        return draw;
    }

    // get turn
    PlayerType GetTurn()
    {
        if (this.turn == 1)
            return PlayerType.White;
        else
            return PlayerType.Black;
    }

    // Promotion this pawn to more valuable piece
    private boolean promotion;
    boolean Promotion()
    {
        return this.promotion;
    }

    private Pawn promotionPawn;
    void PromotionPawn(PieceType pieceType)
    {
        if (turn == 1)
            this.player2.Promotion(promotionPawn, pieceType);
        else
            this.player1.Promotion(promotionPawn, pieceType);
        this.promotionPawn = null;
        this.promotion = false;
        callCheckTest();
    }

    Position getPromotionPawnPositions()
    {
        return promotionPawn.GetPositions();
    }

    private void ShortMethodForCapture(Piece piece, Player owner, Player newOwner)
    {
        owner.LostPiece(piece);
        newOwner.Captured(piece);
    }

    // <editor-fold desc="Check and CheckMat">

    private void CheckTest()
    {
        if (this.turn == 1)
            this.player1.CheckTest(GetPiecesMoves(player2, player1));
        else
            this.player2.CheckTest(GetPiecesMoves(player1, player2));
    }

    private List<PieceMoves> GetPiecesMoves(Player player, Player discountPlayer){
        List<PieceMoves> pieceMovesList = new ArrayList<>();
        for(int i = 0; i < player.GetPiecesCount(); i++)
            pieceMovesList.add(player.GetPieceMoves(i, discountPlayer));
        return pieceMovesList;
    }

    boolean IsThereACheck()
    {
        return getPlayerTurn().IsInCheck();
    }

    boolean IsThereACheckMat()
    {
        return getPlayerTurn().IsInCheckMat();
    }

    // </editor-fold>

    // get the captured pieces of each player
    PieceType[] GetCapturedPieces(PlayerType player)
    {
        return (player == PlayerType.White) ? player1.GetCapturedPieces() : player2.GetCapturedPieces();
    }

    // get the value of captured pieces of each player
    int PiecesValue(PlayerType player)
    {
        return (player == PlayerType.White) ? player1.PiecesValue() : player2.PiecesValue();
    }

    // <editor-fold desc="Clock Timer">

    int getTime(PlayerType player)
    {
        return (player == PlayerType.White) ? player1.getTime() : player2.getTime();
    }

    int timeDown()
    {
        if (turn == 1)
            return player1.timeDown() ? 1 : 0;
        else
            return player2.timeDown() ? 2 : 0;
    }

    // </editor-fold>

    // get a copy of this game (new game has the same properties of this game)
    public Turn GetCopy()
    {
        Turn copy = new Turn(0);
        copy.turn = this.turn;
        copy.player1 = this.player1.GetCopy();
        copy.player2 = this.player2.GetCopy();
        copy.promotion = this.promotion;
        copy.pieceAboutToMove = this.pieceAboutToMove;
        copy.move = this.move;
        return copy;
    }

    // get the player who will play in this turn
    protected Player getPlayerTurn()
    {
        return (turn == 1) ? player1 : player2;
    }
}
