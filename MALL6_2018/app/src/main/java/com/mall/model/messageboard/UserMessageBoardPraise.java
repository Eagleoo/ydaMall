package com.mall.model.messageboard;

/**
 * 
 * 功能： 心情赞对象<br>
 * 时间： 2014-8-18<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public class UserMessageBoardPraise {
	private String id;
	private String mid;
	private String userFace;
	private String userId;
	private String createTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getUserFace() {
		return userFace;
	}

	public void setUserFace(String userFace) {
		this.userFace = userFace;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
