package com.mall.yyrg.model;
/**
 * 商品的期次
 * @author Administrator
 *
 */
public class ShopPeriods {
	/*
	 * <obj yppid="122" ypid="24" periodName="2" status="3"/>
	 */
	private String yppid;
	private String ypid;
	private String periodName;
	private String status;
	public String getYppid() {
		return yppid;
	}
	public void setYppid(String yppid) {
		this.yppid = yppid;
	}
	public String getYpid() {
		return ypid;
	}
	public void setYpid(String ypid) {
		this.ypid = ypid;
	}
	public String getPeriodName() {
		return periodName;
	}
	public void setPeriodName(String periodName) {
		this.periodName = periodName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
