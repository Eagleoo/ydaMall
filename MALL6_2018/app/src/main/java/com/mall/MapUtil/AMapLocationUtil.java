package com.mall.MapUtil;

/**
 * Created by Administrator on 2018/5/16.
 */

public class AMapLocationUtil {

    public static String GAODEMODE = "GAODE";
    public LocationControl locationControl;

    public LocationControl initLocation(String modetag) {
        if (modetag.equals(GAODEMODE)) {
            locationControl = GaoDeLocation.getLocationUtil();
        }
        return locationControl;
    }


}
