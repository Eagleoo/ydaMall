package com.mall.view;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.mall.BusinessDetails.BusinessDetailsActivity;
import com.mall.model.LocationModel;
import com.mall.model.RecBussModel;
import com.mall.model.ShopM;
import com.mall.net.UserFavorite;
import com.mall.net.Web;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Administrator on 2018/4/26.
 */

public class RecAdapter extends BaseAdapter {
    private Context context;
    private List<RecBussModel.ListBean> list;


    public RecAdapter(Context context, List<RecBussModel.ListBean> list) {
        this.context = context;
        this.list = list;

    }

    @Override
    public int getCount() {
        if (list == null) {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.main_layout4_shopm_item, parent, false);
        ImageView logo = (ImageView) view.findViewById(R.id.main_layout4_shopM_item_logo);
        TextView name = (TextView) view.findViewById(R.id.main_layout4_shopM_item_name);
        TextView phone = (TextView) view.findViewById(R.id.main_layout4_shopM_item_phone);
        TextView cate = (TextView) view
                .findViewById(R.id.main_layout4_shopM_item_cate);

        TextView juli = (TextView) view
                .findViewById(R.id.main_layout4_shopM_item_juli);

        TextView gotoThat = (TextView) view
                .findViewById(R.id.main_layout4_shopM_item_goto);

        final TextView mysc = (TextView) view
                .findViewById(R.id.main_layout4_shopM_item_mysc);

        LinearLayout line = (LinearLayout) view
                .findViewById(R.id.main_layout4_shopM_item_line);

        final RecBussModel.ListBean m = list.get(position);
        if (Util.isNull(Util.citylat)) {
            m.setMm(Float.MAX_VALUE);
        } else {
            if (!m.getCate().equals("暂无分类")) {
                LatLng remote = new LatLng(Double.parseDouble(m
                        .getPointY()), Double.parseDouble(m
                        .getPointX()));
                LocationModel locationModel = LocationModel.getLocationModel();
                float distanceM = AMapUtils.calculateLineDistance(new LatLng(locationModel.getLatitude(), locationModel.getLongitude()), remote);
                m.setMm(distanceM);
            } else {
                m.setMm(Float.MAX_VALUE);
            }

            final ShopM shopM = new ShopM();
            shopM.setCate(m.getCate());
            shopM.setId(m.getId());
            shopM.setImg(m.getImg());
            shopM.setISELITE(m.getISELITE());
            shopM.setMm(m.getMm());
            shopM.setName(m.getName());
            shopM.setPointX(m.getPointX());
            shopM.setPointY(m.getPointY());
            shopM.setPhone(m.getPhone());
            shopM.setZone(m.getZone());

            m.setImg(m.getImg().replaceFirst(Web.webImage,
                    Web.webServer));
            Picasso.with(context).load(m.getImg()).into(logo);
            name.setText(m.getName());
            if (m.getFavorite() != null) {
                mysc.setText(m.getFavorite().equals("1") ? "已收藏" : "收藏");
            } else {
                mysc.setText("收藏");
            }

            Spanned fromHtml = Html
                    .fromHtml("<font color='#ff535353'>电话：</font><u><font color='#ff535353'>"
                            + m.getPhone() + "</font></u>");
            phone.setText(fromHtml);
            phone.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    Util.doPhone(m.getPhone(),
                            context);
                }
            });

            cate.setText(m.getCate());


            if (m.getMm() == Float.MAX_VALUE)
                juli.setText("无法计算");
            else
                juli.setText(Util.getMM(m.getMm()));


            gotoThat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    synchronized (v) {
                        if (Util.isNull(m.getPointX())
                                || Util.isNull(m.getPointY())) {
                            Util.show("对不起，该商家没有定位传送门！",
                                    context);
                            return;
                        }


                        Util.doDaoHang(context,
                                shopM);

                    }
                }
            });


            mysc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    if (!"-7".equals(m.getId())) {
                        if (null != UserData.getUser()) {
                            if (m.getFavorite().equals("0")) {
                                UserFavorite.addFavorite(context, m.getId() + "", "1", new UserFavorite.CallBacke() {
                                    @Override
                                    public void doback(String message) {
                                        mysc.setText("已收藏");
                                        m.setFavorite("1");
                                    }

                                });
                            } else {
                                UserFavorite.deletFavorite(context, m.getId() + "", "1", new UserFavorite.CallBackDelete() {
                                    @Override
                                    public void doback(String message) {
                                        mysc.setText("收藏");
                                        m.setFavorite("0");
                                    }

                                });
                            }

//                                if (Data.addMyShopMSc(
//                                        context, shopM))
//
//                                    Util.show("收藏成功！",
//                                            context);
//                                else
//                                    Util.show("收藏失败！",
//                                            context);
                        } else {
                            Util.show("请先登录，在收藏",
                                    context);
                            Util.showIntent(context, LoginFrame.class, new String[]{"home"}, new String[]{"home"});
                        }
                    } else {
                        Util.show("该商家不能收藏！",
                                context);
                    }
                }
            });

            //联盟商家item点击事件
            line.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //赵超临时添加打印日志
                    Log.e("msg", "你点击了这个Item");

                    if (m.getCate().equals("暂无分类")) {
                        return;
                    }


                    if (!"-7".equals(m.getId())) {
                        Util.showIntent(
                                context,
                                BusinessDetailsActivity.class,
                                new String[]{BusinessDetailsActivity.BUSINESS_ID, "name",
                                        "x", "y", "face", BusinessDetailsActivity.BUSINESS_Favorite},
                                new String[]{m.getId(),
                                        m.getName(),
                                        m.getPointX(),
                                        m.getPointY(), m.getImg(), m.getFavorite()});
                    }
                }
            });

            return view;
        }
        return null;
    }
}
