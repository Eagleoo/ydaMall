package com.mall.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class ConnectionDetector {
	private Context _context;
	public ConnectionDetector(Context context) {
		this._context = context;
	}
	public boolean isConnectingToInternet() {
		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null)
				for (int i = 0; i < info.length; i++)
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
		}
		return false;
	}
	  public static boolean isWifiConnected(Context context)
	    {
	        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
	        NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	        if(wifiNetworkInfo.isConnected())
	        {
	            return true ;
	        }
	     
	        return false ;
	    }
	public boolean isMobileConnected(Context context) {
		ConnectivityManager mConnectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		@SuppressWarnings("static-access")
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); // ����������ӣ������������ã��Ͳ���Ҫ�������������
		NetworkInfo info = mConnectivity.getActiveNetworkInfo();
		if (info == null || !mConnectivity.getBackgroundDataSetting()) {
			return false;                                  
		}
		// �ж������������ͣ�ֻ����3G/wifi�����һЩ��ݸ��¡�
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI) {
			return info.isConnected();
		} else if (netType == ConnectivityManager.TYPE_MOBILE
				&& netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming()) {
			return info.isConnected();
		} else if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS
				|| netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
				|| netSubtype == TelephonyManager.NETWORK_TYPE_EDGE) {
			return false;
		} else {
			return false;
		}
	}
}
