package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

public class ShopCarNumberModel {
	private int number;
	@Id
	private int id;
	
	private String userid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

}
