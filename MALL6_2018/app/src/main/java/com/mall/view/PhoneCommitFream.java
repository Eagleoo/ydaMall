package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.PhoneArea;
import com.mall.net.Web;
import com.mall.util.AsynTask;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import net.sourceforge.mm.MMPay;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;

public class PhoneCommitFream extends Activity {

    private String payItem = "";
    private TextView phoneNum;
    private TextView phoneAddr;
    private TextView phoneMoney;
    private TextView phoneBuy;
    @ViewInject(R.id.phone_sb_buy)
    private TextView phoneSbBuy;
    private Button btn;
    private EditText et;
    @ViewInject(R.id.phone_buybbbb)
    private LinearLayout twoLine;

    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView angel_requestAL1;
    @ViewInject(R.id.pay_item_sb_radio)
    private ImageView angel_requestSB1;
    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;
    @ViewInject(R.id.imgtitle_320)
    private TextView top_left;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;

    private PhoneArea pa;
    private RelativeLayout phone_charge_over_Rel;
    private TextView phone_charge_text;
    private Intent intent;

    private void init() {

        phoneNum = (TextView) findViewById(R.id.phone_num);
        phone_charge_text = (TextView) findViewById(R.id.phone_charge_text);
        phoneAddr = (TextView) findViewById(R.id.phone_Addr);
        phoneMoney = (TextView) findViewById(R.id.phone_money);
        phoneBuy = (TextView) findViewById(R.id.phone_buy);
        et = (EditText) findViewById(R.id.phone_two_pwd);
        btn = (Button) findViewById(R.id.phone_commit);
        phone_charge_over_Rel = (RelativeLayout) findViewById(R.id.phone_charge_over);
    }

