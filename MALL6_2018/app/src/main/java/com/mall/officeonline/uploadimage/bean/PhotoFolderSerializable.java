package com.mall.officeonline.uploadimage.bean;

import java.io.Serializable;
import java.util.List;

import com.mall.officeonline.AlbumInfo;

/**    
 */
public class PhotoFolderSerializable implements Serializable {

	/** 
	 * @fields serialVersionUID 
	 */ 
	
	private static final long serialVersionUID = 1L;
	
	private List<AlbumInfo> list;

	public List<AlbumInfo> getList() {
		return list;
	}

	public void setList(List<AlbumInfo> list) {
		this.list = list;
	}

}
