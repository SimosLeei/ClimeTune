package com.app.sl.tabbedapplication;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class AlbumTab extends android.support.v4.app.Fragment {


    private static ArrayList<Albums> AlbumArrayList;
    private static ArrayList<Song> AlbumSongs;
    private ContentResolver AlbumResolver;
    Song song;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_tab, container, false);
        getAlbums();
        final GridView gridView = view.findViewById(R.id.AlbumGrid);
        final AlbumAdapter AlbAdt = new AlbumAdapter(AlbumArrayList, getActivity());
        gridView.setAdapter(AlbAdt);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Albums currAlbum = AlbumArrayList.get(position);
                Log.d("Positionis", String.valueOf(currAlbum.getAlbumid()));
                MatchSongstoAlbum( currAlbum.getAlbumid());
                Log.d("SongsAre:", String.valueOf(AlbumSongs.size()));
                Intent WatchAlbumContent = new Intent(getContext(),AlbumContent.class);
                WatchAlbumContent.putExtra("albumSongs",AlbumSongs);
                WatchAlbumContent.putExtra("AlbumName",currAlbum.getAlbumName());
                startActivity(WatchAlbumContent);


            }
        });

        return view;
    }

    public void onDestroyView(){
        super.onDestroyView();
    }


    public void onDetach(){
        super.onDetach();
    }

    public void getAlbums() {
        AlbumResolver = getActivity().getContentResolver();
        Uri albumArtUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor AlbumCursor = AlbumResolver.query(albumArtUri, null, null, null, null);
        AlbumArrayList = new ArrayList<Albums>();


        if (AlbumCursor != null && AlbumCursor.moveToFirst()) {

            int AlbumColumn = AlbumCursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int AlbumArtColumn = AlbumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int AlbumId = AlbumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);
            int AlbumContent = AlbumCursor.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do {

                String thisAlbumTitle = AlbumCursor.getString(AlbumColumn);
                String thisAlbumArt = AlbumCursor.getString(AlbumArtColumn);
                int thisAlbumId = AlbumCursor.getInt(AlbumId);
                String thisArtist = AlbumCursor.getString(AlbumContent);

                    AlbumArrayList.add(new Albums(thisAlbumTitle, thisAlbumArt, thisArtist, thisAlbumId));

                    Log.d("ArraySizeAlbums", String.valueOf(AlbumArrayList.size()));
            } while (AlbumCursor.moveToNext());
        }
        AlbumCursor.close();
    }

    private void MatchSongstoAlbum(final int Albumid) {

                ContentResolver contentResolver = getContext().getContentResolver();
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

