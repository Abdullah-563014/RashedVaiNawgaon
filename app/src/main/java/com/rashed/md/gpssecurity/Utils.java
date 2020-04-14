package com.rashed.md.gpssecurity;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {

    private static String preferenceName="BikeSecurityApplication";

    public static void setStringToStorage(Context context, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getStringFromStorage(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }
}
