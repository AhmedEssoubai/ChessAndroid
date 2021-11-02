package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Created by AHMED on 29/04/2018.
 */

public class HistoryPage extends Activity {

    TextView tv_empty;
    ProgressBar pb_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_page);
        if (MainPage.getUsername().isEmpty())
            this.finish();
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_last_matchs);
            ((TextView)findViewById(R.id.tv_empty)).setText(R.string.fr_there_is_no_matches);
        }
        tv_empty = findViewById(R.id.tv_empty);
        pb_progress = findViewById(R.id.pb_progress);
        new LoadHistory().execute(MainPage.Host + "getHistory.php?username=" + MainPage.getUsername());
    }

    // the result
    String history;

    private void SetData(List<HistoryMatchModel> Items)
    {
        HistoryMatchAdapter adapter = new HistoryMatchAdapter(Items, this);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
        if (Items.size() > 0)
            tv_empty.setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    class LoadHistory extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            history = "";
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
                history = LoginPage.StreamToString(inputStream);
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
                JSONObject json = new JSONObject(history);
                if (json.getInt("success") == 0)
                    tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_database_error : R.string.en_database_error);
                else
                {
                    List<HistoryMatchModel> Items = new ArrayList<>();
                    JSONArray players = json.getJSONArray("matches");
                    for (int i = 0; i < players.length(); i++)
                    {
                        JSONObject player = players.getJSONObject(i);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");
                        Date datetime;
                        try {
                            datetime = format.parse(player.getString("datetime"));
                        } catch (ParseException e) {
                            datetime = new Date();
                        }
                        String result = player.getString("result");
                        Items.add(new HistoryMatchModel(
                                datetime,
                                player.getInt("duration"),
                                result.equals("g") ? 1 : result.equals("p") ? 2 : 0,
                                player.getInt("score")
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

class HistoryMatchAdapter extends BaseAdapter
{
    private List<HistoryMatchModel> Items;
    private Context context;

    HistoryMatchAdapter(List<HistoryMatchModel> Items, Context context) {
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
        View view = inflater.inflate(R.layout.match_in_history, null);
        if (this.Items.get(position).getResultIndex() == 1) {
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorWon);
            ((ImageView)view.findViewById(R.id.imgv_result)).setImageResource(R.drawable.win_icon);
        }
        else if (this.Items.get(position).getResultIndex() == 2) {
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorLost);
            ((ImageView)view.findViewById(R.id.imgv_result)).setImageResource(R.drawable.loss_icon);
        }
        else {
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorDraw);
            ((ImageView)view.findViewById(R.id.imgv_result)).setImageResource(R.drawable.draw_icon);
        }
        ((TextView)view.findViewById(R.id.tv_date)).setText(this.Items.get(position).getDatetime().toString());
        ((TextView)view.findViewById(R.id.tv_duration)).setText((this.Items.get(position).getDuration() > -1) ? String.format("%s min", String.valueOf(this.Items.get(position).getDuration())) : "∞");
        ((TextView)view.findViewById(R.id.tv_result)).setText(String.format("%s %s%s", context.getResources().getString(this.Items.get(position).getResult()), (this.Items.get(position).getScore() >= 0) ? "+" : "", this.Items.get(position).getScore()));
        return view;
    }
}

class HistoryMatchModel
{
    private Date datetime;
    private int duration;
    private int result;
    private int score;

    HistoryMatchModel(Date datetime, int duration, int result, int score){
        this.datetime = datetime;
        this.duration = duration;
        this.result = result;
        this.score = score;
    }

    Date getDatetime() {
        return datetime;
    }

    int getDuration() {
        return duration;
    }

    int getResult() {
        if (result == 0)
            return ((MainPage.getLang() == 1) ? R.string.fr_draw : R.string.en_draw);
        else if (result == 1)
            return ((MainPage.getLang() == 1) ? R.string.fr_won : R.string.en_won);
        else
            return ((MainPage.getLang() == 1) ? R.string.fr_lost : R.string.en_lost);
    }

    int getResultIndex() {
        return result;
    }

    int getScore() {
        return score;
    }
}
