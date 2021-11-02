package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Created by AHMED on 29/04/2018.
 */

public class leaderboardPage extends Activity {

    TextView tv_empty;
    ProgressBar pb_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaderboard_page);
        tv_empty = findViewById(R.id.tv_empty);
        pb_progress = findViewById(R.id.pb_progress);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_leaderboard);
            ((TextView)findViewById(R.id.tv_empty)).setText(R.string.fr_there_is_no_players);
        }
        new LoadLeaderboard().execute(MainPage.Host + "getLeaderboard.php");
    }

    // the result
    String leaderboard;

    private void SetData(List<LeaderModel> Items)
    {
        LeaderAdapter adapter = new LeaderAdapter(Items, this);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
        if (Items.size() > 0)
            tv_empty.setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    class LoadLeaderboard extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            leaderboard = "";
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
                leaderboard = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                //tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            try
            {
                pb_progress.setProgress(75);
                JSONObject json = new JSONObject(leaderboard);
                if (json.getInt("success") == 0)
                    tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_database_error : R.string.en_database_error);
                else
                {
                    List<LeaderModel> Items = new ArrayList<>();
                    JSONArray players = json.getJSONArray("leaderboard");
                    for (int i = 0; i < players.length(); i++)
                    {
                        JSONObject player = players.getJSONObject(i);
                        Items.add(new LeaderModel(
                                player.getString("username"),
                                player.getInt("avatar"),
                                player.getInt("NbWon"),
                                player.getInt("NbLost"),
                                player.getInt("NbDraw"),
                                player.getInt("Score")
                        ));
                    }
                    SetData(Items);
                }
                pb_progress.setProgress(100);
            }
            catch (Exception e)
            {
                tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
            }

            pb_progress.setVisibility(View.GONE);
        }
    }
}

class LeaderAdapter extends BaseAdapter
{
    private List<LeaderModel> Items;
    private Context context;

    LeaderAdapter(List<LeaderModel> Items, Context context) {
        for(int i = 0; i < Items.size(); i++) {
            Items.get(i).setRank(i + 1);
            if (i > 0)
                if (Items.get(i).getScore() == Items.get(i - 1).getScore())
                    Items.get(i).setRank(Items.get(i - 1).getRank());
        }
        this.Items = Items;
        this.context = context;
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.leader, null);
        TextView tv_username = view.findViewById(R.id.tv_username);
        ((TextView)view.findViewById(R.id.tv_rank)).setText(String.format("#%s", this.Items.get(position).getRank()));
        ((ImageView)view.findViewById(R.id.imgv_player)).setImageResource(Avatars.AvaratList[this.Items.get(position).getAvatar()]);
        tv_username.setText(this.Items.get(position).getUsername());
        ((TextView)view.findViewById(R.id.tv_score)).setText(String.format("Score: %s", String.valueOf(this.Items.get(position).getScore())));
        ((TextView)view.findViewById(R.id.tv_matchs)).setText(String.format("%s: %s %s: %s %s: %s", context.getResources().getString(((MainPage.getLang() == 1) ? R.string.fr_won : R.string.en_won)), String.valueOf(this.Items.get(position).getWon()), context.getResources().getString(((MainPage.getLang() == 1) ? R.string.fr_draw : R.string.en_draw)), String.valueOf(this.Items.get(position).getDraw()) , context.getResources().getString(((MainPage.getLang() == 1) ? R.string.fr_lost : R.string.en_lost)), String.valueOf(this.Items.get(position).getLost())));
        if (MainPage.getUsername().equals(this.Items.get(position).getUsername()))
            view.findViewById(R.id.ll_leader).setBackgroundColor(context.getResources().getColor(R.color.colorDraw));
            //tv_username.setTextColor(context.getResources().getColor(R.color.colorDraw));
        return view;
    }
}

class LeaderModel
{
    private String username;
    private int avatar, won, lost, draw, score, rank;

    LeaderModel(String username, int avatar, int won, int lost, int draw, int score){
        this.username = username;
        this.avatar = avatar;
        this.won = won;
        this.lost = lost;
        this.draw = draw;
        this.score = score;
        rank = 0;
    }

    String getUsername() {
        return username;
    }

    int getAvatar() {
        return avatar;
    }

    int getWon() {
        return won;
    }

    int getLost() {
        return lost;
    }

    int getDraw() {
        return draw;
    }

    int getScore() {
        return score;
    }

    int getRank() {
        return rank;
    }

    void setRank(int rank) {
        this.rank = rank;
    }
}

