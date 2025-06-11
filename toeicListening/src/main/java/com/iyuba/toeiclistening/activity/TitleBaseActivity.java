package com.iyuba.toeiclistening.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.util.GetLocation;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.TitleQuestionAdapter;
import com.iyuba.toeiclistening.entity.Answer;
import com.iyuba.toeiclistening.entity.Explain;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.listener.OnPlayStateChangedListener;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.CheckNetWork;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.NetWorkState;
import com.iyuba.toeiclistening.util.Player;
import com.iyuba.toeiclistening.widget.WordCard;
import com.iyuba.toeiclistening.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.widget.subtitle.SubtitleSynViewMain;
import com.iyuba.toeiclistening.widget.subtitle.TextPage;
import com.iyuba.toeiclistening.widget.subtitle.TextPageSelectTextCallBack;
import com.youdao.sdk.nativeads.ImageService;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import permissions.dispatcher.PermissionUtils;

/**
 * 旧的试题详情展示页面
 * 已经废弃，不再使用
 */
public class TitleBaseActivity extends Activity {
    // 全局变量
    private Context mContext;
    private int curPosition;        //当前听力在听力列表中的位置
    private int currParagraph = 0;

    private TEDBHelper teHelper;
    private ZDBHelper zHelper;

    private int curView = 0;                //=0 tq =1 t =2 q界面
    private ArrayList<Answer> answerList;    //Answer实体的列表
    private ArrayList<Text> textList;        //Text实体的列表
    private TitleInfo curTitleInfo;            //当前某道题的信息
    private Answer curAnswer;
    private TitleQuestionAdapter adapter;    // 问题列表的适配器

    private Animation lefinAnimation;
    private Animation rightoutAnimation;
    private Animation leftoutAnimation;
    private Animation rightinAnimation;

    //母板界面
    private Button backButton;
    private ImageView favTitleImageView;
    private ImageView switchTQImageView;
    private TextView titleNameTextView;
    private ViewFlipper container;            //切换页面
    private WordCard wordCard;
//	private Button shaerImageView;

    // 播放器相关变量
    private Button prevTitleButton;
    private Button preSpeedButton;// 快退
    private Button switchSPButton;
    private Button nextSpeedButton;// 快进
    private Button nextTitleButton;
    private SeekBar seekBar;
    private Player player;
    private PowerManager powerManager;
    private WakeLock wakeLock;

    // title_tq界面的变量
    private ListView answerListViewTQ;
    private SubtitleSynViewMain currentTextViewTQ;
    private ImageView ivCurrentTableTQ;


    // title_t界面的变量
    private SubtitleSynViewMain currentTextViewT = null;
    // title_q界面的变量
    private ListView answerListViewQ;
    private Button btn_explain;
    private Explain explain;

    // Explain界面的变量
    private TextPage explain_text;

    //是否是手动点击了下一题，如果是就不进行自动下一题，只在等待进入下一题时有效
    private boolean isLast = true;

    private TextView mTvProgress1, mTvProgress2;
    private RelativeLayout mRlPrev, mRlNext;
    //private IJKPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.title_base);
        mContext = this;

        init();

