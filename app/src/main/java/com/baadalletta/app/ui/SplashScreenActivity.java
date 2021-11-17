package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.baadalletta.app.R;

public class SplashScreenActivity extends AppCompatActivity {

    private int waktu_loading=3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent login=new Intent(SplashScreenActivity.this, LoginActivity.class);
                startActivity(login);
                finish();

            }
        },waktu_loading);
    }
}