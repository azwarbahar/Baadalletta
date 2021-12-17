package com.baadalletta.app.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.baadalletta.app.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;

public class PreviewPhotoBitmapActivity extends AppCompatActivity {

    private ImageView img_close;
    private PhotoView img_zoom;

    private Uri foto;
    private Bitmap bitmap_from_uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_photo_bitmap);

        img_close = findViewById(R.id.img_close);
        img_zoom = findViewById(R.id.img_zoom);

        foto = Uri.parse(getIntent().getStringExtra("foto"));
        try {
            bitmap_from_uri = MediaStore.Images.Media.getBitmap(this.getContentResolver(), foto);
            Glide.with(this)
                    .asBitmap()
                    .load(bitmap_from_uri)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            img_zoom.setImageBitmap(resource);
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}