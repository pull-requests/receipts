package com.alexfu.onereceiptcamera.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collection;

public class SpinnerDateAdapter extends ArrayAdapter<Integer> {
    private ArrayList<Integer> data;

    public SpinnerDateAdapter(Context context, ArrayList<Integer> objects) {
        super(context, android.R.layout.simple_spinner_item, objects);
        data = objects;
    }

    public void addAll(Collection<? extends Integer> collection) {
        data.addAll(collection);
    }
}
