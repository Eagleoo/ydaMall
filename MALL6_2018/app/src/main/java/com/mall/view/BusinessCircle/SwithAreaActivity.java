package com.mall.view.BusinessCircle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lin.component.CustomProgressDialog;
import com.mall.model.BusinessCircleCityName;
import com.mall.util.CircleImageView;
import com.mall.util.Data;
import com.mall.util.Util;
import com.mall.view.MoreTextView;
import com.mall.view.PopwindowFromBottom;
import com.mall.view.R;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

@ContentView(R.layout.activity_swith_area)
public class SwithAreaActivity extends AppCompatActivity implements PopwindowFromBottom.popwindcallback {

    @ViewInject(R.id.hotcitylist_lv)
    ListView hotcitylist;

    @ViewInject(R.id.swithcitylist_lv)
    ListView swithcitylist;

    @ViewInject(R.id.main)
    private View rootview;

    @ViewInject(R.id.switch_province)
    private MoreTextView provincename;

    @ViewInject(R.id.swithcitytitile_mt)
            private  MoreTextView swithcitytitile_mt;


    MyAdapter hotadpter;
    MyAdapter cityadapter;


    private ArrayList<BusinessCircleCityName> hotlist = new ArrayList<>();//排名前三的列表
    private ArrayList<BusinessCircleCityName> citylist = new ArrayList<>();
    private ArrayList<String> provincenamelsit = new ArrayList<>();

    private Context context;


    private PopwindowFromBottom popwind;

    final ArrayList<BusinessCircleCityName> list1 = new ArrayList<>();

    private MyHanlder myHanlder=new MyHanlder(this);

    String nowplace="";

    BusinessCircleCityName quanguo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;

