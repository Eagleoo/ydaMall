package com.mall.view.BusinessCircle;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jaeger.ninegridimageview.NineGridImageView;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.model.BusinessMessage;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.CircleImageView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.LoginFrame;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.messageboard.FaceConversionUtil;
import com.mall.view.messageboard.MyToast;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

/**extends RecyclerView.ViewHolderAdministrator on 2017/11/29.
 */

public class MyBusinessRecyclerViewAdapter extends RecyclerView.Adapter<MyBusinessRecyclerViewAdapter.ViewHolder> implements

        View.OnClickListener {
    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    private OnItemClickListener mOnItemClickListener = null;
    List<UserMessageBoard> myuserlist;

    Context context;

    private User user;

    public void setCirclename(String circlename) {
        this.circlename = circlename;
    }

    private String circlename;

    public MyBusinessRecyclerViewAdapter(List<UserMessageBoard> userlist, Context context, String circlename) {
        myuserlist = userlist;
        this.context = context;
        this.circlename = circlename;
        user = UserData.getUser();
    }

    public void setData(List<UserMessageBoard> redpakagelist) {

        if (myuserlist != null) {
            myuserlist.clear();
        }
        myuserlist.addAll(redpakagelist);
        notifyDataSetChanged();
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_arealist, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final UserMessageBoard postmessage = myuserlist.get(position);
        holder.itemView.setTag(position);
        holder.area_mtv.setText(circlename + "圈");

        holder.time_tv.setText(Util.friendly_time(postmessage
                .getCreateTime()));
        holder.isthumbup.setText(postmessage.getPraise());
        holder.leavemessage.setText(postmessage.getComments());


        if (postmessage.getGuanzhu().equals("1")) {
            holder.isattentiontv.setText("已关注");
            holder.isattentiontv.setTextColor(context.getResources().getColor(R.color.maincolor));
            holder.isattentiontv.setBackground(SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#ffffff"))
                    .setCornerRadius(Util.dpToPx(context, 3))
                    .setStrokeWidth(Util.dpToPx(context, 1))
                    .setDefaultStrokeColor(context.getResources().getColor(R.color.maincolor))
                    .create());
        } else {
            holder.isattentiontv.setText("+关注");
            holder.isattentiontv.setTextColor(Color.parseColor("#C0C0C0"));
            holder.isattentiontv.setBackgroundResource(R.drawable.boxwhite);
        }


        if ("1".equals(postmessage.getPraiseState())) {
            holder.isthumbup.setImag(R.drawable.message_board_praise_click);

        } else {
            holder.isthumbup.setImag(R.drawable.shoucang);

        }


        String picfiles = postmessage.getFiles();
        holder.username.setText(postmessage.getUserId());

        SpannableString spannableString = FaceConversionUtil.getInstace()
                .getExpressionString(context, postmessage.getContent());
        holder.contenttext.setText(spannableString);

        if (!postmessage.getUserFace().equals("")) {
            Picasso.with(context)
                    .load(postmessage.getUserFace())
                    .error(R.drawable.ic_launcher)
                    .placeholder(R.drawable.ic_launcher)
                    .into(holder.areaitempic);
        } else {
            holder.areaitempic.setImageResource(R.drawable.new_huiyuan_logo);
        }
        holder.areaitempic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != user) {
                    Intent intent = new Intent(context, BusinessPersonSpaceActivity.class);
                    intent.putExtra("userId", postmessage.getUserId());
                    intent.putExtra("userface", postmessage.getUserFace());
                    context.startActivity(intent);
                } else {
                    Util.showIntent("对不起，请先登录！", context, LoginFrame.class);
                }
            }
        });

        final ViewHolder finalHolder = holder;

        holder.isthumbup_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseClick(v, finalHolder, postmessage);
            }
        });


        holder.sharebusinessinfo_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Util.isNull(postmessage.getId())) {
                    fenxiangClick(postmessage);
                }
            }
        });

        holder.isattentiontv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String str = ((TextView) view).getText().toString();
                if (str.equals("已关注")) {
                    deleteFriendfaceintion(view, postmessage.getUserId());
                } else {
                    NewWebAPI.getNewInstance().addFriend(postmessage.getUserId(), UserData.getUser().getUserId(), UserData.getUser().getMd5Pwd(),
                            new WebRequestCallBack() {
                                @Override
                                public void success(Object result) {


                                    if (result == null) {
                                        return;
                                    }

                                    JSONObject json = JSON.parseObject(result.toString());

                                    String message = json.getString("message");

                                    Log.e("String message", "KK" + message);


                                    if (message != null && message.equals("添加好友请求已发送！")) {
                                        Toast.makeText(context, "已经关注了对方", Toast.LENGTH_LONG).show();

                                        ((TextView) view).setText("已关注");
                                        ((TextView) view).setTextColor(Color.parseColor("#E21918"));
                                        ((TextView) view).setBackgroundResource(R.drawable.boxred);
//                                        page = 1;
//                                        getUserPostList(false, zoneid);
                                    } else {
                                        Toast.makeText(context, "关注失败", Toast.LENGTH_LONG).show();
                                    }

                                }

                                @Override
                                public void fail(Throwable e) {
                                    LogUtils.e("网络请求错误：", e);
                                }

                                @Override
                                public void timeout() {
                                    LogUtils.e("网络请求超时！");
                                }

                                public void requestEnd() {

                                }
                            }
                    );
                }


            }
        });

        if (postmessage.getUserId().equals(user.getUserId())) {
            holder.isattentiontv.setVisibility(View.VISIBLE);
            holder.isattentiontv.setText("删除");
            holder.isattentiontv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDialog(postmessage, position);
                }
            });
        } else {
            holder.isattentiontv.setVisibility(View.VISIBLE);
        }

        if (!picfiles.equals("")) {
            String[] str = picfiles.split("\\|,\\|");

            final List<String> piclist = Arrays.asList(str);

            holder.csg_images.setVisibility(View.VISIBLE);


            holder.csg_images.setAdapter(new NineGridImageViewAdapter<String>() {


                @Override
                protected void onDisplayImage(Context context, ImageView imageView, String s) {
                    Log.e("要加载的地址", s + "JJJJJJJ");
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
                LinearLayout.LayoutParams layoutParams;
                if (piclist.size() == 2 || piclist.size() == 4) {
                    layoutParams = new LinearLayout.LayoutParams((int) (Util.aa * 0.6), LinearLayout.LayoutParams.WRAP_CONTENT);
                } else {
                    layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                }
                layoutParams.setMargins(Util.dpToPx(context, 16), Util.dpToPx(context, 16), Util.dpToPx(context, 16), Util.dpToPx(context, 16));
                holder.csg_images.setLayoutParams(layoutParams);

                holder.csg_images.setImagesData(piclist);
            }

        } else {
            holder.csg_images.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        int num = myuserlist == null ? 0 : myuserlist.size();
        return num;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.csg_images)
        NineGridImageView csg_images;
        @BindView(R.id.username)
        TextView username;
        @BindView(R.id.areaitem_cv)
        CircleImageView areaitempic;

        @BindView(R.id.contenttext)
        TextView contenttext;

        @BindView(R.id.isthumbup_mt)
        MoreTextView isthumbup;

        @BindView(R.id.isthumbup_rl)
        View isthumbup_rl;

        @BindView(R.id.isattention_tv)
        TextView isattentiontv;

        @BindView(R.id.leavemessage_mt)
        MoreTextView leavemessage;
        @BindView(R.id.leavemessage_rl)
        View leavemessage_rl;
        @BindView(R.id.sharebusinessinfo)
        MoreTextView sharebusinessinfo;

        @BindView(R.id.sharebusinessinfo_rl)
        View sharebusinessinfo_rl;

        @BindView(R.id.time_tv)
        TextView time_tv;
        @BindView(R.id.area_mtv)
        MoreTextView area_mtv;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
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

    private void deleteFriendfaceintion(final View view, String friendid) {
        NewWebAPI.getNewInstance().deleteFriend(friendid, UserData.getUser().getUserId(), UserData.getUser().getMd5Pwd(),
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {

                        if (result == null) {
                            return;
                        }

                        JSONObject json = JSON.parseObject(result.toString());

                        if (200 == json.getIntValue("code")) {
                            Toast.makeText(context, "取消关注", Toast.LENGTH_SHORT).show();

                            ((TextView) view).setText("+关注");
                            ((TextView) view).setTextColor(Color.parseColor("#C0C0C0"));
                            ((TextView) view).setBackgroundResource(R.drawable.boxwhite);

//                            page = 1;
//                            getUserPostList(false, zoneid);

                        } else {
                            Toast.makeText(context, json.getString("message"), Toast.LENGTH_SHORT).show();
                        }


                        Log.e("返回数据", result.toString() + "PP");

                    }

                    @Override
                    public void fail(Throwable e) {
                        LogUtils.e("网络请求错误：", e);
                    }

                    @Override
                    public void timeout() {
                        LogUtils.e("网络请求超时！");
                        Util.show("小二很忙，系统很累，请稍候...", App.getContext());
                    }

                    public void requestEnd() {

                    }
                });
    }

    private void deleteDialog(final UserMessageBoard umf, final int position) {
        final VoipDialog dialog = new VoipDialog("是否需要删除心情", context, "确定", "否",
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                        if (Util.isNull(umf.getId())) {
//                            try {
//                                db.deleteById(UserMessageBoardCache.class,
//                                        umf.getDateaId());
//                                map.clear();
//                                list.remove(position);
//                                XQAdapter.this.notifyDataSetChanged();
//                            } catch (DbException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
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
                                            myuserlist.remove(position);
                                            EventBus.getDefault().post(new BusinessMessage("deletseccess"));
//                                            myadpter
//                                                    .notifyDataSetChanged();
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
