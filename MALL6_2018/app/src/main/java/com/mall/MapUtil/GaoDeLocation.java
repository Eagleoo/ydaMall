package com.mall.MapUtil;

import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mall.util.Util;
import com.mall.view.App;


/**
 * Created by Administrator on 2018/5/16.
 */

public class GaoDeLocation implements AMapLocationListener, LocationControl {
    private static volatile GaoDeLocation instance;
    private AMapLocationClient locationClient = null;


    public static GaoDeLocation getLocationUtil() {
        if (instance == null) {
            synchronized (AMapLocationUtil.class) {
                if (instance == null) {
                    instance = new GaoDeLocation();
                }
            }
        }
        return instance;
    }

    private GaoDeLocation() {
        initLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location) {

            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            LocationMode locationMode = new LocationMode();

            if (location.getErrorCode() == 0) {

                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                locationMode.setLongitude(location.getLongitude());
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                locationMode.setLatitude(location.getLatitude());
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                locationMode.setCountry(location.getCountry());
                sb.append("省            : " + location.getProvince() + "\n");
                locationMode.setProvince(location.getProvince());
                sb.append("市            : " + location.getCity() + "\n");
                locationMode.setCity(location.getCity());
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                locationMode.setCityCode(location.getCityCode());
                sb.append("区            : " + location.getDistrict() + "\n");
                locationMode.setDistrict(location.getDistrict());
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                locationMode.setDistrictCode(location.getDistrict());
                sb.append("地    址    : " + location.getAddress() + "\n");
                locationMode.setAddress(location.getAddress());
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + Util.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                locationMode.setErrorDetail(location.getLocationDetail());
                locationMode.setErrorInfo(location.getErrorInfo());
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
            }
            //定位之后的回调时间
            sb.append("回调时间: " + Util.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");
            locationMode.setLocationCode(location.getErrorCode());
            locationMode.setLocationTime(Util.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss"));
            //解析定位结果，
            String result = sb.toString();

            Log.e("定位结果KLK", "KLK" + result + "");
            if (mLoactionCall != null) {
                mLoactionCall.locationCall(locationMode);
            }

        } else {
            Util.show("定位失败！");
            Log.e("定位失败", "loc is null");
        }
        stopLocation();
    }


    /**
     * 初始化定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(App.getContext());
        AMapLocationClientOption locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(this);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    LocationInfoCallback mLoactionCall;


    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    @Override
    public void startLocation() {
        // 启动定位
        locationClient.startLocation();
    }


    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    @Override
    public void stopLocation() {
        Log.e("停止定位", "111");
        // 停止定位
        locationClient.stopLocation();
    }

    @Override
    public void destroyLocation() {
        if (null != locationClient) {
            locationClient.onDestroy();
            locationClient = null;
        }
    }

    @Override
    public void setmLoactionCall(LocationInfoCallback mLoactionCall) {
        this.mLoactionCall = mLoactionCall;
    }


}
