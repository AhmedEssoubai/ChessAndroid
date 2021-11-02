package com.chess.nikiva.ahmed.chess.Interfaces.GameModes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.Interfaces.MainGame;
import com.chess.nikiva.ahmed.chess.Interfaces.MainPage;
import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 27/04/2018.
 */

public class ComputerVsComputerPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.computer_vs_computer);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_computer_vs_computer);
            ((TextView)findViewById(R.id.tv_white)).setText(R.string.fr_white);
            ((TextView)findViewById(R.id.tv_wlevel)).setText(getResources().getString(R.string.fr_level) + " 4");
            ((TextView)findViewById(R.id.tv_black)).setText(R.string.fr_black);
            ((TextView)findViewById(R.id.tv_blevel)).setText(getResources().getString(R.string.fr_level) + " 4");
            ((Button)findViewById(R.id.btn_play)).setText(R.string.fr_play);
        }
        ((SeekBar)findViewById(R.id.sb_blevel)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.tv_blevel)).setText(String.format("%s %d", getResources().getString(MainPage.getLang() == 1 ? R.string.fr_level : R.string.en_level), progress + 1));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        ((SeekBar)findViewById(R.id.sb_wlevel)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ((TextView)findViewById(R.id.tv_wlevel)).setText(String.format("%s %d", getResources().getString(MainPage.getLang() == 1 ? R.string.fr_level : R.string.en_level), progress));
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
        intent.putExtra("whiteLevel", ((SeekBar)findViewById(R.id.sb_wlevel)).getProgress() + 1);
        intent.putExtra("blackLevel", ((SeekBar)findViewById(R.id.sb_blevel)).getProgress() + 1);
        intent.putExtra("competition", false);
        intent.putExtra("time", -1);
        startActivity(intent);
        finish();
    }
}
