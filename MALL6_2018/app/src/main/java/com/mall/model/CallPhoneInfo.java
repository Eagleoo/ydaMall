package com.mall.model;

/**
 * 
 * 功能： 通话记录<br>
 * 时间： 2014-8-14<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public class CallPhoneInfo {
	private String id;
	private String callPhone;
	private String startTime;
	private String endTime;
	private String callTime;
	private boolean isConnection;
	private long callStartTime;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCallPhone() {
		return callPhone;
	}
	public void setCallPhone(String callPhone) {
		this.callPhone = callPhone;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getCallTime() {
		return callTime;
	}
	public void setCallTime(String callTime) {
		this.callTime = callTime;
	}
	
	public boolean isConnection() {
		return isConnection;
	}
	public void setConnection(boolean isConnection) {
		this.isConnection = isConnection;
	}
	public long getCallStartTime() {
		return callStartTime;
	}
	public void setCallStartTime(long callStartTime) {
		this.callStartTime = callStartTime;
	}
	
}
