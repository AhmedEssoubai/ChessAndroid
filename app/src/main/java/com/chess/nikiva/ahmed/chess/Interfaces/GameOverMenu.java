package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.R;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

import TheGame.Interface.Board;
import TheGame.ThePlayer.Pieces.PieceType;

/**
 * Created by AHMED on 13/04/2018.
 */

@SuppressLint("ValidFragment")
public class GameOverMenu extends DialogFragment implements View.OnClickListener {

    MainGame mainGame;
    View form;

    @SuppressLint("ValidFragment")
    public GameOverMenu(MainGame mainGame)
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
        form = inflater.inflate(R.layout.end_game, container, false);
        ((TextView)form.findViewById(R.id.tv_gameover)).setText(!mainGame.isGameOverByTime() ? "GameOver" : ((MainPage.getLang() == 1 ? "Temps Écoulé" : "Time Up")));
        ((TextView)form.findViewById(R.id.tv_result)).setText(mainGame.getResult() == 0 ? ((MainPage.getLang() == 1 ? "Équivalent" : "Draw")) : mainGame.getResult() == 1 ? ((MainPage.getLang() == 1 ? "Blanc a gagné" : "White won")) : ((MainPage.getLang() == 1 ? "Noir a gagné" : "Black won")));
        Button btn_resume = form.findViewById(R.id.btn_resume),
                btn_restart = form.findViewById(R.id.btn_restart),
                btn_back_menu = form.findViewById(R.id.btn_back_menu),
                btn_save = form.findViewById(R.id.btn_save);
        if (MainPage.getLang() == 1)
        {
            btn_resume.setText(R.string.fr_resume);
            btn_restart.setText(R.string.fr_restart);
            btn_back_menu.setText(R.string.fr_back_to_main_menu);
            btn_save.setText(R.string.fr_save_match_for_replay_it);
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
        btn_back_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                mainGame.finish();
            }
        });
        if (!mainGame.isMatchRepay())
            btn_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ReplayMatchesPage.matches.addMatch(mainGame.getMatch());
                    //Toast.makeText(mainGame, String.valueOf(ReplayMatchesPage.matches.getMatcheModel(ReplayMatchesPage.matches.getMatchesCount() - 1).getMatchMode()), Toast.LENGTH_LONG).show();
                    try {
                        FileOutputStream fos = mainGame.openFileOutput("matches_for_replay.bin", Context.MODE_PRIVATE);
                        ObjectOutputStream os = new ObjectOutputStream(fos);
                        os.writeObject(ReplayMatchesPage.matches);
                        os.close();
                        fos.close();
                        Toast.makeText(mainGame, mainGame.isGameOverByTime() ? "Enregistré" : "Saved", Toast.LENGTH_LONG).show();
                    }
                    catch (Exception e){
                        Toast.makeText(mainGame, mainGame.isGameOverByTime() ? "pas Enregistré" : "not Saved", Toast.LENGTH_LONG).show();
                    }
                    form.findViewById(R.id.btn_save).setVisibility(View.GONE);
                }
            });
        else
            form.findViewById(R.id.btn_save).setVisibility(View.GONE);
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
        mainGame.stopTime();
    }

}
