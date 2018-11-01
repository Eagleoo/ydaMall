package com.mall;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */

public class MessageEventTwo implements Serializable {
    private String tag;

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public MessageEventTwo(String tag) {
        this.tag = tag;
    }

    public MessageEventTwo() {

    }
}
