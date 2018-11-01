package com.mall.Fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.lin.component.CustomProgressDialog;
import com.mall.BasicActivityFragment.BaseV4Fragment;
import com.mall.MessageEvent;
import com.mall.adapter.MyTestRecyclerViewAdapter;
import com.mall.model.RedPakageBean;
import com.mall.model.openRedBean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.OpenRedBean;
import com.mall.view.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by MECHREVO on 2017/11/1.
 */

public class ListFragment extends BaseV4Fragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;



    int page=0;


    private double lingqu=0;

    LinearLayoutManager myLinearmanager;

    MyTestRecyclerViewAdapter myTestRecyclerViewAdapter;

     CustomProgressDialog cpd;

    private List<RedPakageBean.ListBean> redpakagelist=new ArrayList<>();
    private List<RedPakageBean.ListBean> redpakagelistwei=new ArrayList<>();
    private List<RedPakageBean.ListBean> redpakagelistyi=new ArrayList<>();
    private List<RedPakageBean.ListBean> redpakagelistlingqu=new ArrayList<>();

    private static final String KEY = "key";
    private String state="0";



    public static ListFragment newInstance(String title) {
        ListFragment fragment = new ListFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY, title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMoonEvent(MessageEvent messageEvent){
        if (messageEvent.getRedPakageBean()==null){
            Log.e("EventBus11"+getClass(), "回调" + messageEvent.toString());
            openred(messageEvent.getMessage(), messageEvent.getProgress());
        }


    }

    //MainFragment.java文件中
    public void setData(String state) {
        this.state=state;
        getRedEnListInfo();
    }
    @Override
    public int getContentViewId() {
        return R.layout.fragment_list;
    }

    @Override
    public void initAllMembersView(Bundle savedInstanceState) {
        //注册事件
        EventBus.getDefault().register(this);

        Bundle arguments = getArguments();
        if (arguments != null) {
            state = arguments.getString(KEY);
        }

        myLinearmanager=new LinearLayoutManager(context,LinearLayoutManager.VERTICAL, false);
        myTestRecyclerViewAdapter=new MyTestRecyclerViewAdapter(redpakagelist);
        recyclerView.setLayoutManager(myLinearmanager);
        recyclerView.setAdapter(myTestRecyclerViewAdapter);
        getRedEnListInfo();
    }

    @Override
    public void onDes() {
        EventBus.getDefault().unregister(this);
        if (cpd!=null){
            try{
                cpd.cancel();
                cpd.dismiss();
            }catch (Exception e) {
                System.out.println("myDialog取消，失败！");
                // TODO: handle exception
            }
        }
    }

    private void getRedEnListInfo(){
        if (cpd!=null){
            cpd=null;
        }
        cpd = Util.showProgress("正在获取红包盒,请稍等...", context);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=GetRedBox&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        +"&Page="+"1"+"&pageSize="+999+"&state="+state
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

                        Gson gson =new Gson();
                        RedPakageBean redPakageBean= gson.fromJson(result.toString(),RedPakageBean.class);

                        redpakagelist.clear();
                        redpakagelist.addAll(redPakageBean.getList());
                        redpakagelistwei.clear();
                        redpakagelistyi.clear();
                        redpakagelistlingqu.clear();
                        for (int i=0;i<redpakagelist.size();i++){
                            RedPakageBean.ListBean listBean=redpakagelist.get(i);
                            String state=listBean.getState()+""; //0未开启 1已开启 2已领取
                            if (state.equals("0")){
                                redpakagelistwei.add(listBean);
                            }else if(state.equals("1")){
                                redpakagelistyi.add(listBean);
                            }else if(state.equals("2")){
                                redpakagelistlingqu.add(listBean);


                                BigDecimal bigDecimal1 = new BigDecimal(Double.toString(lingqu));
                                BigDecimal bigDecimal2 = new BigDecimal(listBean.getMoney());
                                System.out.println(bigDecimal1.add(bigDecimal2));//精确的输出

                                lingqu=bigDecimal1.add(bigDecimal2).doubleValue();
                            }
                        }



                        if (state.equals("0")){

                            myTestRecyclerViewAdapter.setData(redpakagelistwei);
                            EventBus.getDefault().post(new MessageEvent(redPakageBean,state));
                        }

                        else if (state.equals("1")){
                            myTestRecyclerViewAdapter.setData(redpakagelistyi);
                            EventBus.getDefault().post(new MessageEvent(redPakageBean,state));

                            page=1;


                        }else
                        if(state.equals("2"))
                        {
                            EventBus.getDefault().post(new MessageEvent(redPakageBean,state));
                            myTestRecyclerViewAdapter.setData(redpakagelistlingqu);
                        }



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


    private void  openred(String redboxid, final int position){
        cpd = Util.showProgress("正在领取红包盒,请稍等...", context);
        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=OpenRedBox&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        +"&redboxid="+redboxid
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

                        Log.e("领取红包",result.toString()+"LLL");

                        Toast.makeText(context,"恭喜你领取成功",Toast.LENGTH_SHORT).show();

                        Gson gson=new Gson();
                        openRedBean openRedBean1=gson.fromJson(result.toString(),openRedBean.class);

                        if (cpd!=null){
                            cpd.cancel();
                            cpd.dismiss();
                        }

                        OpenRedBean openRedBean=new OpenRedBean(context,openRedBean1.getCashCoupon(),openRedBean1.getConsumption());

                        openRedBean.show();

                        WindowManager.LayoutParams lp = openRedBean.getWindow().getAttributes();
                        lp.width = Util.getScreenWidth();
                        openRedBean.getWindow().setAttributes(lp);

                        getRedEnListInfo();
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
