package com.mall.view;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.EdgeEffectCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lidroid.xutils.util.LogUtils;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * 欢迎界面 2017 9 4 晚
 *
 * @author Administrator
 */

public class Leading extends Activity {
    public static boolean isOtherApp = false;
    private static final int TO_THE_END = 0;
    private static final int LEVAVE_FROM_END = 1;

    private int[] pics = {R.drawable.j1
            , R.drawable.j2, R.drawable.j3
    };

    private List<View> guides = new ArrayList<>();
    private ViewPager viewPager;
    private ImageView start;
    private int curPos = 0; // 1
    private int times = 0;
    private SharedPreferences sp;
    private boolean isOpen = false;

    private EdgeEffectCompat leftEdge;
    private EdgeEffectCompat rightEdge;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Util.add(this);
        Intent openIntent = getIntent();

        Log.e("版本号检查", Util.version);
        if (openIntent.hasExtra("action")
                && openIntent.hasExtra("openClassName")) {
            Log.e("其他启动", isOtherApp + "");
            if ("lin00123".equals(openIntent.getStringExtra("action"))) {
                String userId = openIntent.getStringExtra("userId");
                String md5Pwd = openIntent.getStringExtra("md5Pwd");
                String userFace = openIntent.getStringExtra("userFace");
                String userNo = openIntent.getStringExtra("userNo");
                String comefrom = openIntent.getStringExtra("comefrom");
                if (!Util.isNull(userId) && !Util.isNull(md5Pwd)
                        && !Util.isNull(userNo)) {
                    User user = new User();
                    user.setUserId(userId);
                    user.setMd5Pwd(md5Pwd);
                    user.setUserNo(userNo);
                    if (!Util.isNull(userFace))
                        user.setUserFace(userFace);
                    UserData.setUser(user);
                }
                try {
                    isOtherApp = true;
                    if ("ydaphone".equals(comefrom)) {
                        Intent intent = new Intent(this, Class.forName(openIntent
                                .getStringExtra("openClassName")));
                        intent.putExtra("from", "phone");
                        startActivity(intent);
                        this.overridePendingTransition(
                                android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);

                    } else {
                        Util.showIntent(this, Class.forName(openIntent
                                .getStringExtra("openClassName")));
                    }
                    isOpen = true;
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // 如果两次的版本相同则直接取用户的登录次数，否则将登录此时重置为0,用户登录三种情况，
        // 1.用户在相同的软件登录多次 2用户分别安装两次相同版本的软件，并登录多次，3 用户使用不同的版本软件登录

        sp = this.getSharedPreferences("logintimes", MODE_PRIVATE);


        checkpermissions();


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent openIntent = getIntent();
        if (openIntent.hasExtra("action")
                && openIntent.hasExtra("openClassName")) {
            LogUtils.e("action=" + openIntent.getStringExtra("action") + "   "
                    + openIntent.getStringExtra("openClassName") + "   isOpen=" + isOpen);
            if (isOpen) {
                this.finish();
            }
        }
    }

    private void init() {

        this.getView();
        try {
            Field leftEdgeField = viewPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = viewPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(viewPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(viewPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        guides.clear();
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = buildImageView(pics[i]);
            guides.add(iv);
        }

        TelephonyManager mTm = (TelephonyManager) Leading.this
                .getSystemService(TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        final String id = mTm.getDeviceId();
        final String numer = mTm.getLine1Number();
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {

            }

            @Override
            public Serializable run() {
                String phone = "android";
                Web web = new Web(Web.install, "id=" + id + "&phone=" + phone
                        + "&version=" + Util.version + "&made="
                        + Util.getMake() + "&mode=" + Util.getModel()
                        + "&brand=" + Util.getBrand() + "&mobilePhone=" + numer
                        + "&systemVersion=" + Util.getRelease() + "&soft=远大云商");
                return web.getPlan();
            }
        });


        final GuidePageAdapter adapter = new GuidePageAdapter(guides);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (position == pics.length - 1) {
                } else if (curPos == pics.length - 1) {
                    handler.sendEmptyMessageDelayed(LEVAVE_FROM_END, 100);
                }
                curPos = position;
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (rightEdge != null && !rightEdge.isFinished()) {//到了最后一张并且还继续拖动，出现蓝色限制边条了
                    sp = getSharedPreferences("logintimes", MODE_PRIVATE);
                    Editor edit = sp.edit();
                    edit.clear();
                    edit.putInt("times", ++times).commit();
                    edit.putString("version", Util.version).commit();// 将软件的当前版本放入到首选项中
                    Intent intent = new Intent(Leading.this,
                            Lin_MainFrame.class);
                    Leading.this.startActivity(intent);
                    overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                }

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

    }

    private void checkpermissions() {
        Log.e("是否第一次打开app", "1");
        final int[] num = {0, 0, 0};
        final String[] requestPermissionstr = {

                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION


        };
        Log.e("是否第一次打开app", "2");
        RxPermissions rxPermissions = new RxPermissions((Activity) context);
        Log.e("是否第一次打开app", "3");
        rxPermissions.requestEach(requestPermissionstr)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {

                        if (permission.granted) {
                            Log.e("是否第一次打开app", "4");
                            num[0]++;
                            Log.e("是否第一次打开app", "同意权限");
                            if (num[0] == requestPermissionstr.length) {
                                Log.e("是否第一次打开app", sp.getInt("times", 0) + "LLLLLLLLLl");
                                if (!Util.version.equals(sp.getString("version", Util.version))) {
                                    times = 0;
                                } else {
                                    times = sp.getInt("times", 0);
                                }
                                if (times == 0) {
                                    setContentView(R.layout.activity_leading);
                                    init();
                                    File f = new File(Util.apkPath + "Mall" + Util.version + ".apk");
                                    if (f.exists())
                                        f.delete();
                                } else if (times >= 1) {
                                    Intent intent = new Intent(context, LoadFrame.class);
                                    startActivity(intent);
                                    overridePendingTransition(android.R.anim.slide_in_left,
                                            android.R.anim.slide_out_right);
                                    finish();
                                }

                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                            num[1]++;
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                            if (num[1] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");
                                finish();
                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                            num[2]++;
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                            if (num[2] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");
                                Log.e("权限请求1", "permission" + permission.name);
                                Log.e("权限请求2", "permission" + permission.getClass());
//								android.permission.REQUEST_INSTALL_PACKAGES
//								android.permission.REQUEST_INSTALL_PACKAGES

                                try {
                                    Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                    Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent1);
                                } catch (Exception e2) {
                                    // TODO: handle exception
                                }

                                finish();
                            }

                        }

                    }
                });

    }

    private ImageView buildImageView(int id) {
        ImageView iv = new ImageView(this);
        iv.setImageResource(id);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.FILL_PARENT);

        iv.setLayoutParams(params);
        iv.setScaleType(ScaleType.CENTER_CROP);
        return iv;

    }

    private void getView() {
        viewPager = this.findViewById(R.id.contentPager);
        start = findViewById(R.id.open);
        start.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sp = getSharedPreferences("logintimes", MODE_PRIVATE);
                Editor edit = sp.edit();
                edit.clear();
                edit.putInt("times", ++times).commit();
                edit.putString("version", Util.version).commit();// 将软件的当前版本放入到首选项中
                Intent intent = new Intent(Leading.this, Lin_MainFrame.class);
                Leading.this.startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);

                finish();

            }
        });
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == TO_THE_END)
                ;// start.setVisibility(View.VISIBLE);
            else if (msg.what == LEVAVE_FROM_END)
                start.setVisibility(View.GONE);
        }
    };

    class GuidePageAdapter extends PagerAdapter {
        private List<View> views;

        public GuidePageAdapter(List<View> views) {
            this.views = views;
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public Object instantiateItem(View arg0, int arg1) {
            if (arg1 == pics.length - 1) {
                views.get(arg1).setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        sp = getSharedPreferences("logintimes", MODE_PRIVATE);
                        Editor edit = sp.edit();
                        edit.clear();
                        edit.putInt("times", ++times).commit();
                        edit.putString("version", Util.version).commit();// 将软件的当前版本放入到首选项中
                        Intent intent = new Intent(Leading.this,
                                Lin_MainFrame.class);
                        Leading.this.startActivity(intent);
                        overridePendingTransition(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                    }
                });
            }
            ((ViewPager) arg0).addView(views.get(arg1), 0);
            return views.get(arg1);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView(views.get(arg1));
        }

        @Override
        public void finishUpdate(View container) {
            super.finishUpdate(container);
        }
    }
}
