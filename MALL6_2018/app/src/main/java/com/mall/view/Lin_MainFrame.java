package com.mall.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.BroadcastReceiver.NetBroadcastReceiver;
import com.mall.MessageEvent;
import com.mall.SharingredmoneyActivity;
import com.mall.model.BannerInfo;
import com.mall.model.PopUpAds;
import com.mall.model.User;
import com.mall.model.Zone;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.newmodel.NumberSeekBar;
import com.mall.newmodel.SharedPreferencesUtils;
import com.mall.newmodel.UpDataVersionDialog;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.serving.query.activity.QueryMainActivity;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.Data;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BusinessCircle.BusinessCircleActivity;
import com.mall.view.messageboard.MyToast;
import com.mall.view.service.DownloadService;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import io.reactivex.functions.Consumer;

import static com.mall.util.Util.info;
import static com.mall.util.Util.isfristopenPopUpAds;

public class Lin_MainFrame extends TabActivity {

    public static Lin_MainFrame newInstance = null;
    TabHost tabHost;
    private static int size = 0;
    @ViewInject(R.id.homeButton)
    private TextView homeButton;
    @ViewInject(R.id.huodong)
    private TextView huodong;
    @ViewInject(R.id.fujin)
    private TextView fujin;
    @ViewInject(R.id.my_card)
    private TextView my;
    private String toTab = "";
    private int state = 0;
    private TabWidget tabWidget;
    @ViewInject(R.id.xinqing)
    private TextView xinqing;
    @ViewInject(R.id.personrl)
    private View personrl;
    @ViewInject(R.id.imageup)
    private ImageView imageup;

    @ViewInject(R.id.fab_add_comment)
    private DragFloatActionButton fab_add_comment;

    UpDataVersionDialog dataVersionDialog;

    String from = "";

    public Context context;
    private SharedPreferences sp;
    private int times = 0;


    NetBroadcastReceiver netBroadcastReceiver;

