package com.mall.view;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.MessageEvent;
import com.mall.adapter.FragAdapter;
import com.mall.model.PhoneArea;
import com.mall.serving.resturant.ResturantIndex;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.topupcenter.QQGameRechargeFragment;
import com.mall.view.topupcenter.TelephoneRechargeFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;


@ContentView(R.layout.phone_fream)
public class PhoneFream extends AppCompatActivity {
//	private EditText phoneEt = null;
	private PhoneArea pa;
	private ArrayList<String> latestPhones = new ArrayList<String>();
	String items[] = { "50", "100", "300" };
	private String unum = "";

	String temp = "";
	private String money;
	private int isFirstClick = 1;
	private ImageView phone_charge_dhb;
	private String phonename;
	private String phonenumber;
//	private Button btnmo;
	private String phone_charge_addr;
	private TextView show_phone_charge_addr;
//	private RadioGroup radioGroups;

	// private TextView phone_charge_quxiao;
	private Button phone_charge_shape_no;
	private ImageView charge_phone_clear;
	private VoipDialog voipDialog;
	private LinearLayout phone_charge_money50;
	private LinearLayout phone_charge_money100;
	private LinearLayout phone_charge_money300;

	// 赵超修改
	private TextView tv_qq;// Q币充值
	private TextView tv_game;// 网游充值
	private TextView tv_hotel;// 酒店预订

	private TextView checkmyphone;// 检查是不是账户手机
	
	private TextView tv50,tv49,tv100,tv99,tv300,tv299;

	TelephoneRechargeFragment telephoneRecharge;
    QQGameRechargeFragment qqGameRechargeFragment;
    QQGameRechargeFragment GameRechargeFragment;



@ViewInject(R.id.contentviewpager)
	private ViewPager contentviewpager;

	@ViewInject(R.id.phoneiv)
	private ImageView phoneiv;
	@ViewInject(R.id.qqiv)
	private ImageView qqiv;
	@ViewInject(R.id.gameiv)
	private ImageView gameiv;

	@ViewInject(R.id.phonetv)
	private TextView phonetv;

	@ViewInject(R.id.qqtv)
	private TextView qqtv;

	@ViewInject(R.id.gametv)
	private TextView gametv;

	@ViewInject(R.id.phoneline)
	private View phoneline;

	@ViewInject(R.id.qqline)
	private View qqline;

	@ViewInject(R.id.gameline)
	private View gameline;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		ViewUtils.inject(this);
		// 判断用户是否登录
		Util.initTop(this, "充值中心", Integer.MIN_VALUE, null);
		unum = this.getIntent().getStringExtra("unum");
		intView();
		intListener();
		String phone=this.getIntent().getStringExtra("phone");

		Bundle bundle = new Bundle();
		telephoneRecharge = new TelephoneRechargeFragment();
        qqGameRechargeFragment=  QQGameRechargeFragment.newInstance("-1");
		GameRechargeFragment=QQGameRechargeFragment.newInstance("-2");
		if (!Util.isNull(phone)){

//			phoneEt.setText(phone);

			bundle.putString("phone",phone);

		}


		bundle.putString("unum",unum);
		telephoneRecharge.setArguments(bundle);
        qqGameRechargeFragment.setArguments(bundle);
		GameRechargeFragment.setArguments(bundle);


		//构造适配器
		List<Fragment> fragments=new ArrayList<Fragment>();
		fragments.add(telephoneRecharge);
		fragments.add(qqGameRechargeFragment);
		fragments.add(GameRechargeFragment);
		FragAdapter fragAdapter = new FragAdapter(getSupportFragmentManager(), fragments);

