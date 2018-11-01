package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

/**
 * Created by Administrator on 2017/12/4.
 */

public class BannerInfo implements java.io.Serializable{


    /**
     * id : 34
     * url_android : s
     * url_iPhone : wwww
     * cate : 2
     * image : http://test.mall666.cn/user/Annex/130906569055263907.png
     */
    @Id
    private String id;
    private String url_android;
    private String url_iPhone;
    private String cate;
    private String image;
    private String path;

    @Override
    public String toString() {
        return "BannerInfo{" +
                "id='" + id + '\'' +
                ", url_android='" + url_android + '\'' +
                ", url_iPhone='" + url_iPhone + '\'' +
                ", cate='" + cate + '\'' +
                ", image='" + image + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public BannerInfo(){

    }

    public BannerInfo(String id, String url_android, String url_iPhone, String cate, String image) {
        this.id = id;
        this.url_android = url_android;
        this.url_iPhone = url_iPhone;
        this.cate = cate;
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl_android() {
        return url_android;
    }

    public void setUrl_android(String url_android) {
        this.url_android = url_android;
    }

    public String getUrl_iPhone() {
        return url_iPhone;
    }

    public void setUrl_iPhone(String url_iPhone) {
        this.url_iPhone = url_iPhone;
    }

    public String getCate() {
        return cate;
    }

    public void setCate(String cate) {
        this.cate = cate;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
