package com.alexfu.onereceiptcamera.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;

public class SpinnerCategoryAdapter extends ArrayAdapter<CharSequence> {
    private ArrayList<CharSequence> data;

    public SpinnerCategoryAdapter(Context context, ArrayList<CharSequence> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        data = objects;
    }

    public void addAll(Collection<? extends CharSequence> collection) {
        data.addAll(collection);
    }
}