        findView();
        setView();
        addListener();
        autoPlay();//播放音频
    }

    @Override
    protected void onDestroy() {
        TitleInfo titleInfo = DataManager.Instance().titleInfoList.get(curPosition);
        //更新数据
        ArrayList<TitleInfo> titleInfosList = zHelper
                .getTitleInfos(titleInfo.PackName, titleInfo.TestType);
        ArrayList<TitleInfo> favTitleInfos = zHelper.getFavTitleInfos();
        // 更新DataManager和数据库中titleInfo的RightNum的值
        int rightNum = adapter.getRightNum();
        DataManager.Instance().titleInfoList = titleInfosList;
        DataManager.Instance().favTitleInfoList = favTitleInfos;
        if (curPosition <= DataManager.Instance().titleInfoList.size())//越界异常
            DataManager.Instance().titleInfoList.get(curPosition).RightNum = rightNum;
        zHelper.setRightNum(curTitleInfo.TitleNum, rightNum);
        if (player != null) {
            player.stop();//停止播放音频
            //player.pause();//停止播放音频
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!player.isPlaying()) {
            //player.play();
            //player.start();
        }
        //设置屏幕常亮
        if (ConfigManagerMain.Instance().loadBoolean(Constant.KEEP_SCREEN_LIT)) {
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "my :Lock");
            wakeLock.acquire();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!ConfigManagerMain.Instance().loadBoolean(Constant.BACKGROUND_PLAY)) {
            if (player != null) {
                player.pause();
            }
        }

        if (wakeLock != null && wakeLock.isHeld()) {
            wakeLock.release();//关闭屏幕常亮
            wakeLock = null;
        }
    }

    /**
     * 初始化值
     */
    public void init() {
        mContext = this;
        Intent intent = this.getIntent();
        curPosition = intent.getIntExtra("position", -1);
        curTitleInfo = DataManager.Instance().titleInfoList.get(curPosition);

        lefinAnimation = AnimationUtils.loadAnimation(
                this, R.anim.slide_right_in);
        rightoutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_right_out);
        leftoutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_left_out);
        rightinAnimation = AnimationUtils.loadAnimation(mContext, R.anim.slide_left_in);
        upInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.up_in);
        upOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.up_out);
        downInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.down_in);
        downOutAnimation = AnimationUtils.loadAnimation(mContext, R.anim.down_out);

        teHelper = new TEDBHelper(mContext);
        zHelper = new ZDBHelper(mContext);
        answerList = DataManager.Instance().anwserList;
        curAnswer = answerList.get(answerList.size() - 1);//答案列表

        textList = DataManager.Instance().textList;
        explain = DataManager.Instance().explain;

        for (int i = 0; i < textList.size(); i++) {
            //Log.d("$$$$$$$$$测试查看原文Text内容：", textList.get(i).Sentence);
        }


        adapter = new TitleQuestionAdapter(mContext, answerList, tp);
        //地址=Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH/包名/titileNum_sound.mp3
