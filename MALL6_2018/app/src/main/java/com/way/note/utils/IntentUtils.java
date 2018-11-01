package com.way.note.utils;

import java.lang.reflect.Field;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.mall.view.R;
import com.way.note.data.NoteItem;

/**
 * Intent管理工具
 * 
 * @author way
 * 
 */
public class IntentUtils {

	public static final void sendSharedIntent(Context context, NoteItem item) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/*");

		String titleKey = context.getString(R.string.export_title);
		String contentKey = context.getString(R.string.export_content);

		StringBuilder sb = new StringBuilder();
		sb.append(titleKey).append(item.getExprotTitle(context)).append(',');
		sb.append(contentKey).append(item.getExprotContent(context));

		intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
		intent.putExtra(intent.EXTRA_SUBJECT, context.getString(R.string.share));
		context.startActivity(Intent.createChooser(intent,
				context.getString(R.string.share) + ":" + item.getShortTitle()));
	}

	public static void keepDialog(DialogInterface dialog, boolean isClose) {
		try {
			Field field = dialog.getClass().getSuperclass()
					.getDeclaredField("mShowing");
			field.setAccessible(true);
			field.set(dialog, isClose);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
