package com.mall.view;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.amap.api.location.AMapLocation;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.Bean.HomeBannerBean;
import com.mall.adapter.HomeBottomBannerPagerAdapter;
import com.mall.model.RecBussModel;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.newmodel.SharedPreferencesUtils;
import com.mall.scan.CaptureActivity;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.activity.QueryMainActivity;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.Map.LocationMarkerActivity;
import com.mall.view.carMall.CarShopActivity;
import com.squareup.picasso.Picasso;
import com.stx.xhb.xbanner.XBanner;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import me.relex.circleindicator.CircleIndicator;

/**
 * 功能： 远大商城手机客户端主页面<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */

@ContentView(R.layout.main)
public class Lin_MallActivity extends BaseActivity {

    private List<ImageView> imageViews = new ArrayList<ImageView>();
    private List<ImageView> imageViews1 = new ArrayList<ImageView>();
    @ViewInject(R.id.maingrid)
    private ListenedScrollView scrollView;
    @ViewInject(R.id.indicator)
    CircleIndicator indicator;
    @ViewInject(R.id.main_layout4_city)
    private TextView layout4City;
    @ViewInject(R.id.new_layout4_loadmore)
    private TextView layout4Loadmore;
    @ViewInject(R.id.main_layout1_sreach)
    private EditText main_layout1_sreach;
    @ViewInject(R.id.red_text)
    private TextView red_text;

    @ViewInject(R.id.listcont)
    private com.mall.util.ListViewForScrollView listcont;

    @ViewInject(R.id.backtop_iv)
    private ImageView backtop;


    List<RecBussModel.ListBean> shopMList = new ArrayList<>();

    RecAdapter recAdapter;

    @ViewInject(R.id.topbanner)
    private XBanner topbanner;

    @ViewInject(R.id.numberbustv)
    private TextView numberbus;

    @ViewInject(R.id.tomap)
    private View tomap;

    @ViewInject(R.id.inputline)
    private View inputline;

    @ViewInject(R.id.bottomviewpager)
    private ViewPager bottomviewpager;


    private BitmapUtils bmUtil;

    int tv_tuijian_count;

    //赵超修改
    private String DQCity_name;//当前城市名称

    public static List<RedPacket> list = new ArrayList();

    LayoutInflater layout;
    BitmapDisplayConfig config;

    private Context context;

    private boolean isScroll = true;

    private PositionService locationService;

    AMapLocation aMapLocation;

    private boolean getNewYear() {
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        SimpleDateFormat dateDay = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateDay.format(curDate);
        Log.e("当前时间", "day" + day);
        int num = Integer.parseInt(day.replace("-", ""));
        if (num >= 20180209 && num <= 20180223) {
            return true;
        }
        return false;
    }

