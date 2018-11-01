package com.mall.view.RedEnvelopesPackage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MoneyInputFilter;
import com.mall.util.MyLog;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.carMall.RechargeResultActivity;

import net.sourceforge.mm.MMPay;

import java.io.Serializable;

import static com.mall.view.R.id.money_et;

@ContentView(R.layout.activity_red_envelope_recharge)

public class RedEnvelopeRechargeActivity extends AppCompatActivity {

    private Context context;

    @ViewInject(money_et)
    private EditText moneyinput;

    @ViewInject(R.id.title)
    private TextView titletv;


    @ViewInject(R.id.pay_item_weixin_radio)
    private ImageView pay_item_weixin_radio;

    @ViewInject(R.id.pay_item_ali_radio)
    private ImageView pay_item_ali_radio;

    @ViewInject(R.id.pay_item_uppay_radio)
    private ImageView pay_item_uppay_radio;

    @ViewInject(R.id.pay_item_offline_radio)
    private ImageView pay_item_offline_radio;

    @ViewInject(R.id.submit)
    private TextView submit;

    @ViewInject(R.id.tisi)
    private TextView tisi;

    @ViewInject(R.id.tes1)
    private View tes1;

    @ViewInject(R.id.downup)
    public ImageView downup;

    @ViewInject(R.id.pay_item_offline_line)
    private View pay_item_offline_line;

    String Type = "6"; //

    double money = 0;

