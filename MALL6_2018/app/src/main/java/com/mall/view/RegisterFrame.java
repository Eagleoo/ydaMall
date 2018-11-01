package com.mall.view;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.Dialog_can_not_receive;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.AsynTask;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 功能： 注册页面<br>
 */
public class RegisterFrame extends AppCompatActivity implements TextWatcher {

    @ViewInject(R.id.provision)
    private TextView provision;
    @ViewInject(R.id.login)
    private TextView login;
    @ViewInject(R.id.phone)
    private TextView phone;
    @ViewInject(R.id.cannotyam)
    private TextView cannotyam;
    @ViewInject(R.id.LL_1)
    private LinearLayout LL_1;
    @ViewInject(R.id.LL_2)
    private LinearLayout LL_2;
    @ViewInject(R.id.LL_3)
    private LinearLayout LL_3;
    @ViewInject(R.id.LL_4)
    private LinearLayout LL_4;
    @ViewInject(R.id.step_1)
    private LinearLayout step_1;
    @ViewInject(R.id.step_2)
    private LinearLayout step_2;
    @ViewInject(R.id.step_3)
    private LinearLayout step_3;
    @ViewInject(R.id.step_4)
    private LinearLayout step_4;
    @ViewInject(R.id.regPhone)
    private EditText regPhone;
    @ViewInject(R.id.yzmpic_code)
    private EditText yzmpic_code;
    @ViewInject(R.id.regPwd1)
    private EditText regPwd1;
    @ViewInject(R.id.regPwd2)
    private EditText regPwd2;
    @ViewInject(R.id.regID)
    private EditText regID;
    @ViewInject(R.id.yzmpic_view)
    private ImageView yzmpic_view;
    @ViewInject(R.id.next)
    private Button next;
    @ViewInject(R.id.update_basic_user_info_code)
    private EditText code;
    @ViewInject(R.id.update_basic_user_info_send_code)
    private Button send_code;
    @ViewInject(R.id.checkBox)
    private CheckBox checkBox;
    private int _10dp = 10;

