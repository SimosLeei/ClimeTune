package com.app.sl.tabbedapplication;


import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class SongAdapter extends BaseAdapter {

    private ArrayList<Song> songs;
    LayoutInflater songinf;
    Typeface tf;

    public SongAdapter(ArrayList<Song> theSongs,Context context){
        songs=theSongs;
        songinf = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout)songinf.inflate
                (R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        TextView durationView = (TextView)songLay.findViewById(R.id.song_duration);
        //get song using position
        Song currSong = songs.get(position);
        //get title and artist strings
        songView.setText(currSong.getTitle());
        tf = ResourcesCompat.getFont(songView.getContext(),R.font.liquidrom);
        songView.setTypeface(tf);
        artistView.setText(currSong.getArtist());
        tf = ResourcesCompat.getFont(artistView.getContext(),R.font.liquidrom);
        artistView.setTypeface(tf);
        durationView.setText(currSong.getDuration());
        tf = ResourcesCompat.getFont(durationView.getContext(),R.font.liquidrom);
        durationView.setTypeface(tf);
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return songs.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }


}