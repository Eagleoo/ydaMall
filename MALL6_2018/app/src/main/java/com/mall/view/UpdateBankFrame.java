package com.mall.view;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.mall.model.Bank;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 修改银行信息
 * 
 * @author kaifa
 * 
 */
public class UpdateBankFrame extends Activity implements OnItemClickListener {

	private EditText bName = null;
	private EditText bAccount = null;
	private EditText idCard = null;
	private EditText phone = null;
	private TextView bankType = null;
	private EditText banCard = null;
	private EditText twoPwd = null;

	//@ViewInject(R.id.yzmpic_code)
	private EditText yzmpic_code;//
//	@ViewInject(R.id.update_basic_user_info_code)
	private EditText code;//
//	@ViewInject(R.id.yzmpic_view)
	private ImageView yzmpic_view;//
//	@ViewInject(R.id.update_basic_user_info_send_code)
	private Button send_code;//
//	@ViewInject(R.id.yzmpic)
	private LinearLayout yzmpic;//
	private String smsState = "";
	private int s = 60;
	private LinearLayout yanzhengma;
	private EditText update_basic_user_info_code;

	private View phone_line;
	
	private List<Bank> list = null;
	private Button bSub = null;
	private Button bClo = null;
	private PopupWindow popupWindow;
	private NumbersAdapter adapter;
	private List<String> numbers = new ArrayList<String>();
	private String state = "1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.update_bank);
		state = getIntent().getStringExtra("state");

	}

	@Override
	public void onStart() {
		super.onStart();
		init();
	}

	public void init() {
		Util.initTop(this, " 银行信息完善", Integer.MIN_VALUE, null);
		if (null == UserData.getUser()) {
			Util.showIntent("对不起，您还没有登录！", this, LoginFrame.class);
			return;
		}
		Button btn_fs=(Button) findViewById(R.id.update_basic_user_info_send_code);
		bName = Util.getEditText(R.id.bank_name, this);
		yzmpic_code=(EditText) findViewById(R.id.yzmpic_code);
		code=(EditText) findViewById(R.id.update_basic_user_info_code);
		yzmpic_view=(ImageView) findViewById(R.id.yzmpic_view);
		send_code=(Button) findViewById(R.id.update_basic_user_info_send_code);
		yzmpic=(LinearLayout) findViewById(R.id.yzmpic);
		yanzhengma=(LinearLayout) findViewById(R.id.yanzhengma);
		update_basic_user_info_code=(EditText) findViewById(R.id.update_basic_user_info_code);
		phone_line= findViewById(R.id.phone_line);

		int levelid = Integer.parseInt(UserData.getUser().getLevelId());
		if (6 == levelid||4==levelid) {
			phone_line.setVisibility(View.GONE);
		}

		
		bName.setText(UserData.getUser().getName());
		if (!TextUtils.isEmpty(bName.getText().toString().trim())) {
			bName.setEnabled(false);
		}
		bAccount = Util.getEditText(R.id.bank_accountName, this);
		bAccount.setText(UserData.getUser().getName());
		idCard = Util.getEditText(R.id.bank_idCard, this);
		idCard.setText(!Util.isNull(UserData.getUser().getIdNo())?UserData.getUser().getIdNo():UserData.getUser().getPassport());
		if (!TextUtils.isEmpty(idCard.getText().toString().trim())) {
			idCard.setEnabled(false);
		}
		phone = Util.getEditText(R.id.bank_phone, this);
		phone.setText(UserData.getUser().getMobilePhone());
		if (!TextUtils.isEmpty(phone.getText().toString().trim())) {
			phone.setEnabled(false);
			yanzhengma.setVisibility(View.GONE);
		}

		if (6 == levelid||4==levelid) {
			yanzhengma.setVisibility(View.GONE);
		}

		bankType = (TextView) findViewById(R.id.bank_bankName);
		banCard = Util.getEditText(R.id.bank_bankCard, this);
		twoPwd = Util.getEditText(R.id.bank_two, this);
		bSub = Util.getButton(R.id.bank_sub, this);
		bClo = Util.getButton(R.id.bank_cle, this);
		if (TextUtils.isEmpty(UserData.getUser().getBank()) || UserData.getUser().getBank().equals("0")) {
			bankType.setText("选择银行");
		} else {
			bankType.setText(UserData.getUser().getBank());
		}
		getBank();
		
		//发送验证码监听器
		btn_fs.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final User user=UserData.getUser();
				if (Util.isNull(phone.getText().toString())) {
					Util.show("请输入您的手机号！", UpdateBankFrame.this);
					return;
				}
				if (!Util.isPhone(phone.getText().toString())) {
					Util.show("您的手机号格式错误！",UpdateBankFrame.this);
					return;
					//？？？
				}
				Util.asynTask(UpdateBankFrame.this,"正在发送验证码",new IAsynTask() {
					
					@Override
					public void updateUI(Serializable runData) {
						// TODO Auto-generated method stub
						if (null==runData) {
							Util.show("网络错误，请重试！", UpdateBankFrame.this);
							phone.setCompoundDrawables(null, null, null, null);
							return;
						}
						if ((runData+"").equals("图形验证码不正确")) {
							yzmpic.setVisibility(View.VISIBLE);
							Picasso.with(UpdateBankFrame.this).load("http://" + Web.webImage+ "/ashx/ImgCode.ashx?action=imgcode&phone=" + phone.getText().toString()).skipMemoryCache().into(yzmpic_view);
							Util.show(runData+"", UpdateBankFrame.this);
							return;
						}
						if ((runData+"").startsWith("success:")) {
							String[] ayy=(runData+"").split(":");
							phone.setTag(-707,ayy[1]);
							Resources res=UpdateBankFrame.this.getResources();
							Drawable ok=res.getDrawable(R.drawable.registe_success);
							ok.setBounds(0,0,ok.getMinimumWidth(),ok.getMinimumHeight());
							smsState=ayy[1];
							phone.setCompoundDrawables(null, null, ok, null);
							if (Web.url==Web.test_url||Web.url==Web.test_url2) {
								code.setText(ayy[1]);
							}
							//开启线程
							new TimeThread().start();
						}else {
							Util.show(runData+"", UpdateBankFrame.this);
							phone.setCompoundDrawables(null, null, null, null);
						}
					}
					
					@Override
					public Serializable run() {
						// TODO Auto-generated method stub
						if (phone==null) {
							Log.e("phone", "phone");
						}
						if (yzmpic_code==null) {
							Log.e("yzmpic_code", "yzmpic_code");
						}
						Web web = new Web(Web.sendPhoneValidataCode,
								"userId=" + user.getUserId() + "&md5Pwd=" + user.getMd5Pwd() + "&phone="
										+ phone.getText().toString() + "&imgcode=" + yzmpic_code.getText().toString());
						return web.getPlan();
					}
				});
			}
			
		});
		
		bankType.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				// if (numbers.size()>0) {
				showSelectNumberDialog();
				// }else {

				// }
			}
		});
		bSub.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final String name = bName.getText().toString();
				final String account = bAccount.getText().toString();
				final String idcard = idCard.getText().toString();
				final String uPhone = phone.getText().toString();
				String bType_temp = bankType.getText().toString().trim();
				final String two = twoPwd.getText().toString();
				final String yzmcode=update_basic_user_info_code.getText().toString();
				
				if (bType_temp.equals("选择银行")) {
					Util.show("请选择银行", UpdateBankFrame.this);
					return;
				}
				for (Bank bank : list) {
					if (bank.getDesc().equals(bType_temp)) {
						bType_temp = bank.getId();
						break;
					}
				}
				final String bType = bType_temp;
				final String bankcard = banCard.getText().toString();
				if (Util.isNull(name)) {
					Util.show("请输入您的真实姓名.", UpdateBankFrame.this);
					return;
				}
				if (Util.isNull(account)) {
					Util.show("请输入您的开户名.", UpdateBankFrame.this);
					return;
				}
				if (Util.isNull(idcard)) {
					Util.show("请输入您的身份证.", UpdateBankFrame.this);
					return;
				}


                int levelid = Integer.parseInt(UserData.getUser().getLevelId());
				if (6 == levelid||4==levelid) {

				}else{
                    if (Util.isNull(uPhone)) {
                        Util.show("请输入您的手机号码.", UpdateBankFrame.this);
                        return;
                    }
                }


				if (Util.isNull(bankcard)) {
					Util.show("请输入您的银行卡号.", UpdateBankFrame.this);
					return;
				}
				if (Util.isNull(two)) {
					Util.show("请输入您的交易密码.", UpdateBankFrame.this);
					return;
				}
