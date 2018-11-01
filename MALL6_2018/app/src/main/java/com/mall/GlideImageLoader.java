package com.mall;

import android.app.Activity;
import android.content.Context;

import com.bumptech.glide.Glide;
import com.mall.view.R;
import com.yancy.gallerypick.widget.GalleryImageView;
import com.yancy.gallerypick.inter.ImageLoader;
/**
 * Created by Administrator on 2018/3/14.
 */

public class GlideImageLoader implements ImageLoader {

    private final static String TAG = "GlideImageLoader";

    @Override
    public void displayImage(Activity activity, Context context, String path, GalleryImageView galleryImageView, int width, int height) {
        Glide.with(context)
                .load(path)
                .placeholder(R.mipmap.gallery_pick_photo)
                .centerCrop()
                .into(galleryImageView);
    }

    @Override
    public void clearMemoryCache() {

    }
}