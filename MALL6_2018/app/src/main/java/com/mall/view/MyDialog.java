package com.mall.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

/**
 * Created by Administrator on 2018/3/12.
 */

public class MyDialog extends Dialog {

    private String title;

    private ViewGroup add;
    private View view;
    private TextView titletv;


    public MyDialog(String title, View view,final Context context) {
        super(context, R.style.Dialog);
        this.title = title;
        this.view=view;


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.voip_dialog);
        setContentView(R.layout.my_dialog);
        setCanceledOnTouchOutside(false);

        titletv=findViewById(R.id.titletv);
        titletv.setText(title);
        add=findViewById(R.id.add);
        add.addView(view);
        Window w = this.getWindow();
        //w.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        w.setWindowAnimations(R.style.dialog_inout_anim);  //添加动画
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (keyCode == KeyEvent.KEYCODE_BACK && isShowing()) {
                dismiss();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
