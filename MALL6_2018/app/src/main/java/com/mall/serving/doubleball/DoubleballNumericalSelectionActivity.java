package com.mall.serving.doubleball;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.model.User;
import com.mall.net.Web;
import com.mall.serving.doubleball.selectnumview.SelectNumView;
import com.mall.util.Combination;
import com.mall.util.IAsynTask;
import com.mall.util.UserData;
import com.mall.util.Util;
import com.mall.view.LoginFrame;
import com.mall.view.R;

/**
 * 双色球选号页面 <br>
 * 功能： <br>
 * 时间： 2014-7-18<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
@SuppressLint("NewApi")
@ContentView(R.layout.doubleball_numerical_selection)
public class DoubleballNumericalSelectionActivity extends Activity {

	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	@ViewInject(R.id.topright1)
	private ImageView topright;

	@ViewInject(R.id.tv_lottery)
	private TextView tv_lottery;
	@ViewInject(R.id.tv_number_red)
	private TextView tv_number_red;
	@ViewInject(R.id.tv_number_blue)
	private TextView tv_number_blue;

	@ViewInject(R.id.tv_salestop)
	private TextView tv_salestop;
	@ViewInject(R.id.tv_salestop_time)
	private TextView tv_salestop_time;

	@ViewInject(R.id.rg_select)
	private RadioGroup rg_select;

	@ViewInject(R.id.straight_area)
	private View straight_area;
	@ViewInject(R.id.drag_area)
	private View drag_area;

	@ViewInject(R.id.tv_select_money)
	private TextView tv_select_money;

	@ViewInject(R.id.tv_add_number_basket_count)
	private TextView tv_add_number_basket_count;

	// ×××××××××××××××××××××××直选区红球×××××××××××××××××××××××
	@ViewInject(R.id.cb_red_ball_straight01)
	private CheckBox cb_red_ball_straight01;
	@ViewInject(R.id.cb_red_ball_straight02)
	private CheckBox cb_red_ball_straight02;
	@ViewInject(R.id.cb_red_ball_straight03)
	private CheckBox cb_red_ball_straight03;
	@ViewInject(R.id.cb_red_ball_straight04)
	private CheckBox cb_red_ball_straight04;
	@ViewInject(R.id.cb_red_ball_straight05)
	private CheckBox cb_red_ball_straight05;
	@ViewInject(R.id.cb_red_ball_straight06)
	private CheckBox cb_red_ball_straight06;
	@ViewInject(R.id.cb_red_ball_straight07)
	private CheckBox cb_red_ball_straight07;
	@ViewInject(R.id.cb_red_ball_straight08)
	private CheckBox cb_red_ball_straight08;
	@ViewInject(R.id.cb_red_ball_straight09)
	private CheckBox cb_red_ball_straight09;
	@ViewInject(R.id.cb_red_ball_straight10)
	private CheckBox cb_red_ball_straight10;
	@ViewInject(R.id.cb_red_ball_straight11)
	private CheckBox cb_red_ball_straight11;
	@ViewInject(R.id.cb_red_ball_straight12)
	private CheckBox cb_red_ball_straight12;
	@ViewInject(R.id.cb_red_ball_straight13)
	private CheckBox cb_red_ball_straight13;
	@ViewInject(R.id.cb_red_ball_straight14)
	private CheckBox cb_red_ball_straight14;
	@ViewInject(R.id.cb_red_ball_straight15)
	private CheckBox cb_red_ball_straight15;
	@ViewInject(R.id.cb_red_ball_straight16)
	private CheckBox cb_red_ball_straight16;
	@ViewInject(R.id.cb_red_ball_straight17)
	private CheckBox cb_red_ball_straight17;
	@ViewInject(R.id.cb_red_ball_straight18)
	private CheckBox cb_red_ball_straight18;
	@ViewInject(R.id.cb_red_ball_straight19)
	private CheckBox cb_red_ball_straight19;
	@ViewInject(R.id.cb_red_ball_straight20)
	private CheckBox cb_red_ball_straight20;
	@ViewInject(R.id.cb_red_ball_straight21)
	private CheckBox cb_red_ball_straight21;
	@ViewInject(R.id.cb_red_ball_straight22)
	private CheckBox cb_red_ball_straight22;
	@ViewInject(R.id.cb_red_ball_straight23)
	private CheckBox cb_red_ball_straight23;
	@ViewInject(R.id.cb_red_ball_straight24)
	private CheckBox cb_red_ball_straight24;
	@ViewInject(R.id.cb_red_ball_straight25)
	private CheckBox cb_red_ball_straight25;
	@ViewInject(R.id.cb_red_ball_straight26)
	private CheckBox cb_red_ball_straight26;
	@ViewInject(R.id.cb_red_ball_straight27)
	private CheckBox cb_red_ball_straight27;
	@ViewInject(R.id.cb_red_ball_straight28)
	private CheckBox cb_red_ball_straight28;
	@ViewInject(R.id.cb_red_ball_straight29)
	private CheckBox cb_red_ball_straight29;
	@ViewInject(R.id.cb_red_ball_straight30)
	private CheckBox cb_red_ball_straight30;
	@ViewInject(R.id.cb_red_ball_straight31)
	private CheckBox cb_red_ball_straight31;
	@ViewInject(R.id.cb_red_ball_straight32)
	private CheckBox cb_red_ball_straight32;
	@ViewInject(R.id.cb_red_ball_straight33)
	private CheckBox cb_red_ball_straight33;

	// ×××××××××××××××××××××××胆选区红球×××××××××××××××××××××××
	@ViewInject(R.id.cb_red_ball_courage01)
	private CheckBox cb_red_ball_courage01;
	@ViewInject(R.id.cb_red_ball_courage02)
	private CheckBox cb_red_ball_courage02;
	@ViewInject(R.id.cb_red_ball_courage03)
	private CheckBox cb_red_ball_courage03;
	@ViewInject(R.id.cb_red_ball_courage04)
	private CheckBox cb_red_ball_courage04;
	@ViewInject(R.id.cb_red_ball_courage05)
	private CheckBox cb_red_ball_courage05;
	@ViewInject(R.id.cb_red_ball_courage06)
	private CheckBox cb_red_ball_courage06;
	@ViewInject(R.id.cb_red_ball_courage07)
	private CheckBox cb_red_ball_courage07;
	@ViewInject(R.id.cb_red_ball_courage08)
	private CheckBox cb_red_ball_courage08;
	@ViewInject(R.id.cb_red_ball_courage09)
	private CheckBox cb_red_ball_courage09;
	@ViewInject(R.id.cb_red_ball_courage10)
	private CheckBox cb_red_ball_courage10;
	@ViewInject(R.id.cb_red_ball_courage11)
	private CheckBox cb_red_ball_courage11;
	@ViewInject(R.id.cb_red_ball_courage12)
	private CheckBox cb_red_ball_courage12;
	@ViewInject(R.id.cb_red_ball_courage13)
	private CheckBox cb_red_ball_courage13;
	@ViewInject(R.id.cb_red_ball_courage14)
	private CheckBox cb_red_ball_courage14;
	@ViewInject(R.id.cb_red_ball_courage15)
	private CheckBox cb_red_ball_courage15;
	@ViewInject(R.id.cb_red_ball_courage16)
	private CheckBox cb_red_ball_courage16;
	@ViewInject(R.id.cb_red_ball_courage17)
	private CheckBox cb_red_ball_courage17;
	@ViewInject(R.id.cb_red_ball_courage18)
	private CheckBox cb_red_ball_courage18;
	@ViewInject(R.id.cb_red_ball_courage19)
	private CheckBox cb_red_ball_courage19;
	@ViewInject(R.id.cb_red_ball_courage20)
	private CheckBox cb_red_ball_courage20;
	@ViewInject(R.id.cb_red_ball_courage21)
	private CheckBox cb_red_ball_courage21;
	@ViewInject(R.id.cb_red_ball_courage22)
	private CheckBox cb_red_ball_courage22;
	@ViewInject(R.id.cb_red_ball_courage23)
	private CheckBox cb_red_ball_courage23;
	@ViewInject(R.id.cb_red_ball_courage24)
	private CheckBox cb_red_ball_courage24;
	@ViewInject(R.id.cb_red_ball_courage25)
	private CheckBox cb_red_ball_courage25;
	@ViewInject(R.id.cb_red_ball_courage26)
	private CheckBox cb_red_ball_courage26;
	@ViewInject(R.id.cb_red_ball_courage27)
	private CheckBox cb_red_ball_courage27;
	@ViewInject(R.id.cb_red_ball_courage28)
	private CheckBox cb_red_ball_courage28;
	@ViewInject(R.id.cb_red_ball_courage29)
	private CheckBox cb_red_ball_courage29;
	@ViewInject(R.id.cb_red_ball_courage30)
	private CheckBox cb_red_ball_courage30;
	@ViewInject(R.id.cb_red_ball_courage31)
	private CheckBox cb_red_ball_courage31;
	@ViewInject(R.id.cb_red_ball_courage32)
	private CheckBox cb_red_ball_courage32;
	@ViewInject(R.id.cb_red_ball_courage33)
	private CheckBox cb_red_ball_courage33;

	// ×××××××××××××××××××××××拖选区红球×××××××××××××××××××××××
	@ViewInject(R.id.cb_red_ball_drag01)
	private CheckBox cb_red_ball_drag01;
	@ViewInject(R.id.cb_red_ball_drag02)
	private CheckBox cb_red_ball_drag02;
	@ViewInject(R.id.cb_red_ball_drag03)
	private CheckBox cb_red_ball_drag03;
	@ViewInject(R.id.cb_red_ball_drag04)
	private CheckBox cb_red_ball_drag04;
	@ViewInject(R.id.cb_red_ball_drag05)
	private CheckBox cb_red_ball_drag05;
	@ViewInject(R.id.cb_red_ball_drag06)
	private CheckBox cb_red_ball_drag06;
	@ViewInject(R.id.cb_red_ball_drag07)
	private CheckBox cb_red_ball_drag07;
	@ViewInject(R.id.cb_red_ball_drag08)
	private CheckBox cb_red_ball_drag08;
	@ViewInject(R.id.cb_red_ball_drag09)
	private CheckBox cb_red_ball_drag09;
	@ViewInject(R.id.cb_red_ball_drag10)
	private CheckBox cb_red_ball_drag10;
	@ViewInject(R.id.cb_red_ball_drag11)
	private CheckBox cb_red_ball_drag11;
	@ViewInject(R.id.cb_red_ball_drag12)
	private CheckBox cb_red_ball_drag12;
	@ViewInject(R.id.cb_red_ball_drag13)
	private CheckBox cb_red_ball_drag13;
	@ViewInject(R.id.cb_red_ball_drag14)
	private CheckBox cb_red_ball_drag14;
	@ViewInject(R.id.cb_red_ball_drag15)
	private CheckBox cb_red_ball_drag15;
	@ViewInject(R.id.cb_red_ball_drag16)
	private CheckBox cb_red_ball_drag16;
	@ViewInject(R.id.cb_red_ball_drag17)
	private CheckBox cb_red_ball_drag17;
	@ViewInject(R.id.cb_red_ball_drag18)
	private CheckBox cb_red_ball_drag18;
	@ViewInject(R.id.cb_red_ball_drag19)
	private CheckBox cb_red_ball_drag19;
	@ViewInject(R.id.cb_red_ball_drag20)
	private CheckBox cb_red_ball_drag20;
	@ViewInject(R.id.cb_red_ball_drag21)
	private CheckBox cb_red_ball_drag21;
	@ViewInject(R.id.cb_red_ball_drag22)
	private CheckBox cb_red_ball_drag22;
	@ViewInject(R.id.cb_red_ball_drag23)
	private CheckBox cb_red_ball_drag23;
	@ViewInject(R.id.cb_red_ball_drag24)
	private CheckBox cb_red_ball_drag24;
	@ViewInject(R.id.cb_red_ball_drag25)
	private CheckBox cb_red_ball_drag25;
	@ViewInject(R.id.cb_red_ball_drag26)
	private CheckBox cb_red_ball_drag26;
	@ViewInject(R.id.cb_red_ball_drag27)
	private CheckBox cb_red_ball_drag27;
	@ViewInject(R.id.cb_red_ball_drag28)
	private CheckBox cb_red_ball_drag28;
	@ViewInject(R.id.cb_red_ball_drag29)
	private CheckBox cb_red_ball_drag29;
	@ViewInject(R.id.cb_red_ball_drag30)
	private CheckBox cb_red_ball_drag30;
	@ViewInject(R.id.cb_red_ball_drag31)
	private CheckBox cb_red_ball_drag31;
	@ViewInject(R.id.cb_red_ball_drag32)
	private CheckBox cb_red_ball_drag32;
	@ViewInject(R.id.cb_red_ball_drag33)
	private CheckBox cb_red_ball_drag33;

	// ×××××××××××××××××××××××直选区蓝球×××××××××××××××××××××××
	@ViewInject(R.id.cb_blue_ball_straight01)
	private CheckBox cb_blue_ball_straight01;

	@ViewInject(R.id.cb_blue_ball_straight02)
	private CheckBox cb_blue_ball_straight02;

	@ViewInject(R.id.cb_blue_ball_straight03)
	private CheckBox cb_blue_ball_straight03;

	@ViewInject(R.id.cb_blue_ball_straight04)
	private CheckBox cb_blue_ball_straight04;

	@ViewInject(R.id.cb_blue_ball_straight05)
	private CheckBox cb_blue_ball_straight05;

	@ViewInject(R.id.cb_blue_ball_straight06)
	private CheckBox cb_blue_ball_straight06;

	@ViewInject(R.id.cb_blue_ball_straight07)
	private CheckBox cb_blue_ball_straight07;

	@ViewInject(R.id.cb_blue_ball_straight08)
	private CheckBox cb_blue_ball_straight08;

	@ViewInject(R.id.cb_blue_ball_straight09)
	private CheckBox cb_blue_ball_straight09;

	@ViewInject(R.id.cb_blue_ball_straight10)
	private CheckBox cb_blue_ball_straight10;

	@ViewInject(R.id.cb_blue_ball_straight11)
	private CheckBox cb_blue_ball_straight11;

	@ViewInject(R.id.cb_blue_ball_straight12)
	private CheckBox cb_blue_ball_straight12;

	@ViewInject(R.id.cb_blue_ball_straight13)
	private CheckBox cb_blue_ball_straight13;

	@ViewInject(R.id.cb_blue_ball_straight14)
	private CheckBox cb_blue_ball_straight14;

	@ViewInject(R.id.cb_blue_ball_straight15)
	private CheckBox cb_blue_ball_straight15;

	@ViewInject(R.id.cb_blue_ball_straight16)
	private CheckBox cb_blue_ball_straight16;
	// ×××××××××××××××××××××××拖区蓝球××××××××××××××××××××
	@ViewInject(R.id.cb_blue_ball_drag01)
	private CheckBox cb_blue_ball_drag01;

	@ViewInject(R.id.cb_blue_ball_drag02)
	private CheckBox cb_blue_ball_drag02;

	@ViewInject(R.id.cb_blue_ball_drag03)
	private CheckBox cb_blue_ball_drag03;

	@ViewInject(R.id.cb_blue_ball_drag04)
	private CheckBox cb_blue_ball_drag04;

	@ViewInject(R.id.cb_blue_ball_drag05)
	private CheckBox cb_blue_ball_drag05;

	@ViewInject(R.id.cb_blue_ball_drag06)
	private CheckBox cb_blue_ball_drag06;

	@ViewInject(R.id.cb_blue_ball_drag07)
	private CheckBox cb_blue_ball_drag07;

	@ViewInject(R.id.cb_blue_ball_drag08)
	private CheckBox cb_blue_ball_drag08;

	@ViewInject(R.id.cb_blue_ball_drag09)
	private CheckBox cb_blue_ball_drag09;

	@ViewInject(R.id.cb_blue_ball_drag10)
	private CheckBox cb_blue_ball_drag10;

	@ViewInject(R.id.cb_blue_ball_drag11)
	private CheckBox cb_blue_ball_drag11;

	@ViewInject(R.id.cb_blue_ball_drag12)
	private CheckBox cb_blue_ball_drag12;

	@ViewInject(R.id.cb_blue_ball_drag13)
	private CheckBox cb_blue_ball_drag13;

	@ViewInject(R.id.cb_blue_ball_drag14)
	private CheckBox cb_blue_ball_drag14;

	@ViewInject(R.id.cb_blue_ball_drag15)
	private CheckBox cb_blue_ball_drag15;

	@ViewInject(R.id.cb_blue_ball_drag16)
	private CheckBox cb_blue_ball_drag16;

	private List<String> redBallStraightList = new ArrayList<String>();
	private List<String> redBallCourageList = new ArrayList<String>();
	private List<String> redBallDragList = new ArrayList<String>();
	private List<String> blueBallStraightList = new ArrayList<String>();
	private List<String> blueBallDragList = new ArrayList<String>();
	// 号码篮

	private List<Bettingdata> basketArrayList = new ArrayList<Bettingdata>();

	private CheckBox[] redBallCourageCB;
	private CheckBox[] redBallDragCB;
	private CheckBox[] blueBallDragCB;
	private CheckBox[] redBallStraightCB;
	private CheckBox[] blueBallStraightCB;
	// 储存当前是直选还是胆拖区的状态
	private boolean isStraight = true;
	// 是否从其它页面传数据过来
	private boolean isFromOther = false;
	// 最大投注数
	private final int bettingMax = 50;
	// 篮子里的注数
	private int basketNum;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);

		redBallCourageCB = new CheckBox[] { cb_red_ball_courage01,
				cb_red_ball_courage02, cb_red_ball_courage03,
				cb_red_ball_courage04, cb_red_ball_courage05,
				cb_red_ball_courage06, cb_red_ball_courage07,
				cb_red_ball_courage08, cb_red_ball_courage09,
				cb_red_ball_courage10, cb_red_ball_courage11,
				cb_red_ball_courage12, cb_red_ball_courage13,
				cb_red_ball_courage14, cb_red_ball_courage15,
				cb_red_ball_courage16, cb_red_ball_courage17,
				cb_red_ball_courage18, cb_red_ball_courage19,
				cb_red_ball_courage20, cb_red_ball_courage21,
				cb_red_ball_courage22, cb_red_ball_courage23,
				cb_red_ball_courage24, cb_red_ball_courage25,
				cb_red_ball_courage26, cb_red_ball_courage27,
				cb_red_ball_courage28, cb_red_ball_courage29,
				cb_red_ball_courage30, cb_red_ball_courage31,
				cb_red_ball_courage32, cb_red_ball_courage33 };

		redBallDragCB = new CheckBox[] { cb_red_ball_drag01,
				cb_red_ball_drag02, cb_red_ball_drag03, cb_red_ball_drag04,
				cb_red_ball_drag05, cb_red_ball_drag06, cb_red_ball_drag07,
				cb_red_ball_drag08, cb_red_ball_drag09, cb_red_ball_drag10,
				cb_red_ball_drag11, cb_red_ball_drag12, cb_red_ball_drag13,
				cb_red_ball_drag14, cb_red_ball_drag15, cb_red_ball_drag16,
				cb_red_ball_drag17, cb_red_ball_drag18, cb_red_ball_drag19,
				cb_red_ball_drag20, cb_red_ball_drag21, cb_red_ball_drag22,
				cb_red_ball_drag23, cb_red_ball_drag24, cb_red_ball_drag25,
				cb_red_ball_drag26, cb_red_ball_drag27, cb_red_ball_drag28,
				cb_red_ball_drag29, cb_red_ball_drag30, cb_red_ball_drag31,
				cb_red_ball_drag32, cb_red_ball_drag33 };

		blueBallDragCB = new CheckBox[] { cb_blue_ball_drag01,
				cb_blue_ball_drag02, cb_blue_ball_drag03, cb_blue_ball_drag04,
				cb_blue_ball_drag05, cb_blue_ball_drag06, cb_blue_ball_drag07,
				cb_blue_ball_drag08, cb_blue_ball_drag09, cb_blue_ball_drag10,
				cb_blue_ball_drag11, cb_blue_ball_drag12, cb_blue_ball_drag13,
				cb_blue_ball_drag14, cb_blue_ball_drag15, cb_blue_ball_drag16 };

		redBallStraightCB = new CheckBox[] { cb_red_ball_straight01,
				cb_red_ball_straight02, cb_red_ball_straight03,
				cb_red_ball_straight04, cb_red_ball_straight05,
				cb_red_ball_straight06, cb_red_ball_straight07,
				cb_red_ball_straight08, cb_red_ball_straight09,
				cb_red_ball_straight10, cb_red_ball_straight11,
				cb_red_ball_straight12, cb_red_ball_straight13,
				cb_red_ball_straight14, cb_red_ball_straight15,
				cb_red_ball_straight16, cb_red_ball_straight17,
				cb_red_ball_straight18, cb_red_ball_straight19,
				cb_red_ball_straight20, cb_red_ball_straight21,
				cb_red_ball_straight22, cb_red_ball_straight23,
				cb_red_ball_straight24, cb_red_ball_straight25,
				cb_red_ball_straight26, cb_red_ball_straight27,
				cb_red_ball_straight28, cb_red_ball_straight29,
				cb_red_ball_straight30, cb_red_ball_straight31,
				cb_red_ball_straight32, cb_red_ball_straight33 };
		blueBallStraightCB = new CheckBox[] { cb_blue_ball_straight01,
				cb_blue_ball_straight02, cb_blue_ball_straight03,
				cb_blue_ball_straight04, cb_blue_ball_straight05,
				cb_blue_ball_straight06, cb_blue_ball_straight07,
				cb_blue_ball_straight08, cb_blue_ball_straight09,
				cb_blue_ball_straight10, cb_blue_ball_straight11,
				cb_blue_ball_straight12, cb_blue_ball_straight13,
				cb_blue_ball_straight14, cb_blue_ball_straight15,
				cb_blue_ball_straight16 };

		setView();
		setListenner();

		refreshLotteryData();
	}

	/**
	 * 返回点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.topback)
	public void topback(View v) {
		finish();
	}

	/**
	 * 菜单点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.topright1)
	public void topright(View v) {
		new MenuPopupWindow(this, v);

	}

	/**
	 * 投注点击事件，点击跳转
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_doubleball_betting)
	public void doubleballBetting(View v) {
		if (Web.issueNumber == 0) {
			Util.show("未获取到最新彩期，请刷新彩期！", this);
			return;
		}
		if (basketNum <= 0) {
			if (isStraight) {
				int bettingNum = computeStraightSum();
				if (redBallStraightList.size() < 6
						|| blueBallStraightList.size() < 1) {
					Util.show("请选择至少六个红球一个蓝球", this);
					return;

				} else if (bettingNum > 10000) {
					Util.show("每一笔投注不能超过20000元", this);
					return;
				}
			} else {
				int bettingNum = computeDragSum();
				int redDragSize = redBallDragList.size();
				int redCourageSize = redBallCourageList.size();

				if (redCourageSize < 1 || redCourageSize + redDragSize < 7
						|| blueBallDragList.size() < 1) {
					Util.show("胆区至少一个，红球至少选七个，蓝球至少选择一个", this);
					return;
				} else if (bettingNum > 10000) {
					Util.show("每一笔投注不能超过20000元", this);
					return;
				}
			}

		}

		addBasket();

		Intent intent = new Intent(this, DoubleballBettingActivity.class);
		Bundle bundle = new Bundle();

		bundle.putSerializable("basketArrayList",
				(Serializable) basketArrayList);

		intent.putExtras(bundle);

		if (isFromOther) {
			setResult(RESULT_OK, intent);
			finish();

		} else {
			startActivity(intent);
		}

	}

	@Override
	protected void onResume() {
		resetAll();
		basketArrayList.clear();
		if (!isFromOther) {
			basketNum = 0;
			tv_add_number_basket_count.setText(basketNum + "");
		}

		super.onResume();
	}

	/**
	 * 加入号码篮
	 */
	@OnClick(R.id.tv_add_number_basket)
	public void addNumberBasket(View v) {

		if (isStraight) {
			int bettingNum = computeStraightSum();
			if (redBallStraightList.size() < 6
					|| blueBallStraightList.size() < 1) {
				Util.show("请选择至少六个红球一个蓝球", this);
				return;

			} else if (bettingNum > 10000) {
				Util.show("每一注不能超过20000元", this);
				return;
			}
		} else {
			int bettingNum = computeDragSum();
			int redDragSize = redBallDragList.size();
			int redCourageSize = redBallCourageList.size();

			if (redCourageSize < 1 || redCourageSize + redDragSize < 7
					|| blueBallDragList.size() < 1) {
				Util.show("胆区至少一个，红球至少选七个，蓝球至少选择一个", this);
				return;
			} else if (bettingNum > 10000) {
				Util.show("每一注不能超过20000元", this);
				return;
			}
		}

		addBasket();

	}

	/**
	 * 机选点击事件
	 * 
	 * @param v
	 */
	@OnClick(R.id.tv_machine_choose)
	public void machineChoose(View v) {

		new MachineChoosePopupWindow(this, v);
	}

	/**
	 * 获取随机数，并显示出来
	 */
	public void machineRandom(int red, int blue) {
		redBallStraightList.clear();
		blueBallStraightList.clear();
		checkedClear(redBallStraightCB);
		checkedClear(blueBallStraightCB);
		// 红球机选
		redBallStraightList = Combination.machineNumber(red, 33);

		// 蓝球机选
		blueBallStraightList = Combination.machineNumber(blue, 16);
		setCheckBoxChecked(redBallStraightCB, redBallStraightList);
		setCheckBoxChecked(blueBallStraightCB, blueBallStraightList);

		setStraightResult();
	}

	/**
	 * 将机选的结果显示在checkbox上
	 */
	private void setCheckBoxChecked(CheckBox[] cb, List list) {
		for (int i = 0; i < cb.length; i++) {
			for (int j = 0; j < list.size(); j++) {
				if (cb[i].getText().toString().equals(list.get(j).toString())) {

					cb[i].setChecked(true);

				}
			}

		}
	}

	/**
	 * 功能：菜单 <br>
	 * 时间： 2014-7-19<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class MenuPopupWindow extends PopupWindow {

		public MenuPopupWindow(final Context context, View parent) {
			super(context);
			View inflate = LayoutInflater.from(context).inflate(
					R.layout.doubleball_menu, null);

			final TextView tv_menu_refresh = (TextView) inflate
					.findViewById(R.id.tv_menu_refresh);
			final TextView tv_menu_recent = (TextView) inflate
					.findViewById(R.id.tv_menu_recent);
			final TextView tv_menu_introduce = (TextView) inflate
					.findViewById(R.id.tv_menu_introduce);
			final TextView tv_menu_mybetting = (TextView) inflate
					.findViewById(R.id.tv_menu_mybetting);

			View.OnClickListener onClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (v == tv_menu_refresh) {
						refreshLotteryData();
					} else if (v == tv_menu_recent) {
						startActivity(new Intent(
								DoubleballNumericalSelectionActivity.this,
								DoubleballRecentLotteryActivity.class));
					} else if (v == tv_menu_introduce) {
						dismiss();
						new PlayIntroducePopupWindow(
								DoubleballNumericalSelectionActivity.this, v);
					} else if (v == tv_menu_mybetting) {
						User user = UserData.getUser();
						if (user == null) {

							new DoubleballDialog("您还没登录，现在去登录吗？",
									DoubleballNumericalSelectionActivity.this,
									"确定", "取消", new OnClickListener() {

										@Override
										public void onClick(View v) {
											Intent intent = new Intent(
													DoubleballNumericalSelectionActivity.this,
													LoginFrame.class);

											startActivity(intent);

										}
									}, null).show();

						} else {
							Intent intent = new Intent(
									DoubleballNumericalSelectionActivity.this,
									DoubleballMyLotteryOrderInfoActivity.class);

							startActivity(intent);
						}

					}
					dismiss();
				}

			};

			tv_menu_introduce.setOnClickListener(onClickListener);
			tv_menu_mybetting.setOnClickListener(onClickListener);
			tv_menu_recent.setOnClickListener(onClickListener);
			tv_menu_refresh.setOnClickListener(onClickListener);

			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(inflate);

			showAsDropDown(parent, 0, 0);
		}

	}

	/**
	 * 功能：玩法介绍 <br>
	 * 时间： 2014-7-21<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class PlayIntroducePopupWindow extends PopupWindow {
		public PlayIntroducePopupWindow(final Context context, View parent) {
			super(context);
			View inflate = LayoutInflater.from(context).inflate(
					R.layout.doubleball_introduce_popupwindow, null);

			TextView tv_introduce_title = (TextView) inflate
					.findViewById(R.id.tv_introduce_title);
			TextView tv_introduce_content = (TextView) inflate
					.findViewById(R.id.tv_introduce_content);
			TextView tv_introduce_sure = (TextView) inflate
					.findViewById(R.id.tv_introduce_sure);
			tv_introduce_title.setText("玩法介绍");
			tv_introduce_content.setText(R.string.play_way_introduce);

			tv_introduce_sure.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					dismiss();

				}
			});

			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(inflate);

			showAtLocation(parent, Gravity.CENTER, 0, 0);
		}

	}

	/**
	 * 机选的popupwindow 功能： <br>
	 * 时间： 2014-7-18<br>
	 * 备注： <br>
	 * 
	 * @author
	 * 
	 */
	class MachineChoosePopupWindow extends PopupWindow {

		private int red = 6;
		private int blue = 1;

		public MachineChoosePopupWindow(final Context context, View parent) {

			super(context);
			View inflate = LayoutInflater.from(context).inflate(
					R.layout.doubleball_machine_choose_popupwindow, null);
			setWidth(LayoutParams.MATCH_PARENT);
			setHeight(LayoutParams.MATCH_PARENT);
			setBackgroundDrawable(new BitmapDrawable());
			setFocusable(true);
			setOutsideTouchable(true);
			setContentView(inflate);

			final SelectNumView voip_machine_red = (SelectNumView) inflate
					.findViewById(R.id.voip_machine_red);
			final SelectNumView voip_machine_blue = (SelectNumView) inflate
					.findViewById(R.id.voip_machine_blue);

			voip_machine_red.setCount(16);
			voip_machine_red.setRed(true);
			voip_machine_red.creat();
			voip_machine_blue.setCount(16);
			voip_machine_blue.setRed(false);
			voip_machine_blue.creat();

			final TextView tv_machine_cancle = (TextView) inflate
					.findViewById(R.id.tv_machine_cancle);
			final TextView tv_machine_sure = (TextView) inflate
					.findViewById(R.id.tv_machine_sure);

			OnClickListener clickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {

					if (v == tv_machine_sure) {
						int sum = 0;
						red = voip_machine_red.getNum();
						blue = voip_machine_blue.getNum();
						if (red >= 6 && blue >= 1) {

							int redcnk = cnk(red, 6);

							sum = redcnk * blue;
						}
						if (sum > 10000) {
							Util.show("每一笔投注不能超过20000元",
									DoubleballNumericalSelectionActivity.this);
						} else {
							DoubleballNumericalSelectionActivity activity = (DoubleballNumericalSelectionActivity) context;

							activity.machineRandom(red, blue);
							dismiss();
						}

					} else {
						dismiss();
					}

				}
			};

			tv_machine_sure.setOnClickListener(clickListener);
			tv_machine_cancle.setOnClickListener(clickListener);

			showAtLocation(parent, Gravity.CENTER, 0, 0);

		}

	}

	/**
	 * 删除键的功能，重置为未选中状态。
	 * 
	 * @param v
	 */
	@OnClick(R.id.iv_doubleball_delete)
	public void doubleballDelete(View v) {
		resetAll();

	}

	/**
	 * 重置
	 */
	private void resetAll() {
		if (isStraight) {
			checkedClear(redBallStraightCB);
			checkedClear(blueBallStraightCB);
			redBallStraightList.clear();
			blueBallStraightList.clear();
			setStraightResult();
		} else {

			checkedClear(blueBallDragCB);
			checkedClear(redBallCourageCB);
			checkedClear(redBallDragCB);
			blueBallDragList.clear();
			redBallDragList.clear();
			redBallCourageList.clear();
			setDragResult();
		}

	}

	/**
	 * 使CheckBox数组了里的CheckBox都设置为未选中
	 * 
	 * @param cb
	 */
	private void checkedClear(CheckBox[] cb) {
		for (int i = 0; i < cb.length; i++) {
			cb[i].setChecked(false);
			cb[i].setEnabled(true);
		}
	}

	/**
	 * 直选区红球点击事件
	 * 
	 * @param v
	 */
	@OnClick({ R.id.cb_red_ball_straight01, R.id.cb_red_ball_straight02,
			R.id.cb_red_ball_straight03, R.id.cb_red_ball_straight04,
			R.id.cb_red_ball_straight05, R.id.cb_red_ball_straight06,
			R.id.cb_red_ball_straight07, R.id.cb_red_ball_straight08,
			R.id.cb_red_ball_straight09, R.id.cb_red_ball_straight10,
			R.id.cb_red_ball_straight11, R.id.cb_red_ball_straight12,
			R.id.cb_red_ball_straight13, R.id.cb_red_ball_straight14,
			R.id.cb_red_ball_straight15, R.id.cb_red_ball_straight16,
			R.id.cb_red_ball_straight17, R.id.cb_red_ball_straight18,
			R.id.cb_red_ball_straight19, R.id.cb_red_ball_straight20,
			R.id.cb_red_ball_straight21, R.id.cb_red_ball_straight22,
			R.id.cb_red_ball_straight23, R.id.cb_red_ball_straight24,
			R.id.cb_red_ball_straight25, R.id.cb_red_ball_straight26,
			R.id.cb_red_ball_straight27, R.id.cb_red_ball_straight28,
			R.id.cb_red_ball_straight29, R.id.cb_red_ball_straight30,
			R.id.cb_red_ball_straight31, R.id.cb_red_ball_straight32,
			R.id.cb_red_ball_straight33 })
	public void redBallStraight(View v) {
		CheckBox cb = (CheckBox) v;
		String string = cb.getText().toString();

		if (cb.isChecked()) {
			redBallStraightList.add(string);
		} else {
			redBallStraightList.remove(string);
		}
		setStraightResult();
	}

	/**
	 * 直选区蓝球点击事件
	 * 
	 * @param v
	 */
	@OnClick({ R.id.cb_blue_ball_straight01, R.id.cb_blue_ball_straight02,
			R.id.cb_blue_ball_straight03, R.id.cb_blue_ball_straight04,
			R.id.cb_blue_ball_straight05, R.id.cb_blue_ball_straight06,
			R.id.cb_blue_ball_straight07, R.id.cb_blue_ball_straight08,
			R.id.cb_blue_ball_straight09, R.id.cb_blue_ball_straight10,
			R.id.cb_blue_ball_straight11, R.id.cb_blue_ball_straight12,
			R.id.cb_blue_ball_straight13, R.id.cb_blue_ball_straight14,
			R.id.cb_blue_ball_straight15, R.id.cb_blue_ball_straight16 })
	public void blueBallStraight(View v) {
		CheckBox cb = (CheckBox) v;
		String string = cb.getText().toString();

		if (cb.isChecked()) {
			blueBallStraightList.add(string);
		} else {
			blueBallStraightList.remove(string);
		}
		setStraightResult();
	}

	/**
	 * 胆区红球点击事件
	 * 
	 * @param v
	 */
	@OnClick({ R.id.cb_red_ball_courage01, R.id.cb_red_ball_courage02,
			R.id.cb_red_ball_courage03, R.id.cb_red_ball_courage04,
			R.id.cb_red_ball_courage05, R.id.cb_red_ball_courage06,
			R.id.cb_red_ball_courage07, R.id.cb_red_ball_courage08,
			R.id.cb_red_ball_courage09, R.id.cb_red_ball_courage10,
			R.id.cb_red_ball_courage11, R.id.cb_red_ball_courage12,
			R.id.cb_red_ball_courage13, R.id.cb_red_ball_courage14,
			R.id.cb_red_ball_courage15, R.id.cb_red_ball_courage16,
			R.id.cb_red_ball_courage17, R.id.cb_red_ball_courage18,
			R.id.cb_red_ball_courage19, R.id.cb_red_ball_courage20,
			R.id.cb_red_ball_courage21, R.id.cb_red_ball_courage22,
			R.id.cb_red_ball_courage23, R.id.cb_red_ball_courage24,
			R.id.cb_red_ball_courage25, R.id.cb_red_ball_courage26,
			R.id.cb_red_ball_courage27, R.id.cb_red_ball_courage28,
			R.id.cb_red_ball_courage29, R.id.cb_red_ball_courage30,
			R.id.cb_red_ball_courage31, R.id.cb_red_ball_courage32,
			R.id.cb_red_ball_courage33 })
	public void redBallCourage(View v) {
		CheckBox cb = (CheckBox) v;
		String string = cb.getText().toString();

		if (cb.isChecked()) {
			// 胆区选中后拖区相应数字取消选中
			for (int i = 0; i < redBallCourageCB.length; i++) {
				String string2 = redBallCourageCB[i].getText().toString();
				if (string.equals(string2)) {
					redBallDragCB[i].setChecked(false);

					// 拖区移出一个选中后小于20时，其他可点击
					if (redBallDragList.size() == 20) {
						for (int j = 0; j < redBallDragCB.length; j++) {

							redBallDragCB[j].setEnabled(true);

						}

					}
					redBallDragList.remove(string);

				}
			}

			// 胆区选中数为5时，其它都不可点击
			redBallCourageList.add(string);
			if (redBallCourageList.size() >= 5) {
				Util.show("已选5个，除非取消不然其它不可点击", this);
				for (int i = 0; i < redBallCourageCB.length; i++) {

					String cbsString = redBallCourageCB[i].getText().toString();
					for (int j = 0; j < redBallCourageList.size(); j++) {
						String string2 = redBallCourageList.get(j);

						if (cbsString.equals(string2)) {

							redBallCourageCB[i].setEnabled(true);
							break;

						} else {
							redBallCourageCB[i].setEnabled(false);
						}
					}
				}
			}
		} else {

			// 胆区移出一个选中后小于5时，其他可点击
			if (redBallCourageList.size() == 5) {
				for (int i = 0; i < redBallCourageCB.length; i++) {

					redBallCourageCB[i].setEnabled(true);

				}
			}
			redBallCourageList.remove(string);
		}
		setDragResult();
	}

	/**
	 * 拖区红球点击事件
	 * 
	 * @param v
	 */
	@OnClick({ R.id.cb_red_ball_drag01, R.id.cb_red_ball_drag02,
			R.id.cb_red_ball_drag03, R.id.cb_red_ball_drag04,
			R.id.cb_red_ball_drag05, R.id.cb_red_ball_drag06,
			R.id.cb_red_ball_drag07, R.id.cb_red_ball_drag08,
			R.id.cb_red_ball_drag09, R.id.cb_red_ball_drag10,
			R.id.cb_red_ball_drag11, R.id.cb_red_ball_drag12,
			R.id.cb_red_ball_drag13, R.id.cb_red_ball_drag14,
			R.id.cb_red_ball_drag15, R.id.cb_red_ball_drag16,
			R.id.cb_red_ball_drag17, R.id.cb_red_ball_drag18,
			R.id.cb_red_ball_drag19, R.id.cb_red_ball_drag20,
			R.id.cb_red_ball_drag21, R.id.cb_red_ball_drag22,
			R.id.cb_red_ball_drag23, R.id.cb_red_ball_drag24,
			R.id.cb_red_ball_drag25, R.id.cb_red_ball_drag26,
			R.id.cb_red_ball_drag27, R.id.cb_red_ball_drag28,
			R.id.cb_red_ball_drag29, R.id.cb_red_ball_drag30,
			R.id.cb_red_ball_drag31, R.id.cb_red_ball_drag32,
			R.id.cb_red_ball_drag33 })
	public void redBallDrag(View v) {
		CheckBox cb = (CheckBox) v;
		String string = cb.getText().toString();

		if (cb.isChecked()) {
			// 拖区选中后胆区相应数字取消选中
			for (int i = 0; i < redBallDragCB.length; i++) {
				String string2 = redBallDragCB[i].getText().toString();
				if (string.equals(string2)) {
					redBallCourageCB[i].setChecked(false);
					// 胆区移出一个选中后小于5时，其他可点击
					if (redBallCourageList.size() == 5) {
						for (int j = 0; j < redBallCourageCB.length; j++) {

							redBallCourageCB[j].setEnabled(true);

						}

					}
					redBallCourageList.remove(string);

				}
			}
			redBallDragList.add(string);
			if (redBallDragList.size() >= 20) {
				Util.show("已选20个，除非取消不然其它不可点击", this);
				for (int i = 0; i < redBallDragCB.length; i++) {

					String cbsString = redBallDragCB[i].getText().toString();
					for (int j = 0; j < redBallDragList.size(); j++) {
						String string2 = redBallDragList.get(j);

						if (cbsString.equals(string2)) {

							redBallDragCB[i].setEnabled(true);
							break;

						} else {
							redBallDragCB[i].setEnabled(false);
						}
					}
				}
			}

		} else {
			// 拖区移出一个选中后小于20时，其他可点击
			if (redBallDragList.size() == 20) {
				for (int i = 0; i < redBallDragCB.length; i++) {

					redBallDragCB[i].setEnabled(true);

				}
			}
			redBallDragList.remove(string);
		}

		setDragResult();
	}

	/**
	 * 胆拖区蓝球点击事件
	 * 
	 * @param v
	 */
	@OnClick({ R.id.cb_blue_ball_drag01, R.id.cb_blue_ball_drag02,
			R.id.cb_blue_ball_drag03, R.id.cb_blue_ball_drag04,
			R.id.cb_blue_ball_drag05, R.id.cb_blue_ball_drag06,
			R.id.cb_blue_ball_drag07, R.id.cb_blue_ball_drag08,
			R.id.cb_blue_ball_drag09, R.id.cb_blue_ball_drag10,
			R.id.cb_blue_ball_drag11, R.id.cb_blue_ball_drag12,
			R.id.cb_blue_ball_drag13, R.id.cb_blue_ball_drag14,
			R.id.cb_blue_ball_drag15, R.id.cb_blue_ball_drag16 })
	public void blueBallDrag(View v) {
		CheckBox cb = (CheckBox) v;

		String string = cb.getText().toString();

		if (cb.isChecked()) {
			blueBallDragList.add(string);
		} else {
			blueBallDragList.remove(string);
		}

		setDragResult();
	}

	/**
	 * 胆拖区每次点击都计算注数的方法
	 */
	private void setDragResult() {
		int sum = computeDragSum();
		tv_select_money.setText(sum + "注，" + (sum * 2) + "元");

	}

	/**
	 * 直选区每次点击都计算注数的方法
	 */
	private void setStraightResult() {
		int sum = computeStraightSum();
		tv_select_money.setText(sum + "注，" + (sum * 2) + "元");

	}

	/**
	 * 设置一些view的属性
	 */
	private void setView() {
		topCenter.setText("双色球选号");

		Intent intent = getIntent();
		if (intent != null) {
			isFromOther = intent.getBooleanExtra("fromBetting", false);
			if (isFromOther) {

				int bettingSum = intent.getIntExtra("bettingSum", 0);
				basketNum += bettingSum;
				Log.i("result", basketNum + "basketNum");
				tv_add_number_basket_count.setText(basketNum + "");

			}
		}

	}

	/**
	 * 设置监听
	 */
	private void setListenner() {
		// 切换直选、胆拖的监听
		rg_select.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_select_drag:

					isStraight = false;

					straight_area.setVisibility(View.GONE);
					drag_area.setVisibility(View.VISIBLE);
					resetAll();
					break;
				case R.id.rb_select_straight:
					isStraight = true;

					straight_area.setVisibility(View.VISIBLE);
					drag_area.setVisibility(View.GONE);
					resetAll();
					break;
				default:
					break;
				}

			}
		});

	}

	/**
	 * 计算直选区注数
	 * 
	 * @return
	 */
	private int computeStraightSum() {

		int redsize = redBallStraightList.size();
		int bluesize = blueBallStraightList.size();
		int sum = 0;
		if (redsize >= 6 && bluesize >= 1) {

			int redcnk = cnk(redsize, 6);

			sum = redcnk * bluesize;
		}

		return sum;

	}

	/**
	 * 计算胆拖区注数
	 * 
	 * @return
	 */
	private int computeDragSum() {
		int redDragSize = redBallDragList.size();
		int redCourageSize = redBallCourageList.size();
		int bluesize = blueBallDragList.size();
		int redsize = redDragSize + redCourageSize;
		int sum = 0;
		if (redCourageSize >= 1 && bluesize >= 1 && redsize >= 6) {
			int num = 6 - redCourageSize;
			int redcnk = cnk(redDragSize, num);

			sum = redcnk * bluesize;
		}

		return sum;

	}

	/**
	 * 排列组合函数
	 * 
	 * @param n
	 * @param k
	 * @return
	 */
	public int cnk(int n, int k) {
		// BigInteger fenzi = new BigInteger("1");
		// BigInteger fenmu = new BigInteger("1");
		int fenzi = 1;
		int fenmu = 1;
		for (int i = n - k + 1; i <= n; i++) {
			String s = Integer.toString(i);
			BigInteger stobig = new BigInteger(s);
			// fenzi = fenzi.multiply(stobig);
			fenzi *= i;
		}
		for (int j = 1; j <= k; j++) {
			String ss = Integer.toString(j);
			BigInteger stobig2 = new BigInteger(ss);
			// fenmu = fenmu.multiply(stobig2);
			fenmu *= j;
		}
		// BigInteger result = fenzi.divide(fenmu);
		int result = fenzi / fenmu;
		return result;
	}

	/**
	 * 加入篮子
	 */
	public void addBasket() {

		Bettingdata bet = new Bettingdata();
		if (basketNum >= bettingMax) {
			Util.show("上限为50", this);
			return;

		}
		if (isStraight) {

			int bettingNum = computeStraightSum();
			if (redBallStraightList.size() < 6
					|| blueBallStraightList.size() < 1) {

				return;

			} else {
				bet.setStraight(true);
				List<String> blueList = new ArrayList<String>();
				List<String> redList = new ArrayList<String>();

				redList.addAll(redBallStraightList);

				blueList.addAll(blueBallStraightList);
				bet.setRedBallStraightList(redList);
				bet.setBlueBallStraightList(blueList);
				bet.setCount(bettingNum);

				bet.setMoney(bettingNum * 2 + "元");

			}

		} else {
			int bettingNum = computeDragSum();
			int redDragSize = redBallDragList.size();
			int redCourageSize = redBallCourageList.size();
			int num = 6 - redCourageSize;
			if (redCourageSize + redDragSize < 6 || blueBallDragList.size() < 1) {

				return;
			} else {
				bet.setStraight(false);
				List<String> blueList = new ArrayList<String>();
				List<String> redList1 = new ArrayList<String>();
				List<String> redList2 = new ArrayList<String>();
				redList1.addAll(redBallCourageList);
				redList2.addAll(redBallDragList);
				blueList.addAll(blueBallDragList);
				bet.setRedBallCourageList(redList1);
				bet.setRedBallDragList(redList2);
				bet.setBlueBallDragList(blueList);
				bet.setCount(bettingNum);
				bet.setMoney(bettingNum * 2 + "元");

			}

		}

		basketArrayList.add(bet);
		basketNum++;
		tv_add_number_basket_count.setText(basketNum + "");
		resetAll();
		return;

	}

	/**
	 * 正在获取近期彩票信息
	 */
	private void getRecentData() {
		tv_lottery.setText("正在获取近期彩票信息...");
		tv_number_red.setVisibility(View.GONE);
		tv_number_blue.setVisibility(View.GONE);
		tv_salestop.setVisibility(View.GONE);
		tv_salestop_time.setVisibility(View.GONE);

	}

	private void refreshLotteryData() {
		getRecentData();
		if (!Util.isNetworkConnected(this)) {
			tv_lottery.setText("未能获取到近期彩票信息！");
			Util.show("没有检测到网络，请检查您的网络连接...", this);
			return;
		}

		Util.asynTask(new IAsynTask() {
			@Override
			public void updateUI(Serializable runData) {
				HashMap<String, List<LotteryInfo>> map = (HashMap<String, List<LotteryInfo>>) runData;
				List<LotteryInfo> arrayList = map.get("list");
				if (null == arrayList || 0 == arrayList.size()) {
					tv_lottery.setText("未能获取到近期彩票信息！");

				} else {
					LotteryInfo info0 = null;
					LotteryInfo info1 = null;
					if (arrayList.size() == 1) {
						info0 = arrayList.get(0);
						info1 = info0;

					} else {
						info0 = arrayList.get(0);
						info1 = arrayList.get(1);
					}

					String bonusCode = info1.getBonusCode();
					if (!Util.isNull(bonusCode)) {
						String[] split = bonusCode.split(" \\| ");
						String red = "";
						String blue = "";
						if (split.length >= 2) {
							red = split[0];
							blue = split[1];
						}

						String[] reds = red.split(",");
						String[] blues = blue.split(",");
						String redString = Combination.arrayToString(reds, " ");
						String blueString = Combination.arrayToString(blues,
								" ");
						tv_number_red.setText(redString);
						tv_number_blue.setText(blueString);

					} else {
						tv_number_red.setText("暂未开奖");
						tv_number_blue.setText("");
					}

					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
					String stopTime = info0.getStopTime();
					String stop = sdf.format(new Date(stopTime));

					tv_lottery.setText("第" + info1.getIssueNumber() + "期开奖号码：");

					tv_salestop.setText("第" + info0.getIssueNumber() + "期");
					tv_salestop_time.setText("停售时间：" + stop);
					tv_number_red.setVisibility(View.VISIBLE);
					tv_number_blue.setVisibility(View.VISIBLE);
					tv_salestop.setVisibility(View.VISIBLE);
					tv_salestop_time.setVisibility(View.VISIBLE);

				}

			}

			@Override
			public Serializable run() {

				List<LotteryInfo> arrayList = new ArrayList<LotteryInfo>();
				HashMap<String, List<LotteryInfo>> map = new HashMap<String, List<LotteryInfo>>();
				String string = "type=&issueNumber=";
				for (int i = 0; i < 2; i++) {

					Web web = new Web(Web.convience_service,
							Web.getLotteryInfo, string);
					List<LotteryInfo> infolist = web.getList(LotteryInfo.class,
							"issueinfo");
					if (infolist == null || infolist.size() == 0) {
						map.put("list", arrayList);
						return map;
					}
					if (i == 0 && infolist != null && infolist.size() > 0) {
						LotteryInfo info = infolist.get(0);
						String issueNumber = info.getIssueNumber();
						Web.issueNumber = (Integer.parseInt(issueNumber));
						string = "type=SSQ&issueNumber="
								+ (Web.issueNumber - 1);
					}

					arrayList.addAll(infolist);

				}

				map.put("list", arrayList);
				return map;
			}
		});

	}

}
