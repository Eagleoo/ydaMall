package com.mall.serving.voip.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;

import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

public class VoipPhoneUtils {

	/*
	 * 【最新中国手机号段大全】移动：134、135、136、137、138、139、150、
	 * 151、152、157、158、159、182、183、184
	 * 187、188、178(4G)、147(上网卡)；联通：130、131、132、155、
	 * 156、185、186、176(4G)、145(上网卡)； 电信：133、153、180、181、189
	 * 、177(4G)；卫星通信：1349；虚拟运营商：170。
	 */
	// private static String[] heads = { "130", "131", "132", "133", "134",
	// "135", "136", "137", "138", "139", "147", "150", "151", "152",
	// "153", "155", "156", "157", "158", "159", "180", "152", "187", "188",
	// "189" };

	// private static String fileName = "local.dat";
	// private static String TAG = "LocalNameFinder";
	private static ArrayList<NationalTable> ntiCodeList;
	private static ArrayList<AreaTable> strCodeList;
	private static ArrayList<NumHeadItem> numHeadItemList;
	private static ArrayList<NumItem> numItemList;
	private static String currentNumHead;
	public static HashMap<String, ArrayList<NumItem>> map = new HashMap<String, ArrayList<NumItem>>();

	/**
	 * 根据电话号码查找归属地名称
	 * 
	 * @param phoneNum
	 *            电话号码
	 * @param isAreaCode
	 *            是否为区号
	 * @return 归属地名称
	 */
	public static synchronized String findLocalName(String phoneNum,
			boolean isAreaCode, Context context) {
		if (phoneNum == null || phoneNum.length() < 3) {
			return "";
		}
		phoneNum = formatFreeCallNum(phoneNum);
		if (phoneNum == null) {
			return "";
		}
		// CustomLog.v(TAG, "====phoneNum==:" + phoneNum.substring(0, 5));
		// System.out.println("find:"+phoneNum+" local name");
		if (strCodeList == null) {
			strCodeList = new ArrayList<AreaTable>(500);
			ntiCodeList = new ArrayList<NationalTable>(300);
			numHeadItemList = new ArrayList<NumHeadItem>(30);
			numItemList = new ArrayList<NumItem>(10000);
			initData(context);
		}

		if (phoneNum.startsWith("+86")) {
			phoneNum = phoneNum.substring(3);
		} else if (phoneNum.startsWith("86")) {
			phoneNum = phoneNum.substring(2);
		}

		if (phoneNum.startsWith("0")) { // 处理区号
			if (phoneNum.startsWith("00")) {
				String pString = interNational(phoneNum);
				if (pString == "" || pString.equals("")) {
					if (phoneNum.length() >= 5) {
						String phoneNation = phoneNum.substring(0, 5);
						if (phoneNation.equals("00852")) {
							return "中国香港";
						} else if (phoneNation.equals("00853")) {
							return "中国澳门";
						} else if (phoneNation.equals("00886")) {
							return "中国台湾";
						}
					} else {
						return pString;
					}
				} else {
					// 国际区号处理
					return pString;
				}
			} else

			if (phoneNum.startsWith("010") || phoneNum.startsWith("02")) {
				try {
					// 为手机号码
					short head = (short) Integer.parseInt(phoneNum.substring(1,
							3));
					int idx = -1;
					for (int i = 0; i < strCodeList.size(); i++) {
						if (strCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						return "";
					}
					// 二分查找
					// short num = (short)
					// Integer.parseInt(phoneNum.substring(1, 3));
					// int pos = binarySearch(num, head);

					String locaName = strCodeList.get(idx).name;
					short locaCode = strCodeList.get(idx).code;
					if (isAreaCode) {
						return "" + locaCode;
					} else {
						return locaName;
					}

				} catch (Exception e) {
				}
			} else if (phoneNum.startsWith("03") || phoneNum.startsWith("07")
					|| phoneNum.startsWith("05") || phoneNum.startsWith("06")
					|| phoneNum.startsWith("04") || phoneNum.startsWith("08")
					|| phoneNum.startsWith("09")) {
				try {
					//
					short head = (short) Integer.parseInt(phoneNum.substring(1,
							4));
					int idx = -1;
					for (int i = 0; i < strCodeList.size(); i++) {
						if (strCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						return "";
					}
					// 二分查找
					// short num = (short)
					// Integer.parseInt(phoneNum.substring(1, 4));
					// int pos = binarySearch(num, head);

					String locaName = strCodeList.get(idx).name;
					return locaName;
				} catch (Exception e) {
				}
			} else {
				// 为手机号码
				if (phoneNum.length() < 7) {
					return "";
				}
				try {
					// 为手机号码
					short head = (short) Integer.parseInt(phoneNum.substring(1,
							4));
					int idx = -1;
					for (int i = 0; i < numHeadItemList.size(); i++) {
						if (numHeadItemList.get(i).headName == head) {
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						return "";
					}

					// TODO 还可以边读取，边查找
					readPro(numHeadItemList.get(idx).startPos,
							numHeadItemList.get(idx).length, head, context);
					// 二分查找
					short num = (short) Integer.parseInt(phoneNum.substring(4,
							8));

					// System.out.println(num);

					int pos = binarySearch(num, head);

					// System.out.println(numItemList.get(pos).num);

					String locaName = strCodeList
							.get(numItemList.get(pos).index).name;
					short locaCode = strCodeList
							.get(numItemList.get(pos).index).code;
					// System.out.println(locaName+","+locaCode);
					if (isAreaCode) {
						return "" + locaCode;
					} else {
						return locaName;
					}

				} catch (Exception e) {
					// e.printStackTrace();
				}
			}
		} else { // 为手机号码
			if (phoneNum.length() < 7) {
				return "";
			}
			if (phoneNum.startsWith("17909") || phoneNum.startsWith("17951")
					|| phoneNum.startsWith("17911")
					|| phoneNum.startsWith("12593")) {
				phoneNum = phoneNum.substring(5);
			}
			try {
				// 为手机号码
				short head = (short) Integer.parseInt(phoneNum.substring(0, 3));
				int idx = -1;
				for (int i = 0; i < numHeadItemList.size(); i++) {
					if (numHeadItemList.get(i).headName == head) {
						// CustomLog.i("other","phone num = "+phoneNum+" head = "+head);
						idx = i;
						break;
					}
				}
				if (idx == -1) {
					return "";
				}

				// TODO 还可以边读取，边查找
				readPro(numHeadItemList.get(idx).startPos,
						numHeadItemList.get(idx).length, head, context);
				// 二分查找
				short num = (short) Integer.parseInt(phoneNum.substring(3, 7));

				int pos = binarySearch(num, head);
				// int index = numItemList.get(pos).index;
				for (int i = 0; i < numItemList.size(); i++) {
				}
				String locaName = strCodeList.get(numItemList.get(pos).index).name;
				short locaCode = strCodeList.get(numItemList.get(pos).index).code;
				// CustomLog.i("findLocalName","phone head = "+head+" phone middle num = "
				// +num+" pos = " + pos+" local name = "
				// +locaName+" localcode =" +locaCode);
				if (isAreaCode) {
					return "" + locaCode;
				} else {
					return locaName;
				}
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		return "";
	}

	// Type a[],const Type& x,int n
	private static int binarySearch(int num, short head) {

		if (map.containsKey(String.valueOf(head))) {
			// numItemList.clear();
			numItemList = map.get(String.valueOf(head));
			// CustomLog.i("map","map contain head "+head+" and the list has "+numItemList.size()+" elem");
		} else {
			// CustomLog.i("map","the map can't find the haed "+head+" list");
			return -1;
		}

		int left = 0;
		int right = numItemList.size() - 1;
		while (left <= right) {
			int middle = (left + right) / 2;
			short n = numItemList.get(middle).num;
			if (num == n)
				return middle;
			if (num > n) {
				left = middle + 1;
			} else {
				right = middle - 1;
			}
		}
		return right;
	}

	private static class AreaTable {
		short code;
		String name;
	}

	private static class NationalTable {
		short code;
		String name;
	}

	private static class NumHeadItem {
		short headName;
		int startPos;
		int length;
	}

	private static class NumItem {
		short num;
		short index;
	}

	private static short readShort(InputStream is) throws IOException {
		byte[] b = new byte[2];
		is.read(b);
		short value = (short) (((int) (b[0] & 0xff)) | (((int) (b[1] & 0xff)) << 8));
		return value;
	}

	private static int readInt(InputStream is) throws IOException {
		byte[] b = new byte[4];
		is.read(b);
		int value = (int) (((int) (b[0] & 0xff)) | (((int) (b[1] & 0xff)) << 8)
				| (((int) (b[2] & 0xff)) << 16) | (((int) (b[3] & 0xff)) << 24));
		return value;
	}

	private static String readStr(InputStream is) throws IOException {
		int len = is.read();
		byte[] b = new byte[len];
		is.read(b);
		String s = new String(b, "UTF-16");
		return s;
	}

	private static void initData(Context context) {
		try {
			FileInputStream in = new FileInputStream(Util.phoneLocalPath
					+ "PhoneNumberQuery.dat");
			readIndexData(in);
			readAreaData(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readIndexData(InputStream is) throws IOException {
		// 号码段数量
		int num = readShort(is);
		for (int i = 0; i < num; i++) {
			NumHeadItem item = new NumHeadItem();
			item.headName = readShort(is);
			item.startPos = readInt(is);
			item.length = readInt(is);
			// CustomLog.i("numHeadItemList","numHeadItemList headName = "
			// +item.headName+ " startPos = " +item.startPos+
			// " length = "+item.length);
			/*
			 * CustomLog.v(TAG, "numHeadItemList headName = " + item.headName +
			 * " startPos = " + item.startPos + " length = " + item.length);
			 */
			numHeadItemList.add(item);
		}
	}

	private static void readAreaData(InputStream is) throws IOException {
		int count = readShort(is);
		for (int i = 0; i < count; i++) {
			AreaTable areaLine = new AreaTable();
			short code = readShort(is);
			String name = readStr(is);
			areaLine.code = code;
			areaLine.name = name;
			// CustomLog.i("strCodeList","strCodeList name = " +name+ " code = "
			// +
			// code);
			// CustomLog.v(TAG, "strCodeList name = " + name + " code = " +
			// code);
			strCodeList.add(areaLine);
		}

		count = readShort(is);
		for (int i = 0; i < count; i++) {
			NationalTable nationalTable = new NationalTable();
			short code = readShort(is);
			String name = readStr(is);
			nationalTable.code = code;
			nationalTable.name = name;
			// CustomLog.v(TAG, "ntiCodeList name = " + name + " code = " +
			// code);
			ntiCodeList.add(nationalTable);
		}
	}

	private static void readPro(int start, int len, short headName,
			Context context) throws IOException {

		int i = 0;
		if (currentNumHead != null && currentNumHead.equals(headName)
				&& numItemList.size() > 0) {
			return;
		}

		if (map.containsKey(String.valueOf(headName))) {
			// ArrayList<NumItem> temp = map.get(String.valueOf(headName));
			// int count = temp.size();
			// CustomCustomLog.i("key = "+"===============>hashmap contain has inserted head "
			// +headName+" and the list size = "+count);
			return;
		}

		FileInputStream in = null;

		ArrayList<NumItem> tem = new ArrayList<NumItem>(100);
		// numItemList.clear();
		byte[] data = null;
		try {
			data = new byte[len];
			in = new FileInputStream(Util.phoneLocalPath
					+ "PhoneNumberQuery.dat");
			in.skip(start);
			in.read(data);
			ByteArrayInputStream is = new ByteArrayInputStream(data);
			byte[] b = new byte[3];
			while (is.read(b, 0, b.length) != -1) {
				int v = ((int) b[0] & 0xff) | (((int) b[1] & 0xff) << 8)
						| (((int) b[2] & 0xff) << 16);
				short phoneNum = (short) (v >> 10);
				short code = (short) (v & 0x3FF);

				NumItem item = new NumItem();
				item.num = phoneNum;
				item.index = code;
				i++;
				tem.add(item);
			}
			is.close();
			// CustomLog.i("other","-------------->hashmap insert head "
			// +headName+" and add  "+i+" numItem");
			map.put(String.valueOf(headName), tem);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			data = null;
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * xkl
	 * 
	 * @param phoneNum
	 * @return
	 */
	public static String interNational(String phoneNum) {
		String locaName = "";
		try {
			// 为国际号码
			short head = (short) Integer.parseInt(phoneNum.substring(0, 6));
			int idx = -1;
			for (int i = 0; i < ntiCodeList.size(); i++) {
				if (ntiCodeList.get(i).code == head) {
					idx = i;
					break;
				}
			}
			// System.out.println("idx:"+idx+"====head:"+head);
			if (idx == -1) {
				head = (short) Integer.parseInt(phoneNum.substring(0, 5));
				for (int i = 0; i < ntiCodeList.size(); i++) {
					if (ntiCodeList.get(i).code == head) {
						idx = i;
						break;
					}
				}
				if (idx == -1) {
					head = (short) Integer.parseInt(phoneNum.substring(0, 4));
					for (int i = 0; i < ntiCodeList.size(); i++) {
						if (ntiCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					if (idx == -1) {
						head = (short) Integer.parseInt(phoneNum
								.substring(0, 3));
						for (int i = 0; i < ntiCodeList.size(); i++) {
							if (ntiCodeList.get(i).code == head) {
								idx = i;
								break;
							}

						}
						if (idx == -1) {
							return "";
						}
					}
				}
			}

			locaName = ntiCodeList.get(idx).name;
			if (locaName.lastIndexOf("(") != -1) {
				locaName = locaName.substring(0, locaName.lastIndexOf("("));
			} else {
				locaName = locaName.substring(0, locaName.lastIndexOf("（"));
			}
		} catch (Exception e) {
			try {
				short head = (short) Integer.parseInt(phoneNum.substring(0, 5));
				int idx = -1;
				for (int i = 0; i < ntiCodeList.size(); i++) {
					if (ntiCodeList.get(i).code == head) {
						idx = i;
						break;
					}
				}
				locaName = ntiCodeList.get(idx).name;
				if (locaName.lastIndexOf("(") != -1) {
					locaName = locaName.substring(0, locaName.lastIndexOf("("));
				} else {
					locaName = locaName.substring(0, locaName.lastIndexOf("（"));
				}
			} catch (Exception e2) {
				try {
					short head = (short) Integer.parseInt(phoneNum.substring(0,
							4));
					int idx = -1;
					for (int i = 0; i < ntiCodeList.size(); i++) {
						if (ntiCodeList.get(i).code == head) {
							idx = i;
							break;
						}
					}
					locaName = ntiCodeList.get(idx).name;
					if (locaName.lastIndexOf("(") != -1) {
						locaName = locaName.substring(0,
								locaName.lastIndexOf("("));
					} else {
						locaName = locaName.substring(0,
								locaName.lastIndexOf("（"));
					}
				} catch (Exception e3) {
					try {
						short head = (short) Integer.parseInt(phoneNum
								.substring(0, 3));
						int idx = -1;
						for (int i = 0; i < ntiCodeList.size(); i++) {
							if (ntiCodeList.get(i).code == head) {
								idx = i;
								break;
							}
						}
						locaName = ntiCodeList.get(idx).name;
						if (locaName.lastIndexOf("(") != -1) {
							locaName = locaName.substring(0,
									locaName.lastIndexOf("("));
						} else {
							locaName = locaName.substring(0,
									locaName.lastIndexOf("（"));
						}
					} catch (Exception e4) {
						// TODO: handle exception
					}
				}
			}

		}
		return locaName;
	}

	/**
	 * 判断是否是国际电话
	 * 
	 * @param phoneNum
	 * @return
	 * @author: 龙小龙
	 * @version: 2012-3-6 下午03:37:33
	 */
	public static boolean isITT(String phoneNum) {
		if (phoneNum == null || phoneNum.equals("")) {
			return false;
		}

		if (phoneNum.startsWith("00") && !phoneNum.startsWith("0086")) {
			return true;
		}

		if (phoneNum.startsWith("+") && !phoneNum.startsWith("+86")) {
			return true;
		}
		return false;
	}

	/**
	 * 1.以18/17/15/13/14开头的号码只能是11位，且以1开头后面不是数字3,4,5,7,8，那么就是错误号码
	 * 2.仅以一个0开头座机号规则10<=X<=12 3.以两个0开头的号码就是国际电话
	 * 4.以+86/12593/17951/17909/17911的结合规则1和2，其中+86后面的座机号码和手机号码均可以带0也可以不带0
	 * 
	 * @param num
	 * @return 格式后的电话 null标示号码不匹配
	 */
	public static String formatFreeCallNum(String num) {
		if (TextUtils.isEmpty(num)) {
			return null;
		}

		num = num.replace(" ", "");
		String oldString = num.replace("-", "");
		oldString = oldString.replace("+", "");

		if (oldString.length() < 3) {
			return null;
		}

		if (oldString.matches("^86.*"))
			oldString = oldString.substring("86".length());
		if (oldString.matches("^12593.*|17951.*|17909.*|17911.*")) {
			oldString = oldString.substring("12593".length());
		}

		if (oldString.matches("^(0){1}[1-9]*$")) {
			if (oldString.matches("[0-9]{8,12}")) {
				return oldString;
			} else {
				return null;
			}
		}
		if (oldString.startsWith("1")) {
			if (oldString.matches("^13.*|14.*|15.*|18.*|17.*")) {
				if (oldString.matches("[0-9]{11}")) {
					return oldString;
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return oldString;

	}

	/**
	 * 下载文件
	 * 
	 * @param urlStr
	 * @param bWifi
	 * @param mContext
	 */
	public synchronized static void getDownLoadFile(String urlStr,
			boolean bWifi, Context mContext) {
		String SaveFileName = "";
		if (urlStr == null || urlStr.length() < 2) {
			return;
		}
		int Findex = urlStr.lastIndexOf("/");
		if (Findex < 0 || Findex == urlStr.length()) {
			// 使用默认的信息
		} else {
			SaveFileName = urlStr.substring(Findex + 1);
		}
		// if (KcUpdateAPK.IsCanUseSdCard()) {
		// Util.phoneLocalPath = KcCoreService.mWldhFilePath;
		// } else {
		// Util.phoneLocalPath = mContext.getFilesDir().getPath() +
		// File.separator;
		// }
		File tmpFile = new File(Util.phoneLocalPath);
		if (tmpFile.exists()) {
			// 继续
		} else {
			tmpFile.mkdir();// 新建文件夹
		}
		if (Util.phoneLocalPath == null || "" == Util.phoneLocalPath) {
			return;
		}
		final File file = new File(Util.phoneLocalPath + SaveFileName);

		// 判断文件是否存在，不存在就去下载
		if (!file.exists()) {
			try {
				File tempfile = new File(Util.phoneLocalPath + "temp.tmp");
				URL url = new URL(urlStr);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				InputStream input = conn.getInputStream();
				FileOutputStream output = new FileOutputStream(tempfile);
				int len = conn.getContentLength();

				// Map<String, List<String>> headers = conn.getHeaderFields();
				// Set<Entry<String, List<String>>> set = headers.entrySet();
				// Iterator<Entry<String, List<String>>> iter = set.iterator();
				// for (Iterator iterator = set.iterator(); iterator.hasNext();)
				// {
				// Entry<String, List<String>> entry = (Entry<String,
				// List<String>>) iterator.next();
				// StringBuffer sb = new StringBuffer();
				// for (int i = 0; i < entry.getValue().size(); i++) {
				// sb.append(entry.getValue().get(i));
				// sb.append(",");
				// }
				// CustomLog.i("header", entry.getKey() + ":" + sb.toString());
				// }

				int totalReadedLen = 0;
				byte[] buffer = new byte[4 * 1024];
				conn.connect();
				if (conn.getResponseCode() >= 400) {

				} else {
					// 读取大文件
					int readedLen;
					while ((readedLen = input.read(buffer)) != -1) {
						output.write(buffer, 0, readedLen);
						totalReadedLen += readedLen;
					}
					if (totalReadedLen == len) {
						tempfile.renameTo(file);
					}
					output.flush();
					conn.disconnect();
					if (output != null) {
						output.close();
					}
					input.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 下载归属地文件方法
	 * 
	 * @param mContext
	 */
	public static void getPhoneLocalMethod(final Context mContext) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				long time = System.currentTimeMillis();

				// if (KcUpdateAPK.IsCanUseSdCard()) {
				// SavePath = mWldhFilePath;
				// } else {
				// SavePath = mContext.getFilesDir().getPath() + File.separator;
				// }
				getDownLoadFile("http://wap.uuwldh.com/location/PhoneNumberQuery.dat", isWifi(mContext),
						mContext);

			}
		}).start();
	}

	/**
	 * 是否是wifi网络
	 * 
	 * @param mcontext
	 * @return
	 */
	public static boolean isWifi(Context mcontext) {
		// 获取当前可用网络信息
		ConnectivityManager connMng = (ConnectivityManager) mcontext
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInf = connMng.getActiveNetworkInfo();

		// 如果当前是WIFI连接
		if (netInf != null && "WIFI".equals(netInf.getTypeName())) {
			return true;
		}
		return false;
	}

	public static String getPhoneName(Context context,
			final String strPhoneNumber) {
		Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/"
				+ "data/phones/filter/" + strPhoneNumber);
		Cursor cursorCantacts = context.getContentResolver().query(
				uriNumber2Contacts, null, null, null, null);
		if (cursorCantacts.getCount() > 0) {
			cursorCantacts.moveToFirst();

			String columnName = cursorCantacts.getString(cursorCantacts
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			cursorCantacts.close();
			return columnName;

		}
		cursorCantacts.close();
		return "";

	}

	public static boolean isHavePhone(Context context,
			final String strPhoneNumber) {
		Uri uriNumber2Contacts = Uri.parse("content://com.android.contacts/"
				+ "data/phones/filter/" + strPhoneNumber);
		Cursor cursorCantacts = context.getContentResolver().query(
				uriNumber2Contacts, null, null, null, null);
		if (cursorCantacts.getCount() > 0) {
			cursorCantacts.close();
			return true;

		}
		cursorCantacts.close();
		return false;

	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	public static void shortMessageShare(Context context, String phone) {

		String str = " hi，您想打电话不花钱吗？立即点击下面链接免费注册〔远大云商〕会员吧，还可用商币免费兑换所有商品和服务哦";
		String url = "http://" + Web.webImage + "/phone/registe.aspx?unum="
				+ Util.getNo_pUserId(UserData.getUser().getUserId());
		String str2 = "  〔远大云商〕全国客服热线：400-666-3838。";
		String phoneStr = "smsto:" + phone;
		Uri smsToUri = Uri.parse(phoneStr);
		Intent intent = new Intent(Intent.ACTION_SENDTO, smsToUri);
		intent.putExtra("sms_body", str + url + str2);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(intent);
	}

	public static String formatDateTime(String typeFrom, String typeTo,
			String value) {
		String re = value;
		SimpleDateFormat sdfFrom = new SimpleDateFormat(typeFrom);
		SimpleDateFormat sdfTo = new SimpleDateFormat(typeTo);
		try {
			re = sdfTo.format(sdfFrom.parse(re));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return re;
	}

	public static void showIntent(String message, final Context context,
			String right, String left, final View.OnClickListener rightClick,
			final View.OnClickListener leftClick) {
		new VoipDialog(message, context, right, left, rightClick, leftClick)
				.show();

	}

	public static Map<String, String> getNewApiJson(String json) {
		Map<String, String> map = null;

		if (!Util.isNull(json)) {

			map = new HashMap<String, String>();

			try {

				JSONObject jObject = new JSONObject(json);

				String code = jObject.optString("code");
				String message = jObject.optString("message");

				map.put("code", code);
				map.put("message", message);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return map;

	}

}
