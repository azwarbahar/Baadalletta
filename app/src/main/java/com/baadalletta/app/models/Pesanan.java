package com.baadalletta.app.models;


public class Pesanan {

	private int fee_pengerjaan;

	private int ongkir;

	private int id_customer;

	private String created_at;

	private int id_kurir;

	private String status_pesanan;

	private int total;

	private String updated_at;

	private String titik_koordinat;

	private String status_pembayaran;

	private int sub_total;

	private int id;

	private String foto_pengantaran;

	private String mode_pesanan;

	private int id_tipe_pengiriman;

	private String lokasi_tujuan;

	private Customer customer;

	public int getFee_pengerjaan() {
		return fee_pengerjaan;
	}

	public int getOngkir() {
		return ongkir;
	}

	public int getId_customer() {
		return id_customer;
	}

	public String getCreated_at() {
		return created_at;
	}

	public int getId_kurir() {
		return id_kurir;
	}

	public String getStatus_pesanan() {
		return status_pesanan;
	}

	public int getTotal() {
		return total;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public String getTitik_koordinat() {
		return titik_koordinat;
	}

	public String getStatus_pembayaran() {
		return status_pembayaran;
	}

	public int getSub_total() {
		return sub_total;
	}

	public int getId() {
		return id;
	}

	public String getFoto_pengantaran() {
		return foto_pengantaran;
	}

	public String getMode_pesanan() {
		return mode_pesanan;
	}

	public int getId_tipe_pengiriman() {
		return id_tipe_pengiriman;
	}

	public String getLokasi_tujuan() {
		return lokasi_tujuan;
	}

	public Customer getCustomer() {
		return customer;
	}
}