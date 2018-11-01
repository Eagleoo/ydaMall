package com.mall.model;

/**
 * Created by Administrator on 2018/3/30.
 */

public class Column {
    int image;
    String text;
    int tag;
    int count;

    public Column(int image, String text, int tag, int count) {
        this.image = image;
        this.text = text;
        this.tag = tag;
        this.count = count;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTag() {
        return tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}