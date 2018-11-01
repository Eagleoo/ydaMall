package com.mall.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.ThirdPartyLogin;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.Dialog_can_not_receive;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.LocationConstants;
import com.mall.util.MyCountDownTimer;
import com.mall.util.ShowMyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.messageboard.MyToast;
import com.mall.view.service.MessageService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.Serializable;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


/**
 * 手机短信验证登录
 */
public class phoneLoginActivity extends BasicActivity implements TextWatcher {


    @BindView(R.id.phone_numberline)
    public LinearLayout phone_numberline;

    @BindView(R.id.codenumberline)
    public LinearLayout codenumberline;

    @BindView(R.id.imagecodeline)
    public LinearLayout imagecodeline;

    @BindView(R.id.submitcode)
    public Button submitcode;

    @BindView(R.id.loginButton)
    public Button loginButton;

    @BindView(R.id.phone_number_et)
    public EditText phone_number;

    @BindView(R.id.imagesubmitcode)
    public ImageView imagesubmitcode;

    @BindView(R.id.imagesubmitload)
    public ProgressBar imagesubmitload;

    @BindView(R.id.imagesubmitline)
    public RelativeLayout imagesubmitline;

    @BindView(R.id.imagecode_et)
    public EditText imagecode;

    @BindView(R.id.smscod_ed)
    public EditText smscod;

    @BindView(R.id.tv1)
    public TextView tv1;

    @BindView(R.id.tv2)
    public TextView tv2;

    @BindView(R.id.tv3)
    public TextView tv3;

    @BindView(R.id.cannotyam)
    public TextView cannotyam;

    private DbUtils dbUtils;

    private DbUtils db = null;

    private String phoneend = "";

    private String imgecodend = "";


    MyCountDownTimer myCountDownTimer;

    @Override
    public int getContentViewId() {
        return R.layout.activity_phone_login;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        init();
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    private void init() {
        initDate();
        initview();
    }

    private void initDate() {
        dbUtils = DbUtils.create(this, "YDLoginUsers");
        db = DbUtils.create(context);
        try {
            db.deleteAll(ThirdPartyLogin.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    private void initview() {
        Util.initTitle(this, "手机短信验证登录", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCountDownTimer = null;
                finish();
            }
        });
        phone_numberline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());
        codenumberline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());
        imagecodeline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());

        submitcode.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());
        imagesubmitline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());

        phone_number.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("电话登录", charSequence.toString());
                if (phone_number.length() == 11 && Util.checkPhoneNumber(phone_number.getText().toString())) {
                    Util.asynTask(new IAsynTask() {
                        @Override
                        public Serializable run() {
                            Web web = new Web(Web.valiUserName, "userid=" + phone_number.getText().toString());
                            return web.getPlan();
                        }

                        @Override
                        public void updateUI(Serializable runData) {
                            if (Util.isNull(runData)) {
                                Util.show("网络异常，请稍后再试...", phoneLoginActivity.this);
                                return;
                            }
                            if ("success".equals(runData + "")) {
                                VoipDialog voipDialog = new VoipDialog("这个手机号码没有注册过，立即去注册吧。", phoneLoginActivity.this, "去注册", "取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.showIntent(context, RegisterFrame.class);
                                        finish();
                                    }
                                }, null);
                                voipDialog.show();
                                imagesubmitcode.setVisibility(View.INVISIBLE);
                            } else {
                                imagesubmitcode.setVisibility(View.VISIBLE);
                                setImagecode(phone_number.getText().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        imagecode.addTextChangedListener(this);
        smscod.addTextChangedListener(this);

        tv1.setText(Util.justifyString("+86 ", 7));
        tv2.setText(Util.justifyString("图形验证码", 5));
        tv3.setText(Util.justifyString("验证码", 5));
    }

    private void setImagecode(String phonenumberstr) {
        imagesubmitcode.setImageBitmap(null);
        Picasso.with(context).load("http://" + Web.webImage
                + "/ashx/ImgCode.ashx?action=imgcode&phone=" + phonenumberstr)
                .skipMemoryCache().into(mTarget);
    }

    private void phonelogin() {
        String smscodend = smscod.getText().toString();
        Log.e("登录的手机号与验证码",
                "phoneend:" + phoneend + "codend:" + smscodend + "imgecodend" + imgecodend);
        if (Util.isNull(phoneend)) {
            MyToast.makeText(context, "请输入手机号码", 5).show();
            return;
        } else if (Util.isNull(imgecodend)) {
            MyToast.makeText(context, "请输入图形验证码", 5).show();
            return;
        } else if (Util.isNull(smscodend)) {
            MyToast.makeText(context, "请输入短信验证码", 5).show();
            return;
        }

        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(context, "正在登录...");
        NewWebAPI.getNewInstance().doLoginPhone(phoneend, smscodend, imgecodend,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络超时，请重试！", context);
                            return;
                        }
                        // LogUtils.e(result.toString());
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            if (myCountDownTimer != null) {
                                myCountDownTimer.onresetting();
                                myCountDownTimer = null;
                            }
                            smscod.setText("");
                            View mContentView = LayoutInflater.from(context).inflate(R.layout.d_video_audio_dialog, null);
                            TextView video_audio_dialog_content = (TextView) mContentView.findViewById(R.id.video_audio_dialog_content);
                            TextView video_audio_dialog_title = (TextView) mContentView.findViewById(R.id.video_audio_dialog_title);
                            video_audio_dialog_title.setText("温馨提醒");
                            video_audio_dialog_content.setText(json.getString("message"));
                            Button video_audio_dialog_cancel = (Button) mContentView.findViewById(R.id.video_audio_dialog_cancel);
                            video_audio_dialog_cancel.setVisibility(View.GONE);
                            Button video_audio_dialog_sure = (Button) mContentView.findViewById(R.id.video_audio_dialog_sure);
                            video_audio_dialog_sure.setText("确定");
                            final ShowMyPopWindow mPopUpWindow = new ShowMyPopWindow(mContentView, context, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, LocationConstants.CENTER, 0);
                            video_audio_dialog_sure.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mPopUpWindow.dismiss();
                                }
                            });
                            return;
                        }
                        User user = JSON.parseObject(result.toString(), User.class);
                        user.setMd5Pwd(user.getPassword());
                        UserData.setUser(user);
                        LogUtils.e("登录返回的SessionId" + user.getSessionId());
                        getApplicationContext().startService(new Intent(context, MessageService.class));
                        if (!"reg".equals(phoneLoginActivity.this.getIntent().getStringExtra("source"))) {
                            Util.showIntent(phoneLoginActivity.this, Lin_MainFrame.class, new String[]{"toTab"},
                                    new String[]{"usercenter"});
                        } else if ("finish".equals(phoneLoginActivity.this.getIntent().getStringExtra("finish"))) {
                            phoneLoginActivity.this.finish();
                        } else
                            Util.showIntent(phoneLoginActivity.this, Lin_MainFrame.class);
