package com.mall.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.net.UserFavorite;
import com.mall.newmodel.CollectionModel;
import com.mall.util.Util;
import com.mall.view.databinding.ActivityMyCollectionBinding;
import com.mall.view.databinding.CollectProductFrameListItemBinding;

import java.util.ArrayList;
import java.util.List;

public class MyCollectionActivity extends AppCompatActivity {
    ActivityMyCollectionBinding mBinding;
    private Context mContext;
    private String ptype = "";
    MyAdapter myAdapter;
    List<CollectionModel.ListBean> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
//        setContentView(R.layout.activity_my_collection);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_my_collection);
        mBinding.left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
        mBinding.click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openmenu(view);
            }
        });
        init();
        getList("我全部的收藏");
    }

    private void init() {
        initrecycle();
    }

    private void initrecycle() {
        LinearLayoutManager manager = new LinearLayoutManager(this);
        mBinding.collectionlist.setLayoutManager(manager);
        myAdapter = new MyAdapter(mContext, list);
        mBinding.collectionlist.setAdapter(myAdapter);
    }

    public void openmenu(View view) {
        Log.e("dianj1i", "view");
        final View contentView = LayoutInflater.from(mContext).inflate(R.layout.iteamlist, null);
        ListView listView = contentView.findViewById(R.id.rootlist);
        final List<String> strings = new ArrayList<>();
        strings.add("我收藏的商品");
        strings.add("我收藏的店铺");
        strings.add("我全部的收藏");
//        strings.add("收藏商品");
//        strings.add("收藏店铺");
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public Object getItem(int i) {
                return i;
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                View view1 = LayoutInflater.from(mContext).inflate(R.layout.itemtext, viewGroup, false);
                TextView textView = view1.findViewById(R.id.tv);
                textView.setText(strings.get(i));
                return view1;
            }
        });

        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
        // 允许点击外部消失
        popupWindow.setBackgroundDrawable(new BitmapDrawable());//注意这里如果不设置，下面的setOutsideTouchable(true);允许点击外部消失会失效
        popupWindow.setOutsideTouchable(true);   //设置外部点击关闭ppw窗口
        popupWindow.setFocusable(true);
        getpop(popupWindow, view);
//        final ShowMyPopWindow showMyPopWindow=new ShowMyPopWindow(contentView,mContext,ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0);
//        showMyPopWindow.showAsDropDown(view);
//        final CustomPopWindow mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
//                .setView(contentView)
//                .enableBackgroundDark(true) //弹出popWindow时，背景是否变暗
//                .setBgDarkAlpha(0.7f) // 控制亮度
//                .create()
//                .showAsDropDown(view, 0, 20);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("类型", "typeid" + strings.get(i));
                if (i != 3 && i != 4) {
                    getList(strings.get(i));
                } else if (i == 3) {
                    //收藏商品
                    UserFavorite.addFavorite(mContext, 179722 + "", "0", new UserFavorite.CallBacke() {
                        @Override
                        public void doback(String message) {
                            getnet();
                        }


                    });
                } else if (i == 4) {
                    UserFavorite.addFavorite(mContext, 91 + "", "1", new UserFavorite.CallBacke() {
                        @Override
                        public void doback(String message) {
                            getnet();
                        }


                    });
                }

                popupWindow.dismiss();
            }
        });
    }

    private void getpop(PopupWindow mPopupWindow, View select_dev_bg) {
        if (Build.VERSION.SDK_INT != 24) {
            //只有24这个版本有问题，好像是源码的问题
            mPopupWindow.showAsDropDown(select_dev_bg);
        } else {
            //7.0 showAsDropDown没卵子用 得这么写
            int[] location = new int[2];
            select_dev_bg.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            mPopupWindow.showAtLocation(select_dev_bg, Gravity.NO_GRAVITY, x, y + select_dev_bg.getHeight());
        }
    }

    private void getList(String str) {
        if (str.equals("我收藏的商品")) {
            ptype = "0";
        } else if (str.equals("我收藏的店铺")) {
            ptype = "1";
        } else if (str.equals("我全部的收藏")) {
            ptype = "";
        }
        getnet();

    }

    public void getnet() {
        UserFavorite.getFavorite(ptype, new UserFavorite.CollectionCallBacke() {
            @Override
            public void doback(List<CollectionModel.ListBean> models) {
                if (models != null) {
                    list.clear();
                    list.addAll(models);
                    myAdapter.notifyDataSetChanged();
                }
            }


        });
    }

    class MyAdapter extends BaseRecycleAdapter<CollectionModel.ListBean> {

        protected MyAdapter(Context context, List<CollectionModel.ListBean> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, final List<CollectionModel.ListBean> list, final int position) {
            final CollectionModel.ListBean listBean = list.get(position);
            CollectProductFrameListItemBinding mbindg = (CollectProductFrameListItemBinding) mBinding;
            if (listBean.getPtype().equals("0")) {

                Glide.with(mContext).load(listBean.getPRODUCTPHOTO()).into(mbindg.collectItemImg);
                mbindg.collectItemName.setText(listBean.getPRODUCTNAME());
                mbindg.isshow.setText("收藏时间");
                mbindg.collectItemTime.setText(listBean.getDate().split(" ")[0]);
                mbindg.collectItemShop.setText("前去购买");
                mbindg.collectItemShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Util.showIntent(mContext, ProductDeatilFream.class, new String[]{"url"}, new String[]{listBean.getProductid()});
                    }
                });
                //取消收藏
                mbindg.collectItemQuit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserFavorite.deletFavorite(mContext, list.get(position).getProductid(), "0", new UserFavorite.CallBackDelete() {
                            @Override
                            public void doback(String message) {
                                getnet();
                            }

                        });
                    }
                });

            } else {
                Glide.with(mContext).load(listBean.getPRODUCTPHOTO()).into(mbindg.collectItemImg);
                mbindg.collectItemName.setText(listBean.getPRODUCTNAME());
                mbindg.collectItemTime.setText(listBean.getSHOPCPHONE());
                mbindg.collectItemShop.setText("查看详情");
                mbindg.collectItemShop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {


                        Util.showIntent(
                                mContext,
                                BusinessDetailsActivity.class,
                                new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                        "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                                new String[]{listBean.getProductid(),
                                        listBean.getPRODUCTNAME(),
                                        "",
                                        "", "", "1"});
                    }
                });
                //取消收藏
                mbindg.collectItemQuit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserFavorite.deletFavorite(mContext, list.get(position).getProductid(), "1", new UserFavorite.CallBackDelete() {
                            @Override
                            public void doback(String message) {
                                getnet();
                            }


                        });
                    }
                });
            }

        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {


            CollectProductFrameListItemBinding mbindg =
                    DataBindingUtil.inflate(mInflater, R.layout.collect_product_frame_list_item, parent, false);


            return mbindg;
        }

        @Override
        public int setShowRule(int position) {
            return Integer.parseInt(mList.get(position).getPtype());
        }
    }
}
