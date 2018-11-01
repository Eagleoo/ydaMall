package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.MyFragmentPagerAdapter.BaseFragmentAdapter;
import com.mall.model.BusinessCircleCityName;
import com.mall.model.BusinessMessage;
import com.mall.model.User;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CircleImageView;
import com.mall.util.Data;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.LoginFrame;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.UpdateUserMessageActivity;
import com.mall.view.messageboard.MyToast;
import com.mall.view.messageboard.WriterNewMessageBoardFrame;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import zhy.com.highlight.HighLight;
import zhy.com.highlight.position.OnBottomPosCallback;
import zhy.com.highlight.shape.RectLightShape;


@ContentView(R.layout.activity_business_circle)
public class BusinessCircleActivity extends AppCompatActivity {


    @ViewInject(R.id.userheadportrait_cv)
    private CircleImageView userheadportrait;
    @ViewInject(R.id.tomessage_mtv)
    private MoreTextView tomessage_mtv;


    @ViewInject(R.id.areaname_tv)
    private TextView areaname;

    @ViewInject(R.id.reqi_tv)
    private TextView reqi;

    @ViewInject(R.id.xinxi_tv)
    private TextView xinxi;

    @ViewInject(R.id.areaheadportrait_cv)
    CircleImageView areaheadportrait_cv;

    @ViewInject(R.id.viewpager)
    android.support.v4.view.ViewPager vpContent;

    @ViewInject(R.id.myifo_rl)
    RelativeLayout myifo_rl;

    @ViewInject(R.id.postedcommentaries_rl)
    RelativeLayout postedcommentaries_rl;

    @ViewInject(R.id.guanzhu_rl)
    RelativeLayout guanzhu_rl;

    private Context context;

    private User user;

    private String zoneid = "";

    private String circlename = "";

    int amount = 0;

    CustomProgressDialog dialog;

    boolean swich = false;

    List<Fragment> mFragments;

    BusinessCirleListFragment listFragment;

    String[] mTitles = new String[]{
            "主页"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        ViewUtils.inject(this);

        //注册事件
        EventBus.getDefault().register(this);
        init();
    }

    private void init() {
        setupViewPager();
        initdata();
    }

    private void setupViewPager() {

        mFragments = new ArrayList<>();
        listFragment = BusinessCirleListFragment.newInstance();
        mFragments.add(listFragment);

        BaseFragmentAdapter adapter =
                new BaseFragmentAdapter(context, getSupportFragmentManager(), mFragments, mTitles);

        vpContent.setAdapter(adapter);
    }


