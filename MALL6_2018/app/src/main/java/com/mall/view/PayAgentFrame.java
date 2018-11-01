package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.card.adapter.CardUtil;
import com.mall.model.TwoOrderProduct;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.PayType;
import com.mall.util.PayTypeClickCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.yyrg.model.Daifu;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 代付支付页面
 *
 * @author admin
 */
public class PayAgentFrame extends Activity {

    @ViewInject(R.id.pay_money_frame_two_line)
    private LinearLayout twoLine;

    @ViewInject(R.id.shop_pay_pay_money_product_des)
    private TextView payMoneyDes;
    @ViewInject(R.id.pay_userAccount_money)
    private TextView accountMoney;
    @ViewInject(R.id.goodsname)
    private TextView goodsname;
    @ViewInject(R.id.goodsimg)
    private ImageView goodsimgs;
    @ViewInject(R.id.count)
    private TextView count;
    @ViewInject(R.id.price)
    private TextView price;
    @ViewInject(R.id.shop_pay_pay_twoPwd)
    private EditText twoPwd;
    @ViewInject(R.id.pay_submit_payMoney)
    private Button pay_submit_payMoney;
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
    private String orderId;

    private String select = "rec";

    @ViewInject(R.id.pay_item_sto_line)
    private LinearLayout stoLine;
    @ViewInject(R.id.find_daifu)
    private TextView find_daifu;
    @ViewInject(R.id.new_erweima)
    private TextView new_erweima;
    private String goodsimg;
    private String msg;
    private String guid;
    @ViewInject(R.id.daifu_lin)
    private LinearLayout daifu_lin;
    @ViewInject(R.id.lin_message)
    private LinearLayout lin_message;

    public String getPayType() {
        String payType = PayType.getPayType(this);
        if ("2".equals(payType))
            select = "rec";
        if ("3".equals(payType))
            select = "bank";
        if ("4".equals(payType))
            select = "ali";
        if ("5".equals(payType))
            select = "sb";
        if ("7".equals(payType))
            select = "recCon";
        if ("8".equals(payType))
            select = "sto";
        if ("6".equals(payType))
            select = "wx";
        return select;
    }

    private Spanned getHtml(String account, double money) {
        return Html.fromHtml("<html><body><font color='#535353'>" + account
                + "：<font color='#00DB00'>" + money + "</font></body></html>");
    }

    public String getSelect() {
        return select;
    }

