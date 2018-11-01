package com.mall.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.Product;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ServiceProductDeatil extends Activity {

    @ViewInject(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewInject(R.id.product_name)
    TextView product_name;
    @ViewInject(R.id.price)
    TextView price;
    @ViewInject(R.id.price1)
    TextView price1;
    @ViewInject(R.id.zan)
    TextView zan;
    @ViewInject(R.id.detail_text)
    TextView detail_text;
    @ViewInject(R.id.center)
    TextView center;
    @ViewInject(R.id.detail_image)
    ImageView detail_image;

    int mSelect = 0;   //选中项
    List<Product> list = new ArrayList<>();
    String pid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_product_detail);
        ViewUtils.inject(this);
        Util.initTitle(this, "商品详情", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initLMSJProduct();
    }

    @OnClick(R.id.zan)
    public void click(View view) {
        int count = Integer.parseInt(zan.getText().toString());
        zan.setText(count + 1 + "");
        zan.setBackgroundResource(R.drawable.shape_blue_square_bg);
        zan.setCompoundDrawablesWithIntrinsicBounds(null, getResources().getDrawable(R.drawable.product_press), null, null);
        zan.setTextColor(Color.parseColor("#079FD8"));
    }

    /**
     * 初始化联盟商家商品信息
     */
    private void initLMSJProduct() {
        list = (List<Product>) getIntent().getSerializableExtra("products");
        mSelect = getIntent().getIntExtra("position", 0);
        getProductDetail(list.get(mSelect));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ServiceProductDeatil.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new SpacesItemDecoration(Util.dpToPx(ServiceProductDeatil.this, 5)));
        ProductAdapter adapter = new ProductAdapter(list, ServiceProductDeatil.this);
        recyclerView.setAdapter(adapter);
    }

    private void getProductDetail(Product product) {
        pid = product.getPid();
        center.setText(product.getName());
        price.setText(product.getPrice().split("\\.")[0]);
        price1.setText("." + product.getPrice().split("\\.")[1]);
        product_name.setText(product.getName());
//        Glide.with(this).load(product.getThumb())
        Glide.with(this).load(product.getThumb())
//                .transform(new Transformation() {
//            @Override
//            public Bitmap transform(Bitmap bitmap) {
//                return setRoundRect(bitmap);
//            }
//
//            @Override
//            public String key() {
//                return "square()";
//            }
//        })
                .into(detail_image);

        NewWebAPI.getNewInstance().getWebRequest("/Product.aspx?call=getServiceProductInfo&lmsj=lmsj&id=" + product.getPid(), new WebRequestCallBack() {
            @Override
            public void success(Object result) {
                if (Util.isNull(result)) {
                    Util.show("获取商品描述失败！", ServiceProductDeatil.this);
                    return;
                }
                JSONObject json = JSON.parseObject(result.toString());
                if (200 == json.getIntValue("code")) {
                    detail_text.setText(json.getString("productExplain"));
                } else {
                    Util.show(json.getString("message"), ServiceProductDeatil.this);
                }
            }

        });
    }

    private Bitmap setRoundRect(Bitmap bitmap) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
        RectF rectF = new RectF(new Rect(0, 0, widthLight, heightLight));
        canvas.drawRoundRect(rectF, 15, 15, paintColor);
        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);
        bitmap.recycle();
        return output;
    }

    class ProductAdapter extends RecyclerView.Adapter {

        List<Product> list;
        Context context;

        public ProductAdapter(List list, Context context) {
            this.context = context;
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.product_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            ViewHolder viewHolder = (ViewHolder) holder;
            Picasso.with(context).load(list.get(position).getThumb()).into(viewHolder.image);
            viewHolder.name_price.setText(list.get(position).getName() + " ¥" + list.get(position).getPrice());
            if (mSelect == position) {
                viewHolder.imagelin.setBackgroundResource(R.drawable.shape_blue_square_bg);
            } else {
                viewHolder.imagelin.setBackgroundResource(R.drawable.shape_gray_square_bg);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeSelected(position);
                    getProductDetail(list.get(position));
                }
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        public void changeSelected(int positon) { //刷新方法
            if (positon != mSelect) {
                mSelect = positon;
                notifyDataSetChanged();
            }
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView image;
        TextView name_price;
        LinearLayout imagelin;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.image = itemView.findViewById(R.id.image);
            this.name_price = itemView.findViewById(R.id.name_price);
            this.imagelin = itemView.findViewById(R.id.imagelin);
        }
    }

    class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;
            outRect.top = space;
        }
    }

}
