package com.mall.view.wxapi;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.RechargeResultsActivity;
import com.mall.view.carMall.OrderResultActivity;
import com.mall.view.carMall.RechargeResultActivity;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import net.sourceforge.mm.Constants;
import net.sourceforge.mm.MMPay;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";
    @ViewInject(R.id.weixin_pay_icon)
    private ImageView iv;
    @ViewInject(R.id.weixin_pay_message)
    private TextView text;
    private IWXAPI api;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        ViewUtils.inject(this);
        Util.initTop(this, "微信支付结果", Integer.MIN_VALUE, null);
        context = this;
        api = WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1234:
                    Intent intent = new Intent(context, RechargeResultsActivity.class);
                    intent.putExtra("paymoney", Util.redbenamoney + "");
                    startActivity(intent);
                    finish();
                    if (MMPay.getactivity() != null) {
                        MMPay.getactivity().finish();
                    }
                    break;
            }
        }
    };

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {

        if (-1 == resp.errCode) {
            iv.setImageResource(R.drawable.gant);
            text.setText("微信签名错误，请确认你使用的是正版的远大云商！");
            iv.setImageResource(R.drawable.registe_success);
            VoipDialog voipDialog = new VoipDialog("微信签名验证错误，请确认你下载的是正版远大云商..." + resp.errStr
                    , WXPayEntryActivity.this, "下载正版", "恩，知道了", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.openWeb(WXPayEntryActivity.this, "http://" + Web.downAddress + "/Mall.apk");
                }
            }, null);
            voipDialog.show();
            return;
        } else if (-2 == resp.errCode) {


            if (Util.orderBody.equals("红包豆支付")) {
                Util.orderBody = "";
                iv.setImageResource(R.drawable.gant);
                text.setText("支付取消！");
            } else if (Util.orderBody.equals("购物券充值")) {
                toNewWeixinpay("支付取消");
            } else {
                Util.showIntent(this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"1", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                finish();
            }
            return;
        } else if (0 == resp.errCode) {

            if (Util.orderBody.equals("红包豆支付")) {
                Util.orderBody = "";
                iv.setImageResource(R.drawable.gou);
                text.setText("支付成功！");
                Log.e("Util.orderBody", "Util.orderBody" + Util.orderBody);
                if (Util.orderBody.equals("红包豆支付")) {
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            try {

                                Thread.sleep(3000);
                                handler.sendEmptyMessage(1234);

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            } else if (Util.orderBody.equals("购物券充值")) {
                toNewWeixinpay("支付成功");
                finish();
            } else {
                Util.showIntent(this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"0", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                finish();
            }

        } else {

            if (Util.orderBody.equals("红包豆支付")) {
                Util.orderBody = "";
                iv.setImageResource(R.drawable.gant);
                text.setText("支付失败！！");
            } else if (Util.orderBody.equals("购物券充值")) {
                toNewWeixinpay("支付失败");
            } else {
                Util.showIntent(this, OrderResultActivity.class, new String[]{"result", "number_order", "date_order"}, new String[]{"1", Util.order.getOrder().get(0).getOrderId(), Util.order.getOrder().get(0).getDate()});
                finish();
            }

        }
    }

    private void toNewWeixinpay(String state) {
        Util.showIntent(this, RechargeResultActivity.class, new String[]{"statepay"}, new String[]{state});
        finish();
    }
}