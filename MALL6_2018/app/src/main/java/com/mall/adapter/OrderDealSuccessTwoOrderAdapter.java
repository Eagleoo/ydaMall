package com.mall.adapter;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils.TruncateAt;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.util.LogUtils;
import com.mall.model.OrderItem;
import com.mall.model.OrderTwo;
import com.mall.model.TwoOrderProduct;
import com.mall.net.Web;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.query.activity.expressage.ExpressageQueryResultActivity;
import com.mall.serving.query.model.ExpressageHistroty;
import com.mall.serving.query.model.ExpressageInfo;
import com.mall.serving.query.net.JuheWeb;
import com.mall.serving.query.net.JuheWeb.JuheRequestCallBack;
import com.mall.util.BitmapUtils;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.App;
import com.mall.view.LoginFrame;
import com.mall.view.MallServiceFrame;
import com.mall.view.OrderFrame;
import com.mall.view.PayMoneyFrame;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.mall.view.order.EvaluationOrdersActivity;
import com.thinkland.sdk.android.JuheData;
import com.thinkland.sdk.android.Parameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderDealSuccessTwoOrderAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<OrderTwo> list = new ArrayList<OrderTwo>();
    private String orderid;
    private String orderType;
    private Context context;
    private int width;
    private String comFirstNum,or;

    public OrderDealSuccessTwoOrderAdapter(Context context,String o) {
        this.context = context;
        this.or = o;
        inflater = LayoutInflater.from(context);
    }

    public OrderDealSuccessTwoOrderAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public OrderDealSuccessTwoOrderAdapter(List<OrderTwo> list, String orderid,
                                           String orderType, Context context) {
        this.list = list;
        this.orderid = orderid;
        this.orderType = orderType;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setList(List<OrderTwo> list) {
        if (null == this.list) {
            this.list = new ArrayList<OrderTwo>();
        }
        this.list.addAll(list);
    }

    public void UpData() {
        this.notifyDataSetChanged();
    }

    public void clear() {
        if (null != this.list && this.list.size() > 0) {
            this.list.clear();
        }
    }

    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        Holder holder = null;
        if (convertView == null) {
            holder = new Holder();
            convertView = inflater.inflate(
                    R.layout.item_order_deal_success_child, null);
            holder.twoOrderPic = (ImageView) convertView
                    .findViewById(R.id.item_two_order_suc_goods_pic);
            holder.padd=convertView.findViewById(R.id.padd);
            holder.twoOrderCode = (TextView) convertView
                    .findViewById(R.id.item_two_order_suc_order_code);
            holder.twoOrderAllMoney = (TextView) convertView
                    .findViewById(R.id.item_two_order_suc_goods_money);
            holder.twoOrderState = (Button) convertView
                    .findViewById(R.id.item_two_order_suc_order_state);
            holder.twoOrderGoodsCount = (TextView) convertView
                    .findViewById(R.id.item_two_order_suc_goods_count);
            holder.twoOrderGoodsIntroduce = (TextView) convertView
                    .findViewById(R.id.item_two_order_suc_goods_type);
            // holder.two_time = (TextView) convertView
            // .findViewById(R.id.two_time);
            holder.again = (Button) convertView
                    .findViewById(R.id.item_two_order_suc_query_again);
            holder.twoOrderGoodsName = (TextView) convertView
                    .findViewById(R.id.item_two_order_suc_goods_name);
            convertView.setTag(holder);
            holder.twoOrderQueryWL = (Button) convertView
                    .findViewById(R.id.item_two_order_suc_query_wuliu);

        } else {
            holder = (Holder) convertView.getTag();
        }
        // final OrderItem orderItem = list.get(position);

        // holder.twoOrderPic.setLayoutParams(new LinearLayout.LayoutParams(
        // _5dp * 12, _5dp * 14));
        // Util.asynDownLoadImage(
        // orderItem.getImg().replaceFirst("img.mall666.cn",
        // Web.imgServer), holder.twoOrderPic);

        // AnimeUtil.getImageLoad().displayImage(orderItem.getImg(),
        // holder.twoOrderPic, AnimeUtil.getImageOption());

        holder.twoOrderCode.setText("子订单号:" + list.get(position).secondOrderId);
        holder.twoOrderAllMoney.setText("￥ "
                + list.get(position).unitCost.replace(".00", ""));
        holder.twoOrderGoodsName.setText(list.get(position).productName);
        holder.twoOrderGoodsCount.setText("x" + list.get(position).quantity);
        String colorAndSize = list.get(position).colorAndSize;
        String[] s = colorAndSize.split("，");
        if (s.length > 1) {
            String color = s[0];
            String Size = s[1];
            if (!color.equals("颜色：") && Size.equals("尺码：")) {
                holder.twoOrderGoodsIntroduce.setText(color);
            } else if (color.equals("颜色：") && !Size.equals("尺码：")) {
                holder.twoOrderGoodsIntroduce.setText(Size);
            } else if (!color.equals("颜色：") && !Size.equals("尺码：")) {
                holder.twoOrderGoodsIntroduce.setText(colorAndSize);
            }
        }
        // holder.twoOrderState.setText(orderItem.getOrderStatus());
        // holder.two_time.setText(orderItem.getDate());
        if (!Util.isNull(list.get(position).productThumb)) {
            BitmapUtils.loadBitmap(list.get(position).productThumb,
                    holder.twoOrderPic);

        } else {
            // holder.twoOrderPic.setImageResource(resId)
        }
        if (null != holder.twoOrderPic.getDrawable()) {
            Bitmap bitmap = ((BitmapDrawable) holder.twoOrderPic.getDrawable())
                    .getBitmap();
            bitmap = Util.toRoundCorner(bitmap, 10);
            holder.twoOrderPic.setImageBitmap(bitmap);
        }
        holder.twoOrderPic.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ProductDeatilFream.class);
                intent.putExtra("url", list.get(position).productId);
                intent.putExtra("orderType", or);
                context.startActivity(intent);
            }
        });
        holder.again.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, ProductDeatilFream.class);
                intent.putExtra("url", list.get(position).productId);
                intent.putExtra("orderType", or);
                context.startActivity(intent);

            }
        });

        if (list.get(position).iscomment==1){
            holder.twoOrderQueryWL.setText("追加评价");
            holder.padd.setVisibility(View.VISIBLE);
        }else if (list.get(position).iscomment==0){
            holder.twoOrderQueryWL.setText("晒单评论");
            holder.padd.setVisibility(View.VISIBLE);
        }else {
            holder.padd.setVisibility(View.GONE);
            holder.twoOrderQueryWL.setVisibility(View.GONE);
        }


        holder.twoOrderQueryWL.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, EvaluationOrdersActivity.class);
                intent.putExtra("shopinfo",list.get(position));
                context.startActivity(intent);

                // TODO Auto-generated method stub
                // Intent intent = new Intent(context,
                // ExpressageQueryActivity.class);
                // intent.putExtra("postNumber", list.get(position).postNum);
                // intent.putExtra("postCompany", list.get(position).postCpy);
                // intent.putExtra("postPhone", list.get(position).postPhone);
                // context.startActivity(intent);
