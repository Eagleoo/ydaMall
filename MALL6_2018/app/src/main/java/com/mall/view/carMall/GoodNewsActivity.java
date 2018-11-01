package com.mall.view.carMall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.Util;
import com.mall.view.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * 喜报
 */
public class GoodNewsActivity extends AppCompatActivity {

    @ViewInject(R.id.l)
    LinearLayout l;
    List<XibaoBean.ListBean> list = new ArrayList<>();
    public static String XIBAOLIST="XIBAOLIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_news);
        ViewUtils.inject(this);
        Util.initTitle(this, "喜报", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Intent intent=getIntent();
        if (intent.hasExtra(XIBAOLIST)){
            list.addAll( (ArrayList<XibaoBean.ListBean>)intent.getSerializableExtra(XIBAOLIST));
            int num = list.size() > 10 ? 10 : list.size();
            for (int i = 0; i < num; i++) {
                TextView textView = new TextView(GoodNewsActivity.this);
                String string = "<font color='#111111'>热烈祝贺</font><font color='#E50012'>"
                        + list.get(i).getUserId() + "</font><font color='#111111'>喜提爱车</font>";
                textView.setText(Html.fromHtml(string));
                l.addView(textView);
            }
        }else {
            getTichexinxi();
        }

    }

    @OnClick({R.id.more})
    public void click(View view) {

        Intent intent = new Intent(GoodNewsActivity.this, StyleActivity.class);
        intent.putExtra("listcar", (Serializable) list);
        startActivity(intent);
    }

    private void getTichexinxi() {
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Car_put_info" + "&page=1&size=999&search=",
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Gson gson = new Gson();
                        XibaoBean xibaoBean = gson.fromJson(result.toString(), XibaoBean.class);
                        if (!xibaoBean.getCode().equals("200") || xibaoBean.getList() == null
                                || xibaoBean.getList().size() == 0
                                ) {
                            return;
                        }
                        list.addAll(xibaoBean.getList());
                        int num = list.size() > 10 ? 10 : list.size();
                        for (int i = 0; i < num; i++) {
                            TextView textView = new TextView(GoodNewsActivity.this);
                            String string = "<font color='#111111'>热烈祝贺</font><font color='#E50012'>"
                                    + xibaoBean.getList().get(i).getUserId() + "</font><font color='#111111'>喜提爱车</font>";
                            textView.setText(Html.fromHtml(string));
                            l.addView(textView);
                        }

                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {

                    }
                }
        );
    }
}
