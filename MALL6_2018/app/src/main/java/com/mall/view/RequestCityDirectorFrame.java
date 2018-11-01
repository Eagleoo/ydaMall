package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.android.appDemo4.AliPayCallBack;
import com.alipay.android.appDemo4.AliPayNet;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.PayType;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

/**
 * 功能： 申请城市总监<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class RequestCityDirectorFrame extends Activity {
    @ViewInject(R.id.proxy_city_director_zsdw)
    private EditText zsdw_ = null;
    @ViewInject(R.id.proxy_city_directorPhone)
    private EditText phone = null;
    @ViewInject(R.id.request_city_director_name_line)
    private TextView zsdwInfo;
    @ViewInject(R.id.request_city_director_phone_line)
    private LinearLayout phoneLine = null;
    @ViewInject(R.id.request_city_director_pwd_line)
    private LinearLayout twopwdLine = null;
    @ViewInject(R.id.proxy_city_director_pwd)
    private EditText twoPwd = null;


    @ViewInject(R.id.pay_item_rec_ban)
    private TextView pay_item_rec_ban;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.request_proxy_city_director);
        ViewUtils.inject(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        init();
    }


    @OnFocusChange(R.id.proxy_city_director_zsdw)
    public void onBlur(View v, boolean hasFour) {
        if (!hasFour)
            setInviter(zsdw_.getText().toString());
    }


    public String getPayType() {
        return PayType.getPayType(this);
    }


    private void initComponent() {
        User user = UserData.getUser();
        if (null != user && !Util.isNull(user.getMobilePhone())) {
            phoneLine.setVisibility(View.GONE);
            phone.setText(user.getMobilePhone());
        } else {
            phoneLine.setVisibility(View.VISIBLE);
        }
        if (null != user && !"远大商城".equals(UserData.getUser().getInviter())) {
            setInviter(UserData.getUser().getInviter());
        }
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("获取账户余额失败，请充值！", RequestCityDirectorFrame.this);
                    return;
                }
                pay_item_rec_ban.setText("余额:" + runData + "");
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getMoney,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                + "&type=rec");
                return web.getPlan();
            }
        });
    }

    private void setInviter(final String name) {
        Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    InviterInfo ii = (InviterInfo) runData;
                    int levelId = Util.getInt(ii.getLevel());
                    int shopId = Util.getInt(ii.getShopType());
                    boolean isSite = "1".equals(ii.getIsSite());
                    if (5 == levelId || isSite) {
                        zsdw_.setText(ii.getUserid());
                        String name = ii.getName();
                        if (!Util.isNull(name) && 2 <= name.length()) {
                            name = name.substring(0, 1) + "*";
                            if (3 <= ii.getName().length())
                                name += ii.getName().substring(ii.getName().length() - 1);
                        }
                        String phone = ii.getPhone();
                        if (!Util.isNull(phone))
                            phone = phone.substring(0, 3)
                                    + "****"
                                    + phone.subSequence(phone.length() - 4,
                                    phone.length());
                        zsdwInfo.setText("用户资料： " + name + "　" + phone);
                    }
                    zsdwInfo.setVisibility(View.VISIBLE);
                } else {
                    zsdwInfo.setText("用户资料： 　");
                    zsdwInfo.setVisibility(View.GONE);
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getInviter, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&uid="
                        + Util.get(name));
                InviterInfo ii = web.getObject(InviterInfo.class);
                return ii;
            }
        });
    }

    private void init() {
        Util.initTop(this, "申请城市总监", Integer.MIN_VALUE, null);
        initComponent();
        if (null != UserData.getUser()) {
            String secondPwd = UserData.getUser().getSecondPwd();
            if (Util.isNull(secondPwd) || "0".equals(secondPwd)) {
                Util.showIntent("您好，申请【城市总监】需要完善您的个人信息!\n现在需要去完善资料吗？",
                        RequestCityDirectorFrame.this, UpdateTwoPwd.class);
                return;
            }

            this.findViewById(R.id.pay_item_sb_line).setVisibility(View.GONE);
            View ali = this.findViewById(R.id.pay_item_ali_line);
            View rec = this.findViewById(R.id.pay_item_rec_line);
            View xj = this.findViewById(R.id.pay_item_xj_line);
            View uppay = this.findViewById(R.id.pay_item_uppay_line);
            View weixin = this.findViewById(R.id.pay_item_weixin_line);
            PayType.addPayTypeListener(this, R.id.pay_item_xj_line, twopwdLine, ali, rec, xj, uppay, weixin);
        } else {
            Util.show("请重新登录！", this);
        }
    }


    @OnClick(R.id.proxy_city_director_submit)
    public void submitClick(View view) {
        User user = UserData.getUser();
        if (null == user) {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
            return;
        }
        if (Util.isNull(phone.getText().toString())) {
            Util.show("请输入手机号码！", RequestCityDirectorFrame.this);
            return;
        }
        if (Util.isNull(zsdw_.getText().toString())) {
            Util.show("请输入辅导老师！", RequestCityDirectorFrame.this);
            return;
        }
        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", RequestCityDirectorFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(RequestCityDirectorFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }



        final String payType = getPayType();
        Util.asynTask(RequestCityDirectorFrame.this, "正在提交您的申请...",
                new IAsynTask() {
                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.requestCityDirector, "userId="
                                + UserData.getUser().getUserId()
                                + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd()
                                + "&twoPwd="
                                + twoPwd.getText().toString()
                                + "&zsdw="
                                + Util.get(zsdw_.getText()
                                .toString())
                                + "&copyPhone="
                                + phone.getText().toString()
                                + "&zoneId=5&level=5&payType=" + payType);
                        return web.getPlan();
                    }

                    @Override
                    public void updateUI(Serializable runData) {
                        if (null == runData) {
                            Util.show("网络错误，请重试！", RequestCityDirectorFrame.this);
                            return;
                        }
                        if ("success".equals(runData))
                            Util.showIntent("申请成功！",
                                    RequestCityDirectorFrame.this,
                                    ProxySiteFrame.class);
                        else if ((runData + "").contains("完善您的个人信息"))
                            Util.showIntent(runData
                                            + "\n现在需要去完善资料吗？",
                                    RequestCityDirectorFrame.this,
                                    UpdateUserFrame.class);
                        else if ((runData + "").contains("success:")) {
                            String[] info = (runData + "").split(":");
                            aliPay(info[1], Util.getDouble(info[2]));
                        } else
                            Util.show(runData + "", RequestCityDirectorFrame.this);
                    }
                });
    }

    @OnClick(R.id.proxy_city_director_clear)
    public void clearClick(View view) {
        phone.setText("");
        zsdw_.setText("");
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

    private void aliPay(String orderId, double money) {
        new AliPayNet(this).pay(orderId, "申请创业空间", money, new AliPayCallBack() {
            @Override
            public void doSuccess(String aliResultCode) {
                Util.showIntent(RequestCityDirectorFrame.this,
                        Lin_MainFrame.class);
                Util.show("创业空间申请成功！", RequestCityDirectorFrame.this);
            }

            @Override
            public void doFailure(String aliResultCode) {
                Util.show("申请失败，支付宝状态码" + aliResultCode,
                        RequestCityDirectorFrame.this);
            }
        });
    }

}
