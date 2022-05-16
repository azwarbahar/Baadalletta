package com.baadalletta.app.models;

public class ResponseAbsen {

    private String message;

    private int status_code;

    private Absen data;

    public String getMessage() {
        return message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public Absen getData() {
        return data;
    }
}
