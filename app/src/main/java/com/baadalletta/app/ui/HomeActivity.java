package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;

    private ImageView img_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

        img_user = findViewById(R.id.img_user);
        img_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this, AkunActivity.class));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;


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

    }
}