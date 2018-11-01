package com.way.note;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;

import com.mall.view.App;
import com.way.note.data.NoteDataManager;
import com.way.note.utils.IntentUtils;

/**
 * 基础Activity，完成子类Activity共同需要调用的方法，减少代码冗余
 * 
 * @author way
 * 
 */
public class BaseActivity extends Activity {
	/**
	 * 获取便签管理对象
	 * 
	 * @param context
	 * @return
	 */
	public NoteDataManager getDataManager(Context context) {
		return ((App) getApplication()).getNoteDataManager(context);
	}

	protected DialogInterface.OnClickListener cancelDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			IntentUtils.keepDialog(dialog, true);
			dialog.dismiss();
		}
	};
}
