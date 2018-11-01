package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.Dialog_can_not_receive;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.MyCountDownTimer;
import com.mall.util.Util;
import com.mall.view.messageboard.MyToast;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 功能： 忘记密码<br>
 * 时间： 2013-9-4<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class ForgetPasswordFrame extends Activity {
    private LinearLayout phone_numberline, imagecodeline, codenumberline;
    private RelativeLayout imagesubmitline;
    private TextView tv1, tv2, tv3;
    private Button get_yzm;
    private TextView phoneDes;
    private TextView mailDes;
    private EditText phoneOrMail;
    private Button submit;
    private String userid;

    private EditText yzm;
    private TextView cannotyam;
    private int s = 60;
    private int i = 0;
    private LinearLayout yzmpic;
    private EditText yzmpic_code;
    private ImageView yzmpic_view;
    private Context context;
    MyCountDownTimer myCountDownTimer;
    public ProgressBar imagesubmitload;

    private String phoneend = "";

    private String imgecodend = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.forget_password_frame);
        init();
    }

    private void initComponent() {
        Util.initTitle(this, "忘记登录密码", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        SpannableString ss = new SpannableString(phoneOrMail.getHint().toString());
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(12, true);
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        phoneOrMail.setHint(ss);
        get_yzm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                getYzm();
            }
        });
    }

    private void getYzm() {
        if (myCountDownTimer == null) {
            myCountDownTimer = new MyCountDownTimer(get_yzm, 60000, 1000);
        }
        myCountDownTimer.start();
        final String phonenumberstr = phoneOrMail.getText().toString();
        final String imagecodestr = yzmpic_code.getText().toString();

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


        final CustomProgressDialog cpd = Util.showProgress("正在发送验证码...", context);
        Map<String, String> map = new HashMap<String, String>();
        map.put("phone", phonenumberstr);
        map.put("userId", phonenumberstr);
        map.put("imgcode", imagecodestr);
        NewWebAPI.getNewInstance().getWebRequest("/ShortMessage.aspx?call=sendUPM", map,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        String runData = result.toString();
                        Log.e("发送验证码", "runData" + runData);
                        if (null == runData) {
                            Util.show("网络错误，请重试！", context);
                            if (myCountDownTimer != null) {
                                myCountDownTimer.onresetting();
                                myCountDownTimer = null;
                                yzmpic_code.setText("");
                                setImagecode(phonenumberstr);
                            }
                            return;
                        }
                        String code = "";
                        String message = "";
                        try {
                            org.json.JSONObject jsonObject = new org.json.JSONObject(runData.toString());
                            code = jsonObject.optString("code");
                            message = jsonObject.optString("message");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        if ((message + "").equals("图形验证码不正确")) {
                            MyToast.makeText(context, "图形验证码不正确", 5);
                            if (myCountDownTimer != null) {
                                myCountDownTimer.onresetting();
                                myCountDownTimer = null;
                                yzmpic_code.setText("");
                                setImagecode(phonenumberstr);
                            }
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            phoneend = phonenumberstr;
                            imgecodend = yzmpic_code.getText().toString();
                        } else {

                        }
                    }

                    @Override
                    public void fail(Throwable e) {
                        LogUtils.e("网络请求错误：", e);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("网络请求超时！");
                        Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                    }

                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );

    }


    private void init() {
        context = this;
        initfind();
        initview();
        initComponent();
        cannotyam.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // Util.showConnotYzm(ForgetPasswordFrame.this);
                int width = getWindowManager().getDefaultDisplay().getWidth() / 10 * 8;
                int height = width / 3 * 5;
                Dialog_can_not_receive dialog = new Dialog_can_not_receive(ForgetPasswordFrame.this, R.style.dialog,
                        width, height);
                dialog.show();

            }
        });

        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String pm = phoneOrMail.getText().toString();
