package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;


/**
 * 
 * 功能： 产品分类<br>
 * 时间： 2013-3-17<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class Category {
	// 分类ID
	@Id
	private String categoryId;
	// 分类名称
	private String categoryName;
	// 父类id -1为根
	private String categoryParentID;
	private String desc;
	private String productNum;
	
	public String getProductNum() {
		return productNum;
	}

	public void setProductNum(String productNum) {
		this.productNum = productNum;
	}

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

	public String getCategoryParentID() {
		return categoryParentID;
	}

	public void setCategoryParentID(String categoryParentID) {
		this.categoryParentID = categoryParentID;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
