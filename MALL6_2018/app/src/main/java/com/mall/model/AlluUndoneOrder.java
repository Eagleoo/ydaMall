package com.mall.model;

import java.io.Serializable;

public class AlluUndoneOrder implements Serializable{

	private String Cost = "";
	private String status = "";
	private String Date = "";
	private String OrderId = "";
	private String postnum = "";
	private String postcpy = "";
	private String postphone = "";
	private String sId;

	private String img = "";

	private String name = "";

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCost() {
		return Cost;
	}

	public void setCost(String cost) {
		Cost = cost;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getsId() {
		return sId;
	}

	public void setsId(String sId) {
		this.sId = sId;
	}

	public String getDate() {
		return Date;
	}

	public void setDate(String date) {
		Date = date;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public String getPostnum() {
		return postnum;
	}

	public void setPostnum(String postnum) {
		this.postnum = postnum;
	}

	public String getPostcpy() {
		return postcpy;
	}

	public void setPostcpy(String postcpy) {
		this.postcpy = postcpy;
	}

	public String getPostphone() {
		return postphone;
	}

	public void setPostphone(String postphone) {
		this.postphone = postphone;
	}

}
