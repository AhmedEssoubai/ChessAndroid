package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.GameModePage;
import com.chess.nikiva.ahmed.chess.R;

import java.io.File;

import TheGame.Interface.Board;
import TheGame.ThePlayer.Pieces.PieceType;

/**
 * Created by AHMED on 13/04/2018.
 */

@SuppressLint("ValidFragment")
public class PauseMenu extends DialogFragment implements View.OnClickListener {

    MainGame mainGame;

    @SuppressLint("ValidFragment")
    public PauseMenu(MainGame mainGame)
    {
        super();
        this.mainGame = mainGame;
    }

    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View form = inflater.inflate(R.layout.pause_menu, container, false);
        Button btn_resume = form.findViewById(R.id.btn_resume),
                btn_restart = form.findViewById(R.id.btn_restart),
                btn_new_game = form.findViewById(R.id.btn_new_game),
                btn_back_menu = form.findViewById(R.id.btn_back_menu);
        if (MainPage.getLang() == 1)
        {
            btn_resume.setText(R.string.fr_resume);
            btn_restart.setText(R.string.fr_restart);
            btn_new_game.setText(R.string.fr_new_game);
            btn_back_menu.setText(R.string.fr_back_to_main_menu);
        }
        btn_resume.setOnClickListener(this);
        if (mainGame.isMatchRepay())
            btn_restart.setBackgroundResource(R.drawable.non_available_button);
        else
            btn_restart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    mainGame.newGame();
                }
            });
        btn_new_game.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainPage.game = null;
                File dir = mainGame.getFilesDir();
                File file = new File(dir, "match_save.bin");
                file.delete();
                Intent intent = new Intent();
                intent.setClass(mainGame, GameModePage.class);
                startActivity(intent);
                mainGame.finish();
            }
        });
        btn_back_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mainGame.finish();
            }
        });
        return form;
    }

    @Override
    public void onClick(View view)
    {
        this.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mainGame.isGameOver())
            mainGame.stopTime();
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (!mainGame.isGameOver())
            mainGame.startTime();
    }
}
