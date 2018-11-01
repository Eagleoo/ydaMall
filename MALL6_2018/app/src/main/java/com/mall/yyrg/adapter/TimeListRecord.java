package com.mall.yyrg.adapter;

import java.io.Serializable;

public class TimeListRecord implements Serializable{
	/*
	 * <obj yppid="109" productName="香港 正品可爱蛋之家儿童玩具糖 巧克力蛋 6只装#010" photoThumb=
	 * "http://img.yda360.com/ProductImg/26613222/2013/11/25/s2013112504150962.jpg"
	 * price="22" success="45.4545454545455" personTimes="10"
	 * totalPersonTimes="22" lastPersonTimes="cheche628" ypid="28"
	 * annTime="2014/8/8 15:00:00"/>
	 */
	private String yppid;
	private String productName;
	private String photoThumb;
	private String price;
	private String success;
	private String personTimes;
	private String totalPersonTimes;
	private String lastPersonTimes;
	private String ypid;
	private String annTime;
	private String awardUserid;
	public String getAnnTime() {
		return annTime;
	}
	public void setAnnTime(String annTime) {
		this.annTime = annTime;
	}
	public String getAwardUserid() {
		return awardUserid;
	}
	public void setAwardUserid(String awardUserid) {
		this.awardUserid = awardUserid;
	}
	public String getYppid() {
		return yppid;
	}
	public void setYppid(String yppid) {
		this.yppid = yppid;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPhotoThumb() {
		return photoThumb;
	}
	public void setPhotoThumb(String photoThumb) {
		this.photoThumb = photoThumb;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getSuccess() {
		return success;
	}
	public void setSuccess(String success) {
		this.success = success;
	}
	public String getPersonTimes() {
		return personTimes;
	}
	public void setPersonTimes(String personTimes) {
		this.personTimes = personTimes;
	}
	public String getTotalPersonTimes() {
		return totalPersonTimes;
	}
	public void setTotalPersonTimes(String totalPersonTimes) {
		this.totalPersonTimes = totalPersonTimes;
	}
	public String getLastPersonTimes() {
		return lastPersonTimes;
	}
	public void setLastPersonTimes(String lastPersonTimes) {
		this.lastPersonTimes = lastPersonTimes;
	}
	public String getYpid() {
		return ypid;
	}
	public void setYpid(String ypid) {
		this.ypid = ypid;
	}
}
