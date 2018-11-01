package com.mall.officeonline;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.mall.officeonline.CircularSeekBar.OnSeekChangeListener;

public class CirclActivity extends Activity {
	CircularSeekBar circularSeekbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		circularSeekbar = new CircularSeekBar(this);
		circularSeekbar.setMaxProgress(100);
		circularSeekbar.setProgress(100);
		setContentView(circularSeekbar);
		circularSeekbar.invalidate();
		circularSeekbar.setSeekBarChangeListener(new OnSeekChangeListener() {
			@Override
			public void onProgressChange(CircularSeekBar view, int newProgress) {
				Log.d("Welcome","Progress:" + view.getProgress() + "/"+ view.getMaxProgress());
			}
		});

	}
}
