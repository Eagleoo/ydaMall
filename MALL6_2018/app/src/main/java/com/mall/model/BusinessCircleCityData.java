package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by Administrator on 2017/9/25.
 */

public class BusinessCircleCityData implements java.io.Serializable{

    @Id
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String uptime;

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }
}
