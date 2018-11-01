package com.mall.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.model.OrderTwo;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.view.ProductDeatilFream;
import com.mall.view.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class OrderTuiKuanTwo extends NewBaseAdapter {

    private ImageLoader imload;
    private List<OrderTwo> list;
    private String orderid,orderType;

    public OrderTuiKuanTwo(Context c, String orderid, List list,String o) {
        super(c, list);
        imload = AnimeUtil.getImageLoad();
        this.list = list;
        this.orderid = orderid;
        this.orderType = o;


    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            vh = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.new_order_tuikuan_two_item, null);
            vh.two_im = (ImageView) convertView.findViewById(R.id.two_im);
            vh.two_daifukuan = (TextView) convertView.findViewById(R.id.two_daifukuan);
            vh.order_title = (TextView) convertView.findViewById(R.id.order_title);
            vh.order_money = (TextView) convertView.findViewById(R.id.order_money);
            vh.order_num = (TextView) convertView.findViewById(R.id.order_num);
            vh.order_type = (TextView) convertView.findViewById(R.id.order_type);
            vh.timetv = (TextView) convertView.findViewById(R.id.time);
            vh.two_im = (ImageView) convertView.findViewById(R.id.two_im);

            convertView.setTag(vh);
        } else
            vh = (ViewHolder) convertView.getTag();
        final OrderTwo two = list.get(position);
        imload.displayImage(two.productThumb, vh.two_im, AnimeUtil.getImageRectOption());
        vh.order_title.setText(two.productName);
        vh.order_type.setText(two.colorAndSize.replace("颜色：", "").replace("尺码：", ""));
        vh.order_money.setText("￥" + two.unitCost.replace(".00", ""));
        vh.order_num.setText("x" + two.quantity);
        if (two.applystate != null) {
            vh.timetv.setText(two.tui_date);
            vh.two_daifukuan.setVisibility(View.VISIBLE);
            if (two.tui_state.equals("-1")) {
                vh.timetv.setText(two.qx_date);
                if (two.applystate.equals("0")) {
                    vh.two_daifukuan.setText("未申请");
                } else if (two.applystate.equals("1")) {
                    vh.two_daifukuan.setText("申请中");
                } else if (two.applystate.equals("2")) {
                    vh.two_daifukuan.setText("已通过");
                }
            } else if (two.tui_state.equals("0")) {

                vh.two_daifukuan.setText("审核中");
            } else if (two.tui_state.equals("1")) {

                vh.two_daifukuan.setText("驳回");
            } else if (two.tui_state.equals("2")) {

                vh.two_daifukuan.setText("已审核");
            }


        } else {
            vh.two_daifukuan.setVisibility(View.GONE);
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

        return convertView;
    }

    class ViewHolder {
        private TextView order_title;
        private TextView order_money;
        private TextView order_num;
        private ImageView two_im;
        private TextView order_type, two_daifukuan, timetv;

    }
}
