package com.mall.view.RedEnvelopesPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.RedPackageInLetBean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BusinessCircle.RedBeanAccountActivity;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.SetSencondPwdFrame;
import com.mall.view.StoreMainFrame;
import com.mall.view.UpdateUserMessageActivity;


/**
 * 红包豆 入口界面
 */
@ContentView(R.layout.activity_speed_up)
public class SpeedUpActivity extends AppCompatActivity {
    private Context context;

    @ViewInject(R.id.speedupnumber_tv)
    private TextView speedupnumber; //红包豆数量

    @ViewInject(R.id.xiaofeiquannumber_tv)
    private TextView xiaofeiquannumber;  //消费券

    @ViewInject(R.id.redboxnumber_tv)
    private TextView redboxnumber;  //红包盒

    @ViewInject(R.id.xianjingquan_tv)
    private TextView xianjingquan;  //现金券

    @ViewInject(R.id.chongzhibi_tv)
    private TextView chongzhibi;

    @ViewInject(R.id.myteam_rl)
    private View myteam_rl;

    @ViewInject(R.id.ivv5)
    private ImageView ivv5;

    @ViewInject(R.id.diyiren)
    private View diyiren;

    @ViewInject(R.id.ivv3)
    private ImageView ivv3;

    @ViewInject(R.id.itv3)
    private TextView itv3;


