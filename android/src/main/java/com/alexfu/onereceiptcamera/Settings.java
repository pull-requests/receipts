package com.alexfu.onereceiptcamera;

import android.content.SharedPreferences;

public class Settings {
    private static final Settings INSTANCE = new Settings();
    private String mOneReceiptEmail;

    private Settings() {}

    public static Settings getInstance() {
        return INSTANCE;
    }

    public void loadFromSharedPrefs(SharedPreferences sp) {
        mOneReceiptEmail = sp.getString("username_onereceipt", null);
        if(mOneReceiptEmail != null) {
            mOneReceiptEmail = mOneReceiptEmail.concat("@onereceipt.com");
        }
    }

    public String getOneReceiptEmail() {
        return mOneReceiptEmail;
    }

    public boolean hasOneReceiptEmail() {
        return mOneReceiptEmail != null;
    }
}
