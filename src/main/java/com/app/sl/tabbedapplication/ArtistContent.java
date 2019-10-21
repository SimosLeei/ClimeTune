package com.app.sl.tabbedapplication;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistContent extends Activity {

    private ArrayList<? extends Albums> AlbumsInArtistList;
    private static ArrayList<Song> AlbumSongs;

    TextView ChoosenArtist;
    Typeface tf;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.artist_list);

        tf = ResourcesCompat.getFont(getApplicationContext(),R.font.liquidrom);


        Intent getUIlistArtist = getIntent();
        AlbumsInArtistList =  getUIlistArtist.getParcelableArrayListExtra("artistAlbums");
        String ArtistName = getUIlistArtist.getStringExtra("ArtistName");

        ChoosenArtist = findViewById(R.id.ArtistName_In_ArtistContent);
        ChoosenArtist.setText(ArtistName);
        ChoosenArtist.setTypeface(tf);

        GridView gridView = findViewById(R.id.Artists_Albums);
        AlbumAdapter albumAdapter = new AlbumAdapter((ArrayList<Albums>) AlbumsInArtistList,getApplicationContext());
        gridView.setAdapter(albumAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Albums currAlbum = AlbumsInArtistList.get(position);
                Log.d("Positionis", String.valueOf(currAlbum.getAlbumid()));
                MatchSongstoArtistAlbum( currAlbum.getAlbumid());
                Log.d("SongsAre:", String.valueOf(AlbumSongs.size()));

                Intent WatchArtistAlbumContent = new Intent(getApplicationContext(),AlbumContent.class);
                WatchArtistAlbumContent.putExtra("albumSongs",AlbumSongs);
                WatchArtistAlbumContent.putExtra("AlbumName",currAlbum.getAlbumName());
                startActivity(WatchArtistAlbumContent);

            }
        });

    }
    private void MatchSongstoArtistAlbum(final int Albumid) {

        ContentResolver contentResolver = getApplicationContext().getContentResolver();
        Uri AlbumSongUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor AlbumsongsCursor = contentResolver.query(AlbumSongUri, null, null, null, null);
        AlbumSongs = new ArrayList<Song>();

        // if the cursor is null.
        if (AlbumsongsCursor != null && AlbumsongsCursor.moveToFirst()) {

            int IdColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int TitleColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int ArtistColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int AlbumColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int DurationColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int AlbumIdColumn = AlbumsongsCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);


            do {

                Long thisId = AlbumsongsCursor.getLong(IdColumn);
                int thisAlbumId = AlbumsongsCursor.getInt(AlbumIdColumn);
                String thisTitle = AlbumsongsCursor.getString(TitleColumn);
                String thisArtist = AlbumsongsCursor.getString(ArtistColumn);
                String thisAlbum = AlbumsongsCursor.getString(AlbumColumn);
                String RawDuration = AlbumsongsCursor.getString(DurationColumn);


                int Duration = (Integer.parseInt(RawDuration) / 1000);
                int mins = Duration / 60;
                Duration = Duration % 60;
                String FinalDuration = String.format("%02d:%02d", mins, Duration);
                if (thisAlbumId == Albumid) {

                    if (Duration > 0.1) {
                        AlbumSongs.add(new Song(thisId, thisTitle, thisArtist, thisAlbum, FinalDuration, thisAlbumId));
                        Log.d("ArraySizeAlSongs", String.valueOf(AlbumSongs.size()));
                    }
                }
            } while (AlbumsongsCursor.moveToNext());

            // For best practices, close the cursor after use.
        }AlbumsongsCursor.close();
    }
}