    RedPackageInLetBean redPackageInLetBean;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);

        init();
    }

    private void init() {
        myteam_rl.setVisibility(View.INVISIBLE);
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initdata();
    }

    private void initView() {
        initcheck();

    }

    private void initcheck() {

        Log.e("等级", UserData.getUser().getIs_dyr() + "LL");
        Log.e("getShowLevel", UserData.getUser().getShowLevel() + "LL");

        if (!UserData.getUser().getIs_dyr().equals("1")) {

            diyiren.setVisibility(View.GONE);
            chongzhibi.setTextColor(Color.parseColor("#9E9E9E"));
            ivv5.setImageResource(R.drawable.unbkx);
            chongzhibi.setVisibility(View.INVISIBLE);
        }

        if (UserData.getUser().getShowLevel().indexOf("联盟商家") == -1 &&
                UserData.getUser().getShowLevel().indexOf("(商)") == -1 &&
                UserData.getUser().getShowLevel().indexOf("城市经理") == -1 &&
                UserData.getUser().getShowLevel().indexOf("城市总监") == -1) {
            ivv3.setImageResource(R.drawable.jsqgray);
            itv3.setTextColor(Color.parseColor("#868686"));
        }


    }


    private void initdata() {
        getRedBoxmyselfInfo();
    }

    private void setRedPackageInfo(RedPackageInLetBean bean) {

        speedupnumber.setText(bean.getRedbean().split("\\.")[0]);
        xiaofeiquannumber.setText(bean.getConsumption());
        redboxnumber.setText(bean.getRedbox_weikai());
        xianjingquan.setText(bean.getCashroll());
        chongzhibi.setText(bean.getRechargecoin());


    }

    @OnClick({R.id.top_back, R.id.torecharge, R.id.topackup, R.id.tovouchers, R.id.toredbox
            , R.id.toredbeanaccount_rl, R.id.toshopmall, R.id.cashcoupon_rl, R.id.toxiaofeiquan
            , R.id.tojiashu
            , R.id.chongzhibi_rl
            , R.id.myteam_rl
    })
    private void click(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.top_back:
                finish();
                break;
            case R.id.torecharge:  //充值
                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("为保障您的交易安全，请先设置您的交易密码", context, "确定", "取消",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context,
                                            SetSencondPwdFrame.class);
                                    startActivity(intent);
                                }
                            }, null);
                    voipDialog.show();
                    return;
                }
                if (!Util.checkUserInfocomplete()){
                    VoipDialog voipDialog = new
                            VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", context, "立即登记", "稍后登记", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(context,
                                    UpdateUserMessageActivity.class);
                        }
                    }, null);
                    voipDialog.show();
                    return;
                }





                if (Util.isNull(UserData.getUser().getPassport())){
                    String idno = !Util.isNull(UserData.getUser().getIdNo())?UserData.getUser().getIdNo():UserData.getUser().getPassport();
                    if (!Util.isNull(idno)) {
                        int age = Util.getAgeByIDNumber(idno);
                        Log.e("年龄", "age:" + age);
                        if (age >= 18 && age <= 65) {
                            intent = new Intent(context, RedEnvelopeRechargeActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "充值红包豆的会员必须是18-65周岁的人", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "身份证信息异常", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    intent = new Intent(context, RedEnvelopeRechargeActivity.class);
                    startActivity(intent);
                }



                break;
            case R.id.topackup:
                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("为保障您的交易安全，请先设置您的交易密码", context, "确定", "取消",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context,
                                            SetSencondPwdFrame.class);
                                    startActivity(intent);
                                }
                            }, null);
                    voipDialog.show();
                    return;
                }
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                intent.putExtra("title", "封入红包盒");
                startActivity(intent);
                break;
            case R.id.tovouchers:
                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("为保障您的交易安全，请先设置您的交易密码", context, "确定", "取消",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context,
                                            SetSencondPwdFrame.class);
                                    startActivity(intent);
                                }
                            }, null);
                    voipDialog.show();
                    return;
                }
                intent = new Intent(context, ChangeRedEnvelopeActivity.class);
                intent.putExtra("title", "红包豆转入消费券");
                startActivity(intent);
                break;
            case R.id.toredbox: //红包盒子列表
                intent = new Intent(context, RedEnvelopeBoxActivity.class);
                startActivity(intent);
                break;
            case R.id.toredbeanaccount_rl:
                intent = new Intent(context, RedBeanAccountActivity.class);
                intent.putExtra("title", "红包豆账户");
                intent.putExtra("bean", redPackageInLetBean);
                startActivity(intent);
                break;
            case R.id.toshopmall:
                Util.showIntent(context, StoreMainFrame.class);
                break;
            case R.id.cashcoupon_rl://现金券
                intent = new Intent(context, RedBeanAccountActivity.class);
                intent.putExtra("title", "现金券账户");
                intent.putExtra("bean", redPackageInLetBean);
                startActivity(intent);
                break;

            case R.id.toxiaofeiquan://消费券
                intent = new Intent(context, RedBeanAccountActivity.class);
                intent.putExtra("title", "消费券账户");
                intent.putExtra("bean", redPackageInLetBean);
                startActivity(intent);
                break;
            case R.id.tojiashu:
                if (UserData.getUser().getShowLevel().indexOf("联盟商家") != -1 ||
                        UserData.getUser().getShowLevel().indexOf("(商)") != -1 ||
                        UserData.getUser().getShowLevel().indexOf("城市经理") != -1 ||
                        UserData.getUser().getShowLevel().indexOf("城市总监") != -1
                        ) {
                    intent = new Intent(context, AcceleratorActivity.class);
                    intent.putExtra("bean", redPackageInLetBean);
                    startActivity(intent);
                } else {
                    VideoAudioDialog dialog = new VideoAudioDialog(context);
                    dialog.setLeft("取消").setRight("前去升级");
                    dialog.setLeft(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(context, ProxySiteFrame.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    dialog.setContent("只有创客才享有加速，是否前去升级创客");
                    dialog.show();
                }
                break;
            case R.id.chongzhibi_rl:  //充值币
                Log.e("等级", UserData.getUser().getLevelId() + "LL");
                if (!UserData.getUser().getIs_dyr().equals("1")) {
                    return;
                }
                intent = new Intent(context, RedBeanAccountActivity.class);
                intent.putExtra("title", "充值币账户");
                intent.putExtra("bean", redPackageInLetBean);
                startActivity(intent);
                break;
            case R.id.myteam_rl:
                intent = new Intent(context, PersonTeamDetailActivity.class);
                intent.putExtra("title", "我的团队");
                startActivity(intent);
                break;
        }


    }

    public void getRedBoxmyselfInfo() {
        final CustomProgressDialog cpd = Util.showProgress("正在获取您的信息...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box_v2.aspx?call=GetRedBoxInfo&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
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

                        Gson gson = new Gson();
                        redPackageInLetBean = gson.fromJson(result.toString(), RedPackageInLetBean.class);
                        setRedPackageInfo(redPackageInLetBean);


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
