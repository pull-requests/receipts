package com.alexfu.onereceiptcamera;

import android.content.Context;
import android.os.AsyncTask;

import com.alexfu.onereceiptcamera.database.DatabaseManager;

import java.util.ArrayList;

public class LoadCategoriesTask extends AsyncTask<Void, Void, ArrayList<String>> {
    public interface OnCategoriesLoadedListener {
        public void onCategoriesLoaded(ArrayList<String> categories);
    }

    private Context mContext;
    private OnCategoriesLoadedListener mListener;

    public LoadCategoriesTask(Context context, OnCategoriesLoadedListener listener) {
        mContext = context;
        mListener = listener;
    }

    @Override
    protected ArrayList<String> doInBackground(Void... voids) {
        DatabaseManager manager = new DatabaseManager(mContext);
        return manager.getCategories();
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        if(mListener != null) {
            mListener.onCategoriesLoaded(strings);
        }
    }
}
