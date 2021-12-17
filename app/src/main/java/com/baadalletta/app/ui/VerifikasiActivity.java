package com.baadalletta.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.baadalletta.app.R;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.ResponseLogin;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
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

public class VerifikasiActivity extends AppCompatActivity {

    private RelativeLayout rl_simpan;

    private RelativeLayout rl_ktp;
    private RelativeLayout rl_sim;

    private LinearLayout ll_illust_ktp;
    private LinearLayout ll_illust_sim;

    private ImageView img_ktp;
    private ImageView img_sim;

    private Uri uri_ktp;
    private Uri uri_sim;

    private boolean isReadyKtp = false;
    private boolean isReadySim = false;

    private Bitmap bitmap_ktp;
    private Bitmap bitmap_sim;

    private static final String TAG = VerifikasiActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE_KTP = 100;
    public static final int REQUEST_IMAGE_SIM = 200;

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Kurir kurir;
    private String kurir_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifikasi);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        kurir_id = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");

        kurir = getIntent().getExtras().getParcelable("kurir_intent");

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        rl_simpan = findViewById(R.id.rl_simpan);

        rl_ktp = findViewById(R.id.rl_ktp);
        rl_sim = findViewById(R.id.rl_sim);

        ll_illust_ktp = findViewById(R.id.ll_illust_ktp);
        ll_illust_sim = findViewById(R.id.ll_illust_sim);

        img_ktp = findViewById(R.id.img_ktp);
        img_sim = findViewById(R.id.img_sim);

        if (kurir != null) {
            laodData(kurir);
        }

        rl_ktp.setOnClickListener(this::clickKtp);
        rl_sim.setOnClickListener(this::clickSim);
        rl_simpan.setOnClickListener(this::clickSimpan);

    }

    private void laodData(Kurir kurir) {

        String f_ktp = kurir.getFoto_ktp();
        String f_sim = kurir.getFoto_sim();

        if (f_ktp == null || f_ktp.isEmpty()) {
            isReadyKtp = false;
            ll_illust_ktp.setVisibility(View.VISIBLE);
        } else {
            isReadyKtp = true;
            ll_illust_ktp.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(VerifikasiActivity.this)
                    .load(Constanta.URL_PHOTO_KTP_KURIR + f_ktp)
                    .apply(options)
                    .into(img_ktp);
        }

        if (f_sim == null || f_sim.isEmpty()) {
            isReadySim = false;
            ll_illust_sim.setVisibility(View.VISIBLE);
        } else {
            isReadySim = true;
            ll_illust_sim.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.ic_broken_image)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH);

            Glide.with(VerifikasiActivity.this)
                    .load(Constanta.URL_PHOTO_SIM_KURIR + f_sim)
                    .apply(options)
                    .into(img_sim);
        }

    }

    private void clickSimpan(View view) {

        if (uri_ktp == null) {
            new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Maaf..")
                    .setContentText("Foto KTP Belum Tersedia!")
                    .show();

        } else if (uri_sim == null) {
            new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Maaf..")
                    .setContentText("Foto SIM Belum Tersedia!")
                    .show();
        } else {
            startUploadPhoto(uri_ktp, uri_sim);
        }

    }

    private void startUploadPhoto(Uri uri_ktp, Uri uri_sim) {

        SweetAlertDialog pDialog = new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        File file_ktp = new File(uri_ktp.getPath());
        File file_sim = new File(uri_sim.getPath());
        RequestBody foto_ktp = RequestBody.create(MediaType.parse("image/*"), file_ktp);
        RequestBody foto_sim = RequestBody.create(MediaType.parse("image/*"), file_sim);
        MultipartBody.Part foto_ktp_send = MultipartBody.Part.createFormData("foto_ktp", file_ktp.getName(), foto_ktp);
        MultipartBody.Part foto_sim_send = MultipartBody.Part.createFormData("foto_sim", file_sim.getName(), foto_sim);

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseLogin> responseLoginCall = apiInterface.updatePhotoVerifikasi(kurir_id, foto_ktp_send, foto_sim_send);
        responseLoginCall.enqueue(new Callback<ResponseLogin>() {
            @Override
            public void onResponse(Call<ResponseLogin> call, Response<ResponseLogin> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    String pesan = response.body().getMessage();
                    if (kode.equals("200")) {
                        SweetAlertDialog success = new SweetAlertDialog(
                                VerifikasiActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setTitleText("Dokumen Terkirim");
                        success.setCancelable(false);
                        success.setContentText("Akun anda segera terverifikasi");
                        success.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                ImagePickerActivity.clearCache(VerifikasiActivity.this);
                                SweetAlertDialog pDialog = new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                                pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
                                pDialog.setTitleText("Menyimpan Data..");
                                pDialog.setCancelable(false);
                                pDialog.show();
//
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pDialog.dismiss();
                                        finish();
                                    }
                                }, 1000);
                            }
                        });
                        success.show();

                    } else {
                        new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Maaf..")
                                .setContentText(pesan)
                                .show();
                    }

                } else {
                    new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Maaf..")
                            .setContentText("Terjadi Kesalahan Sistem!")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ResponseLogin> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Maaf..")
                        .setContentText(t.getMessage())
                        .show();

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_KTP) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    bitmap_ktp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    uri_ktp = uri;
                    setImage(bitmap_ktp, REQUEST_IMAGE_KTP);
