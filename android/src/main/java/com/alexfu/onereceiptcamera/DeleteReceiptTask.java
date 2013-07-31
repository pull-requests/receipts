package com.alexfu.onereceiptcamera;

import android.content.Context;
import android.os.AsyncTask;
import com.alexfu.onereceiptcamera.database.DatabaseManager;
import com.alexfu.onereceiptcamera.model.Receipt;

import java.util.ArrayList;

public class DeleteReceiptTask extends AsyncTask<ArrayList<Receipt>, Void, Void> {

    private Context mContext;

    public DeleteReceiptTask(Context context) {
        mContext = context;
    }

    @Override
    protected Void doInBackground(ArrayList<Receipt>... receipts) {
        DatabaseManager dbManager = new DatabaseManager(mContext);
        for(int i = 0, len = receipts[0].size(); i < len; i++) {
            dbManager.deleteReceipt(receipts[0].get(i).getId());
        }
        return null;
    }
}
