package com.example.racinggame;

import android.content.Context;
import android.content.SharedPreferences;

public class MSP {
    private static final String SP_FILE = "SharedPrefsRacingApp";

    private SharedPreferences preferences;
    private static MSP msp;

    private MSP(Context context){
        preferences = context.getApplicationContext()
                .getSharedPreferences(SP_FILE, Context.MODE_PRIVATE);
    }

    public static MSP getInstance(Context context){
        if (msp == null)
            msp = new MSP(context);
        return msp;
    }


    public int getIntSP(String key, int defValue) {
        return this.preferences.getInt(key, defValue);
    }

    public String getStrSP(String key, String defValue) {
        return this.preferences.getString(key, defValue);
    }

    public void putIntSP(String key, int value) {
        //accessible only to me and no other application
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }


    public void putStringSP(String key, String value) {
        SharedPreferences.Editor editor = this.preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
}
