package com.mall.view.BusinessCircle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.mall.BasicActivityFragment.BaseV4Fragment;
import com.mall.model.User;
import com.mall.model.messageboard.UserMessageBoard;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.view.messageboard.MyToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import me.dkzwm.widget.srl.MaterialSmoothRefreshLayout;
import me.dkzwm.widget.srl.RefreshingListenerAdapter;

/**
 * Created by Administrator on 2017/11/29.
 */

public class BusinessCirleListFragment extends BaseV4Fragment {
    @BindView(R.id.businesslist)
    public android.support.v7.widget.RecyclerView businesslist;

    @BindView(R.id.smoothRefreshLayout_with_recyclerView)
    public MaterialSmoothRefreshLayout mRefreshLayout;

//    @BindView(R.id.backtop_iv)
//            public ImageView backtop;

    LinearLayoutManager myLinearmanager;

    MyBusinessRecyclerViewAdapter myBusinessRecyclerViewAdapter;
//    Myadapter myadpter;

    private int page = 1;

    private int size = 10;

    private String zoneid = "";

    private String circlename = "";

    private List<UserMessageBoard> userlist = new ArrayList<UserMessageBoard>();

    private User user;

    public void  setPage(int page){
        this.page=page;
    }

    public static BusinessCirleListFragment newInstance() {
        BusinessCirleListFragment fragment = new BusinessCirleListFragment();
        return fragment;
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_businesslist;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {

        init();
    }

    private void init() {
        user = UserData.getUser();

        initadapter();
    }


    private void initadapter() {

        myLinearmanager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false);
        myBusinessRecyclerViewAdapter=new MyBusinessRecyclerViewAdapter(userlist,context,circlename);
        businesslist.setLayoutManager(myLinearmanager);
        businesslist.setAdapter(myBusinessRecyclerViewAdapter);
        businesslist.addOnScrollListener(new RecyclerView.OnScrollListener() {
            //dx是水平滚动的距离，dy是垂直滚动距离，向上滚动的时候为正，向下滚动的时候为负
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);



                int firstVisibleItemPosition=myLinearmanager.findFirstVisibleItemPosition();//可见范围内的第一项的位置
                int lastVisibleItemPosition=myLinearmanager.findLastVisibleItemPosition();//可见范围内的最后一项的位置
                int itemCount=myLinearmanager.getItemCount();//recyclerview中的item的所有的数目

                Log.e("RecyclerView滑动监听","firstVisibleItemPosition"+firstVisibleItemPosition
                +"lastVisibleItemPosition"+lastVisibleItemPosition+"itemCount"+itemCount
                );

//                if (firstVisibleItemPosition>1){
//                    backtop.setVisibility(View.VISIBLE);
//                    backtop.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            businesslist.smoothScrollToPosition(0);
//                        }
//                    });
//                }else {
//                    backtop.setVisibility(View.GONE);
//                }
            }
        });
        myBusinessRecyclerViewAdapter.setOnItemClickListener(new MyBusinessRecyclerViewAdapter.OnItemClickListener(){

            @Override
            public void onItemClick(View view, int position) {
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
        mRefreshLayout.setDisableLoadMore(false);
        mRefreshLayout.materialStyle();
        mRefreshLayout.setEnableScrollToBottomAutoLoadMore(true);
        mRefreshLayout.setOnRefreshListener(new RefreshingListenerAdapter(){
            @Override
            public void onRefreshBegin(boolean isRefresh) {
                if (isRefresh){
                    listrefreshmore(true,zoneid);
                }else{
                    listrefreshmore(false,zoneid);
                }
                mRefreshLayout.refreshComplete();
            }
        });

    }

    public void  refresh(String zone,String circlename){
        this.circlename=circlename;
        this.zoneid=zone;
        if (mRefreshLayout!=null){
            mRefreshLayout.autoRefresh(true);
        }

    }

    public  void  listrefreshmore(boolean isrefresh,String zone){
        this.zoneid=zone;
        if (isrefresh){
            page=1;
            //刷新
        }else {
            page++;
            //加载
        }
        getUserPostList(false,zoneid);
    }


    @Override
    public void onDes() {

    }

    /**
     * @param isShowProgressDialog 是否显示dialog
     */
    public void getUserPostList(final boolean isShowProgressDialog, final String zoneid) {

        IAsynTask asynTask = new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", context);
                    return;
                }
                HashMap<String, List<UserMessageBoard>> map = (HashMap<String, List<UserMessageBoard>>) runData;
                List<UserMessageBoard> list1 = map.get("list");


                Log.e("page","page"+page+"list1"+list1.size());

                if (page==1) {
                    if (list1!=null||list1.size()==0){
                        MyToast.makeText(context,"该城市暂未开通",1);
                    }
                    userlist.clear();
                }
                userlist.addAll(list1);
                Log.e("数据来源长度",userlist.size()+"LK");
                myBusinessRecyclerViewAdapter.setCirclename(circlename);
                myBusinessRecyclerViewAdapter.notifyDataSetChanged();

            }

            @Override
            public Serializable run() {
                String userId = "";
                if (null != user)
                    userId = user.getUserId();
                Web web = new Web(Web.getAllUserMessageBoard_bycity, "userId=" + userId + "&zoneid=" + zoneid + "&page=" + (page) + "&size=" + size + "&loginUser=");
                List<UserMessageBoard> list = web.getList(UserMessageBoard.class);
                HashMap<String, List<UserMessageBoard>> map = new HashMap<String, List<UserMessageBoard>>();
                map.put("list", list);
                return map;
            }
        };

        if (isShowProgressDialog)
            Util.asynTask(context, "正在获取地方商圈动态...", asynTask);
        else
            Util.asynTask(asynTask);

    }

}
