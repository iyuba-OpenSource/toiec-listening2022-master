package com.iyuba.toeiclistening.activity.test.rankinto;

import com.iyuba.module.mvp.MvpView;
import com.iyuba.core.common.network.SpeakRankWork;
import com.iyuba.toeiclistening.entity.Text;


interface HolderMvpView extends MvpView {

    void showMessage(String message);

    void onVoaTitleLoaded(Text voa);

    void onUpvoteSuccess(SpeakRankWork work, int userId);

    void onDownvoteSuccess(SpeakRankWork work, int userId);
}
