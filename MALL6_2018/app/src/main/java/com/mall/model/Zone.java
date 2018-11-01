package com.mall.model;

/**
 * 
 * 功能： 地区类<br>
 * 时间： 2013-4-3<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class Zone implements java.io.Serializable {
	// zoneid
	private String id;
	// 地区名字
	private String name;
	//  父级
	private String parentid;
	

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

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

	@Override
	public String toString() {
		return name;
	}

}