//        curTitleInfo.songPath = Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH
//                + "/" + curTitleInfo.PackName + "/" + textList.get(0).Sound;

        String textNum = curTitleInfo.PackName.substring(4);
        if (curTitleInfo.TestType == 101) {
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
        System.out.println(curTitleInfo.songPath + "-----------------------");

        curView = com.iyuba.configation.ConfigManager.Instance().loadInt(com.iyuba.configation.Constant.EXERCISE_MODE);

    }

    public void findView() {
        initAd();
        backButton = (Button) this.findViewById(R.id.title_base_back);
        btn_explain = (Button) this.findViewById(R.id.btn_explain);
        titleNameTextView = (TextView) this.findViewById(R.id.title_base_title_text);
        favTitleImageView = (ImageView) this.findViewById(R.id.title_base_fav_title);
        switchTQImageView = (ImageView) this
                .findViewById(R.id.title_base_switch_mode);
//		shaerImageView=(Button)findViewById(R.id.share);
        prevTitleButton = (Button) this.findViewById(R.id.title_base_prev);//上一题
        if (curPosition == 0) {
            prevTitleButton.setBackgroundResource(R.drawable.ic_lesson_pre);
        }

        preSpeedButton = (Button) this.findViewById(R.id.title_base_prev_sen);//快退
        switchSPButton = this.findViewById(R.id.title_base_pause_start);//播放开关
        nextSpeedButton = (Button) this.findViewById(R.id.title_base_next_sen);//快进
        nextTitleButton = (Button) this.findViewById(R.id.title_base_next);
        seekBar = (SeekBar) this.findViewById(R.id.title_base_seekbar);
        mTvProgress1 = (TextView) this.findViewById(R.id.tv_player_start);
        mTvProgress2 = (TextView) this.findViewById(R.id.tv_player_over);

        container = (ViewFlipper) this.findViewById(R.id.title_base_container);

        answerListViewTQ = (ListView) this.findViewById(R.id.title_tq_list);
        currentTextViewTQ = (SubtitleSynViewMain) this.findViewById(R.id.title_tq_textpage);

        //currentTextViewTQ=new SubtitleSynView(mContext);
        answerListViewQ = (ListView) this.findViewById(R.id.title_q_list);
        currentTextViewT = (SubtitleSynViewMain) this
                .findViewById(R.id.title_t_textpage);
        wordCard = (WordCard) this.findViewById(R.id.title_base_wordcard);
        explain_text = (TextPage) this.findViewById(R.id.explain_text);

        mRlPrev = this.findViewById(R.id.rl_title_base_prev);
        mRlNext = this.findViewById(R.id.rl_title_base_next);
//		}
    }

    private void initAd() {
        final View adView = findViewById(R.id.youdao_ad);
        final ImageView photoImage = findViewById(R.id.photoImage);
        adView.setVisibility(View.GONE);
        int vip = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
        if (isTime()) {
            vip = 1;
        }
        if (vip == 0) {//!= 1 如果不是VIP开始加载广告，
            YouDaoNative youdaoNative = new YouDaoNative(mContext, "230d59b7c0a808d01b7041c2d127da95",
                    new YouDaoNative.YouDaoNativeNetworkListener() {
                        @Override
                        public void onNativeLoad(final NativeResponse nativeResponse) {
                            List<String> imageUrls = new ArrayList<>();
                            imageUrls.add(nativeResponse.getMainImageUrl());
                            adView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    nativeResponse.handleClick(adView);
                                }
                            });
                            ImageService.get(mContext, imageUrls, new ImageService.ImageServiceListener() {
                                @TargetApi(Build.VERSION_CODES.KITKAT)
                                @Override
                                public void onSuccess(final Map<String, Bitmap> bitmaps) {
                                    if (nativeResponse.getMainImageUrl() != null) {
                                        Bitmap bitMap = bitmaps.get(nativeResponse.getMainImageUrl());
                                        if (bitMap != null) {
                                            photoImage.setImageBitmap(bitMap);
                                            photoImage.setVisibility(View.VISIBLE);
                                            nativeResponse.recordImpression(photoImage);
                                        }
                                    }
                                }

                                @Override
                                public void onFail() {
                                }
                            });
                            adView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onNativeFail(NativeErrorCode nativeErrorCode) {
                            LogUtil.e("有道广告请求失败,onNativeFail" + nativeErrorCode);//returned empty response.
                            adView.setVisibility(View.GONE);
                        }
                    });
            Location location = new Location("appPos");
            GetLocation getLocation = GetLocation.getInstance(mContext);

            String latitude;
            String longitude;
            LogUtil.e("地理位置权限", PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION) + "");
            if (PermissionUtils.hasSelfPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Pair<Double, Double> locations = GetLocation.getLocation(mContext);
                latitude = String.valueOf(locations.first);
                longitude = String.valueOf(locations.second);
            } else {
                latitude = "0";
                longitude = "0";
            }

            //String[] locations = getLocation.getLocation();
            location.setLatitude(Double.parseDouble(latitude));
            location.setLongitude(Double.parseDouble(longitude));
            location.setAccuracy(100);

            RequestParameters requestParameters = RequestParameters.builder()
                    .location(location).build();
            youdaoNative.makeRequest(requestParameters);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    public void setView() {
        titleNameTextView.setText(curTitleInfo.TitleName);
        if (curTitleInfo.Favorite) {
            favTitleImageView.setImageResource(R.drawable.ic_collection_on);//fav_yes
        } else {
            favTitleImageView.setImageResource(R.drawable.ic_collection_off);//fav_no
        }

        //TQ界面上的原文同步,开始载入TQ界面
        currentTextViewTQ.setTextList(textList);
        currentTextViewTQ.setTpstcb(tp);
        currentTextViewTQ.initSubtitleOneSectence();

//        if (curTitleInfo.PartType==404&&!curAnswer.QuesImage.equals("")){
//           ivCurrentTableTQ.setVisibility(View.VISIBLE);
//
//            String imageNameString=curAnswer.QuesImage;
//            String imagePathString= Constant.ASSETS_IMAGE_PATH+"/"+imageNameString;
//            Bitmap bitmap=ImageUtil.getImageFromAssetsFile(mContext, imagePathString);
//            LogUtil.e("显示表格图片路径：", imagePathString);
//
//            if(bitmap!=null){
//                LogUtil.e("ggggggggg", bitmap.getWidth()+"  "+bitmap.getHeight());
//                bitmap=ImageUtil.zoomBitmap(bitmap, bitmap.getWidth()*2,bitmap.getHeight()*2);
//            }
//
//            ivCurrentTableTQ.setImageBitmap(bitmap);
//        }else {
//            ivCurrentTableTQ.setVisibility(View.GONE);
//        }

        //初始化T界面上的同步原文
        currentTextViewT.setTextList(textList);
        currentTextViewT.setTpstcb(tp);
        currentTextViewT.initSubtitleSum();

        //explain界面的初始化
        explain_text.setText(explain.Explain);

        container.setDisplayedChild(curView);
        container.setInAnimation(lefinAnimation);
        container.setOutAnimation(rightoutAnimation);
        if (ConfigManagerMain.Instance().loadBoolean(Constant.SLIDE_CHANGE_QUESTION)) {

        }
    }

    /**
     * 添加监听器
     */
    public void addListener() {
        backButton.setOnClickListener(backOnClickListener);
        btn_explain.setOnClickListener(explainOnClickListener);//解析？
        favTitleImageView.setOnClickListener(favTitleOnClickListener);
        switchTQImageView.setOnClickListener(switchTQOnClickListener);
        mRlPrev.setOnClickListener(preTitleOnClickListener);//上一题 prevTitleButton
        prevTitleButton.setOnClickListener(preTitleOnClickListener);//上一题
        preSpeedButton.setOnClickListener(preSpeedOnClickListener);
        switchSPButton.setOnClickListener(switchSpOnClickListener);
        nextSpeedButton.setOnClickListener(nextSpeedOnClickListener);
        mRlNext.setOnClickListener(nextTitleOnClickListener);//下一题 nextTitleButton
        nextTitleButton.setOnClickListener(nextTitleOnClickListener);//下一题
        seekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        answerListViewTQ.setAdapter(adapter);
        answerListViewQ.setAdapter(adapter);
        //shaerImageView.setOnClickListener(shareClickListener);
    }
    /**
     *
     * 分享按钮的监听器
     *
     */
