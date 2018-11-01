package com.mall.model;

public class ShopOfficeVedioModel {
	private String title = "";
	private String time = "";
	private String vdeioimgurl = "";
	private String vediourl = "";
	private String commentCount = "";
	private String goodclicks = "";
	private String articleid="";
	private String officeid="";
	private String content="";
	private String pageCount="";
	private String COUNT_COMMENT="";
	private String createTime="";
	

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getPageCount() {
		return pageCount;
	}

	public String getCOUNT_COMMENT() {
		return COUNT_COMMENT;
	}

	public void setCOUNT_COMMENT(String cOUNT_COMMENT) {
		COUNT_COMMENT = cOUNT_COMMENT;
	}

	public void setPageCount(String pageCount) {
		this.pageCount = pageCount;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOfficeid() {
		return officeid;
	}

	public void setOfficeid(String officeid) {
		this.officeid = officeid;
	}

	public String getArticleid() {
		return articleid;
	}

	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getVdeioimgurl() {
		return vdeioimgurl;
	}

	public void setVdeioimgurl(String vdeioimgurl) {
		this.vdeioimgurl = vdeioimgurl;
	}

	public String getVediourl() {
		return vediourl;
	}

	public void setVediourl(String vediourl) {
		this.vediourl = vediourl;
	}

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	public String getGoodclicks() {
		return goodclicks;
	}

	public void setGoodclicks(String goodclicks) {
		this.goodclicks = goodclicks;
	}

}
