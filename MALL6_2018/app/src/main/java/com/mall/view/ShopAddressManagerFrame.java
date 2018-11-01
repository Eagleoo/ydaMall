package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.model.ShopAddress;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 功能： 收货地址管理<br>
 * 时间： 2013-12-30<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class ShopAddressManagerFrame extends Activity {

    private int page = 1;
    private final int size = 3000;
    private boolean isRefreshFoot = false;
    @ViewInject(R.id.shop_address_manager_list)
    private ListView listView;
    private ShopAddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.shop_address_manager_frame);
        ViewUtils.inject(this);
        Util.initTitle(this, "收货地址管理", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(ShopAddressManagerFrame.this, ShopAddressFrame.class);
                ShopAddressManagerFrame.this.finish();
            }
        }, "新增");
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && isRefreshFoot) {
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        page();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                // 判断是否滑动到底部弄
                isRefreshFoot = (firstVisibleItem + visibleItemCount == totalItemCount);
            }
        });
        // page();
    }

    private void page() {
        Util.asynTask(this, "正在获取您的收货地址...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null != runData) {
                    HashMap<String, List<ShopAddress>> map = (HashMap<String, List<ShopAddress>>) runData;
                    List<ShopAddress> list = map.get("list");
                    List<ShopAddress> shopAddresse = new ArrayList<ShopAddress>();
                    if (null != list) {
                        if (list.size() > 0) {
                            for (int i = 0; i < list.size(); i++) {
                                if (!list.get(i).getIsDefault().equals("False")) {
                                    shopAddresse.add(list.get(i));
                                    list.remove(i);
                                    break;
                                }
                            }
                            for (int i = 0; i < list.size(); i++) {
                                shopAddresse.add(list.get(i));
                            }
                            list = shopAddresse;
                            if (null == adapter || page == 1) {
                                adapter = new ShopAddressAdapter(ShopAddressManagerFrame.this, list);
                                listView.setAdapter(adapter);
                            } else {

                                adapter.addData(list);
                                adapter.updateUI();
                            }
                        }
                        if (0 == list.size() && page == 1) {
                            Util.show("还没有收货地址，请添加！", ShopAddressManagerFrame.this);
                        }
                    }
                }
                page++;
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopAddressByPage_lin, "userId=" + UserData.getUser().getUserId() + "&pwd="
                        + UserData.getUser().getMd5Pwd() + "&page=" + page + "&size=" + size);
                HashMap<String, List<ShopAddress>> map = new HashMap<String, List<ShopAddress>>();
                map.put("list", web.getList(ShopAddress.class));
                return map;
            }
        });
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        page = 1;
        adapter = null;
        page();
    }

    class ShopAddressAdapter extends BaseAdapter {

        private Context context;
        private List<ShopAddress> list;
        private LayoutInflater flater;

        public ShopAddressAdapter(Context context, List<ShopAddress> list) {
            super();
            this.context = context;
            this.list = list;
            flater = LayoutInflater.from(context);
        }

        public void addData(List<ShopAddress> list) {
            this.list.addAll(list);
        }

        public void clear() {
            this.list.clear();
        }

        public void updateUI() {
            this.notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        /**
         * 设置默认收货地址
         */
        public void setMoRen(final String id) {
            Util.asynTask(new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    if (null != runData) {
                        final ShopAddress sa = (ShopAddress) runData;
                        Util.asynTask(new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if ("success".equals(runData + "")) {
                                    Util.asynTask(context, "正在设置您的默认收货地址。\n请稍等...", new IAsynTask() {
                                        @Override
                                        public void updateUI(Serializable runData) {
                                            if ("success".equals(runData + "")) {
                                                page = 1;
                                                adapter = null;
                                                page();
                                            } else
                                                Util.show(runData + "", context);
                                        }

                                        @Override
                                        public Serializable run() {
                                            Web web = new Web(Web.addShopAddress,
                                                    "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                                                            + UserData.getUser().getMd5Pwd() + "&name="
                                                            + Util.get(sa.getName()) + "&shen=" + sa.getShen() + "&shi="
                                                            + sa.getShi() + "&qu=" + sa.getZone() + "&zipCode="
                                                            + sa.getZipCode() + "&phone=" + sa.getMobilePhone() + "&gj="
                                                            + "" + "&quhao=" + "" + "&zuoji=" + sa.getPhone()
                                                            + "&address=" + Util.get(sa.getAddress())
                                                            + "&isDefault=true");

                                            return web.getPlan();
                                        }
                                    });
                                } else
                                    Util.show("默认失败", context);
                            }

                            @Override
                            public Serializable run() {
                                Web web = new Web(Web.deleteUserShopAddress, "userid=" + UserData.getUser().getUserId()
                                        + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&sid=" + id);
                                return web.getPlan();
                            }
                        });
                    }
                }

                @Override
                public Serializable run() {
                    Web web = new Web(Web.getShopAddressById, "userId=" + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&id=" + id);
                    return web.getObject(ShopAddress.class);
                }
            });
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        private void chanegeDrawable(int imageid, TextView view) {

            Drawable icon = context.getResources().getDrawable(imageid);
            icon.setBounds(0, 0, icon.getMinimumWidth(), icon.getMinimumHeight());
            view.setCompoundDrawables(icon, null, null, null);
        }

        @Override
        public long getItemId(int position) {
            return list.get(position).hashCode();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (0 == list.size())
                return convertView;
            final ShopAddress model = list.get(position);
            ShopAddressHolder holder = null;
            if (null == convertView) {
                holder = new ShopAddressHolder();
                convertView = flater.inflate(R.layout.shop_address_frame_list_item, null);
                ViewUtils.inject(holder, convertView);
                convertView.setTag(holder);
            } else {
                holder = (ShopAddressHolder) convertView.getTag();
            }
            holder.name.setText(model.getName());
            holder.name.setTag(model.getShoppingAddId());
            holder.phone.setText(model.getMobilePhone());
            holder.city.setText(model.getRegion());
            holder.address.setText(model.getAddress());
            holder.moren_address.setTag("1");
            if (model.getIsDefault().equals("False")) {
                holder.moren_address.setText("设为默认");
                chanegeDrawable(R.drawable.new_shouhuo2, holder.moren_address);
            } else {
                holder.moren_address.setText("默认地址");
                chanegeDrawable(R.drawable.new_shouhuo1, holder.moren_address);
            }
            holder.moren_address.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (model.getIsDefault().equals("False")) {
                        model.setIsDefault("True");
                        setMoRen(model.getShoppingAddId());
                    }

                }
            });
            holder.delete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    Util.asynTask(context, "正在删除...", new IAsynTask() {
                        @Override
                        public void updateUI(Serializable runData) {
                            if ("success".equals(runData + "")) {
                                list.remove(model);
                                ShopAddressAdapter.this.updateUI();
                            } else
                                Util.show(runData + "", context);
                        }

                        @Override
                        public Serializable run() {
                            Web web = new Web(Web.deleteUserShopAddress, "userid=" + UserData.getUser().getUserId()
                                    + "&md5Pwd=" + UserData.getUser().getMd5Pwd() + "&sid=" + model.getShoppingAddId());
                            return web.getPlan();
                        }
                    });
                    v.setEnabled(true);
                }
            });
            holder.bianji.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.showIntent(context, ShopAddressFrame.class, new String[]{"id"},
                            new String[]{model.getShoppingAddId()});
                }
            });
            return convertView;
        }
    }

    class ShopAddressHolder {
        @ViewInject(R.id.shop_address_item_name)
        public TextView name;
        @ViewInject(R.id.shop_address_item_phone)
        public TextView phone;
        @ViewInject(R.id.shop_address_item_city)
        public TextView city;
        @ViewInject(R.id.shop_address_item_address)
        public TextView address;
        @ViewInject(R.id.shop_address_item_delete)
        public Button delete;
        @ViewInject(R.id.shop_address_item_bianji)
        public Button bianji;
        @ViewInject(R.id.moren_address)
        public TextView moren_address;

    }

}
