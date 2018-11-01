package com.mall.view;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.Base.BaseActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jaeger.ninegridimageview.ItemImageClickListener;
import com.jaeger.ninegridimageview.NineGridImageViewAdapter;
import com.lin.component.CustomProgressDialog;
import com.mall.adapter.BaseRecycleAdapter;
import com.mall.net.NewWebAPI;
import com.mall.net.Web;
import com.mall.net.WebRequestCallBack;
import com.mall.serving.community.view.picviewpager.PicViewpagerPopup;
import com.mall.util.Util;
import com.mall.view.databinding.ActivityNewProductCommentListBinding;
import com.mall.view.databinding.AppraiseitemBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewProductCommentListActivity extends BaseActivity<ActivityNewProductCommentListBinding> {

    MyAdapter shopAdapter;
    private String pid = "0";
    private List<ShopEvaluationBean.ListBeanX> productCommentListModels = new ArrayList<>();

    @Override
    public int getContentViewId() {
        return R.layout.activity_new_product_comment_list;
    }

    @Override
    public View getattachedview() {
        return null;
    }

    @Override
    protected void initView(ActivityNewProductCommentListBinding mBinding) {
        mBinding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pid = this.getIntent().getStringExtra("pid");
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mBinding.recyclerView.setLayoutManager(linearLayoutManager);
        shopAdapter = new MyAdapter(context, productCommentListModels);
        mBinding.recyclerView.setAdapter(shopAdapter);
    }

    @Override
    protected void initData(ActivityNewProductCommentListBinding mBinding) {
        getData();
    }

    private void getData() {
        final CustomProgressDialog dialog = Util.showProgress("数据加载中...", this);
        NewWebAPI.getNewInstance().getProductComment(pid, 1, 9999,
                new WebRequestCallBack() {
                    @Override
                    public void success(Object result) {
                        Gson gson = new Gson();
                        ShopEvaluationBean shopEvaluationBean = gson.fromJson(result.toString(), ShopEvaluationBean.class);

                        if (!shopEvaluationBean.getCode().equals("200")) {
                            Util.show(shopEvaluationBean.getMessage(),
                                    context);
                            return;
                        }
                        productCommentListModels.clear();
                        productCommentListModels.addAll(shopEvaluationBean.getList());
                        Log.e("评价长度", "list" + productCommentListModels.size());
                        shopAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void requestEnd() {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });
    }

    class MyAdapter extends BaseRecycleAdapter<ShopEvaluationBean.ListBeanX> {
        protected MyAdapter(Context context, List<ShopEvaluationBean.ListBeanX> list) {
            super(context, list);
        }


        @Override
        public void setIteamData(ViewDataBinding mBinding, List<ShopEvaluationBean.ListBeanX> list, int position) {
            ShopEvaluationBean.ListBeanX lmsjComment = list.get(position);
            AppraiseitemBinding binding = (AppraiseitemBinding) mBinding;


            List<ShopEvaluationBean.ListBeanX.ListBean> list1=lmsjComment.getList();
           Gson gson=new Gson();
            binding.csgImages.setVisibility(View.GONE);
            if (list1!=null&&list1.size()>0){
                ShopEvaluationBean.ListBeanX.ListBean bean1=list1.get(0);
                if (!Util.isNull(bean1.getUserFace())) {
                    Picasso.with(context).load(bean1.getUserFace()).error(R.drawable.headertwo).into(binding.face);
                }
                binding.star.setVisibility(View.INVISIBLE);
                binding.name.setText(lmsjComment.getUserId());
                binding.content.setText(bean1.getContent());
                binding.time.setText(bean1.getDate());

                if (!Util.isNull(bean1.getProduct_comment_img())){
                    List<String> images = Arrays.asList(bean1.getProduct_comment_img().split(","));
//

                    Log.e("评论图片","有");
                    binding.csgImages1.setVisibility(View.VISIBLE);

                    binding.csgImages1.setAdapter(new NineGridImageViewAdapter() {
                        @Override
                        protected void onDisplayImage(Context context, ImageView imageView, Object o) {
                            String imageurl = "http://" + Web.imgServer + o.toString();
                            Log.e("图片路径", "ll" + imageurl);
                            Glide.with(context).load(imageurl).into(imageView);
                        }
                    });
                    binding.csgImages1.setImagesData(images);
                    binding.csgImages1.setItemImageClickListener(new ItemImageClickListener<String>() {
                        @Override
                        public void onItemImageClick(Context context1, ImageView imageView, int index, List list) {
                            List<String> image = new ArrayList<>();
                            for (int i = 0; i < list.size(); i++) {
                                Log.e("图片路径", "sss" + "http://" + Web.imgServer + list.get(i));
                                image.add("http://" + Web.imgServer + list.get(i));
                            }
                            new PicViewpagerPopup(context, image, index, true, null);
                        }
                    });
                }else {
                    binding.csgImages1.setVisibility(View.GONE);
                }

                if (list1.size()>1){
                    binding.zuilin.setVisibility(View.VISIBLE);
                    ShopEvaluationBean.ListBeanX.ListBean bean2=list1.get(1);
                    binding.contentzui.setText(bean2.getContent());
                    binding.timezui.setText(bean2.getDate());
                    if (!Util.isNull(bean2.getProduct_comment_img())){
                        List<String> images = Arrays.asList(bean2.getProduct_comment_img().split(","));
//

                        Log.e("评论图片","有");
                        binding.csgImages1zui.setVisibility(View.VISIBLE);

                        binding.csgImages1zui.setAdapter(new NineGridImageViewAdapter() {
                            @Override
                            protected void onDisplayImage(Context context, ImageView imageView, Object o) {
                                String imageurl = "http://" + Web.imgServer + o.toString();
                                Log.e("图片路径", "ll" + imageurl);
                                Glide.with(context).load(imageurl).into(imageView);
                            }
                        });
                        binding.csgImages1zui.setImagesData(images);
                        binding.csgImages1zui.setItemImageClickListener(new ItemImageClickListener<String>() {
                            @Override
                            public void onItemImageClick(Context context1, ImageView imageView, int index, List list) {
                                List<String> image = new ArrayList<>();
                                for (int i = 0; i < list.size(); i++) {
                                    Log.e("图片路径", "sss" + "http://" + Web.imgServer + list.get(i));
                                    image.add("http://" + Web.imgServer + list.get(i));
                                }
                                new PicViewpagerPopup(context, image, index, true, null);
                            }
                        });
                    }else {
                        binding.csgImages1zui.setVisibility(View.GONE);
                    }


                }else {
                    binding.zuilin.setVisibility(View.GONE);
                }

            }














        }

        @Override
        public ViewDataBinding getShowRule(LayoutInflater mInflater, ViewGroup parent, int viewType) {
            return DataBindingUtil.inflate(mInflater, R.layout.appraiseitem, parent, false);
        }

        @Override
        public int setShowRule(int position) {
            return 0;
        }
    }
}
