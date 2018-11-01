package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.Dialog_can_not_receive;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能： 忘记密码<br>
 * 时间： 2013-9-4<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class ForgetTradePasswordFrame extends Activity {
    @ViewInject(R.id.top_center)
    private TextView top_center;
    @ViewInject(R.id.top_left)
    private TextView top_left;

    @ViewInject(R.id.tv_yzm)
    private TextView tv_yzm;
    @ViewInject(R.id.et_yzm)
    private EditText et_yzm;
    @ViewInject(R.id.et_phone)
    private TextView et_phone;
    @ViewInject(R.id.yzmpic)
    private LinearLayout yzmpic;
    @ViewInject(R.id.yzmpic_code)
    private EditText yzmpic_code;
    @ViewInject(R.id.yzmpic_view)
    private ImageView yzmpic_view;

    private String yzmS = "";
    private String phone;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forget_trade_password_frame);
        ViewUtils.inject(this);
        setView();
    }

    private void setView() {
        user = UserData.getUser();
        top_center.setText("忘记交易密码");
        top_left.setVisibility(View.VISIBLE);

        et_phone.setText(user.getMobilePhone());
        phone = et_phone.getText().toString().trim();
    }

    @OnClick({R.id.tv_yzm})
    public void yzmClick(View v) {

        getYzm();
    }

    @OnClick({R.id.cannotyam})
    public void cannotyamClick(View v) {

        // Util.showConnotYzm(ForgetTradePasswordFrame.this);
        int width = this.getWindowManager().getDefaultDisplay().getWidth() / 10 * 8;
        int height = width / 3 * 5;
        Dialog_can_not_receive dialog = new Dialog_can_not_receive(
                ForgetTradePasswordFrame.this, R.style.dialog, width, height);
        dialog.show();
    }

    @OnClick({R.id.btn_sure})
    public void click(View v) {

        final String yzm = et_yzm.getText().toString().trim();
        if (TextUtils.isEmpty(yzm)) {
            Util.show("验证码不能为空", getApplicationContext());
            return;
        }
        if (yzm.length() < 4) {
            Util.show("请输入合适的验证码", ForgetTradePasswordFrame.this);
            return;
        }
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", user.getUserId());
        map.put("phone", et_phone.getText().toString());
        map.put("code", yzm);
        NewWebAPI.getNewInstance().getWebRequest("/ShortMessage.aspx?call=validataSMSCode", map, new WebRequestCallBack() {

            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result)) {
                    Util.show("网络错误，请重试！", ForgetTradePasswordFrame.this);
                    return;
                }
                JSONObject json = JSON.parseObject(result.toString());
                String message = json.getString("message");
                if (message.contains("成功")) {
                    Intent intent = new Intent(ForgetTradePasswordFrame.this,
                            ChangTradeFrame.class);
                    intent.putExtra("code", yzm);
                    intent.putExtra("userId", user.getUserId());
                    intent.putExtra("phone", et_phone.getText().toString());
                    startActivity(intent);
                    finish();
                } else {
                    Util.show(message, ForgetTradePasswordFrame.this);
                    return;
                }

            }

        });
    }

    private void getYzm() {

        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", "" + user.getUserId());
        map.put("phone", et_phone.getText().toString());
        map.put("imgcode", yzmpic_code.getText().toString());
        //  /shortMessage.aspx?call=sendUSPM
        NewWebAPI.getNewInstance().getWebRequest(
                "/shortMessage.aspx?call=sendUSPM", map,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        JSONObject json = JSON.parseObject(result.toString());
                        if (408 == json.getIntValue("code")) {
                            yzmpic.setVisibility(View.VISIBLE);
                            Picasso.with(ForgetTradePasswordFrame.this).load("http://" + Web.webImage
                                    + "/ashx/ImgCode.ashx?action=imgcode&phone=" + et_phone.getText().toString())
                                    .skipMemoryCache().into(yzmpic_view);
                            Util.show(json.getString("message"), ForgetTradePasswordFrame.this);
                            return;
                        }
                        if (200 == json.getIntValue("code")) {
                            Util.show("验证码已经发送成功，\n请注意查收",
                                    ForgetTradePasswordFrame.this);
                            LogUtils.e("验证码：" + result.toString());
                            time = 60;
                            postText();

                            return;
                        } else if (707 == json.getIntValue("code")) {

                            Util.show(json.getString("message"),
                                    ForgetTradePasswordFrame.this);
                            return;
                        } else {
                            Util.show("网络错误，请稍等。",
                                    ForgetTradePasswordFrame.this);
                            return;
                        }

                    }

                    ;

                    @Override
                    public void requestEnd() {

                    }

                    ;
                });
    }

    int time = 60;

    private void postText() {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                time--;
                if (time > 0) {
                    tv_yzm.setText(" " + time + "秒 ");
                    tv_yzm.setEnabled(false);
                    postText();
                } else {
                    tv_yzm.setText("发送验证码");
                    tv_yzm.setEnabled(true);
                }

            }
        }, 1000);

    }

}
