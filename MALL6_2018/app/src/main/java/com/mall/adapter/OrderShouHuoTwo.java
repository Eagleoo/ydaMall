package com.mall.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.view.VideoAudioDialog;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.PopWindowHelp.BasePopWindow;
import com.mall.QuantityView;
import com.mall.model.OrderTwo;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.query.activity.expressage.ExpressageQueryActivity;
import com.mall.serving.query.activity.expressage.ExpressageQueryResultActivity;
import com.mall.serving.query.model.ExpressageHistroty;
import com.mall.serving.query.model.ExpressageInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.Lin_MainFrame;
import com.mall.view.OrderFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.UserCenterFrame;
import com.mall.view.order.EvaluationOrdersActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrderShouHuoTwo extends NewBaseAdapter {

    private ImageLoader imload;
    private List<OrderTwo> list;
    private String orderid;
    private String comFirstNum = "";
    private String orderType,s;

    private String mType = "0";

    CallBack mCallBack;

    public void setCallBack(CallBack callBack) {
        mCallBack = callBack;
    }

    public interface CallBack {
        void doback(String tag);
    }

    public OrderShouHuoTwo(Context c, String orderid, List list,String o,String s) {
        super(c, list);
        imload = AnimeUtil.getImageLoad();
        this.list = list;
        this.orderid = orderid;
        this.orderType=o;
        this.s=s;
    }

    public OrderShouHuoTwo(Context c, String orderid, List list, String type) {
        super(c, list);
        imload = AnimeUtil.getImageLoad();
        this.list = list;
        this.orderid = orderid;
        this.mType = type;
    }


    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.new_order_shouhuo_two_item, null);
            vh.two_dingdan = (TextView) convertView
                    .findViewById(R.id.two_dingdan);
            vh.two_fahuo = (TextView) convertView.findViewById(R.id.two_fahuo);
            vh.more_tv = (TextView) convertView.findViewById(R.id.more_tv);
            // vh.two_time=(TextView)convertView.findViewById(R.id.two_time);
            vh.two_im = (ImageView) convertView.findViewById(R.id.two_im);
            vh.order_title = (TextView) convertView
                    .findViewById(R.id.order_title);
            vh.order_money = (TextView) convertView
                    .findViewById(R.id.order_money);
            vh.order_num = (TextView) convertView.findViewById(R.id.order_num);
            vh.order_type = (TextView) convertView
                    .findViewById(R.id.order_type);
            vh.two_order_wuliu = (TextView) convertView
                    .findViewById(R.id.two_order_wuliu);
            vh.two_order_quxiao = convertView.findViewById(R.id.two_order_quxiao);
            vh.two_order_querenshouhuo = (TextView) convertView
                    .findViewById(R.id.two_order_querenshouhuo);
            vh.two_im = (ImageView) convertView.findViewById(R.id.two_im);
            vh.quantityView_default = (QuantityView) convertView.findViewById(R.id.quantityView_default);
            vh.quantityrl = convertView.findViewById(R.id.quantityrl);
            vh.stateline = convertView.findViewById(R.id.stateline);
            vh.danhaoline = convertView.findViewById(R.id.danhaoline);

            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }


        final OrderTwo two = list.get(position);

        vh.two_dingdan.setText("子订单号：" + two.secondOrderId);
        if (mType.equals("2")) {
            vh.quantityrl.setVisibility(View.VISIBLE);
            vh.danhaoline.setVisibility(View.GONE);
            for (OrderTwo orderTwo : list) {
                Log.e("数据刷新" + position, "orderTwo" + orderTwo.toString());
            }

            try {
                vh.quantityView_default.setEt(list.get(position).number);
                vh.quantityView_default.quantity = list.get(position).number;
//                vh.quantityView_default.setQuantity(list.get(position).number);
            } catch (Exception e) {
                vh.quantityView_default.setEt(0);
                vh.quantityView_default.quantity = 0;
//                vh.quantityView_default.setQuantity(0);
            }


            vh.stateline.setVisibility(View.GONE);
        } else {
            if (position == list.size() - 1) {
                vh.stateline.setVisibility(View.VISIBLE);
                vh.quantityrl.setVisibility(View.GONE);
            } else {
                vh.quantityrl.setVisibility(View.GONE);
                vh.stateline.setVisibility(View.VISIBLE);
            }

        }

        if (two.secondOrderStatus.equals("2")) { //待发货
            vh.two_order_quxiao.setVisibility(View.VISIBLE);

            vh.two_order_wuliu.setVisibility(View.GONE);
            vh.two_order_querenshouhuo.setVisibility(View.GONE);

            if (two.applystate.equals("1")) {
                vh.two_order_quxiao.setText("处理中");
            } else if (two.applystate.equals("0")) {
                vh.two_order_quxiao.setText("取消订单");
            } else if (two.applystate.equals("2")) {
                vh.two_order_quxiao.setText("处理完毕");
            }


        } else if (two.secondOrderStatus.equals("3")) { //待收货
            vh.two_order_quxiao.setVisibility(View.GONE);
//            vh.two_order_quxiao.setText("申请退换货");

            vh.more_tv.setVisibility(View.VISIBLE);
            vh.more_tv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("点击", "申请退换货");
                    View contentview = LayoutInflater.from(context).inflate(R.layout.itemtext, parent, false);
                    TextView textView = contentview.findViewById(R.id.tv);
                    contentview.setBackground(SelectorFactory.newShapeSelector()
                            .setDefaultBgColor(Color.parseColor("#999999"))
                            .setCornerRadius(Util.dpToPx(context, 1))
                            .create());
                    textView.setText("申请退换货");
                    textView.setTextColor(Color.parseColor("#ffffff"));
                    int with = Util.dpToPx(context, 100);
                    int hight = Util.dpToPx(context, 50);
                    final BasePopWindow popWindow      =  new BasePopWindow.Builder(context, contentview).setWidth(with, hight)
                            .isShowTriangle(true).build();
                    textView.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (mCallBack != null) {
                                mCallBack.doback("2");
                            }
                            popWindow.dismiss();

                        }
                    });
