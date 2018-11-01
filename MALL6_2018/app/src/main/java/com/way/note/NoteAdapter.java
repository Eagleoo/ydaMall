package com.way.note;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.mall.view.R;
import com.way.note.data.NoteDataManagerImpl;
import com.way.note.data.NoteItem;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 便签适配器
 * 
 * @author way
 * 
 */
public class NoteAdapter extends BaseAdapter {
	private static final String TAG = "NoteAdapter";

	public static final int SHOW_TYPE_NORMAL = 0;
	public static final int SHOW_TYPE_DELETE = 1;
	public static final int SHOW_TYPE_MOVETOFOLDER = 2;
	private LayoutInflater mListContainer; // 视图容器
	private Context mcontext;
	public int MAX_NUM = 16;// 便签项缩略显示字符数
	private int mShowType;
	private List<NoteItem> mItems = null;
	private int clicknum;

	public NoteAdapter(Context context, List<NoteItem> items) {
		mListContainer = LayoutInflater.from(context); // 创建视图容器并设置上下文
		this.mcontext = context;
		this.mItems = items;
		setAllItemChecked(false);
		mShowType = SHOW_TYPE_NORMAL;
		clicknum=items.size();
	}

	private void setAllItemChecked(boolean isChecked) {
		for (int i = 0; i < getCount(); i++) {
			mItems.get(i).isSelected = isChecked;
		}
	}

	public void setAllItemCheckedAndNotify(boolean isChecked) {
		setAllItemChecked(isChecked);
		notifyDataSetChanged();
	}

	public void toggleChecked(int position) {
		getItem(position).isSelected = !getItem(position).isSelected;
	}

	public void setItemChecked(int position, boolean checked) {
		getItem(position).isSelected = checked;
	}

	public int getSelectedCount() {
		int count = 0;
		for (NoteItem item : mItems) {
			if (item.isSelected) {
				count++;
			}
		}
		return count;
	}

	public void setShowType(int type) {
		mShowType = type;
		if (type == SHOW_TYPE_DELETE || type == SHOW_TYPE_MOVETOFOLDER) {
			setAllItemChecked(false);
		}
		notifyDataSetChanged();
	}

	public int getShowType() {
		return mShowType;
	}

	@Override
	public int getCount() {
		return mItems.size();
	}

	public int getFolderCount() {
		int count = 0;
		for (NoteItem item : mItems) {
			if (item.isFileFolder) {
				count++;
			}
		}
		return count;
	}

	public int getNotesCount() {
		int count = 0;
		for (NoteItem item : mItems) {
			if (!item.isFileFolder) {
				count++;
			}
		}
		return count;
	}

	@Override
	public NoteItem getItem(int position) {
		return mItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setListItems(List<NoteItem> items) {
		this.mItems = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ListItemView listItemView = null;
		if (convertView == null) {
			listItemView = new ListItemView();
			// 获取listview中每个item的布局文件的视图
			convertView = mListContainer.inflate(R.layout.note_listview_item_layout,
					null);
			listItemView.mLeftTextView = (TextView) convertView
					.findViewById(R.id.tv_left);
			listItemView.shixiao = (TextView) convertView
					.findViewById(R.id.shixiao);
			listItemView.mRightTextView = (TextView) convertView
					.findViewById(R.id.tv_right);
			listItemView.mLinearlayout = (ViewGroup) convertView
					.findViewById(R.id.listview_linearlayout);
			listItemView.alrerm_ok_no = (ImageView) convertView
					.findViewById(R.id.alrerm_ok_no);
			listItemView.mCheck = (CheckBox) convertView
					.findViewById(R.id.check);
			listItemView.header=(View)convertView.findViewById(R.id.header);
			listItemView.my_note_alerm_num = (TextView)convertView.findViewById(R.id.my_note_alerm_num);
			convertView.setTag(listItemView);
		} else {
			listItemView = (ListItemView) convertView.getTag();
		}

		if (position >= mItems.size()) {
			position = mItems.size() - 1;
		}
		NoteItem noteItem = mItems.get(position);
		if(0==position){
			listItemView.header.setVisibility(View.VISIBLE);
		}else{
			listItemView.header.setVisibility(View.GONE);
		}
		listItemView.mCheck.setChecked(noteItem.isSelected);
		if (mShowType == SHOW_TYPE_NORMAL) {
			listItemView.mRightTextView.setVisibility(View.VISIBLE);
			listItemView.mCheck.setVisibility(View.GONE);
		} else if (mShowType == SHOW_TYPE_DELETE) {
			listItemView.mRightTextView.setVisibility(View.GONE);
			listItemView.mCheck.setVisibility(View.VISIBLE);
		} else if (mShowType == SHOW_TYPE_MOVETOFOLDER) {
			if (noteItem.isFileFolder) {
				listItemView.mCheck.setVisibility(View.GONE);
			} else {
				listItemView.mRightTextView.setVisibility(View.GONE);
				listItemView.mCheck.setVisibility(View.VISIBLE);
			}
		}

		String content = noteItem.content;
		boolean clock_enable = noteItem.alarmEnable;
		listItemView.my_note_alerm_num.setText("全部提醒共"+clicknum+"个");
		listItemView.mRightTextView.setText(noteItem.getDate(mcontext)	+"  "+ noteItem.getTime(mcontext));
		if (!noteItem.isFileFolder) { // 不是文件夹
			// 应该判断数据库中颜色字段的值,根据该值设置相应的背景图片
			//			listItemView.mLinearlayout
			//					.setBackgroundResource(R.drawable.item_light_blue);
			listItemView.mLeftTextView.setText(noteItem.getTitle());
			if (clock_enable) {
				Log.v(TAG, " ************setclockimage");
				//用于判断闹钟是否过时
				//系统当前时间
				Calendar c = Calendar.getInstance();
				c.setTimeInMillis(new Date().getTime());
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date nowtime = null;
				Date clicktime = null ;
				try {
					nowtime = dateFormat.parse(dateFormat.format(c.getTime()));
					clicktime = dateFormat.parse(noteItem.clockItem.ringtoneDate + " "+noteItem.clockItem.ringtoneTime+":00");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				long diff = nowtime.getTime() - clicktime.getTime();//这样得到的差值是微秒级别

				if(diff<0){
					listItemView.alrerm_ok_no.setImageResource(R.drawable.alrerm_ok);
					listItemView.shixiao.setVisibility(View.INVISIBLE);
				}else{
					listItemView.alrerm_ok_no.setImageResource(R.drawable.alrerm_no);
					listItemView.shixiao.setVisibility(View.VISIBLE);
				}
				

			} else {
				listItemView.alrerm_ok_no.setVisibility(View.INVISIBLE);
			}
		} else { // 是文件夹
			listItemView.mLinearlayout
			.setBackgroundResource(R.drawable.folder_background);
			int count = NoteDataManagerImpl.getNoteDataManger(mcontext)
					.getNotesFromFolder(noteItem._id).size();
			listItemView.mLeftTextView.setText(content + "(" + count + ")");
			listItemView.alrerm_ok_no.setVisibility(View.INVISIBLE);
		}




		return convertView;
	}
}