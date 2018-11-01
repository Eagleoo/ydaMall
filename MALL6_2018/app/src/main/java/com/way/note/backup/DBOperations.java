package com.way.note.backup;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.way.note.data.DBOpenHelper;
import com.way.note.data.NoteDataManagerImpl;

/**
 * 封装数据库增删改查操作,用的内容提供者
 * 
 * @author way
 */
public class DBOperations extends ContentProvider {

	private static final String TAG = "DBOperations";

	DBOpenHelper helper;
	SQLiteDatabase db;

	private static final int EVENTS = 1;
	private static final int EVENTS_ID = 2;

	private static final UriMatcher sURLMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);

	static {
		sURLMatcher.addURI("com.way.note", "items", EVENTS);
		sURLMatcher.addURI("com.way.note", "items/#", EVENTS_ID);
	}

	@Override
	public boolean onCreate() {
		helper = DBOpenHelper.getInstance(getContext());
		db = helper.getWritableDatabase();
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

		// Generate the body of the query
		Log.d(TAG, "query() uri = " + uri);
		int match = sURLMatcher.match(uri);
		switch (match) {
		case EVENTS:
			qb.setTables("items");
			break;
		case EVENTS_ID:
			qb.setTables("items");
			qb.appendWhere("_id=");
			qb.appendWhere(uri.getPathSegments().get(1));
			break;
		default:
			throw new IllegalArgumentException("Unknown URL " + uri);
		}

		return qb.query(db, projection, selection, selectionArgs, null, null,
				sortOrder);
	}

	@Override
	public String getType(Uri uri) {
		int match = sURLMatcher.match(uri);
		switch (match) {
		case EVENTS:
			return "vnd.android.cursor.dir/items";
		case EVENTS_ID:
			return "vnd.android.cursor.item/items";
		default:
			throw new IllegalArgumentException("Unknown URL");
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		Uri newUrl = null;
		SQLiteDatabase db = helper.getWritableDatabase();
		switch (sURLMatcher.match(uri)) {
		case EVENTS:
			long rowId = db.insert("items", DBOpenHelper.NOTE_CONTENT, values);
			if (rowId < 0) {
				throw new SQLException("Failed to insert row");
			}

			newUrl = ContentUris.withAppendedId(
					Uri.parse("content://com.way.note/items"), rowId);

			NoteDataManagerImpl noteDataManger = (NoteDataManagerImpl) NoteDataManagerImpl
					.getNoteDataManger(getContext());
			noteDataManger.updateCacheFromDB();

			break;
		}
		return newUrl;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = helper.getWritableDatabase();
		int count;
		long rowId = 0;
		switch (sURLMatcher.match(uri)) {
		case EVENTS:
			count = db.delete("items", selection, selectionArgs);
			break;
		case EVENTS_ID:
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			if (TextUtils.isEmpty(selection)) {
				selection = "_id=" + segment;
			} else {
				selection = "_id=" + segment + " AND (" + selection + ")";
			}
			count = db.delete("items", selection, selectionArgs);
			break;
		default:
			throw new IllegalArgumentException("Cannot delete from URL: " + uri);
		}

		return count;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		int count;
		long rowId = 0;
		int match = sURLMatcher.match(uri);
		SQLiteDatabase db = helper.getWritableDatabase();
		switch (match) {
		case EVENTS_ID: {
			String segment = uri.getPathSegments().get(1);
			rowId = Long.parseLong(segment);
			count = db.update("items", values, "_id=" + rowId, null);
			break;
		}
		default: {
			throw new UnsupportedOperationException("Cannot update URL: " + uri);
		}
		}
		return count;
	}
}