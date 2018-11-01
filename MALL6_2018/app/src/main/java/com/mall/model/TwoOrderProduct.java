package com.mall.model;

import com.mall.net.Web;

public class TwoOrderProduct {
	private String name;
	private String price;
	private String unit;
	private String message;
	private String tid;
	private String amount;
	private String PresentExp;
	private String ishealth;

	public String getIshealth() {
		return ishealth;
	}

	public void setIshealth(String ishealth) {
		this.ishealth = ishealth;
	}

	public String getPresentExp() {
		return PresentExp;
	}

	public void setPresentExp(String presentExp) {
		PresentExp = presentExp;
	}

	private String img;
	private String expPrice;
	private String pform;
	private String postage;
	private String gwk;
	private String sb;
	private String rebate1;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getImg() {
		if (null != img) {
			img = img.replaceFirst("img.mall666.com", Web.imgServer);
		}
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getExpPrice() {
		return expPrice;
	}

	public void setExpPrice(String expPrice) {
		this.expPrice = expPrice;
	}

	public String getPform() {
		return pform;
	}

	public void setPform(String pform) {
		this.pform = pform;
	}

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getSb() {
		return sb;
	}

	public void setSb(String sb) {
		this.sb = sb;
	}

	public String getGwk() {
		return gwk;
	}

	public void setGwk(String gwk) {
		this.gwk = gwk;
	}

	public String getRebate1() {
		return rebate1;
	}

	public void setRebate1(String rebate1) {
		this.rebate1 = rebate1;
	}

}
