package com.mall.view.carMall;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.List;

public class MyOrderNumberActivity extends AppCompatActivity {

    @ViewInject(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewInject(R.id.loadline)
    RelativeLayout loadline;
    @ViewInject(R.id.progressBarLarge)
    ProgressBar progressBar;
    @ViewInject(R.id.day)
    TextView day;

    List<CarpoolBean.ListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_number);
        ViewUtils.inject(this);
        Util.initTitle(this, "我的单号", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initData();
    }

    private void initData() {
        User user = UserData.getUser();
        final CustomProgressDialog cpd = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Carpool" + "&page=1&size=999&search=" + user.getUserId(),
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }
                        Gson gson = new Gson();
                        CarpoolBean carpoolBean = gson.fromJson(result.toString(), CarpoolBean.class);
                        if (!carpoolBean.getCode().equals("200") || carpoolBean.getList() == null) {
                            Util.show(carpoolBean.getMessage());
                            initData1();
                            return;
                        }
                        list.clear();
                        list.addAll(carpoolBean.getList());
                        if (list.size() > 0) {
                            recyclerView.setVisibility(View.VISIBLE);
                            loadline.setVisibility(View.GONE);
                        } else {
                            loadline.setVisibility(View.VISIBLE);

                            recyclerView.setVisibility(View.GONE);
                            initData1();
                        }
                        recyclerView.setLayoutManager(new LinearLayoutManager(MyOrderNumberActivity.this, LinearLayoutManager.VERTICAL, false));
                        recyclerView.setAdapter(new MyOrderNumberAdapter(list));
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

    private void initData1() {
        final CustomProgressDialog cpd = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "GET_CAR_INFO" + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            return;
                        }
                        Gson gson = new Gson();
                        CarInfoBean carpoolBean = gson.fromJson(result.toString(), CarInfoBean.class);
                        if (!carpoolBean.getCode().equals("200") || Util.isNull(carpoolBean.getList())
                                || carpoolBean.getList().size() == 0
                                ) {
                            return;
                        }
                        CarInfoBean.ListBean listBean = (CarInfoBean.ListBean) carpoolBean.getList().get(0);
                        progressBar.setProgress(15 - Integer.parseInt(listBean.getWITE_DAY()));
                        day.setText("排队单号将在" + (Integer.parseInt(listBean.getWITE_DAY())) + "天后自动生成，请耐心等待！");
                        if (Integer.parseInt(listBean.getWITE_DAY()) == 0) {
                            day.setText("排队单号将在24小时后自动生成，请耐心等待！");
                        }

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

    public class MyOrderNumberAdapter extends RecyclerView.Adapter<MyOrderNumberAdapter.ViewHolder> {

        private List<CarpoolBean.ListBean> list;

        public MyOrderNumberAdapter(List<CarpoolBean.ListBean> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_order_number_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.number.setText(list.get(position).getOrderid());
            holder.date.setText(list.get(position).getDate());
            holder.type.setText(list.get(position).getType() + "万档");
            if (list.get(position).getState().equals("0")) {
                holder.state.setBackgroundResource(R.drawable.corner_3dp_red_shape);
                holder.state.setText("排队中");
            } else if (list.get(position).getState().equals("1")) {
                holder.state.setBackgroundResource(R.drawable.corner_3dp_blue_shape);
                holder.state.setText("已出车");
            } else if (list.get(position).getState().equals("2")) {
                holder.state.setBackgroundResource(R.drawable.corner_3dp_blue_shape);
                holder.state.setText("已提车");
            }
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView number;
            TextView date;
            TextView type;
            TextView state;

            public ViewHolder(View itemView) {
                super(itemView);
                number = itemView.findViewById(R.id.number);
                date = itemView.findViewById(R.id.date);
                type = itemView.findViewById(R.id.type);
                state = itemView.findViewById(R.id.state);
            }
        }
    }
}
