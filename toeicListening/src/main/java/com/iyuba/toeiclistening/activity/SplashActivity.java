package com.iyuba.toeiclistening.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.startup.AppInitializer;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.util.Log;
import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.toeiclistening.AppInit;
import com.iyuba.toeiclistening.BuildConfig;
import com.iyuba.toeiclistening.Constant;
import com.iyuba.toeiclistening.MyWebActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.activity.base.BaseActivity;
import com.iyuba.toeiclistening.databinding.ActivitySplashBinding;
import com.iyuba.toeiclistening.mvp.model.NetWorkManager;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.presenter.SplashPresenter;
import com.iyuba.toeiclistening.mvp.view.SplashContract;
import com.iyuba.toeiclistening.sqlite.TEDBManager;
import com.iyuba.toeiclistening.sqlite.ZDBManager;
import com.iyuba.toeiclistening.util.popup.PrivacyPopup;
import com.yd.saas.base.interfaces.AdViewSpreadListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdSpread;

import java.io.File;

/**
 * 启动页面
 */
public class SplashActivity extends BaseActivity<SplashContract.SplashView, SplashContract.SplashPresenter>
        implements SplashContract.SplashView, AdViewSpreadListener {

    private PrivacyPopup privacyPopup;

    private ActivitySplashBinding binding;

    private AdEntryBean.DataDTO dataDTO;

    private boolean isAdCLick = false;

    private CountDownTimer webTimer;

    /**
     * 是否请求了title的广告
     */
    private boolean isRequestTitle = false;

    private YdSpread mSplashAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dealWindow();

        NetWorkManager.getInstance().init();

        iniDb();

        boolean pState = ConfigManager.Instance().loadBoolean("PrivacyDialog", false);
        if (!pState) {

            initPrivacyPopup();
        } else {
            AppInitializer.getInstance(SplashActivity.this).initializeComponent(AppInit.class);

            int userid = Integer.valueOf(AccountManagerLib.Instace(SplashActivity.this).userId);
            presenter.getAdEntryAll(Constant.ADAPPID + "", 1, userid + "");
            countDownTimer.start();
        }
    }


    public void iniDb() {

        TEDBManager tedbManager = new TEDBManager(SplashActivity.this);//toeiclistening.sqlite
        tedbManager.setVersion(1, 3);//！！！！！！ 10.25 更新！1-->2
        tedbManager.openDatabase();
        tedbManager.closeDatabase();
//        zManager = new ZDBManager(mContext);
//        zManager.openDatabase();
//        zManager.closeDatabase();

        //导入lib库中raw的lib_database数据库使用
        ImportLibDatabase dbLib = new ImportLibDatabase(SplashActivity.this);
        dbLib.setPackageName(getPackageName());
        dbLib.setVersion(1, 2);// 有需要数据库更改使用

        ZDBManager zdbManager = new ZDBManager(SplashActivity.this);//zzaidb.sqlite
        zdbManager.setVersion(2, 3);//！！！！！！
        zdbManager.openDatabase();

        File fDbFile = new File(dbLib.getDBPath());
        if (!fDbFile.exists()) {
            dbLib.openDatabase(dbLib.getDBPath());
        }
    }

    @Override
    public View initLayout() {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        return binding.getRoot();
    }

    @Override
    public SplashContract.SplashPresenter initPresenter() {
        return new SplashPresenter();
    }

    private void initPrivacyPopup() {

        if (privacyPopup == null) {

            privacyPopup = new PrivacyPopup(this);
            privacyPopup.setCallback(new PrivacyPopup.Callback() {
                @Override
                public void yes() {

                    AppInitializer.getInstance(SplashActivity.this).initializeComponent(AppInit.class);

                    ConfigManager.Instance().putBoolean("PrivacyDialog", true);
                    privacyPopup.dismiss();

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }

                @Override
                public void no() {

                    privacyPopup.dismiss();
                    finish();
                }

                @Override
                public void user() {

                    MyWebActivity.startActivity(SplashActivity.this, WebConstant.HTTP_SPEECH_ALL + "/api/protocoluse666.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=爱语吧", "用户协议");
                }

                @Override
                public void privacy() {

                    MyWebActivity.startActivity(SplashActivity.this, WebConstant.HTTP_SPEECH_ALL + "/api/protocolpri.jsp?apptype=" + com.iyuba.configation.Constant.APPName + "&company=1", "隐私政策");
                }
            });
        }
        privacyPopup.showPopupWindow();
    }


    /**
     * 处理状态栏和虚拟返回键
     */
    private void dealWindow() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.R) {

            WindowInsetsController windowInsetsController = getWindow().getInsetsController();
            windowInsetsController.hide(WindowInsets.Type.systemBars());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        } else {

            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isAdCLick) {//点击了就直接跳转mainactivity

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();


        if (mSplashAd != null) {

            mSplashAd.destroy();
        }

        if (countDownTimer != null) {

            countDownTimer.cancel();
        }
    }

    /**
     * 计时器
     */
    CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {


            if (dataDTO == null) {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }
    };

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(ToeicApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getAdEntryAllComplete(AdEntryBean adEntryBean) {

        dataDTO = adEntryBean.getData();
        String type = dataDTO.getType();
        if (type.equals("web")) {

            dealAdWeb();
        } else if (type.startsWith("ads")) {

            dealAds(type);
        } else {

            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        }
    }


    /**
     * 处理web
     */
    private void dealAdWeb() {

        binding.splashFlContent.removeAllViews();
        View adView = LayoutInflater.from(SplashActivity.this).inflate(R.layout.splash_web, null);
        ImageView sw_iv_pic = adView.findViewById(R.id.sw_iv_pic);
        TextView sw_tv_jump = adView.findViewById(R.id.sw_tv_jump);
        binding.splashFlContent.addView(adView);

        Glide.with(adView.getContext())
                .load("http://dev.iyuba.cn/" + dataDTO.getStartuppic())
                .into(sw_iv_pic);
        sw_tv_jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (webTimer != null) {

                    webTimer.cancel();
                }

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        });
        adView.setOnClickListener(view -> {

            if (dataDTO == null) {
                return;
            }
            if (dataDTO.getType().equals("web")) {

                if (!dataDTO.getStartuppicUrl().trim().equals("")) {

                    MyWebActivity.startActivity(SplashActivity.this, dataDTO.getStartuppicUrl(), "详情");
                }
            }
        });
        webTimer = new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long l) {

                sw_tv_jump.setText("跳转(" + (l / 1000) + "s)");
            }

            @Override
            public void onFinish() {

                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                finish();
            }
        }.start();
    }


    /**
     * 获取开屏广告
     *
     * @param type
     */
    private void dealAds(String type) {

        String adKey = null;
        if (type.equals(Constant.AD_ADS1)) {

            adKey = BuildConfig.SPLASH_AD_KEY_BZ;
        } else if (type.equals(Constant.AD_ADS2)) {

            adKey = BuildConfig.SPLASH_AD_KEY_CSJ;
        } else if (type.equals(Constant.AD_ADS3)) {

            adKey = BuildConfig.SPLASH_AD_KEY_BD;
        } else if (type.equals(Constant.AD_ADS4)) {

            adKey = BuildConfig.SPLASH_AD_KEY_YLH;
        } else if (type.equals(Constant.AD_ADS5)) {

            adKey = BuildConfig.SPLASH_AD_KEY_KS;
        }
        if (adKey != null) {

            mSplashAd = new YdSpread.Builder(SplashActivity.this)
                    .setKey(adKey)
                    .setContainer(binding.splashFlContent)
                    .setSpreadListener(this)
                    .setCountdownSeconds(4)
                    .setSkipViewVisibility(true)
                    .build();

            mSplashAd.requestSpread();
            android.util.Log.d("adadad", "name:" + adKey);
        } else {//没有key的时候使用默认的广告

            dealAdWeb();
        }
    }

    @Override
    public void onAdDisplay() {
        Log.d("adadad", "onAdDisplay");
    }

    @Override
    public void onAdClose() {

        startActivity(new Intent(SplashActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onAdClick(String s) {

        isAdCLick = true;
        Log.d("adadad", "onAdClick");
    }

    @Override
    public void onAdFailed(YdError ydError) {

        if (!isRequestTitle) {

            isRequestTitle = true;
            dealAds(dataDTO.getTitle());
        } else {

            dealAdWeb();
        }
        Log.d("adadad", "onAdFailed");
    }
}