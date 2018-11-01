package com.mall.model;

public class CommonTicketModel {
	private String GoodsID = "";
	private String GoodsName = "";
	private String GoodsPrice = "";
	private String CinemaNo = "";
	private String VocherDate = "";
	private String WebMemo = "";
	private String choosed="";
	private String buynum="";
	
	

	public String getBuynum() {
		return buynum;
	}

	public void setBuynum(String buynum) {
		this.buynum = buynum;
	}

	public String getChoosed() {
		return choosed;
	}

	public void setChoosed(String choosed) {
		this.choosed = choosed;
	}

	public String getGoodsID() {
		return GoodsID;
	}

	public void setGoodsID(String goodsID) {
		GoodsID = goodsID;
	}

	public String getGoodsName() {
		return GoodsName;
	}

	public void setGoodsName(String goodsName) {
		GoodsName = goodsName;
	}

	public String getGoodsPrice() {
		return GoodsPrice;
	}

	public void setGoodsPrice(String goodsPrice) {
		GoodsPrice = goodsPrice;
	}

	public String getCinemaNo() {
		return CinemaNo;
	}

	public void setCinemaNo(String cinemaNo) {
		CinemaNo = cinemaNo;
	}

	public String getVocherDate() {
		return VocherDate;
	}

	public void setVocherDate(String vocherDate) {
		VocherDate = vocherDate;
	}

	public String getWebMemo() {
		return WebMemo;
	}

	public void setWebMemo(String webMemo) {
		WebMemo = webMemo;
	}

}
