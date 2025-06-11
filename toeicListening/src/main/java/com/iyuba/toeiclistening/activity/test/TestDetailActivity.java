package com.iyuba.toeiclistening.activity.test;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.TestSendBean;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.module.toolbox.DensityUtil;
import com.iyuba.toeiclistening.BuildConfig;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.TitleQuestionAdapter;
import com.iyuba.toeiclistening.api.ApiRetrofit;
import com.iyuba.toeiclistening.api.ApiUpdateTestRecordInput;
import com.iyuba.toeiclistening.api.UpdateTestRecordInputBean;
import com.iyuba.toeiclistening.api.UpdateTestRecordInputBeanList;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.event.EvaluatePlayEvent;
import com.iyuba.toeiclistening.event.PlayAnimationEvent;
import com.iyuba.toeiclistening.event.ResetPlayDataEvent;
import com.iyuba.toeiclistening.event.TextParagraphEvent;
import com.iyuba.toeiclistening.listener.OnPlayStateChangedListener;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.mvp.model.NetWorkManager;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.model.bean.PdfBean;
import com.iyuba.toeiclistening.mvp.model.bean.TestRecordBean;
import com.iyuba.toeiclistening.mvp.presenter.TestDetailPresenter;
import com.iyuba.toeiclistening.mvp.view.TestDetailContract;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.NetWorkState;
import com.iyuba.toeiclistening.util.VipUtil;
import com.iyuba.toeiclistening.util.popup.MorePopup;
import com.iyuba.toeiclistening.util.popup.SpeedPopup;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.yd.saas.base.interfaces.AdViewBannerListener;
import com.yd.saas.base.interfaces.AdViewInterstitialListener;
import com.yd.saas.config.exception.YdError;
import com.yd.saas.ydsdk.YdBanner;
import com.yd.saas.ydsdk.YdInterstitial;

import org.greenrobot.eventbus.EventBus;
import org.litepal.util.LogUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;


/**
 * 新的试题内容显示页
 * 试题-试题列表- 试题详情主页面
 * by zh 2019.05.05
 */
