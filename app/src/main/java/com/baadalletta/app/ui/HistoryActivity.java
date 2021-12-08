package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.baadalletta.app.R;
import com.baadalletta.app.adapter.PesananHomeAdapter;
import com.baadalletta.app.adapter.RiwayatKurirAdapter;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView rv_riwayat;
    private RiwayatKurirAdapter riwayatKurirAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        rv_riwayat = findViewById(R.id.rv_riwayat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_baseline_chevron_left_24);


        rv_riwayat.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
        riwayatKurirAdapter = new RiwayatKurirAdapter(HistoryActivity.this);
        rv_riwayat.setAdapter(riwayatKurirAdapter);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}