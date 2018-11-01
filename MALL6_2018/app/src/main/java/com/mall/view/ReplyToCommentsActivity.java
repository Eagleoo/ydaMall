package com.mall.view;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.LMSJComment;
import com.mall.net.Web;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.serving.voip.view.gridview.NoScrollGridView;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ContentView(R.layout.activity_reply_to_comments)
public class ReplyToCommentsActivity extends AppCompatActivity {
    @ViewInject(R.id.usercommentfaceiv)
    private ImageView usercommentfaceiv;
    @ViewInject(R.id.lmsj_comment_list_item_user)
    private TextView user;
    @ViewInject(R.id.lmsj_comment_list_item_star)
    public com.hedgehog.ratingbar.RatingBar star;
    @ViewInject(R.id.lmsj_comment_list_item_date)
    public TextView date;
    @ViewInject(R.id.lmsj_comment_list_item_content)
    public TextView content;
    @ViewInject(R.id.csg_images)
    private NoScrollGridView csg_images;

    @ViewInject(R.id.huifulin)
    private View huifulin;

    @ViewInject(R.id.ed)
    private EditText ed;
    @ViewInject(R.id.submit)
    private TextView submit;
    private Context context;
    LMSJComment model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;
        Util.initTop(this, "联盟商家回复", Integer.MIN_VALUE, null);

        model = (LMSJComment) getIntent().getSerializableExtra("info");
        if (!Util.isNull(model.getUser_face())) {
            Picasso.with(context).load(model.getUser_face()).error(R.drawable.headertwo).into(usercommentfaceiv);
        }

        user.setText(model.getUser());
        star.setStar(Float.parseFloat(model.getScore()));
        date.setText(model.getDate());
        content.setText(model.getContent());
        setImages();
        huifulin.setVisibility(View.GONE);
        ed.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setCornerRadius(Util.dpToPx(context, 3))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());
        submit.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                .setStrokeWidth(Util.dpToPx(context, 1))
                .setDefaultStrokeColor(Color.parseColor("#E1E1E1"))
                .create());


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null == UserData.getUser()) {
                    Util.showIntent("您还没登录是否登录？", context, LoginFrame.class);
                    return;
                }
                if (Util.isNull(ed.getText().toString())) {
                    Util.show("请输入要回复的内容", context);
                }
                Util.asynTask(context, "正在发表您的回复...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if (null == runData)
                            Util.show("评论超时，请重试", context);
                        else if ("success".equals(runData + "")) {
                            finish();
                        } else
                            Util.show(runData + "", context);
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.addLMSJComment, "userid="
                                + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&lmsj=" + model.getLmsj() + "&message="
                                + Util.get(ed.getText().toString()) + "&rating="
                                + 0 + "&parentID=" + model.getId() + "&files=" + "");
                        return web.getPlan();
                    }
                });
            }
        });

    }

    private void setImages() {
        String imagefiles = model.getFiles();
        if (!imagefiles.equals("")) {
            String[] imags = imagefiles.split("\\*\\|-_-\\|\\*");
            Log.e("图片长度1", imags.length + "LLLL");
            Log.e("打印地址", Arrays.toString(imags) + "LLL");
            final List<String> piclist = Arrays.asList(imags);
            Log.e("图片长度2", piclist.size() + "LLLL");
            csg_images.setVisibility(View.VISIBLE);


            csg_images.setAdapter(new BaseAdapter() {
                @Override
                public int getCount() {
                    return piclist.size();
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
                    View view = LayoutInflater.from(context).inflate(R.layout.imageitem, parent, false);
                    ImageView imageView = (ImageView) view.findViewById(R.id.upImageView);

                    int width = Util.getScreenSize(context).getWidth() - 200;
                    Log.e("设置宽度1", "width:" + width);
                    int itemwidth = (int) (width / 5);
////										LinearLayout.LayoutParams
////												para = (LinearLayout.LayoutParams) imageView.getLayoutParams();
                    ViewGroup.LayoutParams
                            para = imageView.getLayoutParams();
                    para.width = itemwidth;
                    para.height = itemwidth;

                    view.setLayoutParams(para);
                    Picasso.with(context)
                            .load("http://img.yda360.com/" + piclist.get(position))
                            .error(R.drawable.ic_launcher)
                            .placeholder(R.drawable.ic_launcher)
                            .into(imageView);
                    return view;
                }
            });

            csg_images.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    ArrayList<String> arrayList = new ArrayList<String>();
                    for (int i = 0; i < piclist.size(); i++) {
                        arrayList.add("http://img.yda360.com/" + piclist.get(i));
                    }

                    new PicViewpagerPopup(context, arrayList, position, true, null);
                }
            });

        } else {
            csg_images.setVisibility(View.GONE);
        }
    }
}
