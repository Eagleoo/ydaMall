package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.bitmap.PauseOnScrollListener;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.CollectionsProduct;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 功能： 产品收藏展示页面<br>
 * 时间： 2013-12-30<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class CollectProductFrame extends Activity {

    @ViewInject(R.id.collect_product_listView)
    private ListView listView;
    private DbUtils db;
    private int page = 1;
    private int size = 15;
    private CollectProductAdapter adapter;
    private boolean isRefreshFoot = false;
    private BitmapUtils bmUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.collect_product_frame);
        ViewUtils.inject(this);
        db = DbUtils.create(this);
        bmUtil = new BitmapUtils(this);
        Util.initTitle(this, "收藏的商品", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (null == UserData.getUser()) {
            Util.showIntent("您还没登录，请先登录！", this, LoginFrame.class);
            return;
        }
        listView.setOnScrollListener(new PauseOnScrollListener(bmUtil, false, true, new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE
                        && isRefreshFoot) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        page();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // 判断是否滑动到底部弄
                isRefreshFoot = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        }));
        page();
    }

    private void page() {
        Util.asynTask(this, "正在读取您的收藏...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    HashMap<String, List<CollectionsProduct>> map = (HashMap<String, List<CollectionsProduct>>) runData;
                    List<CollectionsProduct> list = map.get("list");
                    if (null == adapter) {
                        adapter = new CollectProductAdapter(CollectProductFrame.this, list, bmUtil);
                        listView.setAdapter(adapter);
                    } else {
                        adapter.addData(list);
                        adapter.updateUI();
                    }
                    if (0 == list.size()) {
                        Util.show("已经到底啦！", CollectProductFrame.this);
                    }
                    page++;
                } else {
                    Util.show("没找到数据...", CollectProductFrame.this);
                }
            }

            @Override
            public Serializable run() {
                Selector sel = Selector.from(CollectionsProduct.class);
                sel.where("ownerid", "=", UserData.getUser().getUserId());
                sel.limit(size);
                sel.offset(size * (page - 1));
                HashMap<String, List<CollectionsProduct>> hash = new HashMap<String, List<CollectionsProduct>>();
                List<CollectionsProduct> list;
                try {
                    db.createTableIfNotExist(CollectionsProduct.class);
                    list = db.findAll(sel);
                } catch (Exception e) {
                    try {
                        db.dropTable(CollectionsProduct.class);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                    list = new ArrayList<CollectionsProduct>();
                    e.printStackTrace();
                }
                hash.put("list", list);
                return hash;
            }
        });
    }
}

class CollectProductAdapter extends BaseAdapter {

    private Context context;
    private List<CollectionsProduct> list;
    private LayoutInflater flater;
    private BitmapUtils bmUtil;

    public CollectProductAdapter(Context context, List<CollectionsProduct> list, BitmapUtils bmUtil) {
        super();
        this.context = context;
        this.list = list;
        this.bmUtil = bmUtil;
        flater = LayoutInflater.from(context);
    }

    public void addData(List<CollectionsProduct> list) {
        this.list.addAll(list);
    }

    public void updateUI() {
        this.notifyDataSetChanged();
    }

    public void clear() {
        this.list.clear();
    }

    public void clear(int index) {
        this.list.remove(index);
    }

    public void clear(Object obj) {
        this.list.remove(obj);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return list.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (0 == list.size())
            return convertView;
        final CollectionsProduct model = list.get(position);
        CollectHolder holder = null;
        if (null == convertView) {
            convertView = flater.inflate(R.layout.collect_product_frame_list_item, null);
            holder = new CollectHolder();
            ViewUtils.inject(holder, convertView);
            convertView.setTag(holder);
        } else {
            holder = (CollectHolder) convertView.getTag();
        }
        holder.img.setImageResource(R.drawable.zw174);
        final String href = model.getImgUrl();
        final String name = model.getImgUrl().substring(href.lastIndexOf("."));
        final String path = Util.proPath + model.getProductid() + name;
        if (!new File(path).exists()) {
            HttpUtils http = new HttpUtils(6000);
            final ImageView img = holder.img;
            http.download(href, path, new RequestCallBack<File>() {
                @Override
                public void onSuccess(ResponseInfo<File> arg0) {
                    bmUtil.display(img, path);
                }

                @Override
                public void onFailure(HttpException arg0, String arg1) {
                    bmUtil.display(img, href);
                }
            });
        } else
            bmUtil.display(holder.img, path);
        holder.name.setText(model.getProductname());
        holder.isshow.setVisibility(View.GONE);
        holder.date.setText("收藏时间：" + model.getCollectTime());
        holder.img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, ProductDeatilFream.class, new String[]{"url"}, new String[]{model.getProductid()});
            }
        });
        holder.shop.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, ProductDeatilFream.class, new String[]{"url"}, new String[]{model.getProductid()});
            }
        });
        holder.quit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DbUtils db = DbUtils.create(context);
                try {
                    db.delete(
                            CollectionsProduct.class,
                            WhereBuilder.b("ownerid", "=", UserData.getUser().getUserId())
                                    .and("productid", "=", model.getProductid()));
                    clear(model);
                    updateUI();
                } catch (DbException e) {
                    Util.show("删除失败，请重试！", context);
                    e.printStackTrace();
                }
            }
        });
        return convertView;
    }
}

class CollectHolder {
    @ViewInject(R.id.collect_item_img)
    public ImageView img;
    @ViewInject(R.id.collect_item_name)
    public TextView name;
    @ViewInject(R.id.collect_item_time)
    public TextView date;
    @ViewInject(R.id.collect_item_shop)
    public Button shop;
    @ViewInject(R.id.collect_item_quit)
    public Button quit;
    @ViewInject(R.id.isshow)
    public View isshow;
}
