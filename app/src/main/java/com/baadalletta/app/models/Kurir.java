package com.baadalletta.app.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Kurir implements Parcelable {

    private int id;

    private String nama;

    private String alamat;

    private String whatsaap;

    private String status_akun;

    private String status_aksi;

    private String lat;

    private String lng;

    private String foto;

    private String foto_sim;

    private String foto_ktp;

    private String status_verifikasi;

    private String created_at;

    private String updated_at;

    protected Kurir(Parcel in) {
        id = in.readInt();
        nama = in.readString();
        alamat = in.readString();
        whatsaap = in.readString();
        status_akun = in.readString();
        status_aksi = in.readString();
        lat = in.readString();
        lng = in.readString();
        foto = in.readString();
        foto_sim = in.readString();
        foto_ktp = in.readString();
        status_verifikasi = in.readString();
        created_at = in.readString();
        updated_at = in.readString();
    }

    public static final Creator<Kurir> CREATOR = new Creator<Kurir>() {
        @Override
        public Kurir createFromParcel(Parcel in) {
            return new Kurir(in);
        }

        @Override
        public Kurir[] newArray(int size) {
            return new Kurir[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getAlamat() {
        return alamat;
    }

    public String getWhatsaap() {
        return whatsaap;
    }

    public String getStatus_akun() {
        return status_akun;
    }

    public String getStatus_aksi() {
        return status_aksi;
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public String getFoto() {
        return foto;
    }

    public String getFoto_sim() {
        return foto_sim;
    }

    public String getFoto_ktp() {
        return foto_ktp;
    }

    public String getStatus_verifikasi() {
        return status_verifikasi;
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
        parcel.writeString(nama);
        parcel.writeString(alamat);
        parcel.writeString(whatsaap);
        parcel.writeString(status_akun);
        parcel.writeString(status_aksi);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(foto);
        parcel.writeString(foto_sim);
        parcel.writeString(foto_ktp);
        parcel.writeString(status_verifikasi);
        parcel.writeString(created_at);
        parcel.writeString(updated_at);
    }
}
