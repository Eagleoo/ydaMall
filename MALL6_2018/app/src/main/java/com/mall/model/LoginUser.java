package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;

public class LoginUser {
	public String issave;//0 是存储 
	public String getIssave() {
		return issave;
	}
	public void setIssave(String issave) {
		this.issave = issave;
	}
	public String userId="";
	public String userFace;
	@Id
	public String userNo;
	public String pwd;
	public String sessionId;
	
	
}
