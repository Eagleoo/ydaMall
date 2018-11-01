package com.mall.view;

import android.app.Activity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadCallBack;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.util.SoundUtil;
import com.mall.util.UserData;
import com.mall.util.Util;

public class NewShockToShock extends Activity implements SensorEventListener {
    @ViewInject(R.id.root)
    private FrameLayout root;
    @ViewInject(R.id.topCenter)
    private TextView topCenter;
    private ImageView shock_img;
    private LinearLayout container;
    private ImageView ballon, ballon_green;
    private TextView shock_czzh_tex;
    private float sc_height = 0;
    private TextView shock_sb_text;
    private SensorManager mSensorManager;
    private int type = 1;
    private double location_x = 0;
    private double location_y = 0;
    public static final Object lock = new Object();
    private boolean flag = false;
    private PopupWindow distancePopup;
    private CustomProgressDialog pDialog = null;
    private BitmapUtils bmUtils;
    private SharedPreferences sp;
    private int requestTimes = 1;
    private PositionService locationService;
    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation progress) {
                    location_x = progress.getLongitude();
                    location_y = progress.getLatitude();
                }

                @Override
                public void onError(AMapLocation error) {
                    Util.show("获取位置信息失败，请确认网络或者GPS是否正常", NewShockToShock.this);
                }
            });


        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            pDialog.cancel();
            pDialog.dismiss();
            String result = (String) msg.obj;
            JSONObject obj = null;
            try {
                obj = JSON.parseObject(result.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            if (200 != obj.getInteger("code").intValue()) {
                return;
            }
            JSONArray array = obj.getJSONArray("list");
            JSONObject[] objs = array.toArray(new JSONObject[]{});
            if (objs == null || objs.length == 0) {
                return;
            }
            // 获取到的人距离应该在附近，
            mSensorManager.unregisterListener(NewShockToShock.this);
            if (objs.length == 1) {
                // 只要到一个人的情况
                getPopupWindow();
                startPopupWindow(objs[0]);
                distancePopup.showAsDropDown(topCenter, 0, 50);
            } else {
                // 摇到多个人的情况
                Intent intent = new Intent(NewShockToShock.this,
                        ListOfShock.class);
                intent.putExtra("result", result);
                intent.putExtra("title", "摇到的人");
                intent.putExtra("type", type + "");
                NewShockToShock.this.startActivity(intent);
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_shock_to_shock);
        initView();
        initContainerSize();
        rotateAnimation();
        translateAnimation();
        pDialog = CustomProgressDialog.createDialog(this);
        pDialog.setMessage(pDialog, "正在搜寻同时摇晃手机的人");
        ViewUtils.inject(this);
        bmUtils = new BitmapUtils(this);
        shock_sb_text.performClick();
        root.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View arg0, MotionEvent arg1) {
                mSensorManager.registerListener(NewShockToShock.this,
                        mSensorManager
                                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
                return true;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        initSensor();
        requestTimes = 1;
        sp = this.getSharedPreferences("shock_data", 0);
        SoundUtil.InitSound();
        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @OnClick(R.id.shock_zzgz_1)
    public void zzgz_1Click(View view) {

        View pop = LayoutInflater.from(this).inflate(R.layout.new_shock_pop,
                null);

        ImageView iv_1 = (ImageView) pop.findViewById(R.id.iv_1);


        final PopupWindow popupWindow = new PopupWindow(this);
        popupWindow.setWidth(Util.dpToPx(this, 235));
        popupWindow.setHeight(LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(false);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setContentView(pop);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, -120);
        iv_1.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {

                popupWindow.dismiss();
            }
        });

//		Util.alert(this, "摇一摇转账规则",
//				"1、商币账户转账：会员可以将商币转给联盟商家，但会员间不可互转，联盟商家可以将商币转给任何人。\n2、充值账户转账：所有角色可以相互转账，没有限制。");
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    private void startPopupWindow(final JSONObject obj) {
        final View pView = getLayoutInflater().inflate(
                R.layout.shock_one_person_popupview, null);
        final TextView one_shock_sb_text = (TextView) pView
                .findViewById(R.id.one_shock_sb_text);
        final TextView one_shock_czzh_tex = (TextView) pView
                .findViewById(R.id.one_shock_czzh_tex);
        TextView shock_zzgz_1 = (TextView) pView.findViewById(R.id.shock_zzgz_1);
        shock_zzgz_1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                zzgz_1Click(root);
            }
        });
        if (type == 1) {
            sb_click(one_shock_sb_text, one_shock_czzh_tex);
        } else {
            czzhclcik(one_shock_czzh_tex, one_shock_sb_text);
        }
        one_shock_sb_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sb_click(arg0, one_shock_czzh_tex);
            }
        });
        one_shock_czzh_tex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                czzhclcik(arg0, one_shock_sb_text);
            }
        });

        ImageView one_ballon = (ImageView) pView.findViewById(R.id.one_ballon);
        ImageView one_ballon_green = (ImageView) pView
                .findViewById(R.id.one_ballon_green);
        TranslateAnimation up = new TranslateAnimation(0, 0, 0,
                0 - sc_height * 3 / 4);
        up.setInterpolator(new AccelerateInterpolator());
        up.setDuration(3000);
        up.setFillAfter(true);
        one_ballon.startAnimation(up);

        TranslateAnimation up_green = new TranslateAnimation(0, 0, 0,
                0 - sc_height + 50);
        up_green.setInterpolator(new DecelerateInterpolator());
        up_green.setDuration(3000);
        up_green.setFillAfter(true);
        one_ballon_green.startAnimation(up_green);

        TextView one_distance = (TextView) pView
                .findViewById(R.id.one_distance);
        TextView one_name = (TextView) pView.findViewById(R.id.one_name);

        LatLng start = new LatLng(location_x, location_y);
        LatLng end = new LatLng(Double.parseDouble(obj.getString("x")),
                Double.parseDouble(obj.getString("y")));
        double distance = AMapUtils.calculateLineDistance(start, end) / 1000F;
        distance = Util.getDouble(distance, 2);
        if (location_x != 0 && location_y != 0) {
            if (distance == 0) {
                one_distance.setText("近在咫尺");
            } else if (distance < 1) {
                one_distance.setText("相距1000米以内");
            } else {
                one_distance.setText("相距" + distance + "里");
            }
        }
        final ImageView one_face = (ImageView) pView
                .findViewById(R.id.one_face);
        one_name.setText(obj.getString("userid"));
        final int _45dp = Util.pxToDp(this, 45);
        bmUtils.display(one_face, obj.getString("userimg"),
                new BitmapLoadCallBack<View>() {
                    @Override
                    public void onLoadCompleted(View arg0, String arg1,
                                                Bitmap arg2, BitmapDisplayConfig arg3,
                                                BitmapLoadFrom arg4) {
                        Bitmap zoomBm = Util.zoomBitmap(arg2, _45dp, _45dp);
                        one_face.setImageBitmap(Util.toRoundCorner(zoomBm, 5));
                    }

                    @Override
                    public void onLoadFailed(View arg0, String arg1,
                                             Drawable arg2) {
                        one_face.setImageResource(R.drawable.ic_launcher_black_white);
                    }
                });
        LinearLayout one_container_layout = (LinearLayout) pView
                .findViewById(R.id.one_container_layout);
        one_container_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                distancePopup.dismiss();
                String parent = "";
                String userkey = "";
                if (type == 1) {
                    parent = "商币";
                    userkey = "sb";
                } else {
                    parent = "充值账户";
                    userkey = "cp";
                }
                Intent intent = new Intent(NewShockToShock.this,
                        MoneyToMoneyFrame.class);
                intent.putExtra("parentName", parent);
                intent.putExtra("userKey", userkey);
                intent.putExtra("userid", obj.getString("userid"));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                NewShockToShock.this.startActivity(intent);
            }
        });
        pView.setFocusable(true);
        pView.setFocusableInTouchMode(true);
        if (distancePopup != null) {
            distancePopup.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    initSensor();
                }
            });
        }
        pView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                mSensorManager.registerListener(NewShockToShock.this,
                        mSensorManager
                                .getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                        SensorManager.SENSOR_DELAY_NORMAL);
                return true;
            }
        });
        initPopupWindow(pView);
    }

    private void initPopupWindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.MATCH_PARENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimationupanddown);
    }

    private void initSensor() {
        mSensorManager = (SensorManager) this
                .getSystemService(Service.SENSOR_SERVICE);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSensorManager.unregisterListener(this);
    }

    private void initView() {
        Util.initTitle(this, "摇一摇转账", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Util.showIntent(NewShockToShock.this,
                        SettingOfShock.class);
            }
        }, R.drawable.setting_blue);
        shock_img = (ImageView) this.findViewById(R.id.shock_img);
        container = (LinearLayout) this.findViewById(R.id.container);
        ballon = (ImageView) this.findViewById(R.id.ballon);
        ballon_green = (ImageView) this.findViewById(R.id.ballon_green);
        shock_sb_text = (TextView) this.findViewById(R.id.shock_sb_text);
        shock_czzh_tex = (TextView) this.findViewById(R.id.shock_czzh_tex);
        shock_czzh_tex.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                czzhclcik(v, shock_sb_text);
            }
        });
        shock_sb_text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sb_click(v, shock_czzh_tex);
            }
        });
    }

    private void czzhclcik(View v, TextView another) {
        type = 2;
        // shock_sb_text.performClick();
        TextView t = (TextView) v;
        if (t.getTag() != null && "selected".equals(t.getTag())) {

        } else {
            t.setTag("selected");
            t.setTextColor(Color.parseColor("#2498e3"));
            Resources res = getResources();
            Drawable dra = res.getDrawable(R.drawable.shock_czzh);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
            t.setCompoundDrawables(dra, null, null, null);

            another.setTextColor(Color.parseColor("#585859"));
            Drawable dra_sb = res.getDrawable(R.drawable.shock_sb);
            dra_sb.setBounds(0, 0, dra_sb.getMinimumWidth(),
                    dra_sb.getMinimumHeight());
            another.setCompoundDrawables(dra_sb, null, null, null);
            another.setTag("normal");
        }
    }

    private void sb_click(View v, TextView another) {
        type = 1;
        // shock_czzh_tex.performClick();
        TextView t = (TextView) v;
        if (t.getTag() != null && "selected".equals(t.getTag())) {

        } else {
            t.setTag("selected");
            t.setTextColor(Color.parseColor("#2498e3"));
            Resources res = getResources();
            Drawable dra = res.getDrawable(R.drawable.shock_sb_blue);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumHeight());
            t.setCompoundDrawables(dra, null, null, null);

            another.setTextColor(Color.parseColor("#585859"));
            Drawable dra_czzh = res.getDrawable(R.drawable.shock_czzh_gray);
            dra_czzh.setBounds(0, 0, dra_czzh.getMinimumWidth(),
                    dra_czzh.getMinimumHeight());
            another.setCompoundDrawables(dra_czzh, null, null, null);
            another.setTag("normal");
        }
    }

    private void initContainerSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        container.getLayoutParams().height = dm.widthPixels - 50;
        container.getLayoutParams().width = dm.widthPixels - 50;
        sc_height = dm.heightPixels;
    }

    private void rotateAnimation() {
        final RotateAnimation rotate_360 = new RotateAnimation(0, 360,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f);
        rotate_360.setDuration(2000);
        rotate_360.setFillAfter(true);

        AnimationSet set = new AnimationSet(true);
        RotateAnimation animation = new RotateAnimation(10, -10,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f);
        animation.setDuration(500);
        animation.setFillAfter(true);
        final RotateAnimation animation_turn = new RotateAnimation(-10, 30,
                Animation.RELATIVE_TO_PARENT, 0.5f,
                Animation.RELATIVE_TO_PARENT, 0.5f);
        animation_turn.setDuration(500);
        animation_turn.setStartOffset(500);
        animation_turn.setFillAfter(true);

        set.addAnimation(animation);
        set.addAnimation(animation_turn);
        shock_img.setAnimation(set);
        shock_img.startAnimation(set);

        animation_turn.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation arg0) {
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationEnd(Animation arg0) {
                if (flag) {
                    pDialog.show();
                    notifyServiceOfShock();
                }
            }
        });

    }

    private void translateAnimation() {
        TranslateAnimation up = new TranslateAnimation(0, 0, 0,
                0 - sc_height * 3 / 4);
        up.setInterpolator(new AccelerateInterpolator());
        up.setDuration(3000);
        up.setFillAfter(true);
        ballon.startAnimation(up);
        TranslateAnimation up_green = new TranslateAnimation(0, 0, 0,
                0 - sc_height + 50);
        up_green.setInterpolator(new DecelerateInterpolator());
        up_green.setDuration(3000);
        up_green.setFillAfter(true);
        ballon_green.startAnimation(up_green);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();
        // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
        float[] values = event.values;
        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            if ((Math.abs(values[0]) > 15 || Math.abs(values[1]) > 15 || Math
                    .abs(values[2]) > 15)) {
                // System.out.println("----values[0]==="+values[0]+"values[1]="+values[1]+"values[2]="+values[2]);
                requestTimes = 1;
                System.out.println("-----加速度-----");
                flag = true;
                rotateAnimation();
            }
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            // 旋转感应
            // System.out.println("----values[0]==="+values[0]+"values[1]="+values[1]+"values[2]="+values[2]);
            if (Math.abs(values[0] * 10) > 15) {
                flag = true;
                rotateAnimation();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void notifyServiceOfShock() {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
            return;
        }
        synchronized (lock) {
            new Thread() {
                public void run() {
                    requestService(true);
                }

                ;
            }.start();
        }
    }

    private void requestService(boolean b) {
        if (b) {
            if (sp.getBoolean("isvoice", true)) {
                SoundUtil.playSound(6);
            }
            if (sp.getBoolean("viberate", true)) {
                Util.Vibrate(NewShockToShock.this, 500);
            }
        }
        NewWebAPI.getNewInstance().GetShakeList(UserData.getUser().getUserId(),
                UserData.getUser().getMd5Pwd(), type + "", location_x + "",
                location_y + "", "", new WebRequestCallBack() {
                    @Override
                    public void success(Object obj) {
                        requestTimes++;
                        String result = obj.toString();
                        Message message = handler.obtainMessage();
                        message.obj = result;
                        if (!checkResult(result)) {
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (requestTimes < 3) {
                                requestService(false);
                            } else {
                                Util.show("暂未搜寻到其他用户", NewShockToShock.this);
                            }
                        }
                        handler.sendMessage(message);
                    }

                    @Override
                    public void fail(Throwable e) {
                        pDialog.cancel();
                        pDialog.dismiss();
                        requestService(false);
                    }
                });
    }

    private boolean checkResult(String result) {
        JSONObject obj = null;
        try {
            obj = JSON.parseObject(result.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        if (200 != obj.getInteger("code").intValue()) {
            return false;
        }
        JSONArray array = obj.getJSONArray("list");
        JSONObject[] objs = array.toArray(new JSONObject[]{});
        if (objs == null || objs.length == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mSensorManager == null) {
            initSensor();
        } else {
            mSensorManager.registerListener(this,
                    mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
}