//
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

    private String TAG = "LoginPhone";

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {
        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(TAG, logs);
//					getTotalData(alias);
                    // 建议这里往 SharePreference 里写一个成功设置的状态。成功设置一次后，以后不必再次设置了。
                    break;
                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(TAG, logs);
                    // 延迟 60 秒来调用 Handler 设置别名
//					mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    break;
                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(TAG, logs);
            }
//			ExampleUtil.showToast(logs, getApplicationContext());
        }
    };


    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            imagesubmitcode.setImageBitmap(bitmap);
            Log.e("onBitmapLoaded", "11");
            imagesubmitload.setVisibility(View.GONE);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            Log.e("onBitmapFailed", "12");
            imagesubmitload.setVisibility(View.GONE);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

            Log.e("onPrepareLoad", "123");
            imagesubmitload.setVisibility(View.VISIBLE);
        }
    };


    @OnClick({R.id.submitcode, R.id.loginButton, R.id.cannotyam})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.loginButton:
                phonelogin();
                break;
            case R.id.cannotyam:
                int width = getWindowManager().getDefaultDisplay().getWidth() * 8 / 10;
                int height = width / 3 * 5;
                Dialog_can_not_receive dialog = new Dialog_can_not_receive(phoneLoginActivity.this, R.style.dialog, width, height);
                dialog.show();
                break;
            case R.id.submitcode:
                if (myCountDownTimer == null) {
                    myCountDownTimer = new MyCountDownTimer(submitcode, 60000, 1000);
                }
                myCountDownTimer.start();
                final String phonenumberstr = phone_number.getText().toString();
                final String imagecodestr = imagecode.getText().toString();

                if (Util.isNull(phonenumberstr)) {
                    Util.show("手机号不能为空！", context);
                    return;
                }

                if (Util.isNull(imagecodestr)) {
                    Util.show("验证码不能为空！", context);
                    return;
                }

                if (!Util.isPhone(phonenumberstr)) {
                    Util.show("您的手机号格式错误！", context);
                    return;
                }
                Util.asynTask(this, "正在发送验证码...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        Log.e("发送验证码", "runData" + runData);
                        if (null == runData) {
                            Util.show("网络错误，请重试！", context);
                            if (myCountDownTimer != null) {
                                myCountDownTimer.onresetting();
                                myCountDownTimer = null;
                                imagecode.setText("");
                                setImagecode(phonenumberstr);
                            }
                            return;
                        }
                        if ((runData + "").equals("图形验证码不正确")) {
                            MyToast.makeText(context, "图形验证码不正确", 5);
                            if (myCountDownTimer != null) {
                                myCountDownTimer.onresetting();
                                myCountDownTimer = null;
                                imagecode.setText("");
                                setImagecode(phonenumberstr);
                            }
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            phoneend = phonenumberstr;
                            imgecodend = imagecode.getText().toString();
                        } else {

                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.sendRandomCodeForNoMd5Pwd,
                                "phone=" + phonenumberstr + "&imgcode=" + imagecodestr);


                        return web.getPlan();
                    }
                });
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (phone_number.length() == 11 && imagecode.length() == 4 && smscod.length() >= 4) {
            loginButton.setEnabled(true);
        } else
            loginButton.setEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
