package com.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.mall.model.OrderOne;
import com.mall.model.OrderTwo;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.view.R;
import com.mall.yyrg.adapter.MyListView;

import java.util.ArrayList;
import java.util.List;

public class OrderShouHuo extends NewBaseAdapter {

    CallBackSH mCallBack;

    public void setCallBackSh(CallBackSH callBack) {
        mCallBack = callBack;
    }

    public interface CallBackSH {
        void doback(String tag, int position);
    }

    private List<OrderOne> list = new ArrayList<OrderOne>();

    public OrderShouHuo(Context c, List<OrderOne> list) {
        super(c, list);
        this.list = list;

    }

    public void setList(List<OrderOne> list) {

        this.list.addAll(list);

        this.notifyDataSetChanged();

    }

    public void clear2() {
        if (this.list.size() > 0)
            this.list.clear();
        // notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {

            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.new_order_shouhuo_item, null);
            vh.one_dingdan = (TextView) convertView
                    .findViewById(R.id.one_dingdan);
            vh.time = (TextView) convertView.findViewById(R.id.time);
            vh.all_num = (TextView) convertView.findViewById(R.id.all_num);
            vh.all_money = (TextView) convertView.findViewById(R.id.all_money);
            vh.Listview_two_shouhuo = (MyListView) convertView
                    .findViewById(R.id.Listview_two_shouhuo);
            convertView.setTag(vh);
        } else {

            vh = (ViewHolder) convertView.getTag();
        }

        OrderOne one = list.get(position);
        vh.one_dingdan.setText("订单号:" + one.orderId);
        vh.time.setText(one.date.substring(0, one.date.lastIndexOf(":")));
        vh.all_money.setText("￥" + one.cost.replace(".00", ""));
        String arrlist = one.secondOrder;
        String order = one.ordertype;
        List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
        if (twos.size() > 0) {
            OrderShouHuoTwo adapter = new OrderShouHuoTwo(context, one.orderId,
                    twos,order,order);

            adapter.setCallBack(new OrderShouHuoTwo.CallBack() {
                @Override
                public void doback(String tag) {
                    if (mCallBack != null) {
                        mCallBack.doback(tag, position);
                    }
                }
            });

            vh.Listview_two_shouhuo.setAdapter(adapter);
            vh.all_num.setText("共" + twos.size() + "件商品");

        } else {
            vh.Listview_two_shouhuo.setVisibility(View.GONE);
        }

        return convertView;
    }

    class ViewHolder {
        private ImageView iv_gender;
        private TextView one_dingdan;
        private TextView time;
        private TextView all_num;
        private TextView all_money;
        private MyListView Listview_two_shouhuo;
    }
}
