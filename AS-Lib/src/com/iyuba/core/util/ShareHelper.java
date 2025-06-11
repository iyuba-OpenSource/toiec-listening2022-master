package com.iyuba.core.util;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.iyuba.configation.Constant;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.mob.MobSDK;
import com.youdao.sdk.common.YouDaoAd;
import com.youdao.sdk.common.YoudaoSDK;

import java.util.HashMap;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.favorite.WechatFavorite;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by YM on 2021/4/12 15:33
 */
public class ShareHelper {

    public static void init(Context appContext) {
        MobSDK.init(appContext, Constant.SMSAPPKEY, Constant.SMSAPPSECRET);

        MobSDK.submitPolicyGrantResult(true);
        Log.e("MobSDK", "init: ");
        initPlatforms();

        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);
        initOther(appContext);
    }

    private static void initOther(Context context) {

        YouDaoAd.getNativeDownloadOptions().setConfirmDialogEnabled(true);
        YouDaoAd.getYouDaoOptions().setAppListEnabled(false);
        YouDaoAd.getYouDaoOptions().setPositionEnabled(false);
        YouDaoAd.getYouDaoOptions().setSdkDownloadApkEnabled(true);
        YouDaoAd.getYouDaoOptions().setDeviceParamsEnabled(false);
        YouDaoAd.getYouDaoOptions().setWifiEnabled(false);
        YoudaoSDK.init(context);
        Stetho.initialize(
                Stetho.newInitializerBuilder(context)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(context))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(context))
                        .build());
    }

    private static void initPlatforms() {
        String wechatAppId = Constant.WECHAT_APP_KEY;
        String wechatAppSecret = Constant.WECHAT_APP_SECRET;
        String qqId = Constant.QQ_APP_KEY;
        String qqKey = Constant.QQ_APP_SECRET;
        String sinaKey = Constant.SINA_APP_KEY;
        String sinaSecret = Constant.SINA_APP_SECRET;
        setDevInfo(QQ.NAME, qqId, qqKey);
        setDevInfo(QZone.NAME, qqId, qqKey);
        setDevInfo(SinaWeibo.NAME, sinaKey, sinaSecret);
        setDevInfo(Wechat.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatMoments.NAME, wechatAppId, wechatAppSecret);
        setDevInfo(WechatFavorite.NAME, wechatAppId, wechatAppSecret);
    }

    private static void setDevInfo(String platform, String str1, String str2) {
        HashMap<String, Object> devInfo = new HashMap<>();
        if (SinaWeibo.NAME.equals(platform)) {
            devInfo.put("Id", "1");
            devInfo.put("SortId", "1");
            devInfo.put("AppKey", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("RedirectUrl", "http://" + Constant.IYBHttpHead);
            devInfo.put("ShareByAppClient", "true");
        } else if (QQ.NAME.equals(platform)) {

            devInfo.put("Id", "2");
            devInfo.put("SortId", "2");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (QZone.NAME.equals(platform)) {

            devInfo.put("Id", "3");
            devInfo.put("SortId", "3");
            devInfo.put("AppId", str1);
            devInfo.put("AppKey", str2);
            devInfo.put("Enable", "true");
            devInfo.put("ShareByAppClient", "true");
        } else if (Wechat.NAME.equals(platform)) {
            devInfo.put("Id", "4");
            devInfo.put("SortId", "4");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatMoments.NAME.equals(platform)) {
            devInfo.put("Id", "5");
            devInfo.put("SortId", "5");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
            devInfo.put("BypassApproval", "false");
        } else if (WechatFavorite.NAME.equals(platform)) {
            devInfo.put("Id", "6");
            devInfo.put("SortId", "6");
            devInfo.put("AppId", str1);
            devInfo.put("AppSecret", str2);
            devInfo.put("Enable", "true");
        }
        ShareSDK.setPlatformDevInfo(platform, devInfo);
    }
}