    String title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);

        title = getIntent().getStringExtra("userKey");

        Log.e("titletv", "titletv" + title);

        if (!Util.isNull(title)) {
            titletv.setText(title);
        } else {
            title = "红包豆充值";
            titletv.setText(title);
        }
        if (title.equals("购物券充值")) {
            pay_item_offline_line.setVisibility(View.VISIBLE);
        }

        InputFilter[] inputFilters = {new MoneyInputFilter(0)};

        moneyinput.setFilters(inputFilters);

        if (!title.equals("购物券充值")) {

            tishinfo();
        } else {
            moneyinput.setHint("请输入整数");
            tes1.setVisibility(View.VISIBLE);
            tixianfuncation();
            tisi.setText(
                    "1，充值成功后可获得等额购物券和等额拼车券；\n" +
                            "2，“购物券”用于购买创客专区的所有商品（可一次性购买，也可分期购买）。\n" +
                            "3，“拼车券”用于参与汽车排队拼车资格（拼车券达到 1.98 万及以上时," +
                            "创客就可自行选择是否参与排队拼车，以及选择要排队拼车的档次）。");
        }


        float hour = Util.getTimeDifferenceHour("2:00:00");

        MyLog.e("现在的是时间是" + hour);
        if (hour > 0) {
//                    因系统结算，每天的（时间技术根据实际情况定）
//                    期间暂不能充值，请稍后再试!
            Util.show("因系统结算，每天的凌晨00:00 ~ 02:00期间暂不能充值，请稍后再试!", context);


        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("das", "sds");
    }

    private void setstate(ImageView imageView) {
        pay_item_weixin_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_ali_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_uppay_radio.setImageResource(R.drawable.pay_item_no_checked);
        pay_item_offline_radio.setImageResource(R.drawable.pay_item_no_checked);

        switch (imageView.getId()) {
            case R.id.pay_item_weixin_radio:
                pay_item_weixin_radio.setImageResource(R.drawable.pay_item_checked);
                Type = "6";
                break;
            case R.id.pay_item_ali_radio:
                pay_item_ali_radio.setImageResource(R.drawable.pay_item_checked);
                Type = "4";
                break;
            case R.id.pay_item_uppay_radio:
                pay_item_uppay_radio.setImageResource(R.drawable.pay_item_checked);
                Type = "3";
                break;
            case R.id.pay_item_offline_radio:
                pay_item_offline_radio.setImageResource(R.drawable.pay_item_checked);
                Type = "offline";
                break;
        }

    }


    @OnClick({R.id.top_back, R.id.submit, R.id.pay_item_weixin_line, R.id.pay_item_ali_line, R.id.pay_item_uppay_line
            , R.id.find_daifu, R.id.pay_item_offline_line, R.id.downup
    })
    private void click(View view) {

        switch (view.getId()) {
            case R.id.downup:
                tixianfuncation();
                break;
            case R.id.find_daifu:

                if (true) {
                    Util.show("请正在开发中,敬请期待...", context);
                    return;
                }

                if (Util.isNull(moneyinput.getText().toString())) {
                    Util.show("请输入充值金额", context);
                    return;
                }
                if ((money % 100) != 0) {
                    Util.show("请输入100的整数倍", context);
                    return;
                }
                Util.show("开放中...", context);
                break;

            case R.id.top_back:
                finish();
                break;
            case R.id.submit:

                if (Type.equals("offline")) {
                    String str = "请转账到公司指定账户，转账成功后，拨打4006663838客服热线提交转账凭证。\n" + "\n" +
                            "指定账户：\n" +
                            "开户名：深圳远大创业投资发展有限公司\n" +
                            "帐  号：4100 3900 0400 04960\n" +
                            "开户行：中国农业银行软件园支行\n" +
                            "\n" +
                            "开户名：深圳远大创业投资发展有限公司\n" +
                            "帐  号：4420 1516 9000 5251 8548\n" +
                            "开户行：中国建设银行深圳市铁路支行";
                    new MyPopWindow.MyBuilder(RedEnvelopeRechargeActivity.this, str, "确定", null).setColor("#F13232")
                            .setisshowclose(false)
                            .build().showCenter();
                    return;
                }

                if (Util.isNull(moneyinput.getText().toString())) {
                    Util.show("请输入充值金额", context);
                    return;
                }

                if (title.equals("购物券充值")) {
                    Util.asynTask(context, "正在提交，请稍等...",
                            new IAsynTask() {
                                @Override
                                public void updateUI(Serializable runData) {
                                    if (Util.isNull(runData)) {
                                        Util.show("网络错误，请重试！", context);
                                        return;
                                    }
                                    if ((runData + "").startsWith("success:")) {
                                        String[] att = (runData + "").split(":");
                                        if ("4".equals(Type)) {
                                            aliPay(att[1], Double.parseDouble(att[2]), "购物券充值");
                                        } else if ("6".equals(Type)) {
                                            wxPay(att[1], Double.parseDouble(att[2]), "购物券充值");
                                        }
                                    } else
                                        Util.show(runData + "", context);
                                }

                                @Override
                                public Serializable run() {
                                    String url = Web.recChongzhi;
                                    if (title.equals("购物券充值")) {
                                        url = "/Shopping_voucherChongzhi";
                                    }

                                    Web web = new Web(url, "userid="
                                            + UserData.getUser().getUserId()
                                            + "&md5Pwd="
                                            + UserData.getUser().getMd5Pwd()
                                            + "&money=" + moneyinput.getText().toString()
                                            + "&type=" + Type);
                                    return web.getPlan();
                                }
                            });
                    return;
                }

                float hour = Util.getTimeDifferenceHour("2:00:00");

                MyLog.e("现在的是时间是" + hour);
                if (hour > 0) {
//                    因系统结算，每天的（时间技术根据实际情况定）
//                    期间暂不能充值，请稍后再试!
                    Util.show("因系统结算，每天的凌晨00:00 ~ 02:00期间暂不能充值，请稍后再试!", context);

                    return;

                }


                money = Double.parseDouble(moneyinput.getText().toString());

                if ((money % 100) != 0) {
                    Util.show("请输入100的整数倍", context);
                    return;
                }
                setSubmitState(false);
                payred(money);

//                }else{
//                    Toast.makeText(context,"输入100的整数倍",Toast.LENGTH_SHORT).show();
//                }

                break;
            case R.id.pay_item_weixin_line:
                setstate(pay_item_weixin_radio);
                break;

            case R.id.pay_item_ali_line:
                setstate(pay_item_ali_radio);
                break;
            case R.id.pay_item_offline_line:
                setstate(pay_item_offline_radio);
                break;

            case R.id.pay_item_uppay_line:
                setstate(pay_item_uppay_radio);
                break;

        }

    }

    boolean isopen = true;

    private void tixianfuncation() {
        if (isopen) {
            isopen = false;
            downup.animate().rotation(180);
            tisi.setVisibility(View.VISIBLE);
        } else {
            isopen = true;
            downup.animate().rotation(0);
            tisi.setVisibility(View.GONE);
        }
    }

    private void tishinfo() {
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=ToStringChongzhi&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()

                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }


                        String jieguo = json.getString("message");
                        tisi.setText(jieguo);

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                        setSubmitState(true);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }


    private void payred(final double money) {
        final CustomProgressDialog cpd = Util.showProgress("正在生成你订单...", this);

        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=recChongzhi&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&type=" + Type + "&money=" + money
                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        String jieguo = json.getString("message");

                        String[] ayy = (jieguo + "").split(":");
                        if ("4".equals(Type))
                            aliPay(ayy[1], Double.parseDouble(ayy[2]), "红包豆充值");
                        else if ("6".equals(Type))
                            wxPay(ayy[1], Double.parseDouble(ayy[2]), "红包豆充值");

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                        setSubmitState(true);
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });


    }


    public void setSubmitState(boolean isclick) {

        if (isclick) {
            submit.setBackgroundResource(R.drawable.redboxarc);
            submit.setClickable(true);
        } else {
            submit.setBackgroundResource(R.drawable.redboxarcnoclick);
            submit.setClickable(false);
        }

    }

    private void aliPay(String orderId, final double money, final String title) {
        new AliPayNet(this).pay(orderId, title, money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.show("充值成功！", context);


                if (title.equals("购物券充值")) {
                    Util.showIntent(context, RechargeResultActivity.class, new String[]{"statepay"}, new String[]{"支付成功"});
                    finish();
                } else {
                    Intent intent = new Intent(context, RechargeResultsActivity.class);
                    intent.putExtra("paymoney", money + "");
                    startActivity(intent);
                    finish();
                }


            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("充值失败，支付宝状态码：" + aliResultCode, context);
            }
        });
    }

    private void wxPay(String orderId, double money, String title) {
        CustomProgressDialog pd = CustomProgressDialog.showProgressDialog(this, "微信支付中...");
        new MMPay(this, pd, money, orderId, title).pay();
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
            Intent intent = new Intent(context, RechargeResultsActivity.class);
            intent.putExtra("paymoney", money + "");
            startActivity(intent);
            finish();
            msg = "支付成功！";
            return;
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

}
