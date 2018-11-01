package com.mall.serving.orderplane;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mall.model.PlaneCabinModel;
import com.mall.util.Util;
import com.mall.view.R;

public class ChooseCabinTwoWay extends Activity {
    private String city_to_city = "成都-北京";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.choosecabin_twoway);
        init();
    }

    private void init() {
        Util.initTitle(this, city_to_city, new OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseCabinTwoWay.this.finish();
            }
        });
    }


    public class ChooseCabinAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<PlaneCabinModel> list = new ArrayList<PlaneCabinModel>();

        public ChooseCabinAdapter(Context c) {
            inflater = LayoutInflater.from(c);
        }

        @Override
        public int getCount() {
            return this.list.size();
        }

        @Override
        public Object getItem(int position) {
            return this.list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PlaneCabinModel p = list.get(position);
            ViewHolder h = null;
            if (convertView == null) {
                h = new ViewHolder();
                convertView = inflater.inflate(R.layout.plane_cabin_item, null);
                h.discount = (TextView) convertView.findViewById(R.id.discount);
                h.tips = (TextView) convertView.findViewById(R.id.tips);
                h.reback = (TextView) convertView.findViewById(R.id.reback);
                h.price = (TextView) convertView.findViewById(R.id.price);
                h.tickets_num = (TextView) convertView.findViewById(R.id.tickets_num);
                h.elseinfo = (TextView) convertView.findViewById(R.id.elseinfo);
                h.order = (TextView) convertView.findViewById(R.id.order);
                convertView.setTag(h);
            } else {
                h = (ViewHolder) convertView.getTag();
            }
            h.price.setText("￥" + p.getPrice());
            Double d = Double.parseDouble(p.getDiscount());
            int pr = Integer.parseInt(p.getPrice());
            Double rew = Double.parseDouble(p.getRewRates().replace("%", "")) * 100;
            h.discount.setText(p.getCabName() + "" + (d * 10) + "折");
            if (!rew.equals("0")) {
                h.reback.setText("返" + Util.getDouble((pr * rew) / 10000, 2) + "");
            } else {
                h.reback.setVisibility(View.GONE);
            }
            if (p.getSeatNum().equals("A")) {
                h.tickets_num.setText("大于9");
            } else {
                h.tickets_num.setText(p.getSeatNum());
            }
            h.order.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ChooseCabinTwoWay.this, WritePlaneOrder.class);
                    ChooseCabinTwoWay.this.startActivity(intent);
                }
            });
            return convertView;
        }
    }

    public class ViewHolder {
        TextView discount;
        TextView tips;
        TextView reback;
        TextView price;
        TextView tickets_num;
        TextView elseinfo;
        TextView order;
    }
}
