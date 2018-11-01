package com.mall.view.carMall;

import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2018/4/13.
 */

public class CarModeBean {

    private boolean isSelect;
    private String  itemStr;
    private Drawable selectdr;
    private Drawable unselectdr;

    public CarModeBean(boolean isSelect, String itemStr, Drawable selectdr, Drawable unselectdr) {
        this.isSelect = isSelect;
        this.itemStr = itemStr;
        this.selectdr = selectdr;
        this.unselectdr = unselectdr;
    }

    public Drawable getSelectdr() {
        return selectdr;
    }

    public void setSelectdr(Drawable selectdr) {
        this.selectdr = selectdr;
    }

    public Drawable getUnselectdr() {
        return unselectdr;
    }

    public void setUnselectdr(Drawable unselectdr) {
        this.unselectdr = unselectdr;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getItemStr() {
        return itemStr;
    }

    public void setItemStr(String itemStr) {
        this.itemStr = itemStr;
    }
}
