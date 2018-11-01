package com.mall.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.happlylot.MyHapplyLot;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.util.ArrayList;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.OnekeyShareTheme;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;


@ContentView(R.layout.user_center_share_activity)
public class UserCenterShareActivity extends BaseActivity {

    @ViewInject(R.id.share_layout)
    private View share_layout;
    @ViewInject(R.id.share_yunshang_app)
    private ImageView share_yunshang_app;

    @ViewInject(R.id.share_yunshang_vip)
    private ImageView share_yunshang_vip;

    @ViewInject(R.id.share_union_vip)
    private ImageView share_union_vip;
    @ViewInject(R.id.share_union_app)
    private ImageView share_union_app;

    @ViewInject(R.id.share_cy666_app)
    private ImageView share_cy666_app;
    @ViewInject(R.id.share_cy666_vip)
    private ImageView share_cy666_vip;
    @ViewInject(R.id.share_happy)
    private ImageView share_happy;

    @ViewInject(R.id.gv)
    private GridView gv;

    private int[] grid_collect_img = {
            R.drawable.user_collect_shangpin,
            R.drawable.user_collect_shangjia};
    private String[] grid_collect_str = {
            "收藏的商品", "收藏的商家"};
    private Class[] grid_collect_cla = {
            CollectProductFrame.class, CollectAllianceFrame.class};
    private int[] grid_business_img = {
            R.drawable.user_business_fufen, R.drawable.user_business_kongjian};
    private String[] grid_business_str = {
            "我的福分", "我的空间"};

    private int[] grid_share_img = {R.drawable.user_share_huiyuan, R.drawable.user_share_kongjian,
            R.drawable.user_share_shangpin, R.drawable.user_share_shangjia, R.drawable.user_share_yunshang,
            R.drawable.user_share_lmsj};
    private String[] grid_share_str = {"分享会员", "分享空间", "分享产品", "分享商家", "分享云商app", "分享商家app"};

    private List list;
    private int[] grid_img;
    private String[] grid_str;
    private PopupWindow popup;

    private int share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        list = new ArrayList();
        setView();

        for (int i = 0; i < grid_str.length; i++) {
            GridInfo info = new GridInfo();
            info.setTitle(grid_str[i]);
            info.setRid(grid_img[i]);

            list.add(info);
        }

