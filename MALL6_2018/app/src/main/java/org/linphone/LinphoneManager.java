package org.linphone;

import static android.media.AudioManager.MODE_RINGTONE;
import static android.media.AudioManager.STREAM_RING;
import static android.media.AudioManager.VIBRATE_TYPE_RINGER;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mall.view.App;



/**
 * Manager of the low level LibLinphone stuff.<br />
 * Including:
 * <ul>
 * <li>Starting C liblinphone</li>
 * <li>Reacting to C liblinphone state changes</li>
 * <li>Calling Linphone android service listener methods</li>
 * <li>Interacting from Android GUI/service with low level SIP stuff/</li>
 * </ul>
 * Add Service Listener to react to Linphone state changes.
 * 
 * @author Guillaume Beraudo
 */
public final class LinphoneManager {

	private static LinphoneManager instance;
	private static AudioManager mAudioManager;
	private PowerManager mPowerManager;
	private SharedPreferences mPref;
	private Resources mR;
	private WakeLock mIncallWakeLock;
	private static final String TAG = "LinphoneManager";

	// private ListenerDispatcher listenerDispatcher = new ListenerDispatcher();
	private static MediaPlayer mRingerPlayer;
	private static Vibrator mVibrator;

	private LinphoneManager(final Context c) {
		mAudioManager = ((AudioManager) c
				.getSystemService(Context.AUDIO_SERVICE));
		mVibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
		mPowerManager = (PowerManager) c
				.getSystemService(Context.POWER_SERVICE);
	}

	public static Boolean isProximitySensorNearby(final SensorEvent event) {
		float threshold = 4.001f; // <= 4 cm is near
		final float distanceInCm = event.values[0];
		
		final float maxDistance = event.sensor.getMaximumRange();
		Log.i(TAG, "Proximity sensor report [" + distanceInCm + "] "
				+ " for max range [" + maxDistance + "]");
		Map<String, String> map = new HashMap<String, String>();
		map.put("distanceInCm", "Proximity sensor report [" + distanceInCm
				+ "]");
		map.put("maxDistance", "max range  [" + maxDistance + "]");

		boolean isNear = false;
		if (maxDistance >= 255.0f) { // 兼容联想A668t
			if (distanceInCm <= 0.0f) {
				isNear = true;
			}
		} else {
			if (maxDistance <= threshold) {
				// Case binary 0/1 and short sensors
				threshold = maxDistance;
			}
			if (distanceInCm < threshold) {
				isNear = true;
			}
		}
		return isNear;
	}

