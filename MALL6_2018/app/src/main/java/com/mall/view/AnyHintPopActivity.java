package com.mall.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.community.view.droidflakes.FlakeView;
import com.mall.util.UserData;
import com.mall.util.Util;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class AnyHintPopActivity extends BasicActivity {


    FlakeView flakeView;
    boolean isfristcheckredmoney=true;

    @Override
    public int getContentViewId() {
        setTheme(R.style.Transparent);
        return R.layout.activity_any_hint_pop;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {

    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            if (isfristcheckredmoney){
                openredmoneny();
                isfristcheckredmoney=false;
            }

        }
    }


    public void openredmoneny(){
        final View mContentView = LayoutInflater.from(context).inflate(R.layout.newpersonpopupwindow, null);
        final PopupWindow mPopUpWindow=  ShowPopWindow.showShareWindow_TransparentBackground(mContentView,context, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,R.style.popwin_pop_up_anim_style);

        View closepopwind=  mContentView.findViewById(R.id.close_popwind);  //获取。
        ImageView item_pop= (ImageView) mContentView.findViewById(R.id.item_pop);
        ImageView close_popwind_iv= (ImageView) mContentView.findViewById(R.id.close_popwind_iv);
        final ObjectAnimator animator = ObjectAnimator.ofFloat(item_pop,
                "rotationY", 0, 360);
        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();
                finish();
            }
        });
        closepopwind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animator.setDuration(1000).start();
                animator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mPopUpWindow.dismiss();
                        View mContentViewred = LayoutInflater.from(context).inflate(R.layout.randomredmoneyopenendpopupwindow, null);
                        final PopupWindow mPopUpWindowred=  ShowPopWindow.showShareWindow_TransparentBackground(mContentViewred,context, ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,R.style.popwin_pop_up_anim_style);
                        ImageView close_popwind_iv= (ImageView) mContentViewred.findViewById(R.id.close_popwind_iv);
                        LinearLayout container= (LinearLayout) mContentViewred.findViewById(R.id.container);
                        TextView invitefriend= (TextView) mContentViewred.findViewById(R.id.invitefriend);
                        TextView money= (TextView) mContentViewred.findViewById(R.id.money);
                        flakeView = new FlakeView(context, R.drawable.redpocket);
                        container.removeAllViews();
                        container.addView(flakeView);
                        flakeView.resume();
                        close_popwind_iv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                mPopUpWindowred.dismiss();
                                flakeView.pause();
                                finish();

                            }
                        });
                        invitefriend.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context,"邀请朋友",Toast.LENGTH_SHORT).show();

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
                                    oks.setText( getResources().getString(R.string.sharemessage));
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
        });
    }


}
