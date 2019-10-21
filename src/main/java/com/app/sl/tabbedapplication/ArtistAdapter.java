package com.app.sl.tabbedapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class ArtistAdapter extends BaseAdapter {


    private ArrayList<Artist> artistList;
    private LayoutInflater artistinf;
    Typeface tf ;

    public ArtistAdapter(ArrayList<Artist> theArtists, Context context) {

        artistList = theArtists;
        artistinf = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return artistList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView artistname;
        TextView noAlbums;
        TextView noTracks;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        final ViewHolder artistDisplayHolder;
        View ArtistLay= convertView;

        Log.d("ViewPos", String.valueOf(position)+convertView);
        if(ArtistLay==null) {

            ArtistLay = (LinearLayout) artistinf.inflate
                    (R.layout.artist, parent, false);

            artistDisplayHolder = new ViewHolder();
            artistDisplayHolder.artistname = (TextView) ArtistLay.findViewById(R.id.ArtistName);
            artistDisplayHolder.noAlbums = (TextView) ArtistLay.findViewById(R.id.AlbumsOfArtist);
            artistDisplayHolder.noTracks = (TextView) ArtistLay.findViewById(R.id.NoTracks);

            ArtistLay.setTag(artistDisplayHolder);

        }
        else{
            artistDisplayHolder = (ArtistAdapter.ViewHolder) ArtistLay.getTag();
        }
        Artist currArtist = artistList.get(position);


        artistDisplayHolder.artistname.setText(currArtist.getNameofArtist());
        tf= ResourcesCompat.getFont((artistDisplayHolder.artistname).getContext(), R.font.liquidrom);
        artistDisplayHolder.artistname.setTypeface(tf);

       artistDisplayHolder.noAlbums.setText( "Albums: " + currArtist.getNoAlbums());
       artistDisplayHolder.noAlbums.setTypeface(tf);

       artistDisplayHolder.noTracks.setText("Songs: " + currArtist.getNoTracks());
       artistDisplayHolder.noTracks.setTypeface(tf);


        return ArtistLay;
    }
}
