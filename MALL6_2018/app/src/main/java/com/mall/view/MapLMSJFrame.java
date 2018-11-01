package com.mall.view;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
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
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LMSJCity;
import com.mall.model.ShopM;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 功能： 联盟商家地图列表<br>
 * 时间： 2013-10-15<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class MapLMSJFrame extends AppCompatActivity implements OnMarkerClickListener,
        LocationSource, InfoWindowAdapter, OnMapLoadedListener,
        OnInfoWindowClickListener, OnCameraChangeListener,
        OnRouteSearchListener, OnMapClickListener, AMapLocationListener {
    private int page = 1;
    private int size = 500;
    @ViewInject(R.id.map_lmsj_map)
    private MapView mapView;

    private AMap aMap;
    private OnLocationChangedListener mListener;
    private List<Marker> markerList;

    // 导航
    private String cityCode = "";
    private String locationCity = "";
    private LatLng locationLatLng;
    private ProgressDialog progDialog = null;// 搜索时进度条
    private int busMode = RouteSearch.BusDefault;// 公交默认模式
    private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
    private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
    private RouteSearch routeSearch;
    private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
    private boolean isLoad = false;
    private boolean isQie = false; // 是否从列表切换过来的

    private Marker currShowMarker = null;

    private String selCity = "";
    private Map<Integer, BitmapDescriptor> cateBitmap = new HashMap<Integer, BitmapDescriptor>();

    // #start 头部右边选择城市
    @ViewInject(R.id.topright2)
    private TextView topright2;
    @ViewInject(R.id.map_lmsj_cityList_scrollView)
    private ScrollView map_lmsj_cityList_scrollView;
    @ViewInject(R.id.map_lmsj_cityList)
    private LinearLayout map_lmsj_cityList;
    private String selectedCityId = "";
    @ResInject(id = R.drawable.liner_border_white, type = ResType.Drawable)
    private Drawable liner_border_white;
    @ResInject(id = R.drawable.liner_border, type = ResType.Drawable)
    private Drawable liner_border;

    private Context context;

    private PositionService locationService;
    private AMapLocation aMapLocation;

    private ServiceConnection locationServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("MapLMSJFrame", "bind服务成功！");

            PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
            locationService = locationBinder.getService();
            locationService.startLocation();
            locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
                @Override
                public void onProgress(AMapLocation progress) {
                    aMapLocation = progress;
                    selCity = progress.getCity();
                    topright2.setText(selCity);
                }

                @Override
                public void onError(AMapLocation error) {

                }
            });


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.map_lmsj_frame);
        ViewUtils.inject(this);
        context = this;
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
//		requestPermissions();
    }


    private void init() {
        selCity = getIntent().getStringExtra("selCity");
        final int _5dp = Util.dpToPx(this, 5F);
        // 加载城市数据
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<LMSJCity>> map = (HashMap<String, List<LMSJCity>>) runData;
                List<LMSJCity> list = map.get("list");
                TextView cate = null;
                for (LMSJCity mc : list) {
                    cate = new TextView(MapLMSJFrame.this);
                    cate.setText(mc.getName());
                    cate.setTag(mc.getId());
                    cate.setTag(-7, mc.getLevel());
                    LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    ll.setMargins(_5dp * 2, 0, _5dp * 2, _5dp * 2);
                    cate.setLayoutParams(ll);
                    cate.setBackgroundDrawable(MapLMSJFrame.this.getResources()
                            .getDrawable(R.drawable.liner_border));
                    cate.setGravity(Gravity.CENTER_VERTICAL
                            | Gravity.CENTER_HORIZONTAL);
                    cate.setPadding(_5dp, _5dp, _5dp, _5dp);
                    cate.setBackgroundDrawable(liner_border);
                    if (!Util.isNull(selCity)) {
                        if (mc.getName().endsWith(selCity)) {
                            cate.setBackgroundDrawable(liner_border_white);
                        }
                    }
                    cate.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedCityId = v.getTag() + "";
                            int length = map_lmsj_cityList.getChildCount();
                            for (int i = 0; i < length; i++) {
                                View v3 = map_lmsj_cityList.getChildAt(i);
                                if (v3 instanceof TextView)
                                    v3.setBackgroundDrawable(liner_border);
                            }
                            v.setBackgroundDrawable(liner_border_white);
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
        markerList = new ArrayList<Marker>();
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setOnMarkerClickListener(this);
            aMap.setOnInfoWindowClickListener(this);// 设置点击marker事件监听器
            aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
            aMap.setOnCameraChangeListener(this);
            aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
            aMap.setOnMapClickListener(this);

            // 自定义系统定位小蓝点
            MyLocationStyle myLocationStyle = new MyLocationStyle();
            myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                    .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
            myLocationStyle.strokeWidth(2);// 设置圆形的边框粗细
            aMap.setMyLocationStyle(myLocationStyle);
            aMap.setLocationSource(this);// 设置定位监听
            aMap.getUiSettings().setScaleControlsEnabled(true); // 显示地图缩放比例
            aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        }
        final String urlid = this.getIntent().getStringExtra("dh");
        // 说明是从详细页点击进来导航的
        if (!Util.isNull(urlid)) {
            Util.asynTask(this, "正在获取商家信息...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    final ShopM m = (ShopM) runData;
                    addMarker(m);
                    String mode = MapLMSJFrame.this.getIntent().getStringExtra(
                            "mode");
                    routeType = Util.getInt(mode);
                    doStartDaoHang(new LatLng(
                            Double.parseDouble(m.getPointY()), Double
                            .parseDouble(m.getPointX())));
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getShopMID, "id=" + urlid);
                    return web.getObject(ShopM.class);
                }
            });
        }

        isQie = "1".equals(this.getIntent().getStringExtra("isQie"));

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        context.getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
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
        if (markerList != null) {
            for (Marker m : markerList) {
                m.destroy();
                m.remove();
            }
            markerList.clear();
        }

        if (locationService != null) {
            locationService.stopLocation();
            context.getApplicationContext().unbindService(locationServiceConnection);
        }


    }

    /**
     * 监听自定义infowindow窗口的infocontents事件回调
     */
    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    /**
     * 监听自定义infowindow窗口的infowindow事件回调
     */
    @Override
    public View getInfoWindow(Marker marker) {
        // 加上这个判断是为了避免，开始导航时，路口marker的提示窗体InfoWindow不出来
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

    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {
        final ShopM m = (ShopM) marker.getObject();
        ImageView phone = (ImageView) view.findViewById(R.id.marker_phone);
        phone.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.doPhone(m.getPhone(), MapLMSJFrame.this);
            }
        });
        TextView titleUi = ((TextView) view.findViewById(R.id.marker_title));
        titleUi.setText(m.getName());
        ImageView daohang = ((ImageView) view.findViewById(R.id.marker_daohang));
        daohang.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new Builder(MapLMSJFrame.this);
                builder.setTitle("请选择导航模式");
                builder.setPositiveButton("驾车",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                int which) {
                                boolean isBaidu = Util
                                        .isInstall(MapLMSJFrame.this,
                                                "com.baidu.BaiduMap");
                                boolean isGaoDe = Util.isInstall(
                                        MapLMSJFrame.this,
                                        "com.autonavi.minimap");
                                boolean isGoogle = Util.isInstall(
                                        MapLMSJFrame.this,
                                        "com.google.android.apps.maps");
                                AlertDialog builder2 = new Builder(
                                        MapLMSJFrame.this).create();
                                builder2.setTitle("请选择导航软件！");
                                LinearLayout root = new LinearLayout(
                                        MapLMSJFrame.this);
                                root.setOrientation(LinearLayout.VERTICAL);
                                Resources res = MapLMSJFrame.this
                                        .getResources();
                                LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.FILL_PARENT,
                                        LinearLayout.LayoutParams.WRAP_CONTENT);
                                int _5dp = Util.dpToPx(MapLMSJFrame.this, 5F);
                                ll.setMargins(_5dp, _5dp, _5dp, _5dp);
                                if (isBaidu) {
                                    TextView baiduMap = new TextView(
                                            MapLMSJFrame.this);
                                    baiduMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.baidu);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    baiduMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    baiduMap.setText("百度地图");
                                    baiduMap.setGravity(Gravity.CENTER_VERTICAL);
                                    baiduMap.setOnClickListener(new OnClickListener() {
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
                                                MapLMSJFrame.this
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
                                            MapLMSJFrame.this);
                                    gaodeMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.gaode);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    gaodeMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    gaodeMap.setText("高德地图");
                                    gaodeMap.setGravity(Gravity.CENTER_VERTICAL);
                                    gaodeMap.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            try {
                                                Intent intent = Intent
                                                        .getIntent("androidamap://navi?sourceApplication=mall666&poiname=&poiid=&lat="
                                                                + m.getPointY()
                                                                + "&lon="
                                                                + m.getPointX()
                                                                + "&dev=1&style=0");
                                                MapLMSJFrame.this
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
                                            MapLMSJFrame.this);
                                    gugeMap.setLayoutParams(ll);
                                    Drawable dra = res
                                            .getDrawable(R.drawable.guge);
                                    dra.setBounds(0, 0, dra.getMinimumWidth(),
                                            dra.getMinimumWidth());
                                    gugeMap.setCompoundDrawables(dra, null,
                                            null, null);
                                    gugeMap.setText("Google地图");
                                    gugeMap.setGravity(Gravity.CENTER_VERTICAL);
                                    gugeMap.setOnClickListener(new OnClickListener() {
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
                                            MapLMSJFrame.this.startActivity(i);
                                        }
                                    });
                                    root.addView(gugeMap);
                                }
                                if (!isBaidu && !isGoogle && !isGaoDe) {
                                    Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？",
                                            MapLMSJFrame.this,
                                            MapLMSJFrame.class, null,
                                            new String[]{"dh", "mode"},
                                            new String[]{m.getId(), "2"});
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
                                routeType = 3;
                                doStartDaoHang(marker.getPosition());
                            }
                        });
                builder.setNeutralButton("公交",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                routeType = 1;
                                doStartDaoHang(marker.getPosition());
                            }
                        });
                builder.show();
            }
        });
    }

    private void doStartDaoHang(final LatLng endLatlng) {
        if (null == locationLatLng) {
            Util.show("正在获取您的位置，请稍等...", MapLMSJFrame.this);
            Util.asynTask(new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if ("1".equals(runData + ""))
                        doStartDH(endLatlng);
                    else
                        Util.show(runData + "", MapLMSJFrame.this);
                }

                @Override
                public Serializable run() {
                    int i = 0;
                    while (i < 30) {
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } finally {
                            i++;
                            if (null != locationLatLng)
                                break;
                        }
                    }
                    return null == locationLatLng ? "获取定位失败！无法导航。" : "1";
                }
            });
        } else
            doStartDH(endLatlng);
    }

    /**
     * 开始导航
     *
     * @param endLatlng 目的地的经纬度
     */
    private void doStartDH(LatLng endLatlng) {
        showProgressDialog();
        LatLng location = locationLatLng;
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
        /* 您的经纬度 */
                new LatLonPoint(location.latitude, location.longitude),
        /* 您的目的地 */
                new LatLonPoint(endLatlng.latitude, endLatlng.longitude));
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        if (routeType == 1) {// 公交路径规划
            busMode = RouteSearch.BusDefault;
            BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode,
                    cityCode, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == 2) {// 驾车路径规划
            DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode,
                    null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == 3) {// 步行路径规划
            WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
            routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    private void addMarker(final ShopM m) {
        if (!Util.isDouble(m.getPointY()) || !Util.isDouble(m.getPointX()))
            return;
        // 同步锁，避免添加重复商家到地图上
        synchronized (MapLMSJFrame.this) {
            List<Marker> mList = new ArrayList<Marker>();
            for (Marker marker : markerList) {
                ShopM tempM = (ShopM) marker.getObject();
                if (null != tempM) {
                    if (tempM.getId().equals(m.getId()))
                        return;
                } else {
                    mList.add(marker);
                    marker.destroy();
                    marker.remove();
                }
            }
            if (0 != mList.size())
                markerList.removeAll(mList);
            mList.clear();
            mList = null;
        }
        LatLng latlng = new LatLng(Double.parseDouble(m.getPointY()),
                Double.parseDouble(m.getPointX()));
        int lmsjResId = -1;
        if ("餐厅美食".equals(m.getCate()))
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
        if (cateBitmap.containsKey(lmsjResId)) {
            icon = cateBitmap.get(lmsjResId);
        } else {
            if (-1 != lmsjResId)
                icon = BitmapDescriptorFactory.fromResource(lmsjResId);
            else
                icon = BitmapDescriptorFactory.defaultMarker();
            cateBitmap.put(lmsjResId, icon);
        }
        Marker marker = aMap.addMarker(new MarkerOptions().position(latlng)
                .title(m.getName()).snippet(m.getZone()).perspective(true)
                .icon(icon));
        marker.setObject(m);
        markerList.add(marker);
    }

    /**
     * 开始定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;

    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
    }

    /**
     * 监听amap地图加载成功事件回调
     */
    @Override
    public void onMapLoaded() {
        if (mListener != null) {

            locationLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
            cityCode = aMapLocation.getCityCode();
            locationCity = aMapLocation.getCity();
//			mListener.onLocationChanged(locationService.getLocation());// 显示系统小蓝点
            final String urlid = this.getIntent().getStringExtra("dh");
            // 定位成功之后获取附近的联盟商家
            if (!isLoad && Util.isNull(urlid)) {
                isLoad = true;
                Util.asynTask(MapLMSJFrame.this, "正在获取附近的商家...",
                        new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if (null == runData) {
                                    Util.show("没有获取到您的定位...", MapLMSJFrame.this);
                                    if (locationCity == null) {
                                        com.mall.util.Util.show("请打开定位权限...");
                                        try {
                                            Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                            Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                            startActivity(intent1);
                                        } catch (Exception e2) {
                                            // TODO: handle exception
                                        }
                                        finish();
                                    }

                                    return;
                                }
                                HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                                List<ShopM> list = map.get("list");
                                for (ShopM m : list)
                                    addMarker(m);
                                if (!isQie) {
                                    LatLng latlng = locationLatLng;
                                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                                } else {
                                    if (0 < list.size()) {
                                        ShopM m = list.get(0);
                                        LatLng remote = new LatLng(Util.getDouble(m.getPointY())
                                                , Util.getDouble(m.getPointX()));
                                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(remote, 10));
                                    }
                                }
                            }

                            @Override
                            public Serializable run() {
                                if (!Util.isNull(selCity))
                                    locationCity = selCity;

                                if (null != locationCity) {
                                    Web web = new Web(Web.getShopMByCName,
                                            "page=" + page + "&size=" + size
                                                    + "&cName="
                                                    + Util.get(locationCity));
                                    List<ShopM> list = web.getList(ShopM.class);
                                    HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                                    map.put("list", list);
                                    return map;
                                } else
                                    return null;
                            }
                        });
            }
        }
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

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown())
            marker.hideInfoWindow();
        else
            marker.showInfoWindow();
        currShowMarker = marker;
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cp) {
        // loadMoreShopM();
    }

    @Override
    public void onCameraChangeFinish(CameraPosition cp) {

    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        try {
            progDialog.show();
        } catch (Exception e) {

        }
    }

    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                BusRouteResult busRouteResult = result;
                BusPath busPath = busRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
