package com.mall.CapitalAccount;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BasicActivity;
import com.mall.MessageEvent;
import com.mall.model.Bank;
import com.mall.model.User;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.BankUtil;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.mall.view.ShowPopWindow;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/30.
 */

public class MyAccount_BankListActivity extends BasicActivity {
    @BindView(R.id.cardlist)
    public RecyclerView cardlist;

    @BindView(R.id.title)
    public TextView title_tv;



    private String title;

    AccountAdapter accountAdapter;
    List<BankType> itemlist= new ArrayList<>();
    public static String TITLETAG="TITLETAG";

    public static String tag1="银行卡";
    public static String tag2="微信";
    public static String tag3="支付宝";
    public static String tag4="QQ";

    private String type_="";

    @Override
    public int getContentViewId() {
        return R.layout.activity_my_account__bank_list;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        init();
    }

    @Override
    public void EventCallBack(MessageEvent messageEvent) {

    }

    @OnClick({R.id.close})
    public void click(View view){
        switch (view.getId()){
            case R.id.close:
                finish();
                break;
        }
    }

    private void init() {
        initAdapter();
        initgetIntent();
        initdata();
    }

    private void initgetIntent() {
        Intent intent= getIntent();
        String str=intent.getStringExtra(MyAccount_BankListActivity.TITLETAG);
        if (!Util.isNull(str)){
            title=str;
        }

        title_tv.setText("我的"+title);

        if (title.equals(tag1)){

//            accountAdapter.addFooterView(getFooterView());
        }
    }
    private void initdata() {
         User user = UserData.getUser();
        BankType bankType=null;
         if (title.equals(tag1)){
             type_="0";
              bankType= new BankType(user.getBank(),user.getBankCard());
         }else if(title.equals(tag2)){
             type_="1";
             bankType= new BankType(user.getWeixin(),"微信");
         }else  if(title.equals(tag3)){
             type_="2";
             bankType= new BankType(user.getAlipay(),"支付宝");
         }else  if (title.equals(tag4)){
             type_="3";
             bankType= new BankType(user.getAlipay(),"QQ");
         }


        itemlist.add(bankType);
//        itemlist.add(new BankType("3123","微信"));
        accountAdapter.notifyDataSetChanged();
    }

    private void initAdapter() {
        cardlist.setLayoutManager(new LinearLayoutManager(context));
        accountAdapter=new AccountAdapter(R.layout.accountlistitem,itemlist);
        cardlist.setAdapter(accountAdapter);
    }

