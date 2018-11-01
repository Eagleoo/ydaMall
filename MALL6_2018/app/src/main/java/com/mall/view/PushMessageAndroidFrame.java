package com.mall.view;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 功能：推送信息列表 <br>
 * 时间： 2014-9-24<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class PushMessageAndroidFrame extends Activity {
    private ListView listView;
    private int page = 1;
    private int size = 15;
    private BaseMallAdapter<JSONObject> adapter;
    private User user;
    private int status = 0; // 0可以加载，1不能加载，2加载中
    private PopupWindow distancePopup = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.push_message_android_frame);
        listView = this.findViewById(R.id.push_message_android_frame_list);
        Util.initTitle(this, "推送列表", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
    }

    private void init() {
        user = UserData.getUser();
        if (null == user)
            user = new User();
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

    private void page() {
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        status = 2;
        App.getNewWebAPI().getAllPush(page, size, user.getUserId(), user.getMd5Pwd(), new WebRequestCallBack() {
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
                    Util.show(obj.getString("message"), PushMessageAndroidFrame.this);
                    return;
                }
                JSONArray array = obj.getJSONArray("list");
                JSONObject[] objs = array.toArray(new JSONObject[]{});
                if (null == adapter) {
                    adapter = new BaseMallAdapter<JSONObject>(
                            R.layout.push_message_android_frame_item, PushMessageAndroidFrame.this, objs) {
                        @Override
                        public View getView(int position, final View convertView,
                                            ViewGroup parent, final JSONObject t) {
                            for (int i = 0; i < Util.mailllist.size(); i++) {
                                if (Util.mailllist.get(i).getId().equals(t.getString("id"))) {
                                    convertView.findViewById(R.id.hint).setVisibility(View.VISIBLE);
                                    break;
                                } else {
                                    convertView.findViewById(R.id.hint).setVisibility(View.INVISIBLE);
                                }
                            }
                            ImageView pic = convertView.findViewById(R.id.pic);
                            final TextView sender = convertView.findViewById(R.id.sender);
                            if (t.getString("sender").equals("system")) {
                                setText(R.id.sender, "系统消息");
                                pic.setImageResource(R.drawable.system_message);
                                setText(R.id.content, Util.Html2Text(t.getString("content")));
                            } else if (t.getString("sender").equals("系统消息")) {
                                setText(R.id.sender, "远大云商");
                                pic.setImageResource(R.drawable.ic_launcher);
                                setText(R.id.content, Util.Html2Text(t.getString("title")));
                            } else {
                                setText(R.id.sender, t.getString("sender"));
                                Glide.with(PushMessageAndroidFrame.this).load(t.getString("userFace")).placeholder(R.drawable.ic_launcher).into(pic);
                                setText(R.id.content, Util.Html2Text(t.getString("content").split("会员ID：")[0]).split("商户名称：")[0].split("发送人：")[0].replace("消息内容：", ""));
                            }
                            String senderTime = t.getString("senderTime");
                            if (Util.isNull(senderTime))
                                setText(R.id.senderTime, "时间未知");
                            else {
                                String showTime = Util.friendly_time(senderTime);
                                setText(R.id.senderTime, showTime);
                            }
                            convertView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    convertView.findViewById(R.id.hint).setVisibility(View.INVISIBLE);
                                    NewWebAPI.getNewInstance().readPush(user.getUserId(), user.getMd5Pwd(), t.getString("id"), null);
                                    Util.showIntent(PushMessageAndroidFrame.this, PushMessageFrame.class, new String[]{"nid", "mid", "userid", "sendertype"}, new String[]{t.getString("id"), t.getString("toid"), sender.getText().toString(), t.getString("senderType")});
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

    private void reply(View v, String toid, String userid) {
        getPopupWindow();
        startPopupWindow(toid, userid);
        distancePopup.showAsDropDown(v);
    }

    private void replyPushMessage(String toid, String userid, String info, String md5Pwd) {
        final CustomProgressDialog dialog = new CustomProgressDialog(PushMessageAndroidFrame.this);
        dialog.setMessage(dialog, "");
        dialog.show();
        NewWebAPI.getNewInstance().ReplyPushMessage(toid, "0", info, userid, md5Pwd, new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                dialog.cancel();
                dialog.dismiss();
            }

            @Override
            public void fail(Throwable e) {
                dialog.cancel();
                dialog.dismiss();
            }
        });
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view, android.view.WindowManager.LayoutParams.FILL_PARENT, android.view.WindowManager.LayoutParams.FILL_PARENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    /**
     * 初始化并弹出popupwindow
     */
    private void startPopupWindow(final String toid, final String userid) {
        View pview = getLayoutInflater().inflate(R.layout.custom_facerelativelayout, null, false);
        final EditText et_sendmessage = (EditText) pview.findViewById(R.id.et_sendmessage1);
        et_sendmessage.setHint("回复TA");
        Button btn_send = (Button) pview.findViewById(R.id.btn_send);
        btn_send.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String info = et_sendmessage.getText().toString();
                if (Util.isNull(info)) {
                    Toast.makeText(PushMessageAndroidFrame.this, "回复内容不能为空", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    replyPushMessage(toid, userid, info, "e10adc3949ba59abbe56e057f20f883e");
                }
            }
        });
        initpoputwindow(pview);
    }

}
