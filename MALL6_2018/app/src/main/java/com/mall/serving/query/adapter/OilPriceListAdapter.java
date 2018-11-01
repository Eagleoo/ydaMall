package com.mall.serving.query.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.mall.model.LocationModel;
import com.mall.serving.community.adapter.NewBaseAdapter;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.Util;
import com.mall.serving.query.activity.oilprice.BusRouteActivity;
import com.mall.serving.query.activity.oilprice.OilPriceDetailActivity;
import com.mall.serving.query.activity.oilprice.WalkRouteActivity;
import com.mall.serving.query.model.OilPriceInfo.Data;
import com.mall.view.R;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

public class OilPriceListAdapter extends NewBaseAdapter {

	public OilPriceListAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int p, View v, ViewGroup arg2) {

		if (v == null) {
			ViewCache cache = new ViewCache();
			v = LayoutInflater.from(context).inflate(
					R.layout.query_oil_price_list_item, null);
			cache.iv_1 = (ImageView) v.findViewById(R.id.iv_1);
			cache.tv_1 = (TextView) v.findViewById(R.id.tv_1);
			cache.tv_2 = (TextView) v.findViewById(R.id.tv_2);
			cache.tv_3 = (TextView) v.findViewById(R.id.tv_3);
			v.setTag(cache);
		}
		final Data data = (Data) list.get(p);

		ViewCache cache = (ViewCache) v.getTag();
		String brandname = data.getBrandname();
		int rid = R.drawable.query_oil_price_1;
		if (!brandname.contains("中石")) {
			rid = R.drawable.query_oil_price_2;
		}
		cache.iv_1.setImageResource(rid);
		cache.tv_1.setText(data.getName());
		cache.tv_2.setText("￥" + data.getPriceTemp());
		String distance = data.getDistance();
		if (TextUtils.isEmpty(distance)) {
			cache.tv_3.setVisibility(View.GONE);
		}else {
			cache.tv_3.setVisibility(View.VISIBLE);
		}
		cache.tv_3.setText(distance+"米");
		cache.tv_3.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				AnimeUtil.startAnimation(context, view, R.anim.small_2_big,
						new OnAnimEnd() {

							@Override
							public void start() {
								// TODO Auto-generated method stub

							}

							@Override
							public void repeat() {
								// TODO Auto-generated method stub

							}

							@Override
							public void end() {

								final LocationModel aMapLocation=LocationModel.getLocationModel();

								AlertDialog.Builder builder = new AlertDialog.Builder(context);
								builder.setTitle("请选择导航模式");
								builder.setPositiveButton("驾车",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(final DialogInterface dialog,
																int which) {
												boolean isBaidu = com.mall.util.Util
														.isInstall(context,
																"com.baidu.BaiduMap");
												boolean isGaoDe = com.mall.util.Util.isInstall(
														context,
														"com.autonavi.minimap");
												boolean isGoogle = com.mall.util.Util.isInstall(
														context,
														"com.google.android.apps.maps");
												AlertDialog builder2 = new AlertDialog.Builder(
														context).create();
												builder2.setTitle("请选择导航软件！");
												LinearLayout root = new LinearLayout(
														context);
												root.setOrientation(LinearLayout.VERTICAL);
												Resources res = context
														.getResources();
												LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(
														LinearLayout.LayoutParams.FILL_PARENT,
														LinearLayout.LayoutParams.WRAP_CONTENT);
												int _5dp = com.mall.util.Util.dpToPx(context, 5F);
												ll.setMargins(_5dp, _5dp, _5dp, _5dp);
												if (isBaidu) {
													TextView baiduMap = new TextView(
															context);
													baiduMap.setLayoutParams(ll);
													Drawable dra = res
															.getDrawable(R.drawable.baidu);
													dra.setBounds(0, 0, dra.getMinimumWidth(),
															dra.getMinimumWidth());
													baiduMap.setCompoundDrawables(dra, null,
															null, null);
													baiduMap.setText("百度地图");
													baiduMap.setGravity(Gravity.CENTER_VERTICAL);
													baiduMap.setOnClickListener(new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															String lat = "";
															String lng = "";
															lat = aMapLocation
																	.getLatitude()
																	+ "";
															lng = aMapLocation.getLongitude()

																	+ "";
															try {
																Intent intent = Intent.getIntent("baidumap://map/direction?origin=latlng:"
																		+ lat
																		+ ","
																		+ lng
																		+ "|name:当前位置&destination=latlng:"
																		+ aMapLocation.getLatitude()
																		+ ","
																		+ aMapLocation.getLongitude()
																		+ "|name:"
																		+ data.getName()
																		+ " &mode=driving&region="
																		+ aMapLocation
																		.getCity());
																context
																		.startActivity(intent);
															} catch (URISyntaxException e) {
																e.printStackTrace();
															}
														}
													});
													root.addView(baiduMap);
												}
												if (isGaoDe) {
													TextView gaodeMap = new TextView(
															context);
													gaodeMap.setLayoutParams(ll);
													Drawable dra = res
															.getDrawable(R.drawable.gaode);
													dra.setBounds(0, 0, dra.getMinimumWidth(),
															dra.getMinimumWidth());
													gaodeMap.setCompoundDrawables(dra, null,
															null, null);
													gaodeMap.setText("高德地图");
													gaodeMap.setGravity(Gravity.CENTER_VERTICAL);
													gaodeMap.setOnClickListener(new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															try {
																Intent intent = Intent
																		.getIntent("androidamap://navi?sourceApplication=mall666&poiname=&poiid=&lat="
																				+ aMapLocation.getLatitude()
																				+ "&lon="
																				+ aMapLocation.getLongitude()
																				+ "&dev=1&style=0");
																context
																		.startActivity(intent);
															} catch (URISyntaxException e) {
																e.printStackTrace();
															}
														}
													});
													root.addView(gaodeMap);
												}
												if (isGoogle) {
													TextView gugeMap = new TextView(
															context);
													gugeMap.setLayoutParams(ll);
													Drawable dra = res
															.getDrawable(R.drawable.guge);
													dra.setBounds(0, 0, dra.getMinimumWidth(),
															dra.getMinimumWidth());
													gugeMap.setCompoundDrawables(dra, null,
															null, null);
													gugeMap.setText("Google地图");
													gugeMap.setGravity(Gravity.CENTER_VERTICAL);
													gugeMap.setOnClickListener(new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															String lat = "";
															String lng = "";
															lat = aMapLocation.getLatitude()
																	+ "";
															lng = aMapLocation.getLongitude()

																	+ "";
															Intent i = new Intent(
																	Intent.ACTION_VIEW,
																	Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr="
																			+ lat
																			+ ","
																			+ lng
																			+ "&daddr="
																			+ aMapLocation.getLatitude()
																			+ ","
																			+ aMapLocation.getLongitude()
																			+ "&hl=zh"));
															i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
																	& Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
															i.setClassName(
																	"com.google.android.apps.maps",
																	"com.google.android.maps.MapsActivity");
															context.startActivity(i);
														}
													});
													root.addView(gugeMap);
												}
												if (!isBaidu && !isGoogle && !isGaoDe) {
													Util.show("您还没有安装地图导航软件");
//													com.mall.util.Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？",
//															context,
//															MapLMSJFrame.class, null,
//															new String[] { "dh", "mode" },
//															new String[] { aMapLocation.getId(), "2" });
												} else {
													builder2.setView(root);
													builder2.show();
												}
											}
										});
								builder.setNegativeButton("步行",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
																int which) {
//                                routeType = 3;
//                                doStartDaoHang(marker.getPosition());
												LocationModel locationModel=LocationModel.getLocationModel();
												final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(),locationModel.getLongitude());
												final LatLonPoint mEndPoint = new LatLonPoint(Util.getDouble(data.getLat()),Util.getDouble(data.getLon()));
												Intent intent=new Intent(context,WalkRouteActivity.class);
												intent.putExtra("mStartPoint",mStartPoint);
												intent.putExtra("mEndPoint",mEndPoint);
												context.startActivity(intent);

											}
										});
								builder.setNeutralButton("公交",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog,
																int which) {
//                                routeType = 1;
//                                doStartDaoHang(marker.getPosition());
												LocationModel locationModel=LocationModel.getLocationModel();
												final LatLonPoint mStartPoint = new LatLonPoint(locationModel.getLatitude(),locationModel.getLongitude());
												final LatLonPoint mEndPoint = new LatLonPoint(Util.getDouble(data.getLat()),Util.getDouble(data.getLon()));
												Intent intent=new Intent(context,BusRouteActivity.class);
												intent.putExtra("mStartPoint",mStartPoint);
												intent.putExtra("mEndPoint",mEndPoint);
												context.startActivity(intent);
											}
										});
								builder.show();
//								Util.showIntent(context,
//										OilPriceMainActivity.class,
//										new String[] { "dh" ,"from"},
//										new Serializable[] { data,1 });

							}
						});

			}
		});

		v.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Util.showIntent(context, OilPriceDetailActivity.class,
						new String[] { "data" }, new Serializable[] { data });

			}
		});

		return v;
	}

	class ViewCache {
		private ImageView iv_1;
		private TextView tv_1;
		private TextView tv_2;
		private TextView tv_3;

	}
}
