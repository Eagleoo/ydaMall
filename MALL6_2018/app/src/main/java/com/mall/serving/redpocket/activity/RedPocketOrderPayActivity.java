package com.mall.serving.redpocket.activity;

import java.io.IOException;
import java.io.Serializable;

import net.sourceforge.mm.MMPay;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.voip.util.VoipPhoneUtils;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountDetailsFrame;
import com.mall.view.R;

@ContentView(R.layout.voip_order_pay)
public class RedPocketOrderPayActivity extends BaseActivity {

    @ViewInject(R.id.top_center)
    private TextView top_center;
    @ViewInject(R.id.top_left)
    private TextView top_left;
    @ViewInject(R.id.top_right)
    private TextView top_right;

    @ViewInject(R.id.tv_pay_money)
    private TextView tv_pay_money;

    private String payType = "3";

    private String orderId;

    private String select = "rec";

    @ViewInject(R.id.pay_item_rec_radio)
    private ImageView angel_requestCZ1;
    @ViewInject(R.id.pay_item_sb_radio)
    private ImageView angel_requestSB1;
    @ViewInject(R.id.pay_item_reccon_radio)
    private ImageView angel_requestRECCON1;
    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView angel_requestWY1;
    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView angel_requestAL1;
    @ViewInject(R.id.pay_item_sto_radio)
    private ImageView angel_requestSTO1;
    @ViewInject(R.id.pay_item_sto_line)
    private LinearLayout stoLine;

    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;

    @ViewInject(R.id.pay_money_frame_two_line)
    private View twoLine;
    @ViewInject(R.id.shop_pay_pay_twoPwd)
    private EditText twoPwd;

    private int money;
    // 交易密码
    private String pwd = "";

