package com.way.note.search;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.way.note.data.DBOpenHelper;

/**
 * 便签内容提供者
 * 
 * @author way
 */
public class NoteProvider extends ContentProvider {
	private static final String TAG = "NoteProvider";
	DBOpenHelper helper;
	SQLiteDatabase db;
	private static final String DB_TABLE = "items";
	private static final UriMatcher sURIMatcher = new UriMatcher(
			UriMatcher.NO_MATCH);
	private static final int NOTES_ID = 1;
	private static final int NOTES_SUGGESTED_SEARCH = 2;

	static {
		sURIMatcher.addURI("com.way.note.NoteProvider.note", "*/items/#",
				NOTES_ID);
		sURIMatcher.addURI("com.way.note.NoteProvider.note", "*/"
				+ SearchManager.SUGGEST_URI_PATH_QUERY + "/*",
				NOTES_SUGGESTED_SEARCH);
		sURIMatcher.addURI("com.way.note.NoteProvider.note", "*/"
				+ SearchManager.SUGGEST_URI_PATH_QUERY, NOTES_SUGGESTED_SEARCH);
	}

	private static final String[] projectionSearch = {
			DBOpenHelper.ID,
			DBOpenHelper.NOTE_CONTENT + " AS "
					+ SearchManager.SUGGEST_COLUMN_TEXT_1,
			DBOpenHelper.ID + " AS " + SearchManager.SUGGEST_COLUMN_QUERY };

	private static final String[] selectionArgs = {};

	private static final String[] columnsTarget = { DBOpenHelper.ID,

	SearchManager.SUGGEST_COLUMN_TEXT_1, SearchManager.SUGGEST_COLUMN_QUERY };

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		// TODO Auto-generated method stub

		return 0;
	}

	@Override
	public String getType(Uri uri) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean onCreate() {
		// TODO Auto-generated method stub
		helper = new DBOpenHelper(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Log.v(TAG, "query  uri-->" + uri);

		String mSearchString = uri.getPath().endsWith("/") ? "" : uri
				.getLastPathSegment();
		mSearchString = mSearchString.trim().replaceAll("  ", " ");
		Log.v(TAG, "query  mSearchString -->" + mSearchString);

		switch (sURIMatcher.match(uri)) {
		case NOTES_SUGGESTED_SEARCH:
			return search(uri, mSearchString);
		default:
			throw new IllegalArgumentException("Unknown Uri: " + uri);
		}
	}

	private Cursor search(Uri uri, String mSearchString) {
		String myWhere = null;
		if (mSearchString.length() > 0) {
			myWhere = " (" + DBOpenHelper.NOTE_IS_FOLDER + " = '" + 0
					+ "' and " + DBOpenHelper.NOTE_CONTENT + " LIKE '%"
					+ mSearchString + "%')";
		} else {
			myWhere = DBOpenHelper.NOTE_IS_FOLDER + " = 0";
		}

		String sortOrder = DBOpenHelper.NOTE_UPDATE_DATE + " DESC";

		SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
		qBuilder.setTables(DB_TABLE);
		Cursor c = qBuilder.query(helper.getReadableDatabase(),
				projectionSearch, myWhere, selectionArgs, null, null,
				sortOrder, getLimit(uri));
		c.setNotificationUri(getContext().getContentResolver(), uri);
		return sort(c, mSearchString);
	}

	private String getLimit(Uri uri) {
		Log.v(TAG, "getLimit  uri" + uri);
		String limitParam = getQueryParameter(uri, "limit");
		if (limitParam == null) {
			return null;
		}
		// make sure that the limit is a non-negative integer
		try {
			int l = Integer.parseInt(limitParam);
			if (l < 0) {
				return null;
			} else {
			}
			return String.valueOf(l);
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	static String getQueryParameter(Uri uri, String parameter) {
		Log.v(TAG, "getQueryParameter  uri" + uri);
		String query = uri.getEncodedQuery();
		if (query == null) {
			return null;
		}

		int queryLength = query.length();
		int parameterLength = parameter.length();

		String value;
		int index = 0;
		while (true) {
			index = query.indexOf(parameter, index);
			if (index == -1) {
				return null;
			}

			index += parameterLength;

			if (queryLength == index) {
				return null;
			}

			if (query.charAt(index) == '=') {
				index++;
				break;
			}
		}

		int ampIndex = query.indexOf('&', index);
		if (ampIndex == -1) {
			value = query.substring(index);
		} else {
			value = query.substring(index, ampIndex);
		}

		return Uri.decode(value);
	}

	private Cursor sort(Cursor c, String searchString) {
		try {
			final int columnCount = c.getColumnCount();
			for (int i = 0; i < columnCount; i++) {
				Log.d(TAG, c.getColumnName(i));
			}

			final int id_index = c.getColumnIndexOrThrow(DBOpenHelper.ID);
			final int descrip_index = c
					.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_TEXT_1);
			final int query_index = c
					.getColumnIndexOrThrow(SearchManager.SUGGEST_COLUMN_QUERY);
			c.moveToFirst();
			CustomSorter sorter = new CustomSorter(searchString);
			while (!c.isAfterLast()) {
				sorter.add(c.getInt(id_index), c.getString(descrip_index),
						c.getInt(query_index));
				c.moveToNext();
			}
			return sorter.dumpToCorsor(columnsTarget);
		} catch (Exception e) {
			Log.e(TAG, "error sort()", e);
			return null;
		} finally {
			if (c != null)
				c.close();
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
