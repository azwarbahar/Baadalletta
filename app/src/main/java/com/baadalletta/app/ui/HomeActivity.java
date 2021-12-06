package com.baadalletta.app.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.baadalletta.app.BuildConfig;
import com.baadalletta.app.R;
import com.baadalletta.app.adapter.PesananHomeAdapter;
import com.baadalletta.app.directionhelper.FetchURL;
import com.baadalletta.app.directionhelper.TaskLoadedCallback;
import com.baadalletta.app.models.Customer;
import com.baadalletta.app.models.Pesanan;
import com.baadalletta.app.models.ResponsCustomer;
import com.baadalletta.app.models.ResponsPesanan;
import com.baadalletta.app.models.ResponsePesananKurir;
import com.baadalletta.app.network.ApiClient;
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
import com.google.android.gms.maps.model.PolygonOptions;
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

    private PolylineOptions polyOptions = new PolylineOptions();

    private GoogleMap map;
    private Polyline currentPolyline;

    private LatLng latling_baadalletta;

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

    private CardView cv_slide_up;
    private ImageView img_back_slide_up;
    private ImageView btn_jenis_map;
    private static final int REQUEST_CODE_QR_SCAN = 101;
    private View dialogView;

    private LinearLayout ll_refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        ll_refresh = findViewById(R.id.ll_refresh);
        btn_jenis_map = findViewById(R.id.btn_jenis_map);
        img_back_slide_up = findViewById(R.id.img_back_slide_up);
        cv_slide_up = findViewById(R.id.cv_slide_up);
        cv_slide_up.setVisibility(View.GONE);

        img_power = findViewById(R.id.img_power);
        tv_power = findViewById(R.id.tv_power);
        ll_power = findViewById(R.id.ll_power);
        rv_pesanan_home = findViewById(R.id.rv_pesanan_home);
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

        ll_refresh.setVisibility(View.GONE);
        ll_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                map.clear();
                latLngList.clear();
                pesananArrayList.clear();
                mapFragment.getMapAsync(HomeActivity.this);
                laodDataPesanan("6");
            }
        });

        ll_power.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String status_power = tv_power.getText().toString();
                if (status_power.equals("Hidup")) {
                    img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                            R.color.black));
                    tv_power.setText("Mati");
                    tv_power.setTextColor(getResources().getColor(R.color.black));
