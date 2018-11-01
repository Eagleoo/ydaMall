package com.mall.view.carMall;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

/**
 * Created by Administrator on 2018/4/1.
 */

public class CongratulationsDialog extends Dialog {

    String type;
    String order;
    Context context;

    public CongratulationsDialog(Context context, String type, String order) {
        super(context, R.style.dialog);
        this.context = context;
        this.type = type;
        this.order = order;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_congratulations);
        TextView typeView = findViewById(R.id.type);
        RelativeLayout closeView = findViewById(R.id.close);
        TextView orderView = findViewById(R.id.order);
        Button next = findViewById(R.id.next);
        String string = "<font color='#464646'>获得</font><font color='#E61111'>" + type + "万档</font>";
        typeView.setText(Html.fromHtml(string));
        orderView.setText(order);
        closeView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                dismiss();
                return true;
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, MyOrderNumberActivity.class);
                dismiss();
            }
        });
        setCanceledOnTouchOutside(false);
    }
}
