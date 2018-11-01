package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.AccountType;
import com.mall.model.Stored;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.net.Web;
import com.mall.serving.community.net.NewWebAPI;
import com.mall.util.IAsynTask;
import com.mall.util.L;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 功能： 账户明细窗体<br>
 * 时间： 2013-3-24<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
//
@ContentView(R.layout.account_details)
public class AccountDetailsFrame extends Activity {
    private int page = 1;
    private Intent intent = null;
    private ListView accountInfo = null;
    private MyAdapter1 adapter = null;
    boolean isFoot = false; // 用来判断是否已滑动到底部
    private String gwkNumer = "2";
    private String enumType = "0";

    private String strType = "";

    private TextView type;
    private TextView number;
    private TextView yu_e;

    @ViewInject(R.id.downiv)
    private ImageView downiv;

    @ViewInject(R.id.downline)
    private View downline;

    String parentValue = "";
    PopupWindow TypeWindow;

    private Context mcontext;

    View contentViewcity;

    ListView city_list;

    //	ArrayList<String> arrayList=new ArrayList<>();
    List<AccountType.ListBean> accountTypelist = new ArrayList<>();

    AccountAdapter accountAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.account_details);
        ViewUtils.inject(this);
        mcontext = this;

        init();

    }

    public void getRedBoxmyselfInfo() {
        final CustomProgressDialog cpd = Util.showProgress("正在获取您的信息...", mcontext);
        NewWebAPI.getNewInstance().getWebRequest("/red_box_v2.aspx?call=Get_Access_Type&acctype_=account_list_yw&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd(),
                new NewWebAPIRequestCallback() {

                    @Override
                    public void timeout() {
                        Util.show("网络超时！", mcontext);
                        return;
                    }

                    @Override
                    public void success(Object result) {

                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！", mcontext);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"), mcontext);
                            return;
                        }

                        Gson gson = new Gson();

                        AccountType accountType = gson.fromJson(result.toString(), AccountType.class);
                        accountTypelist.addAll(accountType.getList());
                        accountTypelist.add(new AccountType.ListBean("0", "全部"));
//						accountAdapter.notifyDataSetChanged();

                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }

                    @Override
                    public void fail(Throwable e) {

                    }
                });


    }

    class AccountAdapter extends BaseAdapter {
        Context mContext;
        List<AccountType.ListBean> mAccountTypelist;

        public AccountAdapter(Context context, List<AccountType.ListBean> accountTypelist) {
            mContext = context;
            mAccountTypelist = accountTypelist;
        }

        @Override
        public int getCount() {
            return mAccountTypelist.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view1 = LayoutInflater.from(mcontext).inflate(R.layout.itemtext, parent, false);
            TextView tv = (TextView) view1.findViewById(R.id.tv);
            tv.setText(mAccountTypelist.get(position).getType_name());
            return view1;
        }
    }

    @OnClick({R.id.downline})
    private void click(View view) {

        if (!parentValue.equals("业务账户")) {
            return;
        }
        if (accountTypelist.size() == 0) {
            return;
        }

        contentViewcity = LayoutInflater.from(mcontext).inflate(
                R.layout.popwindowlist, null);
        city_list = (ListView) contentViewcity.findViewById(R.id.city_list);
        accountAdapter = new AccountAdapter(mcontext, accountTypelist);
        city_list.setAdapter(accountAdapter);
        city_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (TypeWindow != null) {
                    TypeWindow.dismiss();
                    type.setText(accountTypelist.get(position).getType_name());
                    page = 1;
                    strType = accountTypelist.get(position).getType_id() + "";
                    enumType = accountTypelist.get(position).getType_id() + "";
                    bind(page);
                }
            }
        });


        int width = downline.getWidth();
        L.e("View 宽度" + width);

        TypeWindow = new PopupWindow(contentViewcity, width,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        TypeWindow.setTouchable(true);

        TypeWindow.setAnimationStyle(R.style.popmenu_animation);
        TypeWindow.setBackgroundDrawable(new BitmapDrawable());

        if (Build.VERSION.SDK_INT < 24) {
            TypeWindow.showAsDropDown(downline);
        } else {
            int[] a = new int[2];
            downline.getLocationInWindow(a);
            TypeWindow.showAtLocation(getWindow().getDecorView(), Gravity.NO_GRAVITY, 0, downline.getHeight() + a[1]);
            TypeWindow.update();
        }
    }

    private void init() {
        findview();
        intent = this.getIntent();
        parentValue = intent.getStringExtra("parentName");
        String childrenName = intent.getStringExtra("childrenName");
        String enumType1 = intent.getStringExtra("enumType");
        if (enumType1 != null) {
            enumType = enumType1;
        }
        if ("账户1明细".equals(childrenName)) {
            gwkNumer = "1";
            Util.initTop(this, "购物卡账户1明细", Integer.MIN_VALUE, null);
            setTableHeader();
        } else if ("账户2明细".equals(childrenName)) {
            gwkNumer = "2";
            Util.initTop(this, "购物卡账户2明细", Integer.MIN_VALUE, null);
            setTableHeader();
        } else if ("我的创业包明细".equals(childrenName)) {
            setheader();
        } else {
            Util.initTop(this, parentValue + "明细", Integer.MIN_VALUE, null);
            L.e("标题信息:" + parentValue);
            if (parentValue.equals("业务账户")) {
                downiv.setVisibility(View.VISIBLE);
                getRedBoxmyselfInfo();
            }
        }

        if (null != UserData.getUser()) {
            accountInfo = Util.getListView(R.id.accountDetailsView, this);
            intent = this.getIntent();
            bind(page);
            accountInfo.setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    // 滚动到底部
                    if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
                        page++;
                        bind(page);
                    }
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    if (firstVisibleItem + visibleItemCount == totalItemCount) {
                        isFoot = true;
                    }
                }
            });
        } else {
            Util.doLogin("请先登录！", this);
        }
    }

    private void setheader() {
        Util.initTop(this, "我的创业包明细", Integer.MIN_VALUE, null);
        type.setText("审批类型");
        number.setText("审批数量");
        yu_e.setText("剩余数量");
    }

    private void setTableHeader() {
        // TODO Auto-generated method stub

        type.setText("收入类型");
        number.setText("收入金额");
        yu_e.setText("当前余额");
    }

    private void findview() {
        type = (TextView) this.findViewById(R.id.type);
        number = (TextView) this.findViewById(R.id.number);
        yu_e = (TextView) this.findViewById(R.id.yu_e);

    }

    private void bind(final int page) {
        final String url = "userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                + "&pageSize=" + 20 + "&page=" + page + "&enumType=" + enumType + "&intype=" + strType;

        final String parentValue = intent.getStringExtra("parentName");
        final String userKey = intent.getStringExtra("userKey");

        Util.asynTask(this, "正在获取您的帐户信息...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("无效的帐户类型...", AccountDetailsFrame.this);
                    return;
                }

                HashMap<String, List<Stored>> map3 = (HashMap<String, List<Stored>>) runData;
                List<Stored> list = map3.get("list");

                if (null == list || 0 == list.size()) {
                    Util.show("您没有更多的" + parentValue + "明细", AccountDetailsFrame.this);
                    return;
                }
                List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < list.size(); i++) {
                    Map<String, Object> map2 = new HashMap<String, Object>();
                    map2.put("date", list.get(i).getDate());
                    map2.put("type", list.get(i).getType());
                    map2.put("money", list.get(i).getIncome());
                    map2.put("desc", list.get(i).getDetail());
                    map2.put("bann", list.get(i).getBalance());
                    dataList.add(map2);
                }
                if (null == adapter) {
                    int width = 0;
                    DisplayMetrics dm = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(dm);
                    width = dm.widthPixels;
                    accountInfo.setAdapter(adapter = new MyAdapter1(AccountDetailsFrame.this, dataList, width));
                } else {
                    if (page == 1) {
                        adapter.cleaner();
                    }

                    adapter.addChildren(dataList);
                }
            }

            @Override
            public Serializable run() {
                Web web = null;
                if ("bus".equals(userKey))
                    web = new Web(Web.getBusinessAccount, url);
                else if ("rec".equals(userKey))
                    web = new Web(Web.getRechargeAccount, url);
                else if ("cp".equals(userKey))
                    web = new Web(Web.getLotteryAccount, url);
                else if ("rai".equals(userKey))
                    web = new Web(Web.getInterestsAccount, url);
                else if ("han".equals(userKey))
                    web = new Web(Web.getHandselAccount, url);
                else if ("sto".equals(userKey))
                    web = new Web(Web.getStoredAccount, url.replaceFirst("&enumType=0", "&enumType=" + gwkNumer));
                else if ("sto1".equals(userKey))
                    web = new Web(Web.getStoredAccount, url.replaceFirst("&enumType=0", "&enumType=3"));
                else if ("sb".equals(userKey))
                    web = new Web(Web.getSBDetailList, url);
                else if ("pho".equals(userKey))
                    web = new Web(Web.getPhoneAccount, url);
                else if ("zcb".equals(userKey))
                    web = new Web(Web.getZCBDetailList, url);
                else if ("tixian".equals(userKey))
                    web = new Web(Web.txming, url);
                else if ("hb".equals(userKey))
                    web = new Web(Web.redurl, "/getRed_SeedAccount", url);
                else if ("xj".equals(userKey))
                    web = new Web(Web.redurl, "/getRed_CashAccount", url);
                else if ("gwq".equals(userKey))
                    web = new Web(Web.getBusinessAccount, url);
                else {
                    return null;
                }
                HashMap<String, List<Stored>> map = new HashMap<String, List<Stored>>();
                List<Stored> list = web.getList(Stored.class);
                map.put("list", list);
                return map;
            }
        });
    }
}

