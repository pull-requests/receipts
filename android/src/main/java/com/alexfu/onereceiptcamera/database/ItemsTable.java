package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemsTable {

    public static final String NAME = "items";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_PRICE = "price";

    private ItemsTable() {}

    public static void createTable(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE TABLE ").append(NAME).append(" (");
        statement.append(COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, ");
        statement.append(COL_NAME + " TEXT, ");
        statement.append(COL_AMOUNT + " INTEGER, ");
        statement.append(COL_PRICE + " DOUBLE);");

        database.execSQL(statement.toString());
    }

    public static long insertItem(SQLiteDatabase database, ContentValues values) {
        return database.insertOrThrow(NAME, null, values);
    }

    public static int updateItem(SQLiteDatabase database, ContentValues values, int itemId) {
        return database.update(NAME, values, COL_ID + "=" + itemId, null);
    }

    public static Cursor getItemsForReceipt(SQLiteDatabase database, int receiptId) {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT ").
                append("i.").append(COL_ID).append(", ").
                append("i.").append(COL_NAME).append(", ").
                append("i.").append(COL_AMOUNT).append(", ").
                append("i.").append(COL_PRICE).append(" ").
                append("FROM ").append(NAME).append(" i, ").
                append(ReceiptsToItemsTable.NAME).append(" r2i ").
                append("WHERE i.").append(COL_ID).append("=").
                append("r2i.").append(ReceiptsToItemsTable.COL_ITEM_ID).append(" ").
                append("AND r2i.").append(ReceiptsToItemsTable.COL_RECEIPT_ID).
                append("=").append(receiptId);

        return database.rawQuery(statement.toString(), null);
    }

    public static int delete(SQLiteDatabase database, int itemId) {
        return database.delete(NAME, COL_ID + "=" + itemId, null);
    }

    public static Cursor getItemsCountForReceipt(SQLiteDatabase database, int receiptId) {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT COUNT(*) ").
                append("FROM ").append(NAME).append(" i, ").
                append(ReceiptsToItemsTable.NAME).append(" r2i ").
                append("WHERE i.").append(COL_ID).append("=").
                append("r2i.").append(ReceiptsToItemsTable.COL_ITEM_ID).append(" ").
                append("AND r2i.").append(ReceiptsToItemsTable.COL_RECEIPT_ID).
                append("=").append(receiptId);

        return database.rawQuery(statement.toString(), null);
    }
}
