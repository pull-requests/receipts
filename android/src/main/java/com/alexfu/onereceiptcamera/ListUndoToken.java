package com.alexfu.onereceiptcamera;

import android.os.Parcel;
import android.os.Parcelable;

import com.alexfu.onereceiptcamera.model.Item;

public class ListUndoToken implements Parcelable {

    public int position;
    public Item object;

    public ListUndoToken(int position, Item object) {
        this.position = position;
        this.object = object;
    }

    public static final Parcelable.Creator<ListUndoToken> CREATOR
            = new Parcelable.Creator<ListUndoToken>() {
        public ListUndoToken createFromParcel(Parcel in) {
            return new ListUndoToken(in);
        }

        public ListUndoToken[] newArray(int size) {
            return new ListUndoToken[size];
        }
    };

    private ListUndoToken(Parcel in) {
        position = in.readInt();
        object = in.readParcelable(Item.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(position);
        parcel.writeParcelable(object, flags);
    }
}
