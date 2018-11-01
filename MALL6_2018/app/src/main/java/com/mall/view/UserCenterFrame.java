package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.CapitalAccount.AddBankCardActivity;
import com.mall.CapitalAccount.MyAccount_BankListActivity;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.Column;
import com.mall.model.InMaill;
import com.mall.model.User;
import com.mall.model.UserAccountWithdrawal;
import com.mall.model.recommendbean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.order.RefundOrder;
import com.mall.pushmessage.PushMessage;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.view.pullscrollview.PullScrollView;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CircleImageView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.RedEnvelopesPackage.SpeedUpActivity;
import com.mall.view.WithdrawalBusiness.WithdrawalBusinessApplicationActivity;
import com.mall.view.carMall.CarManageActivity;
import com.mall.view.carMall.CongratulationsDialog;
import com.mall.view.databinding.RecommenditemBinding;
import com.mall.view.healthMall.HealthShopActivity;
import com.mall.widget.DragGridView;
import com.mall.widget.GridViewAdapter;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import io.reactivex.functions.Consumer;

@SuppressLint("NewApi")
@ContentView(R.layout.usercenter_frame2)
public class UserCenterFrame extends BaseActivity {

    @ViewInject(R.id.user_center_bg)
    private ImageView user_center_bg;
    @ViewInject(R.id.scroll)
    private PullScrollView scroll;
    @ViewInject(R.id.tv_noreadpush)
    private TextView tv_noreadpush;
    @ViewInject(R.id.red_text)
    private TextView red_text;
    @ViewInject(R.id.new_logins)
    private View new_logins;
    @ViewInject(R.id.user_center_panel)
    private View user_center_panel;
    @ViewInject(R.id.user_center_face)
    private CircleImageView user_center_face;
    @ViewInject(R.id.sc_user_center_face)
    private TextView sc_user_center_face;
    @ViewInject(R.id.new_membercenter_zz)
    private TextView zz;
    @ViewInject(R.id.new_membercenter_xj)
    public static TextView xj;
    @ViewInject(R.id.new_membercenter_userNum)
    private TextView userNum;
    @ViewInject(R.id.new_membercenter_userPhone)
    private TextView userPhone;
    @ViewInject(R.id.new_membercenter_userJS)
    private TextView userJS;
    @ViewInject(R.id.order_pending_payment_count)
    private TextView order_pending_payment_count;
    @ViewInject(R.id.order_received_count)
    private TextView order_received_count;

    @ViewInject(R.id.gridView)
    private DragGridView gridView;
    @ViewInject(R.id.order_view)
    private LinearLayout order_view;

    @ViewInject(R.id.recommendshopry)
    private RecyclerView recommendshopry;
    List<recommendbean.ListBean> listBeans = new ArrayList<>();
    private List<Column> dataSourceList = new ArrayList<>();
    private List<Column> dataList = new ArrayList<Column>() {{
        add(new Column(R.drawable.usercenter_1, "我的账户", 0, 0));
        add(new Column(R.drawable.usercenter_2, "银行卡", 1, 0));
        add(new Column(R.drawable.usercenter_3, "支付宝/微信", 2, 0));
        add(new Column(R.drawable.usercenter_4, "增值加速", 3, 0));
        add(new Column(R.drawable.usercenter_5, "我的二维码", 4, 0));
        add(new Column(R.drawable.usercenter_6, "我的收藏", 5, 0));
        add(new Column(R.drawable.usercenter_7, "我的数据", 6, 0));
        add(new Column(R.drawable.usercenter_8, "我要分享", 7, 0));
        add(new Column(R.drawable.usercenter_9, "推送消息", 8, 0));
        add(new Column(R.drawable.usercenter_10, "推送过的消息", 9, 0));
        add(new Column(R.drawable.usercenter_11, "业务办理", 10, 0));
        add(new Column(R.drawable.usercenter_12, "客服中心", 11, 0));
        add(new Column(R.drawable.usercenter_13, "我的区域业务", 12, 0));
        add(new Column(R.drawable.usercenter_14, "创客专车", 13, 0));
        add(new Column(R.drawable.usercenter_14, "提现审核", 14, 0));
        add(new Column(R.drawable.usercenter_15, "健康礼包", 15, 0));
    }};
    private GridViewAdapter adapter;
    private Boolean isred = false;
    private int levelid;
    private int shopTypeId;

