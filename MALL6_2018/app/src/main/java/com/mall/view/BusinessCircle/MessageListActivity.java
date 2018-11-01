package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.FocusBean;
import com.mall.model.UnReadMessageBean;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.util.CircleImageView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.activity_message_list)
public class MessageListActivity extends AppCompatActivity {
    private Context context;

    @ViewInject(R.id.messagelist_lv)
    private ListView messagelist;

    List<FocusBean.ListBean> focuslist = new ArrayList<>();

    private List<UnReadMessageBean.CommentsBean> comments = new ArrayList<>();


    Myadapter myadapter;

    UnReadadapter unReadadapter;

    private String title = "";

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        user = UserData.getUser();
        title = getIntent().getStringExtra("title");
        Log.e("title", title + "KL");
        ViewUtils.inject(this);
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
        Util.kfg = "1";
    }

    private void init() {
        initadapter();
        initListen();
        initdata();
    }

    private void initListen() {
        messagelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                if (title != null && title.equals("关注列表")) {
                    FocusBean.ListBean listBean = focuslist.get(position);
                    if (null != user) {
                        Intent intent = new Intent(context, BusinessPersonSpaceActivity.class);
                        intent.putExtra("userId", listBean.getUserId());
                        intent.putExtra("userface", listBean.getUserFace());
                        context.startActivity(intent);
                    } else {
                        Util.showIntent("对不起，请先登录！", context, LoginFrame.class);
                    }
                } else {

                    if (null == user) {
                        Util.showIntent("对不起，请先登录！", context, LoginFrame.class);
                        return;
                    }
                    UnReadMessageBean.CommentsBean userMessageBoard = comments.get(position);
                    UserMessageBoard bean = new UserMessageBoard();
                    Intent intent = new Intent(context, CommentsPageActivity.class);
                    intent.putExtra("id", userMessageBoard.getMid());
                    intent.putExtra("userId", userMessageBoard.getCommentUserId());

                    bean.setUserId(userMessageBoard.getFbz_userid());
                    bean.setUserFace(userMessageBoard.getFbz_face());
                    bean.setCreateTime(userMessageBoard.getCommentCreateTime());
                    bean.setFiles(userMessageBoard.getMoodFiles());
                    bean.setContent(userMessageBoard.getMoodMessage());
                    bean.setCreateTime(userMessageBoard.getFbz_createtime());
                    bean.setType("111");

                    Bundle b = new Bundle();
                    b.putSerializable("UserMessageBoard", bean);
                    Log.e("图片信息", bean.getFiles());
                    intent.putExtras(b);

                    if (!Util.isNull(userMessageBoard.getId())) {
                        context.startActivity(intent);
                    }

                }

            }
        });

    }

    private void initdata() {
        if (title != null && title.equals("关注列表")) {
            ((TextView)findViewById(R.id.center)).setText(title);
            getAllFriend();
        } else {
            ((TextView)findViewById(R.id.center)).setText("信息列表");
            getUnReadList();
        }

    }

    private void initadapter() {
        if (title != null && title.equals("关注列表")) {
            myadapter = new Myadapter(context, focuslist);
            messagelist.setAdapter(myadapter);
        } else {
            unReadadapter = new UnReadadapter(context, comments);
            messagelist.setAdapter(unReadadapter);
        }

    }


    private void getUnReadList() {

        Map<String, String> map = new HashMap<String, String>();


        map.put("userId", UserData.getUser().getUserId());
        map.put("md5Pwd", UserData.getUser().getMd5Pwd());
        final CustomProgressDialog cpd = CustomProgressDialog.showProgressDialog(this, "数据加载中...");

        NewWebAPI.getNewInstance().getWebRequest("/Mood.aspx?call=getUnReaderMoodCommentAndPraise", map, new WebRequestCallBack() {


            @Override
            public void success(Object result) {
                super.success(result);
                if (Util.isNull(result)) {
                    return;
                }

                Log.e("发现未读消息", "YY" + result.toString());
                JSONObject json = JSON.parseObject(result.toString());
                if (200 != json.getIntValue("code")) {
                    Toast.makeText(context, "网络异常", Toast.LENGTH_SHORT).show();
                    return;
                }

                Gson gson = new Gson();

                UnReadMessageBean unread = gson.fromJson(result.toString(), UnReadMessageBean.class);

                comments.clear();
                List<UnReadMessageBean.CommentsBean> list = unread.getComments();
                comments.addAll(list);
                unReadadapter.notifyDataSetChanged();

            }

            @Override
            public void requestEnd() {
                super.requestEnd();
                cpd.dismiss();
            }


        });

    }

    public void getAllFriend() {

        NewWebAPI.getNewInstance().getMyGuanzhu(UserData.getUser().getUserId(),
                UserData.getUser().getMd5Pwd(), new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        String str = result.toString();
                        Log.e("返回数据", str);

                        Gson gson = new Gson();

                        FocusBean bean = gson.fromJson(str, FocusBean.class);

                        if (!bean.getCode().equals("200")) {
                            Toast.makeText(context, bean.getMessage(), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        focuslist.clear();

                        List<FocusBean.ListBean> list = bean.getList();

                        focuslist.addAll(list);

                        myadapter.notifyDataSetChanged();


                    }

                    @Override
                    public void requestEnd() {


                    }
                });
    }

    class Myadapter extends BaseAdapter {
        private Context context;
        private List<FocusBean.ListBean> focuslist;

        public Myadapter(Context context, List<FocusBean.ListBean> focuslist) {
            this.context = context;
            this.focuslist = focuslist;

        }

        @Override
        public int getCount() {
            if (focuslist == null) {
                return 0;
            }
            return focuslist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_businessmessage, parent, false);
                holder = new ViewHolder();
                holder.face = (CircleImageView) convertView.findViewById(R.id.face_civ);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.content_mesage = (TextView) convertView.findViewById(R.id.content_mesage);
                holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
                holder.xinxi_iv = (ImageView) convertView.findViewById(R.id.xinxi_iv);
                holder.view2 = convertView.findViewById(R.id.view2);
                holder.view1 = convertView.findViewById(R.id.view1);
                holder.isattention_tv = (TextView) convertView.findViewById(R.id.isattention_tv);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (title.equals("关注列表")) {
                holder.name.setTextColor(Color.parseColor("#49AFEF"));
                holder.time_tv.setVisibility(View.GONE);
                holder.xinxi_iv.setVisibility(View.GONE);
                holder.view1.setVisibility(View.GONE);
                holder.isattention_tv.setVisibility(View.VISIBLE);
                holder.isattention_tv.setBackground(SelectorFactory.newShapeSelector()
                        .setDefaultBgColor(Color.parseColor("#ffffff"))
                        .setCornerRadius(Util.dpToPx(context, 3))
                        .setStrokeWidth(Util.dpToPx(context, 1))
                        .setDefaultStrokeColor(context.getResources().getColor(R.color.maincolor))
                        .create());
            } else {
                holder.view2.setVisibility(View.GONE);
            }

            FocusBean.ListBean listBean = focuslist.get(position);


            try {
                Picasso.with(context).load(listBean.getUserFace()).into(holder.face);
            } catch (Exception e) {

            }

            holder.name.setText(listBean.getUserId());

            if (!Util.isNull(listBean.getDongtai())) {

                if (title.equals("关注列表")) {
                    holder.content_mesage.setSingleLine(true);
                    holder.content_mesage.setMaxEms(11);
                    holder.content_mesage.setEllipsize(TextUtils.TruncateAt.END);
                    holder.content_mesage.setText(listBean.getDongtai());
                } else {
                    holder.content_mesage.setText(listBean.getDongtai());
                }


            } else {
                holder.content_mesage.setText("还未发布过信息");
            }


            return convertView;
        }


    }

    class ViewHolder {
        CircleImageView face;
        TextView name, content_mesage, time_tv;
        ImageView xinxi_iv;
        View view1, view2;
        TextView isattention_tv;

    }


    class UnReadadapter extends BaseAdapter {

        private Context context;

        private List<UnReadMessageBean.CommentsBean> comments;

        public UnReadadapter(Context context, List<UnReadMessageBean.CommentsBean> comments) {

            this.context = context;
            this.comments = comments;

        }

        @Override
        public int getCount() {
            if (comments == null) {
                return 0;
            }

            return comments.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_businessmessage, parent, false);
                holder = new ViewHolder();
                holder.face = (CircleImageView) convertView.findViewById(R.id.face_civ);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.content_mesage = (TextView) convertView.findViewById(R.id.content_mesage);
                holder.time_tv = (TextView) convertView.findViewById(R.id.time_tv);
                holder.xinxi_iv = (ImageView) convertView.findViewById(R.id.xinxi_iv);
                convertView.setTag(holder);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            UnReadMessageBean.CommentsBean listBean = comments.get(position);


            try {
                Picasso.with(context).load(listBean.getFace()).into(holder.face);
            } catch (Exception e) {

            }

            holder.name.setText(listBean.getCommentMessage());
            holder.content_mesage.setText(listBean.getMoodMessage());
            holder.time_tv.setText(Util.friendly_time(listBean.getCommentCreateTime()));

            String[] imlist = listBean.getMoodFiles().split("\\*\\|\\-_\\-\\|*");

            if (imlist.length >= 1) {
                String url = "http://img.yda360.com//" + imlist[0].replace("mood_", "");
                Picasso.with(context).load(url).into(holder.xinxi_iv);
            } else {
                holder.xinxi_iv.setVisibility(View.GONE);
            }

            return convertView;
        }
    }


}
