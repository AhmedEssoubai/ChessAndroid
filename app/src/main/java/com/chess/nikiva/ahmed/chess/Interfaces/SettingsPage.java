package com.chess.nikiva.ahmed.chess.Interfaces;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 22/04/2018.
 */

public class SettingsPage extends Activity {

    Button btn_color_selected;
    LinearLayout ll_board_colors;
    private int[] controleColor;
    TextView tv_settings, tv_theme, tv_language, tv_about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_page);
        btn_color_selected = findViewById(R.id.btn_color_selected);
        ll_board_colors = findViewById(R.id.ll_board_colors);
        controleColor = new int[]{R.color.colorC1, R.color.colorC2, R.color.colorC3, R.color.colorC4};
        btn_color_selected.setBackgroundColor(getResources().getColor(controleColor[MainPage.getBoardColor()]));
        tv_settings = findViewById(R.id.tv_settings);
        tv_theme = findViewById(R.id.tv_theme);
        tv_language = findViewById(R.id.tv_language);
        tv_about = findViewById(R.id.tv_about);
        Spinner sp_lang = findViewById(R.id.sp_lang);
        sp_lang.setSelection(MainPage.getLang());
        sp_lang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sp_lang_onItemClick(position);
            }
        });
        SetLang();
    }

    private void SetLang()
    {
        if (MainPage.getLang() == 1)
        {
            tv_settings.setText(R.string.fr_settings);
            tv_theme.setText(R.string.fr_theme);
            tv_language.setText(R.string.fr_language);
            tv_about.setText(R.string.fr_about);
        }
        else
        {
            tv_settings.setText(R.string.en_settings);
            tv_theme.setText(R.string.en_theme);
            tv_language.setText(R.string.en_language);
            tv_about.setText(R.string.en_about);
        }
    }

    public void btn_color_selected_onClick(View view) {
        btn_color_selected.setVisibility(View.GONE);
        ll_board_colors.setVisibility(View.VISIBLE);
    }

    public void btn_color_onClick(View view) {
        switch (view.getId())
        {
            case R.id.btn_color1:
                MainPage.setBoardColor(0);
                break;
            case R.id.btn_color2:
                MainPage.setBoardColor(1);
                break;
            case R.id.btn_color3:
                MainPage.setBoardColor(2);
                break;
            case R.id.btn_color4:
                MainPage.setBoardColor(3);
                break;
        }
        btn_color_selected.setBackgroundColor(getResources().getColor(controleColor[MainPage.getBoardColor()]));
        btn_color_selected.setVisibility(View.VISIBLE);
        ll_board_colors.setVisibility(View.GONE);
    }

    private void sp_lang_onItemClick(int position){
        MainPage.setLang(position);
        SetLang();
    }
}