    public View getFooterView() {
        View view = LayoutInflater.from(context).inflate(R.layout.addbankline,null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,AddBankCardActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

        public class AccountAdapter extends BaseQuickAdapter<BankType,BaseViewHolder> {


        public AccountAdapter(@LayoutRes int layoutResId, @Nullable List<BankType> data) {
            super(layoutResId, data);
        }

        @Override
        protected void convert(BaseViewHolder helper, BankType item) {
            View view=helper.getView(R.id.accountitemroot);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    View mContentViewa = LayoutInflater.from(context).inflate(R.layout.deletaccount, null);
                    mContentViewa.
                            setBackground(SelectorFactory.newShapeSelector()
                                    .setDefaultBgColor(Color.parseColor("#ffffff"))
                                    .setStrokeWidth(Util.dpToPx(context,1))
                                    .setCornerRadius(Util.dpToPx(context,5))
                                    .setDefaultStrokeColor(Color.parseColor("#bf767675"))
                                    .create()
                            );
                    int witha= (int) (Util.getScreenWidth()*1);
                    final PopupWindow mPopUpWindowa=  ShowPopWindow.showSharebottomWindow(mContentViewa,context,witha, ViewGroup.LayoutParams.WRAP_CONTENT,R.style.popwin_pop_bottom_anim_style);
                    TextView deleteButton= (TextView) mContentViewa.findViewById(R.id.deleteButton);
                    TextView cancelButton= (TextView) mContentViewa.findViewById(R.id.cancelButton);
                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopUpWindowa.dismiss();
                        }
                    });
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mPopUpWindowa.dismiss();
                            dele();

                        }
                    });
                }
            });
            ImageView cardhead=helper.getView(R.id.cardhead);
            TextView cardname=helper.getView(R.id.cardname);
            TextView cardid=helper.getView(R.id.cardid);
            ImageView rlback=helper.getView(R.id.rlback);
            if (title.equals(tag1)){

                Bank bank= BankUtil.getBank(item.getName());
                cardhead.setImageResource(bank.getIntegers()[1]);
                cardname.setText(item.getName());

                String str=item.getCardid() .substring(0,3) + "*******" + item.getCardid() .substring(item.getCardid() .length()-3, item.getCardid() .length());
                cardid.setText(str);

                rlback.setImageResource(bank.getIntegers()[3]);

            }else if(title.equals(tag2)){
                cardhead.setImageResource(R.drawable.weixinicon1);
                cardname.setText("微信");
                cardid.setText("");
                rlback.setImageResource(R.drawable.weixinbanner1);
            }else  if(title.equals(tag3)){
                cardhead.setImageResource(R.drawable.pay_item_ali1);
                cardname.setText("支付宝");
                cardid.setText(item.getName());
                rlback.setImageResource(R.drawable.pay_banner_ali);
            }else  if(title.equals(tag4)){
                cardhead.setImageResource(R.drawable.qq_item_ali1);
                cardname.setText("QQ");
                cardid.setText("");
                rlback.setImageResource(R.drawable.qqbanner1);
            }

            rlback.setBackground(SelectorFactory.newShapeSelector()
                    .setDefaultBgColor(Color.parseColor("#00ffffff"))
                    .setStrokeWidth(Util.dpToPx(this.mContext,1))
                    .setCornerRadius(Util.dpToPx(this.mContext,1))
                    .setDefaultStrokeColor(Color.parseColor("#00ffffff"))
                    .create());
        }

    }
    class BankType{
        String name;
        String cardid;

        public BankType(String name, String cardid) {
            this.name = name;
            this.cardid = cardid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCardid() {
            return cardid;
        }

        public void setCardid(String cardid) {
            this.cardid = cardid;
        }
    }

    private void dele(){
        final CustomProgressDialog cpd = Util.showProgress("删除中...", this);
        // 0是 qq 1是微信
//        if (type_.equals("3")){
//            NewWebAPI.getNewInstance().getWebRequest("/user.aspx?call=DL_Binding_third_user&userId="
//                            + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
//                            +"&type="+0
//                    ,
//                    new NewWebAPIRequestCallback() {
//
//                        @Override
//                        public void timeout() {
//                            Util.show("网络超时！", context);
//                            return;
//                        }
//
//                        @Override
//                        public void success(Object result) {
//
//                            if (Util.isNull(result)) {
//                                Util.show("网络异常，请重试！", context);
//                                return;
//                            }
//                            JSONObject json = JSON.parseObject(result.toString());
//                            if (200 != json.getIntValue("code")) {
//                                Util.show(json.getString("message"), context);
//                                return;
//                            }
//
//
//                            String jieguo=json.getString("message");
//                            itemlist.clear();
//                            accountAdapter.notifyDataSetChanged();
//
//                        }
//
//                        @Override
//                        public void requestEnd() {
//                            cpd.cancel();
//                            cpd.dismiss();
//                        }
//
//                        @Override
//                        public void fail(Throwable e) {
//
//                        }
//                    });
//        }else {
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=clear_band_wx_alipay&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        + "&type_=" + type_ + "&code=" + "" + "&code_img=" + ""
                ,
                new NewWebAPIRequestCallback() {

                        @Override
                        public void timeout() {
                            Util.show("网络超时！", context);
                            return;
                        }

                        @Override
                        public void success(Object result) {

                            if (Util.isNull(result)) {
                                Util.show("网络异常，请重试！", context);
                                return;
                            }
                            JSONObject json = JSON.parseObject(result.toString());
                            if (200 != json.getIntValue("code")) {
                                Util.show(json.getString("message"), context);
                                return;
                            }


                            String jieguo=json.getString("message");
                            itemlist.clear();
                            accountAdapter.notifyDataSetChanged();

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

    }


