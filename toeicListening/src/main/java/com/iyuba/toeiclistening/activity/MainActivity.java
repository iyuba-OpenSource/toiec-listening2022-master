package com.iyuba.toeiclistening.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.sqlite.ImportLibDatabase;
import com.iyuba.core.common.sqlite.mode.EvaluateRecord;
import com.iyuba.core.common.sqlite.mode.ExerciseRecord;
import com.iyuba.core.common.sqlite.mode.ListeningTestRecord;
import com.iyuba.core.common.sqlite.mode.test.WordsUpdateRecord;
import com.iyuba.core.common.sqlite.op.UserInfoOp;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.event.GoTestCollectEvent;
import com.iyuba.core.event.LoginOutEvent;
import com.iyuba.core.event.UpdateEvaluateRecordEvent;
import com.iyuba.core.event.UpdateExerciseRecordEvent;
import com.iyuba.core.event.UpdateListenRecordEvent;
import com.iyuba.core.event.UpdateWordsRecordEvent;
import com.iyuba.core.me.activity.MeMainFragment;
import com.iyuba.core.me.goldvip.VipCenterGoldActivity;
import com.iyuba.core.me.pay.BuyIyubiActivity;
import com.iyuba.core.me.pay.PayOrderActivity;
import com.iyuba.core.util.AdBlocker;
import com.iyuba.headlinelibrary.event.HeadlineGoVIPEvent;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivityNew;
import com.iyuba.headlinelibrary.ui.content.TextContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivityNew;
import com.iyuba.headlinelibrary.ui.video.VideoMiniContentActivity;
import com.iyuba.imooclib.event.ImoocBuyIyubiEvent;
import com.iyuba.imooclib.event.ImoocBuyVIPEvent;
import com.iyuba.imooclib.event.ImoocPayCourseEvent;
import com.iyuba.module.favor.data.model.BasicFavorPart;
import com.iyuba.module.favor.event.FavorItemEvent;
import com.iyuba.module.movies.IMovies;
import com.iyuba.module.movies.IMoviesManager;
import com.iyuba.module.movies.ui.series.SeriesActivity;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.adapter.MyPagerAdapter;
import com.iyuba.toeiclistening.entity.NewWord;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.fragment.DiscoverFragment;
import com.iyuba.toeiclistening.fragment.VideoFragment;
import com.iyuba.toeiclistening.listener.AppUpdateCallBack;
import com.iyuba.toeiclistening.manager.ConfigManagerMain;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.manager.VersionManager;
import com.iyuba.toeiclistening.service.NotificationService;
import com.iyuba.toeiclistening.setting.SettingConfig;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.vocabulary.WordPassFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;
import personal.iyuba.personalhomelibrary.PersonalHome;
import personal.iyuba.personalhomelibrary.data.model.HeadlineTopCategory;
import personal.iyuba.personalhomelibrary.data.model.Voa;
import personal.iyuba.personalhomelibrary.event.ArtDataSkipEvent;
import timber.log.Timber;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements OnClickListener {

    private Context mContext;                            //上下文对象

    private int currSelectActivity = 0;                    //当前选择，通过该值设置要显示的Activity

    private ViewFlipper container = null;                //作为整个布局的容器

    private ArrayList<PackInfo> packList;
    private ArrayList<NewWord> newWordList;
    private ArrayList<TitleInfo> favTitleList;
    private ZDBHelper zhelper;
    private TEDBHelper teHelper;

    //以下是要在导航栏显示的ImagView视图
    private ImageView imgTestLib, imgNewWordsBook, imgCollectedQuestions,
            imgInformation, imgSetting, Icon_Navigate_Words;

    //以下是导航栏菜单各项所对应的相对布局
    private RelativeLayout rlTestLib, rlVideo, rlCollectedQuestions,
            rlInformation, rlSetting;

    private TextView tvText, tvVideo, tvDiscover, tvMe, tvWord;
    private String version_code;
    private String urlString;//app检查更新的网址
    private String fileDir;//app下载后保存的目录
    private String fileName;//应用保存的文件名
    private RelativeLayout layoutFavWord;
    private ImageView imageFavWord;
    private RelativeLayout layoutSet;
    private ImageView imageSet;
    private ViewPager mViewPager;
    private int mSelectTab = 0;
    private String[] mTitles = {"题库", "单词", "视频", "发现", "我的"};
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private TestLibFragment testLibFragment;//题库
    private DiscoverFragment discoverFragment;//发现
    private WordPassFragment wordPassFragment;//单词闯关
    private MeMainFragment meMainFragment;// 我的
    private VideoFragment videoFragmentNew;//视频
    private String uid;
    private boolean isExit = false;// 是否点过退出
    private boolean sleepregist = false;
    public static final String SYSTEM_EXIT = "com.example.exitsystem.system_exit";
    //private MyReceiver receiver;
    private boolean myreceiverregist = false;

    private String myLessonId;
    private String myTestNumber;
    private String myTestWords;
    private String myEndFlg;
    private String myUserAnswer;
    private String myAppName;

    private String myTestId;
    private String myWordsScore;
    private String myWordsUserAnswer;
    private String myWordsUpdateTime;
    private String myWordsLesson;

    private String mySentence;
    private int myParaid;
    private int myScore;
    private String myNewsid;
    private int myIdindex;
    private String myId;
    private String myCreateTime;
    private String myNewstype;
    private int myIsEvaluate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);            //设置为无标题样式
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CrashApplication.addActivity(this);


        initWidget();                                                //初始化工作
        getView();                                                    //得到视图的ID
        setView();                                                    //设置视图的显示以及绑定监听器
        autoLogin();

