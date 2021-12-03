package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class DetailPesananActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Pesanan pesanan_intent;
    private Customer customer_intent;
    private TextView tv_alamat;
    private TextView tv_jarak;
    private TextView tv_waktu;
    private TextView tv_kode;
    private TextView tv_nama;
    private TextView tv_whatsapp;

    private ImageView img_foto;
    private ImageView img_whatsapp;

    private RelativeLayout rl_mulai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        tv_alamat = findViewById(R.id.tv_alamat);
        tv_jarak = findViewById(R.id.tv_jarak);
        tv_waktu = findViewById(R.id.tv_waktu);
        tv_kode = findViewById(R.id.tv_kode);
        tv_nama = findViewById(R.id.tv_nama);
        tv_whatsapp = findViewById(R.id.tv_whatsapp);
        img_foto = findViewById(R.id.img_foto);
        img_whatsapp = findViewById(R.id.img_whatsapp);
        rl_mulai = findViewById(R.id.rl_mulai);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        pesanan_intent = getIntent().getParcelableExtra("Extra_data");

        if (pesanan_intent == null){
            Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, pesanan_intent.getCustomer().toString(), Toast.LENGTH_SHORT).show();
        }

        initDataIntent(pesanan_intent);

    }

    private void initDataIntent(Pesanan pesanan_intent) {

        String alamat = pesanan_intent.getCustomer().getAlamat();
        String whatsapp = "Whatsapp : "+pesanan_intent.getCustomer().getWhatsapp();
        String kode = "Kode : "+pesanan_intent.getCustomer().getKode();
        String nama = "Nama : "+pesanan_intent.getCustomer().getNama();

        tv_alamat.setText(alamat);
        tv_whatsapp.setText(whatsapp);
        tv_kode.setText(kode);
        tv_nama.setText(nama);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}