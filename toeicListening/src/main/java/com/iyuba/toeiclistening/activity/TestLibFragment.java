package com.iyuba.toeiclistening.activity;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.event.DeleteAudioFileEvent;
import com.iyuba.core.event.TestLibFragmentRefreashEvent;
import com.iyuba.module.toolbox.DensityUtil;
import com.iyuba.sdk.data.iyu.IyuNative;
import com.iyuba.sdk.data.ydsdk.YDSDKTemplateNative;
import com.iyuba.sdk.data.youdao.YDNative;
import com.iyuba.sdk.mixnative.MixAdRenderer;
import com.iyuba.sdk.mixnative.MixNative;
import com.iyuba.sdk.mixnative.MixViewBinder;
import com.iyuba.sdk.mixnative.PositionLoadWay;
import com.iyuba.sdk.mixnative.StreamType;
import com.iyuba.sdk.nativeads.NativeAdPositioning;
import com.iyuba.sdk.nativeads.NativeEventListener;
import com.iyuba.sdk.nativeads.NativeRecyclerAdapter;
import com.iyuba.sdk.nativeads.NativeResponse;
import com.iyuba.toeiclistening.BuildConfig;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.adapter.TestListRecyclerAdapter;
import com.iyuba.toeiclistening.entity.DownTest;
import com.iyuba.toeiclistening.entity.PackInfo;
import com.iyuba.toeiclistening.entity.TitleInfo;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.presenter.TestLibPresenter;
import com.iyuba.toeiclistening.mvp.view.TestLibContract;
import com.iyuba.toeiclistening.sqlite.TEDBHelper;
import com.iyuba.toeiclistening.sqlite.ZDBHelper;
import com.iyuba.toeiclistening.util.Constant;
import com.iyuba.toeiclistening.util.ListFileUtil;
import com.youdao.sdk.nativeads.RequestParameters;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 首页题库列表界面
 * create 2018-12-03
 * by 赵皓 修改
 */
public class TestLibFragment extends Fragment implements TestLibContract.TestLibView {

    private Context mContext;
    private ArrayList<PackInfo> packList;            //套题的数组（每一套题作为一个对象）
    private ArrayList<TitleInfo> titleInfoList;

    //private ListView listView;
    private RecyclerView mRvTestList;
    private Button btn_word;
    private Button btn_collect;

    private TEDBHelper teHelper;
    private ZDBHelper zHelper;

    private ArrayList<DownTest> downTests = new ArrayList<DownTest>();

    private boolean isRefresh;

    private TestListRecyclerAdapter testListRecyclerAdapter;//新的

    private TestLibPresenter testLibPresenter;

    public TestLibFragment() {
        // Required empty public constructor
    }

