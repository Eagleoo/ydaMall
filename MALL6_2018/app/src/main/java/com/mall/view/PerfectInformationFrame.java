package com.mall.view;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.CapitalAccount.AddBankCardActivity;
import com.mall.CapitalAccount.MyAccount_BankListActivity;
import com.mall.MessageEvent;
import com.mall.model.User;
import com.mall.model.UserAccountWithdrawal;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.L;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;

import butterknife.BindView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import static com.mall.util.Util.MSG_AUTH_CANCEL;
import static com.mall.util.Util.MSG_AUTH_COMPLETE;
import static com.mall.util.Util.MSG_AUTH_ERROR;
import static com.mall.util.Util.MSG_LOGIN;

/**
 * 功能： 完善用户资料页面<br>
 * 时间： 2014-10-10<br>
 * 备注： <br>
 *
 * @author Lin~
 */
public class PerfectInformationFrame extends BasicActivity implements Handler.Callback, PlatformActionListener {

    @BindView(R.id.bindingwechat)
    TextView bindingwechat;

    @BindView(R.id.bindingzhifubao)
    TextView bindingzhifubao;

    @BindView(R.id.add_bankid)
    TextView add_bankid;

    @BindView(R.id.bindingqq)
    TextView bindingqq;

    @BindView(R.id.update_user_message)
    TextView update_user_message;

    private User user;

    private Platform platform;

    private String weixinstate = "-1";// 0 为绑定 1为绑定
    private String zhifubaoinstate = "-1";// 0 为绑定 1为绑定
    private String bankinstate = "-1";// 0 为绑定 1为绑定
    CustomProgressDialog cpdlogin;
    private String qqstate = "-1";// 0 为绑定 1为绑定
    private String title = "完善资料";


    @Override
    public int getContentViewId() {
        return R.layout.perfect_information_frame;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        if (!Util.isNull(getIntent().getStringExtra("type"))) {
            String type = getIntent().getStringExtra("type");
            if (type.equals("bank")) {
                title = "银行卡";
                update_user_message.setVisibility(View.GONE);
                bindingzhifubao.setVisibility(View.GONE);
                bindingwechat.setVisibility(View.GONE);
                bindingqq.setVisibility(View.GONE);
            } else if (type.equals("zfb/vx")) {
                title = "支付宝/微信";
                add_bankid.setVisibility(View.GONE);
                update_user_message.setVisibility(View.GONE);
                bindingqq.setVisibility(View.GONE);
            }
        }
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (null == UserData.getUser()) {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
        } else {
            user = UserData.getUser();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null == UserData.getUser()) {
            Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
        } else {
            user = UserData.getUser();
            check_band_account();
        }
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    @butterknife.OnClick({
            R.id.bindingwechat,
            R.id.bindingzhifubao, R.id.add_bankid, R.id.update_user_message
            , R.id.bindingqq
            , R.id.bindingzhifubao1
    })
    public void click(View view) {

        switch (view.getId()) {
            case R.id.add_bankid:

                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(PerfectInformationFrame.this,
                                    SetSencondPwdFrame.class);
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




                if (bankinstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了银行卡", Toast.LENGTH_SHORT).show();
                } else if (bankinstate.equals("0")) {
                    Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagbank});
                } else {
                    Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"银行卡"});
                }
                break;
            case R.id.update_user_message:
                Util.showIntent(this, UpdateUserMessageActivity.class);
                break;
            case R.id.bindingwechat:

                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(PerfectInformationFrame.this,
                                    SetSencondPwdFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                    return;
                }

                if (weixinstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了微信", Toast.LENGTH_SHORT).show();
                } else if (weixinstate.equals("0")) {
                    Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagweixin});
                } else {
                    Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"微信"});
                }
                break;
            case R.id.bindingqq:
                if (qqstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了QQ", Toast.LENGTH_SHORT).show();
                } else if (qqstate.equals("0")) {
                    Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagQQ});
                } else {
                    Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"QQ"});
                }
                break;
            case R.id.bindingzhifubao:
                if ("0".equals(UserData.getUser().getSecondPwd())) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(PerfectInformationFrame.this,
                                    SetSencondPwdFrame.class);
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



                if (zhifubaoinstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了支付宝", Toast.LENGTH_SHORT).show();
                } else if (zhifubaoinstate.equals("0")) {
                    Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagzhifubao});
                } else {
                    Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"支付宝"});
                }
                break;
            case R.id.bindingzhifubao1:
