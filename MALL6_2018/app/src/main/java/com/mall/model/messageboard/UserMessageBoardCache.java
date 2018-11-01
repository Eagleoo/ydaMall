package com.mall.model.messageboard;

import com.lidroid.xutils.db.annotation.Id;

public class UserMessageBoardCache {
	@Id
	public long id;
	private String userId;
	private String userFace = "";
	private String praise = "";
	private String comments = "";
	private String createTime = "";
	private String content = "";
	private String noRead = "";
	private String city = "";
	private String cacheImgFiles = "";
	private String flag="";


	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCacheImgFiles() {
		return cacheImgFiles;
	}

	public void setCacheImgFiles(String cacheImgFiles) {
		this.cacheImgFiles = cacheImgFiles;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserFace() {
		return userFace;
	}

	public void setUserFace(String userFace) {
		this.userFace = userFace;
	}

	public String getPraise() {
		return praise;
	}

	public void setPraise(String praise) {
		this.praise = praise;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getNoRead() {
		return noRead;
	}

	public void setNoRead(String noRead) {
		this.noRead = noRead;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

}
