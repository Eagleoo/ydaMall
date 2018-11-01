package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.mall.util.Util;

/**
 * 功能： 会员优势 时间： 2013-7-13 备注：
 *
 * @author Lin.~
 */
public class VIPShowFrame extends Activity {

    TextView vip_1_tv, tv1, tv2, tv3, tv4, tv5, tv6, tv7, tv8, tv9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.vip_show_frame);
        Util.initTitle(this, "〔注册会员〕简介", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Util.getTextView(R.id.vip_show_reg, this).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(VIPShowFrame.this, RegisterFrame.class);
                    }
                });
        init();
    }

    private void init() {
        // TODO Auto-generated method stub
        findview();
        setTextview();
    }

    private void findview() {
        vip_1_tv = this.findViewById(R.id.vip_1_tv);
        tv1 = this.findViewById(R.id.tva);
        tv2 = this.findViewById(R.id.tvb);
        tv3 = this.findViewById(R.id.tvc);
        tv4 = this.findViewById(R.id.tvd);
        tv5 = this.findViewById(R.id.tve);
        tv6 = this.findViewById(R.id.tvf);
        tv7 = this.findViewById(R.id.tvg);
        tv8 = this.findViewById(R.id.tvh);
        tv9 = this.findViewById(R.id.tvi);
    }

    private void setTextview() {
        String title1 = "<font color=\"#ff0000\"><b>〔打电话免费〕</b></font>";
        String title2 = "<font color=\"#ff0000\"><b>〔谈恋爱免费〕</b></font>";
        String title3 = "<font color=\"#ff0000\"><b>〔找资源免费〕</b></font>";
        String title4 = "<font color=\"#ff0000\"><b>〔拓人脉免费〕</b></font>";
        String title5 = "<font color=\"#ff0000\"><b>〔消费领红包〕</b></font>";
        String title6 = "<font color=\"#ff0000\"><b>〔建数据免费〕</b></font>";
        String title7 = "<font color=\"#ff0000\"><b>〔做事业免费〕</b></font>";
        String title8 = "<font color=\"#ff0000\"><b>〔学赚钱免费〕</b></font>";
        String title9 = "<font color=\"#ff0000\"><b>〔找帮忙免费〕</b></font>";
        String content1 = "<font color=\"#000000\">只需每日签到，轻松得话费，就可永远免费打电话。</font>";
        String content2 = "<font color=\"#000000\">谁说谈恋爱一定要花钱，远大恋爱平台永远都免费。</font>";
        String content3 = "<font color=\"#000000\">资源真实可靠，需要什么找什么，而且永远都免费。</font>";
        String content4 = "<font color=\"#000000\">朋友圈、伙伴圈、创业圈、商务圈，永远对您免费。</font>";
        String content5 = "<font color=\"#000000\">到全国商家消费，领取红包种子，享现金红包特权。</font>";
        String content6 = "<font color=\"#000000\">只要是会员就可永远拥有建立自己大数据的大平台。</font>";
        String content7 = "<font color=\"#000000\">只需一部手机就可通过移动互联网开创自己的事业。</font>";
        String content8 = "<font color=\"#000000\">培训不花钱，课堂天天有，人人都赚钱，永远免费。</font>";
        String content9 = "<font color=\"#000000\">社区天天有人在等你，一个好汉三个帮，绝对免费。</font>";

        String demo2 = getResources().getString(R.string.registration_introduce);
        vip_1_tv.setText(Html.fromHtml(demo2));
        tv1.setText(Html.fromHtml(title1 + content1));
        tv2.setText(Html.fromHtml(title2 + content2));
        tv3.setText(Html.fromHtml(title3 + content3));
        tv4.setText(Html.fromHtml(title4 + content4));
        tv5.setText(Html.fromHtml(title5 + content5));
        tv6.setText(Html.fromHtml(title6 + content6));
        tv7.setText(Html.fromHtml(title7 + content7));
        tv8.setText(Html.fromHtml(title8 + content8));
        tv9.setText(Html.fromHtml(title9 + content9));

    }
}
