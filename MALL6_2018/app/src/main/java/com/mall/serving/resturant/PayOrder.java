package com.mall.serving.resturant;

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

import com.mall.net.Web;
import com.mall.serving.filmticket.PayRoomFee;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class PayOrder extends Activity {
    private List<ImageView> radios = new ArrayList<ImageView>();
    private ImageView pay_by_bank, pay_by_alipay, pay_by_weixin, pay_by_cft, pay_by_account, pay_by_sb;
    private String choose = "银联支付", ordernumber = "", phone = "", payType = "3", paramString;
    private TextView customer_phone, hotel_name;
    private TextView room_number, total_price;
    private Button submit;
    private String hotelname = "", totalprice = "", roomcount = "";
    private TextView sbmoney, zhanghumoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_order);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getMoney();
    }


    private void getMoney() {
        Util.asynTask(PayOrder.this, "获取账户余额...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取充值账户失败！", PayOrder.this);
                    return;
                }
                HashMap<String, Object> map = (HashMap<String, Object>) runData;
                Object rec = map.get("rec");
                Object sb = map.get("sb");
                if (null == rec) {
                    Util.show("获取充值账户失败！", PayOrder.this);
                    return;
                }
                if (null == sb) {
                    Util.show("获取商币账户失败！", PayOrder.this);
                    return;
                }
                zhanghumoney.setText("余额：￥" + rec + "");
                sbmoney.setText("余额：" + sb + "");
            }

            @Override
            public Serializable run() {
                if (null == UserData.getUser())
                    return null;
                Web recWeb = new Web(Web.getMoney, "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&type=rec");
                Web sbWeb = new Web(Web.getMoney, "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&type=sb");
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("rec", recWeb.getPlan());
                map.put("sb", sbWeb.getPlan());
                return map;
            }
        });
    }

    private void init() {
        Util.initTitle(PayOrder.this, "订单支付", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                PayOrder.this.finish();
            }
        });
        getIntentData();
        sbmoney = (TextView) this.findViewById(R.id.sbmoney);
        zhanghumoney = (TextView) this.findViewById(R.id.zhanghu_money);
        hotel_name = (TextView) this.findViewById(R.id.hotel_name);
        hotel_name.setText(hotelname);
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
        customer_phone = (TextView) this.findViewById(R.id.customer_phone);
        customer_phone.setText(phone);
        total_price = (TextView) this.findViewById(R.id.total_price);
        room_number = (TextView) this.findViewById(R.id.room_number);
        total_price.setText("￥" + totalprice + "元");
        room_number.setText(roomcount);
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (payType.equals("2")) {
                    //充值账户
                    LayoutInflater infl = LayoutInflater.from(PayOrder.this);
                    View view = infl.inflate(R.layout.input_second_pwd, null);
                    final EditText pwd = (EditText) view.findViewById(R.id.second_pwd);
                    pwd.setFocusable(true);
                    Button submit = (Button) view.findViewById(R.id.submit);
                    final Dialog ad = new Dialog(PayOrder.this, R.style.CustomDialogStyle);
                    ad.show();
                    Window window = ad.getWindow();
                    WindowManager.LayoutParams pa = window.getAttributes();
                    pa.width = Util.dpToPx(PayOrder.this, 250);
                    window.setBackgroundDrawable(new ColorDrawable(0));
                    window.setContentView(view, pa);
                    submit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Util.isNull(pwd.getText().toString())) {
                                Toast.makeText(PayOrder.this, "请输入交易密码", Toast.LENGTH_LONG).show();
                            }
                            ad.dismiss();
                            getWriteOrderNumber(pwd.getText().toString());
                        }
                    });
                } else {
                    getWriteOrderNumber("");
                }
            }
        });
    }

    private void getWriteOrderNumber(final String pwd) {
        Util.asynTask(PayOrder.this, "正在生成", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                String orderno = "";
                String price = "";
                System.out.println("result=" + result);
                if (result.contains("success")) {
                    if (result.equals("success")) {
                        Toast.makeText(PayOrder.this, "支付成功。", Toast.LENGTH_LONG).show();
                        Util.showIntent(PayOrder.this, ResturantIndex.class);
                    } else if (result.equals("success:7030")) {
                        Toast.makeText(PayOrder.this, "支付成功", Toast.LENGTH_LONG).show();
                        PayOrder.this.finish();
                    } else {
                        if (result.contains("success")) {
                            try {
                                orderno = result.split(":")[1];
                                price = result.split(":")[2];
                                Intent intent = new Intent(PayOrder.this, PayRoomFee.class);
                                intent.putExtra("payWay", choose);
                                intent.putExtra("hotelname", hotelname);
                                intent.putExtra("ordernumber", orderno);
                                System.out.println("订单号===" + ordernumber);
                                intent.putExtra("price", price);
                                PayOrder.this.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(PayOrder.this, "", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    Toast.makeText(PayOrder.this, result, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.writerHotelOrder, paramString + "&payType=" + payType + "&twoPwd=" + new MD5().getMD5ofStr(pwd));
                String result = web.getPlan();
                return result;
            }
        });
    }

    private void getIntentData() {
        hotelname = this.getIntent().getStringExtra("hotelname");
        totalprice = this.getIntent().getStringExtra("totalprice");
        roomcount = this.getIntent().getStringExtra("roomcount");
        phone = this.getIntent().getStringExtra("phone");
        paramString = this.getIntent().getStringExtra("param");
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
            } else if (index == 1) {
                choose = "支付宝支付";
                payType = "4";
            } else if (index == 2) {
                choose = "微信支付";
            } else if (index == 3) {
                choose = "财付通支付";
            } else if (index == 4) {
                choose = "云商账户支付";
                payType = "2";
            } else if (index == 5) {
                choose = "商币支付";
                payType = "5";
            }
        }
    }
}
