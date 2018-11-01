package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

public class CollectionsProduct {
	private String productname;
	@Id
	private String id;
	private String ownerid;
	private String imgUrl;
	private long pressedtimes=0;
	private String productid;
	private String collectTime;
	
	
    public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public long getPressedtimes() {
		return pressedtimes;
	}

	public void setPressedtimes(long pressedtimes) {
		this.pressedtimes = pressedtimes;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public CollectionsProduct(String id,String name,String ownerid,long pressedtimes){
    	this.productname=name;
    	this.id=id;
    	this.ownerid=ownerid;
    	this.pressedtimes=pressedtimes;
    }
	
	public CollectionsProduct() {
		super();
	}

	public String getProductname() {
		return productname;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public void setProductname(String productname) {
		this.productname = productname;
	}

	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

}
