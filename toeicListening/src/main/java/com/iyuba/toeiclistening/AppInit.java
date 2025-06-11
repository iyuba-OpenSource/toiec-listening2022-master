package com.iyuba.toeiclistening;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.startup.Initializer;

import com.iyuba.core.util.ShareHelper;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.imooclib.IMooc;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.iyuba.share.ShareExecutor;
import com.iyuba.share.mob.MobShareExecutor;
import com.iyuba.toeiclistening.activity.MainActivity;
import com.mob.MobSDK;
import com.umeng.commonsdk.UMConfigure;
import com.yd.saas.ydsdk.manager.YdConfig;

import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.data.local.HLDBManager;

/**
 * 启动组件及第三方
 */
public class AppInit implements Initializer<String> {
    @NonNull
    @Override
    public String create(@NonNull Context context) {

        System.loadLibrary("msaoaidsec");

        ShareHelper.init(context);

        MobSDK.submitPolicyGrantResult(true);
        UMConfigure.init(context, "", "", UMConfigure.DEVICE_TYPE_PHONE, "");

        PersonalHome.init(context, Constant.APPID + "", com.iyuba.configation.Constant.AppName);
        PersonalHome.setCategoryType(com.iyuba.configation.Constant.AppName);
        PersonalHome.setMainPath(MainActivity.class.getSimpleName());

        HLDBManager.init(context);
        IHeadline.init(context, Constant.APPID + "", com.iyuba.configation.Constant.AppName);
        IHeadline.setEnableAd(true);
        IHeadline.setAdAppId(com.iyuba.toeiclistening.Constant.ADAPPID);
        IHeadline.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);
        YdConfig.getInstance().init(context, Constant.APPID + "");
        PrivacyInfoHelper.init(context);
        //微课分享是否有隐私弹窗
        PrivacyInfoHelper.getInstance().putApproved(true);
        IMoocDBManager.init(context);
        DLManager.init(context, 8);
        //appname 传type值
        IMooc.init(context, "224", com.iyuba.configation.Constant.AppName);
        IMooc.setAdAppId(com.iyuba.toeiclistening.Constant.ADAPPID);
        IMooc.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);
        //共同的分享
        MobShareExecutor executor = new MobShareExecutor();
        ShareExecutor.getInstance().setRealExecutor(executor);
        return "APP INIT";
    }

    @NonNull
    @Override
    public List<Class<? extends Initializer<?>>> dependencies() {
        return new ArrayList<>();
    }
}
