package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 29/05/2018.
 */

public class Help extends DialogFragment {

    public Help()
    {
        super();
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
        View form = inflater.inflate(R.layout.help, container, false);
        if (MainPage.getLang() == 1)
        {
            ((TextView)form.findViewById(R.id.tv_help)).setText(R.string.fr_help);
            ((TextView)form.findViewById(R.id.tv_help_text)).setText(R.string.fr_help_text);
        }
        return form;
    }
}