    boolean isfrist = true;


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.e("onWindowFocusChanged1", "isfrist" + isfrist);
        if (isfrist) {
            Log.e("onWindowFocusChanged2", "isfrist" + isfrist);
            isfrist = false;
            Log.e("onWindowFocusChanged3", "isfrist" + isfrist);

            if (getSharedPreferences("firstguiding", MODE_PRIVATE).getBoolean("first", true)) {
                setguiding();
                getSharedPreferences("firstguiding", MODE_PRIVATE).edit().putBoolean("first", false).commit();
            }

        }

    }

    HighLight mHightLight;

    private void setguiding() {

        Toast toast = Toast.makeText(context, "拓人脉、找资源、畅所欲言商圈满足你的需要", Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        mHightLight = new HighLight(context)//

                .autoRemove(true)//设置背景点击高亮布局自动移除为false 默认为true
                .intercept(true)//设置拦截属性为false 高亮布局不影响后面布局的滑动效果 而且使下方点击回调失效
                .setClickCallback(new HighLight.OnClickCallback() {
                    @Override
                    public void onClick() {
                        remove(null);
                    }
                })
                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
                .addHighLight(R.id.myifo_vi, R.layout.info_known1, new OnBottomPosCallback(2)
                        , new RectLightShape(0, 0, 0, Util.dpToPx(context, 15), Util.dpToPx(context, 15)))
                .addHighLight(R.id.postedcommentaries_vi, R.layout.info_known2, new OnBottomPosCallback(2)
                        , new RectLightShape(0, 0, 0, Util.dpToPx(context, 15), Util.dpToPx(context, 15)))
                .addHighLight(R.id.guanzhu_vi, R.layout.info_known3, new OnBottomPosCallback(2)
                        , new RectLightShape(0, 0, 0, Util.dpToPx(context, 15), Util.dpToPx(context, 15)));


        mHightLight.show();

//        mHightLight = new HighLight(BusinessCircleActivity.this)//
//                .anchor(findViewById(R.id.id_container))//如果是Activity上增加引导层，不需要设置anchor
//                .autoRemove(false)
//                .enableNext()
//                .setOnLayoutCallback(new HighLightInterface.OnLayoutCallback() {
//                    @Override
//                    public void onLayouted() {
//                        //界面布局完成添加tipview
//
//                        mHightLight.addHighLight(R.id.myifo_vi,R.layout.info_known1,new OnBottomPosCallback(2)
//                                ,new RectLightShape(0,0,0,Util.dpToPx(context,15),Util.dpToPx(context,15)))
//                                .addHighLight(R.id.postedcommentaries_vi,R.layout.info_known1,new OnBottomPosCallback(2)
//                                        ,new RectLightShape(0,0,0,Util.dpToPx(context,15),Util.dpToPx(context,15)))
//                                .addHighLight(R.id.guanzhu_vi,R.layout.info_known1,new OnBottomPosCallback(2)
//                                        ,new RectLightShape(0,0,0,Util.dpToPx(context,15),Util.dpToPx(context,15)));
//                        //然后显示高亮布局
//                        mHightLight.show();
//                    }
//                })
//                .setClickCallback(new HighLight.OnClickCallback() {
//                    @Override
//                    public void onClick() {
//                        Toast.makeText(BusinessCircleActivity.this, "clicked and show next tip view by yourself", Toast.LENGTH_SHORT).show();
//                        mHightLight.next();
//                    }
//                });

    }


    public void clickKnown(View view) {
        if (mHightLight.isShowing() && mHightLight.isNext())//如果开启next模式
        {
            mHightLight.next();
        } else {
            remove(null);
        }
    }

    public void remove(View view) {
        mHightLight.remove();
    }


    private void initdata() {
        //初始化头像
        InitializeHead();
        //获取未读消息
        getUnReaderComment();


    }



    private void InitializeHead() {
        user = UserData.getUser();
        try {
            Log.e("用户头像", user.getUserFace() + "KK");
            Picasso.with(context).load(user.getUserFace()).into(userheadportrait);
        } catch (Exception e) {
            Log.e("加载用户头像失败", e.toString());

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //取消注册事件
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        getUnReaderComment();

        //获得城市
        if (Util.isSelectCity) {

            getAllUserMessagecity();
        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(BusinessMessage messageEvent) {

        if (messageEvent != null) {
            Log.e("EventBus"+getClass(), "回调" + messageEvent.getMessage());
            if (messageEvent.getMessage().equals("Yes")) {
                Refresh();
                MyToast.makeText(App.getActivity(), "动态发布成功", 5)
                        .show();
            } else if (messageEvent.getMessage().equals("deletseccess")) {

                Refresh();
            }
        }

    }

    private void Refresh() {
        if (listFragment != null) {
            listFragment.refresh(zoneid, circlename);
        }

    }


    @OnClick({R.id.switcharea_ll, R.id.userheadportrait_cv, R.id.tomessage_mtv, R.id.postedcommentaries_rl,
            R.id.guanzhu_rl, R.id.myifo_rl})
    public void cliclk(View view) {
        Intent intent = null;
        switch (view.getId()) {

            case R.id.switcharea_ll:
                intent = new Intent(context, SwithAreaActivity.class);
                startActivityForResult(intent, 100);
                break;
            case R.id.userheadportrait_cv:

                if (null != user) {
                    intent = new Intent(context, BusinessPersonSpaceActivity.class);
                    intent.putExtra("userId", user.getNoUtf8UserId());
                    intent.putExtra("userface", user.getUserFace());
                    intent.putExtra("isUser",true);
                    intent.putExtra("zoneid",zoneid);
                    this.startActivity(intent);
                } else {
                    Util.showIntent("对不起，请先登录！", this, LoginFrame.class);
                }
                break;
            case R.id.postedcommentaries_rl:

                if (!Util.checkUserInfocomplete()){
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



                intent = new Intent(context, WriterNewMessageBoardFrame.class);
                intent.putExtra("zoneid", zoneid);
                startActivity(intent);
                break;
            case R.id.guanzhu_rl:
                intent = new Intent(context, MessageListActivity.class);
                intent.putExtra("title", "关注列表");
                startActivity(intent);
                break;
            case R.id.tomessage_mtv:
            case R.id.myifo_rl:

                Log.e("amount", amount + "LLL");

                if (amount == 0) {
                    Toast.makeText(context, "未有未读消息", Toast.LENGTH_SHORT).show();
                    return;
                }

                intent = new Intent(context, MessageListActivity.class);
                intent.putExtra("title", "信息列表");
                startActivity(intent);
                break;
        }

    }

    NoLeakHandler handler=new NoLeakHandler(this);

    private static class NoLeakHandler extends Handler{
        private WeakReference<BusinessCircleActivity> mActivity;
        public NoLeakHandler(BusinessCircleActivity activity){
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BusinessCircleActivity businessCircleActivity=  mActivity.get();
            switch (msg.what) {
                case 1234:
                    CustomProgressDialog dialog=  businessCircleActivity.dialog;
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    businessCircleActivity.zoneid = businessCircleActivity.business.getZoneid();

                    Log.e("图片12312", "zoneid" + businessCircleActivity.business.getZoneid());

                    try {
                        Log.e("图片地址KKK1", Util.getCityPicUrl(businessCircleActivity.business.getZoneid()));
                        Picasso.with(businessCircleActivity).load(Util.getCityPicUrl(businessCircleActivity.business.getZoneid())).error(R.drawable.ic_launcher).into(businessCircleActivity.areaheadportrait_cv);

                    } catch (Exception e) {

                    }

                    businessCircleActivity.circlename = businessCircleActivity.business.getSq_name().replace("省", "").replace("-", "").replace("市", "");
                    businessCircleActivity.areaname.setText(businessCircleActivity.circlename + "圈");
                    businessCircleActivity.xinxi.setText("信息" + businessCircleActivity.business.getCount_());

                    businessCircleActivity.reqi.setText("人气" + businessCircleActivity.business.getRenqi());
                    if (businessCircleActivity.listFragment != null) {
                        businessCircleActivity.listFragment.
                                refresh(businessCircleActivity.zoneid, businessCircleActivity.circlename);
                    }
                    break;
                case 5678:
                    businessCircleActivity.areaname.setText(businessCircleActivity.city + "圈");
                    businessCircleActivity.xinxi.setText("信息");

                    businessCircleActivity.reqi.setText("人气");
                    businessCircleActivity.zoneid = Util.zoneid;
                    try {
                        Log.e("图片地址KKK2", Util.getCityPicUrl(businessCircleActivity.zoneid));
                        Picasso.with(businessCircleActivity).load(Util.getCityPicUrl(businessCircleActivity.zoneid)).error(R.drawable.ic_launcher).into(businessCircleActivity.areaheadportrait_cv);

                    } catch (Exception e) {

                    }
                    businessCircleActivity.listFragment.
                            refresh(Util.zoneid, businessCircleActivity.city);
                    businessCircleActivity.dialog.dismiss();
                    break;
            }
        }


    }



    BusinessCircleCityName business;
    String city = "";

    public void getAllUserMessagecity() {
        dialog = Util.showProgress("城市加载中", context);
        dialog.show();
        new Thread() {
            @Override
            public void run() {
                super.run();

//                String city = getSharedPreferences("city", MODE_PRIVATE)
//                        .getString("city", "深圳市");
                city = Util.getCityStr();
                Log.e("现在的城市", city + "UU");
                Util.isSelectCity = false;
                final ArrayList<BusinessCircleCityName> list1 = Data.getCityid(context, true);
                for (int i = 0; i < list1.size(); i++) {
                    BusinessCircleCityName businessCircleCityName = list1.get(i);

                    if (businessCircleCityName.getSq_name().indexOf(city) != -1) {
                        business = businessCircleCityName;
                        handler.sendEmptyMessage(1234);
                        return;
                    }
                }
                if (dialog != null) {
                    handler.sendEmptyMessage(5678);
                }
            }
        }.start();

    }

    private void getUnReaderComment() {

        if (null != UserData.getUser()) {
            String userId = UserData.getUser().getUserId();
            String md5Pwd = UserData.getUser().getMd5Pwd();
            com.mall.net.NewWebAPI.getNewInstance().getUnReadMoodCommentCount(userId, md5Pwd, new WebRequestCallBack() {
                @Override
                public void success(Object result) {
                    if (null == result) {
                        Util.show("网络异常，请稍后...", context);
                        return;
                    }
                    JSONObject obj = JSON.parseObject(result.toString());
                    if (200 == obj.getIntValue("code")) {
                        amount = obj.getIntValue("message");

                        String str = "";
                        if (amount > 10) {
                            str = "9+";
                        } else {
                            str = amount + "";
                        }

                        tomessage_mtv.setText(Html.fromHtml("我的信息<font color=\"#FF4462\">(" + str + ")</font>"));
                        if (amount == 0) {
                            tomessage_mtv.setText(Html.fromHtml("我的消息"));
                        }

                    } else {
                        Util.show(obj.getString("message"), context);
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        String circlenamestr = "";
        String xinxistr = "";
        String renqistr = "";

        if (resultCode == 100) {
            zoneid = data.getStringExtra("zoneid");
            circlenamestr = data.getStringExtra("name");
            xinxistr = data.getStringExtra("xinxi");
            renqistr = data.getStringExtra("renqi");
        }

        Log.e("选择信息", "zoneid" + zoneid + "circlename" + circlenamestr + "xinxi" + xinxistr + "renqi" + renqistr);
        if (!Util.isNull(circlenamestr)){
            circlename = circlenamestr.replace("省", "").replace("-", "").replace("市", "");
            areaname.setText(circlename + "圈");
            xinxi.setText("信息" + xinxistr);
            reqi.setText("人气" + renqistr);
        }

        try {
            Picasso.with(context).load(Util.getCityPicUrl(zoneid)).error(R.drawable.ic_launcher).into(areaheadportrait_cv);
        } catch (Exception e) {

        }
        if (listFragment != null) {
            listFragment.refresh(zoneid, circlename);
        }

    }

}