//                    BasePopWindow.Builder(context,contentview).

                    popWindow.show(v);
                }
            });
        } else {
//            vh.two_order_quxiao.setVisibility(View.GONE);
            vh.more_tv.setVisibility(View.GONE);
        }


        vh.two_order_quxiao.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (two.secondOrderStatus.equals("2")) {

                    if (two.applystate.equals("1") || two.applystate.equals("2")) {
                        return;
                    }

                    final VideoAudioDialog dialog = new VideoAudioDialog(context);
                    dialog.showtag1(View.GONE).showtag2(View.GONE).setRight("提交").showContent(View.GONE).showdialogtag7(View.VISIBLE).setTitle("取消原因");
                    dialog.setLeft(new OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Log.e("内容", "ed" + dialog.getRemark());
                            if (!Util.isNull(dialog.getRemark())) {
                                getSuccessOrders(two.secondOrderId, dialog.getRemark());
                            } else {
                                Toast.makeText(context, "请先填写取消原因", Toast.LENGTH_LONG).show();
                            }


                        }
                    });
                    dialog.show();
                }


            }
        });
        Log.e("订单状态", two.secondOrderStatus + "");
//        two.number = Integer.parseInt(two.quantity);

        try {
            vh.quantityView_default.setMaxQuantity(Integer.parseInt(two.quantity));
        } catch (Exception e) {
            vh.quantityView_default.setMaxQuantity(0);
        }

        vh.quantityView_default.setOnQuantityChangeListener(new QuantityView.OnQuantityChangeListener() {
            @Override
            public void onQuantityChanged(int newQuantity, boolean programmatically) {
                Log.e("newQuantity", "newQuantity" + newQuantity);
                two.number = newQuantity;


                if (mCallBack != null) {
                    mCallBack.doback("-1");
                }

            }

            @Override
            public void onLimitReached() {

            }
        });
        if (two.secondOrderStatus.equals("2")) {
            vh.two_fahuo.setText("待发货");
            vh.two_fahuo.setVisibility(View.VISIBLE);
            vh.two_order_querenshouhuo
                    .setBackgroundResource(R.drawable.corner_5dp_white_solide_gray);
        } else if (two.secondOrderStatus.equals("500")) {
            vh.two_fahuo.setText("退换货");
            vh.two_fahuo.setVisibility(View.VISIBLE);
            vh.two_order_querenshouhuo
                    .setBackgroundResource(R.drawable.corner_5dp_white_solide_gray);
        } else if (two.secondOrderStatus.equals("3")) {
            vh.two_fahuo.setText("待收货");
            vh.two_fahuo.setVisibility(View.VISIBLE);
            vh.two_order_querenshouhuo
                    .setBackgroundResource(R.drawable.corner_5dp_white_solide_red);
            vh.two_order_querenshouhuo
                    .setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
//                            VoipDialog voipDialog = new VoipDialog("确认收货成功，参与晒单评价。", context, "确定", "取消", new OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    Intent intent = new Intent(context, EvaluationOrdersActivity.class);
//                                    intent.putExtra("shopinfo",list.get(position));
//                                    context.startActivity(intent);
//                                }
//                            }, new OnClickListener() {
//
//                                @Override
//                                public void onClick(View v) {
//                                    Log.e("确认收获","mCallBack"+(mCallBack==null));
//                                    if (mCallBack != null) {
//                                        mCallBack.doback("4");
//                                    }
//
//                                }
//                            });
//                            voipDialog.show();

                            ShouHuo(orderid, two.secondOrderId, position);
                        }

                    });
            vh.two_order_wuliu.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {


//					v.setClickable(false);
//					expressageSearch(list.get(position).postCpy,
//							list.get(position).postNum, v);

                    Log.e("点击", list.get(position).postNum + "^^^^^^^^^" + list.get(position).postCpy);
                    Intent intent = new Intent(context, ExpressageQueryActivity.class);
                    intent.putExtra("postNumber", list.get(position).postNum);
                    intent.putExtra("postCompany", list.get(position).postCpy);
                    context.startActivity(intent); // 启动Activity

                    // expressageSearch("韵达", "3100843345040");

                }
            });
        }
        vh.two_im.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ProductDeatilFream.class);
                intent.putExtra("url", two.productId);
                intent.putExtra("orderType",orderType);
                context.startActivity(intent);
            }
        });
        imload.displayImage(two.productThumb, vh.two_im,
                AnimeUtil.getImageRectOption());
        vh.order_title.setText(two.productName);
        vh.order_type.setText(two.colorAndSize.replace("颜色：", "")
                .replace("尺码:", "").replace("颜色：，尺码：", "").replace("，尺码：", ""));
        vh.order_money.setText("￥" + two.unitCost.replace(".00", ""));
        vh.order_num.setText("x" + two.quantity);
        return convertView;
    }

    private void expressageSearch(String companyname, String no, final View v) {

        if (!Util.isNetworkConnected(context)) {
            Util.show("请检查网络连接～", context);
            return;
        }
        if (Util.isNull(companyname)) {
            Util.show("快递公司为空－－", context);
            return;
        }
        if (Util.isNull(no)) {
            Util.show("快递号为空－－", context);
            return;
        }
        if (companyname.contains("顺丰")) {
            comFirstNum = "sf";
        } else if (companyname.contains("申通")) {
            comFirstNum = "sto";
        } else if (companyname.contains("圆通")) {
            comFirstNum = "yt";
        } else if (companyname.contains("韵达")) {
            comFirstNum = "yd";
        } else if (companyname.contains("天天")) {
            comFirstNum = "tt";
        } else if (companyname.contains("EMS")) {
            comFirstNum = "ems";
        } else if (companyname.contains("中通")) {
            comFirstNum = "zto";
        } else if (companyname.contains("汇通")) {
            comFirstNum = "ht";
        } else if (companyname.contains("全峰")) {
            comFirstNum = "qf";
        } else {
            Util.show(companyname + "暂不支持物流查询！", context);
            return;
        }
        Parameters params = new Parameters();
        params.add("com", comFirstNum);

        params.add("no", no);

        try {
            ExpressageHistroty findFirst = DbUtils.create(App.getContext())
                    .findFirst(
                            Selector.from(ExpressageHistroty.class).where(
                                    "num", "=", no));

            if (findFirst == null) {

                final ExpressageHistroty histroty = new ExpressageHistroty(no,
                        companyname, comFirstNum);
                Util.asynTask(new IAsynTask() {

                    @Override
                    public void updateUI(Serializable runData) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public Serializable run() {
                        try {
                            DbUtils.create(App.getContext()).saveBindingId(
                                    histroty);
                        } catch (DbException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        return null;
                    }
                });

            }

        } catch (DbException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JuheWeb.getJuheData(params, 43, "http://v.juhe.cn/exp/index",
                JuheData.GET, new JuheRequestCallBack() {

                    @Override
                    public void success(int err, String reason, String result) {
                        Map<String, String> map = JsonUtil
                                .getNewApiJsonQuery(result.toString());
                        String message = map.get("message");
                        if (map.get("code").equals("200")) {
                            String results = map.get("list");
                            try {
                                JSONObject jObject = new JSONObject(results);
                                String lists = jObject.optString("list");

                                List<ExpressageInfo> mlist = JsonUtil
                                        .getPersons(
                                                lists,
                                                new TypeToken<List<ExpressageInfo>>() {
                                                });
                                if (mlist != null) {
                                    Util.showIntent(
                                            context,
                                            ExpressageQueryResultActivity.class,
                                            new String[]{"list"},
                                            new Serializable[]{(Serializable) mlist});
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            LogUtils.e("map.get(\"error_code\")   "
                                    + map.get("error_code"));
                            if (map.get("error_code").equals("204304"))
                                Util.show("查询失败", context);
                            else if (map.get("error_code").equals("204302"))
                                Util.show("请填写正确的运单号", context);
                            else if (map.get("error_code").equals("204301"))
                                Util.show("未被识别的快递公司", context);
                            else
                                Util.show("查询失败，错误码：" + map.get("error_code")
                                        + "。错误消息：" + message, context);

                        }

                        // AnimeUtil.cancelImageAnimation(iv_center);
                    }

                    @Override
                    public void requestEnd() {
                        // view.setEnabled(true);
                        // iv_center.setVisibility(View.GONE);
                        v.setClickable(true);
                    }

                    @Override
                    public void fail(int err, String reason, String result) {
                        // TODO Auto-generated method stub

                    }
                });
    }

    private void ShouHuo(final String orderid, final String secid,
                         final int position) {
        final VoipDialog voip = new VoipDialog("你当前正在确认收货,您确定要这么做吗？", context, "确认", "取消", new OnClickListener() {

            @Override
            public void onClick(View v) {
                // voip.cancel();
                // voip.dismiss();
                Util.asynTask(context, "正在确认收货...", new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        if ("success".equals(runData + "")) {
                            VoipDialog voipDialog = new VoipDialog("确认收货成功，参与晒单评价。", context, "确定", "取消", new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(context, EvaluationOrdersActivity.class);
                                    intent.putExtra("shopinfo",list.get(position));
                                    context.startActivity(intent);
                                    list.remove(position);
                                    OrderShouHuoTwo.this.notifyDataSetChanged();
                                }
                            }, new OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    Log.e("确认收获","mCallBack"+(mCallBack==null));
                                    if (mCallBack != null) {
                                        mCallBack.doback("4");
                                    }
                                    list.remove(position);
                                    OrderShouHuoTwo.this.notifyDataSetChanged();

                                }
                            });
                            voipDialog.show();

                        } else {
                            Util.show(runData + "", context);
                        }
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.endOrder, "userId="
                                + UserData.getUser().getUserId()
                                + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd()
                                + "&oid=" + orderid + "&tid=" + secid);
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

    class ViewHolder {
        private TextView two_dingdan;
        private TextView two_fahuo;
        private TextView order_title;
        private TextView order_money;
        private TextView order_num;
        private TextView two_order_wuliu, two_order_quxiao;
        private TextView two_order_querenshouhuo;
        private ImageView two_im;
        private TextView order_type, more_tv;
        private QuantityView quantityView_default;
        private View stateline, quantityrl, danhaoline;

    }

    private void getSuccessOrders(String sorderId, String remark) {
        // TODO Auto-generated method stub
        final CustomProgressDialog dialog = Util.showProgress("正在取消订单...",
                context);
        NewWebAPI.getNewInstance().getWebRequest(
                "/YdaOrder.aspx?call=Pass_Order&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&sorderId=" + sorderId + "&remark=" + remark,
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        // TODO Auto-generated method stub
                        Util.show("网络超时，请重试！", context);
                        dialog.cancel();
                        dialog.dismiss();
                        return;
                    }

                    @Override
                    public void success(Object result) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", context);
                            return;
                        }
                        com.alibaba.fastjson.JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), context);
                            return;
                        }

                        Util.show(json.getString("message"), context);
                        if (mCallBack != null) {
                            mCallBack.doback("1");
                        }

                    }

                    @Override
                    public void requestEnd() {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {
                        // TODO Auto-generated method stub
                        dialog.cancel();
                        dialog.dismiss();
                        Util.show("网络异常，请重试！", context);
                        return;
                    }
                });
    }
}