    private int red;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        setView();
        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);

        View sb = this.findViewById(R.id.pay_item_sb_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        PayType.addPayTypeListener(new PayTypeClickCallback() {
            @Override
            public void click(View v) {
                if (v.getId() == R.id.pay_item_weixin_line ||
                        v.getId() == R.id.pay_item_uppay_line)
                    return;
                Util.asynTask(RedPocketOrderPayActivity.this, "正在获取您的帐户信息...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {

                        if (null == runData) {
                            Util.show("获取帐户信息失败！", RedPocketOrderPayActivity.this);

                        }
                        User user = UserData.getUser();
                        if (user == null) {
                            return;
                        }

                        if ("rec".equals(select)) {
                            pay_item_rec_ban.setText("余额:"
                                    + Util.getDouble(
                                    Util.getDouble(user.getRecharge()), 3));
                        } else if ("sb".equals(select)) {
                            pay_item_sb_ban.setText("余额:"
                                    + Util.getDouble(Util.getDouble(user.getShangbi()),
                                    3));
                        }
                    }

                    @Override
                    public Serializable run() {
                        return Web.reDoLogin();
                    }
                });
            }
        }, this, R.id.pay_item_rec_line, twoLine, rec, sb, uppay, weixin);

        getIntentData();

    }

    /**
     * 标题
     */
    private void setView() {

        top_center.setText("充值支付");
        top_left.setVisibility(View.VISIBLE);
    }


    /**
     * 生成订单号方式
     *
     * @return
     */
    public String getPayType() {
        payType = PayType.getPayType(this);
        if ("2".equals(payType)) {
            return "rec";
        }
        if ("3".equals(payType)) {
            return "bank";
        }
        if ("4".equals(payType)) {
            return "ali";
        }
        if ("5".equals(payType)) {
            return "sb";
        }
        if ("6".equals(payType)) {
            return "wx";
        }
        return "";
    }

    public String getSelect() {
        return select;
    }

    @OnClick(R.id.topback)
    public void topback(View v) {
        finish();

    }

    /**
     * 确认支付的点击事件
     *
     * @param v
     */
    @OnClick(R.id.tv_order_pay)
    public void orderPay(View v) {

//		getOrderId(money);
        Util.showIntent(context, RedPocketOrderPayActivity.class,
                new String[]{"red"}, new Serializable[]{red});

    }

    /**
     * 得到订单号
     */
    private void getOrderId(final int money) {
        if (!Util.isNetworkConnected(this)) {
            Util.show("没有检测到网络，请检查您的网络连接...", this);
            return;
        }

        if (twoLine.getVisibility() == View.VISIBLE) {
            pwd = twoPwd.getText().toString().replace(" ", "");
            if (pwd.equals("")) {
                Util.show("请输入交易密码", this);
                return;
            }
            pwd = new MD5().getMD5ofStr(pwd);

        }
        select = getPayType();
        Util.asynTask(this, "正在生成订单...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

                String data = (String) runData;
                if (null == data)
                    Util.show("生成订单失败！", RedPocketOrderPayActivity.this);
                else {
                    LogUtils.e("select=" + select + "___" + runData);
                    if (select.equals("bank") || select.equals("ali") || "wx".equals(select)) {
                        String[] split = data.split(":");
                        String money = "";
                        if (split.length >= 3) {
                            if (split[0].equals("success")) {
                                orderId = split[1];
                                money = split[2];
                                System.out.println(data);
                            }
                            // 订单生成后付款
                            if (select.equals("ali")) {
                            } else if ("wx".equals(select))
                                wxPay(orderId, Double.parseDouble(money));
                        }

                    } else {
                        if (data.contains(":")) {
                            String[] split = data.split(":");

                            if (split[0].equals("success")) {
                                // Util.show("支付成功。",
                                // VoipOrderPayActivity.this);
                                showMydialog();

                            } else {
                                Util.show(split[0], RedPocketOrderPayActivity.this);
                            }

                            finish();
                        } else if (data.equals("success")) {
                            // Util.show("支付成功。", VoipOrderPayActivity.this);
                            showMydialog();

                        } else {
                            Util.show(data, RedPocketOrderPayActivity.this);
                        }

                    }

                }

            }

            @Override
            public Serializable run() {

                Web myWeb = new Web(Web.rechangePhoneAccount, "userId="
                        + UserData.getUser().getUserId() + "&pwd="
                        + UserData.getUser().getMd5Pwd() + "&money=" + money
                        + "&payType=" + payType + "&twoPwd=" + pwd);


                return myWeb.getPlan();
            }
        });

    }

    /**
     * 得到金额
     */
    private void getIntentData() {

        Intent intent = getIntent();
        if (intent.hasExtra("red")) {
            red = intent.getIntExtra("red", 0);
        }
        if (intent.hasExtra("money")) {


            money = intent.getIntExtra("money", 0);
            String fmoney = String.format("%.2f", (double) money);

            Spanned html = Html.fromHtml("<font color=\"#ff0000\">" + fmoney
                    + "元" + "</font><font color=\"#00ff00\">" + "〔"
                    + (money * 10) + "商币〕" + "</font>");

            tv_pay_money.setText(html);

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
        String str = data.getExtras().getString("pay_result");
        if ("success".equalsIgnoreCase(str)) {
            msg = "支付成功！";
            showMydialog();
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
            showFailDialog(msg);
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消！";
            showFailDialog(msg);
        }
    }

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, "远大话费充值").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "远大话费充值" + orderId, money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {

                showMydialog();

            }

            @Override
            public void doFailure(String aliResultCode) {
                // Util.showIntent("充值话费失败！支付宝状态码：" + aliResultCode,
                // VoipOrderPayActivity.this,
                // WebSiteRequestRecordFrame.class);
                // Util.show("充值话费失败！支付宝状态码：" + aliResultCode,
                // VoipOrderPayActivity.this);

                showFailDialog("充值话费失败！支付宝状态码：" + aliResultCode);
            }
        });
    }

    private void showMydialog() {
        VoipPhoneUtils.showIntent("充值话费成功！是否前去查看充值记录！",
                RedPocketOrderPayActivity.this, "确定", "取消",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RedPocketOrderPayActivity.this,
                                AccountDetailsFrame.class);
                        intent.putExtra("parentName", "话费账户");
                        intent.putExtra("userKey", "pho");
                        startActivity(intent);
                        finish();

                    }
                }, new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        finish();

                    }
                });

    }

    private void showFailDialog(String msg) {
        VoipPhoneUtils.showIntent(msg,
                RedPocketOrderPayActivity.this, "确定", null,
                null, null);


    }
}
