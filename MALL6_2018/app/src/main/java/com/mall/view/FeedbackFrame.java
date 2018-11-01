package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.GlideImageLoader;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.LocationConstants;
import com.mall.util.ShowMyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.yancy.gallerypick.config.GalleryConfig;
import com.yancy.gallerypick.config.GalleryPick;
import com.yancy.gallerypick.inter.IHandlerCallBack;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class FeedbackFrame extends Activity implements ImageBrowseActivity.CallBack {

    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;
    @ViewInject(R.id.fankui_content)
    private EditText fankui_content;
    @ViewInject(R.id.fankui_img1)
    private ImageView img1;
    @ViewInject(R.id.fankui_img2)
    private ImageView img2;
    @ViewInject(R.id.fankui_img3)
    private ImageView img3;
    @ViewInject(R.id.sel_img)
    private ImageView sel_img;
    @ViewInject(R.id.fankui_phone)
    private EditText fankui_phone;

    @ViewInject(R.id.inputline)
    private LinearLayout inputline;
    @ViewInject(R.id.showimage)
    private RecyclerView showimage;

    private int index = 1;

    MyAdapter myAdapter;

    public static List<String> strings = new ArrayList<>();
    private Context context;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        strings.clear();
    }

    String typestr = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.feedback_frame);
        context = this;
        strings.clear();
        Util.initTitle(this, "意见反馈", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewUtils.inject(this);
        init();
    }

    GalleryConfig galleryConfig;
    String TAG = "TAG";

    int maxSize = 3;

    private void init() {
        User user = UserData.getUser();
        if (null != user && !Util.isNull(user.getMobilePhone()))
            fankui_phone.setText(user.getMobilePhone());

        inputline.setBackground(getBackground("#E9E9E9"));
        tv1.setBackground(getBackground("#E9E9E9"));
        tv2.setBackground(getBackground("#E9E9E9"));
        tv3.setBackground(getBackground("#E9E9E9"));
        tv4.setBackground(getBackground("#E9E9E9"));
        initrecylce();

        IHandlerCallBack iHandlerCallBack = new IHandlerCallBack() {
            @Override
            public void onStart() {
                Log.i(TAG, "onStart: 开启");
            }

            @Override
            public void onSuccess(List<String> photoList) {
                Log.i(TAG, "onSuccess: 返回数据" + photoList.size());
                strings.clear();
                for (String s : photoList) {
                    Log.i(TAG, s);
                }
                strings.addAll(photoList);
                myAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel: 取消");
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "onFinish: 结束");
            }

            @Override
            public void onError() {
                Log.i(TAG, "onError: 出错");
            }
        };

        galleryConfig = new GalleryConfig.Builder()
                .imageLoader(new GlideImageLoader())    // ImageLoader 加载框架（必填）
                .iHandlerCallBack(iHandlerCallBack)     // 监听接口（必填）
                .provider("com.mall.view.fileprovider")   // provider (必填)
                .pathList(strings)                         // 记录已选的图片
                .multiSelect(true, 3)                   // 配置是否多选的同时 配置多选数量   默认：false ， 9
