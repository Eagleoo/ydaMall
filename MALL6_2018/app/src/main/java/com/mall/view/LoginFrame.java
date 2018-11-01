package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.lin.component.CustomProgressDialog;
import com.mall.model.LocationModel;
import com.mall.model.LoginUser;
import com.mall.model.LoginUserInfo;
import com.mall.model.ThirdPartyLogin;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.service.MessageService;
import com.mob.tools.utils.UIHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;

/**
 * 功能： 登录窗口<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */

@ContentView(R.layout.login)
public class LoginFrame extends Activity implements Callback, PlatformActionListener {
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;

    @ViewInject(R.id.rember)
    private CheckBox rememberPsw;
    @ViewInject(R.id.name)
    private EditText name = null;
    @ViewInject(R.id.pwd)
    private EditText pwd = null;
    private User user = null;
    private DbUtils dbUtils;
    private BitmapUtils bmUtils;
    @ViewInject(R.id.login_user_hsitory_img)
    private ImageView login_user_hsitory_img;
    @ViewInject(R.id.login_name_clear2)
    private ImageView login_name_clear2;
    @ViewInject(R.id.login_pwd_clear)
    private ImageView login_pwd_clear;
    @ViewInject(R.id.signlogbox)
    private LinearLayout signlogbox;
    @ViewInject(R.id.loginButton)
    private Button loginButton;
    @ViewInject(R.id.xieyi)
    private TextView xieyi;

    private Context context;

    private EditText fourEditText = null;

    private DbUtils db = null;

