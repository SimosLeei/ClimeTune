package com.app.sl.tabbedapplication;


import android.annotation.TargetApi;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import static android.media.AudioManager.AUDIOFOCUS_GAIN;
import static android.media.AudioManager.AUDIOFOCUS_LOSS;
import static android.media.AudioManager.MODE_IN_CALL;
import static android.media.AudioManager.MODE_NORMAL;

public class PlayService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener , AudioManager.OnAudioFocusChangeListener {



    private final static String ACTION = "com.app.sl.tabbedapplication.action.PLAY";
    private final static String PLAYBACKACTION = "com.app.sl.tabbedapplication.action.PLAYNEW";
    private final static String REOPEN = "com.app.sl.tabbedapplication.action.RESTART";

    private Random randomgenerator;
    private Handler mHandler = new Handler();
    private final IBinder musicBind = new MusicBinder();
    Typeface tf;

    Song song;
    Albums album;

    AudioManager mAudioManager;

    private ContentResolver ArtResolve;
    private ArrayList<AlbumArt> AlbumArtList;

    private String action;
    public int positionOfSong;
    private ArrayList<Song> SongList;



    boolean isShuffle = false;
    boolean isRepeat= false;
    boolean isNextPrevButton = false;

    private SeekBar ServiceSongProgressBar;
    private TextView ServiceSongCurrentDurationLabel;
    private Utilities utils = new Utilities();

    public TextView title_display;
    public TextView album_display;
    public TextView artist_display;
    private TextView songTotalDurationLabel;
    private ImageView cover_display;



    String SongTitle;
    String SongArtist;
    String SongAlbum;
    String SongDuration;

     public static MediaPlayer mediaPlayer ;

