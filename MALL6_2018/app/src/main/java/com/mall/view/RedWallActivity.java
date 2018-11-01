package com.mall.view;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.query.util.DisplayUtil;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RedWallActivity extends BaseActivity {


    int texthigh = 30;
    ScrollView scrollView;
    LinearLayout l;
    TextView hint, guize;
    ImageView hintclick, back;
    Boolean around = true;
    Boolean isfrist = true;

    float updateContainerH;
    int updateTextScrollH;
    int measuredHeight;
    int height;
    int perH;
    private MyRun myRun = new MyRun();

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_red_wall);
        findView();
        back.setOnClickListener(new click());
        guize.setOnClickListener(new click());
        hintclick.setOnClickListener(new click());

        scrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        handler.removeCallbacks(myRun);
                        scrollView.scrollTo(0, updateTextScrollH);
                        break;
                    case MotionEvent.ACTION_MOVE:

//	                        int moveY = (int) event.getY();
//	                        mScrollView.scrollTo(0,moveY);

                        break;
                    case MotionEvent.ACTION_UP:
                        float scaleY = scrollView.getScrollY();
                        updateTextScrollH = (int) scaleY;
                        handler.postDelayed(myRun, 1000);
                        break;
                }
                return false;
            }


        });

    }

    private void mesure() {
        updateContainerH = DisplayUtil.dip2px(this, 230);
        perH = DisplayUtil.dip2px(this, 1f);
        Log.e("数据", updateContainerH + "~~~~~~~~~~~~" + perH);
    }

    class MyRun implements Runnable {
        @Override
        public void run() {
            Log.i("MainActivity", "---updateTextScrollH---" + updateTextScrollH + ",height---" + height + ",measuredHeight--" + measuredHeight);
            updateTextScrollH = updateTextScrollH + perH;

            if (updateTextScrollH + height <= measuredHeight) {
                scrollView.scrollTo(0, updateTextScrollH);
                handler.postDelayed(myRun, 100);
            } else {
                scrollView.scrollTo(0, 0);
                handler.removeCallbacks(myRun);
                updateTextScrollH = 0;
                if (height > measuredHeight) {
                    Log.e("长度", "跳转");
                    return;
                }
                handler.postDelayed(myRun, 100);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(myRun);
        handler = null;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        initData();
        mesure();
    }

    private void initData() {
        if (UserData.getUser() == null) {
            hint.setText("请先注册或登录后再领取红包！");
            hintclick.setImageResource(R.drawable.red_zc);
            hintclick.setTag(1);
        } else {
            if (Lin_MallActivity.list.size() != 0) {
                hint.setText("你有" + Lin_MallActivity.list.size() + "个未领取的红包");
                hintclick.setImageResource(R.drawable.red_lq);
                hintclick.setTag(2);
            } else {
                if (Double.parseDouble(UserData.getUser().getRed_Seed()) < 500) {
                    hint.setText("你的红包种子不足，快去附近商家获取红包领取特权吧！");
                    hintclick.setImageResource(R.drawable.red_sj);
                    hintclick.setTag(3);
                } else {
                    hint.setText("消费天天领红包，分享终身有钱赚！");
                    hintclick.setImageResource(R.drawable.red_sj);
                    hintclick.setTag(3);
                }
            }
        }
        Util.asynTask(this, "", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                Log.e("---", result);
                try {
                    JSONArray jsonArray = new JSONObject(result).getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        TextView textView = new TextView(RedWallActivity.this);
                        String string = "<font color='#ffffff'>用户</font><font color='#ffe61d'>"
                                + Util.protectionUserName(jsonObject.optString("userid").replace("_p", ""))
                                + "</font><font color='#ffffff'>获得了</font><font color='#ffe61d'>"
                                + jsonObject.optString("money") + "</font><font color='#ffffff'>元现金红包</font>";
                        textView.setText(Html.fromHtml(string));
                        l.addView(textView);
                    }


                    l.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                    int height = l.getMeasuredHeight();
                    int width = l.getMeasuredWidth();
                    measuredHeight = l.getMeasuredHeight();
                    height = scrollView.getHeight();
                    measuredHeight = measuredHeight - height + 20;
                    Log.e("内容高度", measuredHeight + "");
                    Log.e("MainActivity", "height--" + height + ",measuredHeight--" + measuredHeight);
                    scrollView.scrollTo(0, 0);
                    handler.postDelayed(myRun, 1000);
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            @Override
            public Serializable run() {
                // TODO Auto-generated method stub
                Web web = new Web(Web.redurl, "/Get_RedPackage_Today",
                        "date=" + new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
                String result = web.getPlan();
                return result;
            }

        });
    }

    private void findView() {
        // TODO Auto-generated method stub
        scrollView = findViewById(R.id.scrollView);
        l = findViewById(R.id.l);
        hint = findViewById(R.id.hint);
        hintclick = findViewById(R.id.hintclick);
        back = findViewById(R.id.back);
        guize = findViewById(R.id.guize);
    }

    public class click implements OnClickListener {

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            switch (v.getId()) {
                case R.id.back:
                    finish();
                    break;
                case R.id.guize:
                    GuiZeDialog guiZeDialog = new GuiZeDialog(RedWallActivity.this);
                    guiZeDialog.show();
                    break;
                case R.id.hintclick:

                    if ((Integer) hintclick.getTag() == 1) {
                        Util.showIntent(RedWallActivity.this, RegisterFrame.class);
                    } else if ((Integer) hintclick.getTag() == 2) {
                        Util.showIntent(RedWallActivity.this, RedPackageActivity.class);
                    } else {
                        Util.showIntent(RedWallActivity.this, FindLMSJFrame.class);
                    }
                    break;
            }
        }
    }
}
