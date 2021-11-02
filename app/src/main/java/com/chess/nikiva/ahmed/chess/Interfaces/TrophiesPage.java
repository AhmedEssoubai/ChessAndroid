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
import android.widget.Toast;

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

public class TrophiesPage extends Activity {

    TextView tv_empty;
    ProgressBar pb_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trophies_page);
        if (MainPage.getUsername().isEmpty())
            this.finish();
        tv_empty = findViewById(R.id.tv_empty);
        pb_progress = findViewById(R.id.pb_progress);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_achievement);
            ((TextView)findViewById(R.id.tv_empty)).setText(R.string.fr_there_is_no_achievements);
        }
        new LoadAchievements().execute(MainPage.Host + "getAchievements.php?username=" + MainPage.getUsername());
    }

    void setAchievementsObjective(int objective, int progress)
    {
        ProgressBar progressBar = findViewById(R.id.pb_progressAchiev);
        progressBar.setMax(objective);
        progressBar.setProgress(progress);
    }

    private void setData(List<TrophiesModel> Items, int objectif, int progress)
    {
        TrophiesAdapter adapter = new TrophiesAdapter(Items, this);
        ProgressBar pb_progressAchiev = findViewById(R.id.pb_progressAchiev);
        pb_progressAchiev.setMax(objectif);
        pb_progressAchiev.setMax(progress);
        ((ListView)findViewById(R.id.list)).setAdapter(adapter);
        if (Items.size() > 0)
            tv_empty.setVisibility(View.GONE);
    }

    @SuppressLint("StaticFieldLeak")
    class LoadAchievements extends AsyncTask<String, String, String> {

        String achievements;
        @Override
        protected void onPreExecute() {
            achievements = "";
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
                achievements = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            if (!achievements.isEmpty())
            try
            {
                pb_progress.setProgress(75);
                JSONObject json = new JSONObject(achievements);
                int error = json.getInt("error");
                if (error == 0)
                    tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_database_error : R.string.en_database_error);
                else
                    if (error != 1)
                    {
                        List<TrophiesModel> Items = new ArrayList<>();
                        JSONArray jachievements = json.getJSONArray("achievements");
                        int objectif = 0, progress = 0;
                        for (int i = 0; i < jachievements.length(); i++)
                        {
                            JSONObject achievement = jachievements.getJSONObject(i);
                            TrophiesModel trophie = new TrophiesModel(
                                    achievement.getInt("image"),
                                    achievement.getInt("objectif"),
                                    achievement.getString("en_title"),
                                    achievement.getString("en_description"),
                                    achievement.getString("fr_title"),
                                    achievement.getString("fr_description"),
                                    achievement.getInt("progress")
                            );
                            objectif += trophie.getObjective();
                            progress += trophie.getProgress();
                            Items.add(trophie);
                        }
                        setData(Items, objectif, progress);
                    }
                pb_progress.setProgress(100);
            }
            catch (Exception e)
            {
                //tv_empty.setText((MainPage.getLang() == 1) ? R.string.fr_build_error : R.string.en_build_error);
            }
            pb_progress.setVisibility(View.GONE);
        }
    }
}

class TrophiesAdapter extends BaseAdapter
{
    private List<TrophiesModel> Items;
    private TrophiesPage trophiesPage;
    private static int[] TrophiesImages;
    private int achievementsObjective, achievementsProgress;

    TrophiesAdapter(List<TrophiesModel> Items, TrophiesPage trophiesPage) {
        achievementsObjective = 0;
        achievementsProgress = 0;
        this.Items = Items;
        this.trophiesPage = trophiesPage;
        if (TrophiesImages == null)
            TrophiesImages = new int[]{R.drawable.achivement_0, R.drawable.achivement_1, R.drawable.achivement_2, R.drawable.achivement_3, R.drawable.achivement_4, R.drawable.achivement_5};
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
        LayoutInflater inflater = (LayoutInflater)trophiesPage.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.trophie, null);
        view.findViewById(R.id.imgv_trophie).setBackgroundResource(TrophiesImages[this.Items.get(position).getImage()]);
        ((TextView)view.findViewById(R.id.tv_title)).setText(String.valueOf(this.Items.get(position).getTitre()));
        ((TextView)view.findViewById(R.id.tv_descrip)).setText(String.valueOf(this.Items.get(position).getDescription()));
        ((TextView)view.findViewById(R.id.tv_progress)).setText(String.format("%s/%s", String.valueOf(this.Items.get(position).getProgress()), String.valueOf(this.Items.get(position).getObjective())));
        ProgressBar pb_progress = view.findViewById(R.id.pb_progress);
        pb_progress.setMax(this.Items.get(position).getObjective());
        pb_progress.setProgress(this.Items.get(position).getProgress());
        achievementsObjective += this.Items.get(position).getObjective();
        achievementsProgress += this.Items.get(position).getProgress();
        if (position == Items.size() - 1)
            trophiesPage.setAchievementsObjective(achievementsObjective, achievementsProgress);
        return view;
    }
}

class TrophiesModel
{
    private int image;
    private int objective;
    private String en_titre;
    private String en_description;
    private String fr_titre;
    private String fr_description;
    private int progress;

    TrophiesModel(int image, int objective, String en_titre, String en_description, String fr_titre, String fr_description, int progress){
        this.image = image;
        this.objective = objective;
        this.en_titre = en_titre;
        this.en_description = en_description;
        this.fr_titre = fr_titre;
        this.fr_description = fr_description;
        this.progress = progress;
    }

    public int getImage() {
        return image;
    }

    public int getObjective() {
        return objective;
    }

    public String getTitre() {
        return (MainPage.getLang() == 1) ? fr_titre : en_titre;
    }

    public String getDescription() {
        return (MainPage.getLang() == 1) ? fr_description : en_description;
    }

    public int getProgress() {
        return progress;
    }
}
