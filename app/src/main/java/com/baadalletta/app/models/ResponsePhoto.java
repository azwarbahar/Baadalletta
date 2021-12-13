package com.baadalletta.app.models;

public class ResponsePhoto {

    private String message;

    private int status_code;

    private Pesanan data;

    public Pesanan getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public int getStatus_code() {
        return status_code;
    }
}
