package TheGame.GameAndTurns;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.Player;
import TheGame.ThePlayer.PlayerType;
import TheGame.ThePlayer.Position;

/**
 * Created by AHMED on 06/05/2018.
 */

final class AI
{
    private static Piece capturePiece;

    static Position getMove(Turn turn, int depth)
    {
        Move move = (new AI_Turn(turn, depth, true, turn.GetTurn())).getMove();
        turn.pieceAboutToMove = AI_Turn.getPieceAboutToMove(move.getFrom(), turn);
        capturePiece = AI_Turn.getCapturePiece(move.getTo(), turn);
        return move.getTo();
    }

    static Piece getCapturePiece()
    {
        return capturePiece;
    }
}

class AI_Turn extends Turn
{
    AI_Turn(Turn turn, int depth, boolean TheStart, PlayerType playerType)
    {
        super(0);
        this.depth = depth;
        this.turn = turn.turn;
        this.player1 = turn.player1.GetCopy();
        this.player2 = turn.player2.GetCopy();
        Player player = turn.getPlayerTurn();
        if (turn.pieceAboutToMove != null)
            for (int i = 0; i < player.GetPiecesCount(); i++)
                if (turn.pieceAboutToMove == player.GetPiece(i))
                {
                    pieceAboutToMove = getPlayerTurn().GetPiece(i);
                    break;
                }
        this.playerType = playerType;
        if (TheStart)
            Prospects(true);
    }

    //
    private void AI_MakeMove(Position position, Piece capturePiece)
    {
        move = MakeMove(position, capturePiece);
        if (this.Promotion())
            this.PromotionPawn(PieceType.Queen);
        Prospects(false);
    }

    //
    private void Prospects(boolean TheStart)
    {
        if (depth > 0)
        {
            List<AI_Turn> ai_turns = new ArrayList<>();
            if (!this.IsThereACheckMat() && !this.isDraw())
            {
                Player player = getPlayerTurn();
                for (int i = 0; i < player.GetPiecesCount(); i++) {
                    List<Position>[] listMoves = GetPiecePlayerMoves(player.GetPiece(i));
                    if (listMoves != null)
                        for (int j = 0; j < listMoves.length; j++)
                            for (int k = 0; k < listMoves[j].size(); k++)
                            {
                                ai_turns.add(new AI_Turn(this, depth - 1, false, playerType));
                                ai_turns.get(ai_turns.size() - 1).AI_MakeMove(listMoves[j].get(k), getCapturePiece(listMoves[j].get(k), j));
                            }
                }
            }
            List<Integer> ValuesIndex = new ArrayList<>();
            if (ai_turns.size() == 0)
            {
                if (this.IsThereACheckMat())
                    value = -20000;
                else
                    value = 0;
            }
            else
            {
                ValuesIndex.add(0);
                for (int i = 1; i < ai_turns.size(); i++)
                    if (ai_turns.get(ValuesIndex.get(0)).value == ai_turns.get(i).value)
                        ValuesIndex.add(i);
                    else if (CompareValues(ai_turns.get(ValuesIndex.get(0)).value, ai_turns.get(i).value)) {
                        ValuesIndex = new ArrayList<>();
                        ValuesIndex.add(i);
                    }
                value = ai_turns.get(ValuesIndex.get(0)).value;
                if (TheStart)
                    move = ai_turns.get(ValuesIndex.get((new Random()).nextInt(ValuesIndex.size()))).move;
            }
        }
        else
        {
            if (this.IsThereACheckMat())
                value = -20000;
            else
            {
                value = getPiecesValue(player1) - getPiecesValue(player2);
                if (turn == 2)
                    value = -value;
                if (this.IsThereACheck())
                    value -= 19500;
            }
        }
    }

    private int getPiecesValue(Player player)
    {
        int value = 0;
        for (int i = 0; i < player.GetPiecesCount(); i++)
            value += PiecesEvaluation.getPieceEvaluation(player.GetPiece(i), player.GetType());
        return value;
    }

    // Compare the value of the turns
    private boolean CompareValues(int oldValue, int value)
    {
        if (playerType == PlayerType.White)
        {
            if (turn == 1)
                return oldValue > value;
            else
                return oldValue < value;
        }
        else
            if (turn == 1)
                return oldValue < value;
            else
                return oldValue > value;
    }

    // the captured piece if there is one, in the board we get the captured piece from a box
    private Piece getCapturePiece(Position position, int type)
    {
        if (type == 1)
            return getCapturePiece(position, this);
        return null;
    }

    Move getMove()
    {
        return move;
    }

    private int value, depth;
    private Move move;
    private PlayerType playerType;

    static Piece getCapturePiece(Position position, Turn turn)
    {
        return getPiece(position, (turn.turn == 1) ? turn.player2 : turn.player1);
    }

    static Piece getPieceAboutToMove(Position position, Turn turn)
    {
        return getPiece(position, turn.getPlayerTurn());
    }

    private static Piece getPiece(Position position, Player player)
    {
        for (int i = 0; i < player.GetPiecesCount(); i++)
        {
            Piece piece = player.GetPiece(i);
            if (position.Equal(piece.GetPositions()))
                return player.GetPiece(i);
        }
        return null;
    }
}
