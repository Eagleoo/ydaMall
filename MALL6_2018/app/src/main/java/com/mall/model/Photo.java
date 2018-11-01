package com.mall.model;

/**
 * Created by Administrator on 2017/9/20.
 */

public class Photo {
    private String SmallUrl;

    public Photo(String smallUrl){
        this.SmallUrl=smallUrl;
    }

    public String getSmallUrl() {
        return SmallUrl;
    }

    public void setSmallUrl(String smallUrl) {
        SmallUrl = smallUrl;
    }
}
