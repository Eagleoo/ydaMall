package com.mall.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.lidroid.xutils.view.annotation.event.OnFocusChange;
import com.mall.model.InviterInfo;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.util.IAsynTask;
import com.mall.util.MD5;
import com.mall.util.UserData;
import com.mall.util.Util;

import java.io.Serializable;

@ContentView(R.layout.sb_monettomoney)
public class SBMoneyToMoneyFrame extends Activity {


	@ViewInject(R.id.sbto_userInfo)
	private TextView userInfo;
	@ViewInject(R.id.sbto_sbbl)
	private TextView sbbl;
	@ViewInject(R.id.sbto_jfbl)
	private TextView jfbl;
	@ViewInject(R.id.sbto_sb)
	private TextView sbban;
	@ViewInject(R.id.sbto_jf)
	private TextView jfban;

	@ViewInject(R.id.sbto_userid)
	private EditText userid;
	@ViewInject(R.id.sbto_money)
	private EditText money;
	@ViewInject(R.id.sbto_twopwd)
	private EditText twopwd;
	@ViewInject(R.id.sbto_blLine)
	private LinearLayout blLine;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		Util.initTop(this, "消费转积分", Integer.MIN_VALUE, null);
		init();
	}
	
		
	private void init(){
		final User user = UserData.getUser();
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData){
					Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
					return ;
				}
				sbban.setText("商币余额："+runData+"");
			}
			
			@Override
			public Serializable run() {
				Web web = new Web(Web.getMoney,"userid="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd()+"&type=sb");
				return web.getPlan();
			}
		});
		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData){
					Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
					return ;
				}
				jfban.setText("消费券余额："+runData+"");
			}
			
			@Override
			public Serializable run() {
				Web web = new Web(Web.getMoney
						,"userid="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd()+"&type=han");
				return web.getPlan();
			}
		});
	}
	
	
	@OnFocusChange(R.id.sbto_userid)
	public void getUserInfo(View view,boolean focus){
		if(focus) return ;
		if(Util.isNull(userid.getText().toString()))
			return ;
		Util.asynTask(this, "正在获取会员信息...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData){
					Util.show("网络错误，请重试！",SBMoneyToMoneyFrame.this);
					return ;
				}
				InviterInfo ii = (InviterInfo)runData;
				if(Util.isNull(ii.getUserid())){
					userInfo.setText("用户资料：　");
					Util.show("该用户不存在", SBMoneyToMoneyFrame.this);
					return ;
				}
				userInfo.setVisibility(View.VISIBLE);
				String name = ii.getName();
				if (!Util.isNull(name) && 2 <= name.length()){
					name = name.substring(0, 1) + "*";
					if(3 <= ii.getName().length())
						name+=ii.getName().substring(ii.getName().length()-1);
				}
				String phone = ii.getPhone();
				if (!Util.isNull(phone))
					phone = phone.substring(0, 3)
							+ "****"
							+ phone.subSequence(phone.length() - 4,
									phone.length());
				userInfo.setText("用户资料：" + name+ "　" + phone);
			}

			@Override
			public Serializable run() {
				Web web = new Web(Web.getInviter, "userId="+ UserData.getUser().getUserId()+ "&md5Pwd="+ UserData.getUser().getMd5Pwd() + "&uid="
						+ Util.get(userid.getText().toString()));
				return web.getObject(InviterInfo.class);
			}
		});
	}
	
	@OnFocusChange(R.id.sbto_money)
	public void getShowMoney(View view ,boolean focus){
		if(focus) return ;
		if(Util.isNull(money.getText().toString()))
			return ;
		if(null != sbbl.getTag(-1) && null != jfbl.getTag(-1)){
			double sbp = Util.getDouble(sbbl.getTag(-1)+"");
			double jfp = Util.getDouble(jfbl.getTag(-1)+"");
			double m = Util.getDouble(money.getText().toString());
			sbbl.setText("商币："+Util.getDouble(sbp * m,2)+ "");
			jfbl.setText("消费券："+Util.getDouble(jfp * m,2)+ "");
			blLine.setVisibility(View.VISIBLE);
			return ;
		}
		final User user = UserData.getUser();
		Util.asynTask(SBMoneyToMoneyFrame.this,"正在获取赠送比例...", new IAsynTask() {
			  @Override
			  public void updateUI(Serializable runData) {
				  blLine.setVisibility(View.GONE);
				  if(null == runData){
					  Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
					  return ;
				  }
				  if((runData + "").contains("|,|")){
						blLine.setVisibility(View.VISIBLE);
						double sbp = Util.getDouble((runData + "").split("\\|,\\|")[0]);
						double jfp = Util.getDouble((runData + "").split("\\|,\\|")[1]);
						double m = Util.getDouble(money.getText().toString());
						sbbl.setText("商币："+Util.getDouble(sbp * m,2)+ "");
						jfbl.setText("消费券："+Util.getDouble(jfp * m,2)+ "");
						sbbl.setTag(-1,sbp+"");
						jfbl.setTag(-1,jfp+"");
				  }else{
					  Util.show(runData+"", SBMoneyToMoneyFrame.this);
				  }
			}
	
			@Override
			public Serializable run() {
				Web web = new Web(Web.allianService,Web.allian_getShopProportion
						,"userId="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd());				
				return web.getPlan();
			}
		});
	}
	
	@OnClick(R.id.sbto_updatebl)
	public void updateBLClick(View view){
		view.setEnabled(false);
		AlertDialog dialog = new AlertDialog.Builder(this).create();
		dialog.setTitle("设置赠送比例");
		View root = LayoutInflater.from(this).inflate(R.layout.update_sbtomoney_bl, null);
		
		final EditText sb_bl = (EditText)root.findViewById(R.id.update_sbbl);
		final EditText jf_bl = (EditText)root.findViewById(R.id.update_jfbl);
		Object sb_bl_value = sbbl.getTag(-1);
		Object jf_bl_value = jfbl.getTag(-1);
		sb_bl.setText(null == sb_bl_value?"":(Util.getDouble(sb_bl_value)*100)+"");
		jf_bl.setText(null == jf_bl_value?"":(Util.getDouble(jf_bl_value)*100)+"");
		dialog.setButton3("修改", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(Util.isNull(sb_bl.getText().toString())){
					Util.show("商币比例不能为空",SBMoneyToMoneyFrame.this);
					return ;
				}
				if(Util.isNull(jf_bl.getText().toString())){
					Util.show("消费券比例不能为空",SBMoneyToMoneyFrame.this);
					return ;
				}
				final User user = UserData.getUser();
				Util.asynTask(SBMoneyToMoneyFrame.this, "正在保存您的比例...", new IAsynTask() {
					@Override
					public void updateUI(Serializable runData) {
						if(null == runData){
							Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
							return ;
						}
						if("success".equals(runData+"")){
							sbbl.setTag(-1,Util.getDouble(sb_bl.getText().toString())/100);
							jfbl.setTag(-1,Util.getDouble(jf_bl.getText().toString())/100);
							Object sb_bl_value = sbbl.getTag(-1);
							Object jf_bl_value = jfbl.getTag(-1);
							sb_bl.setText(null == sb_bl_value?"":(Util.getDouble(sb_bl_value)*100)+"");
							jf_bl.setText(null == jf_bl_value?"":(Util.getDouble(jf_bl_value)*100)+"");
						}else{
							Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
						}
					}
					
					@Override
					public Serializable run() {
						Web web = new Web(Web.allianService,Web.allian_saveSendSet,"userid="+user.getUserId()+"&md5Pwd="+user.getMd5Pwd()+"&sb="+sb_bl.getText().toString()+"&jf="+jf_bl.getText().toString());
						return web.getPlan();
					}
				});
			}
		});
		dialog.setButton2("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				dialog.dismiss();
			}
		});
		dialog.setView(root);
		dialog.show();
		view.setEnabled(true);
	}

	@OnClick(R.id.sbto_submit)
	public void submitClick(View view){
		if(Util.isNull(userid.getText().toString())){
			Util.show("对方账户不能为空！", this);
			return;
		}
		if(Util.isNull(money.getText().toString())){
			Util.show("消费金额不能为空！", this);
			return;
		}
		if(Util.isNull(twopwd.getText().toString())){
			Util.show("交易密码不能为空！", this);
			return;
		}
		final User user = UserData.getUser();
		Util.asynTask(this, "正在为您转账...", new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				if(null == runData){
					Util.show("网络错误，请重试！", SBMoneyToMoneyFrame.this);
					return ;
				}
				String result=(String) runData;
				if(result.contains("success")){
					Util.showIntent("转账成功！",SBMoneyToMoneyFrame.this , AccountManagerFrame.class);
				}else{
					Util.show(runData+"", SBMoneyToMoneyFrame.this);
				}
			}
			@Override
			public Serializable run() {
				Web web = new Web(Web.allianService,Web.allian_doSendScore,"userId="
								+ user.getUserId()+ "&md5Pwd="
								+ user.getMd5Pwd()+ "&twoPwd="
								+ new MD5().getMD5ofStr(twopwd.getText().toString())
								+ "&toUserId="+ Util.get(userid.getText().toString())
								+ "&money="+ money.getText().toString());
				return web.getPlan();
			}
		});
	}
	
	@OnClick(R.id.sbto_reset)
	public void resetClick(View view){
		userid.setText("");
		money.setText("");
		twopwd.setText("");
		userInfo.setVisibility(View.GONE);
		blLine.setVisibility(View.GONE);
	}
}
