package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.ResponseKurir;
import com.baadalletta.app.models.ResponseLogin;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout rl_masuk;
    private EditText et_telpon;
    private EditText et_password;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private String kurir_id;
    private Kurir kurir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES,
                MODE_PRIVATE);
        String id_kurur = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");
        if (!id_kurur.isEmpty()) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        et_telpon = findViewById(R.id.et_telpon);
        et_password = findViewById(R.id.et_password);
        rl_masuk = findViewById(R.id.rl_masuk);
        rl_masuk.setOnClickListener(this::clickMasuk);
    }

    private void clickMasuk(View view) {

        et_telpon.setError(null);
        et_password.setError(null);

        String telpon = et_telpon.getText().toString();
        String password = et_password.getText().toString();

        if (telpon.equals("") || telpon.isEmpty()) {
            et_telpon.setError("Lengkapi");
        } else if (password.equals("") || password.isEmpty()) {
            et_password.setError("Lengkapi");
        } else {
            loadLogin(telpon, password);
        }
    }

    private void loadLogin(String telpon, String password) {

        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseLogin> responseLoginCall = apiInterface.postLogin(telpon, password);
        responseLoginCall.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                pDialog.dismiss();

                if (response.isSuccessful()) {

                    String kode = String.valueOf(response.body().getStatus_code());
                    String message = response.body().getMessage();
                    String kurir_id_get = String.valueOf(response.body().getId_kurir());
                    kurir_id = kurir_id_get;
                    if (kode.equals("200")) {
//                        Toast.makeText(LoginActivity.this, kode, Toast.LENGTH_SHORT).show();
                        laodDataKurur(kurir_id_get);
                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Opss..")
                                .setContentText(message)
                                .show();
                    }
                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Mohon Maaf.")
                            .setContentText("Terjadi Kesalahan Sistem")
                            .show();
                }
            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Mohon Maaf.")
                        .setContentText(t.getMessage())
                        .show();

            }
        });
    }

    private void laodDataKurur(String kurir_id_send) {

        SweetAlertDialog pDialog = new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                if (response.isSuccessful()) {
                    String message = response.body().getMessage();
                    String status_code = String.valueOf(response.body().getStatus_code());
                    if (status_code.equals("200")) {
                        kurir = response.body().getData();
                        prosesCekData(kurir);
                    } else {
                        new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Opss..")
                                .setContentText(message)
                                .show();
                    }

                } else {
                    new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Mohon Maaf.")
                            .setContentText("Terjadi Kesalahan Sistem")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ResponseKurir> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Mohon Maaf.")
                        .setContentText("Terjadi Kesalahan Sistem")
                        .show();
            }
        });


    }

    private void prosesCekData(Kurir kurir) {

        String status_akun = kurir.getStatus_akun();
        String kurir_id_session = String.valueOf(kurir.getId());
        if (status_akun.equals("active")) {
            startSessionSave(kurir_id_session);
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else {
            SweetAlertDialog sweetAlertDialogError = new SweetAlertDialog(LoginActivity.this,
                    SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialogError.setTitleText("Opss..");
            sweetAlertDialogError.setCancelable(false);
            sweetAlertDialogError.setContentText("Akun ini telah di suspend!");
            sweetAlertDialogError.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
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
}