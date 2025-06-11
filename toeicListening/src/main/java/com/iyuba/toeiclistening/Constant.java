package com.iyuba.toeiclistening;

public class Constant {

    //下载音频的存储位置
    public static String APP_DATA_PATH = null;

    public final static String APPID = "224";

    public final static String ADAPPID = "2241";

    public static String DOMAIN = "iyuba.cn";

    public static String DOMAIN_LONG = "iyuba.com.cn";

    public static String API_URL = "http://api." + DOMAIN;

    public static String IUSERSPEECH_URL = "http://iuserspeech." + DOMAIN + ":9001";


    public static String API_COM_CN_URL = "http://api." + DOMAIN_LONG;


    public static String STATIC1_URL = "http://static1." + DOMAIN;


    public static String DAXUE_URL = "http://daxue." + DOMAIN;


    public static String URL_DAXUE_I = "http://daxue." + DOMAIN;

    public static String VOA_URL = "http://voa." + DOMAIN;

    public static String URL_VIP = "http://vip." + DOMAIN;

    public static String URL_Q_API = "http://api." + DOMAIN;

    public static String URL_API = "http://api." + DOMAIN;

    public static String URL_APPS = "http://apps." + DOMAIN;

    public static String URL_APP = "http://app." + DOMAIN;

    public static String URL_M = "http://m." + DOMAIN;

    public static String URL_M_QOMOLAMA = "http://m." + DOMAIN;

    public static String URL_AI = "http://ai." + DOMAIN;

    public static String URL_VOA = "http://voa." + DOMAIN;


    public static String URL_DEV = "http://dev." + DOMAIN;

    /**
     * 获取pdf文件
     */
    //https://apps.iyuba.cn/iyuba/getToeicPdfFile.jsp
    public static String GET_TOEIC_PDF_FILE = URL_APPS + "/iyuba/getToeicPdfFile.jsp";


    /**
     * 广告接口
     */
    public static String GET_AD_ENTRY_ALL = URL_DEV + "/getAdEntryAll.jsp";

    /**
     * 上传听力记录（学习）
     */
    public static String UPDATE_STUDY_RECORD_NEW = DAXUE_URL + "/ecollege/updateStudyRecordNew.jsp";


    /**
     * 我的钱包记录
     */
    public static String GET_USER_ACTION_RECORD = URL_API + "/credits/getuseractionrecord.jsp ";

    //广告
    public static final String AD_ADS1 = "ads1";//倍孜
    public static final String AD_ADS2 = "ads2";//创见
    public static final String AD_ADS3 = "ads3";//头条穿山甲
    public static final String AD_ADS4 = "ads4";//广点通优量汇
    public static final String AD_ADS5 = "ads5";//快手

}
