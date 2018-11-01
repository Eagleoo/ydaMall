package com.mall.model;

import java.io.Serializable;

public class BranchStore implements Serializable{
    private String id;
	private String name;
	private String userId;
	private String hdate;
	private String shopcphone;
	private String shopchand1;
	private String pic;
	private String Favorite;

	public String getFavorite() {
		return Favorite;
	}

	public void setFavorite(String favorite) {
		Favorite = favorite;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getHdate() {
		return hdate;
	}

	public void setHdate(String hdate) {
		this.hdate = hdate;
	}

	public String getShopcphone() {
		return shopcphone;
	}

	public void setShopcphone(String shopcphone) {
		this.shopcphone = shopcphone;
	}

	public String getShopchand1() {
		return shopchand1;
	}

	public void setShopchand1(String shopchand1) {
		this.shopchand1 = shopchand1;
	}


	public BranchStore() {
		super();
	}


	public BranchStore(String id, String name, String userId) {
		super();
		this.id = id;
		this.name = name;
		this.userId = userId;
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


	public String getUserId() {
		return userId;
	}


	public void setUserId(String userId) {
		this.userId = userId;
	}
	
}
