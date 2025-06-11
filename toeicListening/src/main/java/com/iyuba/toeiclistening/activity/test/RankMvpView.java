package com.iyuba.toeiclistening.activity.test;

import com.iyuba.core.common.network.SpeakRank;
import com.iyuba.module.mvp.MvpView;

import java.util.List;

interface RankMvpView extends MvpView {
    void showMessage(String message);

    void setSwipeRefreshing(boolean isRefreshing);

    void setNoDataVisible(boolean isVisible);

    void setRecyclerVisible(boolean isVisible);

    void setRecyclerEndless(boolean isEndless);

    void onLatestLoaded(List<SpeakRank> ranks, SpeakRank rank);

    void onMoreLoaded(List<SpeakRank> ranks);
}
