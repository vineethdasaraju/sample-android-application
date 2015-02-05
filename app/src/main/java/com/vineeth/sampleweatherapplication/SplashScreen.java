package com.vineeth.sampleweatherapplication;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.vineeth.sampleweatherapplication.background.GetDataAsyncTask;
import com.vineeth.sampleweatherapplication.background.GetDataCustomAsyncTask;
import com.vineeth.sampleweatherapplication.util.CityPreference;
import com.vineeth.sampleweatherapplication.util.Constants;


public class SplashScreen extends ActionBarActivity {

    TextView title;
    ImageView splashScreenImage;
    Handler handler;
    Activity app = this;
    String city = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        title = (TextView) findViewById(R.id.splash_screen_title);
        splashScreenImage = (ImageView) findViewById(R.id.splash_screen_image);

        splashScreenImage.setVisibility(View.VISIBLE);
        YoYo.with(Techniques.FadeIn).duration(2000).playOn(splashScreenImage);

        handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                title.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.DropOut).duration(1000).playOn(title);
            }
        };
        handler.postDelayed(r, 1000);
        city = new CityPreference(app).getCity();

        if(city.equals("default")){
            updateBasedOnLocation();
        } else {
            GetDataCustomAsyncTask task = new GetDataCustomAsyncTask(app, city);
            task.execute();
        }

    }

    private Location getLastKnownLocation() {

        LocationManager locationManager = (LocationManager) app
                .getSystemService(Context.LOCATION_SERVICE);

        String locationProvider = LocationManager.NETWORK_PROVIDER;
        // Or use LocationManager.GPS_PROVIDER

        Location lastKnownLocation = locationManager
                .getLastKnownLocation(locationProvider);

        return lastKnownLocation;
    }

    private void updateBasedOnLocation(){
        Location lastKnownAddress = getLastKnownLocation();

        double longitude = lastKnownAddress.getLongitude();
        double latitude = lastKnownAddress.getLatitude();

        Constants.locationLatitude = latitude;
        Constants.locationLongitude = longitude;

        GetDataAsyncTask task = new GetDataAsyncTask(app, longitude, latitude);
        task.execute();
    }
}
