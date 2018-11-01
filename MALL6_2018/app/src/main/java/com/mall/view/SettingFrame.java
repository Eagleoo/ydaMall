package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.CapitalAccount.AddBankCardActivity;
import com.mall.CapitalAccount.MyAccount_BankListActivity;
import com.mall.model.UserAccountWithdrawal;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.newmodel.NumberSeekBar;
import com.mall.newmodel.SharedPreferencesUtils;
import com.mall.newmodel.UpDataVersionDialog;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.voip.view.popupwindow.QuitDialog;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.FileUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.List;

@SuppressLint("NewApi")
@ContentView(R.layout.setting_frame)
public class SettingFrame extends Activity {

    VoipDialog voipDialog;
    @ViewInject(R.id.wificheckbox)
    private CheckBox wificheckbox;
    @ViewInject(R.id.version_ic)
    private ImageView versionIc;
    @ViewInject(R.id.version)
    private TextView version;
    @ViewInject(R.id.bindingqq)
    private TextView bindingqq;
    UpDataVersionDialog dataVersionDialog;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;
        Util.initTitle(this, "设置", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        wificheckbox.setChecked(getSharedPreferences("isCheckedWifi", MODE_PRIVATE).getBoolean("isCheckedWifi", false));
        wificheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wificheckbox.setChecked(isChecked);
                getSharedPreferences("isCheckedWifi", MODE_PRIVATE).edit().putBoolean("isCheckedWifi", isChecked).commit();
            }
        });
        version.setText(Util.version);
    }

    @OnClick({R.id.userInfo, R.id.real_name, R.id.exchange_phone, R.id.login_password, R.id.two_password, R.id.address_manager, R.id.setting_nettest, R.id.setting_about_me, R.id.check_version, R.id.setting_exit_login, R.id.setting_clear_cache, R.id.bindingqq})
    public void click(final View view) {
        switch (view.getId()) {
            case R.id.bindingqq:
                if (qqstate.equals("-1")) {
                    Toast.makeText(context, "未检查到是否绑定了QQ", Toast.LENGTH_SHORT).show();
                } else if (qqstate.equals("0")) {
                    Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagQQ});
                } else {
                    Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"QQ"});
                }
                break;
            case R.id.userInfo:
                Util.showIntent(this, UserInfoFrame.class);
                break;
            case R.id.real_name:
                Util.showIntent(this, UpdateUserMessageActivity.class);
                break;
            case R.id.exchange_phone:
                Util.showIntent(this, UpdatePhoneFrame.class);
                break;
            case R.id.login_password:
                Util.showIntent(this, UsermodpassFrame.class);
                break;
            case R.id.two_password:
                String secondPwd = UserData.getUser().getSecondPwd();
                if (TextUtils.isEmpty(secondPwd) || secondPwd.equals("0")) {
                    VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(SettingFrame.this,
                                    SetSencondPwdFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                } else {
                    Util.showIntent(this, UpdateTwoPwdFrame.class);
                }
                break;
            case R.id.address_manager:
                Util.showIntent(this, ShopAddressManagerFrame.class);
                break;
            case R.id.setting_nettest:
                final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "请耐心等待1—3分钟...");
                final Handler h = new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        cpd.cancel();
                        cpd.dismiss();
                        if (500 == msg.what) {
                            Util.show("无法连接云商接口。请检查您的网络！", SettingFrame.this);
                            return;
                        }
                        String[] values = (String[]) msg.obj;
                        String title = values[0];
                        String lost = values[1];
                        String delay = values[2];
                        AlertDialog dialog = new AlertDialog.Builder(SettingFrame.this).create();
                        dialog.setTitle("远大云商网络测试");
                        dialog.setMessage(title + "\n\n 网络丢包率：" + lost + "\n 网络延迟：" + delay + "\n\n注：1.丢包率越大则说明您当前的网络越不稳定。\n2.网络延迟一般在10ms—90ms左右，延迟越大说明您当前网络越慢。");
                        dialog.setButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });
                        dialog.setButton2("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                };
                new Thread(new Runnable() {
                    public void run() {
                        Message msg = h.obtainMessage();
                        String lost = "";
                        String delay = "";
                        Process p;
                        try {
                            p = Runtime.getRuntime().exec("ping -c 50 " + "app.yda360.com");
                            BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                            String str = new String();
                            String title = "";
                            if ((str = buf.readLine()) != null) {
                                title = str;
                            }
                            while ((str = buf.readLine()) != null) {
                                System.out.println("-------" + str + "-------");
                                if (str.contains("packet loss")) {
                                    int i = str.indexOf("received");
                                    int j = str.indexOf("%");
                                    System.out.println("丢包率:" + str.substring(i + 10, j + 1));
                                    lost = str.substring(i + 10, j + 1);
                                }
                                if (str.contains("avg")) {
                                    int i = str.indexOf("/", 20);
                                    int j = str.indexOf(".", i);
                                    System.out.println("延迟:" + str.substring(i + 1, j));
                                    delay = str.substring(i + 1, j);
                                    delay = delay + "ms";
                                }
                            }
                            msg.what = 200;
                            msg.obj = new String[]{title, lost, delay};
                        } catch (IOException e) {
                            e.printStackTrace();
                            msg.what = 500;
                        }
                        h.sendMessage(msg);
                    }
                }).start();
                break;
            case R.id.setting_about_me:
                Util.showIntent(this, AboutMeFrame.class);
                break;
            case R.id.check_version:

                view.setEnabled(false);
                checkVersion(view);
                break;
            case R.id.setting_exit_login:
                if (UserData.getUser() != null) {
                    QuitDialog dialog = new QuitDialog(SettingFrame.this, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent home = new Intent(Intent.ACTION_MAIN);
                            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            home.addCategory(Intent.CATEGORY_HOME);
                            startActivity(home);
                            System.gc();
                            Util.close();
                        }
                    }, new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            UserData.setUser(null);
                            Util.showIntent(SettingFrame.this, LoginFrame.class);
                            finish();
                        }
                    });
                    dialog.show();

                } else {
                    Util.show("您没有登录！", this);
                }
                break;
            case R.id.setting_clear_cache:
                FileUtil.deleteDir(new File(FileUtil.FILE_FILE));
                voipDialog = new VoipDialog("清除图片缓存，您能获取到最新的图片。但是会增加您的流量消耗。", SettingFrame.this, "清除", "取消", new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        AnimeUtil.getImageLoad().clearDiscCache();
                        AnimeUtil.getImageLoad().clearMemoryCache();
                        BitmapUtils bmUtil = new BitmapUtils(SettingFrame.this);
                        bmUtil.clearCache();
                        bmUtil.clearDiskCache();
                        bmUtil.clearMemoryCache();
                        File proDir = new File(Util.proPath);
                        if (proDir.exists()) {
                            for (File pro : proDir.listFiles()) {
                                pro.delete();
                            }
                        }

                        File shopMDir = new File(Util.shopMPath);
                        if (shopMDir.exists()) {
                            for (File shopM : shopMDir.listFiles()) {
                                shopM.delete();
                            }
                        }

                        File shopDir = new File(Util.shopPath);
                        if (shopDir.exists()) {
                            for (File shop : shopDir.listFiles()) {
                                shop.delete();
                            }
                        }

                        File bannerDir = new File(Util.bannerPath);
                        if (bannerDir.exists()) {
                            for (File banner : bannerDir.listFiles()) {
                                banner.delete();
                            }
                        }

                        File downDir = new File(Util.downPath);
                        if (downDir.exists()) {
                            for (File down : downDir.listFiles()) {
                                down.delete();
                            }
                        }
                        File qrCodeDir = new File(Util.qrPath);
                        if (qrCodeDir.exists()) {
                            for (File qrCode : qrCodeDir.listFiles()) {
                                qrCode.delete();
                            }
                        }
                        ActivityManager activityManger = (ActivityManager) SettingFrame.this.getSystemService(ACTIVITY_SERVICE);
                        List<ActivityManager.RunningAppProcessInfo> list = activityManger.getRunningAppProcesses();
                        if (list != null)
                            for (int i = 0; i < list.size(); i++) {
                                ActivityManager.RunningAppProcessInfo apinfo = list.get(i);
                                String[] pkgList = apinfo.pkgList;
                                if (apinfo.importance > ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE) {
                                    for (int j = 0; j < pkgList.length; j++) {
                                        // 2.2以上是过时的,请用killBackgroundProcesses代替
                                        if (android.os.Build.VERSION.SDK_INT < 10)
                                            activityManger.restartPackage(pkgList[j]);
                                        else
                                            activityManger.killBackgroundProcesses(pkgList[j]);
                                    }
                                }
                            }
                        Util.show("缓存清除成功！", SettingFrame.this);

                    }
                }, null);
                voipDialog.show();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        check_band_account();
        checkVersion(null);
    }

    private String qqstate = "-1";// 0 为绑定 1为绑定

    private void check_band_account() {
        final CustomProgressDialog cpd = Util.showProgress("账户检查中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "get_tx_way" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                ,
                new NewWebAPIRequestCallback() {
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
                        Gson gson = new Gson();
                        UserAccountWithdrawal userAccountWithdrawal = gson.fromJson(result.toString(), UserAccountWithdrawal.class);
                        UserAccountWithdrawal.ListBean listBean = userAccountWithdrawal.getList().get(0);
                        qqstate = listBean.getQq();

                        if (listBean.getQq().equals("0")) { //支付宝
                            bindingqq.setVisibility(View.GONE);
                        } else {
                            bindingqq.setText("QQ登录解绑");
                            bindingqq.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    private void checkVersion(final View view) {
        Util.asynTask(this, "正在检测版本...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (view != null) {
                    view.setEnabled(true);
                }

                final String[] info = (String[]) runData;
                if (info.length == 0) {
                    Util.show("网络异常,请检查网络!");
                    return;
                }
                if ("success".equals(info[0])) {
                    if (view != null) {
                        Util.show("您已经是最新版，不需要更新！");
                    }
                    return;
                }
                final String netVer = info[1];
                int cVer = Integer.parseInt(Util.version
                        .replaceAll("\\.", ""));
                final int nVer = Integer.parseInt(netVer.replaceAll(
                        "\\.", ""));
                if (nVer > cVer) {

                    if (view == null) {
                        versionIc.setVisibility(View.VISIBLE);
                        return;
                    }

                    if (dataVersionDialog == null) {
                        dataVersionDialog = Util.updataui(SettingFrame.this);
                    }
                    dataVersionDialog.show();
                    dataVersionDialog.gettv1().setVisibility(View.INVISIBLE);
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

                    dataVersionDialog.setclose(null);

                    dataVersionDialog.setOnclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.openWeb(SettingFrame.this, info[0]);
                            dataVersionDialog.cancel();
                            dataVersionDialog.dismiss();


                        }
                    });
                    dataVersionDialog.setnotupdatedclick(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferencesUtils.setParam(SettingFrame.this, "version", nVer);
                            SharedPreferencesUtils.setParam(SettingFrame.this, "notupdate", true);
                        }
                    });

                }
            }

            @Override
            public Serializable run() {
                return Util.update();
            }
        });

    }
}
