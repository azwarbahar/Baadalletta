package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.baadalletta.app.R;

public class LoginActivity extends AppCompatActivity {

    private RelativeLayout rl_masuk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        rl_masuk = findViewById(R.id.rl_masuk);
        rl_masuk.setOnClickListener(this::clickMasuk);
    }

    private void clickMasuk(View view) {

        startActivity(new Intent(LoginActivity.this, HomeActivity.class));

    }
}