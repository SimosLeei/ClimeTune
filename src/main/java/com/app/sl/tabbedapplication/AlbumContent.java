package com.app.sl.tabbedapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AlbumContent extends Activity {


    private ArrayList<? extends Song> AlbumSongsList;
    TextView ChoosenAlbum;
    Typeface tf;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.album_list);

        tf = ResourcesCompat.getFont(getApplicationContext(),R.font.liquidrom);


        Intent getUIlist = getIntent();
        AlbumSongsList =  getUIlist.getParcelableArrayListExtra("albumSongs");
        String AlbumName = getUIlist.getStringExtra("AlbumName");

        ChoosenAlbum = findViewById(R.id.AlbumName_In_AlbumContent);
        ChoosenAlbum.setText(AlbumName);
        ChoosenAlbum.setTypeface(tf);

        ListView listView = findViewById(R.id.albums_Songs);
        AlbumSongsAdapter albumSongsAdapter = new AlbumSongsAdapter((ArrayList<Song>) AlbumSongsList,getApplicationContext());
        listView.setAdapter(albumSongsAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent startmusicactivity = new Intent(getApplicationContext(),PlayMusicActiviy.class);
                startmusicactivity.putExtra("SongList",AlbumSongsList);
                startmusicactivity.putExtra("pos",position);
                startActivity(startmusicactivity);

            }
        });

     }


}