//				RadioGroup group = (RadioGroup) ForgetPasswordFrame.this.findViewById(R.id.forget_useTypeGroup111);
//				if (Util.isNull(name)) {
//					Util.show("请输入您要找回的帐号！", ForgetPasswordFrame.this);
//				} else
                if (Util.isNull(pm))
                    Util.show("请输入您找回帐号的手机！", ForgetPasswordFrame.this);
                else {
                    if (yzm.getText().length() < 4) {
                        Util.show("请输入合适的验证码", ForgetPasswordFrame.this);
                        return;
                    }
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("userId", phoneOrMail.getText().toString());
                    map.put("phone", phoneOrMail.getText().toString());
                    map.put("code", yzm.getText().toString());

                    final CustomProgressDialog cpd = Util.showProgress("正在校验...", context);
                    NewWebAPI.getNewInstance().getWebRequest("/ShortMessage.aspx?call=validataSMSCode", map,
                            new WebRequestCallBack() {

                                @Override
                                public void success(Object result) {
                                    super.success(result);
                                    if (Util.isNull(result)) {
                                        Util.show("网络错误，请重试！", ForgetPasswordFrame.this);
                                        return;
                                    }
                                    JSONObject json = JSON.parseObject(result.toString());
                                    String message = json.getString("message");
                                    if (message.contains("成功")) {
                                        Util.show(message, ForgetPasswordFrame.this);
                                        Intent intent = new Intent(ForgetPasswordFrame.this, ChangPwdFrame.class);
//										intent.putExtra("userId", userName.getText().toString());
                                        intent.putExtra("userId", phoneOrMail.getText().toString());
                                        intent.putExtra("phone", phoneOrMail.getText().toString());
                                        intent.putExtra("yzm", yzm.getText().toString());
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Util.show(message, ForgetPasswordFrame.this);
                                        return;
                                    }

                                }

                                @Override
                                public void fail(Throwable e) {
                                    LogUtils.e("网络请求错误：", e);
                                }

                                @Override
                                public void timeout() {
                                    LogUtils.e("网络请求超时！");
                                    Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                                }

                                public void requestEnd() {
                                    cpd.cancel();
                                    cpd.dismiss();
                                }

                            });

                }
            }
        });


    }

    private void initview() {
        phoneOrMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("输入监听", s.toString());
                if (phoneOrMail.length() == 11 && Util.checkPhoneNumber(phoneOrMail.getText().toString())) {
                    Util.asynTask(context, "正在校验手机号码...", new IAsynTask() {
                        @Override
                        public Serializable run() {
                            Web web = new Web(Web.valiUserName, "userid=" + phoneOrMail.getText().toString());
                            return web.getPlan();
                        }

                        @Override
                        public void updateUI(Serializable runData) {
                            if (Util.isNull(runData)) {
                                Util.show("网络异常，请稍后再试...", context);
                                return;
                            }
                            Log.e("验证结果", "runData" + runData.toString());
                            if ("success".equals(runData + "")) {
                                VoipDialog voipDialog = new VoipDialog("这个手机号码没有注册过，立即去注册吧。", context, "去注册", "取消", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Util.showIntent(context, RegisterFrame.class);
                                        finish();
                                    }
                                }, null);
                                voipDialog.show();
                                yzmpic_view.setVisibility(View.INVISIBLE);
                            } else {
                                yzmpic_view.setVisibility(View.VISIBLE);
                                setImagecode(phoneOrMail.getText().toString());
                            }
                        }
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

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

        imagesubmitline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());

        get_yzm.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#F3F3F3"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());
        submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#49afef"))
                .setPressedBgColor(Color.parseColor("#8449afef"))
                .setCornerRadius(Util.dpToPx(context, 22))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());

        tv1.setText(Util.justifyString("+86 ", 7));
        tv2.setText(Util.justifyString("图形验证码", 5));
        tv3.setText(Util.justifyString("验证码", 5));

    }


    private Target mTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            yzmpic_view.setImageBitmap(bitmap);
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

    private void setImagecode(String phonenumberstr) {
        yzmpic_view.setImageBitmap(null);
        Picasso.with(context).load("http://" + Web.webImage
                + "/ashx/ImgCode.ashx?action=imgcode&phone=" + phonenumberstr)
                .skipMemoryCache().into(mTarget);
    }

    private void initfind() {
        phone_numberline = (LinearLayout) findViewById(R.id.phone_numberline);
        imagesubmitload = (ProgressBar) findViewById(R.id.imagesubmitload);
        phoneOrMail = Util.getEditText(R.id.forget_myPhoneOrMail, this);

        imagecodeline = (LinearLayout) findViewById(R.id.imagecodeline);
        codenumberline = (LinearLayout) findViewById(R.id.codenumberline);
        imagesubmitline = (RelativeLayout) findViewById(R.id.imagesubmitline);
        get_yzm = (Button) findViewById(R.id.get_yzm);
        submit = Util.getButton(R.id.forget_submit, this);

        tv1 = (TextView) findViewById(R.id.tv1);
        tv2 = (TextView) findViewById(R.id.tv2);
        tv3 = (TextView) findViewById(R.id.tv3);


        mailDes = Util.getTextView(R.id.forget_mail, this);
        phoneDes = Util.getTextView(R.id.forget_phone, this);
        yzmpic = (LinearLayout) findViewById(R.id.yzmpic);
        yzmpic_code = (EditText) findViewById(R.id.yzmpic_code);
        yzmpic_view = (ImageView) findViewById(R.id.yzmpic_view);

        yzm = (EditText) this.findViewById(R.id.yzm_edit);
        cannotyam = (TextView) findViewById(R.id.cannotyam);
    }


}
