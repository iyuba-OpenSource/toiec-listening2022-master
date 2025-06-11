package com.iyuba.toeiclistening.activity;

import static com.iyuba.configation.Constant.APPID;
import static com.iyuba.configation.Constant.envir;
import static com.iyuba.configation.Constant.isPreVerifyDone;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.BuildConfig;
import com.iyuba.core.common.activity.Web;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.util.ReadBitmap;
import com.iyuba.core.common.util.SaveImage;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.http.LOGUtils;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.api.AiyubaAdvApi;
import com.iyuba.toeiclistening.api.AiyubaAdvResult;
import com.iyuba.toeiclistening.entity.DownTest;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.listener.OnFinishedListener;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.setting.SettingConfig;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.TEDBManager;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBManager;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.CopyFileToSD;
import com.iyuba.toeiclistening.util.SDCard;
import com.mob.secverify.PreVerifyCallback;
import com.mob.secverify.SecVerify;
import com.mob.secverify.common.exception.VerifyException;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.utils.ToastFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 欢迎页面！
 */
@RuntimePermissions
public class IniActivity extends Activity {
    private Context mContext;
    private ZDBManager zManager;
    private TEDBManager tedbManager;
    private ZDBHelper zdbHelper;
    private TEDBHelper teHelper;
    private ImageView ad, base;

    private boolean isclickad = false;
    private int recLen = 5;
    private Timer timer = new Timer();
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            if (!isclickad)
                recLen--;
            if (recLen >= 0)
                handler.sendEmptyMessage(5);
        }
    };
    private Button btn_skip;
    private static boolean isGoOut = false;
    private int lastVersion, currentVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ini);


        ad = (ImageView) findViewById(R.id.ad);
        base = (ImageView) findViewById(R.id.base);
        btn_skip = (Button) findViewById(R.id.btn_skip);


        mContext = this;
