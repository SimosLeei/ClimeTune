package com.app.sl.tabbedapplication;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class AlbumSongsAdapter extends BaseAdapter {

    private ArrayList<Song> theAlbumSongs;
    private LayoutInflater Albumsonginf;
    Typeface tf;

    public AlbumSongsAdapter(ArrayList<Song> theSongs,Context context) {
       theAlbumSongs =  theSongs;
       Albumsonginf = LayoutInflater.from(context);

    }
    @Override
    public int getCount() {
        return theAlbumSongs.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public class ViewHolder{
        TextView title;
        TextView artist;
        TextView duration;

    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final AlbumSongsAdapter.ViewHolder albumSongsDisplayHolder;
        View songAlbumLay = convertView;

        Log.d("ViewPos", String.valueOf(position)+convertView);
        if(songAlbumLay==null) {

            songAlbumLay = (LinearLayout)Albumsonginf.inflate
                    (R.layout.album_songs, parent, false);

            albumSongsDisplayHolder = new AlbumSongsAdapter.ViewHolder();
            albumSongsDisplayHolder.title=(TextView)songAlbumLay.findViewById(R.id.Album_song_title);
            albumSongsDisplayHolder.artist=(TextView)songAlbumLay.findViewById(R.id.Album_song_artist);
            albumSongsDisplayHolder.duration=(TextView)songAlbumLay.findViewById(R.id.Album_song_duration);
            songAlbumLay.setTag(albumSongsDisplayHolder);

        }
        else{
            albumSongsDisplayHolder = (AlbumSongsAdapter.ViewHolder) songAlbumLay.getTag();
        }


        tf = ResourcesCompat.getFont(albumSongsDisplayHolder.artist.getContext(),R.font.liquidrom);
        Song currSong = theAlbumSongs.get(position);

        albumSongsDisplayHolder.title.setText(currSong.getTitle());
        albumSongsDisplayHolder.title.setTypeface(tf);

        albumSongsDisplayHolder.artist.setText(currSong.getArtist());
        albumSongsDisplayHolder.artist.setTypeface(tf);

        albumSongsDisplayHolder.duration.setText((currSong.getDuration()));
        albumSongsDisplayHolder.duration.setTypeface(tf);

        return songAlbumLay;
    }

}