    //安装应用的流程
    private void installProcess() {

        boolean haveInstallPermission;
        Log.e("Build.VERSION.SDK_INT", "Build.VERSION.SDK_INT" + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = getPackageManager().canRequestPackageInstalls();
            Log.e("安装应用需要打开未知来源权限", "haveInstallPermission" + haveInstallPermission);
            if (!haveInstallPermission) {//没有权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MyToast.makeText(this, "安装应用需要打开未知来源权限，请去设置中开启权限", 5).show();
                    startInstallPermissionSettingActivity();
                }


            } else {
                VersionCheck();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10086) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                boolean haveInstallPermission = getPackageManager().canRequestPackageInstalls();
                if (haveInstallPermission) {
                    MyToast.makeText(this, "授权成功", 5).show();
                    VersionCheck();
                }
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startInstallPermissionSettingActivity() {
        //注意这个是8.0新API
        Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
        startActivityForResult(intent, 10086);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Util.add(this);
        context = this;

        //注册事件
        EventBus.getDefault().register(this);
        Intent intent = getIntent();

        from = intent.getStringExtra("from");

        setContentView(R.layout.main_frame);
        ViewUtils.inject(this);
        newInstance = this;
        initTab();

        if ("phone".equals(from)) {
            state = 0;
            tabHost.setCurrentTabByTag("home");
        }
        if (Util.isNetworkConnected(context)) {
            getIntentData();
            getBanner();
        } else {
            Toast.makeText(context, "请检查你的网络！", Toast.LENGTH_SHORT).show();
        }
        if (UserData.getUser() != null) {
            User user = UserData.getUser();

            if (!user.getUserLevel().equals("城市总裁") && !user.getUserLevel().equals("城市CEO")) {
                if (Util.isNull(UserData.getUser().getIdNo()) && Util.isNull(UserData.getUser().getPassport())) {
                    VoipDialog voipDialog = new VoipDialog("完善个人信息可获得50元消费券和现金大红包", Lin_MainFrame.this, "立即完善", "稍后完善", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Util.showIntent(Lin_MainFrame.this, UpdateUserMessageActivity.class);
                        }
                    }, null);
                    voipDialog.show();
                }
            }

        }
        if (Util.isNetworkConnected(context)) {
            updateVersion();
        }
        clearData();
    }

    private void clearData() {
        boolean isclear = (boolean) SharedPreferencesUtils.getParam(context, "isCleanArea527", false);
        if (isclear) {

            DbUtils db = DbUtils.create(App.getContext());
            try {
                db.deleteAll(Zone.class);
                SharedPreferencesUtils.setParam(context, "isCleanArea527", true);
            } catch (DbException e) {
                Log.e("数据删除", "e" + e.toString());
                SharedPreferencesUtils.setParam(context, "isCleanArea527", false);
                e.printStackTrace();

            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册事件
        if (netBroadcastReceiver != null) {
            unregisterReceiver(netBroadcastReceiver);//LS:重点！
        }
        EventBus.getDefault().unregister(this);
    }

    String downloadinfo = "";

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent messageEvent) {
        if (messageEvent != null) {
            Log.e("EventBus1" + getClass(), "回调" + messageEvent.getMessage());
            downloadinfo = messageEvent.getMessage();
        }

        if (dataVersionDialog != null) {
            dataVersionDialog.setProgress(messageEvent.getProgress());
//			if (downloadinfo.equals("停止下载")||downloadinfo.equals("下载失败")||downloadinfo.equals("取消下载")
//																	 ||downloadinfo.equals("下载中")
//																	 ){

            if (downloadinfo.equals("停止下载")) {
                dataVersionDialog.getButton_title().setText("立即\n升级");
            } else if (downloadinfo.equals("下载中")) {
                dataVersionDialog.getButton_title().setText("暂停");
            } else if (downloadinfo.equals("下载失败")) {

                dataVersionDialog.getButton_title().setText("暂停");
                Util.MyToast(context, "下载失败,点击重新下载", 1);
            }


            if (messageEvent.getProgress() >= 100) {
                dataVersionDialog.dismiss();
                downloadinfo = "下载完成";
                dataVersionDialog.getButton_title().setText("立即\n升级");
            }

        }
    }


    boolean isfristcheckredmoney = true;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        Util.aa = getWindowManager().getDefaultDisplay().getWidth();
        Log.e("数据55555", Util.aa + "");

        sp = this.getSharedPreferences("fristdialog", MODE_PRIVATE);
        times = sp.getInt("times", 0);

        if (times == 0) {
            SharedPreferences.Editor edit = sp.edit();
            edit.clear();
            edit.putInt("times", ++times).commit();
            openredmoneny();
        }

        if (UserData.getUser() != null) {

            if (!Util.isNull(UserData.getUser().getReg_red_box())) {
                String str = UserData.getUser().getReg_red_box();
                if (str.equals("1")) {
                    if (isfristcheckredmoney) {

                        opeNewPersonRedMoney(UserData.getUser());

                        isfristcheckredmoney = false;
                    }

                }
            }

        }

    }

    public void getBanner() {
        Log.e("isfristopenPopUpAds", isfristopenPopUpAds + "KL");
        Map<String, String> map = new HashMap<String, String>();
        map.put("cate", "6");
        NewWebAPI.getNewInstance().getWebRequest("/banner.aspx?call=getBannerByCate", map, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result))
                    return;
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code"))
                    return;

                Gson gson = new Gson();
                PopUpAds popUpAds = gson.fromJson(result.toString(), PopUpAds.class);
                try {
                    PopUpAds.ListBean listBean = popUpAds.getList().get(0);
                    BannerInfo bannerInfo = new BannerInfo(listBean.getId(), listBean.getUrl_android(),
                            listBean.getUrl_iPhone(), listBean.getCate(), listBean.getImage()
                    );
                    Data.savebanner(bannerInfo);
                } catch (Exception e) {
                    Log.e("图片信息是否存在", "LL" + false);
                    Data.clearbanner();
                    return;
                }

            }
        });


    }


    public void openPopUpAds(final PopUpAds.ListBean listBean) {
        final View mContentView = LayoutInflater.from(context).inflate(R.layout.newpersonpopupwindow, null);
        View closepopwind = mContentView.findViewById(R.id.close_popwind);  //获取。
        ImageView item_pop = (ImageView) mContentView.findViewById(R.id.item_pop);
        String image = listBean.getImage().replace("test.mall666.cn", Web.webImage);
        Log.e("图片地址", "image:" + image);
        /**
         * 设置加载错误时的默认图片
         */
//		Picasso.with(context).load(image).into(item_pop);
        final PopupWindow[] mPopUpWindow = {null};
        Picasso.with(context).load(image).config(Bitmap.Config.ARGB_8888).placeholder(R.drawable.ic_launcher).error(R.drawable.ic_launcher)
                .into(item_pop,
                        new com.squareup.picasso.Callback() {
                            @Override
                            public void onSuccess() {
                                mPopUpWindow[0] = ShowPopWindow.showShareWindow(mContentView, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);

                            }

                            @Override
                            public void onError() {
//						Toast.makeText(getApplicationContext(), "onError", Toast.LENGTH_SHORT).show();
                            }
                        });
        ImageView close_popwind_iv = (ImageView) mContentView.findViewById(R.id.close_popwind_iv);


        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPopUpWindow[0] != null) {
                    mPopUpWindow[0].dismiss();

                }
