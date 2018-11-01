package com.mall.serving.query.configs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.model.ExpressageCompanyInfo;
import com.mall.view.App;
import com.mall.view.R;

public class QueryConfigs {

	private static Map<Integer, Integer> weatherMap;

	public static Map<Integer, Integer> getWeatherMap() {
		if (weatherMap == null) {
			weatherMap = getWeatherMapList();
		}
		return weatherMap;

	}

	private static Map<Integer, Integer> getWeatherMapList() {
		if (weatherMap == null || weatherMap.size() == 0) {
			weatherMap = new HashMap<Integer, Integer>();
			weatherMap.put(0, R.drawable.query_weather_fine);
			weatherMap.put(1, R.drawable.query_weather_cloud);
			weatherMap.put(2, R.drawable.query_weather_cloudy);
			weatherMap.put(3, R.drawable.query_weather_rain_3);
			weatherMap.put(4, R.drawable.query_weather_rain_3);
			weatherMap.put(5, R.drawable.query_weather_rain_2);
			weatherMap.put(6, R.drawable.query_weather_snow);
			weatherMap.put(7, R.drawable.query_weather_rain);
			weatherMap.put(8, R.drawable.query_weather_rain_1);
			weatherMap.put(9, R.drawable.query_weather_rain_2);
			weatherMap.put(10, R.drawable.query_weather_rain_2);
			weatherMap.put(11, R.drawable.query_weather_rain_2);
			weatherMap.put(12, R.drawable.query_weather_rain_2);
			weatherMap.put(13, R.drawable.query_weather_snow_1);
			weatherMap.put(14, R.drawable.query_weather_snow_1);
			weatherMap.put(15, R.drawable.query_weather_snow);
			weatherMap.put(16, R.drawable.query_weather_snow);
			weatherMap.put(17, R.drawable.query_weather_snow);
			weatherMap.put(18, R.drawable.query_weather_snow);
			weatherMap.put(19, R.drawable.query_weather_rain_2);
			weatherMap.put(20, R.drawable.query_weather_dust);
			weatherMap.put(21, R.drawable.query_weather_rain);
			weatherMap.put(22, R.drawable.query_weather_rain_1);
			weatherMap.put(23, R.drawable.query_weather_rain_2);
			weatherMap.put(24, R.drawable.query_weather_rain_3);
			weatherMap.put(25, R.drawable.query_weather_flash);
			weatherMap.put(26, R.drawable.query_weather_snow_1);
			weatherMap.put(27, R.drawable.query_weather_snow);
			weatherMap.put(28, R.drawable.query_weather_snow);
			weatherMap.put(29, R.drawable.query_weather_wind);
			weatherMap.put(30, R.drawable.query_weather_dust);
			weatherMap.put(31, R.drawable.query_weather_dust);
		}
		return weatherMap;
	}

