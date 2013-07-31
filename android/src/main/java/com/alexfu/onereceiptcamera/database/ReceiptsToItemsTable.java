package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class ReceiptsToItemsTable {

    public static final String NAME = "receipts_to_items";
    public static final String COL_ID = "_id";
    public static final String COL_RECEIPT_ID = "receipt_id";
    public static final String COL_ITEM_ID = "item_id";

    private ReceiptsToItemsTable() {}

    public static void createTable(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE TABLE ").append(NAME).append("(");
        statement.append(COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, ");
        statement.append(COL_RECEIPT_ID + " INTEGER, ");
        statement.append(COL_ITEM_ID + " INTEGER);");

        database.execSQL(statement.toString());
    }

    public static long insert(SQLiteDatabase database, ContentValues values) {
        return database.insertOrThrow(NAME, null, values);
    }

    public static void delete(SQLiteDatabase database, int receiptId) {
        database.delete(NAME, COL_RECEIPT_ID + "=" + receiptId, null);
    }
}
