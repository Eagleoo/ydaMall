package com.lin.component;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.BitmapUtils;
import com.mall.model.UserInfo;
import com.mall.util.BitmapLruCache;
import com.mall.util.UserData;
import com.mall.view.R;


import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class BaseMallAdapter<T> extends BaseAdapter {

    protected List<T> list;
    protected Context context;
    protected int itemId;
    protected LayoutInflater flater;
    protected View currView;
    protected BitmapUtils bmUtils;
    protected BitmapLruCache bmLruCache;
    private Map<Integer, SoftReference<View>> viewCaches = null;
    private boolean istwo = false;

    private boolean ishowimage = true;

    public BaseMallAdapter(Context context, T[] arrays) {
        super();
        this.list = new ArrayList<T>();
        for (T t : arrays)
            this.list.add(t);
        this.context = context;
        bmUtils = new BitmapUtils(context);
        bmLruCache = new BitmapLruCache(context);
    }

    public BaseMallAdapter(Context context, List<T> list) {
        super();
        this.list = list;
        if (null == this.list)
            this.list = new ArrayList<T>();
        this.context = context;
        bmUtils = new BitmapUtils(context);
        bmLruCache = new BitmapLruCache(context);
    }

    public BaseMallAdapter(int itemId, Context context, List<T> list) {
        super();
        this.list = list;
        if (null == this.list)
            this.list = new ArrayList<T>();
        this.itemId = itemId;
        this.context = context;
        flater = LayoutInflater.from(context);
        currView = flater.inflate(itemId, null);
        bmUtils = new BitmapUtils(context);
        bmLruCache = new BitmapLruCache(context);
    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            UserInfo userInfo = (UserInfo) list.get(i);
            String sortStr = userInfo.getSortLetter();
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == section) {
                return i;
            }
        }
        return -1;

    }

    public BaseMallAdapter(int itemId, Context context, T[] arrays) {
        super();
        this.list = new ArrayList<T>();
        for (T t : arrays)
            this.list.add(t);
        this.itemId = itemId;
        this.context = context;
        if (itemId == 0) {
            istwo = true;
            flater = LayoutInflater.from(context);
        } else {
            flater = LayoutInflater.from(context);
            currView = flater.inflate(itemId, null);
        }
        bmUtils = new BitmapUtils(context);
        bmLruCache = new BitmapLruCache(context);
    }

    public void clear() {
        this.list.clear();
        System.gc();
    }

    public void add(T t) {
        this.list.add(0, t);
    }

    public void add(List<T> list) {
        this.list.addAll(list);
    }

    public void add(T[] arrays) {
        for (T t : arrays)
            this.list.add(t);
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public void showimage(boolean ishowiamge) {
        this.ishowimage = ishowiamge;
    }

    public void updateUI() {

        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public T getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        synchronized (this) {
            if (0 == list.size())
                return convertView;
        }
        T t = list.get(position);
        if (istwo) {
            try {
                if (((JSONObject) list.get(position)).getString("USERID").equals(UserData.getUser().getUserId())) {
                    convertView = currView = flater.inflate(R.layout.push_message_right, null);
                } else {
                    convertView = currView = flater.inflate(R.layout.push_message_left, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            if (null == convertView) {
                convertView = currView = flater.inflate(itemId, null);
                currView.setTag(viewCaches = new HashMap<Integer, SoftReference<View>>());
            } else {
                currView = convertView;
                viewCaches = (HashMap<Integer, SoftReference<View>>) currView.getTag();
            }
        }
        return getView(position, convertView, parent, t);
    }

    protected ImageView setImage(int viewId, String url) {
        ImageView view = (ImageView) getCacheView(viewId);
        // bmUtils.display(view, url);
        com.mall.util.BitmapUtils.loadBitmap(url, view);
        return view;
    }

    protected ImageView setImage(int viewId, String url, int a1) {
        ImageView view = (ImageView) getCacheView(viewId);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(a1 - 150, a1 - 150);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        view.setLayoutParams(layoutParams);
        // bmUtils.display(view, url);
        com.mall.util.BitmapUtils.loadBitmap(url, view);
        return view;
    }

    protected TextView setText(int viewId, CharSequence text) {
        TextView view = (TextView) getCacheView(viewId);
        view.setText(text);
        return view;
    }

    protected View getCacheView(int viewId) {
        SoftReference<View> cacheView = viewCaches.get(viewId);
        View view = null;
        if (null == cacheView || null == cacheView.get()) {
            cacheView = new SoftReference<View>(currView.findViewById(viewId));
        }
        view = cacheView.get();
        return view;
    }

    public abstract View getView(int position, View convertView, ViewGroup parent, T t);
}
