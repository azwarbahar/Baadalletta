package com.baadalletta.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.baadalletta.app.R;

public class AkunActivity extends AppCompatActivity {

    private LinearLayout ll_edit;
    private CardView cv_edit_password;
    private CardView cv_history;
    private CardView cv_logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_akun);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);

        ll_edit = findViewById(R.id.ll_edit);
        cv_edit_password = findViewById(R.id.cv_edit_password);
        cv_history = findViewById(R.id.cv_history);
        cv_logout = findViewById(R.id.cv_logout);


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
                startActivity(new Intent(AkunActivity.this, LoginActivity.class));
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}