//                    startUpdatePhoto(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == REQUEST_IMAGE_SIM) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    bitmap_sim = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    uri_sim = uri;
                    setImage(bitmap_sim, REQUEST_IMAGE_SIM);
//                    startUpdatePhoto(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void setImage(Bitmap bitmap, int requestImage) {

        if (requestImage == REQUEST_IMAGE_KTP) {
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            img_ktp.setImageBitmap(resource);
                        }
                    });
            ll_illust_ktp.setVisibility(View.GONE);
        } else if (requestImage == REQUEST_IMAGE_SIM) {
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            img_sim.setImageBitmap(resource);
                        }
                    });
            ll_illust_sim.setVisibility(View.GONE);
        }
    }

    private void clickSim(View view) {
        ImagePickerActivity.clearCache(VerifikasiActivity.this);
        Dexter.withActivity(VerifikasiActivity.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions(REQUEST_IMAGE_SIM);
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

    private void clickKtp(View view) {
        ImagePickerActivity.clearCache(VerifikasiActivity.this);
        Dexter.withActivity(VerifikasiActivity.this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions(REQUEST_IMAGE_KTP);
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

    private void showImagePickerOptions(int i) {
        ImagePickerActivity.showImagePickerOptions(VerifikasiActivity.this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent(i);
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent(i);
            }

            @Override
            public void onViewImage() {
                launchViewImage(i);
            }
        });
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(VerifikasiActivity.this);
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

    private void launchViewImage(int i) {

        if (i == REQUEST_IMAGE_KTP) {
            if (uri_ktp == null && !isReadyKtp) {
                new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Maaf..")
                        .setContentText("Gambar KTP Belum Tersedia.")
                        .show();
            } else {
                if (uri_ktp != null) {
                    Intent intent = new Intent(VerifikasiActivity.this, PreviewPhotoBitmapActivity.class);
                    intent.putExtra("foto", uri_ktp.toString());
                    startActivity(intent);
                } else {
                    String f_ktp = kurir.getFoto_ktp();
                    Intent intent = new Intent(VerifikasiActivity.this, PreviewPhotoProfilActivity.class);
                    intent.putExtra("foto", Constanta.URL_PHOTO_KTP_KURIR + f_ktp);
                    startActivity(intent);
                }
            }
        } else if (i == REQUEST_IMAGE_SIM) {
            if (uri_sim == null && !isReadySim) {
                new SweetAlertDialog(VerifikasiActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Maaf..")
                        .setContentText("Gambar SIM Belum Tersedia.")
                        .show();
            } else {
                if (uri_sim != null) {
                    Intent intent = new Intent(VerifikasiActivity.this, PreviewPhotoBitmapActivity.class);
                    intent.putExtra("foto", uri_sim.toString());
                    startActivity(intent);
                } else {
                    String f_sim = kurir.getFoto_sim();
                    Intent intent = new Intent(VerifikasiActivity.this, PreviewPhotoProfilActivity.class);
                    intent.putExtra("foto", Constanta.URL_PHOTO_SIM_KURIR + f_sim);
                    startActivity(intent);
                }
            }
        }
    }

    private void launchCameraIntent(int i) {
        Intent intent = new Intent(VerifikasiActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, i);
    }

    private void launchGalleryIntent(int i) {
        Intent intent = new Intent(VerifikasiActivity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, i);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}