	private static boolean sLastProximitySensorValueNearby;
	private static Set<Activity> sProximityDependentActivities = new HashSet<Activity>();
	private static SensorEventListener sProximitySensorListener = new SensorEventListener() {
		@Override
		public void onSensorChanged(SensorEvent event) {
			Log.i(TAG,
					"SensorEvent.sensor.type = " + event.sensor.getType());
			if (Sensor.TYPE_PROXIMITY == event.sensor.getType()) {
				if (event.timestamp == 0)
					return; // just ignoring for nexus 1
				setsLastProximitySensorValueNearby(isProximitySensorNearby(event));
				proximityNearbyChanged();
			}
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	public static void setsLastProximitySensorValueNearby(
			boolean sLastProximitySensorValueNearby) {
		LinphoneManager.sLastProximitySensorValueNearby = sLastProximitySensorValueNearby;
		Log.i(TAG, "sLastProximitySensorValueNearby = "
				+ LinphoneManager.sLastProximitySensorValueNearby);
	}

	private static void simulateProximitySensorNearby(Activity activity,
			boolean nearby) {
		final Window window = activity.getWindow();
		WindowManager.LayoutParams lAttrs = activity.getWindow()
				.getAttributes();
		View view = ((ViewGroup) window.getDecorView().findViewById(
				android.R.id.content)).getChildAt(0);
		if (nearby) {
			lAttrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
			view.setVisibility(View.INVISIBLE);
		} else {
			lAttrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
			view.setVisibility(View.VISIBLE);
		}
		window.setAttributes(lAttrs);
	}

	private static void proximityNearbyChanged() {
		boolean nearby = sLastProximitySensorValueNearby;
		for (Activity activity : sProximityDependentActivities) {
			simulateProximitySensorNearby(activity, nearby);
		}
	}

	public static synchronized void startProximitySensorForActivity(
			Activity activity) {
		if (sProximityDependentActivities.contains(activity)) {
			Log.i(
					TAG,
					"proximity sensor already active for "
							+ activity.getLocalClassName());
			return;
		}

		if (sProximityDependentActivities.isEmpty()) {
			SensorManager sm = (SensorManager) activity
					.getSystemService(Context.SENSOR_SERVICE);
			Sensor s = sm.getDefaultSensor(Sensor.TYPE_PROXIMITY);
			if (s != null) {
				sm.registerListener(sProximitySensorListener, s,
						SensorManager.SENSOR_DELAY_UI);
				Log.i(TAG, "Proximity sensor detected, registering");
			}
		} else if (sLastProximitySensorValueNearby) {
			simulateProximitySensorNearby(activity, true);
		}
		// simulateProximitySensorNearby(activity, true);

		sProximityDependentActivities.add(activity);
	}

	public static synchronized void stopProximitySensorForActivity(
			Activity activity) {
		sProximityDependentActivities.remove(activity);
		simulateProximitySensorNearby(activity, false);
		if (sProximityDependentActivities.isEmpty()) {
			SensorManager sm = (SensorManager) activity
					.getSystemService(Context.SENSOR_SERVICE);
			sm.unregisterListener(sProximitySensorListener);
			sLastProximitySensorValueNearby = false;
		}
	}

	private static boolean isRinging;

	public synchronized static void startRinging() {
		if (mAudioManager == null) {
			mAudioManager = ((AudioManager) App.getContext()
					.getApplicationContext()
					.getSystemService(Context.AUDIO_SERVICE));
		}
		if (mVibrator == null) {
			mVibrator = (Vibrator) App.getContext()
					.getApplicationContext()
					.getSystemService(Context.VIBRATOR_SERVICE);
		}
		// if (Hacks.needGalaxySAudioHack()) {
		mAudioManager.setMode(MODE_RINGTONE);
		// }

		try {// TODO:mAudioManager.shouldVibrate(VIBRATE_TYPE_RINGER) return
				// false even if vibrate has been set in system when incomming
				// ringing
				// if (mAudioManager.shouldVibrate(VIBRATE_TYPE_RINGER) &&
				// mVibrator !=null) {
			if (mVibrator != null) {
				long[] patern = { 0, 1000, 1000 };
				mVibrator.vibrate(patern, 1);
				Log
						.d(TAG,
								"###################start vibrate################################################");
			} else {
				Log
						.d(TAG,
								"###################NOT start vibrate################################################");
				Log.d(
						TAG,
						"mAudioManager.shouldVibrate(VIBRATE_TYPE_RINGER)="
								+ mAudioManager
										.shouldVibrate(VIBRATE_TYPE_RINGER));
				Log.d(TAG, "mVibrator=" + mVibrator);
			}
			Uri alert = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			if (mRingerPlayer == null) {
				mRingerPlayer = new MediaPlayer();
				mRingerPlayer.setAudioStreamType(STREAM_RING);
				mRingerPlayer.setDataSource(App.getContext()
						.getApplicationContext(), alert);
				// listenerDispatcher.onRingerPlayerCreated(mRingerPlayer);
				mRingerPlayer.prepare();
				mRingerPlayer.setLooping(true);
				mRingerPlayer.start();
			} else {
				Log.d(TAG, "already ringing");
			}
		} catch (Exception e) {
			Log.d(TAG, "cannot handle incoming call");
		}
		isRinging = true;
	}

	public synchronized static void stopRinging() {
		if (mRingerPlayer != null) {
			mRingerPlayer.stop();
			mRingerPlayer.release();
			mRingerPlayer = null;
		}
		if (mVibrator != null) {
			mVibrator.cancel();
		}

		isRinging = false;
		// You may need to call galaxys audio hack after this method
		// routeAudioToReceiver();
	}

}
