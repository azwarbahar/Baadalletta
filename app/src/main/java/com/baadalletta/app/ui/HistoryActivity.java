package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.baadalletta.app.adapter.PesananHomeAdapter;
import com.baadalletta.app.adapter.RiwayatKurirAdapter;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Kurir kurir;
    private String kurir_id;

    private RecyclerView rv_riwayat;
    private ImageView img_kosong;
    private EditText et_cari;
    private SwipeRefreshLayout swipe_continer;

    private RiwayatKurirAdapter riwayatKurirAdapter;

    private ArrayList<Pesanan> pesanans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        kurir_id = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        rv_riwayat = findViewById(R.id.rv_riwayat);
        img_kosong = findViewById(R.id.img_kosong);
        et_cari = findViewById(R.id.et_cari);


        swipe_continer = findViewById(R.id.swipe_continer);
        swipe_continer.setOnRefreshListener(this);
        swipe_continer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);
        swipe_continer.post(new Runnable() {
            @Override
            public void run() {
                loadData(kurir_id);
            }
        });


    }

    private void loadData(String kurir_id) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePesananKurir> responsePesananKurirCall = apiInterface.getPesananIdKurir(kurir_id);
        responsePesananKurirCall.enqueue(new Callback<ResponsePesananKurir>() {
            @Override
            public void onResponse(Call<ResponsePesananKurir> call, Response<ResponsePesananKurir> response) {
                swipe_continer.setRefreshing(false);
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    String pesan = response.body().getMessage();
                    if (kode.equals("200")) {
                        pesanans = (ArrayList<Pesanan>) response.body().getData();
                        if (pesanans.size() < 1) {
                            onDataEmpty();
                        } else {
                            onDataReady();
                            rv_riwayat.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                            riwayatKurirAdapter = new RiwayatKurirAdapter(HistoryActivity.this, pesanans);
                            rv_riwayat.setAdapter(riwayatKurirAdapter);
                        }
                    } else {
                        onDataEmpty();
                        new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Maaf..")
                                .setContentText(pesan)
                                .show();
                    }

                } else {
                    onDataEmpty();
                    new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Maaf..")
                            .setContentText("Kesalahan Sistem!")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ResponsePesananKurir> call, Throwable t) {
                swipe_continer.setRefreshing(false);
                onDataEmpty();
                new SweetAlertDialog(HistoryActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Maaf..")
                        .setContentText(t.getMessage())
                        .show();

            }
        });

    }

    private void onDataEmpty() {
        img_kosong.setVisibility(View.VISIBLE);
        rv_riwayat.setVisibility(View.GONE);
        et_cari.setEnabled(false);
    }

    private void onDataReady() {
        et_cari.setEnabled(true);
        img_kosong.setVisibility(View.GONE);
        rv_riwayat.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        loadData(kurir_id);
    }
}