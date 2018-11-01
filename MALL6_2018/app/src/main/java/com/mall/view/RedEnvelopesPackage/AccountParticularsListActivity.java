package com.mall.view.RedEnvelopesPackage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.VideoAudioDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.RedAccountBean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.ShowPopWindow;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.account_details)
public class AccountParticularsListActivity extends AppCompatActivity {

    private Context context;

    private MyAdapter myadapter;

    @ViewInject(R.id.accountDetailsView)
    private ListView accountDetailsView;

    @ViewInject(R.id.topCenter)
    private TextView topCenter;

    @ViewInject(R.id.detail_header)
    private View detail_header;

    @ViewInject(R.id.type)
    public TextView type_tv;

    @ViewInject(R.id.number)
    public TextView number_tv;

    @ViewInject(R.id.time_tv)
    public TextView time_tv;

    @ViewInject(R.id.yu_e)
    public TextView yu_e;
    @ViewInject(R.id.top_top)
    private View top_top;

    private String title = "";
    private String type_ = "";

    String url = "";

    List<RedAccountBean.ListBean> redlist = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        String str = intent.getStringExtra("title");
        if (!Util.isNull(str)) {
            title = str;
        }
        context = this;
        ViewUtils.inject(this);
        top_top.setBackgroundColor(getResources().getColor(R.color.red));
        inint();

    }

    private void inint() {
        initview();
        initData();

    }


    @OnClick({R.id.topback})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.topback:
                finish();
                break;
        }
    }

    String host = "red_box.aspx";

    private void initview() {
        detail_header.setVisibility(View.GONE);
        topCenter.setText(title + "明细");

        if (title.equals("我的购物券")) {
            url = "getShopping_voucherAccount";
            host = "carpool.aspx";
        } else if (title.equals("我的拼车券")) {
            url = "getCarpool_couponsAccount";
            host = "carpool.aspx";
        } else if (title.equals("红包豆账户")) {
            url = "getRedbeanAccount";
        } else if (title.equals("消费券账户")) {
            url = "GetConsumptionVolume";
        } else if (title.equals("现金券账户")) {
            url = "getRed_boxAccount";
        } else if (title.equals("充值币账户")) {
            url = "getRed_boxRechargePoint";
        } else if (title.equals("提现")||title.equals("提现账户")) {
            detail_header.setVisibility(View.VISIBLE);
            url = "getuser_tx_list";
            type_tv.setText("审核状态");
            number_tv.setText("提现金额");
            yu_e.setText("提现方式");
            time_tv.setText("申请时间");
            type_ = getIntent().getStringExtra("type_");//1、业务账户提现，2、现金红包提现，3、现金券提现,4提现账户明细

        }


        myadapter = new MyAdapter(context, redlist);
        accountDetailsView.setAdapter(myadapter);
        accountDetailsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View mContentView = LayoutInflater.from(context).inflate(R.layout.addbankidinstructions, null);
                mContentView.
                        setBackground(SelectorFactory.newShapeSelector()
                                .setDefaultBgColor(Color.parseColor("#ffffff"))
                                .setStrokeWidth(Util.dpToPx(context, 1))
                                .setCornerRadius(Util.dpToPx(context, 5))
                                .setDefaultStrokeColor(Color.parseColor("#bf767675"))
                                .create()
                        );

                int with = (int) (Util.getScreenWidth() * 0.8);
                TextView titlename = (TextView) mContentView.findViewById(R.id.titlename);
                TextView message = (TextView) mContentView.findViewById(R.id.message);
                titlename.setText("温馨提示");
                message.setText(redlist.get(position).getComments());
                final PopupWindow mPopUpWindow = ShowPopWindow.showShareWindow(mContentView, context, with, ViewGroup.LayoutParams.WRAP_CONTENT, 0);
                TextView closepopwind = (TextView) mContentView.findViewById(R.id.close_popwind);

                closepopwind.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mPopUpWindow.dismiss();
                    }
                });

            }
        });

    }

    private void initData() {

        getHongbaodouList(url);
    }


    private void getHongbaodouList(String call) {

        final CustomProgressDialog cpd = Util.showProgress("明细查询中...", this);


        NewWebAPI.getNewInstance().getWebRequest("/" + host + "?call=" + call + "&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&Page=" + 1 + "&pageSize=" + 999 + "&enumType=" + "" + "&starTime=" +
                        "" + "&endTime=" + "" + "&type_=" + type_
                ,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", context);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }
                        Gson gson = new Gson();
                        RedAccountBean redAccountBean = gson.fromJson(result.toString(), RedAccountBean.class);

                        List<RedAccountBean.ListBean> list = redAccountBean.getList();
                        redlist.clear();
                        ;
                        redlist.addAll(list);
                        myadapter.notifyDataSetChanged();


                    }

                    @Override
                    public void requestEnd() {
                        if (cpd != null) {
                            cpd.cancel();
                            cpd.dismiss();
                        }
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });
    }

    class MyAdapter extends BaseAdapter {
        private Context context;
        private List<RedAccountBean.ListBean> redlist;

        public MyAdapter(Context context, List<RedAccountBean.ListBean> redlist) {
            this.context = context;
            this.redlist = redlist;
        }

        @Override
        public int getCount() {

            if (redlist == null) {
                return 0;
            }

            return redlist.size();
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
            if (title.equals("提现")) {
                convertView = LayoutInflater.from(context).inflate(R.layout.account_list_item_1, parent, false);
                TextView type = (TextView) convertView.findViewById(R.id.type);
                TextView number = (TextView) convertView.findViewById(R.id.number);
                TextView yu_e = (TextView) convertView.findViewById(R.id.yu_e);
                TextView time_tv = (TextView) convertView.findViewById(R.id.time_tv);
                final RedAccountBean.ListBean listBean = redlist.get(position);
                type.setText(listBean.getType());
                number.setText(listBean.getCashcost());
                yu_e.setText(listBean.getTx_type());
                time_tv.setText(listBean.getDate());
            } else {
                convertView = LayoutInflater.from(context).inflate(R.layout.account_list_item, parent, false);
                View itemLine = convertView.findViewById(R.id.itemLine);
                final RedAccountBean.ListBean listBean = redlist.get(position);
                itemLine.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        VideoAudioDialog dialog = new VideoAudioDialog(context);
                        dialog.showcancel(View.GONE).showContent(View.GONE).showtag1(View.GONE).showtag2(View.GONE)
                                .showdialogtag3(View.VISIBLE)
                                .showdialogtag4(View.VISIBLE)
                                .showdialogtag5(View.GONE)
                                .showdialogtag6(View.VISIBLE)
                                .showlinview1(View.GONE)
                                .showtime(View.VISIBLE);


                        dialog.setSure("确认");
                        dialog.setTitle("详细信息");
                        dialog.settime(listBean.getDate());

                        dialog.setTextline3a(Util.justifyString("类型:", 5));
                        dialog.setTextline3b(listBean.getType());
                        dialog.setTextline4a(Util.justifyString("金额:", 5));
                        dialog.setTextline4b(listBean.getIncome());

                        if (!Util.isNull(listBean.getOrderid())) {
                            dialog.showdialogtag5(View.VISIBLE);
                            dialog.setTextline5a(Util.justifyString("交易单号:", 5));
                            dialog.setTextline5b(listBean.getOrderid());
                        }


                        dialog.setTextline6a(Util.justifyString("交易描述:", 5));
                        dialog.setTextline6b(listBean.getDetail());

                        dialog.show();
                    }
                });


                TextView detailItemType = (TextView) convertView.findViewById(R.id.detailItemType);
                detailItemType.setText(redlist.get(position).getType());
                TextView detailItemMoney = (TextView) convertView.findViewById(R.id.detailItemMoney);
                if (!Util.isNull(listBean.getIncome())) {
                    if (listBean.getIncome().indexOf("-") != -1) {
                        detailItemMoney.setTextColor(Color.parseColor("#E21918"));
                        detailItemMoney.setText(redlist.get(position).getIncome());
                    } else {
                        detailItemMoney.setTextColor(Color.parseColor("#85B84F"));
                        detailItemMoney.setText("+" + redlist.get(position).getIncome());
                    }
                }
                TextView detailItemDate = (TextView) convertView.findViewById(R.id.detailItemDate);
                detailItemDate.setText(redlist.get(position).getDate());
            }

            return convertView;
        }
    }
}
