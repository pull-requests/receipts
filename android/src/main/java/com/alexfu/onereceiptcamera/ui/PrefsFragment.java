package com.alexfu.onereceiptcamera.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.alexfu.onereceiptcamera.R;

public class PrefsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
