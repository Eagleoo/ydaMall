package com.mall.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CardExchangeRequest;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;
import com.mall.yyrg.adapter.AsyncImageLoader;
import com.mall.yyrg.adapter.ImageCacheManager;

/**
 * 交换名片请求
 * 
 * @author admin
 * 
 * 
 */
public class CardExchangeCardRequest extends Activity {
	@ViewInject(R.id.listView1)
	private ListView listView1;
	private List<CardExchangeRequest> cardLinkmans = new ArrayList<CardExchangeRequest>();
	private int currentPageShop = 0;
	private CardExchangeCardRequestAdapter cardExchangeCardRequestAdapter;
	@ViewInject(R.id.shoudao_qingqiu)
	private TextView shoudao_qingqiu;
	@ViewInject(R.id.faqi_qingqiu)
	private TextView faqi_qingqiu;
	private int state = 0;// 0为收到的请求，1是发起的请求
	private int width;
	private Dialog dialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_exchange_card_request);
		ViewUtils.inject(this);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		
	}
@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();
	if (cardExchangeCardRequestAdapter == null) {
		cardExchangeCardRequestAdapter = new CardExchangeCardRequestAdapter();
		listView1.setAdapter(cardExchangeCardRequestAdapter);
	}
	firstpageshop();
	scrollPageshop();
}
	@OnClick({ R.id.faqi_qingqiu, R.id.shoudao_qingqiu })
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.faqi_qingqiu:
			state = 1;
			cardLinkmans.clear();
			changeColor(faqi_qingqiu);
			currentPageShop = 0;
			cardExchangeCardRequestAdapter = new CardExchangeCardRequestAdapter();
			listView1.setAdapter(cardExchangeCardRequestAdapter);
			firstpageshop();
			scrollPageshop();
			break;

		case R.id.shoudao_qingqiu:
			cardLinkmans.clear();
			state = 0;
			currentPageShop = 0;
			cardExchangeCardRequestAdapter = new CardExchangeCardRequestAdapter();
			listView1.setAdapter(cardExchangeCardRequestAdapter);
			firstpageshop();
			scrollPageshop();
			changeColor(shoudao_qingqiu);
			break;
		}
	}

	private void changeColor(TextView view) {
		faqi_qingqiu.setTextColor(getResources().getColor(
				R.color.yyrg_shouye_zi));
		shoudao_qingqiu.setTextColor(getResources().getColor(
				R.color.yyrg_shouye_zi));
		view.setTextColor(getResources().getColor(R.color.bg));
		faqi_qingqiu.setBackgroundColor(getResources().getColor(R.color.bg));
		shoudao_qingqiu.setBackgroundColor(getResources().getColor(R.color.bg));
		view.setBackgroundColor(getResources().getColor(R.color.headertop));
	}

	public void firstpageshop() {
		getAllreceiveUserNameCardShare();
	}

	public void scrollPageshop() {
		listView1.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= cardExchangeCardRequestAdapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					getAllreceiveUserNameCardShare();
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}

	private void setImageWidthAndHeight(ImageView imageView, int state,
			int width, int height) {
		if (state == 1) {
			LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) imageView
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			imageView.getLayoutParams();
		}
		if (state == 2) {
			RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) imageView
					.getLayoutParams(); // 取控件textView当前的布局参数
			linearParams.width = width;
			linearParams.height = height;
			imageView.getLayoutParams();
		}

	}

	@OnClick(R.id.top_back)
	public void back(View view) {
		finish();
	}

	public class CardExchangeCardRequestAdapter extends BaseAdapter {
		private LayoutInflater inflater;
		private List<CardExchangeRequest> linkmans = new ArrayList<CardExchangeRequest>();
		private AsyncImageLoader imageLoader;

		public CardExchangeCardRequestAdapter() {
			inflater = LayoutInflater.from(CardExchangeCardRequest.this);
			ImageCacheManager cacheMgr = new ImageCacheManager(
					CardExchangeCardRequest.this);
			imageLoader = new AsyncImageLoader(CardExchangeCardRequest.this,
					cacheMgr.getMemoryCache(), cacheMgr.getPlacardFileCache());
		}

		public void setList(List<CardExchangeRequest> linkmans) {
			this.linkmans.addAll(linkmans);
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return linkmans.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return linkmans.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.card_exchange_card_request_item, null);
			}
			ImageView user_img = (ImageView) convertView
					.findViewById(R.id.user_img);
			TextView city = (TextView) convertView.findViewById(R.id.city);
			final TextView username = (TextView) convertView
					.findViewById(R.id.username);
			TextView exchange = (TextView) convertView
					.findViewById(R.id.exchange);
			final TextView message = (TextView) convertView
					.findViewById(R.id.message);

			setImageWidthAndHeight(user_img, 1, width * 1 / 5, width * 1 / 5);
			ImageView imageView = new ImageView(CardExchangeCardRequest.this);
			city.setVisibility(View.GONE);
			message.setText(linkmans.get(position).getDuty() + "\t"
					+ linkmans.get(position).getCompanyName());
			Bitmap bmp = imageLoader.loadBitmap(imageView,
					linkmans.get(position).getUserFace(), true, width * 1 / 5,
					width * 1 / 5);
			if (bmp == null) {
				user_img.setImageResource(R.drawable.addgroup_item_icon);
			} else {
				user_img.setImageBitmap(bmp);
			}
			Bitmap bitmap = ((BitmapDrawable) user_img.getDrawable())
					.getBitmap();
			bitmap = Util.toRoundCorner(bitmap, 10);
			user_img.setImageBitmap(bitmap);
			if (state == 0) {
				username.setText(linkmans.get(position).getUserid());
			}else {
				exchange.setText(linkmans.get(position).getState());
				exchange.setTextColor(getResources().getColor(
						R.color.card_qianhei));
				exchange.setBackgroundColor(getResources().getColor(R.color.bg));
				username.setText(linkmans.get(position).getTouserid());
			}

			exchange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (state==0) {
						agreeOrRefuse(1, linkmans.get(position).getId());
					}
					
				}
			});
			convertView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (state==0) {
						/**
						 * name=getIntent().getStringExtra("name");
							cardId=getIntent().getStringExtra("id");
							image=getIntent().getStringExtra("image");
							messages=getIntent().getStringExtra("messages");
						 */
						Intent intent=new Intent(CardExchangeCardRequest.this, CardExchangeCardMessage.class);
						intent.putExtra("name", username.getText().toString().trim());
						intent.putExtra("id", linkmans.get(position).getId());
						intent.putExtra("messages", message.getText().toString().trim());
						intent.putExtra("image", linkmans.get(position).getUserFace());
						startActivity(intent);
					}
				}
			});
			convertView.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// TODO Auto-generated method stub
					tishi(linkmans.get(position).getId());
					return false;
				}
			});
			return convertView;
		}

	}

	/**
	 * 获得用户收到/发起的交换名片的请求
	 */
	private void getAllreceiveUserNameCardShare() {
		final User user = UserData.getUser();
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardExchangeCardRequest.this,
								"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Gson gson = new Gson();
								List<CardExchangeRequest> list = new ArrayList<CardExchangeRequest>();
								list = gson.fromJson(
										map.get("list"),
										new TypeToken<List<CardExchangeRequest>>() {
										}.getType());
								List<CardExchangeRequest> list1 = new ArrayList<CardExchangeRequest>();
								if (state==0) {
									for (int i = 0; i < list.size(); i++) {
										if (list.get(i).getState().equals("等待对方同意")) {
											list1.add(list.get(i));
										}
									}
									cardLinkmans.addAll(list1);
									cardExchangeCardRequestAdapter.setList(list1);	
								}else {
									cardLinkmans.addAll(list);
									cardExchangeCardRequestAdapter.setList(list);	
								}
								
							} else {
								Toast.makeText(CardExchangeCardRequest.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardExchangeCardRequest.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardExchangeCardRequest.this,
							"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				// 获取自己发送的
				if (state == 1) {
					web = new Web(1, Web.bBusinessCard,
							Web.getAllInitiateUserNameCardShare,
							"getAllInitiateUserNameCardShare", "userId="
									+ user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&pagesize=20"
									+ "&curpage=" + (++currentPageShop));
				} else {// 获取接收到的
					web = new Web(1, Web.bBusinessCard,
							Web.getAllreceiveUserNameCardShare,
							"getAllreceiveUserNameCardShare", "userId="
									+ user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&pagesize=20"
									+ "&curpage=" + (++currentPageShop));
				}

				String s = web.getPlan();
				return s;
			}

		});
	}

	/**
	 * 删除名片请求
	 */
	public void deleteReceiveNameCardShare(final String id) {
		final User user = UserData.getUser();
		Util.asynTask(this,"删除中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardExchangeCardRequest.this,
								"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardExchangeCardRequest.this,
										"删除成功", Toast.LENGTH_SHORT)
										.show();
								cardLinkmans.clear();
								currentPageShop = 0;
								cardExchangeCardRequestAdapter = new CardExchangeCardRequestAdapter();
								listView1.setAdapter(cardExchangeCardRequestAdapter);
								firstpageshop();
								scrollPageshop();
							} else {
								Toast.makeText(CardExchangeCardRequest.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardExchangeCardRequest.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardExchangeCardRequest.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web=null;
				//state=0,删除发起的名片请求state==1删除收到的名片请求
				if (state==0) {
					web = new Web(1, Web.bBusinessCard,
							Web.deleteReceiveNameCardShare,
							"deleteReceiveNameCardShare", "userId="
									+ user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&id=" + id);
				}else if (state==1) {
					web = new Web(1, Web.bBusinessCard,
							Web.deleteInitiateNameCardShare,
							"deleteInitiateNameCardShare", "userId="
									+ user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&id=" + id);
				}
				 
				String s = web.getPlan();
				return s;
			}

		});
	}

	/**
	 * 同意/拒绝交换名片
	 */
	public void agreeOrRefuse(final int thisstate, final String id) {
		final User user = UserData.getUser();
		Util.asynTask(this,"操作中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				int index = 0;
				Map<String, String> map = new HashMap<String, String>();
				if (runData != null) {
					try {
						System.out.println(runData.toString().replace(" ", "")
								.replace(",}", "}"));
						map = CardUtil.getJosn(runData.toString()
								.replace(" ", "").replace(",}", "}"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if (map == null) {
						Toast.makeText(CardExchangeCardRequest.this,
								"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_LONG).show();
					} else {

						if (map.get("code") != null) {
							if (map.get("code").equals("200")) {
								Toast.makeText(CardExchangeCardRequest.this,
										"交换成功", Toast.LENGTH_SHORT)
										.show();
								cardLinkmans.clear();
								currentPageShop = 0;
								cardExchangeCardRequestAdapter = new CardExchangeCardRequestAdapter();
								listView1.setAdapter(cardExchangeCardRequestAdapter);
								firstpageshop();
								scrollPageshop();
							} else {
								Toast.makeText(CardExchangeCardRequest.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {

							Toast.makeText(CardExchangeCardRequest.this,
									"网络不给力哦，请检查网络是否连接后，再试一下",
									Toast.LENGTH_SHORT).show();
						}
					}
				} else {
					Toast.makeText(CardExchangeCardRequest.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				if (thisstate == 1) {// 同意交换
					web = new Web(1, Web.bBusinessCard,
							Web.editNameCardShareOK, "editNameCardShareOK",
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&id=" + id);
				} else if (thisstate == 0) {// 拒绝
					web = new Web(1, Web.bBusinessCard,
							Web.editNameCardShareNO, "editNameCardShareNO",
							"userId=" + user.getUserId() + "&md5Pwd="
									+ user.getMd5Pwd() + "&id=" + id);
				}
				String s = web.getPlan();
				return s;
			}

		});
	}

	/**
	 * 提示用户是否删除交换信息
	 */
	public void tishi(final String id) {
		LayoutInflater inflaterDl = LayoutInflater.from(this);
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(
				R.layout.card_delete_dialog, null);
		dialog = new Dialog(this, R.style.CustomDialogStyle);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView yihou_update = (TextView) layout
				.findViewById(R.id.yihou_update);
		TextView update_count=(TextView) layout.findViewById(R.id.update_count);
		update_count.setText("确定删除该名片请求？");
		TextView now_update = (TextView) layout.findViewById(R.id.now_update);
		yihou_update.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		now_update.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				deleteReceiveNameCardShare(id);
				dialog.dismiss();
			}
		});
	}
}
