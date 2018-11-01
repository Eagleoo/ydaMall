package com.mall.util;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.view.R;
import com.mall.view.SetSencondPwdFrame;

public class PayType {
    static ImageView angel_requestCZ11 = null;
    static ImageView angel_requestWX11 = null;
    static ImageView angel_requestZZ11 = null;

    public static void addPayTypeListener(final Activity activity, int defaultViewId, final View twoPwd,final View... views) {
        addPayTypeListener(null, activity, defaultViewId, twoPwd, views);
    }

    public static void addPayTypeListener(final PayTypeClickCallback callback, final Activity activity, int defaultViewId, final View twoPwd, final View... views) {

        OnClickListener click = new OnClickListener() {
            public boolean isfirst = true;

            @Override
            public void onClick(View v) {
                if (!isfirst) {
                    if (null != twoPwd) {
                        if (v.getId() == R.id.pay_item_ali_line
                                || v.getId() == R.id.pay_item_uppay_line
                                || v.getId() == R.id.pay_item_weixin_line) {
                            twoPwd.setVisibility(View.GONE);
                        }
                        else if (v.getId() == R.id.pay_item_escc_line){
                            twoPwd.setVisibility(View.GONE);
                        }
                        else {
                            twoPwd.setVisibility(View.VISIBLE);
                            if ("0".equals(UserData.getUser().getSecondPwd())) {
                                VoipDialog voipDialog = new VoipDialog("为保障您的交易安全，请先设置您的交易密码", activity, "确定", "取消", new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Util.showIntent(activity, SetSencondPwdFrame.class);
                                    }
                                }, null);
                                voipDialog.show();
                            }
                        }
                    }
                } else
                    isfirst = false;
                ImageView angel_requestXJ1 = null;
                ImageView angel_requestCZ1 = null;
                ImageView angel_requestWY1 = null;
                ImageView angel_requestAL1 = null;
                ImageView angel_requestWX1 = null;
                ImageView angel_requestSB1 = null;
                ImageView angel_requestRECCON1 = null;
                ImageView angel_requestZZ1 = null;
                ImageView angel_requestES1 = null;
                ImageView car_pay_item_reccon_radio = null;
                ImageView angel_requestHBZZ = null;
                for (final View v1 : views) {
                    if (R.id.pay_item_ali_line == v1.getId()) {
                        Log.e("k", "1");
                        angel_requestAL1 = (ImageView) v1.findViewById(R.id.pay_item_ali_radio);
                        angel_requestAL1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestAL1.setTag("checked");
                    }

                    if (R.id.pay_item_rec_line == v1.getId()) {
                        Log.e("k", "2");

                        angel_requestCZ1 = (ImageView) v1.findViewById(R.id.pay_item_rec_radio);
                        angel_requestCZ11 = angel_requestCZ1;
                        angel_requestCZ1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestCZ1.setTag("checked");
                    }
                    if (R.id.pay_item_uppay_line == v1.getId()) {
                        Log.e("k", "3");
                        angel_requestWY1 = (ImageView) v1.findViewById(R.id.pay_item_uppay_radio);
                        angel_requestWY1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestWY1.setTag("checked");
                    }
                    if (R.id.pay_item_xj_line == v1.getId()) {
                        Log.e("k", "4");
                        angel_requestXJ1 = (ImageView) v1.findViewById(R.id.pay_item_xj_radio);
                        angel_requestXJ1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestXJ1.setTag("checked");
                    }
                    if (R.id.pay_item_weixin_line == v1.getId()) {
                        Log.e("k", "5");
                        angel_requestWX1 = (ImageView) v1.findViewById(R.id.pay_item_weixin_radio);
                        Log.e("微信添加", "id复制");
                        angel_requestWX11 = angel_requestWX1;
                        angel_requestWX1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestWX1.setTag("checked");
                    }
                    if (R.id.pay_item_sb_line == v1.getId()) {
                        Log.e("k", "6");
                        angel_requestSB1 = (ImageView) v1.findViewById(R.id.pay_item_sb_radio);
                        angel_requestSB1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestSB1.setTag("checked");
                    }
                    if (R.id.pay_item_reccon_line == v1.getId()) {
                        Log.e("k", "7");
                        angel_requestRECCON1 = (ImageView) v1.findViewById(R.id.pay_item_reccon_radio);
                        angel_requestRECCON1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestRECCON1.setTag("checked");
                    }
                    if (R.id.car_pay_item_reccon_line == v1.getId()) {
                        Log.e("k", "8");
                        car_pay_item_reccon_radio = v1.findViewById(R.id.car_pay_item_reccon_radio);
                        car_pay_item_reccon_radio.setImageResource(R.drawable.pay_item_checked);
                        car_pay_item_reccon_radio.setTag("checked");
                    }
                    if (R.id.pay_item_zz_line == v1.getId()) {
                        Log.e("k", "9");
                        angel_requestZZ1 = (ImageView) v1.findViewById(R.id.pay_item_zz_radio);

                        angel_requestZZ11 = angel_requestZZ1;
                        angel_requestZZ1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestZZ1.setTag("checked");
                    }
                    if (R.id.pay_item_zz1_line == v1.getId()) {
                        Log.e("k", "9");
                        angel_requestHBZZ = v1.findViewById(R.id.pay_item_zz1_radio);

                        angel_requestHBZZ.setImageResource(R.drawable.pay_item_checked);
                        angel_requestHBZZ.setTag("checked");
                    }
                    if (R.id.pay_item_escc_line == v1.getId()) {
                        Log.e("k", "escc");
                        angel_requestES1 = (ImageView) v1.findViewById(R.id.pay_item_escc_radio);
                        angel_requestES1.setImageResource(R.drawable.pay_item_checked);
                        angel_requestES1.setTag("checked");
                    }
                }

                if (null != angel_requestXJ1) {
                    Log.e("k", "10");
                    angel_requestXJ1.setTag(null);
                    angel_requestXJ1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestCZ1) {
                    Log.e("k", "11");
                    angel_requestCZ1.setTag(null);
                    angel_requestCZ1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestWY1) {
                    Log.e("k", "12");
                    angel_requestWY1.setTag(null);
                    angel_requestWY1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestWX1) {
                    Log.e("k", "13");
                    angel_requestWX1.setTag(null);
                    angel_requestWX1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestAL1) {
                    Log.e("k", "14");
                    angel_requestAL1.setTag(null);
                    angel_requestAL1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestSB1) {
                    Log.e("k", "15");
                    angel_requestSB1.setTag(null);
                    angel_requestSB1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestES1) {
                    Log.e("k", "15");
                    angel_requestES1.setTag(null);
                    angel_requestES1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestRECCON1) {
                    Log.e("k", "16");
                    angel_requestRECCON1.setTag(null);
                    angel_requestRECCON1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != angel_requestZZ1) {
                    Log.e("k", "17");
                    angel_requestZZ1.setTag(null);
                    angel_requestZZ1.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != car_pay_item_reccon_radio) {
                    Log.e("k", "18");
                    car_pay_item_reccon_radio.setTag(null);
                    car_pay_item_reccon_radio.setImageResource(R.drawable.pay_item_no_checked);
                }
                if (null != car_pay_item_reccon_radio) {
                    Log.e("k", "18");
                    angel_requestHBZZ.setTag(null);
                    angel_requestHBZZ.setImageResource(R.drawable.pay_item_no_checked);
                }

                if (R.id.pay_item_ali_line == v.getId()) {
                    Log.e("k", "19");
                    angel_requestAL1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestAL1.setTag("checked");
                }
                if (R.id.pay_item_rec_line == v.getId()) {
                    Log.e("k", "20");
                    angel_requestCZ1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestCZ1.setTag("checked");
                }
                if (R.id.pay_item_uppay_line == v.getId()) {
                    Log.e("k", "21");
                    angel_requestWY1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestWY1.setTag("checked");
                }
                if (R.id.pay_item_xj_line == v.getId()) {
                    Log.e("k", "22");
                    angel_requestXJ1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestXJ1.setTag("checked");
                }
                if (R.id.pay_item_weixin_line == v.getId()) {
                    Log.e("k", "23");
                    angel_requestWX1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestWX1.setTag("checked");
                }
                if (R.id.pay_item_sb_line == v.getId()) {
                    Log.e("k", "24");
                    angel_requestSB1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestSB1.setTag("checked");
                }
                if (R.id.pay_item_reccon_line == v.getId()) {
                    Log.e("k", "25");
                    angel_requestRECCON1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestRECCON1.setTag("checked");
                }
                if (R.id.pay_item_zz_line == v.getId()) {
                    Log.e("k", "26");
                    angel_requestZZ1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestZZ1.setTag("checked");
                }
                if (R.id.car_pay_item_reccon_line == v.getId()) {
                    Log.e("k", "38");
                    car_pay_item_reccon_radio.setImageResource(R.drawable.pay_item_checked);
                    car_pay_item_reccon_radio.setTag("checked");
                }
                if (R.id.pay_item_zz1_line == v.getId()) {
                    Log.e("k", "27");
                    angel_requestHBZZ.setImageResource(R.drawable.pay_item_checked);
                    angel_requestHBZZ.setTag("checked");
                }
                if (R.id.pay_item_escc_line == v.getId()) {
                    Log.e("k", "24");
                    angel_requestES1.setImageResource(R.drawable.pay_item_checked);
                    angel_requestES1.setTag("checked");
                }
                if (null != callback)
                    callback.click(v);
            }
        };
        for (View v : views) {
            v.setOnClickListener(click);
//			if (defaultViewId!=-1){
            if (v.getId() == defaultViewId) {
                v.performClick();
            }

        }
    }

    public static String getPayType(Activity activity) {
        View angel_requestAL1 = activity.findViewById(R.id.pay_item_ali_radio);
        View angel_requestCZ1 = activity.findViewById(R.id.pay_item_rec_radio);
        View angel_requestWY1 = activity.findViewById(R.id.pay_item_uppay_radio);
        View angel_requestXJ1 = activity.findViewById(R.id.pay_item_xj_radio);
        View angel_requestWX1 = activity.findViewById(R.id.pay_item_weixin_radio);
        View angel_requestSB1 = activity.findViewById(R.id.pay_item_sb_radio);
        View angel_requestZZ1 = activity.findViewById(R.id.pay_item_zz_radio);
        View angel_requestES1 = activity.findViewById(R.id.pay_item_escc_radio);
        View angel_requestRECCON1 = activity.findViewById(R.id.pay_item_reccon_radio);
        View car_pay_item_reccon_radio = activity.findViewById(R.id.car_pay_item_reccon_radio);
        View angel_requestHBZZ = activity.findViewById(R.id.pay_item_zz1_radio);


        if ("checked".equals(angel_requestXJ1.getTag() + ""))
            return "1";
        if ("checked".equals(angel_requestCZ1.getTag() + ""))
            return "2";
        if ("checked".equals(angel_requestWY1.getTag() + ""))
            return "3";
        if ("checked".equals(angel_requestAL1.getTag() + ""))
            return "4";
        if ("checked".equals(angel_requestSB1.getTag() + ""))
            return "5";
        if ("checked".equals(angel_requestWX1.getTag() + ""))
            return "6";
        if ("checked".equals(angel_requestRECCON1.getTag() + ""))
            return "7";
        if ("checked".equals(angel_requestZZ1.getTag() + ""))
            return "9";
        if ("checked".equals(car_pay_item_reccon_radio.getTag() + ""))
            return "10";
        if ("checked".equals(angel_requestHBZZ.getTag() + ""))
            return "11";
        if ("checked".equals(angel_requestES1.getTag() + ""))
            return "12";
        return "";
    }

    public static ImageView getPoint1() {
        return angel_requestCZ11;
    }

    public static ImageView getPoint2() {  //微信
        return angel_requestWX11;
    }

    public static ImageView getPoint3() {
        return angel_requestZZ11;
    }

}
