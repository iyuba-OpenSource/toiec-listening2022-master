package com.iyuba.multithread.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

public class NetStatusUtil {
	
//	/**
//     * ping 百度
//     * 
//     * @return
//     */
//    public static void connectionNetwork(final CommonCallBack callBack) {
//    	Thread thread = new Thread(new Runnable() {
//			
//			@Override
//			public void run() {
//				// TODO 自动生成的方法存根
//				boolean result = false;
//				try {
//					 Process process = Runtime.getRuntime().exec(
//				                "/system/bin/ping -c 8 " + "http://www.baidu.com");
//					 int status = process.waitFor();
//					 if (status == 0) {
//						 callBack.onPositive(null);
//					 } else {
//						 callBack.onNegative(null);
//					 }
//				} catch (IOException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					// TODO 自动生成�?catch �?
//					e.printStackTrace();
//				} finally {
//				}
//			}
//		});
//		}
//	
	
	/**
     * 判断当前网络是否是wifi网络
     * 
     * @param context
     * @return boolean
     */
    public static boolean isWifi(Context context) {
	ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
	if (activeNetInfo != null && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI) {
	    return true;
	}
	return false;
    }
	
	public static boolean is2G(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && 
				(activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EDGE
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_GPRS 
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_CDMA)) {
		    return true;
		}
		return false;
	}
	/**
     * 判断当前网络是否3G网络
     * 
     * @param context
     * @return boolean
     */
	public static boolean is3G(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && 
				(activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EHRPD
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_0 
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_A
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_EVDO_B
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSDPA
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSPAP
				|| activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_HSUPA)) {
		    return true;
		}
		return false;
	}
	
	/**
     * 判断当前网络是否4G网络
     * 
     * @param context
     * @return boolean
     */
	public static boolean is4G(Context context){
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetInfo != null && 
				(activeNetInfo.getSubtype() == TelephonyManager.NETWORK_TYPE_LTE)) {
		    return true;
		}
		return false;
	}
	
	/**
     * wifi是否打开
     */
    public static boolean isWifiOpen(Context context) {
	ConnectivityManager mgrConn = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	TelephonyManager mgrTel = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return ((mgrConn.getActiveNetworkInfo() != null && mgrConn.getActiveNetworkInfo().getState() == NetworkInfo.State.CONNECTED) 
		|| mgrTel.getNetworkType() == TelephonyManager.NETWORK_TYPE_UMTS);
    }
    
    /**
     * 获取本机串号imei
     * 
     * @param context
     * @return
     */
    public static String getIMEI(Context context) {
	TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	return telephonyManager.getDeviceId();
    }


	public static boolean isConnected(Context context) {
		 ConnectivityManager cm = (ConnectivityManager) context   
	                .getSystemService(Context.CONNECTIVITY_SERVICE);   
	        if (cm == null) {   
	        	 return false;   
	        } else if (cm.getActiveNetworkInfo()!=null) {
	        	return cm.getActiveNetworkInfo().isAvailable();  
	        }   
	        else {
				return false;
			}
	}
	/**
	 * 检测当的网络（WLAN、3G/2G）状态
	 * @param context Context
	 * @return true 表示网络可用
	 */
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity != null) {
			NetworkInfo info = connectivity.getActiveNetworkInfo();
			if (info != null && info.isConnected())
			{
				// 当前网络是连接的
				if (info.getState() == NetworkInfo.State.CONNECTED)
				{
					// 当前所连接的网络可用
					return true;
				}
			}
		}
		return false;
	}

}
