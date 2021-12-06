package com.baadalletta.app.models;

import java.util.List;

public class ResponsPesanan {

    private int status_code;

    private String message;

    private Pesanan data;

    public int getStatus_code() {
        return status_code;
    }

    public String getMessage() {
        return message;
    }

    public Pesanan getData() {
        return data;
    }
}
