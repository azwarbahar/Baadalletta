package com.baadalletta.app.models;

public class ResponseKurir {

    private String message;

    private int status_code;

    private Kurir data;

    public String getMessage() {
        return message;
    }

    public int getStatus_code() {
        return status_code;
    }

    public Kurir getData() {
        return data;
    }
}
