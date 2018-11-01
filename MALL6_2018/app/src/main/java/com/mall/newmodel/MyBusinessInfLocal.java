package com.mall.newmodel;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Administrator on 2017/10/31.
 */

@DatabaseTable(tableName = "MyBusinessInfLocal")
public class MyBusinessInfLocal {

    @DatabaseField(id = true)
    private int id;

    @DatabaseField
    private String name;

    @DatabaseField
    private String icon;

    @DatabaseField
    private String url;

    @DatabaseField
    private String savepath;

    @DatabaseField
    private long size;

    @DatabaseField
    private String userid;







    public MyBusinessInfLocal() {
    }

    public MyBusinessInfLocal(int id, String name, String icon, String url,String savepath,long size,String userid) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.url = url;
        this.savepath=savepath;
        this.size=size;
        this.userid=userid;

    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }


    public String getSavepath() {
        return savepath;
    }

    public void setSavepath(String savepath) {
        this.savepath = savepath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