    @OnClick(R.id.lin_message)
    public void shangpin(View view) {
        showAllOrder(list);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pay_daifu_frame);
        ViewUtils.inject(this);
        daifu_lin.setVisibility(View.GONE);
        msg = getIntent().getStringExtra("msg");
        try {
            msg = URLDecoder.decode(msg, "utf-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        guid = msg.split("::")[2];
        showDialog(msg.split("::")[7]);
        sum = Double.parseDouble(msg.split("::")[3]);
        pos = Double.parseDouble(msg.split("::")[4]);
        con = Double.parseDouble(msg.split("::")[5]);
        sb = Double.parseDouble(msg.split("::")[6]);
        Log.v("sum", sum + "");
        Log.v("pos", pos + "");
        Log.v("con", con + "");
        Log.v("sb", sb + "");
        pay_submit_payMoney.setText("确认代付");
        this.findViewById(R.id.pay_item_xj_line).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_reccon_line)
                .setVisibility(View.VISIBLE);
        this.findViewById(R.id.pay_item_rec_ban).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_reccon_rec_ban)
                .setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_reccon_con_ban)
                .setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sb_ban).setVisibility(View.GONE);
        this.findViewById(R.id.pay_item_sto_ban).setVisibility(View.GONE);

        View ali = this.findViewById(R.id.pay_item_ali_line);
        View rec = this.findViewById(R.id.pay_item_rec_line);
        View uppay = this.findViewById(R.id.pay_item_uppay_line);
        View weixin = this.findViewById(R.id.pay_item_weixin_line);
        View sbv = this.findViewById(R.id.pay_item_sb_line);
        View reccon = this.findViewById(R.id.pay_item_reccon_line);
        PayType.addPayTypeListener(
                new PayTypeClickCallback() {
                    @Override
                    public void click(View v) {
                        select = getPayType();
                        if (v.getId() == R.id.pay_item_sb_line
                                || v.getId() == R.id.pay_item_rec_line
                                || v.getId() == R.id.pay_item_reccon_line
                                || v.getId() == R.id.pay_item_sto_line) {
                            Util.asynTask(PayAgentFrame.this, "正在获取您的帐户信息...",
                                    new IAsynTask() {
                                        @Override
                                        public void updateUI(
                                                Serializable runData) {
                                            User user = (User) runData;
                                            if (null == user) {
                                                Util.show("获取帐户信息失败！",
                                                        PayAgentFrame.this);
                                                return;
                                            }
                                            accountMoney
                                                    .setVisibility(TextView.VISIBLE);
                                            twoLine.setVisibility(LinearLayout.VISIBLE);
                                            if ("rec".equals(select))
                                                accountMoney.setText(getHtml(
                                                        "您的充值账户余额",
                                                        Util.getDouble(
                                                                Util.getDouble(user
                                                                        .getRecharge()),
                                                                3)));
                                            else if ("sto".equals(select))
                                                accountMoney.setText(getHtml(
                                                        "您的购物卡余额",
                                                        Util.getDouble(
                                                                Util.getDouble(user
                                                                        .getStored()),
                                                                3)));
                                            else if ("recCon".equals(select)) {
                                                payMoneyDes.setText(Html.fromHtml("<html><body><font color='#535353'>合计</font>：<font color='#cf0000'>"
                                                        + Util.getDouble(sum, 3)
                                                        + "元</font>（商品：<font color='#cf0000'>"
                                                        + Util.getDouble((sum
                                                        - pos - con), 3)
                                                        + "</font>  邮费：<font color='#cf0000'>"
                                                        + Util.getDouble(pos, 3)
                                                        + "</font>  消费券：<font color='#cf0000'>"
                                                        + Util.getDouble(con, 3)
                                                        + "</font></body></html>）"));

                                                accountMoney.setText(Html.fromHtml("<html><body><font color='#535353'>您的充值账户余额：<font color='#00DB00'>"
                                                        + Util.getDouble(
                                                        Util.getDouble(user
                                                                .getRecharge()),
                                                        3)
                                                        + "</font>\n消费券：<font color='#00DB00'>"
                                                        + Util.getDouble(
                                                        Util.getDouble(user
                                                                .getConsume()),
                                                        3)
                                                        + "</font></body></html>"));
                                            } else if ("sb".equals(select)) {
                                                payMoneyDes.setText(Html
                                                        .fromHtml("<html><body><font color='#535353'>合计：</font><font color='#cf0000'>"
                                                                + new BigDecimal(
                                                                sb)
                                                                .setScale(
                                                                        0,
                                                                        BigDecimal.ROUND_HALF_UP)
                                                                .intValue()
                                                                + "商币</font></body></html>"));

                                                accountMoney.setText(Html.fromHtml("<html><body><font color='#535353'>您的商币余额：<font color='#00DB00'>"
                                                        + Util.getDouble(
                                                        Util.getDouble(user
                                                                .getShangbi()),
                                                        3)
                                                        + "</font></body></html>"));
                                            }
                                        }

                                        @Override
                                        public Serializable run() {
                                            return Web.reDoLogin();
                                        }
                                    });
                        }
                    }
                }, this, R.id.pay_item_rec_line, twoLine, ali, rec, uppay,
                weixin, sbv,
                reccon);
