package com.mall.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.WebRequestCallBack;
import com.mall.util.CircleImageView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.messageboard.FaceConversionUtil;

public class MessagePushedByMyselfDetail extends Activity {
    @ViewInject(R.id.content)
    private TextView content;
    @ViewInject(R.id.container)
    private ListView container;
    @ViewInject(R.id.userFace)
    private CircleImageView userFace;
    @ViewInject(R.id.userId)
    private TextView userId;
    private String contents = "";
    private User user;
    private String id = "";
    private String mid = "";
    private String pid = "";
    private int page = 1;
    private BaseMallAdapter<JSONObject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_pushed_by_myself_detail);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        Util.initTitle(this, "推送详情", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getIntentData();
        getReplyList();
        container.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= container.getAdapter().getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    getReplyList();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    private void getIntentData() {
        contents = this.getIntent().getStringExtra("contents");
        id = this.getIntent().getStringExtra("id");
        if (!Util.isNull(contents)) {
            if (contents.contains("会员ID：")) {
                String s1 = contents.split("会员ID：")[0];
                String s2 = contents.split("会员ID：")[1].split("会员姓名：")[0];
                String s3 = contents.split("会员姓名：")[1].split("联系电话：")[0];
                String s4 = contents.split("联系电话：")[1].split("区域信息：")[0];
                String s5 = contents.split("区域信息：")[1];
                content.setText(s1 + "\n" + "会员ID：" + s2 + "\n" + "会员姓名：" + s3 + "\n" + "联系电话：" + s4 + "\n" + "区域信息：" + s5);
            } else if (contents.contains("商户名称：")) {
                String s1 = contents.split("商户名称：")[0];
                String s2 = contents.split("商户名称：")[1].split("所在地址：")[0];
                String s3 = contents.split("所在地址：")[1].split("联系电话：")[0];
                String s4 = contents.split("联系电话：")[1];
                content.setText(s1 + "\n" + "商户名称：" + s2 + "\n" + "所在地址：" + s3 + "\n" + "联系电话：" + s4);
            } else if (contents.contains("发送人：")) {
                String s1 = contents.split("发送人：")[0].replace("消息内容：", "");
                String s2 = contents.split("发送人：")[1].split("联系电话：")[0];
                String s3 = contents.split("联系电话：")[1];
                content.setText(s1 + "\n" + "发送人：" + s2 + "\n" + "联系电话：" + s3);
            } else {
                content.setText(contents);
            }
        }
    }

    private void getReplyList() {
        user = UserData.getUser();
        if (user == null) {
            user = new User();
        }
        Glide.with(MessagePushedByMyselfDetail.this).load(user.getUserFace()).error(R.drawable.ic_launcher).into(userFace);
        userId.setText(user.getUserId());
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        App.getNewWebAPI().getAllPushUserSender(page++, 99, user.getUserId(), user.getMd5Pwd(), id, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                JSONObject obj = null;
                try {
                    obj = JSON.parseObject(result.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
                if (200 != obj.getInteger("code").intValue()) {
                    Util.show(obj.getString("message"), MessagePushedByMyselfDetail.this);
                    return;
                }
                JSONArray array1 = obj.getJSONArray("list");
                JSONArray array = new JSONArray();
                for (int i = 0; i < array1.size(); i++) {
                    if (!Util.isNull(((JSONObject) array1.get(i)).getString("countreply"))) {
                        array.add(array1.get(i));
                    }
                }
                JSONObject[] objs = array.toArray(new JSONObject[]{});
                if (objs.length == 0 && page > 1) {
                    page--;
                    return;
                }
                if (null == adapter) {
                    adapter = new BaseMallAdapter<JSONObject>(R.layout.pushmessage_reply_item, MessagePushedByMyselfDetail.this, objs) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent, final JSONObject t) {
                            TextView reply_this_info = (TextView) convertView.findViewById(R.id.content);
                            SpannableString spannableString = FaceConversionUtil.getInstace().getExpressionString(MessagePushedByMyselfDetail.this, t.getString("info"));
                            TextView user_name = (TextView) convertView.findViewById(R.id.userId);
                            user_name.setText(Util.getNo_pUserId(t.getString("userid")));
                            TextView time = (TextView) convertView.findViewById(R.id.time);
                            String senderTime = t.getString("date");
                            if (Util.isNull(senderTime))
                                time.setText("时间未知");
                            else {
                                String showTime = Util.friendly_time(senderTime);
                                time.setText(showTime);
                            }
                            TextView countreply = (TextView) convertView.findViewById(R.id.countreply);
                            countreply.setText("查看更多(" + t.getString("countreply") + ")");
                            final CircleImageView userface = (CircleImageView) convertView.findViewById(R.id.userFace);
                            Glide.with(MessagePushedByMyselfDetail.this).load(t.getString("face")).error(R.drawable.ic_launcher).into(userface);
                            reply_this_info.setText(spannableString);
                            countreply.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MessagePushedByMyselfDetail.this, PushMessageFrame.class);
                                    intent.putExtra("nid", t.getString("mid"));
                                    intent.putExtra("mid", t.getString("id"));
                                    intent.putExtra("userid", Util.getNo_pUserId(t.getString("userid")));
                                    MessagePushedByMyselfDetail.this.startActivity(intent);
                                }
                            });
                            return convertView;
                        }
                    };
                    container.setAdapter(adapter);
                } else
                    adapter.add(objs);
                adapter.updateUI();
            }

            @Override
            public void requestEnd() {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }

}
