package com.mall.serving.query.activity.oilprice;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusPath;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.BusRouteQuery;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.RouteSearch.WalkRouteQuery;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.ResType;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ResInject;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.LocationModel;
import com.mall.net.Web;
import com.mall.serving.community.activity.BaseActivity;
import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.util.IAsynTask;
import com.mall.serving.community.util.JsonUtil;
import com.mall.serving.community.util.Util;
import com.mall.serving.community.view.textview.TextDrawable;
import com.mall.serving.query.model.OilPriceInfo;
import com.mall.serving.query.model.OilPriceInfo.Data;
import com.mall.serving.query.model.OilPriceInfo.Data.Price;
import com.mall.view.PositionService;
import com.mall.view.R;

import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ContentView(R.layout.query_oil_price_main_activity)
public class OilPriceMainActivity extends BaseActivity
		implements OnMarkerClickListener, LocationSource, InfoWindowAdapter, OnMapLoadedListener,
		OnInfoWindowClickListener, OnCameraChangeListener, OnRouteSearchListener, OnMapClickListener {

	@ViewInject(R.id.top_center)
	private TextView top_center;
	@ViewInject(R.id.top_left)
	private TextView top_left;
	@ViewInject(R.id.top_right)
	private TextView top_right;
	@ViewInject(R.id.tv_1)
	private TextView tv_1;
	@ViewInject(R.id.tv_2)
	private TextView tv_2;
	@ViewInject(R.id.et_1)
	private EditText et_1;
	@ViewInject(R.id.iv_1)
	private ImageView iv_1;
	@ViewInject(R.id.ll)
	private View ll;

	@ViewInject(R.id.map_lmsj_map)
	private MapView mapView;

	private AMap aMap;
	private OnLocationChangedListener mListener;
	private List<Marker> markerList;

	// 导航
	private String cityCode = "";

	private LatLng locationLatLng;
	private ProgressDialog progDialog = null;// 搜索时进度条
	private int busMode = RouteSearch.BusDefault;// 公交默认模式
	private int drivingMode = RouteSearch.DrivingDefault;// 驾车默认模式
	private int walkMode = RouteSearch.WalkDefault;// 步行默认模式
	private RouteSearch routeSearch;
	private int routeType = 1;// 1代表公交模式，2代表驾车模式，3代表步行模式
	private boolean isLoad = false;

	private PositionService locationService;

	private Marker currShowMarker = null;

	private String selCity = "";

	// #start 头部右边选择城市

	@ViewInject(R.id.map_lmsj_cityList_scrollView)
	private ScrollView map_lmsj_cityList_scrollView;
	@ViewInject(R.id.map_lmsj_cityList)
	private LinearLayout map_lmsj_cityList;

	@ResInject(id = R.drawable.liner_border_white, type = ResType.Drawable)
	private Drawable liner_border_white;
	@ResInject(id = R.drawable.liner_border, type = ResType.Drawable)
	private Drawable liner_border;
	// #end 头部右边选择城市
	
	private boolean isfrist=true;

	private ServiceConnection locationServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {

		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.e("MapLMSJFrame", "bind服务成功！");
			PositionService.PositionBinder locationBinder = (PositionService.PositionBinder) service;
			locationService = locationBinder.getService();
			locationService.startLocation();
			locationService.setOnPositionListener(new PositionService.OngetPositionListener() {
				@Override
				public void onProgress(AMapLocation progress) {
					if (TextUtils.isEmpty(selCity)){
						selCity = progress.getCity();
						initmap();
					}
				}

				@Override
				public void onError(AMapLocation error) {

				}
			});


		}
	};


	 private void initmap(){
		 if (aMap == null) {
			 aMap = mapView.getMap();
			 aMap.setOnMarkerClickListener(this);
			 aMap.setOnInfoWindowClickListener(this);// 设置点击marker事件监听器
			 aMap.setOnMapLoadedListener(this);// 设置amap加载成功事件监听器
			 aMap.setOnCameraChangeListener(this);
			 aMap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式
			 aMap.setOnMapClickListener(this);

			 // 自定义系统定位小蓝点
			 MyLocationStyle myLocationStyle = new MyLocationStyle();
			 myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.location_marker));// 设置小蓝点的图标
			 myLocationStyle.strokeColor(Color.BLACK);// 设置圆形的边框颜色
			 myLocationStyle.strokeWidth(2);// 设置圆形的边框粗细
			 aMap.setMyLocationStyle(myLocationStyle);
			 aMap.setLocationSource(this);// 设置定位监听
			 aMap.getUiSettings().setScaleControlsEnabled(true); // 显示地图缩放比例
			 aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		 }
	 }


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		setView();

		mapView.onCreate(savedInstanceState);// 此方法必须重写

		init();
	}

	private void setView() {
		top_center.setText("加油站查询");
		top_left.setVisibility(View.VISIBLE);
		top_right.setVisibility(View.VISIBLE);
		top_right.setCompoundDrawablesWithIntrinsicBounds(R.drawable.query_oil_list, 0, 0, 0);

	}

	@OnClick({ R.id.top_right, R.id.tv_1, R.id.tv_2, R.id.iv_1 })
	public void click(View v) {

		switch (v.getId()) {
		case R.id.top_right:
			Util.showIntent(context, OilPriceListActivity.class, new String[] { "info" },
					new Serializable[] { (Serializable) listAll });
			Log.e("汽油类型",listAll.get(0).getAreaname()+"!!!!"+listAll.get(0).getId());
			break;
		case R.id.tv_1:

			String city = et_1.getText().toString().trim();
			if (TextUtils.isEmpty(city)) {
				Util.show("请输入城市名");
				return;
			}
			getOilPriceByCity(city);

			break;
		case R.id.tv_2:

			break;
		case R.id.iv_1:
			AnimeUtil.startAnimation(context, v, R.anim.small_2_big, new OnAnimEnd() {

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

					Util.startVoiceRecognition(context, new DialogRecognitionListener() {

						@Override
						public void onResults(Bundle results) {
							ArrayList<String> rs = results != null ? results.getStringArrayList(RESULTS_RECOGNITION)
									: null;
							if (rs != null && rs.size() > 0) {

								et_1.setText(rs.get(0));

							}

						}
					});
				}
			});

			break;
		}

	}

	private void init() {

		markerList = new ArrayList<Marker>();

//		this.getApplicationContext().bindService(new Intent(OilPriceMainActivity.this, LocationService.class),
//				locationServiceConnection, Context.BIND_AUTO_CREATE);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		for (Marker m : markerList) {
			m.destroy();
			m.remove();
		}
		markerList.clear();

	}

	/**
	 * 监听自定义infowindow窗口的infocontents事件回调
	 */
	@Override
	public View getInfoContents(Marker marker) {
		return null;
	}

	/**
	 * 监听自定义infowindow窗口的infowindow事件回调
	 */
	@Override
	public View getInfoWindow(Marker marker) {
		// 加上这个判断是为了避免，开始导航时，路口marker的提示窗体InfoWindow不出来

		View infoWindow = getLayoutInflater().inflate(R.layout.query_oil_price_marker_window_dialog, null);

		render(marker, infoWindow);
		return infoWindow;

	}

	/**
	 * 自定义infowinfow窗口
	 */
	public void render(final Marker marker, View view) {
		TextView tv = ((TextView) view.findViewById(R.id.marker_title));

		final Data data = (Data) marker.getObject();

		tv.setText(data.getName());

		ImageView daohang = ((ImageView) view.findViewById(R.id.marker_daohang));
		daohang.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Util.showIntent(context, OilPriceMainActivity.class, new String[] { "dh", "from" },
						new Serializable[] { data, 1 });
			}
		});
	}

	private void doStartDaoHang(final LatLng endLatlng) {
		if (null == locationLatLng) {
			Util.show("正在获取您的位置，请稍等...");
			Util.asynTask(new IAsynTask() {
				@Override
				public void updateUI(Serializable runData) {
					if ("1".equals(runData + ""))
						doStartDH(endLatlng);
					else
						Util.show(runData + "");
				}

				@Override
				public Serializable run() {
					int i = 0;
					while (i < 30) {
						try {
							Thread.currentThread().sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						} finally {
							i++;
							if (null != locationLatLng)
								break;
						}
					}
					return null == locationLatLng ? "获取定位失败！无法导航。" : "1";
				}
			});
		} else
			doStartDH(endLatlng);
	}

	/**
	 * 开始导航
	 *
	 * @param endLatlng
	 *            目的地的经纬度
	 */
	private void doStartDH(LatLng endLatlng) {
		showProgressDialog();
		LatLng location = locationLatLng;
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
				/* 您的经纬度 */
				new LatLonPoint(location.latitude, location.longitude),
				/* 您的目的地 */
				new LatLonPoint(endLatlng.latitude, endLatlng.longitude));
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		if (routeType == 1) {// 公交路径规划
			busMode = RouteSearch.BusDefault;
			BusRouteQuery query = new BusRouteQuery(fromAndTo, busMode, cityCode, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
			routeSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
		} else if (routeType == 2) {// 驾车路径规划
			DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
			routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
		} else if (routeType == 3) {// 步行路径规划
			WalkRouteQuery query = new WalkRouteQuery(fromAndTo, walkMode);
			routeSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
		}
	}

	private void addMarker(final Data info) {
	
		String name = "";
		String lat = "";
		String lon = "";
		String city = "";

		lat = info.getLat();
		lon = info.getLon();
		city = info.getAreaname();
		name = info.getName();
		Log.e("地址数据",info.getName());
		if (!Util.isDouble(lon) || !Util.isDouble(lat))
			return;

		synchronized (context) {
			List<Marker> mList = new ArrayList<Marker>();
			for (Marker marker : markerList) {
				Object tempM = marker.getObject();
				if (null != tempM) {
					if (tempM instanceof Data) {
						Data infoM = (Data) tempM;

						if (info.getId().equals(infoM.getId()))
							return;
					}

				} else {
					mList.add(marker);
					marker.destroy();
					marker.remove();
				}
			}
			if (0 != mList.size())
				markerList.removeAll(mList);
			mList.clear();
			mList = null;
		}
		LatLng latlng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));

		BitmapDescriptor icon = null;
		// if (cateBitmap.containsKey(1)) {
		// icon = cateBitmap.get(1);
		// } else {
		TextDrawable up = TextDrawable.builder().beginConfig().fontSize(20).endConfig().buildRound(info.getPriceTemp(),
				context.getResources().getColor(R.color.yellow));

		Drawable down = context.getResources().getDrawable(R.drawable.query_oil_local);

		Drawable drawable = getLayerDrawable(down, up);

		Bitmap bitmap = Util.drawable2Bitmap(drawable);

		icon = BitmapDescriptorFactory.fromBitmap(bitmap);
		// cateBitmap.put(1, icon);
		// }

		Marker marker = aMap.addMarker(new MarkerOptions().position(latlng).visible(true).title(name).snippet(city)
				.perspective(true).icon(icon));
		marker.setObject(info);
		markerList.add(marker);
	}

	/**
	 * 开始定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;

	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
	}

	/**
	 * 监听amap地图加载成功事件回调
	 */
	@Override
	public void onMapLoaded() {
		Log.e("监听amap","地图加载成功事件回调");
		if (mListener != null) {

//			locationLatLng = locationService.getLocationLatlng();
//			try {
//				cityCode = locationService.getCityCode();
//			}catch (Exception e){
//				Log.e("定位失败",e.toString());
//				cityCode="";
//			}
//
//
//			mListener.onLocationChanged(locationService.getLocation());// 显示系统小蓝点
			// final String urlid = this.getIntent().getStringExtra("dh");

			Intent intent = getIntent();
			if (intent.hasExtra("from")) {




				ll.setVisibility(View.GONE);
				top_right.setVisibility(View.GONE);
				if (intent.hasExtra("dh")) {
					Log.e("intent","dh");
					Data data = (Data) intent.getSerializableExtra("dh");
					addMarker(data);
					showDHdialog(data);
				} else if (intent.hasExtra("list")) {
					Log.e("intent","list");
					List<Data> listData = (List<Data>) intent.getSerializableExtra("list");
					listAll.addAll(listData);
					for (Data m : listAll)
						addMarker(m);
					LatLng latlng = locationLatLng;
					aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));

				}

			} else {
				if (!isLoad) {
					isLoad = true;
					try{
						getOilPriceByLat(locationLatLng.longitude, locationLatLng.latitude);
						Log.e("isfrist",isfrist+"");

						if (isfrist) {
//							et_1.setText(locationService.getCity());
//							getOilPriceByCity(locationService.getCity());
							isfrist=false;
						}
					}catch(Exception e){

						if (locationService==null){

							com.mall.util.Util.show("请打开定位权限...");
							try {
								Uri packageURI = Uri.parse("package:" + context.getPackageName());
								Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
								startActivity(intent1);
							} catch (Exception e2) {
								// TODO: handle exception
							}
							finish();
						}


						
					}
					


				
				}
			}

		}
	}

	@Override
	public void onInfoWindowClick(Marker marker) {

		if (null != marker.getObject()) {
			if (marker.getObject() instanceof Data) {
				Data data = (Data) marker.getObject();
				Util.showIntent(context, OilPriceDetailActivity.class, new String[] { "data" },
						new Serializable[] { data });
			}

		}

	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		if (marker.isInfoWindowShown())
			marker.hideInfoWindow();
		else
			marker.showInfoWindow();
		currShowMarker = marker;
		return false;
	}

	@Override
	public void onCameraChange(CameraPosition cp) {
		// loadMoreShopM();
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cp) {

	}

	/**
	 * 隐藏进度框
	 */
	private void dissmissProgressDialog() {
		if (progDialog != null) {
			progDialog.dismiss();
		}
	}

	/**
	 * 显示进度框
	 */
	private void showProgressDialog() {
		if (progDialog == null)
			progDialog = new ProgressDialog(this);
		progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progDialog.setIndeterminate(false);
		progDialog.setCancelable(true);
		progDialog.setMessage("正在搜索");
		try {
			progDialog.show();
		} catch (Exception e) {

		}
	}

	/**
	 * 公交路线查询回调
	 */
	@Override
	public void onBusRouteSearched(BusRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				BusRouteResult busRouteResult = result;
				BusPath busPath = busRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物
//				BusRouteOverlay routeOverlay = new BusRouteOverlay(this, aMap, busPath, busRouteResult.getStartPos(),
//						busRouteResult.getTargetPos());
//				routeOverlay.removeFromMap();
//				routeOverlay.addToMap();
//				routeOverlay.zoomToSpan();
			} else {
				Util.show("对不起，没有搜索到相关数据！");
			}
		} else {
			Util.show("搜索失败,请检查网络连接！");
		}
	}

	/**
	 * 驾车结果回调
	 */
	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				DriveRouteResult driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物

