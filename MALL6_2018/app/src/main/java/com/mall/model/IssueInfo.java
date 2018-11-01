package com.mall.model;

public class IssueInfo {
	private String lotteryId;
	private String issueNumber;
	private String startTime;
	private String stopTime;
	private String closeTime;
	private String prizeTime;
	private String status;
	private String bonusCode;
	private String bonusInfo;
	private String betContent;
	
	private String notifyStatus;
	private String message;
	private String statusCode;
	
	
	public String getNotifyStatus() {
		return notifyStatus;
	}
	public void setNotifyStatus(String notifyStatus) {
		this.notifyStatus = notifyStatus;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getLotteryId() {
		return lotteryId;
	}
	public void setLotteryId(String lotteryId) {
		this.lotteryId = lotteryId;
	}
	public String getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getStopTime() {
		return stopTime;
	}
	public void setStopTime(String stopTime) {
		this.stopTime = stopTime;
	}
	public String getCloseTime() {
		return closeTime;
	}
	public void setCloseTime(String closeTime) {
		this.closeTime = closeTime;
	}
	public String getPrizeTime() {
		return prizeTime;
	}
	public void setPrizeTime(String prizeTime) {
		this.prizeTime = prizeTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getBonusCode() {
		return bonusCode;
	}
	public void setBonusCode(String bonusCode) {
		this.bonusCode = bonusCode;
	}
	public String getBonusInfo() {
		return bonusInfo;
	}
	public void setBonusInfo(String bonusInfo) {
		this.bonusInfo = bonusInfo;
	}
	public String getBetContent() {
		return betContent;
	}
	public void setBetContent(String betContent) {
		this.betContent = betContent;
	}
	
}
