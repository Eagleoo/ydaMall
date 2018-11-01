package com.mall.view.RedEnvelopesPackage;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.view.RedDetailDialog;
import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.SpeedUpDetailBean;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.CharacterParser;
import com.mall.util.MyTime;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.MoreTextView;
import com.mall.view.R;
import com.mall.view.SelectorFactory;
import com.pickerview.view.TimePickerView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@ContentView(R.layout.activity_person_team_detail)
public class PersonTeamDetailActivity extends AppCompatActivity {
    private Context context;

    @ViewInject(R.id.detaillist)
    private ListView mylist;

    @ViewInject(R.id.handertitle)
    private MoreTextView handertitle;

    @ViewInject(R.id.endtime)
    private MoreTextView endtime;

    @ViewInject(R.id.startime)
    private MoreTextView startime;

    @ViewInject(R.id.selecttime)
    private  View selecttime;

    @ViewInject(R.id.tv1)
    private TextView tv1;
    @ViewInject(R.id.tv2)
    private TextView tv2;
    @ViewInject(R.id.tv3)
    private TextView tv3;
    @ViewInject(R.id.tv4)
    private TextView tv4;



    @ViewInject(R.id.allnumber)
    private TextView allnumber;



    @ViewInject(R.id.jiashutime)
    private TextView jiashutime;


    @ViewInject(R.id.allnumber2)
    private TextView allnumber2;

    @ViewInject(R.id.jiashutime2)
    private TextView jiashutime2;

    @ViewInject(R.id.search_push_user_edit)
    private EditText search_push_user_edit;

    @ViewInject(R.id.sidrbar)
    private com.mall.view.SideBars sideBar;

    @ViewInject(R.id.letter_dialog)
    private TextView letterDialog;

    @ViewInject(R.id.input_rl)
    private View rl1;

    @ViewInject(R.id.chaxun)
    private TextView chaxun;




    @ViewInject(R.id.lin2)
    private View lin2;



    @ViewInject(R.id.input_rl)
    private  View input_rl;
    @ViewInject(R.id.client_sort)
    private TextView client_sort;

    @ViewInject(R.id.center_header)
    private TextView center_header;

    @ViewInject(R.id.lin1)
            private  View lin1;

    @ViewInject(R.id.bow)
            private View bow;

    MyAdapter myAdapter;

    TimePickerView pvTime;

    String title="";


    String strStartime="";
    String strEndtime="";



    int timepaixun=0;
    int namepaixun=0;
    int moneypaixun=0;

    String soskey="";

    private boolean issingtime=false;
    private boolean isToday=true;



    List<SpeedUpDetailBean.ListBean> listallglobal=new ArrayList<>();// 全集保存 请求数据 得到后不直接对其做任何操作

    List<SpeedUpDetailBean.ListBean> listtime=new ArrayList<>(); //时间排序

    List<SpeedUpDetailBean.ListBean> listmoney=new ArrayList<>(); //金钱排序

    List<SpeedUpDetailBean.ListBean> listall=new ArrayList<>(); //姓名排序

    List<SpeedUpDetailBean.ListBean> searchlist=new ArrayList<>(); //搜索排序




    private String start_end="0";  // 0 开始 1 结束

    private String call="";

    private String setuserid="";

    private String TAG1="部成员列表";
    private String TAG2="个人业绩明细";

    public static String PERSONTAG="个人业绩";

    /**
     * 汉字转换成拼音的类
     */
    private CharacterParser characterParser;
    private PinyinComparator pinyinComparator;

    String income;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ViewUtils.inject(this);
        Intent intent=getIntent();


        String strStart=intent.getStringExtra("strStartime");
        String strEnd=intent.getStringExtra("strEndtime");
        income=intent.getStringExtra("income");



        if (!Util.isNull(strStart)){
            strStartime=strStart;
        }

        if (!Util.isNull(strEnd)){
            strEndtime=strEnd;
        }




