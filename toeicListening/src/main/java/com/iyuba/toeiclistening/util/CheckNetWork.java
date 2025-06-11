package com.iyuba.toeiclistening.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class CheckNetWork {
    /**
     * @param context
     * @return 检查网络链接是否可用
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo(); //getAllNetworkInfo()

            if (info != null && info.isConnected()) {
                return true;
            }
            return false;
        }

    }

    /**
     * 鍔熻兘锛?
     */
//  private void checkNetworkInfo()
//  {
////      ConnectivityManager conMan = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//      //mobile 3G Data Network
//      State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
////      txt3G.setText(mobile.toString());
//      //wifi
//      State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
////      txtWifi.setText(wifi.toString());
//      
//      //濡傛灉3G缃戠粶鍜寃ifi缃戠粶閮芥湭杩炴帴锛屼笖涓嶆槸澶勪簬姝ｅ湪杩炴帴鐘舵? 鍒欒繘鍏etwork Setting鐣岄潰 鐢辩敤鎴烽厤缃綉缁滆繛鎺?
//      if(mobile==State.CONNECTED||mobile==State.CONNECTING)
//          return;
//      if(wifi==State.CONNECTED||wifi==State.CONNECTING)
//          return;
//      
//      
////      startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//杩涘叆鏃犵嚎缃戠粶閰嶇疆鐣岄潰
//      //startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //杩涘叆鎵嬫満涓殑wifi缃戠粶璁剧疆鐣岄潰
//      
//  }
    //判断wifi
    public static boolean checkWifiStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (null != networkInfo
                && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return true;
        }
        return false;
    }

}
