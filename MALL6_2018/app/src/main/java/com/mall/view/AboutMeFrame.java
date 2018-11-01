package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.mall.util.Util;

public class AboutMeFrame extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.about_me_frame);
        TextView info = findViewById(R.id.info);
        String demo1 = getResources().getString(R.string.about_us);
        info.setText(Html.fromHtml(demo1));

        Util.initTitle(this, "关于我们", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ((TextView) this.findViewById(R.id.about_me_version)).setText("版本：" + Util.version);
    }
}
