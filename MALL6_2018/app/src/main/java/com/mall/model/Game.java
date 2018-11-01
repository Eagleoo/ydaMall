package com.mall.model;

public class Game {

	private String cardid = "";

	private String cardName = "";

	private String money = "";

	private String price = "";

	private String amounts;

	public Game() {
		cardid = "";
		cardName = "";
		money = "";
		price = "";
		amounts = "";
	}

	public Game(String cardid, String cardName) {
		this.cardid = cardid;
		this.cardName = cardName;
	}

	public Game(String cardid, String cardName, String money, String price,
			String amounts) {
		this.cardid = cardid;
		this.cardName = cardName;
		this.money = money;
		this.price = price;
		this.amounts = amounts;
	}

	public String getCardid() {
		return cardid;
	}

	public void setCardid(String cardid) {
		this.cardid = cardid;
	}

	public String getCardName() {
		return cardName;
	}

	public void setCardName(String cardName) {
		this.cardName = cardName;
	}

	public String getMoney() {
		return money;
	}

	public void setMoney(String money) {
		this.money = money;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAmounts() {
		return amounts;
	}

	public void setAmounts(String amounts) {
		this.amounts = amounts;
	}

}
