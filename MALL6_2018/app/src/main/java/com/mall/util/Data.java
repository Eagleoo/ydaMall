package com.mall.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.mall.model.BannerInfo;
import com.mall.model.BusinessCircleCityData;
import com.mall.model.BusinessCircleCityName;
import com.mall.model.Category;
import com.mall.model.Images;
import com.mall.model.MainProduct;
import com.mall.model.Message;
import com.mall.model.ShopM;
import com.mall.model.User;
import com.mall.model.Zone;
import com.mall.net.Web;
import com.mall.view.App;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * 功能： 数据类<br>
 * 时间： 2013-3-17<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
public class Data {
	
	static Web web;
	private static List<Category> classList = null;
	private static List<Category> twoList = new ArrayList<Category>();
	private static List<Category> threeList = new ArrayList<Category>();
	private static List<Zone> shen = null;
	public static List<Images> imgList = new ArrayList<Images>();
	private static List<MainProduct> shangjia = null;
	private static int productClass = 0;
	private static List<ShopM> myShopM;

	/**
	 * 得到商品类别（1为常规区，11为商币兑换区）
	 * 
	 * @return
	 */
	public static int getProductClass() {
		return productClass;
	}

	/**
	 * 设置商品类别（0为常规区，11为商币兑换区）
	 * 
	 * @param productClass
	 */
	public static void setProductClass(int productClass) {
		Data.productClass = productClass;
	}

	public static List<Images> getImgList(Context context) {
		
		return imgList;
	}

	public static void setClassList(List<Category> classList) {
		Data.classList = classList;
	}

