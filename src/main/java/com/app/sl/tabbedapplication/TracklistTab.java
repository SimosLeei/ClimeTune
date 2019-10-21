package com.app.sl.tabbedapplication;


import android.content.ContentResolver;

import android.content.Intent;

import android.database.Cursor;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class TracklistTab extends android.support.v4.app.Fragment  {


    private static ArrayList<Song> songArrayList;
    private ContentResolver musicResolver;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        getSongs();
        final ListView listView = view.findViewById(R.id.ListOfSongs);
        final SongAdapter songAdt = new SongAdapter(songArrayList, getActivity());
        listView.setAdapter(songAdt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

               Intent startmusicactivity = new Intent(getContext(),PlayMusicActiviy.class);
               startmusicactivity.putExtra("SongList",songArrayList);
               startmusicactivity.putExtra("pos",position);
               startActivity(startmusicactivity);
            }
        });
        return view;
    }


    public void getSongs() {
        musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri, null ,null, null, null);
        songArrayList = new ArrayList<Song>();


        if (musicCursor != null && musicCursor.moveToFirst()) {

            int IdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int TitleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int ArtistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int AlbumColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int DurationColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int AlbumIdColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);

            do {

                Long thisId = musicCursor.getLong(IdColumn);
                int thisAlbumId = musicCursor.getInt(AlbumIdColumn);
                String thisTitle = musicCursor.getString(TitleColumn);
                String thisArtist = musicCursor.getString(ArtistColumn);
                String thisAlbum = musicCursor.getString(AlbumColumn);
                String RawDuration = musicCursor.getString(DurationColumn);

                int Duration = (Integer.parseInt(RawDuration) / 1000);
                int mins = Duration / 60;
                Duration = Duration % 60;
                String FinalDuration = String.format("%02d:%02d", mins, Duration);
                if (Duration > 0.1) {

                        songArrayList.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, FinalDuration, thisAlbumId));

                    }
            }
            while (musicCursor.moveToNext());
        }musicCursor.close();
    }

}




