package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.WhereBuilder;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.ShopCar;
import com.mall.model.ShopCarNumberModel;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * 2017  7 15 购物车
 *
 * @author Administrator
 */
public class ShopCarFrame extends Activity {

    private ShopCarListener adapter;
    private List<String> guidList = null;
    @ViewInject(R.id.shop_car_bottom_zj)
    private TextView bottom_zj;
    @ViewInject(R.id.shop_car_bottom_jf)
    private TextView bottom_jf;
    @ViewInject(R.id.shop_car_bottom_zssb)
    private TextView bottom_zssb;
    @ViewInject(R.id.shop_car_bottom_zshf)
    private TextView bottom_zshf;
    @ViewInject(R.id.shopcar_pay)
    private TextView shopcar_pay;
    @ViewInject(R.id.shopcar_del)
    private TextView shopcar_del;
    @ViewInject(R.id.imgtitle_320)
    private TextView imgtitle_320;
    @ViewInject(R.id.shopcar_all)
    private CheckBox shopcar_all;
    @ViewInject(R.id.shop_car_bottom_sbsm)
    private TextView sbsm;

    @ViewInject(R.id.preferetv)  //优惠提示
    private TextView preferetv;

//    private int ishealth = 0;

    private double zj = 0.00D;
    private double jf = 0.00D;
    private double fl = 0.00D;
    private double sbsum = 0.00;
    private DbUtils db;

    private Context context;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        guidList = new ArrayList<>();
        setContentView(R.layout.shop_cart_frame);
        ViewUtils.inject(this);

