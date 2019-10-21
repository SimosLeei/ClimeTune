package com.app.sl.tabbedapplication;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.res.ResourcesCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class YouTubeFragment extends Fragment{


    boolean fragment_added = false;

    JSONObject data = null;
    private YouTubePlayer Player;
    private static final String YoutubeDeveloperKey = String.valueOf(R.string.youtube_key);
    final YouTubePlayerSupportFragment youTubePlayerFragment = YouTubePlayerSupportFragment.newInstance();

    public Typeface tf;
    private TextView CurrCityText;
    private String city = null;
    private boolean canfoundlocation = false;


    private TextView Location;
    private TextView Weather;
    private long currTime;



    private ImageView CurrWeather;
    String CurrentWeather;

    Double tobeConvert;

    private double Longtitude = 0;
    private double Latitude = 0;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.weather_tab, container, false);

        tf = ResourcesCompat.getFont(getActivity(), R.font.liquidrom);

        Location = view.findViewById(R.id.WeatherLocReadyText);
        Location.setText("Current Location:");
        Location.setTypeface(tf);

        Weather = view.findViewById(R.id.WeatherReadyText);
        Weather.setText("Weather:");
        Weather.setTypeface(tf);

        CurrCityText = view.findViewById(R.id.CurrLocation);
        CurrWeather = view.findViewById(R.id.weatherImage);

        if (!canfoundlocation) {
            CurrCityText.setText("Click on icon to get info");
            CurrCityText.setTextSize(15);
            CurrCityText.setTypeface(tf);
            CurrWeather.setImageResource(R.drawable.clickhere);

        } else {

            CurrCityText.setText(city);
            CurrCityText.setTypeface(tf);
            renderWeather(data);
            youTubePlayerFragment.initialize(String.valueOf(YoutubeDeveloperKey), new YouTubePlayer.OnInitializedListener() {

                @Override
                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                    if (!wasRestored) {
                        fragment_added = true;
                        Player = player;
                        Player.setShowFullscreenButton(false);
                        getPlaylist(Player);
                    }
                }
                @Override
                public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {
                    // TODO Auto-generated method stub

                }
            });
            view.setBackground(getResources().getDrawable(R.drawable.youtube_background));
        }

        CurrWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Log.d("Placeis", "OnClick");
                getLocation();
                getJSON(Latitude,Longtitude);

                final Handler handler = new Handler();
                Toast.makeText(getContext(),"Please Wait..", Toast.LENGTH_SHORT).show();
                handler.postDelayed(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        if (data != null) {
                            renderWeather(data);
                                    if(!fragment_added) {
                                        view.setBackground(getResources().getDrawable(R.drawable.youtube_background));
                                        youTubePlayerFragment.initialize(String.valueOf(YoutubeDeveloperKey), new YouTubePlayer.OnInitializedListener() {

                                            @Override
                                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player, boolean wasRestored) {

                                                if (!wasRestored) {
                                                    canfoundlocation = true;
                                                    fragment_added = true;
                                                    Player = player;
                                                    Player.setShowFullscreenButton(false);
                                                    getPlaylist(Player);

                                                }

                                            }
                                            @Override
                                            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {


                                            }
                                        });
                                        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
                                        transaction.add(R.id.youtube_fragment, youTubePlayerFragment).commit();
                                }

                        }else{
                            Toast.makeText(getContext(),"Can't found location",Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 3000);

            }
        });


        return view;
    }


    public void getJSON(final double lat,final double lon) {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon=" +lon+"&units=metric"+
                            "&APPID=cea81f0fd6d5074bfc05d32898e32e1b");

                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    StringBuffer json = new StringBuffer(1024);
                    String tmp = "";

                    while ((tmp = reader.readLine()) != null)
                        json.append(tmp).append("\n");
                    reader.close();

                    data = new JSONObject(json.toString());

                    if (data.getInt("cod") != 200) {
                        System.out.println("Cancelled");
                        return null;
                    }


                } catch (Exception e) {

                    System.out.println("Exception " + e.getMessage());
                    return null;
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void Void) {
                if (data != null) {
                    Log.d("Placeis", data.toString());
                }

            }
        }.execute();

    }

    private void renderWeather(JSONObject jsonObject) {
        try {

            JSONObject main = jsonObject.getJSONObject("main");
             tobeConvert = main.getDouble("temp");
            int temperature  = tobeConvert.intValue();
            String celcius = " ℃ ";
            CurrCityText.setText(jsonObject.getString("name").toUpperCase()+ " " +temperature +celcius);
            CurrCityText.setTextSize(28);


            long Sunset = jsonObject.getJSONObject("sys").getLong("sunset")*1000;
            long Sunrise = jsonObject.getJSONObject("sys").getLong("sunrise")*1000;

            currTime = new Date().getTime();

            JSONObject weatherCondition = jsonObject.getJSONArray("weather").getJSONObject(0);
             CurrentWeather = weatherCondition.getString("id");
            Log.d("Weatheris", String.valueOf(CurrentWeather));


            if (CurrentWeather.equals("800")) { // Clear
                if(currTime>=Sunrise && currTime<Sunset)
                CurrWeather.setImageResource(R.drawable.sun);
                else{
                    CurrWeather.setImageResource(R.drawable.nightclear);
                }
            }else if(CurrentWeather.startsWith("801")){ //Clouds
                if(currTime>=Sunrise && currTime<Sunset)
                    CurrWeather.setImageResource(R.drawable.fewclouds);
                else{
                    CurrWeather.setImageResource(R.drawable.nightclouds);
                }
            }else if(CurrentWeather.startsWith("8")){ //Clouds
            CurrWeather.setImageResource(R.drawable.clouds);
            }else if(CurrentWeather.startsWith("6")){ //Snow
                CurrWeather.setImageResource(R.drawable.snow);

            }else if(CurrentWeather.startsWith("5")){ // rain
                CurrWeather.setImageResource(R.drawable.rain);

            }else if(CurrentWeather.startsWith("3")){ //drizzle
                CurrWeather.setImageResource(R.drawable.drizzle);

            }else if(CurrentWeather.startsWith("2")){ //ThunderStorm
                CurrWeather.setImageResource(R.drawable.thunderstorm);

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getPlaylist(YouTubePlayer YPlayer){


        /** Sun **/
        if (CurrentWeather.equals("800") && tobeConvert>=35 && tobeConvert<40) {

            YPlayer.loadPlaylist("PLSRDGXudTSm-j3G2qLXqtHXh0jEmnHzu1");

        }else if(CurrentWeather.equals("800") && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.cuePlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.equals("800") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.cuePlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.equals("800") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.equals("800") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("800") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("800") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("800") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");


            /** Few Clouds **/

        }else if(CurrentWeather.equals("801") && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.equals("801") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.equals("801") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.equals("801") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("801") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("801") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.equals("801") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

            /** Clouds **/

        }else if(CurrentWeather.startsWith("8")  && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("8") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");


            /** Snow **/

        }else if(CurrentWeather.startsWith("6") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("6") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("6") && tobeConvert>=-15 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("6") && tobeConvert>=-20 && tobeConvert<-15) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("6") && tobeConvert>=-25 && tobeConvert<-20) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");
        }

        /**Rain*/

        else if(CurrentWeather.startsWith("5")  && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("5") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

            /** ThunderStorm */
        } else if(CurrentWeather.startsWith("3")  && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.loadPlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("3") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.loadPlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");


            /** Drizzle */
        } else if(CurrentWeather.startsWith("2")  && tobeConvert>=30 && tobeConvert<35) {

            YPlayer.cuePlaylist("PLSRDGXudTSm9dl38omfSkS5o6P_Gjc23g");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=25 && tobeConvert<30) {

            YPlayer.cuePlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=20 && tobeConvert<25) {

            YPlayer.cuePlaylist("PLSRDGXudTSm9K2iL1nS6pYRR_ofNLoSnX");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=10 && tobeConvert<20) {

            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=0 && tobeConvert<10) {
            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=-10 && tobeConvert<0) {
            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");

        }else if(CurrentWeather.startsWith("2") && tobeConvert>=-20 && tobeConvert<-10) {
            YPlayer.cuePlaylist("PLSRDGXudTSm_kUZ8vN4F5OchOU_WcDTkd");
        }


    }

    public void getLocation() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);


        Log.d("Placeis", "getLocation");
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location loc  = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if(loc!=null) {
            Longtitude = loc.getLongitude();
            Latitude = loc.getLatitude();
        }
        Log.d("Placeis","Lat " +Latitude + ""+"Lon "+Longtitude);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 600000, 100000, new LocationListener() {
            @Override
            public void onLocationChanged(android.location.Location location) {
                Longtitude = location.getLongitude();
                Latitude = location.getLatitude();

                Log.d("Placeis","Lat " +Latitude + ""+"Lon "+Longtitude);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {


            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public void onDestroy(){
        super.onDestroy();


    }

}



