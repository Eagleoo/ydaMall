package com.mall.util;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Base.BasePopWindow;
import com.lidroid.xutils.db.annotation.NotNull;
import com.mall.view.R;

/**
 * Created by Administrator on 2018/3/26.
 */

public class MyPopWindow extends BasePopWindow {
    private MyPopWindow(Builder builder) {

        super(builder);
        Log.e("MyPopWindow", "1");
    }

    private static BasePopWindow Builder(final MyBuilder myBuilder) {

        View contentView = LayoutInflater.from(myBuilder.context).inflate(R.layout.d_video_audio_dialog, null);
        final Button surebu = contentView.findViewById(R.id.video_audio_dialog_sure);
        final Button cancelbu = contentView.findViewById(R.id.video_audio_dialog_cancel);
        TextView content = contentView.findViewById(R.id.video_audio_dialog_content);
        TextView title = contentView.findViewById(R.id.video_audio_dialog_title);
        final ImageView close = contentView.findViewById(R.id.close);

        if (!Util.isNull(myBuilder.title)) {
            title.setText(myBuilder.title);
        }


        if (myBuilder.message.contains("<font")) {
            content.setText(Html.fromHtml(myBuilder.message));
        } else {
            content.setText(myBuilder.message);
        }


        if (!Util.isNull(myBuilder.color)) {
            surebu.setBackgroundColor(Color.parseColor(myBuilder.color));
        }
        surebu.setText(myBuilder.rightstr);
        if (Util.isNull(myBuilder.lefttstr)) {
            cancelbu.setVisibility(View.GONE);
        } else {
            cancelbu.setText(myBuilder.lefttstr);
        }
        Builder builder = new Builder(myBuilder.context, contentView)
                .setIschagebackcolor(true)
                .setWidth(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (myBuilder.animationStyle != 0) {
            builder.setAnimationStyle(myBuilder.animationStyle);
        }
        final BasePopWindow popWindow = builder.build();
        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v.getId() == cancelbu.getId()) {
                    if (myBuilder.leftclick != null) {
                        myBuilder.leftclick.onClick(v);


                    }

                } else if (v.getId() == surebu.getId()) {
                    if (myBuilder.rightclick != null) {
                        myBuilder.rightclick.onClick(v);

                    }


                } else if (v.getId() == close.getId()) {
                    if (myBuilder.onclose != null) {
                        myBuilder.onclose.onClick(v);

                    }
                }
                popWindow.dismiss();
            }
        };
        if (myBuilder.isshowclose) {
            close.setVisibility(View.VISIBLE);
            close.setOnClickListener(clickListener);
        }
        surebu.setOnClickListener(clickListener);
        cancelbu.setOnClickListener(clickListener);

        return popWindow;
    }


    public static class MyBuilder {
        private Context context;
        private String color; // 设置主题颜色
        private String title;//  设置标题
        @NotNull
        private String message;// 设置内容
        @NotNull
        private String rightstr;  //右边显示文字
        private View.OnClickListener rightclick;
        private View.OnClickListener onclose;
        private String lefttstr;  //左边显示文字
        private View.OnClickListener leftclick;
        private int animationStyle;
        private boolean isshowclose;

        public MyBuilder(Context context, String message, String rightstr, View.OnClickListener rightclick) {
            this.context = context;
            this.message = message;
            this.rightstr = rightstr;
            this.rightclick = rightclick;
        }


        public BasePopWindow build() {
            return Builder(this);
        }


        public MyBuilder setColor(String color) {
            this.color = color;
            return this;
        }

        public MyBuilder setisshowclose(boolean isshowclose) {
            this.isshowclose = isshowclose;
            return this;
        }


        public MyBuilder setTitle(String title) {
            this.title = title;
            return this;
        }


        public MyBuilder setLeft(String lefttstr, View.OnClickListener leftclick) {
            this.lefttstr = lefttstr;
            this.leftclick = leftclick;
            return this;
        }


        public MyBuilder setAnimationStyle(int animationStyle) {
            this.animationStyle = animationStyle;
            return this;
        }


    }

}