//                    Toast.makeText(HomeActivity.this, "Sedang Offline", Toast.LENGTH_SHORT).show();
                } else {
                    img_power.setColorFilter(ContextCompat.getColor(HomeActivity.this,
                            R.color.white));
                    tv_power.setText("Hidup");
                    tv_power.setTextColor(getResources().getColor(R.color.white));
//                    Toast.makeText(HomeActivity.this, "Sedang Online", Toast.LENGTH_SHORT).show();
                }

            }
        });


        img_qr_code = findViewById(R.id.img_qr_code);
        img_qr_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });


        img_user = findViewById(R.id.img_user);
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
            }
        });

        btn_jenis_map.setOnClickListener(this::clickjenisMap);

        laodDataPesanan("6");

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
//                            Intent intent = new Intent(HomeActivity.this, InputDataBayiActivity.class);
//                            intent.putExtra("DATA", result);
//                            startActivity(intent);
                        }
                    }, 1500);

                }
            }, 300);
        }
    }

    private void updatePesananScan(String result) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsPesanan> responsPesananCall = apiInterface.setPesananKurir(result, "proses", 6);
        responsPesananCall.enqueue(new Callback<ResponsPesanan>() {
            @Override
            public void onResponse(Call<ResponsPesanan> call, Response<ResponsPesanan> response) {
                if (response.isSuccessful()) {
                    String kode = String.valueOf(response.body().getStatus_code());
                    if (kode.equals("200")) {
                        map.clear();
//                        pesananArrayList.add(response.body().getData());
                        latling_baadalletta = null;
                        position_hashap.clear();
                        markerMapPesanan.clear();
                        latLngList.clear();
                        polylines.clear();
                        marker_list.clear();
                        initMapsBaadalletta();
                        laodDataPesanan("6");


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
                        rv_pesanan_home.setLayoutManager(new LinearLayoutManager(HomeActivity.this));
                        pesananHomeAdapter = new PesananHomeAdapter(HomeActivity.this, pesananArrayList, HomeActivity.this);
                        rv_pesanan_home.setAdapter(pesananHomeAdapter);
                        initMapsPesanan(pesananArrayList);

                        Toast.makeText(HomeActivity.this, "Pesan : " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(HomeActivity.this, "Kode = " + status_code + " and Pesan : " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponsePesananKurir> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(HomeActivity.this, "Error : " + t.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void clickMarkerPesanan(Marker marker) {

        if (markerMapPesanan.get(marker.getId()) != null) {
            Pesanan pesanan = markerMapPesanan.get(marker.getId());
            builder_item.include(marker.getPosition());
            builder_item.include(marker_list.get(0).getPosition());

            LatLngBounds bounds = builder_item.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.16); // offset from edges of the map 10% of screen

            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            map.animateCamera(cu);
            for (int i = 0; i < marker_list.size(); i++) {
                if (position_hashap.get(i).equals(marker.getId())) {
//                    for(Polyline line : polylines)
//                    {
//                        line.remove();
//                    }
//                    polylines.clear();
                    new FetchURL(HomeActivity.this).execute(getUrl(marker_list.get(0).getPosition(),
                            marker_list.get(i + 1).getPosition(), "driving"), "driving");
                    break;
                }
            }

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
//        PolygonOptions polyOptions = new PolygonOptions();
//        polyOptions.strokeColor(Color.BLUE);
//        polyOptions.strokeWidth(5);
        int number_marker = 0;
        final int[] first_poly = {0};
        for (int i = 0; i < pesananArrayList.size(); i++) {
            String customer_id = String.valueOf(pesananArrayList.get(i).getId_customer());
            ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
            Call<ResponsCustomer> responsCustomerCall = apiInterface.getCustomerId(customer_id);
            int finalNumber_marker = number_marker;
            int finalI = i;
            responsCustomerCall.enqueue(new Callback<ResponsCustomer>() {
                @Override
                public void onResponse(Call<ResponsCustomer> call, Response<ResponsCustomer> response) {
                    if (response.isSuccessful()) {
                        String kode = String.valueOf(response.body().getStatus_code());
                        if (kode.equals("200")) {
                            Customer customer = response.body().getData();
                            if (!customer.getLatitude().equals("-") || (customer.getLatitude() != null)) {
                                double latitude_pelanggan = Double.parseDouble(customer.getLatitude());
                                double longitude_pelanggan = Double.parseDouble(customer.getLongitude());
                                Marker marker = map.addMarker(new MarkerOptions().title("Rumah Pelanggan")
                                        .icon(bitmapDescriptor(HomeActivity.this, finalNumber_marker))
                                        .position(new LatLng(latitude_pelanggan, longitude_pelanggan)));
                                latLngList.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
                                marker_list.add(marker);
                                builder.include(marker.getPosition());
//                                polyOptions.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
                                String idmark = marker.getId();
                                position_hashap.add(idmark);
                                markerMapPesanan.put(idmark, pesananArrayList.get(finalNumber_marker));

                                if (first_poly[0] == 0) {
                                    first_poly[0] = 1;
                                    Findroutes(latling_baadalletta, latLngList.get(finalI));
                                } else {
//                                    if (finalI > 1) {
                                    Findroutes(latLngList.get(finalI - 1), latLngList.get(finalI));
//                                    }
                                }

                                LatLngBounds bounds = builder.build();
                                int width = getResources().getDisplayMetrics().widthPixels;
                                int height = getResources().getDisplayMetrics().heightPixels;
                                int padding = (int) (width * 0.16); // offset from edges of the map 10% of screen

                                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                map.animateCamera(cu);
                            }
                        } else {
                            Toast.makeText(HomeActivity.this, "Data Customer tidak ditemukan", Toast.LENGTH_SHORT).show();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ResponsCustomer> call, Throwable t) {

                }
            });
            number_marker++;
        }
//        map.addPolygon(polyOptions);
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

        String customer_id = String.valueOf(pesananArrayList.get(position).getId_customer());

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
        Toast.makeText(HomeActivity.this, "Finding Route...", Toast.LENGTH_LONG).show();

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
            Toast.makeText(HomeActivity.this, "Unable to get location", Toast.LENGTH_LONG).show();
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