    private String str = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        dbUtils = DbUtils.create(this, "YDLoginUsers");
        db = DbUtils.create(LoginFrame.this);
        try {
            db.deleteAll(ThirdPartyLogin.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
        bmUtils = new BitmapUtils(this);
        init();
    }

    public void init() {
        initview();
        Util.initTitle(this, "用户登录", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SpannableString ss = new SpannableString(name.getHint().toString());
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        name.setHint(new SpannedString(ss));

        SpannableString p2 = new SpannableString(pwd.getHint().toString());
        AbsoluteSizeSpan p22 = new AbsoluteSizeSpan(12, true);
        p2.setSpan(p22, 0, p2.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        pwd.setHint(new SpannedString(p2));

        pwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (pwd.getText().toString().length() != 0 && fourEditText == pwd)
                    login_pwd_clear.setVisibility(View.VISIBLE);
                else
                    login_pwd_clear.setVisibility(View.GONE);
                if (pwd.length() > 5) {
                    loginButton.setEnabled(true);
                } else {
                    loginButton.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        OnFocusChangeListener focus = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    fourEditText = (EditText) v;
                    if (fourEditText == pwd)
                        login_pwd_clear.setVisibility(View.VISIBLE);
                    if (fourEditText == name)
                        login_name_clear2.setVisibility(View.VISIBLE);
                } else {
                    if (fourEditText == pwd)
                        login_pwd_clear.setVisibility(View.GONE);
                    if (fourEditText == name)
                        login_name_clear2.setVisibility(View.GONE);
                    fourEditText = null;
                }
            }
        };
        name.setOnFocusChangeListener(focus);
        pwd.setOnFocusChangeListener(focus);
        try {
            LoginUserInfo model = dbUtils.findFirst(Selector.from(LoginUserInfo.class).orderBy("id", true));
            if (null != model && !Util.isNull(model.getUserId())) {
                name.setText("");
                pwd.setText("");
                name.setText(Util.getNo_pUserId(model.getUserId()));

                if (!Util.isNull(model.getIssave()) && model.getIssave().equals("0")) {
                    pwd.setText(model.getPwd());
                } else {
                    pwd.setText("");
                }

            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        if (getSharedPreferences("Checked", MODE_PRIVATE).getBoolean("Checked", false) == false) {
            pwd.setText("");
        } else {
            rememberPsw.setChecked(true);
        }
    }

    private void initview() {
        signlogbox.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#B0B0B0"))
                .create());
        xieyi.setText(Html.fromHtml("登录表示你同意该软件 <font color='#49AFEF'>用户服务协议</font>"));
        xieyi.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, ProvisionActivity.class,
                        new String[]{"userloginprotocol"},
                        new String[]{"userloginprotocol"});
            }
        });
    }

    @OnClick(R.id.sms)
    public void setMsgLogin(View view) {
        Util.showIntent(context, phoneLoginActivity.class);
    }

    @OnClick(R.id.login_pwd_clear)
    public void pwdClearClick(View view) {
        pwd.setText("");
    }

    @OnClick(R.id.login_name_clear2)
    public void nameClearClick(View view) {
        name.setText("");
    }

    @OnClick(R.id.tvQq)
    public void loginForQQ(View view) {
        authorize(new QZone());
    }

    @OnClick(R.id.wechat)
    public void loginForWechat(View view) {
        authorize(new Wechat());
    }

    CustomProgressDialog ApplyCpd;

    private void authorize(Platform plat) {

        ApplyCpd = CustomProgressDialog.showProgressDialog(this, "申请授权中...");
        plat.setPlatformActionListener(this);
        plat.removeAccount(true);
        if (plat instanceof QZone) {
            plat.SSOSetting(false);
        } else
            plat.SSOSetting(false);
        plat.showUser(null);
    }

    public void onComplete(Platform platform, int action, HashMap<String, Object> res) {
        if (ApplyCpd != null) {
            ApplyCpd.cancel();
            ApplyCpd.dismiss();
        }

        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            res.put("sessionId", platform.getDb().getUserId());
            res.put("type", platform.getName());
            login(platform.getName(), platform.getDb().getUserId(), res);
        }
        System.out.println("[" + res + "]");
        System.out.println("------User getUserGender ---------" + platform.getDb().getUserGender());
        System.out.println("------User getUserIcon ---------" + platform.getDb().getUserIcon());
        System.out.println("------User getUserName ---------" + platform.getDb().getUserName());
        System.out.println("------User Name ---------" + platform.getName());
        System.out.println("------User ID ---------" + platform.getDb().getUserId());
        System.out.println("------Open ID ---------" + platform.getDb().getUserId());
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (ApplyCpd != null) {
            ApplyCpd.cancel();
            ApplyCpd.dismiss();
        }
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (ApplyCpd != null) {
            ApplyCpd.cancel();
            ApplyCpd.dismiss();
        }
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId, HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = userInfo;
        UIHandler.sendMessage(msg, this);
    }

    public boolean handleMessage(Message msg) {
        final HashMap<String, Object> userInfo = (HashMap<String, Object>) msg.obj;
        switch (msg.what) {
            case MSG_LOGIN: {
                Map<String, String> param = new HashMap<String, String>();
                param.put("third_user", userInfo.get("sessionId") + "");
                param.put("type", "QZone".equals(userInfo.get("type") + "") ? "0" : "1");
                NewWebAPI.getNewInstance().getWebRequest("/User.aspx?call=SL_Binding_third_user", param,
                        new WebRequestCallBack() {
                            @Override
                            public void success(Object result) {
                                super.success(result);
                                if (null == result) {
                                    Util.showAlert("网络异常，请重试！", LoginFrame.this);
                                    return;
                                }
                                JSONObject json = JSON.parseObject(result.toString());
                                int code = json.getIntValue("code");
                                String message = json.getString("message");
                                final String openID = userInfo.get("sessionId") + "";
                                final String type = "QZone".equals(userInfo.get("type") + "") ? "0" : "1";
                                // 说明系统错误
                                if (707 != code && 200 != code) {
                                    Util.showAlert(message, LoginFrame.this);
                                    // 说明绑定过，
                                } else if (200 == code) {
                                    dologin("", "", type, message);
                                } else {
                                    VoipDialog voipDialog = new VoipDialog("检测到您是第一次使用第三方登录，请选择您的绑定方式？", LoginFrame.this, "已有帐号绑定", "没有帐号绑定", new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Util.showIntent(LoginFrame.this, LoginThirdActivity.class,
                                                    new String[]{"openId", "sessionType"},
                                                    new String[]{openID, type});
                                        }
                                    }, new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Util.showIntent(LoginFrame.this, RegisterThirdActivity.class,
                                                    new String[]{"openId", "sessionType"},
                                                    new String[]{openID, type});
                                        }
                                    });
                                    voipDialog.show();
                                }
                            }

                            @Override
                            public void requestEnd() {
                                super.requestEnd();
                            }
                        });
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
            }
            break;
        }
        return false;
    }

    @OnFocusChange(R.id.name)
    public void loginNameForcus(View v, boolean hasForcus) {
        if (hasForcus)
            name.setSelection(name.getText().length());
    }

    @OnClick(R.id.login_user_hsitory)
    public void LoginUserHistory(View v) {
        LinearLayout container = (LinearLayout) this.findViewById(R.id.login_users_container);
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
        } else {
            container.setVisibility(View.VISIBLE);
        }
        List<LoginUser> list;
        try {
            list = dbUtils.findAll(LoginUser.class);
            if (list != null && list.size() > 0) {
                initLoginUsersContainer(list, container);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    @OnFocusChange(R.id.pwd)
    public void passwrodForcus(View v, boolean hasForcus) {
        if (hasForcus) {
            pwd.setSelection(pwd.getText().length());
            LinearLayout container = (LinearLayout) this.findViewById(R.id.login_users_container);
            container.setVisibility(View.GONE);
        }

    }

    @OnClick(R.id.login_wangjimima)
    public void forgetPasswordClick(View v) {
        Intent intent = new Intent();
        intent.setClass(LoginFrame.this, ForgetPasswordFrame.class);
        intent.putExtra("class", LoginFrame.class.toString());
        LoginFrame.this.startActivity(intent);
    }

    private boolean checkIsSaved(List<LoginUser> list, User user) {

        boolean saved = false;
        Log.e("数据列表", (list == null) + "");
        if (null == list) {
            str = "添加";
            return saved;
        }
        for (int i = 0; i < list.size(); i++) {
            Log.e("数据库", list.get(i).userNo + "!!!!!!!!" + user.getUserNo());
            if (list.get(i).userNo.equals(user.getUserNo())) {

                saved = false; //数据库里面有，更新,否则添加
                str = "更新";
                break;
            } else {
                str = "添加";
            }
        }

        return saved;
    }

    @OnClick(R.id.loginButton)
    public void loginClick(View v) {
        if (!Util.isNetworkConnected(LoginFrame.this)) {
            Util.showAlert("未检测到网络连接...", LoginFrame.this);
            return;
        }
        String lName = name.getText().toString();
        final String lpwd = pwd.getText().toString();

        if (Util.isNull(lName)) {
            Util.showAlert("请输入用户名", LoginFrame.this);
            return;
        } else if (Util.isNull(lpwd)) {
            Util.showAlert("请输入密码", LoginFrame.this);
            return;
        } else if ("".equals(lpwd.trim())) {
            Util.showAlert("空格不能作为登录密码", LoginFrame.this);
            return;
        } else {
            final String nName = lName;
        }
        dologin(lpwd, lName, "", "");
    }


    @OnClick(R.id.register)
    public void reClick(View view) {
        Intent intent = new Intent();
        intent.setClass(LoginFrame.this, RegisterFrame.class);
        intent.putExtra("className", LoginFrame.class.toString());
        LoginFrame.this.startActivity(intent);
    }

    /**
     * @param lpwd 密码
     */
    private void dologin(final String lpwd, final String nName, String type, String sessionId) {

        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "正在登录...");

        final String pwd = new MD5().getMD5ofStr(lpwd);
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
        NewWebAPI.getNewInstance().doLogin(nName, pwd, lat, lon, province, city, "", type, sessionId,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.showAlert("网络超时，请重试！", LoginFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
//                            请使用手机号加验证码登录
                            if (json.getString("message").equals("请使用手机号加验证码登录")) {
                                Toast.makeText(context, "请使用手机号加验证码登录", Toast.LENGTH_LONG).show();
                                Util.showIntent(context, phoneLoginActivity.class);
                                return;
                            }

                            final AlertDialog dialog = new AlertDialog.Builder(LoginFrame.this).create();
                            View view = LayoutInflater.from(LoginFrame.this).inflate(R.layout.login_dialog_panel,
                                    null);
                            TextView msg = view.findViewById(R.id.login_dialog_message);
                            TextView btn1 = view.findViewById(R.id.login_dialog_button1);
                            TextView btn2 = view.findViewById(R.id.login_dialog_button2);
                            msg.setText(json.getString("message"));
                            btn1.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    LoginFrame.this.pwd.setFocusable(true);
                                    LoginFrame.this.pwd.setFocusableInTouchMode(true);
                                    LoginFrame.this.pwd.requestFocus();
                                    LoginFrame.this.pwd.setText("");
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                            btn2.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent();
                                    intent.setClass(LoginFrame.this, ForgetPasswordFrame.class);
                                    intent.putExtra("class", LoginFrame.class.toString());
                                    LoginFrame.this.startActivity(intent);
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                            dialog.setTitle(null);
                            dialog.setView(view, 0, 0, 0, 0);
                            dialog.show();
                            return;
                        }
                        User user = JSON.parseObject(result.toString(), User.class);
                        user.setUserName(name.getText().toString());
                        user.setLoginDate("这是我保存的日期");
                        user.setId(Long.parseLong(user.getUserNo()));
                        user.setPwd(lpwd);
                        user.setMd5Pwd(pwd);
                        if (Util.isNull(lpwd)) {
                            Log.e("登录密码", user.getPassword());
                            user.setMd5Pwd(user.getPassword());
                        }

                        try {
                            List<LoginUser> list = dbUtils.findAll(LoginUser.class);
                            LoginUser lu = new LoginUser();
                            lu.userId = user.getNoUtf8UserId();
                            lu.pwd = lpwd;
                            lu.userFace = user.getUserFace();
                            lu.userNo = user.getUserNo();
                            lu.sessionId = json.getString("sessionId");
                            Log.e("是否储存", rememberPsw.isChecked() + "");
                            if (rememberPsw.isChecked()) {
                                lu.setIssave("0");
                            } else {
                                lu.setIssave("1");
                            }

                            boolean checked = checkIsSaved(list, user);//返回为真数据库不包含
                            Log.e("添加还是更新", str + "");
                            if (str.equals("添加")) {
//									if (true) {
                                dbUtils.saveBindingId(lu);
                            } else if (str.equals("更新")) {
                                dbUtils.update(lu, "sessionId", "pwd", "userFace", "userId", "userNo", "issave");
                            } else {

                            }

                        } catch (DbException e) {
                            Log.e("信息储存异常", e.toString());
                            e.printStackTrace();
                        }
                        UserData.setUser(user);
                        try {
                            dbUtils.deleteAll(User.class);
                            try {
                                dbUtils.save(user);
                            } catch (DbException e) {
                                dbUtils.dropTable(User.class);
                                dbUtils.save(user);
                                e.printStackTrace();
                            }
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        LoginUserInfo lui = new LoginUserInfo();
                        lui.setUserId(nName);
                        lui.setPwd(lpwd);
                        Log.e("是否储存", rememberPsw.isChecked() + "");
                        if (rememberPsw.isChecked()) {
                            lui.setIssave("0");
                        }
                        try {
                            dbUtils.save(lui);
                        } catch (DbException e) {
                            e.printStackTrace();
                        }
                        LogUtils.e("登录返回的SessionId" + user.getSessionId());
                        LoginFrame.this.getApplicationContext()
                                .startService(new Intent(LoginFrame.this, MessageService.class));
                        if ("home".equals(LoginFrame.this.getIntent().getStringExtra("home"))) {
                            Util.showIntent(LoginFrame.this, Lin_MainFrame.class, new String[]{"toTab"},
                                    new String[]{"home"});
                        } else if ("huodong".equals(LoginFrame.this.getIntent().getStringExtra("huodong"))) {
                            Util.showIntent(LoginFrame.this, Lin_MainFrame.class, new String[]{"toTab"},
                                    new String[]{"note"});
                        } else if ("PhoneFream".equals(LoginFrame.this.getIntent().getStringExtra("PhoneFream"))) {
                            Util.showIntent(LoginFrame.this, PhoneFream.class);
                            LoginFrame.this.finish();
                        } else if ("finish".equals(LoginFrame.this.getIntent().getStringExtra("finish"))) {
                            LoginFrame.this.finish();
                        } else if ("store".equals(LoginFrame.this.getIntent().getStringExtra("finish"))) {
                            Util.showIntent(LoginFrame.this, StoreMainFrame.class, new String[]{"toTab"}, new String[]{"shopping_cart"});
                            LoginFrame.this.finish();
                        } else if (!"reg".equals(LoginFrame.this.getIntent().getStringExtra("source"))) {
                            Util.showIntent(LoginFrame.this, Lin_MainFrame.class, new String[]{"toTab"},
                                    new String[]{"usercenter"});
                        } else
                            Util.showIntent(LoginFrame.this, Lin_MainFrame.class);
                        getSharedPreferences("Checked", MODE_PRIVATE).edit()
                                .putBoolean("Checked", rememberPsw.isChecked()).commit();


                        Log.e("设置别名", "user.getUserId()" + user.getUserId());
                        JPushInterface.setAliasAndTags(getApplicationContext(),
//										 user.getUserId(),
                                user.getUserId(),
                                null,
                                mAliasCallback);
                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });
    }


    private String TAG = "Login";

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
        }
    };

    private void initLoginUsersContainer(final List<LoginUser> list, final LinearLayout container) {
        container.removeAllViews();
        for (int i = 0; i < list.size(); i++) {

            final LoginUser user = list.get(i);
            Log.e("是否显示密码", (user.getIssave() == null) + "");

            LinearLayout layout = (LinearLayout) getLayoutInflater().inflate(R.layout.login_user_item, null);
            final ImageView logo = (ImageView) layout.findViewById(R.id.user_logo);
            TextView user_name = (TextView) layout.findViewById(R.id.user_name);
            ImageView delete = (ImageView) layout.findViewById(R.id.delete);
            if (!Util.isNull(user.userId)) {
                user_name.setText(Util.getNo_pUserId(user.userId));
            }
            if (!Util.isNull(user.userFace)) {
                bmUtils.display(logo, user.userFace, new BitmapLoadCallBack<View>() {
                    @Override
                    public void onLoadCompleted(View arg0, String arg1, Bitmap arg2, BitmapDisplayConfig arg3,
                                                BitmapLoadFrom arg4) {
                        logo.setImageBitmap(Util.getRoundedCornerBitmap(arg2));
                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                        Resources res = LoginFrame.this.getResources();
                        Bitmap bi = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_black_white);
                        logo.setImageBitmap(Util.getRoundedCornerBitmap(bi));
                    }
                });
            } else {
                Resources res = LoginFrame.this.getResources();
                Bitmap bi = BitmapFactory.decodeResource(res, R.drawable.ic_launcher_black_white);
                logo.setImageBitmap(Util.getRoundedCornerBitmap(bi));
            }
            layout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    name.setText(Util.getNo_pUserId(user.userId));
                    if (!Util.isNull(user.getIssave())) {
                        Log.e("储存的数据是", user.getIssave() + "");
                    }
                    if (!Util.isNull(user.getIssave()) && user.getIssave().equals("0")) {

                        pwd.setText(user.pwd);
                    } else {
                        pwd.setText("");
                    }
                    name.invalidate();
                    pwd.invalidate();
                    container.setVisibility(View.GONE);
                }
            });
            delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dbUtils.deleteById(LoginUser.class, user.userNo);
                        List<LoginUser> l = dbUtils.findAll(LoginUser.class);
                        if (l != null && l.size() > 0) {
                            initLoginUsersContainer(l, container);
                        } else {
                            container.setVisibility(View.GONE);
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });
            container.addView(layout);
        }
    }
}