//				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap,
//						busPath, busRouteResult.getStartPos(),
//						busRouteResult.getTargetPos());
//				routeOverlay.removeFromMap();
//				routeOverlay.addToMap();
//				routeOverlay.zoomToSpan();
            } else {
                Util.show("对不起，没有搜索到相关数据！", MapLMSJFrame.this);
            }
        } else {
            Util.show("搜索失败,请检查网络连接！", MapLMSJFrame.this);
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                DriveRouteResult driveRouteResult = result;
                DrivePath drivePath = driveRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物
//				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
//						this, aMap, drivePath, driveRouteResult.getStartPos(),
//						driveRouteResult.getTargetPos());
//				drivingRouteOverlay.removeFromMap();
//				drivingRouteOverlay.addToMap();
//				drivingRouteOverlay.zoomToSpan();
            } else {
                Util.show("对不起，没有搜索到相关数据！", MapLMSJFrame.this);
            }
        } else {
            Util.show("搜索失败,请检查网络连接！", MapLMSJFrame.this);
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        dissmissProgressDialog();
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
                WalkRouteResult walkRouteResult = result;
                WalkPath walkPath = walkRouteResult.getPaths().get(0);
                aMap.clear();// 清理地图上的所有覆盖物

//				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
//						aMap, walkPath, walkRouteResult.getStartPos(),
//						walkRouteResult.getTargetPos());
//				walkRouteOverlay.removeFromMap();
//				walkRouteOverlay.addToMap();
//				walkRouteOverlay.zoomToSpan();
            } else {
                Util.show("对不起，没有搜索到相关数据！", MapLMSJFrame.this);
            }
        } else {
            Util.show("搜索失败,请检查网络连接！", MapLMSJFrame.this);
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    public void onMapClick(LatLng l) {
        if (null != currShowMarker && currShowMarker.isVisible())
            currShowMarker.hideInfoWindow();
    }

    @OnClick(R.id.topright)
    public void topRightClick(View v) {
        if (Util.isNull(selCity))
            selCity = topright2.getText().toString();
        if (!topright2.getText().toString().equals(aMapLocation.getCity())) {
            selCity = topright2.getText().toString();
        }
        Util.showIntent(this, LMSJFrame.class);
    }

    @OnClick(R.id.topright2)
    public void topRight2Click(View v) {
        if (map_lmsj_cityList.getVisibility() == View.GONE) {
            map_lmsj_cityList_scrollView.setVisibility(View.VISIBLE);
            map_lmsj_cityList.setVisibility(View.VISIBLE);
            mapView.setVisibility(View.GONE);
        } else {
            mapView.setVisibility(View.VISIBLE);
            map_lmsj_cityList_scrollView.setVisibility(View.GONE);
            map_lmsj_cityList.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.topback)
    public void topback(View v) {
        finish();
    }

    private void getShopMByCity(final String cityId) {
        Util.asynTask(this, "正在加载中...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                List<ShopM> list = map.get("list");
                if (null == list)
                    list = new ArrayList<ShopM>();
                aMap.clear();
                // 自定义系统定位小蓝点
                MyLocationStyle myLocationStyle = new MyLocationStyle();
                myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.location_marker));// 设置小蓝点的图标
                myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
                myLocationStyle.strokeWidth(2);// 设置圆形的边框粗细
                aMap.setMyLocationStyle(myLocationStyle);
//				mListener.onLocationChanged(locationService.getLocation());// 显示系统小蓝点
                markerList.clear();
                for (ShopM m : list) {
                    addMarker(m);
                }
                if (0 != list.size()) {
                    ShopM m = list.get(0);
                    LatLng latlng = new LatLng(Util.getDouble(m.getPointY()), Util.getDouble(m.getPointX()));
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 10));
                }
            }

            @Override
            public Serializable run() {
                String method = Web.getShopMByCity;
                String param = "page=" + page + "&size=" + (Integer.MAX_VALUE / 2) + "&zoneid=" + cityId + "&level=6";
                Web web = new Web(method, param);
                List<ShopM> list = web.getList(ShopM.class);
                HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                map.put("list", list);
                return map;
            }
        });
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        Log.e("设置经纬度", "精度" + aMapLocation.getLongitude() + "纬度" + aMapLocation.getLatitude());

    }
}
