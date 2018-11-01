package com.mall.model;

import java.io.Serializable;

public class ShopOfficeArticleModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String username="";
	private String from_userid="";
	private String articleid="";
	private String createTime="";
	private String clicks="";
	private String title="";
	private String goodclicks="";
	private String commentCount="";
	private String isreprint="";
	private String pageCount="";
	private String content="";
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getFrom_userid() {
		return from_userid;
	}
	public void setFrom_userid(String from_userid) {
		this.from_userid = from_userid;
	}
	public String getArticleid() {
		return articleid;
	}
	public void setArticleid(String articleid) {
		this.articleid = articleid;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getClicks() {
		return clicks;
	}
	public void setClicks(String clicks) {
		this.clicks = clicks;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGoodclicks() {
		return goodclicks;
	}
	public void setGoodclicks(String goodclicks) {
		this.goodclicks = goodclicks;
	}
	public String getCommentCount() {
		return commentCount;
	}
	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}
	public String getIsreprint() {
		return isreprint;
	}
	public void setIsreprint(String isreprint) {
		this.isreprint = isreprint;
	}
	public String getPageCount() {
		return pageCount;
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
	

}
