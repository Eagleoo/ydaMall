package com.mall.Bean;

/**
 * Created by Administrator on 2018/5/2.
 */

public class ReturnGoodsBean {
    String sorderId="";
    String pid="";
    String pid_v="";
    String remark="";
    String rtype="";

    public String getSorderId() {
        return sorderId;
    }

    public void setSorderId(String sorderId) {
        this.sorderId = sorderId;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getPid_v() {
        return pid_v;
    }

    public void setPid_v(String pid_v) {
        this.pid_v = pid_v;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRtype() {
        return rtype;
    }

    public void setRtype(String rtype) {
        this.rtype = rtype;
    }

    @Override
    public String toString() {
        return "ReturnGoodsBean{" +
                "sorderId='" + sorderId + '\'' +
                ", pid='" + pid + '\'' +
                ", pid_v='" + pid_v + '\'' +
                ", remark='" + remark + '\'' +
                ", rtype='" + rtype + '\'' +
                '}';
    }
}
