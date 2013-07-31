package com.alexfu.onereceiptcamera.model;

import android.database.Cursor;
import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Calendar;

public class Receipt implements Parcelable {
    private int mId, mNumOfItems;
    private String mLabel, mCategory;
    private Calendar mDate;
    private double mTotal;
    private Uri mImageUri;
    private boolean mSent;

    public Receipt(Cursor cursor) {
        mId = cursor.getInt(cursor.getColumnIndex("_id"));
        mLabel = cursor.getString(cursor.getColumnIndex("label"));
        mDate = Calendar.getInstance();
        mDate.setTimeInMillis(cursor.getLong(cursor.getColumnIndex("date")));
        mCategory = cursor.getString(cursor.getColumnIndex("category"));
        String uriString = cursor.getString(cursor.getColumnIndex("img_path"));
        if(uriString != null)
            mImageUri = Uri.parse(uriString);
        mSent = cursor.getInt(cursor.getColumnIndex("sent")) == 1;
        mTotal = cursor.getDouble(cursor.getColumnIndex("total"));
    }

    public Receipt() {
        mId = -1;
    }

    public void setCategory(String category) {
        mCategory = category;
    }

    public String getCategory() {
        return mCategory;
    }

    public void setTotal(double total) {
        mTotal = total;
    }

    public double getTotal() {
        return mTotal;
    }

    public void setLabel(String label) {
        mLabel = label;
    }

    public String getLabel() {
        return mLabel;
    }

    public int getId() {
        return mId;
    }

    public void setDate(long timeInMillis) {
        if(mDate == null) {
            mDate = Calendar.getInstance();
        }

        mDate.setTimeInMillis(timeInMillis);
    }

    public Calendar getDate() {
        return mDate;
    }

    public void setSent(boolean isSent) {
        mSent = isSent;
    }

    public boolean isSent() {
        return mSent;
    }

    public void setImageUri(Uri imageUri) {
        mImageUri = imageUri;
    }

    public Uri getImagePath() {
        return mImageUri;
    }

    public void setNumberOfItems(int count) {
        mNumOfItems = count;
    }

    public int getNumberOfItems() {
        return mNumOfItems;
    }

    /* Parcelable */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(mId);
        parcel.writeString(mLabel);
        parcel.writeLong(mDate.getTimeInMillis());
        parcel.writeString(mCategory);
        parcel.writeInt(mSent ? 1 : 0);
        parcel.writeParcelable(mImageUri, flags);
    }

    public static final Parcelable.Creator<Receipt> CREATOR
            = new Parcelable.Creator<Receipt>() {
        public Receipt createFromParcel(Parcel in) {
            return new Receipt(in);
        }

        public Receipt[] newArray(int size) {
            return new Receipt[size];
        }
    };

    private Receipt(Parcel in) {
        mId = in.readInt();
        mLabel = in.readString();
        mDate = Calendar.getInstance();
        mDate.setTimeInMillis(in.readLong());
        mCategory = in.readString();
        mSent = in.readInt() == 1;
        mImageUri = in.readParcelable(Uri.class.getClassLoader());
    }
}
