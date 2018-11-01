package com.mall.model;

import java.io.Serializable;

public class YdNewsCommentModel implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private String id="";
    private String newsid="";
    private String Praise="";
    private String userid="";
    private String adddate="";
    private String info="";
    private String Source="";
    private String userno="";
    
	public String getUserno() {
		return userno;
	}
	public void setUserno(String userno) {
		this.userno = userno;
	}
	public String getSource() {
		return Source;
	}
	public void setSource(String source) {
		Source = source;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNewsid() {
		return newsid;
	}
	public void setNewsid(String newsid) {
		this.newsid = newsid;
	}
	public String getPraise() {
		return Praise;
	}
	public void setPraise(String praise) {
		Praise = praise;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getAdddate() {
		return adddate;
	}
	public void setAdddate(String adddate) {
		this.adddate = adddate;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
    
}