    int progress;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent){
        return super.onUnbind(intent);
    }

    @Override
    public void onAudioFocusChange(int i) {

        if(i<=0) {
           mediaPlayer.pause();
        } else {
         mediaPlayer.start();
    }

    }

    public class MusicBinder extends Binder {
        PlayService getService() {
            return PlayService.this;
        }
    }

    @TargetApi(Build.VERSION_CODES.N)
    public void onCreate(){
        super.onCreate();

        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        mediaPlayer = new MediaPlayer();
            initMusicPlayer();
            getAlbumArt();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
        }
        mAudioManager.abandonAudioFocus(this);
    }

    public void initMusicPlayer(){
        if(mediaPlayer==null) {
            mediaPlayer = new MediaPlayer();
        }

        mediaPlayer.reset();
        mediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);

    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        updateProgressBar();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCompletion(MediaPlayer mp) {
        //mediaPlayer.getCurrentPosition();
        mp.stop();
        mediaPlayer.reset();
        if(isRepeat){
            songDisplay(positionOfSong);
        }
        if(isShuffle){
            randomgenerator = new Random();
            positionOfSong = randomgenerator.nextInt(SongList.size());
            songDisplay(positionOfSong);
        }
        if(!isShuffle && !isRepeat && positionOfSong<SongList.size()-1){
            positionOfSong++;
            songDisplay(positionOfSong);
        }

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    /** Pass objects from PlayMusicActivity **/

    public void setAction(String PlayAction){
        action = PlayAction;
    }

    public void setView(TextView title , TextView album , TextView artist , TextView duration,ImageView cover){
        title_display = title;
        album_display= album;
        artist_display = artist;
        songTotalDurationLabel = duration;
        cover_display = cover;


    }

    public void setBoolean(boolean onRepeat,boolean onShuffle, boolean onNextPrev){
        isRepeat = onRepeat;
        isShuffle = onShuffle;
        isNextPrevButton =  onNextPrev;
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setSongPosition(int pos){

        positionOfSong = pos;
        if(isNextPrevButton){

            songDisplay(positionOfSong);
        }
        if(!isRepeat && !isShuffle) {
            songDisplay(positionOfSong);
        }


    }

    public void setSeekbar(SeekBar seekbar){
        ServiceSongProgressBar = seekbar;
    }

    public void setSongCurrentDurationLabel(TextView currDuration){
        ServiceSongCurrentDurationLabel = currDuration;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void giveList(ArrayList songlist){
        SongList = songlist;
    }
    /** End of Passing Methods **/


    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void songDisplay(int Position) {



        long SongId = SongList.get(Position).getId();
        SongTitle = SongList.get(Position).getTitle();
        SongArtist = SongList.get(Position).getArtist();
        SongAlbum = SongList.get(Position).getAlbum();
        SongDuration = SongList.get(Position).getDuration();
        int SongAlbumId = SongList.get(Position).getAlbumId();




         tf = ResourcesCompat.getFont(getApplicationContext(), R.font.liquidrom);
        album_display.setText(SongAlbum);
        album_display.setTypeface(tf);
        title_display.setText(SongTitle);
        title_display.setTypeface(tf);
        songTotalDurationLabel.setText(SongDuration);
        songTotalDurationLabel.setTypeface(tf);
        artist_display.setText(SongArtist);
        artist_display.setTypeface(tf);



        song = new Song(SongId, SongTitle, SongArtist, SongAlbum, SongDuration, SongAlbumId);

        findAlbumArt(SongAlbumId);
        playSong(song);



        Log.d("theNextsong", SongTitle);

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playSong(Song song){

        long songID = song.getId();
        Log.d("theTitleis:",song.getTitle());

        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,songID);

        if (ACTION == action) {
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(),trackUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync();
            ServiceSongProgressBar.setProgress(0);
            ServiceSongProgressBar.setMax(100);
        }
        if(PLAYBACKACTION==action){
            mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;

            initMusicPlayer();

            try {
                mediaPlayer.setDataSource(getApplicationContext(),trackUri);

            } catch (IOException e) {
                e.printStackTrace();
            }

            mediaPlayer.prepareAsync();
            ServiceSongProgressBar.setProgress(0);
            ServiceSongProgressBar.setMax(100);

            }
            if(REOPEN==action){
            Log.d("Place","ACTION = REOPEN");

            action=PLAYBACKACTION;

            }
        sendMessageToActivity(song);

        ServiceSongProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // remove message Handler from updating progress bar
                mHandler.removeCallbacks(mUpdateTimeTask);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                mHandler.removeCallbacks(mUpdateTimeTask);
                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = utils.progressToTimer(seekBar.getProgress(), totalDuration);

                // forward or backward to certain seconds
                mediaPlayer.seekTo(currentPosition);

                // update timer progress again
                updateProgressBar();
            }
        });
        }

    private void sendMessageToActivity(Song playingnow) {
        Intent intent = new Intent("passSong");
        intent.putExtra("song", playingnow);
        intent.putExtra("songPosition",positionOfSong);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void  findAlbumArt(int Album_id) {


                for(AlbumArt art:AlbumArtList) {
                    if (art.getAlbumArtId() == Album_id) {
                        final String RawAlbumArt = art.getAlbumArt();
                        Log.d("Hello", String.valueOf(art));

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                cover_display.post(new Runnable() {
                                    @Override
                                    public void run() {

                                        if (RawAlbumArt != null) {
                                            Bitmap artwork = BitmapFactory.decodeFile(RawAlbumArt);
                                            cover_display.setImageBitmap(artwork);
                                        } else {
                                            cover_display.setImageResource(R.drawable.generic);
                                        }
                                    }
                                });
                            }
                        }).start();
                    }
                }
    }


    public void getAlbumArt() {
        ArtResolve = getApplicationContext().getContentResolver();
        Uri albumArtUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI;
        Cursor AlbumCursor = ArtResolve.query(albumArtUri, null, null, null, null);
        AlbumArtList = new ArrayList<>();


        if (AlbumCursor != null && AlbumCursor.moveToFirst()) {

            int AlbumArtColumn = AlbumCursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART);
            int AlbumId = AlbumCursor.getColumnIndex(MediaStore.Audio.Albums._ID);

            do {

                String thisAlbumArt = AlbumCursor.getString(AlbumArtColumn);
                int thisAlbumId = AlbumCursor.getInt(AlbumId);

                    AlbumArtList.add(new AlbumArt(thisAlbumArt, thisAlbumId));
                    Log.d("GetAlbumArtID", String.valueOf(thisAlbumId));

            } while (AlbumCursor.moveToNext());
        }
        AlbumCursor.close();
    }





    /** Update SeekBar progress Code**/

    public void updateProgressBar(){
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }
    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            if(mediaPlayer.isPlaying()) {
                long totalDuration = mediaPlayer.getDuration();
                long currentDuration = mediaPlayer.getCurrentPosition();

                ServiceSongCurrentDurationLabel.setText("" + utils.milliSecondsToTimer(currentDuration));
                tf = ResourcesCompat.getFont(getApplicationContext(), R.font.liquidrom);
                ServiceSongCurrentDurationLabel.setTypeface(tf);

                // Updating progress bar
                progress = (int) (utils.getProgressPercentage(currentDuration, totalDuration));
                ServiceSongProgressBar.setProgress(progress);
            }
            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    /** End of SeekBar code**/



    private void stopMedia() {
        if (mediaPlayer == null) return;
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
    }

}
