package com.baadalletta.app.models;

import com.google.android.gms.maps.model.LatLng;

public class Place {

    private String id_pesanan;

    private LatLng latLng;

    private Pesanan pesanan;

    public Place(String id_pesanan, LatLng latLng, Pesanan pesanan) {
        this.id_pesanan = id_pesanan;
        this.latLng = latLng;
        this.pesanan = pesanan;
    }

    public Pesanan getPesanan() {
        return pesanan;
    }

    public void setPesanan(Pesanan pesanan) {
        this.pesanan = pesanan;
    }

    public String getId_pesanan() {
        return id_pesanan;
    }

    public void setId_pesanan(String id_pesanan) {
        this.id_pesanan = id_pesanan;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }
}