//		//这两行必不可少  在网络请求的时候 需要用上这两个
        RuntimeManager.setApplication(getApplication());
        RuntimeManager.setApplicationContext(mContext);
        RuntimeManager.setDisplayMetrics(this);
        preVerify();

        try {
            //这个是软件版本
            currentVersion = getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
            lastVersion = com.iyuba.configation.ConfigManager.Instance().loadInt("version");

        } catch (Exception e) {

            LogUtil.e("异常", e.toString());
            lastVersion = 0;
            e.printStackTrace();
        }

        initKaiPing();
        iniData();

        int startCount = ConfigManager.Instance().loadInt("startCount", 0);
        ConfigManager.Instance().putInt("startCount", startCount + 1);

        handler.sendEmptyMessage(1);// 引入数据库

        btn_skip = (Button) findViewById(R.id.btn_skip);
        btn_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacksAndMessages(null);
                handler.sendEmptyMessage(6);
                btn_skip.setClickable(false);//只能点一次
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    iniDb();
                    break;
                case 2:
                    iniAudio();
                    break;
                case 3:
                    checkFiles();
                case 4:
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                        IniActivityPermissionsDispatcher.initLocationWithPermissionCheck(IniActivity.this);
//                    } else {
//                        getUid(); //设置临时账户
//                    }
//
                    LogUtil.e("文件校验完毕，可以进行跳转");
                    isGoOut = true;
                    if (timer != null) {
                        timer.schedule(timerTask, 1000, 1000);
                    }
                    break;
                case 5:
                    btn_skip.setText("跳过(" + recLen + "s)");
                    if (recLen < 1) {
                        handler.sendEmptyMessage(6);
                    }
                    break;
                case 6:
                    Intent intent3 = new Intent(IniActivity.this, MainActivity.class);
                    intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent3.putExtra("isFirstInfo", 0);
                    startActivity(intent3);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    public void iniData() {
        if (ConfigManagerMain.Instance().loadInt(Constant.FIRST_LOAD) == 0) {
            ConfigManagerMain.Instance().putInt(Constant.FIRST_LOAD, 0);
            ConfigManagerMain.Instance().putBoolean(Constant.KEEP_SCREEN_LIT, true);
            ConfigManagerMain.Instance().putBoolean(Constant.BACKGROUND_PLAY, true);
            ConfigManagerMain.Instance().putBoolean(Constant.SLIDE_CHANGE_QUESTION, true);
            ConfigManagerMain.Instance().putBoolean(Constant.TEXT_SYNS, true);
            ConfigManagerMain.Instance().putInt(Constant.THEME_BACNGROUND,
                    mContext.getResources().getColor(R.color.background1));
            ConfigManagerMain.Instance().putInt(Constant.TEXTSIZE,
                    Constant.TEXTSIZE_MEDIUM);
            ConfigManagerMain.Instance().putInt(Constant.TEXTCOLOR,
                    mContext.getResources().getColor(R.color.skyBlue));
            ConfigManagerMain.Instance().putInt(Constant.ISLOGIN,
                    0);
            ConfigManagerMain.Instance().putInt(Constant.EXERCISE_MODE, 0);
            ConfigManagerMain.Instance().putBoolean(Constant.AUTOLOGIN, true);

            if (currentVersion > lastVersion) {//三星 欢迎商店图违规
                com.iyuba.configation.ConfigManager.Instance().putInt("version", currentVersion);
                Intent intent2 = new Intent(IniActivity.this, HelpUseActivity.class);
                intent2.putExtra("isFirstInfo", 0);
                startActivityForResult(intent2, 2);

                timer.cancel();
                timerTask.cancel();
                timer.purge();
                timer = null;
            }
        }
        //只要用户没有登录  都不是VIP 开始都初始化为非vip
        SettingConfig.Instance().setVip(0);
        SettingConfig.Instance().setIyubaAmount("0");
    }

    public void iniDb() {
        tedbManager = new TEDBManager(mContext);//toeiclistening.sqlite
        tedbManager.setVersion(1, 2);//！！！！！！ 10.25 更新！1-->2
        tedbManager.openDatabase();
        tedbManager.closeDatabase();
//        zManager = new ZDBManager(mContext);
//        zManager.openDatabase();
//        zManager.closeDatabase();

        //导入lib库中raw的lib_database数据库使用
        ImportLibDatabase dbLib = new ImportLibDatabase(mContext);
        dbLib.setPackageName(mContext.getPackageName());
        dbLib.setVersion(1, 2);// 有需要数据库更改使用

        ZDBManager zdbManager = new ZDBManager(mContext);//zzaidb.sqlite
        zdbManager.setVersion(2, 3);//！！！！！！
        zdbManager.openDatabase();

        File fDbFile = new File(dbLib.getDBPath());
        if (!fDbFile.exists()) {
            dbLib.openDatabase(dbLib.getDBPath());
        }

        handler.sendEmptyMessageDelayed(2, 1500);
    }

    public void iniAudio() {
        if (SDCard.hasSDCard()) {
            try {
                CopyFileToSD copyFileToSD = new CopyFileToSD(mContext,
                        Constant.ASSETS_AUDIO_PATH, com.iyuba.toeiclistening.Constant.APP_DATA_PATH
                        + Constant.SDCARD_AUDIO_PATH,
                        new OnFinishedListener() {

                            @Override
                            public void onFinishedListener() {

                                handler.sendEmptyMessage(4);// 开始main
                            }

                            @Override
                            public void onErrorListener() {
                                // TODO Auto-generated method stub
                                handler.sendEmptyMessage(4);// 导入文件失败

                            }
                        });
                LogUtil.d("********音频文件拷贝成功！", "音频文件拷贝成功！");
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            //handler.sendEmptyMessage(0);// 没有sd卡提示
            CustomToast.showToast(mContext, "没有sd卡");
            handler.sendEmptyMessage(4);// 导入文件失败
        }
    }

    /**
     * 检查文件的下载进度
     * <p>
     * 通过比较当前SD卡中的每个题包中的文件数（Answer+Text的总数）与
     * 数据库中存放的Text和Answer总数比较来判断下载进度
     */
    public void checkFiles() {
        zdbHelper = new ZDBHelper(mContext);
        teHelper = new TEDBHelper(mContext);
        ArrayList<PackInfo> packInfos = zdbHelper.getPackInfo();    //数据库中所有的套题
        for (int i = 0; i < packInfos.size(); i++) {
            PackInfo packInfo = packInfos.get(i);                    //得到其中某一套题的信息
            if (packInfo.Progress == 1) {                            //判断下载进度是否是完成状态
                String fileDir = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                        + "/" + packInfo.PackName;
                File file = new File(fileDir);
                int packNum = zdbHelper.getDownPackNum(packInfo.PackName);
                ArrayList<DownTest> arrayList = teHelper.getDownTests(packInfo.PackName, packNum, packInfo.TestType);
                //Text和Answer的总数（作为评判下载是否完成的一个对比数字）
                int fileSum = teHelper.getDownTestsNum(packInfo.PackName, packInfo.TestType);
                if (file.exists()) {
                    String[] filesStrings = file.list();
                    float progress = filesStrings.length / (float) fileSum;
                    if (progress != packInfo.Progress) {
                        //如果计算出来的进度和之前数据库中存放的进度不统一，则重新设置
                        zdbHelper.setProgress(packInfo.PackName, progress, true);
                    }
                }
                //文件不存在设置下载进度为0
                else {
                    zdbHelper.setProgress(packInfo.PackName, 0, false);
                }
            }
        }
        handler.sendEmptyMessage(4);
    }

    private boolean CheckNetWork() {
        if (NetWorkState.isConnectingToInternet() && NetWorkState.getAPNType() != 1) {
            return true;
        }
        return false;

    }

    //开屏广告
    private void initKaiPing() {

        if (CheckNetWork()) {
            Retrofit retrofit;
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(3, TimeUnit.SECONDS)
                    .build();
            AiyubaAdvApi advApi = null;
            retrofit = new Retrofit.Builder().
                    client(client).
                    addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(AiyubaAdvApi.BASEURL).build();
            advApi = retrofit.create(AiyubaAdvApi.class);

            LogUtil.e("tag--adurl", "http://app." + com.iyuba.core.util.Constant.IYBHttpHead + "/dev/getAdEntryAll.jsp?uid=0" + "&appId=" + APPID + "&flag="
                    + AiyubaAdvApi.KPFLAG);
            advApi.getAdvByaiyuba("0", APPID, AiyubaAdvApi.KPFLAG).enqueue(new Callback<List<AiyubaAdvResult>>() {
                @Override
                public void onResponse(Call<List<AiyubaAdvResult>> call, Response<List<AiyubaAdvResult>> response) {
                    if (response.isSuccessful()) {
                        final AiyubaAdvResult result = response.body().get(0);
                        LogUtil.e("Tag-", result.getResult());
                        if (result.getResult().equals("1")) {
                            LogUtil.e("Tag--result", result.getData().getStartuppic());
                            if ("youdao".equals(result.getData().getType())) {
                                showYoudaoAd();
                                LogUtil.e("TAG--Ad", "youdao");
                            } else if ("web".equals(result.getData().getType())) {
                                LogUtil.e("Tag-web-myadv", result.getData().getStartuppic_Url());
                                if (ad != null && mContext != null && !isFinishing()) {
                                    Glide.with(mContext).load("http://static3." + Constant.IYBHttpHead + "/dev/" +
                                            result.getData().getStartuppic()).into(ad);
                                    ad.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            startActivityForResult(new Intent(mContext, Web.class)
                                                    .putExtra("url", result.getData().getStartuppic_Url()), 1);
//                                            startActivity(new Intent(mContext, Web.class)
//                                                    .putExtra("url", result.getData().getStartuppic_Url()));
                                        }
                                    });
                                }
                            } else {
                                showYoudaoAd();
                                LogUtil.e("Tag--ad", "youdao");
                            }
                        } else {
                            LogUtil.e("Tag--ad", "youdao");
                            showYoudaoAd();
                        }
                    } else {
                        LogUtil.e("Tag--ad", "youdao");
                        showYoudaoAd();
                    }
                }

                @Override
                public void onFailure(Call<List<AiyubaAdvResult>> call, Throwable t) {
                    LogUtil.e("Tag--errot", "广告自己的" + t.toString());
                    showYoudaoAd();
                }
            });
        } else {
            ad.setOnClickListener(v -> {
                if (!ConfigManager.Instance().loadString("startuppic_Url").equals("")) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, WebActivity.class);
                    intent.putExtra("url", ConfigManager.Instance().loadString("startuppic_Url"));
                    LogUtil.e("InitActivity url ", ConfigManager.Instance().loadString("startuppic_Url"));
                    startActivityForResult(intent, 1);
                    handler.removeCallbacksAndMessages(null);
                    finish();
                }
            });

            int screenWidth = ((Activity) mContext).getWindowManager()
                    .getDefaultDisplay().getWidth();
            int screenHeight = ((Activity) mContext).getWindowManager()
                    .getDefaultDisplay().getHeight();
            double screenRatio = (screenHeight * 0.2627) / screenWidth;

            base.setImageBitmap(ReadBitmap.readBitmap(mContext,
                    R.drawable.head_default));//引导页
            //base
            screenRatio = (screenHeight * 0.86) / screenWidth;


            try {
                File adPic = new File(envir + "/ad/ad.jpg");
                if (adPic.exists() && mContext != null) {
                    ad.setImageBitmap(SaveImage.resizeImage(ReadBitmap
                                    .readBitmap(mContext, new FileInputStream(adPic)),
                            screenRatio));
                }

            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        return;
    }

    private void showYoudaoAd() {
        if (mContext != null) {
            YouDaoNative youdaoNative = new YouDaoNative(mContext, "9755487e03c2ff683be4e2d3218a2f2b",
                    new YouDaoNative.YouDaoNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(final NativeResponse nativeResponse) {
                            List<String> imageUrls = new ArrayList<>();
                            imageUrls.add(nativeResponse.getMainImageUrl());
                            ad.setOnClickListener(v -> {
                                nativeResponse.handleClick(ad);
                                isclickad = true;
                            });
                            if (mContext != null && ad != null && !isFinishing())
                                ImageService.get(mContext, imageUrls, new ImageService.ImageServiceListener() {
                                    @TargetApi(Build.VERSION_CODES.KITKAT)
                                    @Override
                                    public void onSuccess(final Map<String, Bitmap> bitmaps) {
                                        if (nativeResponse.getMainImageUrl() != null) {
                                            Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
                                            if (bitMap != null) {
                                                ad.setImageBitmap(bitMap);
                                                nativeResponse.recordImpression(ad);
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFail() {

                                    }
                                });

                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {
                            LogUtil.e("onNativeFail", "onNativeFail" + nativeErrorCode.toString());

                        }
                    });


            RequestParameters requestParameters = RequestParameters.builder()
                    .build();
            youdaoNative.makeRequest(requestParameters);
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        IniActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
        //getUid();
    }

    @NeedsPermission(Manifest.permission.READ_PHONE_STATE)
    public void initLocation() {
        //用户给了权限会调用这个
    }

    @OnPermissionDenied(Manifest.permission.READ_PHONE_STATE)
    public void locationDenied() {
        CustomToast.showToast(IniActivity.this, "权限请求失败，临时账号错误!\n请使用正式账号");
    }

    public static long getTimeDate() {
        Date date = new Date();
        long unixTimestamp = date.getTime() / 1000 + 3600 * 8; //东八区;
        long days = unixTimestamp / 86400;
        return days;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (timer != null) {
            timer.cancel();
            timerTask.cancel();
            timer.purge();
            timer = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                //进行跳转
                handler.sendEmptyMessage(6);
                break;
            case 2:
                finish();
                break;
            default:
        }
    }

    /**
     * 建议提前调用预登录接口，可以加快免密登录过程，提高用户体验
     */
    private void preVerify() {
        //设置在1000-10000之内
        SecVerify.setTimeOut(5000);
        //移动的debug tag 是CMCC-SDK,电信是CT_ 联通是PriorityAsyncTask
        SecVerify.setDebugMode(true);
//        SecVerify.setUseCache(true);
        SecVerify.preVerify(new PreVerifyCallback() {
            @Override
            public void onComplete(Void data) {
                if (BuildConfig.DEBUG) {
                    ToastFactory.showShort(mContext, "预登录成功");
                }
                isPreVerifyDone = true;
                LOGUtils.i("预登录成功");
                SecVerify.autoFinishOAuthPage(false);
//                SecVerify.setUiSettings(CustomizeUtils.customizeUi());
            }

            @Override
            public void onFailure(VerifyException e) {
                isPreVerifyDone = false;
                String errDetail = null;
                if (e != null) {
                    errDetail = e.getMessage();
                }
                LOGUtils.i("onFailure errDetail " + errDetail);
                if (BuildConfig.DEBUG) {
                    // 登录失败
                    LOGUtils.i("preVerify failed");
                    // 错误码
                    int errCode = e.getCode();
                    // 错误信息
                    String errMsg = e.getMessage();
                    // 更详细的网络错误信息可以通过t查看，请注意：t有可能为null
                    String msg = "错误码: " + errCode + "\n错误信息: " + errMsg;
                    if (!TextUtils.isEmpty(errDetail)) {
                        msg += "\n详细信息: " + errDetail;
                    }
                    LOGUtils.i(msg);
//                    ToastFactory.showShort(mContext, msg);
                }
            }
        });
    }
}
