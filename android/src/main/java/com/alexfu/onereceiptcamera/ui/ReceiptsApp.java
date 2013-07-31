package com.alexfu.onereceiptcamera.ui;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.alexfu.onereceiptcamera.Settings;

public class ReceiptsApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Settings.getInstance().loadFromSharedPrefs(sp);
    }

    public static Settings getSettings() {
        return Settings.getInstance();
    }
}
