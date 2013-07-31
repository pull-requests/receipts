package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReceiptsTable {

    public static final String NAME = "receipts";
    public static final String COL_ID = "_id";
    public static final String COL_LABEL = "label";
    public static final String COL_DATE = "date";
    public static final String COL_TOTAL = "total";
    public static final String COL_SENT = "sent";
    public static final String COL_IMG_PATH = "img_path";
    public static final String COL_ACTIVE = "active";

    private ReceiptsTable() {}

    public static void createTable(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE TABLE ").append(NAME).append("(");
        statement.append(COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, ");
        statement.append(COL_LABEL + " TEXT, ");
        statement.append(COL_DATE + " TEXT, ");
        statement.append(COL_TOTAL + " TEXT, ");
        statement.append(COL_SENT + " TINYINT, ");
        statement.append(COL_IMG_PATH + " TEXT, ");
        statement.append(COL_ACTIVE).append(" TINYINT DEFAULT 1);");

        database.execSQL(statement.toString());
    }

    public static Cursor getReceipts(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("SELECT ");
        statement.append("r.").append(COL_ID).append(", ");
        statement.append("r.").append(COL_LABEL).append(", ");
        statement.append("r.").append(COL_DATE).append(", ");
        statement.append("r.").append(COL_TOTAL).append(", ");
        statement.append("r.").append(COL_SENT).append(", ");
        statement.append("r.").append(COL_IMG_PATH).append(", ");
        statement.append("c.").append(CategoriesTable.COL_NAME).append(" AS category ");
        statement.append("FROM ").append(NAME).append(" r, ");
        statement.append(CategoriesTable.NAME).append(" c, ");
        statement.append(ReceiptsToCategoriesTable.NAME).append(" r2c ");
        statement.append("WHERE r.").append(COL_ID).append("=");
        statement.append("r2c.").append(ReceiptsToCategoriesTable.COL_RECEIPT_ID).append(" ");
        statement.append("AND c.").append(CategoriesTable.COL_ID).append("=");
        statement.append("r2c.").append(ReceiptsToCategoriesTable.COL_CATEGORY_ID);
        statement.append(" AND r.").append(COL_ACTIVE).append("=1");
        statement.append(" ORDER BY r.").append(COL_DATE).append(" DESC;");

        return database.rawQuery(statement.toString(), null);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        while(oldVersion < newVersion) {
            oldVersion++;
            switch (oldVersion) {
                case 2:
                    database.execSQL("ALTER TABLE " + NAME + " ADD COLUMN " + COL_ACTIVE + " TINYINT DEFAULT 1;");
                    break;
            }
        }
    }

    public static long insertReceipt(SQLiteDatabase database, ContentValues values) {
        return database.insert(NAME, null, values);
    }

    public static int updateReceipt(SQLiteDatabase database, ContentValues values, int receiptId) {
        return database.update(NAME, values, COL_ID + "=" + receiptId, null);
    }

    public static void deleteReceipt(SQLiteDatabase database, int receiptId) {
        int rowsAffected = database.delete(NAME, COL_ID + "=" + receiptId, null);

        // Remove associated entries from other tables
        ItemsTable.delete(database, receiptId);
        ReceiptsToItemsTable.delete(database, receiptId);
    }

    public static int inactivateReceipt(SQLiteDatabase database, int receiptId) {
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVE, 0);
        return database.update(NAME, values, COL_ID + "=" + receiptId, null);
    }

    public static int activateReceipt(SQLiteDatabase database, int receiptId) {
        ContentValues values = new ContentValues();
        values.put(COL_ACTIVE, 1);
        return database.update(NAME, values, COL_ID + "=" + receiptId, null);
    }
}
