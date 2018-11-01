package com.mall.adapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mall.MessageEvent;
import com.mall.model.RedPakageBean;
import com.mall.util.MyTime;
import com.mall.util.Util;
import com.mall.view.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017/11/1.
 */

public class MyTestRecyclerViewAdapter extends
        RecyclerView.Adapter<MyTestRecyclerViewAdapter.ViewHolder>{
    List<RedPakageBean.ListBean> redpakagelist;

    public MyTestRecyclerViewAdapter(List<RedPakageBean.ListBean> redpakagelist) {
        this.redpakagelist=redpakagelist;
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.redenitem,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final RedPakageBean.ListBean bean=redpakagelist.get(position);
        if (bean.getState().equals("1")){
            try {
                String[] time=bean.getTimeopen().split(" ");  //2017-10-09 10:36:09

                holder.boxtime_tv.setText(time[0]+"\t\n"+time[1]);  //开启时间
            }catch (Exception e){
                Log.e("Exception",e.toString());
                holder.boxtime_tv.setText(bean.getTimeopen());  //开启时间
            }
        }else {
            holder.boxtime_tv.setText(bean.getTimeopen());  //开启时间
        }
        holder.boxidtag.setText(Util.justifyString("盒子编号:",5));
        holder.kaiqishijian.setText(Util.justifyString("开启时间:",5));
        holder.shengyushijian_tag.setText(Util.justifyString("剩余时间:",5));
        holder.moreTextView.setText(Util.justifyString("红包价值:",5));

        if (bean.getState().equals("0")){
            holder.redbox.setVisibility(View.GONE);
            holder.moreTextView.setText(Util.justifyString("盒子价值:",5));
        }else

        if (bean.getState().equals("1")){
            holder.shengyushijian_ll.setVisibility(View.GONE);
            holder.redbox.setVisibility(View.VISIBLE);
            holder.kaiqishijian.setText(Util.justifyString("时间:",5));

            holder.redbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                    openred(bean.getId(),position);

                    EventBus.getDefault().post(new MessageEvent(bean.getId(),position));

                }
            });
        }

        else if(bean.getState().equals("2")){
            holder.redbox.setVisibility(View.GONE);
            holder.kaiqishijian.setText(Util.justifyString("领取时间:",5));
            holder.boxtime_tv.setText(bean.getOnendate());
            holder.shengyushijian_ll.setVisibility(View.GONE);
        }

        holder.boxid_tv.setText(bean.getId());

        MyTime myTime=Util.getshengyutime(bean.getTimeopen());
        holder.boxsurplustime_tv.setText(myTime.getDay() + "天" + myTime.getHour() + "小时" + myTime.getMin() + "分" + myTime.getSec() + "秒");  //剩余时间
        holder.boxmoney_tv.setText(bean.getMoney());
    }

    @Override
    public int getItemCount() {
        int num=redpakagelist == null ? 0 : redpakagelist.size();
        return num;
    }

    public  void  setData(List<RedPakageBean.ListBean> redpakagelist){
        if (this.redpakagelist!=null){
            this.redpakagelist.clear();
        }
        this.redpakagelist.addAll(redpakagelist);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.boxid_tv)
        TextView boxid_tv;
        @BindView(R.id.boxtime_tv)
        TextView boxtime_tv;
        @BindView(R.id.boxsurplustime_tv)
        TextView boxsurplustime_tv;
        @BindView(R.id.boxmoney_tv)
        TextView boxmoney_tv;
        @BindView(R.id.kaiqishijian)
        TextView kaiqishijian;
        @BindView(R.id.moreTextView)
        TextView moreTextView;
        @BindView(R.id.boxidtag)
        TextView boxidtag;
        @BindView(R.id.redbox)
        ImageView redbox;
        @BindView(R.id.shengyushijian_ll)
        View shengyushijian_ll;
        @BindView(R.id.shengyushijian_tag)
        TextView shengyushijian_tag;



        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }



}
