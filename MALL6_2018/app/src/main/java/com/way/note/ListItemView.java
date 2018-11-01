package com.way.note;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * ListView中每一行中包含的组件 这个类把ListView中的每一行都包装成一个ListItemView对象
 * 
 * @author way
 */
public final class ListItemView {
	public ViewGroup mLinearlayout;
	public TextView mLeftTextView;
	public TextView shixiao;
	public TextView my_note_alerm_num;
	public TextView mRightTextView;
	public ImageView alrerm_ok_no;
	public CheckBox mCheck;
	public View header;
}
