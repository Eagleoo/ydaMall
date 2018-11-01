package com.mall.model;

import com.mall.net.Web;

public class ShopCarItem {
	private String name;
	private String amountUnit;
	private String amount;
	private String price;
	private String img;
	private String pid;
	private String guid;
	private String youfei;
	private String colorAndSize;
	private String pfrom;
	private String sb;
	private String exp1;
	private String rebate1;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAmountUnit() {
		return amountUnit;
	}

	public void setAmountUnit(String amountUnit) {
		this.amountUnit = amountUnit;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		if (null != img) {
			img = img.replace("img.mall666.cn", Web.imgServer);
		}
		this.img = img;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getYoufei() {
		return youfei;
	}

	public void setYoufei(String youfei) {
		this.youfei = youfei;
	}

	public String getColorAndSize() {
		if (null == colorAndSize)
			colorAndSize = "";
		return colorAndSize;
	}

	public void setColorAndSize(String colorAndSize) {
		this.colorAndSize = colorAndSize;
	}

	public String getPfrom() {
		return pfrom;
	}

	public void setPfrom(String pfrom) {
		this.pfrom = pfrom;
	}

	public String getSb() {
		return sb;
	}

	public void setSb(String sb) {
		this.sb = sb;
	}

	public String getExp1() {
		return exp1;
	}

	public void setExp1(String exp1) {
		this.exp1 = exp1;
	}

	public String getRebate1() {
		return rebate1;
	}

	public void setRebate1(String rebate1) {
		this.rebate1 = rebate1;
	}

}
