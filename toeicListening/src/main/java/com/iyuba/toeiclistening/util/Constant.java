package com.iyuba.toeiclistening.util;

import com.iyuba.configation.WebConstant;

/**
 * 程序中常用的常量
 *
 * @author 魏申鸿
 */
public class Constant {

    // assets目录下图片存放文件夹
    public static final String ASSETS_IMAGE_PATH = "image";
    // images转存到sd卡上文件夹
    public static final String SDCARD_IMAGE_PATH = "image";
    // mp3文件存放sd卡上文件夹
    public static final String SDCARD_AUDIO_PATH = "audio";
    public static final String ASSETS_AUDIO_PATH = "audio";
    public static final String SDCARD_APK_PATH = "apk";
    // 语音下载服务器地址 http://static2.iyuba.cn/sounds/toeic/1/01.mp3
    // 这里要换成托业的下载，暂时没有替换（下面的地址也有需要更换的）
    public static final String SERVER_PATH = "http://static2." + WebConstant.CN_SUFFIX + "sounds/toeic/";
    public static final String SERVER_VIP_PATH = "http://staticvip." + WebConstant.CN_SUFFIX + "sounds/toeic/";

    //请求头  警告 此项目中至少有4处这样的设置
    public static String IYBHttpHead = com.iyuba.configation.Constant.IYBHttpHead;//爱语吧统一请求地址头部，更新于2019.1.14 原为 "+com.iyuba.core.util.Constant.IYBHttpHead+"  '+Constant.IYBHttpHead+'
    public static String IYBHttpHead2 = com.iyuba.configation.Constant.IYBHttpHead2;  //"+com.iyuba.toeiclistening.util.Constant.IYBHttpHead+"


    // 语音的格式
    public static final String AUDIO_FORMATE = ".mp3";
    //播放音频时快进，前进的时间
    public static final int SEEK_NEXT = 5000;
    //播放音频时快退，后退的时间
    public static final int SEEK_PRE = -5000;
    //保存收藏单词发音的地址
    public static final String SDCARD_FAVWORD_AUDIO_PATH = "word";
    //训练模式
    public static final String EXERCISE_MODE = "exerciseMode";
    //应用名称
    public static final String APP_NAME = "托业听力";
    //首次载入的名称
    public static final String FIRST_LOAD = "firstLoad";
    //保持屏幕常亮名称
    public static final String KEEP_SCREEN_LIT = "keepScreenLit";
    //允许后台播放名称
    public static final String BACKGROUND_PLAY = "backgroundPlay";
    //允许左右滑动换题
    public static final String SLIDE_CHANGE_QUESTION = "slideChangeQuestion";
    //原文同步滚动
    public static final String TEXT_SYNS = "textSyns";
    //主题
    public static final String THEME_BACNGROUND = "themeBackground";
    //字体
    public static final String TEXTSIZE = "textSize";

    //字体大小
    public static final int TEXTSIZE_SMALL = 14;
    public static final int TEXTSIZE_MEDIUM = 18;
    public static final int TEXTSIZE_BIG = 22;

    //原文高亮颜色
    public static final String TEXTCOLOR = "textColor";
    //是否登录  0,没有登录（含注销），1已经登录，2以前登陆过并且没有注销
    public static final String ISLOGIN = "isLogin";
    public static final String AUTOLOGIN = "autoLogin";


    //需要修改

    //意见反馈的网址
    public final static String feedBackUrl = "http://api.IYBHttpHead/mobile/android/toeic/feedback.plain?uid=";
    public final static String APPID = "224";
    public final static String ADAPPID = "2241";
    public final static String APPNAME = "托业听力";
    public final static String searchUrl = "221";
    public final static String titleUrl = "";
    public final static String PAYAMOUNT = "100";
    public final static String APP_TYPE = "ToeicListening";
    //	http://static3."+IYBHttpHead+"/android/apk/ToeicListening/ToeicListening.apk
    //应用更新  网址
    public static String APP_UPDATE_PATH = "http://api." + IYBHttpHead + "/data/app/android-apk/ToeicListening.apk";
    public final static String TEST_SEVER_PATH = "http://m." + IYBHttpHead2 + "/toeic/main.jsp?";//试题所在网址
    public final static String PUBLIC_ACCOUNT_ID = "295451";//公共账号


    //public static int textColor = ConfigManager.Instance().loadInt("textColor");
//	public static int textSize = ConfigManager.Instance().loadInt("textSize");

}
