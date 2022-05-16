package com.baadalletta.app.models;

public class Absen {

    private int id;
    private String id_kurir;
    private String tanggal;
    private String time;
    private String updated_at;
    private String created_at;
    private Kurir kurir;

    public int getId() {
        return id;
    }

    public String getId_kurir() {
        return id_kurir;
    }

    public String getTanggal() {
        return tanggal;
    }

    public String getTime() {
        return time;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public Kurir getKurir() {
        return kurir;
    }
}
