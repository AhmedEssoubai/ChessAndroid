package com.chess.nikiva.ahmed.chess.Interfaces;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.chess.nikiva.ahmed.chess.Interfaces.GameModes.MatchMode;
import com.chess.nikiva.ahmed.chess.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import TheGame.GameAndTurns.Match;

/**
 * Created by AHMED on 28/04/2018.
 */

public class ReplayMatchesPage extends Activity {

    public static Matches matches;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.replay_matchs_page);
        if (MainPage.getLang() == 1)
        {
            ((TextView)findViewById(R.id.tv_title)).setText(R.string.fr_replay_matchs);
            ((TextView)findViewById(R.id.tv_empty)).setText(R.string.fr_there_is_no_matches_to_replay);
        }
        List<MatchModel> Items = new ArrayList<>();
        for (int i  = 0; i < matches.getMatchesCount(); i++)
        {
            //Toast.makeText(this, String.valueOf(matches.getMatcheModel(i).getMatchMode()), Toast.LENGTH_LONG).show();
            Items.add(matches.getMatcheModel(i));
        }
        ReplayMatchAdapter adapter = new ReplayMatchAdapter(Items, this);
        ((ListView) findViewById(R.id.list)).setAdapter(adapter);
        if (Items.size() > 0)
            findViewById(R.id.tv_empty).setVisibility(View.GONE);
    }
}

class Matches implements Serializable
{
    private List<Match> listMatches;
    Matches()
    {
        listMatches = new ArrayList<>();
    }

    int getMatchesCount()
    {
        return listMatches.size();
    }

    Match getMatche(int index)
    {
        return listMatches.get(index);
    }

    void removeMatches(int index)
    {
        listMatches.remove(index);
    }

    MatchModel getMatcheModel(int index)
    {
        Match match = listMatches.get(index);
        return new MatchModel(match.getDatetime(), match.getTime(), match.getResult(), match.getMatchMode(), index);
    }

    void addMatch(Match match)
    {
        listMatches.add(match);
    }

}

class ReplayMatchAdapter extends BaseAdapter
{
    private List<MatchModel> Items;
    private Activity activity;

    ReplayMatchAdapter(List<MatchModel> Items, Activity activity) {
        this.Items = Items;
        this.activity = activity;
    }

    @Override
    public Object getItem(int position) {
        return Items.get(position);
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public long getItemId(int position) {
        return this.Items.get(position).getId();
    }

    private void deleteMatch(int id){
        for(int i = 0; i < Items.size(); i++)
            if (Items.get(i).getId() == id){
                Items.remove(i);
                ReplayMatchesPage.matches.removeMatches(i);
                break;
            }
        try
        {
            FileOutputStream fos = activity.openFileOutput("matches_for_replay.bin", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(ReplayMatchesPage.matches);
            os.close();
            fos.close();
        }
        catch (Exception e){
        }
        this.notifyDataSetChanged();
    }

    private void watchMatch(int id)
    {
        int index = -1;
        for(int i = 0; i < Items.size(); i++)
            if (Items.get(i).getId() == id)
                index = i;
        Intent intent = new Intent();
        intent.setClass(activity, MainGame.class);
        intent.putExtra("indexMatch", index + 1);
        activity.startActivity(intent);
        activity.finish();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.match_for_replay, null);
        //Date d = this.Items.get(position).getDatetime();
        if (this.Items.get(position).getMatchMode() == MatchMode.PlayerVsComputer)
            ((ImageView)view.findViewById(R.id.imgv_mode)).setImageResource(R.drawable.mode_vs_computer);
        else if (this.Items.get(position).getMatchMode() == MatchMode.PlayerVsPlayer)
            ((ImageView)view.findViewById(R.id.imgv_mode)).setImageResource(R.drawable.mode_vs_player);
        else if (this.Items.get(position).getMatchMode() == MatchMode.ComputerVsComputer)
            ((ImageView)view.findViewById(R.id.imgv_mode)).setImageResource(R.drawable.mode_watch);
        else
            ((ImageView)view.findViewById(R.id.imgv_mode)).setImageResource(R.drawable.mode_rank);
        if (this.Items.get(position).getResultIndex() == 1)
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorWon);
        else if (this.Items.get(position).getResultIndex() == 2)
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorLost);
        else
            (view.findViewById(R.id.ll_match)).setBackgroundResource(R.color.colorDraw);
        ((TextView)view.findViewById(R.id.tv_date)).setText(this.Items.get(position).getDatetime().toString());
        ((TextView)view.findViewById(R.id.tv_duration)).setText((this.Items.get(position).getDuration() > -1) ? String.format("%s min", String.valueOf(this.Items.get(position).getDuration())) : "∞");
        ((TextView)view.findViewById(R.id.tv_result)).setText(this.Items.get(position).getResult());
        final int id = this.Items.get(position).getId();
        view.findViewById(R.id.ib_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMatch(id);
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                watchMatch(id);
            }
        };
        view.findViewById(R.id.imgv_mode).setOnClickListener(onClickListener);
        view.findViewById(R.id.ll_match).setOnClickListener(onClickListener);
        view.findViewById(R.id.tv_date).setOnClickListener(onClickListener);
        view.findViewById(R.id.tv_result).setOnClickListener(onClickListener);
        return view;
    }
}

class MatchModel
{
    private Date datetime;
    private int duration;
    private int result;
    private MatchMode matchMode;
    private int id;

    MatchModel(Date datetime, int duration, int result, MatchMode matchMode, int id){
        this.datetime = datetime;
        this.duration = duration;
        this.result = result;
        this.matchMode = matchMode;
        this.id = id;
    }

    Date getDatetime() {
        return datetime;
    }

    int getDuration() {
        return duration;
    }

    int getResult() {
        if (result == 0)
            return ((MainPage.getLang() == 1) ? R.string.fr_draw : R.string.en_draw);
        else if (result == 1)
            return ((MainPage.getLang() == 1) ? R.string.fr_won : R.string.en_won);
        else
            return ((MainPage.getLang() == 1) ? R.string.fr_lost : R.string.en_lost);
    }

    int getResultIndex() {
        return result;
    }

    MatchMode getMatchMode() {
        return matchMode;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }
}
