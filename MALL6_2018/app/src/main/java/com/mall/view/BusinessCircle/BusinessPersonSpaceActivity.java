package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.view.VideoAudioDialog;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.Photo;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CircleImageView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.UpdateUserMessageActivity;
import com.mall.view.messageboard.FaceConversionUtil;
import com.mall.view.messageboard.MyToast;
import com.mall.view.messageboard.WriterNewMessageBoardFrame;
import com.pulltorefresh.PullToRefreshListView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

@ContentView(R.layout.activity_business_person_space)
public class BusinessPersonSpaceActivity extends AppCompatActivity {

    @ViewInject(R.id.businesslist)
    private ListView businesslist;

    @ViewInject(R.id.refreshable_view)

    private PullToRefreshListView refreshable_view;

    @ViewInject(R.id.areaheadportrait_cv)
    private CircleImageView areaheadportrait;

    @ViewInject(R.id.areaname_tv)
    private TextView areaname;

    @ViewInject(R.id.funsnumber)
    private TextView funsnumber;


    private User user;

    Myadapter myadpter;

    ArrayList<String> strlist=new ArrayList<>();
    ArrayList<Photo> piclist=new ArrayList<>();

    private ArrayList<UserMessageBoard> userlist = new ArrayList<UserMessageBoard>();


    private Context context;

    private int page=1;

