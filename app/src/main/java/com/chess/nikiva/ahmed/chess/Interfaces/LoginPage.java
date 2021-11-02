package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EmptyStackException;

/**
 * Created by AHMED on 27/04/2018.
 */

public class LoginPage extends Activity {

    EditText et_username, et_password;
    boolean working;
    TextView tv_error;
    ProgressBar pb_progress;
    String[] LoginErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);
        et_username = findViewById(R.id.et_username);
        et_password = findViewById(R.id.et_password);
        tv_error = findViewById(R.id.tv_error);
        pb_progress = findViewById(R.id.pb_progress);
        if (MainPage.getLang() == 1)
        {
            et_username.setHint(R.string.fr_user_name);
            et_password.setHint(R.string.fr_password);
            ((Button)findViewById(R.id.btn_sign_up)).setText(R.string.fr_sing_up);
        }
        working = false;
        LoginErrors = new String[]{"Username or password is wrong", "There is a problem connecting to the database", "Le nom d'utilisateur ou le mot de passe est incorrect", "Un problème est survenu lors de la connexion à la base de données"};
    }

    public void btn_sign_up_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, SignUpPage.class);
        startActivity(intent);
    }

    String username;

    public void btn_login_onClick(View view)
    {
        if (!working) {
            username = et_username.getText().toString();
            String password = et_password.getText().toString();
            if (username.length() < 5 || password.length() < 8)
                tv_error.setText(LoginErrors[MainPage.getLang() * 2]);
            else
                new Login().execute(MainPage.Host + "login.php?username=" + username.trim().replace("\n", "") + "&password=" + password);
        }
    }

    private void LoginSecssed()
    {
        MainPage.setUsername(username);
        Intent intent = new Intent();
        intent.setClass(this, ProfilePage.class);
        startActivity(intent);
        finish();
    }

    // the result
    String loginResult;

    @SuppressLint("StaticFieldLeak")
    class Login extends AsyncTask<String, String, String> {
        String errorStat;

        @Override
        protected void onPreExecute() {
            //super.onPreExecute();
            working = true;
            loginResult = "";
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
                loginResult = StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                errorStat = getResources().getString((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
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
                    JSONObject json = new JSONObject(loginResult);
                    int error = json.getInt("error");
                    if (error != -1)
                        errorStat = LoginErrors[error + MainPage.getLang() * 2];
                    else
                        LoginSecssed();
                    pb_progress.setProgress(100);
                }
                catch (Exception e)
                {
                    errorStat = getResources().getString((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
                }
            }
            tv_error.setText(errorStat);
            pb_progress.setProgress(0);
            working = false;
        }

    }

    public static String StreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String Text = "", line;
        try
        {
            while ((line = bufferedReader.readLine()) != null)
                Text += line;
            inputStream.close();
        }
        catch (Exception e){}
        return Text;
    }
}
