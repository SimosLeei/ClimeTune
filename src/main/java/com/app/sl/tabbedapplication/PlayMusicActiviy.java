package com.app.sl.tabbedapplication;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.Random;


//TODO: GETSONGINFO METHOD --> Done
//TODO: FIX PAUSE-PLAY BUTTON BUG (WHILE PRESSING NEXT OR PREV) -->Done
//TODO: FIX SEEKBAR ONTOUCH EVENT


public class PlayMusicActiviy extends MainActivity {

    private static String ACTION = "com.app.sl.tabbedapplication.action.PLAY";

    private Intent PlayMusic;
    private PlayService playSrv;
    private PlayService.MusicBinder binder;
    private boolean musicBound = false;

    public static  ArrayList<? extends Song> songList;
    public boolean isShuffle = false;
    public boolean isNextPrevButton = false;
    public boolean isRepeat = false;
    Random randomgenerator;


    private String actionis;

    Song song;
    int songPosition;

    private TextView songTotalDurationLabel;
    private TextView album_info;
    private TextView artist_info;
    private TextView title_info;
    private ImageView cover_info;
    private TextView songCurrentDurationLabel;
    private SeekBar songProgressBar;


    private ImageView next;
    private ImageView prev;
    private ImageView shuffle;
    private ImageView repeat;
    private ImageView pausePlay;


    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_activity);


        Toolbar myToolbar = (Toolbar) findViewById(R.id.pmtoolbar);
        setSupportActionBar(myToolbar);


        TextView appTitle = findViewById(R.id.apptitle);
        Typeface tf = ResourcesCompat.getFont(getApplicationContext(), R.font.liquidrom);
        appTitle.setTypeface(tf);

        /** Image Buttons **/
        pausePlay = findViewById(R.id.pause_play);
        next = findViewById(R.id.forward);
        prev = findViewById(R.id.prev);
        shuffle = findViewById(R.id.shuffle);
        repeat = findViewById(R.id.repeat);


        Intent UIinfo = getIntent();
        actionis = UIinfo.getAction();

        if(actionis=="com.app.sl.tabbedapplication.action.RESTART"){
            Log.d("Place","Action Restart");
            songPosition = UIinfo.getIntExtra("PosSong",0);
            if(playSrv.mediaPlayer.isPlaying()) {
                pausePlay.setImageResource(R.drawable.pausebutton);
            }else{
                pausePlay.setImageResource(R.drawable.playbutton);
            }
        }else {
            songPosition = UIinfo.getIntExtra("pos", 0);
        }
            songList = UIinfo.getParcelableArrayListExtra("SongList");




            /** SeekBar **/
            songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
            songProgressBar = (SeekBar) findViewById(R.id.Durationseekbar);


            /** Song Views **/
            cover_info = findViewById(R.id.TrackImage);

            album_info = findViewById(R.id.album_info);
            artist_info = findViewById(R.id.artist_info);
            title_info = findViewById(R.id.title_info);
            songTotalDurationLabel = findViewById(R.id.songTotalDurationLabel);

            final Animation animAlpha = AnimationUtils.loadAnimation(this, R.anim.anim_alpha);

            /** End of Song Views**/


            /** Click Listeners for Pause,play etc **/


            pausePlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayPause();
                    v.startAnimation(animAlpha);

                }
            });


            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playNext();
                }
            });

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playPrev();
                }
            });

            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playShuffle();
                }
            });

            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playRepeat();
                }
            });
            /** End of Action Buttons **/

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Place", "OnStart");
            if (PlayMusic == null) {
                Intent PlayMusic = new Intent(getApplicationContext(), PlayService.class);
                bindService(PlayMusic, musicConnection, Context.BIND_AUTO_CREATE);
                startService(PlayMusic);
            }
        }

    @Override
    public void onPause(){
        super.onPause();
        Log.d("Place","OnPause");
//        SharedPreferences Prefs = getPreferences(Activity.MODE_PRIVATE);
//        SharedPreferences.Editor editor = Prefs.edit();
//        editor.putInt("songPos",songPosition);
//        editor.commit();

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Place","OnResume");


        Log.d("songBound", String.valueOf(musicBound));

    }

    @Override
    public void onStop() {
        super.onStop();
        if (musicBound) {
            Log.d("Place", "OnStop");
            ACTION = "com.app.sl.tabbedapplication.action.PLAYNEW";
            Log.d("Songbound", String.valueOf(musicBound));
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (musicBound) {
            Log.d("Place","OnDestroy");
            ACTION = "com.app.sl.tabbedapplication.action.PLAYNEW";
            Log.d("Songbound", String.valueOf(musicBound));
        }
    }



    /**
     * Connectivity with service
     **/


    private ServiceConnection musicConnection = new ServiceConnection() {

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (PlayService.MusicBinder) service;
            //get service
            playSrv = binder.getService();
            //pass SongList - Action - View Info
            if(actionis == "com.app.sl.tabbedapplication.action.RESTART"){
                ACTION="com.app.sl.tabbedapplication.action.RESTART";
                playSrv.setAction(ACTION);
            }else{
                if (musicBound) {
                playSrv.setAction(ACTION);
                }
                if (!musicBound) {
                playSrv.setAction(ACTION);
                }
            }

            playSrv.setSongCurrentDurationLabel(songCurrentDurationLabel);
            playSrv.setSeekbar(songProgressBar);
            playSrv.setView(title_info, album_info, artist_info, songTotalDurationLabel, cover_info);
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
            playSrv.giveList(songList);
            playSrv.setSongPosition(songPosition);

            musicBound = true;

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    /**
     * End of Connectivity Code
     **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Button Methods
     **/

    public void PlayPause() {
        if (playSrv.mediaPlayer.isPlaying()) {
            playSrv.mediaPlayer.pause();

            pausePlay.setImageResource(R.drawable.playbutton);
        } else {
            if (playSrv.mediaPlayer != null) {
                playSrv.mediaPlayer.start();
                pausePlay.setImageResource(R.drawable.pausebutton);

            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playNext() { //TODO SHUFFLE AND NEXT -->Done
        if (playSrv.mediaPlayer != null && !isShuffle && songPosition < songList.size() - 1) {
            songPosition++;
            isNextPrevButton = true;
            pausePlay.setImageResource(R.drawable.pausebutton);
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
            playSrv.setSongPosition(songPosition);
            isNextPrevButton = false;
        }
        if (playSrv.mediaPlayer != null && isShuffle && songPosition < songList.size()) {
            randomgenerator = new Random();
            isNextPrevButton = true;
            pausePlay.setImageResource(R.drawable.pausebutton);
            songPosition = randomgenerator.nextInt(songList.size());
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
            playSrv.setSongPosition(songPosition);
            isNextPrevButton = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playPrev() {
        if (playSrv.mediaPlayer != null && songPosition != 0) {
            songPosition--;
            isNextPrevButton = true;
            pausePlay.setImageResource(R.drawable.pausebutton);
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
            playSrv.setSongPosition(songPosition);
            isNextPrevButton = false;
        }
        if (playSrv.mediaPlayer != null && isShuffle && songPosition != 0) {
            randomgenerator = new Random();
            isNextPrevButton = true;
            pausePlay.setImageResource(R.drawable.pausebutton);
            songPosition = randomgenerator.nextInt(songList.size());
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
            playSrv.setSongPosition(songPosition);
            isNextPrevButton = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playShuffle() {
        if (!isShuffle) {
            isShuffle = true;
            shuffle.setImageResource(R.drawable.exchange);
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
        } else {
            shuffle.setImageResource(R.drawable.shuffle);
            isShuffle = false;
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void playRepeat() {
        if (!isRepeat) {
            isRepeat = true;
            repeat.setImageResource(R.drawable.repeaton);
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);
        } else {
            repeat.setImageResource(R.drawable.replay);
            isRepeat = false;
            playSrv.setBoolean(isRepeat, isShuffle, isNextPrevButton);

        }
    }

    /**
     * End of Methods
     **/


}





