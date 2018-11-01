package com.mall.model;

import java.io.Serializable;

public class ShopOfficeInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String office_id = "";
	private String userid = "";
	private String clicks = "";// 访客
	private String txtSigner = "";
	private String ImgLogo = "";//办公室头像
	private String ImgLogo1 = "";
	private String HfImage1 = "";
	private String art_count = "";// 文章数量
	private String vdieo_count = "";// 视频数量
	private String mp3_count = "";// MP3数量
	private String bg_css = "";
	private String remark = "";//办公室简介
	private String userCount = "";// 会员数量
	private String userRole = "";
    private String productcount="";
    private String domainstr="";
    private String mallName="";
    private String officename="";//办公室名称
    private String content="";
    private String userNo="";
    private String crown="";
    private String sun="";
    private String moon="";
    private String star="";
    
    
	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getCrown() {
		return crown;
	}

	public void setCrown(String crown) {
		this.crown = crown;
	}

	public String getSun() {
		return sun;
	}

	public void setSun(String sun) {
		this.sun = sun;
	}

	public String getMoon() {
		return moon;
	}

	public void setMoon(String moon) {
		this.moon = moon;
	}

	public String getStar() {
		return star;
	}

	public void setStar(String star) {
		this.star = star;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOfficename() {
		return officename;
	}

	public void setOfficename(String officename) {
		this.officename = officename;
	}

	public String getDomainstr() {
		return domainstr;
	}

	public void setDomainstr(String domainstr) {
		this.domainstr = domainstr;
	}

	public String getMallName() {
		return mallName;
	}

	public void setMallName(String mallName) {
		this.mallName = mallName;
	}

	public String getProductcount() {
		return productcount;
	}

	public void setProductcount(String productcount) {
		this.productcount = productcount;
	}

	public String getOffice_id() {
		return office_id;
	}

	public void setOffice_id(String office_id) {
		this.office_id = office_id;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getClicks() {
		return clicks;
	}

	public void setClicks(String clicks) {
		this.clicks = clicks;
	}

	public String getTxtSigner() {
		return txtSigner;
	}

	public void setTxtSigner(String txtSigner) {
		this.txtSigner = txtSigner;
	}

	public String getImgLogo() {
		return ImgLogo;
	}

	public void setImgLogo(String imgLogo) {
		ImgLogo = imgLogo;
	}

	public String getImgLogo1() {
		return ImgLogo1;
	}

	public void setImgLogo1(String imgLogo1) {
		ImgLogo1 = imgLogo1;
	}

	public String getHfImage1() {
		return HfImage1;
	}

	public void setHfImage1(String hfImage1) {
		HfImage1 = hfImage1;
	}

	public String getArt_count() {
		return art_count;
	}

	public void setArt_count(String art_count) {
		this.art_count = art_count;
	}

	public String getVdieo_count() {
		return vdieo_count;
	}

	public void setVdieo_count(String vdieo_count) {
		this.vdieo_count = vdieo_count;
	}

	public String getMp3_count() {
		return mp3_count;
	}

	public void setMp3_count(String mp3_count) {
		this.mp3_count = mp3_count;
	}

	public String getBg_css() {
		return bg_css;
	}

	public void setBg_css(String bg_css) {
		this.bg_css = bg_css;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getUserCount() {
		return userCount;
	}

	public void setUserCount(String userCount) {
		this.userCount = userCount;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

}