    //相册请求码
    private static final int ALBUM_REQUEST_CODE = 1;
    //相机请求码
    private static final int CAMERA_REQUEST_CODE = 2;
    //剪裁请求码
    private static final int CROP_REQUEST_CODE = 3;

    //调用照相机返回图片文件
    private File tempFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(context);
        initView();
        initRecycle();
    }

    MyAdapter myAdapter;

    private void initRecycle() {
        GridLayoutManager manager = new GridLayoutManager(this, 2) {
            @Override
            public boolean canScrollVertically() {
                return false;//解决滑动卡顿
            }
        };
        recommendshopry.setLayoutManager(manager);
        myAdapter = new MyAdapter(context, listBeans);
        recommendshopry.setAdapter(myAdapter);
        myAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Util.showIntent(UserCenterFrame.this,
                        ProductDeatilFream.class, new String[]{"url", "tagkk"},
                        new String[]{listBeans.get(position).getPid(), true + ""});
            }
        });
        getShopRe();
    }

    int page = 1;

    private void getShopRe() {
//        http://test.yda360.cn/api_test/Product.aspx?call=getProductList&type=&ascOrDesc=desc&cid=&page=2&size=12&v_v=5.2.7&USER_KEY=349d4ee26249c7d23f373ea52eafcb03&USER_KEYPWD=20180316140442950
        String params = "type=新品&ascOrDesc=" + "asc" + "&cid=" + "589";
        //http://test.yda360.cn/api_test/Product.aspx?call=getProductList&type=%E6%96%B0%E5%93%81&ascOrDesc=asc&cid=589&page=1&size=12&v_v=5.2.7&USER_KEY=64be0f1db06bb58e5c0a1ebd41944125&USER_KEYPWD=20180316172518450
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest(
                "/Product.aspx?call=getProductList&type=新品&cid=1873&page=1&size=12",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络错误，请重试！", UserCenterFrame.this);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), UserCenterFrame.this);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject[] ancient = json.getJSONArray("list").toArray(
//                        com.alibaba.fastjson.JSONObject[] ancient = json.getJSONArray("weishang").toArray(
                                new com.alibaba.fastjson.JSONObject[]{});
                        int length = ancient.length > 6 ? 6 : ancient.length;
                        for (int i = 0; i < length; i++) {
                            recommendbean.ListBean bean = new recommendbean.ListBean();
                            bean.setName(ancient[i].getString("name"));
                            bean.setThumb(ancient[i].getString("thumb"));
                            bean.setPid(ancient[i].getString("pid"));
                            listBeans.add(bean);
                        }

                        myAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        cpd.dismiss();

                    }

                    @Override
                    public void fail(Throwable e) {
                        super.fail(e);
                    }
                });
    }

    public class MyAdapter extends BaseRecycleAdapter<recommendbean.ListBean> {

        protected MyAdapter(Context context, List<recommendbean.ListBean> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, final List<recommendbean.ListBean> list, final int position) {

            GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) mBinding.getRoot().getLayoutParams();
            layoutParams.height = Util.aa / 3 * 2;
            layoutParams.width = Util.aa / 2;
//            layoutParams.setMargins(12,12,12,12);
            mBinding.getRoot().setLayoutParams(layoutParams);
//            if (layoutParams == null) {
//                layoutParams = new AbsListView.LayoutParams(Util.aa / 2, Util.aa / 2);
//                mBinding.getRoot().setLayoutParams(layoutParams);
//            } else {
//                layoutParams.height = Util.aa / 2;
//                layoutParams.width = Util.aa / 2;
//            }


            RecommenditemBinding binding = (RecommenditemBinding) mBinding;

            binding.root1.setBackground(SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                    .setStrokeWidth(Util.dpToPx(mContext, 1))
                    .setDefaultStrokeColor(Color.parseColor("#dddddd"))
                    .create());
            binding.recommend1Title.setText(list.get(position).getName());
            Glide.with(mContext).load(list.get(position).getThumb()).error(R.drawable.new_yda__top_zanwu).into(binding.recommend1Image);
//            Glide.with(mContext).load("http://pic.58pic.com/58pic/12/35/76/65b58PICUtw.jpg").into(binding.recommend1Image);

        }


        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {

            return DataBindingUtil.inflate(mInflater, R.layout.recommenditem, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e("------", "onStart()");
        if (Util.checkLoginOrNot()) {
            setIsRed();
            setUserMessage();
            getAllOrderNum();
            getUserCarpool();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("------", "onResume()");
        if (Util.checkLoginOrNot()) {
            new_logins.setVisibility(View.GONE);
            user_center_panel.setVisibility(View.VISIBLE);
            order_pending_payment_count.setVisibility(View.VISIBLE);
            order_received_count.setVisibility(View.VISIBLE);
            setNoGetRed();
            setNoReadPush();
            setNoReplyPush();
        } else {
            user_center_panel.setVisibility(View.GONE);
            new_logins.setVisibility(View.VISIBLE);
            red_text.setVisibility(View.GONE);
            tv_noreadpush.setVisibility(View.GONE);
            order_pending_payment_count.setVisibility(View.GONE);
            order_received_count.setVisibility(View.GONE);
            for (int i = 0; i < dataSourceList.size(); i++) {
                if (dataSourceList.get(i).getTag() == 9) {
                    dataSourceList.get(i).setCount(0);
                    break;
                }
            }
            adapter.notifyDataSetChanged();
            gridView.setAlpha(0.5f);
            order_view.setAlpha(0.5f);
        }
    }

    private void isTixianDate() {
        final CustomProgressDialog cpd = Util.showProgress("加载中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "GetjiasuMessage" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            VoipDialog voipDialog = new
                                    VoipDialog(json.getString("message"), json.getString("info"), context, "确定", "", null, null);
                            voipDialog.show();
                            return;
                        }


                        if (!Util.checkUserInfocomplete()) {
                            VoipDialog voipDialog = new
                                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", context, "立即登记", "稍后登记", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Util.showIntent(context,
                                            UpdateUserMessageActivity.class);
                                }
                            }, null);
                            voipDialog.show();
                            return;
                        }
