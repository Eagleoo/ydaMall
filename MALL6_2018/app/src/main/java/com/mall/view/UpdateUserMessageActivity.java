package com.mall.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.model.Zone;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.CheckPAndI;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.LocationConstants;
import com.mall.util.ShowMyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateUserMessageActivity extends Activity {


    @ViewInject(R.id.idcardtv)
    private TextView idcardtv;
    @ViewInject(R.id.inviterID)
    private EditText inviterID;
    @ViewInject(R.id.phone)
    private EditText phone;
    @ViewInject(R.id.yzmpic_code)
    private EditText yzmpic_code;
    @ViewInject(R.id.phone_code)
    private EditText phone_code;
    @ViewInject(R.id.rname)
    private EditText rname;
    @ViewInject(R.id.ids)
    private EditText ids;
    @ViewInject(R.id.message)
    private TextView message;
    @ViewInject(R.id.address)
    private TextView address;
    @ViewInject(R.id.send_code)
    private Button send_code;
    @ViewInject(R.id.submit)
    private Button submit;
    @ViewInject(R.id.yzmpic_view)
    private ImageView yzmpic_view;
    @ViewInject(R.id.sex_man)
    private RadioButton sex_man;
    @ViewInject(R.id.sex_woman)
    private RadioButton sex_woman;
    @ViewInject(R.id.tagtv)
    private TextView tagtv;
    @ViewInject(R.id.txLinearLayout)
    private LinearLayout txLinearLayout;
    @ViewInject(R.id.dxLinearLayout)
    private LinearLayout dxLinearLayout;
    @ViewInject(R.id.phoneLinearLayout)
    private LinearLayout phoneLinearLayout;
    @ViewInject(R.id.idLinearLayout)
    private LinearLayout idLinearLayout;

    @ViewInject(R.id.adminSp)
    private TextView adminSp;

    @ViewInject(R.id.isoverseas)
    private TextView isoverseas;

    @ViewInject(R.id.overareline)
    private View overareline;

    private PositionService locationService;
    private User user;
    private SmsObserver smsObserver;
    private int _5dp = 5;
    private int _10dp = 10;
    private int s = 60;
    private String area = "";
    private int selZoneId = -1;
    private List<Zone> sheng = Data.getShen();
    private boolean hasPhone = true;

    private static final String[] adminStr = {"中国大陆", "中国香港", "中国澳门", "中国台湾"
    };

    String adminSpStr = "中国大陆";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_message);
        ViewUtils.inject(this);

        initadminArea();

        user = UserData.getUser();
        if (user == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        smsObserver = new SmsObserver(this, smsHandler);
        getContentResolver().registerContentObserver(SMS_INBOX, true, smsObserver);


        if (Util.isNull(user.getZone())) {
            Intent intent = new Intent();
            intent.setAction(PositionService.TAG);
            intent.setPackage(getPackageName());
            getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
        }


        Util.initTitle(this, "完善个人信息", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Spanned html1 = Html.fromHtml("<font size=\"3\" color=\"#5E5E5E\">保密承诺：" + "</font>"
                + "你的所有资料仅限参与互联网创业服务及相关工作，远大云商承诺对你的资料进行高度保密，绝不向任何第三方提供");
        tagtv.setText(html1);

        submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#49afef"))
                .setPressedBgColor(Color.parseColor("#2596DC"))
                .setStrokeWidth(Util.dpToPx(UpdateUserMessageActivity.this, 1))
                .setCornerRadius(Util.dpToPx(UpdateUserMessageActivity.this, 25))
                .setDefaultStrokeColor(Color.parseColor("#2596DC"))
                .create());
        if (!Util.isNull(UserData.getUser().getZoneId()) &&

                !UserData.getUser().getZoneId().equals("0") &&

                (!Util.isNull(UserData.getUser().getIdNo()) || !Util.isNull(UserData.getUser().getPassport())) &&

                !UserData.getUser().getIdNo().equals("0") &&
                !Util.isNull(UserData.getUser().getMobilePhone())
                ) {
            submit.setVisibility(View.GONE);
            sex_man.setEnabled(false);
            sex_woman.setEnabled(false);
        }
        _5dp = Util.dpToPx(this, 5F);
        _10dp = Util.dpToPx(this, 10F);
        bindHavedInfo();
    }

    private void initadminArea() {
        isoverseas.setText(Html.fromHtml("<font color=\"#ff0000\">【温馨提示】:"
                + "</font>证件包括港澳居民身份证，港澳台同胞回乡证，台胞证。证件号码不可更改，请务必仔细核对，切勿填错。"));
        final List<String> strings = Arrays.asList(adminStr);
        // 将可选内容与ArrayAdapter连接起来
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        overareline.measure(w, h);
        final int width = overareline.getMeasuredWidth();
        adminSp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View pview = getLayoutInflater().inflate(R.layout.iteamlist,
                        null, false);
                final PopupWindow fixedPopupWindow = new PopupWindow(pview,
                        width,
                        android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
                fixedPopupWindow.setOutsideTouchable(true);
                fixedPopupWindow.setBackgroundDrawable(new BitmapDrawable());
                ListView listView = pview.findViewById(R.id.rootlist);
                listView.setBackground(SelectorFactory.newShapeSelector()
                        .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                        .setStrokeWidth(Util.dpToPx(UpdateUserMessageActivity.this, 1))
                        .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                        .create());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                        getApplicationContext(), R.layout.spinner_stytle,
                        strings);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        fixedPopupWindow.dismiss();
                        isoverseas.setVisibility(View.VISIBLE);
                        tagtv.setVisibility(View.GONE);
                        String str = strings.get(i);
                        adminSp.setText(str);
                        idcardtv.setText("证件号");
                        if (str.equals("中国台湾")) {
                            shengId = "3620";
                        } else if (str.equals("中国香港")) {
                            shengId = "3619";

                        } else if (str.equals("中国澳门")) {
                            shengId = "3618";

                        } else {
                            isoverseas.setVisibility(View.GONE);
                            tagtv.setVisibility(View.VISIBLE);
                            shengId = "";
                            address.setText("");
                            idcardtv.setText("身份证号");
                        }
                        adminSpStr = str;

                        if (adminSpStr.equals("中国香港") || adminSpStr.equals("中国澳门") || adminSpStr.equals("中国台湾")) {
                            address.setText(adminSpStr);
                            showShi();
                        }
                    }
                });

