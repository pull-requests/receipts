package com.alexfu.onereceiptcamera.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.alexfu.onereceiptcamera.model.Item;
import com.alexfu.onereceiptcamera.model.Receipt;

import java.util.ArrayList;

public class DatabaseManager {
    private DatabaseProducer mProducer;

    public DatabaseManager(Context context) {
        mProducer = new DatabaseProducer(context);
    }

    public ArrayList<Receipt> getReceipts() {
        ArrayList<Receipt> receipts = new ArrayList<Receipt>();
        SQLiteDatabase database = mProducer.getWritableDatabase();
        Cursor cursor = ReceiptsTable.getReceipts(database);
        while(cursor.moveToNext()) {
            Receipt r = new Receipt(cursor);
            Cursor count = ItemsTable.getItemsCountForReceipt(database, r.getId());
            count.moveToFirst();

            r.setNumberOfItems(count.getInt(0));
            receipts.add(r);
        }
        cursor.close();
        database.close();
        return receipts;
    }

    public long insertReceipt(Receipt r) {
        ContentValues values = new ContentValues();
        values.put(ReceiptsTable.COL_LABEL, r.getLabel());
        values.put(ReceiptsTable.COL_DATE, r.getDate().getTimeInMillis());
        values.put(ReceiptsTable.COL_TOTAL, r.getTotal());
        values.put(ReceiptsTable.COL_SENT, r.isSent());
        if(r.getImagePath() != null)
            values.put(ReceiptsTable.COL_IMG_PATH, r.getImagePath().toString());

        SQLiteDatabase database = mProducer.getWritableDatabase();
        long receiptId = ReceiptsTable.insertReceipt(database, values);
        long categoryId = CategoriesTable.getIdForName(database, r.getCategory());

        values.clear();
        values.put(ReceiptsToCategoriesTable.COL_RECEIPT_ID, receiptId);
        values.put(ReceiptsToCategoriesTable.COL_CATEGORY_ID, categoryId);

        ReceiptsToCategoriesTable.insert(database, values);

        database.close();

        return receiptId;
    }

    public int updateReceipt(Receipt r) {
        ContentValues values = new ContentValues();
        values.put(ReceiptsTable.COL_LABEL, r.getLabel());
        values.put(ReceiptsTable.COL_DATE, r.getDate().getTimeInMillis());
        values.put(ReceiptsTable.COL_TOTAL, r.getTotal());
        values.put(ReceiptsTable.COL_SENT, r.isSent());
        if(r.getImagePath() != null)
            values.put(ReceiptsTable.COL_IMG_PATH, r.getImagePath().toString());

        SQLiteDatabase database = mProducer.getWritableDatabase();
        int rowsUpdated = ReceiptsTable.updateReceipt(database, values, r.getId());
        long categoryId = CategoriesTable.getIdForName(database, r.getCategory());

        values.clear();
        values.put(ReceiptsToCategoriesTable.COL_RECEIPT_ID, r.getId());
        values.put(ReceiptsToCategoriesTable.COL_CATEGORY_ID, categoryId);

        ReceiptsToCategoriesTable.update(database, values, r.getId());

        database.close();

        return rowsUpdated;
    }

    public ArrayList<String> getCategories() {
        ArrayList<String> categories = new ArrayList<String>();
        SQLiteDatabase database = mProducer.getWritableDatabase();
        Cursor cursor = CategoriesTable.getCategories(database);
        while(cursor.moveToNext()) {
            categories.add(cursor.getString(0));
        }

        cursor.close();
        database.close();
        return categories;
    }

    public ArrayList<Item> getItemsForReceipt(int receiptId) {
        ArrayList<Item> items = new ArrayList<Item>();
        SQLiteDatabase database = mProducer.getWritableDatabase();
        Cursor cursor = ItemsTable.getItemsForReceipt(database, receiptId);
        while(cursor.moveToNext()) {
            items.add(new Item(cursor));
        }

        cursor.close();
        database.close();
        return items;
    }

    public long insertItem(Item item, int receiptId) {
        ContentValues values = new ContentValues();
        values.put(ItemsTable.COL_NAME, item.getName());
        values.put(ItemsTable.COL_AMOUNT, item.getAmount());
        values.put(ItemsTable.COL_PRICE, item.getPrice());

        SQLiteDatabase database = mProducer.getWritableDatabase();
        long itemId = ItemsTable.insertItem(database, values);

        values.clear();
        values.put(ReceiptsToItemsTable.COL_ITEM_ID, itemId);
        values.put(ReceiptsToItemsTable.COL_RECEIPT_ID, receiptId);

        ReceiptsToItemsTable.insert(database, values);

        database.close();

        return itemId;
    }

    public void updateItem(Item item) {
        ContentValues values = new ContentValues();
        values.put(ItemsTable.COL_NAME, item.getName());
        values.put(ItemsTable.COL_AMOUNT, item.getAmount());
        values.put(ItemsTable.COL_PRICE, item.getPrice());

        SQLiteDatabase database = mProducer.getWritableDatabase();
        ItemsTable.updateItem(database, values, item.getId());

        database.close();
    }

    public void deleteItem(int itemId) {
        SQLiteDatabase database = mProducer.getWritableDatabase();
        ItemsTable.delete(database, itemId);
        database.close();
    }

    public void deleteReceipt(int receiptId) {
        SQLiteDatabase database = mProducer.getWritableDatabase();
        ReceiptsTable.inactivateReceipt(database, receiptId);
        database.close();
    }

    public void restoreReceipt(int receiptId) {
        SQLiteDatabase database = mProducer.getWritableDatabase();
        ReceiptsTable.activateReceipt(database, receiptId);
        database.close();
    }

    public void deleteReceiptPermanently(int receiptId) {
        SQLiteDatabase database = mProducer.getWritableDatabase();
        ReceiptsTable.deleteReceipt(database, receiptId);
        database.close();
    }
}
