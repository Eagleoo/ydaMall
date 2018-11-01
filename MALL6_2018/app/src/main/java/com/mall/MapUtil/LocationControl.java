package com.mall.MapUtil;

/**
 * Created by Administrator on 2018/5/16.
 */

public interface LocationControl {
    public void startLocation();

    public void stopLocation();

    public void destroyLocation();

    public void setmLoactionCall(LocationInfoCallback mLoactionCall);
}
