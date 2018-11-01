package com.mall.card.bean;
/**
 * 名片的分组
 * @author admin
 *
 */
public class CardGrouping {
	/**
	 * "userId":"zhouyi007","id":"1","isdel":"0",
	 * "date":"2014/11/25 17:33:28","groupname":"客户","remark":""
	 */
	private String userId;
	private String id;
	private String isdel;
	private String date;
	private String groupname;
	private String remark;
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getIsdel() {
		return isdel;
	}
	public void setIsdel(String isdel) {
		this.isdel = isdel;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getGroupname() {
		return groupname;
	}
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}
