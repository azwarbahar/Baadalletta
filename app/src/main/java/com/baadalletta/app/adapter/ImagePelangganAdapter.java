package com.baadalletta.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.baadalletta.app.R;
import com.baadalletta.app.utils.Constanta;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.ArrayList;
import java.util.Objects;

public class ImagePelangganAdapter extends PagerAdapter {
    // Context object
    Context context;

    // Array of images
    ArrayList<String> images;

    // Layout Inflater
    LayoutInflater mLayoutInflater;

    // Viewpager Constructor
    public ImagePelangganAdapter(Context context, ArrayList<String> images) {
        this.context = context;
        this.images = images;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // return the number of images
        return images.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((LinearLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        // inflating the item.xml
        View itemView = mLayoutInflater.inflate(R.layout.item_img_slide, container, false);

        // referencing the image view from the item.xml file
        PhotoView imageView =  itemView.findViewById(R.id.img_zoom);

        // setting the image in the imageView
//        imageView.setImageResource(images[position]);

        RequestOptions options = new RequestOptions()
                .centerInside()
                .placeholder(R.drawable.loading_animation)
                .error(R.drawable.ic_broken_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .priority(Priority.HIGH);

        Glide.with(context)
                .load(images.get(position))
                .apply(options)
                .into(imageView);

        // Adding the View
        Objects.requireNonNull(container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((LinearLayout) object);
    }
}
