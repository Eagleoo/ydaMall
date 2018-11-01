package com.mall.model.messageboard;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * 功能： 心情实体类<br>
 * 时间： 2014-8-16<br>
 * 备注： <br>
 * @author Lin.~
 *
 */
public class UserMessageBoard implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String type="";

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	private String id="";
	private String userId="";
	private String userFace="";
	private String praise="";
	private String praiseState="";
	private String comments="";
	private String files="";
	private List<Bitmap> list;
	private String createTime="";
	private String content="";
	private String noRead="";
	private String city="";
	private String flag="";
	private long dateaId;
	private String imgCacheFiles="";
	private String from="";
	private String localFilesPaths="";
	private String guanzhu="";

	public String getGuanzhu() {
		return guanzhu;
	}

	public void setGuanzhu(String guanzhu) {
		this.guanzhu = guanzhu;
	}

	public static int type_finish = 1;
	public static int type_sending = 2;
	public static int type_fail = 3;
	
	
	
	
	public String getLocalFilesPaths() {
		return localFilesPaths;
	}
	public void setLocalFilesPaths(String localFilesPaths) {
		this.localFilesPaths = localFilesPaths;
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getImgCacheFiles() {
		return imgCacheFiles;
	}
	public void setImgCacheFiles(String imgCacheFiles) {
		this.imgCacheFiles = imgCacheFiles;
	}
	public long getDateaId() {
		return dateaId;
	}
	public void setDateaId(long dateaId) {
		this.dateaId = dateaId;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public List<Bitmap> getList() {
		return list;
	}
	public void setList(List<Bitmap> list) {
		this.list = list;
	}
	public String getNoRead() {
		return noRead;
	}
	public void setNoRead(String noRead) {
		this.noRead = noRead;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserFace() {
		return userFace;
	}
	public void setUserFace(String userFace) {
		this.userFace = userFace;
	}
	public String getPraise() {
		return praise;
	}
	public void setPraise(String praise) {
		this.praise = praise;
	}
	public String getPraiseState() {
		return praiseState;
	}
	public void setPraiseState(String praiseState) {
		this.praiseState = praiseState;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getFiles() {
		return files;
	}
	public void setFiles(String files) {
		this.files = files;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	
}
