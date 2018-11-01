package com.mall.util;

import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mall.view.R;

/**
 * Created by Administrator on 2018/3/8.
 */

public class BindingHelper {

    /**
     * 1.加载图片,无需手动调用此方法
     * 2.使用@BindingAdapter注解设置自定义属性的名称，imageUrl就是属性的名称，
     * 当ImageView中使用imageUrl属性时，会自动调用loadImage方法，
     *
     * @param imageView ImageView
     * @param url       图片地址
     */
    @BindingAdapter({"imageUrl"})
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load("file://"+url)
                .placeholder(R.drawable.community_image_head_rect)
                .error(R.drawable.community_image_head_rect)
                .into(imageView);
    }


}