//				if (Util.isNull(yzmcode)) {
//					Util.show("请输入您的验证码.", UpdateBankFrame.this);
//					return;
//				}
				Util.asynTask(UpdateBankFrame.this, "正在完善您的银行信息...", new IAsynTask() {

					@Override
					public void updateUI(Serializable runData) {
						if ("success".equals(runData)) {
							Web.reDoLogin();
							// Util.showIntent("修改完成，是否需要去提现！",
							// UpdateBankFrame.this, RequestTX.class);
							finish();
						} else
							Util.show(runData + "", UpdateBankFrame.this);
					}

					@Override
					public Serializable run() {
						Web web = new Web(Web.updateBank,
								"userid=" + UserData.getUser().getUserId() + "&md5Pwd=" + UserData.getUser().getMd5Pwd()
										+ "&twoPwd=" + new MD5().getMD5ofStr(two) + "&accountName=" + Util.get(account)
										+ "&name=" + Util.get(name) + "&idCard=" + idcard + "&phone=" + uPhone
										+ "&bankType=" + bType + "&banCard=" + bankcard+"&yzmcode="+yzmcode);
						return web.getPlan();
					}
				});
			}
		});

		bClo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				banCard.setText("");
				bName.setText("");
				bAccount.setText("");
				idCard.setText("");
				phone.setText("");
				bankType.setText("");
				twoPwd.setText("");
			}
		});
	}
	
	//使用Handler更新UI
	private Handler handler=new Handler(){
		public void handleMessage(android.os.Message msg) {
			super.handleMessage(msg);
			if (707==msg.what) {
				if ("0".equals(msg.obj+"")) {
					send_code.setText("短信验证");
					send_code.setEnabled(true);
					s=60;
				}else {
					int ss=Util.getInt(msg.obj);
					if (10>ss) 
						ss=Integer.parseInt("0"+ss);
						send_code.setText("\u3000"+ss+"秒\u3000");
						send_code.setEnabled(false);
				}
			}
		}
	};
	
	//开线程
	class TimeThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			super.run();
			while (s>0) {
				s--;
				Message message=new Message();
				message.what=707;
				message.obj=s;
				handler.sendMessage(message);
				if (0==s) 
					break;
				try {
					this.sleep(1000);//睡1s
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void getBank() {
		Util.asynTask(this, "正在获取银行类型...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<Bank>> map = (HashMap<String, List<Bank>>) runData;
				List<Bank> list1 = map.get("list");
				List<String> data = new ArrayList<String>(list1.size());
				for (Bank bank : list1) {
					data.add(bank.getDesc());
				}
				list = list1;
				numbers = data;
				// showSelectNumberDialog();
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getBank, "");
				List<Bank> list1 = web.getList(Bank.class);
				HashMap<String, List<Bank>> map = new HashMap<String, List<Bank>>();
				map.put("list", list1);
				return map;
			}
		});
	}

	/**
	 * 弹出选择号码对话框
	 */
	private void showSelectNumberDialog() {
		// numbers = getNumbers();

		ListView lv = new ListView(this);
		lv.setBackgroundResource(R.drawable.icon_spinner_listview_background);
		// 隐藏滚动条
		lv.setVerticalScrollBarEnabled(false);
		// 让listView没有分割线
		lv.setDividerHeight(0);
		lv.setDivider(null);
		lv.setOnItemClickListener(this);

		adapter = new NumbersAdapter();
		lv.setAdapter(adapter);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		popupWindow = new PopupWindow(lv, bankType.getWidth() - 4, dm.heightPixels * 3 / 5);
		// 设置点击外部可以被关闭
		popupWindow.setOutsideTouchable(true);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());

		// 设置popupWindow可以得到焦点
		popupWindow.setFocusable(true);
		popupWindow.showAsDropDown(bankType, 2, -5); // 显示

	}

	class NumbersAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return numbers.size();
		}

		@Override
		public Object getItem(int position) {
			return numbers.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			NumberViewHolder mHolder = null;
			if (convertView == null) {
				mHolder = new NumberViewHolder();
				convertView = LayoutInflater.from(UpdateBankFrame.this).inflate(R.layout.item_spinner_numbers, null);
				mHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_number);
				convertView.setTag(mHolder);
			} else {
				mHolder = (NumberViewHolder) convertView.getTag();
			}
			mHolder.tvNumber.setText(numbers.get(position));
			return convertView;
		}

	}

	public class NumberViewHolder {
		public TextView tvNumber;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String number = numbers.get(position);
		bankType.setText(number);
		popupWindow.dismiss();
	}
}
