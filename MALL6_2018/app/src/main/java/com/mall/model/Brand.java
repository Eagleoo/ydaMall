package com.mall.model;

public class Brand {
	private String brand;
	private String lsid = "";
	private String name="";
	private String addKey="";
	
	
	
	public String getAddKey() {
		return addKey;
	}

	public void setAddKey(String addKey) {
		this.addKey = addKey;
	}

	private String hotelNum="";
	
	

	public String getHotelNum() {
		return hotelNum;
	}

	public void setHotelNum(String hotelNum) {
		this.hotelNum = hotelNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getLsid() {
		return lsid;
	}

	public void setLsid(String lsid) {
		this.lsid = lsid;
	}

}
