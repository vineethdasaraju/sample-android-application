package com.vineeth.sampleweatherapplication.background;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;

import com.vineeth.sampleweatherapplication.R;
import com.vineeth.sampleweatherapplication.WeatherActivity;
import com.vineeth.sampleweatherapplication.util.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetDataAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    Activity app;
    double longitude, latitude;

    public GetDataAsyncTask(Activity app, double longitude, double latitude) {
        this.app = app;
        this.longitude = longitude;
        this.latitude = latitude;
    }


    @Override
    protected JSONObject doInBackground(Void... params) {
        try {
            String getWeatherByCoordinates = "http://api.openweathermap.org/data/2.5/weather?lat="+latitude+"&lon="+longitude+"&units=metric";
            URL url = new URL(getWeatherByCoordinates);
            HttpURLConnection connection =
                    (HttpURLConnection)url.openConnection();

            connection.addRequestProperty("x-api-key",
                    app.getString(R.string.open_weather_maps_app_id));

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));

            StringBuffer json = new StringBuffer(1024);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();

            JSONObject data = new JSONObject(json.toString());

            if(data.getInt("cod") != 200){
                return null;
            }
            return data;
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {

        Constants.weatherData = jsonObject;
        Handler handler=new Handler();
        Runnable r=new Runnable() {
            public void run() {
                Intent intent = new Intent(app, WeatherActivity.class);
                app.startActivity(intent);
                app.overridePendingTransition  (R.anim.right_slide_in, R.anim.left_slide_out);
                app.finish();
            }
        };
        handler.postDelayed(r, 4000);
    }
}
