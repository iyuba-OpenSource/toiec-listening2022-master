package com.iyuba.toeiclistening.activity.test;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.SpeakRank;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.event.ResetPlayDataEvent;
import com.iyuba.toeiclistening.manager.DataManager;
import com.iyuba.widget.recycler.EndlessListRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * 评测排行！
 */
public class RankFragment extends Fragment implements RankMvpView {

    private static final String TAG = "RankFragment";
    private static final int PAGE_SIZE = 10;

    RelativeLayout mNoDataContainer;
    EndlessListRecyclerView mRankingRecyclerView;
    SwipeRefreshLayout mSwipeContainer;

    RankPresenter mPresenter;

    RankingAdapter rankingAdapter;

    private int textId;

    private String topic;

    public static RankFragment newInstance() {
        return new RankFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPresenter = new RankPresenter();

        topic = "toeic";
        rankingAdapter = new RankingAdapter(topic);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rank, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view);
        mPresenter.attachView(this);

        mSwipeContainer.setOnRefreshListener(mRefreshListener);
        mSwipeContainer.setColorSchemeResources(R.color.blue, R.color.purple, R.color.orange, R.color.red);
        mRankingRecyclerView.setEndless(false);
        mRankingRecyclerView.setAdapter(rankingAdapter);
        mRankingRecyclerView.setOnEndlessListener(mEndlessListener);

        textId = DataManager.Instance().textList.get(0).TitleNum;
        boolean oldEndless = mRankingRecyclerView.getEndless();
        String userId = AccountManagerLib.Instace(getContext()).userId;
        rankingAdapter.setRankVoaId(textId);
        mPresenter.refresh(userId, textId, PAGE_SIZE, oldEndless, topic);
        EventBus.getDefault().register(this);
    }

    private void initView(View view) {

        mNoDataContainer = view.findViewById(R.id.relative_container);
        mRankingRecyclerView = view.findViewById(R.id.recycler);
        mSwipeContainer = view.findViewById(R.id.swipe_refresh_container);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mSwipeContainer.setRefreshing((boolean) msg.obj);
                    LogUtil.e("排行刷新");
                    break;
                case 1:
                    LogUtil.e("排行有无数据" + msg.obj);
                    mNoDataContainer.setVisibility((boolean) msg.obj ? View.VISIBLE : View.GONE);
                    break;
                case 2:
                    LogUtil.e("排行列表显示" + msg.obj);
                    mRankingRecyclerView.setVisibility((boolean) msg.obj ? View.VISIBLE : View.GONE);
                    break;
                case 3:
                    LogUtil.e("排行数据加载完");
                    mRankingRecyclerView.setEndless((boolean) msg.obj);
                    break;
                case 4:
                    rankingAdapter.notifyDataSetChanged();
                    break;
                case 6:
                    CustomToast.showToast(getContext(), (String) msg.obj);
                    break;
                default:
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ResetPlayDataEvent event) {
        ArrayList<Text> textList = ((TestDetailActivity) Objects.requireNonNull(getActivity())).textList;
        textId = textList.get(0).TitleNum;
        LogUtil.e("新的试题ID" + textId);
        rankingAdapter.setRankVoaId(textId);
        String userId = AccountManagerLib.Instace(getContext()).userId;
        boolean oldEndless = mRankingRecyclerView.getEndless();
        mPresenter.refresh(userId, textId, PAGE_SIZE, oldEndless, topic);
    }

    @Override
    public void showMessage(String message) {
        //CustomToast.showToast(getContext(), message);
        Message msg = new Message();
        msg.what = 6;
        msg.obj = message;
        handler.sendMessage(msg);
    }

    @Override
    public void setSwipeRefreshing(boolean isRefreshing) {
        //这是子线程了！！！
        //mSwipeContainer.setRefreshing(isRefreshing);
        Message message = new Message();
        message.what = 0;
        message.obj = isRefreshing;
        handler.sendMessage(message);
    }

    @Override
    public void setNoDataVisible(boolean isVisible) {
        //mNoDataContainer.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        Message message = new Message();
        message.what = 1;
        message.obj = isVisible;
        handler.sendMessage(message);
    }

    @Override
    public void setRecyclerVisible(boolean isVisible) {
        //mRankingRecyclerView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        Message message = new Message();
        message.what = 2;
        message.obj = isVisible;
        handler.sendMessage(message);
    }

    @Override
    public void setRecyclerEndless(boolean isEndless) {
        //mRankingRecyclerView.setEndless(isEndless);
        Message message = new Message();
        message.what = 3;
        message.obj = isEndless;
        handler.sendMessage(message);
    }

    @Override
    public void onLatestLoaded(List<SpeakRank> ranks, SpeakRank rank) {
        rankingAdapter.setData(ranks, rank);
        handler.sendEmptyMessage(4);

    }

    @Override
    public void onMoreLoaded(List<SpeakRank> ranks) {
        rankingAdapter.addData(ranks);
        handler.sendEmptyMessage(4);
    }

    private SwipeRefreshLayout.OnRefreshListener mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            String userId = String.valueOf(AccountManagerLib.Instace(getActivity()).userId);

            boolean oldEndless = mRankingRecyclerView.getEndless();
            mPresenter.refresh(userId, textId, PAGE_SIZE, oldEndless, topic);
        }
    };

    private EndlessListRecyclerView.OnEndlessListener mEndlessListener = new EndlessListRecyclerView.OnEndlessListener() {
        @Override
        public void onEndless() {
            String userId = String.valueOf(AccountManagerLib.Instace(getActivity()).userId);
            if (userId.equals("")) {
                userId = "0";
            }
            String last = String.valueOf(rankingAdapter.getLastRank());
            mPresenter.loadMore(userId, textId, last, PAGE_SIZE, topic);
        }
    };

}
