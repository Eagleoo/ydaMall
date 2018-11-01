package com.mall.view.carMall;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.databinding.ActivityRechargeResultBinding;

import java.util.HashMap;
import java.util.Map;

public class RechargeResultActivity extends AppCompatActivity {

    ActivityRechargeResultBinding binding;
    String statepay = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_recharge_result);
        Intent intent = getIntent();
        statepay = intent.getStringExtra("statepay") + "";

        if (statepay.equals("支付成功")) {
            binding.stateimv.setImageResource(R.drawable.order_success);
        } else if (statepay.equals("支付失败")) {
            binding.stateimv.setImageResource(R.drawable.order_fail);
        } else if (statepay.equals("支付取消")) {
            binding.stateimv.setImageResource(R.drawable.order_fail);
        }
        binding.tv.setText(statepay);
        Util.initTitle(this, statepay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        checkpc();
        binding.leftline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void checkpc() {
        getYE();
    }

    private void getYE() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("type", "1,2,3,4,5,6,7,8,9,10,13,14,15,16,17,18");
        NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney",
                map, new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", RechargeResultActivity.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试", RechargeResultActivity.this);
                            return;
                        }

                        String number = "0.00";

                        if (!Util.isNull(json.getString("carpool_coupons"))) {
                            number = json.getString("carpool_coupons");
                        }
                        isGoCar(number);

                    }
                });
    }

    private void isGoCar(String money) {

        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);

        NewWebAPI.getNewInstance().getWebRequest("/" + "carpool.aspx" + "?call=" + "getCarpool_coupons_car" + "&money_="
                        + money.split("\\.")[0]
                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", RechargeResultActivity.this);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", RechargeResultActivity.this);
                            return;
                        }
                        final JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), RechargeResultActivity.this);
                            return;
                        }
                        if (json.getString("type").equals("1")) {
                            binding.centertitle.setText(Html.fromHtml("恭喜!你已获得<font color=\"#FD0F2B\">" + json.getString("car") + "万档</font>汽车拼车资格"));
                            binding.righttv.setText("立即拼车");
                            binding.righttv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Util.showIntent(RechargeResultActivity.this, SelectCarNormActivity.class, new String[]{"money"}
                                            , new String[]{json.getString("car")}
                                    );

                                }
                            });
                        } else {

                            binding.centertitle.setText(Html.fromHtml("亲!还差<font color=\"#FD0F2B\">" + json.getString("money") + "</font>就可参" + "<font color=\"#FD0F2B\">" + json.getString("car") + "万档</font>" + "排队拼车"));
                            binding.righttv.setText("去购物专区");
                            binding.righttv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Util.showIntent(RechargeResultActivity.this, CarShopActivity.class
                                    );
                                }
                            });
                        }


                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }
}
