package com.mall.util;

/**
 * Created by Administrator on 2017/10/23.
 */

public class MyTime {
    long diff;
    long day;//计算差多少天
    long hour ;//计算差多少小时

    public long getDiff() {
        return diff;
    }

    public void setDiff(long diff) {
        this.diff = diff;
    }

    public long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    public long getHour() {
        return hour;
    }

    public void setHour(long hour) {
        this.hour = hour;
    }

    public long getMin() {
        return min;
    }

    public void setMin(long min) {
        this.min = min;
    }

    public long getSec() {
        return sec;
    }

    public void setSec(long sec) {
        this.sec = sec;
    }

    long min ;//计算差多少分钟
    long sec ;//计算差多少秒//输出结果
}
