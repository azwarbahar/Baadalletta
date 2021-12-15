package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.baadalletta.app.adapter.ImagePelangganAdapter;

import java.util.ArrayList;

public class PreviewPhotoActivity extends AppCompatActivity {

    private ImageView img_close;
    private ViewPager viewPager;
    private ImagePelangganAdapter imagePelangganAdapter;
    // images array
    int[] images;
    private ArrayList<String> img_customer = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo);

        img_customer = (ArrayList<String>) getIntent().getSerializableExtra("img_customer");

        if (img_customer.size() > 0){
            // Img sliding


            viewPager = findViewById(R.id.viewpager_img);
            imagePelangganAdapter = new ImagePelangganAdapter(PreviewPhotoActivity.this, img_customer);
            viewPager.setAdapter(imagePelangganAdapter);
        }


        img_close = findViewById(R.id.img_close);
        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}