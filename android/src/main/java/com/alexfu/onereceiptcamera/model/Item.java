package com.alexfu.onereceiptcamera.model;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.alexfu.onereceiptcamera.database.ItemsTable;

public class Item implements Parcelable {

    private int mId, mAmount;
    private String mName;
    private double mPrice;

    public Item() {
        mPrice = 0.0;
        mAmount = 1;
        mId = -1;
    }

    public Item(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_ID));
        mAmount = cursor.getInt(cursor.getColumnIndex(ItemsTable.COL_AMOUNT));
        mName = cursor.getString(cursor.getColumnIndex(ItemsTable.COL_NAME));
        mPrice = cursor.getDouble(cursor.getColumnIndex(ItemsTable.COL_PRICE));
    }

    public int getId() {
        return mId;
    }

    public void setAmount(int amount) {
        mAmount = amount;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public void setPrice(double price) {
        mPrice = price;
    }

    public double getPrice() {
        return mPrice;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeInt(mAmount);
        parcel.writeString(mName);
        parcel.writeDouble(mPrice);
    }

    public static final Parcelable.Creator<Item> CREATOR
            = new Parcelable.Creator<Item>() {
        public Item createFromParcel(Parcel in) {
            return new Item(in);
        }

        public Item[] newArray(int size) {
            return new Item[size];
        }
    };

    private Item(Parcel in) {
        mId = in.readInt();
        mAmount = in.readInt();
        mName = in.readString();
        mPrice = in.readDouble();
    }
}
