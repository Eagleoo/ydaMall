package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.BaseMallAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

/**
 * 功能： 业务明细<br>
 * 时间： 2013-3-8<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class BusinessInfoFrame extends Activity {

    @ViewInject(R.id.aduditListView)
    private ListView listView;
    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;
    private int page = 1;
    private int lastItem = 0;
    private int status = 0; // 0可以加载，1不能加载，2加载中

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.businessinfo);
        ViewUtils.inject(this);
        Util.initTop(this, "体系内业务", Integer.MIN_VALUE, null);
        init();
    }

    private void init() {
        tv1.setText("会员账户");
        tv2.setText("联系电话");
        tv3.setText("系统角色");
        tv4.setVisibility(View.GONE);

        page();
        listView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (lastItem >= listView.getAdapter().getCount()
                        && scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
                    synchronized (listView) {
                        if (0 == status) {
                            status = 1;
                            page();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lastItem = firstVisibleItem + visibleItemCount;
            }
        });
    }

    public void showMessage(String id, String name, String js, String tjr,
                            String zsr, String quyi) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(BusinessInfoFrame.this);
        View layout = LayoutInflater.from(BusinessInfoFrame.this).inflate(
                R.layout.member_detail, null);
        dialog.setIcon(android.R.drawable.ic_dialog_dialer);
        dialog.setTitle("详细信息:");
        dialog.setView(layout);
        // 得到控件，并赋值
        TextView before_id = (TextView) layout.findViewById(R.id.before_id);
        TextView before_name = (TextView) layout.findViewById(R.id.before_name);
        TextView before_js = (TextView) layout.findViewById(R.id.before_js);
        TextView before_tjr = (TextView) layout.findViewById(R.id.before_tjr);
        TextView before_zsr = (TextView) layout.findViewById(R.id.before_zsr);
        before_id.setText("会员帐号：");
        before_name.setText("会员姓名：");
        before_js.setText("系统角色：");
        before_tjr.setText("推荐人：");
        before_zsr.setText("招商人：");

        TextView id1 = layout.findViewById(R.id.mname);
        TextView name1 = layout.findViewById(R.id.mphone);
        TextView js1 = layout.findViewById(R.id.msex);
        TextView tjr1 = layout.findViewById(R.id.mlevel);
        TextView zsr1 = layout.findViewById(R.id.mdate);
        TextView quyi1 = layout.findViewById(R.id.mquyi);
        LinearLayout m6 = layout.findViewById(R.id.m6);
        m6.setVisibility(View.VISIBLE);
        id1.setText(id);
        name1.setText(name);
        js1.setText(js);
        zsr1.setText(zsr);
        quyi1.setText(quyi);
        tjr1.setText(tjr);
        dialog.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    private void page() {
        final CustomProgressDialog cpd = Util.showProgress("正在获取您的业务...", this);
        status = 2;
        User user = UserData.getUser();
        NewWebAPI.getNewInstance().getTiXiUser(user.getUserId(),
                user.getMd5Pwd(), (page++) + "", "15", "",
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        if (null == result) {
                            Util.show("数据加载中，请稍后...", BusinessInfoFrame.this);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            String message = json.getString("message");
                            Util.show(message, BusinessInfoFrame.this);
                            return;
                        }
                        ListAdapter adapter = listView.getAdapter();
                        JSONArray array = json.getJSONArray("list");
                        JSONObject[] objs = array.toArray(new JSONObject[]{});
                        if (null == adapter) {
                            adapter = new BaseMallAdapter<JSONObject>(
                                    R.layout.business_info_item,
                                    BusinessInfoFrame.this, objs) {
                                @Override
                                public View getView(int position,
                                                    View convertView, ViewGroup parent,
                                                    final JSONObject t) {
                                    setText(R.id.bus_info_item_name, Util
                                            .getNo_pUserId(t
                                                    .getString("userId")));
                                    setText(R.id.bus_info_item_date,
                                            t.getString("level"));
                                    getCacheView(R.id.bus_info_item_state)
                                            .setVisibility(View.GONE);
                                    setText(R.id.bus_info_item_type,
                                            t.getString("mobilePhone"));
                                    bindListener(convertView, t);
                                    return convertView;
                                }
                            };
                            listView.setAdapter(adapter);
                        } else {
                            ((BaseMallAdapter<JSONObject>) adapter).add(objs);
                            ((BaseMallAdapter<JSONObject>) adapter).updateUI();
                        }

                    }

                    public void doCreate(final DialogInterface dialog,
                                         String openOrBack, String applyId,
                                         String audioType, String twoPwd, String remark) {
                        CustomProgressDialog cpd = Util.showProgress(
                                "正在处理您的操作...", BusinessInfoFrame.this);
                        final User user = UserData.getUser();
                        NewWebAPI.getNewInstance().aduditBusiness(
                                user.getUserId(), user.getMd5Pwd(), applyId,
                                audioType, new MD5().getMD5ofStr(twoPwd),
                                remark, openOrBack, new WebRequestCallBack() {
                                    @Override
                                    public void success(Object result) {
                                        JSONObject obj = JSON
                                                .parseObject(result.toString());
                                        if (200 != obj.getIntValue("code")) {
                                            Util.show(obj.getString("message"),
                                                    BusinessInfoFrame.this);
                                        } else {
                                            Util.show(obj.getString("message"),
                                                    BusinessInfoFrame.this);
                                            dialog.cancel();
                                            dialog.dismiss();
                                        }
                                    }
                                });
                    }

                    public void bindListener(View view, final JSONObject json) {
                        view.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //						String[] items = new String[] {
                                //								"会员账号：" + Util.getNo_pUserId(json.getString("userId")),
                                //								"会员姓名 ：" + json.getString("name"),
                                //								"系统角色 ：" + json.getString("level"),
                                //								"推荐人 ："
                                //										+ Util.getNo_pUserId(json
                                //												.getString("inviter")),
                                //										"招商人 ："
                                //												+ Util.getNo_pUserId(json
                                //														.getString("merchants")),
                                //												"所属区域 ：" + json.getString("zone") };
                                //
                                //						DialogInterface.OnClickListener click = new DialogInterface.OnClickListener() {
                                //							@Override
                                //							public void onClick(DialogInterface dialog,
                                //									int which) {
                                //							}
                                //						};
                                //
                                //						new AlertDialog.Builder(BusinessInfoFrame.this)
                                //						.setItems(items, click).show()
                                //						.setCanceledOnTouchOutside(true);
                                showMessage(Util.getNo_pUserId(json.getString("userId")),
                                        json.getString("name"),
                                        json.getString("level"),
                                        Util.getNo_pUserId(json.getString("inviter")),
                                        Util.getNo_pUserId(json.getString("merchants")),
                                        json.getString("zone"));
                            }
                        });
                    }

                    @Override
                    public void requestEnd() {
                        super.requestEnd();
                        status = 0;
                        cpd.cancel();
                        cpd.dismiss();
                    }
                });
    }

}
