package com.mall.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
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

public class OrderFuKuan extends NewBaseAdapter<OrderOne> {
    private Context context;
    private OrderFuKuanTwo adaptertwo;
    private String photo;

    private String sid;

    public OrderFuKuan(Context c, List<OrderOne> list) {
        super(c, list);
        if (null == this.list) {
            this.list = new ArrayList<OrderOne>();
        }
        this.context = c;
        this.list = list;

    }

    public void setList(List<OrderOne> list) {
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    public void clear() {
        if (null != this.list && this.list.size() > 0) {
            this.list.clear();
        }
        // notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.new_order_fukuan_item, null);
            vh.one_dingdan = convertView.findViewById(R.id.one_dingdan2);
            vh.time = convertView.findViewById(R.id.time);
            vh.all_number = convertView.findViewById(R.id.all_number);
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

        final OrderOne order = list.get(position);
        vh.one_dingdan.setText("订单号：" + order.orderId);
        vh.all_jifen
                .setText("(可用消费券" + order.integral.replace(".00", "") + "分)");
        vh.time.setText(order.date.substring(0, order.date.lastIndexOf(":")));
        vh.all_money.setText("￥" + order.cost.replace(".00", ""));
        String arrlist = order.secondOrder;
        String oderType=order.ordertype;
        if (!Util.isNull(arrlist)) {
            List<OrderTwo> twos = JSON.parseArray(arrlist, OrderTwo.class);
            if (twos.size() > 0) {
                photo = twos.get(0).productThumb;
                sid = twos.get(0).secondOrderId;
                adaptertwo = new OrderFuKuanTwo(context, twos,oderType);
                vh.Listview_fukuan_two.setAdapter(adaptertwo);
                vh.all_number.setText("共" + twos.size() + "件商品");

            }
        }

        // 删除订单
        convertView.setOnLongClickListener(new OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//				 delOrder(sid, position);
                delOrder(order.orderId, position);
                return false;
            }
        });
        // 取消订单
        vh.order_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                cancelOrder(sid, position);
//				cancelOrder(order.orderId, position);
            }

        });
        vh.order_friend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PayByFriend(order, photo);
            }

        });
        vh.order_pay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // PayNow(sid);
                PayNow(order.orderId);
            }

        });

        return convertView;

    }

    private void PayNow(String orderifd) {


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

    private void PayByFriend(OrderOne order, String photo) {
        final OnekeyShare oks = new OnekeyShare();

        String username = "";
        try {
            username = URLDecoder.decode(UserData.getUser().getUserId(),
                    "utf-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        // pos邮费 没传 "0"
        final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + order.orderId + "," + order.cost + "," + 0 + "&username="
                + username;
        // final String url = "http://" + Web.webImage + "/phone/pay1.aspx?p1="
        // + sid + "," + order.cost + "," + "0" + "&username=" + username;
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
        oks.setSiteUrl("http://" + Web.webImage + "/phone/pay1.aspx?p1="
                + order.orderId + "," + order.cost + "," + "0");
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
        }, new OnClickListener() {

            @Override
            public void onClick(View v) {

            }

        });
        voip.show();

    }

    private void delOrder(final String orderid, final int postition) {
        final VoipDialog voipDialog = new VoipDialog("你当前正在删除订单,您确定要这么做吗？", context, "确定", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                Map<String, String> map = new HashMap<>();
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

        }, null);
        voipDialog.show();
    }

    class ViewHolder {
        private ImageView iv_gender;
        private TextView one_dingdan;
        private TextView time;
        private TextView all_number;
        private TextView all_money;
        private TextView all_jifen;
        private TextView order_cancel;
        private TextView order_friend;
        private TextView order_pay;
        private MyListView Listview_fukuan_two;
    }
}
