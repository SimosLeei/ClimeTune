package com.app.sl.tabbedapplication;

import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class ArtistTab extends android.support.v4.app.Fragment {

    private ArrayList<Artist> ArtistArrayList;
    private ArrayList<Albums>  ArtistAlbums;
    private ContentResolver ArtistResolver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.artist_tab, container, false);
        getArtists();
        final ListView listView = view.findViewById(R.id.ListOfArtists);
        final ArtistAdapter artAdt = new ArtistAdapter(ArtistArrayList, getActivity());
        listView.setAdapter(artAdt);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Artist currArtist = ArtistArrayList.get(position);
                Log.d("AlbumsArtist", currArtist.getNameofArtist());
                MatchAlbumstoArtist( currArtist.getNameofArtist());
                Log.d("AlbumsAre:", String.valueOf(ArtistAlbums.size()));
                Intent goToArtistContent = new Intent(getContext(),ArtistContent.class);
                goToArtistContent.putExtra("artistAlbums",ArtistAlbums);
                goToArtistContent.putExtra("ArtistName",currArtist.getNameofArtist());
                startActivity(goToArtistContent);

            }
        });

        return view;
    }


    public void getArtists() {
        ArtistResolver = getActivity().getContentResolver();
        Uri ArtistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI;
        Cursor ArtistCursor = ArtistResolver.query(ArtistUri, null, null, null, null);
        ArtistArrayList = new ArrayList<Artist>();


        if (ArtistCursor != null && ArtistCursor.moveToFirst()) {

            int ArtistColumn = ArtistCursor.getColumnIndex(MediaStore.Audio.Artists.ARTIST);
            //int idColumn = ArtistCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int NoAlbumsColumn = ArtistCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_ALBUMS);
            int NoTracksColumn =ArtistCursor.getColumnIndex(MediaStore.Audio.Artists.NUMBER_OF_TRACKS);


            do {

                String thisArtist = ArtistCursor.getString(ArtistColumn);
               // int thisId = ArtistCursor.getInt(idColumn);
                String thisNoAlbums = ArtistCursor.getString(NoAlbumsColumn);
                String thisNoTracks = ArtistCursor.getString(NoTracksColumn);


                    ArtistArrayList.add(new Artist(thisArtist,thisNoAlbums,thisNoTracks));//thisId


            }
            while (ArtistCursor.moveToNext());
        }
    }
    private void MatchAlbumstoArtist(final String artistName) {

        ContentResolver ArtistAlbumResolver = getActivity().getContentResolver();
        Uri ArtistAlbumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor AlbumArtistCursot = ArtistAlbumResolver.query(ArtistAlbumUri, null, null, null, null);
        ArtistAlbums = new ArrayList<Albums>();


        if (AlbumArtistCursot != null && AlbumArtistCursot.moveToFirst()) {

            int AlbumColumn = AlbumArtistCursot.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int AlbumArtColumn = AlbumArtistCursot.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int AlbumId = AlbumArtistCursot.getColumnIndex(MediaStore.Audio.Albums._ID);
            int AlbumContent = AlbumArtistCursot.getColumnIndex(MediaStore.Audio.Albums.ARTIST);

            do {

                String thisAlbumTitle = AlbumArtistCursot.getString(AlbumColumn);
                String thisAlbumArt = AlbumArtistCursot.getString(AlbumArtColumn);
                int thisAlbumId = AlbumArtistCursot.getInt(AlbumId);
                String thisArtist = AlbumArtistCursot.getString(AlbumContent);


                Log.d("AlbumsArtistinAl",thisArtist);
                if (thisArtist.equals(artistName)) {

                    ArtistAlbums.add(new Albums(thisAlbumTitle, thisAlbumArt, thisArtist, thisAlbumId));

                    }

            } while (AlbumArtistCursot.moveToNext());

            // For best practices, close the cursor after use.
        }AlbumArtistCursot.close();
    }

}
