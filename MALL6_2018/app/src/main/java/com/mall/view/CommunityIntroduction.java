package com.mall.view;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.QuitDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

public class CommunityIntroduction extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hudong_jieshao);
        ViewUtils.inject(this);
        Util.initTitle(this, "〔远大社区〕简介", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.to_shenqing})
    public void onclick(View view) {
        switch (view.getId()) {
            case R.id.to_shenqing:
                gotoCommunity();
                break;
        }
    }

    private void gotoCommunity() {
        User user = UserData.getUser();
        if (user == null) {
            Util.showIntent(CommunityIntroduction.this,
                    LoginFrame.class);
            return;
        }

        if (Util.isInstall(this, "com.yda360.ydacommunity")) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cn = new ComponentName("com.yda360.ydacommunity", "com.yda360.ydacommunity.activity.LeadingActivity");
            intent.putExtra("action", "lin00123");
            intent.setComponent(cn);
            if (null != UserData.getUser()) {
                intent.putExtra("userId", UserData.getUser().getUserId());
                intent.putExtra("md5Pwd", UserData.getUser().getMd5Pwd());
                intent.putExtra("userNo", UserData.getUser().getUserNo());
                intent.putExtra("userFace", UserData.getUser().getUserFace());
            }
            intent.putExtra("openClassName", "com.yda360.ydacommunity.activity.LaunchActivity");

            startActivity(intent);
        } else {
            QuitDialog dialog = new QuitDialog(this, "亲，首次使用社区请安装【远大社区】！", "立即安装", "稍后下载", new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.openWeb(CommunityIntroduction.this, "http://" + Web.webServer + "/yuandaapp/plugs/Community.apk");
                }
            }, new OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
            dialog.show();
        }
    }
}
