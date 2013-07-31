package com.alexfu.onereceiptcamera.data;

import android.content.Context;
import android.widget.ArrayAdapter;

public class SpinnerMonthAdapter extends ArrayAdapter<CharSequence> {
    public SpinnerMonthAdapter(Context context, CharSequence[] months) {
        super(context, android.R.layout.simple_spinner_item, months);
    }
}