		contentviewpager.setAdapter(fragAdapter);

	}

	private void checkstate(int position){


		phoneiv.setImageResource(R.drawable.phoneuncheck);
		qqiv.setImageResource(R.drawable.qquncheck);
		gameiv.setImageResource(R.drawable.gameuncheck);
		phonetv.setTextColor(Color.parseColor("#A6A6A6"));
		qqtv.setTextColor(Color.parseColor("#A6A6A6"));
		gametv.setTextColor(Color.parseColor("#A6A6A6"));
		phoneline.setVisibility(View.INVISIBLE);
		qqline.setVisibility(View.INVISIBLE);
		gameline.setVisibility(View.INVISIBLE);
		switch (position){
			case 0:
				phoneiv.setImageResource(R.drawable.phonecheck);
				phonetv.setTextColor(Color.parseColor("#FF3A5A"));
				phoneline.setVisibility(View.VISIBLE);
				break;
			case 1:
				qqiv.setImageResource(R.drawable.qqcheck);
				qqtv.setTextColor(Color.parseColor("#FF3A5A"));
				qqline.setVisibility(View.VISIBLE);
				break;
			case 2:
				gameiv.setImageResource(R.drawable.gamecheck);
				gametv.setTextColor(Color.parseColor("#FF3A5A"));
				gameline.setVisibility(View.VISIBLE);
				break;
		}



	}

	private void intListener() {
		contentviewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {

				Log.e("TAG", "onPageSelected------>"+position);
				if (position==1){
					qqGameRechargeFragment.cid="-1";
				}else if(position==2){
					GameRechargeFragment.cid="-2";
				}
				checkstate(position);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		// 赵超修改,Q币充值按钮点击事件
		tv_qq.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PhoneFream.this, GameFrame.class);
				intent.putExtra("unum", unum);
				intent.putExtra("cid", "-1");
				startActivity(intent);
			}
		});
		// 赵超修改，网游充值按钮点击事件
		tv_game.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PhoneFream.this, GameFrame.class);
				intent.putExtra("unum", unum);
				intent.putExtra("cid", "-2");
				startActivity(intent);
			}
		});
		// 赵超修改，酒店预订点击事件
		tv_hotel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(PhoneFream.this, ResturantIndex.class);
				intent.putExtra("unum", unum);
				startActivity(intent);
			}
		});

		phone_charge_dhb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivityForResult(new Intent(Intent.ACTION_PICK,
						ContactsContract.Contacts.CONTENT_URI), 0);

			}
		});






	}

	private void intView() {

		// 赵超修改
		tv_qq = (TextView) findViewById(R.id.tv_qq);
		tv_game = (TextView) findViewById(R.id.tv_game);
		tv_hotel = (TextView) findViewById(R.id.tv_hotel);
	
		tv50=(TextView) findViewById(R.id.tv50);
		tv49=(TextView) findViewById(R.id.tv49);
		tv100=(TextView) findViewById(R.id.tv100);
		tv99=(TextView) findViewById(R.id.tv99);
		tv300=(TextView) findViewById(R.id.tv300);
		tv299=(TextView) findViewById(R.id.tv299);
		
		checkmyphone = (TextView) findViewById(R.id.checkmyphonetv);

		phone_charge_dhb = (ImageView) findViewById(R.id.phone_charge_dhb);
		charge_phone_clear = (ImageView) findViewById(R.id.charge_phone_clear);


		
		String	phonenumber1	=UserData.getUser().getMobilePhone().replaceAll("\\s*", ""); 
		Log.e("数据是2",phonenumber1);
		String phonenum2 ="";
		try {
			 phonenum2 = phonenumber1.substring(0, 3) + " "
					+ phonenumber1.substring(3, 7) + " "
					+ phonenumber1.substring(7, 11);
		} catch (Exception e) {
			// TODO: handle exception
			phonenum2=phonenumber1;
		}


		
//		phoneEt.setText(UserData.getUser().getMobilePhone());

		checkmyphone.setVisibility(View.VISIBLE);
		show_phone_charge_addr = (TextView) findViewById(R.id.show_phone_charge_addr);


	}



	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("回调","1");
		super.onActivityResult(requestCode, resultCode, data);
		Log.e("回调","2"+requestCode+"!!!!!"+resultCode);
		if (resultCode == AppCompatActivity.RESULT_OK) {
			// ContentProvider展示数据类似一个单个数据库表
			// ContentResolver实例带的方法可实现找到指定的ContentProvider并获取到ContentProvider的数据
			ContentResolver reContentResolverol = getContentResolver();
			// URI,每个ContentProvider定义一个唯一的公开的URI,用于指定到它的数据集
			Uri contactData = data.getData();
			// 查询就是输入URI等参数,其中URI是必须的,其他是可选的,如果系统能找到URI对应的ContentProvider将返回一个Cursor对象.
			Cursor cursor = managedQuery(contactData, null, null, null, null);
			cursor.moveToFirst();
			// 获得DATA表中的名字
			phonename = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
			// 条件为联系人ID
			String contactId = cursor.getString(cursor
					.getColumnIndex(ContactsContract.Contacts._ID));
			// 获得DATA表中的电话号码，条件为联系人ID,因为手机号码可能会有多个
			Cursor phone = reContentResolverol.query(
					ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
					ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = "
							+ contactId, null, null);
			while (phone.moveToNext()) {
				phonenumber = phone
						.getString(phone
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
				
//				137 3068 2321
				Log.e("数据是1",phonenumber);
			String	phonenumber1	=phonenumber.replaceAll("\\s*", ""); 
				Log.e("数据是2",phonenumber1);
				String phonenum2 ="";
				try {
					 phonenum2 = phonenumber1.substring(0, 3) + " "
							+ phonenumber1.substring(3, 7) + " "
							+ phonenumber1.substring(7, 11);
				} catch (Exception e) {
					// TODO: handle exception
					phonenum2=phonenumber1;
				}
				EventBus.getDefault().post(new MessageEvent(phonenum2));

			}

		}
	}

	@OnClick({R.id.bn1,R.id.bn2,R.id.bn3})
	private void  click(View view){
		switch (view.getId()){
			case R.id.bn1:
				contentviewpager.setCurrentItem(0,true);
				break;
			case R.id.bn2:
				contentviewpager.setCurrentItem(1,true);
				break;
			case R.id.bn3:
				contentviewpager.setCurrentItem(2,true);
				break;
		}
	}



}
