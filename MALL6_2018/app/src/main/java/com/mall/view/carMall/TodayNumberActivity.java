package com.mall.view.carMall;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.Util;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.List;

public class TodayNumberActivity extends AppCompatActivity {

    @ViewInject(R.id.recyclerView)
    RecyclerView recyclerView;
    int date = 0;//1今日

    List<CarpoolBean.ListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_number);
        ViewUtils.inject(this);
        String title = getIntent().getStringExtra("title");
        if (title.equals("今日单号")) {
            date = 1;
        }
        Util.initTitle(this, title, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        final CustomProgressDialog cpd = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Carpool" + "&page=1&size=999&search=&date=" + date,
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Gson gson = new Gson();
                        CarpoolBean carpoolBean = gson.fromJson(result.toString(), CarpoolBean.class);
                        if (!carpoolBean.getCode().equals("200") || carpoolBean.getList() == null || carpoolBean.getList().size() == 0) {
                            Util.show("暂无数据");
                            return;
                        }
                        list.clear();
                        list.addAll(carpoolBean.getList());
                        recyclerView.setLayoutManager(new LinearLayoutManager(TodayNumberActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new TodayNumberAdapter(list));
                    }

                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );

    }

    public class TodayNumberAdapter extends RecyclerView.Adapter<TodayNumberAdapter.ViewHolder> {

        private List<CarpoolBean.ListBean> list;

        public TodayNumberAdapter(List<CarpoolBean.ListBean> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.today_number_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            holder.userName.setText(Util.protectionUserName(list.get(position).getUserId()));
            holder.type.setText(list.get(position).getType() + "万档");
            holder.number.setText(list.get(position).getOrderid());
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView number;
            TextView userName;
            TextView type;

            public ViewHolder(View itemView) {
                super(itemView);
                number = itemView.findViewById(R.id.number);
                userName = itemView.findViewById(R.id.userName);
                type = itemView.findViewById(R.id.type);
            }
        }
    }
}
