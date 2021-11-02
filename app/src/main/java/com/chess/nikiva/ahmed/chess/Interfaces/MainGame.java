package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EmptyStackException;

import TheGame.GameAndTurns.Game;
import TheGame.GameAndTurns.Match;
import TheGame.Interface.Board;
import TheGame.ThePlayer.PlayerType;

public class MainGame extends Activity {

    private Game game;
    private PauseMenu pauseMenu;
    private Board board;
    int whiteLevel, blackLevel, result;
    private boolean gameover, neverShowed, gameOverByTime, competition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_game);
        pb_progress = findViewById(R.id.pb_progress);
        result = 2;
        gameOverByTime = false;
        this.gameover = false;
        this.neverShowed = true;
        //Options Menu
        this.TabOptions = new ImageButton[]{ this.findViewById(R.id.btn_time), this.findViewById(R.id.btn_notation), this.findViewById(R.id.btn_pieces), this.findViewById(R.id.btn_controller) };
        this.TabLLOptions = new View[]{ this.findViewById(R.id.ll_time), this.findViewById(R.id.sv_notation), this.findViewById(R.id.ll_pieces), this.findViewById(R.id.ll_controller) };
        //Board
        LinearLayout layout = findViewById(R.id.Board);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        LinearLayout game_title = findViewById(R.id.ll_panel);
        switch (MainPage.getBoardColor()){
            case 0:
                game_title.setBackgroundColor(getResources().getColor(R.color.colorC1));
                break;
            case 1:
                game_title.setBackgroundColor(getResources().getColor(R.color.colorC2));
                break;
            case 2:
                game_title.setBackgroundColor(getResources().getColor(R.color.colorC3));
                break;
            default:
                game_title.setBackgroundColor(getResources().getColor(R.color.colorC4));
                break;
        }
        whiteLevel = 0;
        blackLevel = 0;
        competition = false;
        time = -1;
        Match matchForReplay = null;
        Intent intent = getIntent();
        if (intent != null)
        {
            Bundle extras = intent.getExtras();
            if (extras != null)
            {
                int matchIndex = extras.getInt("indexMatch") - 1;
                if (matchIndex > -1)
                    matchForReplay = ReplayMatchesPage.matches.getMatche(matchIndex);
                else
                {
                    whiteLevel = extras.getInt("whiteLevel");
                    blackLevel = extras.getInt("blackLevel");
                    competition = extras.getBoolean("competition");
                    time = extras.getInt("time");
                }
            }
        }
        if (matchForReplay == null)
        {
            if (MainPage.game == null)
            {
                MainPage.game = new Game(whiteLevel, blackLevel, time, competition);
                beginMatch = true;
                if (competition)
                    new CallServer().execute(MainPage.Host + "beginMatche.php?username=" + MainPage.getUsername() + "&level=" + blackLevel);
            }
            else
            {
                whiteLevel = MainPage.game.getWhiteLevel();
                blackLevel = MainPage.game.getBlackLevel();
                competition = MainPage.game.isCompetition();
                time = MainPage.game.getTime();
            }
            game = MainPage.game;
        }
        else
        {
            time = matchForReplay.getTime();
            game = null;
        }
        setClock(time);
        //
        board = new Board(this, layout, metrics, game, matchForReplay);
        //
        pauseMenu = new PauseMenu(this);
        int opt = 1;
        if (time > 0) {
            this.TabOptions[0].setVisibility(View.VISIBLE);
            opt = 0;
        }
        if (matchForReplay != null)
        {
            getControls();
            setButtonBackground(ib_first, R.drawable.non_available_button);
            setButtonBackground(ib_previous, R.drawable.non_available_button);
            this.TabOptions[3].setVisibility(View.VISIBLE);
        }
        this.ib_options_Click(this.TabOptions[opt]);
    }

    public boolean isGameOver() {
        return gameover;
    }

    public void gameIsOver(int result, boolean byTime) {
        if (game != null)
        {
            gameover = true;
            beginMatch = true;
            if (competition)
                new CallServer().execute(MainPage.Host + "endMatche.php?username=" + MainPage.getUsername() + "&duration=" + game.getDuration() + "&result=" + (result == 0 ? "n" : result == 1 ? "g" : "p") + "&level=" + blackLevel + "&moves=" + ((game.getMatch().getMovesCount() + 1) / 2) + "&idM=" + game.getIdMatch());
            MainPage.game = null;
            game.getMatch().setResult(result);
        }
        else
        {
            clock.cancel();
            ib_pause.setImageResource(R.drawable.c_play);
        }
        gameOverByTime = byTime;
        this.result = result;
        (new GameOverMenu(this)).show(getFragmentManager(), null);
    }

    public boolean isGameOverByTime()
    {
        return gameOverByTime;
    }

    public int getResult()
    {
        return result;
    }

    public void newGame() {
        result = 2;
        gameOverByTime = false;
        this.gameover = false;
        game = new Game(whiteLevel, blackLevel, time, competition);
        if (competition)
            new CallServer().execute(MainPage.Host + "beginMatche.php?username=" + MainPage.getUsername() + "&level=" + blackLevel);
        MainPage.game = game;
        board.newGame(game);
    }

    // options menu
    ImageButton[] TabOptions;
    View[] TabLLOptions;

    public void ib_options_Click(View view) {
        for (ImageButton option : this.TabOptions) option.setBackgroundColor(Color.TRANSPARENT);
        for (View option : this.TabLLOptions) option.setVisibility(View.GONE);
        view.setBackgroundColor(getResources().getColor(R.color.colorSystem));
        if (view == this.findViewById(R.id.btn_notation))
            this.findViewById(R.id.sv_notation).setVisibility(View.VISIBLE);
        else if (view == this.findViewById(R.id.btn_pieces))
        {
            if (this.neverShowed)
                board.SetCapturedPieces();
            this.findViewById(R.id.ll_pieces).setVisibility(View.VISIBLE);
            this.neverShowed = false;
        }
        else if (view == this.findViewById(R.id.btn_time))
            this.findViewById(R.id.ll_time).setVisibility(View.VISIBLE);
        else
            this.findViewById(R.id.ll_controller).setVisibility(View.VISIBLE);
    }

    public Match getMatch(){return game.getMatch();}

    // <editor-fold desc="Clock Timer">
    Clock clock;
    TextView tv_wclock, tv_bclock;
    int time;

    private void setClock(int time) {
        this.time = time;
        seconds = -1;
        if (time != -1)
        {
            tv_wclock = findViewById(R.id.tv_wclock);
            tv_bclock = findViewById(R.id.tv_bclock);
            this.ReCreateTheClock();
        }
        else if (game == null)
            this.ReCreateTheClock();
        else if (whiteLevel > 0 && blackLevel > 0)
        {
            seconds = 0;
            this.ReCreateTheClock();
        }
    }

    private void ReCreateTheClock()
    {
        stopTime();
        this.clock = new Clock((time < 1 ? 10 : time) * 60 * 1000 * 2, 1000);
        this.clock.start();
    }

    public void startTime(){
        if (this.clock != null)
            ReCreateTheClock();
    }

    public void stopTime(){
        if (this.clock != null)
            this.clock.cancel();
    }
    public void btn_pause_OnClick(View view) {
        pauseMenu.show(this.getFragmentManager(), null);
    }

    // the time to do the next move for cup
    int seconds;
    private void giveSecond()
    {
        seconds++;
        if (seconds == 3)
        {
            game.AI_Move();
            board.DrawPieces();
            seconds = 0;
        }
    }

    public void secondsToZero()
    {
        seconds = 0;
    }

    // is this a replay of a match
    boolean isMatchRepay()
    {
        return game == null;
    }

    // return a integer time to string
    public String timeToString(int time)
    {
        int min = time / 60, sec = time % 60;
        return String.format("%s%s:%s%s", min < 10 ? "0" : "", min, sec < 10 ? "0" : "", sec);
    }

    private class Clock extends CountDownTimer
    {
        Clock(int millisInFuture, int countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            if (time > -1)
            {
                int bTime, wTime;
                if (game != null)
                {
                    int result = game.timeDown();
                    game.addSecond();
                    wTime = game.getTime(PlayerType.White);
                    bTime = game.getTime(PlayerType.Black);
                    if (result != 0)
                        gameIsOver(result, true);
                }
                else
                {
                    if (board.getMatchForReplay().makeTime()) {
                        setButtonBackground(ib_first, R.drawable.myappbutton);
                        setButtonBackground(ib_previous, R.drawable.myappbutton);
                        board.DrawTurn();
                        if (board.getMatchForReplay().IsTheMatchIsOver())
                            gameIsOver(board.getMatchForReplay().getTurn(board.getMatchForReplay().getIndex()) == PlayerType.White ? 2 : 1, false);
                    }
                    else if (board.getMatchForReplay().IsTheMatchIsOver())
                        gameIsOver(board.getMatchForReplay().getTurn(board.getMatchForReplay().getIndex()) == PlayerType.White ? 2 : 1, true);
                    wTime = board.getMatchForReplay().getWhitePlayerTime();
                    bTime = board.getMatchForReplay().getBlackPlayerTime();
                }
                tv_wclock.setText(timeToString(wTime));
                tv_bclock.setText(timeToString(bTime));
            }
            else
            {
                if (game != null)
                {
                    giveSecond();
                    game.addSecond();
                }
                else
                {
                    if (board.getMatchForReplay().makeTime())
                    {
                        setButtonBackground(ib_first, R.drawable.myappbutton);
                        setButtonBackground(ib_previous, R.drawable.myappbutton);
                        board.DrawTurn();
                        if (board.getMatchForReplay().IsTheMatchIsOver())
                            gameIsOver(board.getMatchForReplay().getTurn(board.getMatchForReplay().getIndex()) == PlayerType.White ? 2 : 1, false);
                    }
                }
            }
            if (game == null)
            {
                if (board.getMatchForReplay().IsTheMatchIsOver())
                {
                    setButtonBackground(ib_next, R.drawable.non_available_button);
                    setButtonBackground(ib_last, R.drawable.non_available_button);
                }
            }
        }

        @Override
        public void onFinish() {
            ReCreateTheClock();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.startTime();
    }

    @Override
    public void onBackPressed() {
        btn_pause_OnClick(null);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (MainPage.game != null)
            try {
                FileOutputStream fos = openFileOutput("match_save.bin", Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(game);
                os.close();
                fos.close();
            }
            catch (Exception e){
            }
    }
    // </editor-fold>

    // <editor-fold desc="Replay Controller">
    ImageButton ib_first, ib_previous, ib_pause, ib_next, ib_last, ib_slow, ib_fast;

    private void setButtonBackground(ImageButton button, int resource)
    {
        button.setBackgroundResource(resource);
        button.setPadding(0, 0, 0, 0);
    }

    public void getControls()
    {
        ib_first = findViewById(R.id.ib_first);
        ib_previous = findViewById(R.id.ib_previous);
        ib_pause = findViewById(R.id.ib_pause);
        ib_next = findViewById(R.id.ib_next);
        ib_last = findViewById(R.id.ib_last);
        ib_slow = findViewById(R.id.ib_slow);
        ib_fast = findViewById(R.id.ib_fast);
    }

    public void ib_first_onClick(View view) {
        if (!board.getMatchForReplay().isInFirst())
        {
            setButtonBackground(ib_last, R.drawable.myappbutton);
            setButtonBackground(ib_next, R.drawable.myappbutton);
            board.getMatchForReplay().setIndex(0);
            setButtonBackground(ib_first, R.drawable.non_available_button);
            setButtonBackground(ib_previous, R.drawable.non_available_button);
            board.DrawTurn();
        }
    }

    public void ib_previous_onClick(View view) {
        if (!board.getMatchForReplay().isInFirst())
        {
            setButtonBackground(ib_last, R.drawable.myappbutton);
            setButtonBackground(ib_next, R.drawable.myappbutton);
            if (!board.getMatchForReplay().toPrevious())
            {
                setButtonBackground(ib_first, R.drawable.non_available_button);
                setButtonBackground(ib_previous, R.drawable.non_available_button);
            }
            board.DrawTurn();
        }
    }

    public void ib_pause_onClick(View view) {
        if (board.getMatchForReplay().Pause())
        {
            clock.cancel();
            ib_pause.setImageResource(R.drawable.c_play);
        }
        else
        {
            clock.start();
            ib_pause.setImageResource(R.drawable.c_pause);
        }
    }

    public void ib_next_onClick(View view) {
        if (!board.getMatchForReplay().isInLast())
        {
            setButtonBackground(ib_first, R.drawable.myappbutton);
            setButtonBackground(ib_previous, R.drawable.myappbutton);
            if (!board.getMatchForReplay().toNext())
            {
                setButtonBackground(ib_last, R.drawable.non_available_button);
                setButtonBackground(ib_next, R.drawable.non_available_button);
            }
            board.DrawTurn();
        }
    }

    public void ib_last_onClick(View view) {
        if (!board.getMatchForReplay().isInLast())
        {
            setButtonBackground(ib_first, R.drawable.myappbutton);
            setButtonBackground(ib_previous, R.drawable.myappbutton);
            board.getMatchForReplay().toLast();
            setButtonBackground(ib_last, R.drawable.non_available_button);
            setButtonBackground(ib_next, R.drawable.non_available_button);
            board.DrawTurn();
        }
    }

    public void ib_slow_onClick(View view) {
        if (!board.getMatchForReplay().isInMinSpeed())
        {
            setButtonBackground(ib_fast, R.drawable.myappbutton);
            if (!board.getMatchForReplay().Slower())
                setButtonBackground(ib_slow, R.drawable.non_available_button);
        }
    }

    public void ib_fast_onClick(View view) {
        if (!board.getMatchForReplay().isInMaxSpeed())
        {
            setButtonBackground(ib_slow, R.drawable.myappbutton);
            if (!board.getMatchForReplay().Faster())
                setButtonBackground(ib_fast, R.drawable.non_available_button);
        }
    }
    // </editor-fold>

    // <editor-fold desc="Connecting to the server">
    ProgressBar pb_progress;
    private boolean beginMatch;

    private void throwError(String error)
    {
        if (!error.isEmpty())
        {
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            MainPage.game = null;
            this.finish();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class CallServer extends AsyncTask<String, String, String> {
        private String errorStat, Result;

        @Override
        protected void onPreExecute() {
            Result = "";
            errorStat = "";
        }

        protected String doInBackground(String... args) {
            try
            {
                pb_progress.setProgress(10);
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(MainPage.TimeOut);
                pb_progress.setProgress(50);
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                if (urlConnection.getResponseCode() != 200)
                    throw new EmptyStackException();
                pb_progress.setProgress(75);
                Result = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                errorStat = "1 -- " + e.getMessage();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }


        protected void onPostExecute(String file_url) {
            if (errorStat.isEmpty())
            {
                try
                {
                    pb_progress.setProgress(75);
                    JSONObject json = new JSONObject(Result);
                    int success = json.getInt("success");
                    if (success == 0)
                        throw new EmptyStackException();
                    if (beginMatch)
                        game.setIdMatch(json.getInt("IdM"));
                    pb_progress.setProgress(100);
                }
                catch (Exception e)
                {
                    //Log.d("Error", e.getMessage() + "  ---> " + stat);
                    errorStat = "2 -- " + e.getMessage();
                }
            }
            pb_progress.setProgress(0);
            throwError(errorStat);
        }

    }
    // </editor-fold>
}
