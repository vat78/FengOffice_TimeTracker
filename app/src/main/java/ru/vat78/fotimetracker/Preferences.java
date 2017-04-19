package ru.vat78.fotimetracker;

import android.content.SharedPreferences;

/**
 * Created by vat on 18.12.2015.
 */
public class Preferences {
    SharedPreferences preferences;

    public Preferences(SharedPreferences preferences) {
        this.preferences = preferences;
    }
    
    public void registerOnSharedPreferenceChangeListener(SharedPreferences.OnSharedPreferenceChangeListener listener){
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public int getInt(String key, int default_value) {
        return preferences.getInt(key, default_value);
    }

    public long getLong(String key, int default_value) {
        return preferences.getLong(key, default_value);
    }

    public String getString(String key, String default_value) {
        return preferences.getString(key, default_value);
    }

    public boolean getBoolean(String key, boolean default_value) {
        return preferences.getBoolean(key, default_value);
    }

    public void set(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public void set(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public void set(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public void set(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }
}
