package com.chess.nikiva.ahmed.chess.Interfaces;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chess.nikiva.ahmed.chess.R;

/**
 * Created by AHMED on 11/05/2018.
 */

@SuppressLint("ValidFragment")
class AvatarsMenu extends DialogFragment implements View.OnClickListener {

    SignUpPage page;

    @SuppressLint("ValidFragment")
    public AvatarsMenu(SignUpPage page)
    {
        super();
        this.page = page;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View form = inflater.inflate(R.layout.avatars_menu, container, false);
        form.findViewById(R.id.ib_avatar0).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar1).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar2).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar3).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar4).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar5).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar6).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar7).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar8).setOnClickListener(this);
        form.findViewById(R.id.ib_avatar9).setOnClickListener(this);
        return form;
    }

    @Override
    public void onClick(View view)
    {
        int avatar = 0;
        switch (view.getId())
        {
            case R.id.ib_avatar1:
                avatar = 1;
                break;
            case R.id.ib_avatar2:
                avatar = 2;
                break;
            case R.id.ib_avatar3:
                avatar = 3;
                break;
            case R.id.ib_avatar4:
                avatar = 4;
                break;
            case R.id.ib_avatar5:
                avatar = 5;
                break;
            case R.id.ib_avatar6:
                avatar = 6;
                break;
            case R.id.ib_avatar7:
                avatar = 7;
                break;
            case R.id.ib_avatar8:
                avatar = 8;
                break;
            case R.id.ib_avatar9:
                avatar = 9;
                break;
        }
        page.setAvatar(avatar);
        this.dismiss();
    }

}