    private int size = 10;
    private String userId="";
    private String userface="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        ViewUtils.inject(this);
        init();
        Util.kfg="1";
    }


    private void init() {
        initdata();
        initadapter();
        initListening();
    }

    private void initListening() {

        refreshable_view.setOnRefreshListener(new PullToRefreshListView.PullToRefreshListener() {
            @Override
            public void onRefresh() {
                //下拉监听
                page=1;
                getMyPostMessage(false);
            }
        },12);

        //上拉加载更多
        businesslist.setOnScrollListener(new AbsListView.OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= myadpter.getCount()
                        && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    getMyPostMessage(true);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    @OnClick({R.id.top_back,R.id.header})
    public void click(View view){
        switch (view.getId()){
            case R.id.top_back:
                finish();
                break;

        }
    }

    private void initadapter() {
        myadpter=new Myadapter(context,userlist);
        businesslist.setAdapter(myadpter);

    }
    boolean isperson;
    String zoneid;
    private void initdata() {
        user = UserData.getUser();
        Intent intent = getIntent();
        userId = intent.getStringExtra("userId");
        userface = intent.getStringExtra("userface");
         isperson = intent.getBooleanExtra("isUser",false);
        zoneid = intent.getStringExtra("zoneid");
        areaname.setText(userId+"");

        try {
            Picasso.with(context)
                    .load(userface)
                    .error(R.drawable.ic_launcher)
                    .placeholder(R.drawable.ic_launcher)
                    .into(areaheadportrait);
        }catch (Exception e){
            areaheadportrait.setImageResource(R.drawable.new_huiyuan_logo);
        }



        getMyPostMessage(true);
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        strlist.add("1");
        piclist.add(new Photo("http://img2.imgtn.bdimg.com/it/u=2974104803,1439396293&fm=200&gp=0.jpg"));
        piclist.add(new Photo("http://img3.cache.netease.com/photo/0003/2012-05-10/8168FJIJ00AJ0003.jpg"));
        piclist.add(new Photo("http://scimg.jb51.net/allimg/121228/2-12122PUK43O.jpg"));
    }

    private void getMyPostMessage(final boolean isShowProgressDialog){
        IAsynTask asynTask = new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", context);
                    return;
                }
                refreshable_view.finishRefreshing();
                HashMap<String, List<UserMessageBoard>> map = (HashMap<String, List<UserMessageBoard>>) runData;

                if(Util.isNull(map)){
                    return;
                }

                List<UserMessageBoard> list1 = map.get("list");
                if (!isShowProgressDialog){
                    userlist.clear();
                }
                if(Util.isNull(list1)){
                    return;
                }
                if (list1.size()==0){
                    if (isperson){
                        if (userlist.size()==0){
                            VideoAudioDialog dialog = new VideoAudioDialog(context);
                            dialog.setTitle("温馨提示");
                            dialog.showcancel(View.GONE);
                            dialog.setRight("立即发布");
                            dialog.setContent("您未发布过信息，立即发布");
                            dialog.setLeft(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (!Util.checkUserInfocomplete()) {
                                        VoipDialog voipDialog = new
                                                VoipDialog("根据政府相关规定，从事互联网业务，需要进行实名登记", context, "立即登记", "稍后登记", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                Util.showIntent(context,
                                                        UpdateUserMessageActivity.class);
                                            }
                                        }, null);
                                        voipDialog.show();
                                        return;
                                    }

                                    Intent intent = new Intent(context, WriterNewMessageBoardFrame.class);
                                    intent.putExtra("zoneid", zoneid);
                                    startActivity(intent);
                                }
                            });
                            dialog.show();
                        }
                    }
                    return;
                }

                userlist.addAll(list1);
                funsnumber.setText("粉丝："+list1.get(0).getGuanzhu());
                myadpter.notifyDataSetChanged();






            }

            @Override
            public Serializable run() {
                String loginUser = "";
                if(null != user)
                    loginUser = user.getUserId();
                Web web = new Web(Web.getUserMessageBoard, "userId=" + userId
                        + "&page=" + (page++) + "&size=" + size+"&loginUser="+loginUser);
                List<UserMessageBoard> list = web.getList(UserMessageBoard.class);
                HashMap<String, List<UserMessageBoard>> map = new HashMap<String, List<UserMessageBoard>>();
                map.put("list", list);
                return map;
            }
        };
        if(isShowProgressDialog)
            Util.asynTask(this, "正在获取网友心情...", asynTask);
        else
            Util.asynTask(asynTask);
    }


    class Myadapter extends BaseAdapter{

        private Context context;

        private ArrayList<UserMessageBoard> userlist;

        public Myadapter(Context context ,ArrayList<UserMessageBoard> strlist){
            this.context=context;
            this.userlist=strlist;

        }

        @Override
        public int getCount() {
            if (userlist==null){
                return 0;
            }

            return userlist.size();
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder=null;
            if (convertView==null){
                convertView = LayoutInflater.from(context).inflate(R.layout.layout_arealist,parent,false);
                holder=new ViewHolder();
                holder.csg_images= (NineGridImageView) convertView.findViewById(R.id.csg_images);
                holder.username= (TextView) convertView.findViewById(R.id.username);
                holder.area_mtv= (TextView) convertView.findViewById(R.id.area_mtv);
                holder.leavemessage_rl=  convertView.findViewById(R.id.leavemessage_rl);
                holder.areaitempic= convertView.findViewById(R.id.areaitem_cv);
                holder.contenttext= (TextView) convertView.findViewById(R.id.contenttext);
                holder.isthumbup= (MoreTextView) convertView.findViewById(R.id.isthumbup_mt);
                holder.isattentiontv= (TextView) convertView.findViewById(R.id.isattention_tv);
                holder.leavemessage= (MoreTextView) convertView.findViewById(R.id.leavemessage_mt);
                holder.sharebusinessinfo= (MoreTextView) convertView.findViewById(R.id.sharebusinessinfo);
                holder.time_tv= (TextView) convertView.findViewById(R.id.time_tv);
                convertView.setTag(holder);



            }else{
                holder = (ViewHolder)convertView.getTag();
            }
            final UserMessageBoard postmessage=userlist.get(position);
            holder.area_mtv.setVisibility(View.GONE);

            holder.time_tv.setText(Util.friendly_time(postmessage
                    .getCreateTime()));
            holder.isthumbup.setText(postmessage.getPraise());
            holder.leavemessage.setText(postmessage.getComments());

//            holder.isattentiontv.setText("已关注");
//            holder.isattentiontv.setTextColor(Color.parseColor("#E21918"));
//            holder.isattentiontv.setBackgroundResource(R.drawable.boxred);

//            holder.isattentiontv.setText("+关注");
//            holder.isattentiontv.setTextColor(Color.parseColor("#C0C0C0"));
//            holder.isattentiontv.setBackgroundResource(R.drawable.boxwhite);

            if ("1".equals(postmessage.getPraiseState())) {
                holder.isthumbup.setImag(R.drawable.message_board_praise_click);

            }else{
                holder.isthumbup.setImag(R.drawable.shoucang);

            }

            holder.leavemessage_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null == user) {
                        Util.showIntent("对不起，请先登录！", context, LoginFrame.class);
                        return;
                    }
                    UserMessageBoard userMessageBoard = userlist.get(position);
                    Intent intent = new Intent(context, CommentsPageActivity.class);
                    intent.putExtra("id", userMessageBoard.getId());
                    intent.putExtra("userId", userMessageBoard.getUserId());
                    Bundle b = new Bundle();
                    b.putSerializable("UserMessageBoard", userMessageBoard);
                    intent.putExtras(b);
                    if (!Util.isNull(userMessageBoard.getId())) {
                        context.startActivity(intent);
                    }
                }
            });


            String picfiles=postmessage.getFiles();
            holder.username.setText(postmessage.getUserId());
            SpannableString spannableString = FaceConversionUtil.getInstace()
                    .getExpressionString(context, postmessage.getContent());
            holder.contenttext.setText(spannableString);

            if (!postmessage.getUserFace().equals("")){
                Picasso.with(context)
                        .load(postmessage.getUserFace())
                        .error(R.drawable.ic_launcher)
                        .placeholder(R.drawable.ic_launcher)
                        .into(holder.areaitempic);
            }else{
                holder.areaitempic.setImageResource(R.drawable.new_huiyuan_logo);
            }

            final ViewHolder finalHolder = holder;
            holder.isthumbup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    praiseClick(v, finalHolder, postmessage);
                }
            });
            String loginUser = "";

                loginUser = user.getUserId();
            if (loginUser.equals(userId)){ //自己
                holder.isattentiontv.setText("删除");

                holder.isattentiontv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteDialog(postmessage, position);
                    }
                });
            }else{

                holder.isattentiontv.setVisibility(View.GONE);

            }



            holder.sharebusinessinfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Util.isNull(postmessage.getId())) {
                        fenxiangClick(postmessage);
                    }
                }
            });

            if (!picfiles.equals("")){
                String [] str=picfiles.split("\\|,\\|");

                final List<String> piclist = Arrays.asList(str);

                holder.csg_images.setVisibility(View.VISIBLE);
                if (piclist.size()!=0){
                    Log.e("加载图片","显示");
                    holder.csg_images.setImagesData(piclist);
                }


                holder.csg_images.setAdapter(new NineGridImageViewAdapter<String>() {


                    @Override
                    protected void onDisplayImage(Context context, ImageView imageView, String s) {
                        Log.e("要加载的地址",s+"JJJJJJJ");
                        Picasso.with(context)
                                .load(s)
                                .error(R.drawable.ic_launcher)
                                .placeholder(R.drawable.ic_launcher)
                                .into(imageView);
                    }

                    @Override
                    protected ImageView generateImageView(Context context) {
                        return super.generateImageView(context);
                    }

                    @Override
                    protected void onItemImageClick(Context context, ImageView imageView, int index, List list) {
                        super.onItemImageClick(context, imageView, index, list);

                        Log.e("图片点击",index+"KK");

                        ArrayList<String> arrayList = new ArrayList<String>();
                        for (int i=0;i<list.size();i++){
                            arrayList.add(piclist.get(i));
                        }


                        new PicViewpagerPopup(context, arrayList, index, true, null);
                    }
                });
            }else{
                holder.csg_images.setVisibility(View.GONE);
            }



            return convertView;
        }

    }
    class ViewHolder{
        NineGridImageView csg_images;
        TextView username;
        CircleImageView areaitempic;
        TextView contenttext,area_mtv;
        MoreTextView isthumbup; //是否点赞  1是关注 0未关注
        TextView isattentiontv; //是否关注  1是关注 0未关注

        MoreTextView leavemessage;//留言

        MoreTextView sharebusinessinfo;  //分享

        TextView time_tv;
        View leavemessage_rl;

    }

    // 点赞
    private void praiseClick(View view, final ViewHolder holder,
                             final UserMessageBoard umb) {
        if (null == user) {
            Util.showIntent("您还没登录，请先登录！", context, LoginFrame.class);
            return;
        }
        view.setEnabled(false);
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", context);
                    return;
                }
                Resources res = context.getResources();
                Drawable praise = null;
                String currPraise = holder.isthumbup.getText()
                        .toString();
                if ("success:已赞".equals(runData + "")) {

                    holder.isthumbup.setText((Util
                            .getInt(currPraise) + 1) + "");

                    holder.isthumbup.setImag(R.drawable.message_board_praise_click);
                } else {

                    holder.isthumbup.setImag(R.drawable.shoucang);

                    holder.isthumbup.setText((Util
                            .getInt(currPraise) - 1) + "");
                }

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.operateUserMessageBoardPraise, "mid="
                        + umb.getId() + "&userId=" + user.getUserId()
                        + "&userPaw=" + user.getMd5Pwd());
                return web.getPlan();
            }
        });
        view.setEnabled(true);
    }

    public void fenxiangClick(UserMessageBoard umb) {
        String url = "";
        final String title = "分享" + Util.getMood_No_pUserId(umb.getUserId())
                + "的心情";
        String imageUrl = "http://www.yda360.com/newPage/130926/images/logo.png", imageFiles = "";
        imageFiles = umb.getFiles();
        if (!Util.isNull(imageFiles)) {
            imageUrl = imageFiles.split("\\|,\\|")[0].replace("mood_", "");
        } else {
            if (!Util.isNull(umb.getUserFace())) {
                imageUrl = umb.getUserFace();
            }
        }
        OnekeyShare oks = new OnekeyShare();
        oks.setTitle("心情分享");
        oks.setTitleUrl(imageUrl);
        oks.setUrl(imageUrl);
        oks.setAddress("10086");
        oks.setText(umb.getContent());
        oks.setSite("远大云商");
        oks.setSiteUrl(url);
        oks.setSilent(false);
        oks.setImageUrl(imageUrl);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, Platform.ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);

                }
            }
        });
        oks.show(context);
    }

    private void deleteDialog(final UserMessageBoard umf, final int position) {
        final VoipDialog dialog = new VoipDialog("是否需要删除心情", context, "确定", "否",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            if (user != null) {
                                final CustomProgressDialog dialog = Util
                                        .showProgress("正在删除", context);
                                NewWebAPI.getNewInstance().deleteMood(
                                        user.getUserId(), user.getMd5Pwd(),
                                        umf.getId(), new WebRequestCallBack() {
                                            @Override
                                            public void success(Object result) {
                                                MyToast.makeText(context, "删除成功", 5)
                                                        .show();
                                                userlist.remove(position);
                                                myadpter
                                                        .notifyDataSetChanged();
                                                dialog.cancel();
                                                dialog.dismiss();
                                                super.success(result);
                                            }
                                        });
                            } else {
                                MyToast.makeText(context, "您还没有登录，请先登录", 25).show();
                            }
//                        }
                    }
                }, null);
        dialog.show();
    }

}
