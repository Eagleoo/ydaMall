package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by Administrator on 2017/9/25.
 */

public class BusinessCircleCityName implements java.io.Serializable{

    @Id
     private String zoneid;
    private String sq_name;
    private  String count_;
    private String renqi;

    public String getRenqi() {
        return renqi;
    }

    public void setRenqi(String renqi) {
        this.renqi = renqi;
    }

    public String getZoneid() {
        return zoneid;
    }

    public void setZoneid(String zoneid) {
        this.zoneid = zoneid;
    }

    public String getSq_name() {
        return sq_name;
    }

    public void setSq_name(String sq_name) {
        this.sq_name = sq_name;
    }

    public String getCount_() {
        return count_;
    }

    public void setCount_(String count_) {
        this.count_ = count_;
    }
}