    private ArrayList<HomeBannerBean> setTopbanner() {
        ArrayList<HomeBannerBean> homeBannerBeans = new ArrayList<>();
        homeBannerBeans.add(new HomeBannerBean(getNewYear() ? R.drawable.newyearbanner : R.drawable.topbanner1, getNewYear() ? "http://www.yda360.cn/News_show.asp?id=2239&typeid=1&ntypeid=23" : "http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view", null));
        homeBannerBeans.add(new HomeBannerBean(R.drawable.topbanner2, "http://a.app.qq.com/o/simple.jsp?pkgname=com.YdAlainMall.alainmall2", null));
        homeBannerBeans.add(new HomeBannerBean(R.drawable.topbanner3, "http://a.app.qq.com/o/simple.jsp?pkgname=com.cy666.activity", null));
        homeBannerBeans.add(new HomeBannerBean(R.drawable.topbanner4, null, PhoneFream.class));
        homeBannerBeans.add(new HomeBannerBean(R.drawable.topbanner5, null, QueryMainActivity.class));
        return homeBannerBeans;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("Lin_MallActivity", "onCreate");
        ViewUtils.inject(this);
        context = this;
        initView();
        if (autoSwitchTask == null) {
            autoSwitchTask = new AutoSwitchTask(Lin_MallActivity.this);
        }
        handler = new Handler();

        handler.removeCallbacks(autoSwitchTask);
        handler.postDelayed(autoSwitchTask, 4000);
        MyOnTouchListener onTouchListener = new MyOnTouchListener() {
            @Override
            public boolean onTouch(MotionEvent ev) {
                bottomviewpager.onTouchEvent(ev);
                return false;
            }
        };
        registerMyOnTouchListener(onTouchListener);

        Util.add(this);
        backtop.setVisibility(View.GONE);
        layout4Loadmore.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#EAEAEA"))
                .create());
        inputline.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#EAEAEA"))
                .create());

        scrollView.setOnScrollListener(new ListenedScrollView.OnScrollListener() {
            @Override
            public void onBottomArrived() {

            }

            @Override
            public void onScrollStateChanged(ListenedScrollView view, int scrollState) {

            }

            @Override
            public void onScrollChanged(int l, int t, int oldl, int oldt) {
                if (oldt > 3) {
                    backtop.setVisibility(View.VISIBLE);
                } else {
                    backtop.setVisibility(View.INVISIBLE);
                }
            }

        });
        backtop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.fullScroll(ScrollView.FOCUS_UP);

                    }
                });
            }
        });

        _mainActivity = (Lin_MainFrame) getParent();

        if (Web.test_url.equals(Web.url) || Web.test_url2.equals(Web.url)) {
//            Util.showAlert("当前是测试版本！！！！", this, new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Toast.makeText(Lin_MallActivity.this, "OnClick Called", Toast.LENGTH_LONG).show();
//                }
//            });
        }
        getLocation();
        recAdapter = new RecAdapter(context, shopMList);
        listcont.setAdapter(recAdapter);
        tomap.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                checkpermissions();
            }
        });
        bmUtil = new BitmapUtils(this);
        bmUtil.configDefaultLoadFailedImage(R.drawable.zw174);
        savecity = getSharedPreferences("city", MODE_PRIVATE)
                .getString("city", "深圳市");
        layout4City.setText(savecity);
        Log.e("城市保存1", "LL" + layout4City.getText().toString());
        Util.setSelectCity(layout4City.getText().toString(), true);


        getRecBusiness(false);
        Util.initTop(this, "", Integer.MIN_VALUE, null);

    }

    private void initView() {
        final ArrayList<HomeBannerBean> arrayList = setTopbanner();
        topbanner.setData(arrayList, null);
        topbanner.setmAdapter(new XBanner.XBannerAdapter() {

            @Override
            public void loadBanner(XBanner banner, Object model, View view, int position) {
                HomeBannerBean homeBannerBean = (HomeBannerBean) model;
                Picasso.with(context).load(homeBannerBean.getDrawble()).into((ImageView) view);
            }
        });
        topbanner.invalidate();

        topbanner.setOnItemClickListener(new XBanner.OnItemClickListener() {
            @Override
            public void onItemClick(XBanner banner, int position) {
                HomeBannerBean homeBannerBean = arrayList.get(position);
                if (homeBannerBean.getaClass() != null) {
                    Intent intent = new Intent(context, homeBannerBean.getaClass());
                    startActivity(intent);
                } else if (homeBannerBean.getUrl() != null) {
                    Util.openWeb(context, homeBannerBean.getUrl());
                }
            }
        });
    }


    VideoAudioDialog dialog;
    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            Log.e("Lin_MallActivity", "bind服务成功！");
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(final AMapLocation progress) {
                    Log.e("Lin_MallActivity", "定位监听" + progress.getCity());
                    if (progress != null) {
                        if (!Util.isNull(progress.getCity())) {
                            aMapLocation = progress;
                            if (!progress.getCity().equals(savecity)) {


                                if (dialog == null) {
                                    dialog = new VideoAudioDialog(context);
                                }
                                dialog.setTitle("温馨提示");
                                dialog.setLeft("取消");
                                dialog.setRight("切换");
                                String str = "是否切换到当前城市<font color=\"#FF5500\">" + progress.getCity() + "</font>";
                                dialog.setContent(Html.fromHtml(str));
                                dialog.setRight(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        // TODO Auto-generated method stub


                                        Log.e("地址切换", "2");
                                        layout4City.setText(getSharedPreferences("city", MODE_PRIVATE)
                                                .getString("city", "深圳市"));
                                        Util.SaveCity = getSharedPreferences("city", MODE_PRIVATE)
                                                .getString("city", "深圳市");
                                        savecity = getSharedPreferences("city", MODE_PRIVATE)
                                                .getString("city", "深圳市");
                                        Log.e("城市保存2", "LL" + layout4City.getText().toString());
                                        Util.setSelectCity(layout4City.getText().toString(), true);
                                        Util.citylong = progress.getLongitude() + "";
                                        Util.citylat = progress.getLatitude() + "";
                                        getRecBusiness(true);
                                    }
                                });

                                dialog.setLeft(new OnClickListener() {

                                    @Override
                                    public void onClick(View arg0) {
                                        // TODO Auto-generated method stub
                                        Log.e("地址切换", "1");
                                        layout4City.setText(progress.getCity());
                                        getSharedPreferences("city", MODE_PRIVATE).edit()
                                                .putString("city", progress.getCity()).commit();
                                        Util.SaveCity = progress.getCity();
                                        Util.citylong = progress.getLongitude() + "";
                                        Util.citylat = progress.getLatitude() + "";
                                        savecity = progress.getCity();
                                        Log.e("城市保存3", "LL" + layout4City.getText().toString());
                                        Util.setSelectCity(layout4City.getText().toString(), true);
                                        mainLayout4Page = 1;
                                        getRecBusiness(true);
                                    }
                                });
                                dialog.show();
                            } else {
                                Util.SaveCity = progress.getCity();
                                Util.citylong = progress.getLongitude() + "";
                                Util.citylat = progress.getLatitude() + "";
                                savecity = progress.getCity();
                                Log.e("城市保存4", "LL" + layout4City.getText().toString());
                                Util.setSelectCity(layout4City.getText().toString(), true);
                                mainLayout4Page = 1;
                                getRecBusiness(true);
                            }
                        } else {
                            layout4City.setText(getSharedPreferences("city", MODE_PRIVATE)
                                    .getString("city", "深圳市"));
                            Util.SaveCity = getSharedPreferences("city", MODE_PRIVATE)
                                    .getString("city", "深圳市");
                            savecity = getSharedPreferences("city", MODE_PRIVATE)
                                    .getString("city", "深圳市");
                            Log.e("城市保存5", "LL" + layout4City.getText().toString());
                            Util.setSelectCity(layout4City.getText().toString(), true);
                            Util.citylong = progress.getLongitude() + "";
                            Util.citylat = progress.getLatitude() + "";
                            getRecBusiness(true);
                        }
                    } else {
                        Toast.makeText(context, "没有获取到位置", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onError(AMapLocation error) {

                }

            });

        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (locationService != null) {
            locationService.stopLocation();
            context.getApplicationContext().unbindService(locationServiceConnection);
        }
        kk = 0;

    }

    @OnClick({R.id.t1, R.id.t2, R.id.t3, R.id.t4, R.id.t5, R.id.t6, R.id.t7,
            R.id.t8, R.id.t9, R.id.t10})
    public void layout4meishi(View v) {
        String[] key = null;
        String[] values = null;
        key = new String[]{"type", "cateid", "title"};
        values = new String[]{"cate", v.getTag() + "", ((TextView) v).getText().toString()};
        Util.showIntent(this, LMSJFrame.class, key, values);
    }

    @OnClick({R.id.main_layout1_speak})
    public void voiceSearch(View view) {
        final int[] num = {0, 0, 0};
        final String[] requestPermissionstr = {

                Manifest.permission.RECORD_AUDIO

        };
        RxPermissions rxPermissions = new RxPermissions(_mainActivity);
        rxPermissions.requestEach(requestPermissionstr)
                .subscribe(new Consumer<Permission>() {

                    @Override
                    public void accept(Permission permission) throws Exception {


                        if (permission.granted) {
                            Log.e("是否第一次打开app", "4");
                            num[0]++;
                            Log.e("是否第一次打开app", "同意权限");
                            if (num[0] == requestPermissionstr.length) {

                                Util.startVoiceRecognition(context, new DialogRecognitionListener() {

                                    @Override
                                    public void onResults(Bundle results) {
                                        ArrayList<String> rs = results != null ? results
                                                .getStringArrayList(RESULTS_RECOGNITION) : null;
                                        if (rs != null && rs.size() > 0) {
                                            String str = rs.get(0).replace("。", "").replace("，", "");
                                            main_layout1_sreach.setText(str);
                                        }

                                    }
                                });


                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                            num[1]++;
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                            if (num[1] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                            num[2]++;
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                            if (num[2] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                                try {
                                    Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                    Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent1);
                                } catch (Exception e2) {
                                    // TODO: handle exception
                                }

                            }

                        }


                    }
                });
    }

    Lin_MainFrame _mainActivity;
    String savecity = "";


    private void checkpermissions() {
        Lin_MainFrame _mainActivity = (Lin_MainFrame) getParent();
        Log.e("是否第一次打开app", "1");
        final int[] num = {0, 0, 0};
        final String[] requestPermissionstr = {

                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

        };
        Log.e("是否第一次打开app", "2");
        RxPermissions rxPermissions = new RxPermissions(_mainActivity);
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

                                Util.showIntent(Lin_MallActivity.this, LocationMarkerActivity.class);

                            }

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                            num[1]++;
                            Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                            if (num[1] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                            }
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                            num[2]++;
                            Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                            if (num[2] == 1) {
                                com.mall.util.Util.show("请允许应用权限请求...");

                                try {
                                    Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                    Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                    startActivity(intent1);
                                } catch (Exception e2) {
                                    // TODO: handle exception
                                }

                            }

                        }

                    }
                });
    }


    @OnClick({R.id.selectcity})
    public void click1(View view) {
        switch (view.getId()) {
            case R.id.selectcity:

                Intent intent = new Intent(this, CitySelectActivity.class);
                intent.putExtra("city", savecity);
                intent.putExtra("dqCity_name", layout4City.getText().toString());
                this.startActivityForResult(intent, CitySelectActivity._RESUESTCODE);

                break;

            default:
                break;
        }
    }

    int kk = 0;

    @Override
    protected void onResume() {
        if (kk > 0) {
            mainLayout4Page = 1;
            getRecBusiness(false);
        }
        kk++;
        Log.e("Lin_MallActivity", "onResume");
        // TODO Auto-generated method stub
        if (Util.checkLoginOrNot()) {

            Util.asynTask1(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    String result = (String) runData;
                    Log.e("---", result);
                    list.clear();
                    try {
                        JSONArray jsonArray = new JSONObject(result)
                                .getJSONArray("list");
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            RedPacket redPacket = gson.fromJson(
                                    jsonObject.toString(), RedPacket.class);
                            list.add(redPacket);
                        }
                        red_text.setVisibility(View.VISIBLE);
                        red_text.setText(Lin_MallActivity.list.size() + "");
                        if (Lin_MallActivity.list.size() > 99) {
                            red_text.setText(99 + "+");
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                @Override
                public Serializable run() {
                    // TODO Auto-generated method stub
                    Web web = new Web(Web.redurl, "/Get_RedPackage_Allday",
                            "userId=" + UserData.getUser().getUserId()
                                    + "&md5Pwd="
                                    + UserData.getUser().getMd5Pwd());
                    String result = web.getPlan();
                    return result;
                }

            });
        }
        if (null == UserData.getUser()) {
            red_text.setVisibility(View.GONE);
        }
        super.onResume();
    }


    public void getLocation() {
        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        context.getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);
    }


    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return imageViews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup arg0, int arg1) {
            arg0.addView(imageViews.get(arg1), LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);
            return imageViews.get(arg1);
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }
    }

    int mainLayout4ScrollViewY = 0;
    int mainLayout4Page = 1;
    String requestMethod = Web.getTuiJianShopMMByPage;
    String backCityId = "";
    private boolean isLoadLayout4 = false;



    public void click(View v) {
        switch (v.getId()) {
            case R.id.main_layout1_sbtn:
                String sValue = main_layout1_sreach.getText().toString();
                sreach(sValue);
                break;
            case R.id.c2:
                Util.showIntent(this, RedPackageActivity.class);
                break;
            case R.id.image:
                Util.showIntent(Lin_MallActivity.this, RedWallActivity.class);
                break;
            case R.id.carImage:

                boolean ischeck = (boolean) SharedPreferencesUtils.getParam(context, "jsyeHtmel", false);
                if (ischeck) {
                    Util.showIntent(context, CarShopActivity.class);
                    return;
                }
                SharedPreferencesUtils.setParam(context, "jsyeHtmel", true);
                Intent intent0 = new Intent(Lin_MallActivity.this, com.mall.view.carMall.WebViewActivity.class);
                intent0.putExtra("url", Web.imageip + "/phone/car/jsye.html");
                intent0.putExtra("title", "项目介绍");
                startActivity(intent0);
                break;

            case R.id.c1:
                Log.e("语音检查", "1");
                final int[] num = {0, 0, 0};
                final String[] requestPermissionstr = {
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA,
                };
                RxPermissions rxPermissions = new RxPermissions(_mainActivity);
                Log.e("语音检查", "2");
                rxPermissions.requestEach(requestPermissionstr)
                        .subscribe(new Consumer<Permission>() {
                            @Override
                            public void accept(Permission permission) throws Exception {
                                Log.e("语音检查", "3");
                                if (permission.granted) {
                                    num[0]++;
                                    Log.e("语音检查", "num[0]" + num[0]);
                                    if (num[0] == requestPermissionstr.length) {
                                        Util.showIntent(context, CaptureActivity.class);
                                    }
                                } else if (permission.shouldShowRequestPermissionRationale) {
                                    Log.e("语音检查", "4");
                                    Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                                    num[1]++;
                                    Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                                    if (num[1] == 1) {
                                        com.mall.util.Util.show("请允许应用权限请求...");

                                    }
                                } else {
                                    Log.e("语音检查", "5");
                                    // 用户拒绝了该权限，并且选中『不再询问』
                                    Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                                    num[2]++;
                                    Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                                    if (num[2] == 1) {
                                        com.mall.util.Util.show("请允许应用权限请求...");

                                        try {
                                            Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                            startActivity(intent1);
                                        } catch (Exception e2) {
                                            // TODO: handle exception
                                        }
                                    }
                                }
                            }
                        });
                break;
            case R.id.image1:
                Util.showIntent(Lin_MallActivity.this, StoreMainFrame.class);
                break;
            case R.id.new_layout4_loadmore:
                Util.showIntent(Lin_MallActivity.this, LMSJFrame.class, new String[]{"type", "orderby"}, new String[]{"orderby", "3"});
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("resultCode1", "resultCode" + resultCode);
        if (resultCode == CitySelectActivity._RESUESTCODE) {
            Log.e("当前城市1", "data.getStringExtra(\"name\")" + data.getStringExtra("name"));
            LogUtils.e(data.getStringExtra("id") + "___"
                    + data.getStringExtra("name"));

            //赵超修改
            DQCity_name = data.getStringExtra("dqCity_name");

            layout4City.setText(data.getStringExtra("name"));
            Log.e("城市保存6", "LL" + layout4City.getText().toString());
            Util.setSelectCity(layout4City.getText().toString(), true);
//			clearLMSJData();
            mainLayout4Page = 1;
            getRecBusiness(true);
        }
    }

    /**
     * 查询商家
     *
     * @param sValue 关键字
     */
    private void sreach(String sValue) {
        if (!Util.isNull(sValue) && !"".equals(sValue.trim())) {
            Intent intent = new Intent();
            intent.setClass(Lin_MallActivity.this, LMSJFrame.class);
            intent.putExtra("type", "search");
            intent.putExtra("keyword", sValue);
            Lin_MallActivity.this.startActivity(intent);
            main_layout1_sreach.setText("");
        } else
            Util.show("请输入您要查询的商家!", Lin_MallActivity.this);
    }

    private void getRecBusiness(final boolean isCityClick) {
        Log.e("getRecBusiness", "1");
        if (Util.isNull(Util.citylat)) {
            return;
        }
        Log.e("getRecBusiness", "2");
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", mainLayout4Page + "");
        params.put("size", "20");
        if (UserData.getUser() != null) {
            params.put("userId", UserData.getUser().getUserId());
            params.put("md5Pwd", UserData.getUser().getMd5Pwd());
        }
        String city = "";
        String call = "getHomeLMSJ";
        if (Util.isNull(savecity))
            call = "getTJLMSJ";// 没有定位获取推荐
        if (!isCityClick) {
            city = Util.isNull(layout4City.getText().toString()) ? savecity
                    : layout4City.getText().toString();
        } else {

            city = layout4City.getText().toString();
        }


        params.put("lat", Util.citylat + "");
        params.put("lon", Util.citylong
                + "");
        Log.e("getRecBusiness", "3");
        params.put("cName", city);
        mainLayout4Page++;
        Log.e("getRecBusiness", "4");
        NewWebAPI.getNewInstance().getWebRequest("/Alliance.aspx?call=" + call,
                params, new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", Lin_MallActivity.this);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON
                                .parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    Lin_MallActivity.this);
                            return;
                        }
                        Log.e("tag1", "1");

                        try {
                            tv_tuijian_count = Integer.parseInt(json
                                    .getString("message"));

                            Log.e("tag1", "a" + tv_tuijian_count);
                            numberbus.setText("(" + tv_tuijian_count + ")");
                        } catch (Exception e) {
                            // TODO: handle exception
                            numberbus.setText("(0)");
                        }
                        try {
                            if (tv_tuijian_count == 0) {
                                RecBussModel.ListBean m = new RecBussModel.ListBean();
                                m.setCate("暂无分类");
                                m.setId("-7");
                                m.setImg("http://" + Web.webImage
                                        + "/rebate/nodata.jpg");
                                m.setName("您的所在城市，暂无联盟商家");
                                m.setMm(Float.MAX_VALUE);
                                m.setPhone("400-666-3838");
                                shopMList.clear();
                                shopMList.add(m);
                                recAdapter.notifyDataSetChanged();
                                layout4Loadmore.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {

                        }
                        Log.e("tag1", "b");

                        Gson gson = new Gson();
                        Log.e("tag1", "c");



                        final RecBussModel recBussModel = gson.fromJson(
//                                str
                                result.toString()
                                , RecBussModel.class);
                        Log.e("tag1", "d" + mainLayout4Page);
                        if (mainLayout4Page == 2) {
                            shopMList.clear();

                        }

                        bottomviewpager.setAdapter(new HomeBottomBannerPagerAdapter(recBussModel.getAdv2(), context));
                        indicator.setViewPager(bottomviewpager);
                        if (recBussModel.getAdv2().size() == 1) {
                            indicator.setVisibility(View.INVISIBLE);
                        }
                        Log.e("tag1", "c");
                        shopMList.addAll(recBussModel.getList());
                        Log.e("tag1", "d" + shopMList.size());
                        recAdapter.notifyDataSetChanged();
                        Log.e("tag1", "e");
                        if (shopMList.size() == tv_tuijian_count) {
                            layout4Loadmore.setVisibility(View.GONE);
                        } else {
                            layout4Loadmore.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void fail(Throwable e) {
                        LogUtils.e("网络请求错误：", e);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("网络请求超时！");
//						Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                    }

                    public void requestEnd() {

                    }
                });
    }

    Handler handler;
    AutoSwitchTask autoSwitchTask;

    private static class AutoSwitchTask implements Runnable {
        private final WeakReference<Lin_MallActivity> mXBanner;

        private AutoSwitchTask(Lin_MallActivity mXBanner) {
            this.mXBanner = new WeakReference<>(mXBanner);
        }

        @Override
        public void run() {
            Lin_MallActivity banner = mXBanner.get();
            if (banner != null) {
                if (banner.bottomviewpager != null && banner.bottomviewpager.getAdapter() != null) {
                    if (banner.bottomviewpager.getCurrentItem() == banner.bottomviewpager.getAdapter().getCount() - 1) {
                        banner.bottomviewpager.setCurrentItem(0);
                    } else {
                        int currentItem = banner.bottomviewpager.getCurrentItem() + 1;
                        banner.bottomviewpager.setCurrentItem(currentItem);
                    }

                }
                banner.handler.postDelayed(banner.autoSwitchTask, 4000);

            }
        }
    }

    private ArrayList<MyOnTouchListener> onTouchListeners = new ArrayList<MyOnTouchListener>(
            10);

    public interface MyOnTouchListener {
        public boolean onTouch(MotionEvent ev);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        for (MyOnTouchListener listener : onTouchListeners) {
            if (listener != null) {
                listener.onTouch(ev);
                Log.e("toc", ev.getAction() + "LL");
                switch (ev.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        handler.removeCallbacks(autoSwitchTask);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_OUTSIDE:
                        handler.postDelayed(autoSwitchTask, 4000);
                        break;
                }

            }
        }
        return super.dispatchTouchEvent(ev);
    }

    public void registerMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.add(myOnTouchListener);
    }

    public void unregisterMyOnTouchListener(MyOnTouchListener myOnTouchListener) {
        onTouchListeners.remove(myOnTouchListener);
    }


    private String getMM(float distanceM) {
        String distanceValue = distanceM + "";
        if (distanceM > 1000F) {
            distanceValue = Util.getDouble(
                    Double.valueOf((distanceM / 1000F) + ""), 2)
                    + "公里";
        } else {
            distanceValue = Util.getDouble(Double.valueOf(distanceM + ""), 2)
                    + "米";
        }
        distanceValue = distanceValue.replaceFirst("\\.00", "");
        return distanceValue;
    }


}
