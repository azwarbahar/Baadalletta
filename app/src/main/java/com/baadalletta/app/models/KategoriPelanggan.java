package com.baadalletta.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class KategoriPelanggan implements Parcelable {

    private int id;

    private String kode;

    private String nama;

    private String kata_kunci;

    private String created_at;

    private String updated_at;

    protected KategoriPelanggan(Parcel in) {
        id = in.readInt();
        kode = in.readString();
        nama = in.readString();
        kata_kunci = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<KategoriPelanggan> CREATOR = new Creator<KategoriPelanggan>() {
        @Override
        public KategoriPelanggan createFromParcel(Parcel in) {
            return new KategoriPelanggan(in);
        }

        @Override
        public KategoriPelanggan[] newArray(int size) {
            return new KategoriPelanggan[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getKode() {
        return kode;
    }

    public String getNama() {
        return nama;
    }

    public String getKata_kunci() {
        return kata_kunci;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(kode);
        parcel.writeString(nama);
        parcel.writeString(kata_kunci);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
