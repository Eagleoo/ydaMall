package com.mall.model;

public class ShopMInfo implements java.io.Serializable {

	private static final long serialVersionUID = -9217630937055549981L;


	private String id;
	private String userid;
	private String shopyysj;

	public String getShopyysj() {
		return shopyysj;
	}

	public void setShopyysj(String shopyysj) {
		this.shopyysj = shopyysj;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	private String name;
	private String weixin;
	private String qq;
	private String zone;
	private String address;
	private String cate;
	private String dianhua;
	private String logo;
	private String lat;
	private String lng;
	private String images;
	private String count;
	private String zhichi2;
	private String content;
	private String shopcstate;
	private String isCooper;
	private boolean isstandbyred;// 是否支持红包种子
	private boolean isIsstandbyexchange; //是否支持换购

	public boolean isIsstandbyred() {
		return zhichi2.contains("hb");
	}

	public void setIsstandbyred(boolean isstandbyred) {
		this.isstandbyred = isstandbyred;
	}

	public boolean isIsstandbyexchange() {
		return zhichi2.contains("hbhg");
	}

	public void setIsstandbyexchange(boolean isstandbyexchange) {
		isIsstandbyexchange = isstandbyexchange;
	}

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

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCate() {
		return cate;
	}

	public void setCate(String cate) {
		this.cate = cate;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLng() {
		return lng;
	}

	public void setLng(String lng) {
		this.lng = lng;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDianhua() {
		return dianhua;
	}

	public void setDianhua(String dianhua) {
		this.dianhua = dianhua;
	}

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getImages() {
		return images;
	}

	public void setImages(String images) {
		this.images = images;
	}

	public String getZhichi2() {
		return zhichi2;
	}

	public void setZhichi2(String zhichi2) {
		this.zhichi2 = zhichi2;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getShopcstate() {
		return shopcstate;
	}

	public void setShopcstate(String shopcstate) {
		this.shopcstate = shopcstate;
	}

	public String getIsCooper() {
		return isCooper;
	}

	public void setIsCooper(String isCooper) {
		this.isCooper = isCooper;
	}

}
