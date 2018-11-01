package com.mall.util;

import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mall.model.ShopOfficeInfo;
import com.mall.model.User;
import com.mall.view.App;

public class UserData {
	private static User user;
	private static String uName;
	private static String upwd;
	private static int ver = 2;
	private static DbUtils dbUtils = DbUtils.create(App.getContext());
	private static ShopOfficeInfo officeInfo;

	public static void setOfficeInfo(ShopOfficeInfo o) {
		UserData.officeInfo = o;
	}

	public static ShopOfficeInfo getOfficeInfo() {
		return officeInfo;
	}

	public static void setUser(User user) {
		try {
			dbUtils.deleteAll(User.class);
			if (null != user){
				try {
					dbUtils.save(user);
				} catch (DbException e) {
					dbUtils.dropTable(User.class);
					dbUtils.save(user);
					e.printStackTrace();
				}
			}
		} catch (DbException e) {
			e.printStackTrace();
		}
		UserData.user = user;
	}

	public static User getUser() {
		if (null == user) {
			try {
				List<User> list = dbUtils.findAll(User.class);
				user =  (null == list || 0 == list.size() ? null : list.get(0));
				if(null != user && Util.isNull(user.getUserNo()) && Util.isNull(user.getSessionId())){
					dbUtils.deleteAll(User.class);
					user = null;
				}
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return user;
	}

	public static String getuName() {
		return uName;
	}

	public static void setuName(String uName) {
		UserData.uName = uName;
	}

	public static String getUpwd() {
		return upwd;
	}

	public static void setUpwd(String upwd) {
		UserData.upwd = upwd;
	}

	public static int getVer() {
		return ver;
	}

	public static void setVer(int ver) {
		UserData.ver = ver;
	}

}
