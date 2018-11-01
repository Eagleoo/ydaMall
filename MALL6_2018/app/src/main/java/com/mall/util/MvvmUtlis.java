package com.mall.util;

import android.databinding.BindingAdapter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/4/13.
 */

public class MvvmUtlis {
    @BindingAdapter({"image"})
    public static void imageLoader(ImageView imageView, int drawable) {
        imageView.setImageResource(drawable);
    }
    @BindingAdapter({"backgroundColor"})
    public static void setBackGroundColor(View view, int color){
        view.setBackgroundColor(color);
    }
    @BindingAdapter({"background"})
    public static void setBackGround(View view, Drawable background){
        view.setBackground(background);
    }

    @BindingAdapter({"textColor"})
    public static void setTextColor(TextView textView, int color){
        textView.setTextColor(color);
    }
}