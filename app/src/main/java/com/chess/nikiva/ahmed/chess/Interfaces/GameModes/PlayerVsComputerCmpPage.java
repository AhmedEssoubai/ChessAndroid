package com.chess.nikiva.ahmed.chess.Interfaces.GameModes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.Interfaces.MainGame;
import com.chess.nikiva.ahmed.chess.Interfaces.MainPage;
import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 27/04/2018.
 */

public class PlayerVsComputerCmpPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_vs_computer_cmp);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_player_vs_computer);
            ((TextView)findViewById(R.id.tv_level)).setText(getResources().getString(R.string.fr_level) + " 4");
            ((Button)findViewById(R.id.btn_play)).setText(R.string.fr_play);
        }
        ((SeekBar)findViewById(R.id.sb_level)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.tv_level)).setText(String.format("%s %d", getResources().getString(MainPage.getLang() == 1 ? R.string.fr_level : R.string.en_level), progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void btn_play_onClick(View view) {
        Intent intent = new Intent();
        intent.setClass(this, MainGame.class);
        intent.putExtra("whiteLevel", 0);
        intent.putExtra("blackLevel", ((SeekBar)findViewById(R.id.sb_level)).getProgress() + 1);
        intent.putExtra("competition", true);
        intent.putExtra("time", -1);
        startActivity(intent);
        finish();
    }

}
