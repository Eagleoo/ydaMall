package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.util.UserData;
import com.mall.util.Util;

public class YWJSActivity extends Activity {

    private Context context;

    @ViewInject(R.id.new_jieshao_chaungye)
    TextView new_jieshao_chaungye;

    @ViewInject(R.id.new_jieshao_lianm)
    TextView new_jieshao_lianm;

    @ViewInject(R.id.new_hudong)
    TextView new_hudong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_yewujieshao);
        ViewUtils.inject(this);
        context = this;

        User user = UserData.getUser();
        if (user != null) {
            int levelid = Integer.parseInt(user.getLevelId());
            if (levelid == 4 || levelid == 6 || levelid == 11) {
                new_jieshao_chaungye.setVisibility(View.GONE);
                new_jieshao_lianm.setVisibility(View.GONE);
                new_hudong.setVisibility(View.GONE);
            }

        }
        Util.initTitle(this, "业务介绍", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.new_jieshao_huiyuan, R.id.new_jieshao_chaungye,
            R.id.new_jieshao_lianm,
            R.id.new_jieshao_csyyzx, R.id.new_hudong})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.new_hudong:
                Util.showIntent(this, CommunityIntroduction.class);
                break;
            case R.id.new_jieshao_huiyuan:
                Util.showIntent(this, VIPShowFrame.class);
                break;
            case R.id.new_jieshao_chaungye:
                Util.showIntent(this, WebSiteFrame.class); //城市经理
                break;
            case R.id.new_jieshao_lianm:
                Util.showIntent(this, MainRightBottomLMSJFrame.class);  //商圈系统
                break;
            case R.id.new_jieshao_csyyzx:
                Intent intent = new Intent(context, WebViewActivity1.class);
                intent.putExtra("url", "http://www.yda360.com/Article/ArticleDetail.aspx?id=695&typeid=191");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