//				DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(this, aMap, drivePath,
//						driveRouteResult.getStartPos(), driveRouteResult.getTargetPos());
//				drivingRouteOverlay.removeFromMap();
//				drivingRouteOverlay.addToMap();
//				drivingRouteOverlay.zoomToSpan();
			} else {
				Util.show("对不起，没有搜索到相关数据！");
			}
		} else {
			Util.show("搜索失败,请检查网络连接！");
		}
	}

	/**
	 * 步行路线结果回调
	 */
	@Override
	public void onWalkRouteSearched(WalkRouteResult result, int rCode) {
		dissmissProgressDialog();
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				WalkRouteResult walkRouteResult = result;
				WalkPath walkPath = walkRouteResult.getPaths().get(0);
				aMap.clear();// 清理地图上的所有覆盖物

//				WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(this, aMap, walkPath,
//						walkRouteResult.getStartPos(), walkRouteResult.getTargetPos());
//				walkRouteOverlay.removeFromMap();
//				walkRouteOverlay.addToMap();
//				walkRouteOverlay.zoomToSpan();
			} else {
				Util.show("对不起，没有搜索到相关数据！");
			}
		} else {
			Util.show("搜索失败,请检查网络连接！");
		}
	}

	@Override
	public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

	}

	@Override
	public void onMapClick(LatLng l) {
		if (null != currShowMarker && currShowMarker.isVisible())
			currShowMarker.hideInfoWindow();
	}

