package com.iyuba.configation;

import android.os.Environment;

/**
 * 程序中常用的常量
 *
 * @author 魏申鸿
 */
public class Constant {

    //请求头
    public static String IYBHttpHead = "iyuba.cn";
    public static String IYBHttpHead2 = "iyuba.com.cn";

    //微课升级地址
    public final static String appUpdateUrl = "http://api." + Constant.IYBHttpHead + "/mobile/android/jlpt1/islatest.plain?currver=";//升级地址
    public final static int IO_BUFFER_SIZE = 100 * 1024;
    public final static String MOB_CLASS_PACK_BGPIC = "http://static3." + Constant.IYBHttpHead + "/resource/categoryIcon/";
    public final static String appfile = "bible4";//更新时的前缀名
    public final static String MicroClassReqPackId = "1";
    public final static String reqPackDesc = "class.jlpt1";
    public final static int price = 999;//应用内终身价格
    //移动课堂所需的相关API
    public final static String MOB_CLASS_DOWNLOAD_PATH = "http://static3." + Constant.IYBHttpHead + "/resource/";
    public final static String MOB_CLASS_PAYEDRECORD_PATH = "http://app." + Constant.IYBHttpHead + "/pay/apiGetPayRecord.jsp?";
    public final static String MOB_CLASS_PACK_IMAGE = "http://static3." + Constant.IYBHttpHead + "/resource/packIcon/";
    public final static String MOB_CLASS_PACK_TYPE_IMAGE = "http://static3." + Constant.IYBHttpHead + "/resource/nmicon/";
    //日志音频地址 ，非VIP
    public static final String AUDIO_ADD = WebConstant.HTTP_STATIC + Constant.IYBHttpHead + "/sounds";
    //日志音频地址 ，VIP
    public static final String AUDIO_VIP_ADD = "http://staticvip." + Constant.IYBHttpHead + "/sounds";

    //日志视频地址 ，VIP
    public static final String VIDEO_VIP_ADD = "http://staticvip." + Constant.IYBHttpHead + "/video";

    public final static String AppName = "ToeicListening";//爱语吧承认的英文缩写
    public final static String APPName = "托业听力";//正式名称
    public static String envir;//文件夹路径
    public final static String envir1 = Environment
            .getExternalStorageDirectory() + "/iyuba/";//文件夹路径

    public final static String screenShotAddr = envir + "/screenshot.jpg";//截图位置
    public static final String WECHAT_APP_KEY = "wx4b3175639bf0a681";
    public static final String WECHAT_APP_SECRET = "4cf14d7f09c8c284a172918ca6b8ae89";
    public static final String QQ_APP_KEY = "101740087";
    public static final String QQ_APP_SECRET = "2f4e9206b08538e5b279a5e33a26e946";
    public static final String SINA_APP_KEY = "1825345706";
    public static final String SINA_APP_SECRET = "dd86e5a6e41fd00ecd895083ee5cac0c";

    public static String simRecordAddress;//不能越级创建文件夹！

    // 听歌中用
    public final static String append = ".mp3";//文件append
    public final static String recordAddr = envir + "/sound.amr";//跟读音频
    public final static String voiceCommentAddr = envir + "/voicecomment.amr";//语音评论


    public final static String addCreditsUrl = "http://api." + Constant.IYBHttpHead + "/credits/updateScore.jsp?";
    public static String AppIcon = "http://app." + Constant.IYBHttpHead + "/android/images/ToeicListening/ToeicListening.png";


    //训练模式

    public final static String SMSAPPKEY = "1bf0126d663c5";
    public final static String SMSAPPSECRET = "b1701bd733609e44604aed1dbb60ea3e";
    public static int mode;//播放模式
    public static int type;//听歌播放模式
    public static int download;//是否下载
    public static int recordId;//学习记录篇目id，用于主程序
    public static String recordStart;//学习记录开始时间，用于主程序

    public static final String PIC_ABLUM__ADD = "http://static1." + IYBHttpHead + "/data/attachment/album/";

    // 程序存放在sd卡上地址
    public static final String APP_DATA_PATH = Environment
            .getExternalStorageDirectory() + "/com.iyuba.toeiclistening/";
    // mp3文件存放sd卡上文件夹
    public static final String SDCARD_AUDIO_PATH = "audio";
    public static final String EXERCISE_MODE = "exerciseMode";
    //应用名称
    public static final String APP_NAME = "托业听力";

    //意见反馈的网址
    public final static String feedBackUrl = "http://api." + IYBHttpHead + "/mobile/android/toeic/feedback.plain?uid=";
    public final static String APPID = "224";//托业听力
    public final static String APP_TYPE = "ToeicListening";
    public final static String TOPICID = "toeic";
    // <!-- 托业听力黄金会员productId 15-->


    public final static String videoAddr = APP_DATA_PATH + "/audio";//音频文件存储位置

    public static boolean isPreVerifyDone = false;

    public static String getEvaluateAddress() {
        return simRecordAddress = envir + "/sound";
    }
}
