package com.mall.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.lin.component.CustomProgressDialog;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.model.Zone;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CheckPAndI;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.LocationConstants;
import com.mall.util.ShowMyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**
 * 功能： 注册成功页面<br>
 */
public class RegisteSuccessFrame extends Activity implements KeyboardChangeListener.KeyBoardListener {


    @ViewInject(R.id.tagtv)
    private TextView tagtv;
    @ViewInject(R.id.idcardtv)
    private TextView idcardtv;
    @ViewInject(R.id.tjr_message)
    private TextView tjrMsg;
    @ViewInject(R.id.reg_success_sel_address)
    private TextView sel_address;
    private int selZoneId = -1;
    @ViewInject(R.id.reg_success_sel_fenxiang)
    private EditText fxText;
    @ViewInject(R.id.rname)
    private EditText rname;
    @ViewInject(R.id.ids)
    private EditText ids;
    @ViewInject(R.id.sex_man)
    private RadioButton man;

    @ViewInject(R.id.adminSp)
    private TextView adminSp;


    @ViewInject(R.id.isoverseas)
    private TextView isoverseas;

    @ViewInject(R.id.overareline)
    private View overareline;

    private static final String[] adminStr = {"中国大陆", "中国香港", "中国澳门", "中国台湾"
    };

    String adminSpStr = "中国大陆";


    private List<Zone> sheng = Data.getShen();
    private int _5dp;
    public String oneProvince = "";
    public String oneCity = "";
    public String oneArea = "";
    private Context context;

    @ViewInject(R.id.reg_success_submit)
    private Button reg_success_submit;

    private PositionService locationService;
    private KeyboardChangeListener mKeyboardChangeListener;

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
                    oneProvince = progress.getProvince();
                    oneCity = progress.getCity();
                    oneArea = progress.getDistrict();
                    if (!Util.isNull(oneProvince) && !Util.isNull(oneCity) && !Util.isNull(oneArea)) {
                        sel_address.setText(oneProvince + "-" + oneProvince + "-" + oneArea);
                        isoverseas.setVisibility(View.VISIBLE);
                        tagtv.setVisibility(View.GONE);
                        try {
                            idcardtv.setText("证件号");
                            if (oneProvince.contains("香港")) {
                                adminSp.setText("中国香港");
                            } else if (oneProvince.contains("澳门")) {
                                adminSp.setText("中国澳门");
                            } else if (oneProvince.contains("台湾")) {
                                adminSp.setText("中国台湾");
                            } else {
                                isoverseas.setVisibility(View.GONE);
                                tagtv.setVisibility(View.VISIBLE);
                                adminSp.setText("中国大陆");
                                idcardtv.setText("身份证号");
                            }
                        } catch (Exception e) {

                        }


                    } else {
                        oneProvince = oneCity = oneArea = "";
                        Util.showAlert("获取当前城市失败，无法设置当前城市,请手动选择", RegisteSuccessFrame.this);
                    }
                }

                @Override
                public void onError(AMapLocation error) {
                    oneProvince = oneCity = oneArea = "";
                    Util.showAlert("获取当前城市失败，无法设置当前城市,请手动选择", RegisteSuccessFrame.this);
                }
            });

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.registe_success_frame);
        ViewUtils.inject(this);
        initadminArea();
        mKeyboardChangeListener = new KeyboardChangeListener(this);
        mKeyboardChangeListener.setKeyBoardListener(this);
        context = this;
        reg_success_submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#49afef"))
                .setPressedBgColor(Color.parseColor("#2596DC"))
                .setStrokeWidth(Util.dpToPx(RegisteSuccessFrame.this, 1))
                .setCornerRadius(Util.dpToPx(RegisteSuccessFrame.this, 25))
                .setDefaultStrokeColor(Color.parseColor("#2596DC"))
                .create());
        tjrMsg.setText(Html.fromHtml("<html><body>请正确填写ID，如不知分享ID请致电 <font color='#66bbf2'>400-666-3838</font></body></html>"));
        Util.initTitle(this, "完善个人信息", new OnClickListener() {
            @Override
            public void onClick(View v) {
                VoipDialog voipDialog = new VoipDialog("亲！未完善资料将不会获赠50元消费券和现金大红包，确定要离开此页吗？", RegisteSuccessFrame.this, "我要完善", "稍后完善", null, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(RegisteSuccessFrame.this, Lin_MainFrame.class);
                        intent.putExtra("toTab", "usercenter");
                        RegisteSuccessFrame.this.startActivity(intent);
                    }
                });
                voipDialog.show();
            }
        });
        _5dp = Util.dpToPx(this, 5F);

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
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
                        .setStrokeWidth(Util.dpToPx(RegisteSuccessFrame.this, 1))
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
                        String str = strings.get(i);
                        adminSp.setText(str);
                        isoverseas.setVisibility(View.VISIBLE);
                        tagtv.setVisibility(View.GONE);
                        idcardtv.setText("证件号");
                        if (str.equals("中国台湾")) {
                            shengId = "3620";
                        } else if (str.equals("中国香港")) {
                            shengId = "3619";
                        } else if (str.equals("中国澳门")) {
                            shengId = "3618";
                        } else {
                            isoverseas.setVisibility(View.GONE);
                            shengId = "";
                            sel_address.setText("");
                            idcardtv.setText("身份证号");
                            isoverseas.setVisibility(View.GONE);
                            tagtv.setVisibility(View.VISIBLE);
                        }
                        adminSpStr = str;

                        if (adminSpStr.equals("中国香港") || adminSpStr.equals("中国澳门") || adminSpStr.equals("中国台湾")) {
                            sel_address.setText(adminSpStr);
                            showShi();
                        }
                    }
                });

                fixedPopupWindow.showAsDropDown(overareline, 0, 0);
