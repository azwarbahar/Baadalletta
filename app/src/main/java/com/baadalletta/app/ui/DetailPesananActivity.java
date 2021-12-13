package com.baadalletta.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.baadalletta.app.BuildConfig;
import com.baadalletta.app.R;
import com.baadalletta.app.directionhelper.FetchURL;
import com.baadalletta.app.directionhelper.TaskLoadedCallback;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.models.ResponsePhoto;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPesananActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnPolylineClickListener,
        TaskLoadedCallback {

    private GoogleMap map;
    private Polyline currentPolyline;

    private Drawable vectorDrawble;
    private Bitmap bitmap;

    private Marker baadaletta_marker;
    List<Marker> marker_list = new ArrayList<Marker>();

    private LatLngBounds.Builder builder = new LatLngBounds.Builder();

    private Pesanan pesanan_intent;
    private Customer customer;
    private TextView tv_alamat;
    private TextView tv_jarak;
    private TextView tv_waktu;
    private TextView tv_kode;
    private TextView tv_nama;
    private TextView tv_whatsapp;

    private ImageView img_foto;
    private ImageView img_whatsapp;

    private RelativeLayout rl_mulai;
    private ImageView img_mulai;
    private TextView tv_mulai;

    private RelativeLayout continer_dialog;
    private LinearLayout ll_foto;
    private LinearLayout ll_batal;

    private Bitmap bitmap_bukti_tindakan;

    private String pesanan_id;

    private boolean delivery_status = false;

    private static final String TAG = DetailPesananActivity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;

    private Bitmap bitmap_foto_pengantaran;

    private ArrayList<Pesanan> pesanans_delivery;
    private boolean isDelivery_ready = false;


    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private Kurir kurir;
    private String kurir_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pesanan);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        kurir_id = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        // dialog
        continer_dialog = findViewById(R.id.continer_dialog);
        continer_dialog.setVisibility(View.GONE);
        ll_foto = findViewById(R.id.ll_foto);
        ll_batal = findViewById(R.id.ll_batal);

        tv_alamat = findViewById(R.id.tv_alamat);
        tv_jarak = findViewById(R.id.tv_jarak);
        tv_waktu = findViewById(R.id.tv_waktu);
        tv_kode = findViewById(R.id.tv_kode);
        tv_nama = findViewById(R.id.tv_nama);
        tv_whatsapp = findViewById(R.id.tv_whatsapp);
        img_foto = findViewById(R.id.img_foto);
        img_whatsapp = findViewById(R.id.img_whatsapp);
        rl_mulai = findViewById(R.id.rl_mulai);
        img_mulai = findViewById(R.id.img_mulai);
        tv_mulai = findViewById(R.id.tv_mulai);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        pesanan_intent = getIntent().getParcelableExtra("Extra_data");

        if (pesanan_intent == null) {
            Toast.makeText(this, "Data Tidak Ada", Toast.LENGTH_SHORT).show();
        } else {
            initDataIntent(pesanan_intent);
        }

        img_foto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DetailPesananActivity.this, PreviewPhotoActivity.class));
            }
        });

        ll_batal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continer_dialog.setVisibility(View.GONE);
            }
        });
        rl_mulai.setOnClickListener(this::clickMulai);
        ll_foto.setOnClickListener(this::clickCamera);

        img_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Pesan WhatsApp")
                        .setContentText("Ingin Mengirim Pesan WhatsApp ?")
                        .setConfirmText("Ya")
                        .setCancelText("Batal")
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
//                                try {
                                // Check if whatsapp is installed
                                String number_whatsapp_send;
                                String number_whatsapp = customer.getWhatsapp();
                                String number_whatsapp_first = number_whatsapp.substring(0, 1);
                                if (number_whatsapp_first.equals("0")) {
                                    number_whatsapp_send = "62" + number_whatsapp.substring(1);
                                } else {
                                    number_whatsapp_send = number_whatsapp;
                                }
//                                    getPackageManager().getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/" + number_whatsapp_send));
                                startActivity(intent);
