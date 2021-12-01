package com.baadalletta.app.models;

import java.util.List;

public class ResponsePesananKurir{

	private int status_code;

	private List<Pesanan> data;

	private String message;

	public int getStatus_code() {
		return status_code;
	}

	public List<Pesanan> getData() {
		return data;
	}

	public String getMessage() {
		return message;
	}
}