package com.mall.serving.doubleball.addandsubview;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.view.R;

/**
 * 
 * @author yj
 * 
 */
public class AddAndSubView extends LinearLayout {
	Context context;

	OnNumChangeListener onNumChangeListener;
	TextView addButton;
	TextView subButton;
	EditText editText;
	int num; // editText中的数值

	public AddAndSubView(Context context) {
		super(context);
		this.context = context;
		num = 1;
		control();
	}

	/**
	 * 带初始数据实例化
	 * 
	 * @param context
	 * @param 初始数据
	 */
	public AddAndSubView(Context context, int num) {
		super(context);
		this.context = context;
		this.num = num;
		control();
	}

	public AddAndSubView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		num = 1;
		control();
	}

	/**
	 * 
	 */
	private void control() {

		initialise(); // 实例化内部view

		setViewListener();
	}

	/**
	 * 实例化内部View
	 */
	private void initialise() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.addandsubview, null);
		addButton = (TextView) view.findViewById(R.id.tv_add);
		subButton = (TextView) view.findViewById(R.id.tv_sub);
		editText = (EditText) view.findViewById(R.id.et_num);
		// 设置输入类型为数字
		editText.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
		editText.setText(String.valueOf(num));

		addButton.setTag("+");
		subButton.setTag("-");
		view.setFocusable(true);
		view.setFocusableInTouchMode(true);
		addView(view);
	}

	/**
	 * 设置editText中的值
	 * 
	 * @param num
	 */
	public void setNum(int num) {
		this.num = num;
		editText.setText(String.valueOf(num));
	}

	/**
	 * 获取editText中的值
	 * 
	 * @return
	 */
	public int getNum() {
		if (editText.getText().toString() != null) {
			return Integer.parseInt(editText.getText().toString());
		} else {
			return 1;
		}
	}

	/**
	 * 设置EditText文本变化监听
	 * 
	 * @param onNumChangeListener
	 */
	public void setOnNumChangeListener(OnNumChangeListener onNumChangeListener) {
		this.onNumChangeListener = onNumChangeListener;
	}

	/**
	 * 设置文本变化相关监听事件
	 */
	private void setViewListener() {
		addButton.setOnClickListener(new OnButtonClickListener());
		subButton.setOnClickListener(new OnButtonClickListener());
		editText.addTextChangedListener(new OnTextChangeListener());
	}

	/**
	 * 加减按钮事件监听器
	 * 
	 * @author ZJJ
	 * 
	 */
	class OnButtonClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			String numString = editText.getText().toString();
			if (numString == null || numString.equals("")) {
				num = 1;
				editText.setText("1");
			} else {
				if (v.getTag().equals("+")) {
					if (++num <= 0) // 先加，再判断
					{
						num--;
//						Toast.makeText(context, "请输入一个大于等于1的数字",
//								Toast.LENGTH_SHORT).show();
					} else {
						editText.setText(String.valueOf(num));

						if (onNumChangeListener != null) {
							onNumChangeListener.onNumChange(AddAndSubView.this,
									num);
						}
					}
				} else if (v.getTag().equals("-")) {
					if (--num <= 0) // 先减，再判断
					{
						num++;
//						Toast.makeText(context, "请输入一个大于等于1的数字",
//								Toast.LENGTH_SHORT).show();
					} else {
						editText.setText(String.valueOf(num));
						if (onNumChangeListener != null) {
							onNumChangeListener.onNumChange(AddAndSubView.this,
									num);
						}
					}
				}
			}
		}
	}

	/**
	 * EditText输入变化事件监听器
	 * 
	 * @author ZJJ
	 * 
	 */
	class OnTextChangeListener implements TextWatcher {

		@Override
		public void afterTextChanged(Editable s) {
			String numString = s.toString();
			if (numString == null || numString.equals("")) {
				num = 1;

				if (onNumChangeListener != null) {
					onNumChangeListener.onNumChange(AddAndSubView.this, num);
				}
			} else {
				int numInt = Integer.parseInt(numString);
				if (numInt <= 0) {
					editText.setText("1");
//					Toast.makeText(context, "请输入一个大于等于1的数字", Toast.LENGTH_SHORT)
//							.show();
				} else if (numInt > 100) {
//					Toast.makeText(context, "请输入一个小于100的数字",
//							Toast.LENGTH_SHORT).show();
					editText.setText("100");
				} else {
					// 设置EditText光标位置 为文本末端
					editText.setSelection(editText.getText().toString()
							.length());
					num = numInt;
					if (onNumChangeListener != null) {
						onNumChangeListener
								.onNumChange(AddAndSubView.this, num);
					}
				}
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {

		}

	}

	public interface OnNumChangeListener {
		/**
		 * 输入框中的数值改变事件
		 * 
		 * @param view
		 *            整个AddAndSubView
		 * @param num
		 *            输入框的数值
		 */
		public void onNumChange(View view, int num);
	}

}
