package com.baadalletta.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.ResponseKurir;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AkunActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout ll_edit;
    private CardView cv_edit_password;
    private CardView cv_history;
    private CardView cv_logout;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Kurir kurir;
    private String kurir_id;

    private TextView tv_nama;
    private TextView tv_telpon;
    private TextView tv_status;

    private SwipeRefreshLayout swipe_continer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        kurir_id = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        tv_nama = findViewById(R.id.tv_nama);
        tv_telpon = findViewById(R.id.tv_telpon);
        tv_status = findViewById(R.id.tv_status);

        ll_edit = findViewById(R.id.ll_edit);
        cv_edit_password = findViewById(R.id.cv_edit_password);
        cv_history = findViewById(R.id.cv_history);
        cv_logout = findViewById(R.id.cv_logout);

        ll_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AkunActivity.this, EditAkunActivity.class));
            }
        });

        cv_edit_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AkunActivity.this, EditPasswordActivity.class));
            }
        });

        cv_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AkunActivity.this, HistoryActivity.class));
            }
        });

        cv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Logout")
                        .setContentText("Ingin Keluar Dari Akun ?")
                        .setCancelButton("Tidak", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                            }
                        })
                        .setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                startActivity(new Intent(AkunActivity.this, LoginActivity.class));
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.apply();
                                editor.clear();
                                editor.commit();
                                finish();
                            }
                        })
                        .show();
            }
        });



        swipe_continer = findViewById(R.id.swipe_continer);
        swipe_continer.setOnRefreshListener(this);
        swipe_continer.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_blue_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_green_dark);
        swipe_continer.post(new Runnable() {
            @Override
            public void run() {
                laodDataKurur(kurir_id);
            }
        });


    }

    private void laodDataKurur(String kurir_id_send) {

        SweetAlertDialog pDialog = new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Cek Akun Kurir..");
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseKurir> responseKurirCall = apiInterface.getKurirId(kurir_id_send);
        responseKurirCall.enqueue(new Callback<ResponseKurir>() {
            @Override
            public void onResponse(Call<ResponseKurir> call, Response<ResponseKurir> response) {
                pDialog.dismiss();
                swipe_continer.setRefreshing(false);
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    String status_code = String.valueOf(response.body().getStatus_code());
                    if (status_code.equals("200")) {
                        kurir = response.body().getData();
                        prosesCekData(kurir);
                        initDataKurur(kurir);
                    } else {
                        new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Opss..")
                                .setContentText(message)
                                .show();
                    }

                } else {
                    new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Mohon Maaf.")
                            .setContentText("Terjadi Kesalahan Sistem")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ResponseKurir> call, Throwable t) {
                pDialog.dismiss();
                swipe_continer.setRefreshing(false);
                new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Mohon Maaf.")
                        .setContentText("Terjadi Kesalahan Sistem")
                        .show();
            }
        });


    }

    private void initDataKurur(Kurir kurir) {

        tv_nama.setText(kurir.getNama());
        tv_telpon.setText(kurir.getWhatsaap());
        tv_status.setText(kurir.getStatus_akun());

    }

    private void prosesCekData(Kurir kurir) {

        String status_akun = kurir.getStatus_akun();
        String kurir_id_session = String.valueOf(kurir.getId());
        if (status_akun.equals("active")) {
            startSessionSave(kurir_id_session);
            Log.e("Kurir", "status : " + status_akun);
        } else {
            SweetAlertDialog sweetAlertDialogError = new SweetAlertDialog(AkunActivity.this,
                    SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialogError.setTitleText("Opss..");
            sweetAlertDialogError.setCancelable(false);
            sweetAlertDialogError.setContentText("Akun ini telah di suspend!");
            sweetAlertDialogError.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    Intent intent = new Intent(AkunActivity.this, LoginActivity.class);
                    startActivity(intent);
                    SharedPreferences mPreferences1 = getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = mPreferences1.edit();
                    editor.apply();
                    editor.clear();
                    editor.commit();
                    finish();
                }
            });
            sweetAlertDialogError.show();
        }

    }

    private void startSessionSave(String kurir_id) {
        editor = sharedpreferences.edit();
        editor.putString(Constanta.SESSION_ID_KURIR, kurir_id);
        editor.apply();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onRefresh() {
        laodDataKurur(kurir_id);
    }
}