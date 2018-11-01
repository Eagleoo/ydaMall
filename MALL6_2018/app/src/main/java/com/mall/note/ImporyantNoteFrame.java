package com.mall.note;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.easier.ui.CalendarView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.mall.util.Util;
import com.mall.view.R;
import com.way.note.NoteActivity;

public class ImporyantNoteFrame extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imporyantnote_list);
		ViewUtils.inject(this);
	}

	@OnClick({ R.id.search,  R.id.all_jishi, R.id.all_jishi,
			R.id.qingkong_js, R.id.js_setting, R.id.js_rili ,R.id.add_js})
	public void onclick(View view) {
		switch (view.getId()) {
		case R.id.add_js:
			Util.showIntent(this, AddOneNote.class);
			break;
		case R.id.js_rili:
			Util.showIntent(this, CalendarView.class);
			break;
		case R.id.js_setting:

			break;
		case R.id.qingkong_js:
			Util.showIntent(this, NoteActivity.class);
		//	Util.showIntent(this, AddOneNote.class);
			break;
		case R.id.all_jishi:
			Util.showIntent(this, ImportantNoteList.class);
			break;
		case R.id.search:

			break;

	
		}
	}
}
