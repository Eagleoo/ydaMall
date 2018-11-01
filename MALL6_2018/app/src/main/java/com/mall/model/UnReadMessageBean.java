package com.mall.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class UnReadMessageBean implements Serializable {

	@SerializedName("code")
	private String _$Code25; // FIXME check this code
	private String cache;
	private String message;
	private List<?> praises;
	private List<CommentsBean> comments;

	public String get_$Code25() {
		return _$Code25;
	}

	public void set_$Code25(String _$Code25) {
		this._$Code25 = _$Code25;
	}

	public String getCache() {
		return cache;
	}

	public void setCache(String cache) {
		this.cache = cache;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<?> getPraises() {
		return praises;
	}

	public void setPraises(List<?> praises) {
		this.praises = praises;
	}

	public List<CommentsBean> getComments() {
		return comments;
	}

	public void setComments(List<CommentsBean> comments) {
		this.comments = comments;
	}

	public static class CommentsBean implements Serializable {
		@SerializedName("id")
		private String id; // FIXME check this code

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		private String mid;
		private String fbz_userid;
		private String fbz_face;
		private String fbz_createtime;

		public String getFbz_userid() {
			return fbz_userid;
		}

		public void setFbz_userid(String fbz_userid) {
			this.fbz_userid = fbz_userid;
		}

		public String getFbz_face() {
			return fbz_face;
		}

		public void setFbz_face(String fbz_face) {
			this.fbz_face = fbz_face;
		}

		public String getFbz_createtime() {
			return fbz_createtime;
		}

		public void setFbz_createtime(String fbz_createtime) {
			this.fbz_createtime = fbz_createtime;
		}

		private String face;
		private String commentUserId;
		private String commentMessage;
		private String commentCreateTime;
		private String moodMessage;
		private String moodFiles;
		private String isPraise;
		private String type;



		public String getMid() {
			return mid;
		}

		public void setMid(String mid) {
			this.mid = mid;
		}

		public String getFace() {
			return face;
		}

		public void setFace(String face) {
			this.face = face;
		}

		public String getCommentUserId() {
			return commentUserId;
		}

		public void setCommentUserId(String commentUserId) {
			this.commentUserId = commentUserId;
		}

		public String getCommentMessage() {
			return commentMessage;
		}

		public void setCommentMessage(String commentMessage) {
			this.commentMessage = commentMessage;
		}

		public String getCommentCreateTime() {
			return commentCreateTime;
		}

		public void setCommentCreateTime(String commentCreateTime) {
			this.commentCreateTime = commentCreateTime;
		}

		public String getMoodMessage() {
			return moodMessage;
		}

		public void setMoodMessage(String moodMessage) {
			this.moodMessage = moodMessage;
		}

		public String getMoodFiles() {
			return moodFiles;
		}

		public void setMoodFiles(String moodFiles) {
			this.moodFiles = moodFiles;
		}

		public String getIsPraise() {
			return isPraise;
		}

		public void setIsPraise(String isPraise) {
			this.isPraise = isPraise;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}
	}
}