//				finish();


            }
        });
        closepopwind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//				Util.showIntent(context,RegisterFrame.class);
                Intent intent = new Intent();
                intent.setClass(context, ProductDeatilFream.class);
                intent.putExtra("url", listBean.getId());
                startActivity(intent);

                if (mPopUpWindow[0] != null) {
                    mPopUpWindow[0].dismiss();

                }

            }
        });

    }

    public void openredmoneny() {
        final View mContentView = LayoutInflater.from(context).inflate(R.layout.newpersonpopupwindow, null);
        final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);

        View closepopwind = mContentView.findViewById(R.id.close_popwind);  //获取。
        ImageView item_pop = (ImageView) mContentView.findViewById(R.id.item_pop);
        ImageView close_popwind_iv = (ImageView) mContentView.findViewById(R.id.close_popwind_iv);
        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();
            }
        });
        closepopwind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.showIntent(context, RegisterFrame.class);

                mPopUpWindow.dismiss();

            }
        });
    }

    public void getSharingRedBoxList() {
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=get_inviter_red_box_list&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() +
                        "&pageSize=" + 999 + "&page=" + 1 + "&state=" + 0,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

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
                        String str = json.getString("message");
                        int number = 0;
                        if (!Util.isNull(str)) {
                            number = Integer.parseInt(str);
                        }

                        if (number > 0) {
                            fab_add_comment.setVisibility(View.VISIBLE);
                        } else {
                            fab_add_comment.setVisibility(View.GONE);
                        }

                    }

                    @Override
                    public void requestEnd() {

                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });


    }

    @OnClick({R.id.homeButton, R.id.huodong, R.id.fujin, R.id.xinqing,
            R.id.my_card, R.id.personrl, R.id.fab_add_comment})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.fab_add_comment:
                Log.e("点击事件", "R.id.fab_add_comment");
                Intent intent1 = new Intent(context, SharingredmoneyActivity.class);
                startActivity(intent1);
                break;
            case R.id.homeButton:
                state = 0;
                toTab = "";
                changeColor(homeButton, huodong, fujin, xinqing, my);
                chanegeDrawable(R.drawable.new_page_shouye_1, homeButton);
                chanegeDrawable(R.drawable.squnch, huodong);
                chanegeDrawable(R.drawable.main_office, fujin);
                chanegeDrawable(R.drawable.bottom_find, xinqing);
                chanegeDrawable(R.drawable.new_page_my, my);
                tabHost.setCurrentTabByTag("home");
                break;
            case R.id.huodong:
                toTab = "";
                if (UserData.getUser() == null) {
                    Toast.makeText(this, "您还没登录哦，请前去登录", Toast.LENGTH_SHORT).show();
                    Util.showIntent(Lin_MainFrame.this, LoginFrame.class, new String[]{"huodong"}, new String[]{"huodong"});
                } else {
                    // Util.showIntent(this, NoteMainFrame.class);
                    state = 1;
                    changeColor(huodong, fujin, homeButton, xinqing, my);
                    chanegeDrawable(R.drawable.new_page_shouye, homeButton);
                    chanegeDrawable(R.drawable.sqch, huodong);
                    chanegeDrawable(R.drawable.main_office, fujin);
                    chanegeDrawable(R.drawable.bottom_find, xinqing);
                    chanegeDrawable(R.drawable.new_page_my, my);
                    tabHost.setCurrentTabByTag("notepad");
                }
                break;
            case R.id.fujin:
                state = 2;
                toTab = "";
                changeColor(fujin, huodong, homeButton, xinqing, my);
                chanegeDrawable(R.drawable.new_page_shouye, homeButton);
                chanegeDrawable(R.drawable.squnch, huodong);
                chanegeDrawable(R.drawable.main_officepressed, fujin);
                chanegeDrawable(R.drawable.bottom_find, xinqing);
                chanegeDrawable(R.drawable.new_page_my, my);
                tabHost.setCurrentTabByTag("kongjian");
                // fujinFind(view);
                // Util.showIntent(this, ShopOfficeList.class);
                break;

            case R.id.xinqing:
                state = 3;
                toTab = "";
                changeColor(xinqing, huodong, fujin, homeButton, my);
                chanegeDrawable(R.drawable.new_page_shouye, homeButton);
                chanegeDrawable(R.drawable.squnch, huodong);
                chanegeDrawable(R.drawable.main_office, fujin);
                chanegeDrawable(R.drawable.bottom_find_click, xinqing);
                chanegeDrawable(R.drawable.new_page_my, my);
                tabHost.setCurrentTabByTag("mood");
                break;
            case R.id.personrl:
            case R.id.my_card:
                state = 4;
                toTab = "";
                changeColor(my, huodong, fujin, xinqing, homeButton);
                chanegeDrawable(R.drawable.new_page_shouye, homeButton);
                chanegeDrawable(R.drawable.squnch, huodong);
                chanegeDrawable(R.drawable.main_office, fujin);
                chanegeDrawable(R.drawable.bottom_find, xinqing);
                chanegeDrawable(R.drawable.new_page_my_1, my);
                tabHost.setCurrentTabByTag("my");
                break;
        }
    }

    public void initTab() {
        if (!Util.list.contains(this))
            Util.list.add(this);
        tabHost = getTabHost();
        tabWidget = tabHost.getTabWidget();
        tabHost.addTab(tabHost.newTabSpec("home").setIndicator("home")
                .setContent(new Intent(this, Lin_MallActivity.class)));
        Intent userCenter = new Intent(this, UserCenterFrame.class);
        userCenter.putExtra("from", "shouye");
        tabHost.addTab(tabHost.newTabSpec("my").setIndicator("my")
                .setContent(userCenter));
        tabHost.addTab(tabHost.newTabSpec("mood").setIndicator("mood")
                .setContent(new Intent(this, FindFrame.class)));
        tabHost.addTab(tabHost.newTabSpec("notepad").setIndicator("mood")
                .setContent(new Intent(this, BusinessCircleActivity.class)));
//				.setContent(new Intent(this, NoteMainFrame.class)));
        tabHost.addTab(tabHost.newTabSpec("kongjian").setIndicator("mood")
                .setContent(new Intent(this, QueryMainActivity.class)));
        // Util.showIntent(this, HuoDongFrame.class, new String[] { "type" },
        // new String[] { "tj" });
//		Intent intent = new Intent(this, NoteMainFrame.class);
        Intent intent = new Intent(this, BusinessCircleActivity.class);
        intent.putExtra("type", "tj");
        intent.putExtra("from", "shouye");
        tabHost.addTab(tabHost.newTabSpec("notepad").setIndicator("notepad")
                .setContent(intent));
        for (int i = 0; i < tabWidget.getChildCount(); i++) {
            // 设置tab背景颜色
            tabHost.setBackgroundColor(getResources().getColor(
                    R.color.transparent));
        }
    }

    private void VersionCheck() {
        try {
            Util.asynTask(new IAsynTask() {
                @Override
                public synchronized void updateUI(Serializable runData) {
                    Log.e("查看新特性", "1");
                    final String[] info = (String[]) runData;
                    Log.e("查看新特性", "2");
                    if (Util.isNull(info)) {
                        Log.e("查看新特性", "3");
                        return;
                    }
                    Log.e("查看新特性", "4");
                    for (String str : info) {
                        Log.e("查看新特性信息kk", "str" + str);
                    }
                    Log.e("查看新特性", "5");
                    if(info.length!=0){
                        if ("success".equals(info[0])) {

                            int versionTitle = (int) SharedPreferencesUtils.getParam(App.getContext(), "versionTitle", 0);
                            int cVer1 = Integer.parseInt(Util.version
                                    .replaceAll("\\.", ""));
                            SharedPreferencesUtils.setParam(App.getContext(), "versionTitle", cVer1);
                            Log.e("versionTitle", "versionTitle" + versionTitle + "cVer1" + cVer1);
                            if (cVer1 > versionTitle) {

                                String info1 = "";
                                for (String string : info) {
                                    info1 += string;
                                }
                                Log.e("最后信息", "info11" + info1);


//                            Util.showAlert("查看新特性", Lin_MainFrame.this, info, info.length >= 5 ? true : false, new View.OnClickListener() {
//                                @Override
//                                public void onClick(View view) {
//                                    Intent intent = new Intent(Lin_MainFrame.this, WebViewActivity.class);
//                                    intent.putExtra("url", "http://" + Web.webImage + "/phone/version.html");
//                                    intent.putExtra("title", "版本说明");
//                                    startActivity(intent);
//                                }
//                            });


                            }

                            Log.e("查看新特性", "6");
                            return;
                        }



                    Log.e("查看新特性", "8");
                    final String netVer = info[1];
                    Log.e("查看新特性", "9");
                    int cVer = Integer.parseInt(Util.version
                            .replaceAll("\\.", ""));
                    Util.netversion = netVer;

                    final int nVer = Integer.parseInt(netVer.replaceAll(
                            "\\.", ""));
                    Log.e("查看新特性", "10");

                    if (nVer > cVer) {

                        SharedPreferencesUtils.setParam(App.getContext(), "versionTitle", nVer);
                        Util.info = info;
                        String str = (String) SharedPreferencesUtils.getParam(App.getContext(), "version", "");

                        Log.e("检查版本", "str" + str);
                        if (str.equals("" + nVer)) {
                            boolean notupdata = (boolean) SharedPreferencesUtils.getParam(App.getContext(), "notupdate", false);
                            Log.e("是否允许更新", "notupdata" + notupdata);
                            if (notupdata) {
                                return;
                            }
                        }

                    }
                        //首先检查权限
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                            downloadinfo(nVer + "");
                        } else {

                            final int[] num = {0};
                            RxPermissions rxPermissions = new RxPermissions((Activity) context);
                            rxPermissions.requestEach(android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    .subscribe(new Consumer<Permission>() {
                                        @Override
                                        public void accept(Permission permission) throws Exception {

                                            if (permission.granted) {
                                                // 用户已经同意该权限
                                                num[0]++;
                                                if (num[0] == 2) {
                                                    downloadinfo(nVer + "");

                                                }

                                            } else if (permission.shouldShowRequestPermissionRationale) {
                                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                            } else {
                                                // 用户拒绝了该权限，并且选中『不再询问』
                                            }

                                        }
                                    });
                        }


                    }


                }

                @Override
                public Serializable run() {
                    return Util.update();
                }
            });
        } catch (Exception e) {

        }


    }

    private void downloadinfo(final String nVer) {

        //是否允许wifi更新
        boolean isCheckedWifi = getSharedPreferences("isCheckedWifi", MODE_PRIVATE).getBoolean("isCheckedWifi", false);
        if (isCheckedWifi) {
            //允许wifi更新,检查是否wifi状态下
            int netWorkState = Util.getNetworkType(context);
            if (netWorkState == 10) {
                Intent intent1 = new Intent(context, DownloadService.class);
                intent1.putExtra("downloadtype", "start");
                context.startService(intent1);
            }

        } else {
            //不允许wifi更新，界面显示

            if (dataVersionDialog == null) {
                dataVersionDialog = Util.updataui(Lin_MainFrame.this);
                if (!dataVersionDialog.isShowing()) {
                    dataVersionDialog.show();
                }
            }


            final NumberSeekBar numberSeekBar = dataVersionDialog.getSeekBar();
            numberSeekBar.setVisibility(View.INVISIBLE);
            StringBuffer messaage = new StringBuffer();
            messaage.append("最新版本：" + info[1] + "\n");
            messaage.append("更新日期：" + info[2] + "\n");
            messaage.append("更新内容：");
            for (int i = 3; i < info.length; i++) {
                if (!Util.isNull(info[i])) {
                    messaage.append("\n" + info[i]);
                } else {
                    break;
                }
            }
            dataVersionDialog.setUpdataMessage(messaage.toString());
            dataVersionDialog.getButton_title().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    numberSeekBar.setVisibility(View.VISIBLE);
                    Log.e("downloadinfo", downloadinfo + "ll");
                    //检查是否正在下载
                    if (downloadinfo.equals("下载中")) {
                        Intent intent1 = new Intent(context, DownloadService.class);
                        intent1.putExtra("downloadtype", "stop");
                        context.startService(intent1);

                    } else if (downloadinfo.equals("停止下载") || downloadinfo.equals("") || downloadinfo.equals("下载完成")
                            || downloadinfo.equals("取消下载")
                            ) {
                        Intent intent1 = new Intent(context, DownloadService.class);
                        intent1.putExtra("downloadtype", "start");
                        context.startService(intent1);
                    } else if (downloadinfo.equals("下载失败")) {
                        Intent intent1 = new Intent(context, DownloadService.class);
                        intent1.putExtra("downloadtype", "restart");
                        context.startService(intent1);

                    }

                }
            });
            dataVersionDialog.setclose(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (downloadinfo.equals("下载中")) {
                        //检查是否正在下载

                        Toast.makeText(App.getContext(), "将继续为你在后台下载", Toast.LENGTH_SHORT).show();
                    }


                }
            });

            dataVersionDialog.setnotupdatedclick(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtils.setParam(App.getContext(), "version", nVer);
                    SharedPreferencesUtils.setParam(App.getContext(), "notupdate", true);
                }
            });

        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (state != 0) {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 0) {
                changeColor(homeButton, huodong, fujin, xinqing, my);
                chanegeDrawable(R.drawable.new_page_shouye_1, homeButton);
                chanegeDrawable(R.drawable.squnch, huodong);
                chanegeDrawable(R.drawable.main_office, fujin);
                chanegeDrawable(R.drawable.bottom_find, xinqing);
                chanegeDrawable(R.drawable.new_page_my, my);
                tabHost.setCurrentTabByTag("home");
                state = 0;
                return true;
            }
        } else {
            if (event.getAction() == KeyEvent.ACTION_DOWN
                    && event.getKeyCode() == KeyEvent.KEYCODE_BACK
                    && event.getRepeatCount() == 0) {

                if (Leading.isOtherApp) {
                    Log.e("isOtherApp", "Yes");
                    Util.close();

                } else {
                    Log.e("isOtherApp", "No");
                    Intent home = new Intent(Intent.ACTION_MAIN);
                    home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    home.addCategory(Intent.CATEGORY_HOME);
                    startActivity(home);
                }

                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onResume() {
        super.onResume();


        if (UserData.getUser() == null) {
            fab_add_comment.setVisibility(View.GONE);
        }


    }

    private void updateVersion() {

        checkepermissions();

    }

    private void checkepermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            installProcess();
        } else {
            VersionCheck();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();


        if (UserData.getUser() != null) {
            getSharingRedBoxList();
        }

        if (Util.kfg.equals("1")) {
            Util.kfg = "";
            return;
        }

        Util.kfg = "";

        toTab = this.getIntent().getStringExtra("toTab");

        if (null != toTab) {
            if (toTab.equals("usercenter")) {
                my.performClick();
                getIntent().putExtra("toTab", "");
            } else if (toTab.equals("note")) {
                huodong.performClick();
                getIntent().putExtra("toTab", "");
            } else if (toTab.equals("home")) {
                homeButton.performClick();
                getIntent().putExtra("toTab", "");
            }
        }
    }


    FlakeView flakeView;

    public void opeNewPersonRedMoney(final User user) {
        final View mContentView = LayoutInflater.from(context).inflate(R.layout.randomredmoneypopupwindow, null);
        final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);
        View closepopwind = mContentView.findViewById(R.id.close_popwind);  //获取。
        ImageView item_pop = (ImageView) mContentView.findViewById(R.id.item_pop);
        ImageView close_popwind_iv = (ImageView) mContentView.findViewById(R.id.close_popwind_iv);
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
                                    return;
                                }

                                user.setReg_red_box("0");

                                UserData.setUser(user);

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
                                        ImageView close_popwind_iv = (ImageView) mContentViewred.findViewById(R.id.close_popwind_iv);
                                        LinearLayout container = (LinearLayout) mContentViewred.findViewById(R.id.container);
                                        TextView invitefriend = (TextView) mContentViewred.findViewById(R.id.invitefriend);
                                        TextView money = (TextView) mContentViewred.findViewById(R.id.money);
                                        money.setText(moneyStr + "元");
                                        flakeView = new FlakeView(context, R.drawable.redpocket);
                                        container.removeAllViews();
                                        container.addView(flakeView);
                                        flakeView.resume();
                                        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                try {
                                                    mPopUpWindowred.dismiss();
                                                    flakeView.pause();
                                                    UserCenterFrame.xj.setText(moneyStr);
                                                } catch (Exception e) {

                                                }
                                            }
                                        });
                                        invitefriend.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Toast.makeText(context, "邀请朋友", Toast.LENGTH_SHORT).show();

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


            }
        });

    }

    /**
     * 返回首页
     *
     * @return
     */
    public TextView getMain_tab_home() {
        return homeButton;
    }

    /**
     * 附近
     *
     * @return
     */
    public TextView getMain_tab_lmsj() {
        return fujin;
    }

    /**
     * 会员中心
     *
     * @return
     */
    public TextView getMain_tab_catagory() {
        return my;
    }

    /**
     * 活动
     *
     * @return
     */
    public TextView getMain_tab_car() {
        return huodong;
    }

    /**
     * 心情
     *
     * @return
     */
    public TextView getMain_tab_buy() {
        return xinqing;
    }

    private void chanegeDrawable(int imageid, TextView view) {

        Drawable icon = this.getResources().getDrawable(imageid);
        icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
        view.setCompoundDrawables(null, icon, null, null);
    }

    public void fujinFind(View v) {
        v.setEnabled(false);
        tabHost.setCurrentTabByTag("fujin");
        v.setEnabled(true);
    }

    @Override
    public void onBackPressed() {
        if (state != 0) {
            tabHost.setCurrentTabByTag("home");
        } else {
            super.onBackPressed();
        }
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

            if (state != 0) {
                state = 0;
                tabHost.setCurrentTabByTag("home");

            }

            return true;

        }

        return super.onKeyDown(keyCode, event);
    }

    private void changeColor(TextView... textViews) {
        for (int i = 0; i < textViews.length; i++) {
            if (i == 0) {
                textViews[i].setTextColor(getResources().getColor(
                        R.color.calendar_zhe_day));
            } else {
                textViews[i].setTextColor(getResources().getColor(
                        R.color.new_xiaozi));
            }
        }
    }

    // 升级后的遮罩
    @ViewInject(R.id.main_shadom5)
    public View main_shadom5;

    @OnClick(R.id.main_shadom5)
    public void shadomClick(View v) {

    }

    private void getIntentData() {
        String msg = getIntent().getDataString();

        if (TextUtils.isEmpty(msg)) {
            return;
        }
        if (msg.startsWith("http://") || msg.startsWith("https://")
                || msg.matches("(([a-zA-z0-9]|-){1,}\\.){1,}[a-zA-z0-9]{1,}-*")) {
            if (msg.contains("NewProDetail")) {
                String pid = msg.substring(msg.lastIndexOf("=") + 1);
                if (Util.isInt(pid)) {
                    Util.showIntent(this, ProductDeatilFream.class,
                            new String[]{"url"}, new String[]{pid});
                    return;
                }
            }

            if ((msg.contains("shopCollectsPage"))) {
                String pid = msg.substring(msg.lastIndexOf("=") + 1);

                if (Util.isInt(pid)) {
                    Util.showIntent(this, LMSJDetailFrame.class, new String[]{
                            "id", "name"}, new String[]{pid, "联盟商家详情"});
                    return;
                }
            }

            if (msg.contains("phoneRegiste")) {

                String pid = msg.substring(msg.lastIndexOf("=") + 1);

                if (Util.isInt(pid)) {
                    Util.showIntent(this, RegisterFrame.class,
                            new String[]{"inviter"}, new String[]{pid});
                    return;
                }
            }
            if (msg.contains("pay1.aspx?p1=")) {
                String[] strings = msg.split("username=");
                if (strings.length > 1) {
                    if (UserData.getUser() == null) {
                        Util.showIntent(this, LoginFrame.class);
                        return;
                    }
                    Util.showIntent(this, PayAgentFrame.class,
                            new String[]{"msg"}, new String[]{strings[1]});
                    return;
                }
            }

        }

    }

    private int LOCATIONCODE = 111;

    private int permissionlenght = 0;


}
