package com.mall;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.SharingAdapter;
import com.mall.model.SharingredMoneyInfo;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.RecyclerViewSpacesItemDecoration;
import com.mall.view.ShowPopWindow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;


@ContentView(R.layout.activity_sharingredmoney)
public class SharingredmoneyActivity extends Activity {

    @ViewInject(R.id.rediteam_recycle)
    public RecyclerView rediteam;

    @ViewInject(R.id.redmoney_number)
    TextView redmoney_number;

    SharingAdapter sharingAdapter;
    FlakeView flakeView;

    Context context;

    List<SharingredMoneyInfo.ListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;
        init();
    }


    @com.lidroid.xutils.view.annotation.event.OnClick({R.id.close})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.close:
                finish();
                break;
        }
    }

    private void init() {
        initAdapter();
        initdata();
    }

    private void initdata() {
        getSharingRedBoxList(true);
    }

    CustomProgressDialog cpd;

    public void getSharingRedBoxList(final boolean isshow) {

        if (isshow) {
            cpd = Util.showProgress("正在获取您的分享红包...", this);
        }

        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=get_inviter_red_box_list&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd() +
                        "&pageSize=" + 999 + "&page=" + 1 + "&state=" + 0
                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }
                        redmoney_number.setText("您分享的会员已成功注册，恭喜您获得" + json.getString("message") + "个现金红包!");

                        Gson gson = new Gson();
                        SharingredMoneyInfo sharingredMoneyInfo = gson.fromJson(result.toString(), SharingredMoneyInfo.class);

                        list.clear();
                        list.addAll(sharingredMoneyInfo.getList());
                        sharingAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestEnd() {
                        if (isshow) {
                            cpd.cancel();
                            cpd.dismiss();
                        }

                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }

    private void initAdapter() {
        sharingAdapter = new SharingAdapter(R.layout.sharingredmoney, list);
        //GridLayout 3列
        GridLayoutManager mgr = new GridLayoutManager(context, 3);
        rediteam.setLayoutManager(mgr);
        //设置item间距，30dp

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();
        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//top间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//底部间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 20);//左间距

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 20);//右间距

        rediteam.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        sharingAdapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);

        rediteam.setAdapter(sharingAdapter);

        sharingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
//                Toast.makeText(context,"item点击了",Toast.LENGTH_SHORT).show();

                final SharingredMoneyInfo.ListBean listBean = list.get(position);

                View mContentViewred = LayoutInflater.from(context).inflate(R.layout.randomredmoneypopupwindow, null);
                final PopupWindow mPopUpWindowred = ShowPopWindow.showShareWindow(mContentViewred, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);
                View closepopwind_red = mContentViewred.findViewById(R.id.close_popwind);  //获取
                ImageView item_pop = (ImageView) mContentViewred.findViewById(R.id.item_pop);
                ImageView close_popwindred_iv = (ImageView) mContentViewred.findViewById(R.id.close_popwind_iv);
                final ObjectAnimator animator = ObjectAnimator.ofFloat(item_pop,
                        "rotationY", 0, 360);

                closepopwind_red.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        final CustomProgressDialog cpd = Util.showProgress("正在领取红包...", context);
                        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=OP_inviter_red_box&userId="
                                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                                        + "&ly_user=" + listBean.getRedbox_ly() + "&id=" + listBean.getId()
                                ,
                                new NewWebAPIRequestCallback() {

                                    @Override
                                    public void timeout() {
                                        Util.show("网络超时！", context);
                                        return;
                                    }

                                    @Override
                                    public void success(Object result) {

                                        if (Util.isNull(result)) {
                                            Util.show("网络异常，请重试！", context);
                                            return;
                                        }
                                        final JSONObject json = JSON.parseObject(result.toString());
                                        if (200 != json.getIntValue("code")) {
                                            Util.show(json.getString("message"), context);
                                            return;
                                        }

                                        getSharingRedBoxList(false);

                                        animator.setDuration(1000).start();

                                        animator.addListener(new Animator.AnimatorListener() {
                                            @Override
                                            public void onAnimationStart(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationEnd(Animator animation) {
                                                mPopUpWindowred.dismiss();
                                                View mContentViewred = LayoutInflater.from(context).inflate(R.layout.randomredmoneyopenendpopupwindow, null);
                                                final PopupWindow mPopUpWindowred = ShowPopWindow.showShareWindow(mContentViewred, context, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, R.style.popwin_pop_up_anim_style);
                                                ImageView close_popwind_iv = mContentViewred.findViewById(R.id.close_popwind_iv);
                                                LinearLayout container = mContentViewred.findViewById(R.id.container);
                                                TextView money_tv = mContentViewred.findViewById(R.id.money);
                                                money_tv.setText(json.getString("message") + "元");
                                                TextView invitefriend = mContentViewred.findViewById(R.id.invitefriend);
                                                flakeView = new FlakeView(context, R.drawable.redpocket);
                                                container.removeAllViews();
                                                container.addView(flakeView);
                                                flakeView.resume();
                                                close_popwind_iv.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {
                                                        mPopUpWindowred.dismiss();
                                                        flakeView.pause();
                                                    }
                                                });
                                                invitefriend.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View v) {

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
                                                                public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
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

                                            @Override
                                            public void onAnimationCancel(Animator animation) {

                                            }

                                            @Override
                                            public void onAnimationRepeat(Animator animation) {

                                            }
                                        });

                                    }

                                    @Override
                                    public void requestEnd() {
                                        cpd.cancel();
                                        cpd.dismiss();
                                    }

                                    @Override
                                    public void fail(Throwable e) {

                                    }
                                });


                    }
                });
                close_popwindred_iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopUpWindowred.dismiss();
                    }
                });
            }
        });

    }
}
