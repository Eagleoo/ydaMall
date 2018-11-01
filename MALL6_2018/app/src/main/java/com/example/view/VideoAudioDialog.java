package com.example.view;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mall.view.R;

public class VideoAudioDialog {

    private AlertDialog dialog;
    private OnClickListener left;
    private OnClickListener right;
    private View root;
    private TextView title;
    private TextView content;
    private Button sure;
    private Button cancel;
    private View tag1;  // 显示第一行文字
    private View tag2; // 显示第二行文字
    private TextView video_audio_dialog_line1;
    private TextView video_audio_dialog_line2;
    private TextView video_audio_dialog_time;  //显示时间


    private View dialogtag3;
    private TextView video_audio_dialog_line3a;
    private TextView video_audio_dialog_line3b;


    private View dialogtag4;
    private TextView video_audio_dialog_line4a;
    private TextView video_audio_dialog_line4b;


    private View dialogtag5;
    private TextView video_audio_dialog_line5a;
    private TextView video_audio_dialog_line5b;


    private View dialogtag6;
    private TextView video_audio_dialog_line6a;
    private TextView video_audio_dialog_line6b;

    private View dialogtag7;
    private EditText video_audio_dialog_line7a;

    private View linview1;
    private View linview2;

    public EditText getInput(){
        return video_audio_dialog_line7a;
    }

    public String getRemark() {
        return video_audio_dialog_line7a.getText().toString();
    }


    public VideoAudioDialog(Context context) {
        dialog = new AlertDialog.Builder(context, R.style.MyDialog).create();

        dialog.show();
        Window window = dialog.getWindow();

        //w.setGravity(Gravity.BOTTOM);  //此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialog_inout_anim);  //添加动画
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));  // 有白色背景，加

        root = LayoutInflater.from(context).inflate(R.layout.d_video_audio_dialog, null);
        dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        tag1 = root.findViewById(R.id.dialogtag1);
        tag2 = root.findViewById(R.id.dialogtag2);

        video_audio_dialog_time = (TextView) root.findViewById(R.id.video_audio_dialog_time);
        video_audio_dialog_line1 = (TextView) root.findViewById(R.id.video_audio_dialog_line1);
        video_audio_dialog_line2 = (TextView) root.findViewById(R.id.video_audio_dialog_line2);

        dialogtag3 = root.findViewById(R.id.dialogtag3);
        video_audio_dialog_line3a = (TextView) root.findViewById(R.id.video_audio_dialog_line3a);
        video_audio_dialog_line3b = (TextView) root.findViewById(R.id.video_audio_dialog_line3b);

        dialogtag4 = root.findViewById(R.id.dialogtag4);
        video_audio_dialog_line4a = (TextView) root.findViewById(R.id.video_audio_dialog_line4a);
        video_audio_dialog_line4b = (TextView) root.findViewById(R.id.video_audio_dialog_line4b);

        dialogtag5 = root.findViewById(R.id.dialogtag5);
        video_audio_dialog_line5a = (TextView) root.findViewById(R.id.video_audio_dialog_line5a);
        video_audio_dialog_line5b = (TextView) root.findViewById(R.id.video_audio_dialog_line5b);

        dialogtag6 = root.findViewById(R.id.dialogtag6);
        video_audio_dialog_line6a = (TextView) root.findViewById(R.id.video_audio_dialog_line6a);
        video_audio_dialog_line6b = (TextView) root.findViewById(R.id.video_audio_dialog_line6b);

        dialogtag7 = root.findViewById(R.id.dialogtag7);

        video_audio_dialog_line7a = root.findViewById(R.id.video_audio_dialog_line7a);


        linview1 = root.findViewById(R.id.linview1);
        linview2 = root.findViewById(R.id.linview2);

        title = (TextView) root.findViewById(R.id.video_audio_dialog_title);
        content = (TextView) root.findViewById(R.id.video_audio_dialog_content);
        Spanned html = Html.fromHtml("<font color=\"#FF5500\">" + "(返回键)"
                + "</font>");
        content.setText("由于远大视频还在建设之中,目前所有视频和音频均系跳转到第三方平台播放,跳转后如需返回,按手机" + html);
        sure = (Button) root.findViewById(R.id.video_audio_dialog_sure);
        cancel = (Button) root.findViewById(R.id.video_audio_dialog_cancel);


        window.setContentView(root);
    }


    public VideoAudioDialog setRight(CharSequence txt) {
        sure.setText(txt);
        return this;
    }

    public VideoAudioDialog setRightColor(int color) {
        sure.setBackgroundColor(color);
        return this;
    }

    public VideoAudioDialog setLeft(CharSequence txt) {
        cancel.setText(txt);
        return this;
    }


    public VideoAudioDialog showlinview1(int ViState) {
        linview1.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showlinview2(int ViState) {
        linview2.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showcancel(int ViState) {
        cancel.setVisibility(ViState);
        return this;
    }


    public VideoAudioDialog showContent(int ViState) {
        content.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showtag1(int ViState) {
        tag1.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showtag2(int ViState) {
        tag2.setVisibility(ViState);
        return this;
    }


    public VideoAudioDialog showdialogtag3(int ViState) {
        dialogtag3.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showdialogtag4(int ViState) {
        dialogtag4.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showdialogtag5(int ViState) {
        dialogtag5.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showdialogtag6(int ViState) {
        dialogtag6.setVisibility(ViState);
        return this;
    }

    public VideoAudioDialog showdialogtag7(int ViState) {
        dialogtag7.setVisibility(ViState);
        return this;
    }

    public void setTextline3a(CharSequence chars) {
        video_audio_dialog_line3a.setText(chars);
    }

    public void setTextline3b(CharSequence chars) {
        video_audio_dialog_line3b.setText(chars);
    }

    public void setTextline4a(CharSequence chars) {
        video_audio_dialog_line4a.setText(chars);
    }

    public void setTextline4b(CharSequence chars) {
        video_audio_dialog_line4b.setText(chars);
    }

    public void setTextline5a(CharSequence chars) {
        video_audio_dialog_line5a.setText(chars);
    }

    public void setTextline5b(CharSequence chars) {
        video_audio_dialog_line5b.setText(chars);
    }

    public void setTextline6a(CharSequence chars) {
        video_audio_dialog_line6a.setText(chars);
    }

    public void setTextline6b(CharSequence chars) {
        video_audio_dialog_line6b.setText(chars);
    }

    public void settime(CharSequence time) {
        video_audio_dialog_time.setText(time);
    }

    public VideoAudioDialog showtime(int ViState) {
        video_audio_dialog_time.setVisibility(ViState);
        return this;
    }


    public void setlin1(CharSequence line1) {
        video_audio_dialog_line1.setText(line1);
    }

    public void setlin2(CharSequence line2) {
        video_audio_dialog_line2.setText(line2);
    }

    public void setSure(CharSequence title) {
        this.sure.setText(title);
    }

    public void setTitle(CharSequence title) {
        this.title.setText(title);
    }

    public void setContent(CharSequence content) {
        this.content.setText(content);
    }

    public void setLeft(OnClickListener left) {
        this.left = left;
    }

    public void setRight(OnClickListener right) {
        this.right = right;
    }

    public void show() {
        sure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != left) {
                    left.onClick(v);
                }
                dialog.cancel();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != right) {
                    right.onClick(v);
                }
                dialog.cancel();
                dialog.dismiss();
            }
        });


//		dialog.setView(root, 0, 0, 0, 0);


//		dialog.getWindow().getAttributes().width = (UIUtils.getScreenWidth(dialog.getContext())/3)*2;

    }

}
