package com.mall.model;

import android.graphics.Bitmap;

/**
 * 首页推荐热销产品
 * 
 * @author kaifa
 * 
 */
public class MainProduct {
	private String id;
	private String pid;
	private String name;
	private String image;
	private String price;
	private Bitmap bitmap;
	private String expPrice1;
	private String priceMarket;
	private String sbj;
	private String zhe;
	private String thumb="";
	private String stocks="";
	
	
	


	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getZhe() {
		return zhe;
	}

	public void setZhe(String zhe) {
		this.zhe = zhe;
	}

	public String getSbj() {
		return sbj;
	}

	public void setSbj(String sbj) {
		this.sbj = sbj;
	}

	public MainProduct() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public String getExpPrice1() {
		return expPrice1;
	}

	public void setExpPrice1(String expPrice1) {
		this.expPrice1 = expPrice1;
	}

	public String getPriceMarket() {
		return priceMarket;
	}

	public void setPriceMarket(String priceMarket) {
		this.priceMarket = priceMarket;
	}

	public String getStocks() {
		return stocks;
	}

	public void setStocks(String stocks) {
		this.stocks = stocks;
	}

}
