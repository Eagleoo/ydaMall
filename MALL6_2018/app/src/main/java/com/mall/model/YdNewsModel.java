package com.mall.model;

import java.io.Serializable;

public class YdNewsModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id = "";
	private String tid = "";
	private String nid = "";
	private String Praise = "";
	private String Comment = "";
	private String picurl = "";
	private String new_from = "";
	public String content = "";
	private String title = "";
	private String click_sum="";
	private String newdate="";
	
	

	public String getNewdate() {
		return newdate;
	}

	public void setNewdate(String newdate) {
		this.newdate = newdate;
	}

	public String getClick_sum() {
		return click_sum;
	}

	public void setClick_sum(String click_sum) {
		this.click_sum = click_sum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}

	public String getNid() {
		return nid;
	}

	public void setNid(String nid) {
		this.nid = nid;
	}

	public String getPraise() {
		return Praise;
	}

	public void setPraise(String praise) {
		Praise = praise;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String comment) {
		Comment = comment;
	}

	public String getPicurl() {
		return picurl;
	}

	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}

	public String getNew_from() {
		return new_from;
	}

	public void setNew_from(String new_from) {
		this.new_from = new_from;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

}
