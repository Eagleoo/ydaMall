package com.mall.yyrg.model;

public class TiXiUser {
/**
 * {"userId":"wgwk","userType":"1","level":"会员","name":"",
 * "mobilePhone":"15268596523","inviter":"远大商城",
 * "merchants":"wg069","zone":"四川省 成都市 郫县 ",】
 * "date":"2014/12/23 11:11:06"}
 */
	private String userId;
	private String userType;
	private String level;
	private String name;
	private String mobilePhone;
	private String inviter;
	private String merchants;
	private String zone;
	private String date;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMobilePhone() {
		return mobilePhone;
	}
	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}
	public String getInviter() {
		return inviter;
	}
	public void setInviter(String inviter) {
		this.inviter = inviter;
	}
	public String getMerchants() {
		return merchants;
	}
	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}
	public String getZone() {
		return zone;
	}
	public void setZone(String zone) {
		this.zone = zone;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