    private int step = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.register);
        ViewUtils.inject(this);
        if (getIntent().hasExtra("openClassName")) {
            Leading.isOtherApp = true;
        }
        _10dp = Util.dpToPx(this, 10F);
        initComponent();
        Spanned html1 = Html.fromHtml("注册便视为同意<font color=\"#49afef\">《远大云商注册条款》" + "</font>");
        provision.setText(html1);
        Spanned html2 = Html.fromHtml("已有账号，立即<font color=\"#49afef\">登录" + "</font>");
        login.setText(html2);
    }

    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        // TODO

    };

    @OnClick({R.id.next, R.id.provision, R.id.cannotyam, R.id.update_basic_user_info_send_code, R.id.login, R.id.cannotyam})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.next:
                next.setEnabled(false);
                if (step == 1) {
                    if (!checkBox.isChecked()) {
                        Util.showAlert("请阅读并同意", this);
                        next.setEnabled(true);
                        return;
                    }
                    if (!Util.isNull(yzmpic_code.getText().toString()) && !Util.isNull(regPhone.getText().toString())) {
                        Util.asynTask(this, "正在发送验证码...", new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if (null == runData) {
                                    Util.showAlert("网络错误，请重试！", RegisterFrame.this);
                                    return;
                                }
                                if ((runData + "").equals("图形验证码不正确")) {
                                    Util.showAlert(runData + "", RegisterFrame.this);
                                    Picasso.with(RegisterFrame.this).load("http://" + Web.webImage
                                            + "/ashx/ImgCode.ashx?action=imgcode&phone=" + regPhone.getText().toString())
                                            .skipMemoryCache().into(yzmpic_view);
                                    yzmpic_code.setText("");
                                    return;
                                }
                                if ((runData + "").startsWith("success:")) {
                                    new TimeThread().start();
                                    phone.setText(regPhone.getText().toString());
                                    LL_1.setVisibility(View.GONE);
                                    LL_2.setVisibility(View.VISIBLE);
                                    LL_3.setVisibility(View.GONE);
                                    LL_4.setVisibility(View.GONE);
                                    step_1.setBackground(null);
                                    step_2.setBackgroundResource(R.drawable.register_step_bg);
                                    step_3.setBackground(null);
                                    step_4.setBackground(null);
                                    step_4.setVisibility(View.GONE);
                                    next.setText("提交验证码");
                                    cannotyam.setVisibility(View.VISIBLE);
                                    code.setText("");
                                    step = 2;
                                }
                            }

                            @Override
                            public Serializable run() {
                                Web web = new Web(Web.sendRandomCodeForNoMd5Pwd,
                                        "phone=" + regPhone.getText().toString() + "&imgcode=" + yzmpic_code.getText().toString());
                                return web.getPlan();
                            }
                        });
                    } else
                        Util.showAlert("请完善信息", RegisterFrame.this);
                } else if (step == 2) {
                    if (!Util.isNull(code.getText().toString())) {
                        Util.asynTask(RegisterFrame.this, "验证中..", new IAsynTask() {
                            @Override
                            public Serializable run() {
                                Web web = new Web(Web.newAPI + "/user.aspx", "", "call=check_user_mobile_code&phone=" + regPhone.getText().toString() + "&imgCode=" + yzmpic_code.getText().toString() + "&code=" + code.getText().toString());
                                return web.getPlan();
                            }

                            @Override
                            public void updateUI(Serializable runData) {
                                if (null == runData) {
                                    Util.showAlert("网络错误，请重试！", RegisterFrame.this);
                                    return;
                                }
                                try {
                                    JSONObject jsonObject = new JSONObject(runData.toString());
                                    if (jsonObject.getString("message").equals("success")) {
                                        LL_1.setVisibility(View.GONE);
                                        LL_2.setVisibility(View.GONE);
                                        LL_3.setVisibility(View.VISIBLE);
                                        LL_4.setVisibility(View.GONE);
                                        step_1.setBackground(null);
                                        step_2.setBackground(null);
                                        step_3.setBackgroundResource(R.drawable.register_step_bg);
                                        step_4.setBackground(null);
                                        step_4.setVisibility(View.GONE);
                                        next.setText("确认注册");
                                        cannotyam.setVisibility(View.GONE);
                                        step = 3;
                                    } else {
                                        if (runData.toString().contains("短信验证码输入错误！")) {
                                            Util.showAlert("短信验证码输入错误！", RegisterFrame.this);
                                            code.setText("");
                                        } else if (runData.toString().contains("短信验证码已过期")) {
                                            Util.showAlert("短信验证码已过期！", RegisterFrame.this);
                                        } else {
                                            Util.showAlert("图形验证码已过期", RegisterFrame.this);
                                            step = 1;
                                            LL_1.setVisibility(View.VISIBLE);
                                            LL_2.setVisibility(View.GONE);
                                            LL_3.setVisibility(View.GONE);
                                            LL_4.setVisibility(View.GONE);
                                            step_1.setBackgroundResource(R.drawable.register_step_bg);
                                            step_2.setBackground(null);
                                            step_3.setBackground(null);
                                            step_4.setBackground(null);
                                            step_4.setVisibility(View.GONE);
                                            yzmpic_code.setText("");
                                            next.setText("发送手机验证码");
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } else
                        Util.showAlert("请完善信息", RegisterFrame.this);
                } else if (step == 3) {
                    if (!regPwd1.getText().toString().equals(regPwd2.getText().toString())) {
                        Util.showAlert("两次密码不一致！", RegisterFrame.this);
                    } else {
                        VoipDialog voipDialog = new VoipDialog("是否设置用户名，用户名与手机号码都可以登录。", RegisterFrame.this, "是", "否", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                LL_1.setVisibility(View.GONE);
                                LL_2.setVisibility(View.GONE);
                                LL_3.setVisibility(View.GONE);
                                LL_4.setVisibility(View.VISIBLE);
                                step_1.setBackground(null);
                                step_2.setBackground(null);
                                step_3.setBackground(null);
                                step_4.setVisibility(View.VISIBLE);
                                step_4.setBackgroundResource(R.drawable.register_step_bg);
                                next.setText("完成");
                                cannotyam.setVisibility(View.GONE);
                                step = 4;
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                register();
                            }
                        });
                        voipDialog.show();
                    }
                } else {
                    register();
                }
                break;
            case R.id.provision:
                Util.showIntent(this, ProvisionActivity.class);
                break;
            case R.id.cannotyam:
                int width = getWindowManager().getDefaultDisplay().getWidth() * 8 / 10;
                int height = width / 3 * 5;
                Dialog_can_not_receive dialog = new Dialog_can_not_receive(RegisterFrame.this, R.style.dialog, width, height);
                dialog.show();
                break;
            case R.id.update_basic_user_info_send_code:
                Util.asynTask(this, "正在发送验证码...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null == runData) {
                            Util.showAlert("网络错误，请重试！", RegisterFrame.this);
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            new TimeThread().start();
                        } else {
                            Util.showAlert("图形验证码已过期", RegisterFrame.this);
                            step = 1;
                            LL_1.setVisibility(View.VISIBLE);
                            LL_2.setVisibility(View.GONE);
                            LL_3.setVisibility(View.GONE);
                            LL_4.setVisibility(View.GONE);
                            step_1.setBackgroundResource(R.drawable.register_step_bg);
                            step_2.setBackground(null);
                            step_3.setBackground(null);
                            step_4.setBackground(null);
                            step_4.setVisibility(View.GONE);
                            yzmpic_code.setText("");
                            next.setText("发送手机验证码");
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.sendRandomCodeForNoMd5Pwd,
                                "phone=" + regPhone.getText().toString() + "&imgcode=" + yzmpic_code.getText().toString());
                        return web.getPlan();
                    }
                });
                break;
            case R.id.login:
                Intent intent = new Intent(RegisterFrame.this, LoginFrame.class);
                intent.putExtra("source", "reg");
                RegisterFrame.this.startActivity(intent);
                break;
        }
    }

    private void register() {
        final String phone = regPhone.getText().toString();
        final String ID = Util.isNull(regID.getText().toString()) ? phone + "_p" : regID.getText().toString();
        final String pwd1 = regPwd1.getText().toString();
        String pwd2 = regPwd2.getText().toString();
        final String yzmcode = code.getText().toString();
        if (TextUtils.isEmpty(yzmcode)) {
            Util.showAlert("验证码不能为空！", RegisterFrame.this);
        } else if (Util.isNull(pwd1)) {
            Util.showAlert("请输入您的密码！", RegisterFrame.this);
        } else if (pwd1.length() < 6) {
            Util.showAlert("密码不能小于6位！", RegisterFrame.this);
        } else if (6 > pwd1.trim().length()) {
            Util.showAlert("空格不能作为登录密码！", RegisterFrame.this);
        } else if (Util.isNull(pwd2)) {
            Util.showAlert("请输入您的确认密码！", RegisterFrame.this);
        } else if (!pwd1.equals(pwd2)) {
            Util.showAlert("两次密码不一致！", RegisterFrame.this);
        } else {
            Intent intent = RegisterFrame.this.getIntent();
            final String inviter = intent.getStringExtra("inviter");
            Util.asynTask(RegisterFrame.this, "正在注册中...", new AsynTask() {
                @Override
                public Serializable run() {
                    Web web = new Web(Web.registor,
                            "userid=" + ID + "&pwd="
                                    + pwd1 + "&code=" + yzmcode
                                    + "&phone=" + phone + "&inviter=" + inviter + "&thirdLoginId=&thirdLoginType=");
                    String result = web.getPlan();
                    return result;
                }

                @Override
                public void updateUI(Serializable result) {
                    if (Util.isNull(result)) {
                        Util.showAlert("网络错误，请重试！", RegisterFrame.this);
                        return;
                    }
                    if (result.toString().contains("success")) {
                        User user = new User();
                        user.setMd5Pwd(new MD5().getMD5ofStr(pwd1));
                        user.setUserId(ID);
                        UserData.setUser(user);
                        Web.reDoLogin(user.getUserId(), user.getMd5Pwd());
                        Util.showIntent(RegisterFrame.this, RegisteSuccessFrame.class);
                        finish();
                    }
                }

            });
        }
    }

    private void initComponent() {
        Util.initTitle(this, "用户快速注册", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        OnFocusChangeListener focus = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (Util.isNull(regPhone.getText().toString()) || Util.isNull(regPhone.getText().toString().trim())) {
                        Util.showAlert("手机号不能为空", RegisterFrame.this);
                        return;
                    }
                    if (Util.checkPhoneNumber(regPhone.getText().toString())) {
                        Util.asynTask(new IAsynTask() {
                            @Override
                            public Serializable run() {
                                Web web = new Web(Web.valiUserName, "userid=" + regPhone.getText().toString());
                                return web.getPlan();
                            }

                            @Override
                            public void updateUI(Serializable runData) {

                                if (Util.isNull(runData)) {
                                    Util.showAlert("网络异常，请稍后再试...", RegisterFrame.this);
                                    return;
                                }
                                if ("success".equals(runData + "")) {
                                    setImageOk(regPhone);
                                    Picasso.with(RegisterFrame.this).load("http://" + Web.webImage
                                            + "/ashx/ImgCode.ashx?action=imgcode&phone=" + regPhone.getText().toString())
                                            .skipMemoryCache().into(yzmpic_view);
                                    yzmpic_code.setText("");
                                    return;
                                } else {
                                    Util.showAlert(runData + "", RegisterFrame.this);
                                    setImageError(regPhone);
                                    VoipDialog voipDialog = new VoipDialog("该手机号已经被注册，验证手机号后可直接登录", RegisterFrame.this, "确定", "取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Util.showIntent(RegisterFrame.this, phoneLoginActivity.class);
                                            finish();
                                        }
                                    }, null);
                                    voipDialog.show();
                                }
                            }
                        });
                    } else {
                        Util.showAlert("请正确输入手机号码", RegisterFrame.this);
                        setImageError(regPhone);
                    }
                }
            }
        };
        yzmpic_code.setOnFocusChangeListener(focus);
        yzmpic_code.addTextChangedListener(this);
        regPhone.addTextChangedListener(this);
        code.addTextChangedListener(this);
        regPwd1.addTextChangedListener(this);
        regPwd2.addTextChangedListener(this);
        regID.addTextChangedListener(this);
    }

    private void setImageOk(EditText editText) {
        Resources res = RegisterFrame.this.getResources();
        Drawable success = res.getDrawable(R.drawable.right);
        success.setBounds(0, 0, success.getMinimumWidth(), success.getMinimumHeight());
        editText.setCompoundDrawables(null, null, success, null);
        editText.setPadding(0, 0, _10dp, 0);
        next.setEnabled(true);
    }

    private void setImageError(EditText editText) {
        Resources res = RegisterFrame.this.getResources();
        Drawable error = res.getDrawable(R.drawable.error);
        error.setBounds(0, 0, error.getMinimumWidth(), error.getMinimumHeight());
        editText.setCompoundDrawables(null, null, error, null);
        editText.setPadding(0, 0, _10dp, 0);
        next.setEnabled(false);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, final int i2) {
        if (step == 1) {
            if (regPhone.length() == 11 && yzmpic_code.length() == 4) {
                next.setEnabled(true);
            } else {
                next.setEnabled(false);
            }
        }
        if (step == 2) {
            if (code.length() == 6 || code.length() == 4) {
                next.setEnabled(true);
            } else {
                next.setEnabled(false);
            }
        }
        if (step == 3) {
            if (regPwd1.length() > 5 && regPwd2.length() > 5) {
                next.setEnabled(true);
            } else {
                next.setEnabled(false);
            }
        }
        if (step == 4) {
            String userText = regID.getText().toString();
            char[] c = userText.toCharArray();
            int len = 0;
            boolean isError = false;
            for (char ch : c) {
                if ((ch + "").matches("[\\u4e00-\\u9fa5]")) {
                    len += 2;
                } else if ((ch + "").matches("\\w")) {
                    len++;
                } else
                    isError = true;
            }
            if (isError) {
                Util.showAlert("会员名包含非法字符", RegisterFrame.this);
                setImageError(regID);
                return;
            }
            if (len > 5) {
                Pattern pattern = Pattern.compile("[0-9]*");
                Matcher isNum = pattern.matcher(userText);
                if (isNum.matches()) {
                    if (!Util.checkPhoneNumber(userText)) {
                        Util.showAlert("您的用户名不能全为数字", RegisterFrame.this);
                        regID.setCompoundDrawables(null, null, null, null);
                        setImageError(regID);
                        return;
                    }
                }
                Util.asynTask(RegisterFrame.this, "用户名检测中...", new IAsynTask() {
                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.valiUserName, "userid=" + Util.get(regID.getText().toString()));
                        return web.getPlan();
                    }

                    @Override
                    public void updateUI(Serializable runData) {
                        if (Util.isNull(runData)) {
                            Util.showAlert("网络异常，请稍后再试...", RegisterFrame.this);
                        }
                        if ("success".equals(runData + "")) {
                            setImageOk(regID);
                            next.setEnabled(true);
                        } else {
                            Util.showAlert(runData + "", RegisterFrame.this);
                            setImageError(regID);
                            next.setEnabled(false);
                        }
                    }
                });
            } else if (len <= 5) {
                next.setEnabled(false);
                if (len == 0) {
                    Util.showAlert("6-20个字符，一个汉字为2个字符", RegisterFrame.this);
                }
                setImageError(regID);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private int s = 60;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (707 == msg.what) {
                if ("0".equals(msg.obj + "")) {
                    send_code.setText("获取验证码");
                    send_code.setEnabled(true);
                    s = 60;
                } else {
                    int ss = Util.getInt(msg.obj);
                    if (10 > ss)
                        ss = Integer.parseInt("0" + ss);
                    send_code.setText("重新获取(" + ss + "S)");
                    send_code.setEnabled(false);
                }
            }
        }
    };

    class TimeThread extends Thread {
        @Override
        public void run() {
            while (s > 0) {
                s--;
                Message msg = new Message();
                msg.what = 707;
                msg.obj = s;
                handler.sendMessage(msg);
                if (0 == s)
                    break;
                try {
                    this.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