//        IntentFilter filter = new IntentFilter(); //注册广播，用于退出程序
//        filter.addAction(SYSTEM_EXIT);
//        receiver = new MyReceiver();
//        this.registerReceiver(receiver, filter);

        setCommonData();
        checkAppUpdate();
        initViewPage();
        // setActivity(TestLibActivity.class);                            //首次启动时默认打开的是导航栏中题库选项对应的界面

        LogUtil.e("包名：" + getPackageName());//com.iyuba.toeiclistening
        EventBus.getDefault().register(this);
//        PersonalManager.getInstance().setMainPath("com.iyuba.toeiclistening.activity.MainActivity");//新
//        PersonalManager.getInstance().categoryType=PersonalType.VOA;
//        PersonalManager.getInstance().AppId=com.iyuba.configation.Constant.APPID;
        PersonalHome.setAppInfo(com.iyuba.configation.Constant.APPID, com.iyuba.configation.Constant.AppName);
//        PersonalHome.setDebug(BuildConfig.DEBUG);
        PersonalHome.setIsCompress(true);
        PersonalHome.setCategoryType(com.iyuba.configation.Constant.APPName);
        PersonalHome.setEnableEditNickname(false);
        PersonalHome.setMainPath(MainActivity.class.getName());


    }

    private void initWidget() {
//		Log.d("执行到的地方测试：","执行到MainActivity中的初始化函数！！");

        mContext = this;
        teHelper = new TEDBHelper(mContext);
        zhelper = new ZDBHelper(mContext);                            //初始化时从数据库中读取题库里所有的套题的信息
        packList = zhelper.getPackInfo();

//		for(int i = 0;i < packList.size(); i++){
//			Log.d("777777777mList的内容：",packList.get(i).PackName.toString());
//			Log.d("777777777包中的题目数：",packList.get(i).TitleSum + "");
//			Log.d("777777777包里面问题的总数：",packList.get(i).QuestionSum + "");
//			Log.d("777777777包答对的题目总数：",packList.get(i).RightSum + "");
//		}

        favTitleList = zhelper.getFavTitleInfos();

        String userName = ConfigManager.Instance().loadString(
                AccountManagerLib.USERNAME);
        if (userName != null && userName != "") {
            newWordList = zhelper.getNewWords(ConfigManager
                    .Instance().loadString(AccountManagerLib.USERNAME));
        } else {
            newWordList = new ArrayList<NewWord>();
        }

        DataManager.Instance().packInfoList = packList;                //并将套题的相关信息放到DataManager里面
        DataManager.Instance().newWordList = newWordList;
        DataManager.Instance().favTitleInfoList = favTitleList;
    }

    public void getView() {
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                updateUI(mSelectTab, i);
                mSelectTab = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        rlTestLib = (RelativeLayout) this.findViewById(R.id.RL_Navigate_TestLib);

        layoutFavWord = (RelativeLayout) this.findViewById(R.id.main_navigate_fav_word);

        rlVideo = (RelativeLayout) this.findViewById(R.id.video_all);//RL_Navigate_Words

        Icon_Navigate_Words = (ImageView) this.findViewById(R.id.Icon_Navigate_Words);
        rlInformation = (RelativeLayout) this.findViewById(R.id.RL_Navigae_Information);//资讯被隐藏

        imageFavWord = (ImageView) this.findViewById(R.id.main_image_fav_word);

        imageSet = (ImageView) this.findViewById(R.id.main_image_set);

        imgTestLib = (ImageView) this.findViewById(R.id.Icon_Navigate_TestLib);
        imgInformation = (ImageView) this.findViewById(R.id.Icon_Navigate_Information);
        layoutSet = (RelativeLayout) this.findViewById(R.id.main_navigate_set);
        //layoutSet.setBackgroundResource(R.color.colorPrimary);

        tvText = this.findViewById(R.id.tv_test);
        tvVideo = this.findViewById(R.id.tv_video);
        tvDiscover = this.findViewById(R.id.tv_discover);
        tvMe = this.findViewById(R.id.tv_me);
        tvWord = this.findViewById(R.id.tv_word);

    }

    public void setView() {
        rlTestLib.setOnClickListener(this);
        layoutSet.setOnClickListener(this);
        rlInformation.setOnClickListener(this);
        layoutFavWord.setOnClickListener(this);
        rlVideo.setOnClickListener(this);

        imgTestLib.setImageResource(R.drawable.ic_main_test);
        tvText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
    }

    /*
     * 初始化initViewPage
     */
    private void initViewPage() {
        for (int i = 0; i < mTitles.length; i++) {

            String title = mTitles[i];
            if (title.equals("题库")) {

                testLibFragment = TestLibFragment.newInstance(mTitles[0]);
                mFragments.add(testLibFragment);
            } else if (title.equals("单词")) {

                wordPassFragment = WordPassFragment.newInstance();
                mFragments.add(wordPassFragment);

            } else if (title.equals("视频")) {

                videoFragmentNew = new VideoFragment();
                mFragments.add(videoFragmentNew);
            } else if (title.equals("发现")) {

                discoverFragment = DiscoverFragment.newInstance(mTitles[3]);
                mFragments.add(discoverFragment);
            } else if (title.equals("我的")) {

                meMainFragment = MeMainFragment.newInstance(mTitles[4]);
                mFragments.add(meMainFragment);
            }
        }
        FragmentManager manager = getSupportFragmentManager();
        MyPagerAdapter adapter = new MyPagerAdapter(manager, mFragments, mTitles);
        mViewPager.setCurrentItem(0);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /*
     * 响应MainActivity中的导航菜单项被点击的事件
     * */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.RL_Navigate_TestLib:                    //题库Activity的值为0
                if (currSelectActivity != 0) {
                    updateUI(currSelectActivity, 0);
                    currSelectActivity = 0;
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.video_all:       //  视频
                if (currSelectActivity != 1) {
                    updateUI(currSelectActivity, 1);
                    currSelectActivity = 1;
                    mViewPager.setCurrentItem(2);
                }
                break;
            case R.id.RL_Navigae_Information:                //资讯Activity 单词
                if (currSelectActivity != 2) {
                    updateUI(currSelectActivity, 2);
                    currSelectActivity = 2;
                    mViewPager.setCurrentItem(1);
                }
                break;
            case R.id.main_navigate_fav_word:  //发现
                if (currSelectActivity != 3) {// FavWord这个activity的值为1
                    updateUI(currSelectActivity, 3);
                    currSelectActivity = 3;
                    mViewPager.setCurrentItem(3);
                }
                break;
            case R.id.main_navigate_set:       //我的
                if (currSelectActivity != 4) {// Set这个activity的值为2
                    updateUI(currSelectActivity, 4);
                    currSelectActivity = 4;
                    mViewPager.setCurrentItem(4);
                }
                break;

        }
    }

    /*
     * 功能：更新UI
     * @param:preSelectActivity   上一个打开的Activity
     * @param:currSelectActivity  将要打开的Activity
     * */
    private void updateUI(int preSelectActivity, int curSelectActivity) {

        imgTestLib.setImageResource(R.drawable.ic_main_test_off);//题库
        tvText.setTextColor(mContext.getResources().getColor(R.color.main_tab_gray));

        Icon_Navigate_Words.setImageResource(R.drawable.ic_main_video_off);//视频 friendhui
        tvVideo.setTextColor(mContext.getResources().getColor(R.color.main_tab_gray));

        imgInformation.setImageResource(R.drawable.ic_main_word_off);//单词 ic_main_video_off
        tvWord.setTextColor(mContext.getResources().getColor(R.color.main_tab_gray));

        imageFavWord.setImageResource(R.drawable.ic_main_discover_off);//发现
        tvDiscover.setTextColor(mContext.getResources().getColor(R.color.main_tab_gray));

        imageSet.setImageResource(R.drawable.ic_main_me_off);//我的
        tvMe.setTextColor(mContext.getResources().getColor(R.color.main_tab_gray));


        String navStr = mTitles[curSelectActivity];
        if (navStr.equals("题库")) {

            imgTestLib.setImageResource(R.drawable.ic_main_test);
            tvText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (navStr.equals("单词")) {

            imgInformation.setImageResource(R.drawable.ic_main_word_on);
            tvWord.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (navStr.equals("视频")) {

            Icon_Navigate_Words.setImageResource(R.drawable.ic_main_video);
            tvVideo.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (navStr.equals("发现")) {

            imageFavWord.setImageResource(R.drawable.ic_main_discover);
            tvDiscover.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        } else if (navStr.equals("我的")) {

            imageSet.setImageResource(R.drawable.ic_main_me);
            tvMe.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
        }
    }

    public void autoLogin() {
        if (SettingConfig.Instance().isAutoLogin()) { // 自动登录
            String[] nameAndPwd = AccountManagerLib.Instace(mContext).getUserNameAndPwd();
            String userName = nameAndPwd[0];
            String userPwd = nameAndPwd[1];
            if (userName != null && !userName.equals("")) {
                AccountManagerLib.Instace(mContext).login(userName, userPwd, null);
                AccountManagerLib.Instace(mContext).setLoginState(1);
                EventBus.getDefault().post(new ChangeVideoEvent(true));//event发布
            }
        }
    }

    public void checkAppUpdate() {
        VersionManager.Instace(this).checkNewVersion(VersionManager.Instace(mContext).VERSION_CODE,
                appUpdateCallBack);
    }

    //检查应用更新的回调函数
    AppUpdateCallBack appUpdateCallBack = new AppUpdateCallBack() {
        @Override
        public void appUpdateSave(String version_cod, String newAppNetworkUrl) {
            version_code = version_cod;
            urlString = newAppNetworkUrl;
            Constant.APP_UPDATE_PATH = newAppNetworkUrl;
            handler.sendEmptyMessage(0);
        }

        @Override
        public void appUpdateFaild() {
            handler.sendEmptyMessage(1);
        }

        @Override
        public void appUpdateBegin(String newAppNetworkUrl) {
        }
    };


    Handler handler = new Handler(msg -> {

        switch (msg.what) {
            case 0:
                showAlertAndCancel("有新的版本更新，新的版本增加以下功能。\n1.界面全新改版，学习更加方便！\n" +
                                "2.修复部分bug，让你的使用更加流畅。\n是否更新？",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                //用系统自带浏览器下载
                                Intent intent = new Intent();
                                intent.setData(Uri.parse(Constant.APP_UPDATE_PATH));
                                intent.setAction(Intent.ACTION_VIEW);
                                startActivity(intent);
                                //  自定义下载 和更新下载更新
                            }
                        });
                break;
            default:
                break;
        }
        return false;
    });


    private void showAlertAndCancel(String msg,
                                    DialogInterface.OnClickListener ocl) {
        AlertDialog alert = new AlertDialog.Builder(this).create();
        alert.setTitle(R.string.alert_title);
        alert.setMessage(msg);
        alert.setIcon(android.R.drawable.ic_dialog_alert);
        alert.setButton(AlertDialog.BUTTON_POSITIVE,
                getResources().getString(R.string.alert_btn_ok), ocl);
        alert.setButton(AlertDialog.BUTTON_NEGATIVE,
                getResources().getString(R.string.alert_btn_cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        alert.show();
    }

    private ProgressDialog dialog;

    @SuppressWarnings("static-access")
    private void showAlert(String msg) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);// 设置风格为圆形进度
        dialog.show(this,
                getResources().getString(R.string.about_update_running_title),
                msg, true);
        dialog.dismiss();
    }

    /**
     * 退出应用提示
     */
    private void pressAgainExit() {
        int isdowning = ConfigManager.Instance().loadInt("isdowning");
        if (isdowning <= 0) {
            if (isExit) {
                //sendMsgToService(NotificationService.KEY_COMMAND_REMOVE);
                ConfigManager.Instance().putInt("isdowning", 0);
                exit();
//                Intent intent = new Intent();
//                intent.setAction(MainActivity.SYSTEM_EXIT);
//                sendBroadcast(intent);
            } else {
                CustomToast.showToast(getApplicationContext(), "再按一次退出程序");
                doExitInOneSecond();
            }
        } else {
            Dialog dialog = new AlertDialog.Builder(mContext)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.alert_title)
                    .setMessage(R.string.alert_exit_content)
                    .setPositiveButton(R.string.alert_btn_exit,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                    finish();
                                }
                            })
                    .setNeutralButton(R.string.alert_btn_cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int whichButton) {
                                }
                            }).create();
            dialog.show();// 如果要显示对话框，一定要加上这句
        }
    }

    private void doExitInOneSecond() {
        isExit = true;
        HandlerThread thread = new HandlerThread("doTask");
        thread.start();
        new Handler(thread.getLooper()).postDelayed(task, 1500);// 1.5秒内再点有效
    }

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            isExit = false;
        }
    };

    private void unBind() {
        //unbindService(BackgroundManager.Instace().conn);
        try {
            sendMsgToService(NotificationService.KEY_COMMAND_REMOVE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendMsgToService(String cmd) {
        Intent intent = new Intent(getApplicationContext(),
                NotificationService.class);
        intent.setAction(NotificationService.ACTION_NOTIFICATION_CONTROL);
        intent.putExtra(NotificationService.COMMAND_KEY, cmd);
        startService(intent);
    }

    private void exit() {
        saveFix();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //FlurryAgent.onEndSession(mContext);
                ImportLibDatabase.mdbhelper.close();
                CrashApplication.exit();
            }
        }.start();
    }

    private void saveFix() {
        if (AccountManagerLib.Instace(mContext).userInfo != null) {
            new UserInfoOp(mContext).saveData(AccountManagerLib.Instace(mContext).userInfo);
        }
        ConfigManager.Instance().putInt("mode", com.iyuba.configation.Constant.mode);
        ConfigManager.Instance().putInt("type", com.iyuba.configation.Constant.type);
        ConfigManager.Instance().putInt("download", com.iyuba.configation.Constant.download);
    }

    private void setCommonData() {

        User user = new User();
        AccountManagerLib managerLib = AccountManagerLib.Instace(mContext);
        String vipStatus = ConfigManager.Instance().loadString("vipStatus");
        String userid = ConfigManager.Instance().loadString("userId");
        if (userid != null && !userid.equals("") && !userid.equals("0")) {
            user.vipStatus = vipStatus;
            user.uid = (managerLib.userId) == null ? 0 : Integer.parseInt(managerLib.userId);
            user.name = managerLib.userName;
            if (AdBlocker.shouldBlockAd(getApplication())) {
                user.vipStatus = "1";
            }
            IyuUserManager.getInstance().setCurrentUser(user);
        }
    }

//    class sleepReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            sleepregist = true;
//            CustomToast.showToast(context, "晚安…");
//             CrashApplication.exit();
//        }
//    }

//    private class MyReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            sleepregist = true;
//            context.unregisterReceiver(this);
//            finish();
//        }
//    }

    @Override
    public void onBackPressed() {
        pressAgainExit();
    }


    @Override
    protected void onDestroy() {
        unBind();
//        if (myreceiverregist) {
//            try {
//                this.unregisterReceiver(receiver);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        super.onDestroy();
        EventBus.getDefault().unregister(mContext);
    }

    /**
     * 退出登录
     *
     * @param loginOutEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent loginOutEvent) {

        TEDBHelper tedbHelper = new TEDBHelper(MainActivity.this);
        tedbHelper.deleteEvaluate();
        tedbHelper.resetEvaluate();
    }

    /**
     * 爱语币购买课程
     *
     * @param ImoocBuyIyubiEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyIyubiEvent ImoocBuyIyubiEvent) {

        Intent intent = new Intent();
        intent.setClass(MainActivity.this, BuyIyubiActivity.class);
        intent.putExtra("title", "购买爱语币");
        startActivity(intent);
    }

    /**
     * 微课直购
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocPayCourseEvent event) {

        Intent intent = new Intent(MainActivity.this, PayOrderActivity.class);
        intent.putExtra("amount", event.courseId + "");
        intent.putExtra("productId", 200 + "");
        intent.putExtra("body", event.body);//"花费" + price + "元购买全站vip"
        intent.putExtra("price", event.price);  //价格 60 price + ""
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(FavorItemEvent fEvent) {
        //收藏页面点击
        setCommonData();
        //看一看
        IMovies.init(getApplicationContext(), Constant.APPID, Constant.APP_TYPE);
        IMoviesManager.appId = com.iyuba.configation.Constant.APPID;
        BasicFavorPart fPart = fEvent.items.get(fEvent.position);
        goFavorItem(fPart);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(GoTestCollectEvent event) {
        startActivity(new Intent(mContext, CollectedQuestionsActivity.class));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ImoocBuyVIPEvent event) {
        startActivity(VipCenterGoldActivity.buildIntent(mContext, 2));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)  //vip跳转
    public void fromMoocBuyGoldenVip(HeadlineGoVIPEvent event) {
        startActivity(VipCenterGoldActivity.buildIntent(mContext, 0));
    }

    //更新听力进度数据库
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateListenRecordEvent event) {
//        Timber.e("听力记录MainActivity" + event.deliver);
        if (event.response.result.equals("1")) {
            ArrayList<ListeningTestRecord> listeningTestRecords = new ArrayList<>();
            listeningTestRecords = event.deliver;
            for (int i = 0; i < event.deliver.size(); i++) {
                myLessonId = listeningTestRecords.get(i).lessonId;
                myTestWords = listeningTestRecords.get(i).testWords;
                myTestNumber = listeningTestRecords.get(i).testNumber;
//                Timber.e("更新听力进度数据库 === " + myLessonId + " : " + myTestNumber);
                myEndFlg = listeningTestRecords.get(i).endFlg;
                //判断后向数据库中写入听力进度
                try {
                    if (Integer.parseInt(myTestNumber) > zhelper.getStudyTime(Integer.parseInt(myLessonId))
                            && Integer.parseInt(myTestNumber) <= zhelper.getSoundTime(Integer.parseInt(myLessonId))) {
                        zhelper.updateStudyTime(Integer.parseInt(myLessonId), Integer.parseInt(myTestNumber));
                    }
                } catch (Exception e) {
                    meMainFragment.isError = true;
                    e.printStackTrace();
                }
                ;
            }
        }
//        meMainFragment.checkUpdate();
    }

    //更新做题进度数据库
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateExerciseRecordEvent event) {
//        Timber.e("做题记录MainActivity" + event.deliver);
        if (event.response.result.equals("1")) {
            ArrayList<ExerciseRecord> exerciseRecords = new ArrayList<>();
            exerciseRecords = event.deliver;
            for (int i = 0; i < event.deliver.size(); i++) {
                myLessonId = exerciseRecords.get(i).lessonId;
                myTestNumber = exerciseRecords.get(i).testNumber;
                myUserAnswer = exerciseRecords.get(i).userAnswer;
                myAppName = exerciseRecords.get(i).appName;
                //判断后向数据库中写入做题进度
                try {
                    if (myAppName.equals("toeic") || myAppName.equals("ToeicListening")) {
                        if (Integer.parseInt(myUserAnswer.split(" ")[0])
                                > zhelper.getRightNum(Integer.parseInt(myLessonId))) {
                            zhelper.updateRightNum(Integer.parseInt(myLessonId),
                                    Integer.parseInt(myUserAnswer.split(" ")[0]));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    meMainFragment.isError = true;
                }
                ;
            }
        }
//        meMainFragment.checkUpdate();
    }

    //更新评测进度数据库
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateEvaluateRecordEvent event) {
//        Timber.e("评测记录MainActivity === " + event.deliver.size());
        if (event.response.result.equals("200")) {
            ArrayList<EvaluateRecord> evaluateRecord = new ArrayList<>();
            evaluateRecord = event.deliver;
            for (int i = 0; i < event.deliver.size(); i++) {

                mySentence = evaluateRecord.get(i).sentence;
                myParaid = Integer.parseInt(evaluateRecord.get(i).paraid);
                myScore = (int) Math.round(Double.parseDouble(evaluateRecord.get(i).score) * 20);
                myNewsid = evaluateRecord.get(i).newsid;
                myIdindex = Integer.parseInt(evaluateRecord.get(i).idindex);
                myId = evaluateRecord.get(i).id;
                myCreateTime = evaluateRecord.get(i).createTime;
                myNewstype = evaluateRecord.get(i).newstype;
                if (myScore != 0) {
                    myIsEvaluate = 1;
                } else if (myScore == 0) {
                    myIsEvaluate = 0;
                } else {
                    myIsEvaluate = 0;
                }
                try {
                    teHelper.updateIsEvaluate(myParaid, myIdindex, myIsEvaluate);
                } catch (Exception e) {
                    e.printStackTrace();
                    meMainFragment.isError = true;

                }
//                Timber.e("myParaid " + myParaid);
//                Timber.e("myIdindex " + myIdindex);
//                Timber.e("myIsEvaluate " + myIsEvaluate);

            }
        }
//        meMainFragment.checkUpdate();
    }

    //更新单词数据库
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(UpdateWordsRecordEvent event) {
//        Timber.e("单词MainActivity" + event.deliver);
        if (event.response.result.equals("200")) {
            ArrayList<WordsUpdateRecord> wordsUpdateRecords = new ArrayList<>();
            wordsUpdateRecords = event.deliver;
            for (int i = 0; i < event.deliver.size(); i++) {
                myTestId = wordsUpdateRecords.get(i).testId;
                myWordsScore = wordsUpdateRecords.get(i).score.split("%")[0];
                myWordsUserAnswer = wordsUpdateRecords.get(i).userAnswer;
                myWordsUpdateTime = wordsUpdateRecords.get(i).updateTime;
                myWordsLesson = wordsUpdateRecords.get(i).lesson;
//                Timber.e("更新单词数据库 === myWordsScore: " + myWordsScore
//                        + " myWordsUserAnswer: " + myWordsUserAnswer);
                //判断后向数据库中写入单词进度
                try {
                    if (Integer.parseInt(myWordsScore) == 100 && myWordsScore != null) {
                        teHelper.updateWordFromServer(myWordsUserAnswer, myWordsUserAnswer, 2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    meMainFragment.isError = true;
                }
            }
            double rate = (teHelper.getTotalStudiedWordsNum() / WordPassFragment.WORD_COUNT) + 1;
            Timber.e("应设置的闯关数量 === " + rate);
            ConfigManagerMain.Instance().putInt("stage", (int) Math.floor(rate));
            wordPassFragment.initData();//刷新关数以及相关显示
        }
//        meMainFragment.checkUpdate();
    }


    private void goFavorItem(BasicFavorPart part) {
        String userIdStr = AccountManagerLib.Instace(mContext).userId;
        switch (part.getType()) {
            case "news":
                startActivity(TextContentActivity.getIntent2Me(mContext,
                        part.getId(), part.getTitle(), part.getTitleCn(), part.getType()
                        , part.getCategoryName(), part.getCreateTime(), part.getPic(), part.getSource()));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(mContext, part.getCategoryName(),
                        part.getTitle(), part.getTitleCn(),
                        part.getPic(), part.getType(), part.getId(), part.getSound()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(mContext,
                        part.getCategoryName(), part.getTitle(), part.getTitleCn(), part.getPic(),
                        part.getType(), part.getId()));
                break;
            case "series":
                Intent intent = SeriesActivity.buildIntent(mContext, part.getSeriesId(), part.getId());
                startActivity(intent);
                break;
            case "smallvideo":
                startActivity(VideoMiniContentActivity.buildIntentForOne(this, part.getId(), 0, 1, 1));
                break;
        }
    }

    @SuppressLint("NeedOnRequestPermissionsResult")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this, requestCode, grantResults);
    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void initLocation() {

    }

    @OnPermissionDenied({Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void locationDenied() {
        CustomToast.showToast(MainActivity.this, "开通存储权限才可以正常使用app，请到系统设置中开启");
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ArtDataSkipEvent event) {
        Voa voa = event.voa;
        //文章跳转
        switch (event.type) {
            case "news":
                HeadlineTopCategory topCategory = event.headline;
                startActivity(TextContentActivity.getIntent2Me(mContext,
                        topCategory.id, topCategory.Title, topCategory.TitleCn, topCategory.type
                        , topCategory.Category, topCategory.CreatTime, topCategory.getPic(), topCategory.Source));
                break;
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn,
                        voa.getPic(), event.type, String.valueOf(voa.voaid), voa.sound));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivityNew.getIntent2Me(mContext,
                        voa.categoryString, voa.title, voa.title_cn, voa.getPic(),
                        event.type, String.valueOf(voa.voaid), voa.sound));//voa.getVipAudioUrl()
                break;
        }
    }

}
