package com.mall.serving.redpocket.util;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.mall.net.Web;
import com.mall.serving.community.view.dialog.CustomListDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import static com.mob.tools.utils.ResHelper.getCachePath;

public class ShareUtil {
	
	public static  String[] typeStr = { "元现金", "商币", "元话费", "消费券" };
	

	public static void showShareDialog(final Context context,
			final String text, final String message) {

		new CustomListDialog(context, "发红包", new String[] { "发红包到微信好友",
				"发红包到微信朋友圈","发红包到QQ" }, new CustomListDialog.OnItemClick() {

			@Override
			public void itemClick(AdapterView<?> arg0, View arg1, int p,
					long arg3) {
				switch (p) {
				case 0:
					showRedPocketShare(context, false, Wechat.NAME, false,
							text, message);
					break;
				case 1:
					showRedPocketShare(context, false, WechatMoments.NAME,
							false, text, message);
					break;
				case 2:
					showRedPocketShare(context, false, QQ.NAME,
							false, text, message);
					break;

				}
			}
		}).show();

	}

	public static void showRedPocketShare(Context context, boolean silent,
			String platform, boolean captureView, String text, String message) {

		final OnekeyShare oks = new OnekeyShare();
		final String url =  "http://"+Web.webServer+"/phone/RedPackageInfo.aspx?orderid="
				+ message;
		System.out.println(url);
		final String title = "〔远大云商〕发红包啰";
		String str = "我正通过〔远大云商〕给你发红包，赶快去拆吧！留言：" + text;
		oks.setTitle(str);
		oks.setTitleUrl(url);
		oks.setUrl(url);
		oks.setAddress("10086");
		if (platform.equals(WechatMoments.NAME)) {
			oks.setTitle(str);
		} else {
			oks.setTitle(title);
		}

		oks.setSite("远大云商");
		oks.setText(str);
		oks.setVenueName("远大云商");
		oks.setVenueDescription("远大云商");
		oks.setSilent(silent);
//		oks.setImageUrl(imageUrl);
		

		// Bitmap logo1 = BitmapFactory.decodeResource (context.getResources (
		// ), R. drawable.logo_wechatmoments ) ;
		// // 定义图标的标签
		// String label = "微信朋友圈" ;
		// // 图标点击后会通过Toast提示消息
		// View.OnClickListener listener = new View.OnClickListener ( ) {
		// public void onClick ( View v ) {
		//
		// oks.setPlatform(WechatMoments.NAME);
		// }
		// } ;
		// oks. setCustomerLogo (logo1, label, listener ) ;
		try {
			String cachePath = getCachePath(
					context, null);
			String imagePath = cachePath + "/redpocket_share.png";
			File file = new File(imagePath);
			if (!file.exists()) {
				FileOutputStream fos = new FileOutputStream(file);

				InputStream fis = context.getResources().getAssets()
						.open("redpocket_share.png");

				int i = -1;
				while ((i = fis.read()) != -1) {
					fos.write(i);
				}
				fis.close();
				fos.close();

			}
			oks.setImagePath(imagePath);
		} catch (Throwable t) {

		}

		oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
			@Override
			public void onShare(Platform platform, ShareParams paramsToShare) {
				if ("ShortMessage".equals(platform.getName())) {
					paramsToShare.setImageUrl(null);
					paramsToShare.setText(paramsToShare.getText()+"\n"+url.toString());
				}
			}
		});

		oks.setPlatform(platform);
		oks.show(context);
	}
}
