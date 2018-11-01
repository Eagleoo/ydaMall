package com.way.note;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.URLSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mall.view.R;
import com.way.note.data.NoteDataManager;
import com.way.note.data.NoteItem;
import com.way.note.utils.IntentUtils;
import com.way.note.utils.StringUtils;

/**
 * 一条便签的详细信息页面，查看和编辑
 * 
 * @author way
 */
@SuppressLint("ResourceAsColor")
public class NoteEditor extends BaseActivity implements View.OnClickListener {
	public static final String TAG = "NoteEditor";
	public static final String OPEN_TYPE = "open_type";
	public static final String ID = "id";
	public static final String NOTE_FOLDER_ID = "folder_id";

	public static final int TYPE_NEW_NOTE = 0;
	public static final int TYPE_NEW_FOLDER_NOTE = 1;
	public static final int TYPE_EDIT_NOTE = 2;
	public static final int TYPE_EDIT_FOLDER_NOTE = 3;

	private int mOpenType;// 用于判断是新建便签还是更新便签

	private TextView mDateTimeTextView;
	private EditText mNowContent;

	private String mOldContent;// 数据库中原有的便签的内容
	private Intent mIntent;// 接受传递过来的Intent对象
	private int mId;// 被编辑的便签的ID
	int mAlarmEnable = 0;// 便签闹铃是否开启

	TextView mRemainNumTextView;// 用来显示剩余字数
	TextView mTextView;
	int mMaxNum = 1000;// 便签输入限制的最大字数
	int num = 0;
	private Boolean save_success;// 是否保存成功
	private boolean needSave = true;
	private Date date;// 日期
	private DateFormat dateFormat;// 日期形式
	private boolean is24Hours;// 是否为24小时制

	long mPreTimeStamp = -1;
	static String WHERE_TIME;
	String mPreDescription;
	private String note_id = "";
	// 菜单
	private static final int Menu_delete = Menu.FIRST;
	private static final int Menu_setAlarm = Menu.FIRST + 1;
	private static final int Menu_send_home = Menu.FIRST + 2;
	private static final int Menu_save = Menu.FIRST + 3;
	private static final int Menu_cancel = Menu.FIRST + 4;
	private static final int Menu_Refresh = Menu.FIRST + 5;
	private static final int MENU_SHARE = Menu.FIRST + 6;
	private static final int MENU_EDIT_TITLE = Menu.FIRST + 7;
	private String count;
	private String title;
	NoteDataManager mDataManager = null;
	NoteItem noteItem = null;
	int noteDd;
	int folderID = -1;
	String mOldTitle = null;
	String mNewTitle = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mIntent = getIntent();
		Log.v(TAG, "OnCreate intent-->" + getIntent() + "  data-->"
				+ (mIntent == null ? "null" : mIntent.getExtras()));
		setContentView(R.layout.note_editor);