//                fixedPopupWindow.showAtLocation();

                if (Build.VERSION.SDK_INT < 24) {
                    fixedPopupWindow.showAsDropDown(overareline, 0, 0);
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    fixedPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y + view.getHeight());
                }

            }
        });


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationService != null) {
            locationService.stopLocation();
            getApplicationContext().unbindService(locationServiceConnection);
        }
    }

    private void bindHavedInfo() {
        if (Util.isNull(user.getMobilePhone())) {
            phoneLinearLayout.setVisibility(View.VISIBLE);
            hasPhone = false;
            txLinearLayout.setVisibility(View.VISIBLE);
            dxLinearLayout.setVisibility(View.VISIBLE);
        } else {
            phone.setText(user.getMobilePhone());
            phone.setEnabled(false);
            hasPhone = true;
            phoneLinearLayout.setVisibility(View.GONE);
        }
        if (user.getLevelId().equals("4") || user.getLevelId().equals("6")) {
            phoneLinearLayout.setVisibility(View.GONE);
            idLinearLayout.setVisibility(View.GONE);
        }
        if (!Util.isNull(user.getZone())) {
            address.setText(user.getZone());
            address.setClickable(false);
            adminSp.setClickable(false);
            Log.e("定位", "address.getText().toString()" + address.getText().toString());
            if (address.getText().toString().contains("香港")) {
                Log.e("定位1", "address.getText().toString()" + address.getText().toString());
                adminSp.setText("中国香港");
            } else if (address.getText().toString().contains("澳门")) {
                Log.e("定位2", "address.getText().toString()" + address.getText().toString());
                adminSp.setText("中国澳门");
            } else if (address.getText().toString().contains("台湾")) {
                Log.e("定位3", "address.getText().toString()" + address.getText().toString());
                adminSp.setText("中国台湾");
            } else {
                Log.e("定位4", "address.getText().toString()" + address.getText().toString());
                adminSp.setText("中国大陆");
            }
        }
        if (!Util.isNull(user.getInviter())) {
            inviterID.setText(Util.getNo_pUserId(user.getInviter()));
            inviterID.setEnabled(false);
            tjrFocus(inviterID, false);
        }
        if (!Util.isNull(user.getSex())) {
            if ("男".equals(user.getSex())) {
                sex_man.setChecked(true);
            } else {
                sex_woman.setChecked(true);
            }
        }
        if (!Util.isNull(user.getIdNo())) {
            ids.setText(user.getIdNo());
            ids.setEnabled(false);
        }
        if (!Util.isNull(user.getPassport())) {
            ids.setText(user.getPassport());
            ids.setEnabled(false);
            idcardtv.setText("证件号");
        }
        if (!Util.isNull(user.getName())) {
            rname.setText(user.getName());
            rname.setEnabled(false);
        }
    }

    @OnFocusChange({R.id.inviterID, R.id.yzmpic_code})
    public void tjrFocus(View v, boolean hasFocus) {
        if (v.getId() == R.id.inviterID) {
            if (!hasFocus && !Util.isNull(inviterID.getText().toString())) {
                Util.asynTask(this, "正在获取推荐人信息...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null != runData) {
                            InviterInfo info = (InviterInfo) runData;
                            if (Util.getInt(info.getLevel()) >= 2 || Util.getInt(info.getShopType()) >= 3
                                    || "远大商城".equals(info.getUserid())) {
                                String name = info.getName();
                                if (!Util.isNull(name))
                                    name = name.substring(0, 1) + "**";
                                String phone = info.getPhone();
                                if (!Util.isNull(phone))
                                    phone = phone.substring(0, 3) + "****"
                                            + phone.substring(phone.length() - 4, phone.length());
                                message.setText("姓名：" + name + "\t\t\t手机：" + phone);
                            } else {
                                inviterID.setEnabled(true);
                                message.setText("该会员不能作为分享ID。");
                            }
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.getInviter, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&uid=" + Util.get(inviterID.getText().toString()));
                        return web.getObject(InviterInfo.class);
                    }
                });
            } else {
                message.setTextColor(Color.parseColor("#535353"));
                message.setText("请正确填写ID，如不知分享ID请致电 400-666-3838");
            }
        } else if (v.getId() == R.id.yzmpic_code) {
            if (hasFocus) {
                if (Util.checkPhoneNumber(phone.getText().toString())) {
                    validata_phone();
                } else {
                    Util.show("请输入正确手机号获取图形验证码", this);
                }
            }
        }
    }

    private void setImageOk() {
        Resources res = UpdateUserMessageActivity.this.getResources();
        Drawable success = res.getDrawable(R.drawable.right);
        success.setBounds(0, 0, success.getMinimumWidth(), success.getMinimumHeight());
        phone.setCompoundDrawables(null, null, success, null);
        phone.setPadding(0, 0, _10dp, 0);
    }

    private void setImageError() {
        Resources res = UpdateUserMessageActivity.this.getResources();
        Drawable error = res.getDrawable(R.drawable.error);
        error.setBounds(0, 0, error.getMinimumWidth(), error.getMinimumHeight());
        phone.setCompoundDrawables(null, null, error, null);
        phone.setPadding(0, 0, _10dp, 0);
    }

    @OnClick({R.id.send_code, R.id.submit})
    public void Click(View view) {
        switch (view.getId()) {
            case R.id.send_code:
                if (Util.isNull(phone.getText().toString())) {
                    Util.show("请输入您的手机号！", this);
                    return;
                }
                if (!Util.isPhone(phone.getText().toString())) {
                    Util.show("您的手机号格式错误！", this);
                    return;
                }
                Util.asynTask(this, "正在发送验证码...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null == runData) {
                            Util.show("网络错误，请重试！", UpdateUserMessageActivity.this);
                            phone.setCompoundDrawables(null, null, null, null);
                            return;
                        }
                        if ((runData + "").equals("图形验证码不正确")) {
                            txLinearLayout.setVisibility(View.VISIBLE);
                            Picasso.with(UpdateUserMessageActivity.this).load("http://" + Web.webImage + "/ashx/ImgCode.ashx?action=imgcode&phone=" + phone.getText().toString()).skipMemoryCache()
                                    .into(yzmpic_view);
                            phone_code.setText("");
                            Util.show(runData + "", UpdateUserMessageActivity.this);
                            return;
                        }
                        if ((runData + "").startsWith("success:")) {
                            Resources res = UpdateUserMessageActivity.this.getResources();
                            Drawable ok = res.getDrawable(R.drawable.registe_success);
                            ok.setBounds(0, 0, ok.getMinimumWidth(), ok.getMinimumHeight());
                            phone.setCompoundDrawables(null, null, ok, null);
                            new TimeThread().start();
                        } else {
                            Util.show(runData + "", UpdateUserMessageActivity.this);
                            phone.setCompoundDrawables(null, null, null, null);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.sendPhoneValidataCode,
                                "userId=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&phone="
                                        + phone.getText().toString() + "&imgcode=" + yzmpic_code.getText().toString());
                        return web.getPlan();
                    }
                });
                break;
            case R.id.submit:
                view.setEnabled(false);
                if (inputStringcheck()) {
                    final CustomProgressDialog dialog = Util.showProgress("正在验证分享ID...", this);
                    Util.asynTask(new IAsynTask() {
                        @Override
                        public void updateUI(Serializable runData) {
                            InviterInfo inviter = (InviterInfo) runData;
                            dialog.cancel();
                            dialog.dismiss();
                            if (null == inviter || Util.isNull(inviter.getUserid())) {
                                Util.show("您输入的分享ID不存在！", UpdateUserMessageActivity.this);
                                return;
                            }
                            if (!"远大商城".equals(inviter.getUserid()) && Integer.parseInt(inviter.getLevel()) == 1) {
                                Util.show("您输入的分享ID无效！", UpdateUserMessageActivity.this);
                                return;
                            }
                            update();
                        }

                        @Override
                        public Serializable run() {
                            Web web = new Web(Web.getInviter, "userId=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd()
                                    + "&uid=" + Util.get(inviterID.getText().toString()));
                            return web.getObject(InviterInfo.class);
                        }
                    });
                }
                view.setEnabled(true);
                break;
        }
    }

    private Boolean inputStringcheck() {
        Log.e("levelid", user.getLevelId());
        int levelid = Integer.parseInt(user.getLevelId());
        if (Util.isNull(address.getText().toString())) {
            Util.show("请选择您的地址！", this);
            return false;
        }

        if (Util.isNull(inviterID.getText().toString())) {
            Util.show("请输入您的分享ID！", this);
            return false;
        }
        if (!hasPhone) {
            if (levelid == 4 || levelid == 6) {

            } else {
                if (Util.isNull(phone.getText().toString())) {
                    Util.show("手机号不能为空！", this);
                    return false;
                }
                if (!Util.isPhone(phone.getText().toString())) {
                    Util.show("手机号码验证错误！", this);
                    return false;
                }
                if (Util.isNull(phone_code.getText().toString())) {
                    Util.show("请输入你的验证码！", this);
                    return false;
                }
            }
        }
        if (rname.getText().toString() == null
                || rname.getText().toString().equals("")) {
            Util.show("请输入真实姓名", this);
            return false;
        }
        if (Util.checkChineseNumber(rname.getText().toString()) < 2) {
            Util.show("真实姓名至少包含两个汉字", this);
            return false;
        }
        if (levelid == 4 || levelid == 6) {

        } else {
            if (ids.getText().toString() == null
                    || ids.getText().toString().equals("")) {
                Util.show("请输入身份证号", this);
                return false;
            }
            if (TextUtils.isEmpty(UserData.getUser().getIdNo())) {
                String idcar = ids.getText().toString().trim().toLowerCase();
                String str = adminSp.getText().toString();
                if (str.equals("中国香港") || str.equals("中国澳门") || str.equals("中国台湾")) {
                    return true;

                } else {
                    if (!TextUtils.isEmpty(idcar)
                            && !CheckPAndI.isIdentityNo(
                            idcar, UpdateUserMessageActivity.this)) {
                        Toast.makeText(this, "请输入正确身份证格式", Toast.LENGTH_LONG).show();
                        return false;
                    }
                }

            }
        }
        return true;
    }

    private void validata_phone() {
        final CustomProgressDialog dialog = Util.showProgress("正在验证手机号是否重复...", this);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                dialog.cancel();
                dialog.dismiss();
                if (null == runData) {
                    Util.show("网络错误，请重试！", UpdateUserMessageActivity.this);
                    return;
                }
                if ("success".equals(runData + "")) {
                    setImageOk();
                    Picasso.with(UpdateUserMessageActivity.this).load("http://" + Web.webImage
                            + "/ashx/ImgCode.ashx?action=imgcode&phone=" + phone.getText().toString())
                            .skipMemoryCache().into(yzmpic_view);
                    yzmpic_code.setText("");
                    return;
                } else {
                    Util.show(runData + "", UpdateUserMessageActivity.this);
                    setImageError();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.validataPhoneExiste,
                        "phone=" + Util.get(phone.getText().toString()) + "&userid=" + UserData.getUser().getUserId());
                return web.getPlan();
            }
        });
    }

    private void update() {
        String sex_text = sex_man.isChecked() ? "男" : "女";

        String overseas = (adminSp.getText().toString().equals("中国香港") || adminSp.getText().toString().equals("中国澳门") || adminSp.getText().toString().equals("中国台湾")) ? "1" : "0";
        Log.e("是否海外", "overseas" + overseas);
        NewWebAPI.getNewInstance().updateUserInfo(user.getUserId(), user.getMd5Pwd(), area, selZoneId + "",
                phone.getText().toString(), phone_code.getText().toString(), inviterID.getText().toString(), rname.getText().toString(), ids.getText().toString(), Util.get(sex_text),
                overseas, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (null == result) {
                            Util.show("网络异常，请重试！", UpdateUserMessageActivity.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        String message = json.getString("message");
                        Util.show(message, UpdateUserMessageActivity.this);
                        if (200 == code) {
                            Util.show("完善资料成功！", UpdateUserMessageActivity.this);
                            Web.reDoLogin();
                            UpdateUserMessageActivity.this.finish();
                            Intent intent = new Intent(UpdateUserMessageActivity.this,
                                    Lin_MainFrame.class);
                            intent.putExtra("toTab", "usercenter");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                    }
                });
    }

    private TextView backSheng;
    private TextView backShi;
    private String shengId = "";
    private String shiId = "";

    @OnClick(R.id.address)
    public void addressClick(View v) {
        if (adminSpStr.equals("中国香港") || adminSpStr.equals("中国澳门") || adminSpStr.equals("中国台湾")) {
            address.setText(adminSpStr);
            showShi();
            return;
        }


//        final AlertDialog builder = new AlertDialog.Builder(this).create();
        View root = LayoutInflater.from(this).inflate(R.layout.product_detail_youfei, null);
        int with = (int) (Util.getScreenSize(this).getWidth() * 0.75);
        int hight = (int) (Util.getScreenSize(this).getHeight() * 0.75);

        final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(root, this, with, hight, LocationConstants.CENTER, 0);
        LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
        TextView titile = (TextView) root.findViewById(R.id.titile);
        titile.setText("请选择省份");
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
        TextView s = null;
        for (Zone z : sheng) {
            if (z.getName().contains("澳门") || z.getName().contains("香港") || z.getName().contains("台湾")) {
                continue;
            }
            s = getTextView(z.getName(), z.getId());
            s.setLayoutParams(ll);
            line.addView(s);
            s.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != backSheng)
                        backSheng.setTextColor(Color.parseColor("#535353"));
                    backSheng = (TextView) v;
                    address.setText(((TextView) v).getText());
                    ((TextView) v).setTextColor(Color.parseColor("#ff0000"));
                    shengId = v.getTag() + "";
                    area = "";
                    showShi();
                    showMyPopWindow.dismiss();

//                    builder.cancel();
//                    builder.dismiss();
                }
            });
        }
