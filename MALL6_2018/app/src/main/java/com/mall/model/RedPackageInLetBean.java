package com.mall.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/29.
 */

public class RedPackageInLetBean implements Serializable {


    /**
     * code : 200
     * cache : 17216360
     * message : 接口访问成功，业务逻辑处理中
     * redbean : 6000
     * redbox : 20
     * Consumption : 22
     * Cashroll : 100
     * Rechargecoin : 1000
     * ggimg :
     */

    private String code;
    private String cache;
    private String message;
    private String redbean;
    private String redbox_weikai;
    private String redbox_yikai;
    private String Sector_performance;
    private String Friend_achievement;
    private String redboxtime;
    private String zhongshu;
    private String addUpFriend;
    private String addUpSection;
    private String thisUpSection;  //当前部门加速天数
    private String thisUpFriend;   //当前个人加速天数

    private String jdFriend;
    private String jdSection;

    private String zr_day;

    private String bmlj_count;

    private String grlj_count;

    public String getZr_day() {
        return zr_day;
    }

    public void setZr_day(String zr_day) {
        this.zr_day = zr_day;
    }

    public String getBmlj_count() {
        return bmlj_count;
    }

    public void setBmlj_count(String bmlj_count) {
        this.bmlj_count = bmlj_count;
    }

    public String getGrlj_count() {
        return grlj_count;
    }

    public void setGrlj_count(String grlj_count) {
        this.grlj_count = grlj_count;
    }

    public String getJdFriend() {
        return jdFriend;
    }

    public void setJdFriend(String jdFriend) {
        this.jdFriend = jdFriend;
    }

    public String getJdSection() {
        return jdSection;
    }

    public void setJdSection(String jdSection) {
        this.jdSection = jdSection;
    }

    public String getThisUpSection() {
        return thisUpSection;
    }

    public void setThisUpSection(String thisUpSection) {
        this.thisUpSection = thisUpSection;
    }

    public String getThisUpFriend() {
        return thisUpFriend;
    }

    public void setThisUpFriend(String thisUpFriend) {
        this.thisUpFriend = thisUpFriend;
    }

    public String getAddUpFriend() {
        return addUpFriend;
    }

    public void setAddUpFriend(String addUpFriend) {
        this.addUpFriend = addUpFriend;
    }

    public String getAddUpSection() {
        return addUpSection;
    }

    public void setAddUpSection(String addUpSection) {
        this.addUpSection = addUpSection;
    }

    public String getZhongshu() {
        return zhongshu;
    }

    public void setZhongshu(String zhongshu) {
        this.zhongshu = zhongshu;
    }

    public String getRedboxtime() {
        return redboxtime;
    }

    public void setRedboxtime(String redboxtime) {
        this.redboxtime = redboxtime;
    }

    public String getSector_performance() {
        return Sector_performance;
    }

    public void setSector_performance(String sector_performance) {
        Sector_performance = sector_performance;
    }

    public String getFriend_achievement() {
        return Friend_achievement;
    }

    public void setFriend_achievement(String friend_achievement) {
        Friend_achievement = friend_achievement;
    }

    public String getRedbox_weikai() {
        return redbox_weikai;
    }

    public void setRedbox_weikai(String redbox_weikai) {
        this.redbox_weikai = redbox_weikai;
    }

    public String getRedbox_yikai() {
        return redbox_yikai;
    }

    public void setRedbox_yikai(String redbox_yikai) {
        this.redbox_yikai = redbox_yikai;
    }

    public String getRedbox_yilinqu() {
        return redbox_yilinqu;
    }

    public void setRedbox_yilinqu(String redbox_yilinqu) {
        this.redbox_yilinqu = redbox_yilinqu;
    }

    private String redbox_yilinqu;
    private String Consumption;
    private String Cashroll;
    private String Rechargecoin;
    private String ggimg;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCache() {
        return cache;
    }

    public void setCache(String cache) {
        this.cache = cache;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRedbean() {
        return redbean;
    }

    public void setRedbean(String redbean) {
        this.redbean = redbean;
    }



    public String getConsumption() {
        return Consumption;
    }

    public void setConsumption(String Consumption) {
        this.Consumption = Consumption;
    }

    public String getCashroll() {
        return Cashroll;
    }

    public void setCashroll(String Cashroll) {
        this.Cashroll = Cashroll;
    }

    public String getRechargecoin() {
        return Rechargecoin;
    }

    public void setRechargecoin(String Rechargecoin) {
        this.Rechargecoin = Rechargecoin;
    }

    public String getGgimg() {
        return ggimg;
    }

    public void setGgimg(String ggimg) {
        this.ggimg = ggimg;
    }
}
