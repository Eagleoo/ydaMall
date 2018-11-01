package com.mall.BusinessDetails;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.model.BranchStore;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.view.RecyclerViewSpacesItemDecoration;
import com.mall.view.databinding.MyshopitemBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2018/3/30.
 */


@ContentView(R.layout.layout_mybranchershop)
public class MyBranchesShopActivity extends Activity {

    @ViewInject(R.id.shoplist)
    RecyclerView shoplist;
    @ViewInject(R.id.topCenter)
    TextView topCenter;
    @ViewInject(R.id.topback)
    ImageView topback;
    List<BranchStore> branchStores = new ArrayList<>();
    MyAdapter myAdapter;
    Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        context = this;
        topCenter.setText("分店");
        topback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Intent intent = getIntent();
        if (intent.hasExtra("listobj")) {
            branchStores.addAll((List<BranchStore>) intent.getSerializableExtra("listobj"));
        }
        myAdapter = new MyAdapter(this, branchStores);

        HashMap<String, Integer> stringIntegerHashMap = new HashMap<>();

        stringIntegerHashMap.put(RecyclerViewSpacesItemDecoration.BOTTOM_DECORATION, Util.dpToPx(context, 20));//top间距

        shoplist.addItemDecoration(new RecyclerViewSpacesItemDecoration(stringIntegerHashMap));
        shoplist.setLayoutManager(new LinearLayoutManager(this));
        shoplist.setAdapter(myAdapter);
        myAdapter.setmOnRecyclerviewItemClickListener(new BaseRecycleAdapter.OnRecyclerviewItemClickListener() {
            @Override
            public void onItemClickListener(View v, int position) {
                BranchStore m = branchStores.get(position);
                Util.showIntent(
                        MyBranchesShopActivity.this,
                        BusinessDetailsActivity.class,
                        new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                        new String[]{m.getId(),
                                m.getName(),
                                "",
                                "", "", m.getFavorite()});
            }
        });
    }

    class MyAdapter extends BaseRecycleAdapter<BranchStore> {

        protected MyAdapter(Context context, List<BranchStore> list) {
            super(context, list);
        }

        @Override
        public void setIteamData(ViewDataBinding mBinding, List<BranchStore> list, int position) {
            MyshopitemBinding binding = (MyshopitemBinding) mBinding;
            final BranchStore item = list.get(position);
//            mBinding.getRoot().setBackground(SelectorFactory.newShapeSelector()
//                    .setDefaultBgColor(Color.parseColor("#ffffff"))
//                    .setStrokeWidth(Util.dpToPx(mContext, 1))
//                    .setCornerRadius(Util.dpToPx(mContext, 5))
//                    .setDefaultStrokeColor(Color.parseColor("#E9E9E9"))
//                    .create());
//            if (!Util.isNull(item.getPic())) {
//                Picasso.with(mContext).load(item.getPic()).into(binding.shopic);
//            }
            binding.shopname.setText(item.getName());
//            try {
//                binding.time.setText(item.getHdate().split(" ")[0]);
//            } catch (Exception e) {
//                binding.time.setText(item.getHdate());
//            }
//            binding.phone.setText(item.getShopcphone());
            binding.area.setText(item.getShopchand1());

            binding.callphone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.doPhone(item.getShopcphone(), context);
                }
            });


        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            return DataBindingUtil.inflate(mInflater, R.layout.myshopitem, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }
    }

}
