package com.mall.yyrg.model;

public class HotShopRecord {
	/*
	 * <obj logo="" userId="联盟商家001" buyTime="2014/9/15 17:30:47"
	 *  buyIPAddr="四川省 成都市" totalTimes="11"/>
	 */
	private String logo;
	private String userId;
	private String buyTime;
	private String buyIPAddr;
	private String totalTimes;
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getBuyTime() {
		return buyTime;
	}
	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
	}
	public String getBuyIPAddr() {
		return buyIPAddr;
	}
	public void setBuyIPAddr(String buyIPAddr) {
		this.buyIPAddr = buyIPAddr;
	}
	public String getTotalTimes() {
		return totalTimes;
	}
	public void setTotalTimes(String totalTimes) {
		this.totalTimes = totalTimes;
	}
	
}
