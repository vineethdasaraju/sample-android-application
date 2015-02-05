package com.vineeth.sampleweatherapplication.background;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.vineeth.sampleweatherapplication.R;
import com.vineeth.sampleweatherapplication.util.CityPreference;
import com.vineeth.sampleweatherapplication.util.Constants;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

public class GetDataLatLongAsyncTask extends AsyncTask<Void, Void, JSONObject> {

    double longitude, latitude;
    Activity app;
    ProgressDialog pd = null;
    TextView cityField, pressureField, currentTemperatureField, updatedField, weatherIcon, humidityField, currentWeatherInfo;

    public GetDataLatLongAsyncTask(Activity app, Double longitude, Double latitude, TextView currentWeatherInfo, TextView cityField, TextView pressureField, TextView humidityField, TextView currentTemperatureField, TextView updatedField, TextView weatherIcon) {
        this.app = app;
        this.longitude = longitude;
        this.latitude = latitude;
        this.cityField = cityField;
        this.pressureField = pressureField;
        this.currentTemperatureField = currentTemperatureField;
        this.updatedField = updatedField;
        this.weatherIcon = weatherIcon;
        this.humidityField = humidityField;
        this.currentWeatherInfo = currentWeatherInfo;
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
    protected void onPreExecute() {
        pd = ProgressDialog.show(app,"Please wait", "Updating your weather information");
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        renderWeather(jsonObject);
        pd.dismiss();
    }
    public void renderWeather(JSONObject json){
        try {
            String currentCity = json.getString("name").toUpperCase(Locale.US) +
                    ", " +
                    json.getJSONObject("sys").getString("country");

            String bcity = new CityPreference(app).getCity();
            new CityPreference(app).setCity(currentCity);
            String acity = new CityPreference(app).getCity();
            Constants.customLocation = currentCity;
            Constants.homeLocation = currentCity;
            JSONObject details = json.getJSONArray("weather").getJSONObject(0);
            JSONObject main = json.getJSONObject("main");
            String currentWeatherDetails = details.getString("description");
            currentWeatherInfo.setText(currentWeatherDetails);

            cityField.setText(currentCity);

            humidityField.setText("Humidity: " + main.getString("humidity") + "%");

            pressureField.setText("Pressure: " + main.getString("pressure") + " hPa");

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
                icon = app.getString(R.string.weather_sunny);
            } else {
                icon = app.getString(R.string.weather_clear_night);
            }
        } else {
            switch(id) {
                case 2 : icon = app.getString(R.string.weather_thunder);
                    break;
                case 3 : icon = app.getString(R.string.weather_drizzle);
                    break;
                case 7 : icon = app.getString(R.string.weather_foggy);
                    break;
                case 8 : icon = app.getString(R.string.weather_cloudy);
                    break;
                case 6 : icon = app.getString(R.string.weather_snowy);
                    break;
                case 5 : icon = app.getString(R.string.weather_rainy);
                    break;
            }
        }
        weatherIcon.setText(icon);
    }
}
