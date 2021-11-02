package TheGame.GameAndTurns;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.MatchMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.PlayerType;

/**
 * Created by AHMED on 12/05/2018.
 */

public class Match implements Serializable{
    private Date datetime;
    private MatchMode matchMode;
    private int time, result;

    public Match(MatchMode matchMode, int time) {
        datetime = new Date();
        this.matchMode = matchMode;
        this.time = time;
        turns = new ArrayList<>();
        result = 2;
    }

    // get the notation of this match
    public String GetNotation()
    {
        String notation = "";
        for(int i = 0; i < getMovesCount() - 1; i++)
            notation = String.format("%s%s%s", notation, (i % 2 == 0) ? String.format("%s%s. ", (i > 0) ? "\n" : "", i / 2 + 1) : "     ", getMove(i + 1).GetNotation());
        return notation;
    }

    // The moves that occurred in this game
    protected List<MatchTurn> turns;

    public List<MatchTurn> getTurns()
    {
        return turns;
    }

    public int getMovesCount()
    {
        return turns.size();
    }

    public Move getMove(int index)
    {
        return (index >= turns.size() || index < 0) ? null : turns.get(index).move;
    }

    public Move LastMove()
    {
        return (turns.size() == 0) ? null : turns.get(turns.size() - 1).move;
    }

    public void addMove(Move move, int[][] board, PlayerType turn, PieceType[] capturedPieces1, PieceType[] capturedPieces2)
    {
        turns.add(new MatchTurn(board, move, turn, capturedPieces1, capturedPieces2));
    }

    public PlayerType getTurn(int index)
    {
        return turns.get(index).turn;
    }

    public int getBox(int x, int y, int index)
    {
        return turns.get(index).getBox(x, y);
    }

    public void updateBox(int x, int y, int index, int value)
    {
        turns.get(index).setBox(x, y, value);
    }

    public Date getDatetime() {
        return datetime;
    }

    public MatchMode getMatchMode() {
        return matchMode;
    }

    public int getTime() {
        return time;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public PieceType[] getCapturedPieces1(int index) {
        return turns.get(index).getCapturedPieces1();
    }

    public PieceType[] getCapturedPieces2(int index) {
        return turns.get(index).getCapturedPieces2();
    }

    private class MatchTurn implements Serializable{
        Move move;
        private int[][] board;
        PlayerType turn;
        private PieceType[] capturedPieces1, capturedPieces2;

        MatchTurn(int[][] board, Move move, PlayerType turn, PieceType[] capturedPieces1, PieceType[] capturedPieces2)
        {
            this.board = board;
            this.move = move;
            this.turn = turn;
            this.capturedPieces1 = capturedPieces1;
            this.capturedPieces2 = capturedPieces2;
        }

        int getBox(int x, int y)
        {
            return board[y][x];
        }

        void setBox(int x, int y, int index)
        {
            board[y][x] = index;
        }

        PieceType[] getCapturedPieces1() {
            return capturedPieces1;
        }

        PieceType[] getCapturedPieces2() {
            return capturedPieces2;
        }
    }
}
