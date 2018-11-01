package com.mall.serving.doubleball;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.order.LotteryOrders;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BMFWFrane;
import com.mall.view.R;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.doubleball_order_pay)
public class DoubleballOrderPayActivity extends Activity {
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    @ViewInject(R.id.tv_pay_money)
    private TextView tv_pay_money;
    @ViewInject(R.id.tv_order_pay_title)
    private TextView tv_order_pay_title;

    private String lotteryListString = "";
    private String type = "SSQ";
    private String payType = "3";
    private List<LotteryOrderData> lotteryList;

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

    private int sum;
    // 交易密码
    private String pwd = "";
    private User user;
    private String phoneNumber;
    private LotteryOrderData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
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
                if (v.getId() != R.id.pay_item_ali_line
                        && v.getId() != R.id.pay_item_uppay_line
                        && v.getId() != R.id.pay_item_weixin_line) {
                    Util.asynTask(DoubleballOrderPayActivity.this, "正在获取您的帐户信息...", new IAsynTask() {
                        @Override
                        public void updateUI(Serializable runData) {
                            User user = (User) runData;
                            if (null == user) {
                                Util.show("获取帐户信息失败！", DoubleballOrderPayActivity.this);
                                return;
                            }

                            if ("rec".equals(select)) {
                                pay_item_rec_ban.setText("余额:"
                                        + Util.getDouble(
                                        Util.getDouble(user.getRecharge()), 3));
                            } else if ("sto".equals(select)) {
                            } else if ("recCon".equals(select)) {

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
            }
        }, this, R.id.pay_item_sb_line, twoLine, rec, sb, uppay, weixin);
        user = UserData.getUser();
        phoneNumber = user.getMobilePhone();
        Log.i("result", phoneNumber);
        getIntentData();
        lotteryListToString();

    }

    /**
     * 标题
     */
    private void setView() {

        topCenter.setText("订单支付");
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

        getOrderId();

    }

    /**
     * 得到订单号
     */
    private void getOrderId() {
        if (!Util.isNetworkConnected(this)) {
            Util.show("没有检测到网络，请检查您的网络连接...", this);
            return;
        }

        if (twoLine.getVisibility() == View.VISIBLE) {
            pwd = twoPwd.getText().toString();
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
                    Util.show("生成订单失败！", DoubleballOrderPayActivity.this);
                else {
                    if (select.equals("bank") || select.equals("ali") || "wx".equals(select)) {

                        String[] split = data.split(":");
                        String money = "";
                        if (split.length >= 4) {
                            if (split[1].equals("success")) {
                                orderId = split[2];
                                money = split[3];
                                // Util.show(data,
                                // DoubleballOrderPayActivity.this);
                            }

                            // 订单生成后付款
                            if (select.equals("ali")) {
                                //aliPay(orderId, Double.parseDouble(money));
                            } else if ("wx".equals(select))
                                wxPay(orderId, Double.parseDouble(money));
                        }

                    } else {
                        if (data.contains(":")) {
                            String[] split = data.split(":");

                            if (split[0].equals("success")) {
                                // Util.show("支付成功。",
                                // DoubleballOrderPayActivity.this);
                                showOrderDialog();
                            } else {
                                Util.show(split[0],
                                        DoubleballOrderPayActivity.this);
                            }

                            finish();
                        } else if (data.equals("success")) {
                            // Util.show("支付成功。",
                            // DoubleballOrderPayActivity.this);
                            // finish();
                            showOrderDialog();
                        } else {
                            Util.show(data, DoubleballOrderPayActivity.this);
                        }

                    }

                }

            }

            @Override
            public Serializable run() {
                user = Web.reDoLogin();
                Web myWeb;

                if(!Util.isNull(UserData.getUser().getIdNo())?true:false){
                     myWeb = new Web(Web.convience_service,
                            Web.createLotteryOrder, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&type="
                            + type + "&lotteryList=" + lotteryListString
                            + "&payType=" + payType + "&twoPwd=" + pwd
                            + "&idCard=" + UserData.getUser().getIdNo()
                            + "&name=" + UserData.getUser().getName()
                            + "&overseas=" + 0
                            + "&phone=" + phoneNumber);
                }else {
                     myWeb = new Web(Web.convience_service,
                            Web.createLotteryOrder, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&type="
                            + type + "&lotteryList=" + lotteryListString
                            + "&payType=" + payType + "&twoPwd=" + pwd
                            + "&passport=" + UserData.getUser().getPassport()
                            + "&name=" + UserData.getUser().getName()
                             + "&overseas=" + 1
                            + "&phone=" + phoneNumber);
                }



                return myWeb.getPlan();
            }
        });

    }

    /**
     * 得到lotteryList，用来提交
     */
    private void getIntentData() {
        Intent intent = getIntent();

        if (intent != null) {
            Bundle bundle = intent.getExtras();
            lotteryList = (ArrayList<LotteryOrderData>) bundle
                    .getSerializable("lotteryList");
            int money = bundle.getInt("money");

            String fmoney = String.format("%.2f", (double) money);

            Spanned html = Html.fromHtml("<font color=\"#ff0000\">" + fmoney
                    + "元" + "</font><font color=\"#00ff00\">" + "〔"
                    + (money * 10) + "商币〕" + "</font>");
            tv_pay_money.setText(html);
            sum = money;
            data = lotteryList.get(0);
            tv_order_pay_title.setText("远大彩票-双色球-代购-" + data.getIssueNumber()
                    + "-" + data.getBetContent());
        }

    }

    /**
     * 将lotteryList转换成符合要求的字符串
     */
    private void lotteryListToString() {

        for (int i = 0; i < lotteryList.size(); i++) {
            LotteryOrderData data = lotteryList.get(i);
            String string = data.getPlayType() + "，" + data.getIssueNumber()
                    + "，" + data.getMultiple() + "，" + data.getTotalmoney()
                    + "，" + data.getBetContent() + "。";
            lotteryListString += string;
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
            showOrderDialog();
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
            showFailDialog(msg);
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消！";
            showFailDialog(msg);
        }
        // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // builder.setTitle("支付结果通知");
        // builder.setMessage(msg);
        // builder.setInverseBackgroundForced(true);
        // builder.setNegativeButton("确定", new DialogInterface.OnClickListener()
        // {
        // @Override
        // public void onClick(DialogInterface dialog, int which) {
        // dialog.dismiss();
        // }
        // });
        // builder.create().show();
    }

    private void wxPay(final String orderId, final double money) {
        CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, cpd, money, orderId, "彩票订单支付").pay();
    }

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "远大彩票-双色球-代购-" + data.getIssueNumber()
                + "-" + orderId, money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {

                showOrderDialog();
            }

            @Override
            public void doFailure(String aliResultCode) {
                // Util.showIntent("购买彩票失败！支付宝状态码：" + aliResultCode,
                // DoubleballOrderPayActivity.this,
                // DoubleballOrderPayActivity.class);

                showFailDialog("购买彩票失败！支付宝状态码：" + aliResultCode);
            }
        });
    }

    private void showOrderDialog() {

        new DoubleballDialog("购买彩票成功！是否前去查看彩票订单？",
                DoubleballOrderPayActivity.this, "确定", "取消",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(
                                DoubleballOrderPayActivity.this,
                                LotteryOrders.class);

                        startActivity(intent);
                        finish();
                    }
                }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        DoubleballOrderPayActivity.this,
                        BMFWFrane.class);

                startActivity(intent);
                finish();

            }
        }).show();

    }

    private void showFailDialog(String msg) {
        new DoubleballDialog(msg, DoubleballOrderPayActivity.this, "确定", null,
                null, null).show();

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
}
