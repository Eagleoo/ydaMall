package com.mall.serving.voip.view.popupwindow;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.R;

public class VoipDialog extends Dialog {

    private String title;
    private String message;
    private Spanned span;
    private String left;
    private String right;
    private View.OnClickListener leftClick;
    private View.OnClickListener rightClick;
    private TextView dialog_left;
    private TextView dialog_right;

    public VoipDialog(String message, final Context context, String right,
                      String left, final View.OnClickListener rightClick,
                      final View.OnClickListener leftClick) {
        super(context, R.style.Dialog);

        this.message = message;
        this.left = left;
        this.right = right;
        this.leftClick = leftClick;
        this.rightClick = rightClick;

    }

    public VoipDialog(Spanned span, final Context context, String right,
                      String left, final View.OnClickListener rightClick,
                      final View.OnClickListener leftClick) {
        super(context, R.style.Dialog);

        this.span = span;
        this.left = left;
        this.right = right;
        this.leftClick = leftClick;
        this.rightClick = rightClick;

    }

    public VoipDialog(String title, String message, final Context context, String right,
                      String left, final View.OnClickListener rightClick,
                      final View.OnClickListener leftClick) {
        super(context, R.style.Dialog);

        this.title = title;
        this.message = message;
        this.left = left;
        this.right = right;
        this.leftClick = leftClick;
        this.rightClick = rightClick;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.voip_dialog);
        setCanceledOnTouchOutside(false);
        TextView tv_dialog_title = (TextView) this.findViewById(R.id.tv_dialog_title);
        TextView tv_dialog_content = (TextView) findViewById(R.id.tv_dialog_content);
        dialog_left = (TextView) findViewById(R.id.dialog_left);
        dialog_right = (TextView) findViewById(R.id.dialog_right);
        final TextView dialog_sure = (TextView) findViewById(R.id.dialog_sure);
        tv_dialog_title.setText("温馨提示");
        if (!Util.isNull(title)) {
            tv_dialog_title.setText(title);
        }

        if (!Util.isNull(message)) {
            tv_dialog_content.setText(message);
        } else if (!Util.isNull(span)) {
            tv_dialog_content.setText(span);
        }

        if (Util.isNull(left) || Util.isNull(right)) {
            dialog_left.setVisibility(View.GONE);
            dialog_right.setVisibility(View.GONE);
            dialog_sure.setVisibility(View.VISIBLE);
        } else {
            dialog_left.setText(left);
            dialog_right.setText(right);
        }

        View.OnClickListener clickListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (v == dialog_left) {
                    Log.e("leftClick11", "sss" + (leftClick == null));
                    dismiss();
                    if (leftClick != null) {
                        leftClick.onClick(v);
                    }
                } else if (v == dialog_right || v == dialog_sure) {
                    dismiss();
                    if (rightClick != null) {
                        rightClick.onClick(v);
                    }
                }
            }
        };
        dialog_left.setOnClickListener(clickListener);
        dialog_right.setOnClickListener(clickListener);
        dialog_sure.setOnClickListener(clickListener);
        Window w = this.getWindow();
        //w.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        w.setWindowAnimations(R.style.dialog_inout_anim);  //添加动画  
    }


    public TextView getDialog_left() {
        return dialog_left;
    }

    public TextView getDialog_right() {
        return dialog_right;
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
