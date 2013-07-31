package com.alexfu.onereceiptcamera.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

import com.alexfu.onereceiptcamera.R;

public class SettingActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(R.string.settings);
        setContentView(R.layout.activity_settings);
        if(savedInstanceState == null) {
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_content, new PrefsFragment())
                    .commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default: return false;
        }
    }
}
