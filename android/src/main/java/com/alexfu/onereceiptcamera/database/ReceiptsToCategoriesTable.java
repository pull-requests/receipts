package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ReceiptsToCategoriesTable {

    public static final String NAME = "receipts_to_categories";
    public static final String COL_ID = "_id";
    public static final String COL_RECEIPT_ID = "receipt_id";
    public static final String COL_CATEGORY_ID = "category_id";

    private ReceiptsToCategoriesTable() {}

    public static void createTable(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE TABLE ").append(NAME).append("(");
        statement.append(COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, ");
        statement.append(COL_RECEIPT_ID + " INTEGER, ");
        statement.append(COL_CATEGORY_ID + " INTEGER);");

        database.execSQL(statement.toString());
    }

    public static long insert(SQLiteDatabase database, ContentValues values) {
        return database.insert(NAME, null, values);
    }

    public static int update(SQLiteDatabase database, ContentValues values, int receiptId) {
        return database.update(NAME, values, COL_RECEIPT_ID + "=" + receiptId, null);
    }
}
