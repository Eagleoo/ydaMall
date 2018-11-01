package com.mall.yyrg.model;

public class BaskSingle {
/*
 * <obj logo="" userId="联盟商家001" sharetime="2014-09-05 00:00:00" periodName="9"
 *  title="打扫打扫对" shareimgs="" commCount="0"><p>计划idsiods</p></obj>
 * 
 */
	private String logo;
	private String userId;
	private String showName="";
	private String sharetime;
	private String periodName;
	private String title;
	private String shareimgs;
	private String	commCount;
	private String content;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
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
	
	public String getShowName() {
		return showName;
	}
	public void setShowName(String showName) {
		this.showName = showName;
	}
	public String getSharetime() {
		return sharetime;
	}
	public void setSharetime(String sharetime) {
		this.sharetime = sharetime;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getShareimgs() {
		return shareimgs;
	}
	public void setShareimgs(String shareimgs) {
		this.shareimgs = shareimgs;
	}
	public String getCommCount() {
		return commCount;
	}
	public void setCommCount(String commCount) {
		this.commCount = commCount;
	}
	
}
