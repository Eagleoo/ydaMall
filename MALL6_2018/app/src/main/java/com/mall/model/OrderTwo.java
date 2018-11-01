package com.mall.model;

import java.io.Serializable;

public class OrderTwo implements Serializable {
    public String secondOrderId;
    public String postNum;
    public String postCpy;
    public String postPhone;
    public String unitCost;
    public String quantity;
    public String secondOrderStatus;
    public String productName;
    public String productThumb;
    public String colorAndSize;
    public String productId;
    public String applystate; //0 未申请 1 申请中 2取消
    public String tui_state; //-1 未申请 0申请审核中 1驳回 2已审核
    public String qx_date;
    public String tui_date;
    public int iscomment;
    public int number;

    @Override
    public String toString() {
        return "OrderTwo{" +
                "secondOrderId='" + secondOrderId + '\'' +
                ", postNum='" + postNum + '\'' +
                ", postCpy='" + postCpy + '\'' +
                ", postPhone='" + postPhone + '\'' +
                ", unitCost='" + unitCost + '\'' +
                ", quantity='" + quantity + '\'' +
                ", secondOrderStatus='" + secondOrderStatus + '\'' +
                ", productName='" + productName + '\'' +
                ", productThumb='" + productThumb + '\'' +
                ", colorAndSize='" + colorAndSize + '\'' +
                ", productId='" + productId + '\'' +
                ", number=" + number +
                '}';
    }
}
