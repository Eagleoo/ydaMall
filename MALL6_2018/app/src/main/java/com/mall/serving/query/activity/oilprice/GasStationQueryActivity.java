package com.mall.serving.query.activity.oilprice;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
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
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.LocationModel;
import com.mall.net.Web;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.textview.TextDrawable;
import com.mall.serving.query.model.OilPriceInfo;
import com.mall.view.PositionService;
import com.mall.view.R;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.functions.Consumer;

public class GasStationQueryActivity extends BasicActivity
        implements
        LocationSource
        ,AMap.InfoWindowAdapter
        , AMap.OnInfoWindowClickListener
{
    // View
    @BindView(R.id.top_center)
    public TextView top_center;
    @BindView(R.id.top_left)
    public TextView top_left;
    @BindView(R.id.top_right)
    public TextView top_right;
    @BindView(R.id.et_1)
    public EditText et_1;
    @BindView(R.id.map)
    public MapView mapView;
    private AMap aMap;

    private PositionService locationService;
    private AMapLocation aMapLocation;

    // 监听
    private OnLocationChangedListener mListener;

    //数据
    private String locationCity="";
    private LatLng locationLatLng;
    List<OilPriceInfo.Data> listAll = new ArrayList<OilPriceInfo.Data>();


    private ServiceConnection locationServiceConnection= new ServiceConnection() {
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
                        Log.e("定位成功后回调函数设置经纬度","精度"+amapLocation.getLongitude()+"纬度"+amapLocation.getLatitude());
                        locationLatLng =new LatLng(amapLocation.getLatitude(),amapLocation.getLongitude());
                        if (amapLocation != null
                                && amapLocation.getErrorCode() == 0) {
                            aMapLocation=amapLocation;
                            mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                            locationCity=amapLocation.getCity();
                            et_1.setText(locationCity);
                            getGasStationList(locationCity);

                        } else {
                            String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                            Log.e("AmapErr",errText);
                        }
                    }
                }

                @Override
                public void onError(AMapLocation error) {

                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    public int getContentViewId() {
        return R.layout.activity_gas_station_query;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();

    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        initview();
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
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {


        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(com.amap.api.maps2d.model.BitmapDescriptorFactory
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

    private void initview() {
        top_center.setText("加油站查询");
        top_left.setVisibility(View.VISIBLE);
        top_right.setVisibility(View.VISIBLE);
        top_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.query_oil_list, 0, 0, 0);
    }

   private void getGasStationList(String city){

           String url = "http://apis.juhe.cn/oil/region?key=2b40e756b00a23f9ac5d1fa53d32dbf0&format=2&city=" + city;
           oilpriceQuery(url, true);

   }
    private void oilpriceQuery(final String url, final boolean isCity) {
        Util.asynTask(context,"正在获取加油站列表...",new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                // TODO Auto-generated method stub
                Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
                String message = map.get("message");
                if (map.get("code").equals("200")) {
                    String lists = map.get("list");

                    OilPriceInfo info = JsonUtil.getPerson(lists, OilPriceInfo.class);
                    if (info == null) {
                        return;
                    }

                    List<OilPriceInfo.Data> mlist = info.getData();
                    if (mlist == null) {
                        return;
                    }

                    if (aMapLocation!=null) {
                        if (mlist==null&&mlist.size()==0){
                            return;
                        }
                        aMap.clear();
                        setUpMap();
                        for (OilPriceInfo.Data m : mlist) {
//							getOilPriceByLat(locationLatLng.longitude, locationLatLng.latitude);
                            double lon=Double.parseDouble(m.getLon());
                            double lat=Double.parseDouble(m.getLat());
                            com.amap.api.maps.model.LatLng remote = new com.amap.api.maps.model.LatLng(lat, lon);
                            float distanceM = AMapUtils.calculateLineDistance(
                                    new com.amap.api.maps.model.LatLng(aMapLocation.getLatitude(),aMapLocation.getLongitude()), remote);

//                            aMapLocation
                            m.setDistance(distanceM+"");
//
                            Log.e("经纬度","本地经纬度"+locationLatLng.longitude+"."+locationLatLng.latitude+"查询经纬度"+lon+"."+lat);

                            List<OilPriceInfo.Data.Price> lp = m.getPrice();
                            if (lp != null && lp.size() > 0) {
                                m.setPriceTemp(lp.get(0).getPrice());
                            }
                            addMarker(m);


                        }
                    }

                    listAll.clear();


                    listAll.addAll(mlist);

                    if(0 != listAll.size()){
                        OilPriceInfo.Data m = listAll.get(0);
                        LatLng latlng = new LatLng(com.mall.util.Util.getDouble(m.getLat()), com.mall.util.Util.getDouble(m.getLon()));
                        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 8));
                    }


                } else {

                }
            }

            @Override
            public Serializable run() {
                // TODO Auto-generated method stub
                Web web = new Web(url);
                return web.getPlan();
            }
        });
    }

    @OnClick({R.id.top_right,R.id.iv_1,R.id.tv_1})
    public void click(View view){
        switch (view.getId()){
            case R.id.top_right:
                Util.showIntent(context, OilPriceListActivity.class, new String[] { "info" },
                        new Serializable[] { (Serializable) listAll });
                Log.e("汽油类型",listAll.get(0).getAreaname()+"!!!!"+listAll.get(0).getId());
                break;
            case R.id.iv_1:

                checkpermissions(view);


                break;
            case R.id.tv_1:
                getGasStationList(et_1.getText().toString());
                break;
        }
    }

    public void getLocation() {

        Intent intent = new Intent();
        intent.setAction(PositionService.TAG);
        intent.setPackage(getPackageName());
        getApplicationContext().bindService(intent, locationServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    @Override
    public View getInfoWindow(Marker marker) {

        View infoWindow = getLayoutInflater().inflate(R.layout.query_oil_price_marker_window_dialog, null);

        render(marker, infoWindow);
        return infoWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }


    /**
     * 自定义infowinfow窗口
     */
    public void render(final Marker marker, View view) {

        TextView tv = ((TextView) view.findViewById(R.id.marker_title));

        final OilPriceInfo.Data m = (OilPriceInfo.Data) marker.getObject();

        tv.setText(m.getName());

        ImageView daohang = ((ImageView) view.findViewById(R.id.marker_daohang));
        daohang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Util.showIntent(context, OilPriceMainActivity.class, new String[] { "dh", "from" },
//                        new Serializable[] { data, 1 });


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("请选择导航模式");
                builder.setPositiveButton("驾车",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialog,
                                                int which) {
                                boolean isBaidu = com.mall.util.Util
                                        .isInstall(context,
                                                "com.baidu.BaiduMap");
                                boolean isGaoDe = com.mall.util.Util.isInstall(
                                        context,
                                        "com.autonavi.minimap");
                                boolean isGoogle = com.mall.util.Util.isInstall(
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
                                int _5dp = com.mall.util.Util.dpToPx(context, 5F);
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
                                                        + m.getLat()
                                                        + ","
                                                        + m.getLon()
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
                                                                + m.getLat()
                                                                + "&lon="
                                                                + m.getLon()
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
                                                            + m.getLat()
                                                            + ","
                                                            + m.getLon()
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
                                    Util.show("您还没有安装地图导航软件");
//                                    com.mall.util.Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？",
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



                                LocationModel locationModel=LocationModel.getLocationModel();
                                final OilPriceInfo.Data data = (OilPriceInfo.Data) marker.getObject();
                                final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(),locationModel.getLongitude());
                                final LatLonPoint mEndPoint = new LatLonPoint(Util.getDouble(data.getLat()),Util.getDouble(data.getLon()));
                                Intent intent=new Intent(context,WalkRouteActivity.class);
                                intent.putExtra("mStartPoint",mStartPoint);
                                intent.putExtra("mEndPoint",mEndPoint);
                                startActivity(intent);
//                                routeType = 3;
//                                doStartDaoHang(marker.getPosition());
                            }
                        });
                builder.setNeutralButton("公交",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
//                                routeType = 1;
//                                doStartDaoHang(marker.getPosition());

                                LocationModel locationModel=LocationModel.getLocationModel();
                                final OilPriceInfo.Data data = (OilPriceInfo.Data) marker.getObject();
                                final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(),locationModel.getLongitude());
                                final LatLonPoint mEndPoint = new LatLonPoint(Util.getDouble(data.getLat()),Util.getDouble(data.getLon()));
                                Intent intent=new Intent(context,BusRouteActivity.class);
                                intent.putExtra("mStartPoint",mStartPoint);
                                intent.putExtra("mEndPoint",mEndPoint);
                                startActivity(intent);
                            }
                        });
                builder.show();


            }
        });
    }


    /**
     * 激活定位
     */

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        Log.e("activate","激活定位");
        getLocation();
    }

    @Override
    public void deactivate() {

    }
    private void checkpermissions(final View view) {
        Log.e("是否第一次打开app","1");
        final int[] num = {0,0,0};
        final String[] requestPermissionstr = {

                Manifest.permission.RECORD_AUDIO

        };
        Log.e("是否第一次打开app","2");
        RxPermissions rxPermissions = new RxPermissions((Activity) context);
        Log.e("是否第一次打开app","3");

        rxPermissions.requestEach(requestPermissionstr)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {

                        if (permission.granted) {
                            Log.e("是否第一次打开app","4");
                            num[0]++;
                            Log.e("是否第一次打开app","同意权限");
                            if (num[0] == requestPermissionstr.length) {
                                AnimeUtil.startAnimation(context, view, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {

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

                                        Util.startVoiceRecognition(context, new DialogRecognitionListener() {

                                            @Override
                                            public void onResults(Bundle results) {
                                                ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION)
                                                        : null;
                                                if (rs != null && rs.size() > 0) {

                                                    et_1.setText(rs.get(0));

                                                }

                                            }
                                        });
                                    }
                                });

                            }

                        }else if(permission.shouldShowRequestPermissionRationale){
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            Log.e("权限检查","用户拒绝了该权限，没有选中『不再询问』");
                            num[1]++;
                            Log.e("权限检查","用户拒绝了该权限，没有选中『不再询问』"+num[1]);
                            if (num[1]==1){
                                com.mall.util.Util.show("请允许应用权限请求...");

                            }
                        }else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            Log.e("权限检查","用户拒绝了该权限，并且选中『不再询问");
                            num[2]++;
                            Log.e("权限检查","用户拒绝了该权限，并且选中『不再询问"+num[2]);
                            if (num[2]==1){
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

    private void addMarker(final OilPriceInfo.Data info) {

        String name = "";
        String lat = "";
        String lon = "";
        String city = "";

        lat = info.getLat();
        lon = info.getLon();
        city = info.getAreaname();
        name = info.getName();
        Log.e("地址数据",info.getName());
        if (!Util.isDouble(lon) || !Util.isDouble(lat))
            return;

//        synchronized (context) {
//            List<com.amap.api.maps.model.Marker> mList = new ArrayList<com.amap.api.maps.model.Marker>();
//            for (com.amap.api.maps.model.Marker marker : markerList) {
//                Object tempM = marker.getObject();
//                if (null != tempM) {
//                    if (tempM instanceof OilPriceInfo.Data) {
//                        OilPriceInfo.Data infoM = (OilPriceInfo.Data) tempM;
//
//                        if (info.getId().equals(infoM.getId()))
//                            return;
//                    }
//
//                } else {
//                    mList.add(marker);
//                    marker.destroy();
//                    marker.remove();
//                }
//            }
//            if (0 != mList.size())
//                markerList.removeAll(mList);
//            mList.clear();
//            mList = null;
//        }
        LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

        BitmapDescriptor icon = null;
        // if (cateBitmap.containsKey(1)) {
        // icon = cateBitmap.get(1);
        // } else {
        TextDrawable up = TextDrawable.builder().beginConfig().fontSize(20).endConfig().buildRound(info.getPriceTemp(),
                context.getResources().getColor(R.color.yellow));

        Drawable down = context.getResources().getDrawable(R.drawable.query_oil_local);

        Drawable drawable = getLayerDrawable(down, up);

        Bitmap bitmap = Util.drawable2Bitmap(drawable);

        icon = BitmapDescriptorFactory.fromBitmap(bitmap);
        // cateBitmap.put(1, icon);
        // }

        Marker marker = aMap.addMarker(new MarkerOptions().position(latlng).visible(true).title(name).snippet(city)
//                .perspective(true)
                .icon(icon));
        marker.setObject(info);

    }

    public LayerDrawable getLayerDrawable(Drawable down, Drawable up) {
        Drawable[] layerList = new Drawable[2];
        layerList[0] = down;
        layerList[1] = up;

        LayerDrawable layerDrawable = new LayerDrawable(layerList);
        layerDrawable.setLayerInset(1, 20, 20, 20, 40);
        return layerDrawable;
    }
}