//                new AliPayNet(this).functionlogIn();
                break;
        }

    }

    @Override
    public boolean handleMessage(Message msg) {
        if (cpdlogin != null) {
            cpdlogin.dismiss();
            cpdlogin.cancel();
        }
        final HashMap<String, Object> userInfo = (HashMap<String, Object>) msg.obj;
        switch (msg.what) {
            case MSG_LOGIN: {

            }
            break;
            case MSG_AUTH_CANCEL: {
                Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_CANCEL--------");
            }
            break;
            case MSG_AUTH_ERROR: {
                Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_ERROR--------");
            }
            break;
            case MSG_AUTH_COMPLETE: {
                Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
                System.out.println("--------MSG_AUTH_COMPLETE-------");
                if (this.platform != null) {
                    bindingaccount(platform.getDb().getUserId(), "1", "", "");
                }
            }
            break;
        }
        return false;
    }

    @Override
    public void onComplete(Platform platform, int action, HashMap<String, Object> hashMap) {
        if (action == Platform.ACTION_USER_INFOR) {
            L.e("[" + hashMap + "]");
            L.e("------User getUserGender ---------" + platform.getDb().getUserGender());
            L.e("------User getUserIcon ---------" + platform.getDb().getUserIcon());
            L.e("------User getUserName ---------" + platform.getDb().getUserName());
            L.e("------User Name ---------" + platform.getName());
            L.e("------User ID ---------" + platform.getDb().getUserId());
            L.e("------Open ID ---------" + platform.getDb().getUserId());
            this.platform = platform;
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);

        }
    }

    @Override
    public void onError(Platform platform, int action, Throwable throwable) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        throwable.printStackTrace();
    }

    @Override
    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void bindingaccount(String openid, String type_, String code, String code_img) {
        String message = "";
        if (type_.equals("1")) {
            message = "微信";
        } else if (type_.equals("2")) {
            message = "支付宝";
        }

        final CustomProgressDialog cpd = Util.showProgress(message + "绑定中...", this);
        final String finalMessage = message;
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "band_wx_alipay" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&open_id=" + openid + "&type_=" + type_ + "&code=" + code + "&code_img=" + code_img
                ,
                new NewWebAPIRequestCallback() {

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
                        if (200 == json.getIntValue("code")) {
                            Util.show("你已经成功绑定了" + finalMessage, context);

                            bindingwechat.setText("\t你已经绑定了微信");
                            weixinstate = "1";
                        }


                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    private void check_band_account() {
        final CustomProgressDialog cpd = Util.showProgress("账户检查中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "get_tx_way" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                ,
                new NewWebAPIRequestCallback() {
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
                        UserAccountWithdrawal userAccountWithdrawal = gson.fromJson(result.toString(), UserAccountWithdrawal.class);
                        UserAccountWithdrawal.ListBean listBean = userAccountWithdrawal.getList().get(0);
                        weixinstate = listBean.getWx();
                        zhifubaoinstate = listBean.getAlipay();
                        bankinstate = listBean.getBank();
                        qqstate = listBean.getQq();

                        if (listBean.getBank().equals("0")) {//  银行卡
                            add_bankid.setText("\t未绑定银行卡,去绑定");
                        } else {
                            add_bankid.setText("\t已经绑定银行卡");
                        }
                        if (listBean.getWx().equals("0")) { // 微信
                            bindingwechat.setText("\t未绑定微信,去绑定");
                        } else {
                            bindingwechat.setText("\t你已经绑定了微信");
                        }
                        if (listBean.getAlipay().equals("0")) { //支付宝
                            bindingzhifubao.setText("\t未绑定支付宝,去绑定");
                        } else {
                            bindingzhifubao.setText("\t你已经绑定了支付宝");
                        }
                        if (listBean.getQq().equals("0")) { //支付宝
                            bindingqq.setText("\t未绑定QQ,去绑定");
                        } else {
                            bindingqq.setText("\t你已经绑定了QQ");
                        }
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

}
