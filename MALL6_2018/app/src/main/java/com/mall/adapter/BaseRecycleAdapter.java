package com.mall.adapter;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mall.util.MyLog;

import java.util.List;

/**
 * Created by Administrator on 2018/3/14.
 */
public abstract class BaseRecycleAdapter<E> extends RecyclerView.Adapter<BaseRecycleAdapter.MyViewHolder> {

    public Context mContext;
    public List<E> mList;
    LayoutInflater mInflater;


    //声明自定义的监听接口
    private OnRecyclerviewItemClickListener mOnRecyclerviewItemClickListener = null;



    public interface OnRecyclerviewItemClickListener {
        void onItemClickListener(View v, int position);

    }

    protected BaseRecycleAdapter(Context context, List<E> list) {
        mContext = context;
        mList = list;
        mInflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmOnRecyclerviewItemClickListener(OnRecyclerviewItemClickListener clickListener) {
        this.mOnRecyclerviewItemClickListener = clickListener;
    }

    public List<E> getmList() {
        return mList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MyLog.e("viewType", "viewType" + viewType);
        ViewDataBinding mBinding = getShowRule(mInflater, parent, viewType);
        MyViewHolder holder = new MyViewHolder(mBinding);
        holder.getBinding().getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRecyclerviewItemClickListener != null) {
                    mOnRecyclerviewItemClickListener.onItemClickListener(v, ((int) v.getTag()));
                }

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        setIteamData(holder.getBinding(), mList, position);
        holder.getBinding().getRoot().setTag(position);//给view设置tag以作为参数传递到监听回调方法中
        holder.getBinding().executePendingBindings();//加一行，问题解决
    }

    @Override
    public int getItemCount() {
        int num = mList != null ? mList.size() : 0;
        return num;
    }

    public abstract void setIteamData(ViewDataBinding mBinding, List<E> list, int position);

    public abstract ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType);

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        private ViewDataBinding mbinding;

        public ViewDataBinding getBinding() {
            return mbinding;
        }

        public MyViewHolder(ViewDataBinding binding) {
            super(binding.getRoot());
            this.mbinding = binding;
        }
    }

    public abstract int setShowRule(int position);

    @Override
    public int getItemViewType(int position) {

        return setShowRule(position);

    }
}
