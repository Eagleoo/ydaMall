package com.mall.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnItemClick;
import com.mall.model.PhoneOrdersBean;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class PhoneOrders extends Activity {
    @ViewInject(R.id.phone_list)
    private ListView phone_list;
    private Dialog dialog;
    private PopupWindow distancePopup = null;
    private int currentPageShop = 0;
    private PhoneOrdersAdapter phoneOrdersAdapter;
    private List<PhoneOrdersBean> phoneOrdersBeans = new ArrayList<PhoneOrdersBean>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_orders);
        ViewUtils.inject(this);
        Util.initTitle(this, "手机订单", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (phoneOrdersAdapter == null) {
            phoneOrdersAdapter = new PhoneOrdersAdapter(this);
            phone_list.setAdapter(phoneOrdersAdapter);
        }
        firstpageshop();
        scrollPageshop();
    }

    /**
     * 初始化并弹出popupwindow
     *
     */
    private void startPopupWindow(PhoneOrdersBean ordersBean) {
        View pview = getLayoutInflater().inflate(R.layout.phone_orders_dialog,
                null, false);
        ViewUtils.inject(pview);
        TextView top_back = (TextView) pview.findViewById(R.id.top_back);
        TextView order_number = (TextView) pview.findViewById(R.id.order_number);
        TextView order_time = (TextView) pview.findViewById(R.id.order_time);
        TextView phone_number = (TextView) pview.findViewById(R.id.phone_number);
        TextView mianzhi = (TextView) pview.findViewById(R.id.mianzhi);
        TextView pay_money = (TextView) pview.findViewById(R.id.pay_money);
        TextView pay_type = (TextView) pview.findViewById(R.id.pay_type);
        TextView pay_state = (TextView) pview.findViewById(R.id.pay_state);
        TextView order_sstate = (TextView) pview.findViewById(R.id.order_sstate);
        order_number.setText(ordersBean.getOrderId());
        order_time.setText(ordersBean.getOrderTime());
        phone_number.setText(ordersBean.getMobilePhone());
        mianzhi.setText("￥" + ordersBean.getMianzhi());
        pay_money.setText("￥" + ordersBean.getPayTotal());
        pay_type.setText(ordersBean.getPayType());
        pay_state.setText(ordersBean.getPsyState());
        order_sstate.setText(ordersBean.getOrderState());
        top_back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                distancePopup.dismiss();
            }
        });
        initpoputwindow(pview);
    }

    private void getPopupWindow() {
        if (distancePopup != null && distancePopup.isShowing()) {
            distancePopup.dismiss();
        }
    }

    /**
     * 新建一个popupwindow实例
     *
     * @param view
     */
    private void initpoputwindow(View view) {
        distancePopup = new PopupWindow(view,
                android.view.WindowManager.LayoutParams.MATCH_PARENT,
                android.view.WindowManager.LayoutParams.WRAP_CONTENT, true);
        distancePopup.setOutsideTouchable(true);
        distancePopup.setFocusable(true);
        distancePopup.setBackgroundDrawable(new BitmapDrawable());
        distancePopup.setAnimationStyle(R.style.popupanimation);
    }

    @OnItemClick({R.id.phone_list})
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        // TODO Auto-generated method stub
        getPopupWindow();
        startPopupWindow(phoneOrdersBeans.get(position));
        distancePopup.showAtLocation(view, 0, 0, 0);
    }

    private void getPhoneOrders() {
        Util.asynTask(this, "正在加载数据", new IAsynTask() {
            @SuppressWarnings("unchecked")
            @Override
            public void updateUI(Serializable runData) {
                if (runData != null) {
                    int index = 0;
                    List<PhoneOrdersBean> list = new ArrayList<PhoneOrdersBean>();
                    list = ((HashMap<Integer, List<PhoneOrdersBean>>) runData)
                            .get(index++);
                    phoneOrdersAdapter.setList(list);
                    phoneOrdersBeans.addAll(list);
                } else {
                    Toast.makeText(PhoneOrders.this, "操作失败，请检查网络是否连接后，再试一下",
                            Toast.LENGTH_LONG).show();
                }
            }

            @SuppressLint("UseSparseArrays")
            @Override
            public Serializable run() {
                int index = 0;
                Web web = new Web(Web.getServicePhoneOrder,
                        "userid=" + UserData.getUser().getUserId()
                                + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&page="
                                + (++currentPageShop) + "&size=" + 20);
                List<PhoneOrdersBean> list = web.getList(PhoneOrdersBean.class);
                HashMap<Integer, List<PhoneOrdersBean>> map = new HashMap<Integer, List<PhoneOrdersBean>>();
                map.put(index++, list);
                return map;
            }

				/*@Override
                public void onError(Throwable e) {
					Toast.makeText(PhoneOrders.this, "操作失败，请检查网络是否连接后，再试一下",
							Toast.LENGTH_LONG).show();
				}*/
        });
    }

    public void firstpageshop() {
        getPhoneOrders();
    }

    public void scrollPageshop() {
        phone_list.setOnScrollListener(new OnScrollListener() {
            int lastItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= phoneOrdersAdapter.getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    getPhoneOrders();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }
}
