package com.mall.serving.query.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.activity.Joke.JokeActivity;
import com.mall.serving.query.activity.calendar.CalendarActivity;
import com.mall.serving.query.activity.car.CarMainActivity;
import com.mall.serving.query.activity.constellation.ConstellationActivity;
import com.mall.serving.query.activity.cookbook.CookBookMainActivity;
import com.mall.serving.query.activity.expressage.ExpressageQueryActivity;
import com.mall.serving.query.activity.flight.FlightQueryActivity;
import com.mall.serving.query.activity.idcard.IDCardQueryActivity;
import com.mall.serving.query.activity.oilprice.GasStationQueryActivity;
import com.mall.serving.query.activity.postcode.PostCodeQueryActivity;
import com.mall.serving.query.activity.trainticket.TrainTicketQueryActivity;
import com.mall.serving.query.activity.weather.WeatherMainQueryActivity;
import com.mall.serving.voip.adapter.NewBaseAdapter;
import com.mall.util.Util;
import com.mall.view.Lin_MainFrame;
import com.mall.view.R;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

@ContentView(R.layout.query_main_activity)
public class QueryMainActivity extends BaseActivity {
    @ViewInject(R.id.top_center)
    private TextView top_center;
    @ViewInject(R.id.top_left)
    private TextView top_left;
    @ViewInject(R.id.top_right)
    private TextView top_right;
    @ViewInject(R.id.gridview)
    private GridView gridview;

    @ViewInject(R.id.iv_person)
    private ImageView iv_person;
    @ViewInject(R.id.iv_text1)
    private ImageView iv_text1;
    @ViewInject(R.id.iv_text2)
    private ImageView iv_text2;
    @ViewInject(R.id.iv_search)
    private ImageView iv_search;

    private List list;
    private int[] imgGrid = {R.drawable.query_train, R.drawable.query_flight, R.drawable.query_weather,
            R.drawable.joke, R.drawable.calendar, R.drawable.query_constellation, R.drawable.query_menu,
            R.drawable.query_idcard, R.drawable.query_postcode, R.drawable.query_oil, R.drawable.query_car,
            R.drawable.query_express};

    private String[] strGrid = {"火车票查询", "航班动态", "天气预报", "笑话大全", "万年历", "星座运势", "菜谱大全", "身份证查询", "邮编查询", "全国加油站油价",
            "全国车辆违章", "常用快递"};

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        ViewUtils.inject(this);

        list = new ArrayList();
        for (int i = 0; i < strGrid.length; i++) {
            GridInfo info = new GridInfo();
            info.setTitle(strGrid[i]);
            info.setRid(imgGrid[i]);
            list.add(info);
        }
        QueryAdater adapter = new QueryAdater(context, list);
        gridview.setAdapter(adapter);

        setView();
        int width = Util.getScreenWidth();
        TranslateAnimation moveRightAnimation = new TranslateAnimation(-width, 0f, 0f, 0f);
        TranslateAnimation moveLeftAnimation = new TranslateAnimation(width, 0f, 0f, 0f);

        moveRightAnimation.setDuration(1000);
        moveLeftAnimation.setDuration(1000);

        iv_text1.startAnimation(moveRightAnimation);
        iv_search.startAnimation(moveRightAnimation);
        iv_text2.startAnimation(moveLeftAnimation);
        AlphaAnimation alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(3000);
        iv_person.startAnimation(alpha);

    }

    private void setView() {
        Util.initTitle(this, "便民查询");
    }

    @OnItemClick(R.id.gridview)
    public void onItemClick(AdapterView<?> arg0, View arg1, int p, long arg3) {
        switch (p) {
            case 0:
                Util.showIntent(context, TrainTicketQueryActivity.class);
                break;
            case 1:
                Util.showIntent(context, FlightQueryActivity.class);
                break;
            case 2:
                Util.showIntent(context, WeatherMainQueryActivity.class);
                break;
            case 3:
//			Util.showIntent(context, BoxOfficeActivity.class);
                Util.showIntent(context, JokeActivity.class);
                break;
            case 4:
//			Util.showIntent(context, TravelMainActivity.class);
                Util.showIntent(context, CalendarActivity.class);// 万年历
//			Util.showIntent(context, TelevisionActivity.class);
                break;
            case 5:
                Util.showIntent(context, ConstellationActivity.class);
                break;
            case 6:
                Util.showIntent(context, CookBookMainActivity.class);
                break;
            case 7:
                Util.showIntent(context, IDCardQueryActivity.class);
                break;
            case 8:
                Util.showIntent(context, PostCodeQueryActivity.class);
                break;
            case 9:

                checkpermissions();

                //检查系统权限


                break;
            case 10:
                Util.showIntent(context, CarMainActivity.class);
                break;
            case 11:
                Util.showIntent(context, ExpressageQueryActivity.class);
                break;
        }

    }

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

                                Util.showIntent(context, GasStationQueryActivity.class);

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

    class QueryAdater extends NewBaseAdapter {

        public QueryAdater(Context c, List list) {
            super(c, list);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int p, View arg1, ViewGroup arg2) {
            TextView tv = new TextView(context);

            GridInfo info = (GridInfo) list.get(p);
            tv.setText(info.getTitle());
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, Util.dpToPx(QueryMainActivity.this,5), 0, Util.dpToPx(QueryMainActivity.this,10));
            tv.setTextSize(12);
            tv.setEllipsize(TruncateAt.END);
            tv.setSingleLine();
            tv.setTextColor(Color.BLACK);

            tv.setCompoundDrawablePadding(Util.dpToPx(QueryMainActivity.this,5));
            tv.setBackgroundResource(R.drawable.community_white_lightblue_selector);

            tv.setCompoundDrawablesWithIntrinsicBounds(0, info.getRid(), 0, 0);

            return tv;
        }

    }

    class GridInfo {
        private String title;
        private int rid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

    }

}
