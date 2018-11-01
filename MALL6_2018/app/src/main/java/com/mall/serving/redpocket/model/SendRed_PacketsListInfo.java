package com.mall.serving.redpocket.model;

import java.io.Serializable;

public class SendRed_PacketsListInfo implements Serializable {

	private String senduser = "";
	private String sendtime = "";
	private String times = "";
	private String used = "";
	private String usedmoney = "";
	private String lave = "";
	private String Status = "";

	private String state = "";
	private String type = "";
	private String money = "";
	private String orderid = "";
	private String remark = "";
	private String id = "";

	public String getSenduser() {
		return senduser;
	}

	public void setSenduser(String senduser) {
		this.senduser = senduser;
	}

	public String getSendtime() {
		return sendtime;
	}

	public String getUsed() {
		return used;
	}

	public void setUsed(String used) {
		this.used = used;
	}

	public String getLave() {
		return lave;
	}

	public void setLave(String lave) {
		this.lave = lave;
	}

	public String getUsedmoney() {
		return usedmoney;
	}

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public void setUsedmoney(String usedmoney) {
		this.usedmoney = usedmoney;
	}

	public void setSendtime(String sendtime) {
		this.sendtime = sendtime;
	}

	public String getTimes() {
		return times;
	}

	public void setTimes(String times) {
		this.times = times;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
