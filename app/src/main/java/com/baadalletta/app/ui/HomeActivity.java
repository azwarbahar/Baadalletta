package com.baadalletta.app.ui;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.baadalletta.app.models.ResponsePesananKurir;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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
        PesananHomeAdapter.PesananListRecyclerClickListener {

    private GoogleMap map;
    private Polyline currentPolyline;

    private ImageView img_user;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        slide_up = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);
        slide_down = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);

        sliding_layout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

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


        img_user = findViewById(R.id.img_user);
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
            }
        });

        laodDataPesanan("6");

    }

    private void laodDataPesanan(String kurir_id) {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponsePesananKurir> responsePesananKurirCall = apiInterface.getPesananMaps(kurir_id, "proses");
        responsePesananKurirCall.enqueue(new Callback<ResponsePesananKurir>() {
            @Override
            public void onResponse(Call<ResponsePesananKurir> call, Response<ResponsePesananKurir> response) {
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
        PolygonOptions polyOptions = new PolygonOptions();
        polyOptions.strokeColor(Color.BLUE);
        polyOptions.strokeWidth(5);
        for (int i = 0; i < pesananArrayList.size(); i++) {
            Customer customer =  pesananArrayList.get(i).getCustomer();
            if (!customer.getLatitude().equals("-") || (customer.getLatitude() != null)) {
                double latitude_pelanggan = Double.parseDouble(customer.getLatitude());
                double longitude_pelanggan = Double.parseDouble(customer.getLongitude());
                Marker marker = map.addMarker(new MarkerOptions().title("Rumah Pelanggan")
                        .icon(bitmapDescriptor(this, i))
                        .position(new LatLng(latitude_pelanggan, longitude_pelanggan)));
                marker_list.add(marker);
                builder.include(marker.getPosition());
                polyOptions.add(new LatLng(latitude_pelanggan, longitude_pelanggan));
                String idmark = marker.getId();
                position_hashap.add(idmark);
                markerMapPesanan.put(idmark, pesananArrayList.get(i));

            }
        }
//        map.addPolygon(polyOptions);
        LatLngBounds bounds = builder.build();
        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.16); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
        map.animateCamera(cu);
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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        double latitude_baadalletta = Double.parseDouble(Constanta.LATITUDE_BAADALLETTA);
        double longitude_baadalletta = Double.parseDouble(Constanta.LONGITUDE_BAADALLETTA);
        Marker marker1 = map.addMarker(new MarkerOptions().icon(bitmapDescriptorBaadalletta(this))
                .position(new LatLng(latitude_baadalletta, longitude_baadalletta)));
        builder.include(marker1.getPosition());
        marker_list.add(0, marker1);

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

        Customer customer = pesananArrayList.get(position).getCustomer();
        double latitude_pelanggan_click = Double.parseDouble(customer.getLatitude());
        double longitude_pelanggan_click = Double.parseDouble(customer.getLongitude());
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude_pelanggan_click,
                longitude_pelanggan_click), 15));
        showPanel();
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
}