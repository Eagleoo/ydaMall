package com.mall.view.messageboard;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.BusinessMessage;
import com.mall.model.LocationModel;
import com.mall.model.messageboard.UserMessageBoardCache;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.AppraiseResultsActivity;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.ShowPopWindow;
import com.mall.view.messageboard.ViewPager.OnPageChangeListener;
import com.mall.view.service.UploadService;
import com.mall.view.service.UploadService.UploadBinder;
import com.mall.yyrg.YYRGSelectBaskImage;
import com.mall.yyrg.adapter.BitmapUtil;
import com.mall.yyrg.adapter.MyGridView;
import com.mall.yyrg.adapter.SelcctPhotoAdapter;
import com.mall.yyrg.adapter.YYRGUtil;
import com.mall.yyrg.model.PhotoInfo;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.reactivex.functions.Consumer;

import static com.yalantis.ucrop.util.FileUtils.getPath;
//import com.picture.reduce.enlarge.viewpager.GestureDetector;
//import com.picture.reduce.enlarge.viewpager.ImageViewTouch;

//import com.picture.reduce.enlarge.viewpager.ScaleGestureDetector;

/**
 * 功能： 发布动态<br>
 * 时间： 2014-8-18<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class WriterNewMessageBoardFrame extends Activity {
    @ViewInject(R.id.xq_content)
    private EditText xq_content;
    @ViewInject(R.id.btn_send)
    private Button btn_send;
    @ViewInject(R.id.writer_message_show_address_chk)
    private CheckBox show_address;
    @ViewInject(R.id.writer_message_show_address)
    private TextView address;

    @ViewInject(R.id.top_back)
    private TextView top_back;

    @ViewInject(R.id.texttop_back)
    private TextView texttop_back;

    @ViewInject(R.id.merchant_iv)
    private ImageView merchant_iv;

    @ViewInject(R.id.location_line)
    private View location_line;

    @ViewInject(R.id.view1)
    private View view1;

    @ViewInject(R.id.view2)
    private View view2;

    @ViewInject(R.id.lmsj_comment_user_rating)
    private RatingBar ratingBar;

    private boolean isShowAddress = true;
    private UploadService uploadService;
    private MyGridView add_bask_image;
    private SelcctPhotoAdapter photoAdapter;
    private List<PhotoInfo> allPhotoInfos = new ArrayList<PhotoInfo>();
    private int width;
    private PopupWindow distancePopup = null;
    private int uploadState;
    private Uri photoUri;
    private TextView show_image_count;
    private Dialog dialog;
    private ViewPager mViewPager;
    private int pagerMarginPixels = 0;
    private ImagePagerAdapter mPagerAdapter;
    private GestureDetector mGestureDetector;
    private List<ImageView> arrImages;
    private String nowView = "2";
    private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
    private int currentPic = 0;
    private boolean mPaused;
    private boolean mOnScale = false;
    private boolean mOnPagerScoll = false;
    private boolean mControlsShow = false;
    private ScaleGestureDetector mScaleGestureDetector;
    private Handler handler;
    public Map<Integer, ImageViewTouch> viewss = new HashMap<Integer, ImageViewTouch>();
    private BitmapUtils bmUtil;
    @ViewInject(R.id.top_view)
    private LinearLayout top_view;
    private String picPath;
    // 所有要上传的图片
    private List<Bitmap> allUpImages = new ArrayList<Bitmap>();
    private List<String> path_absolutes = new ArrayList<String>();
    private List<String> uppath_absolutes = new ArrayList<String>();
    private SharedPreferences sp;
    private UserMessageBoardCache umbcache;
    private DbUtils db;

    private String ShopType = "";

    private String zoneid = "";

    public ServiceConnection uploadImageServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //如果启动服务失败，则不使用后台上传
            upload("");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            UploadService.UploadBinder uploadBinder = (UploadBinder) service;
            uploadService = uploadBinder.getService();
            String s = xq_content.getText().toString().replaceAll("&", "＆").replaceAll("=", "＝").replaceAll("'", "‘");
            final float score = ratingBar.getRating();
            uploadService.setContent(s);
            uploadService.setSecore(score);
            uploadService.setlid(lid);

            uploadService.setContext(WriterNewMessageBoardFrame.this);
            uploadService.setId(umbcache.getId());
            Log.e("上传KKKKKK", s);
            Log.e("Type", Type + "KKKKK");
            Util.MyToast(App.getContext(), "正在发布您的动态...", 1);
            if (Type.equals("1")) {
                Log.e("文件问题", "1");
//				uploadService.setBitmapStr(allUpImages,Type);
                uppath_absolutes.clear();
                for (int i = 0; i < allPhotoInfos.size(); i++) {
                    Log.e("图片地址", "绝对路径：" + allPhotoInfos.get(i).getPath_absolute());
                    if (!Util.isNull(allPhotoInfos.get(i).getPath_absolute())) {
                        uppath_absolutes.add(allPhotoInfos.get(i).getPath_absolute().replace("file://", ""));
                    }
                }
                String[] toBeStored = uppath_absolutes.toArray(new String[uppath_absolutes.size()]);
                uploadService.uploadImageFromLocalFiles(toBeStored, Type, zoneid);
            } else {
                Log.e("文件问题", "2");
//				uploadService.setBitmaps(allUpImages);
                uppath_absolutes.clear();
                for (int i = 0; i < allPhotoInfos.size(); i++) {
                    Log.e("图片地址", "绝对路径：" + allPhotoInfos.get(i).getPath_absolute());
                    if (!Util.isNull(allPhotoInfos.get(i).getPath_absolute())) {
                        uppath_absolutes.add(allPhotoInfos.get(i).getPath_absolute().replace("file://", ""));
                    }
                }
                String[] toBeStored = uppath_absolutes.toArray(new String[uppath_absolutes.size()]);
                uploadService.uploadImageFromLocalFiles(toBeStored, Type, zoneid);
                MyToast.makeText(App.getActivity(), "动态发布中", 5)
                        .show();

            }


        }
    };

    private Context context;
    private String Type = "";

    private String lid = "";
    private String name = "";
    @ViewInject(R.id.shopname)
    private TextView shopname;

    private String keyop = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_message_board);
        context = this;
        ViewUtils.inject(this);
        top_back.setVisibility(View.GONE);
        texttop_back.setVisibility(View.VISIBLE);


        Intent intent = getIntent();
        String id1 = intent.getStringExtra("zoneid");
        if (!Util.isNull(id1)) {
            zoneid = id1;
            Util.kfg = "1";
        }


        if (intent.getStringExtra("Type") != null && intent.getStringExtra("Type").equals("1")) {
            keyop = "11";
            Type = intent.getStringExtra("Type");
            ShopType = intent.getStringExtra("ShopType");
            String face = intent.getStringExtra("face");
            lid = intent.getStringExtra("lmsj");
            name = intent.getStringExtra("name");
            shopname.setText(name);
            Log.e("地址", "图片地址" + face);


            Picasso.with(context).load(face).into(merchant_iv);
            location_line.setVisibility(View.GONE);
        } else {
            view1.setVisibility(View.GONE);
            view2.setVisibility(View.GONE);
        }

        db = DbUtils.create(this, "xqcache");
        umbcache = new UserMessageBoardCache();//
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        List<Object> list = (List<Object>) msg.obj;
                        if (list != null) {
                            if (list.size() > 0) {
                                Bitmap bbmm = null;
                                if (3 == list.size() && "setImage".equals(list.get(2) + ""))
                                    bbmm = Util.zoomBitmap((Bitmap) list.get(0), 480, 640);
//									bbmm = BitmapFactory.decodeFile(list.get(0)+"");//filePath
                                else
                                    bbmm = Util.getLocationThmub(list.get(1) + "", 480, 640);
//									bbmm = BitmapFactory.decodeFile(list.get(1)+"");//filePath
                                System.gc();
                                allUpImages.add(bbmm);
                                Bundle bundle = msg.getData();
                                path_absolutes.add(bundle.getString("fileName"));
                                if (allUpImages.size() == allPhotoInfos.size() - 1) {
                                    umbcache.setUserId(UserData.getUser().getUserIdNoEncodByUTF8());
                                    Date date = new Date(System.currentTimeMillis());
                                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                    umbcache.setCreateTime(format.format(date).toString());
                                    saveBitmaps(allUpImages, allPhotoInfos);
                                }
                            }
                        }
                        break;
                }
            }
        };
        bmUtil = new BitmapUtils(this);
        bmUtil.configDefaultLoadFailedImage(R.drawable.new_yda__top_zanwu);
        add_bask_image = (MyGridView) findViewById(R.id.add_bask_image);
        //绑定服务
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        Bitmap bitmap = getBitmapFromResources(this, R.drawable.tianjaishizhi);
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.setBitmap(bitmap);
        allPhotoInfos.add(photoInfo);
        if (photoAdapter == null) {
            photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, add_bask_image, width * 2 / 9);
        }
        add_bask_image.setAdapter(photoAdapter);
    }


    @OnClick({R.id.texttop_back})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.texttop_back:
                finish();
                break;
        }
    }

    private void saveBitmaps(final List<Bitmap> images, final List<PhotoInfo> photoList) {
        Util.asynTask(WriterNewMessageBoardFrame.this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                try {
                    umbcache.setId(System.currentTimeMillis());
                    umbcache.setFlag("no");
                    umbcache.setUserFace(UserData.getUser().getUserFace());
                    db.saveBindingId(umbcache);
                } catch (DbException e) {
                    e.printStackTrace();
                }

                if (Type.equals("1")) {
                    WriterNewMessageBoardFrame.this.getApplicationContext().bindService(new Intent(WriterNewMessageBoardFrame.this, UploadService.class), uploadImageServiceConnection, Context.BIND_AUTO_CREATE);
                    Intent intent = new Intent(WriterNewMessageBoardFrame.this, AppraiseResultsActivity.class);
                    intent.putExtra("from", "writemessage");
                    intent.putExtra("shopType", ShopType);
                    WriterNewMessageBoardFrame.this.startActivity(intent);
                    WriterNewMessageBoardFrame.this.finish();
                } else {
                    WriterNewMessageBoardFrame.this.getApplicationContext().bindService(new Intent(WriterNewMessageBoardFrame.this, UploadService.class), uploadImageServiceConnection, Context.BIND_AUTO_CREATE);
//					Intent intent=new Intent(WriterNewMessageBoardFrame.this,MessageBoardFrame.class);
//					Intent intent=new Intent();
//					intent.putExtra("from", "writemessage");
//					WriterNewMessageBoardFrame.this.startActivity(intent);
                    WriterNewMessageBoardFrame.this.finish();
                }

            }

            @Override
            public Serializable run() {
                LogUtils.e("photoList.size()=" + photoList.size());
                String imgfile = Util.saveBitmap(images, photoList);
                umbcache.setCacheImgFiles(imgfile);//保存图片在
                return "";
            }
        });
    }

    @Override
    protected void onStart() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FaceConversionUtil.getInstace().getFileText(getApplication());
            }
        }).start();
        if (UserData.getUser() == null) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            Util.showIntent(WriterNewMessageBoardFrame.this, LoginFrame.class, new String[]{"finish"}, new String[]{"finish"});
        } else {
            init();
        }
        super.onStart();
    }

    @OnItemClick(R.id.add_bask_image)
    public void onItemClick(AdapterView<?> parent, final View view, final int position,
                            long id) {

        final int[] num = {0, 0, 0};
        final String[] requestPermissionstr = {
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
        };
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.requestEach(requestPermissionstr)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            num[0]++;
                            if (num[0] == requestPermissionstr.length) {

                                if (position == 0) {
                                    if (allPhotoInfos.size() < 10) {
                                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                                .hideSoftInputFromWindow(getCurrentFocus()
                                                                .getWindowToken(),

                                                        InputMethodManager.HIDE_NOT_ALWAYS);
                                        getPopupWindow();
                                        startPopupWindow();
                                        distancePopup.showAsDropDown(view);
                                    } else {
                                        Toast.makeText(WriterNewMessageBoardFrame.this, "最多发布9张动态图片", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    getPopupWindow();
                                    startPopupWindows(position);
                                    distancePopup.showAsDropDown(top_view);
                                }

                            }
                        }
                    }
                });


    }

    @OnClick(R.id.writer_message_show_address_chk)
    public void check(View view) {
        isShowAddress = show_address.isChecked();
    }


    private void init() {

        String title = "动态";
        if (keyop.equals("11")) {
            title = "评价";
        }

        final String finalTitle = title;
        Util.initTop2(this, title, "发布", new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                WriterNewMessageBoardFrame.this.finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                arg0.setEnabled(false);
                if (Util.isNull(xq_content.getText().toString())) {
                    Toast.makeText(WriterNewMessageBoardFrame.this, "请输入" + finalTitle + "内容", Toast.LENGTH_LONG).show();
                    arg0.setEnabled(true);
                    return;
                }
                umbcache.setContent(xq_content.getText().toString().replaceAll("&", "＆").replaceAll("=", "＝").replaceAll("'", "‘"));
                arg0.setEnabled(true);
                if (allPhotoInfos.size() > 1) {
                    for (int i = 1; i < allPhotoInfos.size(); i++) {
                        Bitmap bitmap = null;
                        if (allPhotoInfos.get(i).getBitmap() != null) {
                            bitmap = allPhotoInfos.get(i).getBitmap();
                            List<Object> list = new ArrayList<Object>();
                            list.add(bitmap);
                            allPhotoInfos.get(i).setPath_file(allPhotoInfos.get(i).getPath_absolute());
                            LogUtils.e("allPhotoInfos.get(i).getPath_file()________________________" + allPhotoInfos.get(i).getPath_file() + "");
                            list.add(allPhotoInfos.get(i).getPath_file() + "");
                            Message msg = new Message();
                            msg.what = 1;
                            msg.obj = list;
                            Bundle bundle = new Bundle();
                            msg.setData(bundle);
                            handler.sendMessage(msg);
                        } else {
                            ImageView logo = new ImageView(WriterNewMessageBoardFrame.this);
                            allPhotoInfos.get(i).setPath_absolute(allPhotoInfos.get(i).getPath_file());
                            setImage(logo, allPhotoInfos.get(i).getPath_file(), i);
                        }
                    }
                } else {
                    upContent("");
                }
            }
        });
        initView();

    }

    private void initView() {

        if (keyop.equals("11")) {
            xq_content.setHint("感谢您的评价");
        }

        btn_send.setVisibility(View.GONE);
        xq_content.getLayoutParams().height = Util.dpToPx(this, 120);
        xq_content.getLayoutParams().width = LayoutParams.FILL_PARENT;
//		xq_content.setBackgroundResource(R.drawable.layout_width_no_corner);
        xq_content.setGravity(Gravity.TOP);

        xq_content.setSingleLine(false);
        xq_content.setMaxLines(10);
        xq_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1,
                                          int arg2, int arg3) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int lines = xq_content.getLineCount();
                // 限制最大输入行数
                if (lines > 10) {
                    String str = s.toString();
                    int cursorStart = xq_content.getSelectionStart();
                    int cursorEnd = xq_content.getSelectionEnd();
                    if (cursorStart == cursorEnd && cursorStart < str.length()
                            && cursorStart >= 1) {
                        str = str.substring(0, cursorStart - 1)
                                + str.substring(cursorStart);
                    } else {
                        str = str.substring(0, s.length() - 1);
                    }
                    // setText会触发afterTextChanged的递归
                    xq_content.setText(str);
                    // setSelection用的索引不能使用str.length()否则会越界
                    xq_content.setSelection(xq_content.getText().length());
                    if (Util.isNull(str)) {
                        Util.show("您复制的内容超过最大限制！", WriterNewMessageBoardFrame.this);
                    }
                }
            }
        });
        try {
            LocationModel locationModel = LocationModel.getLocationModel();
            if (!Util.isNull(locationModel.getCity())) {
                address.setText(locationModel.getProvince() + locationModel.getCity());
                umbcache.setCity(locationModel.getProvince() + locationModel.getCity());
            }
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }
    }


    private void upContent(final String filespath) {
        final float score = ratingBar.getRating();
        LogUtils.e(xq_content.getText().toString());
        final CustomProgressDialog dialog = Util.showProgress("正在发布您的动态...", this);
        dialog.setCancelable(false);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                dialog.cancel();
                dialog.dismiss();
                if (Util.isNull(runData)) {
                    Util.show("网络错误，请重试！", WriterNewMessageBoardFrame.this);
                    return;
                }
                if ((runData + "").contains("success")) {
                    if (Type.equals("1")) {
                        Util.show("评论发布成功", WriterNewMessageBoardFrame.this);
                    } else {

                        Util.show("动态发布成功", WriterNewMessageBoardFrame.this);
                        EventBus.getDefault().post(new BusinessMessage("Yes"));
                    }
                    if (Type.equals("1")) {
                        Intent intent = new Intent(WriterNewMessageBoardFrame.this, AppraiseResultsActivity.class);
                        intent.putExtra("from", "writemessage");
                        intent.putExtra("shopType", ShopType);
                        WriterNewMessageBoardFrame.this.startActivity(intent);
                        WriterNewMessageBoardFrame.this.finish();
                    } else {
                        WriterNewMessageBoardFrame.this.finish();
                    }
                } else {
                    Util.show(runData + "", WriterNewMessageBoardFrame.this);
                }
            }

            @Override
            public Serializable run() {
                String city = "";
                String lat = "";
                String lon = "";
                LocationModel locationModel = LocationModel.getLocationModel();
                if (isShowAddress) {
                    String province = locationModel.getProvince();
                    String ci = locationModel.getCity();
                    if (!Util.isNull(province) && !Util.isNull(ci))
                        city = Util.get(province + ci);
                }
                if (!Util.isNull(locationModel.getLatitude())) {
                    lat = locationModel.getLatitude() + "";
                    lon = locationModel.getLongitude() + "";
                }


                Web web = null;

                Log.e("Type", Type + "KL");

                if (Type.equals("1")) {

                    web = new Web(Web.addLMSJComment, "userid="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&lmsj=" + lid + "&message="
                            + Util.get(xq_content.getText().toString()) + "&rating="
                            + score + "&parentID=-1" + "&files=" + filespath);

                } else {
                    web = new Web(Web.addUserMessageBoard, "userId="
                            + UserData.getUser().getUserId() + "&message="
                            + xq_content.getText().toString().replaceAll("&", "＆").replaceAll("=", "＝").replaceAll("'", "‘") + "&files="
                            + filespath + "&forward=-1" + "&userPaw="
                            + UserData.getUser().getMd5Pwd() + "&city=" + city + "&lat=" + lat + "&lon=" + lon + "&zoneid=" + zoneid);
                }


                return web.getPlan();
            }
        });
    }

    @SuppressLint("NewApi")
    private void upload(final String id) {
        if (xq_content.getText().toString().trim().length() == 0) {
            Util.show("请填写您的动态状态", this);
            return;
        }
        if (!isHasImage()) {
            upContent("");
            return;
        }
        final CustomProgressDialog dialog = CustomProgressDialog.createDialog(this);
        dialog.setMessage(dialog, "动态发布中...");
        dialog.setCancelable(false);
        dialog.show();
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if ((runData + "").contains("success:")) {
                    Toast.makeText(WriterNewMessageBoardFrame.this, "图片上传成功", Toast.LENGTH_SHORT).show();
                    upContent(result.replace("success:", ""));
                } else {
                    upContent("");
                }
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public Serializable run() {
                String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
                String NAMESCROPE = "http://mynameislin.cn/";
                String METHOD_NAME = "uploadMoodImage";
                String URL = "http://" + "img.yda360.com" + "/ImageServiceUpLoad.asmx";
                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                List<Bitmap> imgList = new ArrayList<Bitmap>();
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < allUpImages.size(); k++) {
                    Bitmap bm = allUpImages.get(k);
                    if (bm.isRecycled()) {
                        continue;
                    }
                    int bmByteCount = 0;
                    try {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                        bm.compress(compressFormat, 100, out);// 将Bitmap压缩到ByteArrayOutputStream中
                        InputStream sbs = new ByteArrayInputStream(out
                                .toByteArray());
                        byte[] buffer = new byte[30 * 1024];
                        int count = 0;
                        int i = 0;
                        String fileName = "mood_android" + System.currentTimeMillis() + ""
                                + new Random().nextInt(Integer.MAX_VALUE / 2) + ".jpg";
                        bmByteCount = out.size();

                        while ((count = sbs.read(buffer)) >= 0) {
                            String uploadBuffer = new String(Base64.encode(
                                    buffer, 0, count, Base64.DEFAULT));
                            SoapObject request = new SoapObject(NAMESCROPE,
                                    METHOD_NAME);
                            request.addProperty("id", "");
                            Date curDate = new Date(System.currentTimeMillis());
                            request.addProperty("userKey", Util.getUSER_KEY(curDate));
                            request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
                            request.addProperty("image", uploadBuffer);
                            request.addProperty("fileName", fileName);
                            request.addProperty("tag", i);
                            request.addProperty("length", bmByteCount);
                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                    SoapEnvelope.VER11);
                            envelope.bodyOut = request;
                            envelope.dotNet = true;
                            envelope.setOutputSoapObject(request);
                            HttpTransportSE ht = new HttpTransportSE(URL);
                            Log.e("图片地址", URL);
                            ht.debug = true;
                            try {
                                ht.call(SOAP_ACTION, envelope);
                                Object obj = envelope.bodyIn;
                                String s = obj.toString();
                                result = s.substring(s.indexOf("success"),
                                        s.indexOf(";"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            i++;
                        }
                        out.close();
                        sbs.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        bm.recycle();
                    }
                    sb.append(result);
                    sb.append("*|-_-|*");
                }
                for (Bitmap bm : imgList) {
                    if (!bm.isRecycled())
                        bm.recycle();
                }
                imgList.clear();
                return sb.toString();
            }
        });
    }

    private boolean isHasImage() {
        if (allUpImages.size() > 0) {
            return true;
        }
        return false;
    }

    public static Bitmap getBitmapFromResources(Activity act, int resId) {
        Resources res = act.getResources();
        return BitmapFactory.decodeResource(res, resId);
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param
     */
    private void startPopupWindow() {

        View pview = getLayoutInflater().inflate(R.layout.select_pic_layout,
                null, false);
        Button btn_take_photo = (Button) pview
                .findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) pview
                .findViewById(R.id.btn_pick_photo);
        Button btn_cancel = (Button) pview.findViewById(R.id.btn_cancel);
        btn_take_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadState = 1;
                takePhoto();
                distancePopup.dismiss();
            }
        });
        btn_pick_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadState = 2;
                Intent intent = new Intent(WriterNewMessageBoardFrame.this, YYRGSelectBaskImage.class);
                intent.putExtra("maxCount", 10);
                startActivityForResult(intent, 1000);
                distancePopup.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                distancePopup.dismiss();
            }
        });
        initpoputwindow(pview);
        ShowPopWindow.darkenBackground(0.45f, context);

    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        if (distancePopup != null) {
            distancePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    ShowPopWindow.darkenBackground(1f, context);
                }
            });
        }
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    private final int FROM_CAMERA = 0;

    private void takePhoto() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, FROM_CAMERA);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param
     */
    private void startPopupWindows(final int postion) {
        View pview = getLayoutInflater().inflate(
                R.layout.xq_show_select_image, null, false);
        TextView returns = (TextView) pview.findViewById(R.id.top_back);
        TextView delete = (TextView) pview.findViewById(R.id.delete);
        show_image_count = (TextView) pview.findViewById(R.id.show_image_count);
        TextView show_wancheng = (TextView) pview
                .findViewById(R.id.show_wancheng);
        show_wancheng.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoAdapter = new SelcctPhotoAdapter(
                        WriterNewMessageBoardFrame.this, allPhotoInfos,
                        add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);
                distancePopup.dismiss();
            }
        });
        returns.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoAdapter = new SelcctPhotoAdapter(
                        WriterNewMessageBoardFrame.this, allPhotoInfos,
                        add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);
                distancePopup.dismiss();
            }
        });
        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });
        mViewPager = (ViewPager) pview.findViewById(R.id.vp);
        arrImages = new ArrayList<ImageView>();
        DisplayMetrics dm = new DisplayMetrics();
        bitmaps.clear();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        for (int i = 1; i < allPhotoInfos.size(); i++) {
            ImageView imageView = new ImageView(this);
            Bitmap bitmap = null;
            if (allPhotoInfos.get(i).getBitmap() != null) {
                bitmap = allPhotoInfos.get(i).getBitmap();
            } else {
                bitmap = YYRGUtil.compressImageFromFile(allPhotoInfos.get(i).getPath_absolute());
            }
            imageView.setImageBitmap(bitmap);
            imageView.setTag(i + 1);
            arrImages.add(imageView);
            Bitmap image = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            bitmaps.add(image);
        }
        mViewPager.setPageMargin(pagerMarginPixels);
        mPagerAdapter = new ImagePagerAdapter();
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
        mViewPager.setCurrentItem(currentPic);
        setupOnTouchListeners(mViewPager);
        show_image_count.setText("1/" + arrImages.size());
        initpoputwindow(pview);
    }

    private void deleteImage() {
        // 将布局文件转化成view对象
        LayoutInflater inflaterDl = LayoutInflater.from(this);
        LinearLayout layout = (LinearLayout) inflaterDl.inflate(
                R.layout.tuichu_login, null);
        dialog = new AlertDialog.Builder(this).create();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        final int width = dm.widthPixels;
        dialog.show();
        WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
        params.width = width * 4 / 5;
        dialog.getWindow().setAttributes(params);
        dialog.getWindow().setContentView(layout);
        TextView update_number = (TextView) layout
                .findViewById(R.id.update_number);
        update_number.setText("要删除这张图片吗？");
        TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
        no_tuichu.setText("否");
        TextView queding_tuichu = (TextView) layout
                .findViewById(R.id.queding_tuichu);
        queding_tuichu.setText("是");
        no_tuichu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                photoAdapter = new SelcctPhotoAdapter(
                        WriterNewMessageBoardFrame.this, allPhotoInfos,
                        add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);
                mPagerAdapter = new ImagePagerAdapter();
                mViewPager.setAdapter(mPagerAdapter);
                dialog.dismiss();
            }
        });
        queding_tuichu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allPhotoInfos.size() < 1) {
                    dialog.dismiss();
                    distancePopup.dismiss();
                    return;
                }
                int _80dp = Util.dpToPx(WriterNewMessageBoardFrame.this, 80F);
                int temp = Integer.parseInt(nowView) - 1;
                allPhotoInfos.remove(temp);
                bitmaps.clear();
                arrImages = new ArrayList<ImageView>();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                int networkType = Util.getNetworkType(WriterNewMessageBoardFrame.this);
                for (int i = 1; i < allPhotoInfos.size(); i++) {
                    ImageView imageView = new ImageView(
                            WriterNewMessageBoardFrame.this);
                    BitmapFactory.Options opts = new BitmapFactory.Options();
                    opts.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(allPhotoInfos.get(i).getPath_absolute(), opts);
                    Bitmap tagBm = null;
                    if (1 == networkType) {
                        if (opts.outWidth > 480 && opts.outHeight > 800)
                            tagBm = Util.getLocationThmub(picPath, 480, 800);
                        else
                            tagBm = BitmapFactory.decodeFile(picPath);
                    } else if (2 == networkType) {
                        if (opts.outWidth > 240 && opts.outHeight > 400)
                            tagBm = Util.getLocationThmub(picPath, 240, 400);
                        else
                            tagBm = BitmapFactory.decodeFile(picPath);
                    } else if (3 == networkType) {
                        if (opts.outWidth > 320 && opts.outHeight > 480)
                            tagBm = Util.getLocationThmub(picPath, 320, 480);
                        else
                            tagBm = BitmapFactory.decodeFile(picPath);
                    } else {
                        Util.show("请检查您的网络!", WriterNewMessageBoardFrame.this);
                        return;
                    }
                    Bitmap bm = Util.zoomBitmap(tagBm, _80dp, _80dp);
                    imageView.setImageBitmap(bm);
                    imageView.setTag(i + 1);
                    arrImages.add(imageView);
                    bitmaps.add(bm);
                }


                mViewPager.setPageMargin(pagerMarginPixels);
                mPagerAdapter = new ImagePagerAdapter();
                mViewPager.setAdapter(null);
                mViewPager.setAdapter(mPagerAdapter);
                mPagerAdapter.notifyDataSetChanged();
                mViewPager.setOnPageChangeListener(mPageChangeListener);
                mViewPager.setCurrentItem(currentPic);
                setupOnTouchListeners(mViewPager);


                photoAdapter = new SelcctPhotoAdapter(
                        WriterNewMessageBoardFrame.this, allPhotoInfos,
                        add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);

                if (temp != 1) {
                    mViewPager.setCurrentItem(temp);
                    show_image_count.setText(temp + 1 + "/" + arrImages.size());
                } else if (temp + 1 >= arrImages.size()) {
                    show_image_count.setText(arrImages.size() + "/"
                            + arrImages.size());
                } else {
                    show_image_count.setText("1/" + arrImages.size());
                }
                if (arrImages.size() == 0) {
                    show_image_count.setText("0/" + arrImages.size());
                }
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
    }

    private class ImagePagerAdapter extends PagerAdapter {
        public Map<Integer, ImageViewTouch> views = new HashMap<Integer, ImageViewTouch>();

        @Override
        public int getCount() {
            return bitmaps.size();
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ImageViewTouch imageView = new ImageViewTouch(
                    WriterNewMessageBoardFrame.this);
            imageView.setLayoutParams(new LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            imageView.setFocusableInTouchMode(true);
            Bitmap b = bitmaps.get(position);
            imageView.setImageBitmapResetBase(b, true);
            imageView.setTag(position + 1);
            ((ViewPager) container).addView(imageView);
            views.put(position, imageView);
            viewss.put(position, imageView);
            return imageView;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ImageViewTouch imageView = (ImageViewTouch) object;
            imageView.setImageBitmapResetBase(null, true);
            imageView.clear();
            ((ViewPager) container).removeView(imageView);
            views.remove(position);
        }

        @Override
        public void startUpdate(View container) {
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((ImageViewTouch) object);
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }
    }

    private ImageViewTouch getCurrentImageView() {
        if (mPagerAdapter.views != null) {
            if (mPagerAdapter.views.get(mViewPager.getCurrentItem()) != null) {
                return (ImageViewTouch) mPagerAdapter.views.get((mViewPager
                        .getCurrentItem()));
            } else {
                ImageViewTouch img = new ImageViewTouch(
                        WriterNewMessageBoardFrame.this);
                img.setImageBitmap(BitmapFactory.decodeResource(
                        WriterNewMessageBoardFrame.this.getResources(),
                        R.drawable.ic_launcher));
                return img;
            }
        } else {
            ImageViewTouch img = new ImageViewTouch(
                    WriterNewMessageBoardFrame.this);
            img.setImageBitmap(BitmapFactory.decodeResource(
                    WriterNewMessageBoardFrame.this.getResources(),
                    R.drawable.ic_launcher));
            return img;
        }
    }

    OnPageChangeListener mPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageSelected(int position, int prePosition) {
            ImageView imageView = viewss.get(position);
            nowView = "" + imageView.getTag();
            show_image_count.setText(imageView.getTag() + "/"
                    + arrImages.size());
            mViewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            mOnPagerScoll = true;
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                mOnPagerScoll = true;
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                mOnPagerScoll = false;
            } else {
                mOnPagerScoll = false;
            }
        }
    };

    private void setupOnTouchListeners(View rootView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
            mScaleGestureDetector = new ScaleGestureDetector(this,
                    new MyOnScaleGestureListener());
        }
        mGestureDetector = new GestureDetector(this, new MyGestureListener());
        OnTouchListener rootListener = new OnTouchListener() {
            @SuppressLint("NewApi")
            public boolean onTouch(View v, MotionEvent event) {
                if (!mOnScale) {
                    if (!mOnPagerScoll) {
                        mGestureDetector.onTouchEvent(event);
                    }
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
                    if (!mOnPagerScoll) {
                        mScaleGestureDetector.onTouchEvent(event);
                    }
                }

                ImageViewTouch imageView = getCurrentImageView();
                if (imageView == null || null == imageView.mBitmapDisplayed.getBitmap()) {
                    return true;
                }
                if (!mOnScale) {
                    Matrix m = imageView.getImageViewMatrix();
                    RectF rect = new RectF(0, 0, imageView.mBitmapDisplayed
                            .getBitmap().getWidth(), imageView.mBitmapDisplayed
                            .getBitmap().getHeight());
                    m.mapRect(rect);
                    if (!(rect.right > imageView.getWidth() + 0.1 && rect.left < -0.1)) {
                        try {
                            mViewPager.onTouchEvent(event);
                        } catch (ArrayIndexOutOfBoundsException e) {
                        }
                    }
                }
                return true;
            }
        };
        rootView.setOnTouchListener(rootListener);
    }

    private class MyOnScaleGestureListener extends
            ScaleGestureDetector.SimpleOnScaleGestureListener {

        float currentScale;
        float currentMiddleX;
        float currentMiddleY;

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            final ImageViewTouch imageView = getCurrentImageView();
            if (imageView == null) {
                return;
            }
            if (currentScale > imageView.mMaxZoom) {
                imageView
                        .zoomToNoCenterWithAni(currentScale
                                        / imageView.mMaxZoom, 1, currentMiddleX,
                                currentMiddleY);
                currentScale = imageView.mMaxZoom;
                imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
                        currentMiddleY);
            } else if (currentScale < imageView.mMinZoom) {
                imageView.zoomToNoCenterWithAni(currentScale,
                        imageView.mMinZoom, currentMiddleX, currentMiddleY);
                currentScale = imageView.mMinZoom;
                imageView.zoomToNoCenterValue(currentScale, currentMiddleX,
                        currentMiddleY);
            } else {
                imageView.zoomToNoCenter(currentScale, currentMiddleX,
                        currentMiddleY);
            }
            imageView.center(true, true);
            imageView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mOnScale = false;
                }
            }, 300);
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mOnScale = true;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector, float mx, float my) {
            ImageViewTouch imageView = getCurrentImageView();
            if (imageView == null) {
                return true;
            }
            float ns = imageView.getScale() * detector.getScaleFactor();
            currentScale = ns;
            currentMiddleX = mx;
            currentMiddleY = my;
            if (detector.isInProgress()) {
                imageView.zoomToNoCenter(ns, mx, my);
            }
            return true;
        }
    }

    private class MyGestureListener extends
            GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2,
                                float distanceX, float distanceY) {
            // Log.d(TAG, "gesture onScroll");
            if (mOnScale) {
                return true;
            }
            if (mPaused) {
                return false;
            }
            ImageViewTouch imageView = getCurrentImageView();
            if (imageView == null) {
                return true;
            }
            imageView.panBy(-distanceX, -distanceY);
            imageView.center(true, true);

            // 超出边界效果去掉这个
            imageView.center(true, true);

            return true;
        }

        @Override
        public boolean onUp(MotionEvent e) {
            return super.onUp(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mControlsShow) {
            } else {
            }

            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mPaused) {
                return false;
            }
            ImageViewTouch imageView = getCurrentImageView();
            if (imageView == null) {
                return true;
            }
            if (imageView.mBaseZoom < 1) {
                if (imageView.getScale() > 2F) {
                    imageView.zoomTo(1f);
                } else {
                    imageView.zoomToPoint(3f, e.getX(), e.getY());
                }
            } else {
                if (imageView.getScale() > (imageView.mMinZoom + imageView.mMaxZoom) / 2f) {
                    imageView.zoomTo(imageView.mMinZoom);
                } else {
                    imageView.zoomToPoint(imageView.mMaxZoom, e.getX(),
                            e.getY());
                }
            }
            return true;
        }
    }

    private void setImage(final ImageView logo, final String href, final int i) {
        bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
                                        BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {

                // bitmap = arg2;
                List<Object> list = new ArrayList<Object>();
                list.add(arg2);
                list.add(allPhotoInfos.get(i).getPath_file());
                list.add("setImage");
                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                LogUtils.e("setImage");
                handler.sendMessage(msg);
                super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                logo.setImageResource(R.drawable.new_yda__top_zanwu);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("拍照返回1", "requestCode" + requestCode + "uploadState" + uploadState + "photoUri" + photoUri + "resultCode" + resultCode);
        if (requestCode == 1000 && resultCode == 1001) {
            Log.e("拍照返回2", "requestCode" + requestCode + "uploadState" + uploadState);
            if (uploadState == 2) {
                allPhotoInfos.addAll(BitmapUtil.getPhotoInfos);
                Log.e("现在的图片集长度", allPhotoInfos.size() + "sdasd" + allPhotoInfos.get(1).getBitmap());
                photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);
            }
        } else if (photoUri != null && requestCode == FROM_CAMERA) {
            Log.e("拍照返回4", "requestCode" + requestCode + "uploadState" + uploadState);
//			Uri uri = data.getData();
            Log.e("2", "拍照" + photoUri);
            if (resultCode == -1) {
                savePhoto(photoUri);
            }


        } else if (resultCode == Activity.RESULT_OK) {
            Log.e("拍照返回3", "requestCode" + requestCode + "uploadState" + uploadState);
            doPhoto(requestCode, data);
        }
    }

    private void savePhoto(Uri uri) {
        String storageState = Environment.getExternalStorageState();
        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            // picPath = Environment.getExternalStorageDirectory()
            // .getAbsolutePath() + "/lmsj/puduct/pic.jpg";
            Log.e("3", uri + "");
            picPath = getPath(WriterNewMessageBoardFrame.this, uri);
            Log.e("picPath", picPath);
            if (null != picPath) {
                final int _1024dp = Util.dpToPx(this, 800);
                final int _768dp = Util.dpToPx(this, 800);
                Bitmap tagBm = Util.getLocationThmub(picPath, _1024dp, _768dp);
                int r = BitmapUtil.readPictureDegree(picPath);
                if (0 != r) {
                    tagBm = BitmapUtil.rotaingImageView(r, tagBm);
                }
                PhotoInfo photoInfo = new PhotoInfo();
                photoInfo.setBitmap(tagBm);
                photoInfo.setPath_absolute(picPath);
                List<PhotoInfo> list = new ArrayList<PhotoInfo>();
                list.add(photoInfo);
                allPhotoInfos.addAll(list);
                photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, add_bask_image, width * 2 / 9);
                add_bask_image.setAdapter(photoAdapter);
            }
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选择图片后，获取图片的路径
     *
     * @param requestCode
     * @param data
     */

    private void doPhoto(int requestCode, Intent data) {
        String[] pojo = {MediaStore.Images.Media.DATA};
        Cursor cursor = managedQuery(photoUri, pojo, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);
            cursor.moveToFirst();
            picPath = cursor.getString(columnIndex);
            // 在android4.0之后的会自动的关闭cursor 所以要加这个处理
            if (VERSION.SDK_INT < 14) {
                cursor.close();
            }
        }
        if (picPath != null && (picPath.endsWith(".png") || picPath.endsWith(".PNG") || picPath.endsWith(".jpg") || picPath.endsWith(".JPG"))) {
            final int _50dp = Util.dpToPx(this, 50F);
            final int _1024dp = Util.dpToPx(this, 500F);
            final int _768dp = Util.dpToPx(this, 300F);
            Bitmap bm = Util.getLocalBitmap(picPath, _50dp, _50dp);
            Bitmap tagBm = Util.getLocationThmub(picPath, _1024dp, _768dp);
            PhotoInfo photoInfo = new PhotoInfo();
            photoInfo.setBitmap(tagBm);
            List<PhotoInfo> list = new ArrayList<PhotoInfo>();
            list.add(photoInfo);
            allPhotoInfos.addAll(list);
            photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, add_bask_image,
                    width * 2 / 9);
            add_bask_image.setAdapter(photoAdapter);
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