//                fixedPopupWindow.showAtLocation();

                if (Build.VERSION.SDK_INT < 24) {
                    fixedPopupWindow.showAsDropDown(overareline, 0, 0);
                } else {
                    int[] location = new int[2];
                    view.getLocationOnScreen(location);
                    int x = location[0];
                    int y = location[1];
                    fixedPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, y + view.getHeight());
                }
            }
        });


    }

    private boolean isqueryUserInfo = true;
    private String fxcode = "key";

    @OnFocusChange(R.id.reg_success_sel_fenxiang)
    public void tjrFocus(View v, boolean hasFocus) {
        if (Util.isNull(fxText.getText().toString())) {
            tjrMsg.setText(Html.fromHtml("<html><body>请正确填写ID，如不知分享ID请致电 <font color='#66bbf2'>400-666-3838</font></body></html>"));
        }
        if (hasFocus) {
            isqueryUserInfo = true;
        } else {
            isqueryUserInfo = false;
        }
        if (!hasFocus && !Util.isNull(fxText.getText().toString()) && !fxcode.equals(fxText.getText().toString())) {
            fxcode = fxText.getText().toString();
            Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (null != runData) {
                        InviterInfo info = (InviterInfo) runData;
                        if (TextUtils.isEmpty(info.getUserid())) {
                            tjrMsg.setTextColor(Color.parseColor("#c6c6c6"));
                            tjrMsg.setText("对不起，该会员不存在");
                            return;
                        }
                        int levelId = Util.getInt(info.getLevel());
                        if (6 != levelId) {
                            String name = info.getName();
                            if (!Util.isNull(name) && 2 <= name.length()) {
                                name = name.substring(0, 1) + "*";
                                if (3 <= info.getName().length())
                                    name += info.getName().substring(info.getName().length() - 1);
                            }
                            String phone = info.getPhone();
                            if (!Util.isNull(phone))
                                phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
                            tjrMsg.setText("用户资料：" + name + "　" + phone);
                        } else {
                            tjrMsg.setText("该会员不能作为分享ID。");
                        }
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getInviter, "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&uid="
                            + Util.get(fxText.getText().toString()));
                    return web.getObject(InviterInfo.class);
                }
            });
        }
    }

    /**
     * 提交保存
     *
     * @param v
     */
    @OnClick({R.id.reg_success_submit})
    public void submitClick(final View v) {

        final User user = UserData.getUser();
        if (-1 == selZoneId && Util.isNull(oneProvince) && Util.isNull(oneCity) && Util.isNull(oneArea)) {
            Util.showAlert("请选择您的地址！", RegisteSuccessFrame.this);
        } else if (Util.isNull(fxText.getText().toString())) {
            Util.showAlert("请输入您的分享ID！", RegisteSuccessFrame.this);
        } else {
            String str = inputStringcheck();
            if (str.equals("1")) {
                return;
            }
            final CustomProgressDialog dialog = Util.showProgress("正在验证分享ID...", this);

            Util.asynTask(new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    InviterInfo inviter = (InviterInfo) runData;
                    dialog.cancel();
                    dialog.dismiss();
                    if (null == inviter || Util.isNull(inviter.getUserid())) {
                        Util.showAlert("您输入的分享ID不存在！", RegisteSuccessFrame.this);
                        return;
                    }
                    if (Integer.parseInt(inviter.getLevel()) == 6) {
                        Util.showAlert("运营中心不能作为分享ID！", RegisteSuccessFrame.this);
                        return;
                    }
                    validata_end(v);
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getInviter, "userId=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&uid=" + Util.get(fxText.getText().toString()));
                    return web.getObject(InviterInfo.class);
                }
            });
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            VoipDialog voipDialog = new VoipDialog("亲！未完善资料将不会获赠50元消费券和现金大红包，确定要离开此页吗？", this, "我要完善", "稍后完善", null, new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(RegisteSuccessFrame.this, Lin_MainFrame.class);
                    intent.putExtra("toTab", "usercenter");
                    RegisteSuccessFrame.this.startActivity(intent);
                }
            });
            voipDialog.show();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void validata_end(final View view) {
        String sex_text = man.isChecked() ? "男" : "女";
        final CustomProgressDialog dialog = Util.showProgress("正在完善您的资料...", RegisteSuccessFrame.this);
        User user = UserData.getUser();
        String overseas = (adminSp.getText().toString().equals("中国香港") || adminSp.getText().toString().equals("中国澳门") || adminSp.getText().toString().equals("中国台湾")) ? "1" : "0";
        Log.e("是否海外", "overseas" + overseas);
        NewWebAPI.getNewInstance().updateUserInfo(user.getUserId(), user.getMd5Pwd(), oneArea, selZoneId + "", user.getMobilePhone(),
                "", fxText.getText().toString(), rname.getText().toString(), ids.getText().toString(), Util.get(sex_text), overseas, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (null == result) {
                            Util.showAlert("网络异常，请重试！", RegisteSuccessFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        int code = json.getIntValue("code");
                        String message = json.getString("message");
                        Util.showAlert(message, RegisteSuccessFrame.this);
                        if (200 == code) {
//                            opeNewPersonRedMoney(view);
                            Web.reDoLogin();
                            Intent intent = new Intent(RegisteSuccessFrame.this, Lin_MainFrame.class);
                            intent.putExtra("toTab", "usercenter");
                            RegisteSuccessFrame.this.startActivity(intent);
                        }
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
    }

    private String inputStringcheck() {
        if (rname.getText().toString() == null
                || rname.getText().toString().equals("")) {
            Util.showAlert("请输入真实姓名", this);
            return "1";
        }
        if (Util.checkChineseNumber(rname.getText().toString()) < 2) {
            Util.showAlert("真实姓名至少包含两个汉字", this);
            return "1";
        }
        if (ids.getText().toString() == null
                || ids.getText().toString().equals("")) {
            if (adminSp.getText().toString().equals("中国大陆")) {
                Util.showAlert("请输入身份证号", this);
            } else {
                Util.showAlert("请输入证件号", this);
            }


            return "1";
        }
        if (TextUtils.isEmpty(UserData.getUser().getIdNo())) {

            String idcar = ids.getText().toString().trim().toLowerCase();

            String str = adminSp.getText().toString();
            if (str.equals("中国香港") || str.equals("中国澳门") || str.equals("中国台湾")) {
                return "0";
            } else {
                if (!TextUtils.isEmpty(idcar) && !CheckPAndI.isIdentityNo(idcar, RegisteSuccessFrame.this)) {
                    Util.showAlert("请输入正确身份证格式", this);
                    return "1";
                }
            }

        }
        return "0";
    }

    private TextView backSheng;
    private TextView backShi;
    private String shengId = "";
    private String shiId = "";
    FlakeView flakeView;

    public void opeNewPersonRedMoney(final View view) {
        final View mContentView = LayoutInflater.from(context).inflate(R.layout.randomredmoneypopupwindow, null);
        final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);
        View closepopwind = mContentView.findViewById(R.id.close_popwind);  //获取。
        ImageView item_pop = mContentView.findViewById(R.id.item_pop);
        ImageView close_popwind_iv = mContentView.findViewById(R.id.close_popwind_iv);
        final ObjectAnimator animator = ObjectAnimator.ofFloat(item_pop,
                "rotationY", 0, 360);
        closepopwind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final CustomProgressDialog cpd = Util.showProgress("正在领取您的注册红包...", context);
                NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=OP_user_reg_red_box&userId="
                                + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                        new NewWebAPIRequestCallback() {

                            @Override
                            public void timeout() {
                                Util.show("网络超时！", context);
                                return;
                            }

                            @Override
                            public void success(Object result) {

                                Log.e("开启新人红包", result.toString());

                                if (Util.isNull(result)) {
                                    Util.show("网络异常，请重试！", context);
                                    return;
                                }
                                JSONObject json = JSON.parseObject(result.toString());
                                if (200 != json.getIntValue("code")) {
                                    Util.show(json.getString("message"), context);
                                    UserData.getUser().setReg_red_box("0");
                                    return;
                                }
                                final String moneyStr = json.getString("message");
                                animator.setDuration(1000).start();
                                animator.addListener(new Animator.AnimatorListener() {
                                    @Override
                                    public void onAnimationStart(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        mPopUpWindow.dismiss();
                                        View mContentViewred = LayoutInflater.from(context).inflate(R.layout.randomredmoneyopenendpopupwindow, null);
                                        final PopupWindow mPopUpWindowred = ShowPopWindow.showShareWindow(mContentViewred, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);
                                        ImageView close_popwind_iv = mContentViewred.findViewById(R.id.close_popwind_iv);
                                        LinearLayout container = mContentViewred.findViewById(R.id.container);
                                        TextView invitefriend = mContentViewred.findViewById(R.id.invitefriend);
                                        TextView money = mContentViewred.findViewById(R.id.money);
                                        money.setText(moneyStr + "元");
                                        flakeView = new FlakeView(context, R.drawable.redpocket);
                                        container.removeAllViews();
                                        container.addView(flakeView);
                                        flakeView.resume();
                                        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                mPopUpWindowred.dismiss();
                                                flakeView.pause();

                                                switch (view.getId()) {
                                                    case R.id.reg_success_submit:
                                                        Intent intent = new Intent(RegisteSuccessFrame.this, Lin_MainFrame.class);
                                                        intent.putExtra("toTab", "usercenter");
                                                        RegisteSuccessFrame.this.startActivity(intent);
                                                        break;
                                                    default:
                                                        break;
                                                }
                                            }
                                        });
                                        invitefriend.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {

                                                if (null == UserData.getUser()) {
                                                    Util.show("您还没登录，请先登录！", context);
                                                    return;
                                                }

                                                final User user = UserData.getUser();
                                                if (user != null) {
                                                    if (2 > Util.getInt(user.getLevelId())) {
                                                        Util.show("您的会员等级不能分享会员。", context);
                                                        return;
                                                    }
                                                    if ("6".equals(user.getLevelId())) {
                                                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                                                        return;
                                                    }
                                                    final OnekeyShare oks = new OnekeyShare();
                                                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum=" + user.getUtf8UserId()
                                                            + "&shareVersion=mall";
                                                    final String title = getResources().getString(R.string.sharetitle);
                                                    oks.setTitle(title);
                                                    oks.setTitleUrl(url);
                                                    oks.setUrl(url);
                                                    oks.setImageUrl("http://app.yda360.com/phone/images/icon_mall.png?r=1");
                                                    oks.setAddress("10086");
                                                    oks.setComment("快来注册吧");
                                                    oks.setText(getResources().getString(R.string.sharemessage));
                                                    oks.setSite(title);
                                                    oks.setSilent(false);
                                                    oks.setSiteUrl(url);
                                                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                                        @Override
                                                        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                                                            if ("ShortMessage".equals(platform.getName())) {
                                                                paramsToShare.setImageUrl(null);
                                                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                                                            }
                                                        }
                                                    });
                                                    oks.show(context);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onAnimationCancel(Animator animation) {

                                    }

                                    @Override
                                    public void onAnimationRepeat(Animator animation) {

                                    }
                                });


                            }

                            @Override
                            public void requestEnd() {
                                cpd.cancel();
                                cpd.dismiss();
                            }

                            @Override
                            public void fail(Throwable e) {

                            }
                        });


            }
        });

        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();

                switch (view.getId()) {
                    case R.id.reg_success_submit:
                        Intent intent = new Intent(RegisteSuccessFrame.this, Lin_MainFrame.class);
                        intent.putExtra("toTab", "usercenter");
                        RegisteSuccessFrame.this.startActivity(intent);
                        break;
                    default:
                        break;
                }

            }
        });


    }

    @OnClick(R.id.reg_success_sel_address)
    public void addressClick(View v) {

        if (adminSpStr.equals("中国香港") || adminSpStr.equals("中国澳门") || adminSpStr.equals("中国台湾")) {
            sel_address.setText(adminSpStr);
            showShi();
            return;
        }

//        final AlertDialog builder = new AlertDialog.Builder(this).create();
//        builder.setTitle("请选择省份");
        View root = LayoutInflater.from(this).inflate(R.layout.product_detail_youfei, null);
        int with = (int) (Util.getScreenSize(this).getWidth() * 0.75);
        int hight = (int) (Util.getScreenSize(this).getHeight() * 0.75);
        final com.mall.util.ShowMyPopWindow showMyPopWindow = new com.mall.util.ShowMyPopWindow(root, this, with, hight, LocationConstants.CENTER, 0);
        LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
        TextView titile = (TextView) root.findViewById(R.id.titile);
        titile.setText("请选择省份");
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
        TextView s = null;
        for (Zone z : sheng) {
            if (z.getName().contains("澳门")||z.getName().contains("香港")||z.getName().contains("台湾")){
                continue;
            }
            s = getTextView(z.getName(), z.getId());
            s.setLayoutParams(ll);
            line.addView(s);
            s.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != backSheng)
                        backSheng.setTextColor(Color.parseColor("#535353"));
                    backSheng = (TextView) v;
                    sel_address.setText(((TextView) v).getText());
                    ((TextView) v).setTextColor(Color.parseColor("#ff0000"));
                    shengId = v.getTag() + "";
                    oneProvince = oneCity = oneArea = "";
                    showShi();
                    showMyPopWindow.dismiss();
                }
            });
        }
    }

    private TextView getTextView(String text, String tag) {
        TextView s = new TextView(this);
        s.setText(text);
        s.setTag(tag);
        s.setTextColor(Color.parseColor("#535353"));
        s.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.liner_border));
        s.setEllipsize(TruncateAt.MARQUEE);
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
//                final AlertDialog builder = new AlertDialog.Builder(RegisteSuccessFrame.this).create();
                View root = LayoutInflater.from(RegisteSuccessFrame.this).inflate(R.layout.product_detail_youfei, null);
                int with = (int) (Util.getScreenSize(RegisteSuccessFrame.this).getWidth() * 0.75);
                int hight = (int) (Util.getScreenSize(RegisteSuccessFrame.this).getHeight() * 0.75);
                final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(root, RegisteSuccessFrame.this, with, hight, LocationConstants.CENTER, 0);
                LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
                TextView titile = (TextView) root.findViewById(R.id.titile);
                titile.setText("请选择城市");
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
                TextView s = null;
                for (Zone z : shi) {
                    s = getTextView(z.getName(), z.getId());
                    s.setLayoutParams(ll);
                    line.addView(s);
                    s.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != backShi)
                                backShi.setTextColor(Color.parseColor("#535353"));
                            backShi = (TextView) v;
                            sel_address.setText(sel_address.getText() + " - " + ((TextView) v).getText());
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

                View root = LayoutInflater.from(RegisteSuccessFrame.this).inflate(R.layout.product_detail_youfei, null);
                int with = (int) (Util.getScreenSize(RegisteSuccessFrame.this).getWidth() * 0.75);
                int hight = (int) (Util.getScreenSize(RegisteSuccessFrame.this).getHeight() * 0.75);
                final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(root, RegisteSuccessFrame.this, with, hight, LocationConstants.CENTER, 0);
                LinearLayout line = (LinearLayout) root.findViewById(R.id.product_detail_youfei_line);
                TextView titile = (TextView) root.findViewById(R.id.titile);
                titile.setText("请选择区/县");
                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.setMargins(_5dp, _5dp / 2, _5dp, _5dp / 2);
                TextView s = null;
                for (Zone z : qu) {
                    s = getTextView(z.getName(), z.getId());
                    s.setLayoutParams(ll);
                    line.addView(s);
                    s.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (null != backShi)
                                backShi.setTextColor(Color.parseColor("#535353"));
                            backShi = (TextView) v;
                            sel_address.setText(sel_address.getText() + " - " + ((TextView) v).getText());
                            sel_address.setTag(-7, v.getTag() + "");
                            selZoneId = Util.getInt(sel_address.getTag(-7));
                            LogUtils.d(sel_address.getTag(-7) + "__");
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

    @Override
    public void onKeyboardChange(boolean isShow, int keyboardHeight) {
        // TODO Auto-generated method stub
        if (!isShow) {
            if (Util.isNull(fxText.getText().toString())) {
                tjrMsg.setText(Html.fromHtml("<html><body>请正确填写ID，如不知分享ID请致电 <font color='#66bbf2'>400-666-3838</font></body></html>"));
            }
            if (isqueryUserInfo && !Util.isNull(fxText.getText().toString())) {
                fxcode = fxText.getText().toString();
                //当键盘收起的时候且 分享id 焦点持有进行查询
                Util.asynTask(this, "正在获取招商信息...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null != runData) {
                            InviterInfo info = (InviterInfo) runData;
                            if (TextUtils.isEmpty(info.getUserid())) {
                                tjrMsg.setTextColor(Color.parseColor("#c6c6c6"));
                                tjrMsg.setText("对不起，该会员不存在");
                                return;
                            }
                            int levelId = Util.getInt(info.getLevel());
                            if (6 != levelId) {
                                String name = info.getName();
                                if (!Util.isNull(name) && 2 <= name.length()) {
                                    name = name.substring(0, 1) + "*";
                                    if (3 <= info.getName().length())
                                        name += info.getName().substring(info.getName().length() - 1);
                                }
                                String phone = info.getPhone();
                                if (!Util.isNull(phone))
                                    phone = phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length());
                                tjrMsg.setText("用户资料：" + name + "　" + phone);
                            } else {
                                tjrMsg.setText("该会员不能作为分享ID。");
                            }
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.getInviter, "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&uid="
                                + Util.get(fxText.getText().toString()));
                        return web.getObject(InviterInfo.class);
                    }
                });
            } else {
                Log.e("键盘监听", "输入信息为空");
            }
        }
    }
}