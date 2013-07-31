package com.alexfu.onereceiptcamera.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.IBinder;

import com.alexfu.onereceiptcamera.database.DatabaseProducer;
import com.alexfu.onereceiptcamera.database.ReceiptsTable;
import com.alexfu.onereceiptcamera.ui.ReceiptsApp;

import java.io.File;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

public class UploadImageService extends Service implements Callback<UploadImageServiceResponse> {

    private static String TEST_URL = "http://192.168.1.4:3000";
    private static String PRODUCTION_URL = "http://infinite-spire-4513.herokuapp.com/";
    private static final RestAdapter API_ADAPTER = new RestAdapter.Builder()
            .setServer(PRODUCTION_URL).build();

    public static final String ACTION_UPDATE = "com.alexfu.onereceiptcamera.service.ACTION_UPDATE";

    private long mReceiptId;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File file = null;
        if(intent.hasExtra("uri")) {
            Uri uri = Uri.parse(intent.getStringExtra("uri"));
            file = new File(uri.getPath());
        } else if(intent.hasExtra("path")) {
            file = new File(intent.getStringExtra("path"));
        }

        mReceiptId = intent.getLongExtra("receipt_id", -1);

        if(file != null) {
            UploadImageApi api = API_ADAPTER.create(UploadImageApi.class);
            TypedString email = new TypedString(ReceiptsApp.getSettings().getOneReceiptEmail());
            api.upload(new TypedFile("image/jpeg", file), email, this);
        } else {
            stopSelf();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void success(UploadImageServiceResponse response, Response rawResponse) {
        if(response.status) {
            ContentValues values = new ContentValues();
            values.put(ReceiptsTable.COL_SENT, 1);

            DatabaseProducer producer = new DatabaseProducer(this);
            SQLiteDatabase database = producer.getWritableDatabase();
            database.update(ReceiptsTable.NAME, values, ReceiptsTable.COL_ID + "=" + mReceiptId, null);
            database.close();

            sendBroadcast(new Intent(ACTION_UPDATE));
        }
        stopSelf();
    }

    @Override
    public void failure(RetrofitError retrofitError) {
        stopSelf();
    }
}