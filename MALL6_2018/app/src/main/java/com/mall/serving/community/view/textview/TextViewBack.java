package com.mall.serving.community.view.textview;




import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public class TextViewBack extends android.support.v7.widget.AppCompatTextView {
	public TextViewBack(final Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if (context instanceof Activity) {
					Activity act = (Activity) context;

					act.finish();
				}

			}
		});
	}
}