//                                } catch (PackageManager.NameNotFoundException e) {
//                                    Toast.makeText(DetailPesananActivity.this, "WhatsApp not Installed", Toast.LENGTH_SHORT).show();
//                                }
                            }
                        })
                        .show();
            }
        });

    }

    private void clickCamera(View view) {
        continer_dialog.setVisibility(View.GONE);
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
//                            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//                            File file = new File(Environment.getExternalStorageDirectory(),
//                                    "/MTR_Tamalate/Laporan Petugas/photo_" + timeStamp + ".png");
//                            imageUri = Uri.fromFile(file);

//                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
//                            startActivityForResult(intent, 0);

                            Intent intent = new Intent(DetailPesananActivity.this, ImagePengantaranPickerActivity.class);
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePengantaranPickerActivity.REQUEST_IMAGE_CAPTURE);

                            // setting aspect ratio
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_ASPECT_RATIO_Y, 1);

                            // setting maximum bitmap width and height
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
                            intent.putExtra(ImagePengantaranPickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

                            startActivityForResult(intent, REQUEST_IMAGE);


//                            Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                            startActivityForResult(takePicture, 0);

                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    bitmap_foto_pengantaran = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    startUpdatePhoto(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void startUpdatePhoto(Uri uri) {

        SweetAlertDialog pDialog = new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        File file = new File(uri.getPath());
        RequestBody foto = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part foto_pesanan_send = MultipartBody.Part.createFormData("foto", file.getName(), foto);
        String pesanan_id_send = pesanan_id;
        RequestBody type_send = RequestBody.create(MediaType.parse("text/plain"), "foto_pengantaran");

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePhoto> responsePhotoCall = apiInterface.updatePhoto(pesanan_id_send, foto_pesanan_send, type_send);
        responsePhotoCall.enqueue(new Callback<ResponsePhoto>() {
            @Override
            public void onResponse(Call<ResponsePhoto> call, Response<ResponsePhoto> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    String pesan = response.body().getMessage();
                    if (kode.equals("200")) {
//                        Toast.makeText(DetailPesananActivity.this, pesan, Toast.LENGTH_SHORT).show();
                        updateStatus("done");

                    } else {
                        Toast.makeText(DetailPesananActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }
                } else {

                    Toast.makeText(DetailPesananActivity.this, "gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePhoto> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(DetailPesananActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });


    }

    private void updateStatus(String status_pesanan) {

        SweetAlertDialog pDialog = new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#0187C6"));
        pDialog.setTitleText("Update Pesanan..");
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePhoto> responseUpdateStatusCall = apiInterface.updateStatusPesanan(pesanan_id, status_pesanan);
        responseUpdateStatusCall.enqueue(new Callback<ResponsePhoto>() {
            @Override
            public void onResponse(Call<ResponsePhoto> call, Response<ResponsePhoto> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    String pesan = response.body().getMessage();
                    if (kode.equals("200")) {
                        SweetAlertDialog success = new SweetAlertDialog(
                                DetailPesananActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                        success.setTitleText("Berhasil..");
                        success.setCancelable(false);
                        success.setContentText("Pengantaran Selesai.");
                        success.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismiss();
                                pDialog.dismiss();
                                if (status_pesanan.equals("delivery")) {
                                    Pesanan pesanan = response.body().getData();
                                    initDataIntent(pesanan);
                                } else {
                                    configIntent();
                                }
                            }
                        });
                        success.show();

                    } else {
                        Toast.makeText(DetailPesananActivity.this, pesan, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailPesananActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePhoto> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(DetailPesananActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void configIntent() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SweetAlertDialog pDialog = new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                pDialog.setTitleText("Menyimpan Data..");
                pDialog.setCancelable(true);
                pDialog.show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pDialog.dismiss();
                        finish();
                    }
                }, 2500);
            }
        }, 500);


    }

    private void showSettingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    private void clickMulai(View view) {

        if (delivery_status) {
            continer_dialog.setVisibility(View.VISIBLE);
        } else {
            if (isDelivery_ready) {
                new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Tidak Bisa.")
                        .setContentText("Anda sedang dalam pengantaran!")
                        .show();

            } else {
                SweetAlertDialog success = new SweetAlertDialog(
                        DetailPesananActivity.this, SweetAlertDialog.WARNING_TYPE);
                success.setTitleText("Mulai");
                success.setCancelable(false);
                success.setContentText("Ingin Memulai Pengantaran.");
                success.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        updateStatus("delivery");
                    }
                });
                success.setCancelButton("Batal", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                    }
                });
                success.show();

            }
        }

    }


    private void checkPesananDelivery(String kurir_id) {

        SweetAlertDialog pDialog = new SweetAlertDialog(DetailPesananActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePesananKurir> responsePesananKurirCall = apiInterface.getPesananMaps(kurir_id, "delivery");
        responsePesananKurirCall.enqueue(new Callback<ResponsePesananKurir>() {
            @Override
            public void onResponse(Call<ResponsePesananKurir> call, Response<ResponsePesananKurir> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    int status_code = response.body().getStatus_code();
                    if (status_code == 200) {
                        pesanans_delivery = (ArrayList<Pesanan>) response.body().getData();
                        if (pesanans_delivery.size() > 0) {
                            isDelivery_ready = true;
                            tv_mulai.setText("Mulai");
                            img_mulai.setImageResource(R.drawable.ic_baseline_navigation_24);
                            rl_mulai.setBackground(ContextCompat.getDrawable(DetailPesananActivity.this,
                                    R.drawable.bg_mulai_disable_btn));
                        } else {
                            isDelivery_ready = false;
                            tv_mulai.setText("Mulai");
                            img_mulai.setImageResource(R.drawable.ic_baseline_navigation_24);
                            rl_mulai.setBackground(ContextCompat.getDrawable(DetailPesananActivity.this, R.drawable.bg_mulai_btn));

                        }
                    } else {
                        isDelivery_ready = false;
                        tv_mulai.setText("Mulai");
                        img_mulai.setImageResource(R.drawable.ic_baseline_navigation_24);
                        rl_mulai.setBackground(ContextCompat.getDrawable(DetailPesananActivity.this, R.drawable.bg_mulai_btn));

                    }
                } else {
                    isDelivery_ready = false;
                    tv_mulai.setText("Mulai");
                    img_mulai.setImageResource(R.drawable.ic_baseline_navigation_24);
                    rl_mulai.setBackground(ContextCompat.getDrawable(DetailPesananActivity.this, R.drawable.bg_mulai_btn));

                }
            }

            @Override
            public void onFailure(Call<ResponsePesananKurir> call, Throwable t) {
                pDialog.dismiss();
                isDelivery_ready = false;
                tv_mulai.setText("Mulai");
                img_mulai.setImageResource(R.drawable.ic_baseline_navigation_24);
                rl_mulai.setBackground(ContextCompat.getDrawable(DetailPesananActivity.this, R.drawable.bg_mulai_btn));

            }
        });

    }

    private void initDataIntent(Pesanan pesanan_intent) {

        pesanan_id = String.valueOf(pesanan_intent.getId());

        String customer_id = String.valueOf(pesanan_intent.getId_customer());

        //set status pesanan
        String status_pesanan = pesanan_intent.getStatus_pesanan();
        if (status_pesanan.equals("delivery")) {
            tv_mulai.setText("Sampai");
            delivery_status = true;
            img_mulai.setImageResource(R.drawable.ic_baseline_done_24);
            rl_mulai.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_mulai_primary_dark_btn));


        } else if (status_pesanan.equals("proses")) {
            checkPesananDelivery(kurir_id);
        }


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
        responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
            @Override
            public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {

                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {

                        customer = response.body().getData();
                        String alamat = customer.getAlamat();
                        String whatsapp = "Whatsapp : " + customer.getWhatsapp();
                        String kode_pelanggan = "Kode : " + customer.getKode();
                        String nama = "Nama : " + customer.getNama();

                        String latitude_customer = customer.getLatitude();
                        String longitude_customer = customer.getLongitude();
                        initMaps(latitude_customer, longitude_customer);

                        tv_alamat.setText(alamat);
                        tv_whatsapp.setText(whatsapp);
                        tv_kode.setText(kode_pelanggan);
                        tv_nama.setText(nama);
                    } else {
                        Toast.makeText(DetailPesananActivity.this, "Data Customer tida ada", Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onFailure(Call<ResponsCustomer> call, Throwable t) {

            }
        });

    }

    private void initMaps(String latitude_customer, String longitude_customer) {

        double latitude_pelanggan = Double.parseDouble(latitude_customer);
        double longitude_pelanggan = Double.parseDouble(longitude_customer);
        Marker marker = map.addMarker(new MarkerOptions().title("Rumah Pelanggan")
                .icon(bitmapDescriptor(DetailPesananActivity.this))
                .position(new LatLng(latitude_pelanggan, longitude_pelanggan)));
        marker_list.add(marker);
        builder.include(marker.getPosition());
//        polyOptions.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
//        String idmark = marker.getId();
//        position_hashap.add(idmark);
//        markerMapPesanan.put(idmark, pesananArrayList.get(finalNumber_marker));

        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels + 1;
        int padding = (int) (width * 0.40); // offset from edges of the map 10% of screen


        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        map.moveCamera(cu);

        new FetchURL(DetailPesananActivity.this).execute(getUrl(marker_list.get(0).getPosition(),
                marker_list.get(1).getPosition(), "driving"), "driving");
    }

    private BitmapDescriptor bitmapDescriptor(Context context) {
        int height = 80;
        int width = 55;
        vectorDrawble = ContextCompat.getDrawable(context, R.drawable.img_marker);
        assert vectorDrawble != null;
        vectorDrawble.setBounds(0, 0, width, height);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawble.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private BitmapDescriptor bitmapDescriptorBaadalletta(Context context) {
        int height = 60;
        int width = 35;
        vectorDrawble = ContextCompat.getDrawable(context, R.drawable.marker_baadalletta);
        assert vectorDrawble != null;
        vectorDrawble.setBounds(0, 0, width, height);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawble.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setPadding(0, 25, 0, 14);

        double latitude_baadalletta = Double.parseDouble(Constanta.LATITUDE_BAADALLETTA);
        double longitude_baadalletta = Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA);
        baadaletta_marker = map.addMarker(new MarkerOptions().icon(bitmapDescriptorBaadalletta(this))
                .position(new LatLng(latitude_baadalletta, longitude_baadalletta)));
        builder.include(baadaletta_marker.getPosition());
        marker_list.add(0, baadaletta_marker);

        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style_grey));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }

    }

    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + BuildConfig.API_KEY_MAPS;
        return url;
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }
}