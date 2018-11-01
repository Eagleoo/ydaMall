package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.card.adapter.CardUtil;
import com.mall.model.TwoOrderProduct;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.WebView.EsccWebViewActivity;
import com.mall.view.carMall.OrderBeanAll;
import com.mall.view.carMall.OrderResultActivity;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class PayMoneyFrame extends Activity {

    @ViewInject(R.id.goodsprice)
    private TextView goodsprice; // 第一行的商品总价

    @ViewInject(R.id.redtitle)
    private TextView redtitle; // 红白种子提示

    @ViewInject(R.id.pay_money_frame_two_line)
    private LinearLayout twoLine;

    @ViewInject(R.id.shop_pay_pay_money_product_des)
    private TextView payMoneyDes;

    @ViewInject(R.id.shop_pay_pay_twoPwd)
    private EditText twoPwd;

    @ViewInject(R.id.pay_submit_payMoney)
    private Button pay_btn;

    private List<TwoOrderProduct> list;

    private User user = null;

    // 商品总价（包含邮费）
    private double sum = 0.0D;
    // 商品邮费
    private double pos = 0.0D;
    // 积分支付
    private double con = 0.0D;
    // 商币支付
    private double sb = 0.0D;
    // 种子支付
    private double zz = 0.0D;
    private String orderId;

    private String select = "wx";

    String selectedPayType = "";

    @ViewInject(R.id.pay_item_sto_line)
    private LinearLayout stoLine;
    @ViewInject(R.id.find_daifu)
    private TextView find_daifu;
    @ViewInject(R.id.new_erweima)
    private TextView new_erweima;
    private String goodsimg;
    private int daifu = 0;
    private String guid;
    private String red1 = "";

    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;  //充值账户支付

    @ViewInject(R.id.pay_item_sb_ban)
    private TextView pay_item_sb_ban;  //商币账户支付

    @ViewInject(R.id.pay_item_zz_ban)  //红包种子+充值支付
    private TextView pay_item_zz_ban;

    @ViewInject(R.id.pay_item_zz1_ban)  //红包种子支付
    private TextView pay_item_zz1_ban;

    @ViewInject(R.id.pay_item_reccon_rec_ban)
    private TextView pay_item_reccon_rec_ban;

    @ViewInject(R.id.pay_item_escc_ban)
    private TextView pay_item_escc_ban;

    public String getPayType() {
        String payType = PayType.getPayType(this);
        Log.e("payType", "payType" + payType);
        if ("1".equals(payType)) {
            select = "xj";
            Util.payType = "现金支付";
        }
        if ("2".equals(payType)) {
            select = "rec";
            Util.payType = "充值账户支付";
        }
        if ("3".equals(payType)) {
            select = "bank";
            Util.payType = "银行卡支付";
        }
        if ("4".equals(payType)) {
            select = "ali";
            Util.payType = "支付宝支付";
        }
        if ("5".equals(payType)) {
            select = "sb";
            Util.payType = "商币账户支付";
        }
        if ("7".equals(payType)) {
            select = "recCon";
            Util.payType = "充值+消费券支付";
        }
        if ("8".equals(payType))
            select = "sto";
        if ("6".equals(payType)) {
            select = "wx";
            Util.payType = "微信支付";
        }
        if ("9".equals(payType)) {
            select = "zz";
            Util.payType = "充值+红包种子支付";
        }

        if ("10".equals(payType)) {
            select = "shopVou";
            Util.payType = "购物券账户支付";

        }
        if ("11".equals(payType)) {
            select = "hbzz";
            Util.payType = "红包种子账户支付";
        }
        if ("12".equals(payType)) {
            select = "escc";
            Util.payType = "ESCC支付";
        }
        return select;
    }

    private Spanned getHtml(String account, double money) {
        return Html.fromHtml("<html><body><font color='#2498e2'>" + account
                + "：<font color='#00DB00'>" + money + "</font></body></html>");
    }

    public String getSelect() {
        return select;
    }

    @OnClick({R.id.tv_more})
    public void hideOrShow(View v) {
        TextView tv_more = (TextView) v;
        View ll_hide = findViewById(R.id.ll_hide);
        if (ll_hide.getVisibility() == View.GONE) {
            ll_hide.setVisibility(View.VISIBLE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.pay_more_show, 0);
        } else {
            ll_hide.setVisibility(View.GONE);
            tv_more.setCompoundDrawablesWithIntrinsicBounds(0, 0,
                    R.drawable.pay_more_hide, 0);
        }

    }

    private void getYE() {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("type", "1,2,3,4,5,6,7,8,9,10,13,14,15");
        NewWebAPI.getNewInstance().getWebRequest("/Money.aspx?call=getMoney",
                map, new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", PayMoneyFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show("网络异常,请重试", PayMoneyFrame.this);
                            return;
                        }
                        pay_item_rec_ban.setText("￥" + json.getString("rec"));// 充值
                        pay_item_sb_ban.setText(json.getString("sb") + "枚");// 商币

                        pay_item_zz_ban.setText(json.getString("rec") + "+" + json.getString("red_s"));// 充值+红包种子
                        pay_item_zz1_ban.setText(json.getString("red_s"));// 红包种子
                        pay_item_reccon_rec_ban.setText(json.getString("rec") + "+" + json.getString("con"));

                    }
                });
    }

    private void showmoney(View view) {
        pay_item_rec_ban.setVisibility(View.INVISIBLE);
        pay_item_sb_ban.setVisibility(View.INVISIBLE);
        pay_item_zz_ban.setVisibility(View.INVISIBLE);
        pay_item_zz1_ban.setVisibility(View.INVISIBLE);
        pay_item_reccon_rec_ban.setVisibility(View.INVISIBLE);
        pay_item_escc_ban.setVisibility(View.INVISIBLE);
        switch (view.getId()) {
            case R.id.pay_item_rec_line:
                pay_item_rec_ban.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_item_sb_line:
                pay_item_sb_ban.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_item_reccon_line:
                pay_item_reccon_rec_ban.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_item_zz_line:
                pay_item_zz_ban.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_item_zz1_line:
                pay_item_zz1_ban.setVisibility(View.VISIBLE);
                break;
            case R.id.pay_item_escc_line:
                pay_item_escc_ban.setVisibility(View.VISIBLE);

                break;
        }
    }

    View reccon;
    View sbv;
    View rec;
    View xj;
    View carpayline;
    View zzline;
    View hbzz;
    View escc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_money_frame);
        ViewUtils.inject(this);

        xj = this.findViewById(R.id.pay_item_xj_line);

        xj.setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_reccon_line)
                .setVisibility(View.VISIBLE);
