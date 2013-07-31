package com.alexfu.onereceiptcamera.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseProducer extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "receipts.db";
    private static final int VERSION = 2;

    public DatabaseProducer(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        ReceiptsTable.createTable(database);
        CategoriesTable.createTable(database);
        ReceiptsToCategoriesTable.createTable(database);
        ItemsTable.createTable(database);
        ReceiptsToItemsTable.createTable(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        ReceiptsTable.onUpgrade(database, oldVersion, newVersion);
    }

    @Override
    public SQLiteDatabase getWritableDatabase() {
        SQLiteDatabase database = super.getWritableDatabase();
        database.execSQL("PRAGMA foreign_keys = ON;");
        return database;
    }
}
