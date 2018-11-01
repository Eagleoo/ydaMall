package com.mall.model;

/**
 * 
 * 功能： 耳机订单<br>
 * 时间： 2013-3-8<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class OrderItem {

	private String num;
	private String cost;
	private String date;
	private String orderStatus;
	private String secondOrderId;
	private String serviceOrder;

	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public String getNum() {
		return num;
	}

	public void setNum(String num) {
		this.num = num;
	}

	public String getCost() {
		return cost;
	}

	public void setCost(String cost) {
		this.cost = cost;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getSecondOrderId() {
		return secondOrderId;
	}

	public void setSecondOrderId(String secondOrderId) {
		this.secondOrderId = secondOrderId;
	}

	public boolean getServiceOrder() {
		return "true".equals((serviceOrder+"").toLowerCase());
	}

	public void setServiceOrder(String serviceOrder) {
		this.serviceOrder = serviceOrder;
	}
	
}
