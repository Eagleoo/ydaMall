package com.mall.serving.voip.util;

import java.io.IOException;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import com.mall.view.App;
import com.mall.view.R;


/**
 * 音频管理与播放提示音
 * 
 * @author xiaozhenhua
 * 
 */
public class AudioPlayer implements OnCompletionListener {

	private static AudioPlayer audioPlayer;

	public static AudioPlayer getInstance() {
		if (audioPlayer == null) {
			audioPlayer = new AudioPlayer();
		}
		return audioPlayer;
	}

	private AudioManager mAudioManager;
	private Vibrator mVibrator;
	private MediaPlayer mRingerPlayer;

	public AudioPlayer() {
		mAudioManager = ((AudioManager) App.getContext().getSystemService(Context.AUDIO_SERVICE));
		mVibrator = (Vibrator) App.getContext().getSystemService(Context.VIBRATOR_SERVICE);
	}

	/**
	 * 播放来电音
	 * 
	 * @param RingtoneManagerType
	 * @param vibrator
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:39:04
	 */
	public synchronized void startRinging() {
		if (mAudioManager != null) {
			mAudioManager.setMode(AudioManager.MODE_RINGTONE);
		}
		try {
			if (mVibrator != null) {
				long[] patern = { 0, 1000, 1000 };
				mVibrator.vibrate(patern, 1);
			}
			if (mRingerPlayer == null) {
				mRingerPlayer = MediaPlayer.create(App.getContext(), R.raw.callphonering);
			}
			mRingerPlayer.setOnCompletionListener(this);
			mRingerPlayer.prepare();
			mRingerPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 关闭来电响铃
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:21:49
	 */
	public synchronized void stopRinging() {
		if (android.os.Build.BRAND != null
				&& (android.os.Build.BRAND.toString().contains("Xiaomi") || android.os.Build.BRAND.toString().equals(
						"Xiaomi"))) {
//			SystemMediaConfig.restoreMediaConfig(null);
		}
		stopRingBefore180Player();
		if (mVibrator != null) {
			mVibrator.cancel();
		}
	}

	/**
	 * 播放提示音(挂断/回播/半回播/免费转直播)
	 * 
	 * @param mContext
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:23:22
	 */
	public synchronized void playerAlertAudio(int resourceId) {
		MediaPlayer mp = MediaPlayer.create(App.getContext(), resourceId);
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		mp.setLooping(false);
		mp.start();
	}

	/**
	 * 加入华为两台机型在 在startAudio将Mode重新设置成MODE_NORMAL 解决这两台机器在作为免费被叫时，主叫听不到声音的问题
	 * 
	 * @data:2013-7-10 下午3:27:28
	 */
	public void startAudioSetMode() {
		if (AudioPlayer.getInstance().getPlayoutSpeaker()) {
			AudioPlayer.getInstance().setPlayoutSpeaker(true, 8);
		}
		// String modelString = Build.MODEL.replaceAll(" ", "");
		// if (modelString.equalsIgnoreCase("HUAWEIY310-T10")
		// || modelString.equalsIgnoreCase("HUAWEIY220T")
		// || modelString.equalsIgnoreCase("ZTEU793")) {
		// mAudioManager.setMode(AudioManager.MODE_NORMAL);
		// }
	}

	public int getCurrentAudioMode() {
		return mAudioManager.getMode();
	}

    /**
     * 播放提示音
     * 
     * @param 资源文件Id R.raw.rId
     * @param isLoop  是否循环播放
     */
	public void startRingBefore180Player(final int rId,final boolean isLoop) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				setPlayoutSpeaker(false, 1);
				if (mRingerPlayer == null) {
					mRingerPlayer = new MediaPlayer();
				}
				AssetFileDescriptor afd = App.getContext().getResources()
						.openRawResourceFd(rId);
				if (afd == null) {
					return;
				}
				try {
					if (isSetMode() && mAudioManager != null) {
						mAudioManager.setMode(AudioManager.MODE_IN_CALL);
						Log.d("WebRTCADjava", "3_SET_MODE:AudioManager.MODE_IN_CALL");
					}
					mRingerPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
					mRingerPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
					afd.close();
					mRingerPlayer.prepare();
					mRingerPlayer.setLooping(isLoop);
					mRingerPlayer.start();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalStateException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	/**
	 * 停止去电提示音
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-2-18 下午4:56:12
	 */
	public void stopRingBefore180Player() {
		
		try {
			if (mRingerPlayer != null) {
				mRingerPlayer.stop();
				mRingerPlayer.release();
				mRingerPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 通话时用于设置免提或内放
	 * 
	 * @param loudspeakerOn
	 *            true:外放 false:内放
	 * @author: xiaozhenhua
	 * @data:2013-2-19 下午4:27:47
	 */
	public synchronized void setPlayoutSpeaker(boolean loudspeakerOn, int index) {
		Log.d("WebRTCADjava", "SET_PLAYOUT_SPEAKER:" + loudspeakerOn + "     INDEX:" + index);
		int apiLevel = Integer.parseInt(android.os.Build.VERSION.SDK);
		if ((3 == apiLevel) || (4 == apiLevel)) {
			// 1.5 and 1.6 devices
			if (loudspeakerOn) {
				// route audio to back speaker
				mAudioManager.setMode(AudioManager.MODE_NORMAL);
				Log.d("WebRTCADjava", "3_SET_MODE:AudioManager.MODE_NORMAL");
			} else {
				// route audio to earpiece
				Log.d("WebRTCADjava", "SDK 1.5 and 1.6 devices:route audio to earpiece success");
				mAudioManager.setMode(AudioManager.MODE_IN_CALL);
				Log.d("WebRTCADjava", "4_SET_MODE:AudioManager.MODE_IN_CALL");
			}
		} else {
			// 2.x devices
			if ((android.os.Build.BRAND.equals("Samsung") || android.os.Build.BRAND.equals("samsung"))
					&& ((5 == apiLevel) || (6 == apiLevel) || (7 == apiLevel))) {
				// Samsung 2.0, 2.0.1 and 2.1 devices
				if (loudspeakerOn) {
					// route audio to back speaker
					mAudioManager.setMode(AudioManager.MODE_IN_CALL);
					Log.d("WebRTCADjava", "5_SET_MODE:AudioManager.MODE_IN_CALL");
					mAudioManager.setSpeakerphoneOn(loudspeakerOn);
					Log.d("WebRTCADjava",
							"Samsung and Samsung 2.1 and down devices:route audio to  back speaker success");
				} else {
					// route audio to earpiece
					mAudioManager.setSpeakerphoneOn(loudspeakerOn);
					mAudioManager.setMode(AudioManager.MODE_NORMAL);
					Log.d("WebRTCADjava", "6_SET_MODE:AudioManager.MODE_NORMAL");
					Log.d("WebRTCADjava", "Samsung and Samsung 2.1 and down devices:route audio to  earpiece success");
				}
			} else {
				if (false) {
					mAudioManager.setMode(AudioManager.MODE_IN_CALL);
				} else {
					if (isSetMode()) {
						String modelString = Build.MODEL.replaceAll(" ", "");
						if (loudspeakerOn
								&& (modelString.equalsIgnoreCase("HUAWEIY300-0000") || modelString
										.equalsIgnoreCase("HUAWEIC8813"))) {
							mAudioManager.setMode(AudioManager.MODE_NORMAL);
						}
						Log.d("WebRTCADjava", "_audioManager.setMode(AudioManager.MODE_IN_CALL)");
						mAudioManager.setMode(AudioManager.MODE_IN_CALL);
						Log.d("WebRTCADjava", "7_SET_MODE:AudioManager.MODE_IN_CALL");
					} else {
						mAudioManager.setMode(AudioManager.MODE_NORMAL);
					}
				}
				mAudioManager.setSpeakerphoneOn(loudspeakerOn);
				
			}
		}
		currentPlayerDevices();
	}

	public boolean getPlayoutSpeaker() {
		if (mAudioManager != null) {
			return mAudioManager.isSpeakerphoneOn();
		}
		return false;
	}

	/**
	 * 测式函数
	 * 
	 * @author: xiaozhenhua
	 * @data:2013-4-10 上午11:14:23
	 */
	public void currentPlayerDevices() {
		Log.d("WebRTCADjava", "CURRENT_PALYER_DEVICES:");
		if (mAudioManager.isBluetoothA2dpOn()) {
			Log.d("WebRTCADjava", "BLUETOOTAH");
		} else if (mAudioManager.isSpeakerphoneOn()) {
			Log.d("WebRTCADjava", "外放");
		} else if (mAudioManager.isWiredHeadsetOn()) {
			Log.d("WebRTCADjava", "有线耳机");
		} else if (!mAudioManager.isSpeakerphoneOn()) {
			Log.d("WebRTCADjava", "内放");
		} else {
			Log.d("WebRTCADjava", "其它设备");
		}
		Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:" + mAudioManager.getMode());
		switch (mAudioManager.getMode()) {
		case AudioManager.MODE_INVALID:
			Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:AudioManager.MODE_INVALID");
			break;
		case AudioManager.MODE_CURRENT:
			Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:AudioManager.MODE_CURRENT");
			break;
		case AudioManager.MODE_NORMAL:
			Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:AudioManager.MODE_NORMAL");
			break;
		case AudioManager.MODE_RINGTONE:
			Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:AudioManager.MODE_RINGTONE");
			break;
		case AudioManager.MODE_IN_CALL:
			Log.d("WebRTCADjava", "CURRENT_PLAYER_MODE:AudioManager.MODE_IN_CALL");
			break;
		}
		Log.d("WebRTCADjava", "------------------------------");
	}

	/**
	 * 判断品牌或机型是否需要设置AudioManager的Mode
	 * 
	 * @return
	 * @author: xiaozhenhua
	 * @data:2013-4-10 上午11:56:11
	 */
	public boolean isSetMode() {
		String brandString = android.os.Build.BRAND;
		String modelString = "";
		if (Build.MODEL != null)
			modelString = Build.MODEL.replaceAll(" ", "");
		Log.d("WebRTCADjava", "phone band =" + brandString);
		Log.d("WebRTCADjava", "phone modelString = " + modelString);
		return brandString != null
				&& (brandString.equalsIgnoreCase("yusu")
						|| brandString.equalsIgnoreCase("yusuH701")
						|| brandString.equalsIgnoreCase("yusuA2")
						|| brandString.equalsIgnoreCase("qcom")
						|| brandString.equalsIgnoreCase("motoME525")
						|| (brandString.equalsIgnoreCase("Huawei") && !modelString.equals("HUAWEIY220T")
								&& !modelString.equals("HUAWEIT8600") && !modelString.equals("HUAWEIY310-T10"))
						|| brandString.equalsIgnoreCase("lge")
						|| brandString.equalsIgnoreCase("SEMC")
						|| (brandString.equalsIgnoreCase("ZTE") && !modelString.equalsIgnoreCase("ZTEU880E")
								&& !modelString.equalsIgnoreCase("ZTEV985")
								&& !modelString.equalsIgnoreCase("ZTE-TU880")
								&& !modelString.equalsIgnoreCase("ZTE-TU960s") && !modelString
								.equalsIgnoreCase("ZTEU793")) || modelString.equalsIgnoreCase("LenovoS850e")
						|| modelString.equalsIgnoreCase("LenovoA60") || modelString.equalsIgnoreCase("HTCA510e")
						|| (brandString.equalsIgnoreCase("Coolpad") && modelString.equalsIgnoreCase("7260"))
						|| brandString.equalsIgnoreCase("ChanghongV10") || modelString.equalsIgnoreCase("MI2"));
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		mp.stop();
		mp.release();
	}
}
