package com.baadalletta.app.models;


import android.os.Parcel;
import android.os.Parcelable;

public class Customer implements Parcelable {

	private String whatsapp;

	private String distance;

	private String kec_id;

	private String kode_referal;

	private String latitude;

	private String created_at;

	private int id_kategori_pelanggan;

	private String foto_rumah;

	private String foto_pelanggan;

	private String updated_at;

	private String kode;

	private String no_ktp;

	private int id;

	private String longitude;

	private String jenis_pelanggan;

	private String prov_id;

	private String label_lokasi;

	private String foto_ktp;

	private String alamat;

	private String kel_id;

	private String nama;

	private String pekerjaan;

	private String tanggal;

	private String kota_id;

	private String status;

	private KategoriPelanggan kategori_pelanggan;

	protected Customer(Parcel in) {
		whatsapp = in.readString();
		distance = in.readString();
		kec_id = in.readString();
		kode_referal = in.readString();
		latitude = in.readString();
		created_at = in.readString();
		id_kategori_pelanggan = in.readInt();
		foto_rumah = in.readString();
		foto_pelanggan = in.readString();
		updated_at = in.readString();
		kode = in.readString();
		no_ktp = in.readString();
		id = in.readInt();
		longitude = in.readString();
		jenis_pelanggan = in.readString();
		prov_id = in.readString();
		label_lokasi = in.readString();
		foto_ktp = in.readString();
		alamat = in.readString();
		kel_id = in.readString();
		nama = in.readString();
		pekerjaan = in.readString();
		tanggal = in.readString();
		kota_id = in.readString();
		status = in.readString();
		kategori_pelanggan = in.readParcelable(KategoriPelanggan.class.getClassLoader());
	}

	public static final Creator<Customer> CREATOR = new Creator<Customer>() {
		@Override
		public Customer createFromParcel(Parcel in) {
			return new Customer(in);
		}

		@Override
		public Customer[] newArray(int size) {
			return new Customer[size];
		}
	};

	public String getWhatsapp() {
		return whatsapp;
	}

	public String getDistance() {
		return distance;
	}

	public String getKec_id() {
		return kec_id;
	}

	public String getKode_referal() {
		return kode_referal;
	}

	public String getLatitude() {
		return latitude;
	}

	public String getCreated_at() {
		return created_at;
	}

	public int getId_kategori_pelanggan() {
		return id_kategori_pelanggan;
	}

	public String getFoto_rumah() {
		return foto_rumah;
	}

	public String getFoto_pelanggan() {
		return foto_pelanggan;
	}

	public String getUpdated_at() {
		return updated_at;
	}

	public String getKode() {
		return kode;
	}

	public String getNo_ktp() {
		return no_ktp;
	}

	public int getId() {
		return id;
	}

	public String getLongitude() {
		return longitude;
	}

	public String getJenis_pelanggan() {
		return jenis_pelanggan;
	}

	public String getProv_id() {
		return prov_id;
	}

	public String getLabel_lokasi() {
		return label_lokasi;
	}

	public String getFoto_ktp() {
		return foto_ktp;
	}

	public String getAlamat() {
		return alamat;
	}

	public String getKel_id() {
		return kel_id;
	}

	public String getNama() {
		return nama;
	}

	public String getPekerjaan() {
		return pekerjaan;
	}

	public String getTanggal() {
		return tanggal;
	}

	public String getKota_id() {
		return kota_id;
	}

	public String getStatus() {
		return status;
	}

	public KategoriPelanggan getKategori_pelanggan() {
		return kategori_pelanggan;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(whatsapp);
		parcel.writeString(distance);
		parcel.writeString(kec_id);
		parcel.writeString(kode_referal);
		parcel.writeString(latitude);
		parcel.writeString(created_at);
		parcel.writeInt(id_kategori_pelanggan);
		parcel.writeString(foto_rumah);
		parcel.writeString(foto_pelanggan);
		parcel.writeString(updated_at);
		parcel.writeString(kode);
		parcel.writeString(no_ktp);
		parcel.writeInt(id);
		parcel.writeString(longitude);
		parcel.writeString(jenis_pelanggan);
		parcel.writeString(prov_id);
		parcel.writeString(label_lokasi);
		parcel.writeString(foto_ktp);
		parcel.writeString(alamat);
		parcel.writeString(kel_id);
		parcel.writeString(nama);
		parcel.writeString(pekerjaan);
		parcel.writeString(tanggal);
		parcel.writeString(kota_id);
		parcel.writeString(status);
		parcel.writeParcelable(kategori_pelanggan, i);
	}
}