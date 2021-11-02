package com.chess.nikiva.ahmed.chess.Interfaces.GameModes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.Interfaces.MainPage;
import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 27/04/2018.
 */

public class GameModePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_mode_page);
        if (MainPage.getUsername().isEmpty())
            findViewById(R.id.btn_PvsCC).setVisibility(View.GONE);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_play)).setText(R.string.fr_play);
            ((Button)findViewById(R.id.btn_PvsCC)).setText(R.string.fr_player_vs_computer);
            ((Button)findViewById(R.id.btn_PvsC)).setText(R.string.fr_player_vs_computer);
            ((Button)findViewById(R.id.btn_PvsP)).setText(R.string.fr_player_vs_player);
            ((Button)findViewById(R.id.btn_CvsC)).setText(R.string.fr_computer_vs_computer);
        }
    }

    public void btn_PvsCC_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerVsComputerCmpPage.class);
        startActivity(intent);
        finish();
    }

    public void btn_PvsC_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerVsComputerPage.class);
        startActivity(intent);
        finish();
    }

    public void btn_PvsP_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, PlayerVsPlayerPage.class);
        startActivity(intent);
        finish();
    }

    public void btn_CvsC_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, ComputerVsComputerPage.class);
        startActivity(intent);
        finish();
    }
}
