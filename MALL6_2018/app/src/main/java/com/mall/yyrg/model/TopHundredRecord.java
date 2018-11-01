package com.mall.yyrg.model;
/**
 * 计算的数据
 * @author Administrator
 *
 */
public class TopHundredRecord {
/*
 * <obj yuoid="547" buyTime="2014/9/26 13:50:39" 
 * userid="联盟商家001" productName="五季梦2014夏季 拼接无袖裙子 纯色圆领修身雪纺背心连衣裙Q13456" 
 * personTimes="1" periodName="1"/>
 */
	private String yuoid;
	private String buyTime;
	private String userid;
	private String productName;
	private String personTimes;
	private String periodName;
	private String state;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getYuoid() {
		return yuoid;
	}
	public void setYuoid(String yuoid) {
		this.yuoid = yuoid;
	}
	public String getBuyTime() {
		return buyTime;
	}
	public void setBuyTime(String buyTime) {
		this.buyTime = buyTime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPersonTimes() {
		return personTimes;
	}
	public void setPersonTimes(String personTimes) {
		this.personTimes = personTimes;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	
}
