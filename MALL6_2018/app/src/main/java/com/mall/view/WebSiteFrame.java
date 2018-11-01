package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 功能： 〔城市经理〕简介<br>
 * 时间： 2013-7-15<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class WebSiteFrame extends Activity {

    private TextView tv1;
    private TextView tv2;
    private TextView tv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.web_site_frame);
        init();
        Util.initTitle(this, "〔城市经理〕简介", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        this.findViewById(R.id.woyaokaitong).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                if (UserData.getUser() != null) {

                    if (!Util.checkUserInfocomplete()){
                        VoipDialog voipDialog = new
                                VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", WebSiteFrame.this, "立即登记", "稍后登记", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Util.showIntent(WebSiteFrame.this,
                                        UpdateUserMessageActivity.class);
                            }
                        }, null);
                        voipDialog.show();
                        return;
                    }




                    int shopTypeId = Integer.parseInt(UserData.getUser().getShopTypeId());

                    if (shopTypeId == 10) {
                        Util.show("您已是联盟商家，不能再申请城市经理。", WebSiteFrame.this);
                    } else if (-1 < shopTypeId && shopTypeId != 10) {
                        Util.show("您已是" + UserData.getUser().getShowLevel() + "，不需要再次申请。", WebSiteFrame.this);
                    } else {
                        Util.showIntent(WebSiteFrame.this, SiteFrame.class);
                    }
                } else {
                    Toast.makeText(WebSiteFrame.this, "您还没有登录,请先登录", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void init() {
        findView();
        setColor();
    }

    private void findView() {
        tv1 = (TextView) this.findViewById(R.id.tvf);
        tv2 = (TextView) this.findViewById(R.id.tvg);
        tv3 = (TextView) this.findViewById(R.id.tvh);

    }

    private void setColor() {

        String title = "<font color=\"#ff0000\">　　<b>O2O移动超级大系统：</b></font>";
        String content = "<font color=\"#000000\">创业者成为〔城市经理〕后，不但可以快速锁定线上消费者，还可以通过向线下实体商家免费赠送“全国商圈客户管理大系统”来锁定千万实体商户，让自己成为全国商家的股东，真正实现O2O平台互联互通。</font><br>";
        String title2 = "<font color=\"#ff0000\">　　<b>锁定数据超级大系统：</b></font>";
        String content2 = "<font color=\"#000000\">不论创业者是通过自己分享会员产生的系统数据，还是通过拓展线下商家锁定的数据，都永远属于自己的系统大数据，这些数据不论做什么（消费还是创业），都可为创业者带来丰厚的系统收益，而且终身享受。</font><br>";

        String title3 = "<font color=\"#ff0000\">　　<b>倍增财富超级系统：</b></font>";
        String content3 = "<font color=\"#000000\">创业者依靠一部手机，既可通过自己分享锁定会员，又可通过拓展的商家锁定会员。不论这些会员去商家消费，还是去平台购物，个人创业等，都将让自己获取永远的收益，一年可轻松进账10万元，而且一年更比一年高。</font><br>";

        tv1.setText(Html.fromHtml(title + content));
        tv2.setText(Html.fromHtml(title2 + content2));
        tv3.setText(Html.fromHtml(title3 + content3));
    }

}
