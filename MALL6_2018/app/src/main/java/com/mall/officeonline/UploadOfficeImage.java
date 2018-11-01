package com.mall.officeonline;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.lidroid.xutils.bitmap.callback.BitmapLoadFrom;
import com.lidroid.xutils.bitmap.callback.DefaultBitmapLoadCallBack;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.AlbumClassifyModel;
import com.mall.net.Web;
import com.mall.officeonline.uploadimage.util.BitmapUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.WriterNewMessageBoardFrame;
import com.mall.yyrg.adapter.MyGridView;
import com.mall.yyrg.adapter.SelcctPhotoAdapter;
import com.mall.yyrg.model.PhotoInfo;

public class UploadOfficeImage extends Activity {
    private PopupWindow distancePopup;
    private List<PhotoInfo> allPhotoInfos = new ArrayList<PhotoInfo>();
    private int uploadState = 0;
    private String picPath;
    private Uri photoUri;
    private int imageCount = 0;
    private int imageTag = 0;
    private String imagepath;
    private int width = 0;
    private MyGridView images;
    private SelcctPhotoAdapter photoAdapter;
    private BitmapUtils bmUtil;
    private Handler handler;
    @ViewInject(R.id.classify_id)
    private LinearLayout classify_id;
    private List<AlbumClassifyModel> list = new ArrayList<AlbumClassifyModel>();
    @ViewInject(R.id.choose_class)
    private TextView choose_class;
    private String typeid = "1";
    @ViewInject(R.id.submit)
    private TextView submit;
    private List<Bitmap> allUpImages = new ArrayList<Bitmap>();
    private int uploadCount = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_office_image);
        ViewUtils.inject(this);
        bmUtil = new BitmapUtils(this);
        init();
    }

    @SuppressLint("HandlerLeak")
    private void init() {
        Util.initTitle(this, " 上传相册图片",
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UploadOfficeImage.this.finish();
                    }
                }, new OnClickListener() { //点击加号
                    @Override
                    public void onClick(View v) {
                        getPopupWindow();
                        startPopupWindow();
                        distancePopup.showAsDropDown(v);
                    }
                }, R.drawable.note_add);
        images = (MyGridView) this.findViewById(R.id.add_image);
        getClassifyId();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        width = dm.widthPixels;
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 1:
                        List<Object> list = (List<Object>) msg.obj;
                        allUpImages.add((Bitmap) list.get(0));
                        if (list != null) {
                            if (list.size() > 0) {
                                upload("uploadofficeimage", (Bitmap) list.get(0), msg.arg1);
                            }
                        }
                        break;
                    case 2:
                        UploadOfficeImage.this.finish();
                        break;
                }
            }
        };
        Bitmap bitmap = getBitmapFromResources(this, R.drawable.issue_tjtx);
        PhotoInfo photoInfo = new PhotoInfo();
        photoInfo.setBitmap(bitmap);
        allPhotoInfos.add(photoInfo);
        if (photoAdapter == null) {
            photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, images, width * 2 / 9);
        }
        images.setAdapter(photoAdapter);
    }

    @OnClick(R.id.submit)
    public void Submit(final View v) {
        allPhotoInfos.remove(0);
        Log.e("图片集的长度", allPhotoInfos.size() + "");
//		for (int i = 0; i < allPhotoInfos.size(); i++) {

//			Log.e("提交","1");
//			Bitmap bitmap = null;
//			if (allPhotoInfos.get(i).getBitmap() != null) {
//				Log.e("提交","2");
//				bitmap = allPhotoInfos.get(i).getBitmap();
//				Log.e("提交","3");
//				List<Object> list = new ArrayList<Object>();
//				Log.e("提交","4");
//				list.add(bitmap);
//				Log.e("提交","5");
//				list.add(i);
//				Log.e("提交","6");
//				Message msg = new Message();
//				Log.e("提交","7");
//				msg.what = 1;
//				Log.e("提交","8");
//				msg.obj = list;
//				Log.e("提交","9");
//				msg.arg1 = i;
//				Log.e("提交","10");
//				handler.sendMessage(msg);
//				Log.e("提交","11");
//			} else {
//				Log.e("提交","12");
//				ImageView logo = new ImageView(this);
//				Log.e("提交","13");
//				setImage(logo, allPhotoInfos.get(i).getPath_file(), i);
//			}

        Log.e("提交", "1");
        for (int i = 0; i < allPhotoInfos.size(); i++) {
            Log.e("提交", "2");
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
                Log.e("提交", "3");
                ImageView logo = new ImageView(this);
                allPhotoInfos.get(i).setPath_absolute(allPhotoInfos.get(i).getPath_file());
                setImage(logo, allPhotoInfos.get(i).getPath_file(), i);
            }
//			}

        }
    }

    @OnClick(R.id.choose_class)
    public void ChooseClass(final View v) {
        final Dialog ad = new Dialog(UploadOfficeImage.this,
                R.style.CustomDialogStyle);
        View view = getLayoutInflater().inflate(
                R.layout.office_album_classifys_choose, null);
        LinearLayout layout = (LinearLayout) view.findViewById(R.id.container);
        Resources rs = this.getResources();
        final Drawable down = rs.getDrawable(R.drawable.radiobutton_down);
        final Drawable up = rs.getDrawable(R.drawable.radiobutton_up);
        down.setBounds(0, 0, down.getMinimumWidth(), down.getMinimumHeight());
        up.setBounds(0, 0, up.getMinimumWidth(), up.getMinimumHeight());
        LinearLayout.LayoutParams tParams = new LinearLayout.LayoutParams(
                LayoutParams.FILL_PARENT, Util.dpToPx(this, 30));
        tParams.setMargins(0, Util.dpToPx(this, 1), 0, 0);
        for (int i = 0; i < list.size(); i++) {
            TextView t = new TextView(this);
            t.setLayoutParams(tParams);
            t.setGravity(Gravity.CENTER_VERTICAL);
            t.setBackgroundColor(Color.WHITE);

            t.setPadding(Util.pxToDp(this, 5), 0, Util.pxToDp(this, 5), 0);
            if (i == 0) {
                t.setCompoundDrawables(null, null, down, null);
            } else {
                t.setCompoundDrawables(null, null, up, null);
            }
            t.setText("  " + list.get(i).getPhotoTypeName());
            t.setTag(list.get(i).getPhotoTypeid());
            t.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView t = (TextView) v;
                    t.setCompoundDrawables(null, null, down, null);
                    typeid = t.getTag().toString();
                    choose_class.setText("  " + t.getText().toString());
                    if (ad != null) {
                        ad.dismiss();
                    }
                }
            });
            layout.addView(t);
        }
        ad.show();
        Window window = ad.getWindow();
        WindowManager.LayoutParams pa = window.getAttributes();
        pa.width = Util.dpToPx(UploadOfficeImage.this, 250);
        window.setBackgroundDrawable(new ColorDrawable(0));
        window.setContentView(view, pa);
    }

    private void getClassifyId() {
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        HashMap<Integer, List<AlbumClassifyModel>> map = (HashMap<Integer, List<AlbumClassifyModel>>) runData;
                        list = map.get(1);
                        if (list != null && list.size() > 0) {
                            choose_class
                                    .setText(list.get(0).getPhotoTypeName());
                        }
                    } else {
                        Toast.makeText(UploadOfficeImage.this, "没有获取到相册分类", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.GetOfficePhotoClass,
                            "officeid="
                                    + UserData.getOfficeInfo().getOffice_id());
                    List<AlbumClassifyModel> list = web
                            .getList(AlbumClassifyModel.class);
                    HashMap<Integer, List<AlbumClassifyModel>> map = new HashMap<Integer, List<AlbumClassifyModel>>();
                    map.put(1, list);
                    return map;
                }
            });
        }
    }

    public static Bitmap getBitmapFromResources(Activity act, int resId) {
        Resources res = act.getResources();
        return BitmapFactory.decodeResource(res, resId);
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
            photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, images,
                    width * 2 / 9);
            images.setAdapter(photoAdapter);
        } else {
            Toast.makeText(this, "选择图片文件不正确", Toast.LENGTH_LONG).show();
        }
    }

    public void AddOfficeUserPhoto(final String imgStr, final int index) {
//		ds
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        }
        final String userId = UserData.getUser().getUserId();
        final String userPaw = UserData.getUser().getMd5Pwd();
        if (UserData.getOfficeInfo() != null) {
            Util.asynTask(this, "", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (runData != null) {
                        if ("ok".equals(runData + "")) {
                            if (uploadCount == allPhotoInfos.size()) {
                                Toast.makeText(UploadOfficeImage.this, "上传成功", Toast.LENGTH_LONG).show();
                                UploadOfficeImage.this.finish();
                            }
                        }
                    } else {
                        Toast.makeText(UploadOfficeImage.this, "上传失败...", Toast.LENGTH_LONG).show();
                    }
                    uploadCount++;
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.officeUrl, Web.AddOfficeUserPhoto, "officeid=" + UserData.getOfficeInfo().getOffice_id() + "&userID=" + userId + "&userPaw=" + userPaw + "&imgStr=" + imgStr + "&typeid=" + typeid + "&title=&dec=");
                    return web.getPlan();
                }
            });
        }
    }

    private void upload(final String id, final Bitmap bitmap, final int nowin) {
        final CustomProgressDialog dialog = Util.showProgress("图片上传...", this);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                dialog.cancel();
                dialog.dismiss();
                Log.e("返回地址", "runData :" + runData.toString());
                if (!Util.isNull(runData)) {
                    ///upload/s_offices_mood_android14956796384110.42768751212331346.jpg:0
                    String url = runData.toString().split(":")[0];
                    String index = runData.toString().split(":")[1];
                    AddOfficeUserPhoto(url, Integer.parseInt(index));
                }
            }

            @SuppressLint("NewApi")
            @Override
            public Serializable run() {
//				String NAMESCROPE = "http://lin00123.cn/";
                String NAMESCROPE = "http://mynameislin.cn/";
                String METHOD_NAME = "UploadOfficeUserPhoto";
                String URL = Web.imgServer2 + "/ImageServiceUpLoad.asmx";
                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                String userPhoto = null;
                int resultVlaue = 0;
                int counts = 0;
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                    bitmap.compress(compressFormat, 85, out);
                    InputStream sbs = new ByteArrayInputStream(out.toByteArray());
                    byte[] buffer = new byte[30 * 1024];
                    int count = 0;
                    if (out.size() % (30 * 1024) == 0) {
                        counts = out.size() / (30 * 1024);
                    } else {
                        counts = out.size() / (30 * 1024) + 1;
                    }
                    imageCount = counts;
                    int i = 1;
                    String fileName = "mood_android" + System.currentTimeMillis() + "" + new Random().nextDouble() + ".jpg";
                    while ((count = sbs.read(buffer)) >= 0) {
                        String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
                        SoapObject request = new SoapObject(NAMESCROPE, METHOD_NAME);
                        Date curDate = new Date(System.currentTimeMillis());
//							request.addProperty("userKey","a");
                        request.addProperty("userKey", Util.getUSER_KEY(curDate));
                        request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
                        request.addProperty("image", uploadBuffer);
                        request.addProperty("fileName", fileName);
                        request.addProperty("userID", UserData.getUser().getUserId());
                        request.addProperty("userPaw", UserData.getUser().getMd5Pwd());
                        request.addProperty("tag", i);
                        request.addProperty("end", counts);
                        imageTag = i;
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = request;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);
                        HttpTransportSE ht = new HttpTransportSE(URL);
                        ht.debug = true;
                        try {
                            ht.call(SOAP_ACTION, envelope);
                            Object obj = envelope.bodyIn;
                            resultVlaue++;
                            Log.v("------", obj + "");
                            String[] userPhotos = obj.toString().split("success:");
                            if (userPhotos.length == 2) {
                                userPhotos = userPhotos[1].split(";");
                                if (userPhotos.length == 2) {
                                    userPhoto = userPhotos[0] + ":" + nowin;
                                }
                            }

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
                }
                return userPhoto;
            }

        });
    }

    private Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();// 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;// 每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private void takePhoto() {
        String SDState = Environment.getExternalStorageState();
        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);// "android.media.action.IMAGE_CAPTURE"
            ContentValues values = new ContentValues();
            photoUri = this.getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(intent, 1);
        } else {
            Toast.makeText(this, "内存卡不存在", Toast.LENGTH_LONG).show();
        }
    }

    @OnItemClick(R.id.add_image)
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
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
                Toast.makeText(this, "一次最多发布9张图片", Toast.LENGTH_SHORT).show();
            }
        } else {
            getPopupWindow();
            startPopupWindow();
            distancePopup.showAsDropDown(view);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 如果是选择本地图片
        if (requestCode == 1000 && resultCode == 1001) {
            if (uploadState == 2) {

                allPhotoInfos.addAll(BitmapUtil.getPhotoInfos);
                Log.e("现在的图片集长度", allPhotoInfos.size() + "sdasd" + allPhotoInfos.get(1).getBitmap());
                photoAdapter = new SelcctPhotoAdapter(this, allPhotoInfos, images, width * 2 / 9);
                images.setAdapter(photoAdapter);
            }
        } else if (resultCode == Activity.RESULT_OK) {// 如果是调用照相机
            doPhoto(requestCode, data);
        }
    }

    private void setImage(final ImageView logo, String href, final int i) {
        bmUtil.display(logo, href, new DefaultBitmapLoadCallBack<View>() {
            @Override
            public void onLoadCompleted(View arg0, String arg1, Bitmap arg2,
                                        BitmapDisplayConfig arg3, BitmapLoadFrom arg4) {
                System.out.println("--------------display---------------"
                        + arg2.getHeight());
                // bitmap = arg2;
                List<Object> list = new ArrayList<Object>();
                list.add(arg2);
                list.add(i);
                Message msg = new Message();
                msg.what = 1;
                msg.obj = list;
                msg.arg1 = i;
                handler.sendMessage(msg);
                super.onLoadCompleted(arg0, arg1, arg2, arg3, arg4);
            }

            @Override
            public void onLoadFailed(View arg0, String arg1, Drawable arg2) {
                logo.setImageResource(R.drawable.new_yda__top_zanwu);
            }
        });
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
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    /**
     * 初始化并弹出popupwindow
     *
     * @param i
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
                Intent intent = new Intent(UploadOfficeImage.this,
                        SelectUploadImageActivity.class);
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
    }
}
