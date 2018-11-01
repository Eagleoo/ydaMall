package com.mall.serving.resturant;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkRouteResult;
import com.mall.util.IAsynTask;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;

//路线图
public class RouteMap extends Activity implements OnRouteSearchListener, OnMapLoadedListener, OnMarkerClickListener, OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter {
    private MapView mapView;
    private AMap amap;

    private int busMode = RouteSearch.BusDefault;
    private int drivingMode = RouteSearch.DrivingDefault;
    private int walkMode = RouteSearch.WalkDefault;
    private BusRouteResult busRouteResult;
    private DriveRouteResult driveRouteResult;
    private WalkRouteResult walkRouteResult;
    private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式

    private LatLonPoint startPoint;
    private LatLonPoint endPoint;
    private RouteSearch routeSearch;
    private LatLng start, end;
    private double startLat, startLng, endLat, endLng;
    private String busSearchCityName = "";
    private String rearchMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routemap);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        if (amap == null) {
            amap = mapView.getMap();
            registerListener();
        }
        init();
    }

    private void init() {
        getIntentData();
        Util.initTitle(this, "入住路线图", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                RouteMap.this.finish();
            }
        });
        addMarker();
        routeSearch = new RouteSearch(this);
        routeSearch.setRouteSearchListener(this);
        searchRouteResult(startPoint, endPoint);
    }

    private void addMarker() {
        amap.addMarker(new MarkerOptions()
                .anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                .title("您的位置")
                .position(start)
                .draggable(false));
        amap.addMarker(new MarkerOptions()
                .anchor(0.5f, 1)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_jiudian))
                .title("目标酒店")
                .position(end)
                .draggable(false));
    }

    private void getIntentData() {
        startLat = this.getIntent().getDoubleExtra("startLat", 0.00);
        startLng = this.getIntent().getDoubleExtra("startLng", 0.00);
        endLat = this.getIntent().getDoubleExtra("endLat", 0.00);
        endLng = this.getIntent().getDoubleExtra("endLng", 0.00);
        busSearchCityName = this.getIntent().getStringExtra("cityname");
        start = new LatLng(startLat, startLng);
        end = new LatLng(endLat, endLng);
        startPoint = new LatLonPoint(startLat, startLng);//出发地点的经纬度
        endPoint = new LatLonPoint(endLat, endLng);//目的地的经纬度
        rearchMode = this.getIntent().getStringExtra("rearchMode");//模式：公交、步行、驾车
        if (rearchMode.equals("1")) {
            busRoute();
        } else if (rearchMode.equals("2")) {
            drivingRoute();
        } else if (rearchMode.equals("3")) {
            walkRoute();
        }
    }

    private void searchRouteResult(LatLonPoint start, LatLonPoint end) {
        Util.asynTask(RouteMap.this, "正在获取路线，请稍候...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
            }

            @Override
            public Serializable run() {
                final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);
                if (routeType == 1) {// 公交路径规划
                    BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, busSearchCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
                    routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
                } else if (routeType == 2) {// 驾车路径规划
                    Log.i("RoteMap", "-------------------驾车路径搜寻-------------------");
                    DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
                    routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
                } else if (routeType == 3) {// 步行路径规划
                    WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
                    routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
                }
                return "";
            }
        });
    }

    private void registerListener() {
        amap.setOnMapClickListener(RouteMap.this);
        amap.setOnMarkerClickListener(RouteMap.this);
        amap.setOnInfoWindowClickListener(RouteMap.this);
        amap.setInfoWindowAdapter(RouteMap.this);
    }

    /**
     * 选择公交模式
     */
    private void busRoute() {
        routeType = 1;// 标识为公交模式
        busMode = RouteSearch.BusDefault;

    }

    /**
     * 选择驾车模式
     */
    private void drivingRoute() {
        routeType = 2;// 标识为驾车模式
        drivingMode = RouteSearch.DrivingDefault;
    }

    /**
     * 选择步行模式
     */
    private void walkRoute() {
        routeType = 3;// 标识为步行模式
        walkMode = RouteSearch.WalkMultipath;
    }

    /**
     * 公交路线查询回调
     */
    @Override
    public void onBusRouteSearched(BusRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
//				busRouteResult = result;
//				BusPath busPath = busRouteResult.getPaths().get(0);
//				amap.clear();// 清理地图上的所有覆盖物
//				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, amap,
//						busPath, busRouteResult.getStartPos(),
//						busRouteResult.getTargetPos());
//				routeOverlay.removeFromMap();
//				routeOverlay.addToMap();
//				routeOverlay.zoomToSpan();
            }
        }
    }

    /**
     * 驾车结果回调
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
//				driveRouteResult = result;
//				DrivePath drivePath = driveRouteResult.getPaths().get(0);
//				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
//						this, amap, drivePath, driveRouteResult.getStartPos(),
//						driveRouteResult.getTargetPos());
//				drivingRouteOverlay.removeFromMap();
//				drivingRouteOverlay.addToMap();
//				drivingRouteOverlay.zoomToSpan();
            }
        }
    }

    /**
     * 步行路线结果回调
     */
    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
        if (rCode == 0) {
            if (result != null && result.getPaths() != null
                    && result.getPaths().size() > 0) {
//				walkRouteResult = result;
//				WalkPath walkPath = walkRouteResult.getPaths().get(0);
//				amap.clear();// 清理地图上的所有覆盖物
//				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this,
//						amap, walkPath, walkRouteResult.getStartPos(),
//						walkRouteResult.getTargetPos());
//				walkRouteOverlay.removeFromMap();
//				walkRouteOverlay.addToMap();
//				walkRouteOverlay.zoomToSpan();
            }
        }
    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onMapLoaded() {
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public void onMapClick(LatLng arg0) {
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker.isInfoWindowShown()) {
            marker.hideInfoWindow();
        } else {
            marker.showInfoWindow();
        }
        return false;
    }

    @Override
    public View getInfoContents(Marker arg0) {
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        return null;
    }
}
