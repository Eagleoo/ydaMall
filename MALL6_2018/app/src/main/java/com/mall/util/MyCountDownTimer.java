package com.mall.util;

import android.os.CountDownTimer;
import android.widget.Button;

/**
 * Created by Administrator on 2017/12/14.
 */

public  class MyCountDownTimer extends CountDownTimer {

    Button btn_djs;

    public MyCountDownTimer(Button btn_djs, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.btn_djs = btn_djs;
    }

    //计时过程
    @Override
    public void onTick(long l) {
        if (btn_djs!=null){
            //防止计时过程中重复点击
            btn_djs.setClickable(false);
            btn_djs.setText(l / 1000 + "s");
        }

    }

    //计时完毕的方法
    @Override
    public void onFinish() {
        if (btn_djs!=null){
            //重新给Button设置文字
            btn_djs.setText("重新获取验证码");
            //设置可点击
            btn_djs.setClickable(true);
        }

    }

    public void onresetting(){

        if (btn_djs!=null){
            btn_djs.setText("获取验证码");
            //设置可点击
            btn_djs.setClickable(true);
            btn_djs=null;
        }

    }
}