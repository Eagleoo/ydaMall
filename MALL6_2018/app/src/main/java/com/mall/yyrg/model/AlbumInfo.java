package com.mall.yyrg.model;

import java.io.Serializable;
import java.util.List;

import android.graphics.Bitmap;

/**    
 *  ���bean<br>
 *  {@link #image_id}ͼƬid<br>
 *  {@link #path_absolute} ���·��<br>
 *  {@link #path_file} ������ʾ��·��<br>
 *  {@link #name_album} ������<br>
 */
public class AlbumInfo implements Serializable{

	private static final long serialVersionUID = 1L;
	private int image_id;
	private String path_absolute;
	private String path_file;
	private String name_album;
	private List<PhotoInfo> list;
	private Bitmap bitmap;
	
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getImage_id() {
		return image_id;
	}
	public void setImage_id(int image_id) {
		this.image_id = image_id;
	}
	public String getPath_absolute() {
		return path_absolute;
	}
	public void setPath_absolute(String path_absolute) {
		this.path_absolute = path_absolute;
	}
	public String getPath_file() {
		return path_file;
	}
	public void setPath_file(String path_file) {
		this.path_file = path_file;
	}
	public String getName_album() {
		return name_album;
	}
	public void setName_album(String name_album) {
		this.name_album = name_album;
	}
	public List<PhotoInfo> getList() {
		return list;
	}
	public void setList(List<PhotoInfo> list) {
		this.list = list;
	}
}
