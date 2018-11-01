package com.mall.Bean;

/**
 * Created by Administrator on 2018/1/15.
 */

public class HomeBannerBean {
  int drawble;
  String url;
  Class aClass;

    public int getDrawble() {
        return drawble;
    }

    public void setDrawble(int drawble) {
        this.drawble = drawble;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Class getaClass() {
        return aClass;
    }

    public void setaClass(Class aClass) {
        this.aClass = aClass;
    }

    public HomeBannerBean(int drawble, String url, Class aClass) {
        this.drawble = drawble;
        this.url = url;
        this.aClass = aClass;
    }
}
