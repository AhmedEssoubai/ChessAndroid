package TheGame.GameAndTurns;

import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;
import TheGame.ThePlayer.PlayerType;

/**
 * Created by AHMED on 08/05/2018.
 */

final class PiecesEvaluation
{
    /*
        this class help the A.I for evaluation the pieces from there position on the
        board.
        the evaluation of position is add to evaluation of the piece
     */

    // <editor-fold desc="Evaluation Tables">

    private static int[][] PawnEvaluation = new int[][]
            {
                new int[]{0,  0,  0,  0,  0,  0,  0,  0},
                new int[]{50, 50, 50, 50, 50, 50, 50, 50},
                new int[]{10, 10, 20, 30, 30, 20, 10, 10},
                new int[]{5,  5, 10, 25, 25, 10,  5,  5},
                new int[]{0,  0,  0, 20, 20,  0,  0,  0},
                new int[]{5, -5,-10,  0,  0,-10, -5,  5},
                new int[]{5, 10, 10,-20,-20, 10, 10,  5},
                new int[]{0,  0,  0,  0,  0,  0,  0,  0}
            };
    private static int[][] KnightEvaluation = new int[][]
            {
                    new int[]{-50,-40,-30,-30,-30,-30,-40,-50},
                    new int[]{-40,-20,  0,  0,  0,  0,-20,-40},
                    new int[]{-30,  0, 10, 15, 15, 10,  0,-30},
                    new int[]{-30,  5, 15, 20, 20, 15,  5,-30},
                    new int[]{-30,  0, 15, 20, 20, 15,  0,-30},
                    new int[]{-30,  5, 10, 15, 15, 10,  5,-30},
                    new int[]{-40,-20,  0,  5,  5,  0,-20,-40},
                    new int[]{-50,-40,-30,-30,-30,-30,-40,-50}
            };
    private static int[][] BishopEvaluation = new int[][]
            {
                    new int[]{-20,-10,-10,-10,-10,-10,-10,-20},
                    new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                    new int[]{-10,  0,  5, 10, 10,  5,  0,-10},
                    new int[]{-10,  5,  5, 10, 10,  5,  5,-10},
                    new int[]{-10,  0, 10, 10, 10, 10,  0,-10},
                    new int[]{-10, 10, 10, 10, 10, 10, 10,-10},
                    new int[]{-10,  5,  0,  0,  0,  0,  5,-10},
                    new int[]{-20,-10,-10,-10,-10,-10,-10,-20}
            };
    private static int[][] RookEvaluation = new int[][]
            {
                    new int[]{0,  0,  0,  0,  0,  0,  0,  0},
                    new int[]{5, 10, 10, 10, 10, 10, 10,  5},
                    new int[]{-5,  0,  0,  0,  0,  0,  0, -5},
                    new int[]{-5,  0,  0,  0,  0,  0,  0, -5},
                    new int[]{-5,  0,  0,  0,  0,  0,  0, -5},
                    new int[]{-5,  0,  0,  0,  0,  0,  0, -5},
                    new int[]{-5,  0,  0,  0,  0,  0,  0, -5},
                    new int[]{0,  0,  0,  5,  5,  0,  0,  0}
            };
    private static int[][] QueenEvaluation = new int[][]
            {
                    new int[]{-20,-10,-10, -5, -5,-10,-10,-20},
                    new int[]{-10,  0,  0,  0,  0,  0,  0,-10},
                    new int[]{-10,  0,  5,  5,  5,  5,  0,-10},
                    new int[]{-5,  0,  5,  5,  5,  5,  0, -5},
                    new int[]{0,  0,  5,  5,  5,  5,  0, -5},
                    new int[]{-10,  5,  5,  5,  5,  5,  0,-10},
                    new int[]{-10,  0,  5,  0,  0,  0,  0,-10},
                    new int[]{-20,-10,-10, -5, -5,-10,-10,-20}
            };
    private static int[][] KingEvaluation = new int[][]
            {
                    new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
                    new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
                    new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
                    new int[]{-30,-40,-40,-50,-50,-40,-40,-30},
                    new int[]{-20,-30,-30,-40,-40,-30,-30,-20},
                    new int[]{-10,-20,-20,-20,-20,-20,-20,-10},
                    new int[]{20, 20,  0,  0,  0,  0, 20, 20},
                    new int[]{20, 30, 10,  0,  0, 10, 30, 20}
            };

    // </editor-fold>

    static int getPieceEvaluation(Piece piece, PlayerType playerType)
    {
        int evaluation = piece.GetEvaluation();
        PieceType pieceType = piece.GetType();
        switch (pieceType)
        {
            case Pawn:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), PawnEvaluation, playerType);
                break;
            case Knight:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), KnightEvaluation, playerType);
                break;
            case Bishop:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), BishopEvaluation, playerType);
                break;
            case Rook:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), RookEvaluation, playerType);
                break;
            case Queen:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), QueenEvaluation, playerType);
                break;
            case King:
                evaluation += getPositionEvaluation(piece.GetPositions().GetX(), piece.GetPositions().GetY(), KingEvaluation, playerType);
                break;
        }
        return evaluation;
    }

    private static int getPositionEvaluation(int x, int y, int[][] positionEvaluationTable,PlayerType playerType)
    {
        if (playerType == PlayerType.White)
            return positionEvaluationTable[y][x];
        return positionEvaluationTable[7 - y][x];
    }
}
