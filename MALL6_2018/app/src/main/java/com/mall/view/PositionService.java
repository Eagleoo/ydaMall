package com.mall.view;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.mall.model.LocationModel;
import com.mall.util.Util;

/**
 * Created by Administrator on 2017/12/19.
 */

public class PositionService extends Service implements AMapLocationListener {

    public static String TAG = "com.mall.view.PositionService";

    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    PositionBinder positionBinder;
    private  static AMapLocation mylocation;

    private  OngetPositionListener ongetPositionListener;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        if (positionBinder==null){
            positionBinder=new PositionBinder();
        }
        initLocation();
        return positionBinder;
    }

    public class  PositionBinder extends Binder {

        public PositionService getService() {
            return PositionService.this;
        }

    }


    public static AMapLocation getLocationLatlng() {
        return mylocation;
    }

    /**
     * 注册回调接口的方法，供外部调用
     * @param ongetPositionListener
     */
    public void setOnPositionListener(OngetPositionListener ongetPositionListener) {
        this.ongetPositionListener = ongetPositionListener;
    }

    /**
     * 服务器创建的时候调用
     */
    @Override
    public void onCreate() {
        super.onCreate();

        Log.e("PositionService","onCreate()");
    }

    /**
     * 服务启动的时候调用
     *
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("PositionService","onStartCommand()");

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 服务销毁的时候调用
     */
    @Override
    public void onDestroy() {
        Log.e("PositionService","onDestroy()");
        super.onDestroy();
        stopLocation();
    }


    /**
     * 初始化定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private void initLocation(){
        if (locationClient==null){
            //初始化client
            locationClient = new AMapLocationClient(this.getApplicationContext());

            locationOption = getDefaultOption();
            locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            locationClient.setLocationOption(locationOption);
            // 设置定位监听
            locationClient.setLocationListener(this);
        }

    }

    @Override
    public void onLocationChanged(AMapLocation location) {
        if (null != location) {
            this.mylocation=location;

            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if(location.getErrorCode() == 0){

                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");

                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                // 获取当前提供定位服务的卫星个数
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                //定位完成的时间
                sb.append("定位时间: " + Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n");
                LocationModel.setLocationModel(
                        new LocationModel.Builder()
                .setLocationType(location.getLocationType())
                        .setLatitude(location.getLatitude())
                        .setLongitude(location.getLongitude())
                        .setAccuracy(location.getAccuracy())
                        .setSpeed(location.getSpeed())
                        .setBearing(location.getBearing())
                        .setSatellites(location.getSatellites())
                        .setCountry(location.getCountry())
                        .setProvince(location.getProvince())
                        .setCity(location.getCity())
                        .setCityCode(location.getCityCode())
                        .setDistrict(location.getDistrict())
                        .setAdCode(location.getAdCode())
                        .setAddress(location.getAddress())
                        .setTime(Utils.formatUTC(location.getTime(), "yyyy-MM-dd HH:mm:ss") + "\n").build()
                );

                ongetPositionListener.onProgress(location);
                stopLocation();
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
                if(location.getErrorCode()==4){
                    stopLocation();
                }
                ongetPositionListener.onError(location);

            }
            //定位之后的回调时间
            sb.append("回调时间: " + Utils.formatUTC(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss") + "\n");

            //解析定位结果，
            String result = sb.toString();
//				tvResult.setText(result);
            Log.e("定位结果",result+"");

        } else {
            Util.show("定位失败！");
//				tvResult.setText("定位失败，loc is null");
            Log.e("定位失败","loc is null");
            ongetPositionListener.onError(null);
        }
    }

    /**
     * 默认的定位参数
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    private AMapLocationClientOption getDefaultOption(){
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

    /**
     * 开始定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    public void startLocation(){
        //根据控件的选择，重新设置定位参数
//		resetOption();
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }
    /**
     * 停止定位
     *
     * @since 2.8.0
     * @author hongming.wang
     *
     */
    public void stopLocation(){
        Log.e("停止定位","stopLocation"+(locationClient==null));
        if (locationClient!=null){
            locationClient.stopLocation();
        }
        // 停止定位

    }

    public interface OngetPositionListener {
        void onProgress(AMapLocation progress);
        void onError(AMapLocation error);
    }


}
