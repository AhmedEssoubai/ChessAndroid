package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.R;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.EmptyStackException;

/**
 * Created by AHMED on 27/04/2018.
 */

public class SignUpPage extends Activity {

    int avatar;
    boolean forEdit, working;
    AvatarsMenu avatarsMenu;
    TextView tv_error;
    ImageButton ib_avatar;
    ProgressBar pb_progress;
    EditText et_username, et_fist_name, et_last_name, et_password, et_Cpassword, et_Oldpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_page);
        ib_avatar = findViewById(R.id.ib_avatar);
        et_username = findViewById(R.id.et_username);
        et_fist_name = findViewById(R.id.et_fist_name);
        et_last_name = findViewById(R.id.et_last_name);
        et_password = findViewById(R.id.et_password);
        et_Cpassword = findViewById(R.id.et_Cpassword);
        pb_progress = findViewById(R.id.pb_progress);
        tv_error = findViewById(R.id.tv_error);
        this.working = false;
        if (!MainPage.getUsername().isEmpty())
        {
            findViewById(R.id.btn_help).setVisibility(View.VISIBLE);
            et_username.setVisibility(View.GONE);
            ((Button)findViewById(R.id.btn_sign_up)).setText((MainPage.getLang() == 1) ? R.string.fr_save : R.string.en_save);
            et_Oldpassword = findViewById(R.id.et_Oldpassword);
            et_Oldpassword.setVisibility(View.VISIBLE);
            et_password.setHint("New password");
            forEdit = true;
            avatar = 0;
            Intent intent = getIntent();
            if (intent != null) {
                Bundle extras = intent.getExtras();
                if (extras != null) {
                    ((TextView)findViewById(R.id.tv_sign_up)).setText(MainPage.getUsername());
                    et_fist_name.setText(extras.getString("fist_name"));
                    et_last_name.setText(extras.getString("last_name"));
                    setAvatar(extras.getInt("avatar"));
                }
            }
        }
        else
        {
            forEdit = false;
            avatar = 0;
            if (MainPage.getLang() == 1)
            {
                ((Button)findViewById(R.id.btn_sign_up)).setText(R.string.fr_sing_up);
                ((TextView)findViewById(R.id.tv_sign_up)).setText(R.string.fr_sing_up);
            }
        }
        if (MainPage.getLang() == 1)
        {
            et_username.setHint(R.string.fr_user_name);
            et_fist_name.setHint(R.string.fr_first_name);
            et_last_name.setHint(R.string.fr_last_name);
            et_password.setHint(R.string.fr_password);
            et_Cpassword.setHint(R.string.fr_password_confirmation);
            if (et_Oldpassword != null)
                et_Oldpassword.setHint(R.string.fr_password);
        }
    }

    public void setAvatar(int avatar)
    {
        this.avatar = avatar;
        ib_avatar.setBackgroundResource(Avatars.AvaratCList[this.avatar]);
    }

    public void ib_avatarOnClick(View view) {
        if (avatarsMenu == null)
            avatarsMenu = new AvatarsMenu(this);
        this.avatarsMenu.show(getFragmentManager(), null);
    }

    public void btn_sign_upOnClick(View view)
    {
        if (!working)
            tv_error.setText(forEdit ? Edit() : SignUp());
    }

    private String SignUp()
    {
        String username = et_username.getText().toString().trim(),
            firstname = et_fist_name.getText().toString().trim(),
            lastname = et_last_name.getText().toString().trim(),
            password = et_password.getText().toString();
        if (username.isEmpty() || firstname.isEmpty() || lastname.isEmpty() || password.isEmpty())
            return (MainPage.getLang() == 1) ? "Toutes les informations sont requises" : "All information is required";
        if (username.length() < 5)
            return (MainPage.getLang() == 1) ? "Le nom d'utilisateur doit contenir au moins 5 caractères" : "Username must contain at least 5 characters";
        if (password.length() < 8)
            return (MainPage.getLang() == 1) ? "Le mot de passe doit contenir au moins 8 caractères" : "Password must contain at least 8 characters";
        if (!password.equals((et_Cpassword.getText().toString())))
            return (MainPage.getLang() == 1) ? "Le mot de passe ne correspond pas" : "Password does not match";
        new SignUpAsync().execute(MainPage.Host + "signup.php?username=" + username + "&firstname=" + firstname + "&familyname=" + lastname + "&password=" + password + "&avatar=" + avatar);
        return "";
    }

    private String Edit()
    {
        String firstname = et_fist_name.getText().toString().trim(),
                lastname = et_last_name.getText().toString().trim();
        if (firstname.isEmpty() || lastname.isEmpty())
            return (MainPage.getLang() == 1) ? "Prénom et nom de famille requis" : "First name and Family name required";
        String password = et_Oldpassword.getText().toString();
        if (password.isEmpty())
            return (MainPage.getLang() == 1) ? "Mot de passe requis pour enregistrer les modifications" : "Password required to save the modifications";
        if (password.length() < 8)
            return (MainPage.getLang() == 1) ? "Mot de passe à court" : "Password to short";
        String newPassword = et_password.getText().toString();
        if (!newPassword.isEmpty())
        {
            if (newPassword.length() < 8)
                return (MainPage.getLang() == 1) ? "Le mot de passe doit contenir au moins 8 caractères" : "Password must contain at least 8 characters";
            if (!newPassword.equals((et_Cpassword.getText().toString())))
                return (MainPage.getLang() == 1) ? "Le mot de passe ne correspond pas" : "Password does not match";
            newPassword = "&newpassword=" + newPassword;
        }
        new SignUpAsync().execute(MainPage.Host + "updateProfile.php?username=" + (MainPage.getUsername()) + "&firstname=" + firstname + "&familyname=" + lastname + "&password=" + password + "&avatar=" + avatar + newPassword);
        return "";
    }

    private void SignUpSuccessed()
    {
        ProfilePage.profilePage.setAvatar(avatar);
        Toast.makeText(this, (forEdit) ? ((MainPage.getLang() == 1) ? "L'édition réussit" : "The edit succeed") : ((MainPage.getLang() == 1) ? "L'inscription réussie" : "The sign up succeed"), Toast.LENGTH_LONG).show();
        finish();
    }

    // the result
    String signUpResult;

    public void btn_helpOnClick(View view)
    {
        (new Help()).show(getFragmentManager(), null);
    }

    @SuppressLint("StaticFieldLeak")
    class SignUpAsync extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            working = true;
            signUpResult = "";
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
                signUpResult = LoginPage.StreamToString(inputStream);
                inputStream.close();
            }
            catch (Exception e)
            {
                tv_error.setText((MainPage.getLang() == 1) ? R.string.fr_server_error : R.string.en_server_error);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        protected void onPostExecute(String file_url) {
            if (!signUpResult.isEmpty())
                try
                {
                    pb_progress.setProgress(75);
                    JSONObject json = new JSONObject(signUpResult);
                    int error = json.getInt("error");
                    if (error != -1)
                        if (error == 0)
                            tv_error.setText((MainPage.getLang() == 1) ? R.string.fr_database_error : R.string.en_database_error);
                        else
                            tv_error.setText(forEdit ? "The password is wrong" : "The username is already exists");
                    else
                        SignUpSuccessed();
                    pb_progress.setProgress(100);
                }
                catch (Exception e)
                {
                    tv_error.setText((MainPage.getLang() == 1) ? R.string.fr_build_error : R.string.en_build_error);
                }
            pb_progress.setProgress(0);
            working = false;
        }

    }
}
