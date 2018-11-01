package com.mall.view.RedEnvelopesPackage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;


@ContentView(R.layout.activity_recharge_results)
public class RechargeResultsActivity extends AppCompatActivity {
    private Context context;

    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.textresule_tv1)
    private TextView textresule_tv1;

    @ViewInject(R.id.textresule_tv2)
    private TextView textresule_tv2;


    private String money="0";
    private String number="0";

    private float beishu=0f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        title.setText("充值成功");
        Intent intent=getIntent();
        money=intent.getStringExtra("paymoney");



        tishinfo(money);


    }

    @OnClick({R.id.top_back,R.id.toredbox_mtv,R.id.zhuanruxiaofeiquan})
    private void click(View view){

        Intent intent;
        switch (view.getId()){
            case R.id.top_back:
                finish();
                break;
            case R.id.toredbox_mtv:
                intent=new Intent(context,ChangeRedEnvelopeActivity.class);
                intent.putExtra("title","封入红包盒");
                startActivity(intent);
                finish();
                break;
            case  R.id.zhuanruxiaofeiquan:
                intent= new Intent(context,ChangeRedEnvelopeActivity.class);
                intent.putExtra("title","转入消费券");
                startActivity(intent);
                finish();
                break;
        }
    }

    private void tishinfo(String money){
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=ToStringChongzhiOK&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()+
                "&money="+money

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


                        String jieguo=json.getString("message");
////                        *充值成后赠送3倍红包豆
//                        beishu=Float.parseFloat(jieguo.replace("*充值成功后赠送","").replace("倍红包豆",""));
//                        number=Float.parseFloat(money)*beishu+"";
//                        Spanned html = Html.fromHtml("您已成功充值<font color=\"#FF2145\">"+money + "</font>"+"元，获得"+number+"红包豆");

                        try {
                            textresule_tv2.setText(jieguo);
                        }catch (Exception e){

                        }

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }
}
