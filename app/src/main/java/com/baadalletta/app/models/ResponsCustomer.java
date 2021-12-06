package com.baadalletta.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ResponsCustomer implements Parcelable {

    private String message;

    private int status_code;

    private Customer data;

    protected ResponsCustomer(Parcel in) {
        message = in.readString();
        status_code = in.readInt();
        data = in.readParcelable(Customer.class.getClassLoader());
    }

    public static final Creator<ResponsCustomer> CREATOR = new Creator<ResponsCustomer>() {
        @Override
        public ResponsCustomer createFromParcel(Parcel in) {
            return new ResponsCustomer(in);
        }

        @Override
        public ResponsCustomer[] newArray(int size) {
            return new ResponsCustomer[size];
        }
    };

    public String getMessage() {
        return message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public Customer getData() {
        return data;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeInt(status_code);
        parcel.writeParcelable(data, i);
    }
}
