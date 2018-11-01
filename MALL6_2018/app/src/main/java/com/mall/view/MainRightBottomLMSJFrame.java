package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 申请商圈系统
 *
 * @author Administrator
 */

public class MainRightBottomLMSJFrame extends Activity {

    private Context content;

    @OnClick({R.id.lianmengshangjia_sqlm1})
    public void doSQLM(View v) {

        check();
    }

    public void check() {
        if (UserData.getUser() != null) {
            if (!Util.checkUserInfocomplete()){
                VoipDialog voipDialog = new
                        VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", MainRightBottomLMSJFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(MainRightBottomLMSJFrame.this,
                                UpdateUserMessageActivity.class);
                    }
                }, null);
                voipDialog.show();
                return;
            }





            int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());
            if (shopTypeId == 10) {
                Util.show("您已是联盟商家，不需要再次申请。", MainRightBottomLMSJFrame.this);
            } else if (4 < shopTypeId && shopTypeId < 10) {
                Util.show("您已是城市经理/城市总监，不能再申请商圈系统", MainRightBottomLMSJFrame.this);
            } else {
                Util.showIntent(MainRightBottomLMSJFrame.this, RequestAllianceFrame.class);
            }
        } else {
            Toast.makeText(MainRightBottomLMSJFrame.this, "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
        }
    }

    TextView tv1, tv2, tv3, tv4;
//	ImageView unionPic;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.main_rightbottom_lmsj);
        Util.initTitle(this, "〔商圈系统〕简介", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewUtils.inject(this);
        content = this;
//		unionPic = (ImageView) this.findViewById(R.id.union_pic);
//
//		int W = getWindowManager().getDefaultDisplay().getWidth();// 获取屏幕高度
//
//		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (W / 2));
//		unionPic.setLayoutParams(params);
        init();
    }

    private void init() {
        tv1 = (TextView) this.findViewById(R.id.tv1);
        tv2 = (TextView) this.findViewById(R.id.tv2);
        tv3 = (TextView) this.findViewById(R.id.tv3);
        tv4 = (TextView) this.findViewById(R.id.tv4);

        String title1 = "<font color=\"#ff0000\">　　<b>客户管理：</b></font>";
        String content1 = "<font color=\"#000000\">给消费者赠送红包种子，绑定新会员，锁定老会员，从此将消费者变成自己的大数据。</font><br>";

        String title2 = "<font color=\"#ff0000\">　　<b>职员管理 ：</b></font>";
        String content2 = "<font color=\"#000000\">对员工实行福分制管理，不需多花一分钱，员工从此变股东，让优秀员工永远跟随你。</font><br>";
        String title3 = "<font color=\"#ff0000\">　　<b>消息推送 ：</b></font>";
        String content3 = "<font color=\"#000000\">商家活动、促销信息，数据定位直达精准会员，想发给谁就发给谁，还可一键到社区。</font><br>";
        String title4 = "<font color=\"#ff0000\">　　<b>商品发布 ：</b></font>";
        String content4 = "<font color=\"#000000\">特色商品、特色服务，手机随时拍照，配上简单文字，瞬时发布，所有客户立马知晓。</font><br>";

        tv1.setText(Html.fromHtml(title1 + content1));
        tv2.setText(Html.fromHtml(title2 + content2));
        tv3.setText(Html.fromHtml(title3 + content3));
        tv4.setText(Html.fromHtml(title4 + content4));

    }
}
