package com.mall.view.topupcenter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.BaseMallAdapter;
import com.mall.MessageEvent;
import com.mall.model.LatestPhone;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.AsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.PhoneCommitFream;
import com.mall.view.PhoneFream;
import com.mall.view.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Administrator on 2017/9/19.
 */

public class TelephoneRechargeFragment extends Fragment {

    @ViewInject(R.id.phone_charge_num)
    private EditText phoneEt;


    @ViewInject(R.id.charge_phone_clear)
    private ImageView charge_phone_clear;

    @ViewInject(R.id.checkmyphonetv)
    private  TextView checkmyphone;

    @ViewInject(R.id.show_phone_charge_addr)
    private TextView show_phone_charge_addr;

    @ViewInject(R.id.phone_latest_list)
    private ListView phone_latest_list;


    @ViewInject(R.id.phone_charge_dhb)
    private ImageView phone_charge_dhb;



    @ViewInject(R.id.tv50)
    private TextView tv50;

    @ViewInject(R.id.tv49)
    private TextView tv49;

    @ViewInject(R.id.tv100)
    private TextView tv100;

    @ViewInject(R.id.tv99)
    private TextView tv99;

    @ViewInject(R.id.tv300)
    private TextView tv300;

    @ViewInject(R.id.tv299)
    private TextView tv299;

    @ViewInject(R.id.phone_charge_money50)
    private LinearLayout phone_charge_money50;
    @ViewInject(R.id.phone_charge_money100)
    private LinearLayout phone_charge_money100;
    @ViewInject(R.id.phone_charge_money300)
    private LinearLayout phone_charge_money300;

    private VoipDialog voipDialog;


    private Context context;

    private String phone_charge_addr;

    private String money;

    private int isFirstClick = 1;

