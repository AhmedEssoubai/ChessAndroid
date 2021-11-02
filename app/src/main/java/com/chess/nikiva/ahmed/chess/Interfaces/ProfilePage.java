package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;

/**
 * Created by AHMED on 27/04/2018.
 */

public class ProfilePage extends Activity {
    ProgressBar pb_progress;
    String username;
    TextView f;
    static ProfilePage profilePage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_page);
        profilePage = this;
        pb_progress = findViewById(R.id.pb_progress);
        username = MainPage.getUsername();
        if (username.isEmpty())
            this.finish();
        Log.d("HTTP", MainPage.Host + "getInfos.php?username=" + username);
        new LoadInformations().execute(MainPage.Host + "getInfos.php?username=" + username);
        f = findViewById(R.id.tv_score);
    }

    public void btn_history_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, HistoryPage.class);
        startActivity(intent);
    }

    public void btn_trophies_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, TrophiesPage.class);
        startActivity(intent);
    }

    int avatar;
    String firstname, lastname;

    private void SetData(int avatar, String firstname, String lastname, int wins, int draws, int loses, int score)
    {
        this.firstname = firstname;
        this.lastname = lastname;
        ((TextView)findViewById(R.id.tv_username)).setText(username);
        ((TextView)findViewById(R.id.tv_fullname)).setText(firstname + " " + lastname);
        ((TextView)findViewById(R.id.tv_nbwins)).setText(String.valueOf(wins));
        ((TextView)findViewById(R.id.tv_nbdraws)).setText(String.valueOf(draws));
        ((TextView)findViewById(R.id.tv_nbloses)).setText(String.valueOf(loses));
        ((TextView)findViewById(R.id.tv_score)).setText(String.valueOf(score));
        setAvatar(avatar);
        findViewById(R.id.ll_profile_options).setVisibility(View.VISIBLE);
        findViewById(R.id.ll_matches_info).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_edit).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_sign_out).setVisibility(View.VISIBLE);
    }

    private void Error(int error)
    {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
        this.finish();
    }

    public void ib_editOnClick(View view)
    {
        Intent intent = new Intent();
        intent.setClass(this, SignUpPage.class);
        intent.putExtra("fist_name", firstname);
        intent.putExtra("last_name", lastname);
        intent.putExtra("avatar", avatar);
        startActivity(intent);
    }

    void setAvatar(int avatar)
    {
        this.avatar = avatar;
        ((ImageView)findViewById(R.id.iv_avatar)).setImageResource(Avatars.AvaratCList[avatar]);
        MainPage.mainPage.setAvatar(avatar);
    }

    public void ib_sign_outOnClick(View view) {
        MainPage.setUsername("");
        this.finish();
    }

    @SuppressLint("StaticFieldLeak")
    class LoadInformations extends AsyncTask<String, String, String> {
        String infos;
        boolean error;

        @Override
        protected void onPreExecute() {
            infos = "";
            error = false;
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
                infos = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                error = true;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            if (error)
                Error((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
            else
            {
                try
                {
                    pb_progress.setProgress(75);
                    JSONObject json = new JSONObject(infos);
                    //Log.d("Json", "onPostExecute: " + infos);
                    if (json.getInt("success") == 0)
                        Error((MainPage.getLang() == 1) ? R.string.fr_database_error : R.string.en_database_error);
                    else
                    {
                        JSONObject infosObject = json.getJSONArray("infos").getJSONObject(0);
                        SetData(infosObject.getInt("avatar"), infosObject.getString("firstname"), infosObject.getString("familyname"), infosObject.getInt("NbWon"), infosObject.getInt("NbLost"), infosObject.getInt("NbDraw"), infosObject.getInt("Score"));
                    }
                    pb_progress.setProgress(100);
                }
                catch (Exception e)
                {
                    Error((MainPage.getLang() == 1) ? R.string.fr_build_error : R.string.en_build_error);
                }
                pb_progress.setVisibility(View.GONE);
            }
        }
    }
}
