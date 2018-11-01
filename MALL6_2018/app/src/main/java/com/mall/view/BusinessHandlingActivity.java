package com.mall.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.Web;
import com.mall.util.Util;

public class BusinessHandlingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business_handling);
        ViewUtils.inject(this);
        Util.initTitle(this, "业务办理", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @OnClick({R.id.business_introduction, R.id.business_application, R.id.business_cooperate, R.id.business_operation_center})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.business_introduction:
                Util.showIntent(this, YWJSActivity.class);
                break;
            case R.id.business_application:
                Util.showIntent(this, ProxySiteFrame.class);
                break;
            case R.id.business_cooperate:
                Intent intent1 = new Intent(this, WebViewActivity.class);
                intent1.putExtra("url", "http://yda360.com/Activity/SupplierJoin.html?from=singlemessage&isappinstalled=0");
                intent1.putExtra("title", "我要合作");
                startActivity(intent1);
                break;
            case R.id.business_operation_center:
                Intent intent = new Intent(this, WebViewActivity.class);
                intent.putExtra("url", Web.imageip+"/phone/yyzx/");
                intent.putExtra("title", "运营中心");
                startActivity(intent);
                break;
        }
    }
}
