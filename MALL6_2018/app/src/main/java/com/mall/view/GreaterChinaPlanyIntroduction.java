package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;


@ContentView(R.layout.activity_greater_china_plany_introduction)
public class GreaterChinaPlanyIntroduction extends Activity {
    @ViewInject(R.id.top_center)
    private TextView top_center;
    @ViewInject(R.id.top_left)
    private TextView top_left;


    @ViewInject(R.id.jianjietv1)
    private TextView jianjietv1;
    @ViewInject(R.id.jianjietv2)
    private TextView jianjietv2;
    @ViewInject(R.id.jianjietv3)
    private TextView jianjietv3;
    @ViewInject(R.id.jianjietv4)
    private TextView jianjietv4;
    @ViewInject(R.id.jianjietv5)
    private TextView jianjietv5;

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;
        init();
    }

    private void init() {
        // TODO Auto-generated method stub

        top_center.setText("〔幸福大中华双创计划〕简介");

        top_left.setVisibility(View.VISIBLE);
        String title1 = "<font color=\"#ff0000\">　　<b>一、『幸福大中华双创计划』简介：</b></font><br/>";
        String content1 = "<font color=\"#000000\">　　『幸福大中华双创计划』是“幸福大中华志愿者双创（福利）计划”的简称，是由国家十三五战略幸福大中华执行机构联合远大云商移动互联网大众创业服务平台共同推出的，旨在帮助转业退伍军人和社会各界贤达人士通过牵手“互联网+创业创新”来解决自己的就业创业问题。</font><br>";
        jianjietv1.setText(Html.fromHtml(title1 + content1));


        String title2 = "<font color=\"#ff0000\">　　<b>二、『幸福大中华双创计划』的宗旨：</b></font><br/>";
        String content2 = "<font color=\"#000000\">　　是由国家十三五战略幸福大中华执行机构通过联手中国首家移动互联网大众创业服务平台，带动退伍军人实现从共和国的保卫者向建设者转变，从消费者向经营者转变，从老兵向老板转变。并带领转业退伍军人及其亲属，通过以创业带动就业的形式，促进自己的事业发展和生活保障，为全面实现小康社会而努力。</font><br>";
        jianjietv2.setText(Html.fromHtml(title2 + content2));


        String title3 = "<font color=\"#ff0000\">　　<b>三、『幸福大中华双创计划』的申请条件：</b></font><br/>";
        String content3 = "<font color=\"#000000\">　　1、凡年龄在18周岁以上，有理想、有道德、有信仰、身体健康的退伍军人、大学毕业生，以及社会各界贤达人士均可申请加入幸福大中华志愿者双创（福利）计划。<br/>　　2、申请加入幸福大中华志愿者双创（福利）计划时，必须先提交申请表，经幸福大中华所在县级以上机构批准，并通过宣誓后成为幸福大中华志愿者，同时，向幸福大中华支付2680元志愿者双创（福利）计划费用。</font><br>";
        jianjietv3.setText(Html.fromHtml(title3 + content3));
        String title4 = "<font color=\"#ff0000\">　　<b>四、『幸福大中华双创计划』志愿者权益：</b></font><br/>";
        String content4 = "<font color=\"#000000\">　　1、获得价值1680元健康产品一套.<br/>　　2、获得价值2000元的中国沙漠（志愿者）建设兵团军服一套。<br/>　　3、获得远大云商联盟商家赠送的2680红包种子（每天按万分之五享受现金红包发送）。<br/>　　4、享有远大云商创客（城市经理）资格，拥有远大云商创客（城市经理）的全部权益。<br/>　　5、获得中国拥军优属基金会消费增值公益基金管理委员会提供的最高30万大病互助公益基金保障（详见合同）。<br/>　　6、通过幸福大中华机构组织的考核，有机会成为幸福大中华的工作人员。<br/>　　7、免费参加由幸福大中华机构组织的部队军训和文化技能培训以及传统文化学习。</font><br>";
        jianjietv4.setText(Html.fromHtml(title4 + content4));

        String title5 = "<font color=\"#ff0000\">　　<b>五、『幸福大中华双创计划』解释权：</b></font><br/>";
        String content5 = "<font color=\"#000000\">　　幸福大中华执行机构对『幸福大中华双创计划』拥有解释权。参与幸福大中华双创计划的志愿者遇任何问题或疑虑均可咨询所在地幸福大中华执行机构办事处。若遇与创业工作或与创业系统相关问题时，则可直接咨询远大云商全国客户服务中心（全国客服免费电话：400-666-3838）。</font><br>";
        jianjietv5.setText(Html.fromHtml(title5 + content5));
    }

    @OnClick({R.id.tomain, R.id.toapply})
    public void todo(View v) {
        switch (v.getId()) {
            case R.id.tomain:

                Util.showIntent(context, Lin_MainFrame.class);
                break;
            case R.id.toapply:
                if (null != UserData.getUser()) {
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



                    int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
                    if (-1 < shopTypeId && shopTypeId != 10) {
                        Log.e("角色等级", UserData.getUser().getShopType());

                        if (UserData.getUser().getShopType().equals("城市经理")) {
                            Util.show("您已经是【" + "城市经理" + "】，\n无须重复申请。", context);
                        } else {
                            Util.show("您是【" + UserData.getUser().getShopType() + "】经理，\n不需要再重复申请城市经理。", context);
                        }

                    } else if (10 == shopTypeId) {
                        Util.show("您是【联盟商家】，\n不需要再申请城市经理。", context);
                    } else if (UserData.getUser().getLevel().contains("代理")) {
                        Util.show(
                                "您是【" + UserData.getUser().getZone() + "】【" + UserData.getUser().getLevel() + "】，\n不需要再申请城市经理。",
                                context);
                    } else {
                        Util.asynTask(this, "正在获取您的申请记录...", new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if ("success".equals(runData)) {
                                    Intent intent = new Intent();
                                    intent.setClass(context, SiteFrame.class);
                                    intent.putExtra("className", SiteFrame.class.toString());
                                    intent.putExtra("type", "2680");
                                    context.startActivity(intent);
                                } else {
                                    Util.show(runData + "", context);
                                    String key = "1111";
                                    if ("您已提交城市总监申请，请撤消后再申请新业务。".equals(runData + ""))
                                        key = "qddl";
                                    else if ("您已提交创业大使申请，请撤消后再申请新业务。".equals(runData + ""))
                                        key = "cyds";
                                    Util.showIntent(context, WebSiteRequestRecordFrame.class);
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
                    Util.showIntent("请先登录之后再申请城市经理", context, LoginFrame.class, ProxySiteFrame.class);
                }
                break;

            default:
                break;
        }

    }


}
