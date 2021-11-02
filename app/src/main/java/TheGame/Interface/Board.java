package TheGame.Interface;

import android.app.Activity;
import android.app.FragmentManager;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.MatchMode;
import com.chess.nikiva.ahmed.chess.Interfaces.MainGame;
import com.chess.nikiva.ahmed.chess.Interfaces.MainPage;
import com.chess.nikiva.ahmed.chess.Interfaces.PromotionMenu;
import com.chess.nikiva.ahmed.chess.R;

import java.util.List;
import java.util.Vector;

import TheGame.GameAndTurns.Game;
import TheGame.GameAndTurns.Match;
import TheGame.GameAndTurns.Move;
import TheGame.ThePlayer.Pieces.Bishop;
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
 * Created by AHMED on 08/04/2018.
 */

public class Board {
    // board activity
    private MainGame activity;
    // the too players who play in this board
    private Game game;
    // the board boxes
    private Vector<Vector<Box>> boxes;
    // panel for promotion
    private PromotionMenu promotionMenu;
    private FragmentManager manager;
    // if true the user can't play
    private boolean watchMode;
    // text view show the witch player turn now and the other show the notation of this game
    private TextView tv_turn, tv_notation;
    // the views who contains the pieces captured
    private LinearLayout ll_wpieces, ll_bpieces;
    // text view show the value of the captured pieces each player have
    private TextView tv_wvalues, tv_bvalues;
    // the match we wanna reply if in the reply mode
    private MatchForReplay matchForReplay;

    public Board(MainGame activity, LinearLayout board, DisplayMetrics metrics, Game game, Match match){
        this.game = game;
        this.activity = activity;
        if (this.game == null)
        {
            this.watchMode = true;
            matchForReplay = new MatchForReplay(match);
        }
        else
        if (this.game.getMatch().getMatchMode() == MatchMode.ComputerVsComputer)
            this.watchMode = false;
        this.manager = activity.getFragmentManager();
        this.tv_turn = activity.findViewById(R.id.tv_turn);
        this.tv_notation = activity.findViewById(R.id.tv_notation);
        this.ll_wpieces = activity.findViewById(R.id.ll_wpieces);
        this.ll_bpieces = activity.findViewById(R.id.ll_bpieces);
        this.tv_wvalues = activity.findViewById(R.id.tv_wvalues);
        this.tv_bvalues = activity.findViewById(R.id.tv_bvalues);
        fillColors();
        this.newBoard(activity, board, metrics.widthPixels);
    }

    public void newGame(Game game)
    {
        this.game = game;
        this.matchForReplay = null;
        this.watchMode = false;
        this.DrawPieces();
        this.tv_notation.setText(game.GetNotation());
        this.SetCapturedPieces();
    }

    // write on the title who's turn now
    private void SetTurnOnTitle()
    {
        boolean white;
        if (game == null)
            white = (matchForReplay.getTurn(matchForReplay.index) == PlayerType.White);
        else
            white = (game.GetTurn() == PlayerType.White);
        if (white)
            this.tv_turn.setText((MainPage.getLang() == 1) ? R.string.fr_w_move : R.string.en_w_move);
        else
            this.tv_turn.setText((MainPage.getLang() == 1) ? R.string.fr_b_move : R.string.en_b_move);
    }

