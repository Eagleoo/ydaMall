package com.mall.view.service;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.db.table.TableUtils;
import com.mall.model.User;


public class UserProvider extends ContentProvider {
	// MIME类型
	static final String PERSIONS_TYPE = "vnd.android.cursor.dir/user";
	static final String PERSION_ITEM_TYPE = "vnd.android.cursor.item/user";
	// 返回码
	static final int CODES = 2;
	static final int CODE = 1;
	// 授权
	static final String AUTHORITY = "com.mall.view.userdata.ContentProvider.userprovider"; // 授权
	static final UriMatcher uriMatcher; // Uri匹配

	private DbUtils dbUtils;

	static { // 注册匹配的Uri以及返回码
		uriMatcher = new UriMatcher(UriMatcher.NO_MATCH); // 不匹配任何路径返回-1
		uriMatcher.addURI(AUTHORITY, "user", CODES); // 匹配content://com.dongzi/persion
														// 返回2
		uriMatcher.addURI(AUTHORITY, "user/#", CODE); // 匹配content://com.dongzi/persion/1234
															// 返回1
	}

	@Override
	public boolean onCreate() {
		try {
			dbUtils = DbUtils.create(this.getContext(),"YDLoginUsers");
		} catch (Exception eee) {
			eee.printStackTrace();
		}
		return false;
	}
 
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		String tableName = TableUtils.getTableName(User.class);
		SQLiteDatabase db = dbUtils.getDatabase();
		try {
			Cursor cursor = db.query(tableName, new String[]{"userId","userNo","md5Pwd","sessionId","userFace","pwd"},
					"weixin=?", new String[]{"0"}, null, null, sortOrder);
			if(cursor.moveToNext()){
				ContentValues cv = new ContentValues();
				cv.put("weixin", "1");
				db.update(tableName, cv, null, null);
				cursor.move(0);
			}
			return cursor;
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public String getType(Uri uri) {
		// 该方法用于返回当前Url所代表数据的MIME类型。
		switch (uriMatcher.match(uri)) {
		case CODES:
			return PERSIONS_TYPE; // 这里CODES代表集合，故返回的是集合类型的MIME
		case CODE:
			return PERSION_ITEM_TYPE;
		default:
			throw new IllegalArgumentException("throw Uri:" + uri.toString());
		}
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