    public static TestLibFragment newInstance(String title) {
        TestLibFragment fragment = new TestLibFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testLibPresenter = new TestLibPresenter();
        testLibPresenter.attchView(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        String title = bundle.getString("title");
        View view = inflater.inflate(R.layout.testlib, container, false);
        mContext = getActivity();
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        testListRecyclerAdapter.notifyDataSetChanged();
        handler.sendEmptyMessage(3);//只是刷新！
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (testLibPresenter != null) {

            testLibPresenter.detachView();
        }
    }

    public void init(View container) {

        teHelper = new TEDBHelper(mContext);
        zHelper = new ZDBHelper(mContext);
        btn_word = container.findViewById(R.id.btn_word);
        btn_collect = container.findViewById(R.id.btn_collect);
        btn_word.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, WordCollectionNew.class));//NewWordsBookActivity 单词本
            }
        });
        btn_collect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, CollectedQuestionsActivity.class));
            }
        });

        btn_collect.setVisibility(View.GONE);
        btn_word.setVisibility(View.GONE);

        packList = DataManager.Instance().packInfoList;                            //得到套题对象的列表
        titleInfoList = DataManager.Instance().titleInfoList;                      //得到套题对象的列表
        mRvTestList = container.findViewById(R.id.rv_test_lib_list);//testlib_list
        initRecycleView();
        ArrayList<PackInfo> list = new ArrayList<>();
        list.addAll(packList);
        setView(list);
    }

    private void initRecycleView() {
        //创建LinearLayoutManager 对象
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        //设置RecyclerView 布局
        mRvTestList.setLayoutManager(mLayoutManager);
        //设置Adapter
        testListRecyclerAdapter = new TestListRecyclerAdapter(mContext);
        mRvTestList.setAdapter(testListRecyclerAdapter);
        mRvTestList.setItemAnimator(null);

        String vipStatus = ConfigManager.Instance().loadString("vipStatus");
        String userid = ConfigManager.Instance().loadString("userId");

        if (userid == null || userid.equals("")) {

            testLibPresenter.getAdEntryAll(Constant.ADAPPID, 2, "0");
        } else {

            if (vipStatus != null && vipStatus.equals("0")) {

                testLibPresenter.getAdEntryAll(Constant.ADAPPID, 2, userid);
            }
        }
    }

    private void setView(final ArrayList<PackInfo> list) {
        //long lastTime = ConfigManager.Instance().loadLong("time");
        //ConfigManager.Instance().putLong("time", System.currentTimeMillis());
        //if (System.currentTimeMillis() - lastTime > 10000) {
        LogUtil.e("packList数量1 " + list.size());
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.e("packList数量2 " + list.size());
                for (int i = 0; i < list.size(); i++) {
                    LogUtil.e("packList数量3 " + list.size());
                    PackInfo packInfo = list.get(i);
                    int packNum = zHelper.getDownPackNum(packInfo.PackName);
                    LogUtil.e("下载！packNum " + packNum + "PackName:" + packInfo.PackName + "TestType" + packInfo.TestType);
                    downTests = teHelper.getDownTests(packInfo.PackName, packNum, packInfo.TestType);
                    LogUtil.e("下载！downTests " + downTests.size());
                    if (downTests.size() != 0) {
                        int num = checkDownFilesByPack(packInfo.PackName, downTests.size(), packInfo.TestType);
                        if (num == 100) {
                            list.get(i).IsDownload = true;
                            list.get(i).Progress = 1.0f;
                            list.get(i).IsFree = true;
                            zHelper.setState(packInfo.PackName, true, true, 1.0f);
                        } else {
                            list.get(i).IsDownload = false;
                            list.get(i).Progress = 0f;
                            list.get(i).IsFree = false;
                            zHelper.setState(packInfo.PackName, false, false, 0);
                        }
                    }
                }
                testListRecyclerAdapter.setData(list);
                handler.sendEmptyMessage(3);//刷新！
            }
        }).start();

        if (isRefresh) {
            handler.sendEmptyMessage(2);//重新写入
        } else {
            handler.sendEmptyMessage(1);//初始写入
        }
    }

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {

            switch (message.what) {
                case 1:
                    testListRecyclerAdapter.setData(packList);
                    testListRecyclerAdapter.notifyDataSetChanged();
                    break;
                case 2:
                    testListRecyclerAdapter.resetData(packList);
                    testListRecyclerAdapter.notifyDataSetChanged();
                    isRefresh = false;
                    break;
                case 3:
                    testListRecyclerAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    //
    public static int checkDownFilesByPack(String packName, int testSum, int testType) {
        LogUtil.e("mp3文件--下载文件检查:" + packName);
        int fileNum = 0;
        // 检测audio下该年份文件夹是否存在
        String savePAth = "";
        if (testType == 101) {
            savePAth = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH + "/" + packName;
        } else {
            savePAth = com.iyuba.toeiclistening.Constant.APP_DATA_PATH + Constant.SDCARD_AUDIO_PATH + "/" + "2019" + packName;
        }
        File file1 = new File(savePAth);
        if (!file1.exists()) {// 该年份文件夹不存在
            LogUtil.e("mp3文件--没有:" + savePAth);
            return fileNum;
        } else {// 检测该年份文件夹下文件数,过滤出mp3文件个数
            FilenameFilter mf = new ListFileUtil.MyFilenameFilter(".mp3");
            LogUtil.e("mp3文件--存在：" + ListFileUtil.listFilesByFilenameFilter(mf, savePAth) + "该年份题总数：" + testSum);
            if (testSum == 55) testSum = 54;
            fileNum = ListFileUtil.listFilesByFilenameFilter(mf, savePAth) * 100 / testSum;
            LogUtil.e("mp3文件：" + fileNum + "packName :" + packName);
        }
        return fileNum;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)  //删除资源后的刷新！
    public void onEvent(DeleteAudioFileEvent event) {
        isRefresh = true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(TestLibFragmentRefreashEvent event) {
        onResume();
    }

    @Override
    public void getAdEntryAllComplete(AdEntryBean adEntryBean) {

        if (adEntryBean.getResult().equals("-1")) {

//            initYouDao();
        } else {

            setAdAdapter(adEntryBean.getData());
        }
    }


    private void setAdAdapter(AdEntryBean.DataDTO dataBean) {

        OkHttpClient mClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build();

        EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE,
                RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE,
                RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
        RequestParameters requestParameters = new RequestParameters.RequestParametersBuilder()
                .location(null)
                .keywords("日语")
                .desiredAssets(desiredAssets)
                .build();
        YDNative ydNative = new YDNative(requireActivity(), "edbd2c39ce470cd72472c402cccfb586", requestParameters);

        IyuNative iyuNative = new IyuNative(requireActivity(), Constant.ADAPPID, mClient);

        YDSDKTemplateNative csjTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_CSJ);
        YDSDKTemplateNative ylhTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_YLH);
//        YDSDKTemplateNative ksTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_KS);
        YDSDKTemplateNative bdTemplateNative = new YDSDKTemplateNative(requireActivity(), BuildConfig.TEMPLATE_SCREEN_AD_KEY_BD);

        //添加key
        HashMap<Integer, YDSDKTemplateNative> ydsdkMap = new HashMap<>();
        ydsdkMap.put(StreamType.TT, csjTemplateNative);
        ydsdkMap.put(StreamType.GDT, ylhTemplateNative);
//        ydsdkMap.put(StreamType.KS, ksTemplateNative);
        ydsdkMap.put(StreamType.BAIDU, bdTemplateNative);


        MixNative mixNative = new MixNative(ydNative, iyuNative, ydsdkMap);
        PositionLoadWay loadWay = new PositionLoadWay();
        loadWay.setStreamSource(new int[]{
                Integer.parseInt(dataBean.getFirstLevel()),
                Integer.parseInt(dataBean.getSecondLevel()),
                Integer.parseInt(dataBean.getThirdLevel())});
        mixNative.setLoadWay(loadWay);

        int startPosition = 3;
        int positionInterval = 5;
        NativeAdPositioning.ClientPositioning positioning = new NativeAdPositioning.ClientPositioning();
        positioning.addFixedPosition(startPosition);
        positioning.enableRepeatingPositions(positionInterval);
        NativeRecyclerAdapter mAdAdapter = new NativeRecyclerAdapter(requireActivity(), testListRecyclerAdapter, positioning);
        mAdAdapter.setNativeEventListener(new NativeEventListener() {
            @Override
            public void onNativeImpression(View view, NativeResponse nativeResponse) {

            }

            @Override
            public void onNativeClick(View view, NativeResponse nativeResponse) {

            }
        });
        mAdAdapter.setAdSource(mixNative);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        //设置穿山甲广告不显示的问题
        mixNative.setWidthHeight(displayMetrics.widthPixels, DensityUtil.dp2px(ToeicApplication.getApplication(), 120));
        mixNative.setYDSDKTemplateNativeClosedListener(new MixNative.YDSDKTemplateNativeClosedListener() {
            @Override
            public void onClosed(View view) {
                View itemView = (View) ((View) view.getParent()).getParent();
                RecyclerView.ViewHolder viewHolder = mRvTestList.getChildViewHolder(itemView);
                int position = viewHolder.getBindingAdapterPosition();
                mAdAdapter.removeAdsWithAdjustedPosition(position);
            }
        });

        MixViewBinder mixViewBinder = new MixViewBinder.Builder(R.layout.item_ad_mix)
                .templateContainerId(R.id.mix_fl_ad)
                .nativeContainerId(R.id.headline_ll_item)
                .nativeImageId(R.id.native_main_image)
                .nativeTitleId(R.id.native_title)
                .build();
        MixAdRenderer mixAdRenderer = new MixAdRenderer(mixViewBinder);
        mAdAdapter.registerAdRenderer(mixAdRenderer);
        mRvTestList.setAdapter(mAdAdapter);
        mAdAdapter.loadAds();
    }

    @Override
    public void showLoading(String msg) {

    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void toast(String msg) {

    }
}