    // create new board
    private void newBoard(final Activity activity, LinearLayout board, int widthPixels)
    {
        this.boxes = new Vector<>();
        int width = widthPixels / 8;
        for (int y = 0; y < 8; y++)
        {
            LinearLayout row = new LinearLayout(activity);
            row.setLayoutParams(new LinearLayout.LayoutParams(GridLayout.LayoutParams.MATCH_PARENT, GridLayout.LayoutParams.WRAP_CONTENT));
            row.setOrientation(LinearLayout.HORIZONTAL);
            this.boxes.add(new Vector<Box>());
            for (int x = 0; x < 8; x++)
            {
                this.boxes.get(y).add(new Box(activity, width));
                this.boxes.get(y).get(x).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Board.this.onBoxClick((Box)v);
                    }
                });
                row.addView(this.boxes.get(y).get(x));
            }
            board.addView(row);
        }
        if (matchForReplay != null)
            DrawTurn();
        else
            this.DrawPieces();
    }

    // <editor-fold desc="draw the board">

    // draw the board from a turn in match replay
    public void DrawTurn()
    {
        this.DrawBoardColor();
        for (int y = 0; y < this.boxes.size(); y++)
            for (int x = 0; x < this.boxes.get(y).size(); x++)
            {
                PlayerType playerType = PlayerType.White;
                if (matchForReplay.getBox(x, y, matchForReplay.getIndex()) < 0)
                    playerType = PlayerType.Black;
                PieceType pieceType = Piece.typeOf(Math.abs(matchForReplay.getBox(x, y, matchForReplay.getIndex())));
                if (pieceType != null)
                    this.boxes.get(y).get(x).setImageResource(this.GetPieceImage(pieceType, playerType));
                else
                    this.boxes.get(y).get(x).setImageBitmap(null);
            }
        this.DrawCheckAndCheckMat();
        this.tv_notation.setText(matchForReplay.GetNotation());
        this.SetCapturedPieces();
        this.SetTurnOnTitle();
    }

    // draw the pieces on the board
    public void DrawPieces()
    {
        this.DrawBoardColor();
        for (int y = 0; y < this.boxes.size(); y++)
            for (int x = 0; x < this.boxes.get(y).size(); x++)
            {
                this.boxes.get(y).get(x).piece = null;
                this.boxes.get(y).get(x).setImageBitmap(null);
            }
        DrawPlayerPieces(game.getWhitePlayer());
        DrawPlayerPieces(game.getBlackPlayer());
        this.DrawCheckAndCheckMat();
        this.tv_notation.setText(game.GetNotation());
        this.SetCapturedPieces();
        this.SetTurnOnTitle();
    }

    // draw the pieces of one player
    private void DrawPlayerPieces(Player player)
    {
        int count = player.GetPiecesCount();
        for (int i = 0; i < count; i++) {
            Piece piece = player.GetPiece(i);
            Position pos = piece.GetPositions();
            this.boxes.get(pos.GetY()).get(pos.GetX()).piece = piece;
            this.boxes.get(pos.GetY()).get(pos.GetX()).setImageResource(this.GetPieceImage(piece, player.GetType()));
        }
    }

    // get piece image by instanceof piece
    private int GetPieceImage(Piece piece, PlayerType playerType)
    {
        if (piece instanceof Pawn)
            return playerType == PlayerType.Black ? R.drawable.bp : R.drawable.wp;
        else if (piece instanceof Knight)
            return playerType == PlayerType.Black ? R.drawable.bn : R.drawable.wn;
        else if (piece instanceof Bishop)
            return playerType == PlayerType.Black ? R.drawable.bb : R.drawable.wb;
        else if (piece instanceof Rook)
            return playerType == PlayerType.Black ? R.drawable.br : R.drawable.wr;
        else if (piece instanceof Queen)
            return playerType == PlayerType.Black ? R.drawable.bq : R.drawable.wq;
        else
            return playerType == PlayerType.Black ? R.drawable.bk : R.drawable.wk;
    }

    // get piece image by piece type
    private int GetPieceImage(PieceType piece, PlayerType playerType)
    {
        if (piece == PieceType.Pawn)
            return playerType == PlayerType.Black ? R.drawable.bp : R.drawable.wp;
        else if (piece == PieceType.Knight)
            return playerType == PlayerType.Black ? R.drawable.bn : R.drawable.wn;
        else if (piece == PieceType.Bishop)
            return playerType == PlayerType.Black ? R.drawable.bb : R.drawable.wb;
        else if (piece == PieceType.Rook)
            return playerType == PlayerType.Black ? R.drawable.br : R.drawable.wr;
        else if (piece == PieceType.Queen)
            return playerType == PlayerType.Black ? R.drawable.bq : R.drawable.wq;
        else
            return playerType == PlayerType.Black ? R.drawable.bk : R.drawable.wk;
    }

    // the colors of the board from the resources
    private int[] blackColor, whiteColor;

    // fill the tables of colors
    private void fillColors(){
        blackColor = new int[]{R.color.colorB1, R.color.colorB2, R.color.colorB3, R.color.colorB4};
        whiteColor = new int[]{R.color.colorW1, R.color.colorW2, R.color.colorW3, R.color.colorW4};
    }

    // draw the board color
    private void DrawBoardColor()
    {
        int color = blackColor[MainPage.getBoardColor()];
        for (int y = 0; y < this.boxes.size(); y++)
            for (int x = 0; x < this.boxes.get(y).size(); x++)
            {
                this.boxes.get(y).get(x).setBackgroundColor(activity.getResources().getColor(color));
                if (x < 7)
                    if (color == blackColor[MainPage.getBoardColor()])
                        color = whiteColor[MainPage.getBoardColor()];
                    else
                        color = blackColor[MainPage.getBoardColor()];
                this.boxes.get(y).get(x).PossibleMove(false);
                this.boxes.get(y).get(x).CapturedBox(false);
            }
        this.DrawLastMove();
    }

    // draw the the possible moves of a piece
    private void DrawPiecePossibleMoves(List<Position>[] listMoves)
    {
        for (int i = 0; i < listMoves[0].size(); i++)
            this.boxes.get(listMoves[0].get(i).GetY()).get(listMoves[0].get(i).GetX()).PossibleMove(true);
        for (int i = 0; i < listMoves[1].size(); i++)
            this.boxes.get(listMoves[1].get(i).GetY()).get(listMoves[1].get(i).GetX()).CapturedBox(true);
    }

    // drawing the last move in the game
    private void DrawLastMove()
    {
        Move lastMove = (this.matchForReplay == null) ? this.game.LastMove() : this.matchForReplay.getMove();
        if (lastMove != null)
        {
            this.boxes.get(lastMove.getFrom().GetY()).get(lastMove.getFrom().GetX()).setBackgroundColor(Color.parseColor("#cdc319"));
            this.boxes.get(lastMove.getTo().GetY()).get(lastMove.getTo().GetX()).setBackgroundColor(Color.parseColor("#ece22d"));
        }
    }

    // draw check and checkMat(in checkMat draw the check first after that draw the piece who target the king)
    private void DrawCheckAndCheckMat(){
        if (matchForReplay == null)
        {
            if (game.IsThereACheck()){
                Position kingPos = (game.GetTurn() == PlayerType.White) ? game.getWhitePlayer().GetKing().GetPositions() : game.getBlackPlayer().GetKing().GetPositions();
                this.boxes.get(kingPos.GetY()).get(kingPos.GetX()).setBackgroundColor(activity.getResources().getColor(R.color.colorCheck));
                if (game.IsThereACheckMat())
                {
                    Position threatPos = (game.GetTurn() == PlayerType.White) ? game.getWhitePlayer().GetThreatPiece() : game.getBlackPlayer().GetThreatPiece();
                    this.boxes.get(threatPos.GetY()).get(threatPos.GetX()).setBackgroundColor(Color.parseColor("#ff1b00"));
                    this.watchMode = true;
                    activity.gameIsOver(game.GetTurn() == PlayerType.White ? 2 : 1, false);
                }
            }
            if (game.isDraw())
            {
                this.watchMode = true;
                activity.gameIsOver(0, false);
            }
        }
    }

    // </editor-fold>

    // Pawn promotion
    public void MakeThePromotion()
    {
        this.game.PromotionPawn(this.promotionMenu.GetPromotionPiece());
        this.promotionMenu = null;
        this.DrawPieces();
    }

    // When the player click on box
    private void onBoxClick(Box box)
    {
        if (!this.watchMode)
            if (box.IsCapturedBox() || box.IsPossibleMove()) {
                int newX = -1, newY = -1;
                for (int y = 0; y < this.boxes.size(); y++) {
                    for (int x = 0; x < this.boxes.get(y).size(); x++)
                        if (this.boxes.get(y).get(x) == box) {
                            newX = x;
                            newY = y;
                            break;
                        }
                    if (newX != -1)
                        break;
                }
                Piece piece = box.piece;
                if (box.IsCapturedBox() && piece == null)
                    piece = this.boxes.get(this.game.GetPositionsPieceMoving().GetY()).get(newX).piece;
                this.DrawBoardColor();
                this.game.MakeMove(new Position(newX, newY), piece);
                if (this.game.Promotion())
                {
                    this.promotionMenu = new PromotionMenu(this);
                    this.promotionMenu.show(this.manager, null);
                }
                this.DrawPieces();
            } else if (box.piece != null) {
                this.DrawBoardColor();
                this.SetPieceMoves(box.piece);
                this.DrawCheckAndCheckMat();
            }
    }

    // the view captured pieces of each player
    //List<ImageView>[] listCapturedPieces;

    // set the captured pieces of each player and there values
    public void SetCapturedPieces()
    {
        ll_wpieces.removeAllViewsInLayout();
        ll_bpieces.removeAllViewsInLayout();
        PieceType[] capturedPieces = (matchForReplay == null) ? game.GetCapturedPieces(PlayerType.White) : matchForReplay.getCapturedPieces1(matchForReplay.getIndex());
        for(int i = 0; i < capturedPieces.length; i++)
            ll_wpieces.addView(getCapturedPieceView(capturedPieces[i], PlayerType.Black)); // Black player for get the image of the pieces black
        capturedPieces = (matchForReplay == null) ? game.GetCapturedPieces(PlayerType.Black) : matchForReplay.getCapturedPieces2(matchForReplay.getIndex());
        for(int i = 0; i < capturedPieces.length; i++)
            ll_bpieces.addView(getCapturedPieceView(capturedPieces[i], PlayerType.White));
        int value = (matchForReplay == null) ? game.PiecesValue(PlayerType.White) - game.PiecesValue(PlayerType.Black) : 0;
        tv_wvalues.setText((value > 0) ? String.format("+%s", value) : " ");
        tv_bvalues.setText((value < 0) ? String.format("+%s", -value) : " ");
    }

    // short method to create a view for captured piece
    private ImageView getCapturedPieceView(PieceType capturedPiece, PlayerType player)
    {
        ImageView iv_capturedPiece = new ImageView(activity);
        iv_capturedPiece.setLayoutParams(new LinearLayout.LayoutParams(ll_wpieces.getHeight(), ll_wpieces.getHeight()));
        iv_capturedPiece.setPadding(0, 0, 0, 0);
        iv_capturedPiece.setScaleType(ImageView.ScaleType.FIT_XY);
        if (player == PlayerType.Black)
            iv_capturedPiece.setBackgroundColor(Color.WHITE);
        iv_capturedPiece.setImageResource(this.GetPieceImage(capturedPiece, player));
        return iv_capturedPiece;
    }

    // Search for piece moves
    private void SetPieceMoves(Piece piece)
    {
        List<Position>[] listMoves = this.game.GetPiecePlayerMoves(piece);
        if (listMoves != null)
            this.DrawPiecePossibleMoves(listMoves);
        else
            for (int y = 0; y < this.boxes.size(); y++)
                for (int x = 0; x < this.boxes.get(y).size(); x++)
                {
                    this.boxes.get(y).get(x).CapturedBox(false);
                    this.boxes.get(y).get(x).PossibleMove(false);
                }
    }

    // Check if a position out if this board
    public static boolean OutOfBoard(Position position)
    {
        return position.GetX() < 0 || position.GetY() < 0 || position.GetX() >= 8 || position.GetY() >= 8;
    }

    /*
        Change the direction of a piece

        there is 8 directions

        -- [left - up]    -- [none - up]   -- [right - up]
        -- [left - none]  -- [none - none] -- [right - none]
        -- [left - down]  -- [none - down] -- [right - down]

     */
    public static int[] ChangeDirection(int Xdir, int Ydir, int ChangeVal)
    {
        int[] directions = new int[]{Xdir, Ydir};
        while (ChangeVal > 0){
            if (directions[0] == -1 && directions[1] == -1)
                directions = new int[]{0, -1};
            else if (directions[0] == 0 && directions[1] == -1)
                directions = new int[]{1, -1};
            else if (directions[0] == 1 && directions[1] == -1)
                directions = new int[]{1, 0};
            else if (directions[0] == 1 && directions[1] == 0)
                directions = new int[]{1, 1};
            else if (directions[0] == 1 && directions[1] == 1)
                directions = new int[]{0, 1};
            else if (directions[0] == 0 && directions[1] == 1)
                directions = new int[]{-1, 1};
            else if (directions[0] == -1 && directions[1] == 1)
                directions = new int[]{-1, 0};
            else if (directions[0] == -1 && directions[1] == 0)
                directions = new int[]{-1, -1};
            ChangeVal--;
        }
        return directions;
    }

    // get the match for reply if there's one
    public MatchForReplay getMatchForReplay() {
        return matchForReplay;
    }

    // class to make the replay more easy
    public class MatchForReplay extends Match
    {
        private int timeP1, timeP2, seconds, index, timeToPlay;
        private boolean matchIsOver, pause;

        MatchForReplay(Match match)
        {
            super(match.getMatchMode(), match.getTime());
            turns = match.getTurns();
            timeP1 = getTime() * 60;
            timeP2 = getTime() * 60;
            seconds = 0;
            index = 0;
            timeToPlay = 4;
            matchIsOver = false;
            pause = false;
        }

        public int getIndex()
        {
            return index;
        }

        public boolean IsTheMatchIsOver()
        {
            return matchIsOver;
        }

        // get the move of this turn(from the 'index')
        public Move getMove()
        {
            return getMove(index);
        }

        // add second to the time
        public boolean makeTime()
        {
            boolean moved = false;
            if (index < turns.size() - 1)
                if (this.getTime() > -1)
                {
                    if (getTurn(index).equals(PlayerType.White))
                    {
                        timeP1--;
                        if (timeP1 == getMove(index + 1).getTime())
                            moved = true;
                        if (timeP1 == 0)
                            matchIsOver = true;
                        else
                            if (timeP1 < 5)
                                timeP1 = 5;
                    }
                    else
                    {
                        timeP2--;
                        if (timeP2 == getMove(index + 1).getTime())
                            moved = true;
                        if (timeP2 == 0)
                            matchIsOver = true;
                        else
                        if (timeP2 < 5)
                            timeP2 = 5;
                    }
                }
                else
                {
                    this.seconds++;
                    if (this.seconds == timeToPlay)
                        moved = true;
                }
                if (moved)
                {
                    index++;
                    if (index == getMovesCount() - 1)
                        matchIsOver = true;
                    else
                        if (getMove(index).isCheckMat() || getMove(index).isDraw())
                            matchIsOver = true;
                    this.seconds = 0;
                }
            return moved;
        }

        // set the index of a turn(or move) in this match
        public void setIndex(int index)
        {
            if (this.index != index)
            {
                this.index = index;
                // set the time of each player in a specific turn
                if (this.getTime() > -1)
                {
                    if (getTurn(index).equals(PlayerType.White))
                    {
                        if (index > 0)
                        {
                            timeP1 = getMove(index).getTime();
                            if (index > 1)
                                timeP2 = getMove(index - 1).getTime();
                            else
                                timeP2 = getTime() * 60;
                        }
                        else
                        {
                            timeP1 = getTime() * 60;
                            timeP2 = getTime() * 60;
                        }
                    }
                    else
                    {
                        if (index > 0)
                        {
                            timeP2 = getMove(index).getTime();
                            if (index > 1)
                                timeP1 = getMove(index - 1).getTime();
                            else
                                timeP1 = getTime() * 60;
                        }
                        else
                        {
                            timeP1 = getTime() * 60;
                            timeP2 = getTime() * 60;
                        }
                    }
                }
                else
                    this.seconds = 0;
                if (index < turns.size())
                    matchIsOver = false;
            }
        }

        @Override
        public String GetNotation() {
            String notation = "";
            for(int i = 0; i <= index - 1; i++)
                notation = String.format("%s%s%s", notation, (i % 2 == 0) ? String.format("%s%s. ", (i > 0) ? "\n" : "", i / 2 + 1) : "     ", getMove(i + 1).GetNotation());
            return notation;
        }

        // this procedures connected with the interface controller

        public boolean Pause() {
            pause = !pause;
            return pause;
        }

        public boolean toNext() {
            index++;
            if (index >= this.turns.size() - 1) {
                index = this.turns.size() - 1;
                return false;
            }
            return true;
        }

        public void toLast()
        {
            setIndex(this.turns.size() - 1);
        }

        public boolean toPrevious() {
            index--;
            if (index <= 0) {
                index--;
                return false;
            }
            return true;
        }

        public boolean Faster() {
            timeToPlay--;
            seconds = 0;
            if (timeToPlay <= 1) {
                timeToPlay = 1;
                return false;
            }
            return true;
        }

        public boolean Slower() {
            timeToPlay++;
            seconds = 0;
            if (timeToPlay >= 10) {
                timeToPlay = 10;
                return false;
            }
            return true;
        }

        public boolean isInLast()
        {
            return (index >= turns.size() - 1);
        }

        public boolean isInFirst()
        {
            return (index <= 0);
        }

        public boolean isInMaxSpeed()
        {
            return (timeToPlay >= 10);
        }

        public boolean isInMinSpeed()
        {
            return (timeToPlay <= 1);
        }
        // get the time of the players

        public int getWhitePlayerTime(){
            return this.timeP1;
        }

        public int getBlackPlayerTime(){
            return this.timeP2;
        }
    }
}
