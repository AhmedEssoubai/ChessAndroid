package com.chess.nikiva.ahmed.chess.Interfaces.GameModes;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.Interfaces.MainGame;
import com.chess.nikiva.ahmed.chess.Interfaces.MainPage;
import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 27/04/2018.
 */

public class PlayerVsPlayerPage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_vs_player);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_player_vs_computer);
            ((TextView)findViewById(R.id.tv_clock)).setText(R.string.fr_clock_time);
            ((Button)findViewById(R.id.btn_play)).setText(R.string.fr_play);
        }
    }

    public void btn_play_onClick(View view) {
        RadioButton[] options = new RadioButton[]{findViewById(R.id.rb_opt1), findViewById(R.id.rb_opt2), findViewById(R.id.rb_opt3), findViewById(R.id.rb_opt4), findViewById(R.id.rb_opt5), findViewById(R.id.rb_opt6)};
        int[] time = new int[]{-1, 5, 10, 15, 20, 30};
        int index;
        for (index = 0; index < options.length; index++)
            if (options[index].isChecked())
                break;
        Intent intent = new Intent();
        intent.setClass(this, MainGame.class);
        intent.putExtra("whiteLevel", 0);
        intent.putExtra("blackLevel", 0);
        intent.putExtra("competition", false);
        intent.putExtra("time", time[index]);
        startActivity(intent);
        finish();
    }
}
