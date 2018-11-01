package com.mall.serving.filmticket;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MyToast;

public class PayCommonTicket extends Activity {
    private List<ImageView> radios = new ArrayList<ImageView>();
    private ImageView pay_by_bank, pay_by_alipay, pay_by_weixin, pay_by_cft,
            pay_by_account, pay_by_sb;
    private TextView sbmoney, zhanghumoney, total_price, order, goods_name;
    private String choose = "银联支付", ordernumber = "", totalprice = "",
            payType = "3", goodsname = "";
    private Button submit;
    private User user;
    private int screenwidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_common_ticket);
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        } else {
            screenwidth = getWindowManager().getDefaultDisplay().getWidth();
            user = UserData.getUser();
            init();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void init() {
        Util.initTitle(PayCommonTicket.this, "订单支付", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PayCommonTicket.this.finish();
            }
        });
        initView();
        getMoney();
        getIntentdata();
    }

    private void getIntentdata() {
        goodsname = this.getIntent().getStringExtra("goodsname");
        totalprice = this.getIntent().getStringExtra("totalprice");
        ordernumber = this.getIntent().getStringExtra("ordernumber");
        order.setText("订单编号：" + ordernumber);
        total_price.setText("订单金额：" + totalprice);
        goods_name.setText(goodsname);
    }

    private void initView() {
        sbmoney = (TextView) this.findViewById(R.id.sbmoney);
        zhanghumoney = (TextView) this.findViewById(R.id.zhanghu_money);
        pay_by_alipay = (ImageView) this.findViewById(R.id.pay_by_alipay);
        pay_by_weixin = (ImageView) this.findViewById(R.id.pay_by_weixin);
        pay_by_cft = (ImageView) this.findViewById(R.id.pay_by_cft);
        pay_by_account = (ImageView) this.findViewById(R.id.pay_by_account);
        pay_by_sb = (ImageView) this.findViewById(R.id.pay_by_sb);
        radios.add(pay_by_bank);
        radios.add(pay_by_alipay);
        radios.add(pay_by_weixin);
        radios.add(pay_by_cft);
        radios.add(pay_by_account);
        radios.add(pay_by_sb);
        pay_by_bank.setOnClickListener(new RadioButtonOnClick(0));
        pay_by_alipay.setOnClickListener(new RadioButtonOnClick(1));
        pay_by_weixin.setOnClickListener(new RadioButtonOnClick(2));
        pay_by_cft.setOnClickListener(new RadioButtonOnClick(3));
        pay_by_account.setOnClickListener(new RadioButtonOnClick(4));
        pay_by_sb.setOnClickListener(new RadioButtonOnClick(5));
        goods_name = (TextView) this.findViewById(R.id.goodsname);
        order = (TextView) this.findViewById(R.id.order);
        total_price = (TextView) this.findViewById(R.id.price);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payType.equals("2") || payType.equals("5")) {
                    // 充值账户
                    LayoutInflater infl = LayoutInflater
                            .from(PayCommonTicket.this);
                    View view = infl.inflate(R.layout.input_second_pwd, null);
                    final EditText pwd = (EditText) view
                            .findViewById(R.id.second_pwd);
                    pwd.setFocusable(true);
                    Button submit = (Button) view.findViewById(R.id.submit);
                    final Dialog ad = new Dialog(PayCommonTicket.this,
                            R.style.CustomDialogStyle);
                    ad.show();
                    Window window = ad.getWindow();
                    WindowManager.LayoutParams pa = window.getAttributes();
                    pa.width = screenwidth * 4 / 5;
                    window.setBackgroundDrawable(new ColorDrawable(0));
                    window.setContentView(view, pa);
                    submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Util.isNull(pwd.getText().toString())) {
                                Toast.makeText(PayCommonTicket.this, "请输入交易密码",
                                        Toast.LENGTH_LONG).show();
                            }
                            ad.dismiss();
                            pay(new MD5().getMD5ofStr(pwd.getText().toString()));
                        }
                    });
                } else {
                    pay("");
                }

            }
        });
    }

    private void getMoney() {
        Util.asynTask(this, "获取账户余额...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取充值账户失败！", PayCommonTicket.this);
                    return;
                }
                HashMap<String, Object> map = (HashMap<String, Object>) runData;
                Object rec = map.get("rec");
                Object sb = map.get("sb");
                if (null == rec) {
                    Util.show("获取充值账户失败！", PayCommonTicket.this);
                    return;
                }
                if (null == sb) {
                    Util.show("获取商币账户失败！", PayCommonTicket.this);
                    return;
                }
                zhanghumoney.setText("余额：￥" + rec + "");
                sbmoney.setText("余额：" + sb + "");
            }

            @Override
            public Serializable run() {
                if (null == UserData.getUser())
                    return null;
                Web recWeb = new Web(Web.getMoney, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&type=rec");
                Web sbWeb = new Web(Web.getMoney, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&type=sb");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("rec", recWeb.getPlan());
                map.put("sb", sbWeb.getPlan());
                return map;
            }
        });
    }

    private void pay(final String twopwd) {
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if (result.equals("success")) {
                    MyToast.makeText(PayCommonTicket.this,
                            "支付成功，电子码将以短信形式发送到您的手机", 5).show();
                    PayCommonTicket.this.finish();
                } else if (result.contains("success:")) {
                    String price, order;
                    if (result.split(":").length == 3) {
                        price = result.split(":")[2];
                        order = result.split(":")[1];
                        Intent intent = new Intent(PayCommonTicket.this,
                                PayRoomFee.class);
                        intent.putExtra("price", price);
                        intent.putExtra("ordernumber", ordernumber);
                        intent.putExtra("payWay", choose);
                        PayCommonTicket.this.startActivity(intent);
                    }
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service,
                        Web.pay_filmCommTicket, "userId=" + user.getUserId()
                        + "&md5Pwd=" + user.getMd5Pwd() + "&payType="
                        + payType + "&orderId=" + ordernumber
                        + "&twopwd=" + twopwd);
                return web.getPlan();
            }
        });
    }

    private class RadioButtonOnClick implements OnClickListener {
        private int index = 0;

        public RadioButtonOnClick(int index) {
            this.index = index;
        }

        @Override
        public void onClick(View v) {
            ImageView view = (ImageView) v;
            if (view.getTag().equals("noselected")) {
                view.setImageResource(R.drawable.radiobutton_down);
                view.setTag("selected");
            }
            for (int i = 0; i < radios.size(); i++) {
                if (i != index) {
                    ImageView imageview = radios.get(i);
                    imageview.setImageResource(R.drawable.radiobutton_up);
                    imageview.setTag("noselected");
                }
            }
            if (index == 0) {
                choose = "银联支付";
                payType = "3";
                total_price.setText("订单金额：" + totalprice + "元");
            } else if (index == 1) {
                choose = "支付宝支付";
                payType = "4";
                total_price.setText("订单金额：" + totalprice + "元");
            } else if (index == 2) {
                choose = "微信支付";
                total_price.setText("订单金额：" + totalprice + "元");
            } else if (index == 3) {
                choose = "财付通支付";
                total_price.setText("订单金额：" + totalprice + "元");
            } else if (index == 4) {
                choose = "云商账户支付";
                payType = "2";
                total_price.setText("订单金额：" + totalprice + "元");
            } else if (index == 5) {
                choose = "商币支付";
                payType = "5";
                total_price
                        .setText("商币："
                                + Util.getDouble(
                                Double.parseDouble(totalprice) * 10, 2));
            }
        }
    }
}
