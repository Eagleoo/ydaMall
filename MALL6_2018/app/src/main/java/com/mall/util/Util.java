package com.mall.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.Vibrator;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.baidu.voicerecognition.android.VoiceRecognitionConfig;
import com.baidu.voicerecognition.android.ui.BaiduASRDigitalDialog;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.model.BannerInfo;
import com.mall.model.InMaill;
import com.mall.model.LocationModel;
import com.mall.model.ShopM;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.newmodel.UpDataVersionDialog;
import com.mall.officeonline.ShopOfficeFrame;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.view.App;
import com.mall.view.AppManager;
import com.mall.view.LMSJDetailFrame;
import com.mall.view.Lin_MainFrame;
import com.mall.view.LoginFrame;
import com.mall.view.MapLMSJFrame;
import com.mall.view.R;
import com.mall.view.StoreMainFrame;
import com.mall.view.UserCenterFrame;
import com.mall.view.carMall.OrderBeanAll;
import com.mall.view.messageboard.MyToast;
import com.mall.view.service.UpdateService;
import com.mall.yyrg.model.PhotoInfo;
import com.tapadoo.alerter.Alerter;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.functions.Consumer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class Util {

    public static boolean isSelectCity = false;
    private static String selectcitystr = "";
    public static String zoneid = "";

    public static String netversion = "";
    public static String[] info;

    //未读消息
    public static List<InMaill> mailllist = new ArrayList<>();


    public static void setSelectCity(String citystr, boolean isSelect) {
        selectcitystr = citystr;
        isSelectCity = isSelect;
    }

    public static String getCityStr() {
        if (isSelectCity) {
            return selectcitystr;
        }
        return "";
    }

    public static boolean update = false;
    public static boolean isfristopenPopUpAds = true;
    public static String version = ""; // 正式版
    public static String detailID = "1873";// 正式版
    public static String[] week = new String[]{"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    public static String SaveCity = "";
    public static String citylong = ""; //精度
    public static String citylat = "";    //纬度

    public static int aa = 0;

    public static String orderBody = "";
    public static String redbenamoney = "";

    public static OrderBeanAll order;
    public static String payType;

    // 微信登录tag
    public static final int MSG_LOGIN = 2;
    public static final int MSG_AUTH_CANCEL = 3;
    public static final int MSG_AUTH_ERROR = 4;
    public static final int MSG_AUTH_COMPLETE = 5;

    public static String kfg = "";

    public static final String rootPath = "/sdcard/yuanda/";
    public static final String proPath = "/sdcard/yuanda/prod/";
    public static final String qrPath = Environment.getExternalStorageDirectory() + "/yuanda/远大二维码/";
    public static final String bannerPath = "/sdcard/yuanda/banner/";
    public static final String downPath = "/sdcard/yuanda/download/";
    public static final String shopMPath = "/sdcard/yuanda/shopM/";
    public static final String shopPath = "/sdcard/yuanda/shop/";
    public static final String apkPath = "/sdcard/yuanda/download/";
    public static final String phoneLocalPath = "/sdcard/yuanda/";
    public static final String xqImgCache = "/sdcard/yuanda/xqcache";
    public static final String _400 = "4006663838";

    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);
    private static ExecutorService bitmapSaveService = Executors.newFixedThreadPool(3);


    /**
     *
     */


    static HashMap<String, String> shopcate = new HashMap<>();


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


    public interface PermissionsCallBack {
        void success();

        void failure();

    }

    public static void checkpermissions(final String[] requestPermissionstr, Activity mActivity, final PermissionsCallBack permissionsCallBack) {
        RxPermissions rxPermissions = new RxPermissions(mActivity);
        rxPermissions.request(requestPermissionstr).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {
                if (aBoolean) {
                    permissionsCallBack.success();
                } else {
                    permissionsCallBack.failure();
                }
            }
        });
    }

