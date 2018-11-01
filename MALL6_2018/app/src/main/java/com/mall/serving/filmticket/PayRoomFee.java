package com.mall.serving.filmticket;

import net.sourceforge.mm.MMPay;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

public class PayRoomFee extends Activity {
    private String payWay = "支付宝";
    private String order = "";
    private double totalPrice = 1000;
    private ProgressDialog mProgress = null;
    private String hotelname;
    private String userid;
    private LinearLayout order_callback_layout;
    private TextView order_callback, hotel_name, ordernumer;
    private String desc = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payroomfee);
        init();
    }

    private void init() {
        User user = UserData.getUser();
        if (null == user) {
            Toast.makeText(PayRoomFee.this, "您还没有登录,请先登录", 0).show();
            Util.showIntent(PayRoomFee.this, LoginFrame.class);
            return;
        } else {
            userid = user.getUserId();
        }
        Util.initTitle(PayRoomFee.this, "订单支付", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PayRoomFee.this.finish();
            }
        });
        getIntentData();
        order_callback_layout = (LinearLayout) this.findViewById(R.id.order_callback_layout);
        hotel_name = (TextView) this.findViewById(R.id.hotel_name);
        ordernumer = (TextView) this.findViewById(R.id.ordernumer);
        order_callback = (TextView) this.findViewById(R.id.order_callback);
    }

    private void getIntentData() {
        if (this.getIntent() != null) {
            desc = this.getIntent().getStringExtra("desc");
            order = this.getIntent().getStringExtra("ordernumber");
            if (!Util.isNull(this.getIntent().getStringExtra("price"))) {
                totalPrice = Double.parseDouble(this.getIntent().getStringExtra("price"));
            }
            if (!Util.isNull(this.getIntent().getStringExtra("planeprice"))) {
                totalPrice = Integer.parseInt(this.getIntent().getStringExtra("planeprice"));
            }
            if (Util.isNull(this.getIntent().getStringExtra("payWay"))) {
            } else {
                payWay = this.getIntent().getStringExtra("payWay");
            }
            if (payWay.equals("微信支付")) {
                CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(PayRoomFee.this, "微信支付中...");
                System.out.println("-----------totalPrice===" + totalPrice + "---------order====" + order + "---desc=" + desc);
                new MMPay(PayRoomFee.this, pd, totalPrice, order, desc).pay();

            }
        }
    }

    private void ordercallback() {
        order_callback_layout.setVisibility(View.VISIBLE);
        order_callback.setText("您的订单已经提交失败！\n详情请拨打下方的热线咨询电话。");
        hotel_name.setText(hotelname);
        ordernumer.setText(order);
    }

    // 支付宝
    private void payByAliPay() {
        AliPayNet payNet = new AliPayNet(PayRoomFee.this);
        payNet.pay(order, "联盟商家Android客户端支付订单", totalPrice, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                System.out.println("---------aliResultCode=====" + aliResultCode);
                order_callback_layout = (LinearLayout) PayRoomFee.this.findViewById(R.id.order_callback_layout);
                order_callback = (TextView) PayRoomFee.this.findViewById(R.id.order_callback);
                ordernumer = (TextView) PayRoomFee.this.findViewById(R.id.ordernumer);
                order_callback_layout.setVisibility(View.VISIBLE);
                ordernumer.setText("您的订单号：" + order);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Toast.makeText(PayRoomFee.this, "支付失败", Toast.LENGTH_LONG).show();
                order_callback_layout.setVisibility(View.VISIBLE);
                order_callback.setText("很抱歉，您的订单没有支付成功，您可以更换支付方式，或者联系我们");
            }
        });
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
            return true;
        }

        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            mProgress.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String result = data.getExtras().getString("pay_result");
            System.out.println("银联支付结果=====" + result);
            if (result.equalsIgnoreCase("success")) {
                order_callback_layout.setVisibility(View.VISIBLE);
                ordernumer.setText("订单号：" + order);
            } else if (result.equalsIgnoreCase("failed")) {
                order_callback_layout.setVisibility(View.VISIBLE);
                order_callback.setText("银联支付失败。您可以更换支付方式，或则联系我们");
                ordernumer.setText("订单号：" + order);
            } else if (result.equalsIgnoreCase("cancel")) {
                order_callback_layout.setVisibility(View.VISIBLE);
                order_callback.setText("您取消了支付订单");
                ordernumer.setText("订单号：" + order);
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}