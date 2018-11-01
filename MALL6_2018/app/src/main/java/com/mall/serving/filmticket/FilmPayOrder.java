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
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MyToast;


public class FilmPayOrder extends Activity {

    private List<ImageView> radios = new ArrayList<ImageView>();
    private ImageView pay_by_bank, pay_by_alipay, pay_by_weixin, pay_by_cft,
            pay_by_account, pay_by_sb;
    private String choose = "银联支付", ordernumber = "";
    private Button submit;
    private String theatername = "", totalprice = "", time = "", seat = "",
            name = "", cityNo = "", filmNo = "", cinemaNo = "", seqNo = "",
            HallNo = "", filmName = "", frontimage = "", fangyingS = "";
    private String itemPrice = "";
    private int Seats = 0;
    private EditText customer_phone;
    private TextView theater_name, moive_name, watch_time, seatT, total_price;
    private String SeatsString = "", payType = "3";
    private String userId = "";
    private String md5Pwd = "";
    private String secondpwd = "";
    private UserInfo userInfo;
    private User user;
    private LinearLayout pwd_layout;
    private EditText pwdEdit;
    private String orderno = "", orderprice = "";
    private TextView sbmoney, zhanghumoney;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filmpayoder);
        user = UserData.getUser();
        if (user == null) {
            Util.showIntent(this, LoginFrame.class);
        } else {
            init();
            getMoney();
        }

    }

    private void getMoney() {
        Util.asynTask(this, "获取账户余额...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取充值账户失败！", FilmPayOrder.this);
                    return;
                }
                HashMap<String, Object> map = (HashMap<String, Object>) runData;
                Object rec = map.get("rec");
                Object sb = map.get("sb");
                if (null == rec) {
                    Util.show("获取充值账户失败！", FilmPayOrder.this);
                    return;
                }
                if (null == sb) {
                    Util.show("获取商币账户失败！", FilmPayOrder.this);
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
        Util.initTitle(FilmPayOrder.this, "电影订单支付", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                FilmPayOrder.this.finish();
            }
        });
        initView();
        getIntentData();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initView() {
        sbmoney = (TextView) this.findViewById(R.id.sbmoney);
        zhanghumoney = (TextView) this.findViewById(R.id.zhanghu_money);
        theater_name = (TextView) this.findViewById(R.id.theater_name);
        moive_name = (TextView) this.findViewById(R.id.moive_name);
        watch_time = (TextView) this.findViewById(R.id.watch_time);
        seatT = (TextView) this.findViewById(R.id.seatT);
        total_price = (TextView) this.findViewById(R.id.total_price);

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
        customer_phone = (EditText) this.findViewById(R.id.customer_phone);
        if (!Util.isNull(user.getMobilePhone())) {
            customer_phone.setText(user.getMobilePhone());
        }
        customer_phone.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText e = (EditText) v;
                if (hasFocus) {
                    e.setBackgroundResource(R.drawable.editext_no_border_focus);
                } else {
                    e.setBackgroundResource(R.drawable.editext_no_border);
                }
            }
        });
        submit = (Button) this.findViewById(R.id.submit);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = UserData.getUser();
                if (null == user) {
                    Toast.makeText(FilmPayOrder.this, "您还没有登陆,请先登录", 0).show();
                    Util.showIntent(FilmPayOrder.this, LoginFrame.class);
                    return;
                } else {
                    userId = user.getUserId();
                    md5Pwd = user.getMd5Pwd();
                }
                if (!Util.isNull(customer_phone.getText().toString())
                        && Util.checkPhoneNumber(customer_phone.getText().toString())) {
                    if (payType.equals("2") || payType.equals("5")) {
                        //充值账户
                        LayoutInflater infl = LayoutInflater.from(FilmPayOrder.this);
                        View view = infl.inflate(R.layout.input_second_pwd, null);
                        final EditText pwd = (EditText) view.findViewById(R.id.second_pwd);
                        pwd.setFocusable(true);
                        Button submit = (Button) view.findViewById(R.id.submit);
                        final Dialog ad = new Dialog(FilmPayOrder.this, R.style.CustomDialogStyle);
                        ad.show();
                        Window window = ad.getWindow();
                        WindowManager.LayoutParams pa = window.getAttributes();
                        pa.width = Util.dpToPx(FilmPayOrder.this, 250);
                        window.setBackgroundDrawable(new ColorDrawable(0));
                        window.setContentView(view, pa);
                        submit.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (Util.isNull(pwd.getText().toString())) {
                                    Toast.makeText(FilmPayOrder.this, "请输入交易密码", Toast.LENGTH_LONG).show();
                                }
                                ad.dismiss();
                                getOrder(new MD5().getMD5ofStr(pwd.getText().toString()));

                            }
                        });
                    } else {
                        if (Util.isNull(orderno) && Util.isNull(orderprice)) {
                            getOrder("");
                        } else if (!Util.isNull(orderno) && !Util.isNull(orderprice)) {
                            Intent intent = new Intent(FilmPayOrder.this, PayRoomFee.class);
                            intent.putExtra("price", orderprice);
                            intent.putExtra("ordernumber", orderno);
                            intent.putExtra("payWay", choose);
                            intent.putExtra("desc", "远大云商android客户端电影票订单支付");
                            FilmPayOrder.this.startActivity(intent);
                        }
                    }
                } else {
                    Toast.makeText(FilmPayOrder.this, "手机号格式不正确", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getIntentData() {
        theatername = this.getIntent().getStringExtra("theatername");
        totalprice = this.getIntent().getStringExtra("price");
        name = this.getIntent().getStringExtra("name");
        time = this.getIntent().getStringExtra("watchtime");
        seat = this.getIntent().getStringExtra("seat");
        filmName = this.getIntent().getStringExtra("filmName");
        cityNo = this.getIntent().getStringExtra("cityNo");
        filmNo = this.getIntent().getStringExtra("filmno");
        cinemaNo = this.getIntent().getStringExtra("cinemaNo");
        seqNo = this.getIntent().getStringExtra("seqno");
        HallNo = this.getIntent().getStringExtra("HallNo");
        itemPrice = this.getIntent().getStringExtra("oneprice");
        frontimage = this.getIntent().getStringExtra("frontimage");
        fangyingS = this.getIntent().getStringExtra("fangying");
        Seats = this.getIntent().getIntExtra("seats", 1);
        SeatsString = this.getIntent().getStringExtra("Seats");
        theater_name.setText(theatername);
        moive_name.setText(name);
        watch_time.setText(time);
        seatT.setText(seat);
        total_price.setText(totalprice);
    }

    private void getOrder(final String pwd) {
        Util.asynTask(FilmPayOrder.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                orderno = "";
                orderprice = "";
                if (result.contains("success")) {
                    if (result.equals("success")) {
                        Toast.makeText(FilmPayOrder.this, "支付成功。",
                                Toast.LENGTH_LONG).show();
                        FilmPayOrder.this.finish();
                    } else if (result.equals("success:7030")) {
                        Toast.makeText(FilmPayOrder.this, "支付成功,请到", Toast.LENGTH_LONG).show();
                    } else {
                        if (result.contains("success")) {
                            try {
                                orderno = result.split(":")[1];
                                orderprice = result.split(":")[2];
                                Intent intent = new Intent(FilmPayOrder.this, PayRoomFee.class);
                                intent.putExtra("price", orderprice);
                                intent.putExtra("ordernumber", orderno);
                                intent.putExtra("payWay", choose);
                                FilmPayOrder.this.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(FilmPayOrder.this, "生成订单失败", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                } else {
                    if (result.contains("锁定座位失败")) {
                        result += ".您预订的作为可能已经被其他人抢先预订。";
                    }
                    MyToast.makeText(FilmPayOrder.this, result, 5).show();
                }
                System.out.println(result);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service,
                        Web.Film_createSeatTicketOrder, "Mobile=" + customer_phone.getText().toString()
                        + "&cinemaNo=" + cinemaNo + "&FilmNo=" + filmNo
                        + "&SeqNo=" + seqNo + "&HallNo=" + HallNo
                        + "&Price=" + itemPrice + "&Seats="
                        + SeatsString + "&CityNo=" + cityNo
                        + "&userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&filmName=" + Util.get(name)
                        + "&payType=" + payType + "&twoPwd=" + new MD5().getMD5ofStr(pwd));
                String result = web.getPlan();
                return result;
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
                total_price.setText(totalprice + "元");
            } else if (index == 1) {
                choose = "支付宝支付";
                payType = "4";
                total_price.setText(totalprice + "元");
            } else if (index == 2) {
                choose = "微信支付";
                payType = "6";
                total_price.setText(totalprice + "元");
            } else if (index == 3) {
                choose = "财付通支付";
                total_price.setText(totalprice + "元");
            } else if (index == 4) {
                choose = "云商账户支付";
                payType = "2";
                total_price.setText(totalprice + "元");
            } else if (index == 5) {
                choose = "商币支付";
                payType = "5";
                total_price.setText("商币：" + Util.getDouble(Double.parseDouble(totalprice.replace("￥", "")) * 10, 2));
            }
        }
    }

}
