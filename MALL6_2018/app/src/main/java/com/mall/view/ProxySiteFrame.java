package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

/**
 * 功能： 申请加盟<br> 我的业务申请
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class ProxySiteFrame extends Activity {

    @ViewInject(R.id.proxy_shopsite_myjiaose)
    private TextView proxy_shopsite_myjiaose;
    @ViewInject(R.id.proxy_shopsite_jyjiaose)
    private TextView proxy_shopsite_jyjiaose;
    @ViewInject(R.id.proxy_site_updateProxy)
    private LinearLayout proxy_site_updateProxy;
    @ViewInject(R.id.proxy_site_site)
    private LinearLayout proxy_site_site;
    @ViewInject(R.id.proxy_site_vip)
    private LinearLayout proxy_site_vip;
    @ViewInject(R.id.proxy_site_card)
    private LinearLayout proxy_site_card;
    @ViewInject(R.id.proxy_site_lmsj)
    private LinearLayout proxy_site_lmsj;
    @ViewInject(R.id.proxy_site_qu)
    private LinearLayout proxy_site_qu;
    @ViewInject(R.id.shengji_site_site)
    private LinearLayout shengji_site_site;
    @ViewInject(R.id.proxy_site_reg)
    private LinearLayout proxy_site_reg;

    @ViewInject(R.id.proxy_site_site1)
    private LinearLayout proxy_site_site1;//申请幸福大中华城市经理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.proxy_shopsite);
        ViewUtils.inject(this);
        Util.initTitle(this, "我的业务申请", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != UserData.getUser())
                    Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                else
                    Util.showIntent("对不起，您还未登录！", ProxySiteFrame.this, LoginFrame.class);
            }
        }, "申请记录");
        proxy_site_updateProxy.setVisibility(View.GONE);
        User user = UserData.getUser();
        if (null != user) {
            int level = Util.getInt(user.getLevelId());
            int shop = Util.getInt(user.getShopTypeId());
            proxy_shopsite_myjiaose.setText("会员角色：" + user.getUserLevel());
            proxy_shopsite_jyjiaose.setText("系统角色：" + user.getShowLevel());

            if ((2 == level && 3 <= shop && 10 >= shop) || (5 == level || 6 == level || level == 8 || level == 9)) {
                proxy_site_vip.setVisibility(View.GONE); // 申请VIP
                proxy_site_site.setVisibility(View.GONE); // 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE); // 申请城市经理众筹
                //proxy_site_card.setVisibility(View.GONE); // 申请购物卡
                proxy_site_lmsj.setVisibility(View.GONE); // 申请联盟商家
                //proxy_site_student.setVisibility(View.GONE); // 申请大学生空间
                //proxy_site_move.setVisibility(View.GONE); // 申请移动创客
                shengji_site_site.setVisibility(View.GONE); // 申升级城市经理（主要用于移动创客）
                proxy_site_updateProxy.setVisibility(View.VISIBLE); // 升级城市总监
                proxy_site_qu.setVisibility(View.GONE); // 申请城市总监
                if (4 == level || 5 == level || 6 == level || 8 == level) {
                    //setting_business_cyds.setVisibility(View.GONE);
                    proxy_site_updateProxy.setVisibility(View.GONE);
                    //setting_business_cyds.setVisibility(View.GONE); // 申请创大使
                    proxy_site_updateProxy.setVisibility(View.GONE); // 升级城市总监
                }
                if (10 == shop && (user.getShowLevel().contains("城市") || user.getShowLevel().contains("经理")))
                    proxy_site_updateProxy.setVisibility(View.VISIBLE); // 升级城市总监
                else
                    proxy_site_updateProxy.setVisibility(View.GONE); // 升级城市总监
            }
            if (level == 9) {
                shengji_site_site.setVisibility(View.VISIBLE);
                //proxy_site_city_zc.setVisibility(View.GONE);
                proxy_site_qu.setVisibility(View.GONE);
            }
        }

        if (null != user) {
            if ("普通会员".equals(user.getShowLevel())) {
                //show_next.setVisibility(View.VISIBLE);
                proxy_site_site.setVisibility(View.VISIBLE);// 申请城市经理
                proxy_site_qu.setVisibility(View.VISIBLE);// 申请〔城市总监〕
                proxy_site_lmsj.setVisibility(View.VISIBLE);// 申请〔商圈系统〕
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                proxy_site_card.setVisibility(View.GONE);// 申请【远大购物卡】
                //setting_business_cyds.setVisibility(View.GONE);// 创业大使
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
            } else if ("城市经理".equals(user.getShowLevel())) {

                //setting_business_cyds.setVisibility(View.VISIBLE);// 创业大使
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                proxy_site_updateProxy.setVisibility(View.VISIBLE);// 申请〔升级城市总监〕
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                //show_next.setVisibility(View.GONE);
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理
                //jianxijuese.setVisibility(View.GONE);
            } else if (user.getShowLevel().contains("城市总监")) {
                // ("城市总监".equals(user.getShowLevel()))
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                //show_next.setVisibility(View.GONE);
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                //setting_business_cyds.setVisibility(View.GONE);
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
                //jianxijuese.setVisibility(View.GONE);
            } else if ("联盟商家".equals(user.getShowLevel())) {

                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //setting_business_cyds.setVisibility(View.GONE);// 创业大使
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕

            } else if ("移动创客".equals(user.getShowLevel())) {
                shengji_site_site.setVisibility(View.VISIBLE);// 申请〔升级城市经理〕
                //setting_business_cyds.setVisibility(View.VISIBLE);// 创业大使
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕

            } else if ("创业大使".equals(user.getShowLevel())) {
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                //setting_business_cyds.setVisibility(View.GONE);// 创业大使
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
            } else if ("联盟商家[城市经理]".equals(user.getShowLevel())) {
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                //setting_business_cyds.setVisibility(View.VISIBLE);// 创业大使
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                proxy_site_updateProxy.setVisibility(View.VISIBLE);// 申请〔升级城市总监〕

            } else if ("见习创客".equals(user.getShowLevel())) {
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                // proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                //setting_business_cyds.setVisibility(View.GONE);// 创业大使
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                //jianxijuese.setVisibility(View.GONE);
                proxy_site_reg.setVisibility(View.GONE);
            } else if (user.getShowLevel().contains("大学生")) {
                proxy_site_vip.setVisibility(View.GONE);// vip会员
                proxy_site_card.setVisibility(View.VISIBLE);// 申请【远大购物卡】
                //setting_business_cyds.setVisibility(View.GONE);// 创业大使
                proxy_site_qu.setVisibility(View.GONE);// 申请〔城市总监〕
                proxy_site_site.setVisibility(View.GONE);// 申请城市经理
                //proxy_site_city_zc.setVisibility(View.GONE);// 申请〔城市经理(众筹)
                proxy_site_lmsj.setVisibility(View.GONE);// 申请〔商圈系统〕
                //show_next.setVisibility(View.GONE);
                //proxy_site_student.setVisibility(View.GONE);// //申请【大学生空间】
                //proxy_site_move.setVisibility(View.GONE);// 申请〔移动创客〕
                shengji_site_site.setVisibility(View.GONE);// 申请〔升级城市经理〕
                proxy_site_updateProxy.setVisibility(View.GONE);// 申请〔升级城市总监〕
                //jianxijuese.setVisibility(View.GONE);
                proxy_site_reg.setVisibility(View.GONE);
            }
        } else {
            Util.showIntent(ProxySiteFrame.this, LoginFrame.class);
            finish();
            return;
        }

        if (null != user) {
            int level = Util.getInt(user.getLevelId());
            int shop = Util.getInt(user.getShopTypeId());
            if (4 == level || 5 == level || 6 == level) {
                proxy_site_updateProxy.setVisibility(View.GONE);
                //setting_business_cyds.setVisibility(View.GONE); // 申请创大使
                proxy_site_updateProxy.setVisibility(View.GONE); // 升级城市总监
                proxy_site_site.setVisibility(View.GONE);  //申请城市经理
                proxy_site_qu.setVisibility(View.GONE);  //申请城市总监
                proxy_site_lmsj.setVisibility(View.GONE); // 申请联盟商家
            }
        }

    }

    @OnClick(R.id.proxy_site_reg)
    public void regClick(View v) {
        boolean isLogin = (null != UserData.getUser());
        if (isLogin) {
            String[] keys = new String[]{"inviter"};
            String[] values = new String[]{""};
            User user = UserData.getUser();
            values[0] = user.getUserId();
            Util.showIntent(this, RegisterFrame.class, keys, values);
        } else
            Util.showIntent(this, RegisterFrame.class);
    }

    @OnClick(R.id.shengji_site_site)
    public void shengjiClick(View v) {
        if (null != UserData.getUser()) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }



            Util.showIntent(ProxySiteFrame.this, MoveGuestUpFrame.class);
        } else {
            Util.showIntent("请先登录之后在申请升级城市经理", ProxySiteFrame.this, LoginFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_site1)
    public void siteClick1(View v) {
        if (null != UserData.getUser()) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }




            int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
            if (-1 < shopTypeId && shopTypeId != 10) {
                Log.e("角色等级", UserData.getUser().getShopType());

                if (UserData.getUser().getShopType().equals("城市经理")) {
                    Util.show("您已经是【" + "城市经理" + "】，\n无须重复申请。", ProxySiteFrame.this);
                } else {
                    Util.show("您是【" + UserData.getUser().getShopType() + "】经理，\n不需要再重复申请城市经理。", ProxySiteFrame.this);
                }

            } else if (10 == shopTypeId) {
                Util.show("您是【联盟商家】，\n不需要再申请城市经理。", ProxySiteFrame.this);
            } else if (UserData.getUser().getLevel().contains("代理")) {
                Util.show(
                        "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不需要再申请城市经理。",
                        ProxySiteFrame.this);
            } else {
                Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData)) {
                            Intent intent = new Intent();
                            intent.setClass(ProxySiteFrame.this, SiteFrame.class);
                            intent.putExtra("className", SiteFrame.class.toString());
                            intent.putExtra("type", "2680");
                            ProxySiteFrame.this.startActivity(intent);
                        } else {
                            Util.show(runData + "", ProxySiteFrame.this);
                            String key = "1111";
                            if ("您已提交城市总监申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "qddl";
                            else if ("您已提交创业大使申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "cyds";
                            Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.isSite, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd());
                        return web.getPlan();
                    }
                });
            }
        } else {
            Util.showIntent("请先登录之后再申请城市经理", ProxySiteFrame.this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_site)
    public void siteClick(View v) {
        if (null != UserData.getUser()) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }



            int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
            if (-1 < shopTypeId && shopTypeId != 10) {
                Util.show("您是【" + UserData.getUser().getShopType() + "】经理，\n不需要再重复申请城市经理。", ProxySiteFrame.this);
            } else if (10 == shopTypeId) {
                Util.show("您是【联盟商家】，\n不需要再申请城市经理。", ProxySiteFrame.this);
            } else if (UserData.getUser().getLevel().contains("代理")) {
                Util.show(
                        "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不需要再申请城市经理。",
                        ProxySiteFrame.this);
            } else {
                Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData)) {
                            Intent intent = new Intent();
                            intent.setClass(ProxySiteFrame.this, SiteFrame.class);
                            intent.putExtra("className", SiteFrame.class.toString());
                            ProxySiteFrame.this.startActivity(intent);
                        } else {
                            Util.show(runData + "", ProxySiteFrame.this);
                            String key = "1111";
                            if ("您已提交城市总监申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "qddl";
                            else if ("您已提交创业大使申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "cyds";
                            Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.isSite, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd());
                        return web.getPlan();
                    }
                });
            }
        } else {
            Util.showIntent("请先登录之后再申请城市经理", ProxySiteFrame.this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_card)
    public void cardClick(View v) {
        if (null != UserData.getUser()) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }




            Intent intent = new Intent();
            intent.setClass(ProxySiteFrame.this, RequestShopCardFrame.class);
            intent.putExtra("className", RequestShopCardFrame.class.toString());
            ProxySiteFrame.this.startActivity(intent);
        } else {
            Util.showIntent("请先登录之后再申请购物卡", ProxySiteFrame.this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_lmsj)
    public void lmsjClick(View v) {
        if (null != UserData.getUser()) {

            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }




            int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
            if (UserData.getUser().getShopType().contains("店") || UserData.getUser().getShopType().contains("级")) {
                Util.show("您是【" + UserData.getUser().getShopType() + "】经理，\n不能再申请联盟商家。", ProxySiteFrame.this);
            } else if (10 == shopTypeId) {
                Util.show("您是【联盟商家】，\n不需要再申请联盟商家。", ProxySiteFrame.this);
            } else if (UserData.getUser().getLevel().contains("代理")) {
                Util.show(
                        "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不需要再申请联盟商家。",
                        ProxySiteFrame.this);
            } else {
                Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData)) {
                            Intent intent = new Intent();
                            intent.setClass(ProxySiteFrame.this, RequestAllianceFrame.class);
                            intent.putExtra("className", ProxySiteFrame.class.toString());
                            ProxySiteFrame.this.startActivity(intent);
                        } else {
                            Util.show(runData + "", ProxySiteFrame.this);
                            String key = "1111";
                            if ("您已提交城市总监申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "qddl";
                            else if ("您已提交创业大使申请，请撤消后再申请新业务。".equals(runData + ""))
                                key = "cyds";
                            Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.isSite, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd());
                        return web.getPlan();
                    }
                });
            }
        } else {
            Util.showIntent("请先登录之后再申请联盟商家", this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_qu)
    public void proxyQuClick(View v) {
        if (null != UserData.getUser()) {

            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }



            int shopTypeId = Util.getInt(UserData.getUser().getShopTypeId());
            int levelId = Util.getInt(UserData.getUser().getLevelId());
            if (10 == shopTypeId) {
                Util.show("您是【联盟商家】，\n不能再申请渠道代理。", this);
            } else if (5 == levelId || 6 == levelId) {
                Util.show(
                        "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不能申请其他渠道代理。",
                        this);
            } else {
                Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        String message = runData.toString();
                        if ("success".equals(runData)) {
                            Intent intent = new Intent();
                            intent.setClass(ProxySiteFrame.this, RequestCityCenterFrame.class);
                            intent.putExtra("className", RequestCityCenterFrame.class.toString());
                            intent.putExtra("titleName", "城市总监");
                            intent.putExtra("level", "5");
                            ProxySiteFrame.this.startActivity(intent);
                        } else {
                            Util.show(runData + "", ProxySiteFrame.this);
                            String key = "qddl";
                            if ("您已提交创业大使申请，请撤消后再申请新业务。".equals(runData + "")) {
                                key = "cyds";
                            }
                            Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.isProxy, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd());
                        return web.getPlan();
                    }
                });
            }
        } else {
            Util.showIntent("请先登录之后再申请渠道代理", this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_shi)
    public void proxyShiClick(View v) {
        if (null != UserData.getUser()) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(ProxySiteFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }



            int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
            if (10 == shopTypeId) {
                Util.show("您是【联盟商家】，\n不能再申请渠道代理。", this);
            } else if (UserData.getUser().getLevel().contains("代理")) {
                Util.show(
                        "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不能申请其他渠道代理。",
                        this);
            } else {
                Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData)) {
                            Intent intent = new Intent();
                            intent.setClass(ProxySiteFrame.this, RequestCityCenterFrame.class);
                            intent.putExtra("className", RequestCityCenterFrame.class.toString());
                            intent.putExtra("titleName", "城市运营中心");
                            intent.putExtra("level", "6");
                            ProxySiteFrame.this.startActivity(intent);
                        } else {
                            Util.showIntent(ProxySiteFrame.this, WebSiteRequestRecordFrame.class);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.isProxy, "userid=" + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd());
                        return web.getPlan();
                    }
                });
            }
        } else {
            Util.showIntent("请先登录之后再申请渠道代理", this, LoginFrame.class, ProxySiteFrame.class);
        }
    }

    @OnClick(R.id.proxy_site_updateProxy)
    public void updateProxy(View view) {
        if (null == UserData.getUser()) {
            Util.show("您还没登录，请先登录！", this);
            return;
        }
        if (!Util.checkUserInfocomplete()){
            VoipDialog voipDialog = new
                    VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", ProxySiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(ProxySiteFrame.this,
                            UpdateUserMessageActivity.class);
                }
            }, null);
            voipDialog.show();
            return;
        }



        int level = Util.getInt(UserData.getUser().getLevelId());
        int shopType = Util.getInt(UserData.getUser().getShopTypeId());
        if (level == 2 && shopType >= 3 && shopType < 10)
            Util.showIntent(this, UpradeCityDirectorFrame.class);
        else
            Util.show("对不起，您目前的级别不能升级城市总监！", this);
    }

}
