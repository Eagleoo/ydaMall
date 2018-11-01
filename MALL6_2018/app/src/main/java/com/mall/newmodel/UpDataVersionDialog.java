package com.mall.newmodel;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

/**
 * Created by Administrator on 2017/10/31.
 */

public class UpDataVersionDialog extends Dialog {
    private Context context;
    private String message;
    private TextView uptext,tv1,button_title;
    private ImageView close;
    private NumberSeekBar bar1;

    int number=0;

    public TextView getButton_title() {
        return button_title;
    }

    public UpDataVersionDialog(@NonNull Context context, String message) {
        super(context, R.style.reddialog);
        this.context=context;
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.updataversion_layout);
        uptext= (TextView) findViewById(R.id.uptext);
        close= (ImageView) findViewById(R.id.close);
        uptext.setMovementMethod(ScrollingMovementMethod.getInstance());
        bar1= (NumberSeekBar) findViewById(R.id.bar1);
        bar1.setTextColor(Color.parseColor("#ffffff"));
        bar1.setTextSize(Util.dpToPx(context,12));
        tv1= (TextView) findViewById(R.id.tv1);
        button_title= (TextView) findViewById(R.id.button_title);
        //添加下划线
        tv1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);

        uptext.setText(message);
    }

    public void setUpdataMessage(String message){
        uptext.setText(message);
    }


    public void  setProgress(int progress){
        Log.e("下载进度","progress"+progress);
        if(progress==-1){
            bar1.setProgress(0);
            Util.show("下载出错,请重新点击下载");
            button_title.setText("立即\n升级");
            number=0;
        }else {
            bar1.setProgress(progress);
        }

    }

    public TextView gettv1(){
        return tv1;
    }

    public NumberSeekBar getSeekBar(){

        return bar1;
    }

    public  void  setOnclick(final View.OnClickListener click){


        button_title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click!=null){
                    click.onClick(button_title);
                }

                if(number%2==0){
                    button_title.setText("暂停");
                }else{
                    button_title.setText("立即\n升级");
                }
                number++;
            }
        });
    }
    public void  setnotupdatedclick(final View.OnClickListener click){


        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click!=null){
                    click.onClick(tv1);
                }
                cancel();
                dismiss();
            }
        });
    }


    public void  setclose(final View.OnClickListener click){


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (click!=null){
                    click.onClick(tv1);
                }
                cancel();
                dismiss();
            }
        });
    }

}
