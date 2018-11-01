package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.MessageBoardCommentModel;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.Web;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.util.CircleImageView;
import com.mall.util.IAsynTask;
import com.mall.util.ListViewForScrollView;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.FaceConversionUtil;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * 商圈评论
 */

@ContentView(R.layout.activity_comments_page)
public class CommentsPageActivity extends AppCompatActivity {

    @ViewInject(R.id.commentarieslist_lfs)
    ListViewForScrollView commentarieslist;

    @ViewInject(R.id.et_sendmessage1)
    EditText inputText;

    @ViewInject(R.id.btn_send)
    Button btn;

    @ViewInject(R.id.username)
    private TextView username;
    @ViewInject(R.id.areaitem_cv)
    private CircleImageView areaitem;

    @ViewInject(R.id.time_tv)
    private TextView time;

    @ViewInject(R.id.csg_images)
    private NineGridImageView csg;

    @ViewInject(R.id.message_tv)
    private TextView message_tv;


    Myadapter myadapter;

    private int page = 0;
    private User user;


    Context context;
    ArrayList<String> strlist = new ArrayList<>();
    private ArrayList<MessageBoardCommentModel> list = new ArrayList<MessageBoardCommentModel>();

    private String id = "";
    private String userId = "";
    private String Type = "";
    private UserMessageBoard umb;

    private boolean iszhijiepinglun = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        Util.initTitle(this, "评论", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        init();
        Util.kfg = "1";
    }

    private void init() {
        initdata();
        initadpter();
        bindData(true);

    }

    private void initadpter() {
        myadapter = new Myadapter(context, list);
        commentarieslist.setAdapter(myadapter);
    }

    private void initdata() {
        user = UserData.getUser();
        Intent intent = getIntent();
        id = this.getIntent().getStringExtra("id");
        userId = this.getIntent().getStringExtra("userId");
        umb = (UserMessageBoard) getIntent().getSerializableExtra(
                "UserMessageBoard");

        username.setText(umb.getUserId());

        SpannableString spannableString = FaceConversionUtil.getInstace()
                .getExpressionString(context, umb.getContent());


        message_tv.setText(spannableString);

        Type = umb.getType();


        try {
            Log.e("用户头像", user.getUserFace() + "KK");
            Picasso.with(context).load(umb.getUserFace()).into(areaitem);
        } catch (Exception e) {
            Log.e("加载用户头像失败", e.toString());
        }

        time.setText(Util.friendly_time(umb
                .getCreateTime()));
        String picfiles = umb.getFiles();
        if (!picfiles.equals("")) {
            String[] str;
            if (Type.equals("111")) {
                str = picfiles.split("\\*\\|-_-\\|\\*");
            } else {
                str = picfiles.split("\\|,\\|");
            }

            final List<String> piclist = Arrays.asList(str);

            csg.setAdapter(new NineGridImageViewAdapter<String>() {
                @Override
                protected void onDisplayImage(Context context, ImageView imageView, String s) {
                    Log.e("要加载的地址", s + "JJJJJJJ");
                    if (Type.equals("111")) {
                        Picasso.with(context)
                                .load("http://img.yda360.com//" + s)
                                .error(R.drawable.ic_launcher)
                                .placeholder(R.drawable.ic_launcher)
                                .into(imageView);
                    } else {
                        Picasso.with(context)
                                .load(s)
                                .error(R.drawable.ic_launcher)
                                .placeholder(R.drawable.ic_launcher)
                                .into(imageView);
                    }


                }

                @Override
                protected ImageView generateImageView(Context context) {
                    return super.generateImageView(context);
                }

                @Override
                protected void onItemImageClick(Context context, ImageView imageView, int index, List list) {
                    super.onItemImageClick(context, imageView, index, list);

                    Log.e("图片点击", index + "KK");

                    ArrayList<String> arrayList = new ArrayList<String>();
                    for (int i = 0; i < list.size(); i++) {
                        arrayList.add(piclist.get(i));
                    }


                    new PicViewpagerPopup(context, arrayList, index, true, null);
                }

            });
            if (piclist.size() != 0) {
                Log.e("加载图片", "显示");
                csg.setImagesData(piclist);
            }
        } else {
            csg.setVisibility(View.GONE);
        }


        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");

    }

