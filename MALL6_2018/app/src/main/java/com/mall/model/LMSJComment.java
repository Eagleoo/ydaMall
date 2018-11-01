package com.mall.model;

public class LMSJComment implements java.io.Serializable {
	private static final long serialVersionUID = 4162259021957867624L;
	private String id;
	private String user;
	private String lmsj;
	private String score;
	private String date;
	private String content;
	private String exp1;
	private String exp2;
	private String exp3;
	private String exp4;
	private String huifu_user;

	public String getHuifu_user() {
		return huifu_user;
	}

	public void setHuifu_user(String huifu_user) {
		this.huifu_user = huifu_user;
	}

	private String files;
	private String user_face;

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getUser_face() {
		return user_face;
	}

	public void setUser_face(String user_face) {
		this.user_face = user_face;
	}

	public String getFiles() {
		return files;
	}

	public void setFiles(String files) {
		this.files = files;
	}

	public String getExp4() {
		return exp4;
	}

	public void setExp4(String exp4) {
		this.exp4 = exp4;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public String getLmsj() {
		return lmsj;
	}
	public void setLmsj(String lmsj) {
		this.lmsj = lmsj;
	}
	public String getScore() {
		return score;
	}
	public void setScore(String score) {
		this.score = score;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getExp1() {
		return exp1;
	}
	public void setExp1(String exp1) {
		this.exp1 = exp1;
	}
	public String getExp2() {
		return exp2;
	}
	public void setExp2(String exp2) {
		this.exp2 = exp2;
	}
	public String getExp3() {
		return exp3;
	}
	public void setExp3(String exp3) {
		this.exp3 = exp3;
	}
	
}
