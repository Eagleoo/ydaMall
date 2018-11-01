package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.WebRequestCallBack;
import com.mall.pushmessage.PushMessage;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;

public class MessagePushedByMySelf extends Activity {
    @ViewInject(R.id.listView)
    private ListView listView;
    private User user;
    private int status = 0;
    private int page = 1;
    private BaseMallAdapter<JSONObject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_pushed_by_myself);
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        if (UserData.getUser() == null) {
            Util.showIntent(this, LoginFrame.class);
        } else {
            user = UserData.getUser();
        }
        int levelid = Integer.parseInt(user.getLevelId());
        int shopTypeId = Integer.parseInt(user.getShopTypeId());
        if (5 == levelid || 6 == levelid) {
            initTopOne();
        } else {
            if (shopTypeId >= 3 && shopTypeId < 10) {
                initTopOne();
            } else {
                initTopTwo();
            }
        }
        page();
        listView.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= listView.getAdapter().getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    synchronized (listView) {
                        if (0 == status) {
                            status = 1;
                            page();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    private void initTopOne() {
        Util.initTitle(this, "我的推送消息", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                },
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.showIntent(MessagePushedByMySelf.this,
                                PushMessage.class);
                    }
                }, R.drawable.note_add);
    }

    private void initTopTwo() {
        Util.initTitle(this, "我的推送消息", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void page() {
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        status = 2;
        App.getNewWebAPI().getAllPushSender(page, 15, user.getUserId(),
                user.getMd5Pwd(), new WebRequestCallBack() {
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
                            Util.show(obj.getString("message"),
                                    MessagePushedByMySelf.this);
                            return;
                        }
                        JSONArray array = obj.getJSONArray("list");
                        if (array.size() == 0 && page == 1) {
                            VoipDialog voipDialog = new VoipDialog("你还没有推送过消息", MessagePushedByMySelf.this, "立即推送", "我知道了", new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Util.showIntent(MessagePushedByMySelf.this, PushMessage.class);
                                }
                            }, null);
                            voipDialog.show();
                        }
                        JSONObject[] objs = array.toArray(new JSONObject[]{});
                        if (null == adapter) {
                            adapter = new BaseMallAdapter<JSONObject>(
                                    R.layout.message_pushed_by_myself_item,
                                    MessagePushedByMySelf.this, objs) {
                                @SuppressLint({"InlinedApi", "NewApi"})
                                @Override
                                public View getView(int position,
                                                    View convertView, ViewGroup parent,
                                                    final JSONObject t) {
                                    TextView message = convertView
                                            .findViewById(R.id.message);
                                    message.setText(t.getString("content").replace("消息内容：", "").split("发送人：")[0].split("会员ID：")[0].split("商户名称：")[0]);
                                    TextView send_count = convertView
                                            .findViewById(R.id.send_count);
                                    send_count.setText("推送人数：" + t.getString("COUNT_NUM") + "人");
                                    TextView message_send_date = convertView
                                            .findViewById(R.id.message_send_date);
                                    message_send_date.setText("  "
                                            + Util.friendly_time(t
                                            .getString("senderTime")));
                                    TextView reply_count = convertView
                                            .findViewById(R.id.count);
                                    if (!Util.isNull(t.getString("COUNTREPLY"))) {
                                        reply_count.setText(t
                                                .getString("COUNTREPLY"));
                                        reply_count.setVisibility(View.VISIBLE);
                                    } else {
                                        reply_count.setText("  ");
                                        reply_count.setVisibility(View.GONE);
                                    }
                                    convertView.setOnClickListener(new OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent
                                                    (MessagePushedByMySelf.this,
                                                            MessagePushedByMyselfDetail.class);
                                            intent.putExtra(
                                                    "contents",
                                                    t.getString("content"));
                                            intent.putExtra("id",
                                                    t.getString("id"));
                                            MessagePushedByMySelf.this
                                                    .startActivity(intent);
                                        }
                                    });
                                    return convertView;
                                }
                            };
                            listView.setAdapter(adapter);
                        } else
                            adapter.add(objs);
                        adapter.updateUI();
                        page++;
                    }

                    @Override
                    public void requestEnd() {
                        status = 0;
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
    }
}
