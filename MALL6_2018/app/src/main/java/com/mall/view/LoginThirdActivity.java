package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.LocationModel;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

public class LoginThirdActivity extends Activity implements TextWatcher {

    @ViewInject(R.id.login_wangjimima)
    private TextView login_wangjimima;
    @ViewInject(R.id.hint)
    private TextView hint;
    @ViewInject(R.id.phone)
    private EditText phone;
    @ViewInject(R.id.pwd)
    private EditText pwd;
    @ViewInject(R.id.next)
    private Button next;

    private String openId = "";
    private String sessionType = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_third);
        ViewUtils.inject(this);
        initComponent();
    }

    private void initComponent() {
        openId = this.getIntent().getStringExtra("openId");
        sessionType = this.getIntent().getStringExtra("sessionType");
        if (sessionType.equals("0")) {
            type = "QQ";
        } else if (sessionType.equals("1"))
            type = "微信";
        hint.setText("关联后，您的" + type + "账号和远大账号都可以登录");
        phone.addTextChangedListener(this);
        pwd.addTextChangedListener(this);
    }

    @OnClick({R.id.topback, R.id.next, R.id.login_wangjimima})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.topback:
                finish();
                break;
            case R.id.login_wangjimima:
                Intent intent = new Intent();
                intent.setClass(this, ForgetPasswordFrame.class);
                intent.putExtra("class", LoginFrame.class.toString());
                this.startActivity(intent);
                break;
            case R.id.next:
                login();
                break;
        }
    }

    private void login() {
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在登录...");
        String lat = "";
        String lon = "";
        String province = "";
        String city = "";
        try {
            LocationModel locationModel = LocationModel.getLocationModel();
            if (!Util.isNull(locationModel.getLatitude())) {
                lat = locationModel.getLatitude() + "";
                lon = locationModel.getLongitude() + "";
                province = locationModel.getProvince();
                if (!Util.isNull(province)) {
                    province = Util.get(province);
                }
                city = locationModel.getCity();
                if (!Util.isNull(city)) {
                    city = Util.get(city);
                }
            }
        } catch (Exception e) {
            LogUtils.e("获取定位错误！");
        }
        NewWebAPI.getNewInstance().doLogin(phone.getText().toString(), new MD5().getMD5ofStr(pwd.getText().toString()), lat, lon, province, city, openId, sessionType, "",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络超时，请重试！", LoginThirdActivity.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            final AlertDialog dialog = new AlertDialog.Builder(LoginThirdActivity.this).create();
                            View view = LayoutInflater.from(LoginThirdActivity.this).inflate(R.layout.login_dialog_panel,
                                    null);
                            TextView msg = view.findViewById(R.id.login_dialog_message);
                            TextView btn1 = view.findViewById(R.id.login_dialog_button1);
                            TextView btn2 = view.findViewById(R.id.login_dialog_button2);
                            msg.setText(json.getString("message"));
                            btn1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LoginThirdActivity.this.pwd.setFocusable(true);
                                    LoginThirdActivity.this.pwd.setFocusableInTouchMode(true);
                                    LoginThirdActivity.this.pwd.requestFocus();
                                    LoginThirdActivity.this.pwd.setText("");
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                            if (json.getIntValue("code") == 999) {
                                btn2.setText("登录原账户");
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setClass(LoginThirdActivity.this, LoginFrame.class);
                                        LoginThirdActivity.this.startActivity(intent);
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });
                            } else {
                                btn2.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent();
                                        intent.setClass(LoginThirdActivity.this, ForgetPasswordFrame.class);
                                        intent.putExtra("class", LoginFrame.class.toString());
                                        LoginThirdActivity.this.startActivity(intent);
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });
                            }
                            dialog.setTitle(null);
                            dialog.setView(view, 0, 0, 0, 0);
                            dialog.show();
                            return;
                        } else {
                            User user = new User();
                            user.setMd5Pwd(new MD5().getMD5ofStr(pwd.getText().toString()));
                            user.setUserId(phone.getText().toString());
                            UserData.setUser(user);
                            Web.reDoLogin(user.getUserId(), user.getMd5Pwd());
                            VoipDialog voipDialog = new VoipDialog("关联成功", phone.getText().toString() + " 已经与您的" + type + "账号成功关联！该账号和您的" + type + "账号都可以用来登录", LoginThirdActivity.this, "我知道了", null, new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Util.showIntent(LoginThirdActivity.this, Lin_MainFrame.class, new String[]{"toTab"},
                                            new String[]{"usercenter"});
                                    finish();
                                }
                            }, null);
                            voipDialog.show();
                        }
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (phone.getText().toString().length() >= 3 && pwd.getText().toString().length() > 5) {
            next.setEnabled(true);
        } else {
            next.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
