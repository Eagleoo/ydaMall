package com.mall.yyrg.model;
/**
 * 上期获奖者
 * @author Administrator
 *
 */
public class LastPeriod {
	/*
	 * <obj logo="" userId="super" buyTime="2014/8/16 18:15:42" buyIPAddr="四川省 成都市" 
	 * annNum="10000003" annTime="2014/8/16 18:16:18"/>
	 */
	private String logo;
	private String userId;
	private String buyTime;
	private String buyIPAddr;
	private String annNum;
	private String annTime;
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
	public String getAnnNum() {
		return annNum;
	}
	public void setAnnNum(String annNum) {
		this.annNum = annNum;
	}
	public String getAnnTime() {
		return annTime;
	}
	public void setAnnTime(String annTime) {
		this.annTime = annTime;
	}
}
