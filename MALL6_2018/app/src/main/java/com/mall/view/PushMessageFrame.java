package com.mall.view;

import android.app.Activity;
import android.os.Bundle;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.util.CircleImageView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.messageboard.FaceConversionUtil;

public class PushMessageFrame extends Activity {
    @ViewInject(R.id.reply_list)
    private ListView listView;
    @ViewInject(R.id.push_message_web)
    private WebView webView;
    private int page = 1;
    private String mid = "";
    private String pid = "0";
    private String id = "";
    private BaseMallAdapter<JSONObject> adapter;
    private User user;
    @ViewInject(R.id.input_layout)
    private LinearLayout input_layout;
    @ViewInject(R.id.et_sendmessage1)
    private EditText et_sendmessage;
    private String sendertype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.push_message_frame);
        ViewUtils.inject(this);
        mid = getIntent().getStringExtra("mid");
        Log.e("2",mid);
        id = getIntent().getStringExtra("nid");
        String userid = (null == UserData.getUser()) ? "" : UserData.getUser().getUserId();
        Util.initTitle(this, getIntent().getStringExtra("userid"), new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        sendertype = getIntent().getStringExtra("sendertype");
        if (sendertype != null && (sendertype.equals("0") || sendertype.equals("99"))) {
            input_layout.setVisibility(View.GONE);
        }
        WebView webView = new WebView(PushMessageFrame.this);
        webView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);// 滚动条在WebView内侧显示
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("http://" + Web.webImage + "/notification.aspx?id=" + id + "&userId=" + userid);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
            }
        });
        listView.addHeaderView(webView);
        user = UserData.getUser();
        if (user == null) {
            user = new User();
        }
        page();
        Button btn_send = this.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = et_sendmessage.getText().toString();
                if (Util.isNull(info)) {
                    Toast.makeText(PushMessageFrame.this, "回复内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    replyPushMessage(mid, info, user.getMd5Pwd(), pid);
                }
            }
        });
    }

    private void page() {
        if (Util.isNull(UserData.getUser())) {
            return;
        }
        final CustomProgressDialog dialog = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getReplyMessage(mid, page, 99, user.getUserId(), user.getMd5Pwd(), new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                dialog.cancel();
                dialog.dismiss();
                if (Util.isNull(result)) {
                    return;
                }
                JSONObject obj = null;
                try {
                    obj = JSON.parseObject(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (200 != obj.getInteger("code").intValue()) {
                    Util.show(obj.getString("message"), PushMessageFrame.this);
                    return;
                }
                JSONArray array = obj.getJSONArray("list");
                JSONObject[] objs = array.toArray(new JSONObject[]{});
                final JSONObject[] hashObjs = objs;
                if (null == adapter) {
                    adapter = new BaseMallAdapter<JSONObject>(
                            0, PushMessageFrame.this, objs) {
                        @Override
                        public View getView(int position, View convertView,
                                            ViewGroup parent, final JSONObject t) {
                            if (!t.getString("id").equals(user.getUserId())) {
                                pid = t.getString("id");
                            }
                            String infos = "";
                            infos = t.getString("info");
                            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(PushMessageFrame.this, infos);
                            TextView info = (TextView) convertView.findViewById(R.id.content);
                            TextView user_name = (TextView) convertView.findViewById(R.id.userId);
                            user_name.setText(Util.getNo_pUserId(t.getString("USERID")));
                            info.setText(spannableString);
                            CircleImageView img = (CircleImageView) convertView.findViewById(R.id.userFace);
                            Glide.with(PushMessageFrame.this).load(t.getString("userFace")).error(R.drawable.ic_launcher).into(img);
                            convertView.setOnLongClickListener(new OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View v) {
                                    if (Util.isNull(user.getUserIdNoEncodByUTF8())) {
                                        Util.showIntent(PushMessageFrame.this, LoginFrame.class);
                                    }
                                    if (t.getString("USERID").equals(user.getUserIdNoEncodByUTF8())) {
                                        Util.deleteItemDialog(PushMessageFrame.this, "是否删除这条回复", "确定", "取消", new OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                deleteItem(t.getString("id"));
                                            }
                                        });
                                    }
                                    return true;
                                }
                            });
                            return convertView;
                        }
                    };
                    listView.setAdapter(adapter);
                } else
                    adapter.add(objs);
                listView.setSelection(adapter.getCount() - 1);
                listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
                adapter.updateUI();
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }

        });
    }

    private void deleteItem(final String id) {
        final CustomProgressDialog dialog = Util.showProgress("正在删除....", this);
        NewWebAPI.getNewInstance().DeleteReplyPush(id, user.getUserId(), user.getMd5Pwd(), new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                dialog.cancel();
                dialog.dismiss();
                if (Util.isNull(result)) {
                    Util.show("删除失败", PushMessageFrame.this);
                    return;
                }
                JSONObject obj = null;
                try {
                    obj = JSON.parseObject(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (200 != obj.getInteger("code").intValue()) {
                    Util.show(obj.getString("message"), PushMessageFrame.this);
                    return;
                } else {
                    page = 1;
                    if (adapter != null) {
                        adapter.clear();
                    }
                    page();
                }
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }

        });
    }

    private void replyPushMessage(final String toid, final String info, final String md5Pwd, final String id) {
        final CustomProgressDialog dialog = Util.showProgress("", this);
        NewWebAPI.getNewInstance().ReplyPushMessage(toid, id, info, UserData.getUser().getUserId(), md5Pwd, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                et_sendmessage.setText("");
                dialog.cancel();
                dialog.dismiss();
                JSONObject obj = JSON.parseObject(result.toString());
                if (200 != obj.getInteger("code").intValue()) {
                    Util.show(obj.getString("message"), PushMessageFrame.this);
                    return;
                } else {
                    page = 1;
                    adapter.clear();
                    page();
                    adapter.updateUI();
                }
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }
}