	public static List<Category> getClassList(Activity frame) {
		if (null != classList && 0 != classList.size())
			return classList;
		DbUtils db = DbUtils.create(frame);
		try {
			classList = db.findAll(Selector.from(Category.class));
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		if (null == classList || 0 == classList.size()) {
			classList = new Web(Web.getAllClass, "parentID=")
					.getList(Category.class);
			try {
				db.saveAll(classList);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return classList;
	}

	public static boolean addMyShopMSc(Activity frame, ShopM m) {
		if (null == myShopM)
			getMyShopMSc(frame);
		User user = UserData.getUser();
		if(null != user)
			m.setUserid(user.getNoUtf8UserId());
		else{
			return false;
		}			
		DbUtils db = DbUtils.create(frame);
		try {
			ShopM shopm = db.findById(ShopM.class, m.getId());
			if (null == shopm) {
				db.save(m);
				myShopM.add(m);
			} else {
				db.update(m);
				ShopM oldM = null;
				for (ShopM om : myShopM) {
					if (om.getId().equals(m.getId())) {
						oldM = om;
						break;
					}
				}
				if (null != oldM) {
					myShopM.remove(oldM);
					myShopM.add(m);
				}
			}
			return true;
		} catch (DbException e1) {
			e1.printStackTrace();
			try {
				db.dropTable(ShopM.class);
				addMyShopMSc(frame, m);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static List<ShopM> getMyShopMSc(Activity frame) {
		if (null != myShopM && 0 != myShopM.size())
			return myShopM;
		DbUtils db = DbUtils.create(frame);
		try {
			myShopM = db.findAll(Selector.from(ShopM.class));
		} catch (DbException e1) {
			e1.printStackTrace();
		}
		if (null == myShopM)
			myShopM = new ArrayList<ShopM>();
		db = null;
		return myShopM;
	}

	public static List<Category> getThreeCategory() {
		if (null != threeList && 0 != threeList.size())
			return threeList;
		for (Category o : twoList) {
			for (Category t : classList) {
				if (o.getCategoryId().equals(t.getCategoryParentID())
						&& !"活动专区".equals(t.getCategoryName())
						&& !"业务拓展资料".equals(t.getCategoryName()))
					threeList.add(t);
			}
		}
		return threeList;
	}

	public static List<Category> getThreeCategory(String idOrName) {

		List<Category> threeList = new ArrayList<Category>();
		boolean isID = Util.isInt(idOrName);
		for (Category o : twoList) {
			for (Category t : classList) {
				if (isID && t.getCategoryParentID().equals(idOrName)
						&& !"活动专区".equals(t.getCategoryName())
						&& !"业务拓展资料".equals(t.getCategoryName()))
					threeList.add(t);
				if (!isID && o.getCategoryName().equals(idOrName)
						&& o.getCategoryId().equals(t.getCategoryParentID())
						&& !"活动专区".equals(t.getCategoryName())
						&& !"业务拓展资料".equals(t.getCategoryName()))
					threeList.add(t);
			}
		}
		return threeList;
	}

	public static List<Category> getTwoCategory() {

		if (null != twoList && 0 != twoList.size())
			return twoList;
		List<Category> oneList = new ArrayList<Category>();
		for (Category c : classList) {
			if ("-1".equals(c.getCategoryParentID()))
				oneList.add(c);
		}
		for (Category o : oneList) {
			for (Category t : classList) {
				if (o.getCategoryId().equals(t.getCategoryParentID())
						&& !"活动专区".equals(t.getCategoryName().trim())
						&& !"业务拓展资料".equals(t.getCategoryName()))
					twoList.add(t);
			}
		}
		return twoList;
	}

	public static List<Category> getTwoCategory(String cateid) {
		List<Category> list_two = new ArrayList<Category>();
		for (Category t : classList) {
			if ((cateid + "").equals(t.getCategoryParentID())
					&& !"活动专区".equals(t.getCategoryName().trim())
					&& !"业务拓展资料".equals(t.getCategoryName()))
				list_two.add(t);
		}
		return list_two;
	}

	public static void resetCategory() {
		classList = null;
	}

	public static List<Message> getMessage(Activity frame) {
		List<Message> m1List = new Web(Web.getAllMessage, "pageSize=20&page=1")
				.getList(Message.class);
		if (null == m1List){
			m1List = new ArrayList<Message>();
		} 
		return m1List;
	}

	
	public static List<Zone> getShen() {
		if (null == shen) {
			DbUtils db = DbUtils.create(App.getContext());
			try{
				shen = db.findAll(Selector.from(Zone.class).where("parentId", "=", "-1"));
			}catch(DbException ee){
				ee.printStackTrace();
			}
			if(null == shen || 0 >= shen.size()){
				Web web = new Web(Web.getZoneByParent, "zoneId=0");
				shen = web.getList(Zone.class);
				try {
					db.saveOrUpdateAll(shen);
				} catch (DbException e) {
					e.printStackTrace();
				}
			}
		}
		return shen;
	}

	public static void setShen(List<Zone> shen) {
		Data.shen = shen;
	}


	public static void savebanner(BannerInfo bannerInfo){


		Util.download( bannerInfo);

	}

	public static  BannerInfo  getbanner(){
		DbUtils db = DbUtils.create(App.getContext());
		BannerInfo bannerInfo=null;
		try {
			Log.e("getTime","14"+(db==null));
			bannerInfo=db.findFirst(Selector.from(BannerInfo.class));

		} catch (DbException e) {
			e.printStackTrace();
			Log.e("getTime","16"+e.toString());
		}
		return  bannerInfo;
	}

	public static  void   clearbanner(){
		DbUtils db = DbUtils.create(App.getContext());
		BannerInfo bannerInfo=null;
		try {

			db.deleteAll(BannerInfo.class);
		} catch (DbException e) {
			e.printStackTrace();
			Log.e("getTime","16"+e.toString());
		}
	}

	public static void saveTime(){
		Log.e("getTime","1");
		DbUtils db = DbUtils.create(App.getContext());
		Log.e("getTime","2");
		try {
			Log.e("getTime","3");
			db.findAll(Selector.from(BusinessCircleCityData.class));
		} catch (DbException e) {
			Log.e("getTime","4"+e.toString());
			e.printStackTrace();
		}
		Log.e("getTime","5");
		BusinessCircleCityData data=new BusinessCircleCityData();
		Log.e("getTime","6");
		data.setId("1");
		Log.e("getTime","7");
		Date curDate    =   new Date(System.currentTimeMillis());//获取当前时间
		Log.e("getTime","8");
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Log.e("getTime","9");
		String    time    =    df.format(curDate);
		Log.e("getTime","10");
		data.setUptime(time);
		Log.e("getTime","11");
		try {
			Log.e("getTime","12");
			db.saveOrUpdate(data);
		} catch (DbException e) {
			Log.e("getTime","13"+e.toString());
			e.printStackTrace();
		}
	}

	public static String  getTime(){
		String time="";
		Log.e("getTime","1");
		DbUtils db = DbUtils.create(App.getContext());

		try {
			Log.e("getTime","14");
			BusinessCircleCityData businessCircleCityData=db.findFirst(Selector.from(BusinessCircleCityData.class));
			Log.e("getTime","15");
			Log.e("数据",businessCircleCityData.getUptime());
			time=businessCircleCityData.getUptime();
		} catch (DbException e) {
			e.printStackTrace();
			Log.e("getTime","16"+e.toString());
		}
		return  time;
	}


	public static boolean isUpe(){
		boolean isupdata=false;

	String time1=	getTime();
		MyTime myTime=	Util.getshengyutime(time1);
		Log.e("天数差","myTime.getDay()"+myTime.getDay());
					if (myTime.getDay()>=1){
				isupdata=true;
			}

		return isupdata;
	}

	public static ArrayList<BusinessCircleCityName> getCityid(final Context context,boolean isupdata){
		List<BusinessCircleCityName> cityNames=new ArrayList<>();
		cityNames.clear();
		if (web==null) {
			web=new Web();
		}

		DbUtils db = DbUtils.create(App.getContext());
		if (Web.test_url.equals(Web.url) || Web.test_url2.equals(Web.url)) {
			Log.e("环境检查","当前是测试版本");
			web.setRequest(Web.getAllUserMessagecity, "num="+ "1","UTF-8");
			cityNames=web.getList(BusinessCircleCityName.class);
			try {
				db.saveOrUpdateAll(cityNames);
				saveTime();
			} catch (DbException e) {
				e.printStackTrace();
				Log.e("数据更新异常",e.toString()+"LL");
			}
		}else{
			Log.e("环境检查","当前是" +
					"正式版本");
			try {
				cityNames = db.findAll(Selector.from(BusinessCircleCityName.class));
//			if (isupdata){
//				db.dropTable(BusinessCircleCityName.class);
//			}

			} catch (Exception e) {
				e.printStackTrace();
			}

			if (cityNames==null||cityNames.size()==0){
				Log.e("当本地没有缓存的时候请求网络数据","true");
				web.setRequest(Web.getAllUserMessagecity, "num="+ "1","UTF-8");
				cityNames=web.getList(BusinessCircleCityName.class);
				try {
					db.saveOrUpdateAll(cityNames);
					saveTime();
				} catch (DbException e) {
					e.printStackTrace();
					Log.e("数据更新异常",e.toString()+"LL");
				}
			}else{
				if (isUpe()){
					Log.e("超过一天请求网络更新数据","true");
					web.setRequest(Web.getAllUserMessagecity, "num="+ "1","UTF-8");
					cityNames=web.getList(BusinessCircleCityName.class);
					try {
						db.saveOrUpdateAll(cityNames);
						saveTime();
					} catch (DbException e) {
						e.printStackTrace();
					}
				}else{
					Log.e("在一天之内读取本地数据","true");

				}
			}

		}

			return (ArrayList<BusinessCircleCityName>) cityNames;

	}

//	private static void  Uptime(){
//		BusinessCircleCityData data=new BusinessCircleCityData();
//		data.setId(1);
//		Date    curDate    =   new Date(System.currentTimeMillis());//获取当前时间]
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String    time    =    df.format(curDate);
//		data.setUptime(time);
//		DbUtils db = DbUtils.create(App.getContext());
//
//		try {
//			db.saveOrUpdate(data);
//			BusinessCircleCityData dbFirst = db.findFirst(Selector.from(BusinessCircleCityData.class));
//			Log.e("更新时间",dbFirst.getUptime()+"LL");
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
//
//	}


//	private static boolean  checkUptime(){
//		boolean isupdata=false;
//		DbUtils db = DbUtils.create(App.getContext());
//		try {
//
//			BusinessCircleCityData dbFirst = db.findFirst(Selector.from(BusinessCircleCityData.class));
//			if (Util.isNull(dbFirst.getUptime())){
//				isupdata=false
//				return
//			}
//			String time1=dbFirst.getUptime();
//			Log.e("保存的时间",time1+"LL");
//			MyTime myTime=	Util.getshengyutime(time1);
//			if (myTime.getDay()>=1){
//				isupdata=true;
//			}
//
//		} catch (DbException e) {
//			e.printStackTrace();
//		}
//return isupdata;
//	}




	public static List<Zone> getShi(String zoneid) {
		if (web==null) {
			web=new Web();
		}
		DbUtils db = DbUtils.create(App.getContext());
		List<Zone> shi = new ArrayList<Zone>();
		try{
			shi = db.findAll(Selector.from(Zone.class).where("parentId", "=", zoneid));
		}catch(DbException ee){
			ee.printStackTrace();
		}
		if(null == shi || 0 >= shi.size()){
			web.setRequest(Web.getZoneByParent, "zoneId=" + zoneid,"UTF-8");
//			Web web = new Web(Web.getZoneByParent, "zoneId=" + zoneid);
			shi = web.getList(Zone.class);
			try {
				db.saveOrUpdateAll(shi);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return shi;
	}

	public static List<Zone> getQu(String zoneid) {
		DbUtils db = DbUtils.create(App.getContext());
		List<Zone> qu = new ArrayList<Zone>();
		try{
			qu = db.findAll(Selector.from(Zone.class).where("parentId", "=", zoneid));
		}catch(DbException ee){
			ee.printStackTrace();
		}
		if(null == qu || 0 >= qu.size()){
			Web web = new Web(Web.getZoneByParent, "zoneId=" + zoneid);
			qu = web.getList(Zone.class);
			try {
				db.saveOrUpdateAll(qu);
			} catch (DbException e) {
				e.printStackTrace();
			}
		}
		return qu;
	}

	public static Zone getZone(String zoneidOrZoneName) {
		DbUtils db = DbUtils.create(App.getContext());
		Zone zone = null;
		try{
			zone = db.findFirst(Selector.from(Zone.class).where("zoneid", "=", zoneidOrZoneName).or("zoneName", "like","%"+zoneidOrZoneName+"%"));
		}catch(DbException ee){
			ee.printStackTrace();
		}
		return zone;
	}
	
	public static List<MainProduct> getMainTuiJian(String tj) {
		if ("".equals(tj) && null != shangjia)
			return shangjia;
		Web web = new Web(Web.getPY, "tuijian=" + tj + "&size=15&cateId=419");
		shangjia = web.getList(MainProduct.class);
		for (final MainProduct main : shangjia) {
			String href = main.getImage();
			href = href.replace(Web.webImage, Web.webServer);
			final String name = href.substring(href.lastIndexOf("."));
			final String path = Util.proPath + main.getId() + name;
			if (!new File(path).exists()) {
				try {
					Util.downLoad(href, path);
					main.setBitmap(Util.getLocalBitmap(path));
				} catch (IOException e) {
					main.setBitmap(Util.getBitmap(href));
				}
			} else
				main.setBitmap(Util.getLocalBitmap(path));
			main.setImage(href);
		}
		return shangjia;
	}
}
