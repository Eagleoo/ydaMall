package com.alipay.android.appDemo4;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alipay.sdk.app.AuthTask;
import com.alipay.sdk.app.PayTask;
import com.lidroid.xutils.util.LogUtils;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class AliPayNet {

	private Activity context;
	private AliPayCallBack callBack;

	dologincallback callback;


	//APPID
	public static final String APPID= "2015120800936432";
	// 商户PID
	public static final String PARTNER = "2088901901964898";
	// 商户收款账号
	// public static final String SELLER = "2201277132@qq.com";
	public static final String SELLER = "2745437937@qq.com";


	/** 商户私钥，pkcs8格式 */
	/** 如下私钥，RSA2_PRIVATE 或者 RSA_PRIVATE 只需要填入一个 */
	/** 如果商户两个都设置了，优先使用 RSA2_PRIVATE */
	/** RSA2_PRIVATE 可以保证商户交易在更加安全的环境下进行，建议使用 RSA2_PRIVATE */
	/** 获取 RSA2_PRIVATE，建议使用支付宝提供的公私钥生成工具生成， */
	/** 工具地址：https://doc.open.alipay.com/docs/doc.htm?treeId=291&articleId=106097&docType=1 */

	// 商户私钥，pkcs8格式
	public static final String RSA_PRIVATE = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCq5twY0kR6SGwgvR0qQCwiyu5YegCfY1/QFheQK3RWCO+eafVov+3rqUVXI+ps6mBu3kOZsjumBTLo+LjJefZgf2nLTW79A9Y0dDFsGMieiTeuwQpOHhhsMuvRzomrgi/FWlTSNMCXu0eTPMg1pB+UVLTbnrXnHK6WbN69XRY4oTm66GwwiqpiTkxk9yAENtxufk1EZ2ZjeuSBfIJW7Qba7BZumnqifj2KCEqsonRKYbYtsS9MCNBMQj3Ab/Sxuxu0owjvcSRZvbpTxYmXHg32ChP1KPKrf5qNGOuFBxBS5/SnM4RKi6luUoFgkrmLACZ2SfNlbLLhj+jq7cCbjRInAgMBAAECggEBAKqNZ5sRxCrwuY/WixOOJT23nTMI1jQ6XPTyByPmadwuYMuL3NBjsdFcBQoYmquFHyWlhHsO5v4g9BSRmv/eSiU1ImQ0a43u1UdVqbjB5vxn3fz1Qw6AdEkObE7eOn/BlWDza/C3mngR+zUSFI+LscQ64J7H/aqd8hHGpmb+Td3GSeQd/EqLpCxrOho6htILiuoZW/VZU7EMLUF0YakgRe4hG+zfC1IMn7cV4wlYYHSXVldwmZWIwnflBtJ9/1nrQFNjiBe2TiThH89yF2JWga4sThqr8wYPoSklJBxFoOvjnoh+eSspyv4FoDbwyp04cSd8MPCYGsYhg5ggLjpga6kCgYEA83LcX2j20JvVT4RO6JSstD/zIGgd9RLSkb95pcX/DHglqNuNBWP7FN4AL6FWEGPs5W0n07RuEd+aL+3SXT9WFAK/O+/Ejka5uHjiYcCAeiZqtdCR6QmEljzV6AJwQbuS+Q5PYXEcdSs+akF5Duu4d1ynKV18VE9+Qhzbjw7ge90CgYEAs7Z+jdwblZL61dDoZ7wPjIYIWtZ3IgMVJv6kuYs6FN4hedJvavIRf9ueOeoau0zlPjqi2dALuR4+0m8+JQQHFqUEQWDksl6H+LmS8lZkEep1UwXWAabS4T1D169oWkNg01PbQ5PNEN7gagmcfwmf4Itli8nRDO5rV61SZ44st9MCgYBsrTHDRdAof+hRfvbazMPsE0Atj00QQj7N4XQklKauolPtdVUWuvMy9YePu6330yPFp1zIHRulzvO9waTPC58Zf8BuuTI9cUUnHI+yo4S3Ar9QOrNoHF+b0byLvcrvDRUnKUQ2c5AIni1WsbOg/Ylzf2EADkOaP4JVcRxyWGnN4QKBgFidmHHjI0byHvXn+XGNbi1guiXwsvX2hyufTSKg4JcBoYgsl/woOTDO3oq4QU4ycKfqyfqVd/JkU534dLBT+BrWa+7BFIRP0MR43VjZ+KZM8mVp70kJNdsTFqz4NYCs0MXkB66e5vN2szoWWzDZUhirI/t2HLs/pXaqZIPju3E5AoGAL8sHr4tk05B3Krhmqeb1kmlQ2Xad2OPI6b0uMsQhiZ6BZSj/ckH6QCW38jtMC1Nc6cX6S6o9EDvh4BegDIDtMNvElpSK2pvQ46U97NnUhuTaVKKlnwggbPnwiYfqgrnJXKCulmtG75C98lLjbeMUHcx0562s63o3EumG9DcgZDA=";
	public static final String RSA_PRIVATEpay = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAOyAOyB+tpvMu0zGoXuXb6XrwTvOiDq2ov4ugkN8U+keAYSbHfTK2axHfxfErtkBzLnVHKrap2SY+9AUtEpqrtHwd614MGIrah/S1b0Zg59BLrV3O8rdYUyZyqYB1R2Z5c/XqliDqxQpsfWWjrgq2H3cOO64M66JvH2JHBSBFEcZAgMBAAECgYA+1m9fWeuhB8u4QYqEcR8sIbDM+DpAB2jVSnw3/B+7b8qNdj2GXiNp3/FH2m1uoUjJE3Ozfbc+RluZ1PZ2U1QxVwQ8tPjuR1lIwa87YuvgEKclhht4HEnm66p4WsEtNUQLBWuEDoVfF3s2e+/aLx+P1zKcSjieCTAwgQCHWac+tQJBAPkUawyG5Facjud6J48ISnkHI1zMrqL5AgZf//gwu4w/NtHv41u7m1BN93TwxzwEmI9xB5LIrieyVehxcghDWZsCQQDzElh4T5KOdkQeTJZgbmEUUh1eTAdmQyMbtb6OhcoXKLfJQKW60DyanCsSdGC1wnXG09GcV0/r+2YbWfYi65dbAkB/BwG9JsImxbs/pBWupWzNom1Pc3eQ1+tHcwgo/Dl5wfdkQ7Iw41HHN6v+8Ji7LCDd7qgNHNlsl0+mtsyQGEEHAkEAsZvzFx4VmZEjHvyZWKbuuplxqihls7xU1/eJ4LwzuY5gFGbvcjycO+DmvBpbXBRWWM0HuXQ3CthseInYhxWcRwJAMEBQ4FY5ZNrH5hnVKw3Wo1g22uhusQbcuf9T4+bXpwKnldZPYS10dw7H9rDFzxGehlKAYzdbpVlkz4fKjTEXAA==";

	public static final String RSA2_PRIVATE = "";



	// 支付宝公钥
	public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";

	/** 支付宝账户登录授权业务：入参target_id值 */
	public static final String TARGET_ID = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

	private static final int SDK_PAY_FLAG = 1;
	private static final int SDK_AUTH_FLAG = 2;

	public AliPayNet(Activity context) {
		super();
		this.context = context;
	}

	private String getOrderInfo(String tid, String orderTitle, double money) {
		// 签约合作者身份ID
		String orderInfo = "partner=" + "\"" + PARTNER + "\"";
		// 签约卖家支付宝账号
		orderInfo += "&seller_id=" + "\"" + SELLER + "\"";
		// 商户网站唯一订单号
		orderInfo += "&out_trade_no=" + "\"" + tid + "\"";
		// 商品名称
		orderInfo += "&subject=" + "\"" + orderTitle + "\"";
		// 商品详情
		orderInfo += "&body=" + "\"" + orderTitle + "\"";
		// 商品金额
		orderInfo += "&total_fee=" + "\"" + money + "\"";
		// 服务器异步通知页面路径
		orderInfo += "&notify_url=" + "\""
				+ "http://app.yda360.com/alipay/Notify1.aspx" + "\"";
		// 服务接口名称， 固定值
		orderInfo += "&service=\"mobile.securitypay.pay\"";
		// 支付类型， 固定值
		orderInfo += "&payment_type=\"1\"";
		// 参数编码， 固定值
		orderInfo += "&_input_charset=\"utf-8\"";
		// 设置未付款交易的超时时间
		// 默认30分钟，一旦超时，该笔交易就会自动被关闭。
		// 取值范围：1m～15d。
		// m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
		// 该参数数值不接受小数点，如1.5h，可转换为90m。
		orderInfo += "&it_b_pay=\"1c\"";
		// extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
		// orderInfo += "&extern_token=" + "\"" + extern_token + "\"";
		// 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
		orderInfo += "&return_url=\" \"";
		// 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
		// orderInfo += "&paymethod=\"expressGateway\"";
		LogUtils.e(orderInfo);
		return orderInfo;
	}

	private NoLeakHandler mHandler=new NoLeakHandler(this);

	private static class NoLeakHandler extends Handler{
		private WeakReference<AliPayNet> mActivity;

		public NoLeakHandler(AliPayNet activity){
			mActivity = new WeakReference<>(activity);
		}

		@Override
		public void handleMessage(Message msg) {
			if (mActivity.get()==null){
				return;
			}
			AliPayNet aliPayNet =	mActivity.get();
			Log.e("FLAG","FLAG"+msg.what+"Message"+msg.obj.toString());
if (msg.what==SDK_AUTH_FLAG){
	AuthResult authResult = new AuthResult((Map<String, String>) msg.obj, true);
	Log.e("登录返回信息","authResult"+authResult.toString());
	String resultStatus = authResult.getResultStatus();

	// 判断resultStatus 为“9000”且result_code
	// 为“200”则代表授权成功，具体状态码代表含义可参考授权接口文档
	if (TextUtils.equals(resultStatus, "9000") && TextUtils.equals(authResult.getResultCode(), "200")) {
		// 获取alipay_open_id，调支付时作为参数extern_token 的value
		// 传入，则支付账户为该授权账户
		Toast.makeText(aliPayNet.context,
				"授权成功", Toast.LENGTH_SHORT)
				.show();
		Log.e("授权成功","授权成功"+String.format("authCode:%s", authResult.getAuthCode()));

	} else {
		// 其他状态值则为授权失败
		Toast.makeText(aliPayNet.context,
				"授权失败" , Toast.LENGTH_SHORT).show();
		Log.e("授权失败","授权失败"+String.format("authCode:%s", authResult.getAuthCode()));

	}
	if (aliPayNet.callback!=null){
		aliPayNet.callback.call(authResult);
	}
}else {
	PayResult payResult = new PayResult((String) msg.obj);

	// 支付宝返回此次支付结果及加签，建议对支付宝签名信息拿签约时支付宝提供的公钥做验签
	String resultInfo = payResult.getResult();
	String resultStatus = payResult.getResultStatus();
	// 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
	if (TextUtils.equals(resultStatus, "9000")) {
		aliPayNet.callBack.doSuccess("9000");
	} else {
		// 判断resultStatus 为非“9000”则代表可能支付失败
		// “8000”代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
		if (TextUtils.equals(resultStatus, "8000")) {
			aliPayNet.callBack.doSuccess("8000");
		} else {
			aliPayNet.callBack.doFailure(resultInfo + ":" + resultStatus);
		}
	}
}



		}
	}





	public void pay(String orderId, String orderTitle, double money,
			final AliPayCallBack callBack) {
		this.callBack = callBack;

		// 对订单做RSA 签名
		String orderInfo = getOrderInfo(orderId, orderTitle, money);
		String sign = sign(orderInfo);
		try {
			// 仅需对sign 做URL编码
			sign = URLEncoder.encode(sign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		// 完整的符合支付宝参数规范的订单信息
		final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
				+ getSignType();
		LogUtils.e("payInfo===" + payInfo);
		;
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 构造PayTask 对象
				PayTask alipay = new PayTask(context);
				// 调用支付接口，获取支付结果
//				Map<String,String> map=alipay.payV2(payInfo,true);
				String result = alipay.pay(payInfo,true);
				Log.e( "支付返回","result"+result);
				Message msg = new Message();
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * get the sdk version. 获取SDK版本号
	 * 
	 */
	public void getSDKVersion() {
		PayTask payTask = new PayTask(context);
		String version = payTask.getVersion();
		Toast.makeText(context, version, Toast.LENGTH_SHORT).show();
	}

	public interface dologincallback{
		public void call(AuthResult authResult);
	}


	/**
	 * 支付宝账户授权业务
	 *
	 * @param
	 */
	public void functionlogIn(dologincallback callback) {

		this.callback=callback;
		if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(APPID)
				|| (TextUtils.isEmpty(RSA2_PRIVATE) && TextUtils.isEmpty(RSA_PRIVATE))
				|| TextUtils.isEmpty(TARGET_ID)) {
			new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置PARTNER |APP_ID| RSA_PRIVATE| TARGET_ID")
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialoginterface, int i) {
						}
					}).show();
			return;
		}

		/**
		 * 这里只是为了方便直接向商户展示支付宝的整个支付流程；所以Demo中加签过程直接放在客户端完成；
		 * 真实App里，privateKey等数据严禁放在客户端，加签过程务必要放在服务端完成；
		 * 防止商户私密数据泄露，造成不必要的资金损失，及面临各种安全风险；
		 *
		 * authInfo的获取必须来自服务端；
		 */
//		boolean rsa2 = (RSA2_PRIVATE.length() > 0);
		boolean rsa2 = false;
		Map<String, String> authInfoMap = buildAuthInfoMap(PARTNER, APPID, TARGET_ID, rsa2);
		String info = buildOrderParam(authInfoMap);
		Log.e("info","info"+info);

		String privateKey = rsa2 ? RSA2_PRIVATE : RSA_PRIVATE;
		String sign = getSign(authInfoMap, privateKey, rsa2);
//		final String authInfo = info + "&" + sign;

		// 商户唯一标识，如：kkkkk091125
		String target_id1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				.replaceAll("\\s*", "")
				.replaceAll("-","")
				.replaceAll(":","")
				;

		final String str="apiname=com.alipay.account.auth&app_id="+APPID+"&app_name=mc&auth_type=AUTHACCOUNT&biz_type=openservice&method=alipay.open.auth.sdk.code.get" +
				"&pid="+PARTNER+"&product_id=APP_FAST_LOGIN&scope=kuaijie" +
				"&target_id="+target_id1+ "&" +getSign(authInfoMap, privateKey, rsa2)+"&sign_type="+(rsa2 ? "RSA2" : "RSA");

		Log.e("authInfo","authInfo"+str);
		Runnable authRunnable = new Runnable() {

			@Override
			public void run() {
				// 构造AuthTask 对象
				AuthTask authTask = new AuthTask(context);
				// 调用授权接口，获取授权结果
				Map<String, String> result = authTask.authV2(str, true);

				Message msg = new Message();
				msg.what = SDK_AUTH_FLAG;
				msg.obj = result;
				mHandler.sendMessage(msg);
			}
		};

		// 必须异步调用
		Thread authThread = new Thread(authRunnable);
		authThread.start();
	}


	/**
	 * 构造授权参数列表
	 *
	 * @param pid
	 * @param app_id
	 * @param target_id
	 * @return
	 */
	public static Map<String, String> buildAuthInfoMap(String pid, String app_id, String target_id, boolean rsa2) {
		Map<String, String> keyValues = new HashMap<String, String>();

		// 商户签约拿到的app_id，如：2013081700024223
		keyValues.put("app_id", app_id);

		// 商户签约拿到的pid，如：2088102123816631
		keyValues.put("pid", pid);

		// 服务接口名称， 固定值
		keyValues.put("apiname", "com.alipay.account.auth");

		// 商户类型标识， 固定值
		keyValues.put("app_name", "mc");

		// 业务类型， 固定值
		keyValues.put("biz_type", "openservice");

		// 产品码， 固定值
		keyValues.put("product_id", "APP_FAST_LOGIN");

		// 授权范围， 固定值
		keyValues.put("scope", "kuaijie");

		// 商户唯一标识，如：kkkkk091125
		String target_id1=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())
				.replaceAll("\\s*", "")
				.replaceAll("-","")
				.replaceAll(":","")
				;
		Log.e("target_id1","target_id1:"+target_id1);
		keyValues.put("target_id", target_id1);

		// 授权类型， 固定值
		keyValues.put("auth_type", "AUTHACCOUNT");

		keyValues.put("method", "alipay.open.auth.sdk.code.get");

		// 签名类型
		keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

		return keyValues;
	}

	/**
	 * 构造支付订单参数列表
	 * @param
	 * @param app_id
	 * @param
	 * @return
	 */
	public static Map<String, String> buildOrderParamMap(String app_id, boolean rsa2) {
		Map<String, String> keyValues = new HashMap<String, String>();

		keyValues.put("app_id", app_id);

		keyValues.put("biz_content", "{\"timeout_express\":\"30m\",\"product_code\":\"QUICK_MSECURITY_PAY\",\"total_amount\":\"0.01\",\"subject\":\"1\",\"body\":\"我是测试数据\",\"out_trade_no\":\"" + getOutTradeNo() +  "\"}");

		keyValues.put("charset", "utf-8");

		keyValues.put("method", "alipay.trade.app.pay");

		keyValues.put("sign_type", rsa2 ? "RSA2" : "RSA");

		keyValues.put("timestamp", "2016-07-29 16:55:53");

		keyValues.put("version", "1.0");

		return keyValues;
	}

	/**
	 * 构造支付订单参数信息
	 *
	 * @param map
	 * 支付订单参数
	 * @return
	 */
	public static String buildOrderParam(Map<String, String> map) {
		List<String> keys = new ArrayList<String>(map.keySet());

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			sb.append(buildKeyValue(key, value, true));
			sb.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		sb.append(buildKeyValue(tailKey, tailValue, true));

		return sb.toString();
	}

	/**
	 * 拼接键值对
	 *
	 * @param key
	 * @param value
	 * @param isEncode
	 * @return
	 */
	private static String buildKeyValue(String key, String value, boolean isEncode) {
		StringBuilder sb = new StringBuilder();
		sb.append(key);
		sb.append("=");
		if (isEncode) {
			try {
				sb.append(URLEncoder.encode(value, "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				sb.append(value);
			}
		} else {
			sb.append(value);
		}
		return sb.toString();
	}

	/**
	 * 对支付参数信息进行签名
	 *
	 * @param map
	 *            待签名授权信息
	 *
	 * @return
	 */
	public static String getSign(Map<String, String> map, String rsaKey, boolean rsa2) {
		List<String> keys = new ArrayList<String>(map.keySet());
		// key排序
		Collections.sort(keys);

		StringBuilder authInfo = new StringBuilder();
		for (int i = 0; i < keys.size() - 1; i++) {
			String key = keys.get(i);
			String value = map.get(key);
			authInfo.append(buildKeyValue(key, value, false));
			authInfo.append("&");
		}

		String tailKey = keys.get(keys.size() - 1);
		String tailValue = map.get(tailKey);
		authInfo.append(buildKeyValue(tailKey, tailValue, false));

		String oriSign = SignUtils.sign(authInfo.toString(), rsaKey, rsa2);
		String encodedSign = "";

		try {
			encodedSign = URLEncoder.encode(oriSign, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "sign=" + encodedSign;
	}

	/**
	 * 要求外部订单号必须唯一。
	 * @return
	 */
	private static String getOutTradeNo() {
		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
		Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);
		return key;
	}



	/**
	 * sign the order info. 对订单信息进行签名
	 * 
	 * @param content
	 *            待签名订单信息
	 */
	public String sign(String content) {
		return SignUtils.sign(content, RSA_PRIVATEpay);
	}

	/**
	 * get the sign type we use. 获取签名方式
	 * 
	 */
	public String getSignType() {
		return "sign_type=\"RSA\"";
	}

}