        UsercenterApater apaterGrid = new UsercenterApater(this, list);
        gv.setAdapter(apaterGrid);
        setListener();
    }

    private void setView() {
        Intent intent = getIntent();

        String shareStr = "";
        if (intent.hasExtra("share")) {
            share = intent.getIntExtra("share", 0);
        }
        switch (share) {
            case 0:
                shareStr = "我的收藏";
                grid_img = grid_collect_img;
                grid_str = grid_collect_str;
                share_layout.setVisibility(View.GONE);
                Util.initTitle(UserCenterShareActivity.this, shareStr, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                break;
            case 1:
                shareStr = "我的业务";
                grid_img = grid_business_img;
                grid_str = grid_business_str;
                share_layout.setVisibility(View.GONE);
                Util.initTitle(UserCenterShareActivity.this, shareStr, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                break;
            case 2:
                shareStr = "我要分享";
                grid_img = grid_share_img;
                grid_str = grid_share_str;
                gv.setVisibility(View.GONE);
                Util.initTitle(UserCenterShareActivity.this, shareStr, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null == popup) {
                            popup = new PopupWindow(v, Util.dpToPx(UserCenterShareActivity.this, 150F),
                                    android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
                            popup.setOutsideTouchable(true);
                            popup.setFocusable(true);
                            popup.setBackgroundDrawable(new BitmapDrawable());
                            View root = LayoutInflater.from(UserCenterShareActivity.this)
                                    .inflate(R.layout.user_center_share_popup, null);
                            TextView user = (TextView) root.findViewById(R.id.popup_share_user);
                            TextView mall = (TextView) root.findViewById(R.id.popup_share_mallApp);
                            TextView lmsj = (TextView) root.findViewById(R.id.popup_share_lmsjApp);
                            TextView cy6 = (TextView) root.findViewById(R.id.popup_share_cy6App);
                            user.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    userQRCode();
                                    popup.dismiss();
                                }
                            });
                            mall.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downMall();
                                    popup.dismiss();
                                }
                            });
                            lmsj.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    downLMSJ();
                                    popup.dismiss();
                                }
                            });

                            cy6.setOnClickListener(new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    // TODO Auto-generated method stub
                                    downCy6();
                                    popup.dismiss();
                                }
                            });
                            popup.setContentView(root);
                        }
                        popup.showAsDropDown(findViewById(R.id.right_text));
                    }
                }, "显示二维码");
                break;
            default:
                break;
        }
    }

    private void userQRCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertview = inflater.inflate(R.layout.personcard, null);
        ImageView person_card_view = (ImageView) alertview.findViewById(R.id.person_card);
        TextView show_text2 = (TextView) alertview.findViewById(R.id.show_text2);
        show_text2.setText("邀请【会员注册】");
        show_text2.setVisibility(View.VISIBLE);
        person_card_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downLoadImage("http://" + Web.webImage + "/phone/registe.aspx?inviter=" + UserData.getUser().getUtf8UserId(), "reg_" + UserData.getUser().getUtf8UserId());
                return false;
            }
        });
        try {
            createImage("http://" + Web.webImage + "/phone/registe.aspx?inviter=" + UserData.getUser().getUtf8UserId(),
                    person_card_view);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        dialog.setContentView(alertview);
    }

    // 生成二维码图片
    private void createImage(String url, ImageView iv) throws WriterException {
        int _300dp = Util.dpToPx(context, 300F);
        BitMatrix matrix = new MultiFormatWriter().encode(url, BarcodeFormat.QR_CODE, _300dp, _300dp);
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

    private void downMall() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertview = inflater.inflate(R.layout.personcard, null);
        ImageView person_card_view = (ImageView) alertview.findViewById(R.id.person_card);
        TextView show_text = (TextView) alertview.findViewById(R.id.show_text);
        TextView show_text2 = (TextView) alertview.findViewById(R.id.show_text2);
        show_text2.setText("下载【远大云商】");
        show_text2.setVisibility(View.VISIBLE);
        show_text.setText("微信不支持扫描下载哦，请使用QQ或其他软件扫描");
        try {
            createImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view", person_card_view);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        dialog.setCancelable(true);
        dialog.setContentView(alertview);
        dialog.show();
        person_card_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downLoadImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view", "mall");
                return false;
            }
        });
    }

    private void downLMSJ() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertview = inflater.inflate(R.layout.personcard, null);
        ImageView person_card_view = (ImageView) alertview.findViewById(R.id.person_card);
        TextView show_text = (TextView) alertview.findViewById(R.id.show_text);
        TextView show_text2 = (TextView) alertview.findViewById(R.id.show_text2);
        show_text2.setText("下载【远大商圈】");
        show_text2.setVisibility(View.VISIBLE);
        show_text.setText("微信不支持扫描下载哦，请使用QQ或其他软件扫描");
        try {
            createImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.YdAlainMall.alainmall2", person_card_view);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        dialog.setCancelable(true);
        dialog.setContentView(alertview);
        dialog.show();
        person_card_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downLoadImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.YdAlainMall.alainmall2", "alainmall");
                return false;
            }
        });
    }

    private void downCy6() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.show();
        LayoutInflater inflater = LayoutInflater.from(context);
        View alertview = inflater.inflate(R.layout.personcard, null);
        ImageView person_card_view = (ImageView) alertview.findViewById(R.id.person_card);
        TextView show_text = (TextView) alertview.findViewById(R.id.show_text);
        TextView show_text2 = (TextView) alertview.findViewById(R.id.show_text2);
        show_text2.setText("下载【远大创业】");
        show_text2.setVisibility(View.VISIBLE);
        show_text.setText("微信不支持扫描下载哦，请使用QQ或其他软件扫描");
        try {
            createImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.cy666.activity", person_card_view);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        dialog.setCancelable(true);
        dialog.setContentView(alertview);
        dialog.show();
        person_card_view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                downLoadImage("http://a.app.qq.com/o/simple.jsp?pkgname=com.cy666.activity", "cy666");
                return false;
            }
        });
    }

    private void setListener() {
        OnItemClickListener click = null;
        switch (share) {
            case 0:
                click = new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int p, long arg3) {
                        if (null == UserData.getUser()) {
                            Util.show("您还没登录，请先登录！", context);
                            return;
                        }
                        Util.showIntent(UserCenterShareActivity.this, grid_collect_cla[p]);

                    }
                };
                break;
            case 1:
                click = new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View arg1, int positon, long arg3) {

                        if (null == UserData.getUser()) {
                            Util.show("您还没登录，请先登录！", context);
                            return;
                        }

                        final User user = UserData.getUser();

                        int levelid = Integer.parseInt(user.getLevelId());
                        int shopTypeId = Integer.parseInt(user.getShopTypeId());

                        switch (positon) {
                            case 0:
                                Util.showIntent(context, MyHapplyLot.class);
                                break;
                            case 1:
                                if (!user.getIsSite().equals("0")) {
                                    Util.showIntent(context, MainActivity.class);  //我的业务到我的空间 跳转
                                } else {
                                    Util.show("对不起，此帐号不能进行此操作！", context);
                                }

                                break;
                        }

                    }
                };
                break;
            case 2:
                click = new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> arg0, View view, final int positon, long arg3) {
                        if (null == UserData.getUser()) {
                            Util.show("您还没登录，请先登录！", context);
                            return;
                        }

                        final User user = UserData.getUser();

                        int levelid = Integer.parseInt(user.getLevelId());
                        int shopTypeId = Integer.parseInt(user.getShopTypeId());

                        switch (positon) {
                            case 0:
                                if (user != null) {
                                    if (2 > Util.getInt(user.getLevelId())) {
                                        Util.show("您的会员等级不能分享会员。", context);
                                        return;
                                    }
                                    if ("6".equals(user.getLevelId())) {
                                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                                        return;
                                    }
                                    if (user.getMobilePhone().equals("")){
                                        Util.show("你的资料未完善,请先完善资料", context);
                                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                                        return;
                                    }
                                    final OnekeyShare oks = new OnekeyShare();
                                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum="
                                            + user.getUtf8UserId();
                                    final String title = "【远大创业】让你成功创业！";
                                    oks.setTitle(title);
                                    oks.setTitleUrl(url);
                                    oks.setUrl(url);
                                    oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher1.png?r=1");
                                    oks.setAddress("10086");
                                    oks.setComment("快来注册吧");
                                    oks.setTheme(OnekeyShareTheme.CLASSIC);
                                    oks.setText("一个让创业者喜欢什么行业就成功什么行业的手机APP，一个让创业者成为实体商家股东的手机APP…");
                                    oks.setSite(title);
                                    oks.setSilent(false);
                                    oks.setSiteUrl(url);
                                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                        @Override
                                        public void onShare(Platform platform, ShareParams paramsToShare) {
                                            if ("ShortMessage".equals(platform.getName())) {
                                                paramsToShare.setImageUrl(null);
                                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                                            }
                                        }
                                    });
                                    oks.show(context);
                                }
                                break;
                            case 1:
                                if (user != null) {
                                    if ("6".equals(user.getLevelId())) {
                                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                                        return;
                                    }
                                    if (Util.getInt(user.getShopTypeId()) >= 3) {

                                        if (user.getMobilePhone().equals("")){
                                            Util.show("你的资料未完善,请先完善资料", context);
                                            Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                                            return;
                                        }
                                        final OnekeyShare oks = new OnekeyShare();
                                        final String url = "http://" + Web.webImage + "/user/office/myOffices.aspx?unum="
                                                + user.getUserNo();
                                        final String title = "4G时代，邀请您用手机屏幕创业";
                                        oks.setTitle(title);
                                        oks.setTitleUrl(url);
                                        oks.setUrl(url);
                                        oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher1.png?r=1");
                                        oks.setAddress("10086");
                                        oks.setComment("快来注册吧");
                                        oks.setText("朋友你好！中国已进入4G时代，让你的手机也变成获取财富的工具吧，点击就可去我的创业空间……");
                                        oks.setSite("4G时代，邀请您用手机屏幕创业");
                                        oks.setSiteUrl(url);
                                        oks.setSilent(false);
                                        oks.setSite(title);
                                        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                            @Override
                                            public void onShare(Platform platform, ShareParams paramsToShare) {
                                                if ("ShortMessage".equals(platform.getName())) {
                                                    paramsToShare.setImageUrl(null);
                                                    paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                                                }
                                            }
                                        });

                                        oks.show(context);
                                    } else {
                                        Util.showIntent("对不起，您还没有创业空间，您可以前去申请创业空间!", context, "去申请", "再逛逛",
                                                new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Util.showIntent(context, ProxySiteFrame.class);
                                                        dialog.cancel();
                                                        dialog.dismiss();
                                                    }
                                                }, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.cancel();
                                                        dialog.dismiss();
                                                    }
                                                });
                                    }
                                }
                                break;
                            case 2:
                                Util.showIntent(context, StoreMainFrame.class);
                                break;
                            case 3:
                                Util.show("该功能正在开发中", context);
                                break;

                            case 4:
                                if (2 > Util.getInt(user.getLevelId())) {
                                    Util.show("您的会员等级不能分享云商App。", context);
                                } else {
                                    if (user.getMobilePhone().equals("")){
                                        Util.show("你的资料未完善,请先完善资料", context);
                                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                                        return;
                                    }
                                    OnekeyShare oks = new OnekeyShare();
                                    final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view";
                                    String title = "请您安装中国首家移动创业系统";
                                    oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher.png");
                                    oks.setTitle(title);
                                    oks.setTitleUrl(url);
                                    oks.setUrl(url);
                                    oks.setAddress("10086");
                                    oks.setComment("快来注册吧");
                                    oks.setText("朋友你好！中国已进入4G时代，我向你隆重推荐中国首家用手机屏幕创业系统，请点击本链接……");
                                    oks.setSite(title);
                                    oks.setSilent(false);
                                    oks.setSiteUrl(url);
                                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                        @Override
                                        public void onShare(Platform platform, ShareParams paramsToShare) {
                                            if ("ShortMessage".equals(platform.getName())) {
                                                paramsToShare.setImageUrl(null);
                                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                                            }
                                        }
                                    });
                                    oks.show(context);
                                }

                                break;
                            case 5:
                                if (2 > Util.getInt(user.getLevelId())) {
                                    Util.show("您的会员等级不能分享商家App。", context);
                                } else {
                                    if (user.getMobilePhone().equals("")){
                                        Util.show("你的资料未完善,请先完善资料", context);
                                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                                        return;
                                    }

                                    OnekeyShare oks = new OnekeyShare();
                                    final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.YdAlainMall.alainmall2";
                                    String title = "请您安装全国商圈客户管理系统";
                                    oks.setTitle(title);
                                    oks.setTitleUrl(url);
                                    oks.setImageUrl("http://app.yda360.com/phone/images/lmsj_icon.png");
                                    oks.setUrl(url);
                                    oks.setAddress("10086");
                                    oks.setComment("快来注册吧");
                                    oks.setText("老板好！中国已进入4G时代，我向你隆重推荐用手机屏幕锁定客户的超级大系统，请点击本链接……");
                                    oks.setSite(title);
                                    oks.setSilent(false);
                                    oks.setSiteUrl(url);
                                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                                        @Override
                                        public void onShare(Platform platform, ShareParams paramsToShare) {
                                            if ("ShortMessage".equals(platform.getName())) {
                                                paramsToShare.setImageUrl(null);
                                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                                            }
                                        }
                                    });
                                    oks.show(context);
                                }
                                break;
                        }
                    }
                };
                break;

            default:
                break;
        }
        gv.setOnItemClickListener(click);

        share_yunshang_vip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }


                final User user = UserData.getUser();

                if ("0".equals(UserData.getUser().getZoneId())) {
                    VoipDialog voipDialog = new VoipDialog("请完善资料，才具备推荐人资格", context, "去完善", "取消", new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.showIntent(context, UpdateUserMessageActivity.class);
                        }
                    }, null);
                    voipDialog.show();
                    return;
                }

                if (user != null) {
                    if (2 > Util.getInt(user.getLevelId())) {
                        Util.show("您的会员等级不能分享会员。", context);
                        return;
                    }
                    if ("6".equals(user.getLevelId())) {
                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                        return;
                    }
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    final OnekeyShare oks = new OnekeyShare();
                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum=" + user.getUtf8UserId()
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
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                            }
                        }
                    });
                    oks.show(context);
                }

            }
        });
        share_yunshang_app.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }

                final User user = UserData.getUser();
                if (2 > Util.getInt(user.getLevelId())) {
                    Util.show("您的会员等级不能分享云商App。", context);
                } else {
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    OnekeyShare oks = new OnekeyShare();
                    final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.mall.view";
                    String title = "请你安装【远大云商】手机APP";
                    oks.setImageUrl("http://app.yda360.com/phone/images/icon_mall.png");
                    oks.setTitle(title);
                    oks.setTitleUrl(url);
                    oks.setUrl(url);
                    oks.setAddress("10086");
                    oks.setComment("快来注册吧");
                    oks.setText("一个让消费者天天领红包的手机APP，一个让消费者终身有钱赚的手机APP，一个让消费者永远…");
                    oks.setSite(title);
                    oks.setSilent(false);
                    oks.setSiteUrl(url);
                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                            }
                        }
                    });
                    oks.show(context);
                }

            }
        });

        share_union_vip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }

                final User user = UserData.getUser();
                if (user != null) {
                    if (2 > Util.getInt(user.getLevelId())) {
                        Util.show("您的会员等级不能分享会员。", context);
                        return;
                    }
                    if ("6".equals(user.getLevelId())) {
                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                        return;
                    }
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    final OnekeyShare oks = new OnekeyShare();
                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum=" + user.getUtf8UserId()
                            + "&shareVersion=lmsj";
                    final String title = "【远大商圈】让客户主动来消费！";
                    oks.setTitle(title);
                    oks.setTitleUrl(url);
                    oks.setUrl(url);
                    oks.setImageUrl("http://app.yda360.com/phone/images/lmsj_icon.png");
                    oks.setAddress("10086");
                    oks.setComment("快来注册吧");
                    oks.setText("一个让客户主动来消费的手机APP，一个让生意天天火爆的手机APP,一个让商家与客户精准互动的手机APP…");
                    oks.setSite(title);
                    oks.setSilent(false);
                    oks.setSiteUrl(url);
                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                            }
                        }
                    });
                    oks.show(context);
                }

            }
        });
        share_union_app.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }

                final User user = UserData.getUser();

                if (2 > Util.getInt(user.getLevelId())) {
                    Util.show("您的会员等级不能分享商家App。", context);
                } else {
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    OnekeyShare oks = new OnekeyShare();
                    final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.YdAlainMall.alainmall2";
                    String title = "请你安装【远大商圈】手机APP";
                    oks.setTitle(title);
                    oks.setTitleUrl(url);
                    oks.setImageUrl("http://app.yda360.com/phone/images/lmsj_icon.png");
                    oks.setUrl(url);
                    oks.setAddress("10086");
                    oks.setComment("快来注册吧");
                    oks.setText("服务行业老板必用大数据收集神器。从此客户变数据，数据成财富。一个让数据永远为你赚钱的手机APP…");
                    oks.setSite(title);
                    oks.setSilent(false);
                    oks.setSiteUrl(url);
                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                            }

                        }
                    });
                    oks.show(context);
                }
            }
        });

        share_cy666_app.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }

                final User user = UserData.getUser();
                if (2 > Util.getInt(user.getLevelId())) {
                    Util.show("您的会员等级不能分享创业App。", context);
                } else {
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    OnekeyShare oks = new OnekeyShare();
                    final String url = "http://a.app.qq.com/o/simple.jsp?pkgname=com.cy666.activity";
                    String title = "请你安装【远大创业】手机APP";
                    oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher.png");
                    oks.setTitle(title);
                    oks.setTitleUrl(url);
                    oks.setUrl(url);
                    oks.setAddress("10086");
                    oks.setComment("快来注册吧");
                    oks.setText("一个让创业者不开公司，不办厂房，不租店铺，不开网店，不做微商同样成功创业的手机APP…");
                    oks.setSite(title);
                    oks.setSilent(false);
                    oks.setSiteUrl(url);
                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {

                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());

                            }
                        }
                    });
                    oks.show(context);
                }
            }
        });

        share_cy666_vip.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    return;
                }

                final User user = UserData.getUser();
                if (user != null) {
                    if (2 > Util.getInt(user.getLevelId())) {
                        Util.show("您的会员等级不能分享会员。", context);
                        return;
                    }
                    if ("6".equals(user.getLevelId())) {
                        Util.show("对不起，请登录您的城市总监账号在进行此操作！", context);
                        return;
                    }
                    if (user.getMobilePhone().equals("")){
                        Util.show("你的资料未完善,请先完善资料", context);
                        Util.showIntent(UserCenterShareActivity.this, UpdateUserMessageActivity.class);
                        return;
                    }
                    final OnekeyShare oks = new OnekeyShare();
                    final String url = "http://" + Web.webImage + "/phone/registe.aspx?unum=" + user.getUtf8UserId()
                            + "&shareVersion=cy666";
                    final String title = "【远大创业】让你成功创业！";
                    oks.setTitle(title);
                    oks.setTitleUrl(url);
                    oks.setUrl(url);
                    oks.setImageUrl("http://app.yda360.com/phone/images/ic_launcher.png");
                    oks.setAddress("10086");
                    oks.setComment("快来注册吧");
                    oks.setText("一个让创业者喜欢什么行业就成功什么行业的手机APP，一个让创业者成为实体商家股东的手机APP…");
                    oks.setSite(title);
                    oks.setSilent(false);
                    oks.setSiteUrl(url);
                    oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
                        @Override
                        public void onShare(Platform platform, ShareParams paramsToShare) {
                            if ("ShortMessage".equals(platform.getName())) {
                                paramsToShare.setImageUrl(null);
                                paramsToShare.setText(paramsToShare.getText() + "\n" + url.toString());
                            }
                        }
                    });
                    oks.show(context);
                }

            }
        });

    }

    class GridInfo {
        private String title;
        private int rid;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getRid() {
            return rid;
        }

        public void setRid(int rid) {
            this.rid = rid;
        }

    }

    class UsercenterApater extends NewBaseAdapter {
        public UsercenterApater(Context c, List list) {
            super(c, list);
        }

        @Override
        public View getView(int p, View arg1, ViewGroup arg2) {
            TextView tv = new TextView(context);

            GridInfo info = (GridInfo) list.get(p);
            tv.setText(info.getTitle());
            tv.setGravity(Gravity.CENTER);
            tv.setPadding(0, Util.dpToPx(context, 5), 0, Util.dpToPx(context, 5));
            tv.setTextSize(10);
            tv.setEllipsize(TruncateAt.END);
            tv.setSingleLine();
            tv.setTextColor(Color.BLACK);

            tv.setCompoundDrawablePadding(Util.dpToPx(context, 5));
            tv.setBackgroundResource(R.drawable.mybranch_dianji);

            tv.setCompoundDrawablesWithIntrinsicBounds(0, info.getRid(), 0, 0);
            return tv;
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void downLoadImage(final String str, final String name) {
        String[] items = new String[]{"保存"};
        DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        String SDState = Environment.getExternalStorageState();
                        if (SDState.equals(Environment.MEDIA_MOUNTED)) {
                            BitMatrix matrix = null;
                            try {
                                matrix = new MultiFormatWriter().encode(str,
                                        BarcodeFormat.QR_CODE, 300, 300);
                            } catch (WriterException e1) {
                                e1.printStackTrace();
                            }
                            if (null == matrix) {
                                Util.show("创建二维码文件失败!", UserCenterShareActivity.this);
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
                            Util.saveBitmapToSdCard(name + ".jpg", "远大二维码", erweimaBitmap);
                            Util.show("二维码已保存在：/sdcard/DCIM/远大二维码/" + name + ".jpg", UserCenterShareActivity.this);
                        } else {
                            Toast.makeText(UserCenterShareActivity.this, "内存卡不存在", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        };
        new AlertDialog.Builder(context).setItems(items, click).show().setCanceledOnTouchOutside(true);
    }

}
