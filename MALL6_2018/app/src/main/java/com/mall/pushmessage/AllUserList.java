package com.mall.pushmessage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.voicerecognition.android.ui.DialogRecognitionListener;
import com.lidroid.xutils.ViewUtils;
import com.lin.component.CustomProgressDialog;
import com.lin.component.SerchMemberAdapter;
import com.mall.model.MemberInfo;
import com.mall.model.User;
import com.mall.model.UserInfo;
import com.mall.net.NewWebAPI;
import com.mall.net.WebRequestCallBack;
import com.mall.util.CharacterParser;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;
import com.mall.widget.SideBars;
import com.mall.widget.SideBars.OnTouchingLetterChangedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@SuppressLint("DefaultLocale")
public class AllUserList extends Activity {
	private EditText edit;
	private ImageView search_push;
	private UserInfo userInfo;
	private User user;
	private String md5Pwd = "";
	private String userid = "";
	private ArrayList<String> userlist = new ArrayList<String>();
	private ListView listview;
	private SerchMemberAdapter adapter;
	private List<MemberInfo> alluserlist = new ArrayList<MemberInfo>();

	private List<MemberInfo> listForDate = new ArrayList<MemberInfo>();
	private List<MemberInfo> searchList = new ArrayList<MemberInfo>();
	private List<MemberInfo> letterList = new ArrayList<MemberInfo>();
	private List<MemberInfo> levelList = new ArrayList<MemberInfo>();
	private View selectAllView;
	private int windowHeight;
	private String allcount = "";
	private int page = 1;
	private String type = "3";
	String all = "";
	String tunjian = "";
	String fudao = "";
	private ImageView speak;
	public final int YES = 135;
	public final int NO = YES + 1;
	private TextView quding;
	private String title;
	private CheckBox CheckBox_selectAll;
	int count = 0;
	private int theState = 0;
	private boolean isResult = false;
	private TextView Letter;
	private View letterView;
	private String date = "";
	private int times = 0;

	private TextView letterDialog;
	private SideBars sideBar;
	TextView NavigateTitle;
	TextView topright;
	private ImageView topright_im;
	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private boolean IsAll = false;
	private String allUserCount = "";
	
