package com.mall.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.view.BusinessCircle.RedBeanAccountActivity;

/**
 * Created by Administrator on 2017/9/29.
 */

public class OpenRedBean extends Dialog {
    Context context;
    ImageView close;
    ImageView image;
    private int realImgShowWidth, realImgShowHeight;
    public TextView textView4,rotateTextView2,toxianjin,toxiaofei;
    String xinjing;
    String xiaofei;

    public OpenRedBean(@NonNull Context context,String xinjing,String xiaofei) {
        super(context, R.style.reddialog);
        this.context = context;
        this.xinjing=xinjing;
        this.xiaofei=xiaofei;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.redbean);
        close = (ImageView) findViewById(R.id.close);
        image = (ImageView) findViewById(R.id.image);

        textView4 = (TextView) findViewById(R.id.textView4); //现金券
        rotateTextView2 = (TextView) findViewById(R.id.rotateTextView2); //消费券

        toxianjin = (TextView) findViewById(R.id.toxianjin); //现金券
        toxiaofei = (TextView) findViewById(R.id.toxiaofei); //消费券

        textView4.setText(xinjing);
        rotateTextView2.setText(xiaofei);

        toxianjin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                              Intent intent= new Intent(context,RedBeanAccountActivity.class);
                                intent.putExtra("title","现金券账户");
//                                intent.putExtra("bean",redPackageInLetBean);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        toxiaofei.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                                Intent   intent= new Intent(context,RedBeanAccountActivity.class);
                                intent.putExtra("title","消费券账户");
//                                intent.putExtra("bean",redPackageInLetBean);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
//        getImgDisplaySize();

    }

    /**
     *
     * @param txt
     */
    public void settextView4(String txt){
        textView4.setText(txt);
    }
    public void setrotateTextView2(String txt){
        rotateTextView2.setText(txt);
    }


}