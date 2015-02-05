package com.vineeth.sampleweatherapplication.util;

import android.app.Activity;
import android.content.SharedPreferences;

public class CityPreference {


    static SharedPreferences prefs;

    public CityPreference(Activity activity){
        prefs = activity.getPreferences(Activity.MODE_PRIVATE);
    }

    public String getCity(){
        return prefs.getString("city", "default");
    }

    public static void setCity(String city){
        prefs.edit().putString("city", city).commit();
    }
}