        context = this;
        Util.initTop(this, "购物车", Integer.MIN_VALUE, new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, null);
        db = DbUtils.create(this);

    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        bind();
    }


    public void bind() {
        fl = 0.00D;
        jf = 0.00D;
        zj = 0.00D;
        sbsum = 0.00D;
        final ListView listView = (ListView) findViewById(R.id.carShow);
        final LinearLayout LinearLayout = (LinearLayout) findViewById(R.id.shopcar_bottom_line);
        imgtitle_320.setText("编辑");
        imgtitle_320.setVisibility(View.VISIBLE);

        if (null == UserData.getUser()) {
            Util.showIntent("对不起，您还没有登录。", this, LoginFrame.class);
            return;
        }
        try {
            db.delete(ShopCarNumberModel.class, WhereBuilder.b("userid", "=",
                    UserData.getUser().getUserId()));
        } catch (DbException e) {
            e.printStackTrace();
        }
        Util.asynTask(this, "正在加载您的购物车...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                HashMap<String, List<ShopCar>> map = (HashMap<String, List<ShopCar>>) runData;
                List<ShopCar> list = map.get("data");
                if (null == list || 0 == list.size()) {
                    VoipDialog voipDialog = new VoipDialog("您的购物车还未加入任何商品，去选购吧！", ShopCarFrame.this, "去选购", "取消", new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Util.showIntent(context, StoreMainFrame.class);
                        }
                    }, null);
                    voipDialog.show();
                    LinearLayout.setVisibility(View.GONE);
                } else {
                    String pfrom = "";
                    LinearLayout.setVisibility(View.VISIBLE);
                    int number = 0;
                    for (ShopCar sc : list) {
                        int num = Util.getInt(sc.getNum());// 每种商品的数量
                        if (Util.isInt(sc.getFl()) || Util.isDouble(sc.getFl())) {
                            fl += Util.getDouble(sc.getFl());
                        }// 分账
                        // 是否来自商币区
                        if ("11".equals(sc.getPfrom()))
                            pfrom = "11";
                        // 总金额,可用积分
                        zj += Util.getDouble(sc.getAllPrice());
                        jf += Util.getDouble(sc.getJf());

                        sbsum += new BigDecimal(sc.getSb()).setScale(0,
                                BigDecimal.ROUND_HALF_UP).intValue()
                                * num;

                        // 得到商品数量。
                        number += num;
                    }

                    // 如果是商币区，则无分账
                    if ("11".equals(pfrom)) {
                        bottom_zssb.setVisibility(TextView.GONE);
                        bottom_zshf.setVisibility(TextView.GONE);
                    } else {
                        bottom_zssb.setVisibility(TextView.VISIBLE);
                        bottom_zshf.setVisibility(TextView.VISIBLE);
                    }
                    DecimalFormat df = new DecimalFormat("#0.00");
                    bottom_zshf.setText("￥" + df.format(fl));
                    bottom_zssb.setText("￥" + df.format(fl));

                    bottom_jf.setText("￥" + df.format(jf));


                    preferetv.setText(Html.fromHtml("使用消费券,立减" + "<font color=\"#009900\">" + df.format(jf)
                            + "</font>" + "元"));

                    bottom_zj.setText("￥" + df.format(zj));

                    sbsm.setText(""
                            + new BigDecimal(sbsum).setScale(0,
                            BigDecimal.ROUND_HALF_UP));

                    shopcar_pay.setText("结算" + "(" + list.size() + ")");
                    shopcar_del.setText("删除" + "(" + list.size() + ")");
                }
                int num = 0;
                for (int i = 0; i < list.size(); i++) {
                    num += Util.getInt(list.get(i).getNum());
                }
                ShopCarNumberModel scm = new ShopCarNumberModel();
                scm.setNumber(num);
                scm.setUserid(UserData.getUser().getNoUtf8UserId());
                try {
                    db.save(scm);
                } catch (DbException e) {
                    e.printStackTrace();
                }
                listView.setAdapter(adapter = new ShopCarListener(
                        ShopCarFrame.this, list, shopcar_del, shopcar_pay,
                        bottom_zj, preferetv, shopcar_all));
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getShopCar, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd()

                );
                List<ShopCar> lsc = web.getList(ShopCar.class);
                if (lsc != null && lsc.size() != 0) {
                    for (final ShopCar car : lsc) {
                        Log.e("car.getimg----", car.getImg() + "");
                        String s = car.getImg().replaceFirst("img.mall666.com",
                                Web.imgServer);
                        car.setImg(s);
                    }
                }
                HashMap<String, List<ShopCar>> map = new HashMap<String, List<ShopCar>>();
                map.put("data", lsc);
                return map;
            }
        });
    }

    @OnClick(R.id.imgtitle_320)
    public void delClick(View v) {

        if (imgtitle_320.getText().equals("编辑")) {
            imgtitle_320.setText("完成");
            shopcar_pay.setVisibility(View.GONE);
            shopcar_del.setVisibility(View.VISIBLE);
        } else {
            imgtitle_320.setText("编辑");
            shopcar_del.setVisibility(View.GONE);
            shopcar_pay.setVisibility(View.VISIBLE);
        }

    }

    @OnClick(R.id.shopcar_pay)
    public void shopClick(final View v) {
        if (null == UserData.getUser()) {
            Util.showIntent("对不起，您还没有登录!", this, LoginFrame.class);
            return;
        }

        if (Util.isNull(adapter.getSelectedItem())) {
            Util.show("请选择您要购买的商品！", this);
            return;
        }
        String secondPwd = UserData.getUser().getSecondPwd();
        if (Util.isNull(secondPwd) || "0".equals(secondPwd)) {
            Util.showIntent("您还没有设置交易密码，请先设置交易密码。", this,
                    SetSencondPwdFrame.class);
            return;
        }

        v.setEnabled(false);
        Util.asynTask(ShopCarFrame.this, "正在验证商品信息...", new IAsynTask() {
            @Override
            public void updateUI(final Serializable runData) {
                v.setEnabled(true);
                if (null == runData) {
                    Util.show("网络错误，请重试！", ShopCarFrame.this);
                    return;
                }
                if (!(runData + "").contains("success")) {
                    Util.show(runData + "", ShopCarFrame.this);
                } else {
                    if ((runData + "").contains("success:")) {
                        Util.showIntent(
                                "您的订单同时包含购物卡商品和常规商品，不能用购物卡账户支付！，您要继续吗？",
                                ShopCarFrame.this, "继续付款", "再逛逛",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String guids = adapter
                                                .getSelectedItem_1();
                                        Util.showIntent(ShopCarFrame.this,
                                                PayCommitFrame.class,
                                                new String[]{"guid", "zj",
                                                        "jf", "fl", "sbsm"},
                                                new String[]{guids, zj + "",
                                                        jf + "", fl + "",
                                                        sbsum + ""});
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                        dialog.dismiss();
                                    }
                                });
                    } else {
                        String guids = adapter.getSelectedItem_1();
                        Log.e("info===============", guids + "::" + zj + "::" + jf + "::" + fl + "::" + sbsum + "");
                        Util.showIntent(ShopCarFrame.this,
                                PayCommitFrame.class, new String[]{"guid",
                                        "zj", "jf", "fl", "sbsm"},
                                new String[]{guids, zj + "", jf + "",
                                        fl + "", sbsum + ""});


                    }
                }
            }

            @Override
            public Serializable run() {
                String guids = adapter.getSelectedItem_1();


                Web web = new Web(Web.validataShopOrder, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids=" + guids);
                return web.getPlan();
            }
        });
    }

    @OnClick(R.id.shopcar_del)
    public void deleteClick(final View v) {
        if (null == UserData.getUser()) {
            Util.showIntent("对不起，您还没有登录!", this, LoginFrame.class);
            return;
        }
        v.setEnabled(false);
        final String ids = adapter.getSelectedItem();
        if (Util.isNull(ids)) {
            Util.show("请选择要删除的商品！", this);
            return;
        }
        Util.asynTask(this, "正在删除中...", new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if ("success".equals(runData))
                    bind();
                else
                    Util.show(runData + "", ShopCarFrame.this);
                v.setEnabled(true);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.delShopCar, "userId="
                        + UserData.getUser().getUserId() + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&ids=" + ids);
                return web.getPlan();
            }
        });
    }

    public List<String> getGuidList() {
        return guidList;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Util.showIntent(ShopCarFrame.this, Lin_MainFrame.class,
                    new String[]{"toTab"}, new String[]{"usercenter"});
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}

