package com.mall.model;

public class LoginUserInfo {
	
	private int id;
	private String userId;
	private String pwd;
	private String issave;// 0是存储
	public String getIssave() {
		return issave;
	}


	public void setIssave(String issave) {
		this.issave = issave;
	}


	public int getId() {
		return id;
	}
	

	public void setId(int id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
	
	
}
