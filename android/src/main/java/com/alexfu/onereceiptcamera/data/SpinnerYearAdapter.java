package com.alexfu.onereceiptcamera.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class SpinnerYearAdapter extends ArrayAdapter<Integer> {
    public SpinnerYearAdapter(Context context, ArrayList<Integer> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
    }
}
