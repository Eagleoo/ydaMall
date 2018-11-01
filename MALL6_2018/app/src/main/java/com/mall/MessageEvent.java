package com.mall;

import com.mall.model.RedPakageBean;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/9/19.
 */

public class MessageEvent implements Serializable {
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

    public MessageEvent(String message) {
        this.message = message;
    }
    public MessageEvent(int progress){
        this.progress=progress;
    }

    public MessageEvent(String id,int position){
        this.message = id;
        this.progress=position;
    }

    public MessageEvent(RedPakageBean redPakageBean,String message){
        this.redPakageBean=redPakageBean;
        this.message=message;
    }
}
