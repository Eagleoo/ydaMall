package com.mall.view;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.AsynTask;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterThirdActivity extends Activity implements TextWatcher {

    @ViewInject(R.id.login)
    private TextView login;
    @ViewInject(R.id.hint)
    private TextView hint;
    @ViewInject(R.id.regPhone)
    private EditText regPhone;
    @ViewInject(R.id.yzmLinearLayout)
    private LinearLayout yzmLinearLayout;
    @ViewInject(R.id.yzmpic_code)
    private EditText yzmpic_code;
    @ViewInject(R.id.regPwd)
    private EditText regPwd;
    @ViewInject(R.id.yzmpic_view)
    private ImageView yzmpic_view;
    @ViewInject(R.id.update_basic_user_info_code)
    private EditText code;
    @ViewInject(R.id.update_basic_user_info_send_code)
    private Button send_code;
    @ViewInject(R.id.next)
    private Button next;

    private SmsObserver smsObserver;
    private Uri SMS_INBOX = Uri.parse("content://sms/");
    private int _10dp = 10;

    private String openId = "";
    private String sessionType = "";
    private String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_third);
        ViewUtils.inject(this);
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);
        _10dp = Util.dpToPx(this, 10F);
        initComponent();
        Spanned html2 = Html.fromHtml("已有账号，立即<font color=\"#49afef\">登录" + "</font>");
        login.setText(html2);
    }

    @OnClick({R.id.topback, R.id.next, R.id.update_basic_user_info_send_code, R.id.login})
    private void click(View v) {
        switch (v.getId()) {
            case R.id.topback:
                finish();
                break;
            case R.id.update_basic_user_info_send_code:
                Util.asynTask(this, "正在发送验证码...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null == runData) {
                            Util.show("网络错误，请重试！", RegisterThirdActivity.this);
                            return;
                        }
                        if ((runData + "").equals("图形验证码不正确")) {
                            Util.show(runData + "", RegisterThirdActivity.this);
                            yzmLinearLayout.setVisibility(View.VISIBLE);
                            Picasso.with(RegisterThirdActivity.this).load("http://" + Web.webImage
                                    + "/ashx/ImgCode.ashx?action=imgcode&phone=" + regPhone.getText().toString())
                                    .skipMemoryCache().into(yzmpic_view);
                            yzmpic_code.setText("");
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            new TimeThread().start();
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
                Intent intent = new Intent(this, LoginFrame.class);
                intent.putExtra("source", "reg");
                this.startActivity(intent);
                break;
            case R.id.next:
                register();
                break;
        }
    }

    private void register() {
        final String phone = regPhone.getText().toString();
        final String pwd = regPwd.getText().toString();
        final String yzmcode = code.getText().toString();
        Util.asynTask(this, "正在注册中...", new AsynTask() {
            @Override
            public Serializable run() {
                Web web = new Web(Web.registor,
                        "userid=" + phone + "_p" + "&pwd="
                                + pwd + "&code=" + yzmcode
                                + "&phone=" + phone + "&inviter=" + "" + "&thirdLoginId=" + openId
                                + "&thirdLoginType=" + sessionType);
                String result = web.getPlan();
                return result;
            }

            @Override
            public void updateUI(Serializable result) {
                if (Util.isNull(result)) {
                    Util.show("网络错误，请重试！", RegisterThirdActivity.this);
                    return;
                }
                if (result.toString().contains("success")) {
                    User user = new User();
                    user.setMd5Pwd(new MD5().getMD5ofStr(pwd));
                    user.setUserId(phone);
                    UserData.setUser(user);
                    Web.reDoLogin(user.getUserId(), user.getMd5Pwd());
                    VoipDialog voipDialog = new VoipDialog("关联成功", phone + " 已经与您的" + type + "账号成功关联！该账号和您的" + type + "账号都可以用来登录", RegisterThirdActivity.this, "我知道了", null, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Util.showIntent(RegisterThirdActivity.this, Lin_MainFrame.class, new String[]{"toTab"},
                                    new String[]{"usercenter"});
                            finish();
                        }
                    }, null);
                    voipDialog.show();
                } else {
                    Util.show(result + "", RegisterThirdActivity.this);
                }
            }

        });
    }

    private void initComponent() {
        openId = this.getIntent().getStringExtra("openId");
        sessionType = this.getIntent().getStringExtra("sessionType");
        if (sessionType.equals("0")) {
            type = "QQ";
        } else if (sessionType.equals("1"))
            type = "微信";
        hint.setText("注册后，您的" + type + "账号和远大账号都可以登录");
        regPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (regPhone.getText().toString().length() == 11) {
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
                                    Util.show("网络异常，请稍后再试...", RegisterThirdActivity.this);
                                    return;
                                }
                                if ("success".equals(runData + "")) {
                                    setImageOk();
                                    yzmLinearLayout.setVisibility(View.VISIBLE);
                                    Picasso.with(RegisterThirdActivity.this).load("http://" + Web.webImage
                                            + "/ashx/ImgCode.ashx?action=imgcode&phone=" + regPhone.getText().toString())
                                            .skipMemoryCache().into(yzmpic_view);
                                    yzmpic_code.setText("");
                                    return;
                                } else {
                                    yzmLinearLayout.setVisibility(View.GONE);
                                    Util.show(runData + "", RegisterThirdActivity.this);
                                    setImageError();
                                    VoipDialog voipDialog = new VoipDialog("该手机号已经被注册，验证手机号后可直接登录", RegisterThirdActivity.this, "确定", "取消", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Util.showIntent(RegisterThirdActivity.this, phoneLoginActivity.class);
                                            finish();
                                        }
                                    }, null);
                                    voipDialog.show();
                                }
                            }
                        });
                    } else {
                        Util.show("请正确输入手机号码", RegisterThirdActivity.this);
                        setImageError();
                    }
                } else {
                    yzmLinearLayout.setVisibility(View.GONE);
                    setImageError();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        code.addTextChangedListener(this);
        regPwd.addTextChangedListener(this);
    }

    private void setImageOk() {
        Resources res = this.getResources();
        Drawable success = res.getDrawable(R.drawable.right);
        success.setBounds(0, 0, success.getMinimumWidth(), success.getMinimumHeight());
        regPhone.setCompoundDrawables(null, null, success, null);
        regPhone.setPadding(0, 0, _10dp, 0);
    }

    private void setImageError() {
        Resources res = this.getResources();
        Drawable error = res.getDrawable(R.drawable.error);
        error.setBounds(0, 0, error.getMinimumWidth(), error.getMinimumHeight());
        regPhone.setCompoundDrawables(null, null, error, null);
        regPhone.setPadding(0, 0, _10dp, 0);
    }

    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        // TODO

    };

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

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (regPhone.getText().toString().length() == 11 && regPwd.getText().toString().length() > 5 && code.getText().toString().length() == 4) {
            next.setEnabled(true);
        } else {
            next.setEnabled(false);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

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

    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"body"};// "_id", "address",
        // "person",, "date",
        // "type
        String where = " date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            // 这里我是要获取自己短信服务号码中的验证码~~
            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
            Matcher matcher = pattern.matcher(body);
            code.setText(patternCode(body));
        }
    }

    class SmsObserver extends ContentObserver {

        public SmsObserver(Context context, Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            // 每当有新短信到来时，使用我们获取短消息的方法
            getSmsFromPhone();
        }
    }
}
