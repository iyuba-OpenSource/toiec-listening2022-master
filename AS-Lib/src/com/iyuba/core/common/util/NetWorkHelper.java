package com.iyuba.core.common.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class NetWorkHelper {
    private static NetWorkHelper sInstance;

    public static void init(Context appContext) {
        if (sInstance == null) {
            sInstance = new NetWorkHelper(appContext);
        }
    }

    public static NetWorkHelper getInstance() {
        if (sInstance == null) {
            throw new NullPointerException("NetWorkHelper is not initialized yet");
        }
        return sInstance;
    }

    private ConnectivityManager mConnectivityManager;
    private WifiManager mWifiManager;

    private NetWorkHelper(Context appContext) {
        mConnectivityManager = (ConnectivityManager) appContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mWifiManager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
    }

    public boolean isConnectingToInternet() {
        if (mConnectivityManager != null) {
            NetworkInfo[] infos = mConnectivityManager.getAllNetworkInfo();
            if (infos != null) {
                for (NetworkInfo Info : infos) {
                    if (Info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean isWifiOn() {
        if (mConnectivityManager != null) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED
                    && networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }
        return false;
    }

    public boolean isConnected() {
        if (mConnectivityManager != null) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * APN type: 0 #=> no network or unknown
     * 1 #=> mobile network
     * 2 #=> wifi network
     */
    public int getAPNType() {
        int netType = 0;
        if (mConnectivityManager != null) {
            NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (networkInfo != null) {
                switch (networkInfo.getType()) {
                    case ConnectivityManager.TYPE_MOBILE:
                        netType = 1;
                        break;
                    case ConnectivityManager.TYPE_WIFI:
                        netType = 2;
                        break;
                    default:
                        break;
                }
            }
        }
        return netType;
    }

    public String getMacAddress() {
        if (mWifiManager != null) {
            WifiInfo wInfo = mWifiManager.getConnectionInfo();
            if (wInfo != null) return wInfo.getMacAddress();
        }
        return "";
    }

}
