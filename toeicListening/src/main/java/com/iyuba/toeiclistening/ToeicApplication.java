package com.iyuba.toeiclistening;

import android.app.Application;
import android.os.Environment;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.IHeadline;
import com.iyuba.headlinelibrary.data.local.db.HLDBManager;
import com.iyuba.imooclib.IMooc;
import com.iyuba.module.commonvar.CommonVars;
import com.iyuba.module.dl.BasicDLDBManager;
import com.iyuba.module.favor.BasicFavor;
import com.iyuba.module.favor.data.local.BasicFavorDBManager;
import com.iyuba.module.movies.IMovies;
import com.iyuba.module.privacy.IPrivacy;
import com.iyuba.module.privacy.PrivacyInfoHelper;
import com.umeng.commonsdk.UMConfigure;

import org.litepal.LitePalApplication;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.PersonalType;
import timber.log.Timber;

public class ToeicApplication extends Application {

    private static ToeicApplication application;


    public static ToeicApplication getApplication() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        //初始化文件存储路径
        com.iyuba.toeiclistening.Constant.APP_DATA_PATH = getExternalFilesDir(null).getAbsolutePath() + File.separator;
        //初始化评测文件存储路径
        Constant.envir = getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();

        CrashApplication.init(this);


        Timber.plant(new Timber.DebugTree());

        UMConfigure.preInit(this, "", "");

        if (!ConfigManager.Instance().loadString("short1").equals("")) {
            com.iyuba.configation.Constant.IYBHttpHead = ConfigManager.Instance().loadString("short1");
            com.iyuba.configation.Constant.IYBHttpHead2 = ConfigManager.Instance().loadString("short2");
        }
        String extraUrl = "http://iuserspeech." + com.iyuba.configation.Constant.IYBHttpHead + ":9001/test/ai/";
        String extraMergeUrl = "http://iuserspeech." + Constant.IYBHttpHead + ":9001/test/merge/";

        IPrivacy.init(this);
        IPrivacy.setPrivacyUsageUrl(WebConstant.HTTP_SPEECH_ALL + "/api/protocoluse666.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=爱语吧",
                WebConstant.HTTP_SPEECH_ALL + "/api/protocolpri.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=1");
        //MobSDK.init(this, "1073eb7a17c9d", "81db9d7f5f315fa12a454ca4fc41290a");
        //SMSSDK.initSDK(this, Constant.SMSAPPID, Constant.SMSAPPSECRET);
        CommonVars.domain = Constant.IYBHttpHead;
        CommonVars.domainLong = Constant.IYBHttpHead2;

        LitePalApplication.initialize(this);

        //微课
        //微课模块
        DLManager.init(getApplicationContext(), 8);
        IMooc.init(getApplicationContext(), Constant.APPID, Constant.AppName);
        IMooc.setAdAppId(com.iyuba.toeiclistening.Constant.ADAPPID);
        IMooc.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);
        IMooc.setEnableShare(true);

        //看一看
        BasicFavorDBManager.init(getApplicationContext());
        BasicDLDBManager.init(getApplicationContext());

        IMovies.init(getApplicationContext(), Constant.APPID, Constant.AppName);

        //视频模块
        IHeadline.init(getApplicationContext(), Constant.APPID, Constant.AppName);
        IHeadline.setAdAppId(com.iyuba.toeiclistening.Constant.ADAPPID);
        IHeadline.setYdsdkTemplateKey(
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS,
                BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD,
                null);
        IHeadline.setExtraMseUrl(extraUrl);
        IHeadline.setExtraMergeAudioUrl(extraMergeUrl);

        BasicDLDBManager.init(getApplicationContext());
        BasicFavorDBManager.init(getApplicationContext());
        HLDBManager.init(getApplicationContext());

        List<String> typeFilter = new ArrayList<>();
        typeFilter.add(HeadlineType.SMALLVIDEO);
        typeFilter.add(HeadlineType.VOAVIDEO);
        typeFilter.add(HeadlineType.TED);
        typeFilter.add(HeadlineType.MEIYU);
        typeFilter.add(HeadlineType.TOPVIDEOS);
        typeFilter.add(HeadlineType.BBCWORDVIDEO);
        BasicFavor.setTypeFilter(typeFilter);


        DLManager.init(getApplicationContext(), 8);  //下载
        BasicFavor.init(getApplicationContext(), Constant.APPID);

        //个人中心初始化
        PersonalHome.init(getApplicationContext(), Constant.APPID, Constant.AppName);
        PersonalHome.setEnableEditNickname(false);
        PersonalHome.setEnableShare(false);
        PersonalHome.setCategoryType(PersonalType.VOA);
        PrivacyInfoHelper.getInstance().putApproved(true);
    }


}
