package com.baadalletta.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.ResponseKurir;
import com.baadalletta.app.models.ResponsePhoto;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AkunActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private LinearLayout ll_edit;
    private CardView cv_edit_password;
    private CardView cv_history;
    private CardView cv_logout;
    private CardView cv_verifed;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Kurir kurir;
    private String kurir_id;

    private TextView tv_nama;
    private TextView tv_telpon;
    private TextView tv_status;

    private SwipeRefreshLayout swipe_continer;

    private ImageView img_profile;
    private static final String TAG = AkunActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    private String foto_profil;

    private boolean isPhotoProfileReady = true;

    private TextView tv_status_verived;
    private boolean isVerifed = false;


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

        img_profile = findViewById(R.id.img_profile);
        tv_nama = findViewById(R.id.tv_nama);
        tv_telpon = findViewById(R.id.tv_telpon);
        tv_status = findViewById(R.id.tv_status);

        tv_status_verived = findViewById(R.id.tv_status_verived);
        ll_edit = findViewById(R.id.ll_edit);
        cv_edit_password = findViewById(R.id.cv_edit_password);
        cv_history = findViewById(R.id.cv_history);
        cv_logout = findViewById(R.id.cv_logout);
        cv_verifed = findViewById(R.id.cv_verifed);

        cv_verifed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isVerifed) {
                    if (isPhotoProfileReady) {
                        if (kurir != null) {
                            Intent intent = new Intent(AkunActivity.this, VerifikasiActivity.class);
                            intent.putExtra("kurir_intent", kurir);
                            startActivity(intent);
                        }
                    } else {
                        new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Maaf..")
                                .setContentText("Pasang foto profil terlebih dahulu.")
                                .show();
                    }
                }
            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(AkunActivity.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

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

        ImagePickerActivity.clearCache(this);

    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(AkunActivity.this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }

            @Override
            public void onViewImage() {
                launchViewImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    startUpdatePhoto(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startUpdatePhoto(Uri uri) {

        SweetAlertDialog pDialog = new SweetAlertDialog(AkunActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        File file = new File(uri.getPath());
        RequestBody foto = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part foto_pesanan_send = MultipartBody.Part.createFormData("foto", file.getName(), foto);
        RequestBody type_send = RequestBody.create(MediaType.parse("text/plain"), "foto_profil");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePhoto> responsePhotoCall = apiInterface.updatePhotoProfil(kurir_id, foto_pesanan_send, type_send);
        responsePhotoCall.enqueue(new Callback<ResponsePhoto>() {
            @Override
            public void onResponse(Call<ResponsePhoto> call, Response<ResponsePhoto> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    String pesan = response.body().getMessage();
                    if (kode.equals("200")) {
//                        Toast.makeText(DetailPesananActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        laodDataKurur(kurir_id);
                    } else {
                        Toast.makeText(AkunActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(AkunActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePhoto> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(AkunActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void launchViewImage() {
//        Toast.makeText(getActivity(), "Lihat Gambar!!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(AkunActivity.this, PreviewPhotoProfilActivity.class);
        intent.putExtra("foto", foto_profil);
        startActivity(intent);
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(AkunActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(AkunActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(AkunActivity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
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

        String foto = kurir.getFoto();
        foto_profil = Constanta.URL_PHOTO_KURIR + foto;

        String status_verif = kurir.getStatus_verifikasi();
        if (status_verif.equals("verifikasi")) {
            isVerifed = true;
            tv_status_verived.setText("Sudah");
            tv_status_verived.setTextColor(ContextCompat.getColor(this, R.color.doneText));
        } else {
            isVerifed = false;
            tv_status_verived.setText("Belum");
            tv_status_verived.setTextColor(ContextCompat.getColor(this, R.color.cancelText));
        }

        if (foto == null || foto.equals("")) {

            Glide.with(this)
                    .load(R.drawable.foto_default)
                    .into(img_profile);
            isPhotoProfileReady = false;
        } else {
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(AkunActivity.this)
                    .load(Constanta.URL_PHOTO_KURIR + foto)
                    .apply(options)
                    .into(img_profile);
            isPhotoProfileReady = true;
        }


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