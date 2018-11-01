package com.mall.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.model.Column;
import com.mall.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/15.
 */

public class GridViewAdapter extends BaseAdapter {

    Context context;
    List<Column> list;
    int i;//0-会员中心,1-创客专车列表

    public GridViewAdapter(Context context, List list, int i) {
        this.context = context;
        this.list = list;
        this.i=i;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Column getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (i==0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_view, null);
        }else if (i==1){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_grid_view_car, null);
        }
        ImageView item_imv = convertView.findViewById(R.id.item_imv);
        TextView item_tv = convertView.findViewById(R.id.item_tv);
        TextView item_count = convertView.findViewById(R.id.item_count);
        item_imv.setImageResource(list.get(position).getImage());
        item_tv.setText(list.get(position).getText());
        int count = list.get(position).getCount();
        if (count != 0) {
            item_count.setVisibility(View.VISIBLE);
            item_count.setText(count + "");
        } else {
            item_count.setVisibility(View.GONE);
            item_count.setText(count + "");
        }
        return convertView;
    }

    protected void moveItem(int start, int end) {
        List<Column> tmpList = new ArrayList<>();
        if (start < end) {
            tmpList.clear();
            for (Column s : list) tmpList.add(s);
            Column endMirror = tmpList.get(end);

            tmpList.remove(end);
            tmpList.add(end, getItem(start));

            for (int i = start + 1; i <= end; i++) {
                tmpList.remove(i - 1);
                if (i != end) {
                    tmpList.add(i - 1, getItem(i));
                } else {
                    tmpList.add(i - 1, endMirror);
                }
            }

        } else {
            tmpList.clear();
            for (Column s : list) tmpList.add(s);
            Column startMirror = tmpList.get(end);
            tmpList.remove(end);
            tmpList.add(end, getItem(start));

            for (int i = start - 1; i >= end; i--) {
                tmpList.remove(i + 1);
                if (i != start) {
                    tmpList.add(i + 1, getItem(i));
                } else {
                    tmpList.add(i + 1, startMirror);
                }
            }

        }
        list.clear();
        list.addAll(tmpList);
        notifyDataSetChanged();
    }
}