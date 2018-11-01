package com.mall.note;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mall.model.NoteModel;
import com.mall.view.R;

public class NoteAdapter extends BaseAdapter {
	private Context c;
	private LayoutInflater layoutInflater;
	private List<NoteModel> list = new ArrayList<NoteModel>();

	public NoteAdapter(Context c) {
		this.c = c;
		layoutInflater = LayoutInflater.from(c);
	}

	public void setList(List<NoteModel> list) {
		this.list.addAll(list);
		this.notifyDataSetChanged();
	}

	public void clear() {
		list.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return this.list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final NoteModel n = this.list.get(position);
		final ViewHolder holder;
		if (convertView == null) {
			convertView = (LinearLayout) layoutInflater.inflate(
					R.layout.all_note_list_item, null);
			holder = new ViewHolder();
			holder.t1 = (TextView) convertView.findViewById(R.id.title);
			holder.t2 = (TextView) convertView.findViewById(R.id.context);
			holder.open_or_close_Liner = (LinearLayout) convertView.findViewById(R.id.open_or_close_Liner);
			holder.open_or_close = (ImageView) convertView.findViewById(R.id.open_or_close);
			holder.content_Liner = (LinearLayout) convertView.findViewById(R.id.content_Liner);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.t1.setText(n.getPublishTime());
//		if (n.getTitle().indexOf("--..--") != -1) {
			holder.t2.setText(n.getContent());
//		} else {
//			holder.t2.setText(n.getTitle());
//		}
			holder.open_or_close_Liner.setOnClickListener(new OnClickListener() {
				int tag=0;	
				@Override
				public void onClick(View v) {
					switch(tag){
					case 0://关
						holder.content_Liner.setVisibility(View.GONE);
						holder.open_or_close.setImageResource(R.drawable.note_rili_close);
						tag=1;
						break;
					case 1://开
						holder.content_Liner.setVisibility(View.VISIBLE);
						holder.open_or_close.setImageResource(R.drawable.note_rili_open);
						tag=0;
						break;
					}
					
				}
			});
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(c, AddOneNote.class);
				if (n.getTitle().indexOf("--..--") != -1) {
					intent.putExtra("endtime", list.get(position).getTitle());
					intent.putExtra("jishiXq", list.get(position).getContent());
				} else {
					intent.putExtra("jishiXq", list.get(position).getTitle());
					intent.putExtra("endtime", list.get(position).getContent());
				}
				intent.putExtra("time", list.get(position).getPublishTime());
				intent.putExtra("jishiId", list.get(position).getId());
				c.startActivity(intent);
			}
		});
		convertView.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// 将布局文件转化成view对象
				LayoutInflater inflaterDl = LayoutInflater.from(c);
				LinearLayout layout = (LinearLayout) inflaterDl.inflate(
						R.layout.tuichu_lmsj_dialog, null);
				// final Dialog dialog = new AlertDialog.Builder(
				// c).create();
				Dialog dialog1 = null;
				if (c instanceof Activity) {
					Activity ac = (Activity) c;
					if(null != ac.getParent())
						dialog1 = new Dialog(ac.getParent(), R.style.CustomDialogStyle);
					else
						dialog1 = new Dialog(ac, R.style.CustomDialogStyle);
				} else {
					dialog1 = new Dialog(c, R.style.CustomDialogStyle);
				}

				final Dialog dialog = dialog1;
				DisplayMetrics dm = new DisplayMetrics();

				if (c instanceof ImportantNoteList) {
					final ImportantNoteList ac = (ImportantNoteList) c;
					ac.getWindowManager().getDefaultDisplay().getMetrics(dm);
				} else if (c instanceof NoteSearchActivity) {
					final NoteSearchActivity ac = (NoteSearchActivity) c;
					ac.getWindowManager().getDefaultDisplay().getMetrics(dm);
				}

				int width = dm.widthPixels;
				dialog.show();
				WindowManager.LayoutParams params = dialog.getWindow()
						.getAttributes();
				params.width = width * 4 / 5;
				dialog.getWindow().setAttributes(params);
				dialog.getWindow().setContentView(layout);
				TextView update_count = (TextView) layout
						.findViewById(R.id.update_count);
				TextView yihou_update = (TextView) layout
						.findViewById(R.id.yihou_update);
				TextView now_update = (TextView) layout
						.findViewById(R.id.now_update);
				update_count.setText("确定要删除该条记事？");
				yihou_update.setText("取\t\t消");
				now_update.setText("确\t\t定");
				yihou_update.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
				now_update.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (c instanceof ImportantNoteList) {
							final ImportantNoteList ac = (ImportantNoteList) c;
							ac.deleteData(list.get(position).getId());
						} else if (c instanceof NoteSearchActivity) {
							final NoteSearchActivity ac = (NoteSearchActivity) c;
							ac.deleteData(list.get(position).getId());
						}

						dialog.dismiss();
					}
				});
				dialog.setCanceledOnTouchOutside(false);
				return false;
			}
		});
		return convertView;
	}

	public class ViewHolder {
		private TextView t1;
		private TextView t2;
		private LinearLayout content_Liner;
		private LinearLayout open_or_close_Liner;
		private ImageView open_or_close;
	}

}
