package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.net.Web;
import com.mall.util.Util;

@ContentView(R.layout.provision_layout)
public class ProvisionActivity extends Activity {

    @ViewInject(R.id.webview)
    private WebView webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        getIntentData();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        String title = "注册条款";
        String url = "http://app.yda360.com/xy.htm";
        if (intent.hasExtra("RequestCityCenterFrame")) {
            title = "城市总监合同";
            url = "http://app.yda360.com/cszj.html?1";
        }
        if (intent.hasExtra("MoveGuestFrame")) {
            title = "移动创客合同";
            url = "http://app.yda360.com/ydck.html";
        }
        if (intent.hasExtra("PioneerAngelFrame")) {
            title = "创业大使合同";
            url = "http://app.yda360.com/cyds.html";
        }
        if (intent.hasExtra("Site_ZCFrame")) {
            title = "城市经理合同";
            url = "http://app.yda360.com/csjl.html?1";
        }
        if (intent.hasExtra("StudentRequestFrame")) {
            title = "大学生空间合同";
            url = "http://app.yda360.com/dxscy.html";
        }
        if (intent.hasExtra("jianxichuangke")) {
            title = "见习创客合同";
            url = "http://app.yda360.com/jxck.html";
        }
        if (intent.hasExtra("RequestAllianceFrame")) {
            title = "联盟商家合同";
            url = "http://app.yda360.com/lmsj.html?1";
        }
        if (intent.hasExtra("userloginprotocol")) {
            title = "用户服务协议";
            url = "http://" + Web.webImage + "/%E6%B3%A8%E5%86%8C%E6%9D%A1%E6%AC%BE.html";
        }
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        webview.loadUrl(url);
    }

}
