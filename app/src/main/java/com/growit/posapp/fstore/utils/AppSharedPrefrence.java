package com.growit.posapp.fstore.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AppSharedPrefrence
{
    public static enum PREF_KEY {
        USER_PROFILE_INFO("profile_info");

        public final String KEY;
        PREF_KEY(String key) {
            this.KEY = key;
        }
    }

    public static void putString(Context context, PREF_KEY key, String value) {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(key.KEY, value);
            editor.commit();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getString(Context context, PREF_KEY key) {
        String value="";
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            value = sharedPref.getString(key.KEY, null);
        }catch (Exception e){
            e.printStackTrace();
            value="";
        }
        return value;
    }
}