//                .crop(false)                             // 快捷开启裁剪功能，仅当单选 或直接开启相机时有效
                .crop(true, 1, 1, 500, 500)             // 配置裁剪功能的参数，   默认裁剪比例 1:1
                .isShowCamera(true)                     // 是否现实相机按钮  默认：false
                .filePath("/Gallery/Pictures")          // 图片存放路径
                .build();

    }

    private void initrecylce() {
        GridLayoutManager mgr = new GridLayoutManager(this, maxSize);
        showimage.setLayoutManager(mgr);
        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 20);//左间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 20);//右间距
        showimage.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        myAdapter = new MyAdapter(this, strings);
        showimage.setAdapter(myAdapter);
        myAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                if (strings.size() > 0) {
                    Intent intent = new Intent(FeedbackFrame.this, ImageBrowseActivity.class);
                    intent.putStringArrayListExtra("imageList", (ArrayList<String>) strings);
                    intent.putExtra("index", position);
                    startActivityForResult(intent, 1234);
                }
            }


        });
    }

    @Override
    public void doCall(List<String> strings) {
        strings.clear();
        strings.addAll(strings);
    }


    public class MyAdapter extends BaseRecycleAdapter<String> {


        protected MyAdapter(Context context, List<String> list) {
            super(context, list);
        }


        @Override
        public void setIteamData(ViewDataBinding mBinding, List<String> list, int position) {
            String str = list.get(position);
            mBinding.setVariable(com.mall.view.BR.item1, str);

        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {

            return DataBindingUtil.inflate(mInflater, R.layout.imageitem1, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }

    }


    private StateListDrawable getBackground(String color) {
        return SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(this, 1))
                .setCornerRadius(Util.dpToPx(this, 2))
                .setDefaultStrokeColor(Color.parseColor(color))
                .create();
    }

    @OnClick(R.id.fankui_submit)
    public void submitClick(View v) {
        v.setEnabled(false);
        if (fankui_content.getText().toString().trim().length() == 0
                && strings.size() == 0) {
            Util.show("请输入您的反馈内容或截图", this);
        } else {
            imagestr = "";
            if (strings.size() > 0) {
                uploadImageFromLocalFiles(strings);
            } else {
                updataMessage(imagestr);
            }


        }
        v.setEnabled(true);
    }

    String imagestr = "";

    public void updataMessage(final String imagestr) {
        String message = "";
//        String finalImagesfile="";
//        if (!imagestr.equals("")){
//            String[] strarry = imagestr.split("\\*\\|-_-\\|\\*");
//            Log.e("strarry", "strarry" + strarry.length);
//            String imagesfile = "";
//            for (String str : strarry) {
//
////            imagesfile+="<img src="+"http://img.yda360.com/"+str+">"+"</img>";
//                imagesfile += "<img src=" + "\\\"" + "http://img.yda360.com/" + str + "\\\"" + ">" + "</img>";
//            }
//             finalImagesfile = "" + imagesfile;
//        }
//
//
//         message = fankui_content.getText().toString() + finalImagesfile;
//        Log.e("上传的图片地址", "message" + message);


        final String finalMessage = message;

        Util.asynTask(this, "正在提交您的反馈...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (Util.isNull(runData)) {
                    Util.show("网络错误，请重试！", FeedbackFrame.this);
                    return;
                }
                if ((runData + "").contains("success:")) {
                    String[] arr = (runData + "").split(":");
                    Toast.makeText(context, "意见反馈成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Util.show(runData + "", FeedbackFrame.this);
                }
            }

            @Override
            public Serializable run() {
                User user = UserData.getUser();
                String userId = null;
                if (null != user)
                    userId = user.getUserId();
                else
                    userId = "";


                Log.e("上传的图片地址111", "message" + finalMessage);
                Web web = new Web(Web.fankuiMessage, "type="
                        + typestr + "&message="
                        + fankui_content.getText().toString() + "&picurl=" + imagestr + "&phone="
                        + fankui_phone.getText().toString() + "&userid="
                        + userId + "&images=" + strings.size() + "&source=android");
                return web.getPlan();
            }
        });
    }


    public void uploadImageFromLocalFiles(final List<String> localFiles) {

        Log.e("上传", "1");
        Util.asynTask(this, "正在上传图片", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                Log.e("上传图片返回地址", runData.toString() + "LLL");
                imagestr = runData.toString().replace("success:", "");
                updataMessage(imagestr);
//				String result = (String) runData;
//
//				if ((runData + "").contains("success:")) {
//					imageUploadSuccessed="yes";
//					upload(content, result.replace("success:", ""));
//				} else {
//					UserMessageBoardCache usb;
//					try {
//						usb = db.findById(UserMessageBoardCache.class, id);
//						usb.setFlag("no");
//						db.saveOrUpdate(usb);
//					} catch (DbException e1) {
//						e1.printStackTrace();
//					}
//				}


            }

            @SuppressLint("NewApi")
            @Override
            public Serializable run() {
                String result = "";
//				String NAMESCROPE = "http://lin00123.cn/";
                String NAMESCROPE = "http://mynameislin.cn/";
                String METHOD_NAME = "uploadMoodImage";
                String URL = "http://" + "img.yda360.com"
                        + "/ImageServiceUpLoad.asmx";

                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                List<Bitmap> imgList = new ArrayList<Bitmap>();
                StringBuilder sb = new StringBuilder();
                for (int k = 0; k < localFiles.size(); k++) {
                    Bitmap bm = Util.getLocationThmub(localFiles.get(k), 480, 640);
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
                            String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
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


//    @SuppressLint("NewApi")
//    private void upload(final String id) {
//
//
//        Util.asynTask(new IAsynTask() {
//            @Override
//            public void updateUI(Serializable runData) {
//                Log.e("上传图片", "runData" + runData.toString());
//                if (0 != Integer.parseInt(runData + "")) {
//                    Util.showIntent("上传成功！", FeedbackFrame.this, UserCenterFrame.class);
//
//                }
//                Util.show("上传失败！", FeedbackFrame.this);
//            }
//
//            @Override
//            public Serializable run() {
//                String NAMESCROPE = "http://mynameislin.cn/";
//                String METHOD_NAME = "uoloadFeedbackImage";
//                String URL = "http://" + Web.webImage + "/GetUserInfo.asmx";
//                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
//                int resultVlaue = 0;
//
//                for (String str : strings) {
//                    Bitmap bm = Util.getLocationThmub(str, 480, 640);
//                    try {
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
//                        bm.compress(compressFormat, 80, out);
//                        InputStream sbs = new ByteArrayInputStream(out
//                                .toByteArray());
//                        byte[] buffer = new byte[30 * 1024];
//                        int count = 0;
//                        int i = 0;
//                        String fileName = "fb_" + id + "_" + java.util.UUID.randomUUID().toString() + ".jpg";
//                        while ((count = sbs.read(buffer)) >= 0) {
//                            String uploadBuffer = new String(Base64.encode(
//                                    buffer, 0, count, Base64.DEFAULT));
//                            SoapObject request = new SoapObject(NAMESCROPE,
//                                    METHOD_NAME);
//                            Date curDate = new Date(System.currentTimeMillis());
//                            request.addProperty("userKey", Util.getUSER_KEY(curDate));
//                            request.addProperty("userKeyPwd", Util.getUSER_KEYPWD(curDate));
//                            request.addProperty("image", uploadBuffer);
//                            LogUtils.e(uploadBuffer);
//                            request.addProperty("fileName", fileName);
//                            request.addProperty("tag", i);
//
//                            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
//                                    SoapEnvelope.VER11);
//                            envelope.bodyOut = request;
//                            envelope.dotNet = true;
//                            envelope.setOutputSoapObject(request);
//
//                            HttpTransportSE ht = new HttpTransportSE(URL);
//                            ht.debug = true;
//                            try {
//                                ht.call(SOAP_ACTION, envelope);
//                                Object obj = envelope.bodyIn;
//                                resultVlaue++;
//                                LogUtils.v(obj + "");
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                            i++;
//                        }
//                        out.close();
//                        sbs.close();
//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                return resultVlaue;
//            }
//        });
//    }


    @OnClick({R.id.tv1, R.id.tv2, R.id.tv3, R.id.tv4})
    public void click(View view) {
        tv1.setBackground(getBackground("#E9E9E9"));
        tv2.setBackground(getBackground("#E9E9E9"));
        tv3.setBackground(getBackground("#E9E9E9"));
        tv4.setBackground(getBackground("#E9E9E9"));
        switch (view.getId()) {
            case R.id.tv1:
                tv1.setBackground(getBackground("#49afef"));

                break;
            case R.id.tv2:
                tv2.setBackground(getBackground("#49afef"));
                break;
            case R.id.tv3:
                tv3.setBackground(getBackground("#49afef"));
                break;
            case R.id.tv4:
                tv4.setBackground(getBackground("#49afef"));
                break;
        }
        typestr = ((TextView) view).getText().toString();
    }

    @OnClick({R.id.sel_img})
    public void selImgClick(View view) {

        showOptionsDialog(view);


    }


    // 选择图片来源
    public void showOptionsDialog(View view) {
        Log.e("选择图片来源", "view" + view.getId());
        startPopupWindow();
    }

    private void startPopupWindow() {
        View pview = getLayoutInflater().inflate(R.layout.select_pic_layout,
                null, false);
        Button btn_take_photo = (Button) pview
                .findViewById(R.id.btn_take_photo);
        Button btn_pick_photo = (Button) pview
                .findViewById(R.id.btn_pick_photo);
        Button btn_cancel = (Button) pview.findViewById(R.id.btn_cancel);
        final com.mall.util.ShowMyPopWindow showMyPopWindow = new ShowMyPopWindow(pview, context, android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, LocationConstants.CENTER, 0);
        btn_take_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

//                galleryConfig.getBuilder().isOpenCamera(true).build();
//                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(FeedbackFrame.this);
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).openCamera(FeedbackFrame.this);
                showMyPopWindow.dismiss();
            }
        });
        btn_pick_photo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                GalleryPick.getInstance().setGalleryConfig(galleryConfig).open(FeedbackFrame.this);
                showMyPopWindow.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMyPopWindow.dismiss();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.e("resultCode", "resultCode" + resultCode + "requestCode" + requestCode);
        if (requestCode == 1234) {
            Log.e("list长度2", "ss" + strings.size());
            myAdapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("list长度3", "ss" + strings.size());
        myAdapter.notifyDataSetChanged();
    }
}
