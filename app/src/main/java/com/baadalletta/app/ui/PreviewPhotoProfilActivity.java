package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

public class PreviewPhotoProfilActivity extends AppCompatActivity {

    private ImageView img_close;
    private PhotoView img_zoom;

    String foto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo_profil);

        img_close = findViewById(R.id.img_close);
        img_zoom = findViewById(R.id.img_zoom);

        foto = getIntent().getStringExtra("foto");

        Glide.with(PreviewPhotoProfilActivity.this)
                .load(foto)
                .placeholder(R.drawable.loading_animation)
                .into(img_zoom);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


    }
}