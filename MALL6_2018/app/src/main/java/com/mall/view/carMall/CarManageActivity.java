package com.mall.view.carMall;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.Column;
import com.mall.model.RedPackageInLetBean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.BusinessCircle.RedBeanAccountActivity;
import com.mall.view.Lin_MainFrame;
import com.mall.view.LoginFrame;
import com.mall.view.ProxySiteFrame;
import com.mall.view.R;
import com.mall.view.RedEnvelopesPackage.RedEnvelopeRechargeActivity;
import com.mall.widget.DragGridView;
import com.mall.widget.GridViewAdapter;

import java.util.ArrayList;
import java.util.List;

public class CarManageActivity extends AppCompatActivity {

    private List<Column> dataSourceList = new ArrayList<>();
    private List<Column> dataList = new ArrayList<Column>() {{
        add(new Column(R.drawable.carmanage_0, "业务介绍", 0, 0));
        add(new Column(R.drawable.carmanage_1, "拼车流程", 1, 0));
        add(new Column(R.drawable.carmanage_2, "购物专区", 2, 0));
        add(new Column(R.drawable.carmanage_3, "我的订单", 3, 0));
        add(new Column(R.drawable.carmanage_4, "出车公示", 4, 0));
        add(new Column(R.drawable.carmanage_5, "提车风采", 5, 0));
        add(new Column(R.drawable.carmanage_6, "我的单号", 6, 0));
        add(new Column(R.drawable.carmanage_7, "今日单号", 7, 0));
        add(new Column(R.drawable.carmanage_8, "排单总表", 8, 0));
        add(new Column(R.drawable.carmanage_9, "我的购物券", 9, 0));
        add(new Column(R.drawable.carmanage_10, "我的拼车券", 10, 0));
    }};
    @ViewInject(R.id.gridView)
    private DragGridView gridView;
    @ViewInject(R.id.czxfq)
    private ImageView czxfq;
    private GridViewAdapter adapter;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_manage);
        ViewUtils.inject(this);
        key = getIntent().getStringExtra("return");
        Util.initTitle(this, "创客专车", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.isNull(key)) {
                    finish();
                } else {
                    Util.showIntent(CarManageActivity.this, Lin_MainFrame.class, new String[]{"toTab"}, new String[]{"usercenter"});
                    finish();
                }
            }
        });
        dataSourceList.addAll(dataList);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AnimeUtil.startAnimation(CarManageActivity.this, view, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void end() {
                        switch (dataSourceList.get(position).getTag()) {
                            case 0:
                                Intent intent0 = new Intent(CarManageActivity.this, WebViewActivity.class);
                                intent0.putExtra("url", Web.imageip + "/phone/car/jsye.html");
                                intent0.putExtra("title", "业务介绍");
                                startActivity(intent0);
                                break;
                            case 1:
                                Intent intent1 = new Intent(CarManageActivity.this, WebViewActivity.class);
                                intent1.putExtra("url", Web.imageip + "/phone/car/flow.html");
                                intent1.putExtra("title", "拼车流程");
                                startActivity(intent1);
                                break;
                            case 2:
                                Util.showIntent(CarManageActivity.this, CarShopActivity.class);
                                break;
                            case 3:
                                Util.showIntent(CarManageActivity.this, MyOrderActivity.class);
                                break;
                            case 4:
                                Util.showIntent(CarManageActivity.this, CarListActivity.class);
                                break;
                            case 5:
                                Util.showIntent(CarManageActivity.this, StyleActivity.class);
                                break;
                            case 6:
                                Util.showIntent(CarManageActivity.this, MyOrderNumberActivity.class);
                                break;
                            case 7:
                                Intent intent4 = new Intent(CarManageActivity.this, TodayNumberActivity.class);
                                intent4.putExtra("title", "今日单号");
                                startActivity(intent4);
                                break;
                            case 8:
                                Intent intent5 = new Intent(CarManageActivity.this, TodayNumberActivity.class);
                                intent5.putExtra("title", "排单总表");
                                startActivity(intent5);
                                break;
                            case 9:
//                                String[] keys = new String[]{"title", "bean", "yeMoney"};
//                                String[] vals = new String[]{"我的购物劵", new RedPackageInLetBean(), "0"};
//                                Util.showIntent(CarManageActivity.this, RedBeanAccountActivity.class, keys, vals);
                                Intent intent9 = new Intent(CarManageActivity.this, RedBeanAccountActivity.class);
                                intent9.putExtra("title", "我的购物券");
                                intent9.putExtra("bean", new RedPackageInLetBean());
                                startActivity(intent9);
                                break;
                            case 10:
                                Intent intent = new Intent(CarManageActivity.this, RedBeanAccountActivity.class);
                                intent.putExtra("title", "我的拼车券");
                                intent.putExtra("bean", new RedPackageInLetBean());
                                startActivity(intent);
                                break;
                        }
                    }

                    @Override
                    public void repeat() {

                    }
                });
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    private void setNoVisible(int tag) {
        for (int i = 0; i < dataSourceList.size(); i++) {
            if (dataSourceList.get(i).getTag() == tag) {
                dataSourceList.remove(i);
                break;
            }
        }
    }

    @OnClick(R.id.czxfq)
    public void click(View view) {


        if (!Util.checkLoginOrNot()) {
            Util.show("请先登录", CarManageActivity.this);
            Util.showIntent(CarManageActivity.this, LoginFrame.class);
            return;
        }

        if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                && !UserData.getUser().getUserLevel().contains("联盟商家")
                ) {

            String str = "仅创客和商家可参与购物拼车";
            new MyPopWindow.MyBuilder(CarManageActivity.this, str, "立即申请", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(CarManageActivity.this, ProxySiteFrame.class);
                    CarManageActivity.this.startActivity(intent);
                }
            }).setColor("#F13232")
                    .setisshowclose(true)
                    .build().showCenter();
        } else {


            if (!UserData.getUser().getUserLevel().contains("城市经理") && !UserData.getUser().getUserLevel().contains("城市总监")
                    && !UserData.getUser().getUserLevel().contains("联盟商家")) {
                String str = "仅创客和商家可参与购物拼车";
                new MyPopWindow.MyBuilder(CarManageActivity.this, str, "立即申请", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(CarManageActivity.this, ProxySiteFrame.class);
                        CarManageActivity.this.startActivity(intent);
                    }
                }).setColor("#F13232")
                        .setisshowclose(true)
                        .build().showCenter();
                return;
            }


            Util.showIntent(CarManageActivity.this, RedEnvelopeRechargeActivity.class,
                    new String[]{"userKey"}, new String[]{"购物券充值"}
            );
        }
    }

    private void initData() {
        dataSourceList.clear();
        dataSourceList.addAll(dataList);

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


                        if (listBean.getIS_CZ().equals("0")) {

                            setNoVisible(6);
                            setNoVisible(7);
                            setNoVisible(8);
                            setNoVisible(9);
                            setNoVisible(10);

                        } else {
                            czxfq.setVisibility(View.GONE);
                            if (listBean.getIS_PC().equals("0")) {
                                setNoVisible(6);
                                setNoVisible(7);
                                setNoVisible(8);
                            } else {
                                if (listBean.getIS_PC_ORDER().equals("0")) {
                                    setNoVisible(7);
                                    setNoVisible(8);
                                } else {

                                }
                            }


                            if (!listBean.getIS_PC().equals("0")) {//是否参与拼车


                                if (listBean.getIS_PC_ORDER().equals("0")) {//是否产生单号
                                    setNoVisible(7);
                                    setNoVisible(8);
                                }

                            }
                        }


                        adapter = new GridViewAdapter(CarManageActivity.this, dataSourceList, 1);
                        gridView.setAdapter(adapter);
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


}
