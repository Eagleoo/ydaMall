package com.mall.serving.doubleball;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Combination;
import com.mall.view.R;

@ContentView(R.layout.doubleball_betting_details)
public class DoubleballBettingDetailsActivity extends Activity {

	@ViewInject(R.id.topCenter)
	private TextView topCenter;
	@ViewInject(R.id.lv_bet_detail)
	private ListView lv_bet_detail;

	private List<String> basketList;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		ViewUtils.inject(this);
		basketList = new ArrayList<String>();
		setView();
		getIntentData();
		DetailListAdapter adapter = new DetailListAdapter(this);
		lv_bet_detail.setAdapter(adapter);

	}

	private void setView() {
		topCenter.setText("投注详情");

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

	private void getIntentData() {
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			Bettingdata data = (Bettingdata) bundle.getSerializable("data");
             
			getBetDetail(data);

		}

	}

	private void getBetDetail(Bettingdata data) {
		List<String> redNumList = new ArrayList<String>();
		List<String> blueNumList = new ArrayList<String>();
		if (data.isStraight()) {

			List<String> blueBallStraightList = data.getBlueBallStraightList();
			List<String> redBallStraightList = data.getRedBallStraightList();
			int size = redBallStraightList.size();
			if (size < 6 || blueBallStraightList.size() < 1) {
				return;
			} else if (size == 6) {
				String string = Combination.arrayToString(
						Combination.listToString(redBallStraightList), " ");
				redNumList.add(string);
			} else {
				redNumList = Combination.combination(
						Combination.listToString(redBallStraightList), 6," ");
			}

			blueNumList = blueBallStraightList;

		} else {
			List<String> blueBallDragList = data.getBlueBallDragList();

			List<String> redBallCourageList = data.getRedBallCourageList();
			List<String> redBallDragList = data.getRedBallDragList();
			int redDragSize = redBallDragList.size();
			int redCourageSize = redBallCourageList.size();
			int num = 6 - redCourageSize;
			if ((redCourageSize + redDragSize )< 6 || blueBallDragList.size() < 1) {
				return;
			} else if (num == redDragSize) {
				String string = Combination
						.arrayToString(Combination.combineStringArray(
								Combination.listToString(redBallCourageList),
								Combination.listToString(redBallDragList)), " ");
				redNumList.add(string);
			} else {
				List<String[]> tempList = Combination.combinationStringArray(
						Combination.listToString(redBallDragList), num);
				String[] str1 = Combination.listToString(redBallCourageList);

				for (int i = 0; i < tempList.size(); i++) {
					String[] array = Combination.combineStringArray(str1,
							tempList.get(i));
					String toString = Combination.arrayToString(array, " ");
					redNumList.add(toString);
				}

			}

			blueNumList = blueBallDragList;

		}

		listAddData(redNumList, blueNumList);

	}

	/**
	 * 将两个list合并成一个list
	 * 
	 * @param list1
	 * @param list2
	 */
	private void listAddData(List<String> list1, List<String> list2) {

		for (int i = 0; i < list1.size(); i++) {
			for (int j = 0; j < list2.size(); j++) {
				basketList.add(list1.get(i) + " " + list2.get(j));

			}
		}
	}

	/**
	 * 功能：投注列表的adapter <br>
	 * 时间： 2014-7-19<br>
	 * 备注： <br>
	 * 
	 * @author Lin.~
	 * 
	 */
	class DetailListAdapter extends BaseAdapter {
		private Context context;

		public DetailListAdapter(Context context) {
			super();
			this.context = context;

		}

		@Override
		public int getCount() {
			if (basketList == null) {
				return 0;
			}
			return basketList.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return basketList.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int postion, View v, ViewGroup arg2) {
			if (v == null) {
				ViewCache cache = new ViewCache();
				v = LayoutInflater.from(context).inflate(
						R.layout.doubleball_betting_list_item, null);

				
				cache.cb_ball_number_01 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_01);
				cache.cb_ball_number_02 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_02);
				cache.cb_ball_number_03 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_03);
				cache.cb_ball_number_04 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_04);
				cache.cb_ball_number_05 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_05);
				cache.cb_ball_number_06 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_06);
				cache.cb_ball_number_07 = (CheckBox) v
						.findViewById(R.id.cb_ball_number_07);
				v.setTag(cache);

			}

			ViewCache cache = (ViewCache) v.getTag();
			final String string = basketList.get(postion);
			String[] split = string.split(" ");
			if (split.length>=7) {
				cache.cb_ball_number_01.setText(split[0]);
				cache.cb_ball_number_02.setText(split[1]);
				cache.cb_ball_number_03.setText(split[2]);
				cache.cb_ball_number_04.setText(split[3]);
				cache.cb_ball_number_05.setText(split[4]);
				cache.cb_ball_number_06.setText(split[5]);
				cache.cb_ball_number_07.setText(split[6]);
				
				
			}
			
			
	
			
			
			return v;
		}

		class ViewCache {

			
			
			CheckBox cb_ball_number_01;
			CheckBox cb_ball_number_02;
			CheckBox cb_ball_number_03;
			CheckBox cb_ball_number_04;
			CheckBox cb_ball_number_05;
			CheckBox cb_ball_number_06;
			CheckBox cb_ball_number_07;
		
		}
	}

}
