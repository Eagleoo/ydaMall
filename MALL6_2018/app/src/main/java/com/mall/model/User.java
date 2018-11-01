package com.mall.model;

import com.lidroid.xutils.db.annotation.Id;
import com.mall.util.Util;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class User implements java.io.Serializable {
	private static final long serialVersionUID = -4402753250095335903L;
	@Id
	public long id;
	private String userId = "";
	private String md5Pwd = "";
	private String userNo = "";
	// 推荐人
	private String inviter = "";
	// 招商人
	private String merchants = "";
	// 真是名字
	private String name = "";
	// 性别
	private String sex = "";
	// 用户等级
	private String level = "";
	private String levelId = "";
	// 显示会员角色
	private String userLevel = "";
	// 显示经营角色
	private String showLevel = "";
	// 区域，字符串
	private String zone = "";
	private String zoneId = "";
	// 业务账户
	private String circulate = "";
	// 赠送账户
	private String consume = "";
	// 手机
	private String mobilePhone = "";
	// 充值账户
	private String recharge = "";
	// 二级密码，MD5加密
	private String secondPwd = "";
	// 预存账户
	private String stored = "";
	// 权益账户
	private String interests = "";
	// 网店类型
	private String shopType = "";
	private String shopTypeId = "";
	// 身份证
	private String idNo = "";

	private String passport="";

	public String getPassport() {
		return passport;
	}

	public void setPassport(String passport) {
		this.passport = passport;
	}

	// 开户银行
	private String bank;
	// 卡号
	private String bankCard;
	// 账户名
	private String accountName;
	private String shangbi;
	private String qq;
	private String weixin;
	private String nickName;
	private String mail;
	private String userFace;
	private String isSite = "";
	private String loginDate = "";
	private String pwd = "";
	private String userName = "";
	private String ydhf = "";
	private String communityNickName = "";
	private String sessionId = "";
	private String shopCard1 = "-1.00";
	private String s1 = "";
	private String s2 = "";
	private String s3 = "";
	private String l5Date;
	private String zcb;
	private String hasStorered2;
	private String Red_Seed;
	private String Red_Cash;
	private String RegDate;
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	private String alipay;

	private String Is_dyr;

	private String reg_red_box; //新人 红包

	private String inviter_red_box; //分享 红包

	public String getReg_red_box() {
		return reg_red_box;
	}

	public void setReg_red_box(String reg_red_box) {
		this.reg_red_box = reg_red_box;
	}

	public String getInviter_red_box() {
		return inviter_red_box;
	}

	public void setInviter_red_box(String inviter_red_box) {
		this.inviter_red_box = inviter_red_box;
	}

	public String getIs_dyr() {
		return Is_dyr;
	}

	public void setIs_dyr(String is_dyr) {
		Is_dyr = is_dyr;
	}

	public String getAlipay() {
		return alipay;
	}

	public void setAlipay(String alipay) {
		this.alipay = alipay;
	}

	public String getRegDate() {
		return RegDate;
	}

	public void setRegDate(String RegDate) {
		this.RegDate = RegDate;
	}

	public String getRed_Cash() {
		return Red_Cash;
	}

	public void setRed_Cash(String Red_Cash) {
		this.Red_Cash = Red_Cash;
	}

	public String getRed_Seed() {
		return Red_Seed;
	}

	public void setRed_Seed(String Red_Seed) {
		this.Red_Seed = Red_Seed;
	}

	public String getCommunityNickName() {
		return communityNickName;
	}

	public void setCommunityNickName(String communityNickName) {
		this.communityNickName = communityNickName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getLoginDate() {
		return loginDate;
	}

	public void setLoginDate(String loginDate) {
		this.loginDate = loginDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getIsSite() {
		return isSite;
	}

	public void setIsSite(String isSite) {
		this.isSite = isSite;
	}

	public String getUserId() {
		return userId;
	}

	public String getUtf8UserId() {
		try {
			if (!Util.isNull(userId))
				return java.net.URLEncoder.encode(userId, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return userId;
	}

	public String getUserIdNoEncodByUTF8() {
		return userId;
	}

	public String getNoUtf8UserId() {
		if (!Util.isNull(userId))
			if (userId.contains("_?_"))
				return userId.replaceAll("_\\?_", "&");
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getMd5Pwd() {
		return md5Pwd;
	}

	public void setMd5Pwd(String md5Pwd) {
		this.md5Pwd = md5Pwd;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}

	public String getMerchants() {
		return merchants;
	}

	public void setMerchants(String merchants) {
		this.merchants = merchants;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getZone() {
		return zone;
	}

	public void setZone(String zone) {
		this.zone = zone;
	}

	public String getCirculate() {
		return circulate;
	}

	public void setCirculate(String circulate) {
		this.circulate = circulate;
	}

	public String getConsume() {
		return consume;
	}

	public void setConsume(String consume) {
		this.consume = consume;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getRecharge() {
		return recharge;
	}

	public void setRecharge(String recharge) {
		this.recharge = recharge;
	}

	public String getSecondPwd() {
		return secondPwd;
	}

	public void setSecondPwd(String secondPwd) {
		this.secondPwd = secondPwd;
	}

	public String getStored() {
		return stored;
	}

	public void setStored(String stored) {
		this.stored = stored;
	}

	public String getInterests() {
		return interests;
	}

	public void setInterests(String interests) {
		this.interests = interests;
	}

	public String getShopType() {
		return shopType;
	}

	public void setShopType(String shopType) {
		this.shopType = shopType;
	}

	public String getIdNo() {
		return idNo;
	}

	public void setIdNo(String idNo) {
		this.idNo = idNo;
	}

	public String getLevelId() {
		return levelId;
	}

	public void setLevelId(String levelId) {
		this.levelId = levelId;
	}

	public String getZoneId() {
		return zoneId;
	}

	public void setZoneId(String zoneId) {
		this.zoneId = zoneId;
	}

	public String getShopTypeId() {
		return shopTypeId;
	}

	public void setShopTypeId(String shopTypeId) {
		this.shopTypeId = shopTypeId;
	}

	public String getBank() {
		return bank;
	}

	public void setBank(String bank) {
		this.bank = bank;
	}

	public String getBankCard() {
		return bankCard;
	}

	public void setBankCard(String bankCard) {
		this.bankCard = bankCard;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getShangbi() {
		return shangbi;
	}

	public void setShangbi(String shangbi) {
		this.shangbi = shangbi;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getWeixin() {
		return weixin;
	}

	public void setWeixin(String weixin) {
		this.weixin = weixin;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getUserFace() {
		return userFace;
	}

	public void setUserFace(String userFace) {
		this.userFace = userFace;
	}

	public String getShowLevel() {
		return showLevel;
	}

	public void setShowLevel(String showLevel) {
		this.showLevel = showLevel;
	}

	public String getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(String userLevel) {
		this.userLevel = userLevel;
	}

	public String getYdhf() {
		return ydhf;
	}

	public void setYdhf(String ydhf) {
		this.ydhf = ydhf;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getZcb() {
		return zcb;
	}

	public void setZcb(String zcb) {
		this.zcb = zcb;
	}

	public String getShopCard1() {
		return shopCard1;
	}

	public void setShopCard1(String shopCard1) {
		this.shopCard1 = shopCard1;
	}

	/**
	 * 是否有购物卡2，是判断明细
	 * 
	 * @return
	 */
	public String getHasStorered2() {
		return hasStorered2;
	}

	public void setHasStorered2(String hasStorered2) {
		this.hasStorered2 = hasStorered2;
	}

	public String getL5Date() {
		return l5Date;
	}

	public void setL5Date(String l5Date) {
		this.l5Date = l5Date;
	}

	public boolean l5DateIs2015_06_10after() {
		try {
			Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse(l5Date + " 00:00:01");
			Date _20150610 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
					.parse("2015-06-10 00:00:00");
			return (date.getTime() > _20150610.getTime());
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getS1() {
		return s1;
	}

	public void setS1(String s1) {
		this.s1 = s1;
	}

	public String getS2() {
		return s2;
	}

	public void setS2(String s2) {
		this.s2 = s2;
	}

	public String getS3() {
		return s3;
	}

	public void setS3(String s3) {
		this.s3 = s3;
	}

}
