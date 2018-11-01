package com.mall.serving.doubleball;

import java.io.Serializable;

public class LotteryOrderData implements Serializable{

	private String playType;
	private String issueNumber;
	private String multiple;
	private String totalmoney;
	private String betContent;
	public String getPlayType() {
		return playType;
	}
	public void setPlayType(String playType) {
		this.playType = playType;
	}
	public String getIssueNumber() {
		return issueNumber;
	}
	public void setIssueNumber(String issueNumber) {
		this.issueNumber = issueNumber;
	}
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String multiple) {
		this.multiple = multiple;
	}
	public String getTotalmoney() {
		return totalmoney;
	}
	public void setTotalmoney(String totalmoney) {
		this.totalmoney = totalmoney;
	}
	public String getBetContent() {
		return betContent;
	}
	public void setBetContent(String betContent) {
		this.betContent = betContent;
	}

	
	
	
}