class ViewHolder {
    public TextView date;
    public TextView type;
    public TextView bann;
    public TextView money;
    public LinearLayout itemLine;
}

class MyAdapter1 extends BaseAdapter {

    private LayoutInflater mInflater;
    private Activity account = null;
    private List<Map<String, Object>> dataList = null;
    private int width;

    public MyAdapter1(Activity context, List<Map<String, Object>> dataList, int width) {
        this.account = context;
        this.mInflater = LayoutInflater.from(context);
        this.dataList = dataList;
        this.width = width;
    }

    public void cleaner() {
        L.e("清除数据");
        this.dataList.clear();
        this.notifyDataSetChanged();
    }

    public void addChildren(List<Map<String, Object>> dataList) {
        this.dataList.addAll(dataList);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return dataList.get(0);
    }

    @Override
    public long getItemId(int arg0) {
        return dataList.get(0).hashCode();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.account_details_item, null);
            holder.date = (TextView) convertView.findViewById(R.id.detailItemDate);
            holder.type = (TextView) convertView.findViewById(R.id.detailItemType);
            holder.money = (TextView) convertView.findViewById(R.id.detailItemMoney);
            holder.bann = (TextView) convertView.findViewById(R.id.detailItemBann);
            holder.itemLine = (LinearLayout) convertView.findViewById(R.id.itemLine);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final Context c = convertView.getContext();
        OnClickListener click = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object desc = dataList.get(position).get("desc");
                if (Util.isNull(desc)) {
                    Util.detailInformation(account, "暂无详细内容!", "详细信息", width);
                } else {
                    String message = "时间：" + dataList.get(position).get("date") + "\n类型："
                            + dataList.get(position).get("type") + "\n金额："
                            + Util.deleteX(dataList.get(position).get("money") + "") + "\n描述："
                            + desc.toString().replaceAll("_p", "") + "";
                    Util.detailInformation(account, message, "详细信息", width);
                }
            }
        };
        holder.itemLine.setOnClickListener(click);
        holder.date.setText(dataList.get(position).get("date") + "");
        holder.type.setText(dataList.get(position).get("type") + "");
        holder.money.setText(dataList.get(position).get("money") + "");
        if (!Util.isNull(dataList.get(position).get("bann") + "")) {
            holder.bann.setText(dataList.get(position).get("bann") + "");
        }

        return convertView;
    }


}
