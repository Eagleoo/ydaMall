package com.mall.serving.community.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.mall.serving.community.view.progress.CustomProgress;
import com.mall.view.App;
import com.mall.view.R;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {
	public static String version = com.mall.util.Util.version;
	public static final int NETTYPE_WIFI = 0x01;
	public static final int NETTYPE_CMWAP = 0x02;
	public static final int NETTYPE_CMNET = 0x03;
	private static ExecutorService executorService = Executors
			.newFixedThreadPool(10);
	public static final String apkPath = "/sdcard/yuanda/download/";

	// ×××××××××××××××××××××××××××××××××××手机参数相关方法
	// 开始×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××

	/**
	 * 生产商家
	 * 
	 * @return
	 */
	public static String getMake() {
		return android.os.Build.MANUFACTURER;
	}

	/*
	 * 获得固件版本
	 */
	public static String getRelease() {
		return android.os.Build.VERSION.RELEASE;
	}

	/*
	 * 获得手机型号
	 */
	public static String getModel() {
		return android.os.Build.MODEL;
	}

	/*
	 * 获得手机品牌
	 */
	public static String getBrand() {
		return android.os.Build.BRAND;
	}

	/**
	 * 获取手机信息
	 */
	public void getPhoneInfo() {
		TelephonyManager tm = (TelephonyManager) App.getContext()
				.getSystemService(Context.TELEPHONY_SERVICE);
		String mtyb = android.os.Build.BRAND;// 手机品牌
		String mtype = android.os.Build.MODEL; // 手机型号
		String imei = tm.getDeviceId();
		String imsi = tm.getSubscriberId();
		String numer = tm.getLine1Number(); // 手机号码
		String serviceName = tm.getSimOperatorName(); // 运营商
	}

	public static boolean isNull(Object obj) {
		return null == obj || "".equalsIgnoreCase(obj.toString());
	}

	public static boolean isPhoneNumber(String phonenumber) {
		Pattern pa = Pattern.compile("^[1][3,4,5,8,7][0-9]{9}$");
		Matcher ma = pa.matcher(phonenumber);
		return ma.matches();
	}

	/**
	 * 打电话
	 * 
	 * @param phone
	 * @param context
	 */
	public static void doPhone(String phone, Context context) {
		Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
				+ phone));
		context.startActivity(phoneIntent);
	}

	public static void doSMS(String phone, String content, Context c) {
		Uri uri = null;
		if (!Util.isNull(phone))
			uri = Uri.parse("smsto:" + phone);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", content);
		c.startActivity(intent);
	}

	/**
	 * 屏幕高度
	 * 
	 * @return
	 */
	public static int getScreenHeight() {
		WindowManager manager = (WindowManager) App.getContext()
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		Display display = manager.getDefaultDisplay();
		display.getMetrics(displayMetrics);

		return displayMetrics.heightPixels;

	}

	/**
	 * 屏幕宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth() {
		WindowManager manager = (WindowManager) App.getContext()
				.getSystemService(Context.WINDOW_SERVICE);

		DisplayMetrics displayMetrics = new DisplayMetrics();
		Display display = manager.getDefaultDisplay();
		display.getMetrics(displayMetrics);

		return displayMetrics.widthPixels;

	}

	/**
	 * 屏幕分辨率
	 * 
	 * @param drame
	 * @return
	 */
	public static float getDip(Activity drame) {

		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, drame
				.getResources().getDisplayMetrics());
	}
	/**
	 * 获得状态栏的高度
	 * 
	 * @param context
	 * @return
	 */
	public static int getStatusHeight(Context context) {
		int statusHeight = -1;
		try {
			Class<?> clazz = Class.forName("com.android.internal.R$dimen");
			Object object = clazz.newInstance();
			int height = Integer.parseInt(clazz.getField("status_bar_height")
					.get(object).toString());
			statusHeight = context.getResources().getDimensionPixelSize(height);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusHeight;
	}
	/**
	 * 安装
	 */
	public void instance(Context context, File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 是否安装了
	 * 
	 * @param packageName
	 * @return
	 */
	public static boolean isInstall(String packageName) {
		PackageManager packageManager = App.getContext().getPackageManager();
		List<ApplicationInfo> packs = packageManager
				.getInstalledApplications(PackageManager.GET_META_DATA);
		for (ApplicationInfo info : packs) {
			if (info.packageName.equals(packageName))
				return true;
		}
		return false;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) App.getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		return ni != null && ni.isConnected();
	}

	// ××××××××××××××××××××××××××××××××××××手机参数相关方法
	// 结束××××××××××××××××××××××××××××××××××××××××××

	// ×××××××××××××××××××××××××××××××××××字符串相关方法开始×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
	/**
	 * 判断是否是中文字符
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isChina(String str) {
		Pattern p = Pattern.compile("[\\u4E00-\\u9FA5]+");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 判断邮政编码
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isCard(String str) {
		Pattern p = Pattern.compile("[1-9]\\d{5}(?!\\d)");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 邮箱验证
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmail(String str) {
		Pattern p = Pattern
				.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	// 使用递归替换字符
	public static String replaceAll(String temp, String old, String str) {
		StringBuffer sb = new StringBuffer();
		int i = temp.indexOf(old);

		if (i > -1) {
			sb.append(temp.substring(0, i) + str);
		} else {
			return temp;
		}
		if ((i + old.length()) < temp.length()) {
			sb.append(replaceAll(temp.substring(i + old.length()), old, str));
		}
		return sb.toString();
	}

	public static byte[] toByteArray(InputStream input) throws IOException {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte[] buffer = new byte[4096];
		int n = 0;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
		return output.toByteArray();
	}

	public static String getToUp(double n) {
		String fraction[] = { "角", "分" };
		String digit[] = { "零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖" };
		String unit[][] = { { "元", "万", "亿" }, { "", "拾", "佰", "仟" } };

		String head = n < 0 ? "负" : "";
		n = Math.abs(n);

		String s = "";
		for (int i = 0; i < fraction.length; i++) {
			s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i])
					.replaceAll("(零.)+", "");
		}
		if (s.length() < 1) {
			s = "整";
		}
		int integerPart = (int) Math.floor(n);

		for (int i = 0; i < unit[0].length && integerPart > 0; i++) {
			String p = "";
			for (int j = 0; j < unit[1].length && n > 0; j++) {
				p = digit[integerPart % 10] + unit[1][j] + p;
				integerPart = integerPart / 10;
			}
			s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i]
					+ s;
		}
		return head
				+ s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "")
						.replaceAll("(零.)+", "零").replaceAll("^整$", "零元整");
	}

	// ×××××××××××××××××××××××××××××××××××字符串相关方法结束××××××××××××××××××××××

	// ×××××××××××××××××××××××××××××××××××数字相关方法 开始××××××××××××××
	public static boolean isInt(Object obj) {
		if (!isNull(obj)) {
			return obj.toString().matches("^[-]?\\d+$");
		}
		return false;
	}

	public static int getInt(Object obj) {
		if (!isNull(obj)) {
			String newValue = obj.toString().replaceAll("\\D+", "");
			if (isNull(newValue))
				return 0;
			return Integer.parseInt(newValue);
		}
		return 0;
	}

	public static boolean isDouble(Object obj) {
		if (!isNull(obj)) {
			return obj.toString().matches("^\\d+(\\.[\\d]+)?$");
		}
		return false;
	}

	public static double getDouble(Object v) {
		if (!isNull(v)) {
			if (isDouble(v))
				return Double.parseDouble(v + "");
			else
				return 0.0D;
		}
		return 0.0D;
	}

	public static double getDouble(Double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(
				Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	// ×××××××××××××××××××××××××××××××××××数字相关方法 结束××××××××××

	// ×××××××××××××××××××××××××××××××××××日期相关方法 开始××××××××××××××××

	private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		}
	};

	private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected SimpleDateFormat initialValue() {
			return new SimpleDateFormat("yyyy-MM-dd");
		}
	};

	public static Date toDate(String sdate) {
		try {
			return dateFormater.get().parse(sdate);
		} catch (ParseException e) {
			return null;
		}
	}

	public static String friendly_time(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "Unknown";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 10) {
			ftime = days + "天前";
		} else {
			ftime = dateFormater2.get().format(time);

		}
		return ftime;
	}

	public static String getRecentTime(String sdate) {
		Date time = toDate(sdate);
		if (time == null) {
			return "1月前";
		}
		String ftime = "";
		Calendar cal = Calendar.getInstance();

		// 判断是否是同一天
		String curDate = dateFormater2.get().format(cal.getTime());
		String paramDate = dateFormater2.get().format(time);
		if (curDate.equals(paramDate)) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
			return ftime;
		}

		long lt = time.getTime() / 86400000;
		long ct = cal.getTimeInMillis() / 86400000;
		int days = (int) (ct - lt);
		if (days == 0) {
			int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
			if (hour == 0)
				ftime = Math.max(
						(cal.getTimeInMillis() - time.getTime()) / 60000, 1)
						+ "分钟前";
			else
				ftime = hour + "小时前";
		} else if (days == 1) {
			ftime = "昨天";
		} else if (days == 2) {
			ftime = "前天";
		} else if (days > 2 && days <= 30) {
			ftime = days + "天前";
		} else {
			// ftime = dateFormater2.get().format(time);
			int month = days / 30;
			ftime = month + "月前";
		}
		return ftime;
	}

	/**
	 * 日期时间格式转换
	 * 
	 * @param typeFrom
	 *            原格式
	 * @param typeTo
	 *            转为格式
	 * @param value
	 *            传入的要转换的参数
	 * @return
	 */
	public static String formatDateTime(String typeFrom, String typeTo,
			String value) {
		String re = value;
		SimpleDateFormat sdfFrom = new SimpleDateFormat(typeFrom);
		SimpleDateFormat sdfTo = new SimpleDateFormat(typeTo);
		try {
			re = sdfTo.format(sdfFrom.parse(re));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return re;
	}

	public static int getMonthLastDay(int year, int month) {
		Calendar a = Calendar.getInstance();
		a.set(Calendar.YEAR, year);
		a.set(Calendar.MONTH, month);
		a.set(Calendar.DATE, 1);// 把日期设置为当月第一天
		a.roll(Calendar.DATE, -1);// 日期回滚一天，也就是最后一天
		int maxDate = a.get(Calendar.DATE);
		return maxDate;
	}

	public static String getCurrentYear() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.YEAR) + "";
	}

	public static String getCurrentMonth() {
		Calendar c = Calendar.getInstance();
		return (c.get(Calendar.MONTH) + 1) + "";
	}

	public static String getCurrDay() {
		Calendar c = Calendar.getInstance();
		return c.get(Calendar.DAY_OF_MONTH) + "";
	}

	// ×××××××××××××××××××××××××××××××××××日期相关方法结束××××××××××××××××××

	// ×××××××××××××××××××××××××××××××××××图片相关方法开始××××××××××××××××××××××××××
	/**
	 * 
	 * @param imgPath
	 * @param bitmap
	 * @param imgFormat
	 *            图片格式
	 * @return
	 */

	@SuppressLint("NewApi")
	public static String imgToBase64(String imgPath, Bitmap bitmap,
			String imgFormat) {
		if (imgPath != null && imgPath.length() > 0) {
			bitmap = readBitmap(imgPath);
		}
		if (null == bitmap) {

			return null;
		}
		ByteArrayOutputStream out = null;
		try {
			out = new ByteArrayOutputStream();
			Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
			if (imgFormat.equalsIgnoreCase("png"))
				compressFormat = Bitmap.CompressFormat.PNG;
			bitmap.compress(compressFormat, 85, out);
			out.flush();
			out.close();
			byte[] imgBytes = out.toByteArray();
			return Base64.encodeToString(imgBytes, Base64.DEFAULT);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				out.flush();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 得到圆形的bitmap
	 * 
	 * @param bitmap
	 *
	 *            以长或宽的比例为半径，2表示二分之一，8表示八分之一
	 * @return
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		float roundPx;
		float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
		if (width <= height) {
			roundPx = width / 2;
			top = 0;
			bottom = width;
			left = 0;
			right = width;
			height = width;
			dst_left = 0;
			dst_top = 0;
			dst_right = width;
			dst_bottom = width;
		} else {
			roundPx = height / 2;
			float clip = (width - height) / 2;
			left = clip;
			right = width - clip;
			top = 0;
			bottom = height;
			width = height;
			dst_left = 0;
			dst_top = 0;
			dst_right = height;
			dst_bottom = height;
		}
		Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect src = new Rect((int) left, (int) top, (int) right,
				(int) bottom);
		final Rect dst = new Rect((int) dst_left, (int) dst_top,
				(int) dst_right, (int) dst_bottom);
		final RectF rectF = new RectF(dst);
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(
				android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, src, dst, paint);
		return output;
	}

	/**
	 * 设置Bitmap圆角
	 * 
	 *
	 *            传入的Bitmap
	 * @param outerRadiusRat
	 *            圆角半径
	 * @return 返回处理后的Bitmap
	 */
	public static Bitmap getRectBitmap(Bitmap output, int outerRadiusRat) {
		int x = output.getWidth();
		int y = output.getHeight();
		// 根据源文件新建一个darwable对象
		Drawable imageDrawable = new BitmapDrawable(output);

		// 新建一个新的输出图片
		output = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);

		try {

			Canvas canvas = new Canvas(output);

			// 新建一个矩形
			RectF outerRect = new RectF(0, 0, x, y);

			// 产生一个红色的圆角矩形
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setColor(Color.RED);
			canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat,
					paint);

			// 将源图片绘制到这个圆角矩形上
			// 详解见http://lipeng88213.iteye.com/blog/1189452
			paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
			imageDrawable.setBounds(0, 0, x, y);
			canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
			imageDrawable.draw(canvas);
			canvas.restore();
		} catch (Exception e) {
			// TODO: handle exception
		}

		return output;
	}

	private static Bitmap readBitmap(String imgPath) {
		try {
			return BitmapFactory.decodeFile(imgPath);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 图片压缩
	 * 
	 * @param image
	 * @return
	 */
	public static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) {
			options -= 10;
			baos.reset();
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
		return bitmap;
	}

	/**
	 * 得到缩略图
	 * 
	 * @param srcPath
	 * @return
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;
		float ww = 480f;
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);
	}

	/**
	 * 放大缩小图片
	 * 
	 * @param bitmap
	 * @param w
	 * @param h
	 * @return
	 */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		Bitmap newbmp = null;
		if (bitmap != null) {
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			Matrix matrix = new Matrix();
			float scaleWidht = ((float) w / width);
			float scaleHeight = ((float) h / height);
			matrix.postScale(scaleWidht, scaleHeight);
			newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		}
		return newbmp;
	}

	/**
	 * 加载本地缩略图
	 * 
	 * @param path
	 * @param showWidth
	 * @param showHeight
	 * @return
	 */
	public static Bitmap getLocationThmub(String path, int showWidth,
			int showHeight) {
		// 对于图片的二次采样,主要得到图片的宽与高
		int width = 0;
		int height = 0;
		int sampleSize = 1; // 默认缩放为1
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true; // 仅仅解码边缘区域
		// 如果指定了inJustDecodeBounds，decodeByteArray将返回为空
		BitmapFactory.decodeFile(path, options);
		// 得到宽与高
		height = options.outHeight;
		width = options.outWidth;
		// 图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
		while ((height / sampleSize > showHeight)
				|| (width / sampleSize > showWidth)) {
			sampleSize += 1;
		}
		// 不再只加载图片实际边缘
		options.inJustDecodeBounds = false;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		// 并且制定缩放比例
		options.inSampleSize = sampleSize;
		Bitmap bitmap = null;
		try {
			bitmap = BitmapFactory.decodeFile(path, options);
		} catch (OutOfMemoryError oError) {
			oError.printStackTrace();
		} catch (Exception e) {
		}

		return bitmap;
	}

	public static Bitmap getLocalBitmap(String path) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		return getLocalBitmap(path, opts);
	}

	public static Bitmap getLocalBitmap(String path, int width, int height) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inSampleSize = 2;
		opts.inJustDecodeBounds = false;
		opts.inPreferredConfig = Bitmap.Config.ARGB_4444;
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.outWidth = width;
		opts.outHeight = height;
		Bitmap bm = getLocalBitmap(path, opts);
		return zoomBitmap(bm, width, height);
	}

	public static Bitmap ReadBitmap(int screenWidth, int screenHight,
			Bitmap bitmap) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPreferredConfig = Config.ARGB_8888;
		options.inInputShareable = true;
		options.inPurgeable = true;
		return getBitmap(bitmap, screenWidth, screenHight);
	}

	/***
	 * 等比例压缩图片
	 * 
	 * @param bitmap
	 * @param screenWidth
	 * @param screenHight
	 * @return
	 */
	public static Bitmap getBitmap(Bitmap bitmap, int screenWidth,
			int screenHight) {
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();
		Log.e("jj", "图片宽度" + w + ",screenWidth=" + screenWidth);
		Matrix matrix = new Matrix();
		float scale = (float) screenWidth / w;
		float scale2 = (float) screenHight / h;

		// scale = scale < scale2 ? scale : scale2;

		// 保证图片不变形.
		matrix.postScale(scale, scale);
		// w,h是原图的属性.
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
	}

	public static Bitmap getLocalBitmap(String path,
			BitmapFactory.Options options) {
		if (Util.isNull(path))
			return null;
		Bitmap temBitmap = null;
		try {
			temBitmap = BitmapFactory.decodeFile(path, options);
		} catch (Throwable t) {
			t.printStackTrace();

		}
		return temBitmap;
	}

	public static Bitmap bitmapToScale(Bitmap bitmap, float m) {
		try {
			Matrix matrix = new Matrix();
			matrix.postScale(m, m); // 长和宽放大缩小的比例
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
					bitmap.getHeight(), matrix, true);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return bitmap;
	}

	public static Bitmap getHttpBitmap(String imgUrl) {

		Bitmap bitmap = null;
		if (!Util.isNetworkConnected()) {

			return null;
		}

		InputStream is = getHttpPicInputStream(imgUrl);
		if (is != null) {

			bitmap = BitmapFactory.decodeStream(is);

			try {
				is.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return bitmap;

	}

	public static InputStream getHttpPicInputStream(String url) {
		InputStream is = null;
		URL url1 = null;
		try {
			url1 = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
			conn.setDoInput(true);
			conn.setConnectTimeout(30000);
			conn.setRequestMethod("GET");
			conn.connect();

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				is = conn.getInputStream();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} catch (ProtocolException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		} catch (IOException e) {
			e.printStackTrace(); // To change body of catch statement use File |
									// Settings | File Templates.
		}

		return is;
	}

	/**
	 * Bitmap转化为drawable
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Drawable bitmap2Drawable(Bitmap bitmap) {
		return new BitmapDrawable(bitmap);
	}

	/**
	 * Drawable 转 bitmap
	 * 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();

			// 取 drawable 的颜色格式
			Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
					: Bitmap.Config.RGB_565;
			// 建立对应 bitmap
			Bitmap bitmap = Bitmap.createBitmap(w, h, config);
			// 建立对应 bitmap 的画布
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			// 把 drawable 内容画到画布中
			drawable.draw(canvas);
			return bitmap;
		}
	}

	/**
	 * dp转px
	 * 
	 *
	 * @param dpValue
	 * @return
	 */
	public static int dpToPx(float dpValue) {
		final float scale = App.getContext().getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int pxToDp(float pxValue) {
		final float scale = App.getContext().getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	// ×××××××××××××××××××××××××××××××××××图片相关方法
	// 结束×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××

	// ×××××××××××××××××××××××××××××××××××View相关方法
	// 开始×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××
	public static Button getButton(int id, Activity activity) {
		return (Button) activity.findViewById(id);
	}

	public static EditText getEditText(int id, Activity activity) {
		return (EditText) activity.findViewById(id);
	}

	public static String get(String k) {
		try {
			return java.net.URLEncoder.encode(k, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return k;
		}
	}

	public static TextView getTextView(int id, Activity activity) {
		return (TextView) activity.findViewById(id);
	}

	public static DatePicker getDate(int id, Activity activity) {
		return (DatePicker) activity.findViewById(id);
	}

	public static Spinner getSpinner(int id, Activity activity) {
		return (Spinner) activity.findViewById(id);
	}

	public static ListView getListView(int id, Activity activity) {
		return (ListView) activity.findViewById(id);
	}

	public static ImageButton getImageButton(int id, Activity activity) {
		return (ImageButton) activity.findViewById(id);
	}

	public static ImageView getImageView(int id, Activity activity) {
		return (ImageView) activity.findViewById(id);
	}

	public static RadioButton getRadioButton(int id, Activity activity) {
		return (RadioButton) activity.findViewById(id);
	}

	public static GridView getGirdView(int id, Activity activity) {
		return (GridView) activity.findViewById(id);
	}

	public static View getIdView(int id, Activity activity) {
		return (View) activity.findViewById(id);
	}

	public static View getIdView(int id, View v) {
		return (View) v.findViewById(id);
	}

	public static View getView(Context context, int layoutId) {
		LayoutInflater flater = LayoutInflater.from(context);
		return flater.inflate(layoutId, null);
	}

	public static String getViewText(TextView tv) {
		return tv.getText().toString();
	}

	// ×××××××××××××××××××××××××××××××××××View相关方法
	// 结束×××××××××××××××××××××××××××××××××××××××××××××××××××××××××××

	/**
	 * 提示文字Toast
	 * 
	 * @param message
	 */
	public static void show(String message) {
		Toast toast = Toast.makeText(App.getContext(), message,
				Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, (getScreenHeight() / 5));
		toast.show();
	}

	/**
	 * 调用浏览器打开
	 * 
	 * @param activity
	 * @param url
	 */
	public static void openWeb(Context activity, String url) {
		Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		activity.startActivity(intent);
		if (activity instanceof Activity)
			((Activity) activity).overridePendingTransition(
					android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
	}

	/**
	 * 打开微信
	 * 
	 * @param context
	 * @param url
	 * @return
	 */
	public static boolean openWeChat(Context context, String url) {
		if (isInstall("com.tencent.mm")) {
			try {
				Intent localIntent = new Intent("android.intent.action.VIEW");
				localIntent.setData(Uri.parse(url));
				localIntent.setPackage("com.tencent.mm");
				// localIntent.setComponent(new
				// ComponentName("com.tencent.mm",""));
				context.startActivity(localIntent);
				if (context instanceof Activity)
					((Activity) context).overridePendingTransition(
							android.R.anim.slide_in_left,
							android.R.anim.slide_out_right);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				Util.show(e.getMessage() + "\n\n" + e.getLocalizedMessage());
				return false;
			}
		}
		return false;
	}

	/**
	 * 页面跳转
	 * 
	 * @param context
	 * @param c
	 */
	public static void showIntent(final Context context, final Class c) {
		showIntent(context, c, null, null);
	}

	/**
	 * 传数据的页面跳转
	 * 
	 * @param context
	 * @param c
	 * @param keys
	 * @param values
	 */
	@SuppressLint("NewApi")
	public static void showIntent(final Context context, final Class c,
			String[] keys, Serializable[] values) {
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
		AnimeUtil.reDoLogin();
		Intent intent = new Intent();
		intent.setClass(context, c);
		if (null != keys) {
			int i = 0;
			for (String key : keys) {
				intent.putExtra(key, values[i]);
				i++;
			}
		}

		context.startActivity(intent);
		// if (context instanceof Activity)
		// ((Activity) context).overridePendingTransition(
		// R.anim.appear_top_left_in,
		// R.anim.disappear_bottom_right_out);

	}

	public static void showIntentForResult(final Activity context,
			final Class c, String[] keys, Serializable[] values, int result) {
		AnimeUtil.reDoLogin();
		Intent intent = new Intent();
		intent.setClass(context, c);
		if (null != keys) {
			int i = 0;
			for (String key : keys) {
				intent.putExtra(key, values[i]);
				i++;
			}
		}

		context.startActivityForResult(intent, result);
		// if (context instanceof Activity)
		// ((Activity) context).overridePendingTransition(
		// R.anim.appear_top_left_in,
		// R.anim.disappear_bottom_right_out);

	}

	/**
	 * 用动画效果的页面关闭
	 * 
	 * @param activity
	 */
	public static void finish(Activity activity) {
		activity.finish();
		// activity.overridePendingTransition(R.anim.appear_bottom_right_in,
		// R.anim.disappear_top_left_out);

	}

	/**
	 * 有提示的异步任务
	 * 
	 * @param context
	 * @param message
	 * @param task
	 */
	public static void asynTask(final Context context, final String message,
			final IAsynTask task) {
		Util.showProgress(message, context);
		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				String dataType = data.getString("IAsynTaskResult");
				Serializable ser = data.getSerializable("IAsynTaskRunData");
			
				if ("success".equals(dataType))
					task.updateUI(ser);
				else if ("error".equals(dataType)) {
					Util.show("网络异常，请稍候再试！");

					if (null == ser)
						Log.e("Util异步任务错误！", ((Throwable) ser) + "");
					else
						((Throwable) ser).printStackTrace();
				} else {
					task.updateUI(null);
				}
				Util.closeProgress();
			}
		};

		executorService.execute(new Runnable() {
			// new Thread(new Runnable(){
			public void run() {
				Message msg = new Message();
				Bundle data = new Bundle();
				try {
					data.putSerializable("IAsynTaskRunData", task.run());
					data.putString("IAsynTaskResult", "success");
				} catch (Throwable e) {

					data.putSerializable("IAsynTaskRunData", e);
					data.putString("IAsynTaskResult", "error");
				}
				msg.setData(data);
				handler.sendMessage(msg);
			}
		});
	}

	/**
	 * 无提示的异步任务
	 * 
	 * @param task
	 */
	public static void asynTask(final IAsynTask task) {

		final Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				Bundle data = msg.getData();
				Serializable ser = data.getSerializable("runData");
				if ("success".equals(data.getString("IAsynTaskResult")))
					task.updateUI(ser);
			}
		};

		new Thread(new Runnable() {
			public void run() {
				Serializable runData = task.run();
				Message msg = new Message();
				Bundle data = new Bundle();
				data.putString("IAsynTaskResult", "success");
				data.putSerializable("runData", runData);
				msg.setData(data);
				handler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * 显示带进度条的dialog
	 * 
	 * @param message
	 * @param context
	 */
	public static void showProgress(String message, Context context) {
		CustomProgress.showProgressDialog(context, message);

	}

	/**
	 * 关闭带进度条的dialog
	 * 
	 */
	public static void closeProgress() {
		CustomProgress.hideProgressDialog();
	}

	public static List<Activity> list = new ArrayList<Activity>();

	public static void add(Activity a) {
		list.add(a);
	}

	public static void close() {
		for (Activity a : list) {
			a.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		ActivityManager manager = (ActivityManager) App.getContext().getSystemService(Context.ACTIVITY_SERVICE);   
		manager.restartPackage(App.getContext().getPackageName()); 
		System.exit(0);
	}

	public static int addNotification(int id, Context context, String title,
			String content) {
		int icon = android.R.drawable.stat_notify_chat;
		return addNotification(id, context, icon, title, content, new Intent(),
				null);
	}

	public static int addNotification(int id, Context context, int icon,
			String title, String content, Intent intent, RemoteViews view) {
		if (-1 == icon)
			icon = android.R.drawable.stat_notify_chat;
//		Notification notification = new Notification(icon, title,
//				System.currentTimeMillis());// 概要
		PendingIntent pendingIntent = PendingIntent.getActivity(context, 0,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);// 1为请求码 0为Flag标志位
		Notification notification =new NotificationCompat.Builder(context)
				.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
				.setSmallIcon(R.drawable.ic_launcher)

				.setContentTitle(title)
				.setContentText("")
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.build();


		notification.defaults = Notification.DEFAULT_SOUND;// 发送状态栏的默认铃声
		notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后，取消状态栏图标

		if (null != view) {
			// 通知显示的布局
			notification.contentView = view;
		}
		NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);// 得到系统通知服务
		manager.notify(id, notification);// 通知系统我们定义的notification，id为该notification的id；这里定义为100.
		return id;
	}

	public static String getNo_pUserId(String userId) {
		if (!Util.isNull(userId)) {
			if (userId.matches("^1[3|4|5|8|9]\\d{9}[_P|_p]*$")) {
				if (userId.contains("_p"))
					return userId.replaceAll("_p", "");
				if (userId.contains("_P"))
					return userId.replaceAll("_P", "");
			}
		}
		return userId;

	}

	/**
	 * 获取当前网络类型
	 * 
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!Util.isNull(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NETTYPE_CMNET;
				} else {
					netType = NETTYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NETTYPE_WIFI;
		}
		return netType;
	}

	public static Bitmap getBitmapByNetWork(String picPath) {
		int networkType = Util.getNetworkType(App.getContext());

		Bitmap tagBm = null;
		ByteArrayOutputStream decodeBitmap = null;
		if (1 == networkType) {

			decodeBitmap = decodeBitmap(picPath, 150);
		} else if (2 == networkType) {

			decodeBitmap = decodeBitmap(picPath, 80);
		} else if (3 == networkType) {

			decodeBitmap = decodeBitmap(picPath, 100);
		} else {
			Util.show("请检查您的网络!");
			return null;
		}

		if (decodeBitmap != null) {
			byte[] array = decodeBitmap.toByteArray();
			try {
				tagBm = BitmapFactory.decodeByteArray(array, 0, array.length);
			} catch (OutOfMemoryError oError) {
				oError.printStackTrace();
			} catch (Exception e) {
			}

		}

		return tagBm;

	}

	public static void startVoiceRecognition(Context context,
			DialogRecognitionListener mRecognitionListener) {
		final String API_KEY = "RH9qQct23u3FpyRbBIICBTGm";
		final String SECRET_KEY = "2qjL2HCG0TFsUvSog0m31wwFVsf4WZIA";
		Bundle params = new Bundle();
		params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);
		params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
		params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME,
				BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
		BaiduASRDigitalDialog mDialog = new BaiduASRDigitalDialog(context,
				params);
		mDialog.setDialogRecognitionListener(mRecognitionListener);
		mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP,
				VoiceRecognitionConfig.PROP_INPUT);
		mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE,
				VoiceRecognitionConfig.LANGUAGE_CHINESE);

		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, false);
		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, false);
		mDialog.getParams().putBoolean(
				BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, false);
		mDialog.show();

	}

	public static boolean saveBitmapToSdCard(String file, Bitmap bitmap) {
		boolean save = false;
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return save;
		}
		final Bitmap saveBitmap = bitmap;
		String fileName = "";
		fileName = "/sdcard/DCIM/远大图片/" + file;
		File f = new File(fileName);
		final String filePath = f.getAbsolutePath();
		if (!f.exists()) {
			File parent = new File(f.getParent());
			if (!parent.exists()) {
				parent.mkdirs();
			}
			save = saveBitmapForSdCard(saveBitmap, filePath);
		} else {
			return true;
		}
		return save;
	}

	public static boolean saveBitmapForSdCard(Bitmap bitmap, String path) {
		File f = new File(path);
		File parent = new File(f.getParent());
		FileOutputStream out = null;
		boolean compress = false;
		if (!parent.exists()) {
			parent.mkdirs();
		}
		try {
			boolean createNewFile = f.createNewFile();
			if (!createNewFile) {
				return false;
			}
			out = new FileOutputStream(f);
			compress = bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		} finally {
			try {
				if (out != null) {
					out.flush();
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return compress;
	}

	public static ByteArrayOutputStream decodeBitmap(String path, int size) {

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;// 设置成了true,不占用内存，只获取bitmap宽高
		BitmapFactory.decodeFile(path, opts);
		opts.inSampleSize = computeSampleSize(opts, -1, 1024 * 800);

		opts.inJustDecodeBounds = false;// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inPurgeable = true;
		opts.inInputShareable = true;
		opts.inDither = false;
		opts.inPurgeable = true;
		opts.inPreferredConfig = Bitmap.Config.RGB_565;
		opts.inTempStorage = new byte[100 * 1024];
		FileInputStream is = null;
		Bitmap bmp = null;

		ByteArrayOutputStream baos = null;
		try {
			is = new FileInputStream(path);
			bmp = BitmapFactory.decodeFileDescriptor(is.getFD(), null, opts);
			// double scale = getScaling(opts.outWidth * opts.outHeight,
			// showWidth
			// * showHeight);
			// int wtemp=(int) (opts.outWidth * scale);
			// int htemp=(int) (opts.outHeight * scale);
			// if (opts.outWidth * scale>showWidth) {
			// wtemp=showWidth;
			// }
			// if (opts.outHeight * scale>showHeight) {
			// htemp=showHeight;
			// }
			// Bitmap bmp2 = Bitmap.createScaledBitmap(bmp,
			// wtemp,
			// htemp, true);
			//
			// bmp.recycle();
			baos = new ByteArrayOutputStream();

			bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			int options = 100;
			while (baos.toByteArray().length / 1024 > size && options > 4) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
				baos.reset();// 重置baos即清空baos
				bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
				options -= 2;// 每次都减少10
			}
			bmp.recycle();
			return baos;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError oError) {
			oError.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null) {
					is.close();
				}
				if (baos != null) {
					baos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
			System.gc();
		}
		return baos;
	}

	private static double getScaling(int src, int des) {
		/**
		 * 目标尺寸÷原尺寸 sqrt开方，得出宽高百分比
		 */
		double scale = Math.sqrt((double) des / (double) src);
		return scale;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static String getRandomColor() {

		String r, g, b;
		Random random = new Random();
		r = Integer.toHexString(random.nextInt(256)).toUpperCase();
		g = Integer.toHexString(random.nextInt(256)).toUpperCase();
		b = Integer.toHexString(random.nextInt(256)).toUpperCase();

		r = r.length() == 1 ? "0" + r : r;
		g = g.length() == 1 ? "0" + g : g;
		b = b.length() == 1 ? "0" + b : b;

		return "#" + r + g + b;

	}

	public static void setRuntime() {
		try {
			Class<?> cls = Class.forName("dalvik.system.VMRuntime");
			Method getRuntime = cls.getMethod("getRuntime");
			Object obj = getRuntime.invoke(null);// obj就是Runtime
			if (obj == null) {
				System.err.println("obj is null");
			} else {
				System.out.println(obj.getClass().getName());
				Class<?> runtimeClass = obj.getClass();
				int CWJ_HEAP_SIZE = 6 * 1024 * 1024;
				Method setMinimumHeapSize = runtimeClass.getMethod(
						"setMinimumHeapSize", long.class);

				setMinimumHeapSize.invoke(obj, CWJ_HEAP_SIZE);
				Method setTargetHeapUtilization = runtimeClass.getMethod(
						"setTargetHeapUtilization", float.class);
				float TARGET_HEAP_UTILIZATION = 0.75f;
				setTargetHeapUtilization.invoke(obj, TARGET_HEAP_UTILIZATION);
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}



	public static String getTime(long time) {
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm");
		return format.format(new Date(time));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}

	public static String getChatTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));
		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			result = getTime(timesamp);
			break;
		}

		return result;
	}
}
