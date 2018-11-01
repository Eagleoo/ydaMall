package com.way.note;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;

import com.mall.view.R;
import com.way.note.data.NoteItem;

/**
 * 
 * @author way
 * 
 */
public class SearchResultActivity extends NoteActivity {
	private String mSearchContent;
	public static final String TAG = "SearchResultActivity";
	public static final String KEY_CONTENT = "content";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initData();
	}

	// activity is started from EditorNote, EditorNote is started form search.
	private void initData() {
		Intent intent = getIntent();
		if (intent != null) {
			mSearchContent = intent.getStringExtra(KEY_CONTENT);
		}
	}

	@Override
	public void updateDisplay() {
		mItems = getDataManager(this).getNotesIncludeContent(mSearchContent);
		if (mAdapter == null) {
			mAdapter = new NoteAdapter(this, mItems);
			mlistView.setAdapter(mAdapter);
		} else {
			mAdapter.setListItems(mItems);
			mAdapter.notifyDataSetChanged();
		}
		String searchTitle = mSearchContent.length() < 10 ? mSearchContent
				: mSearchContent.substring(0, 9) + "...";
		mTitleTextView.setText(getString(R.string.search_hint) + ":"
				+ searchTitle);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		initData();
		updateDisplay();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_NEW_NOTE, 1, R.string.new_note).setIcon(
				R.drawable.new_note);
		menu.add(Menu.FIRST, Menu_delete, 3, R.string.delete).setIcon(
				R.drawable.note_delete);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu menu");
		if (mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_DELETE
				|| mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_MOVETOFOLDER) {
			return false;
		}
		return true;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		NoteItem contextNoteItem = mItems.get(info.position);
		selectedItemID = contextNoteItem._id;
		menu.setHeaderTitle(contextNoteItem.getShortTitle());
		menu.add(0, Menu_delete, 0, R.string.delete);
		menu.add(0, Menu_setAlarm, 2, R.string.setAlarm);
		menu.add(0, MENU_SHARE, 3, R.string.share);
	}
}
