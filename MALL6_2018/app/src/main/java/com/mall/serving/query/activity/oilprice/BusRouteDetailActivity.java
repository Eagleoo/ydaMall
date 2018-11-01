package com.mall.serving.query.activity.oilprice;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.overlay.BusRouteOverlay;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.mall.serving.community.view.textview.TextViewBack;
import com.mall.view.R;

/**
 * Created by Administrator on 2017/12/22.
 */

public class BusRouteDetailActivity extends Activity implements AMap.OnMapLoadedListener,
        AMap.OnMapClickListener, AMap.InfoWindowAdapter, AMap.OnInfoWindowClickListener, AMap.OnMarkerClickListener {
    private AMap aMap;
    private MapView mapView;
    private BusPath mBuspath;
    private BusRouteResult mBusRouteResult;
    private TextView
            mTitleBusRoute, mDesBusRoute;
    private ListView mBusSegmentList;
    private TextViewBack top_left;
    private BusSegmentListAdapter mBusSegmentListAdapter;
    private LinearLayout
            mBuspathview;
    private BusRouteOverlay mBusrouteOverlay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        mapView = (MapView) findViewById(R.id.route_map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        getIntentData();
        init();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            mBuspath = intent.getParcelableExtra("bus_path");
            mBusRouteResult = intent.getParcelableExtra("bus_result");
        }
    }

    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();

        top_left= (TextViewBack) findViewById(R.id.top_left);
        top_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBusRoute = (TextView) findViewById(R.id.firstline);
        mDesBusRoute = (TextView) findViewById(R.id.secondline);
        String dur = AMapUtil.getFriendlyTime((int) mBuspath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mBuspath.getDistance());
        mTitleBusRoute.setText(dur + "(" + dis + ")");
        int taxiCost = (int) mBusRouteResult.getTaxiCost();
        mDesBusRoute.setText("打车约"+taxiCost+"元");
        mDesBusRoute.setVisibility(View.VISIBLE);
        mBuspathview = (LinearLayout)findViewById(R.id.bus_path);
        configureListView();
    }

    private void registerListener() {
        aMap.setOnMapLoadedListener(this);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
    }

    private void configureListView() {
        mBusSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mBusSegmentListAdapter = new BusSegmentListAdapter(
                this.getApplicationContext(), mBuspath.getSteps());
        mBusSegmentList.setAdapter(mBusSegmentListAdapter);

    }

    public void onBackClick(View view) {
        this.finish();
    }

    public void onMapClick(View view) {
        mBuspathview.setVisibility(View.GONE);
        mapView.setVisibility(View.VISIBLE);
        aMap.clear();// 清理地图上的所有覆盖物
        mBusrouteOverlay = new BusRouteOverlay(this, aMap,
                mBuspath, mBusRouteResult.getStartPos(),
                mBusRouteResult.getTargetPos());
        mBusrouteOverlay.removeFromMap();

    }

    @Override
    public void onMapLoaded() {
        if (mBusrouteOverlay != null) {
            mBusrouteOverlay.addToMap();
            mBusrouteOverlay.zoomToSpan();
        }
    }

    @Override
    public void onMapClick(LatLng arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean onMarkerClick(Marker arg0) {
        // TODO Auto-generated method stub
        return false;
    }


}