        pvTime = new TimePickerView(this, TimePickerView.Type.YEAR_MONTH_DAY);
        pvTime.setTime(new Date());
        pvTime.setCyclic(false);
        pvTime.setCancelable(true);

        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {

            @Override
            public void onTimeSelect(Date date) {

                String time=getTime(date);
                Log.e("选择的时间",getTime(date)+"l");

                if (start_end.equals("0")){
                    startime.setText(time);
                    strStartime=time;
                }else if(start_end.equals("1")){
                    endtime.setText(time);
                    strEndtime=time;
                }

            }
        });

        String str=intent.getStringExtra("title");
        String str1=intent.getStringExtra("setuserid");
        if (!Util.isNull(str)){
            title=str;
        }

        if (!Util.isNull(str1)){
            setuserid=str1;
        }

        if(title.equals("部门业绩")){
            long time=System.currentTimeMillis();
            Date d1=new Date(time);
            strStartime=getTime(d1);
            startime.setText(getTime(d1));
            strEndtime=getTime(d1);
            endtime.setText(getTime(d1));
            lin2.setVisibility(View.VISIBLE);

        }

        init();
    }

    private void init() {
        initview();
        initdata();
        initSide();
    }
    private void initSide() {
        sideBar.setVisibility(View.GONE);
        // TODO Auto-generated method stub
        sideBar.setOnTouchingLetterChangedListener(new com.mall.view.SideBars.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                // TODO Auto-generated method stub
                int position = myAdapter.getPositionForSection(s.charAt(0));
                if (position != -1) {

                    // shopadapter = (ShopUser) member_list.getAdapter();
                    if (null == myAdapter) {
                        return;
                    }
                    View item = myAdapter.getView(0, null, mylist);
                    item.measure(0, 0);
                    int ih = item.getMeasuredHeight();
//                    if (myAdapter.getCount() > mylist.getMeasuredHeight() / ih) {
//                        myAdapter.setState("gone", position);
//                        myAdapter.notifyDataSetChanged();
//                    }

                    mylist.setSelection(position);

                }
            }
        });
        sideBar.setTextView(letterDialog);
    }


    private void initview() {

        input_rl.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#ffffff"))
                .setStrokeWidth(Util.dpToPx(context,1))
                .setCornerRadius(Util.dpToPx(context,3))
                .create());

        chaxun.setBackground(SelectorFactory.newShapeSelector()
                .setDefaultBgColor(Color.parseColor("#FF2146"))
                .setStrokeWidth(Util.dpToPx(context,1))
                .setCornerRadius(Util.dpToPx(context,3))
                .create());

        search_push_user_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (Util.isNull(search_push_user_edit.getText().toString().trim())) {

                    if (title.equals("部门业绩")||title.indexOf(TAG1)!=-1){
                        myAdapter.setData(listtime,0);
                    }

                }
            }
        });


        pinyinComparator = new PinyinComparator();
        characterParser = CharacterParser.getInstance();

        if (title.lastIndexOf("K")==title.length()-1){
            handertitle.setText(title.replace("K","")+"个人");
        }else  if(title.indexOf(TAG2)!=-1){
            handertitle.setText("");
        }else{
            Log.e("title",title+"LLL");
            handertitle.setText(title);
        }


        if (title.indexOf(TAG2)!=-1){

            soskey=getIntent().getStringExtra("soskey");
            center_header.setVisibility(View.VISIBLE);
            input_rl.setVisibility(View.GONE);
            client_sort.setVisibility(View.GONE);
            center_header.setText(title.replace(TAG2,"")+"业绩");
//            call="GetPersonalperformance";
            call="getRedbeanAccount";
            tv2.setText("红包豆金额");
            tv3.setText("时间");
            selecttime.setVisibility(View.GONE);
            handertitle.setText("");
            lin1.setVisibility(View.GONE);




        }else

        if (title.equals("个人业绩")){
            selecttime.setVisibility(View.GONE);
            call="GetPersonalperformance";
            sideBar.setVisibility(View.VISIBLE);
            tv2.setText("系统身份");
            tv3.setText("红包豆总额");

        }else  if(title.equals("部门业绩")){
            selecttime.setVisibility(View.VISIBLE);
            rl1.setVisibility(View.GONE);
            client_sort.setVisibility(View.GONE);
            call="GetDivisionalPerformance";
            tv4.setVisibility(View.VISIBLE);
            tv1.setText("部门名称");
            tv2.setText("新增天数");
            tv3.setText("可加速");
            tv4.setText("保留天数");
//            getTeamResults("","");
        }else if(title.equals("我的团队")){
            allnumber.setText("团队总业绩");
            jiashutime.setVisibility(View.GONE);
            allnumber2.setVisibility(View.GONE);
            call="GetDRMenber";
            tv2.setText("充值金额");
            sideBar.setVisibility(View.VISIBLE);
        }else if(title.indexOf(TAG1)!=-1){
            bow.setVisibility(View.GONE);
            tv3.setText("充值时间");
            selecttime.setVisibility(View.GONE);
            call="GetDivisionalPerformance";

        }else  if (title.indexOf("K")!=-1){
            selecttime.setVisibility(View.GONE);
            call="GetPersonalperformance";
            sideBar.setVisibility(View.VISIBLE);
        }

        myAdapter=new MyAdapter(context,listall,0);
        mylist.setAdapter(myAdapter);

        initlisten();
    }

    private void initlisten() {

        mylist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


//                SpeedUpDetailBean.ListBean listBean=listall.get(i);
                SpeedUpDetailBean.ListBean listBean=myAdapter.getList().get(i);
                if (title.indexOf(TAG2)!=-1){
                    RedDetailDialog dialog = new RedDetailDialog(context);
                    dialog.setUserName(listBean.getUserId()).setPhone(listBean.getPhone()).setMoney(listBean.getJuese())
                            .setRealname(listBean.getName()).settv3("系统角色:").settv4("红包豆总额:");

                    try {
                        dialog.setTime(income.split("\\.")[0]);
                    }catch (Exception e){

                    }
                }else

                    //跳转逻辑更换
                    if (title.equals("部门业绩")){
//                    Intent intent= new Intent(context,PersonTeamDetailActivity.class);
//                    intent.putExtra("title",listBean.getName()+TAG1);
//                    intent.putExtra("setuserid",listBean.getBm());
//                    intent.putExtra("strStartime",strStartime);
//                    intent.putExtra("strEndtime",strEndtime);
//                    startActivity(intent);
                    }else if(title.equals("个人业绩")){

                        Intent intent= new Intent(context,PersonTeamDetailActivity.class);
                        intent.putExtra("title",listBean.getName()+TAG2);
                        intent.putExtra("soskey",listBean.getUserId());
                        intent.putExtra("setuserid",listBean.getBm());
                        intent.putExtra("income",listBean.getIncome());//income
                        startActivity(intent);




                    }else  if(title.equals("我的团队")){
                        RedDetailDialog dialog = new RedDetailDialog(context);
                        dialog.setUserName(listBean.getUserId()).setPhone(listBean.getMobilePhone()).setMoney(listBean.getJuese())
                                .setTime(listBean.getDate()).setRealname(listBean.getName()).settv3("系统角色:");
                    }
                    else if(title.indexOf(TAG1)!=-1){


                        RedDetailDialog dialog = new RedDetailDialog(context);


                        dialog.setUserName(listBean.getCy_num()).setPhone(listBean.getMobilephone()).setMoney(listBean.getJuese())
                                .setRealname(listBean.getName()).settv3("系统角色:").settv4("红包豆总额:");

                        try {
                            dialog.setTime(listBean.getRedboxmoney());
                        }catch (Exception e){

                        }



                    }

                    else{
                        RedDetailDialog dialog = new RedDetailDialog(context);
                        dialog.setUserName(listBean.getUserId()).setPhone(listBean.getPhone()).setMoney(listBean.getJuese())
                                .setTime(listBean.getDate()).setRealname(listBean.getName()).settv3("系统角色:");
                    }

            }
        });

    }

    private void initdata() {



        GetSpeed("1","999","","","",call);

    }

    /**
     *
     * @param page
     * @param pageSize
     * @param enumType  类型
     * @param starTime  时间段查询开始
     * @param endTime   时间段查询结束
     */
    private void GetSpeed(String page,String pageSize,String enumType,String
            starTime, String endTime,String url
    ){

        if (strStartime.trim().equals(strEndtime.trim())){
            Log.e("不是时间段","YES");
            issingtime=true;
        }else{
            issingtime=false;
        }





        MyTime myTime=Util.getshengyutime(strStartime.trim(),strEndtime.trim(),new SimpleDateFormat("yyyy-MM-dd"));

        Log.e("时间比较","myTime:"+myTime.getDay());
        if (myTime.getDay()>0){
            Util.show("结束时间不能低于开始时间",context);
            return;
        }


        long time=System.currentTimeMillis();
        Date d1=new Date(time);
        String today=getTime(d1).trim();

        MyTime myTime1=Util.getshengyutime(today,strEndtime.trim(),new SimpleDateFormat("yyyy-MM-dd"));
        Log.e("时间比较","myTime1:"+myTime1.getDay());
        if (myTime1.getDay()<0){
            Util.show("结束时间不能大于今天",context);
            return;
        }



        Log.e("时间截取","strStartime"+strStartime.trim()+"strEndtime"+strEndtime.trim()+"today"+today+"LLL");

        if (today.equals(strEndtime.trim())){
            Log.e("结束时间","是今天");
            isToday=true;

        }else{
            Log.e("结束时间","不是今天");
            isToday=false;
        }


//        MyTime myTime1=Util.getshengyutime(strStartime.trim(),strEndtime.trim(),new SimpleDateFormat("yyyy-MM-dd"));

//        String host="/red_box.aspx";
        String host="/red_box_v2.aspx";
//        if (title.equals(PERSONTAG)){
//            host="/red_box_v2.aspx";
////            title.replace(TAG2,
//        }else
// if(title.indexOf(TAG2)!=-1){
//            host="/red_box.aspx";
//        }

        final CustomProgressDialog cpd = Util.showProgress("正在加载...", this);
        NewWebAPI.getNewInstance().getWebRequest(host+"?call="+url+"&userId="
                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
                        +"&Page="+page+"&pageSize="+pageSize+"&enumType="+enumType+"&starTime="+strStartime.trim()
                        +"&endTime="+strEndtime.trim()+"&setuserid="+setuserid+"&soskey="+soskey
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

                        Gson gson=new Gson();

                        SpeedUpDetailBean bean=gson.fromJson(result.toString(),SpeedUpDetailBean.class);

                        Log.e("异常1","异常aaa");

                        List<SpeedUpDetailBean.ListBean> list =bean.getList();
                        Log.e("异常2","异常aaa");
                        listallglobal.clear();
                        listtime.clear();
                        listmoney.clear();  //金钱排序
                        listall.clear();
                        Log.e("异常3","异常aaa");

                        try {

                            if (title.equals("部门业绩")||title.indexOf(TAG1)!=-1){
                                Log.e("Exception","sda1");
//                                allnumber.setText("总金额："+bean.getZJE());
                                allnumber.setText("新增天数："+bean.getCount_1()+"天");
                                Log.e("Exception","sda2");
                                if (issingtime){
                                    bow.setVisibility(View.VISIBLE);
                                    if (isToday){
                                        Log.e("Exception","sda3");
//                                    jiashutime.setText("加速金额："+bean.getCAN_JS_MONEY());
                                        jiashutime.setText("可加速天数："+bean.getCount_3()+"天");
                                        Log.e("Exception","sda4");
//                                    allnumber2.setText("加速天数："+bean.getCAN_JS_DAY()+"天");
                                        allnumber2.setText("可使用天数："+bean.getCount_2()+"天");
                                        jiashutime2.setText("需等待天数："+bean.getCount_4()+"天");
                                    }else{
                                        Log.e("Exception","sda3");
//                                    jiashutime.setText("加速金额："+bean.getCAN_JS_MONEY());
                                        jiashutime.setText("可加速天数："+bean.getCount_3()+"天");
                                        Log.e("Exception","sda4");
//                                    allnumber2.setText("加速天数："+bean.getCAN_JS_DAY()+"天");
                                        allnumber2.setText("使用天数："+bean.getCount_2()+"天");
                                        jiashutime2.setText("等待天数："+bean.getCount_4()+"天");
                                    }

                                }else {
                                    bow.setVisibility(View.GONE);
                                }

                                Log.e("Exception","sda5");
//                                jiashutime2.setText("累计加速天数："+bean.getCOUNT_DAY_NUM()+"天");

                            }else{
                                Log.e("Exception","sda6"+bean.getAddUpFriend());
                                allnumber.setText("总金额："+bean.getAllAchievement()+"元");
                                allnumber2.setText("加速天数："+bean.getAddUpFriend()+"天");
                                if (title.equals("我的团队")){
                                    allnumber.setText("团队总金额："+bean.getAllAchievement());
                                    allnumber2.setVisibility(View.GONE);
                                }
                            }


                            Log.e("bean.getAllusercount()",bean.getAllusercount()+"KK");
                            if (title.equals("部门业绩")){
                                handertitle.setText("部门"+"("+list.size()+")");
                            }else  if(title.indexOf(TAG2)!=-1){
                                handertitle.setText("");
                            }else {
                                handertitle.setText(title+"("+bean.getAllusercount()+")");
                            }

                            if (!Util.isNull(bean.getAllusercount())){
                                if (title.lastIndexOf("K")==title.length()-1){
                                    handertitle.setText(title.replace("K","")+"个人"+"("+bean.getAllusercount()+")");
                                }
                                else if(title.indexOf(TAG2)!=-1){
                                    handertitle.setText("");
                                }
                                else{
                                    if (title.equals("部门业绩")){
                                        handertitle.setText("部门"+"("+list.size()+")");
                                    }else {
                                        handertitle.setText(title+"("+bean.getAllusercount()+")");
                                    }

                                }

                            }


                        }catch (Exception e){
                            Log.e("Exception","sda"+e.toString());
                        }


                        if (list==null||list.size()==0){
                            myAdapter.notifyDataSetChanged();
                            return;
                        }
                        Log.e("异常4","异常aaa");
                        listallglobal.addAll(list);

                        Log.e("异常5","异常aaa");
//                        listtime.addAll(list);  //时间排序

                        try {
                            listtime=Util.deepCopy(list);
                        } catch (IOException e) {
                            Log.e("IOException1111",e.toString());
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {

                            Log.e("ClassNotFoundException1",e.toString());

                            e.printStackTrace();
                        }


                        Log.e("listtime1",listtime.get(0).getSortLetter()+"LL");


                        Log.e("listtime11",listtime.get(0).getSortLetter()+"LL");
//                        listmoney.addAll(list);
                        try {
                            listmoney=Util.deepCopy(list);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        Collections.sort(listmoney, new MoneyComparator(title));
                        Log.e("listtime12",listtime.get(0).getSortLetter()+"LL");
                        Collections.reverse(listmoney);
                        Log.e("listtime13",listtime.get(0).getSortLetter()+"LL");

                        Log.e("listtime14",listtime.get(0).getSortLetter()+"LL");
                        listall.addAll(filledData(list));
                        Log.e("listtime15",listtime.get(0).getSortLetter()+"LL");
                        Collections.sort(listall, pinyinComparator);
                        Log.e("listtime16",listtime.get(0).getSortLetter()+"LL");
                        Log.e("listtime2",listtime.get(0).getSortLetter()+"LL");
                        myAdapter.setData(listtime,0);
//                        myAdapter.notifyDataSetChanged();



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

    @OnClick({R.id.startime,R.id.endtime,R.id.chaxun,R.id.handertitle,R.id.search_push_user
            ,R.id.client_sort
    })
    private void  click(View view){
        switch (view.getId()){
            case R.id.handertitle:
                finish();
                break;
            case R.id.startime:
                start_end="0";
                pvTime.show();
                break;
            case R.id.endtime:
                start_end="1";
                pvTime.show();
                break;
            case R.id.chaxun:
//                getTeamResults(startime.getText().toString().trim(),endtime.getText().toString().trim());
                GetSpeed("1","999","",startime.getText().toString().trim(),endtime.getText().toString().trim(),call);
                break;
            case R.id.search_push_user:
                String str=search_push_user_edit.getText().toString();
                if (Util.isNull(str)){
                    Toast.makeText(context,"请输入要查询的内容",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (title.lastIndexOf("K")==title.length()-1){ //查询他人业绩明细
                    soskey=str;
                    setuserid=title.replace("K","");
                }else{
                    soskey=str;
                }



                if(title.equals("部门业绩")){

                    searchlist.clear();
                    if(listall!=null&&listall.size()>0){

                        for (int i = 0; i < listall.size(); i++) {
                            if (listall.get(i).getCy_num()
                                    .contains(soskey)
                                    || listall.get(i).getIncome()
                                    .contains(soskey)
                                    || listall.get(i).getBm()
                                    .contains(soskey)) {
                                searchlist.add(listall.get(i));
                            }


                        }
                        myAdapter.setData(searchlist,0);
                    }

                }else  if(title.indexOf(TAG1)!=-1){
                    searchlist.clear();
                    if(listall!=null&&listall.size()>0){

                        for (int i = 0; i < listall.size(); i++) {
                            if (listall.get(i).getCy_num()
                                    .contains(soskey)
                                    || listall.get(i).getRed_r()
                                    .contains(soskey)
                                    || listall.get(i).getBm()
                                    .contains(soskey)) {
                                searchlist.add(listall.get(i));
                            }


                        }
                        myAdapter.setData(searchlist,0);

                    }
                }

                else{
                    GetSpeed("1","999","","","",call);
                }

                break;
            case R.id.client_sort:
                sortDialog();
                break;
        }
    }

    public  void  initpaixunstate(String  paixun){
        timepaixun=0;
        namepaixun=0;
        moneypaixun=0;

        if (paixun.equals("timepaixun")){
            timepaixun++;

        }else if(paixun.equals("namepaixun")){
            namepaixun++;
        }else if(paixun.equals("moneypaixun")){
            moneypaixun++;
        }



    }

    class MyAdapter extends BaseAdapter{

        private Context context;
        private List<SpeedUpDetailBean.ListBean> list;

        private int state=0;



        public MyAdapter(Context context , List<SpeedUpDetailBean.ListBean> list,int state){

            this.context=context;
            this.list=list;
            this.state=state;

        }

        public List<SpeedUpDetailBean.ListBean> getList(){
            if (list==null){
                return list=new ArrayList<>();
            }
            return list;
        }

        public  void  setData(List<SpeedUpDetailBean.ListBean> list,int state){
            this.list=list;
            this.state=state;
            notifyDataSetChanged();
        }

        public int getPositionForSection(int section) {
            for (int i = 0; i < getCount(); i++) {
                String sortStr = list.get(i).getSortLetter();
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            return -1;

        }

        @Override
        public int getCount() {
            if (list==null){
                return 0;
            }

            return list.size();
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
        public View getView(int position, View view, ViewGroup parent) {

            view= LayoutInflater.from(context).inflate(R.layout.detailitem,parent,false);
            TextView name= (TextView) view.findViewById(R.id.name);
            TextView money= (TextView) view.findViewById(R.id.money);
            TextView time= (TextView) view.findViewById(R.id.time);
            TextView time1= (TextView) view.findViewById(R.id.time1);
            TextView item_shopuser_tvletter= (TextView) view.findViewById(R.id.item_shopuser_tvletter);




            SpeedUpDetailBean.ListBean beanbefor=list.get(position);
            if (state==0){
                item_shopuser_tvletter.setVisibility(View.GONE);
            }else{
                int section = beanbefor.getSortLetter().charAt(0);


                if (position == getPositionForSection(section)) {

                    item_shopuser_tvletter.setVisibility(View.VISIBLE);

                    item_shopuser_tvletter.setText(beanbefor.getSortLetter());

                } else {
                    item_shopuser_tvletter.setVisibility(View.GONE);
                }
            }



            if (title.equals("部门业绩")){
                if (!Util.isNull(beanbefor.getName())){
                    name.setText(beanbefor.getName()+"部");
                }else {
                    name.setText(beanbefor.getBm()+"部");
                }
                DecimalFormat fnum  =   new  DecimalFormat("##0.0");

                float    xzts=Float.parseFloat(beanbefor.getXZTS());
                float    canjs=Float.parseFloat(beanbefor.getCAN_JS_DAY());
                float    bmjs=Float.parseFloat(beanbefor.getBM_JS_DAY());


                try {
                    money.setText(fnum.format(xzts)+"");
                    time.setText(fnum.format(canjs)+"");
                    time1.setText(fnum.format(bmjs)+"");
                }catch (Exception e){

                }


                time1.setVisibility(View.VISIBLE);



            }else if(title.indexOf(TAG1)!=-1){

                if (!Util.isNull(beanbefor.getName())){
                    name.setText(beanbefor.getName());
                }else{
                    name.setText(beanbefor.getCy_num());
                }
                money.setText(beanbefor.getRed_r());
                time.setText(beanbefor.getCzdate());
            }
            else if(title.equals("我的团队")){
                name.setText(beanbefor.getUserId());
                money.setText(beanbefor.getRED_R());
                time.setText(beanbefor.getDate());
            }else if(title.equals("个人业绩")){
                name.setText(beanbefor.getName());
                money.setText(beanbefor.getJuese());
                time.setText(beanbefor.getIncome());
            }else if (title.indexOf(TAG2)!=-1){
                name.setText(beanbefor.getName());
                money.setText(beanbefor.getIncome());
                time.setText(beanbefor.getDate());
            }

            else{
                name.setText(beanbefor.getUserId());
                money.setText(beanbefor.getIncome());
                time.setText(beanbefor.getDate());
            }



            return view;
        }
    }

    //    public void getTeamResults(String starTime,String endTime) {
//        NewWebAPI.getNewInstance().getWebRequest("/red_box.aspx?call=GetDivisionalPerformance&userId="
//                        + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
//
//                +"&Page="+"1"+"&pageSize="+"999"+"&starTime="+starTime+"&endTime="+endTime
//                ,
//                new NewWebAPIRequestCallback() {
//
//                    @Override
//                    public void timeout() {
//                        Util.show("网络超时！", context);
//                        return;
//                    }
//
//                    @Override
//                    public void success(Object result) {
//
//                        if (Util.isNull(result)) {
//                            Util.show("网络异常，请重试！", context);
//                            return;
//                        }
//                        JSONObject json = JSON.parseObject(result.toString());
//                        if (200 != json.getIntValue("code")) {
//                            Util.show(json.getString("message"), context);
//                            return;
//                        }
//
//
//
//
//                    }
//
//                    @Override
//                    public void requestEnd() {
//
//                    }
//
//                    @Override
//                    public void fail(Throwable e) {
//
//                    }
//                });
//
//
//    }
    public static String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd ");
        return format.format(date);
    }


    @SuppressLint("DefaultLocale")
    private List<SpeedUpDetailBean.ListBean> filledData(List<SpeedUpDetailBean.ListBean> list) {
        List<SpeedUpDetailBean.ListBean> mSortList = new ArrayList<SpeedUpDetailBean.ListBean>();

        for (int i = 0; i < list.size(); i++) {

            SpeedUpDetailBean. ListBean listBean=list.get(i);


            Log.e("listBean.getUserId",listBean.getUserId()+"KK");
            String name ="";
            if (title.indexOf(TAG2)!=-1){
                name=listBean.getUserId();
            }else {
                name= listBean.getName().trim();
            }
            Log.e("listBean.name",name+"KK");

            if (!Util.isNull(name)) {

                // 汉字转换成拼音
                String pinyin = characterParser.getSelling(name);

                String sortString = pinyin.substring(0, 1).toUpperCase();

                // 正则表达式，判断首字母是否是英文字母
                if (sortString.matches("[A-Z]")) {
                    listBean.setSortLetter(sortString.toUpperCase());
                } else {
                    listBean.setSortLetter("#");
                }

            } else {
                listBean.setSortLetter("#");
            }

            mSortList.add(listBean);

        }
        return mSortList;

    }

    /**
     *   按照金额升序
     */

    public class MoneyComparator implements Comparator<SpeedUpDetailBean.ListBean> {

        private String title="";

        public MoneyComparator(String title){
            this.title=title;
        }

        public int compare(SpeedUpDetailBean.ListBean o1, SpeedUpDetailBean.ListBean o2) {

//            String a= Integer.parseInt(o1.getIncome())+"";
//            String b= Integer.parseInt(o2.getIncome())+"";
//            return o1.getIncome().compareTo(o2.getIncome());

            double a=0;
            double b=0;
            if (title.equals("我的团队")){

                if (!Util.isNull(o1.getAllAchievement())){
                    a=Double.parseDouble(o1.getRED_R());
                }

                if (!Util.isNull(o2.getAllAchievement())){
                    b= Double.parseDouble(o2.getRED_R());
                }

            }else{
                a= Double.parseDouble(o1.getIncome());
                b= Double.parseDouble(o2.getIncome());
            }



            if(a>b){
                return 1;
            }else if(b>a){
                return -1;
            }

            return 0;

        }
    }

    public class PinyinComparator implements Comparator<SpeedUpDetailBean.ListBean> {
        public int compare(SpeedUpDetailBean.ListBean o1, SpeedUpDetailBean.ListBean o2) {
            if (o1.getSortLetter().equals("@")
                    || o2.getSortLetter().equals("#")) {
                return -1;
            } else if (o1.getSortLetter().equals("#")
                    || o2.getSortLetter().equals("@")) {
                return 1;
            } else {
                return o1.getSortLetter().compareTo(o2.getSortLetter());
            }
        }
    }

    public void sortDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_client_sort,
                null);
        TextView sortDate = (TextView) view
                .findViewById(R.id.dialog_client_sort_date);

//        if (timepaixun%2==0){
//            sortDate.setText("消费时间(降序)");
//        }else{
//            sortDate.setText("消费时间(升序)");
//        }

        sortDate.setText("时间");
        TextView name = (TextView) view
                .findViewById(R.id.dialog_client_sort_letter);
        TextView js = (TextView) view.findViewById(R.id.dialog_client_sort_js);
//        if (moneypaixun%2==0){
//            js.setText("金额(降序)");
//        }else{
//            js.setText("金额(升序)");
//        }

        js.setText("金额");

        if (title.indexOf(TAG1)!=-1){
            sortDate.setVisibility(View.GONE);
        }

        final Dialog dialog = new Dialog(context, R.style.dialog);
        dialog.setContentView(view);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int w = dm.widthPixels;
        dialog.show();
        android.view.WindowManager.LayoutParams parm = dialog.getWindow()
                .getAttributes();
        parm.width = w / 5 * 4;
        dialog.getWindow().setAttributes(parm);
        sortDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

                Collections.reverse(listtime);


                myAdapter.setData(listtime,0);

                initpaixunstate("timepaixun");
                sideBar.setVisibility(View.GONE);
//                String timepaixun="0";
//                String namepaixun="0";
//                String moneypaixun="0";

            }
        });
        name.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

                myAdapter.setData(listall,1);
                initpaixunstate("namepaixun");
                sideBar.setVisibility(View.VISIBLE);
            }
        });
        js.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();
                Collections.reverse(listmoney);
                myAdapter.setData(listmoney,0);

                initpaixunstate("moneypaixun");
                sideBar.setVisibility(View.GONE);


            }
        });

    }



}