//	public LocationService getLocationService() {
//		return locationService;
//	}

	private void getOilPriceByCity(String city) {
		String url = "http://apis.juhe.cn/oil/region?key=2b40e756b00a23f9ac5d1fa53d32dbf0&format=2&city=" + city;
		oilpriceQuery(url, true);
	}

	private void getOilPriceByLat(double lon, double lat) {
		String url = "http://apis.juhe.cn/oil/local?key=2b40e756b00a23f9ac5d1fa53d32dbf0&lon=" + lon + "&lat=" + lat
				+ "&format=2&r=3000";
		oilpriceQuery(url, false);

	}

	List<Data> listAll = new ArrayList<OilPriceInfo.Data>();

	private void oilpriceQuery(final String url, final boolean isCity) {
		Util.asynTask(new IAsynTask() {

			@Override
			public void updateUI(Serializable runData) {
				// TODO Auto-generated method stub
				Map<String, String> map = JsonUtil.getNewApiJsonQuery(runData.toString());
				String message = map.get("message");
				if (map.get("code").equals("200")) {
					String lists = map.get("list");

					OilPriceInfo info = JsonUtil.getPerson(lists, OilPriceInfo.class);
					if (info == null) {
						return;
					}

					List<Data> mlist = info.getData();
					if (mlist == null) {
						return;
					}

					if (locationLatLng!=null) {
						for (int i = 0; i < mlist.size(); i++) {
//							getOilPriceByLat(locationLatLng.longitude, locationLatLng.latitude);
							double lon=Double.parseDouble(mlist.get(i).getLon());     
							double lat=Double.parseDouble(mlist.get(i).getLat());     
							
							
							mlist.get(i).setDistance((int)GetDistance(locationLatLng.longitude,locationLatLng.latitude,lon,lat)+"");
							
							Log.e("经纬度","本地经纬度"+locationLatLng.longitude+"."+locationLatLng.latitude+"查询经纬度"+lon+"."+lat);
							
							
											
							
						}
					}
					
					listAll.clear();    
					
					
					listAll.addAll(mlist);

					for (Data m : listAll) {
						List<Price> lp = m.getPrice();
						if (lp != null && lp.size() > 0) {
							m.setPriceTemp(lp.get(0).getPrice());
						}
						addMarker(m);
					}
					if (!isCity) {
						LatLng latlng = locationLatLng;
						aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 16));
					} else {
						if (0 < listAll.size()) {
							Data m = mlist.get(0);
							LatLng remote = new LatLng(Util.getDouble(m.getLat()), Util.getDouble(m.getLon()));
							aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(remote, 14));
						}
					}

				} else {

				}
			}

			@Override
			public Serializable run() {
				// TODO Auto-generated method stub
				Web web = new Web(url);
				return web.getPlan();
			}
		});
	}

	public LayerDrawable getLayerDrawable(Drawable down, Drawable up) {
		Drawable[] layerList = new Drawable[2];
		layerList[0] = down;
		layerList[1] = up;

		LayerDrawable layerDrawable = new LayerDrawable(layerList);
		layerDrawable.setLayerInset(1, 20, 20, 20, 40);
		return layerDrawable;
	}

	private void showDHdialog(Data data) {

		final String mName = data.getName();
		final String latStr = data.getLat();
		final String lonStr = data.getLon();
		final LatLng remote = new LatLng(Util.getDouble(data.getLat()), Util.getDouble(data.getLon()));
		final LatLonPoint  mEndPoint = new LatLonPoint(Util.getDouble(data.getLat()),Util.getDouble(data.getLon()));
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("请选择导航模式");
		builder.setPositiveButton("驾车", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(final DialogInterface dialog, int which) {
				boolean isBaidu = Util.isInstall(

						"com.baidu.BaiduMap");
				boolean isGaoDe = Util.isInstall(

						"com.autonavi.minimap");
				boolean isGoogle = Util.isInstall(

						"com.google.android.apps.maps");
				AlertDialog builder2 = new Builder(context).create();
				builder2.setTitle("请选择导航软件！");
				LinearLayout root = new LinearLayout(context);
				root.setOrientation(LinearLayout.VERTICAL);
				Resources res = context.getResources();
				LinearLayout.LayoutParams ll = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				int _5dp = Util.dpToPx(5F);
				ll.setMargins(_5dp, _5dp, _5dp, _5dp);
				if (isBaidu) {
					TextView baiduMap = new TextView(context);
					baiduMap.setLayoutParams(ll);
					Drawable dra = res.getDrawable(R.drawable.baidu);
					dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
					baiduMap.setCompoundDrawables(dra, null, null, null);
					baiduMap.setText("百度地图");
					baiduMap.setGravity(Gravity.CENTER_VERTICAL);
					baiduMap.setOnClickListener(new OnClickListener() {
						@SuppressWarnings("static-access")
						@Override
						public void onClick(View v) {
							String lat = "";
							String lng = "";
//							lat = locationService.getLocationLatlng().latitude + "";
//							lng = locationService.getLocationLatlng().longitude + "";
//							try {
//								@SuppressWarnings({ "deprecation", "static-access" })
//								Intent intent = Intent.getIntent("baidumap://map/direction?origin=latlng:" + lat + ","
//										+ lng + "|name:当前位置&destination=latlng:" + latStr + "," + lonStr + "|name:"
//										+ mName + " &mode=driving&region=" + locationService.getCity());
//								context.startActivity(intent);
//							} catch (URISyntaxException e) {
//								e.printStackTrace();
//							}
						}
					});
					root.addView(baiduMap);
				}
				if (isGaoDe) {
					TextView gaodeMap = new TextView(context);
					gaodeMap.setLayoutParams(ll);
					Drawable dra = res.getDrawable(R.drawable.gaode);
					dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
					gaodeMap.setCompoundDrawables(dra, null, null, null);
					gaodeMap.setText("高德地图");
					gaodeMap.setGravity(Gravity.CENTER_VERTICAL);
					gaodeMap.setOnClickListener(new OnClickListener() {
						@SuppressWarnings("deprecation")
						@Override
						public void onClick(View v) {
							try {
								Intent intent = Intent
										.getIntent("androidamap://navi?sourceApplication=mall666&poiname=&poiid=&lat="
												+ latStr + "&lon=" + lonStr + "&dev=1&style=0");
								context.startActivity(intent);
							} catch (URISyntaxException e) {
								e.printStackTrace();
							}
						}
					});
					root.addView(gaodeMap);
				}
				if (isGoogle) {
					TextView gugeMap = new TextView(context);
					gugeMap.setLayoutParams(ll);
					Drawable dra = res.getDrawable(R.drawable.guge);
					dra.setBounds(0, 0, dra.getMinimumWidth(), dra.getMinimumWidth());
					gugeMap.setCompoundDrawables(dra, null, null, null);
					gugeMap.setText("Google地图");
					gugeMap.setGravity(Gravity.CENTER_VERTICAL);
					gugeMap.setOnClickListener(new OnClickListener() {
						@SuppressWarnings("static-access")
						@Override
						public void onClick(View v) {
							String lat = "";
							String lng = "";
//							lat = locationService.getLocationLatlng().latitude + "";
//							lng = locationService.getLocationLatlng().longitude + "";
							Intent i = new Intent(Intent.ACTION_VIEW,
									Uri.parse("http://ditu.google.cn/maps?f=d&source=s_d&saddr=" + lat + "," + lng
											+ "&daddr=" + latStr + "," + lonStr + "&hl=zh"));
							i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK & Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
							i.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
							context.startActivity(i);
						}
					});
					root.addView(gugeMap);
				}
				if (!isBaidu && !isGoogle && !isGaoDe) {
					// Util.showIntent("您还没有安装地图导航软件，是否使用系统路径规划？",
					// context,
					// NearbyPersonActivity.class, null,
					// new String[] { "dh", "mode" },
					// new String[] { m.getId(), "2" });
				} else {
					builder2.setView(root);
					builder2.show();
				}
			}
		});
		builder.setNegativeButton("步行", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				Intent intent=new Intent(context,WalkRouteActivity.class);

				LocationModel locationModel=LocationModel.getLocationModel();


				intent.putExtra("mEndPoint",mEndPoint);
				intent.putExtra("mEndPoint",mEndPoint);
				startActivity(intent);

				routeType = 3;
				doStartDaoHang(remote);
			}
		});
		builder.setNeutralButton("公交", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				routeType = 1;
				doStartDaoHang(remote);
			}
		});
		builder.show();

	}
	
	private static final  double EARTH_RADIUS = 6378137;//赤道半径
	private static double rad(double d){
	    return d * Math.PI / 180.0;
	}
	public static double GetDistance(double lon1,double lat1,double lon2, double lat2) {

		
		try {
		    double radLat1 = rad(lat1);
		    double radLat2 = rad(lat2);
		    double a = radLat1 - radLat2;
		    double b = rad(lon1) - rad(lon2);
		    double s = 2 *Math.asin(Math.sqrt(Math.pow(Math.sin(a/2),2)+Math.cos(radLat1)*Math.cos(radLat2)*Math.pow(Math.sin(b/2),2))); 
		    s = s * EARTH_RADIUS;    
		   return s;//单位米
		} catch (Exception e) {
			// TODO: handle exception
		}
		return 0.00;
	
	}



}
