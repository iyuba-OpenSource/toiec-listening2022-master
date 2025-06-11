package com.iyuba.core.microclass.manager;

import android.content.Context;
import android.util.Log;

import com.iyuba.core.common.listener.AppUpdateCallBack;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.microclass.protocol.appUpdateRequest;
import com.iyuba.core.microclass.protocol.appUpdateResponse;

/**
 * 版本管理
 *
 * @author chentong
 */

public class VersionManager {
    private static VersionManager instance;
    public static final int version = 19;
    public static final String VERSION_CODE = "3.1.0619";//3.0.1214

    private VersionManager() {
    }

    ;

    public static synchronized VersionManager Instace(Context context) {
        if (instance == null) {
            instance = new VersionManager();
        }
        return instance;
    }

    /**
     * 检查是否有新版本
     *
     * @param version
     * @param  aucb
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