    @OnClick({R.id.btn_send, R.id.header})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.btn_send:
                if (null == UserData.getUser()) {
                    Util.show("您还没登录，请先登录！", context);
                    Util.showIntent(context, LoginFrame.class);
                    return;
                }
                if (Util.isNull(inputText.getText().toString())) {
                    Util.show("请输入评论内容", context);
                    return;
                }
                DoComment();
                break;
            case R.id.header:
                inputText.setHint("回复楼主");
                inputText.setTag(-7, "-1");
                userId = id;
                iszhijiepinglun = true;
                inputText.setTag(-8, userId);
                break;
        }
    }

    private void DoComment() {
        Util.asynTask(context, "正在提交评论", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                String result = (String) runData;
                if ("success".equals(result)) {
                    Util.show("评论成功", context);
                    inputText.setText("");
                    inputText.setHint("请输入您的评论...");
                    inputText.setTag(-7, "-1");
                    userId = id;
                    iszhijiepinglun = true;
                    inputText.setTag(-8, userId);
                    page = 0;
                    if (myadapter != null) {
                        myadapter.list.clear();
                    }
                    bindData(true);
                }
            }

            @Override
            public Serializable run() {
                String param = "";
                if (iszhijiepinglun) {
                    param = "mid=" + id
                            + "&userId=" + UserData.getUser().getUserId()
                            + "&toUserId=" + userId + "&message="
                            + inputText.getText().toString() + "&userPaw="
                            + UserData.getUser().getMd5Pwd();
                } else {
                    param = "mid=" + id
                            + "&userId=" + UserData.getUser().getUserId()
                            + "&toUserId=" + userId + "&message="
                            + inputText.getText().toString() + "&userPaw="
                            + UserData.getUser().getMd5Pwd() + "&parentId=" + inputText.getTag(-7);
                }


                Web web = new Web(Web.addUserMessageBoardComment, param);
                String result = web.getPlan();
                return result;
            }
        });
    }


    private void bindData(final boolean isfrist) {
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                @SuppressWarnings("unchecked")
                HashMap<String, List<MessageBoardCommentModel>> map = (HashMap<String, List<MessageBoardCommentModel>>) runData;
                List<MessageBoardCommentModel> resultList = map.get("list");

                if (isfrist) {
                    list.clear();
                }
                list.addAll(resultList);
                myadapter.notifyDataSetChanged();


            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getUserMessageBoardCommentByID, "id="
                        + id + "&size=999" + "&page=" + (++page) + "&loginUser="
                        + user.getUserId());
                List<MessageBoardCommentModel> result = web
                        .getList(MessageBoardCommentModel.class);
                HashMap<String, List<MessageBoardCommentModel>> map = new HashMap<String, List<MessageBoardCommentModel>>();
                map.put("list", result);
                return map;
            }
        });
    }


    class Myadapter extends BaseAdapter {
        private Context context;
        private ArrayList<MessageBoardCommentModel> list;

        public Myadapter(Context context, ArrayList<MessageBoardCommentModel> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            if (list == null) {
                return 0;
            }
            return list.size();
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
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_businesscirclecomments, parent, false);
                holder = new ViewHolder();
                holder.userface = (CircleImageView) convertView.findViewById(R.id.userface_civ);
                holder.username = (TextView) convertView.findViewById(R.id.username_tv);
                holder.content = (TextView) convertView.findViewById(R.id.content_tv);
                holder.floorsnumber = (TextView) convertView.findViewById(R.id.floorsnumber_tv);
                holder.time = (TextView) convertView.findViewById(R.id.time_tv);
                holder.rootitem = convertView.findViewById(R.id.rootitem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final MessageBoardCommentModel mbcm = list.get(position);

            try {
                Picasso.with(context).load(mbcm.getUserFace()).into(holder.userface);
            } catch (Exception e) {
                holder.userface.setImageResource(R.drawable.new_huiyuan_logo);
            }
            holder.username.setText(mbcm.getUserId());
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .getExpressionString(context, mbcm.getMessage().replace("[]", "").replaceFirst("：", ""));

            holder.content.setText(spannableString);
            holder.floorsnumber.setText(mbcm.getExp2());
            holder.time.setText(Util.friendly_time(mbcm.getCreateTime()));
            final String hint = Util.getMood_No_pUserId(mbcm.getUserId());
            holder.rootitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!mbcm.getUserId().equals(user.getNoUtf8UserId())) {
                        //inputText.setHint("回复"+msg.getExp2()+"：");
                        //有昵称显示 否则显示几楼
                        if (!Util.isNull(hint))
                            inputText.setHint("回复" + hint + "：");

                        else
                            inputText.setHint("回复" + mbcm.getExp2() + "：");
                        inputText.setTag(-7, mbcm.getId());
                        inputText.setTag(-8, mbcm.getUserId());
                        userId = mbcm.getUserId();
                        iszhijiepinglun = false;
                    } else if (mbcm.getUserId().equals(user.getNoUtf8UserId())) {
                        inputText.setHint("回复:");
                    }
                }
            });

            return convertView;
        }
    }

    public class ViewHolder {
        LinearLayout item;
        TextView title;
        CircleImageView userface;
        TextView username, content, floorsnumber;
        TextView time;
        TextView lou;
        View rootitem;
    }
}
