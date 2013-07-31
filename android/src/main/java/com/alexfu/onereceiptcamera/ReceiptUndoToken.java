package com.alexfu.onereceiptcamera;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseBooleanArray;

import com.alexfu.onereceiptcamera.model.Receipt;

import java.util.ArrayList;

public class ReceiptUndoToken implements Parcelable {

    private int[] mCheckedPositions;
    private ArrayList<Receipt> mToDelete;

    public ReceiptUndoToken(int[] positions, ArrayList<Receipt> toDelete) {
        mCheckedPositions = positions;
        mToDelete = toDelete;
    }

    public int[] getCheckedPositions() {
        return mCheckedPositions;
    }

    public ArrayList<Receipt> getToDelete() {
        return mToDelete;
    }

    public static final Parcelable.Creator<ReceiptUndoToken> CREATOR
            = new Parcelable.Creator<ReceiptUndoToken>() {
        public ReceiptUndoToken createFromParcel(Parcel in) {
            return new ReceiptUndoToken(in);
        }

        public ReceiptUndoToken[] newArray(int size) {
            return new ReceiptUndoToken[size];
        }
    };

    private ReceiptUndoToken(Parcel in) {
        in.readIntArray(mCheckedPositions);
        in.readList(mToDelete, ArrayList.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeIntArray(mCheckedPositions);
        out.writeList(mToDelete);
    }
}
