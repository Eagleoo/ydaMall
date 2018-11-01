package com.mall.view;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.model.WebSiteModel;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

public class WebSiteRequestRecordFrame extends AppCompatActivity {

    @ViewInject(R.id.listView)
    private ListView listView;

    List<WebSiteModel> webSiteModelslist = new ArrayList<>();
    WebSiteAdapter adapter;
    int key = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.website_request_record_frame);
        Util.initTitle(this, "我的申请记录", new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        ViewUtils.inject(this);
        init();
    }

    private void init() {
        final CustomProgressDialog cp = CustomProgressDialog.showProgressDialog(this, "正在获取您的申请记录...");
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<WebSiteModel>> hash = (HashMap<String, List<WebSiteModel>>) runData;
                List<WebSiteModel> list = hash.get("list");
                webSiteModelslist.addAll(list);
                key += 1;
                if (key == 3) {
                    cp.dismiss();
                    adapter = new WebSiteAdapter(WebSiteRequestRecordFrame.this, webSiteModelslist);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public Serializable run() {
                String method = Web.getWebSiteRequestList;
                Web web = new Web(method, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&page=1&size=99");
                HashMap<String, List<WebSiteModel>> hash = new HashMap<>();
                hash.put("list", web.getList(WebSiteModel.class));
                return hash;
            }
        });
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<WebSiteModel>> hash = (HashMap<String, List<WebSiteModel>>) runData;
                List<WebSiteModel> list = hash.get("list");
                webSiteModelslist.addAll(list);
                key += 1;
                if (key == 3) {
                    cp.dismiss();
                    adapter = new WebSiteAdapter(WebSiteRequestRecordFrame.this, webSiteModelslist);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public Serializable run() {
                String method = Web.getProxyInfoList;
                Web web = new Web(method, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&page=1&size=99");
                HashMap<String, List<WebSiteModel>> hash = new HashMap<>();
                hash.put("list", web.getList(WebSiteModel.class));
                return hash;
            }
        });
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<WebSiteModel>> hash = (HashMap<String, List<WebSiteModel>>) runData;
                List<WebSiteModel> list = hash.get("list");
                webSiteModelslist.addAll(list);
                key += 1;
                if (key == 3) {
                    cp.dismiss();
                    adapter = new WebSiteAdapter(WebSiteRequestRecordFrame.this, webSiteModelslist);
                    listView.setAdapter(adapter);
                }
            }

            @Override
            public Serializable run() {
                String method = Web.getWebSiteLMSJRequestList;
                Web web = new Web(method, "userid="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&page=1&size=99");
                HashMap<String, List<WebSiteModel>> hash = new HashMap<>();
                hash.put("list", web.getList(WebSiteModel.class));
                return hash;
            }
        });
    }

    class WebSiteHolder {
        public LinearLayout line;
        public TextView type;
        public TextView status;
        public TextView date;
        public Button pay;
        public Button quit;
        public LinearLayout state_1;
        public TextView state_2;
        public TextView state_3;
    }

    class WebSiteAdapter extends BaseAdapter {

        private Context context;
        private List<WebSiteModel> list;
        private LayoutInflater flater;

        public WebSiteAdapter(Context context, List<WebSiteModel> list) {
            super();
            this.context = context;
            flater = LayoutInflater.from(context);
            this.list = list;
        }

        public void clear() {
            synchronized (this) {
                this.list.clear();
            }
        }

        public void addData(List<WebSiteModel> list) {
            this.list.addAll(list);
        }

        public void updateUI() {
            this.notifyDataSetChanged();
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            synchronized (this) {
                if (0 == list.size())
                    return null;
            }
            final WebSiteModel model = list.get(position);
            WebSiteHolder holder = null;
            if (null == convertView) {
                convertView = flater.inflate(
                        R.layout.website_request_record_frame_list_item, null);
                holder = new WebSiteHolder();
                holder.line = convertView.findViewById(R.id.state_line);
                holder.type = convertView.findViewById(R.id.website_list_item_type);
                holder.status = convertView.findViewById(R.id.website_list_item_status);
                holder.date = convertView.findViewById(R.id.website_list_item_date);
                holder.pay = convertView.findViewById(R.id.website_list_item_pay);
                holder.quit = convertView.findViewById(R.id.website_list_item_quit);
                holder.state_1 = convertView.findViewById(R.id.state_1);
                holder.state_2 = convertView.findViewById(R.id.state_2);
                holder.state_3 = convertView.findViewById(R.id.state_3);
                convertView.setTag(holder);
            } else
                holder = (WebSiteHolder) convertView.getTag();
            String Type = model.getType();
            if (Type.equals("创业空间")) {
                Type = "城市经理";
            }
            holder.type.setText(Type);
            holder.status.setText(model.getStatusId());
            holder.date.setText(model.getDate());
            if ("待付款".equals(model.getStatusId()) || "处理中".equals(model.getStatusId()) || "未初审".equals(model.getStatusId())) {
                holder.state_1.setVisibility(Button.VISIBLE);
                holder.state_2.setVisibility(Button.GONE);
                holder.state_3.setVisibility(Button.GONE);
                holder.line.setVisibility(View.VISIBLE);
            } else if ("未通过".equals(model.getStatusId()) || "已驳回".equals(model.getStatusId())) {
                holder.state_1.setVisibility(View.GONE);
                holder.state_2.setVisibility(View.VISIBLE);
                holder.state_3.setVisibility(View.GONE);
                holder.line.setVisibility(View.GONE);
            } else if ("已通过".equals(model.getStatusId()) || "已开通".equals(model.getStatusId())) {
                holder.state_1.setVisibility(View.GONE);
                holder.state_2.setVisibility(View.GONE);
                holder.state_3.setVisibility(View.VISIBLE);
                holder.line.setVisibility(View.GONE);
            }

            OnClickListener clickListener = new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog builder = new AlertDialog.Builder(context)
                            .create();
                    builder.setTitle("您的申请详细");
                    String msg = "申请用户：" + Util.getNo_pUserId(model.getUserid()) + "\n";
                    msg += "申请时间：" + model.getDate() + "\n";
                    msg += "申请类型：" + model.getType() + "\n";
                    String pay = "";
                    if (Util.isNull(model.getPayType()))
                        pay = "现金支付";
                    else if ("1".equals(model.getPayType()))
                        pay = "现金支付";
                    else if ("2".equals(model.getPayType()))
                        pay = "充值账户";
                    else if ("3".equals(model.getPayType()))
                        pay = "网银支付";
                    else if ("6".equals(model.getPayType()))
                        pay = "微信支付";
                    else
                        pay = "支付宝";
                    msg += "付款方式：" + pay + "\n";
                    msg += "加盟金额：" + model.getMoney() + "\n";
                    msg += "辅导老师：" + model.getHandle() + "\n";
                    builder.setMessage(msg);
                    if ("3".equals(model.getStatusId())
                            || "未初审".equals(model.getStatusId())
                            || "处理中".equals(model.getStatusId())) {
                        builder.setButton3("撤消申请",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        can(model, model.getId(),
                                                model.getrType());
                                    }
                                });
                    }
                    builder.setButton2("关闭",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    dialog.cancel();
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            };
            convertView.setOnClickListener(clickListener);
            holder.pay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    final String applyid = model.getId();
                    final String rType = model.getrType();
                    final String type = model.getType();

                    Util.asynTask(new IAsynTask() {
                        @Override
                        public Serializable run() {
                            String method = Web.canelWebSite;
                            if ("proxy".equals(rType))
                                method = Web.canelProxy;
                            else if ("angel".equals(rType))
                                method = Web.canelAngel;
                            Web web = new Web(method, "userid="
                                    + UserData.getUser().getUserId()
                                    + "&md5Pwd="
                                    + UserData.getUser().getMd5Pwd()
                                    + "&applyid=" + applyid);
                            return web.getPlan();
                        }

                        @Override
                        public void updateUI(Serializable runData) {
                            if (null == runData) {
                                Util.show("网络异常，请重试！", context);
                                return;
                            }
                            if ("success".equals(runData)) {
                                Class cl = null;
                                if (type.equals("移动创客")) {
                                    cl = MoveGuestFrame.class;
                                } else if (type.equals("联盟商家")) {
                                    cl = RequestAllianceFrame.class;
                                } else if (type.equals("创业大使")) {
                                    cl = PioneerAngelFrame.class;
                                } else if (type.equals("城市经理")) {
                                    cl = SiteFrame.class;
                                } else if (type.equals("购物卡")) {
                                    cl = RequestShopCardFrame.class;
                                } else if (type.equals("城市总监")) {
                                    cl = RequestCityCenterFrame.class;
                                } else if (type.equals("见习创客")) {
                                    cl = a_Jxck.class;
                                } else if (type.contains("大学生")) {
                                    cl = a_StudentMood.class;
                                }
                                if (model.getIsxfdzh() != null && model.getIsxfdzh().equals("1")) {
                                    Intent intent = new Intent();
                                    intent.setClass(context, SiteFrame.class);
                                    intent.putExtra("className", SiteFrame.class.toString());
                                    intent.putExtra("type", "2680");
                                    context.startActivity(intent);
                                } else {
                                    Util.showIntent(context, cl, new String[]{"OrderId",}, new String[]{model.getOrderId()});
                                }
                                webSiteModelslist.clear();
                                key = 0;
                                init();
                            }
                        }
                    });
                    v.setEnabled(true);
                }
            });
            holder.quit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setEnabled(false);
                    can(model, model.getId(), model.getrType());
                    v.setEnabled(true);
                }
            });
            return convertView;
        }

        public void can(final WebSiteModel model, final String applyid,
                        final String rType) {
            Util.asynTask(context, "正在撤销...", new IAsynTask() {
                @Override
                public Serializable run() {
                    String method = Web.canelWebSite;
                    if ("proxy".equals(rType))
                        method = Web.canelProxy;
                    else if ("angel".equals(rType))
                        method = Web.canelAngel;
                    Web web = new Web(method, "userid="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&applyid="
                            + applyid);
                    return web.getPlan();
                }

                @Override
                public void updateUI(Serializable runData) {
                    if (null == runData) {
                        Util.show("网络异常，请重试！", context);
                        return;
                    }
                    if ("success".equals(runData)) {
                        Util.show("撤销申请成功！", context);
                        webSiteModelslist.clear();
                        key = 0;
                        init();
                    } else
                        Util.show(runData + "", context);
                }
            });
        }
    }

}
