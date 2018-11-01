package com.mall.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.model.HotelOrdersBean;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.BitmapUtils;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

public class HotelOrdersMsg extends Activity {

	private TextView topBack;
	private TextView topUserName;
	private ImageView UserPic;
	private TextView inTime;
	private TextView outTime;
	private TextView lastTime;
	private TextView UserName;
	private TextView UserPhone;
	private TextView UserEMail;
	private TextView UserNeed;
	private TextView UserHotelName;
	private TextView UserHotelAddress;
	private TextView UserHotelEmail;
	private TextView UserHotelNeed;

	private List<HotelOrdersBean> orderMsgList = new ArrayList<HotelOrdersBean>();
	HotelOrdersBean data = new HotelOrdersBean();
	private String orderId = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_orders_hotels);
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		findView();
		getInten();
		getInfo();
		setInfo();
		setListener();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		topBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}

	private void getInten() {
		// TODO Auto-generated method stub
		orderId = this.getIntent().getStringExtra("orderId");
		data = (HotelOrdersBean) this.getIntent().getSerializableExtra("data");
	}

	private void getInfo() {
		if (!Util.isNull(orderId)) {

			Util.asynTask(this, "正在加载数据", new IAsynTask() {
				@SuppressWarnings("unchecked")
				@Override
				public void updateUI(Serializable runData) {
					if (runData != null) {
						orderMsgList = new ArrayList<HotelOrdersBean>();
						orderMsgList = ((HashMap<Integer, List<HotelOrdersBean>>) runData)
								.get(0);

						if (null != orderMsgList && orderMsgList.size() > 0) {
							UserName.setText(orderMsgList.get(0).getLinkName());
							UserPhone.setText(orderMsgList.get(0).getPhone());
							UserEMail.setText(orderMsgList.get(0).getMail());

							UserHotelName.setText(orderMsgList.get(0)
									.getHotelName());
							UserHotelAddress.setText(orderMsgList.get(0)
									.getHotelAddress());

						}

					} else {
						Toast.makeText(HotelOrdersMsg.this,
								"操作失败，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG)
								.show();
					}
				}

				@SuppressLint("UseSparseArrays")
				@Override
				public Serializable run() {
					Web web = new Web(Web.getServiceHotelOrderDetail, "userid="
							+ UserData.getUser().getUserId() + "&md5Pwd="
							+ UserData.getUser().getMd5Pwd() + "&orderId="
							+ orderId);
					List<HotelOrdersBean> list = web
							.getList(HotelOrdersBean.class);
					HashMap<Integer, List<HotelOrdersBean>> map = new HashMap<Integer, List<HotelOrdersBean>>();
					map.put(0, list);
					return map;
				}

			});
		} else {
			Util.show("查询出错，请重试！", HotelOrdersMsg.this);

		}
	}

	private void setInfo() {
		// TODO Auto-generated method stub
		if (!Util.isNull(data)) {
			topUserName.setText(data.getLinkman());

			inTime.setText(data.getIntime());
			outTime.setText(data.getOuttime());
			lastTime.setText(data.getLasttime());
		}

		UserNeed.setText("暂无");
		UserHotelEmail.setText("暂无");
		UserHotelNeed.setText("暂无");
		// UserPic
		User user = UserData.getUser();
		if (null != user && !Util.isNull(user.getUserFace())) {
			BitmapUtils.loadBitmap(user.getUserFace(), UserPic);
		}
	}

	private void findView() {
		// TODO Auto-generated method stub

		topBack = (TextView) findViewById(R.id.top_back);
		topUserName = (TextView) findViewById(R.id.dialog_hotel_order_topuser_name);

		UserPic = (ImageView) this
				.findViewById(R.id.dialog_hotel_order_userpic);
		inTime = (TextView) findViewById(R.id.intime);
		outTime = (TextView) findViewById(R.id.outtime);
		lastTime = (TextView) findViewById(R.id.lasttime);
		UserName = (TextView) findViewById(R.id.dialog_hotelorder_LinkName);
		UserPhone = (TextView) findViewById(R.id.user_phones);
		UserEMail = (TextView) findViewById(R.id.mail);
		UserNeed = (TextView) this
				.findViewById(R.id.dialog_hotel_order_userNeed);
		UserHotelName = (TextView) findViewById(R.id.hotelName);
		UserHotelAddress = (TextView) findViewById(R.id.hoteladdress);
		UserHotelEmail = (TextView) this
				.findViewById(R.id.dialog_hotel_order_hotel_email);
		UserHotelNeed = (TextView) this
				.findViewById(R.id.dialog_hotel_order_hotelNeed);
	}

}
