package com.mall.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.mall.model.InviterInfo;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.MyPopWindow;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能： 转账窗体<br>
 * 时间： 2013-3-24<br>
 * 备注： <br>
 *
 * @author Lin.~
 */
public class MoneyToMoneyFrame extends Activity implements OnItemClickListener {
    private Intent intent = null;
    private TextView types = null;
    private Button submit = null;
    private Button clear = null;
    private EditText bierenName = null;
    private EditText bierenMoney = null;
    private EditText twoPwd = null;
    private TextView accName = null;
    private TextView number = null;
    private String method;
    private LinearLayout moneyLine = null;
    private TextView userInfo = null;
    private PopupWindow popupWindow;
    private NumbersAdapter adapter;
    private TextView shoukuan;
    private List<String> numbers = new ArrayList<String>();
    private LinearLayout zhuanzhangzhanghu;
    private String acceptUser = "";
    private String[] account = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.money_to_money);
    }

    @Override
    public void onStart() {
        super.onStart();
        initComponent();
        init();
    }

    private void initComponent() {
        types = findViewById(R.id.zh_type);
        submit = Util.getButton(R.id.moneySubmit, this);
        moneyLine = this.findViewById(R.id.money_userInfoLine);
        shoukuan = findViewById(R.id.shoukuan);
        zhuanzhangzhanghu = findViewById(R.id.zhuanzhangzhanghu);
        userInfo = Util.getTextView(R.id.money_userInfo, this);
        submit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userId = bierenName.getText().toString();
                final String money = bierenMoney.getText().toString();
                if (Util.isNull(userId)) {
                    Util.show("请输入账户名称/手机号码!", MoneyToMoneyFrame.this);
                    return;
                }
                if (!Util.isDouble(money)) {
                    Util.show("请输入正确的金额!", MoneyToMoneyFrame.this);
                    return;
                }
//                parentValue
                if (parentValue.toString().equals("业务账户") || parentValue.toString().equals("现金红包账户") || parentValue.toString().equals("现金券账户")) {
                    if (Integer.parseInt(money) % 100 != 0) {
                        Util.show("请输入100或100的整数倍", MoneyToMoneyFrame.this);
                        return;
                    }
                }


                if (Util.isNull(twoPwd.getText().toString())) {
                    Util.show("请输入您的交易密码!", MoneyToMoneyFrame.this);
                    return;
                }
                if (Util.isNull(acceptUser)) {
                    Util.show("未获取到接收方信息!", MoneyToMoneyFrame.this);
                    return;
                }
                if (!Util.isNetworkConnected(MoneyToMoneyFrame.this)) {
                    Util.show("无网络连接", MoneyToMoneyFrame.this);
                    return;
                }
                Util.asynTask(MoneyToMoneyFrame.this, "正在为您转账...",
                        new IAsynTask() {
                            @Override
                            public void updateUI(Serializable runData) {
                                if ("success".equals(runData + "")) {
                                    new MyPopWindow.MyBuilder(MoneyToMoneyFrame.this, "转账成功", "确定", new OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Intent intent = new Intent();
                                            intent.setClass(
                                                    MoneyToMoneyFrame.this,
                                                    AccountManagerFrame.class);
                                            intent.putExtra(
                                                    "className",
                                                    MoneyToMoneyFrame.this
                                                            .getClass()
                                                            .toString());
                                            startActivity(intent);
                                            finish();
                                        }
                                    })
                                            .setTitle("提示").build().showCenter();

                                } else {
                                    Util.show(runData + "",
                                            MoneyToMoneyFrame.this);
                                }
                            }

                            @Override
                            public Serializable run() {
                                String param = "userId="
                                        + UserData.getUser().getUserId()
                                        + "&md5Pwd="
                                        + UserData.getUser().getMd5Pwd()
                                        + "&twoPwd="
                                        + new MD5().getMD5ofStr(twoPwd
                                        .getText().toString())
                                        + "&reciverId=" + Util.get(userId)
                                        + "&dpoint=" + money;
                                // 0充值，1预存，2赠送
                                String selectedValue = types.getText()
                                        .toString().trim()
                                        + "";
                                if ("充值账户".equals(selectedValue))
                                    param += "&zType=0";
                                else if ("购物卡账户".equals(selectedValue))
                                    param += "&zType=1";
                                    // else if ("购物卡账户1".equals(selectedValue))
                                    // param += "&zType=11";
                                else if ("红包豆账户".equals(selectedValue))
                                    param += "&zType=3";
                                else
                                    param += "&zType=2";
                                Web web = new Web(method, param);
                                return web.getPlan();
                            }
                        });

            }
        });
        clear = Util.getButton(R.id.moneyBack, this);
        clear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bierenName.setText("");
                bierenMoney.setText("");
                twoPwd.setText("");
                moneyLine.setVisibility(LinearLayout.GONE);
                userInfo.setText("");
            }
        });
        bierenName = Util.getEditText(R.id.bierenName, this);
        if (!Util.isNull(this.getIntent().getStringExtra("userid"))) {
            bierenName.setText(this.getIntent().getStringExtra("userid"));
        }
        bierenName.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    getUserInfo();
                }
            }
        });
        bierenMoney = Util.getEditText(R.id.bierenMoney, this);
        twoPwd = Util.getEditText(R.id.zh_twoPwd, this);
        accName = Util.getTextView(R.id.accName, this);
        number = Util.getTextView(R.id.number, this);
    }


    private void getUserInfo() {
        if (Util.isNull(bierenName.getText().toString()))
            return;
        Util.asynTask(new IAsynTask() {
            @Override
            public void updateUI(Serializable runData) {
                if (null == runData) {
                    Util.show("网络错误，请重试！", MoneyToMoneyFrame.this);
                    return;
                }
                InviterInfo ii = (InviterInfo) runData;
                if (Util.isNull(ii.getUserid())) {
                    userInfo.setText("用户资料：　");
                    Util.show("该用户不存在", MoneyToMoneyFrame.this);
                    return;
                }
                acceptUser = ii.getName() + "_";
                String userKey = intent.getStringExtra("userKey");
                if ("sb".equals(userKey)) {
                    if (Util.getInt(UserData.getUser()
                            .getShopTypeId()) <= 2) {
                        if (!"10".equals(ii.getShopType())) {
                            Util.show("普通用户只能转给【联盟商家】",
                                    MoneyToMoneyFrame.this);
                            return;
                        }
                    }
                }

                moneyLine.setVisibility(LinearLayout.VISIBLE);
                String name = ii.getName();
                if (2 <= ii.getName().length()) {
                    name = name.substring(0, 1) + "*";
                    if (3 <= ii.getName().length())
                        name += ii.getName().substring(
                                ii.getName().length() - 1);
                }
                String phone = ii.getPhone();
                if (!Util.isNull(phone))
                    phone = phone.substring(0, 3)
                            + "****"
                            + phone.subSequence(phone.length() - 4,
                            phone.length());
                userInfo.setText("用户资料：" + name + "　" + phone);
            }

            @Override
            public Serializable run() {
                Web web = new Web(Web.getInviter, "userId="
                        + UserData.getUser().getUserId()
                        + "&md5Pwd="
                        + UserData.getUser().getMd5Pwd() + "&uid="
                        + Util.get(bierenName.getText().toString()));
                return web.getObject(InviterInfo.class);
            }
        });

    }

    String parentValue = "";

    private void init() {

        numbers.clear();
        intent = this.getIntent();
        parentValue = intent.getStringExtra("parentName").replace(
                "转账", "");
        final String userKey = intent.getStringExtra("userKey");
        Log.e("userKey", "userKey" + userKey);
        if ("rec".equals(userKey)) {
            account = new String[]{"充值账户"};
            method = Web.recToRecharge;
        } else if ("bus".equals(userKey)) {
            account = new String[]{"充值账户"
//                    , "红包豆账户"
            };
            method = Web.busToMoney;
        } else if ("sto".equals(userKey)) {
            account = new String[]{"购物卡账户"};
            method = Web.stoToMoney_;
        } else if ("han".equals(userKey)) {
            account = new String[]{"消费券账户"};
            method = Web.conToCon;
        } else if ("sb".equals(userKey)) {
            account = new String[]{"商币账户"};
            method = Web.sbToSb;
            int level = Util.getInt(UserData.getUser().getLevelId());
            int shopType = Util.getInt(UserData.getUser().getShopTypeId());
            LogUtils.e("level=" + level + "   shopType=" + shopType);
            if (2 <= level && 3 < shopType) {
                method = Web.usersbToSb;
            }
        } else if ("cp".equals(userKey)) {
            account = new String[]{"充值账户"};
            method = Web.cpToRec;
        } else if ("xj".equals(userKey)) {
            account = new String[]{"充值账户"
//                    , "红包豆账户"
            };
            method = "/Red_CashToMoney";
//            bierenName.setText(UserData.getUser().getUserId());
        }
        for (int i = 0; i < account.length; i++) {
            numbers.add(account[i]);
        }
        types.setText(account[0]);
        types.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectNumberDialog();
            }
        });
        ArrayAdapter<String> spada1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, account);
        spada1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        if (parentValue.equals("消费券账户") || parentValue.equals("商币账户")) {
            shoukuan.setText("转入账户");
            zhuanzhangzhanghu.setVisibility(View.GONE);
            bierenName.setHint("请输入账户名称");
        }
        if (parentValue.equals("业务账户") || parentValue.equals("现金红包账户")) {
            bierenMoney.setHint("请输入100的整数倍");
        }
        Util.initTop(this, parentValue + "转账", Integer.MIN_VALUE, null);
        if (parentValue.equals("充值账户")) {
            findViewById(R.id.showtv).setVisibility(View.GONE);
        }
        if (null != UserData.getUser()) {
            Util.asynTask(this, "正在获取您的余额...", new IAsynTask() {
                @Override
                public void updateUI(Serializable runData) {
                    String cMoney = runData + "";
                    if (!Util.isNull(cMoney))
                        cMoney = Util.getDouble(Util.getDouble(cMoney), 2) + "";
                    if ("sb".equals(userKey))
                        cMoney = Util.getDouble(Util.getDouble(cMoney), 0) + "";
                    accName.setText("您当前的" + parentValue + "余额：");
                    number.setText(cMoney);
                }

                @Override
                public Serializable run() {
                    if (userKey.equals("xj")) {
                        return new Web(Web.getMoney, "userId="
                                + UserData.getUser().getUserId() + "&md5Pwd="
                                + UserData.getUser().getMd5Pwd()
                                + "&type=red_c").getPlan();
                    }
                    return new Web(Web.getMoney, "userId="
                            + UserData.getUser().getUserId() + "&md5Pwd="
                            + UserData.getUser().getMd5Pwd() + "&type="
                            + userKey).getPlan();
                }
            });
        } else {
            Util.show("对不起，请先登录!", this);
        }
    }

    /**
     * 弹出选择号码对话框
     */
    @SuppressLint("ResourceAsColor")
    private void showSelectNumberDialog() {

        ListView lv = new ListView(this);
        lv.setBackgroundResource(R.drawable.liner2_border_white);
        // 隐藏滚动条
        lv.setVerticalScrollBarEnabled(false);
        // 让listView没有分割线
        lv.setDividerHeight(1);
        lv.setOnItemClickListener(this);
        lv.setCacheColorHint(android.R.color.transparent);
        adapter = new NumbersAdapter();
        lv.setAdapter(adapter);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        popupWindow = new PopupWindow(lv, types.getWidth() - 4, numbers.size()
                * Util.dpToPx(this, 40F));
        // 设置点击外部可以被关闭
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.showAtLocation(this.findViewById(R.id.moneytoMoney_),
                Gravity.CENTER, 0, 0); // 显示
    }

    class NumbersAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return numbers.size();
        }

        @Override
        public Object getItem(int position) {
            return numbers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            NumberViewHolder mHolder = null;
            if (convertView == null) {
                mHolder = new NumberViewHolder();
                convertView = LayoutInflater.from(MoneyToMoneyFrame.this)
                        .inflate(R.layout.item_spinner_numbers, null);
                mHolder.tvNumber = (TextView) convertView
                        .findViewById(R.id.tv_number);
                convertView.setTag(mHolder);
            } else {
                mHolder = (NumberViewHolder) convertView.getTag();
            }
            if (types.getText().toString().equals(numbers.get(position))) {
                Drawable dra = MoneyToMoneyFrame.this.getResources()
                        .getDrawable(R.drawable.pay_item_checked);
                dra.setBounds(0, 0, dra.getMinimumWidth(),
                        dra.getMinimumWidth());
                mHolder.tvNumber.setCompoundDrawables(dra, null, null, null);
            }
            mHolder.tvNumber.setText(numbers.get(position));
            return convertView;
        }

    }

    public class NumberViewHolder {
        public TextView tvNumber;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        String number = numbers.get(position);
//        if (number.equals("红包豆账户")) {
//            bierenName.setText(UserData.getUser().getUserId());
//            bierenName.setEnabled(false);
//            getUserInfo();
//        } else {
//            bierenName.setText("");
//            bierenName.setEnabled(true);
//        }

        bierenName.setText("");
        userInfo.setText("");
        moneyLine.setVisibility(View.GONE);


        types.setText(number);
        popupWindow.dismiss();
    }
}
