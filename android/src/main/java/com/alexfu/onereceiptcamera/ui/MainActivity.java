package com.alexfu.onereceiptcamera.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.alexfu.onereceiptcamera.DeleteReceiptTask;
import com.alexfu.onereceiptcamera.R;
import com.alexfu.onereceiptcamera.ReceiptUndoToken;
import com.alexfu.onereceiptcamera.RestoreReceiptTask;
import com.alexfu.onereceiptcamera.UndoBarController;
import com.alexfu.onereceiptcamera.data.StickyAdapter;
import com.alexfu.onereceiptcamera.database.DatabaseManager;
import com.alexfu.onereceiptcamera.model.Receipt;
import com.alexfu.onereceiptcamera.service.UploadImageService;
import com.crittercism.app.Crittercism;
import com.emilsjolander.components.stickylistheaders.StickyListHeadersListView;

import java.util.ArrayList;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener, UndoBarController.UndoListener {

    private StickyListHeadersListView mListView;
    private StickyAdapter mAdapter;
    private ArrayList<Receipt> mReceipts;
    private UndoBarController mUndoBarController;

    private static final int NEW_RECEIPT = 1, UPDATE_RECEIPT = 2;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            new GetReceiptsTasks().execute();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crittercism.init(getApplicationContext(), "51f84cd246b7c27dc2000004");
        setContentView(R.layout.activity_main);
        mReceipts = new ArrayList<Receipt>();

        View undobar = findViewById(R.id.undobar);
        mUndoBarController = new UndoBarController(undobar, this);

        // Setup ListView
        mListView = (StickyListHeadersListView) findViewById(R.id.list);
        mAdapter = new StickyAdapter(this, mReceipts);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int position, long id, boolean checked) {
                mAdapter.setChecked(position, checked);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.main_cab, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem menuItem) {
                switch(menuItem.getItemId()) {
                    case R.id.delete:
                        SparseBooleanArray checkedItemPositions = mListView.getCheckedItemPositions().clone();
                        int[] checkedPositions = new int[checkedItemPositions.size()];
                        ArrayList<Receipt> toDelete = new ArrayList<Receipt>();

                        for(int i = 0; i < checkedItemPositions.size(); i++) {
                            int position = checkedItemPositions.keyAt(i);
                            if(checkedItemPositions.get(position)) {
                                toDelete.add(mReceipts.get(position));
                                checkedPositions[i] = position;
                            }
                        }

                        new DeleteReceiptTask(MainActivity.this).execute(toDelete);

                        mReceipts.removeAll(toDelete);
                        mAdapter.notifyDataSetChanged();
                        mAdapter.clearChecked();

                        String message = getResources().getQuantityString(
                                R.plurals.item_deleted,
                                toDelete.size(),
                                toDelete.size()
                        );

                        mUndoBarController.showUndoBar(false, message, new ReceiptUndoToken(checkedPositions, toDelete));
                        mode.finish();
                        return true;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.clearChecked();
            }
        });

       new GetReceiptsTasks().execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mBroadcastReceiver, new IntentFilter(UploadImageService.ACTION_UPDATE));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mUndoBarController.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mUndoBarController.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.new_receipt:
                startActivityForResult(new Intent(this, EditReceiptActivity.class), NEW_RECEIPT);
                return true;
            case R.id.settings:
                startActivity(new Intent(this, SettingActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case NEW_RECEIPT:
            case UPDATE_RECEIPT:
                if(resultCode == Activity.RESULT_OK) {
                    new GetReceiptsTasks().execute();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        Intent intent = new Intent(this, EditReceiptActivity.class);
        intent.putExtra("receipt", mReceipts.get(position));
        startActivityForResult(intent, UPDATE_RECEIPT);
    }

    @Override
    public void onUndo(Parcelable token) {
        ReceiptUndoToken undoToken = (ReceiptUndoToken) token;
        new RestoreReceiptTask(this).execute(undoToken.getToDelete());
        int[] checkedPositions = undoToken.getCheckedPositions();
        ArrayList<Receipt> removed = undoToken.getToDelete();
        for(int i = 0; i < checkedPositions.length; i++) {
            Receipt receipt = removed.get(i);
            mReceipts.add(checkedPositions[i], receipt);
        }
        mAdapter.notifyDataSetChanged();
    }

    private class GetReceiptsTasks extends AsyncTask<Void, Void, ArrayList<Receipt>> {
        @Override
        protected ArrayList<Receipt> doInBackground(Void... voids) {
            DatabaseManager man = new DatabaseManager(getApplicationContext());
            return man.getReceipts();
        }

        @Override
        protected void onPostExecute(ArrayList<Receipt> receipts) {
            mReceipts.clear();
            mReceipts.addAll(receipts);
            mAdapter.notifyDataSetChanged();
        }
    }
}