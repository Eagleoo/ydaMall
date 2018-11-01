package com.mall.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LMSJComment;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ContentView(R.layout.activity_shop_appraise_list)
public class ShopAppraiseListActivity extends AppCompatActivity {

    @ViewInject(R.id.appraiselist)
    private ListView appraiselist;

    LMSJCommentAdapter myadapter;

    private Context context;

    private  List<LMSJComment> mylist=new ArrayList<>();
    private String lid="";
    private String shopid="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        ViewUtils.inject(this);
        init();
    }

    private void init() {

        initadapter();
        initdata();
        initlisten();
    }

    private void initlisten() {
        appraiselist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("点击了",position+"LLL");
            }
        });
    }

    private void initadapter() {
        myadapter=new LMSJCommentAdapter(context,mylist);

    }

    private void initdata() {

        lid = this.getIntent().getStringExtra("lid");
        shopid = this.getIntent().getStringExtra("userid1");
        readComment();
    }



    private void readComment() {
        Util.asynTask(this, "正在获取网友评论...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<LMSJComment>> map = (HashMap<String, List<LMSJComment>>) runData;
                List<LMSJComment> list1 = map.get("list");
                mylist.clear();
                mylist.addAll(list1);
                appraiselist.setAdapter(myadapter);
                myadapter.notifyDataSetChanged();

            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getLMSJCommentPage, "page=" + 1
                        + "&size=999&id=" + lid);
                HashMap<String, List<LMSJComment>> map = new HashMap<String, List<LMSJComment>>();
                map.put("list", web.getList(LMSJComment.class));
                return map;
            }
        });

    }

    @OnClick({R.id.top_back})
    private void click(View view){
        switch (view.getId()){
            case R.id.top_back:
                finish();
                break;
        }
    }

    class LMSJCommentAdapter extends BaseAdapter {

        private List<LMSJComment> list;
        private Context context;
        private LayoutInflater flater;
        public LMSJCommentAdapter(Context context, List<LMSJComment> list) {
            super();
            this.context = context;
            this.list = list;
            flater = LayoutInflater.from(context);
        }

       



        @Override
        public int getCount() {
            Log.e("数据长度",list.size()+"LLL");
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == list || 0 == list.size())
                return convertView;
            final LMSJComment model = list.get(position);
            LMSJCommentHolder holder = null;
            if (null == convertView) {
                convertView = flater.inflate(R.layout.lmsj_comment_list_item, null);
                holder = new LMSJCommentHolder();
                holder.rootitem=convertView.findViewById(R.id.rootitem);
                holder.user = (TextView) convertView
                        .findViewById(R.id.lmsj_comment_list_item_user);
                holder.date = (TextView) convertView
                        .findViewById(R.id.lmsj_comment_list_item_date);
                holder.star = (RatingBar) convertView
                        .findViewById(R.id.lmsj_comment_list_item_star);
                holder.content = (TextView) convertView
                        .findViewById(R.id.lmsj_comment_list_item_content);
                holder.callback= (TextView) convertView.findViewById(R.id.callbackmessage);
                convertView.setTag(holder);
            } else
                holder = (LMSJCommentHolder) convertView.getTag();

            holder.user.setText(model.getUser());
            holder.date.setText(model.getDate());
            holder.star.setRating(Float.parseFloat(model.getScore()));
            holder.star.setIsIndicator(true);
            holder.content.setText(model.getContent());
            holder.callback.setText(model.getExp4());


            return convertView;
        }
    }

    class LMSJCommentHolder {
        public TextView user;
        public TextView date;
        public RatingBar star;
        public TextView content,callback;
        public  View rootitem;
    }

}