//
//
                        Util.showIntent(context, SpeedUpActivity.class);


                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    private void initView() {
        scroll.setHeader(user_center_bg);
        dataSourceList.addAll(dataList);
        adapter = new GridViewAdapter(context, dataSourceList, 0);
        gridView.setAdapter(adapter);
        if (getSharedPreferences("first", MODE_PRIVATE).getBoolean("first", true)) {
            Util.showIntent(UserCenterFrame.this, ZhezhaoActivity.class);
            getSharedPreferences("first", MODE_PRIVATE).edit().putBoolean("first", false).commit();
        }
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (Util.checkLoginOrNot()) {
                    AnimeUtil.startAnimation(context, view, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {

                        @Override
                        public void start() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void repeat() {
                            // TODO Auto-generated method stub
                        }

                        @Override
                        public void end() {
                            switch (dataSourceList.get(position).getTag()) {
                                case 0:
                                    Util.showIntent(context, AccountManagerFrame.class);
                                    break;
                                case 1:
                                    check_band_account();
                                    break;
                                case 2:
                                    Util.showIntent(context, PerfectInformationFrame.class, new String[]{"type"}, new String[]{"zfb/vx"});
                                    break;
                                case 3:
                                    isTixianDate();


                                    break;
                                case 4:
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    final AlertDialog dialog = builder.show();
                                    LayoutInflater inflater = LayoutInflater.from(context);
                                    View alertview = inflater.inflate(R.layout.personcard, null);
                                    ImageView person_card_view = alertview.findViewById(R.id.person_card);
                                    person_card_view.setOnLongClickListener(new View.OnLongClickListener() {
                                        @Override
                                        public boolean onLongClick(View v) {
                                            String[] items = new String[]{"保存"};

                                            DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case 0:
                                                            String SDState = Environment.getExternalStorageState();
                                                            if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                                                                String str = "http://" + Web.webImage + "/phone/registe.aspx?inviter="
                                                                        + UserData.getUser().getUtf8UserId();
                                                                BitMatrix matrix = null;
                                                                try {
                                                                    matrix = new MultiFormatWriter().encode(str,
                                                                            BarcodeFormat.QR_CODE, 300, 300);
                                                                } catch (WriterException e1) {
                                                                    e1.printStackTrace();
                                                                }
                                                                if (null == matrix) {
                                                                    Util.showAlert("创建二维码文件失败！", context);
                                                                    return;
                                                                }
                                                                int width = matrix.getWidth();
                                                                int height = matrix.getHeight();
                                                                int[] pixels = new int[width * height];
                                                                for (int y = 0; y < height; y++) {
                                                                    for (int x = 0; x < width; x++) {
                                                                        if (matrix.get(x, y)) {
                                                                            pixels[y * width + x] = 0xff000000;
                                                                        } else { // 无信息设置像素点为白色
                                                                            pixels[y * width + x] = 0xffffffff;
                                                                        }
                                                                    }
                                                                }
                                                                Bitmap erweimaBitmap = Bitmap.createBitmap(width, height,
                                                                        Bitmap.Config.ARGB_8888);
                                                                erweimaBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
                                                                Util.saveBitmapToSdCard("reg_" + UserData.getUser().getNoUtf8UserId() + ".jpg", "远大二维码", erweimaBitmap);
                                                                Util.show("二维码已保存在：/sdcard/DCIM/远大二维码/reg_" + UserData.getUser().getNoUtf8UserId() + ".jpg");
                                                            } else {
                                                                Util.showAlert("内存卡不存在！", context);
                                                            }
                                                            break;
                                                    }
                                                }
                                            };
                                            new AlertDialog.Builder(context).setItems(items, click).show().setCanceledOnTouchOutside(true);
                                            return false;
                                        }
                                    });
                                    try {
                                        createImage(person_card_view);
                                    } catch (WriterException e) {
                                        e.printStackTrace();
                                    }
                                    dialog.setContentView(alertview);
                                    break;
                                case 5:
                                    Util.showIntent(context, MyCollectionActivity.class);
                                    break;
                                case 6:
                                    Util.showIntent(context, MemberFrame.class);
                                    break;
                                case 7:
                                    if (2 > Util.getInt(UserData.getUser().getLevelId())) {
                                        Util.show("您的会员等级不能分享会员。", context);
                                        return;
                                    }
                                    if ("6".equals(UserData.getUser().getLevelId())) {
                                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                                        return;
                                    }
                                    if (UserData.getUser().getMobilePhone().equals("")) {
                                        Util.show("你的资料未完善,请先完善资料", context);
                                        Util.showIntent(UserCenterFrame.this, UpdateUserMessageActivity.class);
                                        return;
                                    }
                                    final OnekeyShare oks = new OnekeyShare();
                                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum=" + UserData.getUser().getUtf8UserId()
                                            + "&shareVersion=mall";
                                    final String title = getResources().getString(R.string.sharetitle);
                                    oks.setTitle(title);
                                    oks.setTitleUrl(url);
                                    oks.setUrl(url);
                                    oks.setImageUrl("http://app.yda360.com/phone/images/icon_mall.png?r=1");
                                    oks.setAddress("10086");
                                    oks.setComment("快来注册吧");
                                    oks.setText(getResources().getString(R.string.sharemessage));
                                    oks.setSite(title);
                                    oks.setSilent(false);
                                    oks.setSiteUrl(url);
                                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                        @Override
                                        public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                                            if ("ShortMessage".equals(platform.getName())) {
                                                paramsToShare.setImageUrl(null);
                                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                                            }
                                        }
                                    });
                                    oks.show(context);

