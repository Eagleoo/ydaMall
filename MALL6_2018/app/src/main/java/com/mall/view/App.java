package com.mall.view;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.NewWebAPI;
import com.mall.serving.query.activity.QueryMainActivity;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mob.MobApplication;
import com.pickerview.dao.DBManager;
import com.way.note.data.NoteDataManager;
import com.way.note.data.NoteDataManagerImpl;
import com.yatoooon.screenadaptation.ScreenAdapterTools;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class App extends MobApplication
//implements Thread.UncaughtExceptionHandler
        implements Application.ActivityLifecycleCallbacks {
    public List<Bitmap> bitmaps = new ArrayList<>();
    private static Context context;
    private static Activity activity;
    private static User user = null;
    private DBManager dbHelper;
    private static NewWebAPI api = NewWebAPI.getNewInstance();
    private List<UserMessageBoard> list = new ArrayList<UserMessageBoard>();

    public void setUserMessageBoard(UserMessageBoard umb) {
        this.list.add(umb);
    }

    public List<UserMessageBoard> getList() {
        return list;
    }

    public List<Bitmap> getBitmaps() {
        return bitmaps;
    }

    public void setBitmaps(List<Bitmap> bitmaps) {
        this.bitmaps = bitmaps;
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        App.user = user;
    }

    public static NewWebAPI getApi() {
        return api;
    }

    public static void setApi(NewWebAPI api) {
        App.api = api;
    }

    public NoteDataManager getmDataManager() {
        return mDataManager;
    }

    public void setmDataManager(NoteDataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    public static void setContext(Context context) {
        App.context = context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenAdapterTools.init(this);
        // NBSAppAgent.setLicenseKey("cefc2ca272d44891b4897d9cc4d12296").withLocationServiceEnabled(true).start(this);
        context = getApplicationContext();
        registerActivityLifecycleCallbacks(this);
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        Util.version = Util.getVersion(context);
        Log.e("版本号检查 version", Util.version);
//		Thread.setDefaultUncaughtExceptionHandler(this);
        Intent intent = new Intent(context, PositionService.class);
        context.startService(intent);
//		context.startService(new Intent(LocationService.TAG));
//		SDKInitializer.initialize(getApplicationContext());

        //导入数据库
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();
        setupShortcuts();
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        ScreenAdapterTools.init(this);
    }

    private String[] lables = {"附近商家", "会员商城", "便民查询", "会员中心"};
    private Class[] classes = {LMSJFrame.class, StoreMainFrame.class, QueryMainActivity.class, UserCenterFrame.class};
    private int[] icons = {R.drawable.lable_1, R.drawable.lable_2, R.drawable.lable_3, R.drawable.lable_4};

    private void setupShortcuts() {
        ShortcutManager mShortcutManager = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N_MR1) {
            mShortcutManager = getSystemService(ShortcutManager.class);
            List<ShortcutInfo> infos = new ArrayList<>();
            for (int i = 0; i < lables.length; i++) {
                Intent intent = new Intent(this, classes[i]);
                intent.setAction(Intent.ACTION_VIEW);
                ShortcutInfo info = new ShortcutInfo.Builder(this, i + "")
                        .setShortLabel(lables[i])
                        .setLongLabel(lables[i])
                        .setIcon(Icon.createWithResource(this, icons[i]))
                        .setIntent(intent)
                        .build();
                infos.add(info);
            }
            mShortcutManager.setDynamicShortcuts(infos);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    private void initSensor() {
        // 获取传感器管理服务
        SensorManager mSensorManager = (SensorManager) this.getSystemService(Service.SENSOR_SERVICE);
        // 加速度传感器
        mSensorManager.registerListener(new SensorEventListener() {
                                            @Override
                                            public void onSensorChanged(SensorEvent event) {
                                                int sensorType = event.sensor.getType();
                                                // values[0]:X轴，values[1]：Y轴，values[2]：Z轴
                                                float[] values = event.values;
                                                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                                                    if ((Math.abs(values[0]) > 17 || Math.abs(values[1]) > 17 || Math.abs(values[2]) > 17)) {
                                                        Context context = AppManager.getNewInstance().currentActivity();
                                                        // 检测到晃动后启动OpenDoor效果
                                                        if (context instanceof MoneyToMoneyFrame) {
                                                            return;
                                                        }
                                                        if (null == UserData.getUser() || Util.isNull(UserData.getUser().getUserId()))
                                                            return;
                                                        Intent intent = new Intent(context, MoneyToMoneyFrame.class);
                                                        intent.putExtra("parentName", "商币转账");
                                                        intent.putExtra("userKey", "sb");
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                        context.startActivity(intent);
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                            }
                                        }, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                // 还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，
                // 根据不同应用，需要的反应速率不同，具体根据实际情况设定
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static Context getContext() {
        return context;
    }

    public static Activity getActivity() {
        return activity;
    }

    public static NewWebAPI getNewWebAPI() {
        return api;
    }

    private String getCrashReport(Throwable ex) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String expcetionStr = sw.toString();
        try {
            sw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        pw.close();
        return expcetionStr;
    }

//	@Override
//	public void uncaughtException(Thread thread, Throwable ex) {
//		ex.printStackTrace();
//
//		if (Util.isNetworkConnected(context)) {
//
//			Map<String, String> map = new HashMap<String, String>();
//			String brand = Util.getBrand();
//			String version = Util.version + "——" + Util.getRelease();
//			String model = Util.getModel();
//			String made = Util.getMake();
//			String eStr = getCrashReport(ex);
//			try {
//				FileOutputStream f = new FileOutputStream("/sdcard/yuanda/faillog.txt");
//				f.write(eStr.getBytes());
//				f.close();
//			} catch (FileNotFoundException e1) {
//				e1.printStackTrace();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//
//			map.put("id", new MD5().getMD5ofStr(brand + version + model + made + eStr));
//			map.put("threadName", thread.getName());
//			map.put("classLoader", thread.getContextClassLoader().toString());
//			map.put("message", ex.getMessage());
//			map.put("localizedMessage", eStr);
//			map.put("version", version);
//			map.put("brand", brand);
//			map.put("model", model);
//			map.put("made", made);
//			api.appException(map, new WebRequestCallBack() {
//				@Override
//				public void success(Object result) {
//				}
//
//				@Override
//				public void fail(Throwable e) {
//				}
//
//				@Override
//				public void timeout() {
//				}
//			});
//			new Thread(new Runnable() {
//				public void run() {
//					try {
//						Thread.currentThread().sleep(500);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					System.exit(0);
//				}
//			}).start();
//		} else
//			System.exit(0);
//
//	}

    NoteDataManager mDataManager = null;// 便签管理对象

    public synchronized NoteDataManager getNoteDataManager(Context context) {
        if (mDataManager == null) {
            mDataManager = NoteDataManagerImpl.getNoteDataManger(context);
            mDataManager.initData(context);
        }
        return mDataManager;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        Log.e("onActivityStarted", "Start");
        this.activity = activity;
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

        Log.e("结束", "activity" + activity.toString());

    }
}
