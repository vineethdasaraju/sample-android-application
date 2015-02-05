package com.vineeth.sampleweatherapplication.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.vineeth.sampleweatherapplication.R;
import com.vineeth.sampleweatherapplication.background.GetDataAsyncTask;
import com.vineeth.sampleweatherapplication.background.GetDataByCityAsyncTask;
import com.vineeth.sampleweatherapplication.background.GetDataByCityCustomAsyncTask;
import com.vineeth.sampleweatherapplication.util.CityPreference;
import com.vineeth.sampleweatherapplication.util.Constants;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class WeatherFragment extends Fragment {
    Typeface weatherFont;

    String currentCity="";
    TextView cityField;
    TextView updatedField;
    TextView detailsField;
    TextView currentTemperatureField;
    TextView weatherIcon;
    SwipeRefreshLayout swipeLayout;

    Handler handler;

    public WeatherFragment(){
        handler = new Handler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        cityField = (TextView)rootView.findViewById(R.id.city_field);
        updatedField = (TextView)rootView.findViewById(R.id.updated_field);
        detailsField = (TextView)rootView.findViewById(R.id.details_field);
        currentTemperatureField = (TextView)rootView.findViewById(R.id.current_temperature_field);
        weatherIcon = (TextView)rootView.findViewById(R.id.weather_icon);

        weatherIcon.setTypeface(weatherFont);
        if(Constants.weatherData!=null){
            renderWeather(Constants.weatherData);
        } else {
            Toast.makeText(getActivity(), "Error getting data, please try again", Toast.LENGTH_LONG).show();
        }

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(currentCity.equals("")){
                    if(Constants.locationLongitude!=0 && Constants.locationLatitude!=0){
                        GetDataAsyncTask task = new GetDataAsyncTask(getActivity(),Constants.locationLongitude, Constants.locationLatitude);
                        task.execute();
                    }
                } else {
                    GetDataByCityCustomAsyncTask task = new GetDataByCityCustomAsyncTask(getActivity(), swipeLayout, currentCity, cityField, detailsField, currentTemperatureField, updatedField, weatherIcon);
                    task.execute();
                }
            }
        });

        YoYo.with(Techniques.BounceInLeft).duration(2000).playOn(cityField);
        YoYo.with(Techniques.BounceInLeft).duration(2000).playOn(updatedField);

        YoYo.with(Techniques.RollIn).duration(2000).playOn(detailsField);
        YoYo.with(Techniques.BounceInDown).duration(2000).playOn(currentTemperatureField);
        YoYo.with(Techniques.BounceInDown).duration(2000).playOn(weatherIcon);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        weatherFont = Typeface.createFromAsset(getActivity().getAssets(), "weather.ttf");
    }

    private void renderWeather(JSONObject json){
        try {
            currentCity = json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country");
            cityField.setText(currentCity);

            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            detailsField.setText(
                    details.getString("description").toUpperCase(Locale.US) +
                            "\n" + "Humidity: " + main.getString("humidity") + "%" +
                            "\n" + "Pressure: " + main.getString("pressure") + " hPa");

            currentTemperatureField.setText(
                    String.format("%.2f", main.getDouble("temp"))+ " ℃");

            DateFormat df = DateFormat.getDateTimeInstance();
            String updatedOn = df.format(new Date(json.getLong("dt")*1000));
            updatedField.setText("Last updated: " + updatedOn);

            setWeatherIcon(details.getInt("id"),
                    json.getJSONObject("sys").getLong("sunrise") * 1000,
                    json.getJSONObject("sys").getLong("sunset") * 1000);

        }catch(Exception e){
            Log.e("SimpleWeather", "One or more fields not found in the JSON data");
        }
    }

    private void setWeatherIcon(int actualId, long sunrise, long sunset){
        int id = actualId / 100;
        String icon = "";
        if(actualId == 800){
            long currentTime = new Date().getTime();
            if(currentTime>=sunrise && currentTime<sunset) {
                icon = getActivity().getString(R.string.weather_sunny);
            } else {
                icon = getActivity().getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = getActivity().getString(R.string.weather_thunder);
                    break;
                case 3 : icon = getActivity().getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = getActivity().getString(R.string.weather_foggy);
                    break;
                case 8 : icon = getActivity().getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = getActivity().getString(R.string.weather_snowy);
                    break;
                case 5 : icon = getActivity().getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }

    public void changeCity(String city){
        CityPreference.setCity(city);
        GetDataByCityAsyncTask task = new GetDataByCityAsyncTask(getActivity(), city, cityField, detailsField, currentTemperatureField, updatedField, weatherIcon);
        task.execute();
    }
}
