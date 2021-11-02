package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.GameModePage;
import com.chess.nikiva.ahmed.chess.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EmptyStackException;

import TheGame.GameAndTurns.Game;

/**
 * Created by AHMED on 20/04/2018.
 */

public class MainPage extends Activity {

    private static String Username;
    private static int BoardColor, Lang;
    private static SharedPreferences settings;
    public static Game game;
    public static String Host = "http://000.000.0.0/chess/"; // dir address dyal l server o smiyat dossier li fih lfiles php
    public static MainPage mainPage;
    public static int TimeOut = 3000;

    public static String getUsername()
    {
        return Username;
    }

    public static void setUsername(String username)
    {
        Username = username;
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Username",Username);
        editor.apply();
        if (username.isEmpty())
            mainPage.setAvatar(-1);
    }

    public static int getBoardColor()
    {
        return BoardColor;
    }

    public static void setBoardColor(int boardColor)
    {
        BoardColor = boardColor;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("BoardColor",BoardColor);
        editor.apply();
    }

    public static int getLang()
    {
        return Lang;
    }

    public static void setLang(int lang)
    {
        Lang = lang;
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("Lang",Lang);
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        mainPage = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);
        try {
            FileInputStream fis = openFileInput("matches_for_replay.bin");
            ObjectInputStream is = new ObjectInputStream(fis);
            ReplayMatchesPage.matches = (Matches) is.readObject();
            is.close();
            fis.close();
        }
        catch (Exception e){
            ReplayMatchesPage.matches = new Matches();
        }
        try {
            FileInputStream fis = openFileInput("match_save.bin");
            ObjectInputStream is = new ObjectInputStream(fis);
            game = (Game) is.readObject();
            is.close();
            fis.close();
        }
        catch (Exception e){}
        settings = getSharedPreferences("User", 0);
        Username = settings.getString("Username", "");
        BoardColor = settings.getInt("BoardColor", 2);
        Lang = settings.getInt("Lang", 0);
        new LoadAvatar().execute(Host + "getInfos.php?username=" + Username);
    }

    public void btn_play_onClick(View view) {
        Intent intent = new Intent();
        if (game == null)
            intent.setClass(this, GameModePage.class);
        else
            intent.setClass(this, MainGame.class);
        startActivity(intent);
    }

    public void btn_settings_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, SettingsPage.class);
        startActivity(intent);
    }

    public void btn_login_onClick(View view)
    {
        Intent intent = new Intent();
        if (Username.isEmpty())
            intent.setClass(this, LoginPage.class);
        else
            intent.setClass(this, ProfilePage.class);
        startActivity(intent);
    }

    public void btn_watch_games_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, ReplayMatchesPage.class);
        startActivity(intent);
    }

    public void btn_leaderboard_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, leaderboardPage.class);
        startActivity(intent);
    }

    public void setAvatar(int avatar)
    {
        if (!Username.isEmpty() && avatar > -1)
        {
            findViewById(R.id.btn_login).setBackgroundResource(Avatars.AvaratCList[avatar]);
            findViewById(R.id.btn_leaderboard).setVisibility(View.VISIBLE);
        }
        else
        {
            findViewById(R.id.btn_login).setBackgroundResource(R.drawable.login);
            findViewById(R.id.btn_leaderboard).setVisibility(View.GONE);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoadAvatar extends AsyncTask<String, String, String> {
        boolean error;
        String avatar;

        @Override
        protected void onPreExecute() {
            error = false;
            avatar = "";
        }

        protected String doInBackground(String... args) {
            try
            {
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setConnectTimeout(TimeOut);
                urlConnection.getInputStream();
                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                if (urlConnection.getResponseCode() != 200)
                    throw new EmptyStackException();
                avatar = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                error = true;
                setUsername("");
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            int avtr = -1;
            if (!error)
            {
                try
                {
                    JSONObject json = new JSONObject(avatar);
                    if (json.getInt("success") == 0)
                        throw new EmptyStackException();
                    else
                    {
                        JSONObject infosObject = json.getJSONArray("infos").getJSONObject(0);
                        avtr = infosObject.getInt("avatar");
                    }
                }
                catch (Exception e)
                {
                    setUsername("");
                }
            }
            setAvatar(avtr);
        }
    }
}