//                                    Util.showIntent(context, UserCenterShareActivity.class, new String[]{"share"}, new Serializable[]{2});
                                    break;
                                case 8:
                                    Util.showIntent(context, PushMessage.class);
                                    break;
                                case 9:
                                    Util.showIntent(context, MessagePushedByMySelf.class);
                                    break;
                                case 10:
                                    Util.showIntent(context, BusinessHandlingActivity.class);
                                    break;
                                case 11:
                                    Util.showIntent(context, CustomerServiceActivity.class);
                                    break;
                                case 12:
                                    Util.showIntent(context, AreaBusinessFrame.class);
                                    break;
                                case 13:
                                    Util.showIntent(context, CarManageActivity.class);
                                    break;
                                case 14:
                                    Util.showIntent(context, WithdrawalBusinessApplicationActivity.class);
                                    break;
                                case 15:
                                    Util.showIntent(context, HealthShopActivity.class);
                                    break;
                            }
                        }
                    });
                } else {
                    Util.show("请先登录!");
                    Util.showIntent(context, LoginFrame.class, new String[]{"source"}, new String[]{"reg"});
                }
            }
        });
    }

    private void getUserCarpool() {
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_user_Carpool" + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&userId=" + UserData.getUser().getUserId(),
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Log.e("--------", result.toString());
                        try {
                            JSONObject jsonObject = new JSONObject(result.toString());
                            if (jsonObject.getString("code").equals("200")) {
                                JSONObject jsonObject1 = jsonObject.getJSONArray("list").getJSONObject(0);
                                new CongratulationsDialog(context, jsonObject1.getString("type"), jsonObject1.getString("orderid")).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {
                    }
                }
        );
    }

    private void check_band_account() {
        if ("0".equals(UserData.getUser().getSecondPwd())) {
            VoipDialog voipDialog = new VoipDialog("对不起，您还未设置交易密码。为保障您的交易安全，请先设置交易密码", this, "确定", "取消", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(UserCenterFrame.this,
                            SetSencondPwdFrame.class);
                }
            }, null);
            voipDialog.show();
            return;
        }
        if (!Util.checkUserInfocomplete()) {
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", context, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(context,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }


        final CustomProgressDialog cpd = Util.showProgress("账户检查中...", this);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=" + "get_tx_way" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                ,
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }
                        Gson gson = new Gson();
                        UserAccountWithdrawal userAccountWithdrawal = gson.fromJson(result.toString(), UserAccountWithdrawal.class);
                        UserAccountWithdrawal.ListBean listBean = userAccountWithdrawal.getList().get(0);
                        String bankinstate = listBean.getBank();

                        if (bankinstate.equals("-1")) {
                            Toast.makeText(context, "未检查到是否绑定了银行卡", Toast.LENGTH_SHORT).show();
                        } else if (bankinstate.equals("0")) {
                            Util.showIntent(context, AddBankCardActivity.class, new String[]{"title"}, new String[]{AddBankCardActivity.tagbank});
                        } else {
                            Util.showIntent(context, MyAccount_BankListActivity.class, new String[]{MyAccount_BankListActivity.TITLETAG}, new String[]{"银行卡"});
                        }
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

    @OnClick({R.id.red_image, R.id.user_center_login, R.id.user_center_register, R.id.user_center_face})
    public void otherClick(final View v) {

        AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {

            @Override
            public void start() {
                // TODO Auto-generated method stub

            }

            @Override
            public void repeat() {
                // TODO Auto-generated method stub

            }

            @Override
            public void end() {
                switch (v.getId()) {
                    case R.id.red_image:
                        Util.showIntent(context, RedPackageActivity.class);
                        break;
                    case R.id.user_center_login:
                        Util.showIntent(context, LoginFrame.class, new String[]{"source"}, new String[]{"reg"});
                        break;
                    case R.id.user_center_register:
                        Util.showIntent(context, RegisterFrame.class, new String[]{"source"}, new String[]{"reg"});
                        break;
                    case R.id.user_center_face:
                        showOptionsDialog();
                }
            }
        });
    }

    @OnClick({R.id.iv_setting, R.id.iv_message, R.id.order_manage, R.id.order_pending_payment, R.id.order_received, R.id.order_successful_trade, R.id.order_exchange_goods})
    public void loginClick(final View v) {
        if (Util.checkLoginOrNot()) {
            AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {

                @Override
                public void start() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void repeat() {
                    // TODO Auto-generated method stub

                }

                @Override
                public void end() {
                    Intent intent = new Intent(UserCenterFrame.this, OrderFrame.class);
                    switch (v.getId()) {
                        case R.id.iv_setting:
                            Util.showIntent(context, SettingFrame.class);
                            break;
                        case R.id.iv_message:
                            Util.showIntent(context, PushMessageAndroidFrame.class);
                            break;
                        case R.id.order_manage:
                            Util.showIntent(context, ChangeOrderActivity.class);
                            break;
                        case R.id.order_pending_payment:
                            intent.putExtra("key", "待付款");
                            startActivity(intent);
                            break;
                        case R.id.order_received:
                            intent.putExtra("key", "已付款");
                            startActivity(intent);
                            break;
                        case R.id.order_successful_trade:
                            intent.putExtra("key", "成功");
                            startActivity(intent);
                            break;
                        case R.id.order_exchange_goods:
                            Util.showIntent(UserCenterFrame.this, RefundOrder.class);
                            break;
                    }
                }
            });
        } else {
            Util.show("请先登录!");
            Util.showIntent(context, LoginFrame.class, new String[]{"source"}, new String[]{"reg"});
        }
    }

    // 生成二维码图片
    private void createImage(ImageView iv) throws WriterException {
        String str = "http://" + Web.webImage + "/phone/registe.aspx?inviter=" + UserData.getUser().getUtf8UserId();
        int _300dp = Util.dpToPx(context, 300F);
        BitMatrix matrix = new MultiFormatWriter().encode(str, BarcodeFormat.QR_CODE, _300dp, _300dp);
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (matrix.get(x, y)) {
                    pixels[y * width + x] = 0xff000000;
                } else { // 无信息设置像素点为白色
                    pixels[y * width + x] = 0xffffffff;
                }
            }
        }
        Bitmap erweimaBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        erweimaBitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        iv.setImageBitmap(erweimaBitmap);
    }

    private void setIsRed() {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.optString("code").equals("200")) {
                        int count = Integer.parseInt(jsonObject.optString("cszj_count"));
                        if (count > 4) {
                            isred = true;
                        } else {
                            isred = false;
                        }

                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public Serializable run() {
                // TODO Auto-generated method stub
                Web web = new Web(Web.redurl, "/Get_tx_Count_Cszj",
                        "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd());
                String result = web.getPlan();
                return result;
            }
        });
    }

    private void setUserMessage() {
        if (userNum.getText().toString().equals("会员账号：")) {
            AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
            ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            AnimationSet animationSet = new AnimationSet(true);
            animationSet.addAnimation(alphaAnimation);
            animationSet.addAnimation(scaleAnimation);
            animationSet.setDuration(1000);
            user_center_panel.startAnimation(animationSet);
            setUser(UserData.getUser());
        }
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                User user = (User) runData;
                if (null == user || Util.isNull(user.getUserNo())) {
                    LogUtils.e("获取会员信息失败，请重试！       " + runData + "");
                    return;
                }
                setUser(user);
            }

            @Override
            public Serializable run() {
                return Web.reDoLogin();
            }
        });
    }

    private void setUser(User user) {
        if (TextUtils.isEmpty(user.getUserFace())) {
            sc_user_center_face.setVisibility(View.VISIBLE);
            user_center_face.setImageResource(R.drawable.community_image_head_rect);
        } else {
            sc_user_center_face.setVisibility(View.GONE);
            Picasso.with(context).load(user.getUserFace()).memoryPolicy(MemoryPolicy.NO_CACHE).placeholder(user_center_face.getDrawable()).into(user_center_face);
        }
        userNum.setText("会员帐号：" + Util.getNo_pUserId(user.getNoUtf8UserId()));
        levelid = Integer.parseInt(user.getLevelId());
        Log.e("会员等级", levelid + "");
        shopTypeId = Integer.parseInt(user.getShopTypeId());
        userPhone.setText("手机号码：" + user.getMobilePhone());
        if (levelid == 6 || levelid == 4) {
            userPhone.setVisibility(View.GONE);
            setNoVisible(3);//增值加速
            setNoVisible(7);//我要分享
        } else {
            setNoVisible(12);//我的区域业务
            setNoVisible(14);//我的区域业务
        }
        if (levelid <= 1 || levelid == 6 || levelid == 4) {
            setNoVisible(4);//我的二维码
        }
        if (levelid < 4 || levelid > 6) {
            if (shopTypeId < 3 || shopTypeId > 10) {
                setNoVisible(8);//推送过的消息
                setNoVisible(9);//我的推送消息
            }
        }
//        if(levelid)
        adapter.notifyDataSetChanged();
        userJS.setText(user.getShowLevel());
        if (isred) {
            userJS.setTextColor(Color.parseColor("#FF0000"));
        } else {
            userJS.setTextColor(Color.parseColor("#035380"));
        }
        xj.setText(user.getRed_Cash());
        zz.setText(user.getRed_Seed());
    }

    private void setNoVisible(int tag) {
        for (int i = 0; i < dataSourceList.size(); i++) {
            if (dataSourceList.get(i).getTag() == tag) {
                dataSourceList.remove(i);
                break;
            }
        }
    }

    private void setNoGetRed() {
        // 获取红包
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                Lin_MallActivity.list.clear();
                try {
                    JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
                    Gson gson = new Gson();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        RedPacket redPacket = gson.fromJson(jsonObject.toString(), RedPacket.class);
                        Lin_MallActivity.list.add(redPacket);
                    }
                    red_text.setVisibility(View.VISIBLE);
                    red_text.setText(Lin_MallActivity.list.size() + "");
                    if (Lin_MallActivity.list.size() > 99) {
                        red_text.setText(99 + "+");
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public Serializable run() {
                // TODO Auto-generated method stub
                Web web = new Web(Web.redurl, "/Get_RedPackage_Allday",
                        "userId=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd());
                String result = web.getPlan();
                return result;
            }

        });
    }

    private void setNoReadPush() {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<InMaill>> map = (HashMap<String, List<InMaill>>) runData;
                Util.mailllist.clear();
                Util.mailllist = map.get("list");
                tv_noreadpush.setVisibility(View.VISIBLE);
                tv_noreadpush.setText(Util.mailllist.size() + "");
                if (Util.mailllist.size() > 99) {
                    tv_noreadpush.setText(99 + "+");
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getUnReaderMessage, "userid=" + UserData.getUser().getUserId()
                        + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&page=1&pageSize=99");
                List<InMaill> list = web.getList(InMaill.class);
                HashMap<String, List<InMaill>> map = new HashMap<>();
                map.put("list", list);
                return map;
            }
        });

    }

    private void setNoReplyPush() {
        NewWebAPI.getNewInstance().Get_Push_replay_noread(UserData.getUser().getUserId(), UserData.getUser().getMd5Pwd(), new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                Log.e("未读消息", result.toString());
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    int count = Integer.parseInt(jsonObject.optString("message"));
                    for (int i = 0; i < dataSourceList.size(); i++) {
                        if (dataSourceList.get(i).getTag() == 9) {
                            dataSourceList.get(i).setCount(count);
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getAllOrderNum() {
        Map<String, String> map = new HashMap<>();
        map.put("userId", UserData.getUser().getUserId());
        map.put("md5Pwd", UserData.getUser().getMd5Pwd());
        NewWebAPI.getNewInstance().getWebRequest("/YdaOrder.aspx?call=getOrderNum", map, new WebRequestCallBack() {

            @Override
            public void success(Object result) {
                super.success(result);
                if (null == result) {
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(result.toString());
                    if (200 != Integer.parseInt(jsonObject.getString("code"))) {
                        return;
                    }
                    JSONArray jsonArray = jsonObject.optJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("state").equals("1")) {
                            order_pending_payment_count.setText(object.optString("quantity"));
                        } else if (object.getString("state").equals("2")) {
                            order_received_count.setText(object.optString("quantity"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    // 选择图片来源
    public void showOptionsDialog() {
        if (!Util.isNetworkConnected(this)) {
            Util.showAlert("没有检测到网络，请检查您的网络连接...", context);
            return;
        }
        String[] items = new String[]{"拍照", "选择本地图片"};

        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:// 拍照
                        if (Build.VERSION.SDK_INT >= 23) {
                            final int[] num = {0, 0, 0};
                            final String[] requestPermissionstr = {
                                    android.Manifest.permission.CAMERA
                            };
                            RxPermissions rxPermissions = new RxPermissions(((Activity) context).getParent());
                            rxPermissions.requestEach(requestPermissionstr)
                                    .subscribe(new Consumer<Permission>() {
                                        @Override
                                        public void accept(Permission permission) throws Exception {

                                            if (permission.granted) {
                                                num[0]++;
                                                Log.e("...", "同意权限");
                                                getPicFromCamera();
                                            } else if (permission.shouldShowRequestPermissionRationale) {
                                                // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                                                Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』");
                                                num[1]++;
                                                Log.e("权限检查", "用户拒绝了该权限，没有选中『不再询问』" + num[1]);
                                                if (num[1] == 1) {
                                                    com.mall.util.Util.show("请允许应用权限请求...");

                                                }
                                            } else {
                                                // 用户拒绝了该权限，并且选中『不再询问』
                                                Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问");
                                                num[2]++;
                                                Log.e("权限检查", "用户拒绝了该权限，并且选中『不再询问" + num[2]);
                                                if (num[2] == 1) {
                                                    com.mall.util.Util.show("请允许应用权限请求...");
                                                    try {
                                                        Uri packageURI = Uri.parse("package:" + context.getPackageName());
                                                        Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                                                        startActivity(intent1);
                                                    } catch (Exception e2) {
                                                        // TODO: handle exception
                                                    }

                                                }

                                            }

                                        }
                                    });
                        } else {
                            getPicFromCamera();
                        }
                        break;
                    case 1:// 选择本地图片
                        getPicFromAlbm();
                        break;
                }
            }
        };
        new AlertDialog.Builder(context).setItems(items, click).show().setCanceledOnTouchOutside(true);
    }

    /**
     * 从相机获取图片
     */
    private void getPicFromCamera() {
        //用于保存调用相机拍照后所生成的文件
        tempFile = new File(Environment.getExternalStorageDirectory().getPath(), System.currentTimeMillis() + ".jpg");
        //跳转到调用系统相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //判断版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
            intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, "com.mall.view.fileprovider", tempFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
        } else {    //否则使用Uri.fromFile(file)方法获取Uri
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempFile));
        }
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    /**
     * 从相册获取图片
     */
    private void getPicFromAlbm() {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, ALBUM_REQUEST_CODE);
    }

    /**
     * 裁剪图片
     */
    private void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CAMERA_REQUEST_CODE:
                //调用相机后返回
                if (resultCode == RESULT_OK) {
                    //用相机返回的照片去调用剪裁也需要对Uri进行处理
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Uri contentUri = FileProvider.getUriForFile(context, "com.mall.view.fileprovider", tempFile);
                        cropPhoto(contentUri);
                    } else {
                        cropPhoto(Uri.fromFile(tempFile));
                    }
                }
                break;
            case ALBUM_REQUEST_CODE:    //调用相册后返回
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    cropPhoto(uri);
                }
                break;
            case CROP_REQUEST_CODE:     //调用剪裁后返回
                if (data != null) {
                    Log.e("剪裁后返回", "data" + (data != null));
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        //在这里获得了剪裁后的Bitmap对象，可以用于上传
                        Bitmap image = bundle.getParcelable("data");
                        upLoadPicHead(image);
                        //设置到ImageView上
                        //也可以进行一些保存、压缩等操作后上传
                    }
                }


                break;
        }
    }

    public void upLoadPicHead(final Bitmap bitmap) {
        com.mall.serving.community.util.Util.asynTask(new com.mall.serving.community.util.IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    User user = UserData.getUser();
                    user.setUserFace(
                            "http://" + Web.webImage + "//userface/" + user.getUserNo() + "_97_97.jpg?_=" + runData);
                    if (com.mall.serving.community.util.Util.isNetworkConnected()) {
                        com.mall.serving.community.util.Util.show("上传成功!");
                    } else {
                        com.mall.serving.community.util.Util.show("上传失败!");
                    }
                } else
                    com.mall.serving.community.util.Util.show("上传失败!");
            }

            @Override
            public Serializable run() {
                Serializable result = null;
                User user = UserData.getUser();
                String NAMESCROPE = "http://mywebservice.cn/";
                String METHOD_NAME = "uploadResume";
                String URL = "http://" + Web.webImage + "/WebService_1.asmx";
                String SOAP_ACTION = NAMESCROPE + METHOD_NAME;
                try {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
                    bitmap.compress(compressFormat, 80, out);
                    InputStream sbs = new ByteArrayInputStream(out.toByteArray());
                    byte[] buffer = new byte[80 * 1024];
                    int count = 0;
                    int i = 0;
                    while ((count = sbs.read(buffer)) >= 0) {
                        String uploadBuffer = new String(Base64.encode(buffer, 0, count, Base64.DEFAULT));
                        SoapObject request = new SoapObject(NAMESCROPE, METHOD_NAME);
                        request.addProperty("userNo", user.getUserNo());
                        request.addProperty("image", uploadBuffer);
                        request.addProperty("tag", i);
                        request.addProperty("size", out.size());
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
                        envelope.bodyOut = request;
                        envelope.dotNet = true;
                        envelope.setOutputSoapObject(request);

                        HttpTransportSE ht = new HttpTransportSE(URL);
                        Log.e("信息", "信息LK" + URL);
                        ht.debug = true;
                        try {
                            ht.call(SOAP_ACTION, envelope);
                            Object obj = envelope.bodyIn;
                            LogUtils.e(obj + "");
                            result = (obj + "").substring((obj + "").lastIndexOf("=") + 1);
                            result = (result + "").substring(0, (result + "").length() - 3);
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
                return result;
            }
        });

    }

}