//                expressageSearch(list.get(position).postCpy,
//                        list.get(position).postNum, v);
                // expressageSearch("韵达", "3100843345040");
            }

        });

        convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                // showDialog(list.get(position).getSecondOrderId(),
                // list.get(position).getSecondOrderStatus(), v, orderid,
                // list.get(position));
                showDialog(list.get(position));
            }

        });
        return convertView;
    }

    private void expressageSearch(String companyname, String no, final View v) {

        if (!Util.isNetworkConnected(context)) {
            Util.show("请检查网络连接～", context);
            return;
        }
        if (Util.isNull(companyname) || Util.isNull(no)) {
            Util.show("网络异常，请重试！", context);
            return;
        }
        Parameters params = new Parameters();
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

    private void showDialog(OrderTwo orderSuc) {
        // TODO Auto-generated method stub
        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(R.layout.dialog_order);

        TextView dialogTitle = (TextView) dialog
                .findViewById(R.id.dialog_order_code);
        TextView goodsName = (TextView) dialog
                .findViewById(R.id.dialog_order_goods_name);
        TextView goodsCount = (TextView) dialog
                .findViewById(R.id.dialog_order_goods_count);
        TextView goodsPrice = (TextView) dialog
                .findViewById(R.id.dialog_order_goods_price);
        TextView Message = (TextView) dialog
                .findViewById(R.id.dialog_order_message);
        TextView shutDown = (TextView) dialog
                .findViewById(R.id.dialog_order_shutdown);

        ImageView goodsPic = (ImageView) dialog
                .findViewById(R.id.dialog_order_goods_iv);
        LinearLayout layout = (LinearLayout) dialog
                .findViewById(R.id.dialog_order_layout);
        DisplayMetrics dm = new DisplayMetrics();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(width,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp);

        dialogTitle.setText(orderSuc.secondOrderId);
        goodsName.setText("商品名称:" + orderSuc.productName);
        goodsCount.setText("购买数量:" + orderSuc.quantity);
        goodsPrice.setText("商品单价:" + orderSuc.unitCost);
        Message.setText("购买留言:" + "");
        if (!Util.isNull(orderSuc.productThumb)) {
            BitmapUtils.loadBitmap(orderSuc.productThumb, goodsPic);
        }
        if (Util.isNull(goodsPic.getDrawable())) {
            goodsPic.setImageResource(android.R.drawable.ic_dialog_dialer);
        }
        shutDown.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        dialog.show();
        // pName.setText("商品名称：" + orderSuc.getProductName());
        //
        // pAmount.setText("购买数量：" + orderSuc.getQuantity());
        //
        // pMoney.setText("商品单价：" + orderSuc.getUnitCost());
        //
        // pMessage.setText("购买留言：" + orderSuc.getMessage());
        //
        // dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        // dialog.create().show();

    }

    private void showDialog(final String tid, final String status,
                            final View v, final String gid, final OrderItem orderItem) {

        Util.asynTask(context, "正在获取该订单信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<TwoOrderProduct>> map = (HashMap<String, List<TwoOrderProduct>>) runData;
                List<TwoOrderProduct> list = map.get("result");
                int _5dp = Util.dpToPx(context, 5);
                if (null != list) {
                    AlertDialog.Builder dialog = new Builder(context);
                    LinearLayout root = new LinearLayout(context);
                    LinearLayout.LayoutParams fill = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.FILL_PARENT);
                    root.setLayoutParams(fill);
                    ScrollView scroll = new ScrollView(context);
                    scroll.setLayoutParams(fill);
                    scroll.setFillViewport(true);
                    LinearLayout scrollLine = new LinearLayout(context);
                    scrollLine.setOrientation(LinearLayout.VERTICAL);
                    scrollLine.setLayoutParams(fill);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.FILL_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(_5dp, _5dp, _5dp, _5dp);
                    LinearLayout item = null;
                    TextView pName, pAmount, pMoney, pMessage;
                    ImageView img = null;
                    for (TwoOrderProduct top : list) {
                        item = new LinearLayout(context);
                        item.setOrientation(LinearLayout.HORIZONTAL);

                        item.setLayoutParams(lp);
                        img = new ImageView(context);
                        img.setLayoutParams(new LinearLayout.LayoutParams(
                                _5dp * 12, _5dp * 14));
                        Util.asynDownLoadImage(
                                top.getImg().replaceFirst("img.mall666.cn",
                                        Web.imgServer), img);
                        item.addView(img);

                        LinearLayout infoLine = new LinearLayout(context);
                        infoLine.setOrientation(LinearLayout.VERTICAL);
                        infoLine.setLayoutParams(lp);
                        pName = new TextView(context);
                        pName.setText("商品名称：" + top.getName());
                        pName.setEllipsize(TruncateAt.MARQUEE);
                        pName.setSingleLine(true);

                        pAmount = new TextView(context);
                        pAmount.setText("购买数量：" + top.getAmount() + ""
                                + top.getUnit());
                        pAmount.setEllipsize(TruncateAt.MARQUEE);
                        pAmount.setSingleLine(true);

                        pMoney = new TextView(context);
                        pMoney.setText("商品单价：" + top.getPrice());
                        pMoney.setEllipsize(TruncateAt.MARQUEE);
                        pMoney.setSingleLine(true);

                        pMessage = new TextView(context);
                        pMessage.setText("购买留言：" + top.getMessage());
                        pMessage.setEllipsize(TruncateAt.MARQUEE);
                        pMessage.setSingleLine(true);

                        infoLine.addView(pName);
                        infoLine.addView(pAmount);
                        infoLine.addView(pMoney);
                        infoLine.addView(pMessage);

                        item.addView(infoLine);
                        scrollLine.addView(item);
                        pName = pMoney = pAmount = pMessage = null;
                        item = null;
                        img = null;
                    }
                    scroll.addView(scrollLine);
                    root.addView(scroll);
                    dialog.setView(root);
                    dialog.setTitle(tid);
                    if ("等待付款".equals(status)) {
                        dialog.setPositiveButton("一级订单付款",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (null == UserData.getUser()) {
                                            Util.showIntent("您还没登录，请先登录",
                                                    context, LoginFrame.class);
                                            return;
                                        }
                                        Util.showIntent(context,
                                                PayMoneyFrame.class,
                                                new String[]{"tid"},
                                                new String[]{gid});
                                    }
                                });
                        dialog.setNeutralButton("取消订单",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Util.asynTask(context,
                                                "正在取消订单。\n请稍等...",
                                                new IAsynTask() {
                                                    @Override
                                                    public void updateUI(
                                                            Serializable runData) {
                                                        if ("success"
                                                                .equals(runData
                                                                        + "")) {
                                                            Util.showIntent(
                                                                    context,
                                                                    OrderFrame.class);
                                                        } else {
                                                            Util.show(runData
                                                                            + "",
                                                                    context);
                                                        }
                                                    }

                                                    @Override
                                                    public Serializable run() {
                                                        Web web = new Web(
                                                                Web.quitOrder,
                                                                "userId="
                                                                        + UserData
                                                                        .getUser()
                                                                        .getUserId()
                                                                        + "&md5Pwd="
                                                                        + UserData
                                                                        .getUser()
                                                                        .getMd5Pwd()
                                                                        + "&tid="
                                                                        + tid);
                                                        return web.getPlan();
                                                    }
                                                });

                                    }
                                });
                    } else if (status.contains("确认收货")) {
                        dialog.setPositiveButton("确认收货",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        if (null == UserData.getUser()) {
                                            Util.showIntent("您还没登录，请先登录",
                                                    context, LoginFrame.class);
                                            return;
                                        }
                                        Util.asynTask(context, "正在收货，请稍等...",
                                                new IAsynTask() {
                                                    @Override
                                                    public void updateUI(
                                                            Serializable runData) {
                                                        if ("success"
                                                                .equals(runData
                                                                        + ""))
                                                            Util.showIntent(
                                                                    "收货成功！",
                                                                    context,
                                                                    OrderFrame.class);
                                                        else
                                                            Util.show(runData
                                                                            + "",
                                                                    context);
                                                    }

                                                    @Override
                                                    public Serializable run() {
                                                        Web web = new Web(
                                                                Web.endOrder,
                                                                "userId="
                                                                        + UserData
                                                                        .getUser()
                                                                        .getUserId()
                                                                        + "&md5Pwd="
                                                                        + UserData
                                                                        .getUser()
                                                                        .getMd5Pwd()
                                                                        + "&oid="
                                                                        + gid
                                                                        + "&tid="
                                                                        + tid);
                                                        return web.getPlan();
                                                    }
                                                });
                                    }
                                });

                    } else if (status.contains("交易成功")
                            && orderItem.getServiceOrder()) {
                        dialog.setPositiveButton("查看倦码",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        Util.showIntent(context,
                                                MallServiceFrame.class,
                                                new String[]{"tid"},
                                                new String[]{orderItem
                                                        .getSecondOrderId()});
                                    }
                                });
                    }
                    dialog.setNegativeButton("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                    dialog.setIcon(android.R.drawable.ic_dialog_dialer);
                    dialog.create().show();
                }
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getTwoOrderProduct, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids=" + tid);
                HashMap<String, List<TwoOrderProduct>> map = new HashMap<String, List<TwoOrderProduct>>();
                map.put("result", web.getList(TwoOrderProduct.class));
                return map;
            }
        });

    }

    class Holder {
        private TextView twoOrderCode;
        private ImageView twoOrderPic;
        private TextView twoOrderGoodsName;
        private TextView twoOrderAllMoney;
        private TextView twoOrderGoodstype;
        private TextView twoOrderGoodsCount;
        private TextView twoOrderGoodsIntroduce;
        private Button twoOrderState;
        private Button twoOrderQueryWL;
        private Button again;
        private View twoOrderView,padd;

    }
}
