package com.mall.serving.filmticket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.mall.util.Util;
import com.mall.view.R;

public class CinemaMarker extends Activity implements OnMarkerClickListener, OnMapLoadedListener {
    private AMap aMap;
    private MapView mapView;
    private Marker marker;// 有跳动效果的marker对象
    private LatLng latlng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cinema_marker);
        mapView = (MapView) this.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState); // 此方法必须重写
        init();
    }

    private void init() {
        getIntentData();
        if (aMap == null) {
            aMap = mapView.getMap();
            setUpMap();
        }
        Util.initTitle(this, "酒店位置", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getIntentData() {
        String lat = this.getIntent().getStringExtra("latlng");
        try {
            latlng = new LatLng(Double.parseDouble(lat.split(",")[0]), Double.parseDouble(lat.split(",")[1]));
        } catch (Exception e) {
            latlng = new LatLng(0, 0);
        }

    }

    private void setUpMap() {
        aMap.setOnMarkerClickListener(this);// 设置点击marker事件监听器
        aMap.setOnMapLoadedListener(this);
        addMarkersToMap();// 往地图上添加marker
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap() {
        aMap.addMarker(new MarkerOptions().anchor(0.5f, 0.5f)
                .position(latlng).title("成都市")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker))
                .snippet("成都市:30.679879, 104.064855").draggable(true));
    }

    /**
     * 对marker标注点点击响应事件
     */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker.equals(marker)) {
            if (aMap != null) {
            }
        }
        return false;
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
    }

    @Override
    public void onMapLoaded() {
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(latlng).include(latlng)
                .include(latlng).build();
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20));
    }
}
