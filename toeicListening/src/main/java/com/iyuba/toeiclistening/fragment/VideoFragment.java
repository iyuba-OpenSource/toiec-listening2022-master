package com.iyuba.toeiclistening.fragment;

import static com.iyuba.configation.RuntimeManager.getApplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.event.ChangeVideoEvent;
import com.iyuba.core.util.AdBlocker;
import com.iyuba.headlinelibrary.HeadlineType;
import com.iyuba.headlinelibrary.ui.content.AudioContentActivity;
import com.iyuba.headlinelibrary.ui.content.VideoContentActivity;
import com.iyuba.headlinelibrary.ui.title.DropdownTitleFragmentNew;
import com.iyuba.headlinelibrary.ui.title.HolderType;
import com.iyuba.headlinelibrary.ui.title.ITitleRefresh;
import com.iyuba.module.dl.BasicDLPart;
import com.iyuba.module.dl.DLItemEvent;
import com.iyuba.module.user.IyuUserManager;
import com.iyuba.module.user.User;
import com.iyuba.toeiclistening.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by iyuba on 2017/7/27.
 */

public class VideoFragment extends Fragment {
    private Context mContext;

    private DropdownTitleFragmentNew mExtraFragment;

    private WindowManager mWindowManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initVariables();
    }


    protected void initVariables() {
        //mContext = this;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //nightModeManager = new NightModeManager(mWindowManager, mContext);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_layout_video, null);

        initview();

        EventBus.getDefault().register(this);
        return root;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initview() {

        FragmentManager manager = getChildFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        if (isTime()) {
            LogUtil.e("是否到时间了" + isTime());
        }

        String[] types = new String[]{
                //HeadlineType.ALL,
                //HeadlineType.NEWS,
                //HeadlineType.BBC,
                //HeadlineType.VOA,

                //HeadlineType.CSVOA,
                //HeadlineType.SONG,
                HeadlineType.SMALLVIDEO,
                HeadlineType.VOAVIDEO,

                HeadlineType.TED,
                HeadlineType.MEIYU,
                HeadlineType.TOPVIDEOS,
                HeadlineType.BBCWORDVIDEO,
                //HeadlineType.JAPANVIDEOS
        };


        Bundle bundle = DropdownTitleFragmentNew.buildArguments(10, HolderType.SMALL, types, false);
        mExtraFragment = DropdownTitleFragmentNew.newInstance(bundle);
//        mExtraFragment.setSmallVideoFragment(VideoListFragment.newInstance());

        transaction.replace(R.id.content_video, mExtraFragment);

        transaction.show(mExtraFragment);
        transaction.commit();

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(BasicDLPart event) {
        jumpToCorrectDLActivityByCate(mContext, event);
    }

    public void jumpToCorrectDLActivityByCate(Context context, BasicDLPart basicHDsDLPart) {

        switch (basicHDsDLPart.getType()) {
            //case "voa":
            //case "csvoa":
            //case "bbc":
            // case "song":
//                startActivity(AudioContentActivity.getIntent2Me(context, Constant.APPID, Integer.parseInt(AccountManagerLib.Instace(getActivity()).userId), isvip, basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(),
//                        basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(), basicHDsDLPart.getTitle_cn()));
            //     break;
            case "smallvideo":
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
                //case "japanvideos":
            case "topvideos":
                startActivity(VideoContentActivity.getIntent2Me(context,
                        basicHDsDLPart.getCategoryName(), basicHDsDLPart.getTitle(),
                        basicHDsDLPart.getPic(), basicHDsDLPart.getType(), basicHDsDLPart.getId(),
                        basicHDsDLPart.getTitleCn()));
                break;
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DLItemEvent dlEvent) {
        //视频下载后点击
        BasicDLPart dlPart = dlEvent.items.get(dlEvent.position);

        switch (dlPart.getType()) {
            case "smallvideo":
            case "voa":
            case "csvoa":
            case "bbc":
            case "song":
                startActivity(AudioContentActivity.getIntent2Me(getContext(),
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
            case "voavideo":
            case "meiyu":
            case "ted":
            case "bbcwordvideo":
            case "topvideos":
            case "japanvideos":
                startActivity(VideoContentActivity.getIntent2Me(getContext(),
                        dlPart.getCategoryName(), dlPart.getTitle(), dlPart.getTitleCn(),
                        dlPart.getPic(), dlPart.getType(), dlPart.getId()));
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChangeVideoEvent videoEvnet) {
        LogUtil.e("Event刷新ChangeVideoEvnet" + " 用户名" + ConfigManager.Instance().loadString("userName"));

        if (AccountManagerLib.Instace(mContext).checkUserLogin()) {

            User user = new User();
            AccountManagerLib managerLib = AccountManagerLib.Instace(mContext);
            try {
                user.vipStatus = (managerLib.userInfo.vipStatus) == null ? "0" : managerLib.userInfo.vipStatus;
                user.uid = (managerLib.userId) == null ? 0 : Integer.parseInt(managerLib.userId);
                user.name = managerLib.userName;
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (AdBlocker.shouldBlockAd(getApplication())) {
                user.vipStatus = "6";
            }
            IyuUserManager.getInstance().setCurrentUser(user);
        }


        if (!videoEvnet.isChange) {
            IyuUserManager.getInstance().logout();
        }

        if (mExtraFragment instanceof ITitleRefresh) {
            ((ITitleRefresh) mExtraFragment).refreshTitleContent();
        }

    }

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
