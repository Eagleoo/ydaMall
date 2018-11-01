package com.mall.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.Message;
import com.mall.net.Web;
import com.mall.util.AsynTask;
import com.mall.util.Util;
import com.mall.view.databinding.ActivityCustomerServiceBinding;
import com.mall.view.databinding.CommonproblemitemBinding;
import com.mall.view.databinding.MoreproblemitemBinding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomerServiceActivity extends AppCompatActivity {
    ActivityCustomerServiceBinding mBinding;
    List<Message> grstrings = new ArrayList<>();
    List<Message> linstrings = new ArrayList<>();
    MyAdapter grAdapter;
    MyAdapter linAdapter;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_customer_service);
        mBinding.setVariable(com.mall.view.BR.activity, this);
        initrecycle();


    }

    public void gotoQQ() {
        if (isQQClientAvailable(this)) {
//            String qqUrl = "mqqwpa://im/chat?chat_type=wpa&uin=480992951&version=1";
            String qqUrl = "mqq://im/chat?chat_type=wpa&uin=480992951&version=1&src_type=web&web_src=oicqzone.com";


            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(qqUrl)));
        } else {
            Toast.makeText(this, "请先安装QQ", Toast.LENGTH_LONG).show();
        }

    }

    public static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    public void click(View v) {
        Intent intent = new Intent(this, FeedbackFrame.class);
        startActivity(intent);
    }

    public void callphone() {
        Util.doPhone(Util._400, this);
    }

    private void initrecycle() {
        GridLayoutManager grmgr = new GridLayoutManager(this, 2);
        mBinding.moreproblem.setLayoutManager(grmgr);
        grAdapter = new MyAdapter(this, grstrings, 1);
        mBinding.moreproblem.setAdapter(grAdapter);
        grAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                grstrings.get(position).getTitle();
                Intent intent = new Intent(mContext, problemDetailsActivity.class);
                String title="";
                if (grstrings.get(position).getTitle().contains(",")){
                    title=grstrings.get(position).getTitle().split(",")[0];
                }else {
                    title=grstrings.get(position).getTitle();
                }

                intent.putExtra("title", title);
                intent.putExtra("newsid", Integer.parseInt(grstrings.get(position).getId()));
                startActivity(intent);
            }


        });

        LinearLayoutManager linmgr = new LinearLayoutManager(this);
        mBinding.commonproblem.setLayoutManager(linmgr);
        linAdapter = new MyAdapter(this, linstrings, 0);
        mBinding.commonproblem.setAdapter(linAdapter);
        linAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                Intent intent = new Intent(mContext, problemDetailsActivity.class);
                intent.putExtra("title", linstrings.get(position).getTitle());
                intent.putExtra("newsid", Integer.parseInt(linstrings.get(position).getId()));
                startActivity(intent);
            }


        });

        HashMap<String, Integer> grmgrHashMap = new HashMap<>();
        grmgrHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 10);//top间距

        grmgrHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, 10);//底部间距

        grmgrHashMap.put(RecyclerViewSpacesItemDecoration.LEFT_DECORATION, 20);//左间距

        grmgrHashMap.put(RecyclerViewSpacesItemDecoration.RIGHT_DECORATION, 20);//右间距

        mBinding.moreproblem.addItemDecoration(new RecyclerViewSpacesItemDecoration(grmgrHashMap));

        HashMap<String, Integer> linmgrHashMap = new HashMap<>();
        linmgrHashMap.put(RecyclerViewSpacesItemDecoration.TOP_DECORATION, 1);//top间距
        mBinding.commonproblem.addItemDecoration(new RecyclerViewSpacesItemDecoration(linmgrHashMap));
        getNetDate();
    }

    int typeid = 223;
    int typeidmore = 225;

    public void getNetDate() {
        Util.asynTask(mContext, "加载中...", new AsynTask() {

            public Serializable run() {

                Web web = new Web(Web.getAllArticle_kfzx, "typeid=" + typeid + "&pageSize=" + 9999 + "&page=" + 1);
                List<Message> list = web.getList(Message.class);
                HashMap<String, List<Message>> map = new HashMap<String, List<Message>>();
                map.put("list", list);
                return map;
            }


            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", mContext);
                    return;
                }
                HashMap<String, List<Message>> map = (HashMap<String, List<Message>>) runData;
                List<Message> list1 = map.get("list");

                linstrings.clear();
                if (list1 != null && list1.size() > 0) {
                    linstrings.addAll(list1);
                    Log.e("添加成功", "linstrings" + linstrings.size());
                    linAdapter.notifyDataSetChanged();
                }
            }


        });
        Util.asynTask(mContext, "加载中...", new AsynTask() {

            public Serializable run() {

                Web web = new Web(Web.getAllArticle_kfzx, "typeid=" + typeidmore + "&pageSize=" + 9999 + "&page=" + 1);
                List<Message> list = web.getList(Message.class);
                HashMap<String, List<Message>> map = new HashMap<String, List<Message>>();
                map.put("list", list);
                return map;
            }


            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", mContext);
                    return;
                }
                HashMap<String, List<Message>> map = (HashMap<String, List<Message>>) runData;
                List<Message> list1 = map.get("list");

                grstrings.clear();
                if (list1 != null && list1.size() > 0) {
                    grstrings.addAll(list1);
                    Log.e("添加成功", "grstrings" + grstrings.size());
                    grAdapter.notifyDataSetChanged();
                }
            }


        });
    }

    public class MyAdapter extends BaseRecycleAdapter<Message> {

        int type = 0;

        protected MyAdapter(Context context, List<Message> list, int type) {
            super(context, list);
            this.type = type;
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, List<Message> list, int position) {
            Message str = list.get(position);
            if (mBinding instanceof MoreproblemitemBinding) {
                mBinding.getRoot().setBackground(SelectorFactory.newShapeSelector()
                        .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                        .setPressedBgColor(Color.parseColor("#FFC7C6C6"))
                        .setStrokeWidth(Util.dpToPx(mContext, 1))
                        .setDefaultStrokeColor(Color.parseColor("#DDDDDD"))
                        .create());
                mBinding.setVariable(com.mall.view.BR.adapter, this);
                if (str.getTitle().contains(",")) {
                    mBinding.setVariable(com.mall.view.BR.item1, str.getTitle().split(",")[0]);
                    mBinding.setVariable(com.mall.view.BR.item2, str.getTitle().split(",")[1]);
                } else {
                    mBinding.setVariable(com.mall.view.BR.item1, str.getTitle());
                }

            } else {
                ((CommonproblemitemBinding) mBinding).commonTitle
                        .setBackground(SelectorFactory.newShapeSelector()
                                .setDefaultBgColor(Color.parseColor("#FFFFFF"))
                                .setPressedBgColor(Color.parseColor("#FFC7C6C6"))
                                .create());
                mBinding.setVariable(com.mall.view.BR.adapter, this);
                mBinding.setVariable(com.mall.view.BR.item1, str.getTitle());
            }


        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            if (viewType == 0) {
                return DataBindingUtil.inflate(mInflater, R.layout.commonproblemitem, parent, false);
            } else {
                return DataBindingUtil.inflate(mInflater, R.layout.moreproblemitem, parent, false);

            }

        }

        @Override
        public int setShowRule(int position) {
            return type;
        }

    }

}
