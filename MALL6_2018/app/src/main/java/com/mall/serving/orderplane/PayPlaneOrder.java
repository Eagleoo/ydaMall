package com.mall.serving.orderplane;

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
import com.mall.serving.filmticket.PayRoomFee;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

/**
 * 支付界面废弃
 */
public class PayPlaneOrder extends Activity {
    private String gssOrderId = "", choose = "银联支付", startdate,
            takeoffairport, landingairport, takeofftime, landingtime,
            mobile = "", flightno = "", seatnum = "", orderno = "", price = "",
            con_name = "", payType = "3";
    private TextView city_to_city, take_off_time, ticket_counts, hangban,
            time_to_time, ordernoT, total_price, customer_name, customer_phone;
    private List<ImageView> radios = new ArrayList<ImageView>();
    private ImageView pay_by_bank, pay_by_alipay, pay_by_weixin, pay_by_cft,
            pay_by_account, pay_by_sb;
    private Button submit;
    private String pnr = "", pType = "1";
    private TextView sbmoney, zhanghumoney;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payplaneorder);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMoney();
    }

    private void getMoney() {
        Util.asynTask(this, "获取账户余额...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取充值账户失败！", PayPlaneOrder.this);
                    return;
                }
                HashMap<String, Object> map = (HashMap<String, Object>) runData;
                Object rec = map.get("rec");
                Object sb = map.get("sb");
                if (null == rec) {
                    Util.show("获取充值账户失败！", PayPlaneOrder.this);
                    return;
                }
                if (null == sb) {
                    Util.show("获取商币账户失败！", PayPlaneOrder.this);
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

    private void init() {
        user = new UserData().getUser();
        if (user == null) {
            Toast.makeText(PayPlaneOrder.this, "您还未登录，请先登录", 0).show();
            Util.showIntent(this, LoginFrame.class);
        }
        Util.initTitle(this, "订单支付", new OnClickListener() {
            @Override
            public void onClick(View v) {
                PayPlaneOrder.this.finish();
            }
        });
        initView();
        getIntentData();
        getTicketMoney();
    }

    private void getTicketMoney() {
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                orderno = "";
                if (result.contains("success")) {
                    orderno = result.split(":")[1];
                    ordernoT.setText(orderno);
                    gssOrderId = result.split(":")[2];
                    price = result.split(":")[3];
                    total_price.setText("￥" + price + "元");
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.getTicketMoney,
                        "userid=" + user.getUserId() + "&md5pwd=" + user.getMd5Pwd()
                                + "&pnr=" + pnr + "&pType=" + pType
                                + "&gssOrderid=" + gssOrderId);
                String result = web.getPlan();
                return result;
            }
        });
    }

    private void payByYD(final String pwd, final String payType) {
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                System.out.println("result====" + result);
                String ordernumber = "";
                String price = "";
                // success:orderno:price
                if (result.equals("success")) {
                    Toast.makeText(PayPlaneOrder.this, "支付成功!",
                            Toast.LENGTH_LONG).show();
                    PayPlaneOrder.this.finish();
                } else if (result.contains("success")
                        && result.split(":").length == 3) {
                    ordernumber = result.split(":")[1];
                    price = result.split(":")[2];
                    Intent intent = new Intent(PayPlaneOrder.this,
                            PayRoomFee.class);
                    intent.putExtra("payWay", choose);
                    intent.putExtra("ordernumber", ordernumber);
                    intent.putExtra("price", price);
                    intent.putExtra("desc", "远大云商android客户端订机票订单支付");
                    PayPlaneOrder.this.startActivity(intent);
                } else {
                    Toast.makeText(PayPlaneOrder.this, result,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.payTicketOrder,
                        "userid=" + UserData.getUser().getUserId() + "&md5pwd="
                                + UserData.getUser().getMd5Pwd() + "&twoPwd="
                                + new MD5().getMD5ofStr(pwd) + "&gssOrderid="
                                + gssOrderId + "&pnr=" + pnr + "&payType="
                                + payType);
                return web.getPlan();
            }
        });
    }

    private void getIntentData() {
        Intent intent = this.getIntent();
        pnr = intent.getStringExtra("pnr");
        gssOrderId = intent.getStringExtra("gassid");
        takeoffairport = intent.getStringExtra("startcity");
        landingairport = intent.getStringExtra("landingcity");
        seatnum = intent.getStringExtra("seatnum");
        flightno = intent.getStringExtra("flightno");
        takeofftime = intent.getStringExtra("takeofftime");
        landingtime = intent.getStringExtra("landingtime");
        con_name = intent.getStringExtra("con_name");
        mobile = intent.getStringExtra("con_phone");
        startdate = intent.getStringExtra("startdate");
        city_to_city.setText(takeoffairport + "-" + landingairport);
        take_off_time.setText(startdate);
        ticket_counts.setText(seatnum);
        hangban.setText(flightno);
        time_to_time.setText(takeofftime + " 起飞\n" + landingtime + " 降落");
        // ordernoT.setText(orderno);
        customer_name.setText(con_name);
        customer_phone.setText(mobile);
    }

    private void initView() {
        sbmoney = (TextView) this.findViewById(R.id.sbmoney);
        zhanghumoney = (TextView) this.findViewById(R.id.zhanghu_money);
        city_to_city = (TextView) this.findViewById(R.id.city_to_city);
        take_off_time = (TextView) this.findViewById(R.id.take_off_time);
        ticket_counts = (TextView) this.findViewById(R.id.ticket_counts);
        hangban = (TextView) this.findViewById(R.id.hangban);
        time_to_time = (TextView) this.findViewById(R.id.time_to_time);
        ordernoT = (TextView) this.findViewById(R.id.orderno);
        total_price = (TextView) this.findViewById(R.id.total_price);
        customer_name = (TextView) this.findViewById(R.id.customer_name);
        customer_phone = (TextView) this.findViewById(R.id.customer_phone);
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
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (payType.equals("2") || payType.equals("5")) {
                    // //充值账户
                    // AlertDialog ad = new
                    // AlertDialog.Builder(PayPlaneOrder.this).create();
                    LayoutInflater infl = LayoutInflater
                            .from(PayPlaneOrder.this);
                    View view = infl.inflate(R.layout.input_second_pwd, null);
                    final EditText pwd = (EditText) view
                            .findViewById(R.id.second_pwd);
                    pwd.setFocusable(true);
                    Button submit = (Button) view.findViewById(R.id.submit);
                    // final AlertDialog dialog = builder.show();
                    // Window window = dialog.getWindow();
                    // WindowManager.LayoutParams pa=window.getAttributes();
                    // pa.width=Util.dpToPx(PayPlaneOrder.this, 250);
                    // dialog.setContentView(view,pa);
                    // ad.show();
                    // WindowManager.LayoutParams
                    // pa=ad.getWindow().getAttributes();
                    // pa.width=Util.dpToPx(PayPlaneOrder.this, 250);
                    // ad.setView(view);
                    final Dialog ad = new Dialog(PayPlaneOrder.this,
                            R.style.CustomDialogStyle);
                    ad.show();
                    Window window = ad.getWindow();
                    WindowManager.LayoutParams pa = window.getAttributes();
                    pa.width = Util.dpToPx(PayPlaneOrder.this, 250);
                    window.setBackgroundDrawable(new ColorDrawable(0));
                    window.setContentView(view, pa);
                    submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Util.isNull(pwd.getText().toString())) {
                                Toast.makeText(PayPlaneOrder.this, "请输入交易密码",
                                        Toast.LENGTH_LONG).show();
                            }
                            ad.dismiss();
                            payByYD(pwd.getText().toString(), payType);
                        }
                    });
                } else {
                    payByYD("", payType);
                }
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
            } else if (view.getTag().equals("selected")) {
                view.setImageResource(R.drawable.radiobutton_up);
                view.setTag("noselected");
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
                total_price.setText("￥" + price + "元");
            } else if (index == 1) {
                choose = "支付宝支付";
                payType = "4";
                total_price.setText("￥" + price + "元");
            } else if (index == 2) {
                choose = "微信支付";
                payType = "6";
                total_price.setText("￥" + price + "元");
            } else if (index == 3) {
                choose = "财付通支付";
                total_price.setText("￥" + price + "元");
            } else if (index == 4) {
                choose = "云商账户支付";
                payType = "2";
                total_price.setText("￥" + price + "元");
            } else if (index == 5) {
                choose = "商币支付";
                payType = "5";
                total_price.setText("商币："
                        + Util.getDouble(Double.parseDouble(price) * 10, 2));
            }
        }
    }

}