		if (Intent.ACTION_SEARCH.equals(getIntent().getAction())
				|| Intent.ACTION_VIEW.equals(getIntent().getAction())) {
			doSearch(mIntent);
		} else {
			initData();
		}
		initViews();
	}

	private void initData() {
		Intent intent = getIntent();
		mOpenType = intent.getIntExtra(OPEN_TYPE, -1);
		int noteID = intent.getIntExtra(ID, -1);
		folderID = intent.getIntExtra(NOTE_FOLDER_ID, -1);

		switch (mOpenType) {
		case TYPE_EDIT_NOTE:
		case TYPE_EDIT_FOLDER_NOTE:
			noteItem = getDataManager(this).getNoteItem(noteID);
			if (null == noteItem) {
				Log.d(TAG, "initData --> noteItem is null. Type: " + mOpenType
						+ "ID: " + noteID + ", folderID" + folderID);
				return;
			}
			mOldTitle = noteItem.getTitle();
			break;

		case TYPE_NEW_NOTE:
		case TYPE_NEW_FOLDER_NOTE:
			mOldContent = "";
			mOldTitle = getString(R.string.new_note);
			noteItem = new NoteItem(folderID);
			break;

		default:
			break;
		}
		num = noteItem.getContent().length();
		mOldContent = noteItem.content;
	}

	// 初始化组件
	private void initViews() {
		count=getIntent().getStringExtra("count");
		title=getIntent().getStringExtra("title");
		mRemainNumTextView = (TextView) findViewById(R.id.remain_num);
		mDateTimeTextView = (TextView) findViewById(R.id.tv_note_date_time);
		mNowContent = (EditText) findViewById(R.id.et_content);
		mNowContent.setText(count);
		mTextView = (TextView) findViewById(R.id.note_title);
		findViewById(R.id.editor_image).setOnClickListener(this);
		mNowContent.setAutoLinkMask(0x01 | 0x02 | 0x04);
		mNowContent.setVisibility(View.VISIBLE);
		mNowContent.setCursorVisible(true);
		mNowContent.setBackgroundResource(R.drawable.et_content_bg);
		mNowContent.addTextChangedListener(textwatch);

		mTextView.setOnClickListener(this);
		saveNote();
	}

	private void doSearch(Intent intent) {
		String searchContext = getIntent().getStringExtra(SearchManager.QUERY);
		Log.v(TAG, "doSearch : searchContext-->" + searchContext + " action-->"
				+ intent.getAction());
		if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
			needSave = false;
			Intent mainIntent = new Intent(NoteEditor.this,
					SearchResultActivity.class);
			mainIntent
					.putExtra(SearchResultActivity.KEY_CONTENT, searchContext);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
			mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(mainIntent);
			this.finish();
		} else if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {
			noteItem = getDataManager(this).getNoteItem(
					Integer.parseInt(searchContext));
		}

		if (noteItem != null) {
			Log.v(TAG, "doSearch -->" + noteItem);
			mOpenType = TYPE_EDIT_NOTE;
			num = noteItem.getContent().length();
			mOldContent = noteItem.content;
			mOldTitle = noteItem.getTitle();
		} else {
			Log.v(TAG, "doSearch --> noteItem is null");
			Toast.makeText(this, R.string.search_item_not_exist,
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.v(TAG, "onStart");
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v(TAG, "onResume");
		if (noteItem != null) {
			updateDisplay();
		}
	}

	private void updateDisplay() {
		mRemainNumTextView.setText(num + "" + "/" + mMaxNum);
		mDateTimeTextView.setText(noteItem.getTime(this) + "   "
				+ noteItem.getDate(this));
		mNowContent.setText(noteItem.getContent());
		// 定位光标位置
		int selection_end = (mNowContent.getText().toString()).length();
		mNowContent.setSelection(selection_end);

		mTextView.setText(mOldTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(menu.NONE, MENU_EDIT_TITLE, 1, R.string.edit_title).setIcon(
				R.drawable.ic_menu_edit);
		menu.add(Menu.NONE, Menu_setAlarm, 3, R.string.setAlarm).setIcon(
				R.drawable.alarm_clock);
		menu.add(Menu.NONE, Menu_save, 4, R.string.save).setIcon(
				R.drawable.notesave);
		menu.add(menu.NONE, MENU_SHARE, 5, R.string.share).setIcon(
				android.R.drawable.ic_menu_share);
		menu.add(Menu.NONE, Menu_delete, 6, R.string.delete).setIcon(
				R.drawable.note_delete);
		menu.add(Menu.NONE, Menu_cancel, 7, R.string.Cancel).setIcon(
				R.drawable.note_delete);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		if (mOpenType == TYPE_NEW_NOTE || mOpenType == TYPE_NEW_FOLDER_NOTE) {
			menu.findItem(Menu_delete).setVisible(false);
		} else {
			menu.findItem(Menu_cancel).setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.v(TAG,
				"onOptionsItemSelected: item.getItemId-->" + item.getItemId());
		switch (item.getItemId()) {
		case Menu_delete:
			showDialog(DIALOG_DELETE);
			break;
		case Menu_cancel:
			showDialog(Dialog_CANCLE);
			break;
		case Menu_Refresh:
			refresh();
			break;
		/*
		 * case Menu_send_home: addShortCut(); break;
		 */
		case Menu_setAlarm:
			setAlarm();
			break;
		case Menu_save:
			saveNote();
			break;
		case MENU_SHARE:
			if (null != noteItem) {
				saveNote();
				IntentUtils.sendSharedIntent(this, noteItem);
			} else {
				Log.d(TAG, "noteItem is null");
			}
			break;
		case MENU_EDIT_TITLE:
			editTitle();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void editTitle() {
		/*removeDialog(DIALOG_EDIT_TITLE);
		//showDialog(DIALOG_EDIT_TITLE, null);
*/	}

	public static final int DIALOG_DELETE = 0;
	public static final int Dialog_CANCLE = 1;
	public static final int DIALOG_EDIT_TITLE = 2;

	@Override
	protected Dialog onCreateDialog(int id, final Bundle args) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setNegativeButton(R.string.Cancel, cancelDialog);
		switch (id) {
		case DIALOG_DELETE:
			builder.setTitle(R.string.delete_note);
			builder.setPositiveButton(R.string.Ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							if (null == noteItem) {
								Log.d(TAG,
										"onCreateDialog --> noteItem is null");
								return;
							}
							needSave = false;
							getDataManager(NoteEditor.this).deleteNoteItem(
									noteItem);
							Toast.makeText(getApplicationContext(),
									R.string.delete_success, Toast.LENGTH_SHORT)
									.show();
							NoteEditor.this.finish();
						}
					});
			break;

		case Dialog_CANCLE:
			builder.setTitle(R.string.Cancel);
			builder.setPositiveButton(R.string.Ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							needSave = false;
							NoteEditor.this.finish();
						}
					});
			break;

		case DIALOG_EDIT_TITLE:
			builder.setTitle(R.string.edit_title);
			View layout = LayoutInflater.from(this).inflate(
					R.layout.note_dialog_layout_new_folder, null);
			builder.setView(layout);
			final EditText titleView = (EditText) layout
					.findViewById(R.id.et_dialog_new_folder);

			String title = getNowTitle2Edit();
			if (null != title) {
				titleView.setText(title);
				titleView.setSelection(0, title.length());
			}
			builder.setPositiveButton(R.string.Ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							String title = titleView.getText().toString()
									.trim();
							titleView.setText("");
							if (title.length() > 0) {
								mNewTitle = title;
								mTextView.setText(title);
							} else { // 文件夾名稱为空时，保存失敗
								Toast.makeText(NoteEditor.this,
										R.string.title_len_is_zero,
										Toast.LENGTH_SHORT).show();
							}

						}
					});
			break;
		}
		return builder.create();
	}

	private String getNowTitle2Edit() {
		String title = null;
		if (mNewTitle != null) {
			title = mNewTitle;
		} else if (mOpenType == TYPE_NEW_NOTE
				|| mOpenType == TYPE_NEW_FOLDER_NOTE) {
			title = "";
		} else {
			title = mOldTitle;
		}
		return title;
	}

	private void refresh() {
		Log.v(TAG, "refresh");
		String content = StringUtils.deleteEndSpace(mNowContent.getText()
				.toString());
		mNowContent.setText(content);
		mNowContent.setSelection(content.length());
	}

	private void setAlarm() {
		if (null == noteItem) {
			Log.d(TAG, "setAlarm --> noteItem is null");
			return;
		}
		saveNote();

		String oldContent = mNowContent.getText().toString();
		if (oldContent.equals("")) {
			Toast.makeText(NoteEditor.this, R.string.input_content,
					Toast.LENGTH_SHORT).show();
		} else {
			needSave = true;
			Intent intent = new Intent(this, AlarmActivity.class);
			intent.putExtra(AlarmActivity.NOTE_ID, noteItem._id);
			this.startActivity(intent);
		}
	}

	// 监测编辑框字数变化
	TextWatcher textwatch = new TextWatcher() {
		private CharSequence temp;
		private int selectionStart;
		private int selectionEnd;

		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			temp = s;
		}

		public void afterTextChanged(Editable s) {
			int number = s.length();
			mRemainNumTextView.setText("" + number + "/" + mMaxNum);

			selectionStart = mNowContent.getSelectionStart();
			selectionEnd = mNowContent.getSelectionEnd();
			if (temp.length() > mMaxNum) {
				Toast.makeText(NoteEditor.this, R.string.toast_edit_outbound,
						Toast.LENGTH_SHORT).show();
				Log.v(TAG, " selectionStart=" + selectionStart
						+ " ; selectionEnd =" + selectionEnd);
				// 截断超出的内容
				int i = temp.length() - mMaxNum; // 超出多少字符
				s.delete(selectionStart - i, selectionEnd);
				int tempSelection = selectionEnd;
				mNowContent.setText(s);
				mNowContent.setSelection(tempSelection); // 设置光标在最后
			}
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		Log.i(TAG, "onNewIntent() intent-->" + intent + " extars-->"
				+ (intent == null ? "null" : intent.getExtras().toString()));
		setIntent(intent);
		doSearch(intent);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(TAG, "onPause() needSave-->" + needSave);
		if (needSave) {
			saveNote();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		Log.i(TAG, "OnStop");
	}

	// 保存便签
	private void saveNote() {
		String content = StringUtils.deleteEndSpace(mNowContent.getText()
				.toString());
		Log.v(TAG, "openType =" + mOpenType + " content-->" + content);
		switch (mOpenType) {
		case TYPE_NEW_NOTE:
		case TYPE_NEW_FOLDER_NOTE:
			insertNote(content);
			break;
		case TYPE_EDIT_NOTE:
		case TYPE_EDIT_FOLDER_NOTE:
			updateNote(content);
			break;

		default:
			break;
		}
		//Util.showIntent(this, NoteMainFrame.class);
		Intent intent=new Intent(this, NoteActivity.class);
		intent.putExtra("set", "set");
		intent.putExtra("time", title);
		startActivity(intent);
		finish();
	}

	private boolean isTitleUpdate() {
		switch (mOpenType) {
		case TYPE_NEW_NOTE:
		case TYPE_NEW_FOLDER_NOTE:
			if (mNewTitle != null) {
				return true;
			}

		case TYPE_EDIT_NOTE:
		case TYPE_EDIT_FOLDER_NOTE:
			if (mNewTitle != null && !mNewTitle.equals(mOldTitle)) {
				return true;
			}
		}
		return false;
	}

	private void updateTitle() {
		switch (mOpenType) {
		case TYPE_NEW_NOTE:
		case TYPE_NEW_FOLDER_NOTE:
			if (mNewTitle != null) {
				mOldTitle = mNewTitle;
				noteItem.title = mNewTitle;
			}

		case TYPE_EDIT_NOTE:
		case TYPE_EDIT_FOLDER_NOTE: {
			if (mNewTitle != null) {
				mOldTitle = mNewTitle;
				noteItem.title = mNewTitle;
			}
		}
		}
	}

	private void insertNote(String content) {
		if (content.length() > 0 || isTitleUpdate()) { // 不为空,插入记录
			if (null == noteItem) {
				Log.d(TAG, "insertNote --> noteItem is null");
				return;
			}
			updateTitle();
			noteItem.content = content;
			getDataManager(this).insertItem(noteItem);

			mOldContent = content;
			mOpenType = TYPE_EDIT_NOTE;
			Toast.makeText(NoteEditor.this, R.string.save_success,
					Toast.LENGTH_SHORT).show();
			save_success = true;
		} else {
			// 如果编辑的内容为空，提示保存失败！
			Toast.makeText(NoteEditor.this, R.string.save_failed,
					Toast.LENGTH_SHORT).show();
			save_success = false;
		}
	}

	private void updateNote(String content) {
		Log.v(TAG, "_id =" + mId + " ; content =" + content);
		// 内容不等于旧记录的内容且不为空,更新记录
		if ((content.length() > 0 && !content.equals(mOldContent))
				|| isTitleUpdate()) {
			if (null == noteItem) {
				Log.d(TAG, "updateNote --> noteItem is null");
				return;
			}
			updateTitle();
			noteItem.content = content;
			noteItem.date = new Date().getTime();
			getDataManager(this).updateItem(noteItem);
			mOldContent = content;
			Toast.makeText(NoteEditor.this, R.string.save_success,
					Toast.LENGTH_SHORT).show();
			Log.v(TAG, "updateNote success");
			save_success = true;
		} else if (content.length() == 0) {
			// 如果编辑的内容为空，提示保存失败！
			Toast.makeText(NoteEditor.this, R.string.save_failed,
					Toast.LENGTH_SHORT).show();
			save_success = false;
			Log.v(TAG, "updateNote failture");
		}
	}

	// 添加到左面快捷方式，留待以后完善
	private void addShortCut() {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.v(TAG, "onDestory");
	}

	/**
	 * A custom EditText that draws lines between each line of text that is
	 * displayed.
	 */
	public static class LinedEditText extends EditText {
		private Rect mRect;
		private Paint mPaint;
		private static final String TAG = "NoteEditText";
		private static final String SCHEME_TEL = "tel:";
		private static final String SCHEME_HTTP = "http:";
		private static final String SCHEME_EMAIL = "mailto:";

		private static final Map<String, Integer> sSchemaActionResMap = new HashMap<String, Integer>();
		static {
			sSchemaActionResMap.put(SCHEME_TEL, R.string.tel);
			sSchemaActionResMap.put(SCHEME_HTTP, R.string.web);
			sSchemaActionResMap.put(SCHEME_EMAIL, R.string.email);
		}

		// we need this constructor for LayoutInflater
		public LinedEditText(Context context, AttributeSet attrs) {
			super(context, attrs);

			setWillNotDraw(false);
			setLinkTextColor(0xFF00FF00);
			setLinksClickable(false);
			setHintTextColor(Color.GRAY);
			mRect = new Rect();
			mPaint = new Paint();
			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(0x800000FF);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			int count = getLineCount(); // method entends from TextView.
			Rect r = mRect;
			Paint paint = mPaint;

			for (int i = 0; i < count; i++) {
				// 来获取特定行的基准高度值，而且这个函数第二个参数会返回此行的“外包装”值。再利用这些值绘制这一行的线条.返回的是当前行的左上角Y坐标.
				// method entends from TextView.
				int baseline = getLineBounds(i, r);
				// Log.d(TAG, "LinedEditText*******************************"
				// + "onDraw:" + baseline);
				canvas.drawLine(r.left, baseline + 5, r.right, baseline + 5,
						paint);
			}

			super.onDraw(canvas);
		}

		@Override
		protected void onCreateContextMenu(ContextMenu menu) {
			if (getText() instanceof Spanned) {
				int selStart = getSelectionStart();
				int selEnd = getSelectionEnd();

				int min = Math.min(selStart, selEnd);
				int max = Math.max(selStart, selEnd);

				final URLSpan[] urls = ((Spanned) getText()).getSpans(min, max,
						URLSpan.class);
				if (urls.length == 1) {
					int defaultResId = 0;
					for (String schema : sSchemaActionResMap.keySet()) {
						if (urls[0].getURL().indexOf(schema) >= 0) {
							defaultResId = sSchemaActionResMap.get(schema);
							break;
						}
					}

					if (defaultResId == 0) {
						defaultResId = R.string.note_link_other;
					}

					menu.add(0, 0, 0, defaultResId).setOnMenuItemClickListener(
							new OnMenuItemClickListener() {
								public boolean onMenuItemClick(MenuItem item) {
									// goto a new intent
									urls[0].onClick(LinedEditText.this);
									return true;
								}
							});
				}
			}
			super.onCreateContextMenu(menu);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.editor_image:
		case R.id.note_title:
			editTitle();
			break;

		default:
			break;
		}
	}
}