	public static void saveExpressageCom() {

		try {
			long count = DbUtils.create(App.getContext()).count(
					ExpressageCompanyInfo.class);

			if (count < 1) {

				final List<ExpressageCompanyInfo> list = getExpressageComMap();
				Util.asynTask(new IAsynTask() {
					
					@Override
					public void updateUI(Serializable runData) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public Serializable run() {
						try {
							DbUtils.create(App.getContext()).saveAll(list);
						} catch (DbException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						return null;
					}
				});
				
			}

		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static List<ExpressageCompanyInfo> getExpressageComMap() {

		List<ExpressageCompanyInfo> list = new ArrayList<ExpressageCompanyInfo>();

		list.add(new ExpressageCompanyInfo("澳大利亚邮政", "auspost"));
		list.add(new ExpressageCompanyInfo("AAE", "aae"));
		list.add(new ExpressageCompanyInfo("安信达", "anxindakuaixi"));
		list.add(new ExpressageCompanyInfo("汇通", "huitongkuaidi"));
		list.add(new ExpressageCompanyInfo("百福东方", "baifudongfang"));
		list.add(new ExpressageCompanyInfo("BHT", "bht"));
		list.add(new ExpressageCompanyInfo("邮政", "youzhengguonei"));
		list.add(new ExpressageCompanyInfo("邦送物流", "bangsongwuliu"));
		list.add(new ExpressageCompanyInfo("希伊艾斯", "cces"));
		list.add(new ExpressageCompanyInfo("中国东方", "coe"));
		list.add(new ExpressageCompanyInfo("传喜物流", "chuanxiwuliu"));
		list.add(new ExpressageCompanyInfo("加拿大邮政", "canpost"));
		list.add(new ExpressageCompanyInfo("大田物流", "datianwuliu"));
		list.add(new ExpressageCompanyInfo("德邦物流", "debangwuliu"));
		list.add(new ExpressageCompanyInfo("DPEX", "dpex"));
		list.add(new ExpressageCompanyInfo("DHL", "dhl"));
		list.add(new ExpressageCompanyInfo("D速快递", "dhlde"));
		list.add(new ExpressageCompanyInfo("递四方", "disifang"));
		list.add(new ExpressageCompanyInfo("EMS", "ems"));
		list.add(new ExpressageCompanyInfo("Fedex", "fedex"));
		list.add(new ExpressageCompanyInfo("飞康达物流", "feikangda"));
		list.add(new ExpressageCompanyInfo("飞快达", "feikuaida"));

		list.add(new ExpressageCompanyInfo("风行天下", "fengxingtianxia"));
		list.add(new ExpressageCompanyInfo("飞豹快递", "feibaokuaidi"));
		list.add(new ExpressageCompanyInfo("港中能达", "ganzhongnengda"));
		list.add(new ExpressageCompanyInfo("国通", "guotongkuaidi"));
		list.add(new ExpressageCompanyInfo("广东邮政", "guangdongyouzhengwuliu"));
		list.add(new ExpressageCompanyInfo("GLS", "gls"));
		list.add(new ExpressageCompanyInfo("共速达", "gongsuda"));
		list.add(new ExpressageCompanyInfo("汇通", "huitongkuaidi"));
		list.add(new ExpressageCompanyInfo("汇强", "huiqiangkuaidi"));
		list.add(new ExpressageCompanyInfo("华宇", "tiandihuayu"));
		list.add(new ExpressageCompanyInfo("恒路", "hengluwuliu"));
		list.add(new ExpressageCompanyInfo("华夏龙", "huaxialongwuliu"));
		
		list.add(new ExpressageCompanyInfo("海外环球", "haiwaihuanqiu"));
		list.add(new ExpressageCompanyInfo("河北建华", "hebeijianhua"));
		list.add(new ExpressageCompanyInfo("海盟速递", "haimengsudi"));
		list.add(new ExpressageCompanyInfo("华企快运", "huaqikuaiyun"));
		list.add(new ExpressageCompanyInfo("山东海红", "haihongwangsong"));
		list.add(new ExpressageCompanyInfo("佳吉物流", "jiajiwuliu"));
		list.add(new ExpressageCompanyInfo("佳怡物流", "jiayiwuliu"));
		list.add(new ExpressageCompanyInfo("加运美", "jiayunmeiwuliu"));
		list.add(new ExpressageCompanyInfo("京广", "jinguangsudikuaijian"));
		list.add(new ExpressageCompanyInfo("急先达", "jixianda"));
		list.add(new ExpressageCompanyInfo("晋越", "jinyuekuaidi"));
		list.add(new ExpressageCompanyInfo("捷特", "jietekuaidi"));
		list.add(new ExpressageCompanyInfo("金大", "jindawuliu"));
		list.add(new ExpressageCompanyInfo("嘉里大通", "jialidatong"));
		list.add(new ExpressageCompanyInfo("快捷速递", "kuaijiesudi"));
		list.add(new ExpressageCompanyInfo("康力物流", "kangliwuliu"));
		list.add(new ExpressageCompanyInfo("跨越物流", "kuayue"));
		list.add(new ExpressageCompanyInfo("联昊通", "lianhaowuliu"));
		list.add(new ExpressageCompanyInfo("龙邦物流", "longbanwuliu"));
		list.add(new ExpressageCompanyInfo("蓝镖快递", "lanbiaokuaidi"));
		list.add(new ExpressageCompanyInfo("乐捷递", "lejiedi"));
		list.add(new ExpressageCompanyInfo("联邦快递", "lianbangkuaidi"));
		list.add(new ExpressageCompanyInfo("立即送", "lijisong"));
		list.add(new ExpressageCompanyInfo("隆浪快递", "longlangkuaidi"));
		list.add(new ExpressageCompanyInfo("门对门", "menduimen"));
		list.add(new ExpressageCompanyInfo("美国快递", "meiguokuaidi"));
		list.add(new ExpressageCompanyInfo("明亮物流", "mingliangwuliu"));
		list.add(new ExpressageCompanyInfo("OCS", "ocs"));
		list.add(new ExpressageCompanyInfo("onTrac", "ontrac"));
		list.add(new ExpressageCompanyInfo("全晨快递", "quanchenkuaidi"));
		list.add(new ExpressageCompanyInfo("全际通", "quanjitong"));
		list.add(new ExpressageCompanyInfo("全日通", "quanritongkuaidi"));
		list.add(new ExpressageCompanyInfo("全一快递", "quanyikuaidi"));
		list.add(new ExpressageCompanyInfo("全峰快递", "quanfengkuaidi"));
		list.add(new ExpressageCompanyInfo("七天连锁", "sevendays"));
		list.add(new ExpressageCompanyInfo("如风达", "rufengda"));
		list.add(new ExpressageCompanyInfo("申通", "shentong"));
		list.add(new ExpressageCompanyInfo("顺丰", "shunfeng"));
		list.add(new ExpressageCompanyInfo("三态速递", "santaisudi"));
		list.add(new ExpressageCompanyInfo("盛辉", "shenghuiwuliu"));
		list.add(new ExpressageCompanyInfo("速尔", "suer"));
		list.add(new ExpressageCompanyInfo("盛丰", "shengfengwuliu"));
		list.add(new ExpressageCompanyInfo("上大", "shangda"));
		list.add(new ExpressageCompanyInfo("山东海红", "haihongwangsong"));
		list.add(new ExpressageCompanyInfo("赛澳递", "saiaodi"));
		list.add(new ExpressageCompanyInfo("山西红马甲", "sxhongmajia"));
		list.add(new ExpressageCompanyInfo("圣安", "shenganwuliu"));
		list.add(new ExpressageCompanyInfo("穗佳", "suijiawuliu"));
		list.add(new ExpressageCompanyInfo("华宇", "tiandihuayu"));
		list.add(new ExpressageCompanyInfo("天天", "tiantian"));
		list.add(new ExpressageCompanyInfo("TNT", "tnt"));
		list.add(new ExpressageCompanyInfo("通和天下", "tonghetianxia"));
		list.add(new ExpressageCompanyInfo("UPS", "ups"));
		list.add(new ExpressageCompanyInfo("优速物流", "youshuwuliu"));
		list.add(new ExpressageCompanyInfo("USPS", "usps"));
		list.add(new ExpressageCompanyInfo("万家", "wanjiawuliu"));
		list.add(new ExpressageCompanyInfo("万象", "wanxiangwuliu"));
		list.add(new ExpressageCompanyInfo("微特派", "weitepai"));
		list.add(new ExpressageCompanyInfo("新邦", "xinbangwuliu"));
		list.add(new ExpressageCompanyInfo("信丰", "xinfengwuliu"));
		list.add(new ExpressageCompanyInfo("新邦", "xinbangwuliu"));
		list.add(new ExpressageCompanyInfo("新蛋奥硕物流", "neweggozzo"));
		list.add(new ExpressageCompanyInfo("香港邮政", "hkpost"));
		list.add(new ExpressageCompanyInfo("圆通", "yuantong"));
		list.add(new ExpressageCompanyInfo("韵达", "yunda"));
		list.add(new ExpressageCompanyInfo("运通", "yuntongkuaidi"));
		list.add(new ExpressageCompanyInfo("远成", "yuanchengwuliu"));
		list.add(new ExpressageCompanyInfo("亚风", "yafengsudi"));
		list.add(new ExpressageCompanyInfo("一邦", "yibangwuliu"));
		list.add(new ExpressageCompanyInfo("优速", "youshuwuliu"));
		list.add(new ExpressageCompanyInfo("源伟丰", "yuanweifeng"));
		list.add(new ExpressageCompanyInfo("元智捷诚", "yuanzhijiecheng"));
		list.add(new ExpressageCompanyInfo("越丰", "yuefengwuliu"));
		list.add(new ExpressageCompanyInfo("源安达", "yuananda"));
		list.add(new ExpressageCompanyInfo("原飞航", "yuanfeihangwuliu"));
		list.add(new ExpressageCompanyInfo("忠信达", "zhongxinda"));
		list.add(new ExpressageCompanyInfo("芝麻开门", "zhimakaimen"));
		list.add(new ExpressageCompanyInfo("银捷速递", "yinjiesudi"));
		list.add(new ExpressageCompanyInfo("一统飞鸿", "yitongfeihong"));
		list.add(new ExpressageCompanyInfo("中通", "zhongtong"));
		list.add(new ExpressageCompanyInfo("宅急送", "zhaijisong"));
		list.add(new ExpressageCompanyInfo("中邮", "zhongyouwuliu"));
		list.add(new ExpressageCompanyInfo("中速", "zhongsukuaidi"));
		list.add(new ExpressageCompanyInfo("中天", "zhongtianwanyun"));
		return list;

	}

	public static int getWeatherRid(String str) {
		int img = Util.getInt(str);
		if (img >= getWeatherMap().size()) {
			img = 0;
		}

		Integer integer = getWeatherMap().get(img);
		return integer;

	}
}
