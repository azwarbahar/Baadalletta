package com.baadalletta.app.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Pesanan implements Parcelable {

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

	protected Pesanan(Parcel in) {
		fee_pengerjaan = in.readInt();
		ongkir = in.readInt();
		id_customer = in.readInt();
		created_at = in.readString();
		id_kurir = in.readInt();
		status_pesanan = in.readString();
		total = in.readInt();
		updated_at = in.readString();
		titik_koordinat = in.readString();
		status_pembayaran = in.readString();
		sub_total = in.readInt();
		id = in.readInt();
		foto_pengantaran = in.readString();
		mode_pesanan = in.readString();
		id_tipe_pengiriman = in.readInt();
		lokasi_tujuan = in.readString();
		customer = in.readParcelable(Customer.class.getClassLoader());
	}

	public static final Creator<Pesanan> CREATOR = new Creator<Pesanan>() {
		@Override
		public Pesanan createFromParcel(Parcel in) {
			return new Pesanan(in);
		}

		@Override
		public Pesanan[] newArray(int size) {
			return new Pesanan[size];
		}
	};

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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(fee_pengerjaan);
		parcel.writeInt(ongkir);
		parcel.writeInt(id_customer);
		parcel.writeString(created_at);
		parcel.writeInt(id_kurir);
		parcel.writeString(status_pesanan);
		parcel.writeInt(total);
		parcel.writeString(updated_at);
		parcel.writeString(titik_koordinat);
		parcel.writeString(status_pembayaran);
		parcel.writeInt(sub_total);
		parcel.writeInt(id);
		parcel.writeString(foto_pengantaran);
		parcel.writeString(mode_pesanan);
		parcel.writeInt(id_tipe_pengiriman);
		parcel.writeString(lokasi_tujuan);
		parcel.writeParcelable(customer, i);
	}
}