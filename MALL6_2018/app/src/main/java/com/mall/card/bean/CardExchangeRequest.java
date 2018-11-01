package com.mall.card.bean;

public class CardExchangeRequest {
	/**
	 * "id":"7","userid":"zhouyi007","touserid":"联盟商家",
	 * "date":"2014-10-13 07:54:09","enddate":"",
	 * "state":"等待对方同意","tomessage":"","remessage":""}
	 */
	private String id;
	private String userid;
	private String touserid;
	private String date ;
	private String enddate;
	private String state;
	private String tomessage;
	private String remessage;
	private String userFace;
	private String duty;
	private String companyName;
	
	public String getUserFace() {
		return userFace;
	}
	public void setUserFace(String userFace) {
		this.userFace = userFace;
	}
	public String getDuty() {
		return duty;
	}
	public void setDuty(String duty) {
		this.duty = duty;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getTouserid() {
		return touserid;
	}
	public void setTouserid(String touserid) {
		this.touserid = touserid;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getEnddate() {
		return enddate;
	}
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getTomessage() {
		return tomessage;
	}
	public void setTomessage(String tomessage) {
		this.tomessage = tomessage;
	}
	public String getRemessage() {
		return remessage;
	}
	public void setRemessage(String remessage) {
		this.remessage = remessage;
	}
	
	
}
