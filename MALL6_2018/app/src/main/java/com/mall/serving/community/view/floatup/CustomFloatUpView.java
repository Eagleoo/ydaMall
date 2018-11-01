package com.mall.serving.community.view.floatup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.LinearLayout;

import com.mall.serving.community.util.AnimeUtil;
import com.mall.serving.community.util.AnimeUtil.OnAnimEnd;
import com.mall.serving.community.view.floatup.ObservableScrollView.ScrollViewListener;
import com.mall.view.R;

public class CustomFloatUpView extends LinearLayout {
	private View float_up;
	private int _start_index;
	private int _end_index;

	public CustomFloatUpView(Context context) {
		super(context);
	}

	public CustomFloatUpView(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(
				R.layout.community_custom_float_up, null);
		float_up = view.findViewById(R.id.floatview_up);
		addView(view);

	}

	public void setClick(final AbsListView lv) {

		float_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				AnimeUtil.startAnimation(getContext(), v, R.anim.small_2_big,
						new OnAnimEnd() {

							@Override
							public void start() {
								// TODO Auto-generated method stub

							}

							@Override
							public void repeat() {
								// TODO Auto-generated method stub

							}

							@SuppressLint("NewApi")
							@Override
							public void end() {
								lv.smoothScrollToPosition(0);
								setVisibility(View.GONE);

							}
						});

			}
		});

	}

	public void setListView(final AbsListView lv) {
		if (lv == null) {
			setVisibility(View.GONE);
		} else {
			setClick(lv);
			lv.setOnScrollListener(new OnScrollListener() {

				@Override
				public void onScrollStateChanged(AbsListView view,
						int scrollState) {
					// if (scrollState == OnScrollListener.SCROLL_STATE_IDLE)
					// {// list停止滚动时
					// setVisibility(View.GONE);
					// }else {
					// setVisibility(View.VISIBLE);
					//
					// }

					if (_start_index > 0) {
						setVisibility(View.VISIBLE);
					} else {
						setVisibility(View.GONE);
					}

				}

				@Override
				public void onScroll(AbsListView view, int firstVisibleItem,
						int visibleItemCount, int totalItemCount) {
					// 设置当前屏幕显示的起始index和结束index
					_start_index = firstVisibleItem;
					_end_index = firstVisibleItem + visibleItemCount;
					if (_end_index >= totalItemCount) {
						_end_index = totalItemCount - 1;

					}
				}
			});

		}

	}

	public void setSrcollView(final ObservableScrollView sv) {

		float_up.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				// sv.scrollTo(0, 0);
				sv.smoothScrollTo(0, 0);
				setVisibility(View.GONE);
			}
		});

		sv.setScrollViewListener(new ScrollViewListener() {

			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x,
					int y, int oldx, int oldy) {
				if (y > 100) {
					setVisibility(View.VISIBLE);

				} else {
					setVisibility(View.GONE);

				}

			}
		});

	}

}
