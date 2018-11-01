package com.mall.model;

import android.graphics.Bitmap;

public class Images {
	private String id;
	private String url;
	private String path;
	private int res = Integer.MIN_VALUE;
	private Bitmap bp;

	public int getRes() {
		return res;
	}

	public void setRes(int res) {
		this.res = res;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Bitmap getBp() {
		return bp;
	}

	public void setBp(Bitmap bp) {
		this.bp = bp;
	}

}