public class TestDetailActivity extends AppCompatActivity
        implements TestDetailContract.TestDetailView, AdViewBannerListener {


    ImageButton ibBack;
    TextView tvTitle;
    ImageButton ibCollation;
    RelativeLayout rlToolbar;
    TabLayout tlTabLayout;
    ViewPager vpBody;
    TextView mTvProgress1;
    SeekBar seekBar;
    TextView mTvProgress2;
    RelativeLayout rlSeekBar;
    //上一题
    ImageButton prevTitleButton;
    RelativeLayout rlTitleBasePrev;
    //快退
    Button preSpeedButton;
    Button switchSPButton;
    //快进
    Button nextSpeedButton;
    //下一题
    ImageButton nextTitleButton;
    RelativeLayout rlTitleBaseNext;
    LinearLayout llTitleBasePlayControl;
    RelativeLayout mRlAd;
    ImageView mIvAd;
    TextView mTvCloseAd;

    private AdEntryBean.DataDTO dataDTO;
    private FragmentStatePagerAdapter mFragmentStatePagerAdapter;

    private int curPosition;        //当前听力在听力列表中的位置

    public TitleInfo curTitleInfo;            //当前题的信息
    private ZDBHelper zHelper;
    public ArrayList<Text> textList;        //Text实体的列表

    //是否是手动点击了下一题，如果是就不进行自动下一题，只在等待进入下一题时有效
    private boolean isLast = true;

    private MediaPlayer mediaPlayer;

    private Context mContext;
    private int currParagraph = 0;//播放进度
    public TEDBHelper teHelper;
    private int viewPagePosition;
    private int vip;
    private boolean isAudioPlay = true;
    //为添加数据上传增加字段
    private String uid;
    private String deviceId;
    private String endFlg;
    private String endTime;
    private String testTime;
    private String beginTime;
    private String lessonId;
    private String testNumber;
    private String answerResult;
    private List<TitleQuestionAdapter.DoQuestionCond> answerResultList;
    private String rightAnswer = "1";
    private String userAnswer;
    private String testWords;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String device;

    private QuestionFragment questionFragment;

    private List<Text> testSentences;

    private MorePopup morePopup;

    private SpeedPopup speedPopup;

    /**
     * 速度
     */
    private float speed = 1.0f;


    private TestDetailPresenter testDetailPresenter;

    private DecimalFormat decimalFormat;

    private YdInterstitial ydInterstitial;

    private int interstitialAdPos = 0;

    private void findView() {

        ibBack = findViewById(R.id.ib_back);
        tvTitle = findViewById(R.id.tv_title);
        ibCollation = findViewById(R.id.ib_collation);
        rlToolbar = findViewById(R.id.rl_toolbar);
        tlTabLayout = findViewById(R.id.tl_tab_layout);
        vpBody = findViewById(R.id.vp_body);
        mTvProgress1 = findViewById(R.id.tv_player_start);

        seekBar = findViewById(R.id.title_base_seekbar);
        mTvProgress2 = findViewById(R.id.tv_player_over);
        rlSeekBar = findViewById(R.id.rl_seek_bar);
        prevTitleButton = findViewById(R.id.title_base_prev);
        rlTitleBasePrev = findViewById(R.id.rl_title_base_prev);
        preSpeedButton = findViewById(R.id.title_base_prev_sen);

        switchSPButton = findViewById(R.id.title_base_pause_start);
        nextSpeedButton = findViewById(R.id.title_base_next_sen);
        nextTitleButton = findViewById(R.id.title_base_next);
        rlTitleBaseNext = findViewById(R.id.rl_title_base_next);
        llTitleBasePlayControl = findViewById(R.id.ll_title_base_play_control);
        mRlAd = findViewById(R.id.rl_ad);
        mIvAd = findViewById(R.id.photoImage);
        mTvCloseAd = findViewById(R.id.tv_close_ad);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_detail);

        try {
            device = URLDecoder.decode(android.os.Build.BRAND + android.os.Build.MODEL + android.os.Build.DEVICE, "utf-8");
            deviceId = "";//处理下target30以上会出现问题
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        decimalFormat = new DecimalFormat("#00");
        findView();

        testDetailPresenter = new TestDetailPresenter();
        testDetailPresenter.attchView(this);
        mContext = this;
        zHelper = new ZDBHelper(mContext);
        teHelper = new TEDBHelper(mContext);

        uid = AccountManagerLib.Instace(mContext).userId ==
                null ? "0" : AccountManagerLib.Instace(mContext).userId;
    }

    /**
     * 更多弹窗
     */
    private void initMorePopup(View view) {

        if (morePopup == null) {

            morePopup = new MorePopup(TestDetailActivity.this);
            morePopup.setCallback(new MorePopup.Callback() {
                @Override
                public void getString(String s) {

                    if (s.equals("收藏") || s.equals("已收藏")) {

                        if (curTitleInfo.Favorite) {
                            curTitleInfo.Favorite = false;
//                            ibCollation.setImageResource(R.drawable.ic_collection_off);//fav_no
                            Toast.makeText(mContext, "取消收藏!", Toast.LENGTH_SHORT).show();
                        } else {// 未收藏
                            curTitleInfo.Favorite = true;

//                            ibCollation.setImageResource(R.drawable.ic_collection_on);//fav_yes
                            Toast.makeText(mContext, "收藏成功!", Toast.LENGTH_SHORT).show();
                        }
                        zHelper.setFavoriteTitle(curTitleInfo.TitleNum, curTitleInfo.Favorite);
                        morePopup.dismiss();
                    } else if (s.equals("导出PDF")) {

                        getPdf();
                        morePopup.dismiss();
                    } else {//调速

                        String userID = ConfigManager.Instance().loadString("userId");
                        if (userID.equals("0") || userID.equals("")) {

                            startActivity(new Intent(TestDetailActivity.this, LoginActivity.class));
                            Toast.makeText(TestDetailActivity.this, "请登录", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (VipUtil.isVip()) {

                            initSpeedPopup();
                        } else {
                            alertBugVip();
                        }
                        morePopup.dismiss();
                    }
                }
            });
        }

        List<String> strings = new ArrayList<>();
        if (curTitleInfo != null && curTitleInfo.Favorite) {
            strings.add("已收藏");
        } else {
            strings.add("收藏");
        }
        strings.add("调速");
        strings.add("导出PDF");
        morePopup.initOperation(strings);
        morePopup.showPopupWindow(view);
    }

    /**
     * 获取pdf文件
     */
    private void getPdf() {

        if (curTitleInfo == null) {

            return;
        }

        String userID = ConfigManager.Instance().loadString("userId");
        if (userID.equals("0") || userID.equals("")) {

            startActivity(new Intent(TestDetailActivity.this, LoginActivity.class));
            Toast.makeText(TestDetailActivity.this, "请登录", Toast.LENGTH_SHORT).show();
            return;
        }

        if (VipUtil.isVip()) {

            NetWorkManager.getInstance().init();
            testDetailPresenter.getToeicPdfFile(curTitleInfo.TitleNum + "");
        } else {
            alertBugVip();
        }
    }

    private void alertBugVip() {

        AlertDialog alertDialog = new AlertDialog.Builder(TestDetailActivity.this)
                .setTitle("提示")
                .setMessage("此功能需要会员，是否跳转？")
                .setPositiveButton("去购买", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        startActivity(VipCenterGoldActivity.buildIntent(mContext, 0));
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * 音频调速的弹窗
     */
    private void initSpeedPopup() {

        if (speedPopup == null) {

            speedPopup = new SpeedPopup(TestDetailActivity.this);
            speedPopup.setBackgroundColor(Color.parseColor("#AA222222"));
            speedPopup.setCallback(new SpeedPopup.Callback() {
                @Override
                public void getChoose(float speedFloat) {

                    speed = speedFloat;
                    if (mediaPlayer != null) {

                        PlaybackParams params = new PlaybackParams();
                        params.setSpeed(speedFloat);
                        mediaPlayer.setPlaybackParams(params);
                    }
                    speedPopup.dismiss();
                }
            });
        }
        speedPopup.setChoosed(speed + "x");
        speedPopup.showPopupWindow();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        //Activity布局完成，彻底跑起来之后
        initData();
        initView();
        initAd();
        initListener();
        autoPlay();//音频初始化

    }

    private Handler adHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            initAd();
            return false;
        }
    });

    private void initData() {
        curPosition = getIntent().getIntExtra("position", 0);
        //curTitleInfo=zHelper.getTitleInfos(titleInfo.PackName, titleInfo.TestType);
        curTitleInfo = DataManager.Instance().titleInfoList.get(curPosition);

        textList = DataManager.Instance().textList;
        if (textList.size() == 0) {
            CustomToast.showToast(mContext, "数据为空");
            textList = teHelper.geTexts(curTitleInfo.TitleNum);
        }

        if (textList.size() == 0) {
            CustomToast.showToast(mContext, "数据库数据为空textList");
            return;
        }

        if (curTitleInfo.TestType == 101) {
            String textNum = curTitleInfo.PackName.substring(4);
            curTitleInfo.songPath = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                    + "/" + curTitleInfo.PackName + "/" + textList.get(0).Sound;
            curTitleInfo.webSongPath = com.iyuba.core.util.Constant.TOEIC_VOICE
                    + textNum + "/" + textList.get(0).Sound;
        } else if (curTitleInfo.TestType == 102) {
            curTitleInfo.songPath = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                    + "/" + "2019" + curTitleInfo.PackName + "/" + textList.get(0).Sound;
            curTitleInfo.webSongPath = com.iyuba.core.util.Constant.TOEIC_VOICE
                    + curTitleInfo.TitleNum / 1000 + "/" + textList.get(0).Sound;
        }
        LogUtil.d("path", curTitleInfo.songPath);
        LogUtil.d("webPath", curTitleInfo.webSongPath);
    }

    private void initView() {
        questionFragment = QuestionFragment.newInstance(curPosition);
        List<Fragment> fragments = new ArrayList<>();
        fragments.add(questionFragment);
        fragments.add(TextFragment.newInstance(curPosition));
        fragments.add(ReadEvaluateFragment.newInstance(curTitleInfo));
        fragments.add(RankFragment.newInstance());

        String[] titles = getResources().getStringArray(R.array.page_title_common);

        mFragmentStatePagerAdapter = new TestPagerAdapter(getSupportFragmentManager(), fragments, titles);
        vpBody.setAdapter(mFragmentStatePagerAdapter);
        vpBody.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                viewPagePosition = position;
                switch (position) {
                    case 1:
                        isLast = false;
                        handler.removeMessages(4);//不会自动播放了
                    case 0:
                        llTitleBasePlayControl.setVisibility(View.VISIBLE);
                        rlSeekBar.setVisibility(View.VISIBLE);
                        EventBus.getDefault().post(new EvaluatePlayEvent());
                        break;
                    case 3:
                        EventBus.getDefault().post(new EvaluatePlayEvent());
                    case 2:
                        llTitleBasePlayControl.setVisibility(View.GONE);
                        rlSeekBar.setVisibility(View.GONE);
                        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                            mediaPlayer.pause();
                        }
                        handler.removeMessages(4);//不会自动播放了
                        break;
                    default:
                }
            }

            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        tlTabLayout.setupWithViewPager(vpBody);
        vpBody.setCurrentItem(0);
        vpBody.setOffscreenPageLimit(4);

/*        if (curTitleInfo.Favorite) {
            ibCollation.setImageResource(R.drawable.ic_collection_on);
        } else {
            ibCollation.setImageResource(R.drawable.ic_collection_off);
        }*/

        setTitle();

    }

    private void setTitle() {
        String type = "Part" + String.valueOf(curTitleInfo.PartType).substring(2);
        // 初始化viewHolder里面控件的值
        if (curTitleInfo.TitleName.length() > 8) {
            tvTitle.setText(curTitleInfo.PackName + " - "
                    + type + " - " + curTitleInfo.TitleName.substring(8));
        } else {
            tvTitle.setText("???");
        }
    }


    /**
     * 请求插屏广告
     */
    private void requestAd() {

        String vipStatus = ConfigManager.Instance().loadString("vipStatus");
        String userid = ConfigManager.Instance().loadString("userId");
        if (vipStatus != null && !vipStatus.equals("0")) {

            return;
        }

        if (ydInterstitial != null) {

            ydInterstitial.destroy();
        }
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        String type;
        if (interstitialAdPos == 0) {

            type = BuildConfig.INTERSTITIAL_AD_KEY_CSJ;
        } else if (interstitialAdPos == 1) {

            type = BuildConfig.INTERSTITIAL_AD_KEY_YLH;
        } else {

            type = BuildConfig.INTERSTITIAL_AD_KEY_BD;
        }

        ydInterstitial = new YdInterstitial.Builder(TestDetailActivity.this)
                .setKey(type)
                .setWidth((int) (displayMetrics.widthPixels * 0.8))
                .setHeight((int) (displayMetrics.heightPixels * 0.5))
                .setInterstitialListener(new AdViewInterstitialListener() {
                    @Override
                    public void onAdReady() {

                        Timber.d("onAdReady");
                        ydInterstitial.show();
                    }

                    @Override
                    public void onAdDisplay() {

                        Timber.d("onAdDisplay");
                    }

                    @Override
                    public void onAdClick(String s) {

                        Timber.d("onAdClick");
                    }

                    @Override
                    public void onAdClosed() {

                        Timber.d("onAdClosed");
                    }

                    @Override
                    public void onAdFailed(YdError ydError) {

                        if (interstitialAdPos == 0) {

                            interstitialAdPos = 1;
                            requestAd();
                        } else if (interstitialAdPos == 1) {

                            interstitialAdPos = 2;
                            requestAd();
                        } else {

                            Timber.d(ydError.getMsg());
                        }
                    }
                })
                .build();
        ydInterstitial.requestInterstitial();
    }

    /**
     * 文章载入的时候就播放
     */
    public void autoPlay() {

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {


                requestAd();
            }
        });
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

                int curPos = mp.getDuration();
                int ts = curPos / 1000;
                int m = ts / 60;
                int s = ts % 60;
                mTvProgress2.setText(decimalFormat.format(m) + ":" + decimalFormat.format(s));
                mp.start();
                seekBar.setMax(mp.getDuration());

                beginTime = sdf.format(new Date()).replace(" ", "%20");
                handler.sendEmptyMessage(5);
            }
        });
        //player = new IJKPlayer();
        if (fileIsExists(curTitleInfo.songPath)) {
            try {
                mediaPlayer.setDataSource(curTitleInfo.songPath);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (new NetWorkState(mContext).isConnectingToInternet()) {
            try {
                mediaPlayer.setDataSource(curTitleInfo.webSongPath);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            CustomToast.showToast(mContext, "请检查网络连接！");
        }
        com.facebook.stetho.common.LogUtil.e("音频地址：" + curTitleInfo.songPath);
        com.facebook.stetho.common.LogUtil.e("音频地址web：" + curTitleInfo.webSongPath);
    }

    //判断文件是否存在
    public boolean fileIsExists(String strFile) {
        try {
            File f = new File(strFile);
            if (!f.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* if (!player.isPlaying() && viewPagePosition != 3 && viewPagePosition != 2) {
            player.play();
            Date date = new Date();
            beginTime = sdf.format(date).replace(" ", "%20");
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (ydInterstitial != null) {

            ydInterstitial.destroy();
        }

        if (handler != null) {
            handler.removeMessages(5);
        }
        dataPassenger();
        ArrayList<TitleInfo> favTitleInfo = zHelper.getFavTitleInfos();
        DataManager.Instance().favTitleInfoList = favTitleInfo;
        if (mediaPlayer != null) {
            mediaPlayer.release();//停止播放音频
            Date date = new Date();
            endTime = sdf.format(date).replace(" ", "%20");
            //player.pause();//停止播放音频
        }
        if (adHandler != null) {

            adHandler.removeMessages(1);
        }

        if (testDetailPresenter != null) {

            testDetailPresenter.detachView();
        }

    }

    /**
     * 播放下一篇文章,上一篇文章，更新数据
     *
     * @param position 第几篇文章
     */
    public void refreshData(int position, boolean isNest) {
        //更新数据
        currParagraph = 0;

        curTitleInfo = DataManager.Instance().titleInfoList.get(position);
        textList = teHelper.geTexts(curTitleInfo.TitleNum);

        setTitle();

        String textNum = curTitleInfo.PackName.substring(4);

        if (curTitleInfo.TestType == 101) {
            curTitleInfo.songPath = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                    + "/" + curTitleInfo.PackName + "/"
                    + textList.get(0).Sound;

            curTitleInfo.webSongPath = com.iyuba.core.util.Constant.TOEIC_VOICE
                    + textNum + "/" + textList.get(0).Sound;
        } else if (curTitleInfo.TestType == 102) {//适配新题 2019
            curTitleInfo.songPath = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
                    + "/" + "2019" + curTitleInfo.PackName + "/" + textList.get(0).Sound;

            curTitleInfo.webSongPath = com.iyuba.core.util.Constant.TOEIC_VOICE
                    + curTitleInfo.TitleNum / 1000 + "/" + textList.get(0).Sound;
        }
        seekBar.setProgress(0);

//        if (curTitleInfo.Favorite) {
//            ibCollation.setImageResource(R.drawable.ic_collection_on);
//        } else {
//            ibCollation.setImageResource(R.drawable.ic_collection_off);
//        }


        //通知Fragment 更新试题数据
        EventBus.getDefault().post(new ResetPlayDataEvent(position, isNest));
    }

    private void initListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {//人拉动的，则更新进度条和音频
                    //转换为音频的位置
                    int curPosition = mediaPlayer.getDuration() * progress / seekBar.getMax();
                    //int curPosition = curTitleInfo.SoundTime * progress / seekBar.getMax();
                    mediaPlayer.seekTo(curPosition);//总时长
                }
                int second = mediaPlayer.getCurrentPosition() / 1000;//进度

                currParagraph = getParagraph(second);

                if (viewPagePosition == 1) {//当前在Text
                    handler.sendEmptyMessage(2);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //下一题 ， 没有处理暴力点击 ，可能会出问题
        nextTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPassenger();

                beginTime = sdf.format(new Date()).replace(" ", "%20");
//                handler.sendEmptyMessage(8);
                if (mediaPlayer.isPlaying() || isLast) {
                    isAudioPlay = true;
                } else {
                    isAudioPlay = false;
                }
                isLast = false;

                if (curPosition < DataManager.Instance().titleInfoList.size() - 1) {
                    curPosition += 1;
                    refreshData(curPosition, true);
                    if (fileIsExists(curTitleInfo.songPath)) {

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(curTitleInfo.songPath);
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (new NetWorkState(mContext).isConnectingToInternet()) {

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(curTitleInfo.webSongPath);
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        CustomToast.showToast(mContext, "请检查网络连接！");
                    }
                    isLast = false;
                    nextTitleButton.setImageResource(R.drawable.ic_lesson_next);
                    prevTitleButton.setImageResource(R.drawable.button_pres_one);

                } else {
                    CustomToast.showToast(mContext, "已经是最后一题了", 1000);
                    nextTitleButton.setImageResource(R.drawable.button_nexts_one);
                }
            }
        });

        prevTitleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataPassenger();
//                handler.sendEmptyMessage(8);

                if (mediaPlayer.isPlaying()) {
                    isAudioPlay = true;
                } else {
                    isAudioPlay = false;
                }

                if (curPosition > 0) {//不是第一篇
                    //更新到上一篇文章的数据
                    curPosition -= 1;
                    refreshData(curPosition, false);

                    if (fileIsExists(curTitleInfo.songPath)) {


                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(curTitleInfo.songPath);
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else if (new NetWorkState(mContext).isConnectingToInternet()) {

                        try {
                            mediaPlayer.reset();
                            mediaPlayer.setDataSource(curTitleInfo.webSongPath);
                            mediaPlayer.prepareAsync();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        CustomToast.showToast(mContext, "请检查网络连接！");
                    }
                    com.facebook.stetho.common.LogUtil.e("播放地址！" + curTitleInfo.songPath);
                    isLast = false;
                    prevTitleButton.setImageResource(R.drawable.button_pres_one);
                    nextTitleButton.setImageResource(R.drawable.ic_lesson_next);
                } else {
                    CustomToast.showToast(mContext, "已经是第一题了", 1000);
                    prevTitleButton.setImageResource(R.drawable.ic_lesson_pre);
                }
            }
        });

        preSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    switchSPButton.setBackgroundResource(R.drawable.ic_player_play);
                }
                if (mediaPlayer != null) {

                    PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
                    float speed = (float) (playbackParams.getSpeed() - 0.1);
                    playbackParams.setSpeed(speed);
                }
            }
        });

        nextSpeedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    switchSPButton.setBackgroundResource(R.drawable.ic_player_play);
                }
                if (mediaPlayer != null) {

                    PlaybackParams playbackParams = mediaPlayer.getPlaybackParams();
                    float speed = (float) (playbackParams.getSpeed() + 0.1);
                    playbackParams.setSpeed(speed);
                }
            }
        });

        switchSPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mediaPlayer.isPlaying()) {
                    isAudioPlay = false;
                    mediaPlayer.pause();
                    switchSPButton.setBackgroundResource(R.drawable.button_play);
                    EventBus.getDefault().post(new PlayAnimationEvent(0));
                    if (questionFragment != null) {

                        questionFragment.animation(0);
                    }
                } else {

                    isAudioPlay = true;
                    mediaPlayer.start();
                    Date date = new Date();
                    beginTime = sdf.format(date).replace(" ", "%20");
                    isLast = false;//禁止自动下一题
                    if (isAudioPlay) {
                        //更新播放器开关的图标
                        switchSPButton.setBackgroundResource(R.drawable.button_pause);
                        EventBus.getDefault().post(new PlayAnimationEvent(1));//1为动画开始，0为结束
                    }
                    //player.isAudioPlay=true;
                }
            }
        });

        ibCollation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                initMorePopup(v);
            }
        });

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTvCloseAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRlAd.setVisibility(View.GONE);
            }
        });
    }


    private OnPlayStateChangedListener onPlayStateChangedListener = new OnPlayStateChangedListener() {

        @Override
        public void setPlayTime(String currTime, String allTime) {
            handler.sendEmptyMessage(5);
        }

        @Override
        public void playSuccess() {

            Date date = new Date();
            beginTime = sdf.format(date).replace(" ", "%20");
            if (isAudioPlay) {
                //更新播放器开关的图标
                switchSPButton.setBackgroundResource(R.drawable.button_pause);
                EventBus.getDefault().post(new PlayAnimationEvent(1));//1为动画开始，0为结束
            }
        }

        @Override
        public void playStop() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            EventBus.getDefault().post(new PlayAnimationEvent(0));

        }

        @Override
        public void playResume() {
            if (isAudioPlay) {
                //更新播放器开关的图标
                switchSPButton.setBackgroundResource(R.drawable.button_pause);
                EventBus.getDefault().post(new PlayAnimationEvent(1));
            }
        }

        @Override
        public void playPause() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            EventBus.getDefault().post(new PlayAnimationEvent(0));
            if (questionFragment != null) {

                questionFragment.animation(0);
            }
        }

        @Override
        public void playFaild() {
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            EventBus.getDefault().post(new PlayAnimationEvent(0));
        }

        @Override
        public void playCompletion() {
            //if (isAudioPlay) {
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            if (viewPagePosition == 0) {
                isLast = true;
                handler.sendEmptyMessageDelayed(4, 7000);
                EventBus.getDefault().post(new PlayAnimationEvent(0));
            }

            if (questionFragment != null) {

                questionFragment.animation(0);
            }
        }
    };

    /**
     * 原文同步的handler
     */
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {

            switch (msg.what) {
                case 2://Text上原文同步
                    EventBus.getDefault().post(new TextParagraphEvent(currParagraph));
                    break;
                case 4:
                    if (isLast) {//如果是自动点击才走
                        nextTitleButton.performClick();//自动点击
                        isLast = true;
                    }
                    break;
                case 5:

                    if (mediaPlayer != null) {

                        int curPos = mediaPlayer.getCurrentPosition();
                        int ts = curPos / 1000;
                        int m = ts / 60;
                        int s = ts % 60;

                        mTvProgress1.setText(decimalFormat.format(m) + ":" + decimalFormat.format(s));
                        seekBar.setProgress(curPos);
                    }
                    handler.sendEmptyMessageDelayed(5, 200);
                    //必须获取数据时间，音频文件时间错误！！
//                    mTvProgress2.setText(mediaPlayer.getAudioAllTime(curTitleInfo.SoundTime));
                    break;
                case 6:
                    boolean isLine = new NetWorkState(mContext).isConnectingToInternet(); //有问题
                    com.facebook.stetho.common.LogUtil.e("网络：" +
                            CheckNetWork.isNetworkAvailable(mContext) + " " + isLine);
                    CustomToast.showToast(mContext, "网络异常，请检查网络！！！", 800);
                    break;
                case 7:
                    switchSPButton.performClick();
                    break;
            }
            return false;
        }
    });

    /**
     * 由目前播放到的时间在texts时间点里面查找那段对话的位置
     *
     * @param second 目前播放到的时间
     * @return
     */
    public int getParagraph(int second) {
        int step = 0;
        if (textList != null && textList.size() != 0) {
            for (int i = 0; i < textList.size(); i++) {
                if (second >= textList.get(i).Timing) {
                    step = i + 1;
                } else {
                    break;
                }
            }
        }
        return step;
    }

    private void initAd() {
        mRlAd.setVisibility(View.GONE);
        vip = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
        String userId = ConfigManager.Instance().loadString("userId");
        if (vip == 0) {//!= 1 如果不是VIP开始加载广告，

            testDetailPresenter.getAdEntryAll(Constant.ADAPPID, 4, userId);
        }
    }

    private void dataPassenger() {

        if (mediaPlayer.getCurrentPosition() == mediaPlayer.getDuration()) {
            endFlg = "1";
        } else {
            endFlg = "0";
        }
        Date date = new Date();
        endTime = sdf.format(date).replace(" ", "%20");
        testTime = endTime;
        lessonId = String.valueOf(curTitleInfo.TitleNum);
        int currTime = 0;
        currTime = mediaPlayer.getCurrentPosition();
        if (currTime != 0) {
           /* String[] splitCurrTime = currTime.split(":");
            int sum = 0;
            if (splitCurrTime[0].equals("")) {
                sum = Integer.parseInt(splitCurrTime[1]);
            } else {
                sum = Integer.parseInt(splitCurrTime[0]) * 60 + Integer.parseInt(splitCurrTime[1]);
            }*/

            int sum = currTime / 1000;
            testNumber = String.valueOf(sum);

            zHelper.setStudyTime(curTitleInfo.TitleNum, sum);
        } else {
            zHelper.setStudyTime(curTitleInfo.TitleNum, 0);
            testNumber = String.valueOf(0);
        }


        testSentences = teHelper.geTexts(curTitleInfo.TitleNum);
        ArrayList<String> wordsCounter = new ArrayList();

        for (int i = 0; i < testSentences.size(); i++) {
            for (int j = 0; j < testSentences.get(i).Sentence.split(" ").length; j++) {
                wordsCounter.add(testSentences.get(i).Sentence.split(" ")[j]);
            }
        }

        testWords = String.valueOf(wordsCounter.size());

        //上传听力进度接口

        String begin = beginTime.split(" ")[0];
        String sign2 = MD5.getMD5ofStr(uid + begin + endTime);
        testDetailPresenter.updateStudyRecordNew("json", uid, beginTime, endTime, "toeic",
                "1", testWords, "android", Constant.APPID, "", lessonId,
                sign2, Integer.parseInt(endFlg), Integer.parseInt(testNumber), 1);

        //上传做题进度接口
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        String postdate = sdf.format(new Date());
        String sign = MD5.getMD5ofStr(uid + "iyubaTest" + postdate);

        //多个问题回答情况列表
        answerResultList = questionFragment.adapter.getResult();
        Timber.e("answerResultListsize:---" + answerResultList.size());


        //总对题数
        if (questionFragment.adapter.getRightNum() == 1) {
            userAnswer = "1";
            answerResult = "1";
        } else if (questionFragment.adapter.getRightNum() == 0) {
            userAnswer = "0";
            answerResult = "0";
        } else {
            userAnswer = String.valueOf(questionFragment.adapter.getRightNum());
        }

        //网络请求body赋值

        UpdateTestRecordInputBean updateTestRecordInputBean = new UpdateTestRecordInputBean();
        UpdateTestRecordInputBeanList uploadList = new UpdateTestRecordInputBeanList();
        List<UpdateTestRecordInputBean> updateTestRecordInputBeanList = new ArrayList<>();

        updateTestRecordInputBean.setUid(String.valueOf(uid));
        updateTestRecordInputBean.setUserAnswer(userAnswer);
        updateTestRecordInputBean.setTestTime(testTime);
        updateTestRecordInputBean.setTestNumber(testNumber);
        updateTestRecordInputBean.setRightAnswer(rightAnswer);
        updateTestRecordInputBean.setLessonId(lessonId);
        updateTestRecordInputBean.setBeginTime(beginTime);
        updateTestRecordInputBean.setAppName("ToeicListening");
        Timber.e("answerResultList:---" + answerResult);
        updateTestRecordInputBean.setAnswerResut(answerResult);
        updateTestRecordInputBeanList.add(updateTestRecordInputBean);
        uploadList.datalist = updateTestRecordInputBeanList;

        //网络请求头部

        uploadList.DeviceId = deviceId;
        uploadList.uid = String.valueOf(uid);
        uploadList.sign = sign;


        //网络请求构造
        ApiUpdateTestRecordInput apiUpdateTestRecordInput = ApiRetrofit.getInstance().getApiUpdateTestRecordInput();
        apiUpdateTestRecordInput.sendTestRecord(getBody(uploadList)).enqueue
                (new Callback<TestSendBean>() {
                    @Override
                    public void onResponse(Call<TestSendBean> call, Response<TestSendBean> response) {
                        try {
                            String result = response.body().getResultCode();
                            Timber.e("UpdateTestResult" + result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(Call<TestSendBean> call, Throwable t) {
                        t.printStackTrace();
                        Timber.e("UpdateTestFailure " + t);
                    }
                });

        Timber.e("ApiUpdateTestRecordInput.AnswerResult" + updateTestRecordInputBean.getAnswerResut());
        Timber.e("ApiUpdateTestRecordInput.UserAnswer" + updateTestRecordInputBean.getUserAnswer());
        Timber.e("ApiUpdateTestRecordInput.apiUpdateTestRecordInput" + apiUpdateTestRecordInput);

    }

    //网络请求body构造
    private RequestBody getBody(UpdateTestRecordInputBeanList item) {
        Gson gson = new Gson();
        RequestBody body = null;

        try {
            String json = gson.toJson(item);
            body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
//            return null;
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

        Toast.makeText(TestDetailActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void getToeicPdfFile(PdfBean pdfBean) {


        new AlertDialog.Builder(TestDetailActivity.this)
                .setTitle("导出pdf")
                .setMessage("地址：" + com.iyuba.toeiclistening.Constant.URL_APPS + "/iyuba" + pdfBean.getPath())
                .setPositiveButton("打开", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Uri uri = Uri.parse(com.iyuba.toeiclistening.Constant.URL_APPS + "/iyuba" + pdfBean.getPath());
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("关闭并复制到剪切板", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //获取剪贴板管理器：
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        // 创建普通字符型ClipData
                        ClipData mClipData = ClipData.newPlainText("Label", com.iyuba.toeiclistening.Constant.URL_APPS + "/iyuba" + pdfBean.getPath());
                        // 将ClipData内容放到系统剪贴板里。
                        cm.setPrimaryClip(mClipData);
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void getAdEntryAllComplete(AdEntryBean adEntryBean) {


        dataDTO = adEntryBean.getData();
        String type = dataDTO.getType();
        if (type.equals("web")) {

            deleteOtherAdView();
            mRlAd.setVisibility(View.VISIBLE);
            mIvAd.setVisibility(View.VISIBLE);
            mTvCloseAd.setVisibility(View.VISIBLE);
            Glide.with(TestDetailActivity.this).load("http://static3.iyuba.cn/dev" + dataDTO.getStartuppic()).into(mIvAd);
            adHandler.sendEmptyMessageDelayed(1, 20000);
        } else if (type.startsWith("ads")) {

            dealAds(type);
        }
    }


    /**
     * 处理广告
     *
     * @param type
     */
    private void dealAds(String type) {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = DensityUtil.dp2px(TestDetailActivity.this, 65);

        String adKey = null;
        if (type.equals(com.iyuba.toeiclistening.Constant.AD_ADS2)) {

            adKey = BuildConfig.BANNER_AD_KEY_CSJ;
        } else if (type.equals(com.iyuba.toeiclistening.Constant.AD_ADS4)) {

            adKey = BuildConfig.BANNER_AD_KEY_YLH;
        }
        if (adKey != null) {

            YdBanner mBanner = new YdBanner.Builder(TestDetailActivity.this)
                    .setKey(adKey)
                    .setWidth(width)
                    .setHeight(height)
                    .setMaxTimeoutSeconds(5)
                    .setBannerListener(this)
                    .build();

            mBanner.requestBanner();

            Log.d("banner___", adKey);
        }
    }

    @Override
    public void showUpdateStudyRecord(TestRecordBean testRecordBean) {

        if (testRecordBean.getReward() != null && !testRecordBean.getReward().equals("0")) {

            int reward = Integer.parseInt(testRecordBean.getReward());
            double rewardDouble = reward / 100.0f;
            DecimalFormat decimalFormat = new DecimalFormat("#.##");


            new AlertDialog.Builder(TestDetailActivity.this)
                    .setTitle("恭喜您！")
                    .setMessage("本次学习获得" + decimalFormat.format(rewardDouble) + "元红包奖励,已自动存入您的钱包账户。\n红包可在【爱语吧】微信公众号提现，继续学习领取更多红包奖励吧！")
                    .setPositiveButton("好的", (dialog, which) -> {

                        dialog.dismiss();
                    })
                    .show();
        }
    }

    /**
     * 删除其他view
     */
    public void deleteOtherAdView() {

        int count = mRlAd.getChildCount();
        for (int i = 0; i < count; ) {

            View view1 = mRlAd.getChildAt(i);
            if (view1 != null && view1.getId() != R.id.photoImage && view1.getId() != R.id.tv_close_ad) {

                mRlAd.removeView(view1);
            } else {
                i++;
            }
        }
    }

    @Override
    public void onReceived(View view) {

        deleteOtherAdView();
        mIvAd.setVisibility(View.GONE);
        mTvCloseAd.setVisibility(View.GONE);

        mRlAd.addView(view);
        mRlAd.setVisibility(View.VISIBLE);
        Log.d("banner111", "onReceived");

        adHandler.sendEmptyMessageDelayed(1, 20000);
    }

    @Override
    public void onAdExposure() {

        Log.d("banner111", "onAdExposure");
    }

    @Override
    public void onAdClick(String s) {

        Log.d("banner111", "onAdClick");
    }

    @Override
    public void onClosed() {

        mRlAd.setVisibility(View.GONE);
        Log.d("banner111", "onClosed");
    }

    @Override
    public void onAdFailed(YdError ydError) {

        if (dataDTO != null && dataDTO.getTitle() != null && !dataDTO.getTitle().trim().equals("")) {

            dealAds(dataDTO.getTitle());
            dataDTO = null;
        }
        Log.d("banner111", "onAdFailed" + ydError.toString());
    }
}
