package com.mall.serving.community.util;

import java.util.HashMap;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import com.mall.view.App;
import com.mall.view.R;

public class SoundUtil {
	private static SoundPool sp;
	private static HashMap<Integer, Integer> spMap;

	public static void InitSound() {
		if (sp == null) {
			sp = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
		}

		spMap = new HashMap<Integer, Integer>();
		spMap.put(1, sp.load(App.getContext(), R.raw.busy, 1));
		spMap.put(2, sp.load(App.getContext(), R.raw.message_reciver, 1));
		spMap.put(3, sp.load(App.getContext(), R.raw.randomcall, 1));
		spMap.put(4, sp.load(App.getContext(), R.raw.refresh_succeed, 1));
		spMap.put(5, sp.load(App.getContext(), R.raw.send_flower, 1));
		spMap.put(6, sp.load(App.getContext(), R.raw.shake_sound_male, 1));

	}

	public static void playSound(int sound) {
		int isRemind = SharedPreferencesUtils
				.getSharedPreferencesInt(SharedPreferencesUtils.isRemind);
		if (isRemind == 0) {
			int cycle = 0;
			AudioManager am = (AudioManager) App.getContext().getSystemService(
					Context.AUDIO_SERVICE);
			if (sound == 3 || sound == 5) {
				am.setStreamVolume(AudioManager.STREAM_MUSIC, 50, 0);
			}
			float audioMaxVolumn = am
					.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
			float volumnCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
			float volumnRatio = volumnCurrent / audioMaxVolumn;

			if (sp == null) {
				InitSound();
			}
			sp.play(spMap.get(sound), volumnRatio, volumnRatio, 1, cycle, 1f);
		}
	}

	

}
