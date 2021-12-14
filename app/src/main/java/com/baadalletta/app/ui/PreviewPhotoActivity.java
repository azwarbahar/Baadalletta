package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.baadalletta.app.adapter.ImagePelangganAdapter;

public class PreviewPhotoActivity extends AppCompatActivity {

    private ImageView img_close;
    private ViewPager viewPager;
    private ImagePelangganAdapter imagePelangganAdapter;
    // images array
    int[] images = {12,23,44};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);

        // Img sliding
        viewPager = findViewById(R.id.viewpager_img);
        imagePelangganAdapter = new ImagePelangganAdapter(PreviewPhotoActivity.this, images);
        viewPager.setAdapter(imagePelangganAdapter);

        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}