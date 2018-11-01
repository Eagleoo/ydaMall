package com.mall.view;

public class RedPacket {

	public RedPacket(String date,String id){
		this.date=date;
		this.id=id;
	}

	private String date;
	private String id;

	public void setDate(String date) {
		this.date = date;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDate() {
		return date;
	}

	public String getId() {
		return id;
	}

}
