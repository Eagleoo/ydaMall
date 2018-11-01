package com.way.note;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.util.Util;
import com.mall.view.R;
import com.way.note.data.NoteDataManager;
import com.way.note.data.NoteItem;
import com.way.note.utils.FileUtis;
import com.way.note.utils.IntentUtils;

/*
 * 主界面：显示所有的文件夹和没有父文件夹的便签
 */
public class NoteActivity extends BaseActivity
		implements View.OnClickListener, OnItemClickListener, View.OnCreateContextMenuListener {
	private static final String TAG = "NoteActivity";

	protected ListView mlistView;
	private ImageButton mNewNotebutton;
	protected NoteAdapter mAdapter;
	protected TextView mTitleTextView;
	public static final int MAX_NOTES = 1000;// 最大便签数

	public static final int MAX_FOLDERS = 1000;// 最大文件夹数

	public static final int MAX_FOLDERNAMENUM = 15;// 文件夹名输入限制的最大字数
	// 菜单
	protected static final int MENU_NEW_NOTE = Menu.FIRST;// 新建一个便签
	protected static final int MENU_NEW_FOLDER = Menu.FIRST + 1;// 新建一个文件夹
	protected static final int Menu_delete = Menu.FIRST + 2;// 删除
	protected static final int Menu_delete_ALL = Menu.FIRST + 6;// 删除所有
	protected static final int Menu_update_folder = Menu.FIRST + 7;// 更新文件夹
	protected static final int Menu_moveoutFolder = Menu.FIRST + 8;// 移除文件夹
	protected static final int MENU_SHARE = Menu.FIRST + 9;// 分享
	protected static final int MENU_EXPORT = Menu.FIRST + 10;// 导出

	protected static final int Menu_movetoFolder = Menu.FIRST + 3;// 移进文件夹
	protected static final int Menu_setAlarm = Menu.FIRST + 4;// 设置便签铃声
	protected static final int Menu_folder_rename = Menu.FIRST + 5;// 重命名文件夹

	public static final int FOLDER_NO = -1;// 无文件夹

	// this flag indicate to display the notes in the root or one folder.
	int mFolderID = FOLDER_NO;// 根目录标志，
	protected List<NoteItem> mItems = null;
	private long exitTime = 0L;
	private String toSet;
	private String time;
	private int first = 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.v(TAG, "OnCreate");
		setContentView(R.layout.note_index_page);
		toSet = getIntent().getStringExtra("set");
		time = getIntent().getStringExtra("time");
		initViews();

	}

	private void adddHeader() {
		// View view =
		// LayoutInflater.from(NoteActivity.this).inflate(R.layout.note_header_view,
		// null);
		// TextView my_note_alerm_num =
		// (TextView)view.findViewById(R.id.my_note_alerm_num);
		// if(Util.isNull(mItems)){
		// my_note_alerm_num.setText("全部提醒共0个");
		//
		// }else{
		// my_note_alerm_num.setText("全部提醒共"+mItems.size()+"个");
		// }
		// mlistView.addHeaderView(view);
	}

	public void topbcak(View view) {
		finish();
	}
	


	private void initViews() {
		mTitleTextView = (TextView) findViewById(R.id.tvTitle);// 标题
		mlistView = (ListView) this.findViewById(R.id.page_list);// 存放每个便签的listView
		mlistView.setOnItemClickListener(this);// 注册点击事件
		mlistView.setOnCreateContextMenuListener(this);// 注册一个上下文菜单
		mNewNotebutton = (ImageButton) findViewById(R.id.new_note_button);// 右上角新建便签的button
		mNewNotebutton.setOnClickListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		updateDisplay();
	}

	// 负责更新ListView中的数据
	protected void updateDisplay() {
		Log.i(TAG, "updateDisplay displayFoldID-->" + mFolderID);
		// if(1==first){
		// adddHeader();
		// first=2;
		// }
		if (mFolderID == FOLDER_NO) { // 根目录
			mItems = getDataManager(this).getRootFoldersAndNotes();// 获取文件夹和便签
			if (mAdapter == null) {
				mAdapter = new NoteAdapter(this, mItems);
				mlistView.setAdapter(mAdapter);
			} else {
				mAdapter.setListItems(mItems);
				mAdapter.notifyDataSetChanged();
			}
			mTitleTextView.setText(getString(R.string.mynote));
			if (!TextUtils.isEmpty(toSet)) {
				Intent intent = new Intent(NoteActivity.this, AlarmActivity.class);
				intent.putExtra(AlarmActivity.NOTE_ID, mItems.get(0)._id);
				intent.putExtra("count", mItems.get(0).getShortTitle());
				intent.putExtra("time", time);
				intent.putExtra("addNew", "addNew");
				startActivityForResult(intent, 1);
			}
		} else { // in folder
			mItems = getDataManager(this).getNotesFromFolder(mFolderID);
			mAdapter.setListItems(mItems);
			mAdapter.notifyDataSetChanged();
			mTitleTextView.setText(getDataManager(this).getNoteItem(mFolderID).content);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.v(TAG, "onPause");
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.v(TAG, "onStop");
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.e("生命周期", "NoteActivity 结束" );
	}

	@Override
	public void finish() {
		if (mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_DELETE
				|| mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_MOVETOFOLDER) {
			mAdapter.setShowType(NoteAdapter.SHOW_TYPE_NORMAL);
			mAdapter.notifyDataSetChanged();
			return;
		}

		if (mFolderID != FOLDER_NO) {
			mFolderID = FOLDER_NO;
			updateDisplay();
			return;
		}
		super.finish();
	}

	@Override
	public void onBackPressed() {// 覆盖返回键,2秒内连续按2次返回键，就退出应用
		finish();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, MENU_NEW_NOTE, 1, R.string.new_note).setIcon(R.drawable.new_note);
		menu.add(Menu.NONE, MENU_NEW_FOLDER, 2, R.string.new_folder).setIcon(R.drawable.new_folder);
		menu.add(Menu.NONE, Menu_update_folder, 2, R.string.edit_folder_title).setIcon(R.drawable.edit_folder_title);
		menu.add(Menu.FIRST, Menu_delete, 3, R.string.delete).setIcon(R.drawable.note_delete);
		menu.add(Menu.FIRST, Menu_delete_ALL, 4, R.string.delete_all).setIcon(R.drawable.note_delete);
		menu.add(Menu.NONE, Menu_movetoFolder, 5, R.string.movetoFolder).setIcon(R.drawable.menu_move);
		menu.add(Menu.NONE, Menu_moveoutFolder, 5, R.string.moveoutFolder).setIcon(R.drawable.menu_move);
		menu.add(Menu.NONE, MENU_EXPORT, 5, R.string.menu_export).setIcon(R.drawable.ic_menu_goto);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		Log.v(TAG, "onPrepareOptionsMenu menu");
		if (mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_DELETE
				|| mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_MOVETOFOLDER) {
			return false;
		}

		if (mAdapter.getCount() == 0) {
			menu.setGroupVisible(Menu.FIRST, false);
		} else {
			menu.setGroupVisible(Menu.FIRST, true);
		}

		if (mFolderID == FOLDER_NO) {
			menu.findItem(Menu_update_folder).setVisible(false);
			menu.findItem(Menu_moveoutFolder).setVisible(false);
			menu.findItem(MENU_NEW_FOLDER).setVisible(true);

			if (mAdapter.getFolderCount() == 0 || mAdapter.getNotesCount() == 0) {
				menu.findItem(Menu_movetoFolder).setVisible(false);
			} else {
				menu.findItem(Menu_movetoFolder).setVisible(true);
			}

		} else {
			menu.findItem(MENU_NEW_FOLDER).setVisible(false);
			menu.findItem(Menu_movetoFolder).setVisible(false);
			menu.findItem(Menu_update_folder).setVisible(true);

			if (mAdapter.getCount() == 0) {
				menu.findItem(Menu_moveoutFolder).setVisible(false);
			} else {
				menu.findItem(Menu_moveoutFolder).setVisible(true);
			}
		}

		if (getDataManager(this).getNotes().size() == 0) {
			menu.findItem(MENU_EXPORT).setVisible(false);
		} else {
			menu.findItem(MENU_EXPORT).setVisible(true);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_NEW_NOTE:
			newNote();
			break;
		case MENU_NEW_FOLDER:
			newFolder();
			break;

		case Menu_delete: // 删除多项（checkbox）
			mAdapter.setShowType(NoteAdapter.SHOW_TYPE_DELETE);
			break;

		case Menu_delete_ALL: // 删除全部
			deleteAllNotes();
			break;
		case Menu_movetoFolder:
			mAdapter.setShowType(NoteAdapter.SHOW_TYPE_MOVETOFOLDER);
			break;

		case Menu_moveoutFolder:
			mAdapter.setShowType(NoteAdapter.SHOW_TYPE_MOVETOFOLDER);

			break;
		case Menu_update_folder:
			selectedItemID = mFolderID;
			renameFolder();
			break;

		case MENU_EXPORT:
			exportAllNotes();// 导出
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * 导出所有便签
	 */
	@SuppressWarnings("unchecked")
	private void exportAllNotes() {
		if (!FileUtis.isSDCardExist()) {// 如果SD卡不存在，提示用户
			Toast.makeText(this, R.string.sdcard_is_not_exist, Toast.LENGTH_SHORT).show();
			return;
		}

		File filePath = null;
		try {
			filePath = FileUtis.makeFilePath(this);// 创建文件
		} catch (IOException e) {
			e.printStackTrace();
			Toast.makeText(this, R.string.can_not_make_file, Toast.LENGTH_SHORT).show();
		}

		List<NoteItem> notes = getDataManager(this).getNotes();
		ExportTask task = new ExportTask(this, filePath, notes.size());// 实例化一个异步任务
		task.execute(notes);// 启动异步任务保存便签
	}

	/**
	 * 异步任务保存导出的便签
	 * 
	 * @author way
	 * 
	 */
	public class ExportTask extends AsyncTask<List<NoteItem>, Integer, Boolean> {
		ProgressDialog progressDialog = null;
		File path;// 文件
		private int maxSize;// 最大尺寸
		private Context context;
		String title;
		String time;
		String content;

		public ExportTask(Context context, File filePath, int maxSize) {
			this.context = context;
			this.maxSize = maxSize;
			this.path = filePath;

			initStrings();
			initDialog(context, maxSize);
		}

		private void initDialog(Context context, int maxSize) {
			progressDialog = new ProgressDialog(context, 0);
			progressDialog.setTitle(R.string.exporting);
			progressDialog.setCancelable(false);
			progressDialog.setMax(maxSize);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		}

		private void initStrings() {
			time = context.getString(R.string.export_time);
			title = context.getString(R.string.export_title);
			content = context.getString(R.string.export_content);
		}

		@Override
		protected void onPreExecute() {
			progressDialog.show();
		}

		@Override
		protected Boolean doInBackground(List<NoteItem>... params) {
			boolean success = true;
			List<NoteItem> notes = params[0];
			List<String> values = getNotesString(notes);

			for (int i = 0; i < values.size(); i++) {
				try {
					FileUtis.saveStringWithAppendMode(path, values.get(i));
					publishProgress(i);
				} catch (IOException e) {
					e.printStackTrace();
					success = false;
				}
			}
			return success;
		}

		private List<String> getNotesString(List<NoteItem> notes) {
			StringBuilder sb = new StringBuilder();
			List<String> noteStrings = new ArrayList<String>();

			for (int i = 0; i < notes.size(); i++) {
				NoteItem item = notes.get(i);
				sb.append(time).append(item.getDate(context) + "   " + item.getTime(context)).append('\n');
				sb.append(title).append(item.getExprotTitle(context)).append('\n');
				sb.append(content).append(item.getExprotContent(context)).append('\n');
				sb.append("------------").append('\n');
				if (i != 0 && i % 10 == 0) { // export 10 items one time
					noteStrings.add(sb.toString());
					sb = null;
					sb = new StringBuilder();
				}
			}
			noteStrings.add(sb.toString());
			return noteStrings;
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			Integer integer = values[0];
			progressDialog.setProgress(integer);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			progressDialog.dismiss();
			if (result) {
				String log = context.getString(R.string.export_sucess_log, maxSize + "", path.toString());
				Toast.makeText(context, log, Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(context, R.string.export_error, Toast.LENGTH_SHORT).show();
			}
		}
	}

	Handler mHandler = new Handler();
	ProgressDialog mProgressDialog = null;// 窗口进度条
	Runnable mDeleting = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.v(TAG, " progressDialog.show()********** ");
			String delete = NoteActivity.this.getString(R.string.delete);
			String deleting = NoteActivity.this.getString(R.string.Deleting);
			mProgressDialog = ProgressDialog.show(NoteActivity.this, delete, deleting);
		}
	};

	Runnable mFinishDelete = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.v(TAG, " progressDialog.dismiss()*************** ");
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
				mProgressDialog = null;
				// 删除成功，更新UI
				updateDisplay();
			}
		}

	};

	// 创建新文件夹
	private void newFolder() {
		if (getDataManager(this).getFolderAndAllItems().size() > MAX_FOLDERS) {
			Toast.makeText(this, R.string.toast_add_fail, Toast.LENGTH_SHORT).show();
			return;
		}
	}

	// 观察输入的文件名长度
	TextWatcher textwatch = new TextWatcher() {
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}

		public void afterTextChanged(Editable s) {
			if (s.length() > MAX_FOLDERNAMENUM) {
				s.delete(MAX_FOLDERNAMENUM, s.length());
			}
		}
	};

	// 文件夹同名处理函数
	private void insertFolder(String folderName) {
		List<NoteItem> folders = getDataManager(this).getFolders();
		for (NoteItem noteItem : folders) {
			if (noteItem.content.equals(folderName)) {
				Toast.makeText(NoteActivity.this, R.string.Thisfolderalreadyexists, Toast.LENGTH_SHORT).show();
				return;
			}
		}
		NoteItem item = new NoteItem(folderName, new Date().getTime(), false, -1, true, -1);
		getDataManager(this).insertItem(item);
	}

	Runnable mDeleteThread = new Runnable() {
		// 此为非ui线程，不能处理ui操作。
		@Override
		public void run() {
			mHandler.removeCallbacks(mDeleting);
			mHandler.post(mDeleting);

			List<NoteItem> items = getDataManager(NoteActivity.this).getNotesFromFolder(mFolderID);
			for (NoteItem item : items) {
				getDataManager(NoteActivity.this).deleteNoteItem(item);
			}
			mHandler.removeCallbacks(mFinishDelete);
			mHandler.post(mFinishDelete);
			mHandler.post(mToast_delete_success);
		}

	};

	Runnable mToast_delete_success = new Runnable() {
		@Override
		public void run() {
			Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_LONG).show();
		}
	};

	Runnable mToast_list_is_empty = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Toast.makeText(getApplicationContext(), R.string.list_is_empty, Toast.LENGTH_LONG).show();
		}
	};

	// // 删除全部便签和文件夹
	private void deleteAllNotes() {
	}

	// 删除多条便签
	private void deleteSomeNotes() {
	}

	// 移入文件夹
	private void moveToFolder() {
		List<NoteItem> folders = getDataManager(this).getFolders();
		if (folders.size() == 0) {
			Toast.makeText(getApplicationContext(), R.string.no_folder_found, Toast.LENGTH_LONG).show();
			return;
		}

		String[] folderNameStr = new String[folders.size()];
		for (int i = 0; i < folders.size(); i++) {
			folderNameStr[i] = folders.get(i).content;
		}
		Bundle bundle = new Bundle();
		bundle.putStringArray(DIALOG_KEY_1, folderNameStr);
		removeDialog(DIALOG_MOVE_FOLDER);
	}

	// 移除文件夹
	private void moveOutOfFolder() {
		NoteDataManager dataManager = getDataManager(NoteActivity.this);

		if (mAdapter.getSelectedCount() == 0) { // select is empty.
			Toast.makeText(getApplicationContext(), R.string.pleasechoosenote, Toast.LENGTH_LONG).show();
		}

		for (NoteItem item : mItems) {
			if (item.isSelected) {
				item.parentFolder = -1;
				item.isSelected = false;
				dataManager.updateItem(item);
			}
		}
		mAdapter.setShowType(NoteAdapter.SHOW_TYPE_NORMAL);
		updateDisplay();
	}

	// 修改文件夹名称
	private void renameFolder() {
		removeDialog(DIALOG_RENAME_FOLDER);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.new_note_button:
			newNote();
			break;
		}
	}

	private void selectItems4Move() {
		for (int i = 0; i < mItems.size(); i++) {
			if (!mItems.get(i).isFileFolder) {
				mAdapter.getItem(i).isSelected = true;
			}
		}
		mAdapter.notifyDataSetChanged();
	}

	private void setNormalMode() {
		mAdapter.setShowType(NoteAdapter.SHOW_TYPE_NORMAL);
		mAdapter.setAllItemCheckedAndNotify(false);
	}

	private void newNote() {
		if (mAdapter != null && mAdapter.getCount() > MAX_NOTES) {
			Toast.makeText(this, R.string.toast_add_fail, Toast.LENGTH_SHORT).show();
			return;
		}

		Intent intent = new Intent();
		intent.setClass(NoteActivity.this, NoteEditor.class);
		if (mFolderID != FOLDER_NO) { // start intent in the folder.
			intent.putExtra(NoteEditor.OPEN_TYPE, NoteEditor.TYPE_NEW_FOLDER_NOTE);
			intent.putExtra(NoteEditor.NOTE_FOLDER_ID, mFolderID);
		} else {
			intent.putExtra(NoteEditor.OPEN_TYPE, NoteEditor.TYPE_NEW_NOTE);
		}
		startActivity(intent);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Log.v(TAG, "list click position : " + position);
		// in delete or move mode, do it here.
		if (mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_DELETE
				|| mAdapter.getShowType() == NoteAdapter.SHOW_TYPE_MOVETOFOLDER) {
			ListItemView listItems = (ListItemView) view.getTag();
			listItems.mCheck.toggle();
			NoteAdapter adapter2 = (NoteAdapter) parent.getAdapter();
			adapter2.toggleChecked(position);
			return;
		}
		// in normal mode
		NoteItem noteItem = mItems.get(position);
		if (noteItem.isFileFolder) {
			mFolderID = noteItem._id;
			updateDisplay();
		} else {
			/*
			 * Intent intent = new Intent(); intent.setClass(this,
			 * NoteEditor.class); intent.putExtra(NoteEditor.ID, noteItem._id);
			 * intent.putExtra(NoteEditor.OPEN_TYPE, NoteEditor.TYPE_EDIT_NOTE);
			 * startActivity(intent);
			 */
		}
	}

	int selectedItemID = -1;

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
		NoteItem contextNoteItem = mItems.get(info.position);
		selectedItemID = contextNoteItem._id;
		caoZuo(contextNoteItem);
		// menu.setHeaderTitle(contextNoteItem.getShortTitle());
		//
		// menu.add(0, Menu_delete, 0, R.string.delete);
		// if (contextNoteItem.isFileFolder) {
		// menu.add(0, Menu_folder_rename, 0, R.string.edit_folder_title);
		// return;
		// }
		// /*if (mFolderID == FOLDER_NO) {
		// menu.add(0, Menu_movetoFolder, 1, R.string.movetoFolder);
		// } else {
		// menu.add(0, Menu_moveoutFolder, 1, R.string.moveoutFolder);
		// }*/
		// menu.add(0, Menu_setAlarm, 1, R.string.setAlarm);
		// //menu.add(0, MENU_SHARE, 3, R.string.share);
	}

	@SuppressLint("NewApi")
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case Menu_delete:
			showDialog(DIALOG_DELETE, null);
			return true;

		case Menu_movetoFolder: { // 弹出选项菜单，选择移进哪个文件夹
			mAdapter.setItemChecked(menuInfo.position, true);
			moveToFolder();
			return true;
		}
		case Menu_setAlarm: { // 设置便签闹铃
			NoteItem noteItem = mItems.get(menuInfo.position);
			Intent intent = new Intent(this, AlarmActivity.class);
			intent.putExtra(AlarmActivity.NOTE_ID, noteItem._id);
			this.startActivityForResult(intent, 1);
			return true;
		}
		case Menu_folder_rename: // 文件夹修改名称
			renameFolder();
			return true;
		case Menu_moveoutFolder:
			mAdapter.setItemChecked(menuInfo.position, true);
			moveOutOfFolder();
			return true;
		case MENU_SHARE:
			NoteItem noteItem = mItems.get(menuInfo.position);
			IntentUtils.sendSharedIntent(this, noteItem);
			return true;
		}

		return false;
	}

	public static final int DIALOG_DELETE = 0;
	public static final int DIALOG_NEW_FOLDER = 1;
	public static final int DIALOG_DELTE_SOME_NOTES = 2;
	public static final int DIALOG_DELTE_ALL_NOTES = 3;
	public static final int DIALOG_MOVE_FOLDER = 4;
	public static final int DIALOG_RENAME_FOLDER = 5;

	public static final String DIALOG_KEY_1 = "key1";

	@Override
	protected Dialog onCreateDialog(int id, final Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_DELETE:
			builder.setNegativeButton(R.string.Cancel, cancelDialog);
			builder.setTitle(R.string.delete_note);
			builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					final NoteItem noteItem = getDataManager(NoteActivity.this).getNoteItem(selectedItemID);
					getDataManager(NoteActivity.this).deleteNoteItem(noteItem);

					Toast.makeText(getApplicationContext(), R.string.delete_success, 1000).show();
					updateDisplay();
				}
			});
			break;

		case DIALOG_NEW_FOLDER:
			builder.setNegativeButton(R.string.Cancel, cancelDialog);
			builder.setTitle(R.string.new_folder);
			View layout = LayoutInflater.from(this).inflate(R.layout.note_dialog_layout_new_folder, null);
			builder.setView(layout);
			// 实例化AlertDialog中的EditText对象
			final EditText newFolderEditText = (EditText) layout.findViewById(R.id.et_dialog_new_folder);
			// 对EditText输入内容监听，如果超过最大字符数限制，则弹出提示
			newFolderEditText.addTextChangedListener(textwatch);
			// 设置一个确定的按钮
			builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newFolderName = newFolderEditText.getText().toString().trim();
					newFolderEditText.setText("");
					// TODO: the folder name need to not same.
					if (newFolderName.length() > 0) {
						insertFolder(newFolderName);
						NoteActivity.this.updateDisplay();
						IntentUtils.keepDialog(dialog, true);
					} else { // 文件夾名稱为空时，保存失敗
						Toast.makeText(NoteActivity.this, R.string.folder_add_fail, 1000).show();
						IntentUtils.keepDialog(dialog, false);
					}

				}
			});
			break;

		case DIALOG_DELTE_SOME_NOTES:
			builder.setNegativeButton(R.string.Cancel, cancelDialog);
			builder.setTitle(R.string.delete_note);
			builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					int selectedCount = 0;
					for (int i = 0; i < mAdapter.getCount(); i++) {
						if (mAdapter.getItem(i).isSelected) {
							selectedCount++;
							NoteItem item = mItems.get(i);
							getDataManager(NoteActivity.this).deleteNoteItem(item);
						}
					}
					updateDisplay();
					if (selectedCount > 0) {
						Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_LONG).show();
						mAdapter.setShowType(NoteAdapter.SHOW_TYPE_NORMAL);
					} else {
						Toast.makeText(getApplicationContext(), R.string.selected_is_empty, Toast.LENGTH_LONG).show();
					}
				}
			});
			break;
		case DIALOG_DELTE_ALL_NOTES:
			builder.setNegativeButton(R.string.Cancel, cancelDialog);
			builder.setTitle(R.string.delete_all_note);
			builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO: is time-consumeing operator???
					new Thread(mDeleteThread).start();
				}
			});
			break;
		case DIALOG_MOVE_FOLDER:
			builder.setTitle(R.string.movetoFolder);
			String[] folderTitle = args.getStringArray(DIALOG_KEY_1);
			builder.setItems(folderTitle, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					NoteDataManager dataManager = getDataManager(NoteActivity.this);
					if (dataManager.getFolders().size() == 0) { // list
						// is
						// empty.
						Toast.makeText(getApplicationContext(), R.string.listis_empty, Toast.LENGTH_LONG).show();
						return;
					}

					if (mAdapter.getSelectedCount() == 0) { // select is
						// empty.
						Toast.makeText(getApplicationContext(), R.string.pleasechoosenote, Toast.LENGTH_LONG).show();
						return;
					}

					NoteItem folderItem = dataManager.getFolders().get(which);
					for (NoteItem item : mItems) {
						if (item.isSelected) {
							item.parentFolder = folderItem._id;
							item.isSelected = false;
							dataManager.updateItem(item);
						}
					}
					mAdapter.setShowType(NoteAdapter.SHOW_TYPE_NORMAL);
					updateDisplay();
				}
			});
			break;
		case DIALOG_RENAME_FOLDER:
			builder.setNegativeButton(R.string.Cancel, cancelDialog);
			builder.setTitle(R.string.edit_folder_title);
			View renameLayout = LayoutInflater.from(this).inflate(R.layout.note_dialog_layout_new_folder,
					(ViewGroup) findViewById(R.id.dialog_layout_new_folder_root));
			builder.setView(renameLayout);

			final EditText mFolderEditText = (EditText) renameLayout.findViewById(R.id.et_dialog_new_folder);

			final NoteItem noteItem = getDataManager(this).getNoteItem(selectedItemID);
			mFolderEditText.setText(noteItem.content);
			mFolderEditText.setSelection(noteItem.content.length());
			mFolderEditText.addTextChangedListener(textwatch);

			builder.setPositiveButton(R.string.Ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String newFolderName = mFolderEditText.getText().toString();
					// 新文件夹名称不为空,且不等于原有的名称,则更新
					if (newFolderName.length() == 0 || newFolderName.equals(noteItem.content)) {
						Toast.makeText(NoteActivity.this, R.string.folder_add_fail, Toast.LENGTH_SHORT).show();
						return;
					}

					List<NoteItem> folders = getDataManager(NoteActivity.this).getFolders();
					for (NoteItem item : folders) {
						if (newFolderName.equals(item.content)) {
							Toast.makeText(NoteActivity.this, R.string.Thisfolderalreadyexists, Toast.LENGTH_SHORT)
									.show();
							return;
						}
					}

					noteItem.content = newFolderName;
					getDataManager(NoteActivity.this).updateItem(noteItem);
					updateDisplay();
				}
			});

		default:
			break;
		}
		return builder.create();
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		selectedItemID = savedInstanceState.getInt("selectedItemID");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("selectedItemID", selectedItemID);
	}

	/**
	 * 选择是否删除该记事
	 * 
	 * @param contextNoteItem
	 */
	private void deleteTixing(final NoteItem contextNoteItem) {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this.getParent());
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.tuichu_login, null);
		final Dialog dialog = new AlertDialog.Builder(this.getParent()).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView update_number = (TextView) layout.findViewById(R.id.update_number);
		update_number.setText("您确定要删除该提醒吗？");
		TextView no_tuichu = (TextView) layout.findViewById(R.id.no_tuichu);
		no_tuichu.setText("取消");
		TextView queding_tuichu = (TextView) layout.findViewById(R.id.queding_tuichu);
		queding_tuichu.setText("确定");
		no_tuichu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();

			}
		});
		queding_tuichu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getDataManager(NoteActivity.this).deleteNoteItem(contextNoteItem);

				Toast.makeText(getApplicationContext(), R.string.delete_success, Toast.LENGTH_SHORT).show();
				updateDisplay();
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

	/**
	 * 选择对当前记事的操作
	 * 
	 * @param contextNoteItem
	 */
	public void caoZuo(final NoteItem contextNoteItem) {
		// 将布局文件转化成view对象
		LayoutInflater inflaterDl = LayoutInflater.from(this.getParent());
		LinearLayout layout = (LinearLayout) inflaterDl.inflate(R.layout.js_change_caozuo, null);
		final Dialog dialog = new AlertDialog.Builder(this.getParent()).create();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int width = dm.widthPixels;
		dialog.show();
		WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
		params.width = width * 4 / 5;
		dialog.getWindow().setAttributes(params);
		dialog.getWindow().setContentView(layout);
		TextView jishi_content = (TextView) layout.findViewById(R.id.jishi_content);
		TextView delete_js = (TextView) layout.findViewById(R.id.delete_js);
		TextView update_jishi = (TextView) layout.findViewById(R.id.update_jishi);
		ImageView guanbi = (ImageView) layout.findViewById(R.id.guanbi);
		guanbi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});
		jishi_content.setText(contextNoteItem.getShortTitle());
		delete_js.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				// showDialog(DIALOG_DELETE, null);
				deleteTixing(contextNoteItem);
			}
		});
		update_jishi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(NoteActivity.this, AlarmActivity.class);
				intent.putExtra(AlarmActivity.NOTE_ID, contextNoteItem._id);
				intent.putExtra("count", contextNoteItem.getShortTitle());
				startActivityForResult(intent, 1);
				dialog.dismiss();
			}
		});
		dialog.setCanceledOnTouchOutside(false);
	}

}