    String phone = "";
    String unum = "";




    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getActivity();
        EventBus.getDefault().register(this);  //注册
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_telephonerecharge,container,false);
        ViewUtils.inject(this, view);
        Bundle bundle = getArguments();
        String phone1 = bundle.getString("phone");
        String unum1 = bundle.getString("unum");
        if (!Util.isNull(phone1)){
            phone=phone1;
        }
        if (!Util.isNull(unum1)){
            unum=unum1;
        }

        initListening();
        String str=UserData.getUser().getMobilePhone();
        if (!Util.isNull(str) && str.length()>=11){
           String mobilephone= str.substring(0, 3)+" "+str.substring(3, 7)+" "+str.substring(7, str.length());
            phoneEt.setText(mobilephone);
            findAddress();
        }else{
            Toast.makeText(context,"用户电话信息不正确",Toast.LENGTH_SHORT).show();
        }


        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);//取消注册
    }

    private void initListening() {
        phoneEt.addTextChangedListener(new TextWatcher() {

            private int ChangeBefore;

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                ChangeBefore = s.length();

            }

            @SuppressLint("NewApi")
            @Override
            public void afterTextChanged(Editable s) {
                int ChangeAfter = s.length();
                if (s.length() == 1) {
                    charge_phone_clear.setVisibility(View.VISIBLE);

                }

                int length = phoneEt.getText().toString().length();
                if ((length == 3 || length == 8) && ChangeBefore < ChangeAfter) {
                    phoneEt.setText(phoneEt.getText() + " ");
                    phoneEt.setSelection(phoneEt.getText().toString().length());
                }

                while (s.length() == 13) {
                    if (!Util.isPhone(phoneEt.getText().toString()
                            .replaceAll(" ", ""))) {
                        Util.show("手机号码格式错误！", context);
                        return;
                    }
                    findAddress();
                    break;
                }

            }

        });




    }

    @Subscribe
    public void onMessage(MessageEvent event) {

        phoneEt.setText(event.message);
    }

    @OnClick({R.id.phone_charge_dhb,R.id.charge_phone_clear,R.id.phone_charge_num,R.id.phone_charge_money50,R.id.phone_charge_money100,R.id.phone_charge_money300})

    public void click(View view){
        switch (view.getId()){
            case R.id.phone_charge_dhb:
                Intent intent=new Intent(Intent.ACTION_PICK,
                        ContactsContract.Contacts.CONTENT_URI);

                ((PhoneFream)context).startActivityForResult(intent, 0);
                break;
            case  R.id.charge_phone_clear:
                charge_phone_clear.setVisibility(View.GONE);
                phoneEt.setText("");
                show_phone_charge_addr.setVisibility(View.GONE);
                // phone_charge_quxiao.setVisibility(View.GONE);

                checkmyphone.setVisibility(View.GONE);
                break;
            case  R.id.phone_charge_num:
                if (isFirstClick == 1) {
                    getLatestNum();

                    isFirstClick = 2;

                }
                break;

            case R.id.phone_charge_money50:
                reset();
                tv50.setTextColor(Color.parseColor("#ffffff"));
                tv49.setTextColor(Color.parseColor("#ffffff"));
                phone_charge_money50.setBackgroundResource(R.drawable.phone_charge_shape_ok_check2);
                money = "50";
                chargephone();
                break;
            case R.id.phone_charge_money100:
                reset();
                tv100.setTextColor(Color.parseColor("#ffffff"));
                tv99.setTextColor(Color.parseColor("#ffffff"));
                phone_charge_money100.setBackgroundResource(R.drawable.phone_charge_shape_ok_check2);
                money = "100";
                chargephone();
                break;
            case R.id.phone_charge_money300:
                reset();
                tv300.setTextColor(Color.parseColor("#ffffff"));
                tv299.setTextColor(Color.parseColor("#ffffff"));
                phone_charge_money300.setBackgroundResource(R.drawable.phone_charge_shape_ok_check2);
                money = "300";
                chargephone();
                break;
        }

    }


    // 根据号码 获取归属地
    private void findAddress() {

        try{
            String phone = phoneEt.getText().toString().replaceAll(" ", "");

            if (phone.equals(UserData.getUser().getMobilePhone())) {
                checkmyphone.setVisibility(View.VISIBLE);
            }else{
                checkmyphone.setVisibility(View.GONE);
            }

        }catch(Exception e){
            Log.e("充值界面异常","用户为登录"+e.toString());
        }

        Util.asynTask(context, "正在获取您的运营商...", new AsynTask() {
            @Override
            public Serializable run() {
                Web web = new Web(Web.getPhoneCityForInterface, "phone="
                        + phoneEt.getText().toString().replaceAll(" ", ""));
                return web.getPlan();
            }

            @SuppressLint("NewApi")
            @Override
            public void updateUI(Serializable result) {
                super.updateUI(result);
                if (Util.isNull(result)) {

                    Util.show("无法查到对应号段,请重新输入！", context);
                    show_phone_charge_addr.setVisibility(View.GONE);

                    return;
                }
                if (null != result) {
                    JSONObject json = JSON.parseObject(result.toString());
                    JSONObject root = json.getJSONObject("root");
                    if (!Util.isNull(root)) {

                        show_phone_charge_addr.setVisibility(View.VISIBLE);

                        phone_charge_addr = root.getString("chgmobile") + "|"
                                + root.getString("province") + "|"
                                + root.getString("city") + "|"
                                + root.getString("supplier");

                        show_phone_charge_addr.setText(root
                                .getString("province")
                                + "|"
                                + root.getString("supplier"));

                    }
                }
            }
        });
    }


    private void getLatestNum() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("userId", UserData.getUser().getUserId());
        map.put("md5Pwd", UserData.getUser().getMd5Pwd());
        NewWebAPI.getNewInstance().getWebRequest(
                "/MobilePhone.aspx?call=getRechangList", map,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        super.success(result);
                        if (Util.isNull(result)) {
                            Util.show("网络错误，请重试！", context);
                            return;
                        }
                        JSONObject json = JSON.parseObject(result.toString());
                        if (200 != json.getIntValue("code")) {
                            Util.show(json.getString("message"),
                                    context);
                            return;
                        }
                        String arrlist = json.getString("list");
                        List<LatestPhone> list = JSON.parseArray(arrlist,
                                LatestPhone.class);

                        if ("[]".equals(list.toString())) {

                            return;
                        }
//						phone_charge_dhb.setVisibility(View.GONE);
                        phone_latest_list.setVisibility(View.VISIBLE);
                        // phone_charge_quxiao.setVisibility(View.VISIBLE);
                        BaseMallAdapter<LatestPhone> adapter = (BaseMallAdapter<LatestPhone>) phone_latest_list
                                .getAdapter();
                        if (null == adapter) {
                            adapter = new BaseMallAdapter<LatestPhone>(
                                    R.layout.latest_charge_phone_item,
                                    context, list) {

                                @Override
                                public View getView(final int position,
                                                    View convertView, ViewGroup parent,
                                                    LatestPhone t) {
                                    final TextView latest_number = (TextView) convertView
                                            .findViewById(R.id.latest_number);
                                    TextView latest_name = (TextView) convertView
                                            .findViewById(R.id.latest_name);
                                    ImageView latest_close = (ImageView) convertView
                                            .findViewById(R.id.latest_close);
                                    latest_number.setText(list.get(position).phone);
                                    if (Util.isNull(list.get(position).name))
                                        latest_name.setText("不再通讯录中");
                                    else
                                        latest_name.setText(list.get(position).name);
                                    latest_close
                                            .setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    list.remove(position);
                                                    updateUI();

                                                }
                                            });
                                    convertView
                                            .setOnClickListener(new View.OnClickListener() {

                                                @Override
                                                public void onClick(View v) {
                                                    String phonenum = latest_number
                                                            .getText()
                                                            .toString();
                                                    String phonenum2 = phonenum
                                                            .substring(0, 3)
                                                            + " "
                                                            + phonenum
                                                            .substring(
                                                                    3,
                                                                    7)
                                                            + " "
                                                            + phonenum
                                                            .substring(
                                                                    7,
                                                                    11);
                                                    phoneEt.setText(phonenum2);
                                                    phone_latest_list
                                                            .setVisibility(View.GONE);
                                                    phone_charge_dhb
                                                            .setVisibility(View.VISIBLE);
                                                    // phone_charge_quxiao.setVisibility(View.GONE);

                                                }
                                            });
                                    return convertView;
                                }
                            };
                            phone_latest_list.setAdapter(adapter);

                        } else {
                            adapter.add(list);
                            adapter.updateUI();
                        }
                    }

                });

    }

    public void chargephone(){
        if (Util.isNull(phoneEt.getText().toString())) {
            Util.show("请输入或选择手机号码", context);
            return;
        }
        if (phoneEt.getText().length() < 13) {
            Util.show("请输入正确的手机号", context);
            return;
        }
        if (Util.isNull(money)) {
            Util.show("请选择充值金额", context);
            return;
        }

        showOk();

    }

    private void showOk() {
        voipDialog = new VoipDialog("您正给" + phoneEt.getText().toString()
                + "手机号码充值", context, "确定", "重新修改",
                new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        if (UserData.getUser() == null) {
                            Util.showIntent("您还没有登录，现在去登录吗？", context,
                                    LoginFrame.class, new String[] { "phone",
                                            "frame" }, new String[] {
                                            phoneEt.getText().toString()
                                                    .replaceAll(" ", ""),
                                            "phone" });
                            return;
                        }

                        final Intent intent = new Intent();
                        intent.setClass(context, PhoneCommitFream.class);
                        intent.putExtra("phoneNum", phoneEt.getText()
                                .toString().replaceAll(" ", ""));
                        intent.putExtra("phoneAddr", phone_charge_addr);
                        intent.putExtra("phoneMoney", money + "");
                        intent.putExtra("unum", unum);
                        context.startActivity(intent);

                    }
                }, new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                voipDialog.cancel();
                voipDialog.dismiss();
            }
        });
        voipDialog.show();

    }

    private void reset(){
        tv50.setTextColor(Color.parseColor("#2498e2"));
        tv49.setTextColor(Color.parseColor("#2498e2"));
        tv100.setTextColor(Color.parseColor("#2498e2"));
        tv99.setTextColor(Color.parseColor("#2498e2"));
        tv300.setTextColor(Color.parseColor("#2498e2"));
        tv299.setTextColor(Color.parseColor("#2498e2"));
        phone_charge_money50.setBackgroundResource(R.drawable.phone_charge_shape_no_check);
        phone_charge_money100 .setBackgroundResource(R.drawable.phone_charge_shape_no_check);
        phone_charge_money300 .setBackgroundResource(R.drawable.phone_charge_shape_no_check);

    }


}
