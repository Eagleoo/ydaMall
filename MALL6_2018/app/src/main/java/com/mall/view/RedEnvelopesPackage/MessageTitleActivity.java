package com.mall.view.RedEnvelopesPackage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;
import com.mall.view.R;



@ContentView(R.layout.activity_message_title)
public class MessageTitleActivity extends Activity {
    private Context context;
    @ViewInject(R.id.title)
    private TextView title;

    @ViewInject(R.id.message)
    private TextView message;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.transparent);
        context = this;
        ViewUtils.inject(this);
        WindowManager.LayoutParams wl = getWindow().getAttributes();
        wl.alpha = 0.9f;
        getWindow().setAttributes(wl);
        Intent intent=getIntent();
        String mess=intent.getStringExtra("message");
        String tit=intent.getStringExtra("title");
        title.setText("");
        message.setText("");
        if (!Util.isNull(mess)){
            message.setText(mess);
        }

        if (!Util.isNull(tit)){
            title.setText(tit);
        }
    }

    @OnClick({R.id.close})
    private void  click(View view){
        finish();
    }
}