//	private OnClickListener shareClickListener=new OnClickListener() {
//		
//		@Override
//		public void onClick(View arg0) {
//			// TODO Auto-generated method stub
//			String text = "我正在用android版"
//				+ Constant.APP_NAME
//				+ "背托业听力，里面有套试题不错。地址："+Constant.TEST_SEVER_PATH
//				+"titleNum="+curTitleInfo.TitleNum+"&queNum="+curTitleInfo.QuesNum;
//		Intent shareInt = new Intent(Intent.ACTION_SEND);
//		shareInt.setType("text/*");
//		shareInt.putExtra(Intent.EXTRA_TEXT, text);
//		shareInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		shareInt.putExtra("sms_body", text);
//		startActivity(Intent.createChooser(shareInt, "选择分享方式"));
//		}
//	};
    /**
     * 返回按钮的监听器
     */
    private OnClickListener backOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // TODO Auto-generated method stub
            // 由于此title有修改，重新对titleList赋值
            finish();
        }
    };


    //收藏题目
    private OnClickListener favTitleOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            // 已经收藏
            if (curTitleInfo.Favorite) {
                curTitleInfo.Favorite = false;
                ((ImageView) arg0).setImageResource(R.drawable.ic_collection_off);//fav_no
                Toast.makeText(mContext, "取消收藏!", Toast.LENGTH_SHORT).show();
            } else {// 未收藏
                curTitleInfo.Favorite = true;
                ((ImageView) arg0).setImageResource(R.drawable.ic_collection_on);//fav_yes
                Toast.makeText(mContext, "收藏成功!", Toast.LENGTH_SHORT).show();
            }
            zHelper.setFavoriteTitle(curTitleInfo.TitleNum, curTitleInfo.Favorite);
        }
    };

    private OnClickListener switchTQOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            switch (curView) {
                case 0://t界面
                    switchTQImageView.setImageResource(R.drawable.text_none);
//                    container.showNext();
                    container.setDisplayedChild(1);
                    curView = 1;
                    break;
                case 1://q界面
                    switchTQImageView.setImageResource(R.drawable.text_part);
//                    container.showNext();
                    container.setDisplayedChild(2);
                    curView = 2;
                    break;
                case 2://tq界面
                    switchTQImageView.setImageResource(R.drawable.text_all);
//                    container.showNext();
                    container.setDisplayedChild(0);
                    curView = 0;
                    break;
                case 3://explain界面-tq界面
                    switchTQImageView.setImageResource(R.drawable.text_all);
                    container.setDisplayedChild(0);
                    curView = 0;
                    break;
            }
        }
    };

    //解析监听器
    private OnClickListener explainOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            //检查登录
            if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
                if (CheckNetWork.isNetworkAvailable(mContext)) {//检查网络连接
                    autoRefresh();//检查会员
                } else {
                    handler.sendEmptyMessage(6);
                }
            } else {
                showLoginDialog();
            }
        }
    };

    private int preview = 0;//记录上一个view
    private Animation upOutAnimation;
    private Animation upInAnimation;
    private Animation downInAnimation;
    private Animation downOutAnimation;

    private void autoRefresh() {
        //检查是否是vip
        int isvip = com.iyuba.configation.ConfigManager.Instance().loadInt("isvip");
        if (isvip > 0) {//== 1
            container.clearAnimation();
            if (curView != 3) {
                preview = curView;
                curView = 3;
                container.setOutAnimation(downOutAnimation);
                container.setInAnimation(upInAnimation);
                container.setDisplayedChild(curView);
                explain_text.setText(explain.Explain);
            } else {
                container.setInAnimation(downInAnimation);
                container.setOutAnimation(upOutAnimation);
                container.setDisplayedChild(preview);
                curView = preview;
            }
            //恢复原来设置
            container.clearAnimation();
            container.setInAnimation(lefinAnimation);
            container.setOutAnimation(rightoutAnimation);
        } else {
            showDialog();
        }
    }

    public void showDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示").
                setIcon(android.R.drawable.ic_dialog_alert).setMessage
                        ("您还不是VIP，暂时不能查看试题解析。如果您已经是VIP，请重新登录。是否去购买VIP？").
                setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setClass(mContext, VipCenterGoldActivity.class);//VipCenter
                        startActivity(intent);
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    public void showLoginDialog() {
        AlertDialog dialog = new AlertDialog.Builder(mContext).setTitle("提示").
                setIcon(android.R.drawable.ic_dialog_alert).setMessage
                        ("您还未登录，请先登录！").
                setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,
                                        int whichButton) {
                        Intent intent = new Intent(mContext, LoginActivity.class);
                        startActivity(intent);
                    }
                }).setNeutralButton("取消", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    private OnClickListener preTitleOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (curPosition > 0) {//不是第一篇
                //更新到上一篇文章的数据
                curPosition -= 1;
                container.setInAnimation(rightinAnimation);
                container.setOutAnimation(leftoutAnimation);
                refreshData(curPosition);
                if (fileIsExists(curTitleInfo.songPath)) {
                    player.playAnother(curTitleInfo.songPath);
                } else {
                    player.playAnother(curTitleInfo.webSongPath);
                }
                LogUtil.e("播放地址！" + curTitleInfo.songPath);
                //player.initialize(curTitleInfo.songPath);
                isLast = false;
                prevTitleButton.setBackgroundResource(R.drawable.button_pres_one);
                nextTitleButton.setBackgroundResource(R.drawable.ic_lesson_next);
            } else {
                CustomToast.showToast(mContext, "已经是第一题了", 1000);
                prevTitleButton.setBackgroundResource(R.drawable.ic_lesson_pre);
            }
        }
    };
    private OnClickListener preSpeedOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            player.preSpeed();
        }
    };
    private OnClickListener switchSpOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {

            if (player.isPlaying()) {
                player.pause();
            } else {
                //player.play();
                isLast = false;//禁止自动下一题
            }
        }
    };
    private OnClickListener nextSpeedOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            player.nextSpeed();
        }
    };

    //下一题
    private OnClickListener nextTitleOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View arg0) {
            if (curPosition < DataManager.Instance().titleInfoList.size() - 1) {
                curPosition += 1;
                container.setInAnimation(lefinAnimation);
                container.setOutAnimation(rightoutAnimation);
                refreshData(curPosition);
                if (fileIsExists(curTitleInfo.songPath)) {
                    player.playAnother(curTitleInfo.songPath);
                } else {
                    player.playAnother(curTitleInfo.webSongPath);
                }
                //player.initialize(curTitleInfo.songPath);
                isLast = false;
                nextTitleButton.setBackgroundResource(R.drawable.ic_lesson_next);
                prevTitleButton.setBackgroundResource(R.drawable.button_pres_one);
                //是被手动点击
            } else {
                CustomToast.showToast(mContext, "已经是最后一题了", 1000);
                nextTitleButton.setBackgroundResource(R.drawable.button_nexts_one);
            }

        }
    };

    private OnPlayStateChangedListener onPlayStateChangedListener = new OnPlayStateChangedListener() {

        @Override
        public void setPlayTime(String currTime, String allTime) {

            handler.sendEmptyMessage(5);
        }

        @Override
        public void playSuccess() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_pause);
            //adapter.animation(1);//1为动画开始，0为结束
        }

        @Override
        public void playStop() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            //adapter.animation(0);
        }

        @Override
        public void playResume() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_pause);
            // adapter.animation(1);
        }

        @Override
        public void playPause() {
            //更新播放器开关的图标
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            //adapter.animation(0);

        }

        @Override
        public void playFaild() {
            switchSPButton.setBackgroundResource(R.drawable.button_play);
            //adapter.animation(0);

        }

        @Override
        public void playCompletion() {
            //adapter.animation(0);

            switchSPButton.setBackgroundResource(R.drawable.button_play);
            isLast = true;
            handler.sendEmptyMessageDelayed(4, 7000);
        }
    };

    private OnSeekBarChangeListener seekBarChangeListener = new OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekbar, int progress,
                                      boolean fromUser) {
            if (fromUser) {//人拉动的，则更新进度条和音频
                //转换为音频的位置
                // int curPosition = player.getDur() * progress / seekbar.getMax();
                int curPosition = curTitleInfo.SoundTime * progress / seekbar.getMax();
                player.seekTo(curPosition);//总时长
            }
            int second = player.getCurrentPosition() / 1000;//进度

            currParagraph = getParagraph(second);

            if (curView == 0) {//当前在TQ界面
                //Message message = handler.obtainMessage(0);//
                //handler.sendMessage(message);
                handler.sendEmptyMessage(0);
                //LogUtil.e("message1 "+message.what);
            } else if (curView == 1) {//当前在T界面
                //Message message = handler.obtainMessage(2);//
                //handler.sendMessage(message);
                //LogUtil.e("message2 "+message.what);
                handler.sendEmptyMessage(2);

            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar arg0) {
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }
    };
    /**
     * 选词选中后的回调函数
     */
    private TextPageSelectTextCallBack tp = new TextPageSelectTextCallBack() {


        @Override
        public void selectTextEvent(String selectText) {
//			Toast.makeText(getBaseContext(), "选词回调", 1000).show();
            wordCard.setVisibility(View.GONE);
            if (selectText.matches("^[a-zA-Z]*")) {
                if (CheckNetWork.isNetworkAvailable(mContext)) {
                    wordCard.setVisibility(View.VISIBLE);
                    wordCard.searchWord(selectText);
                } else {
                    Toast.makeText(mContext, R.string.play_check_network, Toast.LENGTH_SHORT).show();
                    LogUtil.e("网络: " + R.string.play_check_network);
                }
            } else {
                CustomToast.showToast(mContext,
                        R.string.play_please_take_the_word, 1000);
            }
        }

        @SuppressLint("ShowToast")
        @Override
        public void selectParagraph(int paragraph) {

        }
    };

    /**
     * 文章载入的时候就播放
     */
    public void autoPlay() {
        player = new Player(mContext, onPlayStateChangedListener, seekBar);
        //player = new IJKPlayer();
        if (fileIsExists(curTitleInfo.songPath)) {
            player.playUrl(curTitleInfo.songPath);
        } else {
            //http://static2.iyuba.cn/sounds/toeic/5/01.mp3
            player.playUrl(curTitleInfo.webSongPath);
        }
        //player.initialize(curTitleInfo.songPath);
        LogUtil.e("音频地址：" + curTitleInfo.songPath);
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

    /**
     * 原文同步的handler
     */
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0://TQ界面上原文同步
                    if (currParagraph != 0) {
//					Log.e("currParagraph22222222222",currParagraph+" ");
                        currentTextViewTQ.snyParagraphTQ(currParagraph);
                    }

                    break;
                case 1:
                    currentTextViewT.unsnyParagraph();
                    break;
                case 2://T界面上原文同步
                    if (currParagraph != 0) {
//					Log.e("currParagraph333333333333",currParagraph+" ");
                        currentTextViewT.snyParagraphT(currParagraph);
                    }
                case 3:
                    LogUtil.e("到底是谁？？？" + msg.what);
                    break;
                case 4:
                    if (isLast) {//如果是自动点击才走
                        nextTitleButton.performClick();//自动点击？？？？
                        isLast = true;
                    }
                    break;
                case 5:
                    mTvProgress1.setText(player.getAudioCurrTime());
                    //必须获取数据时间，音频文件时间错误！！
                    mTvProgress2.setText(player.getAudioAllTime(curTitleInfo.SoundTime));
                    break;
                case 6:
                    boolean isLine = new NetWorkState(mContext).isConnectingToInternet(); //有问题
                    LogUtil.e("网络：" + CheckNetWork.isNetworkAvailable(mContext) + " " + isLine);
                    CustomToast.showToast(mContext, "网络异常，请检查网络！！！", 800);
                    break;
            }
        }
    };

    /**
     * 播放下一篇文章,上一篇文章，更新数据
     *
     * @param position 第几篇文章
     */
    public void refreshData(int position) {
        //保存刚才做题的记录
        zHelper.setRightNum(curTitleInfo.TitleNum, adapter.getRightNum());

        //更新数据
        currParagraph = 0;
        curView = ConfigManagerMain.Instance().loadInt(Constant.EXERCISE_MODE);

        curTitleInfo = DataManager.Instance().titleInfoList.get(position);
        answerList = teHelper.getAnswers(curTitleInfo.TitleNum);
        textList = teHelper.geTexts(curTitleInfo.TitleNum);
        explain = teHelper.getExplain(curTitleInfo.TitleNum);


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

        DataManager.Instance().anwserList = answerList;
        DataManager.Instance().textList = textList;
        seekBar.setProgress(0);
        adapter.answerList = answerList;
        adapter.notifyDataSetChanged();
        setView();
    }

    /**
     * 左右划屏换题
     */
    private OnTouchListener onTouchListener = new OnTouchListener() {

        float oldx = 0;
        float newx = 0;
        float distance = 100;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            LogUtil.e("touch", "ss");
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                oldx = event.getX();
                return true;
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                newx = event.getX();
                if (newx - oldx >= distance) {
                    curPosition += 1;
                    refreshData(curPosition);
                    //LogUtil.e("音频自动切换："+curPosition);
                    if (fileIsExists(curTitleInfo.songPath)) {
                        player.playAnother(curTitleInfo.songPath);
                    } else {
                        player.playAnother(curTitleInfo.webSongPath);
                    }
                } else if (newx - oldx <= -100) {
                    curPosition -= 1;
                    refreshData(curPosition);
                    if (fileIsExists(curTitleInfo.songPath)) {
                        player.playAnother(curTitleInfo.songPath);
                    } else {
                        player.playAnother(curTitleInfo.webSongPath);
                    }
                }
                return true;
            }
            return false;
        }
    };

    private boolean isTime() {
        long time = System.currentTimeMillis() / 1000;
        long flagTime = 1545202447;
        if (flagTime - time > 0) {
            long i = flagTime - time;
            LogUtil.e("时间还没到，剩余时间：" + i);
            return true;
        }
        return false;
    }
}
