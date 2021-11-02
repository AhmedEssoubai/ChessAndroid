package TheGame.GameAndTurns;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.MatchMode;

import java.io.Serializable;
import java.util.List;

import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.Player;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 05/05/2018.
 */

public class Game implements Serializable{

    /*
        - white level: the level of computer difficulty when he play with white pieces, = 0 is mean a Human who will play with white pieces
        - black level: the level of computer difficulty when he play with black pieces, = 0 is mean a Human who will play with black pieces
        - time: the time with minutes of the clock of each player, = -1 is mean there is no time
        - scoreLost: the score the player will lost if he play against the computer in Competition mode, = -1 if the mode is not "Competition"
     */
    private int whiteLevel, blackLevel, time, idMatch, Duration;
    private boolean competition;

    public int getWhiteLevel() {
        return whiteLevel;
    }

    public int getBlackLevel() {
        return blackLevel;
    }

    public boolean isCompetition() {
        return competition;
    }

    public int getTime() {
        return time;
    }

    public Game(int whiteLevel, int blackLevel, int time, boolean competition)
    {
        this.whiteLevel = whiteLevel;
        this.blackLevel = blackLevel;
        this.time = time;
        this.competition = competition;
        MatchMode matchMode = MatchMode.PlayerVsComputer;
        if (blackLevel == 0)
            matchMode = MatchMode.PlayerVsPlayer;
        else if (whiteLevel != 0)
                matchMode = MatchMode.ComputerVsComputer;
        else if (competition)
                matchMode = MatchMode.PlayerVsComputerComp;
        this.match = new Match(matchMode, time);
        this.turn = new Turn(time);
        this.Duration = 0;
        match.addMove(null, getBoard(), turn.GetTurn(), new PieceType[0], new PieceType[0]);
    }

    // The state the game is currently on
    private Turn turn;

    public Player getWhitePlayer()
    {
        return turn.player1;
    }

    public Player getBlackPlayer()
    {
        return turn.player2;
    }

    public void MakeMove(Position position, Piece capturePiece)
    {
        Move move = turn.MakeMove(position, capturePiece);
        PieceType[] capturedPieces1 = getWhitePlayer().GetCapturedPieces(), capturedPieces2 = getBlackPlayer().GetCapturedPieces();
        match.addMove(move, getBoard(), turn.GetTurn(), capturedPieces1, capturedPieces2);
        if (whiteLevel == 0 || blackLevel == 0)
            AI_Move();
    }

    // get a picture of the board
    private int[][] getBoard()
    {
        int[][] board = new int[8][8];
        DrawBoard(turn.player1, board);
        DrawBoard(turn.player2, board);
        return board;
    }

    // This game is end by draw or not
    public boolean isDraw() {
        return turn.isDraw();
    }

    // The duration of the game
    public void addSecond()
    {
        Duration++;
    }

    public void setIdMatch(int idMatch) {
        this.idMatch = idMatch;
    }

    public int getDuration()
    {
        return Duration;
    }

    // The Id of this match(game) if in competition mode
    public int getIdMatch()
    {
        return idMatch;
    }

    // short method for draw the board
    private void DrawBoard(Player player, int[][] board)
    {
        for (int i = 0; i < player.GetPiecesCount(); i++)
        {
            Piece piece = player.GetPiece(i);
            Position position = piece.GetPositions();
            board[position.GetY()][position.GetX()] = piece.GetIndex(player.GetType());
        }
    }

    // do the move by A.I
    public void AI_Move()
    {
        if (!Promotion())
        {
            if (!turn.IsThereACheckMat() && !turn.isDraw())
                if (turn.GetTurn() == PlayerType.Black) {
                    if (blackLevel > 0)
                        this.MakeMove(AI.getMove(turn, blackLevel * 2), AI.getCapturePiece());
                }
                else
                if (turn.GetTurn() == PlayerType.White)
                    if (whiteLevel > 0)
                        this.MakeMove(AI.getMove(turn, whiteLevel * 2), AI.getCapturePiece());
        }
        else
            AI_Promotion();
    }

    // do the computer Promotion
    private void AI_Promotion()
    {
        if (turn.GetTurn() == PlayerType.Black) {
            if (blackLevel > 0)
                this.PromotionPawn(PieceType.Queen);
        }
        else
        if (turn.GetTurn() == PlayerType.White)
            if (whiteLevel > 0)
                this.PromotionPawn(PieceType.Queen);
    }

    // the match have all the information about the game (the moves played, mode, time...etc)
    private Match match;

    public Move LastMove()
    {
        return (match.getMovesCount() == 0) ? null : match.LastMove();
    }

    public String GetNotation()
    {
        return match.GetNotation();
    }

    public PlayerType GetTurn()
    {
        return turn.GetTurn();
    }

    public boolean Promotion()
    {
        if (IsThereACheckMat())
            return false;
        return turn.Promotion();
    }

    public Match getMatch(){return match;}

    public void PromotionPawn(PieceType pieceType)
    {
        Position position = turn.getPromotionPawnPositions();
        this.match.updateBox(position.GetX(), position.GetY(), this.match.getMovesCount() - 1, Piece.indexOf(pieceType));
        turn.PromotionPawn(pieceType);
        this.match.LastMove().setPromotion(pieceType);
        AI_Move();
    }

    public Position GetPositionsPieceMoving()
    {
        return turn.GetPositionsPieceMoving();
    }

    public List<Position>[] GetPiecePlayerMoves(Piece piece)
    {
        return turn.GetPiecePlayerMoves(piece);
    }

    public boolean IsThereACheck()
    {
        return turn.IsThereACheck();
    }

    public boolean IsThereACheckMat()
    {
        return turn.IsThereACheckMat();
    }

    // get the captured pieces of each player
    public PieceType[] GetCapturedPieces(PlayerType player)
    {
        return turn.GetCapturedPieces(player);
    }

    // get the value of captured pieces of each player
    public int PiecesValue(PlayerType player)
    {
        return turn.PiecesValue(player);
    }

    // <editor-fold desc="Clock Timer">

    public int getTime(PlayerType player)
    {
        return turn.getTime(player);
    }

    public int timeDown()
    {
        return turn.timeDown();
    }

    // </editor-fold>
}
