package com.iyuba.toeiclistening.activity.test.rankinto;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.core.common.network.SpeakRankWork;

import java.util.List;

interface SpeakWorkMvpView extends MvpView {

    void showMessage(String message);

    void setSwipeContainer(boolean isRefreshing);

    void onUserWorksLoaded(List<SpeakRankWork> works);

    void onShareCreditAdded(int creditChange, int totalCredit);

}
