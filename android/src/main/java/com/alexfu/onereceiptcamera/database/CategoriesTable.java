package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CategoriesTable {

    public static final String NAME = "categories";
    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";

    private static final String[] CATEGORIES = {"Arts & Entertainment",
        "Deal Site", "Donations", "Entertainment", "Food & Dining",
        "Health & Fitness", "Home", "Kids", "Media", "Office Supplies",
        "Pets", "Shopping", "Travel", "Uncategorized", "Web Hosting"};

    private CategoriesTable() {}

    public static void createTable(SQLiteDatabase database) {
        StringBuilder statement = new StringBuilder();
        statement.append("CREATE TABLE ").append(NAME).append("(");
        statement.append(COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT UNIQUE, ");
        statement.append(COL_NAME + " TEXT);");
        database.execSQL(statement.toString());

        // Load table with initial values.
        ContentValues values = new ContentValues();
        for(String category : CATEGORIES) {
            values.clear();
            values.put(COL_NAME, category);
            insertCategory(database, values);
        }
    }

    public static int getIdForName(SQLiteDatabase database, String name) {
        int id = -1;
        Cursor cursor = database.rawQuery("SELECT " + COL_ID + " FROM " + NAME + " WHERE " +
                COL_NAME + "='" + name + "';", null);

        if(cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }

        cursor.close();
        return id;
    }

    public static Cursor getCategories(SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("SELECT " + COL_NAME + " FROM " + NAME, null);
        return cursor;
    }

    public static long insertCategory(SQLiteDatabase database, ContentValues values) {
        return database.insert(NAME, null, values);
    }
}
