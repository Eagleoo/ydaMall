package com.way.note.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.way.note.utils.ClockUtils;

/**
 * 便签管理类具体实现
 * 
 * @author way
 * 
 */
public class NoteDataManagerImpl implements NoteDataManager {
	private static final String TAG = "NoteDataManagerImpl";
	NoteList mNoteList = new NoteList();
	private static NoteDataManager mDataManager = null;
	private DBOpenHelper mhelper;
	private SQLiteDatabase mDb;
	private Context mContext;
	private volatile boolean mNeedUpdate = false;

	/**
	 * 私有构造器，单例模式
	 * 
	 * @param context
	 */
	private NoteDataManagerImpl(Context context) {
		mhelper = DBOpenHelper.getInstance(context);
		mDb = mhelper.getWritableDatabase();
		mContext = context;
	}

	/**
	 * 通过单例模式提供便签管理对象
	 * 
	 * @param context
	 * @return
	 */
	public static NoteDataManager getNoteDataManger(Context context) {
		if (mDataManager == null) {
			mDataManager = new NoteDataManagerImpl(context);
		}
		return mDataManager;
	}

	@Override
	public void initData(Context context) {
		mNoteList.clear();
		Cursor cursor = null;
		try {
			String orderBy = DBOpenHelper.NOTE_IS_FOLDER + "  desc , "
					+ DBOpenHelper.NOTE_UPDATE_DATE + " desc";
			cursor = mDb.query(DBOpenHelper.TABLE_NAME,
					DBOpenHelper.NOTE_ALL_COLUMS, null, null, null, null,
					orderBy);
			while (cursor.moveToNext()) {
				NoteItem noteItem = buildNoteItem(cursor, context);
				mNoteList.add(noteItem);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		mNeedUpdate = false;
	}

	NoteItem buildNoteItem(Cursor cursor, Context mContext) {
		NoteItem noteItem = new NoteItem();

		noteItem._id = cursor.getInt(cursor.getColumnIndex(DBOpenHelper.ID));
		noteItem.content = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.NOTE_CONTENT));
		noteItem.title = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.NOTE_TITLE));
		int alarmEnable = cursor.getInt(cursor
				.getColumnIndex(DBOpenHelper.NOTE_ALARM_ENABLE));
		if (alarmEnable == 1) {
			noteItem.alarmEnable = true;
		} else {
			noteItem.alarmEnable = false;
		}
		int isFileFolder = cursor.getInt(cursor
				.getColumnIndex(DBOpenHelper.NOTE_IS_FOLDER));
		if (isFileFolder == 1) {
			noteItem.isFileFolder = true;
		} else if (isFileFolder == 0) {
			noteItem.isFileFolder = false;
		}
		noteItem.parentFolder = cursor.getInt(cursor
				.getColumnIndex(DBOpenHelper.NOTE_PARENT_FOLDER));
		noteItem.date = cursor.getLong((cursor
				.getColumnIndex(DBOpenHelper.NOTE_UPDATE_DATE)));
		noteItem.clockItem.ringtoneUrl = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.RINGTONE_URI));
		noteItem.clockItem.ringtoneName = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.RINGTONE_NAME));
		noteItem.clockItem.isVibrate = cursor.getInt(cursor
				.getColumnIndex(DBOpenHelper.ISVIBRATE)) == 1 ? true : false;
		noteItem.clockItem.ringtoneDate = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.RINGTONE_DATE));
		noteItem.clockItem.ringtoneTime = cursor.getString(cursor
				.getColumnIndex(DBOpenHelper.RINGTONE_TIME));
		return noteItem;
	}

	@Override
	public List<NoteItem> getFolderAndAllItems() {
		updateFromDB();
		return mNoteList.getList();
	}

	@Override
	public NoteItem getNoteItem(int id) {
		updateFromDB();
		Log.v(TAG, "allNotes-->" + mNoteList);
		NoteItem item = mNoteList.getNoteItemByID(id);
		if (item == null && id > 0) {
			item = getNoteItemFromDB(id);
			initData(mContext);
		}
		return item;
	}

	@Override
	public int insertItem(NoteItem item) {
		ContentValues cv = buildValuesNoID(item);
		long lid = mDb.insert(DBOpenHelper.TABLE_NAME,
				DBOpenHelper.NOTE_CONTENT, cv);

		item._id = (int) lid;
		mNoteList.addOneItem(item);
		return (int) lid;
	}

	private ContentValues buildValuesNoID(NoteItem item) {
		ContentValues cv = new ContentValues();
		cv.put(DBOpenHelper.NOTE_CONTENT, item.content);
		cv.put(DBOpenHelper.NOTE_ALARM_ENABLE, item.alarmEnable ? 1 : 0);
		cv.put(DBOpenHelper.NOTE_BG_COLOR, item.bgColor);
		cv.put(DBOpenHelper.NOTE_IS_FOLDER, item.isFileFolder ? 1 : 0);
		cv.put(DBOpenHelper.NOTE_PARENT_FOLDER, item.parentFolder);
		cv.put(DBOpenHelper.NOTE_UPDATE_DATE, item.date);
		cv.put(DBOpenHelper.NOTE_TITLE, item.title == null ? "" : item.title);

		cv.put(DBOpenHelper.ISVIBRATE, item.clockItem.isVibrate ? 1 : 0);
		cv.put(DBOpenHelper.RINGTONE_URI, item.clockItem.ringtoneUrl);
		cv.put(DBOpenHelper.RINGTONE_DATE, item.clockItem.ringtoneDate);
		cv.put(DBOpenHelper.RINGTONE_TIME, item.clockItem.ringtoneTime);
		cv.put(DBOpenHelper.RINGTONE_NAME, item.clockItem.ringtoneName);
		return cv;
	}

	@Override
	public int updateItem(NoteItem item) {
		ContentValues cv = buildValuesNoID(item);
		long lid = mDb.update(DBOpenHelper.TABLE_NAME, cv, DBOpenHelper.ID
				+ "=?", new String[] { item._id + "" });

		mNoteList.updateOneItem(item);

		return (int) lid;
	}

	@Override
	public void deleteNoteItem(NoteItem item) {
		String noteWhereClause = " _id = ? or "
				+ DBOpenHelper.NOTE_PARENT_FOLDER + " = ? ";
		mDb.delete(DBOpenHelper.TABLE_NAME, noteWhereClause, new String[] {
				item._id + "", item._id + "" });
		if (item.alarmEnable) {
			ClockUtils.cancleAlarmClock(mContext, item);
		}

		mNoteList.deleteNoteItemOrFolder(item);
	}

	@Override
	public void deleteAllNotes() {
		mDb.delete(DBOpenHelper.TABLE_NAME, null, null);

		mNoteList.clear();
	}

	public List<NoteItem> getNotes() {
		updateFromDB();
		return mNoteList.getNotes();
	}

	@Override
	public List<NoteItem> getFolders() {
		updateFromDB();
		return mNoteList.getFolderList();
	}

	@Override
	public List<NoteItem> getNotesFromFolder(int folderID) {
		updateFromDB();
		return mNoteList.getNotesFromFolder(folderID);
	}

	@Override
	public List<NoteItem> getRootFoldersAndNotes() {
		updateFromDB();
		return mNoteList.getRootFoldersAndNotes();
	}

	@Override
	public List<NoteItem> getRootNotes() {
		updateFromDB();
		return mNoteList.getRootNotes();
	}

	@Override
	public List<NoteItem> getColckAlarmItems() {
		updateFromDB();
		return mNoteList.getColckAlarmItems();
	}

	private void updateFromDB() {
		if (mNeedUpdate) {
			initData(mContext);
		}
	}

	public void updateCacheFromDB() {
		mNeedUpdate = true;
	}

	@Override
	public NoteItem getNoteItemFromDB(int id) {
		Cursor cursor = null;
		NoteItem noteItem = null;
		try {
			cursor = mDb.query(DBOpenHelper.TABLE_NAME,
					DBOpenHelper.NOTE_ALL_COLUMS, "_id=?", new String[] { id
							+ "" }, null, null, null);
			while (cursor.moveToNext()) {
				noteItem = buildNoteItem(cursor, mContext);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return noteItem;
	}

	@Override
	public List<NoteItem> getNotesIncludeContent(String content) {
		updateFromDB();
		return mNoteList.getNotesIncludeContent(content);
	}

	public List<NoteItem> getColckAlarmItemsFromDB() {
		Cursor cursor = null;
		List<NoteItem> noteItems = new ArrayList<NoteItem>();
		try {
			cursor = mDb.query(DBOpenHelper.TABLE_NAME,
					DBOpenHelper.NOTE_ALL_COLUMS,
					DBOpenHelper.NOTE_ALARM_ENABLE + "=?",
					new String[] { 1 + "" }, null, null, null);
			while (cursor.moveToNext()) {
				NoteItem noteItem = buildNoteItem(cursor, mContext);
				noteItems.add(noteItem);
			}
		} finally {
			if (cursor != null) {
				cursor.close();
				cursor = null;
			}
		}
		return noteItems;
	}
}
