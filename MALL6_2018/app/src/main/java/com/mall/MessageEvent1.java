package com.mall;

import com.mall.model.RedPakageBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */


public class MessageEvent1 implements Serializable {
    public  String message;
    public  int progress;

    public RedPakageBean redPakageBean;





    public RedPakageBean getRedPakageBean() {
        return redPakageBean;
    }

    public void setRedPakageBean(RedPakageBean redPakageBean) {
        this.redPakageBean = redPakageBean;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public MessageEvent1(String message) {
        this.message = message;
    }
    public MessageEvent1(int progress){
        this.progress=progress;
    }

    public MessageEvent1(String id, int position){
        this.message = id;
        this.progress=position;
    }

    public MessageEvent1(RedPakageBean redPakageBean, String message){
        this.redPakageBean=redPakageBean;
        this.message=message;
    }
}