class ShopCarItem {
    public CheckBox check;
    public ImageView img;
    public TextView num;
    public TextView youfei;
    public TextView shuxing;
    public TextView name;
    public TextView proce;
    public TextView count;
    public TextView jifen;
    public TextView del;
    public TextView add;
    public TextView shop_state;
}

class ShopCarListener extends BaseAdapter {
    private ShopCarFrame context;
    private LayoutInflater mFalter;
    private List<ShopCar> data;
    private List<String> delList;
    private BitmapUtils bmUtil;
    private TextView shopcar_del;
    private TextView shopcar_pay;
    private TextView preferetv;
    private CheckBox shopcar_all;
    private String first;
    private TextView bottom_zj;
    private double a = 0;
    private double b = 0;

    public ShopCarListener(ShopCarFrame context, List<ShopCar> data,
                           TextView shopcar_del, TextView shopcar_pay, TextView bottom_zj, TextView preferetv,
                           CheckBox shopcar_all) {
        this.context = context;
        this.data = data;
        this.shopcar_del = shopcar_del;
        this.shopcar_pay = shopcar_pay;
        this.shopcar_all = shopcar_all;
        this.preferetv = preferetv;
        this.bottom_zj = bottom_zj;
        if (!Util.isNull(bottom_zj.getText().toString())) {
            a = Double.parseDouble(""
                    + bottom_zj.getText().toString().replaceAll("￥", ""));

            try {
                b = Double.parseDouble(""
                        + preferetv.getText().toString().replaceAll("使用消费券,立减", "").replace("元", ""));
            } catch (Exception e) {
                b = 0.00;
            }

        }
        shopcar_all.setTag("");
        mFalter = LayoutInflater.from(context);
        delList = new ArrayList<String>();
        for (ShopCar sc : data)
            delList.add(sc.getGuid());
        bmUtil = new BitmapUtils(context);
    }

    public String getSelectedItem_1() {
        StringBuffer ids = new StringBuffer();
        for (String id : delList)
            ids.append("'" + id + "',");
        ids.setLength(ids.length() - 1);
        return ids.toString();
    }

    public String getSelectedItem() {
        StringBuffer ids = new StringBuffer();
        for (String id : delList)
            ids.append(id + "|,|");
        if (ids.length() == 0) {
            return null;
        }
        ids.setLength(ids.length() - 3);
        return ids.toString();
    }