    public String getPayType() {
        return PayType.getPayType(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_commit_fream);
        ViewUtils.inject(this);
        Util.initTop(this, "话费充值", Integer.MIN_VALUE, null);
        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View sb = this.findViewById(R.id.pay_item_sb_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        uppay.setVisibility(View.GONE);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View alipay = this.findViewById(R.id.pay_item_ali_line);

        rec.setVisibility(View.GONE);
        sb.setVisibility(View.GONE);

        PayType.addPayTypeListener(this, R.id.pay_item_rec_line, twoLine, sb, rec, weixin, uppay, alipay);
        twoLine.setVisibility(View.GONE);
        PayType.getPoint1().setImageResource(R.drawable.pay_item_no_checked);
        PayType.getPoint1().setTag(null);
        PayType.getPoint2().setImageResource(R.drawable.pay_item_checked);
        PayType.getPoint2().setTag("checked");
        init();
        String address = getIntent().getStringExtra("phoneAddr");
        if (Util.isNull(address)) {
            Util.show("没有获取到号码归属地，请重试！", this);
            return;
        }
        String[] addresss = address.split("\\|");
        pa = new PhoneArea();
        pa.setCity(addresss[2]);
        pa.setProvince(addresss[1]);
        pa.setSupplier(addresss[3]);
        Util.asynTask(this, "正在获取您的余额...", new AsynTask() {
            @Override
            public Serializable run() {
                HashMap<String, String> map = new HashMap<String, String>();
                Web rec_web = new Web(Web.getMoney, "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&type=rec");
                String recMoney = rec_web.getPlan();
                map.put("rec", recMoney + "");

                Web bus_web = new Web(Web.getMoney, "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&type=sb");
                String busMoney = bus_web.getPlan();
                map.put("sb", busMoney + "");
                return map;
            }

            @Override
            public void updateUI(Serializable result) {
                HashMap<String, String> data = (HashMap<String, String>) result;
                pay_item_rec_ban.setText("余额:" + data.get("rec"));
                pay_item_sb_ban.setText("余额:" + data.get("sb"));
            }
        });
        // 得到参数
        intent = getIntent();
        phoneNum.setText(phoneNum.getText() + intent.getStringExtra("phoneNum"));
        phoneAddr.setText(phoneAddr.getText() + intent.getStringExtra("phoneAddr"));
        phoneMoney.setText(phoneMoney.getText() + intent.getStringExtra("phoneMoney"));
        final String pro = Util.get(pa.getProvince().trim());
        final String cit = Util.get(pa.getCity().trim());
        final String sup = Util.get(pa.getSupplier().trim());
        final String pho = intent.getStringExtra("phoneNum");
        final String mia = intent.getStringExtra("phoneMoney");
        try {
            Util.asynTask(this, "正在验证支付金额...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {

                    if (null != runData && -1 != runData.toString().indexOf(":")) {
                        String result_ = runData + "";
                        Log.e("数据", result_ + "");
                        payItem = result_.split(":")[1];
                        String str = result_.split(":")[0];
                        phoneBuy.setText("支付金额：" + str);
                        phoneSbBuy.setText("商币支付：" + (Math.round(Util.getDouble(str) * 10)));
                        phoneBuy.setTag(-7, str);
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getPhoneMoney, "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&phone=" + pho + "&money="
                            + mia + "&lblarea=" + pro + "_" + cit + "_" + sup);
                    return web.getPlan();
                }
            });
            // 根据信息得到要支付金额
            btn.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    v.setEnabled(false);
                    final String payType_ = getPayType();
                    if ("2".equals(payType_) && Util.isNull(et.getText().toString())) {
                        Util.show("请输入交易密码！", PhoneCommitFream.this);
                        return;
                    }
                    Util.asynTask(PhoneCommitFream.this, "正在充值中...", new IAsynTask() {
                        @Override
                        public void updateUI(Serializable runData) {
                            if (Util.isNull(runData)) {
                                Util.show("网络错误，请重试！", PhoneCommitFream.this);
                                return;
                            }
                            if (("success").equals(runData + "")) {
                                //Util.showIntent("充值成功!", PhoneCommitFream.this, Lin_MainFrame.class, Lin_MainFrame.class);
                                chargeOverDo();
                            } else if ((runData + "").contains("success:")) {
                                String[] ayy = (runData + "").split(":");
                                if ("4".equals(payType_))
                                    aliPay(ayy[1], Double.parseDouble(ayy[2]));
                                else if ("6".equals(payType_))
                                    wxPay(ayy[1], Double.parseDouble(ayy[2]));
                            } else {
                                Util.show(runData + "", PhoneCommitFream.this);
                            }
                        }


                        @Override
                        public Serializable run() {
                            Web w = new Web(Web.getGoPhone, "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&twoPwd="
                                    + (new MD5().getMD5ofStr(et.getText().toString())) + "&phone=" + pho + "&unum=" + intent.getStringExtra("unum") + "&oldMoney=" + mia
                                    + "&newMoney=" + (Util.isDouble(phoneBuy.getTag(-7) + "") ? Double.parseDouble(phoneBuy.getTag(-7) + "") : 0) + "&payMoneyType=" + payType_
                                    + "&lblarea=" + pro + "_" + cit + "_" + sup + "&interfaceType=" + payItem);
                            return w.getPlan();
                        }
                    });
                    v.setEnabled(true);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new Builder(PhoneCommitFream.this);
            builder.setMessage("网络连接错误,请稍后重试.....");
            builder.setTitle("提示");
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setClass(PhoneCommitFream.this, Lin_MainFrame.class);
                    PhoneCommitFream.this.startActivity(intent);
                }
            });
            builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent();
                    intent.setClass(PhoneCommitFream.this, Lin_MainFrame.class);
                    PhoneCommitFream.this.startActivity(intent);
                }
            });
            builder.create().show();
        }
    }

    /**
     * 银联支付回调地址
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        String msg = "";
        /*
		 * 支付控件返回字符串:success、fail、cancel 分别代表支付成功，支付失败，支付取消
		 */
        String str = data.getExtras().getString("pay_result");
        if ("success".equalsIgnoreCase(str)) {
            msg = "支付成功！";
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private void wxPay(String orderId, double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, "微信话费支付").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "话费充值", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.show("充值成功！", PhoneCommitFream.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("充值失败，支付宝状态码：" + aliResultCode, PhoneCommitFream.this);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.pa = (PhoneArea) getIntent().getSerializableExtra("phoneArea");
        if (null == pa)
            pa = new PhoneArea();
    }

    @OnClick({R.id.tv_more})
    public void hideOrShow(View v) {
        TextView tv_more = (TextView) v;
        View ll_hide = findViewById(R.id.ll_hide);
        if (ll_hide.getVisibility() == View.GONE) {
            ll_hide.setVisibility(View.VISIBLE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pay_more_show, 0);
        } else {
            ll_hide.setVisibility(View.GONE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.pay_more_hide, 0);
        }

    }

    //充值结束后
    private void chargeOverDo() {
        phone_charge_over_Rel.setVisibility(View.VISIBLE);
        topCenter.setText("手机话费充值");
        top_left.setText("完成");
        phone_charge_text.setText("手机话费成功充值" + phoneMoney.getText() + "！");
        top_left.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(PhoneCommitFream.this, OrderFrame.class);
                startActivity(intent);

            }
        });
    }
}
