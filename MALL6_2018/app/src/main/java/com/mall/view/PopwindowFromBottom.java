package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.mall.util.Util;
import com.mall.view.BusinessCircle.SwithAreaActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/9/20.
 */

public class PopwindowFromBottom extends FixedPopupWindow {
    private View mView;
    private ListView listView;
    private  Context context;
    public PopwindowFromBottom(final Activity context, final List<String> shenZone) {
        super(context);
        this.context=context;


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.popwindowlist, null);
        listView= (ListView) mView.findViewById(R.id.city_list);
        listView.setAdapter(new MyAdpter(context,shenZone));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String shenzone=shenZone.get(position);
                if (shenzone.equals("上海市")){
                    shenzone="上海省";
                }
                ((SwithAreaActivity)context).setprovincename(shenzone);
                dismiss();
            }
        });




        //设置PopupWindow的View
        this.setContentView(mView);
        //设置PopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置PopupWindow弹出窗体的高
        this.setHeight(Util.dpToPx(context,200));
        //设置PopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.Animation);
        //实例化一个ColorDrawable颜色为半透明

        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(new BitmapDrawable());
    }

    class MyAdpter extends BaseAdapter{

        private Context context;
        private ArrayList<String> shenZone;

        public MyAdpter(Context context,List<String> shenZone){
            this.context=context;
            this.shenZone= (ArrayList<String>) shenZone;

        }
        @Override
        public int getCount() {

            if (shenZone==null){
                return 0;
            }
            return shenZone.size();
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
            convertView=LayoutInflater.from(context).inflate(R.layout.itemtext,parent,false);
            TextView textView = (TextView) convertView.findViewById(R.id.tv);
            String shenzone=shenZone.get(position);
            textView.setText(shenzone);
            if (shenzone.equals("上海市")){
                textView.setText("上海省");
            }


            return convertView;
        }
    }
    public interface popwindcallback{
        public void setprovincename(String str);
    }

}