//        reccon.setVisibility(View.GONE);
        String tid = msg.split("::")[7];
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
    }


    public void initComponent() {
        String string = msg.split("::")[1];
        Util.initTitle(this, string + "  的代付", new OnClickListener() {
            @Override
            public void onClick(View v) {
                PayAgentFrame.this.finish();
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
        findDaifu();

    }

    private void pay() {
        final String selectedPayType = getSelect();
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
                    Util.showIntent(PayAgentFrame.this, UserCenterFrame.class);

                    SharedPreferences sharedata = getSharedPreferences("data",
                            0);
                    sharedata.edit().clear().commit();
                    finish();
                } else if ("wx".equals(selectedPayType)) {
                    String[] arr = (runData + "").split(":");
                    if ("wx".equals(arr[0])) {
                        CustomProgressDialog pd = CustomProgressDialog
                                .showProgressDialog(PayAgentFrame.this,
                                        "微信支付中...");
                        new MMPay(PayAgentFrame.this, pd, Double
                                .parseDouble(arr[2]), arr[1], "商品订单支付").pay();
                    } else {
                        Util.show("创建微信订单失败！", PayAgentFrame.this);
                    }
                } else {
                    if (Util.isNull(runData))
                        Util.show("网络错误，请重试！", PayAgentFrame.this);
                    else
                        Util.show(runData + "", PayAgentFrame.this);
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

    // 初始化购物车支付数据，商品金额，商币兑换数目，积分兑换数目
    private void init(final String tid) {
        initComponent();
        Util.asynTask(this, "正在获取余额信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<TwoOrderProduct>> map = (HashMap<String, List<TwoOrderProduct>>) runData;
                List<TwoOrderProduct> list = map.get("result");
                if (null != list) {
                    PayAgentFrame.this.list = list;
                    double d = 0.0D;
                    double postage = 0.0D;
                    double temp_con = 0;
                    double temp_sb = 0;
                    // 购物卡商品
                    int gwk = 0;
                    double rebate1 = 0.0D;
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
                    }
                    if (gwk != list.size()) {
                        stoLine.setVisibility(RadioButton.GONE);
                    }
                    sum = d + postage;
                    pos = postage;
                    con = d - temp_con;
                    sb = temp_sb;// + (postage*5);
                    if ("sb".equals(select)) {
                        payMoneyDes.setText(Html
                                .fromHtml("<html><body><font color='#535353'>合计商币</font>：<font color='#cf0000'>"
                                        + sum
                                        + "枚</font>（商品：<font color='#cf0000'>"
                                        + (sum - pos)
                                        + "</font>  邮费：<font color='#cf0000'>"
                                        + pos + "</font></body></html>）"));
                        accountMoney.setText("商币账户余额：" + user.getShangbi()
                                + "枚");
                    } else {
                        payMoneyDes.setText(Html
                                .fromHtml("<html><body><font color='#535353'>合计</font>：<font color='#cf0000'>"
                                        + Util.getDouble(sum, 3)
                                        + "元</font>（商品：<font color='#cf0000'>"
                                        + Util.getDouble((sum - pos), 3)
                                        + "</font>  邮费：<font color='#cf0000'>"
                                        + Util.getDouble(pos, 3)
                                        + "</font></body></html>）"));
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
            sum += (Integer.parseInt(top.getAmount()) * Util
                    .getInt(top.getSb()));
        }
        payMoneyDes
                .setText(Html
                        .fromHtml("<html><body><font color='#535353'>合计商币</font>：<font color='#cf0000'>"
                                + sum
                                + "枚</font>（商品：<font color='#cf0000'>"
                                + (sum - pos)
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
        } else if ("fail".equalsIgnoreCase(str)) {
            msg = "支付失败！";
        } else if ("cancel".equalsIgnoreCase(str)) {
            msg = "用户取消";
        }
        if ("success".equalsIgnoreCase(str)) {
            Util.showIntent("支付成功。点击确认查看订单状态。", PayAgentFrame.this,
                    PayAgentFrame.class);
        } else {

            new MyPopWindow.MyBuilder(this,msg,"确定",null)
                    .setTitle("支付结果通知").build().showCenter();
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
     * c查询是否已经被代付过
     */
    public void findDaifu() {
        Util.asynTask(this, "验证中…", new IAsynTask() {
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
                        Toast.makeText(PayAgentFrame.this,
                                "网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
                                .show();
                    } else {
                        if (map.get("code") != null) {
                            if (map.get("code").equals("200")) {
                                List<Daifu> daifus = JSONArray.parseArray(map.get("list"), Daifu.class);
                                if (daifus != null && daifus.size() > 0) {
                                    pay();
                                }
                            } else {
                                Toast.makeText(PayAgentFrame.this, map.get("message"), Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(PayAgentFrame.this,
                                    "网络不给力哦，请检查网络是否连接后，再试一下",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(PayAgentFrame.this, "网络不给力哦，请检查网络是否连接后，再试一下",
                            Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {
                int index = 0;
                Web web = null;
                web = new Web(1, Web.newAPI, Web.sl_Pay_for_another_order,
                        "sl_pay_for_another_order", "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&guid=" + guid
                );
                String s = web.getPlan();
                return s;
            }
        });
    }

    public void showAllOrder(List<TwoOrderProduct> list) {
        int _5dp = Util.dpToPx(PayAgentFrame.this, 5);
        if (null != list) {
            AlertDialog.Builder dialog = new Builder(PayAgentFrame.this);
            LinearLayout root = new LinearLayout(PayAgentFrame.this);
            LinearLayout.LayoutParams fill = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.FILL_PARENT);
            root.setLayoutParams(fill);
            ScrollView scroll = new ScrollView(PayAgentFrame.this);
            scroll.setLayoutParams(fill);
            scroll.setFillViewport(true);
            LinearLayout scrollLine = new LinearLayout(PayAgentFrame.this);
            scrollLine.setOrientation(LinearLayout.VERTICAL);
            scrollLine.setLayoutParams(fill);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            lp.setMargins(_5dp, _5dp, _5dp, _5dp);
            LinearLayout item = null;
            TextView pName, pAmount, pMoney, pMessage;
            ImageView img = null;
            for (TwoOrderProduct top : list) {
                item = new LinearLayout(PayAgentFrame.this);
                item.setOrientation(LinearLayout.HORIZONTAL);

                item.setLayoutParams(lp);
                img = new ImageView(PayAgentFrame.this);
                img.setLayoutParams(new LinearLayout.LayoutParams(
                        _5dp * 12, _5dp * 14));
                Util.asynDownLoadImage(
                        top.getImg().replaceFirst("img.mall666.cn",
                                Web.imgServer), img);
                item.addView(img);

                LinearLayout infoLine = new LinearLayout(
                        PayAgentFrame.this);
                infoLine.setOrientation(LinearLayout.VERTICAL);
                infoLine.setLayoutParams(lp);
                pName = new TextView(PayAgentFrame.this);
                pName.setText("商品名称：" + top.getName());
                pName.setEllipsize(TruncateAt.MARQUEE);
                pName.setSingleLine(true);

                pAmount = new TextView(PayAgentFrame.this);
                pAmount.setText("购买数量：" + top.getAmount() + ""
                        + top.getUnit());
                pAmount.setEllipsize(TruncateAt.MARQUEE);
                pAmount.setSingleLine(true);

                pMoney = new TextView(PayAgentFrame.this);
                pMoney.setText("商品单价：" + top.getPrice());
                pMoney.setEllipsize(TruncateAt.MARQUEE);
                pMoney.setSingleLine(true);

                pMessage = new TextView(PayAgentFrame.this);
                pMessage.setText("购买留言：" + top.getMessage());
                pMessage.setEllipsize(TruncateAt.MARQUEE);
                pMessage.setSingleLine(true);

                infoLine.addView(pName);
                infoLine.addView(pAmount);
                infoLine.addView(pMoney);
                infoLine.addView(pMessage);

                item.addView(infoLine);
                scrollLine.addView(item);
                pName = pMoney = pAmount = pMessage = null;
                item = null;
                img = null;
            }
            scroll.addView(scrollLine);
            root.addView(scroll);
            dialog.setView(root);
            dialog.setTitle("订单详细");
            dialog.setNegativeButton("关闭",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                            int which) {
                            dialog.cancel();
                            dialog.dismiss();
                        }
                    });
            dialog.setIcon(android.R.drawable.ic_dialog_dialer);
            dialog.create().show();
        }
    }

    private void showDialog(final String tid) {

        Util.asynTask(PayAgentFrame.this, "正在获取该订单信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<TwoOrderProduct>> map = (HashMap<String, List<TwoOrderProduct>>) runData;
                List<TwoOrderProduct> list = map.get("result");
                PayAgentFrame.this.list = list;
                if (list != null) {
                    goodsname.setText(list.get(0).getName());
                    price.setText("￥" + list.get(0).getPrice());
                    count.setText("数量" + list.get(0).getAmount());
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    final int width = dm.widthPixels;
                    BitmapUtils bmUtil = new BitmapUtils(PayAgentFrame.this);
                    bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
                    bmUtil.display(goodsimgs, list.get(0).getImg().replaceFirst("img.mall666.cn",
                            Web.imgServer), new DefaultBitmapLoadCallBack<View>() {
                        @Override
                        public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
                                                    BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
                            Bitmap bitmap = null;
                            try {
                                bitmap = Util.zoomBitmap(arg2, width / 5, width / 5);
                            } catch (Exception e) {
                                System.gc();
                                bitmap = null;
                                // TODO: handle exception
                            }

                            super.onLoadCompleted(arg0, arg1, bitmap, arg3, arg4);
                        }

                        @Override
                        public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                            goodsimgs.setImageResource(R.drawable.new_yda__top_zanwu);
                        }
                    });
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getTwoOrderProduct, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids=" + tid);
                HashMap<String, List<TwoOrderProduct>> map = new HashMap<String, List<TwoOrderProduct>>();
                map.put("result", web.getList(TwoOrderProduct.class));
                return map;
            }
        });
    }
}
