package com.vineeth.sampleweatherapplication.util;

import org.json.JSONObject;

public class Constants {

    public static double locationLatitude = 0;
    public static double locationLongitude = 0;

    public static String getWeatherByCityNameAPI = "http://api.openweathermap.org/data/2.5/weather?q=%s&units=metric";

    public static JSONObject weatherData = null;
    public static String customLocation = "";
    public static String homeLocation = "";
}
