package com.mall.card.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mall.card.adapter.MyHorizontalScrollView.OnComputeScrollListener;
import com.mall.card.bean.CallBackListenerForViewPager;
import com.mall.view.R;

public class PluginScrollView extends RelativeLayout {

	private MyHorizontalScrollView scrollview;
	private LinearLayout layout;
	private ScrollIndicator indicator;
	private ViewPager viewpager;
	private int currentSelectedButton = -1;
	private int singleButtonWidth = 90;
	private Context context;
	private List<View> testList;
	private int width;
	private String[]  arr;
	private CallBackListenerForViewPager viewPager2;
	public void getWidth(int width) {
		this.width = width;
	}
	public void setArr(String[] arr){
		this.arr=arr;
	}
	public PluginScrollView(Context context) {
		super(context);
		this.context = context;
		init();
	}
	public void setcallBack(CallBackListenerForViewPager viewPager){
		this.viewPager2=viewPager;
	}
	public PluginScrollView(Context context, ViewPager viewPager) {
		this(context, viewPager, null);
	}

	public PluginScrollView(Context context, ViewPager viewPager,
			List<View> testList) {
		super(context);
		this.context = context;
		this.viewpager = viewPager;
		this.testList = testList;
		init();
	}

	public PluginScrollView(Context context, AttributeSet attr) {
		super(context, attr);
		this.context = context;
		init();
	}

	private void init() {

		View view = View.inflate(context,
				R.layout.activity_tab_scrollview_plugin, this);
		scrollview = (MyHorizontalScrollView) view
				.findViewById(R.id.my_horizontal_scrollview);
		scrollview.setHorizontalScrollBarEnabled(false);
		scrollview.setOnComputeScrollListener(new OnComputeScrollListener() {
			@Override
			public void computeScroll(View view) {
				// ���Ʒ����ͷ����ʾ���
				setIndicatorVisibility(view);
			}
		});
		layout = (LinearLayout) view.findViewById(R.id.layout);
		indicator = new ScrollIndicator(context);
		IndicatorArrowClickListener listener = new IndicatorArrowClickListener();
		indicator.setLeftArrowClickListener(listener);
		indicator.setRightArrowClickListener(listener);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
		if (layout != null) {
			View button = layout.getChildAt(0);
			if (button != null) {
				singleButtonWidth = button.getWidth();
			}
		}
	}

	public void setViewPager(ViewPager viewPager) {
		this.viewpager = viewPager;
	}

	public void setTestList(List<View> testList) {
		this.testList = testList;
		initButtons();
	}

	public List<View> getTestList() {
		return this.testList;
	}

	private void initButtons() {
		if (testList == null)
			return;
		currentSelectedButton = -1;
		layout.removeAllViews();
		Log.v("width", width + "");
		for (int i = 0; i < testList.size(); i++) {
			final int j = i;
			TextView mbutton = new TextView(context);
			mbutton.setText(arr[i]);
			mbutton.setTextColor(getResources().getColor(R.color.card_centre_zi));
		    mbutton.setBackgroundResource(R.drawable.bg_scrollview_pink);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			mbutton.setLayoutParams(params);
			mbutton.setGravity(Gravity.CENTER|Gravity.CENTER_VERTICAL);
			setImageWidthAndHeight(mbutton, 1, width / 4);
			mbutton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					viewpager.setCurrentItem(j, true);
				}
			});
			layout.addView(mbutton);
		}
		if (testList.size() > 0) {
			buttonSelected(0);
		}
		setIndicatorVisibility(scrollview);
	}

	private void setImageWidthAndHeight(TextView imageView, int state, int width) {
		RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) imageView
				.getLayoutParams(); // 取控件textView当前的布局参数
		linearParams.width = width;
		imageView.getLayoutParams();
	}

	public int size() {
		if (testList == null)
			return 0;
		return testList.size();
	}

	public void buttonSelected(int position) {
		if (position == currentSelectedButton) {
			return;
		}
		if (currentSelectedButton != -1) {
			getButtonInLayout(currentSelectedButton).setSelected(false);
//			Drawable icon = this.getResources().getDrawable(
//					R.drawable.card_setiao1);
//			icon.setBounds(0, 0, icon.getMinimumWidth(),
//					icon.getMinimumHeight());
//			getButtonInLayout(position).setCompoundDrawables(null, null, null,
//					icon);
			getButtonInLayout(currentSelectedButton).setTextColor(getResources().getColor(R.color.card_centre_zi));
			 getButtonInLayout(currentSelectedButton).setBackgroundResource(R.drawable.bg_scrollview_pink);
		} else {
			

		}
		if (getButtonInLayout(position).getText().toString().trim().equals("管理")) {
			viewPager2.callBack();
		}
		getButtonInLayout(position).setTextColor(getResources().getColor(R.color.card_bu_zi));
		getButtonInLayout(position).setSelected(true);
		 getButtonInLayout(position).setBackgroundResource(R.drawable.btn_scrollview_plugin_unselected);

//		Drawable icon = this.getResources().getDrawable(
//				R.drawable.card_setiao);
		 
//		icon.setBounds(0, 0, icon.getMinimumWidth(),
//				icon.getMinimumHeight());
//		getButtonInLayout(position).setCompoundDrawables(null, null, null,
//				icon);
		currentSelectedButton = position;
		autoScrollView(position);
	}

	private void autoScrollView(int location) {

		int displayX = scrollview.getScrollX(); // ��������� ���
		int displayMaxX = getWidth() + displayX;// ����������յ�

		int icoScrollX = 0;
		for (int i = 0; i < location; i++) {
			icoScrollX += layout.getChildAt(i).getWidth();// ָ����ť�ľ���߾�ľ���
		}
		// ָ����ť�ľ���߾�ľ��� + ������
		int icoScrollMaxX = layout.getChildAt(location).getWidth() + icoScrollX;

		if (icoScrollX < displayX) {
			// �������� ����
			scrollview.smoothScrollBy(icoScrollX - displayX - 10, 0);
		} else if (icoScrollMaxX > displayMaxX) {
			// �������� ����
			scrollview.smoothScrollBy(icoScrollMaxX - displayMaxX, 0);
		}
		setIndicatorVisibility(scrollview);

	}

	private TextView getButtonInLayout(int i) {
		View view = layout.getChildAt(i);
		if (view instanceof TextView) {
			return (TextView) view;
		}
		return null;
	}

	private void setIndicatorVisibility(View v) {
		Log.d("debug",
				"setIndicatorVisibility  ==> v.getScrollX = " + v.getScrollX());

		if (v.getScrollX() <= 1) {
			indicator.hideLeftArrow();
		} else {
			indicator.showLeftArrow();
		}
		int seg = layout.getWidth() - v.getScrollX() - v.getWidth();
		if (seg >= 1) {
			indicator.showRightArrow();
		} else {
			indicator.hideRightArrow();
		}
	}

	private class IndicatorArrowClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.imgview_arrow_left:
				scrollview.smoothScrollBy(-singleButtonWidth, 0);
				break;

			case R.id.imgview_arrow_right:
				scrollview.smoothScrollBy(singleButtonWidth, 0);
				break;
			}
		}
	};
}