//		this.findViewById(R.id.pay_item_rec_ban).setVisibility(View.GONE);
//		this.findViewById(R.id.pay_item_reccon_rec_ban)
//				.setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_reccon_con_ban)
                .setVisibility(View.GONE);
//		this.findViewById(R.id.pay_item_sb_ban).setVisibility(View.GONE);
//		this.findViewById(R.id.pay_item_zz_ban).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sto_ban).setVisibility(View.GONE);
        View ali = this.findViewById(R.id.pay_item_ali_line);
        rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);

        uppay.setVisibility(View.GONE);

        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        sbv = this.findViewById(R.id.pay_item_sb_line);
        reccon = this.findViewById(R.id.pay_item_reccon_line);
        zzline = this.findViewById(R.id.pay_item_zz_line);
        carpayline = this.findViewById(R.id.car_pay_item_reccon_line);
        hbzz = this.findViewById(R.id.pay_item_zz1_line);
        escc = this.findViewById(R.id.pay_item_escc_line);
        PayType.addPayTypeListener(
                new PayTypeClickCallback() {
                    @Override
                    public void click(View v) {
                        select = getPayType();
                        goodsprice
                                .setText(Html
                                        .fromHtml("<font color='#535353'>合计</font>：<font color='#cf0000'>"
                                                + Util.getDouble(sum, 3)
                                                + "元</font>"));
                        payMoneyDes.setText("商品 : "
                                + Util.getDouble((sum - pos), 3) + "元" + "\t\t"
                                + "邮费 : " + Util.getDouble(pos, 3) + "元");
                        if (v.getId() == R.id.pay_item_sb_line
                                || v.getId() == R.id.pay_item_rec_line
                                || v.getId() == R.id.pay_item_reccon_line
                                || v.getId() == R.id.pay_item_sto_line
                                || v.getId() == R.id.pay_item_zz_line
                                || v.getId() == R.id.pay_item_zz1_line
                                //|| v.getId() == R.id.pay_item_escc_line
                                ) {
                            showmoney(v);
                            Util.asynTask(PayMoneyFrame.this, "正在获取您的帐户信息...",
                                    new IAsynTask() {
                                        @Override
                                        public void updateUI(
                                                Serializable runData) {
                                            User user = (User) runData;
                                            if (null == user) {
                                                Util.show("获取帐户信息失败！",
                                                        PayMoneyFrame.this);
                                                return;
                                            }
                                            if ("recCon".equals(select)) {

                                                goodsprice.setText(Html.fromHtml("<font color='#535353'>合计</font>：<font color='#cf0000'>"
                                                        + Util.getDouble(sum, 3)
                                                        + "元</font>"));

                                                payMoneyDes.setText("商品 : "
                                                        + Util.getDouble((sum
                                                        - pos - con), 3)
                                                        + "元"
                                                        + "\t\t"
                                                        + "邮费 : "
                                                        + Util.getDouble(pos, 3)
                                                        + "元"
                                                        + "\t\t"
                                                        + "消费券 : "
                                                        + Util.getDouble(con, 3));
                                            } else if ("sb".equals(select)) {

                                                goodsprice.setText(Html
                                                        .fromHtml("<font color='#535353'>合计商币</font>：<font color='#cf0000'>"
                                                                + Util.getDouble((sb), 3)
                                                                + "枚</font>"));
                                                payMoneyDes.setText("商品 : "
                                                        + Util.getDouble((sum - pos), 3) + "元"
                                                        + "\t\t" + "邮费 : "
                                                        + pos + "元");
                                            } else if ("zz".equals(select)) {
                                                goodsprice.setText(Html.fromHtml("<font color='#535353'>合计</font>：<font color='#cf0000'>"
                                                        + Util.getDouble(sum, 3)
                                                        + "元</font>"));
                                                payMoneyDes.setText("商品 : "
                                                        + Util.getDouble((sum
                                                        - pos - con), 3)
                                                        + "元"
                                                        + "\t\t"
                                                        + "邮费 : "
                                                        + Util.getDouble(pos, 3)
                                                        + "元"
                                                        + "\t\t"
                                                        + "红包种子 : "
                                                        + Util.getDouble(con, 3)
                                                        + "元");
                                            }
//                                            else if ("escc".equals(select)) {
//                                                String tid = getIntent().getStringExtra("tid");
//                                                orderId=tid;
//                                                Map<String, String> map = new HashMap<String, String>();
//                                                map.put("orderId",orderId);
//                                                NewWebAPI.getNewInstance().getWebRequest("/YdaOrder.aspx?call=getHealthOrder", map, new WebRequestCallBack() {
//
//                                                    @Override
//                                                    public void success(Object result) {
//                                                        super.success(result);
//                                                        if (null == result) {
//                                                            Util.show("网络异常,请重试", PayMoneyFrame.this);
//                                                            return;
//                                                        }
//                                                        JSONObject jsons = JSON.parseObject(result.toString());
//                                                        if (200 != jsons.getIntValue("code")) {
//                                                            Util.show("网络异常,请重试", PayMoneyFrame.this);
//                                                            return;
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void requestEnd() {
//                                                        super.requestEnd();
//                                                    }
//
//                                                });
//                                                Util.showIntent(PayMoneyFrame.this, EsccWebViewActivity.class, new String[]{"url","orderId","date_order"}, new String[]{Web.esccurl+"/default_test3.aspx?orderid="+orderId,orderId,Util.order.getOrder().get(0).getDate()});
//                                                Util.show("ESCC支付中!", PayMoneyFrame.this);
//                                                twoLine.setVisibility(View.GONE);
//
//                                            }
                                        }

                                        @Override
                                        public Serializable run() {
                                            return Web.reDoLogin();
                                        }
                                    });
                        }
                    }
                }, this, R.id.pay_item_weixin_line, twoLine, ali, rec, uppay,
                weixin, sbv,
                reccon, zzline, xj, carpayline, hbzz,escc);

        pay_item_zz_ban.setVisibility(View.INVISIBLE);
        pay_item_zz1_ban.setVisibility(View.INVISIBLE);
        twoLine.setVisibility(View.GONE);
        carpayline.setVisibility(View.GONE);
        zzline.setVisibility(View.GONE);
        PayType.getPoint2().setImageResource(R.drawable.pay_item_checked);
        PayType.getPoint2().setTag("checked");
        PayType.getPoint3().setImageResource(R.drawable.pay_item_no_checked);
        PayType.getPoint3().setTag(null);
        select = "wx";

        String tid = getIntent().getStringExtra("tid");
        if (Util.isNull(tid)) {
            Util.show("参数错误!", this);
            finish();
            return;
        }
        goodsimg = getIntent().getStringExtra("img");

        if (!Util.isNull(tid)) {
            orderId = tid;
        } else {
            SharedPreferences sharedata = getSharedPreferences("data", 0);
            tid = sharedata.getString("tid", "");
            sharedata = null;
            orderId = tid;
        }

        SharedPreferences.Editor sharedata = getSharedPreferences("data", 0)
                .edit();
        sharedata.putString("tid", tid);
        sharedata.commit();
        sharedata = null;
        init(tid);
        getYE();
    }

    public void getOrders(String tid) {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "");
        map.put("page", "1");
        map.put("size", "999");
        map.put("orderid", tid);
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(this, "正在获取订单...");
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", PayMoneyFrame.this);
                            return;
                        }
                        JSONObject jsons = JSON.parseObject(result.toString());
                        if (200 != jsons.getIntValue("code")) {
                            Util.show("网络异常,请重试", PayMoneyFrame.this);
                            return;
                        }
                        Gson gson = new Gson();
                        OrderBeanAll orderBeanAll = gson.fromJson(result.toString(), OrderBeanAll.class);
                        if (Ishealth.equals("1")) {
                            //xj.setVisibility(View.VISIBLE);
                            sbv.setVisibility(View.GONE);
                            reccon
                                    .setVisibility(View.GONE);
                        } else {
                            if (orderBeanAll.getOrder().get(0).getOrdertype().equals("7")) {
                                isCarShop();
                            }
                        }

                        Util.order = null;
                        Util.order = orderBeanAll;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.dismiss();
                        super.requestEnd();
                    }

                });
    }

    private void isCarShop() {
        sbv.setVisibility(View.GONE);
        hbzz.setVisibility(View.VISIBLE);
        carpayline.setVisibility(View.VISIBLE);
    }

    public void initComponent() {
        Util.initTitle(this, "订单支付", new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(PayMoneyFrame.this, OrderFrame.class);
                finish();
            }
        });
        user = UserData.getUser();
        if (null != user) {
            if ((user.getShopType() + "").contains("店")
                    || (user.getShopType() + "").contains("级")) {
                stoLine.setVisibility(RadioButton.VISIBLE);
            } else {
                stoLine.setVisibility(RadioButton.GONE);
            }
        }
    }

    @OnClick(R.id.pay_submit_payMoney)
    public void payMoneyClick(View v) {
        pay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            if (null != UserData.getUser()) {
                finish();
                Util.showIntent(this, OrderFrame.class);
            } else {
                finish();
                Util.showIntent(this, UserCenterFrame.class);

            }
            return false;
        }
        return false;
    }

    private void pay() {
        selectedPayType = getSelect();
        Log.e("支付方式", selectedPayType);
        if (selectedPayType.equals("zz")) {
            selectedPayType = "recRdc";
        }
        if (twoLine.getVisibility() == LinearLayout.VISIBLE) {
            if (Util.isNull(twoPwd.getText().toString())) {
                Util.show("请输入二级密码...", this);
                return;
            }
        }
        Util.asynTask(this, "正在支付，请稍等...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if ("success".equals(runData)) {
                    Util.showIntent(PayMoneyFrame.this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"0", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                    SharedPreferences sharedata = getSharedPreferences("data",
                            0);
                    sharedata.edit().clear().commit();
                } else if ("wx".equals(selectedPayType)) {
                    String[] arr = (runData + "").split(":");
                    if ("wx".equals(arr[0])) {
                        CustomProgressDialog pd = CustomProgressDialog
                                .showProgressDialog(PayMoneyFrame.this,
                                        "微信支付中...");
                        new MMPay(PayMoneyFrame.this, pd, Double
                                .parseDouble(arr[2]), arr[1], "商品订单支付").pay();
                    } else {
                        Util.show("创建微信订单失败！", PayMoneyFrame.this);
                    }
                } else if ("ali".equals(selectedPayType)) {
                    Log.i("tag", "支付" + runData);
                    String[] arr = (runData + "").split(":");
                    new AliPayNet(PayMoneyFrame.this).pay(arr[1], "商品订单支付",
                            Double.parseDouble(arr[2]), new AliPayCallBack() {
                                @Override
                                public void doSuccess(String aliResultCode) {
                                    Util.showIntent(PayMoneyFrame.this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"0", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                                }

                                @Override
                                public void doFailure(String aliResultCode) {
                                    Util.showIntent(PayMoneyFrame.this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"1", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                                }
                            });
                }
                else if ("escc".equals(selectedPayType)){
                    Util.show("易云链支付中!", PayMoneyFrame.this);
                    Util.showIntent(PayMoneyFrame.this, EsccWebViewActivity.class, new String[]{"url","orderId","date_order"}, new String[]{Web.esccurl+"/escc_pay/CreateEsccOrder.aspx?orderid="+orderId,orderId,Util.order.getOrder().get(0).getDate()});
                }
                else {
                    if (Util.isNull(runData)) {
                        Util.show("网络异常，请重试！", PayMoneyFrame.this);

                    } else {
                        Util.show(runData + "", PayMoneyFrame.this);
                    }
                }
            }

            @Override
            public Serializable run() {

                Web web = new Web(Web.pay, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&tpwd="
                        + new MD5().getMD5ofStr(twoPwd.getText().toString())
                        + "&tid=" + orderId + "&type=" + selectedPayType);
                return web.getPlan();
            }
        });
    }

    private String Ishealth = "0";

    // 初始化购物车支付数据，商品金额，商币兑换数目，积分兑换数目
    private void init(final String tid) {
        initComponent();
        Util.asynTask(this, "正在获取余额信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<TwoOrderProduct>> map = (HashMap<String, List<TwoOrderProduct>>) runData;
                List<TwoOrderProduct> list = map.get("result");
                if (null != list) {
                    PayMoneyFrame.this.list = list;
                    double d = 0.0D;
                    double postage = 0.0D;
                    double temp_con = 0;
                    double temp_sb = 0;
                    // 购物卡商品
                    int gwk = 0;
                    double rebate1 = 0.0D;
                    double red = 0.0D;
                    try {


                        Ishealth = list.get(0).getIshealth();

                        getOrders(list.get(0).getTid());
                    } catch (Exception e) {

                    }


                    for (TwoOrderProduct top : list) {


                        // 数量
                        int a = Integer.parseInt(top.getAmount());
                        // 远大售价
                        double p = Double.parseDouble(top.getPrice());
                        // 邮费
                        double g = Util.getDouble(top.getPostage());
                        // 可用积分
                        double c = Util.getDouble(top.getExpPrice());
                        double sb = Util.getDouble(top.getSb()) * a;
                        //
                        //

                        d += p * a;
                        if (Util.isDouble(top.getPostage()))
                            postage += g;
                        if (top.getGwk().equals("1"))
                            gwk++;
                        temp_con += c * a;
                        temp_sb += sb;
                        rebate1 += Util.getDouble(top.getRebate1());

                        double r1 = Util.getDouble(top.getPresentExp());
                        if (r1 > 0) {
                            BigDecimal b1 = new BigDecimal(Double.toString(red));
                            BigDecimal b2 = new BigDecimal(Double.toString(p
                                    * a));
                            red = b1.add(b2).doubleValue();
                        }

                    }
                    if (gwk != list.size()) {
                        stoLine.setVisibility(RadioButton.GONE);
                    }
                    sum = d + postage;
                    pos = postage;
                    con = d - temp_con;
                    zz = d - temp_con;
                    sb = temp_sb;// + (postage*5);
                    if ("zz".equals(select)) {

                        red = round(red, 4);

                        goodsprice.setText(Html
                                .fromHtml("<font color='#535353'>合计</font>：<font color='#cf0000'>"
                                        + Util.getDouble(sum, 3) + "元</font>"));
                        redtitle.setText(Html
                                .fromHtml("(*交易成功后,此单可获得红包种子<font color='#cf0000'>"
                                        + red + "</font>个)"));
                        redtitle.setVisibility(View.GONE);
                        redtitle.setText(Html
                                .fromHtml("(*交易成功后,此单可获得红包种子<font color='#cf0000'>"
                                        + 0 + "</font>个)"));
                        redtitle.setVisibility(View.GONE);
                        payMoneyDes.setText("商品 : "
                                + Util.getDouble((sum - pos - con), 3) + "元"
                                + "\t\t" + "邮费 : " + Util.getDouble(pos, 3)
                                + "元" + "\t\t" + "红包种子 : "
                                + Util.getDouble(con, 3) + "元");
                    } else {
                        goodsprice.setText(Html
                                .fromHtml("<font color='#535353'>合计</font>：<font color='#cf0000'>"
                                        + Util.getDouble(sum, 3) + "元</font>"));
                        redtitle.setText(Html
                                .fromHtml("(*交易成功后,此单可获得红包种子<font color='#cf0000'>"
                                        + 0 + "</font>个)"));
                        redtitle.setVisibility(View.GONE);
                        payMoneyDes.setText("商品 : "
                                + Util.getDouble((sum - pos), 3) + "元" + "\t\t"
                                + "邮费 : " + Util.getDouble(pos, 3) + "元");
                    }
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getTwoOrderProduct, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids=" + tid);
                List<TwoOrderProduct> list = web.getList(TwoOrderProduct.class);
                HashMap<String, List<TwoOrderProduct>> map = new HashMap<String, List<TwoOrderProduct>>();
                map.put("result", list);
                return map;
            }
        });
    }

    @OnClick(R.id.pay__sb)
    public void sbRadioClick(View v) {
        sum = 0.0D;
        pos = 0.0D;
        for (TwoOrderProduct top : list) {
            Log.e("商币",
                    Integer.parseInt(top.getAmount()) + "~~~~~~"
                            + Util.getInt(top.getSb()));
            sum += (Integer.parseInt(top.getAmount()) * Util
                    .getInt(top.getSb()));
        }
        payMoneyDes
                .setText(Html
                        .fromHtml("<html><body><font color='#535353'>合计商币</font>：<font color='#cf0000'>"
                                + Util.getDouble((sum), 3)
                                + "枚</font>（商品：<font color='#cf0000'>"
                                + Util.getDouble((sum - pos), 3)
                                + "</font>\t\t邮费：<font color='#cf0000'>"
                                + pos
                                + "</font></body></html>）"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (null == UserData.getUser()) {
            Util.showIntent(this, Lin_MainFrame.class);
            return;
        }
        if (Util.isNull(orderId)) {
            SharedPreferences sharedata = getSharedPreferences("data", 0);
            String tid = sharedata.getString("tid", "");
            orderId = tid;
            LogUtils.e("onstart . tid is " + tid + "     orderId=" + orderId);
            sharedata = null;
            init(tid);
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
            SharedPreferences sharedata = getSharedPreferences("data", 0);
            sharedata.edit().clear().commit();
            // new AlluUndoneOrder().setStatus("已付款");
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消";
        }
        if ("success".equalsIgnoreCase(str)) {
            Util.showIntent("支付成功。点击确认查看订单状态。", PayMoneyFrame.this,
                    OrderFrame.class);
        } else {

            new MyPopWindow.MyBuilder(this, msg, "确定", null)
                    .setTitle("支付结果通知")
                    .build().showCenter();
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("支付结果通知");
//            builder.setMessage(msg);
//            builder.setInverseBackgroundForced(true);
//            builder.setNegativeButton("确定",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            builder.create().show();
        }
    }

    public User getUser() {
        return user;
    }

    public TextView getPayMoneyDes() {
        return payMoneyDes;
    }

    public List<TwoOrderProduct> getList() {
        return list;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getPos() {
        return pos;
    }

    public void setPos(double pos) {
        this.pos = pos;
    }

    public LinearLayout getTwoLine() {
        return twoLine;
    }

    /**
     * 生成二维码
     */
    public void tuErweima(String guid) throws Exception {
        String username = "";
        // try {
        // string = URLEncoder.encode(string, "UTF-8");
        // } catch (UnsupportedEncodingException e) {
        // // TODO Auto-generated catch block
        // e.printStackTrace();
        // }
        try {
            username = URLDecoder.decode(UserData.getUser().getUserId(),
                    "utf-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        String str = "dfdd" + "::" + UserData.getUser().getUserId() + "::"
                + guid + "::" + sum + "::" + pos + "::" + con + "::" + sb
                + "::" + orderId;
        final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + orderId + "," + sum + "," + pos + "&username=" + str;

        BitMatrix matrix = new MultiFormatWriter().encode(url,
                BarcodeFormat.QR_CODE, 600, 600);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else { // 无信息设置像素点为白色
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap erweimaBitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);

        erweimaBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        showDaiFu(erweimaBitmap);
    }

    /**
     * 显示代付的二维码
     */
    public void showDaiFu(final Bitmap bitmap) {
        // 将布局文件转化成view对象
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(
                R.layout.new_daifu_dialog, null);
        final Dialog dialog = new Dialog(this, R.style.CustomDialogStyle);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width * 4 / 5;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(layout);
        ImageView erweima = (ImageView) layout.findViewById(R.id.imageView1);
        erweima.setImageBitmap(bitmap);
        erweima.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
                if (Util.saveBitmapToSdCard(goodsimg, "代付二维码", bitmap)) {
                    Toast.makeText(
                            PayMoneyFrame.this,
                            "当前图片已经保存到/storage/emnulated/0/DCIM/代付二维码/"
                                    + goodsimg, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(PayMoneyFrame.this, "保存失败", Toast.LENGTH_SHORT).show();
                }
                return false;
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    @OnClick({R.id.find_daifu, R.id.new_erweima})
    public void daiFu(View view) {
        switch (view.getId()) {
            case R.id.find_daifu:
                Toast.makeText(
                        PayMoneyFrame.this,
                        "正在开发中,敬请期待..."
                        , Toast.LENGTH_SHORT).show();
//                if (daifu != 3) {
//                    daifu = 2;
//                    faQiDaiFu();
//                } else {
//                    toshare();
//                }
                break;

            case R.id.new_erweima:
                if (daifu != 3) {
                    daifu = 1;
                    faQiDaiFu();

                } else {
                    try {
                        tuErweima(guid);
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void toshare() {
        final OnekeyShare oks = new OnekeyShare();

        String username = "";
        try {
            username = URLDecoder.decode(UserData.getUser().getUserId(),
                    "utf-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + orderId + "," + sum + "," + pos + "&username=" + username;
        final String title = "来自[" + username + "]的代付信息";
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setAddress("10086");
        oks.setImageUrl(goodsimg);
        oks.setComment("快来注册吧");
        oks.setText("我在远大云商看中了一件超喜欢的产品，邀请你帮我代付，没钱也任性！嘻嘻！" + "http://"
                + Web.webImage + "/phone/pay1.aspx?p1=" + orderId + "," + sum
                + "," + pos + "。〔远大云商〕全国客服热线：" + Util._400);
        oks.setSite("远大云商");
        oks.setSiteUrl("http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + orderId + "," + sum + "," + pos);
        oks.setSilent(false);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);
                    paramsToShare.setText(paramsToShare.getText() + "\n"
                            + url.toString());
                }
            }
        });
        oks.show(PayMoneyFrame.this);
    }

    /**
     * 发起代付
     */
    public void faQiDaiFu() {
        Util.asynTask(this, "发起代付…", new IAsynTask() {
            @SuppressWarnings("unchecked")
            @Override
            public void updateUI(Serializable runData) {
                int index = 0;
                Map<String, String> map = new HashMap<String, String>();
                if (runData != null) {
                    try {
                        System.out.println(runData.toString().replace(" ", "")
                                .replace(",}", "}"));
                        map = CardUtil.getJosn(runData.toString()
                                .replace(" ", "").replace(",}", "}"));
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    if (map == null) {
                        Toast.makeText(PayMoneyFrame.this,
                                "网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        if (map.get("code") != null) {
                            if (map.get("code").equals("200")) {
                                try {
                                    guid = map.get("message");

                                    if (daifu == 2) {
                                        toshare();
                                        daifu = 3;
                                    } else {
                                        daifu = 3;
                                        tuErweima(map.get("message"));
                                    }
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                // Toast.makeText(PayMoneyFrame.this, "发起代付成功",
                                // Toast.LENGTH_SHORT).show();
                            } else if (map.get("code").equals("707")
                                    && map.get("message")
                                    .contains("该订单已经申请过代付")) {
                                guid = map.get("message").split(":")[1];

                                if (daifu == 2) {
                                    toshare();
                                    daifu = 3;
                                } else {
                                    daifu = 3;
                                    try {
                                        tuErweima(map.get("message"));
                                    } catch (Exception e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Toast.makeText(PayMoneyFrame.this, "发起代付失败",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PayMoneyFrame.this,
                                    "网络不给力哦，请检查网络是否连接后，再试一下",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PayMoneyFrame.this,
                            "网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {
                int index = 0;
                Web web = null;
                web = new Web(1, Web.newAPI, Web.in_Pay_for_another_order,
                        "in_Pay_for_another_order", "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&orderid="
                        + orderId + "&remark=");
                String s = web.getPlan();
                return s;
            }
        });
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException(
                    "The   scale   must   be   a   positive   integer   or   zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
