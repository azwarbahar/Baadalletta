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
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.baadalletta.app.BuildConfig;
import com.baadalletta.app.R;
import com.baadalletta.app.SortPlaces;
import com.baadalletta.app.adapter.PesananHomeAdapter;
import com.baadalletta.app.directionhelper.TaskLoadedCallback;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Kurir;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.Place;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.ResponsPesanan;
import com.baadalletta.app.models.ResponseKurir;
import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.models.maps.distance.Distance;
import com.baadalletta.app.models.maps.distance.Duration;
import com.baadalletta.app.models.maps.distance.ElementsItem;
import com.baadalletta.app.models.maps.distance.ResponseDistanceMaps;
import com.baadalletta.app.models.maps.distance.RowsItem;
import com.baadalletta.app.network.ApiClient;
import com.baadalletta.app.network.ApiClientMaps;
import com.baadalletta.app.network.ApiInterface;
import com.baadalletta.app.utils.Constanta;
import com.blikoon.qrcodescanner.QrCodeActivity;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener,
        GoogleMap.OnPolylineClickListener,
        TaskLoadedCallback,
        RoutingListener,
        PesananHomeAdapter.PesananListRecyclerClickListener {

    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    private PolylineOptions polyOptions = new PolylineOptions();
    Polyline polylineOptions_final = null;
    private ArrayList<Pesanan> pesanans_diatur = new ArrayList<>();
    private GoogleMap map;
    private Polyline currentPolyline;

    private LatLng latling_baadalletta;
    private Marker marker_baadaletta;

    private ImageView img_user;
    private ImageView img_qr_code;

    private Drawable vectorDrawble;
    private Bitmap bitmap;

    private SlidingUpPanelLayout sliding_layout;

    private RecyclerView rv_pesanan_home;
    private PesananHomeAdapter pesananHomeAdapter;

    private LinearLayout ll_power;
    private TextView tv_power;
    private ImageView img_power;

    private TextView tv_kode;

    private SweetAlertDialog pDialog;

    private ArrayList<Pesanan> pesananArrayList;

    private LatLngBounds.Builder builder = new LatLngBounds.Builder();
    private LatLngBounds.Builder builder_item = new LatLngBounds.Builder();

    List<Marker> marker_list = new ArrayList<Marker>();
    HashMap<String, Pesanan> markerMapPesanan = new HashMap<String, Pesanan>();
    List<String> position_hashap = new ArrayList<String>();
    List<LatLng> latLngList = new ArrayList<LatLng>();
    private List<Polyline> polylines = null;

    // IMG MARKER
    int[] img_marker = {R.drawable.img_marker_1, R.drawable.img_marker_2, R.drawable.img_marker_3,
            R.drawable.img_marker_4, R.drawable.img_marker_5, R.drawable.img_marker_6,
            R.drawable.img_marker_7, R.drawable.img_marker_8, R.drawable.img_marker_9,
            R.drawable.img_marker_10, R.drawable.img_marker_11, R.drawable.img_marker_12,
            R.drawable.img_marker_13, R.drawable.img_marker_14, R.drawable.img_marker_15};

    private Animation slide_up;
    private Animation slide_down;

    private ImageView img_kosong;
    private CardView cv_slide_up;
    //    private TextView tv_kode;
    private TextView tv_jarak;
    private TextView tv_waktu;
    private RelativeLayout rl_lihat;
    private String selected_marker;

    private ImageView img_back_slide_up;
    private ImageView btn_jenis_map;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private View dialogView;

    private LinearLayout ll_refresh;

    private Kurir kurir;
    private String kurir_id;
    private String status_aski;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedpreferences = getApplicationContext().getSharedPreferences(Constanta.MY_SHARED_PREFERENCES, MODE_PRIVATE);
        kurir_id = sharedpreferences.getString(Constanta.SESSION_ID_KURIR, "");

        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        tv_kode = findViewById(R.id.tv_kode);
        tv_jarak = findViewById(R.id.tv_jarak);
        tv_waktu = findViewById(R.id.tv_waktu);
        rl_lihat = findViewById(R.id.rl_lihat);

        ll_refresh = findViewById(R.id.ll_refresh);
        btn_jenis_map = findViewById(R.id.btn_jenis_map);
        img_back_slide_up = findViewById(R.id.img_back_slide_up);
        cv_slide_up = findViewById(R.id.cv_slide_up);
        cv_slide_up.setVisibility(View.GONE);

        img_power = findViewById(R.id.img_power);
        tv_power = findViewById(R.id.tv_power);
        ll_power = findViewById(R.id.ll_power);

        img_kosong = findViewById(R.id.img_kosong);
        rv_pesanan_home = findViewById(R.id.rv_pesanan_home);
        rv_pesanan_home.setVisibility(View.GONE);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        img_back_slide_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cv_slide_up.startAnimation(slide_up);
                cv_slide_up.setVisibility(View.GONE);
            }
        });

        ll_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                mapFragment.getMapAsync(HomeActivity.this);
                laodDataPesanan(kurir_id);
                laodDataKurur(kurir_id);
            }
        });

        ll_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (pesananArrayList.size() > 0) {
                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.WARNING_TYPE)
                            .setTitleText("Opss..")
                            .setContentText("Selesaikan pengantaran anda jika ingin Offline")
                            .show();
                } else {
                    String status_power = tv_power.getText().toString();
                    updateStatusAksi(status_power);
                }

            }
        });

        img_qr_code = findViewById(R.id.img_qr_code);
        img_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_power = tv_power.getText().toString();
                if (status_power.equals("Hidup")) {

                    Dexter.withContext(getApplicationContext())
                            .withPermission(Manifest.permission.CAMERA)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                    Intent intent = new Intent(HomeActivity.this, QrCodeActivity.class);
                                    startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                                    permissionDeniedResponse.getRequestedPermission();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                    permissionToken.continuePermissionRequest();
                                }
                            }).check();
                } else {

                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Opss..")
                            .setContentText("Sedang Mode Offline")
                            .show();

                }
            }
        });

        img_user = findViewById(R.id.img_user);
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
            }
        });

        rl_lihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selected_marker != null) {

//                    Toast.makeText(HomeActivity.this, selected_marker, Toast.LENGTH_SHORT).show();
                    for (int a = 0; a < pesananArrayList.size(); a++) {
                        String id = String.valueOf(pesananArrayList.get(a).getId());
                        if (id.equals(selected_marker)) {
                            Intent intent = new Intent(HomeActivity.this, DetailPesananActivity.class);
                            intent.putExtra("Extra_data", pesananArrayList.get(a));
                            startActivity(intent);
                            break;
                        } else {
//                            Toast.makeText(HomeActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
//                    Toast.makeText(HomeActivity.this, "Terjadi Kesalahan", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btn_jenis_map.setOnClickListener(this::clickjenisMap);

        laodDataPesanan(kurir_id);
        laodDataKurur(kurir_id);

    }

    private void updateStatusAksi(String status_power) {
        String send_status;
        if (status_power.equals("Hidup")) {
            send_status = "offline";
        } else {
            send_status = "online";
        }

        SweetAlertDialog pDialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseKurir> responseKurirCall = apiInterface.setStatusAksi(kurir_id, send_status);
        responseKurirCall.enqueue(new Callback<ResponseKurir>() {
            @Override
            public void onResponse(Call<ResponseKurir> call, Response<ResponseKurir> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {
                        laodDataKurur(kurir_id);
                        if (status_power.equals("Hidup")) {
                            Toast.makeText(HomeActivity.this, "Behasi mengganti Mode Offline", Toast.LENGTH_SHORT).show();
                            img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                                    R.color.black));
                            tv_power.setText("Mati");
                            tv_power.setTextColor(getResources().getColor(R.color.black));

                        } else {
                            Toast.makeText(HomeActivity.this, "Behasi mengganti Mode Online", Toast.LENGTH_SHORT).show();
                            img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                                    R.color.white));
                            tv_power.setText("Hidup");
                            tv_power.setTextColor(getResources().getColor(R.color.white));
                        }
                    } else {
                        Toast.makeText(HomeActivity.this, "Proses gagal1", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Proses gagal2", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<ResponseKurir> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Proses gagal", Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void laodDataKurur(String kurir_id_send) {

        SweetAlertDialog pDialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
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
                        checkStatusAksi(kurir);
                    } else {
                        new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Opss..")
                                .setContentText(message)
                                .show();
                    }

                } else {
                    new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Mohon Maaf.")
                            .setContentText("Terjadi Kesalahan Sistem")
                            .show();
                }

            }

            @Override
            public void onFailure(Call<ResponseKurir> call, Throwable t) {
                pDialog.dismiss();
                new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Mohon Maaf.")
                        .setContentText("Terjadi Kesalahan Sistem")
                        .show();
            }
        });


    }

    private void checkStatusAksi(Kurir kurir) {
        status_aski = kurir.getStatus_aksi();
        if (status_aski.equals("offline")) {
            img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                    R.color.black));
            tv_power.setText("Mati");
            tv_power.setTextColor(getResources().getColor(R.color.black));

        } else {
            img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                    R.color.white));
            tv_power.setText("Hidup");
            tv_power.setTextColor(getResources().getColor(R.color.white));
        }
    }

    private void prosesCekData(Kurir kurir) {

        String status_akun = kurir.getStatus_akun();
        String kurir_id_session = String.valueOf(kurir.getId());
        if (status_akun.equals("active")) {
            startSessionSave(kurir_id_session);
            Log.e("Kurir", "status : " + status_akun);
        } else {
            SweetAlertDialog sweetAlertDialogError = new SweetAlertDialog(HomeActivity.this,
                    SweetAlertDialog.ERROR_TYPE);
            sweetAlertDialogError.setTitleText("Opss..");
            sweetAlertDialogError.setCancelable(false);
            sweetAlertDialogError.setContentText("Akun ini telah di suspend!");
            sweetAlertDialogError.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != Activity.RESULT_OK) {
//            Toast.makeText(this, "COULD NOT GET A GOOD RESULT.", Toast.LENGTH_SHORT).show();
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {

                new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Scan Error..")
                        .setContentText("QR code tidak bisa discan")
                        .show();

            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null) {
                return;
            }

            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");

//            Toast.makeText(HomeActivity.this, "Resul = "+ result, Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
//
                    pDialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
                    pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
                    pDialog.setTitleText("Load Data..");
                    pDialog.setCancelable(false);
                    pDialog.show();
//
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pDialog.dismiss();
                            updatePesananScan(result);
                        }
                    }, 1500);

                }
            }, 300);
        }
    }

    private void updatePesananScan(String result) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsPesanan> responsPesananCall = apiInterface.setPesananKurir(result,
                "proses", Integer.parseInt(kurir_id));
        responsPesananCall.enqueue(new Callback<ResponsPesanan>() {
            @Override
            public void onResponse(Call<ResponsPesanan> call, Response<ResponsPesanan> response) {
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {
                        map.clear();
                        laodDataPesanan(kurir_id);
                        laodDataKurur(kurir_id);

                    } else {
                        Toast.makeText(HomeActivity.this, "Kode bukan 200, Pesan " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Tidak SUkses", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsPesanan> call, Throwable t) {
                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void clickjenisMap(View view) {

        AlertDialog.Builder dialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_jenis_maps, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setTitle("Jenis Maps");
        dialog.show();

        LinearLayout ll_maps_default = dialogView.findViewById(R.id.ll_maps_default);
        LinearLayout ll_maps_satelit = dialogView.findViewById(R.id.ll_maps_satelit);

        ImageView img_maps_default = dialogView.findViewById(R.id.img_maps_default);
        ImageView img_maps_satelit = dialogView.findViewById(R.id.img_maps_satelit);

        TextView tv_maps_default = dialogView.findViewById(R.id.tv_maps_default);
        TextView tv_maps_satelit = dialogView.findViewById(R.id.tv_maps_satelit);

        if (map.getMapType() == GoogleMap.MAP_TYPE_SATELLITE) {

            img_maps_satelit.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.bg_trans_merah));
            img_maps_satelit.setPadding(6, 6, 6, 6);
            tv_maps_satelit.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.ColorPrimaryDark));

            tv_maps_default.setBackground(null);
            tv_maps_default.setPadding(0, 0, 0, 0);
            tv_maps_default.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.grey));
        } else {
            img_maps_default.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.bg_trans_merah));
            img_maps_default.setPadding(6, 6, 6, 6);
            tv_maps_default.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.ColorPrimaryDark));

            img_maps_satelit.setBackground(null);
            img_maps_satelit.setPadding(0, 0, 0, 0);
            tv_maps_satelit.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.grey));
        }

        ll_maps_default.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setMapType(map.MAP_TYPE_NORMAL);
                img_maps_default.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.bg_trans_merah));
                img_maps_default.setPadding(6, 6, 6, 6);
                tv_maps_default.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.ColorPrimaryDark));

                img_maps_satelit.setBackground(null);
                img_maps_satelit.setPadding(0, 0, 0, 0);
                tv_maps_satelit.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.grey));

            }
        });

        ll_maps_satelit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map.setMapType(map.MAP_TYPE_SATELLITE);
                img_maps_satelit.setBackground(ContextCompat.getDrawable(HomeActivity.this, R.drawable.bg_trans_merah));
                img_maps_satelit.setPadding(6, 6, 6, 6);
                tv_maps_satelit.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.ColorPrimaryDark));

                img_maps_default.setBackground(null);
                img_maps_default.setPadding(0, 0, 0, 0);
                tv_maps_default.setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.grey));

            }
        });

    }

    private void laodDataPesanan(String kurir_id) {

        SweetAlertDialog pDialog = new SweetAlertDialog(HomeActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialog.setTitleText("Loading");
        pDialog.setCancelable(false);
        pDialog.show();


        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePesananKurir> responsePesananKurirCall = apiInterface.getPesananMaps(kurir_id, "proses");
        responsePesananKurirCall.enqueue(new Callback<ResponsePesananKurir>() {
            @Override
            public void onResponse(Call<ResponsePesananKurir> call, Response<ResponsePesananKurir> response) {
                pDialog.dismiss();
                if (response.isSuccessful()) {
                    int status_code = response.body().getStatus_code();
                    if (status_code == 200) {
                        pesananArrayList = (ArrayList<Pesanan>) response.body().getData();
                        if (pesananArrayList.isEmpty()) {
                            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA),
                                    Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA)), 12));
                            img_kosong.setVisibility(View.VISIBLE);
                            rv_pesanan_home.setVisibility(View.GONE);
//                            Toast.makeText(HomeActivity.this, "Kosong", Toast.LENGTH_SHORT).show();
                        } else {
                            img_kosong.setVisibility(View.GONE);
                            rv_pesanan_home.setVisibility(View.VISIBLE);
                            distanceSortingOrder(pesananArrayList);
                        }
                    } else {
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA),
                                Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA)), 12));
                        img_kosong.setVisibility(View.VISIBLE);
                        rv_pesanan_home.setVisibility(View.GONE);
                        Toast.makeText(HomeActivity.this, "Kode = " + status_code + " and Pesan : " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA),
                            Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA)), 12));
                    img_kosong.setVisibility(View.VISIBLE);
                    rv_pesanan_home.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "Gagal Load Data Pesanan", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePesananKurir> call, Throwable t) {
                pDialog.dismiss();
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA),
                        Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA)), 12));
                img_kosong.setVisibility(View.VISIBLE);
                rv_pesanan_home.setVisibility(View.GONE);
                Toast.makeText(HomeActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void distanceSortingOrder(ArrayList<Pesanan> pesananArrayList) {

        // jarak yang terdekat adari baadalletta ke a
        // setelah itu jarak yang terdekat dari a ke b
        // setelah itu jarak yang terdekat dari b ke c
        ArrayList<Place> places = new ArrayList<Place>();
        boolean first_loop = true;
        for (int x = 0; x < pesananArrayList.size(); x++) {
            String titik_pesanan = pesananArrayList.get(x).getTitik_koordinat();
            double latitude_pelanggan = Double.parseDouble(titik_pesanan.substring(0, titik_pesanan.lastIndexOf(",")));
            double longitude_pelanggan = Double.parseDouble(titik_pesanan.substring(titik_pesanan.lastIndexOf(",") + 1));
            places.add(new Place(String.valueOf(pesananArrayList.get(x).getId()),
                    new LatLng(latitude_pelanggan, longitude_pelanggan), pesananArrayList.get(x)));
        }

        String lat_ba = Constanta.LATITUDE_BAADALLETTA;
        String longi_ba = Constanta.LONGITUDE_BAADALLETTA;
        String latling_origin = lat_ba + "," + longi_ba;

        LatLng latLng2 = new LatLng(Double.parseDouble(lat_ba), Double.parseDouble(longi_ba));

        Collections.sort(places, new SortPlaces(latLng2));
        pesanans_diatur.clear();

        for (Place p : places) {
            pesanans_diatur.add(p.getPesanan());
            Log.i("Places after sorting", "Place: " + p.getId_pesanan());
            Toast.makeText(HomeActivity.this, p.getId_pesanan(), Toast.LENGTH_SHORT).show();
        }
        rv_pesanan_home.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
        pesananHomeAdapter = new PesananHomeAdapter(HomeActivity.this, pesanans_diatur,
                HomeActivity.this, places);
        rv_pesanan_home.setAdapter(pesananHomeAdapter);
        initMapsPesanan(pesanans_diatur);

    }

    private void clickMarkerPesanan(Marker marker) {

        if (markerMapPesanan.get(marker.getId()) != null) {
            Pesanan pesanan = markerMapPesanan.get(marker.getId());
            selected_marker = String.valueOf(pesanan.getId());
            builder_item.include(marker_baadaletta.getPosition());
            builder_item.include(marker.getPosition());

            LatLngBounds bounds1 = builder_item.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.16); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds1, width, height, padding);
            map.animateCamera(cu);
            for (int i = 0; i < marker_list.size(); i++) {
                if (position_hashap.get(i).equals(marker.getId())) {
//                    new FetchURL(HomeActivity.this).execute(getUrl(marker_list.get(0).getPosition(),
//                            marker_list.get(i + 1).getPosition(), "driving"), "driving");
                    break;
                }
            }

            ApiInterface apiInterface3 = ApiClient.getClient().create(ApiInterface.class);
            Call<ResponsCustomer> responsCustomerCall = apiInterface3.getCustomerId(String.valueOf(pesanan.getId_customer()));
            responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
                @Override
                public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {
                    Customer customer = response.body().getData();
                    tv_kode.setText("Kode : " + customer.getKode());
                }

                @Override
                public void onFailure(Call<ResponsCustomer> call, Throwable t) {

                }
            });

            String latling_distance = pesanan.getTitik_koordinat();
            String lat_ba = Constanta.LATITUDE_BAADALLETTA;
            String longi_ba = Constanta.LONGITUDE_BAADALLETTA;
            String latling_origin = lat_ba + "," + longi_ba;

            ApiInterface apiInterface2 = ApiClientMaps.getClient().create(ApiInterface.class);
            Call<ResponseDistanceMaps> responseDistanceMapsCall = apiInterface2.getDirectionMatrix(latling_origin,
                    latling_distance, BuildConfig.API_KEY_MAPS);
            responseDistanceMapsCall.enqueue(new Callback<ResponseDistanceMaps>() {
                @Override
                public void onResponse(Call<ResponseDistanceMaps> call, Response<ResponseDistanceMaps> response) {

                    String status = response.body().getStatus();
                    if (status.equals("OK")) {
                        List<RowsItem> rowsItem = response.body().getRows();
                        for (int a = 0; a < rowsItem.size(); a++) {
                            List<ElementsItem> elementsItem = rowsItem.get(a).getElements();
                            if (elementsItem.get(a).getStatus().equals("OK")) {
                                for (int b = 0; b < elementsItem.size(); b++) {

                                    Distance distance = elementsItem.get(b).getDistance();
                                    String jarak = distance.getText();
                                    tv_jarak.setText("Jarak : " + jarak);


                                    Duration duration = elementsItem.get(b).getDuration();
                                    String waktu = duration.getText();
                                    tv_waktu.setText("Waktu : " + waktu);
                                }
                            }
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponseDistanceMaps> call, Throwable t) {

                    Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            cv_slide_up.setVisibility(View.VISIBLE);
            cv_slide_up.startAnimation(slide_down);

        } else {
            if (cv_slide_up.getVisibility() == View.VISIBLE) {
                cv_slide_up.startAnimation(slide_up);
                cv_slide_up.setVisibility(View.GONE);
            }
        }
    }

    private void initMapsPesanan(ArrayList<Pesanan> pesananArrayList) {
        int number_marker = 0;
        int first_poly = 0;
        latLngList.clear();
        marker_list.clear();
        position_hashap.clear();
        markerMapPesanan.clear();
        for (int i = 0; i < pesananArrayList.size(); i++) {
            String customer_id = String.valueOf(pesananArrayList.get(i).getId_customer());

            String titik_koordinat = pesananArrayList.get(i).getTitik_koordinat();
            double latitude_pelanggan = Double.parseDouble(titik_koordinat.substring(0, titik_koordinat.lastIndexOf(",")));
            double longitude_pelanggan = Double.parseDouble(titik_koordinat.substring(titik_koordinat.lastIndexOf(",") + 1));

//            Toast.makeText(HomeActivity.this, "Latitude : " + latitude_pelanggan + " AND Longitude : " + longitude_pelanggan, Toast.LENGTH_LONG).show();
            Marker marker = map.addMarker(new MarkerOptions().title("Rumah Pelanggan")
                    .icon(bitmapDescriptor(HomeActivity.this, i))
                    .position(new LatLng(latitude_pelanggan, longitude_pelanggan)));
            latLngList.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
            marker_list.add(marker);
            builder.include(marker.getPosition());
//            polyOptions.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
            String idmark = marker.getId();
            position_hashap.add(idmark);
            markerMapPesanan.put(idmark, pesananArrayList.get(i));

//            Toast.makeText(HomeActivity.this, "Jumlah LatList = "+latLngList.size(), Toast.LENGTH_LONG).show();


            if (first_poly == 0) {
                first_poly = 1;
//                Findroutes(latling_baadalletta, latLngList.get(i));
//                Log.d("Latling", latling_baadalletta.toString());


                GoogleDirection.withServerKey(BuildConfig.API_KEY_MAPS)
                        .from(latling_baadalletta)
                        .to(latLngList.get(i))
                        .transportMode(TransportMode.DRIVING)
                        .avoid(AvoidType.FERRIES)
                        .avoid(AvoidType.HIGHWAYS)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(@Nullable Direction direction) {
//                                Toast.makeText(HomeActivity.this, "Sukses", Toast.LENGTH_SHORT).show();


                                Log.d("eeeeee", direction.getStatus());

                                if (direction.isOK()) {
                                    // Do something
                                    com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);

                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    Log.d("eeeeee", directionPositionList.size() + "eeeeee");

//                                if (polylineOptions_final != null)
//                                {
//                                    polylineOptions_final.remove();
//                                }
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplication(),
                                            directionPositionList, 4, Color.parseColor("#0187C6"));
                                    polylineOptions_final = map.addPolyline(polylineOptions);
                                } else {
                                    // Do something
                                }
//                        Log.d("eeeeee"+direction.getGeocodedWaypointList().toString(), "onDirectionSuccess: ");

//                            direction.getRouteList().get(0).;
//                            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//                            for (int z = 0; z < list.size(); z++) {
//                                LatLng point = list.get(z);
//                                options.add(point);
//                            }
//                            line = myMap.addPolyline(options);

                            }

                            @Override
                            public void onDirectionFailure(@NonNull Throwable t) {
                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

            } else {
//                Log.d("Latling", latLngList.get(i - 1).toString());
//
//                if (latLngList.size() > 4) {
//                    break;
//                } else {
//                    Findroutes(latLngList.get(i - 1), latLngList.get(i));
//                }

                GoogleDirection.withServerKey(BuildConfig.API_KEY_MAPS)
                        .from(latLngList.get(i - 1))
                        .to(latLngList.get(i))
                        .transportMode(TransportMode.DRIVING)
                        .avoid(AvoidType.FERRIES)
                        .avoid(AvoidType.HIGHWAYS)
                        .execute(new DirectionCallback() {
                            @Override
                            public void onDirectionSuccess(@Nullable Direction direction) {
//                                Toast.makeText(HomeActivity.this, "Sukses", Toast.LENGTH_SHORT).show();
                                Log.d("eeeeee", direction.getStatus());

                                if (direction.isOK()) {
                                    // Do something
                                    com.akexorcist.googledirection.model.Route route = direction.getRouteList().get(0);
                                    Leg leg = route.getLegList().get(0);

                                    ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                    Log.d("eeeeee", directionPositionList.size() + "eeeeee");

//                                if (polylineOptions_final != null)
//                                {
//                                    polylineOptions_final.remove();
//                                }
                                    PolylineOptions polylineOptions = DirectionConverter.createPolyline(getApplication(),
                                            directionPositionList, 4, Color.parseColor("#0187C6"));
                                    polylineOptions_final = map.addPolyline(polylineOptions);
                                } else {
                                    // Do something
                                }
//                        Log.d("eeeeee"+direction.getGeocodedWaypointList().toString(), "onDirectionSuccess: ");

//                            direction.getRouteList().get(0).;
//                            PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
//                            for (int z = 0; z < list.size(); z++) {
//                                LatLng point = list.get(z);
//                                options.add(point);
//                            }
//                            line = myMap.addPolyline(options);

                            }

                            @Override
                            public void onDirectionFailure(@NonNull Throwable t) {
                                Toast.makeText(HomeActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();

                            }
                        });

            }

            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.16); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.animateCamera(cu);
        }

//        int last_list = latLngList.size() - 1;


    }

    private BitmapDescriptor bitmapDescriptor(Context context, int position) {
        int height = 80;
        int width = 55;
        vectorDrawble = ContextCompat.getDrawable(context, img_marker[position]);
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
    public void onBackPressed() {
        if (sliding_layout != null &&
                (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }

    private void initMapsBaadalletta() {
        double latitude_baadalletta = Double.parseDouble(Constanta.LATITUDE_BAADALLETTA);
        double longitude_baadalletta = Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA);
        Marker marker1 = map.addMarker(new MarkerOptions().icon(bitmapDescriptorBaadalletta(this))
                .position(new LatLng(latitude_baadalletta, longitude_baadalletta)));
        builder.include(marker1.getPosition());
        latling_baadalletta = new LatLng(latitude_baadalletta, longitude_baadalletta);
        marker_list.add(0, marker1);
        marker_baadaletta = marker1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        initMapsBaadalletta();

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

        map.setOnPolylineClickListener(this);
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(Constanta.LATITUDE_BAADALLETTA),
//                Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA)), 12));
//
//        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(), 12));

//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                map.setMyLocationEnabled(true);
//            } else {
//                //Request Location Permission
//                checkLocationPermission();
//            }
//        } else {
////            buildGoogleApiClient();
//            map.setMyLocationEnabled(true);
//        }

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                clickMarkerPesanan(marker);
                return false;
            }
        });
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
    public void onPesananClicked(int position) {

        String customer_id = String.valueOf(pesanans_diatur.get(position).getId_customer());

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
        responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
            @Override
            public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {

                double latitude_pelanggan_click = Double.parseDouble(response.body().getData().getLatitude());
                double longitude_pelanggan_click = Double.parseDouble(response.body().getData().getLongitude());
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_pelanggan_click,
                        longitude_pelanggan_click), 15));
                showPanel();
            }

            @Override
            public void onFailure(Call<ResponsCustomer> call, Throwable t) {

            }
        });
    }

    private void showPanel() {

//        if (sliding_layout != null &&
//                (sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || sliding_layout.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
        sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//        } else {
//            sliding_layout.setPanelState(SlidingUpPanelLayout.PanelState.ANCHORED);
//        }

    }

    @Override
    public void onPolylineClick(Polyline polyline) {

    }

    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = map.addPolyline((PolylineOptions) values[0]);
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(parentLayout, e.toString(), Snackbar.LENGTH_LONG);
        snackbar.show();

    }

    @Override
    public void onRoutingStart() {
//        Toast.makeText(HomeActivity.this, "Finding Route...", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int shortestRouteIndex) {

        CameraUpdate center = CameraUpdateFactory.newLatLng(latling_baadalletta);
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
//        if(polylines!=null) {
//            polylines.clear();
//        }
        LatLng polylineStartLatLng = null;
        LatLng polylineEndLatLng = null;

        polylines = null;
        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i < arrayList.size(); i++) {

            if (i == shortestRouteIndex) {
                polyOptions.color(getResources().getColor(R.color.ColorPrimary));
                polyOptions.width(7);
                polyOptions.addAll(arrayList.get(shortestRouteIndex).getPoints());
                Polyline polyline = map.addPolyline(polyOptions);
                polylineStartLatLng = polyline.getPoints().get(0);
                int k = polyline.getPoints().size();
                polylineEndLatLng = polyline.getPoints().get(k - 1);
                polylines.add(polyline);

            } else {

            }

        }

        //Add Marker on route starting position
//        MarkerOptions startMarker = new MarkerOptions();
//        startMarker.position(polylineStartLatLng);
//        startMarker.title("My Location");
//        mMap.addMarker(startMarker);

        //Add Marker on route ending position
//        MarkerOptions endMarker = new MarkerOptions();
//        endMarker.position(polylineEndLatLng);
//        endMarker.title("Destination");
//        mMap.addMarker(endMarker);
    }

    @Override
    public void onRoutingCancelled() {
//        Findroutes(start,end);
    }

    // function to find Routes.
    public void Findroutes(LatLng Start, LatLng End) {
        if (Start == null || End == null) {
//            Toast.makeText(HomeActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
        } else {

            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(BuildConfig.API_KEY_MAPS)  //also define your api key here.
                    .build();
            routing.execute();
        }
    }
}