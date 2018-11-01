package com.mall.serving.resturant;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.Web;
import com.mall.officeonline.ShopOfficeList;
import com.mall.serving.redpocket.activity.RedPocketIndexActivity;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.PositionService;
import com.mall.view.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ResturantIndex extends Activity {
    private ViewPager banner;
    private List<View> bannersView;
    private TextView[] dots = null;
    private AtomicInteger what = new AtomicInteger();
    private boolean isContinue = true;
    private LinearLayout dotcontainer;
    private TextView check_in, check_out, resturant_address;
    private Button button;
    private EditText resturant_name;
    private String[] week = new String[]{"周日", "周一", "周二", "周三", "周四", "周五",
            "周六"};

    private String userId;
    private String md5Pwd = "";
    private User user;
    private UserInfo userInfo;
    private TextView city, price;

    private String cityName = "";
    private String cityId = "";
    private String check_in_time;
    private String check_out_time;
    private String price_Range = "三星 价格:100-590元";
    private String lsid = "", addkey = "";
    private LinearLayout voice_search;
    private double lat = 0.00;
    private double lng = 0.00;
    private String districtName = "";
    private ImageView resturant_name_image;
    private PositionService locationService;
    private SharedPreferences sp;
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
                    cityName = progress.getCity();
                    if (!TextUtils.isEmpty(cityName) && cityName.endsWith("市")) {
                        cityName = cityName.replace("市", "");
                    }
                    city.setText(cityName);
                    districtName = progress.getDistrict();
                    city.setTag(-7, districtName);
                    checkCity();
                    lat = progress.getLatitude();
                    lng = progress.getLongitude();
                }

                @Override
                public void onError(AMapLocation error) {
                    lat = 30.679879;
                    lng = 104.064855;
                }
            });
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resurantindex);

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(System.currentTimeMillis());
        int dayofwk = current.get(Calendar.DAY_OF_WEEK);
        int staticMonth = current.get(Calendar.MONTH);
        check_in_time = current.get(Calendar.YEAR) + "-" + (staticMonth + 1)
                + "-" + current.get(Calendar.DAY_OF_MONTH) + "  "
                + week[(dayofwk - 1)];
        int maxDaysOfCurrentMonth = Util.getMonthLastDay(
                current.get(Calendar.YEAR), current.get(Calendar.MONTH));
        if (current.get(Calendar.DAY_OF_MONTH) == maxDaysOfCurrentMonth) {
            if (dayofwk == 7) {
                check_out_time = current.get(Calendar.YEAR) + "-"
                        + (staticMonth + 2) + "-" + 1 + "  " + week[0];
            } else {
                check_out_time = current.get(Calendar.YEAR) + "-"
                        + (staticMonth + 2) + "-" + 1 + "  " + week[dayofwk];
            }
        } else {
            if (dayofwk == 7) {
                check_out_time = current.get(Calendar.YEAR) + "-"
                        + (staticMonth + 1) + "-"
                        + (current.get(Calendar.DAY_OF_MONTH) + 1) + "  "
                        + week[0];
            } else {
                check_out_time = current.get(Calendar.YEAR) + "-"
                        + (staticMonth + 1) + "-"
                        + (current.get(Calendar.DAY_OF_MONTH) + 1) + "  "
                        + week[dayofwk];
            }
        }
        sp = this.getSharedPreferences("resturantcities", 0);
        init();
    }

    private void init() {
        Util.initTitle(ResturantIndex.this, "订酒店", new OnClickListener() {
            @Override
            public void onClick(View v) {
                ResturantIndex.this.finish();
            }
        });
        resturant_name_image = (ImageView) this
                .findViewById(R.id.resturant_name_image);
        resturant_name_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(cityId)) {
                    Toast.makeText(ResturantIndex.this, "您还未选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(ResturantIndex.this,
                        ResurantName.class);
                intent.putExtra("from", "hotbrand");
                intent.putExtra("cityid", cityId);
                ResturantIndex.this.startActivityForResult(intent, 210);
            }
        });
        resturant_name = (EditText) this.findViewById(R.id.resturant_name);
        button = (Button) this.findViewById(R.id.submit);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(cityName)) {
                    Toast.makeText(ResturantIndex.this, "您还没有选择城市，请选择 ",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(ResturantIndex.this, HotelList.class);
                intent.putExtra("cityId", cityId);
                intent.putExtra("intime", check_in_time.split("  ")[0]);
                intent.putExtra("outtime", check_out_time.split("  ")[0]);
                intent.putExtra("pricerange", price_Range);
                intent.putExtra("hname", resturant_name.getText().toString());
                intent.putExtra("cityName", cityName);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                intent.putExtra("addkey", addkey);
                intent.putExtra("lsid", lsid);
                ResturantIndex.this.startActivity(intent);
            }
        });
        city = (TextView) this.findViewById(R.id.city);
        city.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResturantIndex.this,
                        ResturantCity.class);
                ResturantIndex.this.startActivityForResult(intent, 202);
            }
        });
        resturant_address = (TextView) this
                .findViewById(R.id.resturant_address);
        resturant_address.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(ResturantIndex.this,
                        ResurantName.class);
                if (Util.isNull(cityId)) {
                    Toast.makeText(ResturantIndex.this, "您还未选择城市", Toast.LENGTH_SHORT).show();
                    return;
                }
                intent.putExtra("cityid", cityId);
                intent.putExtra("from", "citybrand");
                ResturantIndex.this.startActivityForResult(intent, 206);
            }
        });
        price = (TextView) this.findViewById(R.id.resturan_price);
        price.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResturantIndex.this,
                        ResturantPrice.class);
                ResturantIndex.this.startActivityForResult(intent, 203);
            }
        });
        banner = (ViewPager) this.findViewById(R.id.resturant_banner);
        dotcontainer = (LinearLayout) this.findViewById(R.id.dot_container);
        initBannersView();
        initDotContainer();
        banner.setAdapter(new BannerAdapter());
        banner.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int arg0) {
                banner.setCurrentItem(arg0);
                for (int i = 0; i < dots.length; i++) {
                    if (arg0 == i) {
                        dots[i].setBackgroundColor(Color.parseColor("#f72828"));
                    } else {
                        dots[i].setBackgroundColor(Color.parseColor("#cac8c8"));
                    }

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        bannerHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        }).start();
        check_in = (TextView) this.findViewById(R.id.check_in_time);
        check_out = (TextView) this.findViewById(R.id.check_out_time);
        check_in.setText(check_in_time + "   入住");
        check_out.setText(check_out_time + "   退房");
        check_in.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResturantIndex.this,
                        MyDateWidget.class);
                intent.putExtra("comefrom", "ResturantIndex");
                startActivityForResult(intent, 200);
            }
        });
        voice_search = (LinearLayout) this.findViewById(R.id.voice_search);
        voice_search.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(
                            RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "请开始说话!");
                    startActivityForResult(intent, 205);
                } catch (ActivityNotFoundException e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            ResturantIndex.this);
                    builder.setTitle("语音识别");
                    builder.setMessage("您的手机暂不支持语音搜索功能，点击确定下载安装Google语音搜索软件");
                    builder.setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    Util.openWeb(ResturantIndex.this, "http://"
                                            + Web.voiceApk + "/voice.apk");
                                }
                            });
                    builder.setNegativeButton("取消", null);
                    builder.show();
                }

            }
        });
    }

    private void checkCity() {
        final Handler cityHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        };
        if (Util.isNull(sp.getString("resturantcity", ""))) {
            getCities();
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    String result = sp.getString("resturantcity", "");
                    jsonToObject(result);
                    Message m = new Message();
                    m.obj = cityId;
                    cityHandler.sendMessage(m);
                    Looper.loop();
                }
            }).start();
        }
    }

    private void getCities() {
        Util.asynTask(ResturantIndex.this, "正在获取城市数据...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                jsonToObject(result);
                sp.edit().putString("resturantcity", result).commit();
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.convience_service, Web.getAllCity, "");
                String result = web.getPlanGb2312();
                return result;
            }
        });
    }

    protected void jsonToObject(String result) {
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject myjObject = jsonArray.getJSONObject(i);
                String cName = myjObject.getString("cName");
                String cid = myjObject.getString("cid");
                if (cName.contains(city.getTag(-7).toString())) {
                    if (cName.contains(city.getText().toString())) {
                        cityId = cid;
                        break;
                    }
                } else {
                    if (cName.contains(city.getText().toString())) {
                        cityId = cid;
                        break;
                    }
                }
                if ("".equals(cityId) && !Util.isNull(cityName)) {
                    if (cName.startsWith(cityName)) {
                        cityId = cid;
                        break;
                    }
                    if (cityName.startsWith(cName)) {
                        cityId = cid;
                        break;
                    }
                }
            }
        } catch (JSONException e) {
        }
    }

    private Handler bannerHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            banner.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }

        ;
    };

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > bannersView.size() - 1) {
            what.getAndAdd(-3);
        }
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initBannersView() {
        bannersView = new ArrayList<View>();
        LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, 50);
        ImageView banner1 = new ImageView(this);
        banner1.setImageResource(R.drawable.jd_hb);
        banner1.setLayoutParams(lp);
        ImageView banner2 = new ImageView(this);
        banner2.setImageResource(R.drawable.jd_fkj);
        banner2.setLayoutParams(lp);
        banner1.setScaleType(ScaleType.CENTER_CROP);
        banner2.setScaleType(ScaleType.CENTER_CROP);
        banner1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == UserData.getUser()) {
                    Util.showIntent(ResturantIndex.this, LoginFrame.class);
                    return;
                }
                Util.showIntent(ResturantIndex.this, RedPocketIndexActivity.class);
            }
        });
        banner2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(ResturantIndex.this, ShopOfficeList.class);
            }
        });
        bannersView.add(banner1);
        bannersView.add(banner2);
    }

    private void initDotContainer() {
        dots = new TextView[bannersView.size()];
        for (int i = 0; i < bannersView.size(); i++) {
            TextView dot;
            if (i == 0) {
                dot = createDot();
                dot.setBackgroundColor(Color.parseColor("#f72828"));
                dotcontainer.addView(dot);
                dots[i] = dot;
            } else {
                dot = createDot();
                dot.setBackgroundColor(Color.parseColor("#cac8c8"));
                dotcontainer.addView(dot);
                dots[i] = dot;
            }
        }
    }

    private TextView createDot() {
        TextView te = new TextView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 5);
        params.setMargins(5, 0, 5, 0);
        te.setLayoutParams(params);
        return te;
    }

    public class BannerAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return bannersView.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            // super.destroyItem(container, position, object);
            ((ViewPager) container).removeView(bannersView.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(bannersView.get(position));
            return bannersView.get(position);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("requestCode", requestCode + "");
        if (data != null) {
            if (requestCode == 200) {
                if (!Util.isNull(data.getStringExtra("in"))
                        && !Util.isNull(data.getStringExtra("out"))
                        && !Util.isNull(data.getStringExtra("outweek"))
                        && !Util.isNull(data.getStringExtra("inweek"))) {
                    String intime = data.getStringExtra("in");
                    String outtime = data.getStringExtra("out");
                    String inweek = data.getStringExtra("inweek");
                    String outweek = data.getStringExtra("outweek");
                    check_in_time = intime;
                    check_out_time = outtime;
                    check_in.setText(intime + "    " + inweek + "  入住");
                    check_out.setText(outtime + "   " + outweek + "  退房");
                } else {
                    System.out.println("传递过来的值为空");
                }
            } else if (requestCode == 201) {
                if (!Util.isNull(data.getStringExtra("time"))
                        && !Util.isNull(data.getStringExtra("weekofday"))) {
                    check_out_time = data.getStringExtra("time");
                    String weekofday = data.getStringExtra("weekofday");
                    check_out.setText(check_out_time + "    "
                            + week[Integer.parseInt(weekofday) - 1]);
                }
            } else if (requestCode == 202) {
                if (!Util.isNull(data.getStringExtra("cityName"))
                        && !Util.isNull(data.getStringExtra("cityId"))) {
                    cityName = data.getStringExtra("cityName");
                    cityId = data.getStringExtra("cityId");
                    city.setText(cityName);
                }
            } else if (requestCode == 203) {
                if (!Util.isNull(data.getStringExtra("star_price"))
                        && !Util.isNull(data.getStringExtra("range_price"))) {
                    price_Range = data.getStringExtra("star_price") + " 价格:"
                            + data.getStringExtra("range_price");
                    price.setText(price_Range);
                }
            } else if (requestCode == 205) {
                ArrayList<String> result = data
                        .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result.size() > 0) {
                    resturant_name.setText(result.get(0));
                }
            } else if (requestCode == 206) {
                if (data.getStringExtra("addkey") != null
                        || data.getStringExtra("lsid") != null
                        || data.getStringExtra("name") != null) {
                    lsid = data.getStringExtra("lsid");
                    addkey = data.getStringExtra("addkey");
                    String name = data.getStringExtra("name");
                    System.out.println(name);
                    resturant_address.setText(name);
                } else {

                }
            } else if (requestCode == 210) {
                if (data.getStringExtra("addkey") != null
                        || data.getStringExtra("lsid") != null
                        || data.getStringExtra("name") != null) {
                    lsid = data.getStringExtra("lsid");
                    addkey = data.getStringExtra("addkey");
                    String name = data.getStringExtra("name");
                    System.out.println(name);
                    resturant_name.setText(name);
                } else {

                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
