package com.mall.model;

import java.io.Serializable;

public class ShopOfficeListModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String ofName = "";
	private String userid = "";
	private String mCount = "";
	private String vipMcount = "";
	private String lmsjMerCount = "";
	private String shopMerCount = "";
	private String clicks = "";
	private String s_level = "";
	private String domainSTR = "";
	private String  Growth_index="";
	private String logo="";
	private String crown="";
	private String sun="";
	private String month="";
	private String star="";
	private String id="";
	private String sc="";
	private String unum="";
	private String mobilePhone="";
	private String signature="";
	private boolean isShow;
	
	
	
	
	public boolean isShow() {
		return isShow;
	}
	public void setShow(boolean isShow) {
		this.isShow = isShow;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getSignature() {
		return signature;
	}
	public void setSignature(String signature) {
		this.signature = signature;
	}
	public String getUnum() {
		return unum;
	}
	public void setUnum(String unum) {
		this.unum = unum;
	}
	public String getSc() {
		return sc;
	}
	public void setSc(String sc) {
		this.sc = sc;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getCrown() {
		return crown;
	}
	public void setCrown(String crown) {
		this.crown = crown;
	}
	public String getSun() {
		return sun;
	}
	public void setSun(String sun) {
		this.sun = sun;
	}
	public String getMonth() {
		return month;
	}
	public void setMonth(String month) {
		this.month = month;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getGrowth_index() {
		return Growth_index;
	}

	public void setGrowth_index(String growth_index) {
		Growth_index = growth_index;
	}

	public String getOfName() {
		return ofName;
	}

	public void setOfName(String ofName) {
		this.ofName = ofName;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getmCount() {
		return mCount;
	}

	public void setmCount(String mCount) {
		this.mCount = mCount;
	}

	public String getVipMcount() {
		return vipMcount;
	}

	public void setVipMcount(String vipMcount) {
		this.vipMcount = vipMcount;
	}

	public String getLmsjMerCount() {
		return lmsjMerCount;
	}

	public void setLmsjMerCount(String lmsjMerCount) {
		this.lmsjMerCount = lmsjMerCount;
	}

	public String getShopMerCount() {
		return shopMerCount;
	}

	public void setShopMerCount(String shopMerCount) {
		this.shopMerCount = shopMerCount;
	}

	public String getClicks() {
		return clicks;
	}

	public void setClicks(String clicks) {
		this.clicks = clicks;
	}

	public String getS_level() {
		return s_level;
	}

	public void setS_level(String s_level) {
		this.s_level = s_level;
	}

	public String getDomainSTR() {
		return domainSTR;
	}

	public void setDomainSTR(String domainSTR) {
		this.domainSTR = domainSTR;
	}

}
