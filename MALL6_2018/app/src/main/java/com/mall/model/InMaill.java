package com.mall.model;

public class InMaill {

    private String id;
    private String tid;
    private String title;
    private String description;
    private String senderTypeID;
    private String userFace;
    private String senderType;
    private String content;
    private String sender;
    private String senderTime;
    private String state = "0";

    public void setUserFace(String userFace) {
        this.userFace = userFace;
    }

    public String getUserFace() {

        return userFace;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSenderTypeID() {
        return senderTypeID;
    }

    public void setSenderTypeID(String senderTypeID) {
        this.senderTypeID = senderTypeID;
    }

    public String getSenderType() {
        return senderType;
    }

    public void setSenderType(String senderType) {
        this.senderType = senderType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getSenderTime() {
        return senderTime;
    }

    public void setSenderTime(String senderTime) {
        this.senderTime = senderTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

}
