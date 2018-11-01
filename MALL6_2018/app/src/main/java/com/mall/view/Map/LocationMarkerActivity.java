package com.mall.view.Map;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.lidroid.xutils.util.LogUtils;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.LMSJCity;
import com.mall.model.LocationModel;
import com.mall.model.ShopM;
import com.mall.net.Web;
import com.mall.serving.query.activity.oilprice.BusRouteActivity;
import com.mall.serving.query.activity.oilprice.WalkRouteActivity;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.CitySelectActivity;
import com.mall.view.LMSJDetailFrame;
import com.mall.view.LMSJFrame;
import com.mall.view.PositionService;
import com.mall.view.R;
import com.mall.view.SelectorFactory;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class LocationMarkerActivity extends BasicActivity
        implements
        LocationSource
        , AMap.InfoWindowAdapter
        , AMap.OnInfoWindowClickListener {

    @BindView(R.id.map)
    public MapView mapView;
    @BindView(R.id.topright2)
    public TextView topright2;
    @BindView(R.id.map_lmsj_cityList)
    public LinearLayout map_lmsj_cityList;
    @BindView(R.id.map_lmsj_cityList_scrollView)
    public ScrollView map_lmsj_cityList_scrollView;

    private String locationCity = "";
    private String selectedCityId = "";
    private AMap aMap;
    private AMapLocation aMapLocation;
    private OnLocationChangedListener mListener;
    private LatLng locationLatLng;
    private PositionService locationService;

    public static String NOWLOCTION="nowloction";

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("LocationMarkerActivity", "bind服务成功！");
            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation amapLocation) {

                    if (mListener != null && amapLocation != null) {
                        Log.e("定位成功后回调函数设置经纬度", "精度" + amapLocation.getLongitude() + "纬度" + amapLocation.getLatitude());
                        locationLatLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                        if (amapLocation != null
                                && amapLocation.getErrorCode() == 0) {
                            aMapLocation = amapLocation;
                            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                            locationCity = amapLocation.getCity();
                            topright2.setText(locationCity);
                            getShopLocation();
                            getCityList();
                        } else {
                            String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                            Log.e("AmapErr", errText);
                        }
                    }
                }

                @Override
                public void onError(AMapLocation error) {
                    String errText = "定位失败,";
                    Log.e("AmapErr", errText);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @butterknife.OnClick({R.id.topright, R.id.topright2, R.id.topback})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.topback:
                finish();
                break;
            case R.id.topright:
                Util.showIntent(this, LMSJFrame.class);
                break;
            case R.id.topright2:
                Intent intent = new Intent(this, CitySelectActivity.class);
                intent.putExtra("city", Util.SaveCity);
                intent.putExtra("dqCity_name", topright2.getText().toString());
                this.startActivityForResult(intent, CitySelectActivity._RESUESTCODE);
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == CitySelectActivity._RESUESTCODE) {
            LogUtils.e(data.getStringExtra("id") + "___"
                    + data.getStringExtra("name"));
            selectedCityId = data.getStringExtra("id") + "";
            topright2.setText(data.getStringExtra("name"));

            getShopMByCity(selectedCityId);

        }
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_location_marker;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        String str="";
      try {
          Intent intent=getIntent();
           str=intent.getStringExtra(NOWLOCTION);
      }catch (Exception e){

      }

        topright2.setText(str);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    private void init() {
        initView();
    }

    private void initView() {

        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
    }

    /**
     * 获取异地商家
     *
     * @param cityId
     */
    private void getShopMByCity(final String cityId) {
        Util.asynTask(this, "正在加载中...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                List<ShopM> list = map.get("list");
                Log.e("异地商家", list.size() + "KL");
                if (null != list && list.size() != 0) {
                    aMap.clear();
                    for (ShopM m : list) {
                        addMarker(m);
                    }
                    if (0 != list.size()) {
                        ShopM m = list.get(0);
                        LatLng latlng = new LatLng(Util.getDouble(m.getPointY()), Util.getDouble(m.getPointX()));
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
                    }
                }


            }

            @Override
            public Serializable run() {
                String method = Web.getShopMByCity;
                String param = "page=" + 1 + "&size=" + (Integer.MAX_VALUE / 2) + "&zoneid=" + cityId + "&level=6";
                Web web = new Web(method, param);
                List<ShopM> list = web.getList(ShopM.class);
                HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                map.put("list", list);
                return map;
            }
        });
    }

    /**
     * 获取附近的商家
     */
    private void getShopLocation() {
        Util.asynTask(context, "正在获取附近的商家...",
                new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {

                        HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                        List<ShopM> list = map.get("list");
                        for (ShopM m : list) {
                            addMarker(m);
                        }

                        aMap.moveCamera(CameraUpdateFactory.zoomBy(6));

                    }

                    @Override
                    public Serializable run() {

                        if (!Util.isNull(locationCity)) {
                            Web web = new Web(Web.getShopMByCName,
                                    "page=" + 1 + "&size=" + 500
                                            + "&cName="
                                            + Util.get(locationCity));
                            List<ShopM> list = web.getList(ShopM.class);
                            HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                            map.put("list", list);
                            return map;
                        } else {
                            return null;
                        }

                    }
                });
    }

    /**
     * 得到城市列表
     */
    private void getCityList() {
        final int _5dp = Util.dpToPx(context, 5F);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<LMSJCity>> map = (HashMap<String, List<LMSJCity>>) runData;
                List<LMSJCity> list = map.get("list");
                TextView cate = null;
                for (LMSJCity mc : list) {
                    cate = new TextView(context);
                    cate.setText(mc.getName());
                    cate.setTag(mc.getId());
                    cate.setTag(-7, mc.getLevel());
                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.setMargins(_5dp * 2, 0, _5dp * 2, _5dp * 2);
                    cate.setLayoutParams(ll);
                    cate.setBackgroundDrawable(context.getResources()
                            .getDrawable(R.drawable.liner_border));
                    cate.setGravity(Gravity.CENTER_VERTICAL
                            | Gravity.CENTER_HORIZONTAL);
                    cate.setPadding(_5dp, _5dp, _5dp, _5dp);
                    cate.setBackground(SelectorFactory.newShapeSelector()
                            .setDefaultBgColor(Color.parseColor("#ffcccccc"))
                            .setStrokeWidth(Util.dpToPx(context, 1))
                            .setCornerRadius(Util.dpToPx(context, 3))
                            .create());

                    if (!Util.isNull(locationCity)) {
                        if (mc.getName().endsWith(locationCity)) {
                            cate.setBackground(SelectorFactory.newShapeSelector()
                                    .setDefaultBgColor(Color.parseColor("#ffffff"))
                                    .setStrokeWidth(Util.dpToPx(context, 1))
                                    .setCornerRadius(Util.dpToPx(context, 3))
                                    .create());
                        }
                    }
                    cate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedCityId = v.getTag() + "";
                            int length = map_lmsj_cityList.getChildCount();
                            for (int i = 0; i < length; i++) {
                                View v3 = map_lmsj_cityList.getChildAt(i);
                                if (v3 instanceof TextView)

                                    v3.setBackground(SelectorFactory.newShapeSelector()
                                            .setDefaultBgColor(Color.parseColor("#ffcccccc"))
                                            .setStrokeWidth(Util.dpToPx(context, 1))
                                            .setCornerRadius(Util.dpToPx(context, 3))
                                            .create());
                            }
                            v.setBackground(SelectorFactory.newShapeSelector()
                                    .setDefaultBgColor(Color.parseColor("#ffffff"))
                                    .setStrokeWidth(Util.dpToPx(context, 1))
                                    .setCornerRadius(Util.dpToPx(context, 3))
                                    .create());
                            String[] citys = ((TextView) v).getText().toString().split("-");
                            if (null == citys || 2 != citys.length)
                                citys = ((TextView) v).getText().toString().split(" ");
                            if (null == citys || 2 != citys.length)
                                return;
                            topright2.setText(citys[1]);
                            mapView.setVisibility(View.VISIBLE);
                            map_lmsj_cityList_scrollView.setVisibility(View.GONE);
                            map_lmsj_cityList.setVisibility(View.GONE);

                            getShopMByCity(selectedCityId);
                        }
                    });
                    cate.setTextColor(Color.parseColor("#535353"));
                    map_lmsj_cityList.addView(cate);
                }
                map_lmsj_cityList.invalidate();
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopMCity, "");
                HashMap<String, List<LMSJCity>> map = new HashMap<String, List<LMSJCity>>();
                List<LMSJCity> list = web.getList(LMSJCity.class);
                List<LMSJCity> newData = new ArrayList<LMSJCity>();
                Map<String, String> city = new HashMap<String, String>();
                // 去掉区级，只显示省市，并去掉重复数据
                for (LMSJCity c : list) {
                    String[] citys = c.getName().split(" ");
                    if (3 == citys.length)
                        c.setName(citys[0] + "-" + citys[1]);
                    else if (citys[0].contains("市"))
                        c.setName(citys[0] + "-" + citys[0]);
                    else if (2 == citys.length)
                        c.setName(citys[0] + "-" + citys[1]);
                    if (city.containsKey(c.getName()))
                        continue;
                    city.put(c.getName(), "1");
                    newData.add(c);
                }
                map.put("list", newData);
                return map;
            }
        });
    }


    private void addMarker(final ShopM m) {
        if (!Util.isDouble(m.getPointY()) || !Util.isDouble(m.getPointX()))
            return;
        LatLng latlng = new LatLng(Double.parseDouble(m.getPointY()),
                Double.parseDouble(m.getPointX()));
        int lmsjResId = -1;
        if ("餐饮美食".equals(m.getCate()))
            lmsjResId = R.drawable.marker_meishi;
        else if ("休闲娱乐".equals(m.getCate()))
            lmsjResId = R.drawable.marker_yule;
        else if ("摄影写真".equals(m.getCate()))
            lmsjResId = R.drawable.marker_xiezhen;
        else if ("美容美发".equals(m.getCate()))
            lmsjResId = R.drawable.marker_meirong;
        else if ("汽车养护".equals(m.getCate()))
            lmsjResId = R.drawable.marker_qiche;
        else if ("特惠购物".equals(m.getCate()))
            lmsjResId = R.drawable.marker_gouwu;
        else if ("酒店住宿".equals(m.getCate()))
            lmsjResId = R.drawable.marker_jiudian;
        else if ("旅游度假".equals(m.getCate()))
            lmsjResId = R.drawable.marker_dujia;
        else if ("家居建材".equals(m.getCate()))
            lmsjResId = R.drawable.marker_jiaju;
        else if ("咖啡茶饮".equals(m.getCate()))
            lmsjResId = R.drawable.marker_kafei;
        else if ("养生理疗".equals(m.getCate()))
            lmsjResId = R.drawable.marker_xiyu;
        else if ("健身运动".equals(m.getCate()))
            lmsjResId = R.drawable.marker_yundong;
        else if ("教育培训".equals(m.getCate()))
            lmsjResId = R.drawable.marker_peixun;
        else if ("网吧游戏".equals(m.getCate()))
            lmsjResId = R.drawable.marker_youxi;
        else if ("医疗服务".equals(m.getCate()))
            lmsjResId = R.drawable.marker_yaodian;
        else
            lmsjResId = R.drawable.marker_ohter;

        BitmapDescriptor icon = null;
        if (-1 != lmsjResId) {
            icon = BitmapDescriptorFactory.fromResource(lmsjResId);
        } else {
            icon = BitmapDescriptorFactory.defaultMarker();
        }

        Log.e("添加Marker", "1");

        Marker marker =
                aMap.addMarker(new MarkerOptions().position(latlng)
                        .title(m.getName()).snippet(m.getZone())
//                .perspective(true)
                        .icon(icon));
        Log.e("添加Marker", "2");
        marker.setObject(m);

    }


    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {


        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
        myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
        myLocationStyle.radiusFillColor(Color.argb(100, 0, 0, 180));// 设置圆形的填充颜色
        // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
        myLocationStyle.strokeWidth(1.0f);// 设置圆形的边框粗细
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setOnInfoWindowClickListener(this);// 设置点击marker事件监听器
        aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // aMap.setMyLocationType()


    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationService != null) {
            locationService.stopLocation();
            context.getApplicationContext().unbindService(locationServiceConnection);
        }

    }


    public void getLocation() {

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

    }


    /**
     * 激活定位
     */

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        Log.e("activate", "激活定位");
        getLocation();

    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (marker.getObject() instanceof ShopM) {
            View infoWindow = getLayoutInflater().inflate(
                    R.layout.map_lmsj_frame_marker_window_dialog, null);
            if (aMapLocation != null) {
                render(marker, infoWindow);
            }

            return infoWindow;
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        final ShopM m = (ShopM) marker.getObject();
        ImageView phone = (ImageView) view.findViewById(R.id.marker_phone);
        phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.doPhone(m.getPhone(), context);
            }
        });
        TextView titleUi = ((TextView) view.findViewById(R.id.marker_title));
        titleUi.setText(m.getName());
        ImageView daohang = ((ImageView) view.findViewById(R.id.marker_daohang));
        daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请选择导航模式");
                builder.setPositiveButton("驾车",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                int which) {
                                boolean isBaidu = Util
                                        .isInstall(context,
                                                "com.baidu.BaiduMap");
                                boolean isGaoDe = Util.isInstall(
                                        context,
                                        "com.autonavi.minimap");
                                boolean isGoogle = Util.isInstall(
                                        context,
                                        "com.google.android.apps.maps");
                                AlertDialog builder2 = new AlertDialog.Builder(
                                        context).create();
                                builder2.setTitle("请选择导航软件！");
                                LinearLayout root = new LinearLayout(
                                        context);
                                root.setOrientation(LinearLayout.VERTICAL);
                                Resources res = context
                                        .getResources();
                                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.FILL_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                int _5dp = Util.dpToPx(context, 5F);
                                ll.setMargins(_5dp, _5dp, _5dp, _5dp);
                                if (isBaidu) {
                                    TextView baiduMap = new TextView(
                                            context);
                                    baiduMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.baidu);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    baiduMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    baiduMap.setText("百度地图");
                                    baiduMap.setGravity(Gravity.CENTER_VERTICAL);
                                    baiduMap.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String lat = "";
                                            String lng = "";
                                            lat = aMapLocation
                                                    .getLatitude()
                                                    + "";
                                            lng = aMapLocation.getLongitude()

                                                    + "";
                                            try {
                                                Intent intent = Intent.getIntent("baidumap://map/direction?origin=latlng:"
                                                        + lat
                                                        + ","
                                                        + lng
                                                        + "|name:当前位置&destination=latlng:"
                                                        + m.getPointY()
                                                        + ","
                                                        + m.getPointX()
                                                        + "|name:"
                                                        + m.getName()
                                                        + " &mode=driving&region="
                                                        + aMapLocation
                                                        .getCity());
                                                context
                                                        .startActivity(intent);
                                            } catch (URISyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    root.addView(baiduMap);
                                }
                                if (isGaoDe) {
                                    TextView gaodeMap = new TextView(
                                            context);
                                    gaodeMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.gaode);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    gaodeMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    gaodeMap.setText("高德地图");
                                    gaodeMap.setGravity(Gravity.CENTER_VERTICAL);
                                    gaodeMap.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent = Intent
                                                        .getIntent("androidamap://navi?sourceApplication=mall666&poiname=&poiid=&lat="
                                                                + m.getPointY()
                                                                + "&lon="
                                                                + m.getPointX()
                                                                + "&dev=1&style=0");
                                                context
                                                        .startActivity(intent);
                                            } catch (URISyntaxException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                                    root.addView(gaodeMap);
                                }
                                if (isGoogle) {
                                    TextView gugeMap = new TextView(
                                            context);
                                    gugeMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.guge);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    gugeMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    gugeMap.setText("Google地图");
                                    gugeMap.setGravity(Gravity.CENTER_VERTICAL);
                                    gugeMap.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            String lat = "";
                                            String lng = "";
                                            lat = aMapLocation.getLatitude()
                                                    + "";
                                            lng = aMapLocation.getLongitude()

                                                    + "";
                                            Intent i = new Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="
                                                            + lat
                                                            + ","
                                                            + lng
                                                            + "&daddr="
                                                            + m.getPointY()
                                                            + ","
                                                            + m.getPointX()
                                                            + "&hl=zh"));
                                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                                            i.setClassName(
                                                    "com.google.android.apps.maps",
                                                    "com.google.android.maps.MapsActivity");
                                            context.startActivity(i);
                                        }
                                    });
                                    root.addView(gugeMap);
                                }
                                if (!isBaidu && !isGoogle && !isGaoDe) {
                                    com.mall.serving.community.util.Util.show("您还没有安装地图导航软件");
//                                    Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？",
//                                            context,
//                                            MapLMSJFrame.class, null,
//                                            new String[] { "dh", "mode" },
//                                            new String[] { m.getId(), "2" });
                                } else {
                                    builder2.setView(root);
                                    builder2.show();
                                }
                            }
                        });
                builder.setNegativeButton("步行",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                routeType = 3;
//                                doStartDaoHang(marker.getPosition());
                                LocationModel locationModel = LocationModel.getLocationModel();
                                final ShopM m = (ShopM) marker.getObject();
                                final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(), locationModel.getLongitude());
                                final LatLonPoint mEndPoint = new LatLonPoint(com.mall.serving.community.util.Util.getDouble(m.getPointY()), com.mall.serving.community.util.Util.getDouble(m.getPointX()));
                                Intent intent = new Intent(context, WalkRouteActivity.class);
                                intent.putExtra("mStartPoint", mStartPoint);
                                intent.putExtra("mEndPoint", mEndPoint);
                                startActivity(intent);
                            }
                        });
                builder.setNeutralButton("公交",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                routeType = 1;
//                                doStartDaoHang(marker.getPosition());
                                LocationModel locationModel = LocationModel.getLocationModel();
                                final ShopM m = (ShopM) marker.getObject();
                                final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(), locationModel.getLongitude());
                                final LatLonPoint mEndPoint = new LatLonPoint(com.mall.serving.community.util.Util.getDouble(m.getPointY()), com.mall.serving.community.util.Util.getDouble(m.getPointX()));
                                Intent intent = new Intent(context, BusRouteActivity.class);
                                intent.putExtra("mStartPoint", mStartPoint);
                                intent.putExtra("mEndPoint", mEndPoint);
                                startActivity(intent);
                            }
                        });
                builder.show();
            }
        });
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        if (null != marker.getObject()) {
            ShopM m = (ShopM) marker.getObject();
            Util.showIntent(this, LMSJDetailFrame.class, new String[]{"id",
                    "name", "x", "y"}, new String[]{m.getId(), m.getName(),
                    m.getPointX(), m.getPointY()});
        }
    }
}