        init();
        Util.kfg="1";
    }

    private void init() {
        initview();

        initAdapter();

    }

    @Override
    protected void onStart() {
        super.onStart();
        initdata();
    }

    private void initview() {
        popwind = new PopwindowFromBottom((Activity) context, provincenamelsit);
    }

    private void initdata() {
        getAllUserMessagecity();


    }

    private static  class MyHanlder extends Handler {
        private WeakReference weakReference;
        public  MyHanlder(Context context){
            weakReference=new WeakReference(context);  //将持有的外部类转换弱引用

        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SwithAreaActivity swithAreaActivity = (SwithAreaActivity) weakReference.get();
            switch (msg.what){
                case 1234:
                    swithAreaActivity .swithcitytitile_mt.setText(swithAreaActivity.nowplace);

                    swithAreaActivity.hotadpter.notifyDataSetChanged();
                    swithAreaActivity.cityadapter.notifyDataSetChanged();
                    swithAreaActivity.dialog.cancel();
                    swithAreaActivity.dialog.dismiss();
                    break;
            }
        }
    }

    CustomProgressDialog dialog;

    public void getAllUserMessagecity() {
        dialog = Util.showProgress("城市列表加载中", context);
        new Thread(){
            @Override
            public void run() {
                super.run();



                String city = getSharedPreferences("city", MODE_PRIVATE)
                        .getString("city", "深圳市");
                Log.e("现在的城市", city + "UU");
                list1.clear();
//                Data.isUpe();
                list1.addAll(Data.getCityid(context, true));

                Log.e("数据长度","sdas"+list1.size());

                Collections.sort(list1, new Comparator<BusinessCircleCityName>() {
                    public int compare(BusinessCircleCityName o1, BusinessCircleCityName o2) {


                        return new Double(o2.getCount_()).compareTo(new Double(o1.getCount_()));

                    }
                });

                for (int i = 0; i < list1.size(); i++) {
                    BusinessCircleCityName item = list1.get(i);
                    Log.e("数据1", item.getSq_name()+"yy");

                    if (Util.isNull(item.getSq_name())){
                        continue;
                    }
                    if (i < 4) {
                        hotlist.add(item);
                    }
                    if(item.getSq_name().equals("全国")){
                        quanguo=new BusinessCircleCityName();
                        quanguo.setCount_(item.getCount_());
                        quanguo.setRenqi(item.getRenqi());
                        quanguo.setSq_name(item.getSq_name());
                        quanguo.setZoneid(item.getZoneid());
                        continue;
                    }


                   String [] strings=item.getSq_name().split("-");
                    Log.e("数据2", Arrays.toString(strings)+"yy");
                    String str = item.getSq_name().split("-")[0];  //省
                    String str1 = item.getSq_name().split("-")[1];//城市
                    Log.e("itemkk1",str+"KKK"+str1);



                    Log.e("itemkk2",str1+"KKK"+city);
                    if (str1.equals(city)){
                        Log.e("itemkk3",str1+"KKK"+city);
                        nowplace=str;
                    }

                    if (!provincenamelsit.contains(str)) {
                        provincenamelsit.add(str);
                    }

                    Log.e("檢查1",item.getSq_name()+"JKL"+nowplace);
                    if (item.getSq_name().indexOf(nowplace) != -1&&!nowplace.equals("")) {
                        Log.e("檢查2",item.getSq_name()+"JKL"+nowplace);
                        citylist.add(item);
                    }
                }
                myHanlder.sendEmptyMessage(1234);

            }
        }.start();


    }


    private ArrayList<BusinessCircleCityName> getlist(String city) {
        ArrayList<BusinessCircleCityName> list = new ArrayList<>();
        for (int i = 0; i < list1.size(); i++) {
            BusinessCircleCityName item = list1.get(i);
            if (item.getSq_name().indexOf(city) != -1) {
                list.add(item);
            }
        }
        return list;
    }

    private void initAdapter() {
        hotadpter = new MyAdapter(context, hotlist, true);
        cityadapter = new MyAdapter(context, citylist, false);
        hotcitylist.setAdapter(hotadpter);
        swithcitylist.setAdapter(cityadapter);

    }

    @OnClick({R.id.switch_province,R.id.top_back,R.id.quanguo})
    public void click(View view) {
        switch (view.getId()) {
            case R.id.switch_province:
                showprovincepopwindow();
                break;
            case R.id.top_back:
                finish();
                break;
            case  R.id.quanguo:
                if (quanguo==null){
                    return;
                }

                Intent intent = new Intent();
                intent.putExtra("zoneid", quanguo.getZoneid());
                intent.putExtra("name", quanguo.getSq_name());
                intent.putExtra("xinxi", quanguo.getCount_());
                intent.putExtra("renqi", quanguo.getRenqi());
                setResult(100, intent);
                finish();
                break;
        }
    }

    private void showprovincepopwindow() {
        if (popwind != null) {
            popwind = new PopwindowFromBottom((Activity) context, provincenamelsit);
        }
        popwind.showAtLocation(rootview,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        Util.backgroundAlpha(context, 0.7f);
        popwind.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                Util.backgroundAlpha(context, 1f);
            }
        });
    }

    @Override
    public void setprovincename(String str) {
        swithcitytitile_mt.setText(str);

        if (str.equals("上海省")){
            str="上海市";
        }


        citylist.clear();
        citylist.addAll(getlist(str));
        cityadapter.notifyDataSetChanged();

    }


    class MyAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<BusinessCircleCityName> strlist;

        private boolean ishot;

        public MyAdapter(Context context, ArrayList<BusinessCircleCityName> strlist, boolean ishot) {

            this.context = context;
            this.strlist = strlist;
            this.ishot = ishot;

        }

        @Override
        public int getCount() {

            if (strlist == null) {
                return 0;
            }

            return strlist.size();
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
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.cityitem, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.areaheadportrait_cv = (CircleImageView) convertView.findViewById(R.id.areaheadportrait_cv);
                viewHolder.areaname_tv = (TextView) convertView.findViewById(R.id.areaname_tv);
                viewHolder.switcharea_ll = convertView.findViewById(R.id.switcharea_ll);
                viewHolder.renqi = (TextView) convertView.findViewById(R.id.renqi_tv);
                viewHolder.xinxi = (TextView) convertView.findViewById(R.id.xinxi);
                viewHolder.renqi_iv = (ImageView) convertView.findViewById(R.id.renqi_iv);
                viewHolder.rootitem =  convertView.findViewById(R.id.rootitem);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (ishot){
                if (position==1){
                    viewHolder.renqi_iv.setImageResource(R.drawable.rq);
                }else if(position==2){
                    viewHolder.renqi_iv.setImageResource(R.drawable.circle_tag2);
                }else if(position==3){
                    viewHolder.renqi_iv.setImageResource(R.drawable.circle_tag3);
                }else{
                    viewHolder.renqi_iv.setVisibility(View.GONE);
                }
            }else{
                if (position==0){
                    viewHolder.renqi_iv.setImageResource(R.drawable.rq);
                }else if(position==1){
                    viewHolder.renqi_iv.setImageResource(R.drawable.circle_tag2);
                }else if(position==2){
                    viewHolder.renqi_iv.setImageResource(R.drawable.circle_tag3);
                }else{
                    viewHolder.renqi_iv.setVisibility(View.GONE);
                }
            }




            final BusinessCircleCityName businessCircleCityName = strlist.get(position);
            viewHolder.renqi.setText("人气"+businessCircleCityName.getRenqi());
            viewHolder.areaname_tv.setText(businessCircleCityName.getSq_name().replace("省","").replace("-","").replace("市","") + "圈");
            viewHolder.xinxi.setText("信息"+businessCircleCityName.getCount_());

//            Picasso.with(context).load("https://gss3.bdstatic.com/-Po3dSag_xI4khGkpoWK1HF6hhy/baike/c0%3Dbaike72%2C5%2C5%2C72%2C24/sign=d99cbbe80af431ada8df4b6b2a5fc7ca/80cb39dbb6fd5266bb3d626ead18972bd40736b1.jpg")
//                    .into(viewHolder.areaheadportrait_cv);
            try {
                Picasso.with(context).load(Util.getCityPicUrl(businessCircleCityName.getZoneid())).error(R.drawable.ic_launcher).into(viewHolder.areaheadportrait_cv);
            }catch (Exception e){

            }

            viewHolder.rootitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra("zoneid", businessCircleCityName.getZoneid());
                    intent.putExtra("name", businessCircleCityName.getSq_name());
                    intent.putExtra("xinxi", businessCircleCityName.getCount_());
                    intent.putExtra("renqi", businessCircleCityName.getRenqi());
                    setResult(100, intent);
                    finish();
                }
            });


            return convertView;
        }
    }


    class ViewHolder {
        private CircleImageView areaheadportrait_cv;
        private TextView areaname_tv,renqi,xinxi;
        private View switcharea_ll;
        private ImageView renqi_iv;
        private View rootitem;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果当Activity退出时 Loop中还未处理的信息 这个handler将不会被回收 所以清除Loop中的消息
        if (myHanlder!=null)
        myHanlder.removeCallbacksAndMessages(null);
    }
}
