package com.app.sl.tabbedapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.Image;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlbumAdapter extends BaseAdapter {

    private ArrayList<Albums> albumList;
    private LayoutInflater albuminf;
    Typeface tf ;

    public AlbumAdapter(ArrayList<Albums> theAlbums, Context context) {

         albumList = theAlbums;
         albuminf = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return albumList.size();
    }

    @Override
    public Object getItem(int arg0) {
    return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

   static class ViewHolder{
        ImageView coverView;
        TextView albumTitle;


   }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder albumDisplayHolder;
        View AlbumLay = convertView;

        Log.d("ViewPos", String.valueOf(position)+convertView);
        if(AlbumLay==null) {

            AlbumLay = (RelativeLayout) albuminf.inflate
                    (R.layout.album, parent, false);

            albumDisplayHolder = new ViewHolder();
            albumDisplayHolder.coverView = (ImageView) AlbumLay.findViewById(R.id.image);
            albumDisplayHolder.albumTitle = (TextView) AlbumLay.findViewById(R.id.Album_Name);

            AlbumLay.setTag(albumDisplayHolder);

        }
        else{
                albumDisplayHolder = (ViewHolder) AlbumLay.getTag();
        }
        Albums currAlbum = albumList.get(position);
        final String AlbumArt =  currAlbum.getAlbumArt();

        albumDisplayHolder.albumTitle.setText(currAlbum.getAlbumName());
        tf=ResourcesCompat.getFont((albumDisplayHolder.albumTitle).getContext(), R.font.liquidrom);
        albumDisplayHolder.albumTitle.setTypeface(tf);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("AlumbArtCreation","albumart Create");
                albumDisplayHolder.coverView.post(new Runnable() {
                    @Override
                    public void run() {
                        if(AlbumArt!=null) {
                            Log.d("AlumbArtCreation","albumart Created");
                            Bitmap bm = BitmapFactory.decodeFile(AlbumArt);
                            albumDisplayHolder.coverView.setImageBitmap(bm);
                        }else {
                            albumDisplayHolder.coverView.setImageResource(R.drawable.generic);
                        }
                    }
                });
            }
        }).start();


        return AlbumLay;
    }
}




