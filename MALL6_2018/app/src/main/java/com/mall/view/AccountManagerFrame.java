package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mall.model.RedPackageInLetBean;
import com.mall.model.User;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.AccountHelp.AccountHelpUtil;
import com.mall.view.BusinessCircle.RedBeanAccountActivity;
import com.mall.view.databinding.AccountmanagerBinding;
import com.mall.view.databinding.ItemAccountManagerViewBinding;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能： 账户管理<br>
 * 时间： 2013-3-7<br>
 * 备注： <br>
 *
 * @author Lin.~
 */

public class AccountManagerFrame extends Activity {

    AccountmanagerBinding binding;
    private List<AccountType> dataSourceList = new ArrayList<>();
    private List<AccountType> dataList = new ArrayList<AccountType>() {{
        add(new AccountType(R.drawable.account_yw, "业务账户", "0", 0));
        add(new AccountType(R.drawable.account_cz, "充值账户", "0", 1));
        add(new AccountType(R.drawable.account_zz, "红包种子", "0", 2));
        add(new AccountType(R.drawable.account_xj, "现金红包", "0", 3));
        add(new AccountType(R.drawable.account_xf, "消费券", "0", 4));
//        add(new AccountType(R.drawable.account_xjq, "现金券", "0", 5));
        add(new AccountType(R.drawable.account_sb, "商币账户", "0", 6));
        add(new AccountType(R.drawable.account_gwk, "购物券", "0", 7));
//        add(new AccountType(R.drawable.newtixian, "提现账户", "0", 8));
    }};
    private AccountManagerAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.accountmanager);
        context = this;
        Util.initTitle(this, "账户管理", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AnimeUtil.startAnimation(context, view, R.anim.small_2_big, new AnimeUtil.OnAnimEnd() {
                    @Override
                    public void start() {

                    }

                    @Override
                    public void end() {
                        String parentName = "";
                        String userKey = "";
                        String yeMoney = dataSourceList.get(position).getNumber();
                        switch (dataSourceList.get(position).getTag()) {
                            case 0:
                                parentName = "业务账户";
                                userKey = "bus";
                                break;
                            case 1:
                                parentName = "充值账户";
                                userKey = "rec";
                                break;
                            case 2:
                                parentName = "红包种子账户";
                                userKey = "hb";
                                break;
                            case 3:
                                parentName = "现金红包账户";
                                userKey = "xj";
                                break;
                            case 4:
                                Intent intent1 = new Intent(context, RedBeanAccountActivity.class);
                                intent1.putExtra("title", "消费券账户");
                                startActivity(intent1);
                                return;
                            case 5:
                                Intent intent2 = new Intent(context, RedBeanAccountActivity.class);
                                intent2.putExtra("title", "现金券账户");
                                startActivity(intent2);
                                return;
                            case 6:
                                parentName = "商币账户";
                                userKey = "sb";
                                break;
                            case 7:

//                                parentName = "购物劵账户";
//                                userKey = "gwq";


                                Intent intent7 = new Intent(context, RedBeanAccountActivity.class);
                                intent7.putExtra("title", "我的购物券");
                                intent7.putExtra("bean", new RedPackageInLetBean());
                                startActivity(intent7);

                                return;
                            case 8:

//                                parentName = "购物劵账户";
//                                userKey = "gwq";


                                Intent intent8 = new Intent(context, RedBeanAccountActivity.class);
                                intent8.putExtra("title", "提现账户");
                                startActivity(intent8);


                                return;
                        }
                        String[] keys = new String[]{"parentName", "userKey", "yeMoney"};
                        String[] vals = new String[]{parentName, userKey, yeMoney};
                        Util.showIntent(context, AccountListFrame.class, keys, vals);
                    }

                    @Override
                    public void repeat() {

                    }
                });
            }
        });
    }

    public class AccountManagerAdapter extends BaseAdapter {

        Context context;
        List<AccountType> list;
        ItemAccountManagerViewBinding binding;

        public AccountManagerAdapter(Context context, List list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public AccountType getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_account_manager_view, null);
            binding = DataBindingUtil.bind(convertView);
            binding.setAccountType(list.get(position));
            binding.mAccountImage.setImageResource(list.get(position).getImage());
            if (position == 0 || position == 3 || position == 4 || position == 7)
                binding.mAccountLayout.setBackgroundColor(Color.parseColor("#f3f3f3"));
            else if (position == 1 || position == 2 || position == 5 || position == 6)
                binding.mAccountLayout.setBackgroundColor(Color.parseColor("#ffffff"));
            if (list.get(position).getTag() == 6) {//商币
                binding.mAccountNumber.setPostfixString("枚");
            } else {
                binding.mAccountNumber.setPrefixString("¥");
            }
            binding.mAccountNumber.setNumberString(list.get(position).getNumber());
            return convertView;
        }

    }

    public class AccountType {
        int image;
        String text;
        String number;
        int tag;

        public AccountType(int image, String text, String number, int tag) {
            this.image = image;
            this.text = text;
            this.number = number;
            this.tag = tag;
        }

        public int getImage() {
            return image;
        }

        public void setImage(int image) {
            this.image = image;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public int getTag() {
            return tag;
        }

        public void setTag(int tag) {
            this.tag = tag;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getYE();
    }



    private void getYE() {
        dataSourceList.clear();
        dataSourceList.addAll(dataList);
        final User user = UserData.getUser();

        AccountHelpUtil.getAccountYE(context,null, new AccountHelpUtil.CallBackAccountYE() {
            @Override
            public void getMoney(double account_money) {

            }

            @Override
            public void getMoneyStr(String account_money) {
                JSONObject json = JSON.parseObject(account_money.toString());
                dataSourceList.get(0).setNumber(json.getString("bus"));
                dataSourceList.get(1).setNumber(json.getString("rec"));
                dataSourceList.get(2).setNumber(json.getString("red_s"));
                dataSourceList.get(3).setNumber(json.getString("red_c"));
                dataSourceList.get(4).setNumber(json.getString("con"));
//                dataSourceList.get(5).setNumber(json.getString("cash_c"));
//                dataSourceList.get(6).setNumber(json.getString("sb"));
//                dataSourceList.get(7).setNumber(json.getString("shopping_voucher"));
                dataSourceList.get(5).setNumber(json.getString("sb"));
                dataSourceList.get(6).setNumber(json.getString("shopping_voucher"));
//                dataSourceList.get(8).setNumber(json.getString("Present_account"));
                for (int i = 0; i < dataSourceList.size(); i++) {
                    if (dataSourceList.get(i).getNumber().equals("0") && dataSourceList.get(i).getTag() == 6) {
                        Log.e("for1","KK"+dataSourceList.get(i).getText());
                        dataSourceList.remove(i);
                        break;
                    }
                }
                for (int i = 0; i < dataSourceList.size(); i++) {
                    if (user.getUserLevel().contains("普通会员") && dataSourceList.get(i).getTag() == 7) {
                        Log.e("for2","KK"+dataSourceList.get(i).getText());
                        dataSourceList.remove(i);
                        break;
                    }
                }

                for (int i = 0; i < dataSourceList.size(); i++) {
                    if (!user.getLevelId().equals("6") && dataSourceList.get(i).getTag() == 8) {
                        Log.e("for3","KK"+dataSourceList.get(i).getText());
                        dataSourceList.remove(i);
                        break;
                    }
                }
//                for (int i = 0; i < dataSourceList.size(); i++) {
//                    if (!user.getLevelId().equals("5")) {
//                        Log.e("for4","KK"+dataSourceList.get(i).getText());
//                        dataSourceList.remove(i);
//                        break;
//                    }
//                }
                if (Util.isNull(adapter)) {
                    adapter = new AccountManagerAdapter(context, dataSourceList);
                    binding.gridView.setAdapter(adapter);
                } else {
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

}
