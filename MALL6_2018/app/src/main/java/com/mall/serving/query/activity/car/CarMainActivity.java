package com.mall.serving.query.activity.car;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.adapter.CarMainAdapter;
import com.mall.serving.query.configs.QueryConfigs;
import com.mall.serving.query.model.CarResultInfo;
import com.mall.serving.query.model.CarString;
import com.mall.serving.query.model.WeatherInfo;
import com.mall.serving.query.model.WeatherInfo.Data.Weather;
import com.mall.view.App;
import com.mall.view.PositionService;
import com.mall.view.R;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.query_car_activity)
public class CarMainActivity extends BaseActivity {
    @ViewInject(R.id.top_center)
    private TextView top_center;
    @ViewInject(R.id.top_left)
    private TextView top_left;
    @ViewInject(R.id.top_right)
    private TextView top_right;
    @ViewInject(R.id.iv_center)
    private ImageView iv_center;

    @ViewInject(R.id.tv_1)
    private TextView tv_1;
    @ViewInject(R.id.tv_2)
    private TextView tv_2;
    @ViewInject(R.id.tv_date)
    private TextView tv_date;
    @ViewInject(R.id.tv_weather)
    private TextView tv_weather;
    @ViewInject(R.id.tv_stauts)
    private TextView tv_stauts;
    @ViewInject(R.id.add_car)
    private View add_car;
    @ViewInject(R.id.listview)
    private ListView listview;

    private List list;
    private String city = "北京";
    private CarMainAdapter adapter;

    public static final String TAG = "com.mall.serving.query.activity.car.CarMainActivity";
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
                    if (!Util.isNull(progress.getLocationType())){
                        city = progress.getCity().replaceFirst("市", "");
                    }else {
                        city = "深圳";
                    }
                }

                @Override
                public void onError(AMapLocation error) {
                    city = "深圳";
                }
            });

            setView();
            weatherQuery();

        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

        list = new ArrayList();
        adapter = new CarMainAdapter(context, list);
        listview.setAdapter(adapter);
        listview.setDividerHeight(2);
        getCarList();

        SimpleDateFormat format = new SimpleDateFormat("MM月dd日 EEEE");
        String time = format.format(new Date());

        tv_date.setText(time);
        registerReceiverAtBase(new String[] { TAG });
    }

    private void setView() {
        top_center.setText("违章查询");
        top_left.setVisibility(View.VISIBLE);

        top_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location, 0, 0, 0);
        top_right.setText(city);
        top_right.setCompoundDrawablePadding(5);
        top_right.setVisibility(View.GONE);

    }

    private void weatherQuery() {
        AnimeUtil.startImageAnimation(iv_center);
        Util.asynTask(new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                // TODO Auto-generated method stub
                Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
                String message = map.get("message");

                String lists = map.get("list");
                if (TextUtils.isEmpty(lists)) {
                    return;
                }
                WeatherInfo info = JsonUtil.getPerson(lists, WeatherInfo.class);
                if (info != null) {

                    List<String> xiche = info.getData().getLife().getInfo().getXiche();
                    if (xiche != null && xiche.size() > 0) {
                        tv_stauts.setText(xiche.get(0) + "洗车");
                    }
                    List<Weather> weather = info.getData().getWeather();
                    if (weather != null) {
                        tv_1.setText(weather.get(0).getInfo().getDay().get(2) + "°");
                        tv_2.setText(weather.get(0).getInfo().getNight().get(2) + "°");
                        int weatherRid = QueryConfigs.getWeatherRid(weather.get(0).getInfo().getDay().get(0));
                        tv_weather.setCompoundDrawablesWithIntrinsicBounds(weatherRid, 0, 0, 0);
                        tv_weather.setText(weather.get(0).getInfo().getDay().get(1));
                    }

                }
                iv_center.setVisibility(View.GONE);

            }

            @Override
            public Serializable run() {
                // TODO Auto-generated method stub
                Web web = new Web("http://op.juhe.cn/onebox/weather/query?cityname=" + city
                        + "&key=0f176744e02379c77e43bdc8a1d1e4e4");
                return web.getPlan();
            }
        });
    }

    @OnClick({ R.id.add_car })
    public void click(View v) {
        switch (v.getId()) {
            case R.id.add_car:

                AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

                    @Override
                    public void start() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void repeat() {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void end() {

                        Util.showIntent(context, CarQueryActivity.class, new String[] { "city" }, new String[] { city });

                    }
                });

                break;

            default:
                break;
        }

    }

    private void getCarList() {

        Util.asynTask(new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {

                adapter.notifyDataSetChanged();

            }

            @Override
            public Serializable run() {

                list.clear();
                try {
                    List<CarString> mlist = DbUtils.create(App.getContext()).findAll(CarString.class);

                    if (mlist != null) {

                        for (CarString cityString : mlist) {

                            CarResultInfo person = JsonUtil.getPerson(cityString.getCity(), CarResultInfo.class);
                            if (person != null) {

                                String[] split = cityString.getMes().split(",");
                                if (split.length >= 2) {
                                    person.setClassno(split[0]);
                                    person.setEngineno(split[1]);
                                }

                                list.add(person);

                            }
                        }
                    }

                } catch (DbException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }
        });

    }

    @Override
    public void onReceiveBroadcast(Intent intent) {
        // TODO Auto-generated method stub
        super.onReceiveBroadcast(intent);
        if (intent.getAction().endsWith(TAG)) {
            getCarList();

        }
    }

}
