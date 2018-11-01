package com.mall.yyrg.model;
/**
 * 商品分类
 * @author Administrator
 *
 */
public class GoodClassRecord {
/*
 * <obj categoryId="420" categoryName="女装馆" icount="5"/>
 */
	private String categoryId;
	private String categoryName;
	private String icount;
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getIcount() {
		return icount;
	}
	public void setIcount(String icount) {
		this.icount = icount;
	}
	
}
