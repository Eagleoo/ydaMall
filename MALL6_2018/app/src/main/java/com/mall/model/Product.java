package com.mall.model;

import java.io.Serializable;

public class Product implements Serializable{
	// 产品名称
	private String name;
	// id
	private String id;
	// 简介
	private String intro;
	// 规格
	private String standard;
	// 略缩图
	private String thumb;
	// 录入时间
	private String inputTime;
	// 是否带发票
	private String bill;
	// 是否推荐
	private String elite;
	// 是否热卖
	private String hot;
	// 属性
	private String attr;
	// 市场价
	private String priceMarket;
	// 会员价
	private String price;
	// 产品id
	private String pid;
	private String stocks;// 库存
	private String color;
	private String size;
	private String content;
	private String expPrice1;
	private String categoryId;
	private String sbj;
	private String shortTitle = "";
	private String enableSale;
	private String productid;
	private String ProductName;
	private String productThumb;
	private String salePrice;
	private String priceOriginal;
	private String reviewNum;
	private String user_pay;

	public String getEnableSale() {
		return enableSale;
	}

	public void setEnableSale(String enableSale) {
		this.enableSale = enableSale;
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getProductName() {
		return ProductName;
	}

	public void setProductName(String productName) {
		ProductName = productName;
	}

	public String getProductThumb() {
		return productThumb;
	}

	public void setProductThumb(String productThumb) {
		this.productThumb = productThumb;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}

	public String getPriceOriginal() {
		return priceOriginal;
	}

	public void setPriceOriginal(String priceOriginal) {
		this.priceOriginal = priceOriginal;
	}

	public String getReviewNum() {
		return reviewNum;
	}

	public void setReviewNum(String reviewNum) {
		this.reviewNum = reviewNum;
	}

	public String getUser_pay() {
		return user_pay;
	}

	public void setUser_pay(String user_pay) {
		this.user_pay = user_pay;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public String getStocks() {
		return stocks;
	}

	public void setStocks(String stocks) {
		this.stocks = stocks;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIntro() {
		return intro;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getThumb() {
		return thumb;
	}

	public void setThumb(String thumb) {
		this.thumb = thumb;
	}

	public String getInputTime() {
		return inputTime;
	}

	public void setInputTime(String inputTime) {
		this.inputTime = inputTime;
	}

	public String getBill() {
		return bill;
	}

	public void setBill(String bill) {
		this.bill = bill;
	}

	public String getElite() {
		return elite;
	}

	public void setElite(String elite) {
		this.elite = elite;
	}

	public String getHot() {
		return hot;
	}

	public void setHot(String hot) {
		this.hot = hot;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

	public String getPriceMarket() {
		return priceMarket;
	}

	public void setPriceMarket(String priceMarket) {
		this.priceMarket = priceMarket;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getExpPrice1() {
		if (null == expPrice1)
			expPrice1 = "-1000";
		return expPrice1;
	}

	public void setExpPrice1(String expPrice1) {
		this.expPrice1 = expPrice1;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public String getSbj() {
		return sbj;
	}

	public void setSbj(String sbj) {
		this.sbj = sbj;
	}

}
