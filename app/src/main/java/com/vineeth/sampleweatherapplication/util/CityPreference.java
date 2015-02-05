package com.vineeth.sampleweatherapplication.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {


    static SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public static String getCurrentCity(){
        return prefs.getString("currentCity", "default");
    }

    public static void setCurrentCity(String city){
        prefs.edit().putString("currentCity", city).commit();
    }

    public static String getCity(){
        return prefs.getString("city", "default");
    }

    public static void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
