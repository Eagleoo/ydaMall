package com.mall.model;

/**
 * 
 * 功能： 推荐人简单信息<br>
 * 时间： 2013-12-19<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public class InviterInfo implements java.io.Serializable {

	private static final long serialVersionUID = -7893090803340310871L;

	private String userid;
	private String name;
	private String level;
	private String shopType;
	private String phone;
	private String userNo;
	private String isSite;
	private String shoppingCard1=null;

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getIsSite() {
		return isSite;
	}

	public void setIsSite(String isSite) {
		this.isSite = isSite;
	}

	public String getShoppingCard1() {
		return shoppingCard1;
	}

	public void setShoppingCard1(String shoppingCard1) {
		this.shoppingCard1 = shoppingCard1;
	}


}
