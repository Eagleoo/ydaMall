package com.mall.card;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.card.adapter.CardRecommendationPeopleAdapter;
import com.mall.card.adapter.CardUtil;
import com.mall.card.bean.CallBackListener;
import com.mall.card.bean.CardExchangeRequest;
import com.mall.card.bean.CardLinkman;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.R;

/**
 * 推荐的人
 * @author admin
 *
 */
public class CardRecommendationPeople extends Activity implements CallBackListener{
	private int currentPageShop = 0;
	@ViewInject(R.id.listview)
	private ListView listview;
	private CardRecommendationPeopleAdapter adapter;
	private List<CardLinkman> cardLinkmans=new ArrayList<CardLinkman>();
	private int width;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.card_recommendation_people);
		ViewUtils.inject(this);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width=dm.widthPixels;
		if (adapter==null) {
			adapter=new CardRecommendationPeopleAdapter(this,width);
			adapter.setCallBack(this);
			listview.setAdapter(adapter);
		}
		firstpageshop();
		scrollPageshop();
	}
	@OnClick({R.id.top_back})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.top_back:
			finish();
			break;

		default:
			break;
		}
	}
	public void firstpageshop() {
		tuijian();
	}

	public void scrollPageshop() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					tuijian();
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
			}
		});
	}
	/**
	 * 获得推荐的名片
	 */
	public void tuijian(){
		// TODO Auto-generated method stub
				final User user = UserData.getUser();
				Util.asynTask(new IAsynTask() {
					@SuppressWarnings("unchecked")
					@Override
					public void updateUI(Serializable runData) {
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
							if (map.get("code") != null) {
								if (map.get("code").equals("200")) {
									Gson gson = new Gson();
									List<CardLinkman> list = new ArrayList<CardLinkman>();
									list = gson.fromJson(map.get("list"),
											new TypeToken<List<CardLinkman>>() {
											}.getType());
									cardLinkmans.addAll(list);
									adapter.setList(list);
									
								} else {
									Toast.makeText(CardRecommendationPeople.this,
											map.get("message"), Toast.LENGTH_SHORT)
											.show();
								}
							} else {

								Toast.makeText(CardRecommendationPeople.this,
										"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
										.show();
							}

						} else {
							Toast.makeText(CardRecommendationPeople.this,
									"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
						}
					}

					@SuppressLint("UseSparseArrays")
					@Override
					public Serializable run() {
						Web web = new Web(1, Web.bBusinessCard, Web.getAllStateBusinessCard,
								"getAllStateBusinessCard", "userId="
										+ user.getUserId() + "&md5Pwd="
										+ user.getMd5Pwd() + "&curpage=" + (++currentPageShop) + "&pagesize=20");
						String s = web.getPlan();
						return s;
					}

				});
	}
	@Override
	public void callBack(Object object) {
		if (CardUtil.myCardLinkman == null) {
			CardUtil.xuznzeAdd(this,width,"1");
		} else {
			if (CardUtil.myCardLinkman.getIsme().equals("1")) {
				CardLinkman cardLinkman=(CardLinkman) object;
				addNameCardShare(cardLinkman.getUserid());
			} else {
				CardUtil.xuznzeAdd(this,width,"1");
			}
		}
	
	}
	/**
	 * 发起交换
	 */
	public void addNameCardShare(final String touserid) {

		final User user = UserData.getUser();
		Util.asynTask(CardRecommendationPeople.this,"处理中…",new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
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
					if (map.get("code") != null) {
						if (map.get("code").equals("200")) {
							Toast.makeText(CardRecommendationPeople.this, "发起交换成功",
									Toast.LENGTH_SHORT).show();
							getAllreceiveUserNameCardShare();
						} else {
							Toast.makeText(CardRecommendationPeople.this,
									map.get("message"), Toast.LENGTH_SHORT)
									.show();
						}
					} else {

						Toast.makeText(CardRecommendationPeople.this,
								"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_SHORT)
								.show();
					}

				} else {
					Toast.makeText(CardRecommendationPeople.this,
							"网络不给力哦，请检查网络是否连接后，再试一下", Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = new Web(1, Web.bBusinessCard, Web.addNameCardShare,
						"addNameCardShare", "userId="
								+ user.getUserId()+ "&md5Pwd="
								+ user.getMd5Pwd() + "&touserid=" + touserid);
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 获得用户收到的交换名片的请求
	 */
	private void getAllreceiveUserNameCardShare() {
		final User user = UserData.getUser();
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				getAllInitiateUserNameCardShare();
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
						Toast.makeText(CardRecommendationPeople.this,
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
								if (list.size() > 0) {
									CardUtil.shoudaoRe=list;
								}
							} else {
								Toast.makeText(CardRecommendationPeople.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardRecommendationPeople.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardRecommendationPeople.this, "网络不给力哦，请检查网络后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				// 获取自己发送的
				// 获取接收到的
				web = new Web(1, Web.bBusinessCard,
						Web.getAllreceiveUserNameCardShare,
						"getAllreceiveUserNameCardShare", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&pagesize=999999999"
								+ "&curpage=" + (1));
				String s = web.getPlan();
				return s;
			}

		});
	}
	/**
	 * 获得用户收到的交换名片的请求
	 */
	private void getAllInitiateUserNameCardShare() {
		final User user = UserData.getUser();
		Util.asynTask(this, "加载中…", new IAsynTask() {
			@SuppressWarnings("unchecked")
			@Override
			public void updateUI(Serializable runData) {
				currentPageShop=0;
				adapter=new CardRecommendationPeopleAdapter(CardRecommendationPeople.this,width);
				adapter.setCallBack(CardRecommendationPeople.this);
				listview.setAdapter(adapter);
				firstpageshop();
				scrollPageshop();
				int index = 0;
				getAllreceiveUserNameCardShare();
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
						Toast.makeText(CardRecommendationPeople.this,
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
								if (list.size() > 0) {
									CardUtil.faqiRe=list;
								}
							} else {
								Toast.makeText(CardRecommendationPeople.this,
										map.get("message"), Toast.LENGTH_SHORT)
										.show();
							}
						} else {
							Toast.makeText(CardRecommendationPeople.this,
									"网络不给力哦，请检查网络后，再试一下", Toast.LENGTH_SHORT)
									.show();
						}
					}
				} else {
					Toast.makeText(CardRecommendationPeople.this, "网络不给力哦，请检查网络后，再试一下",
							Toast.LENGTH_LONG).show();
				}
			}

			@SuppressLint("UseSparseArrays")
			@Override
			public Serializable run() {
				Web web = null;
				// 获取接收到的
				web = new Web(1, Web.bBusinessCard,
						Web.getAllInitiateUserNameCardShare,
						"getAllInitiateUserNameCardShare", "userId="
								+ user.getUserId() + "&md5Pwd="
								+ user.getMd5Pwd() + "&pagesize=999999999"
								+ "&curpage=" + (1));
				String s = web.getPlan();
				return s;
			}

		});
	}
}
