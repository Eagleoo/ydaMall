package com.mall.util;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/12/28.
 */

public class NetMessageEvent implements Serializable {
    private int netType;

    public NetMessageEvent(int netType) {
        this.netType = netType;
    }

    public int getNetType() {
        return netType;
    }

    public void setNetType(int netType) {
        this.netType = netType;
    }
}