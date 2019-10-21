package com.app.sl.tabbedapplication;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Build.VERSION.SDK_INT;


public class MainActivity extends AppCompatActivity  {


    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private TextView playingNow;
    private ImageView footerButton;
    View footerView;


    private Song playingNowSong;
    private PlayMusicActiviy pma;
    private Typeface tf;


    private boolean isON = false;
    String currDur;
    int songposition;
    public PlayService playsrv;
    public String Title = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState!=null){
            Title = savedInstanceState.getString("SongPlaying");
            playingNow.setText(Title);
        }
        setContentView(R.layout.activity_main);

        SharedPreferences Prefs = getPreferences(Activity.MODE_PRIVATE);
        songposition = Prefs.getInt("songPos",0);
        Title = Prefs.getString("songTitle",null);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView appTitle = findViewById(R.id.apptitle);
        tf = ResourcesCompat.getFont(getApplicationContext(),R.font.liquidrom);
        appTitle.setTypeface(tf);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

       footerView = findViewById(R.id.footer);

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        playingNow = (TextView) findViewById(R.id.nowplaying);
        footerButton = findViewById(R.id.Pauseplayfooter);
        footerButton.setImageResource(R.drawable.playbuttonfooter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);





        if(Title!=null && pma.songList != null) {
            Log.d("MainActivityonCreate", "Title braod pass");
            playingNow.setText(Title);
            playingNow.setTextSize(16);
            playingNow.setTypeface(tf);
            if (playsrv.mediaPlayer.isPlaying()){
                footerButton.setImageResource(R.drawable.pausebuttonfooter);
            }else if(isON){
                footerButton.setImageResource(R.drawable.playbuttonfooter);
            }

        }else{
            Log.d("MainActivityonCreate", "title broad doesxtn pas");
            footerButton.setImageResource(R.drawable.playbuttonfooter);
        }

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                mSongReceiver, new IntentFilter("passSong"));

        footerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isON) {
                    if (playsrv.mediaPlayer.isPlaying()) {
                        playsrv.mediaPlayer.pause();
                        footerButton.setImageResource(R.drawable.playbuttonfooter);
                    } else {
                        playsrv.mediaPlayer.start();
                        footerButton.setImageResource(R.drawable.pausebuttonfooter);
                    }
                }
            }
        });

    }


    private boolean checkAndRequestPermissions() {
        if (SDK_INT >= Build.VERSION_CODES.M) {
            int permissionReadPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE);
            int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            List<String> listPermissionsNeeded = new ArrayList<>();

            if (permissionReadPhoneState != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
            }

            if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }

            if (!listPermissionsNeeded.isEmpty()) {
                ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        String TAG = "LOG_PERMISSION";
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions

                    if (perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            ) {
                        Log.d(TAG, "Phone state and storage permissions granted");
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                      //shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
                            showDialogOK("Phone state and storage permissions required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            Toast.makeText(this, "Go to settings and enable permissions", Toast.LENGTH_LONG)
                                    .show();
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }


    private BroadcastReceiver mSongReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            playingNowSong = (Song) intent.getSerializableExtra("song");
            songposition = intent.getIntExtra("songPosition",0);
            currDur = String.valueOf(intent.getIntExtra("currDuration",0));
            Title = playingNowSong.getTitle();
            isON = true;

            playingNow.setText(Title);
            playingNow.setTextSize(16);
            playingNow.setTypeface(tf);


        }
    };


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


    public void onClick(View view) {


        if(pma.songList != null) {
            Intent resumeActivity = new Intent(getApplicationContext(), PlayMusicActiviy.class);
            resumeActivity.setAction("com.app.sl.tabbedapplication.action.RESTART");
            resumeActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            resumeActivity.putExtra("SongList", pma.songList);
            resumeActivity.putExtra("PosSong",songposition);
            startActivityIfNeeded(resumeActivity, 0);
        }else{
            Toast.makeText(this,"No song playing",Toast.LENGTH_LONG).show();
        }


    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    TracklistTab tracklistTab = new TracklistTab();
                    return tracklistTab;
                case 1:
                    AlbumTab albumTab = new AlbumTab();
                    return albumTab;

                case 2:
                    ArtistTab artistTab = new ArtistTab();
                    return artistTab;

                case 3:
                    YouTubeFragment youTubeFragment = new YouTubeFragment();
                    return youTubeFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

    }


    public void onResume(){
        super.onResume();

        Log.d("MainActivityonCreate", "resume");

        if (isON && playsrv.mediaPlayer.isPlaying()){
            footerButton.setImageResource(R.drawable.pausebuttonfooter);
        }else if(isON){
            footerButton.setImageResource(R.drawable.playbuttonfooter);
        }

    }

    public void onPause(){
        super.onPause();
        Log.d("MainActivityonCreate", "pause");

        if(Title!=null) {
            SharedPreferences Prefs = getPreferences(Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = Prefs.edit();
        editor.putInt("songPos",songposition);
        editor.putString("songTitle",Title);
        editor.commit();
        }
    }

    public void onStop(){
        super.onStop();
        Log.d("MainActivityonCreate", "stop");
    }

    public void onDestroy(){
        super.onDestroy();
        Log.d("MainActivityonCreate", "Destroy");
    }

    public void onSaveInstanceState(Bundle outState){
        outState.putString("SongPlaying",Title);
        Log.d("MainActivityonCreate", "save instance state");
        super.onSaveInstanceState(outState);

    }

    public void onRestoreInstanceState(Bundle savedInstanceState){
        Log.d("MainActivityonCreate", "Restore state");
        playingNow.setText(savedInstanceState.getString("SongPlaying"));
    }


}

