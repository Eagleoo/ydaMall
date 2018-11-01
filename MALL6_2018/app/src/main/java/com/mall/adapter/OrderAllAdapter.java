package com.mall.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.model.OrderOne;
import com.mall.model.OrderTwo;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.OrderFrame;
import com.mall.view.PayMoneyFrame;
import com.mall.view.PayMoneyFrame1;
import com.mall.view.R;
import com.mall.view.carMall.OrderBeanAll;
import com.mall.yyrg.adapter.MyListView;

import java.io.Serializable;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;

public class OrderAllAdapter extends NewBaseAdapter<OrderOne> {

    private OrderDealSuccessTwoOrderAdapter adapter;

    private String photo;

    private String sid;

    public OrderAllAdapter(Context c, List list) {
        super(c, list);
        this.list = list;

    }

    public void setList(List<OrderOne> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final OrderOne one = list.get(position);
        if (null == one)
            return convertView;
        if ("1".equals(one.orderStatus)) {// 待付款
            ViewHolder vh = null;
            convertView = null;

            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.new_order_fukuan_item, null);
                vh.one_dingdan = convertView.findViewById(R.id.one_dingdan2);
                vh.time = convertView.findViewById(R.id.time);
                vh.all_num = convertView.findViewById(R.id.all_number);
                vh.all_money = convertView.findViewById(R.id.all_money);
                vh.order_cancel = convertView.findViewById(R.id.order_cancel);
                vh.all_jifen = convertView.findViewById(R.id.all_jifen);
                vh.order_friend = convertView.findViewById(R.id.order_friend);
                vh.order_pay = convertView.findViewById(R.id.order_pay);
                vh.Listview_fukuan_two = convertView.findViewById(R.id.Listview_fukuan_two);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.one_dingdan.setText("订单号：" + one.orderId);
            vh.all_jifen
                    .setText("可用消费券" + one.integral.replace(".00", "") + "分");
            vh.time.setText(one.date.subSequence(0, one.date.lastIndexOf(":")));
            vh.all_money.setText("￥" + one.cost.replace(".00", ""));
            String arrlist = one.secondOrder;
            String oderType=one.ordertype;
            if (!Util.isNull(arrlist)) {
                List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
                if (twos.size() > 0) {

                    OrderFuKuanTwo adaptertwo = new OrderFuKuanTwo(context,
                            twos,oderType);
                    vh.Listview_fukuan_two.setAdapter(adaptertwo);
                    vh.all_num.setText("共" + twos.size() + "件商品");
                    photo = twos.get(0).productThumb;
                    sid = twos.get(0).secondOrderId;


                }
            }
            // 取消
            vh.order_cancel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    cancelOrder(sid, position);
                }
            });
            // 朋友代付
            vh.order_friend.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//					PayByFriend(sid, one, photo);
                    PayByFriend(one.orderId, one, photo);

                }
            });
            // 立即付款
            vh.order_pay.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//					PayNow(sid);
                    PayNow(one.orderId);
                }
            });
            // 删除订单
            if (null != convertView) {

                convertView.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {
//						delOrder(sid, position);
                        Log.e("order_sid--------", sid + "");
                        Log.e("one.orderId--------", one.orderId + "");
                        delOrder(one.orderId, position);
                        return false;
                    }
                });
            }

        } else if (one.orderStatus.equals("2") || one.orderStatus.equals("3")) {// 待发收货

            ViewHolder vh = null;
            convertView = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.new_order_shouhuo_item, null);
                vh.one_dingdan = (TextView) convertView
                        .findViewById(R.id.one_dingdan);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.all_num = (TextView) convertView.findViewById(R.id.all_num);
                vh.all_money = (TextView) convertView
                        .findViewById(R.id.all_money);
                vh.Listview_two_shouhuo = (MyListView) convertView
                        .findViewById(R.id.Listview_two_shouhuo);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.one_dingdan.setText("订单号:" + one.orderId);
            vh.time.setText(one.date.subSequence(0, one.date.lastIndexOf(":")));
            vh.all_money.setText("￥" + one.cost.replace(".00", ""));
            String arrlist = one.secondOrder;
            String oderType=one.ordertype;
            List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
            if (twos.size() > 0) {
                OrderShouHuoTwo adapter = new OrderShouHuoTwo(context,
                        one.orderId, twos,oderType,oderType);
                vh.Listview_two_shouhuo.setAdapter(adapter);
                vh.all_num.setText("共" + twos.size() + "件商品");

            } else {
                vh.Listview_two_shouhuo.setVisibility(View.GONE);
            }

        } else if (one.orderStatus.equals("4")) {// 成功
            ViewHolder vh = null;
            convertView = null;
            if (convertView == null) {
                vh = new ViewHolder();
                convertView = inflater.inflate(
                        R.layout.item_order_deal_success, null);
                vh.oneOrderCode = (TextView) convertView
                        .findViewById(R.id.item_order_deal_suc_oneorder_code);
                vh.oneOrderAllMoney = (TextView) convertView
                        .findViewById(R.id.item_order_deal_suc_oneorder_allmoney);
                vh.oneOrderTime = (TextView) convertView
                        .findViewById(R.id.item_order_deal_suc_oneorder_time);
                // holder.order_state = (TextView) convertView
                // .findViewById(R.id.order_state);
                vh.line = convertView.findViewById(R.id.xian_);
                vh.item_order_deal_suc_oneorder_delete = (ImageView) convertView
                        .findViewById(R.id.item_order_deal_suc_oneorder_delete);
                vh.oneOrderView = (LinearLayout) convertView
                        .findViewById(R.id.item_order_deal_suc_ll_oneorder_layout);
                vh.oneOrderListView = (ListView) convertView
                        .findViewById(R.id.item_order_deal_listview);
                vh.line.setVisibility(View.GONE);
                vh.oneOrderListView.setVisibility(View.GONE);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }
            vh.oneOrderAllMoney.setText("共计 ￥"
                    + list.get(position).cost.replace(".00", ""));
            vh.oneOrderCode.setText("订单号:" + list.get(position).orderId);
            vh.oneOrderTime.setText(list.get(position).date.subSequence(0,
                    list.get(position).date.lastIndexOf(":")));
            vh.item_order_deal_suc_oneorder_delete
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            delSuccess(position);

                        }
                    });
            vh.oneOrderView.setOnLongClickListener(new OnLongClickListener() {
                private VoipDialog voipDialog;

                @Override
                public boolean onLongClick(View v) {
                    delSuccess(position);
                    return false;
                }
            });

            vh.line.setVisibility(View.VISIBLE);
            vh.oneOrderListView.setVisibility(View.VISIBLE);
            String oderType=one.ordertype;
            adapter = new OrderDealSuccessTwoOrderAdapter(context,oderType);

            vh.oneOrderListView.setAdapter(adapter);
            List<OrderTwo> twoList = new ArrayList<OrderTwo>();
            twoList = JSON.parseArray(list.get(position).secondOrder,
                    OrderTwo.class);
            Log.e("子list长度", twoList.size() + "");
            if (null != twoList && twoList.size() > 0) {
                adapter.setList(twoList);
                adapter.UpData();

            }

            setListViewHeightBasedOnChildren(vh.oneOrderListView);

        } else if (one.orderStatus.equals("200")
                || one.orderStatus.equals("300")
                || one.orderStatus.equals("400")) {// 退款 退换货
            ViewHolder vh = null;
            convertView = null;
            if (convertView == null) {
                Log.i("tag", "退换货");
                vh = new ViewHolder();
                convertView = LayoutInflater.from(context).inflate(
                        R.layout.new_order_tuikuan_item, null);
                vh.one_dingdan = (TextView) convertView
                        .findViewById(R.id.one_dingdan);
                vh.time = (TextView) convertView.findViewById(R.id.time);
                vh.all_money = (TextView) convertView
                        .findViewById(R.id.all_money);
                vh.one_daifukuan = (TextView) convertView
                        .findViewById(R.id.one_daifukuan);
                vh.Listview_two_shouhuo = (MyListView) convertView
                        .findViewById(R.id.Listview_two_shouhuo);
                convertView.setTag(vh);
            }
            vh = (ViewHolder) convertView.getTag();
            vh.one_dingdan.setText("订单号:" + one.orderId);
            vh.all_money.setText("退款金额￥" + one.cost.replace(".00", ""));
            if (one.orderStatus.equals("200")) {
                vh.one_daifukuan.setText("退款审核中");
            } else if (one.orderStatus.equals("300")) {
                vh.one_daifukuan.setText("退款成功");
            } else if (one.orderStatus.equals("400")) {
                vh.one_daifukuan.setText("退款失败");
            }
            String arrlist = one.secondOrder;
            String oderType=one.ordertype;
            List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
            if (twos.size() > 0) {
                OrderTuiKuanTwo adapter = new OrderTuiKuanTwo(context,
                        one.orderId, twos,oderType);
                vh.Listview_two_shouhuo.setAdapter(adapter);

            } else {
                vh.Listview_two_shouhuo.setVisibility(View.GONE);
            }

        } else {
            return convertView;
        }

        return convertView;
    }

    class ViewHolder {
        private ImageView iv_gender;
        private TextView one_dingdan;
        private TextView time;
        private TextView all_num;
        private TextView all_money;
        private TextView all_jifen;
        private TextView order_cancel;
        private TextView order_friend;
        private TextView order_pay;
        private MyListView Listview_fukuan_two;
        String tag = "1";
        private View oneOrderView;
        private TextView oneOrderCode;
        private ImageView oneOrderDelete;
        private ImageView item_order_deal_suc_oneorder_delete;
        private TextView oneOrderTime;
        private TextView oneOrderAllMoney;
        private ListView oneOrderListView;
        private View line;
        private MyListView Listview_two_shouhuo;
        private TextView one_daifukuan;

    }

    // 取消订单
    private void cancelOrder(final String orderid, final int position) {
        final VoipDialog voip = new VoipDialog("你当前正在取消订单,您确定要这么做吗？", context, "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                Util.asynTask(context, "正在取消订单...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData + "")) {
                            Util.showIntent("订单取消成功！", context,
                                    OrderFrame.class);
                            list.remove(list.get(position));
                            notifyDataSetChanged();
                        } else {
                            Util.show(runData + "", context);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.quitOrder, "userId="
                                + UserData.getUser().getUserId()
                                + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd()
                                + "&tid=" + orderid);
                        return web.getPlan();
                    }
                });

            }
        }, null);
        voip.show();

    }

    // 朋友代付
    private void PayByFriend(String sid, OrderOne order, String photo) {
        final OnekeyShare oks = new OnekeyShare();

        String username = "";
        try {
            username = URLDecoder.decode(UserData.getUser().getUserId(),
                    "utf-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        // pos邮费 没传 "0"
        // final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
        // + order.orderId + "," + order.cost + "," + pos + "&username=" +
        // username;
        final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + sid + "," + order.cost + "," + "0" + "&username=" + username;
        final String title = "来自[" + username + "]的代付信息";
        oks.setTitle(title);
        oks.setTitleUrl(url);
        oks.setUrl(url);
        oks.setAddress("10086");
        oks.setImageUrl(photo);
        oks.setComment("快来注册吧");
        oks.setText("我在远大云商看中了一件超喜欢的产品，邀请你帮我代付，没钱也任性！嘻嘻！" + "http://"
                + Web.webImage + "/phone/pay1.aspx?p1=" + order.orderId + ","
                + order.cost + "," + "0" + "。〔远大云商〕全国客服热线：" + Util._400);
        oks.setSite("远大云商");
        oks.setSiteUrl("http://" + Web.webImage + "/phone/pay1.aspx?p1=" + sid
                + "," + order.cost + "," + "0");
        oks.setSilent(false);
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(Platform platform, ShareParams paramsToShare) {
                if ("ShortMessage".equals(platform.getName())) {
                    paramsToShare.setImageUrl(null);
                    paramsToShare.setText(paramsToShare.getText() + "\n"
                            + url.toString());
                }
            }
        });
        oks.show(context);

    }

    // 朋友代付
    private void PayNow(String orderifd) {

//        dasdas
//        Util.showIntent(context, PayMoneyFrame.class, new String[]{"tid"},
//                new String[]{orderifd});
        getOrders(orderifd);
    }

    public void getOrders(final String tid) {
        User user = UserData.getUser();
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getUserId());
        map.put("md5Pwd", user.getMd5Pwd());
        map.put("state", "");
        map.put("page", "1");
        map.put("size", "999");
        map.put("orderid", tid);
        final CustomProgressDialog cpd = CustomProgressDialog
                .showProgressDialog(context, "加载中...");
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=getMallOrder", map,
                new WebRequestCallBack() {

                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (null == result) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }
                        JSONObject jsons = JSON.parseObject(result.toString());
                        if (200 != jsons.getIntValue("code")) {
                            Util.show("网络异常,请重试", context);
                            return;
                        }
                        Gson gson = new Gson();
                        OrderBeanAll orderBeanAll = gson.fromJson(result.toString(), OrderBeanAll.class);
                        if (orderBeanAll.getOrder().get(0).getOrdertype().equals("7")) {
                            Util.showIntent(context, PayMoneyFrame1.class, new String[]{"tid"},
                                    new String[]{tid});
                        } else {
                            Util.showIntent(context, PayMoneyFrame.class, new String[]{"tid"},
                                    new String[]{tid});
                        }


                        Util.order = null;
                        Util.order = orderBeanAll;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.dismiss();
                        super.requestEnd();
                    }

                });
    }

    private void delOrder(final String orderid, final int postition) {
        final VoipDialog voipDialog = new VoipDialog("你当前正在删除订单,您确定要这么做吗？", context, "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // voipDialog.cancel();
                // voipDialog.dismiss();

                Map<String, String> map = new HashMap<String, String>();
                map.put("orderId", orderid);
                map.put("userId", UserData.getUser().getUserId());
                map.put("md5Pwd", UserData.getUser().getMd5Pwd());
                NewWebAPI.getNewInstance().getWebRequest(
                        "/YdaOrder.aspx?call=deletMallOrder", map,
                        new WebRequestCallBack() {

                            @Override
                            public void success(Object result) {
                                if (Util.isNull(result)) {
                                    Util.show("网络异常，请重试！", context);
                                    return;
                                }
                                JSONObject json = JSONObject
                                        .parseObject(result.toString());
                                String msg = json.getString("message");
                                if (msg.contains("成功")) {
                                    Util.show("订单删除成功", context);
                                    list.remove(postition);
                                    notifyDataSetChanged();
                                } else if (msg.contains("不存在")) {
                                    Util.show(msg, context);
                                }

                            }

                        });

            }
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });
        voipDialog.show();
    }

    private void delSuccess(final int position) {
        VoipDialog voipDialog;
        voipDialog = new VoipDialog("你当前正在删除订单,您确定要这么做吗？", context, "确定", "取消",
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // voipDialog.cancel();
                        // voipDialog.dismiss();

                        Map<String, String> map = new HashMap<String, String>();
                        map.put("orderId", list.get(position).orderId);
                        map.put("userId", UserData.getUser().getUserId());
                        map.put("md5Pwd", UserData.getUser().getMd5Pwd());
                        NewWebAPI.getNewInstance().getWebRequest(
                                "/YdaOrder.aspx?call=deletMallOrder", map,
                                new WebRequestCallBack() {

                                    @Override
                                    public void success(Object result) {
                                        if (Util.isNull(result)) {
                                            Util.show("网络异常，请重试！", context);
                                            return;
                                        }
                                        JSONObject json = JSONObject
                                                .parseObject(result.toString());
                                        if (200 != json.getIntValue("code")) {
                                            Util.show("网络异常，请重试！", context);
                                            return;
                                        }
                                        String msg = json.getString("message");
                                        if (msg.contains("成功"))
                                            Util.show("订单删除成功", context);
                                        list.remove(position);
                                        notifyDataSetChanged();

                                    }

                                });

                    }
                }, new OnClickListener() {

            @Override
            public void onClick(View v) {
            }

        });
        voipDialog.show();

    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        // 获取ListView对应的Adapter
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            // listAdapter.getCount()返回数据项的数目
            View listItem = listAdapter.getView(i, null, listView);
            // 计算子项View 的宽高
            listItem.measure(0, 0);
            // 统计所有子项的总高度
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
