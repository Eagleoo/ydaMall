package com.mall.model;

import com.mall.net.Web;

public class ProductImage {
	private String id;
	private String sma;
	private String big;

	public String getSma() {
		if (null != sma) {
			sma = sma.replaceAll("img.mall666.cn", Web.imgServer);
		}
		return sma;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setSma(String sma) {
		this.sma = sma;
	}

	public String getBig() {
		if (null != big) {
			big = big.replaceAll("img.mall666.cn", Web.imgServer);
		}
		return big;
	}

	public void setBig(String big) {
		this.big = big;
	}

}
