package com.mall.serving.community.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingLeftInAnimationAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingRightInAnimationAdapter;
import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.exception.DbException;
import com.mall.serving.community.view.animation.RotateAnimation;
import com.mall.serving.community.view.progress.AnimationWait;
import com.mall.view.App;
import com.mall.view.R;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class AnimeUtil {

	/**
	 * 给listView加一个刷新动画的EmptyView
	 * 
	 * @param context
	 * @param lv
	 * @param is
	 */
	public static void setAnimationEmptyView(Context context, AbsListView lv,
			boolean is) {

		AnimationWait wait = new AnimationWait(context, is);
		android.view.ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		wait.setLayoutParams(params);
		setAnimationEmptyView(context, lv, wait, " ");
	}
	public static void setNoDataEmptyView(String message, int rid,
			Context context, AbsListView lv, boolean isRemove,
			OnClickListener listener) {

//		View inflate = LayoutInflater.from(context).inflate(
//				R.layout.community_custom_empty, null);
//		android.view.ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
//				ViewGroup.LayoutParams.MATCH_PARENT,
//				ViewGroup.LayoutParams.MATCH_PARENT);
//		inflate.setLayoutParams(params);
//		TextView tv_empty = (TextView) inflate.findViewById(R.id.tv_empty);
//		tv_empty.setOnClickListener(listener);
//		tv_empty.setText(message);
//		tv_empty.setCompoundDrawablesWithIntrinsicBounds(0, rid, 0, 0);
		setNoDataEmptyView(context, lv, rid, message, listener);

	}
	
	/**
	 * 
	 * 给listView加一个刷新动画的EmptyView listview外层最好用FrameLayout单独包着
	 * 
	 * @param context
	 * @param lv
	 * @param is
	 */
	public static void setAnimationEmptyView(Context context, AbsListView lv,
			View bar, String str) {
		ViewGroup parentView = (ViewGroup) lv.getParent();

		removeItem(parentView, lv, -10321);
		removeItem(parentView, lv, -10322);

		LinearLayout wait = getProgressView(context, bar, str);

		parentView.addView(wait);
		lv.setTag(-10322, wait);
		lv.setEmptyView(wait);

	}

	/**
	 * 给listView加一个数据为空时EmptyView listview外层最好用FrameLayout单独包着
	 * 
	 * @param context
	 * @param lv
	 * @param rid
	 * @param message
	 * @param listener
	 */
	public static void setNoDataEmptyView(Context context, AbsListView lv,
			Integer rid, String message, OnClickListener listener) {
		ViewGroup parentView = (ViewGroup) lv.getParent();

		removeItem(parentView, lv, -10321);
		removeItem(parentView, lv, -10322);

		LinearLayout inflate = getEmptyView(context, rid, message, listener);
		parentView.addView(inflate);
		lv.setEmptyView(inflate);
		lv.setTag(-10321, inflate);

	}

	/**
	 * 删除上一个EmptyView
	 * 
	 * @param parentView
	 * @param lv
	 * @param index
	 */
	private static void removeItem(ViewGroup parentView, AbsListView lv, int index) {
		Object tag = lv.getTag(index);
		if (tag != null && tag instanceof View) {
			View s = (View) tag;
			parentView.removeView(s);
			lv.setTag(index, null);
		}

	}

	/**
	 * 得到一个刷新动画的EmptyView
	 * 
	 * @param context
	 * @param bar
	 * @param str
	 * @return
	 */
	private static LinearLayout getProgressView(Context context, View bar,
			String str) {

		LinearLayout ll1 = new LinearLayout(context);
		ll1.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ll1.setGravity(Gravity.CENTER);
		ll1.setLayoutParams(params);
		if (bar == null) {
			bar = new ProgressBar(context);
		}

		TextView textView = new TextView(context);

		textView.setText(str);
		textView.setGravity(Gravity.CENTER);

		ll1.addView(bar);
		ll1.addView(textView);
		return ll1;

	}

	/**
	 * 得到一个数据为空时的EmptyView
	 * 
	 * @param context
	 * @param rid
	 * @param str
	 * @param click
	 * @return
	 */
	private static LinearLayout getEmptyView(Context context, Integer rid, String str,
			OnClickListener click) {

		LinearLayout ll2 = new LinearLayout(context);
		ll2.setOrientation(LinearLayout.VERTICAL);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		ll2.setGravity(Gravity.CENTER);
		ll2.setLayoutParams(params);
		if (click != null) {
			ll2.setOnClickListener(click);
		}

		if (rid != null) {

			ImageView imageView = new ImageView(context);
			imageView.setImageResource(rid);
			ll2.addView(imageView);
		}
		TextView textView = new TextView(context);
		textView.setText(str);
		textView.setGravity(Gravity.CENTER);

		ll2.addView(textView);

		return ll2;

	}

	public static void startAnimation(Context context, View v, int animationId,
			final OnAnimEnd ae) {
		startAnimation(context, v, animationId, null, ae);
	}

	public static void startAnimation(Context context, View v, Animation anim,
			final OnAnimEnd ae) {
		startAnimation(context, v, 0, anim, ae);
	}

	public static void startAnimation(Context context, View v, int animationId,
			Animation anim, final OnAnimEnd ae) {
		if(null == v) return ;
		if (anim == null) {
			anim = AnimationUtils.loadAnimation(context, animationId);
		}
		v.startAnimation(anim);

		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation arg0) {
				if (ae != null) {
					ae.start();
				}

			}

			@Override
			public void onAnimationRepeat(Animation arg0) {
				if (ae != null) {
					ae.repeat();
				}

			}

			@Override
			public void onAnimationEnd(Animation arg0) {
				if (ae != null) {
					ae.end();
				}

			}
		});

	}

	public static void setAnimationAdapter(BaseAdapter adapter, ListView lv,
			AnimationAdapterType type) {
		AnimationAdapter animAdapter = null;
		switch (type) {
		case RightIn:
			animAdapter = new SwingRightInAnimationAdapter(adapter);
			break;
		case LeftIn:
			animAdapter = new SwingLeftInAnimationAdapter(adapter);
			break;
		case BottomIn:
			animAdapter = new SwingBottomInAnimationAdapter(adapter);
			break;
		case AlphaIn:
			animAdapter = new AlphaInAnimationAdapter(adapter);
			break;
		case ScaleIn:
			animAdapter = new ScaleInAnimationAdapter(adapter);
			break;

		default:
			animAdapter = new AlphaInAnimationAdapter(adapter);
			break;
		}

		animAdapter.setAbsListView(lv);

		lv.setAdapter(animAdapter);
	}

	public interface OnAnimEnd {
		void start();

		void end();

		void repeat();
	}

	public enum AnimationAdapterType {
		RightIn, LeftIn, BottomIn, AlphaIn, ScaleIn

	}

	public static ImageLoader getImageLoad() {

		ImageLoader imageLoader = ImageLoader.getInstance();
		if (!imageLoader.isInited()) {

			// ImageLoaderConfiguration config = new
			// ImageLoaderConfiguration.Builder(
			// App.getContext())
			// .threadPriority(Thread.NORM_PRIORITY - 2)
			// .denyCacheImageMultipleSizesInMemory()
			// .memoryCache(new WeakMemoryCache())
			// .memoryCacheExtraOptions(480, 800)
			// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
			// null)
			// .threadPoolSize(3)
			// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
			// .memoryCacheSize(2 * 1024 * 1024)
			// .memoryCacheSizePercentage(13)
			// // default
			// .discCacheSize(50 * 1024 * 1024)
			// // 缓冲大小
			// .discCacheFileCount(100)
			// // 缓冲文件数目
			// .discCacheFileNameGenerator(new Md5FileNameGenerator())
			// .tasksProcessingOrder(QueueProcessingType.LIFO).build();

			ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
					App.getContext())
					.memoryCacheExtraOptions(480, 800)
					// default = device screen dimensions
					.discCacheExtraOptions(480, 800, CompressFormat.PNG, 75,
							null)

					.threadPoolSize(3)
					// default
					.threadPriority(Thread.NORM_PRIORITY - 1)
					// default
					.tasksProcessingOrder(QueueProcessingType.FIFO)
					// default
					.denyCacheImageMultipleSizesInMemory()
					.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
					.memoryCacheSize(2 * 1024 * 1024)
					.memoryCacheSizePercentage(13) // default

					.discCacheSize(50 * 1024 * 1024) // 缓冲大小
					.discCacheFileCount(50) // 缓冲文件数目
					.discCacheFileNameGenerator(new HashCodeFileNameGenerator()) //

					.imageDownloader(new BaseImageDownloader(App.getContext())) //

					.imageDecoder(new BaseImageDecoder(true)) // default
					.defaultDisplayImageOptions(
							DisplayImageOptions.createSimple()) // default
					.writeDebugLogs().build();
			imageLoader.init(config);

		}

		return imageLoader;

	}

	public static DisplayImageOptions getImageOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.community_image_defult)
				.showImageOnFail(R.drawable.community_image_defult)
				.showImageForEmptyUri(R.drawable.community_image_defult)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		return options;

	}

	public static DisplayImageOptions getImageCallOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.community_image_defult)
				.showImageOnFail(R.drawable.community_image_head)
				.showImageForEmptyUri(R.drawable.community_image_head)
				.cacheInMemory(true).cacheOnDisc(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		return options;

	}

	public static DisplayImageOptions getImageRectOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.community_image_defult_rect)
				.showImageOnFail(R.drawable.community_image_head_rect)
				.showImageForEmptyUri(R.drawable.community_image_head_rect)
				.cacheInMemory(true).cacheOnDisc(true)
				.displayer(new RoundedBitmapDisplayer(8))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		return options;

	}

	public static DisplayImageOptions getImageRoundOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.community_image_defult_ciycle)
				.showImageOnFail(R.drawable.community_image_head_ciycle)
				.showImageForEmptyUri(R.drawable.community_image_head_ciycle)
				.cacheInMemory(true)

				.cacheOnDisc(true).displayer(new RoundedBitmapDisplayer(400))
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
		return options;

	}

	public static DisplayImageOptions getImageViewPagerOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.community_image_defult)
				// 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.community_image_defult_fail)
				// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.community_image_defult_fail)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true)
				// 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true).cacheOnDisc(true)

				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;

	}

	public static DisplayImageOptions getImageSimpleOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()

		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true)
				// 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true).cacheOnDisc(true)

				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;

	}

	public static DisplayImageOptions getImageViewPagerSimpleOption() {
		DisplayImageOptions options = new DisplayImageOptions.Builder()

		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.considerExifParams(true)
				// 设置图片加载/解码过程中错误时候显示的图片
				// .cacheInMemory(true).cacheOnDisc(true)

				.bitmapConfig(Bitmap.Config.RGB_565).build();
		return options;

	}

	public static void reDoLogin() {
		if (!Util.isNetworkConnected()) {
			return;
		}
		// Util.asynTask(new IAsynTask() {
		//
		// @Override
		// public void updateUI(Serializable runData) {
		// // TODO Auto-generated method stub
		//
		// }
		//
		// @Override
		// public Serializable run() {
		// // Web.reDoLogin();
		// return "";
		// }
		// });
	}

	public static void setImageViewGray(ImageView iv, float f) {
		try {
			ColorMatrix matrix = new ColorMatrix();
			matrix.setSaturation(f);
			ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
			iv.setColorFilter(filter);
		} catch (Exception e) {
			// TODO: handle exception
		}

	}

	public static void startImageAnimation(ImageView iv) {
		iv.setVisibility(View.VISIBLE);
		AnimationDrawable anim = (AnimationDrawable) iv.getDrawable();
		anim.start();
	}

	public static void cancelImageAnimation(ImageView iv){
		iv.setVisibility(View.GONE);
		AnimationDrawable anim = (AnimationDrawable) iv.getDrawable();
		if(null != anim )
			anim.stop();
	}
	public static boolean isRunBackground() {

		ActivityManager activityManager = (ActivityManager) App.getContext()
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		for (RunningAppProcessInfo appProcess : appProcesses) {
			if (appProcess.processName
					.equals(App.getContext().getPackageName())) {
				if (appProcess.importance == RunningAppProcessInfo.IMPORTANCE_BACKGROUND) {
					Log.i("Tag", String.format("Background App:",
							appProcess.processName));
					return true;
				} else {
					Log.i("Tag", String.format("Foreground App:",
							appProcess.processName));
					return false;
				}
			}
		}
		return false;
	}

	public static void applyRotation(View mContainer, float start, float end) {
		// Find the center of the container
		final float centerX = mContainer.getWidth() / 2.0f;
		final float centerY = mContainer.getHeight() / 2.0f;

		// Create a new 3D rotation with the supplied parameter
		// The animation listener is used to trigger the next animation
		final RotateAnimation rotation = new RotateAnimation(start, end,
				centerX, centerY, 310.0f, true);
		rotation.setDuration(1000);
		rotation.setFillAfter(true);
		rotation.setInterpolator(new AccelerateInterpolator());
		rotation.setAnimationListener(new DisplayNextView(mContainer));

		mContainer.startAnimation(rotation);
	}

	private static class DisplayNextView implements Animation.AnimationListener {
		private View view;

		private DisplayNextView(View view) {
			this.view = view;
		}

		public void onAnimationStart(Animation animation) {
		}

		public void onAnimationEnd(Animation animation) {
			view.post(new SwapViews(view));
		}

		public void onAnimationRepeat(Animation animation) {
		}
	}

	/**
	 * This class is responsible for swapping the views and start the second
	 * half of the animation.
	 */
	private static class SwapViews implements Runnable {
		private View view;

		public SwapViews(View view) {
			this.view = view;
		}

		public void run() {
			final float centerX = view.getWidth() / 2.0f;
			final float centerY = view.getHeight() / 2.0f;
			RotateAnimation rotation;

			// if (mPosition > -1) {
			// rl_layout01.setVisibility(View.GONE);
			// rl_layout02.setVisibility(View.VISIBLE);

			rotation = new RotateAnimation(90, 180, centerX, centerY, 310.0f,
					false);
			// } else {
			// // rl_layout02.setVisibility(View.GONE);
			// // rl_layout01.setVisibility(View.VISIBLE);
			// rotation = new RotateAnimation(90, 0, centerX, centerY, 310.0f,
			// false);
			// }

			rotation.setDuration(500);
			rotation.setFillAfter(true);
			rotation.setInterpolator(new DecelerateInterpolator());

			view.startAnimation(rotation);
		}
	}

	public static android.view.animation.RotateAnimation getRotateAnimation(
			int rotate) {
		// RotateAnimation animation = new RotateAnimation(0, rotate, 0.5f,
		// 0.5f,
		// 310.0f, false);
		android.view.animation.RotateAnimation animation = new android.view.animation.RotateAnimation(
				0, rotate);
		animation.setFillAfter(true);
		animation.setInterpolator(new LinearInterpolator());
		return animation;
	}

	synchronized public static void dbSaveAll(final List list) {

		if (Thread.currentThread().getName().equals("main")) {

			new Thread() {
				public void run() {
					saveAll(list);
				};
			}.start();

		}else {
			saveAll(list);
		}

	}

	synchronized private static void saveAll(List list) {
		try {
			DbUtils.create(App.getContext()).saveBindingIdAll(list);
		} catch (DbException e) {
			e.printStackTrace();
		}

	}
	synchronized public static void dbSave(final Object obj) {
		
		if (Thread.currentThread().getName().equals("main")) {
			
			new Thread() {
				public void run() {
					save(obj);
				};
			}.start();
			
		}else {
			save(obj);
		}
		
	}
	
	synchronized private static void save(Object obj) {
		try {
			DbUtils.create(App.getContext()).save(obj);
		} catch (DbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
