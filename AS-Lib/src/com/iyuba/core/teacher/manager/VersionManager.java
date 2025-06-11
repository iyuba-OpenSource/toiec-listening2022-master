package com.iyuba.core.teacher.manager;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

import com.iyuba.core.common.listener.AppUpdateCallBack;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.teacher.protocol.appUpdateRequest;
import com.iyuba.core.teacher.protocol.appUpdateResponse;

/**
 * 版本管理
 *
 * @author chentong
 */

public class VersionManager {
    private static VersionManager instance;
    public static int version = 19;
    public static String VERSION_CODE = "3.2.0516";//3.0.1214
    public static Context mContext;

    private VersionManager() {
        try {
            version = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(),0).versionCode;
            VERSION_CODE = mContext.getApplicationContext().getPackageManager().getPackageInfo(mContext.getPackageName(),0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    ;

    public static synchronized VersionManager Instace(Context context) {
        mContext = context;
        if (instance == null) {
            instance = new VersionManager();
        }

        return instance;
    }



    /**
     * 检查是否有新版本
     *
     * @param version
     * @param Interface AppUpdateCallBack aucb
     */
    public void checkNewVersion(int version, final AppUpdateCallBack aucb) {
        ClientSession.Instace().asynGetResponse(new appUpdateRequest(version),
                new IResponseReceiver() {
                    @Override
                    public void onResponse(BaseHttpResponse response,
                                           BaseHttpRequest request, int rspCookie) {
                        appUpdateResponse aur = (appUpdateResponse) response;
                        if (aur.result.equals("NO")) {
                            // 有新版本
                            if (aucb != null) {
                                String data = aur.data.replace("||", "★");
                                String[] appUpdateInfos = data.split("★");
                                Log.e("^^^^^^^^^^^^", data);
                                if (appUpdateInfos.length == 2) {
                                    aucb.appUpdateSave(appUpdateInfos[0],
                                            appUpdateInfos[1]);
                                } else {
                                    aucb.appUpdateFaild();
                                }
                            }
                        } else {
                            // 检查失败
                            if (aucb != null) {
                                aucb.appUpdateFaild();
                            }
                        }
                    }
                }, null, null);
    }
}