    public void selectAll(boolean isSelect) {
        if (!isSelect) {
            delList.clear();
            this.notifyDataSetChanged();
            shopcar_pay.setText("结算" + "(" + delList.size() + ")");
            shopcar_del.setText("删除" + "(" + delList.size() + ")");
            bottom_zj.setText("￥" + 0);
            preferetv.setText(Html.fromHtml("使用消费券,立减" + "<font color=\"#009900\">" + 0
                    + "</font>" + "元"));
            return;
        }
        delList.clear();
        for (ShopCar c : data) {
            delList.add(c.getGuid());
        }
        shopcar_pay.setText("结算" + "(" + delList.size() + ")");
        shopcar_del.setText("删除" + "(" + delList.size() + ")");
        bottom_zj.setText("￥" + a);
        preferetv.setText(Html.fromHtml("使用消费券,立减" + "<font color=\"#009900\">" + b
                + "</font>" + "元"));
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ShopCarItem item = null;
        final ShopCar sc = data.get(position);
        if (null == convertView) {
            convertView = mFalter.inflate(R.layout.shop_cart_item, null);
            item = new ShopCarItem();
            item.check = (CheckBox) convertView
                    .findViewById(R.id.shop_car_checkbox);//
            item.img = (ImageView) convertView
                    .findViewById(R.id.shop_car_image);
            item.shop_state = convertView.findViewById(R.id.shop_state);
            item.name = (TextView) convertView.findViewById(R.id.shop_car_t1);
            item.shuxing = (TextView) convertView
                    .findViewById(R.id.shop_car_t2);
            item.proce = (TextView) convertView.findViewById(R.id.shop_car_t3);
            item.jifen = (TextView) convertView.findViewById(R.id.shop_car_t4);
            item.youfei = (TextView) convertView.findViewById(R.id.shop_car_t5);
            item.count = (TextView) convertView.findViewById(R.id.shop_car_t6);
            item.del = (TextView) convertView
                    .findViewById(R.id.shop_car_button1);
            item.num = (TextView) convertView.findViewById(R.id.shop_car_t7);
            item.add = (TextView) convertView
                    .findViewById(R.id.shop_car_button2);
            convertView.setTag(item);
        } else
            item = (ShopCarItem) convertView.getTag();
        item.check.setTag(sc.getGuid());
        item.check.setChecked(delList.contains(item.check.getTag() + ""));
        item.check.setOnCheckedChangeListener(null);
        if(sc.getIshealth().equals("1")){
            item.shop_state.setText("(健康商城)");
        }else {
            item.shop_state.setText(sc.getEnableSale().equals("7") ? "(创客商城)" : "(会员商城)");
        }

        item.check.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                if (isChecked) {
                    if (!delList.contains(buttonView.getTag() + "")) {
                        delList.add(buttonView.getTag() + "");
                    }

                } else if (delList.contains(buttonView.getTag() + "")) {
                    delList.remove(buttonView.getTag() + "");
                }
                shopcar_pay.setText("结算" + "(" + delList.size() + ")");
                shopcar_del.setText("删除" + "(" + delList.size() + ")");

                double allMOney = 0;

                double alljifeng = 0;
                for (int a = 0; a < data.size(); a++) {
                    ShopCar s = data.get(a);
                    for (int b = 0; b < delList.size(); b++) {
                        if (delList.get(b).equals(s.getGuid())) {
                            BigDecimal b1 = new BigDecimal(allMOney);
                            BigDecimal b2 = new BigDecimal(Double.parseDouble(s.getAllPrice()));
                            allMOney = b1.add(b2).doubleValue();
//							allMOney = allMOney
//									+ Double.parseDouble(s.getPrice());
                            BigDecimal b3 = new BigDecimal(alljifeng);
                            BigDecimal b4 = new BigDecimal(Double.parseDouble(s.getJf()));
                            alljifeng = b3.add(b4).doubleValue();
                        }
                    }
                }
//				DecimalFormat    df   = new DecimalFormat("######0.00");  
//				df.format(allMOney); 


                double b1 = round(allMOney, 3);

                bottom_zj.setText("￥" + b1);

                double b2 = round(alljifeng, 3);


                preferetv.setText(Html.fromHtml("使用消费券,立减" + "<font color=\"#009900\">" + b2
                        + "</font>" + "元"));
                shopcar_all.setTag("dont");
                if (delList.size() == data.size())
                    shopcar_all.setChecked(true);
                else
                    shopcar_all.setChecked(false);
                shopcar_all.setTag("");

            }
        });
        item.img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.showIntent(context, ProductDeatilFream.class,
                        new String[]{"url"}, new String[]{sc.getPid()});
            }
        });

        shopcar_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (!shopcar_all.getTag().equals("dont")) {
                    selectAll(isChecked);
                }

            }
        });
        convertView.setOnLongClickListener(new OnLongClickListener() {

            private VoipDialog diao;

            @Override
            public boolean onLongClick(View v) {
                diao = new VoipDialog("你将删除该商品！", context, "确定", "取消",
                        new OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                Util.asynTask(context, "正在删除中...",
                                        new IAsynTask() {
                                            @Override
                                            public void updateUI(
                                                    Serializable runData) {
                                                if ("success".equals(runData)) {
                                                    context.bind();
                                                } else
                                                    Util.show(runData + "",
                                                            context);

                                            }

                                            @Override
                                            public Serializable run() {
                                                Web web = new Web(
                                                        Web.delShopCar,
                                                        "userId="
                                                                + UserData
                                                                .getUser()
                                                                .getUserId()
                                                                + "&md5Pwd="
                                                                + UserData
                                                                .getUser()
                                                                .getMd5Pwd()
                                                                + "&ids="
                                                                + sc.getGuid());
                                                return web.getPlan();
                                            }
                                        });

                            }
                        }, new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        diao.cancel();
                        diao.dismiss();

                    }
                });

                diao.show();
                return false;
            }
        });
        bmUtil.display(item.img, sc.getImg());
        if (Util.isNull(sc.getP()) && Util.isNull(sc.getP1())) {
            item.shuxing.setVisibility(View.GONE);
        } else
            item.shuxing.setText(sc.getP() + "\t\t" + sc.getP1());
        DecimalFormat df = new DecimalFormat("#0.00");
        item.proce.setText("￥" + sc.getPrice());
        item.youfei.setText("￥" + sc.getYoufei());
        item.count.setText("￥" + sc.getAllPrice());
        double ji = Util.getDouble(sc.getJf());
        item.jifen.setText("" + df.format(ji) + "");
        item.num.setText(sc.getNum());
        item.name.setText(sc.getName());
        context.getGuidList().add(sc.getGuid());
        OnClickListener click = new OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setEnabled(false);
                String newAmount = "0";
                if ("del".equals(v.getTag()))
                    newAmount = "-1";
                else
                    newAmount = "1";
                final String new_Amount = newAmount;
                if ("0".equals(newAmount))
                    return;
                Util.asynTask(new IAsynTask() {
                    @Override
                    public void updateUI(Serializable runData) {
                        v.setEnabled(true);
                        if (null == runData) {
                            Util.show("网络错误，请重试！", context);
                            return;
                        }
                        if ("success".equals(runData + "")
                                || "0".equals(runData + ""))
                            context.bind();
                        else
                            Util.show(runData + "", context);
                    }

                    @Override
                    public Serializable run() {
                        Web web = new Web(Web.updateShopCarAmount, "userId="
                                + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd() + "&guid="
                                + sc.getGuid() + "&amount=" + new_Amount);
                        return web.getPlan();
                    }
                });
            }
        };
        if (1 != Util.getInt(sc.getNum())) {
            item.del.setOnClickListener(click);
        } else
            item.del.setEnabled(false);
        item.add.setOnClickListener(click);
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode();
    }

    public static double round(double v, int scale) {
        if (scale < 0) {
            throw new IllegalArgumentException("The   scale   must   be   a   positive   integer   or   zero");
        }
        BigDecimal b = new BigDecimal(Double.toString(v));
        BigDecimal one = new BigDecimal("1");
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