	private TextView client_sort;
	private TextView client_letter1;
	
	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case YES:
				count = adapter.getUsersstring().size();
				quding.setText("确 定(" + count + ")");
				break;
			case NO:
				count = adapter.getUsersstring().size();
				IsAll = false;
				quding.setText("确 定(" + count + ")");
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.adduser);
		initView();
	}

	private void initView() {
		WindowManager wm = this.getWindowManager();
		windowHeight = wm.getDefaultDisplay().getHeight();
		ViewUtils.inject(this);
		if (UserData.getUser() != null) {
			user = UserData.getUser();
			md5Pwd = user.getMd5Pwd();
			userid = user.getUserId();
		} else {
			Util.showIntent(AllUserList.this, LoginFrame.class);
		}
		findView();
		Intent intent = getIntent();
		if (intent.hasExtra("title")) {
			title = intent.getStringExtra("title");
		}
		if (Util.isNull(title)) {
			title = "自定义发送";
			type = "3";
			asyncALLLoadData();
		}
		if (title.contains("全部")) {
			type = "3";
			asyncALLLoadData();
		} else if (title.contains("辅导")||title.contains("创客")) {
			type = "2";
			asyncLoadData();
		} else if (title.contains("邀请")||title.contains("商家")) {
			type = "1";
			asyncLoadData();

		}
		setLs();

	}

	private String yaoQingCount = "";
	private String fuDaoCount = "";

	private void setLs() {
		// TODO Auto-generated method stub
		topright_im.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sortDialog();

			}
		});

		NavigateTitle.setText(title);

		TextView top_back = (TextView) this.findViewById(R.id.top_back);
		top_back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		quding = (TextView) this.findViewById(R.id.a_push_adduser_quding);
		quding.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (null == adapter) {
					adapter = new SerchMemberAdapter(AllUserList.this,
							R.layout.search_member_list, handler);
				}
				if (null == adapter.getUsersstring()
						|| adapter.getUsersstring().size() < 1) {

					finish();

					return;
				}

				if (null != adapter.getUsersstring()
						&& adapter.getUsersstring().size() > 0) {

					for (int i = 0; i < adapter.getUsersstring().size(); i++) {
						String s = adapter.getUsersstring().get(i).getName()
								+ ",,,"
								+ adapter.getUsersstring().get(i).getPhone()
								+ ",,,"
								+ adapter.getUsersstring().get(i).getRegTime()
								+ ",,,"
								+ adapter.getUsersstring().get(i).getUserid();
						userlist.add(s);
					}
				}
				Intent intent = new Intent(AllUserList.this, PushMessage.class);
				if ("3".equals(type)) {
					intent.putStringArrayListExtra("zdylist", userlist);
				} else if ("2".equals(type)) {

					intent.putStringArrayListExtra("allzshylist", userlist);
				} else if ("1".equals(type)) {
					intent.putStringArrayListExtra("alltjhylist", userlist);
				}
				intent.putExtra("totalcount", allcount);
				AllUserList.this.startActivity(intent);
			}
		});
		if (!Util.isNull(this.getIntent().getStringExtra("totalcount"))) {
			allcount = this.getIntent().getStringExtra("totalcount");
		}

		speak.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Util.startVoiceRecognition(AllUserList.this,
						new DialogRecognitionListener() {
							@Override
							public void onResults(Bundle results) {
								ArrayList<String> rs = results != null ? results
										.getStringArrayList(RESULTS_RECOGNITION)
										: null;
								if (rs != null && rs.size() > 0) {
									String str = rs.get(0).replace("。", "")
											.replace("，", "");
									edit.setText(str);
									if (Util.isNull(edit.getText().toString())) {

									} else {
										searchList.clear();
										for (int i = 0; i < alluserlist.size(); i++) {
											boolean ca = alluserlist
													.get(i)
													.getName()
													.contains(
															edit.getText()
																	.toString())
													|| alluserlist
															.get(i)
															.getPhone()
															.contains(
																	edit.getText()
																			.toString());
											if (ca) {
												searchList.add(alluserlist
														.get(i));
											}
										}
										adapter = new SerchMemberAdapter(
												AllUserList.this,
												R.layout.search_member_list,
												handler);
										adapter.setList(searchList, theState);
										listview.setAdapter(adapter);
										listview.setOnScrollListener(null);
									}
								}
							}
						});
			}
		});

		scrollPage();
		selectAllView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (CheckBox_selectAll.isChecked()) {
					CheckBox_selectAll.setChecked(false);
					IsAll = false;
				} else {
					CheckBox_selectAll.setChecked(true);
					IsAll = true;
				}

			}
		});
		CheckBox_selectAll
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {

							if (!isResult) {
								if (theState == 0) {
									if (null != listForDate
											&& listForDate.size() > 0) {
										for (int i = 0; i < listForDate.size(); i++) {

											listForDate.get(i)
													.setSelected(true);

										}

										// adapter.clearlist();
										// adapter.setList(alluserlist,
										// theState);
										adapter.UpData();
									}
								} else if (theState == 1) {

									if (null != letterList
											&& letterList.size() > 0) {
										for (int i = 0; i < letterList.size(); i++) {

											letterList.get(i).setSelected(true);

										}

										// adapter.clearlist();
										// adapter.setList(alluserlist,
										// theState);
										adapter.UpData();
									}
								} else if (theState == 2) {
									if (null != levelList
											&& levelList.size() > 0) {
										for (int i = 0; i < levelList.size(); i++) {
											levelList.get(i).setSelected(true);
										}
										adapter.UpData();
									}
								}
							} else {
								if (null != searchList && searchList.size() > 0) {
									for (int i = 0; i < searchList.size(); i++) {

										searchList.get(i).setSelected(true);

									}

									// adapter.clearlist();
									// adapter.setList(searchlist, theState);
									adapter.UpData();
								}
							}

						} else if (!isChecked) {
							// List<MemberInfo> list2 = adapter.getList();
							// List<MemberInfo> list = new
							// ArrayList<MemberInfo>();
							// list.addAll(list2);
							// count = 0;
							// quding.setText("确 定(0)");
							// userlist.clear();
							// listForDate.clear();
							if (!isResult) {
								if (theState == 0) {
									if (null != listForDate
											&& listForDate.size() > 0) {
										for (int i = 0; i < listForDate.size(); i++) {

											listForDate.get(i).setSelected(
													false);

										}

										// adapter.clearlist();
										// adapter.setList(alluserlist,
										// theState);
										adapter.clearUserString();
										adapter.UpData();
									}
								} else if (theState == 1) {

									if (null != letterList
											&& letterList.size() > 0) {
										for (int i = 0; i < letterList.size(); i++) {
											letterList.get(i)
													.setSelected(false);

										}

										// adapter.clearlist();
										adapter.clearUserString();
										// adapter.setList(alluserlist,
										// theState);
										adapter.UpData();

									}
								} else if (theState == 2) {
									if (null != levelList
											&& levelList.size() > 0) {
										for (int i = 0; i < levelList.size(); i++) {
											levelList.get(i).setSelected(false);

										}

										adapter.clearUserString();

										adapter.UpData();

									}
								}
							} else {
								if (null != searchList && searchList.size() > 0) {
									for (int i = 0; i < searchList.size(); i++) {
										searchList.get(i).setSelected(false);

									}

									// adapter.clearlist();
									adapter.clearUserString();
									// adapter.setList(searchlist, theState);
									adapter.UpData();
								}
							}

						}
					}
				});

		search_push.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final String searStr = edit.getText().toString().trim();
				if (!Util.isNull(searStr)) {

					searchList.clear();

					if (theState == 0) {
						if (null != listForDate && listForDate.size() > 0) {

							for (int i = 0; i < listForDate.size(); i++) {
								if (listForDate.get(i).getName()
										.contains(searStr)
										|| listForDate.get(i).getPhone()
												.contains(searStr)
										|| listForDate.get(i).getUserid()
												.contains(searStr)) {
									searchList.add(listForDate.get(i));
								}
							}
							if (searchList.size() > 0) {
								CheckBox_selectAll.setChecked(false);
								quding.setText("确 定(0)");
								for (int i = 0; i < searchList.size(); i++) {
									searchList.get(i).setSelected(false);
								}

								for (int i = 0; i < listForDate.size(); i++) {
									if (!Util.isNull(listForDate.get(i)
											.getDate().trim())) {
										date = listForDate.get(i).getDate();

										break;
									}
								}

								Letter.setText(date);

								isResult = true;
								adapter.clear();
								adapter.setList(searchList, theState);

							} else {
								Util.show("搜索的客户不存在！", AllUserList.this);
							}
						} else {

						}

					} else if (theState == 1) {
						if (null != letterList && letterList.size() > 0) {

							for (int i = 0; i < letterList.size(); i++) {
								boolean ca = letterList.get(i).getName()
										.contains(searStr)
										|| letterList.get(i).getPhone()
												.contains(searStr)
										|| letterList.get(i).getUserid()
												.contains(searStr);
								if (ca) {
									searchList.add(letterList.get(i));
								}
							}
							if (searchList.size() > 0) {
								CheckBox_selectAll.setChecked(false);
								quding.setText("确 定(0)");
								for (int i = 0; i < searchList.size(); i++) {
									searchList.get(i).setSelected(false);
								}

								isResult = true;

								Collections.sort(searchList, pinyinComparator);
								Letter.setText(searchList.get(0)
										.getSortLetter());

								adapter.clear();
								adapter.setList(searchList, theState);

							} else {

								Util.show("搜索的客户不存在！", AllUserList.this);

							}
						}

					} else if (theState == 2) {
						if (null != levelList && levelList.size() > 0) {
							for (int i = 0; i < levelList.size(); i++) {
								if (levelList.get(i).getUserid()
										.contains(searStr)
										|| levelList.get(i).getName()
												.contains(searStr)
										|| levelList.get(i).getPhone()
												.contains(searStr)) {
									searchList.add(levelList.get(i));
								}
							}
							if (null != searchList && searchList.size() > 0) {
								isResult = true;
								CheckBox_selectAll.setChecked(false);
								quding.setText("确 定(0)");
								for (int i = 0; i < searchList.size(); i++) {
									searchList.get(i).setSelected(false);
								}
								Letter.setText(searchList.get(0).getUserLevel());
								letterView.setVisibility(View.VISIBLE);
								adapter.clear();
								adapter.setList(searchList, theState);
								listview.setSelection(0);
							} else {
								Util.show("搜索的客户不存在！", AllUserList.this);
							}
						}
					}
				} else {
					Util.show("请输入搜索关键字", AllUserList.this);
				}
			}

			private void setLetter() {
				// TODO Auto-generated method stub

			}
		});

		edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (Util.isNull(edit.getText().toString().trim())) {
					isResult = false;
					CheckBox_selectAll.setChecked(false);
					quding.setText("确 定(0)");
					if (theState == 0) {
						if (null != listForDate && listForDate.size() > 0) {

							for (int i = 0; i < listForDate.size(); i++) {
								listForDate.get(i).setSelected(false);
							}
							for (int i = 0; i < listForDate.size(); i++) {
								if (!Util.isNull(listForDate.get(i).getDate())) {

									date = searchList.get(i).getDate();
									Letter.setText(date);
									break;
								}

							}

							adapter.clear();
							adapter.clearUserString();
							adapter.setList(listForDate, theState);
							listview.setSelection(0);
						} else {
							if (type.equals("1") || type.equals("2")) {
								asyncLoadData();
							} else {
								asyncALLLoadData();
							}

						}
					} else if (theState == 1) {
						if (null != letterList && letterList.size() > 0) {

							for (int i = 0; i < letterList.size(); i++) {
								letterList.get(i).setSelected(false);
							}

							Collections.sort(letterList, pinyinComparator);
							Letter.setText(letterList.get(0).getSortLetter());

							adapter.clear();
							adapter.clearUserString();
							adapter.setList(letterList, theState);
							listview.setSelection(0);
						} else {
							if (type.equals("1") || type.equals("2")) {
								asyncLoadData();
							} else {
								asyncALLLoadData();
							}

						}
					} else if (theState == 2) {
						if (null != levelList && levelList.size() > 0) {

							for (int i = 0; i < levelList.size(); i++) {
								levelList.get(i).setSelected(false);
							}

							Letter.setText(levelList.get(0).getShowLevel());

							adapter.clear();
							adapter.clearUserString();
							adapter.setList(levelList, theState);
							listview.setSelection(0);
						} else {
							if (type.equals("1") || type.equals("2")) {
								asyncLoadData();
							} else {
								asyncALLLoadData();
							}

						}
					}
				}
			}
		});

	}

	private void findView() {
		// TODO Auto-generated method stub
		
		client_sort=(TextView) this.findViewById(R.id.client_sort);
		client_sort.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 sortDialog();
			}
		});
		client_letter1=(TextView)findViewById(R.id.client_letter1);
		selectAllView = this.findViewById(R.id.select_all);
		Letter = (TextView) this.findViewById(R.id.client_letter);
		letterView = this.findViewById(R.id.client_letter_rl_layout);// 第一行显示
		NavigateTitle = (TextView) findViewById(R.id.NavigateTitle);
		topright = (TextView) findViewById(R.id.topright);
		topright.setVisibility(View.GONE);
		topright_im = (ImageView) findViewById(R.id.topright_im);
		topright_im.setVisibility(View.INVISIBLE);
		CheckBox_selectAll = (CheckBox) this.findViewById(R.id.select_all_c);
		listview = (ListView) this.findViewById(R.id.add_user_list);
		android.view.ViewGroup.LayoutParams parames = listview
				.getLayoutParams();
		int _170dp = Util.dpToPx(AllUserList.this, 200);
		parames.height = windowHeight - _170dp;
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		edit = (EditText) this.findViewById(R.id.search_push_user_edit);
		search_push = (ImageView) this.findViewById(R.id.search_push_user);

		speak = (ImageView) this.findViewById(R.id.member_the_speak);
	}

	public void sortDialog() {
		View view = getLayoutInflater().inflate(R.layout.dialog_client_sort,
				null);
		TextView sortDate = (TextView) view
				.findViewById(R.id.dialog_client_sort_date);
		TextView name = (TextView) view
				.findViewById(R.id.dialog_client_sort_letter);
		TextView js = (TextView) view.findViewById(R.id.dialog_client_sort_js);

		final Dialog dialog = new Dialog(AllUserList.this, R.style.dialog);
		dialog.setContentView(view);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int w = dm.widthPixels;
		dialog.show();
		android.view.WindowManager.LayoutParams parm = dialog.getWindow()
				.getAttributes();
		parm.width = w / 5 * 4;
		dialog.getWindow().setAttributes(parm);
		sortDate.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			
				CheckBox_selectAll.setChecked(false);
				quding.setText("确 定(0)");
				theState = 0;
				sideBar.setVisibility(View.GONE);
				if (!isResult) {

					if (null != listForDate && listForDate.size() > 0) {
						for (int i = 0; i < listForDate.size(); i++) {
							listForDate.get(i).setSelected(false);
						}

						letterView.setVisibility(View.VISIBLE);

						for (int i = 0; i < listForDate.size(); i++) {
							if (!Util.isNull(listForDate.get(i).getDate()
									.trim())) {
								date = listForDate.get(i).getDate();
								Log.e("date=-=-=-=-=-=", date + "");
								Letter.setText(date);
								client_letter1.setText(date);
					
								break;
							}
						}

						adapter.clear();

						adapter.setList(listForDate, theState);

						listview.setSelection(0);
						adapter.clearUserString();
					} else {
						if (type.equals("1") || type.equals("2")) {
							asyncLoadData();
						} else if ("3".equals(type)) {
							asyncALLLoadData();
						}

					}
				} else {
					if (null != searchList && searchList.size() > 0) {

						for (int i = 0; i < searchList.size(); i++) {
							searchList.get(i).setSelected(false);
						}
						adapter.clear();
						adapter.clearUserString();
						letterView.setVisibility(View.VISIBLE);

						for (int i = 0; i < searchList.size(); i++) {
							if (!Util
									.isNull(searchList.get(i).getDate().trim())) {
								date = searchList.get(i).getDate();

								break;
							}
						}

						Letter.setText(date);

						adapter.setList(searchList, theState);

						listview.setSelection(0);

					}
				}
				letterView.setVisibility(View.GONE);

			}
		});
		name.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				letterView.setVisibility(View.VISIBLE);
				CheckBox_selectAll.setChecked(false);
				quding.setText("确 定(0)");
				theState = 1;

				if (!isResult) {

					if (null != letterList && letterList.size() > 0) {
						sideBar.setVisibility(View.VISIBLE);
						for (int i = 0; i < letterList.size(); i++) {
							letterList.get(i).setSelected(false);
						}

						// Collections.sort(letterList, pinyinComparator);

						Letter.setText(letterList.get(0).getSortLetter());

						if (View.GONE == letterView.getVisibility())
							letterView.setVisibility(View.VISIBLE);
						adapter.clear();
						adapter.setList(letterList, theState);

						listview.setSelection(0);

					} else {

						if (type.equals("1") || type.equals("2")) {
							asyncLoadData();
						} else if ("3".equals(type)) {
							asyncALLLoadData();
						}

					}
				} else {
					if (null != searchList && searchList.size() > 0) {
						// Collections.sort(searchList, pinyinComparator);
						for (int i = 0; i < searchList.size(); i++) {
							searchList.get(i).setSelected(false);
						}

						letterView.setVisibility(View.VISIBLE);

						Letter.setText(searchList.get(0).getSortLetter());
						adapter.clear();

						adapter.setList(searchList, theState);

						listview.setSelection(0);

					}
				}
			}
		});
		js.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				letterView.setVisibility(View.VISIBLE);
				theState = 2;
				CheckBox_selectAll.setChecked(false);
				quding.setText("确 定(0)");

				sideBar.setVisibility(View.GONE);
				if (!isResult) {
					if (null != levelList && levelList.size() > 0) {
						for (int i = 0; i < levelList.size(); i++) {
							levelList.get(i).setSelected(false);
						}
						letterView.setVisibility(View.VISIBLE);
						Letter.setText("系统角色");
						adapter.clear();

						adapter.setList(levelList, theState);
					} else {
						if (type.equals("1") || type.equals("2")) {
							asyncLoadData();
						} else if ("3".equals(type)) {
							asyncALLLoadData();
						}

					}
				} else {
					if (null != searchList && searchList.size() > 0) {
						// if (View.VISIBLE == letterView.getVisibility()) {
						// letterView.setVisibility(View.GONE);
						// }
						letterView.setVisibility(View.VISIBLE);
						Letter.setText("系统角色");
						for (int i = 0; i < searchList.size(); i++) {
							searchList.get(i).setSelected(false);
						}

						adapter.clear();

						adapter.setList(searchList, theState);

						listview.setSelection(0);

					}
				}
			}
		});

	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		initSide();
	}

	public void scrollPage() {
		listview.setOnScrollListener(new OnScrollListener() {
			int lastItem;

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (lastItem >= adapter.getCount()
						&& scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					if (type.equals("1") || type.equals("2")) {
						// asyncLoadData();

					} else {
						// asyncALLLoadData();

					}
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				lastItem = firstVisibleItem + visibleItemCount;
				Log.e("theState__theState","theState"+theState+"isResult"+isResult);
				if (theState == 1) {
					if (!isResult) {

						if (null != letterList && letterList.size() > 0) {
							Log.e("数据滑动1","1");
							String letter = Letter.getText().toString();
							Log.e("数据滑动2","1"+letter);
							Log.e("数据滑动3","2"+letterList.get(
									view.getFirstVisiblePosition())
									.getSortLetter());
							if (!letter.equals(letterList.get(
									view.getFirstVisiblePosition())
									.getSortLetter())) {
								Letter.setText(letterList.get(
										view.getFirstVisiblePosition())
										.getSortLetter());
							} else {
//								adapter.setState("gone",
//										view.getFirstVisiblePosition());
//								adapter.notifyDataSetChanged();
							}
						}

					} else {
						if (null != searchList && searchList.size() > 0) {
							String letter = Letter.getText().toString();
							if (!letter.equals(searchList.get(
									view.getFirstVisiblePosition())
									.getSortLetter())) {
								Letter.setText(searchList.get(
										view.getFirstVisiblePosition())
										.getSortLetter());
							} else {
								adapter.setState("gone",
										view.getFirstVisiblePosition());
								adapter.notifyDataSetChanged();
							}
						}
					}
				}

			}
		});
	}

	private void asyncALLLoadData() {
		final CustomProgressDialog pd = CustomProgressDialog.createDialog(this);
		pd.setMessage(pd, "数据加载中...");
		pd.show();
		String order = "";
		String ascOrDesc = "";
		if (theState == 0) {
			order = "date";
			ascOrDesc = "Desc";

		} else if (theState == 1) {
			order = "py";
			ascOrDesc = "asc";
		} else if (theState == 2) {
			order = "levelShop";
			ascOrDesc = "asc";
		}
		NewWebAPI.getNewInstance().getAllMyUser(userid, md5Pwd, 1, 99999,
				order, ascOrDesc, new WebRequestCallBack() {
					@Override
					public void success(Object result) {
						// String res = result.toString();
						// List<MemberInfo> list = parseStringToObject(res);

						if (Util.isNull(result)) {
							return;
						}
						com.alibaba.fastjson.JSONObject json = JSON
								.parseObject(result.toString());
						if (200 != json.getIntValue("code")) {
							Util.show(json.getString("message"),
									AllUserList.this);
							return;
						}
						List<MemberInfo> list = JSON.parseArray(
								json.getString("list"), MemberInfo.class);
						allUserCount = json.getString("message");
						if (null != list && list.size() > 0) {

							if (adapter == null) {
								adapter = new SerchMemberAdapter(
										AllUserList.this,
										R.layout.search_member_list, handler);
								listview.setAdapter(adapter);
							}
							list = filledData(list);
							if (times == 0) {
								alluserlist.addAll(list);
								times++;
							}

							if (theState == 0) {
								listForDate.addAll(list);
								letterView.setVisibility(View.VISIBLE);
								for (int i = 0; i < listForDate.size(); i++) {

									if (!Util.isNull(listForDate.get(i)
											.getDate().trim())) {
										date = listForDate.get(i).getDate();
//										Letter.setText(date);
										break;

									}

								}

								adapter.clear();
								adapter.setList(listForDate, theState);
								listview.setSelection(0);

							} else if (theState == 1) {

								characterParser = CharacterParser.getInstance();
								pinyinComparator = new PinyinComparator();

								letterList.addAll(list);
								Collections.sort(letterList,
										new Comparator<MemberInfo>() {

											@Override
											public int compare(MemberInfo lhs,
													MemberInfo rhs) {

												if (lhs.getSortLetter().equals(
														"#")) {
													return 1;
												}
												if (rhs.getSortLetter().equals(
														"#")) {
													return -1;
												}

												return 0;
											}
										});

								Letter.setText(letterList.get(0)
										.getSortLetter());
								letterView.setVisibility(View.VISIBLE);
								sideBar.setVisibility(View.VISIBLE);
								adapter.clear();
								adapter.setList(letterList, theState);
								// listview.requestFocus();
								// listview.requestFocusFromTouch();
								listview.setSelection(0);
							} else if (theState == 2) {
								letterView.setVisibility(View.VISIBLE);
								Letter.setText("系统角色");
								levelList.addAll(list);
								adapter.clear();
								adapter.setList(levelList, theState);
								listview.setSelection(0);
							}
							// listForDate.addAll(list);
							// characterParser = CharacterParser.getInstance();
							// pinyinComparator = new PinyinComparator();
							// letterList = filledData(list);

							// alluserlist.addAll(list);
						} else {
							Toast.makeText(AllUserList.this, "没有数据",
									Toast.LENGTH_LONG).show();
						}
						pd.cancel();
						pd.dismiss();
						super.success(result);
					}

					@Override
					public void fail(Throwable e) {
						pd.cancel();
						pd.dismiss();
						super.fail(e);
					}

					@Override
					public void timeout() {
						pd.cancel();
						pd.dismiss();
						super.timeout();
					}

				});
	}

	/**
	 * 为ListView填充数据
	 *
	 * @param
     * * @return
	 */

	@SuppressLint("DefaultLocale")
	private List<MemberInfo> filledData(List<MemberInfo> list) {
		List<MemberInfo> mSortList = new ArrayList<MemberInfo>();

		for (int i = 0; i < list.size(); i++) {

			MemberInfo clk = new MemberInfo();
			clk.setName(list.get(i).getName());
			// clk.setMoney(list.get(i).get);
			clk.setPhone(list.get(i).getPhone());
			clk.setDate(list.get(i).getDate());
			clk.setUserLevel(list.get(i).getUserLevel());
			clk.setShowLevel(list.get(i).getShowLevel());
			clk.setUserid(list.get(i).getUserid());
			// clk.setUserLevel(list.get(i).getLevel());
			String name = list.get(i).getName().trim();
			if (!Util.isNull(name)) {

				// 汉字转换成拼音
				String pinyin = characterParser.getSelling(name);

				String sortString = pinyin.substring(0, 1).toUpperCase();

				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					clk.setSortLetter(sortString.toUpperCase());
				} else {
					clk.setSortLetter("#");
				}
			} else {
				clk.setSortLetter("#");
			}

			mSortList.add(clk);

		}
		return mSortList;

	}

	private void asyncLoadData() {
		final CustomProgressDialog pd = CustomProgressDialog.createDialog(this);
		pd.setMessage(pd, "数据加载中...");
		pd.show();
		String order = "";
		String ascOrDesc = "";
		if (theState == 0) {
			order = "date";
			ascOrDesc = "Desc";

		} else if (theState == 1) {
			order = "py";
			ascOrDesc = "asc";
		} else if (theState == 2) {
			order = "levelShop";
			ascOrDesc = "asc";
		}
		if (type.equals("1")) {
			NewWebAPI.getNewInstance().getMyInviter(userid, md5Pwd, 1, 99999,
					order, ascOrDesc, new WebRequestCallBack() {
						@Override
						public void success(Object result) {
							if (Util.isNull(result)) {
								return;
							}
							com.alibaba.fastjson.JSONObject json = JSON
									.parseObject(result.toString());
							if (200 != json.getIntValue("code")) {
								Util.show(json.getString("message"),
										AllUserList.this);
								return;
							}
							yaoQingCount = json.getString("message");
							List<MemberInfo> list = JSON.parseArray(
									json.getString("list"), MemberInfo.class);

							if (null != list && list.size() > 0) {
								if (adapter == null) {
									adapter = new SerchMemberAdapter(
											AllUserList.this,
											R.layout.search_member_list,
											handler);
									listview.setAdapter(adapter);
								}
								list = filledData(list);
								if (theState == 0) {
									listForDate.addAll(list);
									letterView.setVisibility(View.VISIBLE);
									for (int i = 0; i < listForDate.size(); i++) {

										if (!Util.isNull(listForDate.get(i)
												.getDate().trim())) {
											date = listForDate.get(i).getDate();
											Letter.setText(date);
											break;

										}

									}

									adapter.clear();
									adapter.setList(listForDate, theState);
									listview.setSelection(0);

								} else if (theState == 1) {
									letterView.setVisibility(View.VISIBLE);
									sideBar.setVisibility(View.VISIBLE);

									// list = filledData(list);
									letterList.addAll(list);
									Collections.sort(letterList,
											new Comparator<MemberInfo>() {

												@Override
												public int compare(
														MemberInfo lhs,
														MemberInfo rhs) {

													if (lhs.getSortLetter()
															.equals("#")) {
														return 1;
													}
													if (rhs.getSortLetter()
															.equals("#")) {
														return -1;
													}

													return 0;
												}
											});

									Letter.setText(letterList.get(0)
											.getSortLetter());
									adapter.clear();
									adapter.setList(letterList, theState);
									listview.setSelection(0);
								} else if (theState == 2) {
									letterView.setVisibility(View.VISIBLE);
									Letter.setText("系统角色");
									levelList.addAll(list);
									adapter.clear();
									adapter.setList(levelList, theState);
									listview.setSelection(0);

								}
								// listForDate.addAll(list);
								// characterParser =
								// CharacterParser.getInstance();
								// pinyinComparator = new PinyinComparator();
								// letterList = filledData(list);

								// alluserlist.addAll(list);
							} else {
								Toast.makeText(AllUserList.this, "没有数据",
										Toast.LENGTH_LONG).show();
							}
							pd.cancel();
							pd.dismiss();
							super.success(result);
						}

						@Override
						public void fail(Throwable e) {
							pd.cancel();
							pd.dismiss();
							super.fail(e);
						}

						@Override
						public void timeout() {
							pd.cancel();
							pd.dismiss();
							super.timeout();
						}

					});
		} else if (type.equals("2")) {
			NewWebAPI.getNewInstance().getMyMerchants(userid, md5Pwd, 1, 99999,
					order, ascOrDesc, new WebRequestCallBack() {
						@Override
						public void success(Object result) {
							if (Util.isNull(result)) {
								return;
							}
							com.alibaba.fastjson.JSONObject json = JSON
									.parseObject(result.toString());
							if (200 != json.getIntValue("code")) {
								Util.show(json.getString("message"),
										AllUserList.this);
								return;
							}
							fuDaoCount = json.getString("message");
							List<MemberInfo> list = JSON.parseArray(
									json.getString("list"), MemberInfo.class);
							if (null != list && list.size() > 0) {
								if (adapter == null) {
									adapter = new SerchMemberAdapter(
											AllUserList.this,
											R.layout.search_member_list,
											handler);
									listview.setAdapter(adapter);
								}

								if (theState == 0) {
									listForDate.addAll(list);
									letterView.setVisibility(View.VISIBLE);
									for (int i = 0; i < listForDate.size(); i++) {

										if (!Util.isNull(listForDate.get(i)
												.getDate().trim())) {
											date = listForDate.get(i).getDate();
											Letter.setText(date);
											break;

										}

									}

									adapter.clear();
									adapter.setList(listForDate, theState);
									listview.setSelection(0);
								} else if (theState == 1) {
									letterView.setVisibility(View.VISIBLE);
									sideBar.setVisibility(View.VISIBLE);
									characterParser = CharacterParser
											.getInstance();
									pinyinComparator = new PinyinComparator();
									list = filledData(list);

									Collections.sort(list,
											new Comparator<MemberInfo>() {

												@Override
												public int compare(
														MemberInfo lhs,
														MemberInfo rhs) {

													if (lhs.getSortLetter()
															.equals("#")) {
														return 1;
													}
													if (rhs.getSortLetter()
															.equals("#")) {
														return -1;
													}

													return 0;
												}
											});
									letterList.addAll(list);
									Letter.setText(letterList.get(0)
											.getSortLetter());
									adapter.clear();
									adapter.setList(letterList, theState);
									listview.setSelection(0);
								} else if (theState == 2) {
									letterView.setVisibility(View.VISIBLE);
									Letter.setText("系统角色");
									levelList.addAll(list);
									adapter.clear();
									adapter.setList(levelList, theState);
									listview.setSelection(0);
								}
								// listForDate.addAll(list);
								// characterParser =
								// CharacterParser.getInstance();
								// pinyinComparator = new PinyinComparator();
								// letterList = filledData(list);

								// alluserlist.addAll(list);
							} else {
								Toast.makeText(AllUserList.this, "没有数据",
										Toast.LENGTH_LONG).show();
							}
							pd.cancel();
							pd.dismiss();
							super.success(result);
						}

						@Override
						public void fail(Throwable e) {
							pd.cancel();
							pd.dismiss();
							super.fail(e);
						}

						@Override
						public void timeout() {
							pd.cancel();
							pd.dismiss();
							super.timeout();
						}

					});
		}
	}

	private List<MemberInfo> parseStringToObject(String s) {
		List<MemberInfo> list = new ArrayList<MemberInfo>();
		try {
			JSONObject o = new JSONObject(s);
			String listStirng = o.getString("list");
			JSONArray array = new JSONArray(listStirng);
			for (int i = 0; i < listStirng.length(); i++) {
				MemberInfo m = new MemberInfo();
				JSONObject obj = array.getJSONObject(i);
				m.setName(obj.getString("name"));
				m.setUserid(obj.getString("userId"));
				m.setPhone(obj.getString("phone"));
				m.setLevel(obj.getString("showLevel"));
				list.add(m);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return list;
	}

	private void initSide() {
		// TODO Auto-generated method stub
		sideBar = (SideBars) this.findViewById(R.id.sidrbar);
		letterDialog = (TextView) this.findViewById(R.id.letter_dialog);
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@Override
			public void onTouchingLetterChanged(String s) {
				// TODO Auto-generated method stub
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {

					// shopadapter = (ShopUser) member_list.getAdapter();
					if (null == adapter) {
						return;
					}
					View item = adapter.getView(0, null, listview);
					item.measure(0, 0);
					int ih = item.getMeasuredHeight();
					if (adapter.getCount() > listview.getMeasuredHeight() / ih) {
						adapter.setState("gone", position);
						adapter.notifyDataSetChanged();
					}

					listview.setSelection(position);

				}
			}
		});
		sideBar.setTextView(letterDialog);
	}

	public class PinyinComparator implements Comparator<MemberInfo> {
		public int compare(MemberInfo o1, MemberInfo o2) {
			if (o1.getSortLetter().equals("@")
					|| o2.getSortLetter().equals("#")) {
				return -1;
			} else if (o1.getSortLetter().equals("#")
					|| o2.getSortLetter().equals("@")) {
				return 1;
			} else {
				return o1.getSortLetter().compareTo(o2.getSortLetter());
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == event.KEYCODE_BACK) {
			Util.showIntent(AllUserList.this, PushMessage.class);
		}
		return super.onKeyDown(keyCode, event);
	}

}
