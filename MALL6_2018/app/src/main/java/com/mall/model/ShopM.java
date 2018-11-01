package com.mall.model;

public class ShopM implements java.io.Serializable {
	private static final long serialVersionUID = 4605625502657131304L;
	private String id;
	private String name;
	private String zone;
	private String phone;
	private String ISELITE;
	public String getISELITE() {
		return ISELITE;
	}

	public void setISELITE(String iSELITE) {
		ISELITE = iSELITE;
	}

	private String pointX; //经度
	private String pointY; //纬度
	private String Favorite; //纬度

	public String getFavorite() {
		return Favorite;
	}

	public void setFavorite(String favorite) {
		Favorite = favorite;
	}

	private String cate;
	private String img;
	private float mm=Float.MAX_VALUE;
	private String userid;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPointX() {
		return pointX;
	}

	public void setPointX(String pointX) {
		this.pointX = pointX;
	}

	public String getPointY() {
		return pointY;
	}

	public void setPointY(String pointY) {
		this.pointY = pointY;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public float getMm() {
		return mm;
	}

	public void setMm(float mm) {
		this.mm = mm;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

}