//        builder.setView(root);
//        builder.show();
    }

    private TextView getTextView(String text, String tag) {
        TextView s = new TextView(this);
        s.setText(text);
        s.setTag(tag);
        s.setTextColor(Color.parseColor("#535353"));
        s.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.liner_border));
        s.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        s.setSingleLine(true);
        s.setPadding(_5dp, _5dp / 2, _5dp, _5dp / 2);
        return s;
    }

    private void showShi() {
        Util.asynTask(this, "正在获取城市数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<Zone>> map = (HashMap<String, List<Zone>>) runData;
                List<Zone> shi = map.get("list");
                View root = LayoutInflater.from(UpdateUserMessageActivity.this).inflate(R.layout.product_detail_youfei,
                        null);
                int with = (int) (Util.getScreenSize(UpdateUserMessageActivity.this).getWidth() * 0.75);
                int hight = (int) (Util.getScreenSize(UpdateUserMessageActivity.this).getHeight() * 0.75);
                final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(root, UpdateUserMessageActivity.this, with, hight, LocationConstants.CENTER, 0);
//                final AlertDialog builder = new AlertDialog.Builder(UpdateUserMessageActivity.this).create();


                LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
                TextView textView = (TextView) root.findViewById(R.id.titile);
                textView.setText("请选择城市");
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
                TextView s = null;
                for (Zone z : shi) {
                    s = getTextView(z.getName(), z.getId());
                    s.setLayoutParams(ll);
                    line.addView(s);
                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != backShi)
                                backShi.setTextColor(Color.parseColor("#535353"));
                            backShi = (TextView) v;
                            address.setText(address.getText() + "-" + ((TextView) v).getText());
                            ((TextView) v).setTextColor(Color.parseColor("#ff0000"));
                            shiId = v.getTag() + "";
                            showQu();
                            showMyPopWindow.dismiss();
                        }
                    });
                }

            }

            @Override
            public Serializable run() {
                HashMap<String, List<Zone>> map = new HashMap<String, List<Zone>>();
                map.put("list", Data.getShi(shengId));
                return map;
            }
        });
    }

    private void showQu() {
        Util.asynTask(this, "正在获取区/县数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<Zone>> map = (HashMap<String, List<Zone>>) runData;
                List<Zone> qu = map.get("list");
                View root = LayoutInflater.from(UpdateUserMessageActivity.this).inflate(R.layout.product_detail_youfei,
                        null);
                int with = (int) (Util.getScreenSize(UpdateUserMessageActivity.this).getWidth() * 0.75);
                int hight = (int) (Util.getScreenSize(UpdateUserMessageActivity.this).getHeight() * 0.75);
                final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(root, UpdateUserMessageActivity.this, with, hight, LocationConstants.CENTER, 0);
//                final AlertDialog builder = new AlertDialog.Builder(UpdateUserMessageActivity.this).create();


                LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
                TextView textView = (TextView) root.findViewById(R.id.titile);
                textView.setText("请选择区/县");
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
                TextView s = null;
                for (Zone z : qu) {
                    s = getTextView(z.getName(), z.getId());
                    s.setLayoutParams(ll);
                    line.addView(s);
                    s.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != backShi)
                                backShi.setTextColor(Color.parseColor("#535353"));
                            backShi = (TextView) v;
                            address.setText(address.getText() + "-" + ((TextView) v).getText());
                            address.setTag(-7, v.getTag() + "");
                            selZoneId = Util.getInt(address.getTag(-7));
                            ((TextView) v).setTextColor(Color.parseColor("#ff0000"));

                            showMyPopWindow.dismiss();
                        }
                    });
                }

            }

            @Override
            public Serializable run() {
                HashMap<String, List<Zone>> map = new HashMap<String, List<Zone>>();
                map.put("list", Data.getQu(shiId));
                return map;
            }
        });
    }

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

    private Uri SMS_INBOX = Uri.parse("content://sms/");

    public void getSmsFromPhone() {
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"body"};// "_id", "address",
        String where = " date >  " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cur = cr.query(SMS_INBOX, projection, null, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String body = cur.getString(cur.getColumnIndex("body"));
            // 这里我是要获取自己短信服务号码中的验证码~~
            Pattern pattern = Pattern.compile(" [a-zA-Z0-9]{10}");
            Matcher matcher = pattern.matcher(body);
            phone_code.setText(patternCode(body));
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

    public Handler smsHandler = new Handler() {
        // 这里可以进行回调的操作
        // TODO
    };

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation progress) {
                    String province = progress.getProvince();
                    String city = progress.getCity();
                    area = progress.getDistrict();
                    if (!Util.isNull(province) && !Util.isNull(city) && !Util.isNull(area)) {
                        address.setText(province + "-" + city + "-" + area);
                        bindHavedInfo();
                        try {
                            isoverseas.setVisibility(View.VISIBLE);
                            tagtv.setVisibility(View.GONE);
                            idcardtv.setText("证件号");
                            if (province.contains("香港")) {
                                adminSp.setText("中国香港");
                            } else if (province.contains("澳门")) {
                                adminSp.setText("中国澳门");
                            } else if (province.contains("台湾")) {
                                adminSp.setText("中国台湾");
                            } else {
                                isoverseas.setVisibility(View.GONE);
                                tagtv.setVisibility(View.VISIBLE);
                                adminSp.setText("中国大陆");
                                idcardtv.setText("身份证号");
                            }
                        } catch (Exception e) {

                        }
                    }
                }

                @Override
                public void onError(AMapLocation error) {
                    area = "";
                    Util.show("获取当前城市失败，无法设置当前城市！", UpdateUserMessageActivity.this);
                }
            });

        }
    };
}
