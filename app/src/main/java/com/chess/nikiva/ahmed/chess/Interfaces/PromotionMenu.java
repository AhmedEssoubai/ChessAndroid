package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.RadioButton;

import com.chess.nikiva.ahmed.chess.R;

import java.util.function.Function;

import TheGame.Interface.Board;
import TheGame.ThePlayer.Pieces.Piece;
import TheGame.ThePlayer.Pieces.PieceType;

/**
 * Created by AHMED on 13/04/2018.
 */

@SuppressLint("ValidFragment")
public class PromotionMenu extends DialogFragment implements View.OnClickListener {

    private View form;
    Board board;

    @SuppressLint("ValidFragment")
    public PromotionMenu(Board board)
    {
        super();
        this.board = board;
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
        this.form = inflater.inflate(R.layout.promotion_menu, container, false);
        this.form.findViewById(R.id.btn_promotion).setOnClickListener(this);
        return form;
    }

    @Override
    public void onClick(View view)
    {
        this.dismiss();
    }

    public PieceType GetPromotionPiece()
    {
        PieceType pieceType = PieceType.Queen;
        if (((RadioButton)this.form.findViewById(R.id.rb_knight)).isChecked())
            pieceType = PieceType.Knight;
        else if (((RadioButton)this.form.findViewById(R.id.rb_rook)).isChecked())
            pieceType = PieceType.Rook;
        else if (((RadioButton)this.form.findViewById(R.id.rb_bishop)).isChecked())
            pieceType = PieceType.Bishop;
        return pieceType;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        board.MakeThePromotion();
    }

    /*public Button GetPromotionButton()
    {
        return this.form.findViewById(R.id.btn_promotion);
    }*/

}
