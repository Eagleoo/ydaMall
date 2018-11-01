package com.mall.view.carMall;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lin.component.CustomProgressDialog;
import com.mall.net.NewWebAPI;
import com.mall.net.NewWebAPIRequestCallback;
import com.mall.util.Util;
import com.mall.view.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;
import java.util.List;

public class StyleActivity extends AppCompatActivity {

    @ViewInject(R.id.recyclerView)
    RecyclerView recyclerView;
    @ViewInject(R.id.bg_image)
    ImageView bg_image;

    List<XibaoBean.ListBean> list = new ArrayList<>();
    StyleAdapter styleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_style);
        ViewUtils.inject(this);
        Util.initTitle(this, "提车风采", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(StyleActivity.this, LinearLayoutManager.VERTICAL, false));
        styleAdapter = new StyleAdapter(list);
        recyclerView.setAdapter(styleAdapter);
        Intent intent = getIntent();
        if (intent.hasExtra("listcar")) {
            list.addAll((ArrayList<XibaoBean.ListBean>) intent.getSerializableExtra("listcar"));
            styleAdapter.notifyDataSetChanged();
        } else {
            getTichexinxi();
        }


    }

    public class StyleAdapter extends RecyclerView.Adapter<StyleAdapter.ViewHolder> {

        private List<XibaoBean.ListBean> list;

        public StyleAdapter(List<XibaoBean.ListBean> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.style_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.itemView.setTag(position);
            final XibaoBean.ListBean listBean = list.get(position);
            holder.detail.setText(listBean.getTitle());
            holder.date.setText(listBean.getAdd_date());
            if (!Util.isNull(listBean.getImg1())) {
                Picasso.with(StyleActivity.this).load("http://img.yda360.com" + listBean.getImg1()).transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap bitmap) {
                        return setRoundRect(bitmap);
                    }

                    @Override
                    public String key() {
                        return "square()";
                    }
                }).into(holder.image1);
            }
            if (!Util.isNull(listBean.getImg2())) {
                Picasso.with(StyleActivity.this).load("http://img.yda360.com" + listBean.getImg2()).transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap bitmap) {
                        return setRoundRect(bitmap);
                    }

                    @Override
                    public String key() {
                        return "square()";
                    }
                }).into(holder.image2);
            }
            if (!Util.isNull(listBean.getImg3())) {
                Picasso.with(StyleActivity.this).load("http://img.yda360.com" + listBean.getImg3()).transform(new Transformation() {
                    @Override
                    public Bitmap transform(Bitmap bitmap) {
                        return setRoundRect(bitmap);
                    }

                    @Override
                    public String key() {
                        return "square()";
                    }
                }).into(holder.image3);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(StyleActivity.this, StyleDetailActivity.class);
                    intent.putExtra("carbean", listBean);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return list == null ? 0 : list.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView detail;
            TextView date;
            ImageView image1;
            ImageView image2;
            ImageView image3;

            public ViewHolder(View itemView) {
                super(itemView);
                detail = itemView.findViewById(R.id.detail);
                date = itemView.findViewById(R.id.date);
                image1 = itemView.findViewById(R.id.image1);
                image2 = itemView.findViewById(R.id.image2);
                image3 = itemView.findViewById(R.id.image3);
            }
        }
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

    private void getTichexinxi() {
        final CustomProgressDialog cpd = Util.showProgress("", this);
        NewWebAPI.getNewInstance().getWebRequest("/carpool.aspx?call=" + "Get_Car_put_info" + "&page=1&size=999&search=",
                new NewWebAPIRequestCallback() {
                    @Override
                    public void success(Object result) {
                        if (Util.isNull(result)) {
                            Util.show("网络异常，请重试！");
                            return;
                        }


                        Gson gson = new Gson();
                        XibaoBean xibaoBean = gson.fromJson(result.toString(), XibaoBean.class);
                        if (!xibaoBean.getCode().equals("200") || xibaoBean.getList() == null
                                || xibaoBean.getList().size() == 0) {
                            bg_image.setVisibility(View.VISIBLE);
                            return;
                        }
                        list.addAll(xibaoBean.getList());
                        styleAdapter.notifyDataSetChanged();
                    }


                    @Override
                    public void fail(Throwable e) {

                    }

                    @Override
                    public void timeout() {
                        Util.show("网络超时！");
                        return;
                    }

                    @Override
                    public void requestEnd() {
                        cpd.cancel();
                        cpd.dismiss();
                    }
                }
        );
    }

}
