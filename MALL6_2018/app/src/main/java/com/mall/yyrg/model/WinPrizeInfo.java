package com.mall.yyrg.model;
/**
 * 中奖用户的信息
 * @author Administrator
 *
 */
public class WinPrizeInfo {
/*
 * <obj productId="159166" productName="台湾 台一抹茶味花生酥糖 绿茶酥 进口特产零食 #006"
 *  price="10" annNum="10000005" annTime="2014/9/24 9:51:00" 
 *  buyTime="2014/9/24 9:48:50" buyIPAddr="四川省 成都市"
 *   awardUserid="联盟商家001" logo="" totalPersonTimes="10" 
 *   photoThumb="ProductImg/26613222/2013/11/25/s20131125030331437.jpg" periodName="5"/>
 */
	private String productId;
	private String productName;
	private String price;
	private String annNum;
	private String annTime;
	private String buyTime;
	private String buyIPAddr;
	private String awardUserid;
	private String logo;
	private String totalPersonTimes;
	private String photoThumb;
	private String periodName;
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
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
	public String getAwardUserid() {
		return awardUserid;
	}
	public void setAwardUserid(String awardUserid) {
		this.awardUserid = awardUserid;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public String getTotalPersonTimes() {
		return totalPersonTimes;
	}
	public void setTotalPersonTimes(String totalPersonTimes) {
		this.totalPersonTimes = totalPersonTimes;
	}
	public String getPhotoThumb() {
		return photoThumb;
	}
	public void setPhotoThumb(String photoThumb) {
		this.photoThumb = photoThumb;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	
}