//    new Consumer<Permission>() {
//        @Override
//        public void accept(Permission permission) throws Exception {
//            if (permission.granted) {
//                num[0]++;
//                if (num[0] == requestPermissionstr.length) {
//                    Log.e("权限检查", "同意权限");
//                }
//            } else if (permission.shouldShowRequestPermissionRationale) {
//                Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
//            } else {
//
//            }
//        }
//    }

    public static boolean isNull(Object obj) {
        return null == obj || "".equalsIgnoreCase(obj.toString());
    }

    public static boolean checkPhoneNumber(String phonenumber) {
        Pattern pa = Pattern.compile("^[1][3,4,5,8,7,6][0-9]{9}$");
        Matcher ma = pa.matcher(phonenumber);
        return ma.matches();
    }

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

    /**
     * 日期时间格式转换
     *
     * @param typeFrom 原格式
     * @param typeTo   转为格式
     * @param value    传入的要转换的参数
     * @return
     */
    public static String formatDateTime(String typeFrom, String typeTo, String value) {
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

    public static Date toDate(String sdate) {
        sdate = sdate.replaceAll("/", "-");
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
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
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
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            else
                ftime = hour + "小时前";
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * @param imgPath
     * @param bitmap
     * @param imgFormat 图片格式
     * @return
     */
    @SuppressLint("NewApi")
    public static String imgToBase64(String imgPath, Bitmap bitmap, String imgFormat) {
        if (imgPath != null && imgPath.length() > 0) {
            bitmap = readBitmap(imgPath);
        }
        if (null == bitmap) {
            LogUtils.e("get image " + imgPath + " bitmap is fail");
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
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
        return output;
    }

    private static Bitmap readBitmap(String imgPath) {
        try {
            return BitmapFactory.decodeFile(imgPath);
        } catch (Exception e) {
            return null;
        }
    }

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

    public static List<Activity> list = new ArrayList<Activity>();

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

    public static void show(String message, Context activity) {
        Toast toast = Toast.makeText(activity, message, Toast.LENGTH_SHORT);
        WHD whd = Util.getScreenSize(activity);
        toast.setGravity(Gravity.TOP, 0, (whd.getHeight() / 5));
        toast.show();
    }

    public static void show(String message) {
        Toast toast = Toast.makeText(App.getContext(), message, Toast.LENGTH_LONG);
        WHD whd = Util.getScreenSize(App.getContext());
        toast.setGravity(Gravity.TOP, 0, (whd.getHeight() / 5));
        toast.show();
    }

    public static void showAlert(String message, Activity activity, String[] info, final Boolean isCheckMore, final OnClickListener onClickListener) {
        Alerter alert = Alerter.create(activity)
                .setTitle("查看新版本特性")
                .setTitleColor("#000000")

                .setTextColor("#333333")
                .setDuration(3000)
                .setIcon(R.drawable.tanchuan)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Alerter.hide();
                        if (!isCheckMore) {
                            return;
                        }
                        onClickListener.onClick(view);

                    }
                })
                .setBackgroundColorInt(Color.parseColor("#CCFFFFFF"))
                .enableSwipeToDismiss();
        if (!isCheckMore) {
            alert.setText(info[2] + "\n" + info[3] + "\n" + info[4]);
        }

        if (!isCheckMore) {
//            alert.setIcon1Visib(View.GONE);
        }
        alert.show();

    }

    public static void showAlert(String message, Activity activity) {
        Alerter.create(activity)
                .setText(message)
                .setDuration(1500)
                .setBackgroundColorRes(R.color.red)
                .enableSwipeToDismiss()
                .show();
    }

    public static CustomProgressDialog showProgress(String message, Context context) {
        return CustomProgressDialog.showProgressDialog(context, message);
    }

    public static AlertDialog alert(Context context, String title, String content) {
        final AlertDialog dialog = new AlertDialog.Builder(context).create();
        final View rootView = LayoutInflater.from(context).inflate(R.layout.sel_all_address, null);
        LinearLayout root = rootView.findViewById(R.id.sel_address_container);

        View itemView = LayoutInflater.from(context).inflate(R.layout.sel_all_address_item, null);
        TextView titleV = rootView.findViewById(R.id.sel_address_title);
        TextView item = itemView.findViewById(R.id.sel_item_address);
        titleV.setText(title);
        item.setText(content);
        item.setPadding(15, 15, 15, 15);
        item.setBackgroundColor(Color.WHITE);
        item.setCompoundDrawables(null, null, null, null);
        item.setSingleLine(false);
        root.addView(itemView);
        dialog.setView(rootView, 0, 0, 0, 0);
        dialog.show();
        return dialog;
    }

    public static void openWeb(Context activity, String url) {
        if (!Util.isNull(url)) {
            Intent intent = new Intent("android.intent.action.VIEW", Uri.parse(url));
            activity.startActivity(intent);
            if (activity instanceof Activity)
                ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                        android.R.anim.slide_out_right);
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

    public static boolean openWeChat(Context activity, String url) {
        if (isInstall(activity, "com.tencent.mm")) {
            try {
                Intent localIntent = new Intent("android.intent.action.VIEW");
                localIntent.setData(Uri.parse(url));
                localIntent.setPackage("com.tencent.mm");
                // localIntent.setComponent(new
                // ComponentName("com.tencent.mm",""));
                activity.startActivity(localIntent);
                if (activity instanceof Activity)
                    ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Util.show(e.getMessage() + "\n\n" + e.getLocalizedMessage(), activity);
                return false;
            }
        }
        return false;
    }

    public static View getView(Context context, int layoutId) {
        LayoutInflater flater = LayoutInflater.from(context);
        return flater.inflate(layoutId, null);
    }

    public static void showIntent(String message, final Context activity, final Class c, final String[] params,
                                  final String[] values) {
        AlertDialog.Builder builder = new Builder(activity);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(activity, c);
                intent.putExtra("className", activity.getClass().toString());
                if (null != params && 0 != params.length) {
                    int i = 0;
                    for (String key : params) {
                        intent.putExtra(key, values[i]);
                        i++;
                    }
                }
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    public static void showIntent(String message, final Context activity, final Class c, final Class c1) {

        VoipDialog voipDialog = new VoipDialog(message, activity, "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (c == UserCenterFrame.class) {
                    intent.setClass(activity, Lin_MainFrame.class);
                    intent.putExtra("toTab", "usercenter");
                } else {
                    intent.setClass(activity, c);
                }

                intent.putExtra("className", activity.getClass().toString());
                activity.startActivity(intent);
                if (activity instanceof Activity)
                    ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(activity, c1);
                intent.putExtra("className", activity.getClass().toString());
                activity.startActivity(intent);
                if (activity instanceof Activity)
                    ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
            }
        });
        voipDialog.show();
    }

    public static void showIntent(final Context activity, final Class c) {
        showIntent(activity, c, null, null);
    }

    public static void showIntent(final Context activity, final Class c, String[] keys, Serializable[] values) {
        Intent intent = new Intent();
        intent.setClass(activity, c);
        intent.putExtra("openSource", activity.getClass().toString());
        if (null != keys) {
            int i = 0;
            for (String key : keys) {
                intent.putExtra(key, values[i]);
                i++;
            }
        }
        activity.startActivity(intent);

        if (activity instanceof Activity)
            ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right);
    }

    // 收不到验证码的弹出框
    @SuppressLint("NewApi")
    public static void showConnotYzm(final Context activity) {

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog Yzmdialog = builder.create();
        View views = LayoutInflater.from(activity).inflate(R.layout.d_connotyzm, null);
        views.findViewById(R.id.connot_yzm_but).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Yzmdialog.dismiss();
            }
        });
        Yzmdialog.show();
        WindowManager.LayoutParams parms = Yzmdialog.getWindow().getAttributes();
        int width = Yzmdialog.getWindow().getWindowManager().getDefaultDisplay().getWidth() * 7 / 10;
        int height = width / 2 * 3;
        parms.height = height;
        parms.width = width;
        Yzmdialog.getWindow().setAttributes(parms);
        Yzmdialog.getWindow().setContentView(views);

    }

    public static void showIntent(String message, final Context activity, final Class c) {
        showIntent(message, activity, c, Lin_MainFrame.class);
    }

    public static void showIntent(String message, final Context activity, final Class c, final Class c1,
                                  final String[] keys, final String[] values) {
        AlertDialog.Builder builder = new Builder(activity);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(activity, c);
                intent.putExtra("className", activity.getClass().toString());
                int i = 0;
                for (String key : keys) {
                    intent.putExtra(key, values[i]);
                    i++;
                }
                activity.startActivity(intent);
                if (activity instanceof Activity)
                    ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                            android.R.anim.slide_out_right);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (null != c1) {
                    Intent intent = new Intent();
                    intent.setClass(activity, c1);
                    intent.putExtra("className", activity.getClass().toString());
                    activity.startActivity(intent);
                    if (activity instanceof Activity)
                        ((Activity) activity).overridePendingTransition(android.R.anim.slide_in_left,
                                android.R.anim.slide_out_right);
                }
            }
        });
        builder.create().show();
    }

    public static void showIntent(String message, final Context activity, String left, String right,
                                  final DialogInterface.OnClickListener leftClick, final DialogInterface.OnClickListener rightClick) {
        AlertDialog.Builder builder = new Builder(activity);
        builder.setMessage(message);
        builder.setTitle("提示");
        if (isNull(left))
            left = "确定";
        if (isNull(right))
            right = "取消";
        builder.setPositiveButton(left, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (null != leftClick)
                    leftClick.onClick(dialog, which);
            }
        });
        builder.setNegativeButton(right, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (null != rightClick)
                    rightClick.onClick(dialog, which);
            }
        });
        builder.create().show();
    }

    public static void showIntent(String message, final Context activity, DialogInterface.OnClickListener leftClick,
                                  DialogInterface.OnClickListener rightClick) {
        showIntent(message, activity, null, null, leftClick, rightClick);
    }

    public static void showIntent(final Context activity, String message, final Class c, final Class c1, String left,
                                  String right) {
        AlertDialog.Builder builder = new Builder(activity);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton(left, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(activity, c);
                intent.putExtra("className", activity.getClass().toString());
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton(right, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(activity, c1);
                intent.putExtra("className", activity.getClass().toString());
                activity.startActivity(intent);
            }
        });
        builder.create().show();
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
            newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        }
        return newbmp;
    }

    public static void doPhone(String phone, Context context) {
        String[] dh = phone.split(",");
        if (1 == dh.length)
            dh = phone.split("，");
        if (1 == dh.length)
            dh = phone.split("/");
        if (1 == dh.length)
            dh = phone.split("、");
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + dh[0]));
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

    public static void doLogin(String message, final Activity activity) {
        AlertDialog.Builder builder = new Builder(activity);
        builder.setMessage(message);
        builder.setTitle("提示");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setClass(activity, LoginFrame.class);
                intent.putExtra("className", activity.getClass().toString());
                activity.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                activity.startActivity(new Intent(activity, Lin_MainFrame.class));
            }
        });
        builder.create().show();
    }

    public static void add(Activity a) {
        list.add(a);
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

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnected();
    }

    /**
     * 获取手机信息
     */
    public void getPhoneInfo(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String mtyb = android.os.Build.BRAND;// 手机品牌
        String mtype = android.os.Build.MODEL; // 手机型号
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String imei = tm.getDeviceId();
        String imsi = tm.getSubscriberId();
        String numer = tm.getLine1Number(); // 手机号码
        String serviceName = tm.getSimOperatorName(); // 运营商
    }

    public static double getDouble(Double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The scale must be a positive integer or zero");
        }
        BigDecimal b = null == v ? new BigDecimal("0.0") : new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static String getDouble(double f) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(f);
    }

    public static String getToUp(double n) {
        String fraction[] = {"角", "分"};
        String digit[] = {"零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖"};
        String unit[][] = {{"元", "万", "亿"}, {"", "拾", "佰", "仟"}};

        String head = n < 0 ? "负" : "";
        n = Math.abs(n);

        String s = "";
        for (int i = 0; i < fraction.length; i++) {
            s += (digit[(int) (Math.floor(n * 10 * Math.pow(10, i)) % 10)] + fraction[i]).replaceAll("(零.)+", "");
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
            s = p.replaceAll("(零.)*零$", "").replaceAll("^$", "零") + unit[0][i] + s;
        }
        return head + s.replaceAll("(零.)*零元", "元").replaceFirst("(零.)+", "").replaceAll("(零.)+", "零").replaceAll("^整$",
                "零元整");
    }

    public static boolean doUpdate(final Activity frame, final ImageView imageView, final DialogInterface.OnClickListener leftClick,
                                   final DialogInterface.OnClickListener rightClick) {


        Util.asynTask(new IAsynTask() {

            @Override
            public void updateUI(Serializable runData) {
                if (null == runData)
                    return;
                final String[] updateList = (String[]) runData;
                if (null == updateList || 0 >= updateList.length) {
                    return;
                }

                if (!"success".equals(updateList[0]) && !Util.isNull(updateList[0])) {
                    StringBuffer sb = new StringBuffer(128);
                    for (int i = 3; i < updateList.length; i++)
                        sb.append(updateList[i] + "\n");
                    // sb.append("建议在WIFI下使用。\n");
                    AlertDialog.Builder builder = new Builder(frame);

                    String text = "注：适配安卓4.0及以上版本！\n";

                    builder.setMessage("当前版本：" + Util.version + "\n最新版本：" + updateList[1] + "\n" + text + "更新时间："
                            + updateList[2] + "\n" + sb.toString());
                    // + sb.toString()
                    builder.setTitle("发现新版本，是否去下载？");

                    imageView.setVisibility(View.VISIBLE);
                    update = true;
                    builder.setPositiveButton("下载", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != leftClick)
                                leftClick.onClick(dialog, which);
                            else {
                                // Intent intent = new Intent(
                                // "android.intent.action.VIEW", Uri
                                // .parse(updateList[0]));
                                // frame.startActivity(intent);

                                frame.getApplicationContext().startService(new Intent(frame, UpdateService.class));
                            }
                        }
                    });
                    builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (null != rightClick)
                                rightClick.onClick(dialog, which);
                            else {
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.create().show();

                }
            }

            @Override
            public Serializable run() {

                return Util.update();
            }
        });

        return update;
    }

    public static String getdownloadurl(String ver) {
        return "http://" + Web.downAddress + "/Mall" + ver + ".apk?r=" + System.currentTimeMillis();
    }

    public static String[] update() {

        List<String> result = new ArrayList<String>(4);
        BufferedReader br = null;
        try {
            URL aURL = new URL("http://" + Web.updateAddress + "/update.txt?r=" + System.currentTimeMillis());
            Log.e("updateUrl", aURL + "");
            URLConnection con = aURL.openConnection();
            br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("GBK")));
            String ver = br.readLine();
            Log.e("Check Version", "当前版本=" + version + "        网络版本=" + ver);
            if (!version.equals(ver)) {
                try {
                    int currVer = Integer.parseInt(version.replaceAll("\\.", ""));
                    int webVer = Integer.parseInt(ver.replaceAll("\\.", "").trim());
                    if (webVer > currVer) {
//                        result.add("success");
                        // 下载地址
                        result.add(
                                "http://" + Web.downAddress + "/Mall" + ver + ".apk?r=" + System.currentTimeMillis());
                        Log.e("downUrl",
                                "http://" + Web.downAddress + "/Mall" + ver + ".apk?r=" + System.currentTimeMillis());
                        // 版本号
                        result.add(ver);
                        // 更新时间
                        result.add(br.readLine());
                        String line = null;

                        while (true) {
                            line = br.readLine();
                            if (Util.isNull(line) || line.matches("\r\n|\n|\r"))
                                break;
                            result.add(line);
                        }
                    } else {
                        result.add("success");
                        while (true) {
                            String line = null;
                            line = br.readLine();
                            if (Util.isNull(line) || line.matches("\r\n|\n|\r"))
                                break;
                            result.add(line);
                        }
                    }

                } catch (Exception e) {
                    result.add("success");
                }
            } else {
                result.add("success");
                while (true) {
                    String line = null;
                    line = br.readLine();
                    if (Util.isNull(line) || line.matches("\r\n|\n|\r"))
                        break;
                    result.add(line);
                }
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toArray(new String[result.toArray().length]);
    }

    public static String[] update1() {

        List<String> result = new ArrayList<String>(4);
        BufferedReader br = null;
        try {
            URL aURL = new URL("http://" + Web.updateAddress + "/update.txt?r=" + System.currentTimeMillis());
            Log.e("updateUrl", aURL + "");
            URLConnection con = aURL.openConnection();
            br = new BufferedReader(new InputStreamReader(con.getInputStream(), Charset.forName("GBK")));
            String ver = br.readLine();
            Log.e("Check Version", "当前版本=" + version + "        网络版本=" + ver);

            try {
                int currVer = Integer.parseInt(version.replaceAll("\\.", ""));
                int webVer = Integer.parseInt(ver.replaceAll("\\.", "").trim());

                // 下载地址
                Log.e("downUrl",
                        "http://" + Web.downAddress + "/Mall" + ver + ".apk?r=" + System.currentTimeMillis());
                // 版本号
                result.add(ver);
                // 更新时间
                result.add(br.readLine());
                String line = null;
                while (true) {
                    line = br.readLine();
                    if (Util.isNull(line) || line.matches("\r\n|\n|\r"))
                        break;
                    result.add(line);
                }

            } catch (Exception e) {
                result.add("success");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != br)
                    br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result.toArray(new String[result.toArray().length]);
    }

    public static void asynDownLoadImage(final String url, final ImageView imgView) {
        final Handler handler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bitmap map = msg.getData().getParcelable("img");
                if (null != map)
                    imgView.setImageBitmap(map);
            }
        };
        new Thread(new Runnable() {
            public void run() {
                android.os.Message message = new android.os.Message();
                Bundle data = new Bundle();
                data.putParcelable("img", getBitmap(url));
                message.setData(data);
                handler.sendMessage(message);
            }
        }).start();
    }

    public static void asynTaskTwo(Activity frame, String message, final IAsynTask task) {

        final CustomProgressDialog dialog = Util.showProgress(message, frame);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                // 回调接口的run()方法有返回结果之后，Dialog消失
                dialog.cancel();
                dialog.dismiss();
                Bundle data = msg.getData();
                Serializable ser = data.getSerializable("IAsynTaskRunData");
                if ("success".equals(data.getString("IAsynTaskResult")))
                    task.updateUI(ser);

            }

        };

        new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
                Message msg = new Message();
                Bundle data = new Bundle();
                Serializable runData;
                try {
                    runData = task.run();
                    data.putString("IAsynTaskResult", "success");
                } catch (Throwable e) {
                    runData = e;
                    data.putString("IAsynTaskResult", "error");
                }
                data.putSerializable("IAsynTaskRunData", runData);
                msg.setData(data);
                handler.sendMessage(msg);
                Looper.loop();
            }
        }).start();
    }

    public static void asynTask(final Context frame, final String message, final IAsynTask task) {
        final CustomProgressDialog dialog = Util.showProgress(message, frame);
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Bundle data = msg.getData();
                String dataType = data.getString("IAsynTaskResult");
                Log.e("TAG", "dataType" + dataType);
                Serializable ser = data.getSerializable("IAsynTaskRunData");
                if ("success".equals(dataType))
                    task.updateUI(ser);
                else if ("error".equals(dataType)) {
                    // Util.show("网络异常，请稍候再试！", frame);
                    System.err.println("--------------异步任务错误！-------------");
                    if (null == ser)
                        Log.e("Util异步任务错误！", ((Throwable) ser) + "");
                    else
                        ((Throwable) ser).printStackTrace();
                } else {
                    task.updateUI(null);
                }
                dialog.cancel();
                dialog.dismiss();
            }
        };

        executorService.execute(new Runnable() {
            // new Thread(new Runnable(){
            public void run() {
                Log.e("TAG", "1");
                Message msg = new Message();
                Log.e("TAG", "2");
                Bundle data = new Bundle();
                Log.e("TAG", "3");
                try {
                    Log.e("TAG", "4");
                    data.putSerializable("IAsynTaskRunData", task.run());
                    Log.e("TAG", "5");
                    data.putString("IAsynTaskResult", "success");
                    Log.e("TAG", "6");
                } catch (Throwable e) {
                    Log.e("TAG", "7");
                    LogUtils.e("------------------异步任务错误！-----------------", e);
                    data.putSerializable("IAsynTaskRunData", e);
                    Log.e("TAG", "8");
                    data.putString("IAsynTaskResult", "error");
                    Log.e("TAG", "9");
                }
                Log.e("TAG", "10");
                msg.setData(data);
                Log.e("TAG", "11" + data.getSerializable("IAsynTaskRunData"));
                handler.sendMessage(msg);
            }
        });
    }

    public static void asynTask1(final Context frame, final String message, final IAsynTask task) {
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
                    // Util.show("网络异常，请稍候再试！", frame);
                    System.err.println("--------------异步任务错误！-------------");
                    if (null == ser)
                        Log.e("Util异步任务错误！", ((Throwable) ser) + "");
                    else
                        ((Throwable) ser).printStackTrace();
                } else {
                    task.updateUI(null);
                }
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
                    LogUtils.e("------------------异步任务错误！-----------------", e);
                    data.putSerializable("IAsynTaskRunData", e);
                    data.putString("IAsynTaskResult", "error");
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        });
    }

    public static void asynTask(final IAsynTask task) {
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
                    // Util.show("网络异常，请稍候再试！", App.getContext());
                    System.err.println("--------------异步任务错误！-------------");
                    if (null == ser)
                        Log.e("Util异步任务错误！", ((Throwable) ser) + "");
                    else
                        ((Throwable) ser).printStackTrace();
                } else {
                    task.updateUI(null);
                }
            }
        };

        new Thread(new Runnable() {
            public void run() {
                Message msg = new Message();
                Bundle data = new Bundle();
                try {
                    data.putSerializable("IAsynTaskRunData", task.run());
                    data.putString("IAsynTaskResult", "success");
                } catch (Throwable e) {
                    LogUtils.e("------------------异步任务错误！-----------------", e);
                    data.putSerializable("IAsynTaskRunData", e);
                    data.putString("IAsynTaskResult", "error");
                }
                msg.setData(data);
                handler.sendMessage(msg);
            }
        }).start();
    }

    public static void asynTaskWitSyncronized(final IAsynTask task, final Activity c) {
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
                synchronized (c) {
                    Serializable runData = task.run();
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    data.putString("IAsynTaskResult", "success");
                    data.putSerializable("runData", runData);
                    msg.setData(data);
                    handler.sendMessage(msg);
                }
            }
        }).start();

    }

    public static Bitmap getBitmap(String url) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 2;
        return getBitmap(url, options);
    }

    private static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);

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

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128
                : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
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

    /**
     * 获取当前网络类型
     *
     * @return 0：没有网络
     * 10：WIFI网络
     * 2：2g移动网络
     * 3：3g移动网络
     * 4：4g移动网络
     */
    public static int getNetworkType(Context context) {
        int netType = 0;
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //获取ConnectivityManager对象对应的NetworkInfo对象
        //获取WIFI连接的信息
        NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        //获取移动数据连接的信息
        NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
            MyLog.e("WIFI已连接,移动数据已连接");
            netType = 10;
        } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
            MyLog.e("WIFI已连接,移动数据已断开");
            netType = 10;
        } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
            MyLog.e("WIFI已断开,移动数据已连接" + dataNetworkInfo.getSubtype());
            switch (dataNetworkInfo.getSubtype()) {
                case TelephonyManager.NETWORK_TYPE_CDMA:  // telcom
                case TelephonyManager.NETWORK_TYPE_1xRTT: // telecom
                case TelephonyManager.NETWORK_TYPE_GPRS:  // unicom
                case TelephonyManager.NETWORK_TYPE_EDGE:  // cmcc

                    netType = 2;
                case TelephonyManager.NETWORK_TYPE_EHRPD:  // telecom
                case TelephonyManager.NETWORK_TYPE_EVDO_0: // telecom
                case TelephonyManager.NETWORK_TYPE_EVDO_A: // telecom 3.5G
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // telecom 3.5G
                case TelephonyManager.NETWORK_TYPE_HSPA:   // unicom
                case TelephonyManager.NETWORK_TYPE_HSPAP:  // unicom
                case TelephonyManager.NETWORK_TYPE_HSDPA:  // unicom 3.5G
                case TelephonyManager.NETWORK_TYPE_HSUPA:  // unicom 3.5G
                case TelephonyManager.NETWORK_TYPE_UMTS:   // unicom
                    netType = 3;
                case TelephonyManager.NETWORK_TYPE_LTE:
                    netType = 4;
            }


        } else {
            MyLog.e("WIFI已断开,移动数据已断开");
            netType = 0;
        }

        return netType;
    }

    public static String getNetType(int netType) {
        String netTypeStr = "";

        switch (netType) {
            case 0:
                netTypeStr = "没有网络";
                break;
            case 2:
                netTypeStr = "2G 网络";
                break;
            case 3:
                netTypeStr = "3G 网络";
                break;
            case 4:
                netTypeStr = "4G 网络";
                break;
            case 10:
                netTypeStr = "WIFI";
                break;

        }
        return netTypeStr;
    }

    /**
     * 加载本地缩略图
     *
     * @param path
     * @param showWidth
     * @param showHeight
     * @return
     */
    public static Bitmap getLocationThmub(String path, int showWidth, int showHeight) {
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
        while ((height / sampleSize > showHeight) || (width / sampleSize > showWidth)) {
            sampleSize += 1;
        }
        // 不再只加载图片实际边缘
        options.inJustDecodeBounds = false;
        // 并且制定缩放比例
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Config.ARGB_8888;
        return BitmapFactory.decodeFile(path, options);
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

    public static Bitmap getLocalBitmapWidthOptions(final String path) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, opts);
        int zoomSize = 1;
        int height = opts.outHeight;
        int width = opts.outWidth;
        // 图片实际的宽与高，根据默认最大大小值，得到图片实际的缩放比例
        while ((height / zoomSize > 400) || (width / zoomSize > 240)) {
            zoomSize += 1;
        }
        opts.inJustDecodeBounds = false;
        opts.inSampleSize = zoomSize;
        return BitmapFactory.decodeFile(path, opts);
    }

    public void CropBitmap(Bitmap bitmap) {

    }

    public static Bitmap getLocalBitmap(String path, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 2;
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.outWidth = width;
        opts.outHeight = height;
        Bitmap bm = getLocalBitmap(path, opts);
        return zoomBitmap(bm, width, height);
    }

    public static Bitmap ReadBitmap(Context context, int screenWidth, int screenHight, Bitmap bitmap) {
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
    public static Bitmap getBitmap(Bitmap bitmap, int screenWidth, int screenHight) {
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

    public static Bitmap getLocalBitmap(String path, BitmapFactory.Options options) {
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

    public static Bitmap getLocalBitmap(String path, BitmapFactory.Options options, Context c) {
        if (Util.isNull(path))
            return null;
        Bitmap temBitmap = null;
        try {
            temBitmap = BitmapFactory.decodeFile(path, options);
            if (temBitmap == null) {
                temBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.no_get_banner);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            temBitmap = BitmapFactory.decodeResource(c.getResources(), R.drawable.no_get_banner);
        }
        return temBitmap;
    }

    public static boolean downLoad(String href, String path) throws IOException {
        File localFile = new File(path);
        InputStream in = null;
        OutputStream out = null;
        URL aURL = new URL(href);
        URLConnection con = aURL.openConnection();
        File saveFile = new File(path);
        if (!saveFile.exists()) {
            File parent = new File(saveFile.getParent());
            if (!parent.exists())
                parent.mkdirs();
            saveFile.createNewFile();
            saveFile = null;
        }
        out = new FileOutputStream(new File(path));
        in = con.getInputStream();
        if (null != in) {
            int length = (int) con.getContentLength();
            if (length == localFile.length() && 0 != localFile.length())
                return true;
            if (length != -1) {
                byte[] temp = new byte[1024];
                int readLen = 0;
                while ((readLen = in.read(temp)) > 0) {
                    out.write(temp, 0, readLen);
                }
                out.flush();
                temp = null;
            }
        }

        if (null != in) {
            in.close();
            in = null;
        }
        if (null != out) {
            out.close();
            out = null;
        }
        return true;
    }

    public static Bitmap getBitmap(String url, Options opts) {
        Util.strickMode();
        Bitmap bm = null;
        InputStream in = null;
        BufferedInputStream bin = null;
        try {
            URL aURL = new URL(url);
            URLConnection con = aURL.openConnection();
            con.connect();
            in = con.getInputStream();
            if (null != in) {
                int length = (int) con.getContentLength();
                if (length != -1) {
                    byte[] imgData = new byte[length];
                    byte[] temp = new byte[65535];
                    int readLen = 0;
                    int destPos = 0;
                    while ((readLen = in.read(temp)) > 0) {
                        System.arraycopy(temp, 0, imgData, destPos, readLen);
                        destPos += readLen;
                    }

                    bm = BitmapFactory.decodeByteArray(imgData, 0, imgData.length, opts);
                }
            }
        } catch (FileNotFoundException e) {
            ;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != in)
                    in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != bin)
                    bin.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;
    }

    public static void doDaoHang(final Context context, final ShopM m) {
        if (null == m) {
            Util.show("对不起，没有找到该联盟商家", context);
            return;
        }
        if (Util.isNull(m.getPointX()) || Util.isNull(m.getPointY())) {
            Util.show("对不起，该商家没有设置位置！", context);
            return;
        }
        AlertDialog.Builder builder = new Builder(context);
        builder.setTitle("请选择导航模式");
        String message = "请选择以下任意一种导航模式，快速帮您引导至目的地";
        builder.setMessage(message);
        builder.setPositiveButton("驾车", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                doOpenMapDaohang(context, m);
            }
        });
        builder.setNegativeButton("步行", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                doOpenMapDaohang(context, m);
            }
        });
        builder.setNeutralButton("公交", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                doOpenMapDaohang(context, m);
            }
        });
        builder.show();
    }

    public static void doOpenMapDaohang(final Context context, final ShopM m) {
        boolean isBaidu = Util.isInstall(context, "com.baidu.BaiduMap");
        boolean isGaoDe = Util.isInstall(context, "com.autonavi.minimap");
        boolean isGoogle = Util.isInstall(context, "com.google.android.apps.maps");
        if (!isBaidu && !isGoogle && !isGaoDe) {
            Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？", context, MapLMSJFrame.class, null,
                    new String[]{"dh", "mode"}, new String[]{m.getId(), "2"});
            return;
        }
        final AlertDialog builder2 = new Builder(context).create();
        builder2.setTitle("请选择导航软件！");
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        Resources res = context.getResources();
        LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        int _5dp = Util.dpToPx(context, 5F);
        ll.setMargins(_5dp, _5dp, _5dp, _5dp);
        if (isBaidu) {
            TextView baiduMap = new TextView(context);
            baiduMap.setLayoutParams(ll);
            Drawable dra = res.getDrawable(R.drawable.baidu);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
            baiduMap.setCompoundDrawables(dra, null, null, null);
            baiduMap.setText("百度地图");
            baiduMap.setGravity(Gravity.CENTER_VERTICAL);
            baiduMap.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = "";
                    String lng = "";
                    LocationModel locationModel = LocationModel.getLocationModel();
                    if (!Util.isNull(locationModel.getLatitude())) {
                        lat = (locationModel.getLatitude() + 0.0065) + "";
                        lng = (locationModel.getLongitude() + 0.0060) + "";
                    }
                    try {
                        Intent intent = Intent.getIntent("baidumap://map/direction?origin=latlng:" + lat + "," + lng
                                + "|name:当前位置&destination=latlng:" + (Double.parseDouble(m.getPointY()) + 0.0065) + ","
                                + (Double.parseDouble(m.getPointX()) + 0.0060) + "|name:" + m.getName()
                                + " &mode=driving&region=" + locationModel.getCity());
                        context.startActivity(intent);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                    builder2.cancel();
                    builder2.dismiss();
                }
            });
            root.addView(baiduMap);
        }
        if (isGaoDe) {
            TextView gaodeMap = new TextView(context);
            gaodeMap.setLayoutParams(ll);
            Drawable dra = res.getDrawable(R.drawable.gaode);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
            gaodeMap.setCompoundDrawables(dra, null, null, null);
            gaodeMap.setText("高德地图");
            gaodeMap.setGravity(Gravity.CENTER_VERTICAL);
            gaodeMap.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Intent intent = Intent
                    // .getIntent("androidamap://navi?sourceApplication=mall666&poiname=&poiid=&lat="
                    // + m.getPointY()
                    // + "&lon="
                    // + m.getPointX() + "&dev=1&style=0");
                    Intent intent = new Intent("android.intent.action.VIEW",
                            android.net.Uri.parse(
                                    "androidamap://navi?sourceApplication=Mall666&poiname=" + get(m.getName()) + "&lat="
                                            + m.getPointY() + "&lon=" + m.getPointX() + "&level=10&dev=0&style=2"));
                    intent.setPackage("com.autonavi.minimap");
                    context.startActivity(intent);
                    builder2.cancel();
                    builder2.dismiss();
                }
            });
            root.addView(gaodeMap);
        }
        if (isGoogle) {
            TextView gugeMap = new TextView(context);
            gugeMap.setLayoutParams(ll);
            Drawable dra = res.getDrawable(R.drawable.guge);
            dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
            gugeMap.setCompoundDrawables(dra, null, null, null);
            gugeMap.setText("Google地图");
            gugeMap.setGravity(Gravity.CENTER_VERTICAL);
            gugeMap.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String lat = "";
                    String lng = "";
                    LocationModel locationModel = LocationModel.getLocationModel();
                    if (!Util.isNull(locationModel.getLatitude())) {
                        lat = locationModel.getLatitude() + "";
                        lng = locationModel.getLongitude() + "";
                    }
                    Intent i = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr=" + lat + "," + lng + "&daddr="
                                    + m.getPointY() + "," + m.getPointX() + "&hl=zh"));
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                    i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(i);
                    builder2.cancel();
                    builder2.dismiss();
                }
            });
            root.addView(gugeMap);
        }
        builder2.setView(root);
        builder2.show();
    }

    public static boolean isInstall(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> packs = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        for (ApplicationInfo info : packs) {
            if (info.packageName.equals(packageName))
                return true;
        }
        return false;
    }

    /**
     * 获取当前屏幕大小和密度
     */
    public static WHD getScreenSize(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        manager.getDefaultDisplay().getMetrics(displayMetrics);
        int sWidth = displayMetrics.widthPixels;
        int sHeight = displayMetrics.heightPixels;
        int dpi = displayMetrics.densityDpi;
        return new WHD(sHeight, sWidth, dpi);
    }

    public static float getDip(Activity drame) {

        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, drame.getResources().getDisplayMetrics());
    }

    public static void close() {
        for (Activity a : list) {
            a.finish();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public static Class c = null;

    /**
     * dp转px
     *
     * @param context
     * @param dpValue
     * @return
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getStatusBarHeight(Activity activity) {
        int statusBarHeight = -1;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = activity.getResources().getDimensionPixelSize(resourceId);
        }
        Log.e("状态栏高度", statusBarHeight + "PX");
        return statusBarHeight;
    }

    public static void initTitle(final Activity activity, String message) {
        initTitle(activity, message, null, null, null, Integer.MIN_VALUE, null, Integer.MIN_VALUE);
    }

    public static void initTitle(final Activity activity, String message, OnClickListener left) {
        initTitle(activity, message, left, null, null, Integer.MIN_VALUE, null, Integer.MIN_VALUE);
    }

    public static void initTitle(final Activity activity, String message, OnClickListener left, OnClickListener right, String text) {//右边文字
        initTitle(activity, message, left, right, text, Integer.MIN_VALUE, null, Integer.MIN_VALUE);
    }

    public static void initTitle(final Activity activity, String message, OnClickListener left, OnClickListener right, int img) {//右边图片
        initTitle(activity, message, left, right, null, img, null, Integer.MIN_VALUE);
    }

    public static void initTitle(final Activity activity, String message, OnClickListener left, OnClickListener right, int img, OnClickListener right1, int img1) {//右边两个图片
        initTitle(activity, message, left, right, null, img, right1, img1);
    }

    public static void initTitle(final Activity activity, String message, OnClickListener left, OnClickListener right, String text, int img, OnClickListener right1, int img1) {
        AppManager.getNewInstance().addActivity(activity);
        if (!list.contains(activity))
            list.add(activity);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        View highView = activity.findViewById(R.id.highView);
        highView.setMinimumHeight(getStatusBarHeight(activity));
        TextView center = activity.findViewById(R.id.center);
        center.setText(message);
        ImageView back = activity.findViewById(R.id.back);
        ImageView right_img = activity.findViewById(R.id.right_img);
        ImageView right_img1 = activity.findViewById(R.id.right_img1);
        TextView right_text = activity.findViewById(R.id.right_text);
        if (null != left) {
            back.setOnClickListener(left);
        } else {
            back.setVisibility(View.GONE);
        }
        if (null != right) {
            if (null != text) {
                right_text.setText(text);
                right_text.setVisibility(View.VISIBLE);
                right_text.setOnClickListener(right);
            } else if (Integer.MIN_VALUE != img) {
                right_img.setImageResource(img);
                right_img.setVisibility(View.VISIBLE);
                right_img.setOnClickListener(right);
            }
        } else {
            right_text.setVisibility(View.GONE);
            right_img.setVisibility(View.GONE);
        }
        if (null != right1) {
            right_img1.setImageResource(img1);
            right_img1.setVisibility(View.VISIBLE);
            right_img1.setOnClickListener(right1);
        } else {
            right_img1.setVisibility(View.GONE);
        }
    }

    public static void initTop(final Activity frame, String message, int src, OnClickListener left,
                               OnClickListener right) {
        AppManager.getNewInstance().addActivity(frame);
        if (!list.contains(frame))
            list.add(frame);
        View back = frame.findViewById(R.id.topback);
        if (null != back) {
            if (null != left) {
                back.setOnClickListener(left);
            } else {
                back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        frame.finish();
                    }
                });
            }

            TextView text = (TextView) frame.findViewById(R.id.topCenter);
            if (null != text)
                text.setText(message);
            ImageView right2 = (ImageView) frame.findViewById(R.id.topright);
            if (null != right2) {
                if (Integer.MIN_VALUE != src) {
                    if (src == 0) {
                        right2.setVisibility(View.GONE);
                    } else {
                        right2.setScaleType(ScaleType.CENTER_INSIDE);
                        right2.setImageDrawable(frame.getResources().getDrawable(src));
                        right2.setVisibility(ImageButton.VISIBLE);
                        if (null != right)
                            right2.setOnClickListener(right);
                    }
                }
            }
        }
    }

    public static void initTop(final Activity frame, String message, int src, OnClickListener left,
                               OnClickListener right, String imgtitle) {
        if (!list.contains(frame))
            list.add(frame);
        AppManager.getNewInstance().addActivity(frame);
        View back = frame.findViewById(R.id.topback);
        if (null != back) {
            if (null != left) {
                back.setOnClickListener(left);
            } else {
                back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Intent intent = new Intent(frame,
                        // Lin_MainFrame.class);
                        // frame.startActivity(intent);
                        frame.finish();
                    }
                });
            }
            TextView imagetitle = (TextView) frame.findViewById(R.id.imgtitle_320);
            imagetitle.setText(imgtitle);
            imagetitle.setVisibility(View.VISIBLE);
            TextView text = (TextView) frame.findViewById(R.id.topCenter);
            if (null != text)
                text.setText(message);
            ImageView right2 = (ImageView) frame.findViewById(R.id.topright);
            if (null != right2) {
                if (Integer.MIN_VALUE != src) {
                    right2.setScaleType(ScaleType.CENTER_INSIDE);
                    right2.setImageDrawable(frame.getResources().getDrawable(src));
                    right2.setVisibility(ImageButton.VISIBLE);
                    if (null != right)
                        right2.setOnClickListener(right);
                }
            }
        }
    }

    public static void initTopWithTwoButton(final Activity frame, String title, int src1, int src2,
                                            OnClickListener click1, OnClickListener click2, OnClickListener left) {
        if (!list.contains(frame))
            list.add(frame);
        AppManager.getNewInstance().addActivity(frame);
        View back = frame.findViewById(R.id.topback);
        if (null != back) {
            // 左边返回点击
            if (null != left) {
                back.setOnClickListener(left);
            } else {
                back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        frame.finish();
                    }
                });
            }
            // 中间文字
            TextView text = (TextView) frame.findViewById(R.id.topCenter);
            if (null != text)
                text.setText(title);
            ImageView right1 = (ImageView) frame.findViewById(R.id.rightone);
            ImageView right2 = (ImageView) frame.findViewById(R.id.righttwo);
            if (null != right1) {
                if (Integer.MIN_VALUE != src1) {
                    right1.setScaleType(ScaleType.CENTER_INSIDE);
                    right1.setImageDrawable(frame.getResources().getDrawable(src1));
                    right1.setVisibility(ImageButton.VISIBLE);
                    if (null != right1)
                        right1.setOnClickListener(click1);
                }
            }
            if (null != right2) {
                if (Integer.MIN_VALUE != src2) {
                    right2.setScaleType(ScaleType.CENTER_INSIDE);
                    right2.setImageDrawable(frame.getResources().getDrawable(src2));
                    right2.setVisibility(ImageButton.VISIBLE);
                    if (null != right2)
                        right2.setOnClickListener(click2);
                }
            }
        }
    }

    public static int addNotification(int id, Context context, String title, String content) {
        int icon = android.R.drawable.stat_notify_chat;
        return addNotification(id, context, icon, title, content, new Intent(), null);
    }

    public static int addNotification(int id, Context context, int icon, String title, String content, Intent intent,
                                      RemoteViews view) {
        if (-1 == icon)
            icon = android.R.drawable.stat_notify_chat;
//		Notification notification = new Notification(icon, title, System.currentTimeMillis());// 概要
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);// 1为请求码
        Notification notification = new NotificationCompat.Builder(context)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText("")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(pendingIntent)
                .build();


        // 0为Flag标志位
        notification.defaults = Notification.DEFAULT_SOUND;// 发送状态栏的默认铃声
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击通知之后，取消状态栏图标
        if (null != view) {
            // 通知显示的布局
            notification.contentView = view;
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);// 得到系统通知服务
        manager.notify(id, notification);// 通知系统我们定义的notification，id为该notification的id；这里定义为100.
        return id;
    }

    /**
     * 安装
     */
    public void instance(Context context, File file) {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    public static void initTop(final Activity frame, String message, int src, OnClickListener click) {
        initTop(frame, message, src, null, click);
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
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                    drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            drawable.draw(canvas);
            return bitmap;
        } else {
            return null;
        }
    }

    public static boolean checkLoginOrNot() {
        User user = new UserData().getUser();
        return user == null ? false : true;
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

    public static boolean matchEmail(String email) {
        Pattern p = Pattern.compile("[a-zA-Z0-9-_]+@+[a-zA-Z0-9]+.+[a-zA-Z0-9]");
        Matcher ma = p.matcher(email);
        return ma.matches();
    }

    public static boolean checkQQ(String QQ) {
        String regex = "^[1-9][0-9]{4,} $";
        return check(QQ, regex);
    }

    public static boolean check(String str, String regex) {
        boolean flag = true;
        try {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(str);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
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

    public static String getUSER_KEY(Date curDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy$MM$dd");
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH");
        SimpleDateFormat formatter2 = new SimpleDateFormat("mm-ssSSS");
        String USER_KEY = formatter.format(curDate) + "rwen999" + formatter1.format(curDate) + "yda360"
                + formatter2.format(curDate);
        return stringMd5(stringMd5(USER_KEY).toUpperCase());
    }

    public static String getUSER_KEYPWD(Date curDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        String USER_KEYPWD = formatter.format(curDate);
        return USER_KEYPWD;
    }

    public static String stringMd5(String input) {
        try {
            // 拿到一个MD5转换器（如果想要SHA1加密参数换成"SHA1"）
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            // 输入的字符串转换成字节数组
            byte[] inputByteArray = input.getBytes();
            // inputByteArray是输入字符串转换得到的字节数组
            messageDigest.update(inputByteArray);
            // 转换并返回结果，也是字节数组，包含16个元素
            byte[] resultByteArray = messageDigest.digest();
            // 字符数组转换成字符串返回
            return byteArrayToHex(resultByteArray);

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符）
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

    private boolean chekbirthday(String b) {
        String patter = "yyyy-mm-dd";
        Pattern p = Pattern.compile("[a-zA-Z0-9-_]+@+[a-zA-Z0-9]+.+[a-zA-Z0-9]");
        Matcher ma = p.matcher(b);
        return ma.matches();
    }

    public static String DateComputeAddOneDay(String takeofftime, String landingtime, String date) {
        String s = "";
        int st = Integer.parseInt(takeofftime.replace(":", ""));
        int end = Integer.parseInt(landingtime.replace(":", ""));
        if (st > end) {
            date = date.replace("年", "-");
            date = date.replace("月", "-");
            date = date.replace("日", "-");
            String[] dates = date.split("-");
            int year = Integer.parseInt(dates[0]);
            int month = Integer.parseInt(dates[1]);
            int day = Integer.parseInt(dates[2]);
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month - 1);
            c.set(Calendar.DAY_OF_MONTH, day);
            c.add(Calendar.DAY_OF_MONTH, 1);
            int dayofweek = c.get(Calendar.DAY_OF_WEEK) - 1;
            System.out.println("Util=====dayofweek===" + dayofweek);
            s = year + "年" + month + "月" + c.get(Calendar.DAY_OF_MONTH) + "日" + week[dayofweek];
        }
        return s;
    }

    public static void PutIntentData(Activity a, String[] keys, String[] datas) {
        Intent in = a.getIntent();
        for (int i = 0; i < keys.length; i++) {
            in.putExtra(keys[i], datas[i]);
        }
    }

    @SuppressLint("NewApi")
    public static void strickMode() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    public static void initTop2(final Activity frame, String message, String righttitle, OnClickListener left,
                                OnClickListener right) {
        if (!list.contains(frame))
            list.add(frame);
        AppManager.getNewInstance().addActivity(frame);
        View back = frame.findViewById(R.id.top_back);
        if (null != back) {
            if (null != left) {
                back.setOnClickListener(left);
            } else {
                back.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Intent intent = new Intent(frame,
                        // Lin_MainFrame.class);
                        // frame.startActivity(intent);
                        frame.finish();
                    }
                });
            }
            TextView text = (TextView) frame.findViewById(R.id.toptitle);
            if (null != text)
                text.setText(message);
            TextView right2 = (TextView) frame.findViewById(R.id.topright);
            if (null != righttitle) {
                right2.setText(righttitle);
                right2.setOnClickListener(right);
            }
        }
    }

    public static Bitmap extractMiniThumb(Bitmap source, int width, int height) {
        return extractMiniThumb(source, width, height, true);
    }

    public static Bitmap extractMiniThumb(Bitmap source, int width, int height, boolean recycle) {
        if (source == null) {
            return null;
        }

        float scale;
        if (source.getWidth() < source.getHeight()) {
            scale = width / (float) source.getWidth();
        } else {
            scale = height / (float) source.getHeight();
        }
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        Bitmap miniThumbnail = transform(matrix, source, width, height, false);

        if (recycle && miniThumbnail != source) {
            source.recycle();
        }
        return miniThumbnail;
    }

    public static Bitmap transform(Matrix scaler, Bitmap source, int targetWidth, int targetHeight, boolean scaleUp) {
        int deltaX = source.getWidth() - targetWidth;
        int deltaY = source.getHeight() - targetHeight;
        if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
            /*
             * In this case the bitmap is smaller, at least in one dimension,
             * than the target. Transform it by placing as much of the image as
             * possible into the target and leaving the top/bottom or left/right
             * (or both) black.
             */
            Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight, Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b2);

            int deltaXHalf = Math.max(0, deltaX / 2);
            int deltaYHalf = Math.max(0, deltaY / 2);
            Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf + Math.min(targetWidth, source.getWidth()),
                    deltaYHalf + Math.min(targetHeight, source.getHeight()));
            int dstX = (targetWidth - src.width()) / 2;
            int dstY = (targetHeight - src.height()) / 2;
            Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight - dstY);
            c.drawBitmap(source, src, dst, null);
            return b2;
        }
        float bitmapWidthF = source.getWidth();
        float bitmapHeightF = source.getHeight();

        float bitmapAspect = bitmapWidthF / bitmapHeightF;
        float viewAspect = (float) targetWidth / targetHeight;

        if (bitmapAspect > viewAspect) {
            float scale = targetHeight / bitmapHeightF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        } else {
            float scale = targetWidth / bitmapWidthF;
            if (scale < .9F || scale > 1F) {
                scaler.setScale(scale, scale);
            } else {
                scaler = null;
            }
        }

        Bitmap b1;
        if (scaler != null) {
            // this is used for minithumb and crop, so we want to filter here.
            b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), scaler, true);
        } else {
            b1 = source;
        }

        int dx1 = Math.max(0, b1.getWidth() - targetWidth);
        int dy1 = Math.max(0, b1.getHeight() - targetHeight);

        Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth, targetHeight);

        if (b1 != source) {
            b1.recycle();
        }

        return b2;
    }

    /**
     * 去掉金额多余的位数
     *
     * @param string
     * @return
     */
    public static String deleteX(String string) {
        if (string.indexOf(".") != -1) {
            String string1 = string.split("\\.")[1];
            if (string1.length() == 4) {
                string1 = string1.substring(0, string1.length() - 2);
            } else if (string1.length() == 3) {
                string1 = string1.substring(0, string1.length() - 1);
            } else if (string1.length() == 1) {
                string1 = string1 + "0";
            }
            return string.split("\\.")[0] + "." + string1;
        } else {
            return string + "." + "00";
        }
    }

    public static void detailInformation(Context context, String message, String titles, int width) {
        // 将布局文件转化成view对象
        LayoutInflater inflaterDl = LayoutInflater.from(context);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.detail_information_dialog, null);
        final Dialog dialog = new AlertDialog.Builder(context).create();
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width * 4 / 5;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(layout);
        TextView title = (TextView) layout.findViewById(R.id.title);
        TextView update_count = (TextView) layout.findViewById(R.id.update_count);
        TextView queding = (TextView) layout.findViewById(R.id.queding);
        title.setText(titles);
        update_count.setText(message);
        queding.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
    }

    public static boolean isPhone(String phone) {
        if (Util.isNull(phone))
            return false;
        return phone.matches("^1[3|4|5|7|8|9]\\d{9}$");
    }

    public static String getNo_pUserId(String userId) {
        boolean isPhoneUserId = false;
        if (!Util.isNull(userId)) {
            isPhoneUserId = (userId.matches("^1[3|4|5|7|8|9]\\d{9}[_P|_p]*$"));
        }
        String newUserId = userId;
        if (isPhoneUserId) {
            if (userId.contains("_p"))
                newUserId = userId.replaceAll("_p", "");
            if (userId.contains("_P"))
                newUserId = userId.replaceAll("_P", "");
            // newUserId =
            // newUserId.substring(0,3)+"****"+newUserId.substring(newUserId.length()-4);
        }
        return newUserId;
    }

    public static String getMood_No_pUserId(String userId) {
        boolean isPhoneUserId = false;
        if (!Util.isNull(userId)) {
            isPhoneUserId = (userId.matches("^1[3|4|5|7|8|9]\\d{9}[_P|_p]*$"));
        }
        String newUserId = userId;
        if (isPhoneUserId) {
            if (userId.contains("_p"))
                newUserId = userId.replaceAll("_p", "");
            if (userId.contains("_P"))
                newUserId = userId.replaceAll("_P", "");
            newUserId = newUserId.substring(0, 3) + "****" + newUserId.substring(newUserId.length() - 4);
        } else {
            if (6 < userId.length())
                newUserId = userId.substring(0, 6) + "...";
        }
        return newUserId;
    }

  public   interface CallBackString {
        void callbackStr(String voidstr);
    }

    public static void getVoiceInfo(final Activity activity, final CallBackString callBackString) {
        final String[] requestPermissionstr = {

                Manifest.permission.RECORD_AUDIO

        };
        checkpermissions(requestPermissionstr, activity, new PermissionsCallBack() {

            @Override
            public void success() {
                Util.startVoiceRecognition(activity, new DialogRecognitionListener() {

                    @Override
                    public void onResults(Bundle results) {
                        ArrayList<String> rs = results != null ? results
                                .getStringArrayList(RESULTS_RECOGNITION) : null;
                        if (rs != null && rs.size() > 0) {
                            String str = rs.get(0).replace("。", "").replace("，", "");
                            callBackString.callbackStr(str);
                        }

                    }
                });
            }

            @Override
            public void failure() {
                Toast.makeText(activity, "请求允许应用权限请求", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void startVoiceRecognition(Context context, DialogRecognitionListener mRecognitionListener) {
        final String API_KEY = "RH9qQct23u3FpyRbBIICBTGm";
        final String SECRET_KEY = "2qjL2HCG0TFsUvSog0m31wwFVsf4WZIA";
        Bundle params = new Bundle();
        params.putString(BaiduASRDigitalDialog.PARAM_API_KEY, API_KEY);
        params.putString(BaiduASRDigitalDialog.PARAM_SECRET_KEY, SECRET_KEY);
        params.putInt(BaiduASRDigitalDialog.PARAM_DIALOG_THEME, BaiduASRDigitalDialog.THEME_BLUE_LIGHTBG);
        BaiduASRDigitalDialog mDialog = new BaiduASRDigitalDialog(context, params);
        mDialog.setDialogRecognitionListener(mRecognitionListener);
        mDialog.getParams().putInt(BaiduASRDigitalDialog.PARAM_PROP, VoiceRecognitionConfig.PROP_INPUT);
        mDialog.getParams().putString(BaiduASRDigitalDialog.PARAM_LANGUAGE, VoiceRecognitionConfig.LANGUAGE_CHINESE);

        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_START_TONE_ENABLE, false);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_END_TONE_ENABLE, false);
        mDialog.getParams().putBoolean(BaiduASRDigitalDialog.PARAM_TIPS_TONE_ENABLE, false);
        mDialog.show();

    }

    //
    public static SpannableString spanGreenWithString(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + s);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#29c738"));
        price_mar.setSpan(span, spam.length(), price_mar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spanGreen(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + Util.getDouble(Double.parseDouble(s), 2));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#29c738"));
        price_mar.setSpan(span, spam.length(), price_mar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spanGreenInt(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + Integer.parseInt(s));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#29c738"));
        price_mar.setSpan(span, spam.length(), price_mar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spanRed(String spam, String s) {
        System.out.println("----------s---------" + s);
        SpannableString price_mar = new SpannableString(spam + Util.getDouble(Double.parseDouble(s)));
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        price_mar.setSpan(span, spam.length() - 1, price_mar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spannBlueFromBegin(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + s);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#7caac9"));
        price_mar.setSpan(span, 0, spam.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spanRedWithString(String spam, String s) {
        SpannableString price_mar = new SpannableString(spam + s);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#ff0000"));
        price_mar.setSpan(span, spam.length(), price_mar.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static SpannableString spannedBoldWithMiddleBlack(String left, String middle, String right) {
        SpannableString price_mar = new SpannableString(left + middle + right);
        price_mar.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, left.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan span = new ForegroundColorSpan(Color.parseColor("#515151"));
        price_mar.setSpan(span, left.length(), (middle + left).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return price_mar;
    }

    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        if (listAdapter.getCount() == 1) {
            totalHeight = 30;
        }
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        totalHeight += 50;
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 获取圆角位图的方法
     *
     * @param bitmap 需要转化成圆角的位图
     * @param pixels 圆角的度数，数值越大，圆角越大
     * @return 处理后的圆角位图
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        if (null == bitmap)
            return bitmap;
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static String Html2Text(String inputString) {
        String htmlStr = inputString; // 含html标签的字符串
        htmlStr = htmlStr.replaceAll("</?[^<]+>", "");
        htmlStr = htmlStr.replace("&nbsp;", "");
        String textStr = "";
        java.util.regex.Pattern p_script;
        java.util.regex.Matcher m_script;
        java.util.regex.Pattern p_style;
        java.util.regex.Matcher m_style;
        java.util.regex.Pattern p_html;
        java.util.regex.Matcher m_html;

        java.util.regex.Pattern p_html1;
        java.util.regex.Matcher m_html1;

        try {
            String regEx_script = "<[//s]*?script[^>]*?>[//s//S]*?<[//s]*?///[//s]*?script[//s]*?>"; // 定义script的正则表达式{或<script[^>]*?>[//s//S]*?<///script>
            String regEx_style = "<[//s]*?style[^>]*?>[//s//S]*?<[//s]*?///[//s]*?style[//s]*?>"; // 定义style的正则表达式{或<style[^>]*?>[//s//S]*?<///style>
            String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
            String regEx_html1 = "<[^>]+";
            p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
            m_script = p_script.matcher(htmlStr);
            htmlStr = m_script.replaceAll(""); // 过滤script标签

            p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
            m_style = p_style.matcher(htmlStr);
            htmlStr = m_style.replaceAll(""); // 过滤style标签

            p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
            m_html = p_html.matcher(htmlStr);
            htmlStr = m_html.replaceAll(""); // 过滤html标签

            p_html1 = Pattern.compile(regEx_html1, Pattern.CASE_INSENSITIVE);
            m_html1 = p_html1.matcher(htmlStr);
            htmlStr = m_html1.replaceAll(""); // 过滤html标签

            textStr = htmlStr;

        } catch (Exception e) {
            System.err.println("Html2Text: " + e.getMessage());
        }

        return textStr;// 返回文本字符串
    }

    public static void showChoosedDialog(final Context c, String title, String leftMessage, String rightMessage,
                                         OnClickListener right) {
        final VoipDialog dialog = new VoipDialog(title, c, rightMessage, leftMessage, right, new OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        dialog.setCancelable(true);
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                }
                return false;
            }
        });
        dialog.show();

    }

    public static Bitmap compressImageFromFile(String srcPath) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;// 只读边,不读内容
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;//
        float ww = 480f;//
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;// 设置采样率

        newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        // return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
        // 其实是无效的,大家尽管尝试
        return bitmap;
    }

    public static void drawLayoutDropShadow(Context context, int width, int height, LinearLayout layout) {
        LinearLayout linearLayout = layout;
        BlurMaskFilter blurFilter = new BlurMaskFilter(3, BlurMaskFilter.Blur.OUTER);
        Paint shadowPaint = new Paint();
        shadowPaint.setMaskFilter(blurFilter);
        Drawable drawable = context.getResources().getDrawable(R.drawable.showlinearlayout);
        int[] offsetXY = new int[2];
        Bitmap originalBitmap = drawableToBitmap(drawable, width, height);
        Bitmap shadowBitmap = originalBitmap.extractAlpha(shadowPaint, offsetXY);
        Bitmap shadowImage32 = shadowBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas c = new Canvas(shadowImage32);
        c.drawBitmap(originalBitmap, 0, 0, null);

        Drawable d = new BitmapDrawable(shadowImage32);
        linearLayout.setBackgroundDrawable(d);
    }

    private static Bitmap drawableToBitmap(Drawable drawable, int width, int height) {

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static boolean saveBitmapToSdCard(String file, String pathName, Bitmap bitmap) {
        boolean save = false;
        final Bitmap saveBitmap = bitmap;
        String fileName = "";
        fileName = "/sdcard/DCIM/" + pathName + "/" + file;
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

    public static boolean saveBitmapToSdCard(String file, Bitmap bitmap) {
        return saveBitmapToSdCard(file, "心情图片", bitmap);
    }

    public static String saveBitmap(final List<Bitmap> bitmaps, final List<PhotoInfo> photoList, List<String> file) {
        StringBuilder imageFileName = new StringBuilder();
        for (int i = 0; i < bitmaps.size(); i++) {
            final Bitmap saveBitmap = bitmaps.get(i);
            PhotoInfo info = photoList.get(i + 1);
            String fileName = "";
            if (!Util.isNull(info.getPath_file())) {
                fileName = info.getPath_absolute();
            } else {
                fileName = "/sdcard/xqimgcache/" + saveBitmap.toString() + ".jpg";
            }
            File f = new File(fileName);
            final String filePath = f.getAbsolutePath();
            if (!f.exists()) {
                bitmapSaveService.execute(new Runnable() {
                    @Override
                    public void run() {
                        saveBitmapForSdCard(saveBitmap, filePath);
                    }
                });
            }
            imageFileName.append(fileName + "spkxqadapter");
        }
        return imageFileName.toString();
    }

    public static String saveBitmap(final List<Bitmap> bitmaps, final List<PhotoInfo> photoList) {
        StringBuilder imageFileName = new StringBuilder();
        for (int i = 0; i < bitmaps.size(); i++) {
            final Bitmap saveBitmap = bitmaps.get(i);
            PhotoInfo info = photoList.get(i + 1);
            String fileName = "";
            LogUtils.e(info.getPath_file() + "_____________");
            fileName = "/sdcard/xqimgcache/" + System.currentTimeMillis() + ".jpg";
            File f = new File(fileName);
            final String filePath = f.getAbsolutePath();
            if (!f.exists()) {
                bitmapSaveService.execute(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("saveBitmap", (saveBitmap == null) + "saveBitmap");
                        Log.e("filePath", (filePath == null) + "filePath");
                        saveBitmapForSdCard(saveBitmap, filePath);
                    }
                });
            }
            imageFileName.append(fileName + "spkxqadapter");
        }
        return imageFileName.toString();
    }

    public static boolean saveBitmapForSdCard(Bitmap bitmap, String path) {
        File f = new File(path);
        File parent = new File(f.getParent());
        FileOutputStream out = null;
        if (!parent.exists()) {
            parent.mkdirs();
        }
        try {
            f.createNewFile();
            out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            Log.i("SaveBitmap", "已经保存");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (null != out) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static OnTouchListener TouchLight = new OnTouchListener() {
        public final float[] IMAGE_SELECTED = new float[]{1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1, 0, 50, 0, 0, 0, 1,
                0};
        public final float[] IMAGE_NOT_SELECTED = new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
                0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(IMAGE_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getBackground().setColorFilter(new ColorMatrixColorFilter(IMAGE_NOT_SELECTED));
                v.setBackgroundDrawable(v.getBackground());
            }
            return false;
        }
    };
    public static OnTouchListener ImageViewTouchDark = new OnTouchListener() {
        public final float[] BT_SELECTED = new float[]{1, 0, 0, 0, -50, 0, 1, 0, 0, -50, 0, 0, 1, 0, -50, 0, 0, 0, 1,
                0};
        public final float[] BT_NOT_SELECTED = new float[]{1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1,
                0};

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            ImageView imageView = (ImageView) v;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                imageView.getDrawable().setColorFilter(new ColorMatrixColorFilter(BT_SELECTED));
                imageView.setImageDrawable(imageView.getDrawable());
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                imageView.getDrawable().setColorFilter(new ColorMatrixColorFilter(BT_NOT_SELECTED));
                imageView.setImageDrawable(imageView.getDrawable());
            }
            return true;
        }
    };
    private static VoipDialog dialog;

    public static void showShareToOfficeDialog(final Context c, String title, String leftMessage, String rightMessage,
                                               int resid) {
        dialog = new VoipDialog(title, c, rightMessage, leftMessage, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, ShopOfficeFrame.class);
                c.startActivity(intent);
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (dialog != null) {
                        dialog.dismiss();
                        Intent intent = new Intent(c, StoreMainFrame.class);
                        c.startActivity(intent);
                    }
                }
                return false;
            }
        });
        dialog.show();

    }

    @SuppressLint("NewApi")
    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth(); // 获取位图的宽
        int height = bmp.getHeight(); // 获取位图的高
        int[] pixels = new int[width * height]; // 通过位图的大小创建像素点数组
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) (red * 0.3 + green * 0.3 + blue * 0.4);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Config.RGB_565);

        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);

        Bitmap resizeBmp = ThumbnailUtils.extractThumbnail(newBmp, 380, 460);
        return resizeBmp;
    }

    public static byte[] httpGet(final String url) {
        if (url == null || url.length() == 0) {
            LogUtils.e("httpGet, url is null");
            return null;
        }

        HttpClient httpClient = getNewHttpClient();
        HttpGet httpGet = new HttpGet(url);

        try {
            HttpResponse resp = httpClient.execute(httpGet);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                LogUtils.e("httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }

            return EntityUtils.toByteArray(resp.getEntity());

        } catch (Exception e) {
            LogUtils.e("httpGet exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] httpPost(String url, String entity) {
        if (url == null || url.length() == 0) {
            Log.e("wxhttppost", "httpPost, url is null");
            return null;
        }
        HttpClient httpClient = getNewHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new StringEntity(entity));
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse resp = httpClient.execute(httpPost);
            if (resp.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                Log.e("wxhttppost", "httpGet fail, status code = " + resp.getStatusLine().getStatusCode());
                return null;
            }
            return EntityUtils.toByteArray(resp.getEntity());
        } catch (Exception e) {
            Log.e("wxhttppost", "httpPost exception, e = " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static class SSLSocketFactoryEx extends SSLSocketFactory {

        SSLContext sslContext = SSLContext.getInstance("TLS");

        public SSLSocketFactoryEx(KeyStore truststore)
                throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
            super(truststore);

            TrustManager tm = new X509TrustManager() {

                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType)
                        throws java.security.cert.CertificateException {
                }
            };

            sslContext.init(null, new TrustManager[]{tm}, null);
        }

        @Override
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        }

        @Override
        public Socket createSocket() throws IOException {
            return sslContext.getSocketFactory().createSocket();
        }
    }

    private static HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public static String sha1(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }

        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes());

            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }

    public static void deleteItemDialog(final Context c, String title, String leftMessage, String rightMessage,
                                        OnClickListener right) {
        final VoipDialog dialog = new VoipDialog(title, c, rightMessage, leftMessage, null, right);
        dialog.setCancelable(true);
        dialog.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                    if (dialog != null) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                }
                return false;
            }
        });
        dialog.show();

    }

    public static void Vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    /**
     * 计算相差的小时
     *
     * @param
     * @param endTime
     * @return
     */
    public static float getTimeDifferenceHour(String endTime) {

        Date dataDay = new Date(System.currentTimeMillis());//获取当前时间

        SimpleDateFormat dateDay = new SimpleDateFormat("yyyy-MM-dd");
        String day = dateDay.format(dataDay);
        endTime = day + " " + endTime;
        float hour1 = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        try {
            Date parse = new Date(System.currentTimeMillis());//获取当前时间

            Date parse1 = dateFormat.parse(endTime);

            long diff = parse1.getTime() - parse.getTime();

            String string = Long.toString(diff);

            float parseFloat = Float.parseFloat(string);

            hour1 = parseFloat / (60 * 60 * 1000);


        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return hour1;

    }

    /***
     * 获取指定日后 后 dayAddNum 天的 日期
     *
     * @param day
     *            日期，格式为String："2013-9-3";
     * @param dayAddNum
     *            增加天数 格式为int;
     * @return
     */
    public static String getDateStr(String day, long dayAddNum) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date nowDate = null;
        try {
            nowDate = df.parse(day);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Date newDate2 = new Date(nowDate.getTime() + dayAddNum * 24 * 60 * 60 * 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateOk = simpleDateFormat.format(newDate2);
        return dateOk;
    }


    /**
     * 格式化时间
     *
     * @param l          定位时间
     * @param strPattern 时间格式
     * @return
     */
    public static String formatUTC(long l, String strPattern) {
        if (TextUtils.isEmpty(strPattern)) {
            strPattern = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(strPattern, Locale.CHINA);
        sdf.applyPattern(strPattern);
        return sdf == null ? "NULL" : sdf.format(l);
    }

    /**
     * 将给定的字符串给定的长度两端对齐
     *
     * @param str  待对齐字符串
     * @param size 汉字个数，eg:size=5，则将str在5个汉字的长度里两端对齐
     * @Return
     */
    public static SpannableStringBuilder justifyString(String str, int size) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (TextUtils.isEmpty(str)) {
            return spannableStringBuilder;
        }
        char[] chars = str.toCharArray();
        if (chars.length >= size || chars.length == 1) {
            return spannableStringBuilder.append(str);
        }
        int l = chars.length;
        float scale = (float) (size - l) / (l - 1);
        for (int i = 0; i < l; i++) {
            spannableStringBuilder.append(chars[i]);
            if (i != l - 1) {
                SpannableString s = new SpannableString("　");//全角空格
                s.setSpan(new ScaleXSpan(scale), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableStringBuilder.append(s);
            }
        }
        return spannableStringBuilder;
    }

    public static void backgroundAlpha(Context context, float bgAlpha) {
        WindowManager.LayoutParams lp = ((Activity) context).getWindow().getAttributes();
        lp.alpha = bgAlpha; //0.0-1.0
        ((Activity) context).getWindow().setAttributes(lp);
    }


    public static String getCityPicUrl(String cityid) {

        return "http://app.yda360.com/zone/" + cityid + ".png";
    }


    public static String getShopCate(String str) {

        String str1 = "";
        if (shopcate.size() == 0) {
            initshopcate();
        }
        try {

            str1 = shopcate.get(str);
        } catch (Exception e) {

        }

        return str1;


    }


    private static void initshopcate() {
        shopcate.put("餐厅美食", "1");
        shopcate.put("休闲娱乐", "2");
        shopcate.put("摄影写真", "3");
        shopcate.put("美容美发", "4");
        shopcate.put("汽车之家", "5");
        shopcate.put("特惠购物", "6");
        shopcate.put("酒店住宿", "7");
        shopcate.put("旅游度假", "8");
        shopcate.put("家居建材", "9");
        shopcate.put("咖啡茶饮", "10");
        shopcate.put("养生理疗", "11");
        shopcate.put("健身运动", "12");
        shopcate.put("教育培训", "13");
        shopcate.put("网吧游戏", "14");
        shopcate.put("医药服务", "15");
        shopcate.put("生活服务", "19");


    }

    /**
     * 剩余时间
     */

    public static MyTime getshengyutime(String time) {
        MyTime myTime = new MyTime();
        String str = "";

        try {

            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long nm = 1000 * 60;//一分钟的毫秒数
            long ns = 1000;//一秒钟的毫秒数

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = sdf.parse(time);
            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
            long diff = date.getTime() - curDate.getTime();
            long day = diff / nd;//计算差多少天
            myTime.setDay(Math.abs(day));
            long hour = diff % nd / nh;//计算差多少小时
            myTime.setHour(Math.abs(hour));
            long min = diff % nd % nh / nm;//计算差多少分钟
            myTime.setMin(Math.abs(min));
            long sec = diff % nd % nh % nm / ns;//计算差多少秒//输出结果
            myTime.setSec(Math.abs(sec));
            System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
            str = day + "天" + hour + "小时" + min + "分" + sec + "秒";
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return myTime;
    }


    public static MyTime getshengyutime(String starttime, String endtime, SimpleDateFormat sdf) {
        MyTime myTime = new MyTime();
        String str = "";

        try {

            long nd = 1000 * 24 * 60 * 60;//一天的毫秒数
            long nh = 1000 * 60 * 60;//一小时的毫秒数
            long nm = 1000 * 60;//一分钟的毫秒数
            long ns = 1000;//一秒钟的毫秒数


            Date startDate = sdf.parse(starttime);
            Date endDate = sdf.parse(endtime);
            long diff = startDate.getTime() - endDate.getTime();
            long day = diff / nd;//计算差多少天
            myTime.setDay(day);
            long hour = diff % nd / nh;//计算差多少小时
            myTime.setHour(hour);
            long min = diff % nd % nh / nm;//计算差多少分钟
            myTime.setMin(min);
            long sec = diff % nd % nh % nm / ns;//计算差多少秒//输出结果
            myTime.setSec(sec);
            System.out.println("时间相差：" + day + "天" + hour + "小时" + min + "分钟" + sec + "秒。");
            str = day + "天" + hour + "小时" + min + "分" + sec + "秒";
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return myTime;
    }

    public static String percentage(double result) {
        int temp = (int) (result * 1000);
        result = (double) temp / 10;

        return result + "";
    }


    public static <T> List<T> deepCopy(List<T> src) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(byteOut);
        out.writeObject(src);

        ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
        ObjectInputStream in = new ObjectInputStream(byteIn);
        @SuppressWarnings("unchecked")
        List<T> dest = (List<T>) in.readObject();
        return dest;
    }

    private static final int invalidAge = -1;//非法的年龄，用于处理异常。

    public static int getAgeByIDNumber(String idNumber) {
        String dateStr;
        if (idNumber.length() == 15) {
            dateStr = "19" + idNumber.substring(6, 12);
        } else if (idNumber.length() == 18) {
            dateStr = idNumber.substring(6, 14);
        } else {//默认是合法身份证号，但不排除有意外发生
            return invalidAge;
        }


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date birthday = simpleDateFormat.parse(dateStr);
            return getAgeByDate(birthday);
        } catch (ParseException e) {
            return invalidAge;
        }


    }

    /**
     * 根据生日计算年龄
     *
     * @param dateStr 这样格式的生日 1990-01-01
     */

    public static int getAgeByDateString(String dateStr) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date birthday = simpleDateFormat.parse(dateStr);
            return getAgeByDate(birthday);
        } catch (ParseException e) {
            return -1;
        }
    }


    public static int getAgeByDate(Date birthday) {
        Calendar calendar = Calendar.getInstance();

        //calendar.before()有的点bug
        if (calendar.getTimeInMillis() - birthday.getTime() < 0L) {
            return invalidAge;
        }


        int yearNow = calendar.get(Calendar.YEAR);
        int monthNow = calendar.get(Calendar.MONTH);
        int dayOfMonthNow = calendar.get(Calendar.DAY_OF_MONTH);

        calendar.setTime(birthday);


        int yearBirthday = calendar.get(Calendar.YEAR);
        int monthBirthday = calendar.get(Calendar.MONTH);
        int dayOfMonthBirthday = calendar.get(Calendar.DAY_OF_MONTH);

        int age = yearNow - yearBirthday;


        if (monthNow <= monthBirthday && monthNow == monthBirthday && dayOfMonthNow < dayOfMonthBirthday || monthNow < monthBirthday) {
            age--;
        }

        return age;
    }

    public static int checkChineseNumber(String str) {
        int count = 0;
        String reg = "[\\u4e00-\\u9fa5]";
        str = str.replace(".", "");
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(str);
        while (m.find()) {
            for (int i = 0; i <= m.groupCount(); i++) {
                count = count + 1;
            }
        }
        return count;
    }

    /**
     * 屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        WindowManager manager = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        Display display = manager.getDefaultDisplay();
        display.getMetrics(displayMetrics);

        return displayMetrics.widthPixels;

    }

    public static UpDataVersionDialog updataui(Context context) {

        UpDataVersionDialog upDataVersionDialog = new UpDataVersionDialog(context, "发现新版本");
        WindowManager.LayoutParams lp = upDataVersionDialog.getWindow().getAttributes();
        lp.width = Util.getScreenWidth(); //设置宽度
        upDataVersionDialog.getWindow().setAttributes(lp);
//        upDataVersionDialog.show();
        return upDataVersionDialog;
    }

    public static UpDataVersionDialog updataui() {

        UpDataVersionDialog upDataVersionDialog = new UpDataVersionDialog(App.getContext(), "发现新版本");
        WindowManager.LayoutParams lp = upDataVersionDialog.getWindow().getAttributes();
        lp.width = Util.getScreenWidth(); //设置宽度
        upDataVersionDialog.getWindow().setAttributes(lp);
        upDataVersionDialog.show();
        return upDataVersionDialog;
    }

    public static String getVersion(Context context) {

        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int version_code = info.versionCode;
        String version = info.versionName;
        return version;
    }

    public static void download(final BannerInfo bannerInfo) {
        //获得图片的地址
        final String imgUrl = bannerInfo.getImage();
//        final String imgUrl = "http://pic59.nipic.com/file/20150113/14563866_191535842000_2.jpg";

        String image = imgUrl.substring(imgUrl.lastIndexOf("/") + 1);
        final String imagename = image.split("\\.")[0];
        final String imageName = imagename + ".png";


        new Thread(new Runnable() {
            @Override
            public void run() {
                final Bitmap bitmap = getPic(imgUrl);//下载

                onSaveBitmap(bitmap, imageName, bannerInfo);//保存到本地


            }
        }).start();


        //Target
//        Target target = new Target() {
//
//            @Override
//            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
////				String image = url.substring(url.lastIndexOf("/")+1).split();
//
//                String imageName = imagename + ".png";
//
//                File dcimFile = FileUtil
//                        .getDCIMFile(FileUtil.PATH_PHOTOGRAPH, imageName);
//
//                Log.e("图片检查", "bitmap=" + bitmap);
//                FileOutputStream ostream = null;
//                try {
//                    ostream = new FileOutputStream(dcimFile);
//                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
//                    ostream.close();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//                Log.e("保存地址", "图片下载至:" + dcimFile);
//                DbUtils db = DbUtils.create(App.getContext());
//                try {
//                    db.deleteAll(BannerInfo.class);
//                    db.findAll(Selector.from(BannerInfo.class));
//                } catch (DbException e) {
//                    e.printStackTrace();
//                    Log.e("savebanner", "e" + e.toString());
//                }
//                try {
//                    Log.e("getTime", "12");
//                    bannerInfo.setPath(dcimFile + "");
//                    db.saveOrUpdate(bannerInfo);
//                    Log.e("图片本地保存成功", bannerInfo.getPath());
//                } catch (DbException e) {
//                    Log.e("getTime", "13" + e.toString());
//                    e.printStackTrace();
//                }
//            }
//
//            @Override
//            public void onBitmapFailed(Drawable errorDrawable) {
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//
//            }
//        };
//
//        //Picasso下载
//        Picasso.with(App.getContext()).load(imgUrl).into(target);


    }

    /*
    Android保存图片到系统图库：http://blog.csdn.net/xu_fu/article/details/39158747
    * */
    private static void onSaveBitmap(final Bitmap mBitmap, String filename, BannerInfo bannerInfo) {
        // 第一步：首先保存图片
        //将Bitmap保存图片到指定的路径/sdcard/Boohee/下，文件名以当前系统时间命名,但是这种方法保存的图片没有加入到系统图库中

        final File dcimFile = FileUtil
                .getDCIMFile(FileUtil.PATH_PHOTOGRAPH, filename);

//        File appDir = new File(Environment.getExternalStorageDirectory(), "Boohee");
//
//        if (!appDir.exists()) {
//            appDir.mkdir();
//        }
//
//        String fileName = System.currentTimeMillis() + ".jpg";
//        File file = new File(appDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(dcimFile);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DbUtils db = DbUtils.create(App.getContext());
        try {
            db.deleteAll(BannerInfo.class);
            db.findAll(Selector.from(BannerInfo.class));
        } catch (DbException e) {
            e.printStackTrace();
            Log.e("savebanner", "e" + e.toString());
        }
        try {
            Log.e("getTime", "12");
            bannerInfo.setPath(dcimFile + "");
            db.saveOrUpdate(bannerInfo);
            Log.e("图片本地保存成功", bannerInfo.getPath());
        } catch (DbException e) {
            Log.e("getTime", "13" + e.toString());
            e.printStackTrace();
        }
        Log.e("保存地址", "图片下载至:" + dcimFile);
//        // 第二步：其次把文件插入到系统图库
//        try {
//            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
////   /storage/emulated/0/Boohee/1493711988333.jpg
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        // 第三步：最后通知图库更新
//        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file)));
//        //context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }


    public static Bitmap getPic(String url) {
        //获取okHttp对象get请求
        try {
            OkHttpClient client = new OkHttpClient();
            //获取请求对象
            Request request = new Request.Builder().url(url).build();
            //获取响应体
            ResponseBody body = client.newCall(request).execute().body();
            //获取流
            InputStream in = body.byteStream();
            //转化为bitmap
            Bitmap bitmap = BitmapFactory.decodeStream(in);

            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //安装应用的流程
    private void installProcess() {

        boolean haveInstallPermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //先获取是否有安装未知来源应用的权限
            haveInstallPermission = App.getContext()
                    .getPackageManager().canRequestPackageInstalls();
            Log.e("安装应用需要打开未知来源权限", "haveInstallPermission" + haveInstallPermission);
            if (!haveInstallPermission) {//没有权限
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    MyToast.makeText(App.getContext(), "安装应用需要打开未知来源权限，请去设置中开启权限", 5).show();
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    App.getActivity().startActivityForResult(intent, 10086);
                }


            }
        }

    }

    public static Toast MyToast(Context context, String message, int duration) {
        Toast result = new Toast(context);
        //获取LayoutInflater对象
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //由layout文件创建�?��View对象
        View layout = inflater.inflate(R.layout.dialog_toast, null);
        //实例化ImageView和TextView对象
        TextView textView = (TextView) layout.findViewById(R.id.message);
        textView.setText(message);
        result.setView(layout);
        result.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        result.setDuration(duration);
        return result;
    }

    //打开文件时调用
    public static Intent openFiles(String filesPath) {
        Uri uri = Uri.parse("file://" + filesPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);

        String type = getMIMEType(filesPath);
        intent.setDataAndType(uri, type);
        Log.e("文件类型", "type" + type);
        if (!type.equals("*/*")) {
            App.getContext().startActivity(showOpenTypeDialog(filesPath, type));
        } else {
            App.getContext().startActivity(showOpenTypeDialog(filesPath, "*/*"));
        }
        return intent;
    }

    public static Intent showOpenTypeDialog(String param, String type) {
        Log.e("ViChildError", "showOpenTypeDialog");
        Intent intent = new Intent();
        if (android.os.Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上]
            final File apkFile = new File(param);
            Uri apkUri =
                    FileProvider.getUriForFile(App.getContext(), "com.mall.view.fileprovider", apkFile);
            Log.e("7.0以上路径:", "路径" + apkUri.getPath());
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
            intent.setDataAndType(apkUri, type);
        } else {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setAction(android.content.Intent.ACTION_VIEW);
            Uri uri = Uri.fromFile(new File(param));
            intent.setDataAndType(uri, type);
        }


        return intent;
    }


    /**
     * --获取文件类型 --
     */
    public static String getMIMEType(String filePath) {
        String type = "*/*";
        String fName = filePath;

        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }

        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") {
            return type;
        }

        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0])) {
                type = MIME_MapTable[i][1];
            }
        }
        return type;
    }

    /**
     * -- MIME 列表 --
     */
    public static final String[][] MIME_MapTable =
            {
                    // --{后缀名， MIME类型}   --
                    {".3gp", "video/3gpp"},
                    {".3gpp", "video/3gpp"},
                    {".aac", "audio/x-mpeg"},
                    {".amr", "audio/x-mpeg"},
                    {".apk", "application/vnd.android.package-archive"},
                    {".avi", "video/x-msvideo"},
                    {".aab", "application/x-authoware-bin"},
                    {".aam", "application/x-authoware-map"},
                    {".aas", "application/x-authoware-seg"},
                    {".ai", "application/postscript"},
                    {".aif", "audio/x-aiff"},
                    {".aifc", "audio/x-aiff"},
                    {".aiff", "audio/x-aiff"},
                    {".als", "audio/x-alpha5"},
                    {".amc", "application/x-mpeg"},
                    {".ani", "application/octet-stream"},
                    {".asc", "text/plain"},
                    {".asd", "application/astound"},
                    {".asf", "video/x-ms-asf"},
                    {".asn", "application/astound"},
                    {".asp", "application/x-asap"},
                    {".asx", " video/x-ms-asf"},
                    {".au", "audio/basic"},
                    {".avb", "application/octet-stream"},
                    {".awb", "audio/amr-wb"},
                    {".bcpio", "application/x-bcpio"},
                    {".bld", "application/bld"},
                    {".bld2", "application/bld2"},
                    {".bpk", "application/octet-stream"},
                    {".bz2", "application/x-bzip2"},
                    {".bin", "application/octet-stream"},
                    {".bmp", "image/bmp"},
                    {".c", "text/plain"},
                    {".class", "application/octet-stream"},
                    {".conf", "text/plain"},
                    {".cpp", "text/plain"},
                    {".cal", "image/x-cals"},
                    {".ccn", "application/x-cnc"},
                    {".cco", "application/x-cocoa"},
                    {".cdf", "application/x-netcdf"},
                    {".cgi", "magnus-internal/cgi"},
                    {".chat", "application/x-chat"},
                    {".clp", "application/x-msclip"},
                    {".cmx", "application/x-cmx"},
                    {".co", "application/x-cult3d-object"},
                    {".cod", "image/cis-cod"},
                    {".cpio", "application/x-cpio"},
                    {".cpt", "application/mac-compactpro"},
                    {".crd", "application/x-mscardfile"},
                    {".csh", "application/x-csh"},
                    {".csm", "chemical/x-csml"},
                    {".csml", "chemical/x-csml"},
                    {".css", "text/css"},
                    {".cur", "application/octet-stream"},
                    {".doc", "application/msword"},
                    {".dcm", "x-lml/x-evm"},
                    {".dcr", "application/x-director"},
                    {".dcx", "image/x-dcx"},
                    {".dhtml", "text/html"},
                    {".dir", "application/x-director"},
                    {".dll", "application/octet-stream"},
                    {".dmg", "application/octet-stream"},
                    {".dms", "application/octet-stream"},
                    {".dot", "application/x-dot"},
                    {".dvi", "application/x-dvi"},
                    {".dwf", "drawing/x-dwf"},
                    {".dwg", "application/x-autocad"},
                    {".dxf", "application/x-autocad"},
                    {".dxr", "application/x-director"},
                    {".ebk", "application/x-expandedbook"},
                    {".emb", "chemical/x-embl-dl-nucleotide"},
                    {".embl", "chemical/x-embl-dl-nucleotide"},
                    {".eps", "application/postscript"},
                    {".epub", "application/epub+zip"},
                    {".eri", "image/x-eri"},
                    {".es", "audio/echospeech"},
                    {".esl", "audio/echospeech"},
                    {".etc", "application/x-earthtime"},
                    {".etx", "text/x-setext"},
                    {".evm", "x-lml/x-evm"},
                    {".evy", "application/x-envoy"},
                    {".exe", "application/octet-stream"},
                    {".fh4", "image/x-freehand"},
                    {".fh5", "image/x-freehand"},
                    {".fhc", "image/x-freehand"},
                    {".fif", "image/fif"},
                    {".fm", "application/x-maker"},
                    {".fpx", "image/x-fpx"},
                    {".fvi", "video/isivideo"},
                    {".flv", "video/x-msvideo"},
                    {".gau", "chemical/x-gaussian-input"},
                    {".gca", "application/x-gca-compressed"},
                    {".gdb", "x-lml/x-gdb"},
                    {".gif", "image/gif"},
                    {".gps", "application/x-gps"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".gif", "image/gif"},
                    {".gtar", "application/x-gtar"},
                    {".gz", "application/x-gzip"},
                    {".h", "text/plain"},
                    {".hdf", "application/x-hdf"},
                    {".hdm", "text/x-hdml"},
                    {".hdml", "text/x-hdml"},
                    {".htm", "text/html"},
                    {".html", "text/html"},
                    {".hlp", "application/winhlp"},
                    {".hqx", "application/mac-binhex40"},
                    {".hts", "text/html"},
                    {".ice", "x-conference/x-cooltalk"},
                    {".ico", "application/octet-stream"},
                    {".ief", "image/ief"},
                    {".ifm", "image/gif"},
                    {".ifs", "image/ifs"},
                    {".imy", "audio/melody"},
                    {".ins", "application/x-net-install"},
                    {".ips", "application/x-ipscript"},
                    {".ipx", "application/x-ipix"},
                    {".it", "audio/x-mod"},
                    {".itz", "audio/x-mod"},
                    {".ivr", "i-world/i-vrml"},
                    {".j2k", "image/j2k"},
                    {".jad", "text/vnd.sun.j2me.app-descriptor"},
                    {".jam", "application/x-jam"},
                    {".jnlp", "application/x-java-jnlp-file"},
                    {".jpe", "image/jpeg"},
                    {".jpz", "image/jpeg"},
                    {".jwc", "application/jwc"},
                    {".jar", "application/java-archive"},
                    {".java", "text/plain"},
                    {".jpeg", "image/jpeg"},
                    {".jpg", "image/jpeg"},
                    {".js", "application/x-javascript"},
                    {".kjx", "application/x-kjx"},
                    {".lak", "x-lml/x-lak"},
                    {".latex", "application/x-latex"},
                    {".lcc", "application/fastman"},
                    {".lcl", "application/x-digitalloca"},
                    {".lcr", "application/x-digitalloca"},
                    {".lgh", "application/lgh"},
                    {".lha", "application/octet-stream"},
                    {".lml", "x-lml/x-lml"},
                    {".lmlpack", "x-lml/x-lmlpack"},
                    {".log", "text/plain"},
                    {".lsf", "video/x-ms-asf"},
                    {".lsx", "video/x-ms-asf"},
                    {".lzh", "application/x-lzh "},
                    {".m13", "application/x-msmediaview"},
                    {".m14", "application/x-msmediaview"},
                    {".m15", "audio/x-mod"},
                    {".m3u", "audio/x-mpegurl"},
                    {".m3url", "audio/x-mpegurl"},
                    {".ma1", "audio/ma1"},
                    {".ma2", "audio/ma2"},
                    {".ma3", "audio/ma3"},
                    {".ma5", "audio/ma5"},
                    {".man", "application/x-troff-man"},
                    {".map", "magnus-internal/imagemap"},
                    {".mbd", "application/mbedlet"},
                    {".mct", "application/x-mascot"},
                    {".mdb", "application/x-msaccess"},
                    {".mdz", "audio/x-mod"},
                    {".me", "application/x-troff-me"},
                    {".mel", "text/x-vmel"},
                    {".mi", "application/x-mif"},
                    {".mid", "audio/midi"},
                    {".midi", "audio/midi"},
                    {".m4a", "audio/mp4a-latm"},
                    {".m4b", "audio/mp4a-latm"},
                    {".m4p", "audio/mp4a-latm"},
                    {".m4u", "video/vnd.mpegurl"},
                    {".m4v", "video/x-m4v"},
                    {".mov", "video/quicktime"},
                    {".mp2", "audio/x-mpeg"},
                    {".mp3", "audio/x-mpeg"},
                    {".mp4", "video/mp4"},
                    {".mpc", "application/vnd.mpohun.certificate"},
                    {".mpe", "video/mpeg"},
                    {".mpeg", "video/mpeg"},
                    {".mpg", "video/mpeg"},
                    {".mpg4", "video/mp4"},
                    {".mpga", "audio/mpeg"},
                    {".msg", "application/vnd.ms-outlook"},
                    {".mif", "application/x-mif"},
                    {".mil", "image/x-cals"},
                    {".mio", "audio/x-mio"},
                    {".mmf", "application/x-skt-lbs"},
                    {".mng", "video/x-mng"},
                    {".mny", "application/x-msmoney"},
                    {".moc", "application/x-mocha"},
                    {".mocha", "application/x-mocha"},
                    {".mod", "audio/x-mod"},
                    {".mof", "application/x-yumekara"},
                    {".mol", "chemical/x-mdl-molfile"},
                    {".mop", "chemical/x-mopac-input"},
                    {".movie", "video/x-sgi-movie"},
                    {".mpn", "application/vnd.mophun.application"},
                    {".mpp", "application/vnd.ms-project"},
                    {".mps", "application/x-mapserver"},
                    {".mrl", "text/x-mrml"},
                    {".mrm", "application/x-mrm"},
                    {".ms", "application/x-troff-ms"},
                    {".mts", "application/metastream"},
                    {".mtx", "application/metastream"},
                    {".mtz", "application/metastream"},
                    {".mzv", "application/metastream"},
                    {".nar", "application/zip"},
                    {".nbmp", "image/nbmp"},
                    {".nc", "application/x-netcdf"},
                    {".ndb", "x-lml/x-ndb"},
                    {".ndwn", "application/ndwn"},
                    {".nif", "application/x-nif"},
                    {".nmz", "application/x-scream"},
                    {".nokia-op-logo", "image/vnd.nok-oplogo-color"},
                    {".npx", "application/x-netfpx"},
                    {".nsnd", "audio/nsnd"},
                    {".nva", "application/x-neva1"},
                    {".oda", "application/oda"},
                    {".oom", "application/x-atlasMate-plugin"},
                    {".ogg", "audio/ogg"},
                    {".pac", "audio/x-pac"},
                    {".pae", "audio/x-epac"},
                    {".pan", "application/x-pan"},
                    {".pbm", "image/x-portable-bitmap"},
                    {".pcx", "image/x-pcx"},
                    {".pda", "image/x-pda"},
                    {".pdb", "chemical/x-pdb"},
                    {".pdf", "application/pdf"},
                    {".pfr", "application/font-tdpfr"},
                    {".pgm", "image/x-portable-graymap"},
                    {".pict", "image/x-pict"},
                    {".pm", "application/x-perl"},
                    {".pmd", "application/x-pmd"},
                    {".png", "image/png"},
                    {".pnm", "image/x-portable-anymap"},
                    {".pnz", "image/png"},
                    {".pot", "application/vnd.ms-powerpoint"},
                    {".ppm", "image/x-portable-pixmap"},
                    {".pps", "application/vnd.ms-powerpoint"},
                    {".ppt", "application/vnd.ms-powerpoint"},
                    {".pqf", "application/x-cprplayer"},
                    {".pqi", "application/cprplayer"},
                    {".prc", "application/x-prc"},
                    {".proxy", "application/x-ns-proxy-autoconfig"},
                    {".prop", "text/plain"},
                    {".ps", "application/postscript"},
                    {".ptlk", "application/listenup"},
                    {".pub", "application/x-mspublisher"},
                    {".pvx", "video/x-pv-pvx"},
                    {".qcp", "audio/vnd.qcelp"},
                    {".qt", "video/quicktime"},
                    {".qti", "image/x-quicktime"},
                    {".qtif", "image/x-quicktime"},
                    {".r3t", "text/vnd.rn-realtext3d"},
                    {".ra", "audio/x-pn-realaudio"},
                    {".ram", "audio/x-pn-realaudio"},
                    {".ras", "image/x-cmu-raster"},
                    {".rdf", "application/rdf+xml"},
                    {".rf", "image/vnd.rn-realflash"},
                    {".rgb", "image/x-rgb"},
                    {".rlf", "application/x-richlink"},
                    {".rm", "audio/x-pn-realaudio"},
                    {".rmf", "audio/x-rmf"},
                    {".rmm", "audio/x-pn-realaudio"},
                    {".rnx", "application/vnd.rn-realplayer"},
                    {".roff", "application/x-troff"},
                    {".rp", "image/vnd.rn-realpix"},
                    {".rpm", "audio/x-pn-realaudio-plugin"},
                    {".rt", "text/vnd.rn-realtext"},
                    {".rte", "x-lml/x-gps"},
                    {".rtf", "application/rtf"},
                    {".rtg", "application/metastream"},
                    {".rtx", "text/richtext"},
                    {".rv", "video/vnd.rn-realvideo"},
                    {".rwc", "application/x-rogerwilco"},
                    {".rar", "application/x-rar-compressed"},
                    {".rc", "text/plain"},
                    {".rmvb", "audio/x-pn-realaudio"},
                    {".s3m", "audio/x-mod"},
                    {".s3z", "audio/x-mod"},
                    {".sca", "application/x-supercard"},
                    {".scd", "application/x-msschedule"},
                    {".sdf", "application/e-score"},
                    {".sea", "application/x-stuffit"},
                    {".sgm", "text/x-sgml"},
                    {".sgml", "text/x-sgml"},
                    {".shar", "application/x-shar"},
                    {".shtml", "magnus-internal/parsed-html"},
                    {".shw", "application/presentations"},
                    {".si6", "image/si6"},
                    {".si7", "image/vnd.stiwap.sis"},
                    {".si9", "image/vnd.lgtwap.sis"},
                    {".sis", "application/vnd.symbian.install"},
                    {".sit", "application/x-stuffit"},
                    {".skd", "application/x-koan"},
                    {".skm", "application/x-koan"},
                    {".skp", "application/x-koan"},
                    {".skt", "application/x-koan"},
                    {".slc", "application/x-salsa"},
                    {".smd", "audio/x-smd"},
                    {".smi", "application/smil"},
                    {".smil", "application/smil"},
                    {".smp", "application/studiom"},
                    {".smz", "audio/x-smd"},
                    {".sh", "application/x-sh"},
                    {".snd", "audio/basic"},
                    {".spc", "text/x-speech"},
                    {".spl", "application/futuresplash"},
                    {".spr", "application/x-sprite"},
                    {".sprite", "application/x-sprite"},
                    {".sdp", "application/sdp"},
                    {".spt", "application/x-spt"},
                    {".src", "application/x-wais-source"},
                    {".stk", "application/hyperstudio"},
                    {".stm", "audio/x-mod"},
                    {".sv4cpio", "application/x-sv4cpio"},
                    {".sv4crc", "application/x-sv4crc"},
                    {".svf", "image/vnd"},
                    {".svg", "image/svg-xml"},
                    {".svh", "image/svh"},
                    {".svr", "x-world/x-svr"},
                    {".swf", "application/x-shockwave-flash"},
                    {".swfl", "application/x-shockwave-flash"},
                    {".t", "application/x-troff"},
                    {".tad", "application/octet-stream"},
                    {".talk", "text/x-speech"},
                    {".tar", "application/x-tar"},
                    {".taz", "application/x-tar"},
                    {".tbp", "application/x-timbuktu"},
                    {".tbt", "application/x-timbuktu"},
                    {".tcl", "application/x-tcl"},
                    {".tex", "application/x-tex"},
                    {".texi", "application/x-texinfo"},
                    {".texinfo", "application/x-texinfo"},
                    {".tgz", "application/x-tar"},
                    {".thm", "application/vnd.eri.thm"},
                    {".tif", "image/tiff"},
                    {".tiff", "image/tiff"},
                    {".tki", "application/x-tkined"},
                    {".tkined", "application/x-tkined"},
                    {".toc", "application/toc"},
                    {".toy", "image/toy"},
                    {".tr", "application/x-troff"},
                    {".trk", "x-lml/x-gps"},
                    {".trm", "application/x-msterminal"},
                    {".tsi", "audio/tsplayer"},
                    {".tsp", "application/dsptype"},
                    {".tsv", "text/tab-separated-values"},
                    {".ttf", "application/octet-stream"},
                    {".ttz", "application/t-time"},
                    {".txt", "text/plain"},
                    {".ult", "audio/x-mod"},
                    {".ustar", "application/x-ustar"},
                    {".uu", "application/x-uuencode"},
                    {".uue", "application/x-uuencode"},
                    {".vcd", "application/x-cdlink"},
                    {".vcf", "text/x-vcard"},
                    {".vdo", "video/vdo"},
                    {".vib", "audio/vib"},
                    {".viv", "video/vivo"},
                    {".vivo", "video/vivo"},
                    {".vmd", "application/vocaltec-media-desc"},
                    {".vmf", "application/vocaltec-media-file"},
                    {".vmi", "application/x-dreamcast-vms-info"},
                    {".vms", "application/x-dreamcast-vms"},
                    {".vox", "audio/voxware"},
                    {".vqe", "audio/x-twinvq-plugin"},
                    {".vqf", "audio/x-twinvq"},
                    {".vql", "audio/x-twinvq"},
                    {".vre", "x-world/x-vream"},
                    {".vrml", "x-world/x-vrml"},
                    {".vrt", "x-world/x-vrt"},
                    {".vrw", "x-world/x-vream"},
                    {".vts", "workbook/formulaone"},
                    {".wax", "audio/x-ms-wax"},
                    {".wbmp", "image/vnd.wap.wbmp"},
                    {".web", "application/vnd.xara"},
                    {".wav", "audio/x-wav"},
                    {".wma", "audio/x-ms-wma"},
                    {".wmv", "audio/x-ms-wmv"},
                    {".wi", "image/wavelet"},
                    {".wis", "application/x-InstallShield"},
                    {".wm", "video/x-ms-wm"},
                    {".wmd", "application/x-ms-wmd"},
                    {".wmf", "application/x-msmetafile"},
                    {".wml", "text/vnd.wap.wml"},
                    {".wmlc", "application/vnd.wap.wmlc"},
                    {".wmls", "text/vnd.wap.wmlscript"},
                    {".wmlsc", "application/vnd.wap.wmlscriptc"},
                    {".wmlscript", "text/vnd.wap.wmlscript"},
                    {".wmv", "video/x-ms-wmv"},
                    {".wmx", "video/x-ms-wmx"},
                    {".wmz", "application/x-ms-wmz"},
                    {".wpng", "image/x-up-wpng"},
                    {".wps", "application/vnd.ms-works"},
                    {".wpt", "x-lml/x-gps"},
                    {".wri", "application/x-mswrite"},
                    {".wrl", "x-world/x-vrml"},
                    {".wrz", "x-world/x-vrml"},
                    {".ws", "text/vnd.wap.wmlscript"},
                    {".wsc", "application/vnd.wap.wmlscriptc"},
                    {".wv", "video/wavelet"},
                    {".wvx", "video/x-ms-wvx"},
                    {".wxl", "application/x-wxl"},
                    {".x-gzip", "application/x-gzip"},
                    {".xar", "application/vnd.xara"},
                    {".xbm", "image/x-xbitmap"},
                    {".xdm", "application/x-xdma"},
                    {".xdma", "application/x-xdma"},
                    {".xdw", "application/vnd.fujixerox.docuworks"},
                    {".xht", "application/xhtml+xml"},
                    {".xhtm", "application/xhtml+xml"},
                    {".xhtml", "application/xhtml+xml"},
                    {".xla", "application/vnd.ms-excel"},
                    {".xlc", "application/vnd.ms-excel"},
                    {".xll", "application/x-excel"},
                    {".xlm", "application/vnd.ms-excel"},
                    {".xls", "application/vnd.ms-excel"},
                    {".xlt", "application/vnd.ms-excel"},
                    {".xlw", "application/vnd.ms-excel"},
                    {".xm", "audio/x-mod"},
                    {".xml", "text/xml"},
                    {".xmz", "audio/x-mod"},
                    {".xpi", "application/x-xpinstall"},
                    {".xpm", "image/x-xpixmap"},
                    {".xsit", "text/xml"},
                    {".xsl", "text/xml"},
                    {".xul", "text/xul"},
                    {".xwd", "image/x-xwindowdump"},
                    {".xyz", "chemical/x-pdb"},
                    {".yz1", "application/x-yz1"},
                    {".z", "application/x-compress"},
                    {".zac", "application/x-zaurus-zac"},
                    {".zip", "application/zip"},
                    {"", "*/*"}
            };

    public static String showMM(double lat, double lng) {

        LatLng remote = new LatLng(lat, lng);
        LocationModel locationModel = LocationModel.getLocationModel();
        if (!Util.isNull(locationModel.getCity())) {
            float distanceM = AMapUtils.calculateLineDistance(
                    new LatLng(locationModel.getLatitude(), locationModel.getLongitude())
                    , remote);
            return getMM(distanceM);
        }

        return "";

    }

    public static String getMM(float distanceM) {
        String distanceValue = distanceM + "";
        if (distanceM > 1000F) {
            distanceValue = Util.getDouble(Double.valueOf((distanceM / 1000F) + ""), 3) + "公里";
        } else {
            distanceValue = Util.getDouble(Double.valueOf(distanceM + ""), 3) + "米";
        }
        distanceValue = distanceValue.replaceFirst("\\.00", "");
        return distanceValue;
    }


    public static void setLinkClickIntercept(TextView tv, Context context) {
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable sp = (Spannable) tv.getText();
            URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
            SpannableStringBuilder style = new SpannableStringBuilder(text);
            style.clearSpans();// should clear old spans
            for (URLSpan url : urls) {
                if (-1 != url.getURL().indexOf("shopCollects/shopCollectsPage.aspx?cid=")) {
                    MyURLSpan myURLSpan = new MyURLSpan(url.getURL(), context);
                    style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url),
                            Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                }
            }
            tv.setText(style);
        }
    }

    /**
     * 处理TextView中的链接点击事件 链接的类型包括：url，号码，email，地图 这里只拦截url，即 http:// 开头的URI
     */
    private static class MyURLSpan extends ClickableSpan {
        private String mUrl; // 当前点击的实际链接
        private Context mcontext;

        MyURLSpan(String url, Context context) {
            mUrl = url;
            mcontext = context;
        }

        @Override
        public void onClick(View widget) {
            int index = mUrl.indexOf("?");
            if (-1 != index) {
                String urlParameter = mUrl.substring(index + 1);
                String[] args = urlParameter.split("&");
                String id = "";
                String x = "";
                String y = "";
                for (String arg : args) {
                    if (arg.startsWith("cid=")) {
                        id = arg.split("=")[1];
                    }
                    if (arg.startsWith("x=")) {
                        x = arg.split("=")[1];
                    }
                    if (arg.startsWith("y=")) {
                        y = arg.split("=")[1];
                    }

                }
                final String _id = id;
                final String lat_x = x;
                final String lng_y = y;
                if (Util.isInt(id) && !Util.isNull(x) && !Util.isNull(y)) {
                    widget.setBackgroundColor(Color.parseColor("#00000000"));
                    Util.asynTask(mcontext, "正在为您跳转...", new IAsynTask() {

                        @Override
                        public void updateUI(Serializable runData) {
                            Util.showIntent(mcontext, LMSJDetailFrame.class,
                                    new String[]{"id", "name", "x", "y"},
                                    new String[]{_id, "商家详情", lat_x, lng_y});
                        }

                        @Override
                        public Serializable run() {
                            return null;
                        }
                    });
                } else {
                    Util.openWeb(mcontext, mUrl);
                }
            }
        }
    }

    public static boolean checkUserInfocomplete() {
        if (UserData.getUser().getUserId().equals("远大云商008")) {
            //如果是远大云商008 不用检查
            return true;
        }

        if (UserData.getUser().getUserLevel().equals("城市总裁") && UserData.getUser().getUserLevel().equals("城市CEO")) {
            //如果是城市总裁 / 城市CEO 不用检查
            return true;

        }
        if ("0".equals(UserData.getUser().getZoneId())) {
            //信息未完善
            return false;
        }
        return true;
    }

    public static String protectionUserName(String userid) {


        String name = "";

        String phonenumber = "";
        try {
            if (isNull(userid)) {

                return name;
            }


            if (isPhone(userid)) {

                name = userid.replace("_p", "").replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
                return name;
            }

            if (userid.contains("_p")) {
                try {
                    phonenumber = userid.split("_p")[0];
                } catch (Exception e) {

                }
                if (isPhone(phonenumber)) {
                    name = userid.replace("_p", "").replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");

                } else {
                    if (userid.length() > 2) {
                        name = userid.substring(0, userid.length() - 2) + "**";
                    } else if (userid.length() == 2) {
                        name = userid.substring(0, userid.length() - 1) + "*";
                    } else if (userid.length() == 1) {
                        name = userid + "*";
                    }

                }

            } else if (checkEmail(userid)) {
                name = userid.replaceAll("(\\w?)(\\w+)(\\w)(@\\w+\\.[a-z]+(\\.[a-z]+)?)", "$1****$3$4");
            } else {
                if (userid.length() > 2) {
                    name = userid.substring(0, userid.length() - 2) + "**";
                } else if (userid.length() == 2) {
                    name = userid.substring(0, userid.length() - 1) + "*";
                } else if (userid.length() == 1) {
                    name = userid + "*";
                }
            }
            return name;
        } catch (Exception e) {
            name = "";
        }

        return name;
    }

    /**
     * 验证邮箱
     *
     * @param email
     * @return
     */
    public static boolean checkEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }


}
