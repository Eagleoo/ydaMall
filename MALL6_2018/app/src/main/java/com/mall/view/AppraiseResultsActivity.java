package com.mall.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.model.ShopM;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@ContentView(R.layout.activity_appraise_results)
public class AppraiseResultsActivity extends AppCompatActivity {

    Context context;

    @ViewInject(R.id.recommendmerchant)
    private ListView listView;

    private String shopType="";

    List<ShopM> listbean=new ArrayList<>();

    MyAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=this;
        ViewUtils.inject(this);
        shopType=getIntent().getStringExtra("shopType");
        init();
        

    }
    @OnClick({R.id.top_back,R.id.wancheng,R.id.ivbaner})
    private void click(View view){
        switch (view.getId()){
            case R.id.ivbaner:
                Intent intent= new Intent(context, StoreMainFrame.class);
                startActivity(intent);
                break;
        }

        finish();
    }

    private void init() {
        initAdapter();
        initdata();
    }

    private void initAdapter() {
        myAdapter=new MyAdapter(context,listbean);
        listView.setAdapter(myAdapter);
    }

    private void initdata() {
        getshop(com.mall.util.Util.getShopCate(shopType));
    }

    private void getshop(final String selectedCateId){
        Util.asynTask(context, "正在加载更多数据...", new IAsynTask() {
            @Override
            public Serializable run() {
                String param = "page=" + 1 + "&size=" + 2;
                String method = Web.getShopMByCate;
                param += "&cateid=" + selectedCateId;
                Web web = new Web(method, param);
                List<ShopM> list = web.getList(ShopM.class);
                HashMap<String, List<ShopM>> map = new HashMap<String, List<ShopM>>();
                map.put("list", list);
                return map;
            }

            @Override
            public void updateUI(Serializable runData) {


                HashMap<String, List<ShopM>> map = (HashMap<String, List<ShopM>>) runData;
                List<ShopM> list = map.get("list");
                listbean.clear();
                listbean.addAll(list);
                myAdapter.notifyDataSetChanged();

            }
        });
    }

    class MyAdapter extends BaseAdapter{
        Context context;
        List<ShopM> list;


        public  MyAdapter(Context context ,List<ShopM> list){
            this.context=context;
            this.list=list;
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
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView= LayoutInflater.from(context).inflate(R.layout.pingjiatuijianitem,parent,false);
            TextView textView = (TextView) convertView.findViewById(R.id.item_tv);
            TextView itemcheck_tv = (TextView) convertView.findViewById(R.id.itemcheck_tv);
            TextView info = (TextView) convertView.findViewById(R.id.info);
            final ShopM shopM=list.get(position);
            textView.setText(shopM.getName());
            info.setText(shopM.getCate());
            itemcheck_tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Util.showIntent(
                            context,
                            BusinessDetailsActivity.class,
                            new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                    "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                            new String[]{shopM.getId(),
                                    shopM.getName(),
                                    "",
                                    "", shopM.getImg(), shopM.getFavorite()});
                    finish();
                }
            });
            return convertView;
        }
    }

}
