package net.sourceforge.mm;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;

import com.lidroid.xutils.util.LogUtils;
import com.lin.component.CustomProgressDialog;
import com.mall.net.Web;
import com.mall.serving.voip.view.popupwindow.VoipDialog;
import com.mall.util.MD5;
import com.mall.util.Util;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 
 * 功能： 微信支付<br>
 * 时间： 2014-11-13<br>
 * 备注： <br>
 * 
 * @author Lin.~
 * 
 */
@SuppressLint("NewApi")
public class MMPay {
	private static Activity activity;
	private IWXAPI api;
	private String orderId;
	private String orderBody;
	private double money;
	private CustomProgressDialog pd;
	private StringBuffer sb = new StringBuffer();

	public MMPay(Activity activity, CustomProgressDialog pd, double money, String orderId, String orderBody) {
		super();
		this.activity=null;
		this.activity = activity;
		this.pd = pd;
		this.money = money;
		Util.orderBody="";
		Util.orderBody=orderBody;
		Util.redbenamoney="";
		Util.redbenamoney=money+"";
		this.orderBody = orderBody;
		this.orderId = orderId;
	}

	public static Activity getactivity(){
return activity;
	}


	public void pay() {
		if (Web.test_url.equals(Web.url) || Web.test_url2.equals(Web.url)) {
			Util.show("检测到您当前使用的是演示版，你的支付将会支付到演示版。请确认。", activity);
		}
		api = WXAPIFactory.createWXAPI(activity, Constants.APP_ID);
		if (!api.isWXAppInstalled()) {
			if (null == pd) {
				pd.cancel();
				pd.dismiss();
			}
			VoipDialog voipDialog = new VoipDialog("亲！您还没安装微信哟...", activity, "前去安装", "恩，知道了", new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.openWeb(activity,
							"http://weixin.qq.com/cgi-bin/readtemplate?uin=&stype=&promote=&fr=www.baidu.com&lang=zh_CN&ADTAG=&check=false&t=weixin_download_method&sys=android&loc=weixin,android,web,0");
				}
			}, new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					pd.cancel();
					pd.dismiss();
				}
			});
			voipDialog.show();
			return;
		}
		api.registerApp(Constants.APP_ID);
		if (!api.isWXAppSupportAPI()) {
			if (null == pd) {
				pd.cancel();
				pd.dismiss();
			}
			VoipDialog voipDialog = new VoipDialog("亲！您的微信还不支持支付功能...", activity, "去下载", "恩，知道了", new OnClickListener() {
				@Override
				public void onClick(View v) {
					Util.openWeb(activity,
							"http://weixin.qq.com/cgi-bin/readtemplate?uin=&stype=&promote=&fr=www.baidu.com&lang=zh_CN&ADTAG=&check=false&t=weixin_download_method&sys=android&loc=weixin,android,web,0");
				}
			}, null);
			voipDialog.show();
			return;
		}

		new GetPrepayIdTask().execute();
	}

	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}

	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}

	private String getTraceId() {
		return orderId;
	}

	private String genOutTradNo() {
		return orderId;
	}

	private String genProductArgs() {

		StringBuffer xml = new StringBuffer();

		try {
			String nonceStr = genNonceStr();

			xml.append("</xml>");
			List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
			packageParams.add(new BasicNameValuePair("appid", Constants.APP_ID));
			packageParams.add(new BasicNameValuePair("body", orderBody));
			packageParams.add(new BasicNameValuePair("mch_id", Constants.MER_ID));
			packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
			if (Web.test_url.equals(Web.url) || Web.test_url2.equals(Web.url))
				packageParams.add(new BasicNameValuePair("notify_url", "http://test.yda360.cn/mmPay/wxV3.aspx"));
			else
				packageParams.add(new BasicNameValuePair("notify_url", "http://app.yda360.com/mmPay/wxV3.aspx"));
			packageParams.add(new BasicNameValuePair("out_trade_no", genOutTradNo()));
			packageParams.add(new BasicNameValuePair("spbill_create_ip", "10.10.10.10"));
			if (Web.test_url.equals(Web.url) || Web.test_url2.equals(Web.url))
				packageParams.add(new BasicNameValuePair("total_fee", "1"));
			else
				packageParams.add(new BasicNameValuePair("total_fee", ((int) (money * 100)) + ""));
			packageParams.add(new BasicNameValuePair("trade_type", "APP"));

			String sign = genPackageSign(packageParams);
			packageParams.add(new BasicNameValuePair("sign", sign));

			String xmlstring = toXml(packageParams);

			return xmlstring;

		} catch (Exception e) {
			LogUtils.e("genProductArgs fail, ex = " + e.getMessage());
			return null;
		}
	}

	// ----------------------------------

	/**
	 * 生成签名
	 */

	private String genPackageSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", packageSign);
		return packageSign;
	}

	private void genPayReq(Map<String, String> resultunifiedorder) {
		PayReq req = new PayReq();
		req.appId = Constants.APP_ID;
		req.partnerId = Constants.MER_ID;
		req.prepayId = resultunifiedorder.get("prepay_id");
		req.packageValue = "Sign=WXPay";
		req.nonceStr = genNonceStr();
		req.timeStamp = String.valueOf(genTimeStamp());

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

		req.sign = genAppSign(signParams);

		sb.append("sign\n" + req.sign + "\n\n");
		Log.e("orion", signParams.toString());

		api.registerApp(Constants.APP_ID);
		api.sendReq(req);
		if (null != pd) {
			pd.cancel();
			pd.dismiss();
		}
	}

	private String genAppSign(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(Constants.API_KEY);

		this.sb.append("sign str\n" + sb.toString() + "\n\n");
		String appSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
		Log.e("orion", appSign);
		return appSign;
	}

	private class GetPrepayIdTask extends AsyncTask<Void, Void, Map<String, String>> {

		@Override
		protected void onPreExecute() {
			pd.setMessage(pd, "正在创建预支付订单..");
		}

		@Override
		protected void onPostExecute(Map<String, String> result) {
			if(null != result && !result.containsKey("error")){
				sb.append("prepay_id\n" + result.get("prepay_id") + "\n\n");
				pd.setMessage(pd, "正在调起微信...");
				genPayReq(result);
			}else{
				Util.show(result.get("error"), activity);
				pd.cancel();
				pd.dismiss();
			}
		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String, String> doInBackground(Void... params) {

			String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
			String entity = genProductArgs();

			Log.e("orion", entity + "___1");
			String content="";
			try {
				byte[] buf = Util.httpPost(url, new String(entity.getBytes(), "ISO8859-1"));
				content = new String(buf);
				Log.e("orion", content);
				Map<String, String> xml = decodeXml(content);

				return xml;
			} catch (UnsupportedEncodingException e) {
				Map<String,String> map = new HashMap<String,String>();
				map.put("error", content);
				map.put("srouce", e.getMessage());
				return map;
			}
		}
	}

	public Map<String, String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:

					break;
				case XmlPullParser.START_TAG:

					if ("xml".equals(nodeName) == false) {
						// 实例化student对象
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion", e.toString());
			Util.show(e.toString() + "___3", activity);
		}
		return null;

	}

	private String toXml(List<NameValuePair> params) {
		StringBuilder sb = new StringBuilder();
		sb.append("<xml>");
		for (int i = 0; i < params.size(); i++) {
			sb.append("<" + params.get(i).getName() + ">");

			sb.append(params.get(i).getValue());
			sb.append("</" + params.get(i).getName() + ">");
		}
		sb.append("</xml>");

		Log.e("orion", sb.toString());
		return sb.toString();
	}
}
