package com.iyuba.toeiclistening.activity.test;

import android.text.TextUtils;

import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.network.SpeakRank;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.me.protocol.GetRankInfoResponse;
import com.iyuba.core.me.protocol.RankRequest;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class RankPresenter extends BasePresenter<RankMvpView> {

    private Disposable mDisposable;

    public RankPresenter() {
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(mDisposable);
    }

    public void refresh(String userId, int textId, int pageCount, final boolean oldEndlessState,
                        final String topic) {
        RxUtil.dispose(mDisposable);
        if (userId == null || userId.equals("")) {
            userId = "0";
        }
        ExeProtocol.exe(
                new RankRequest(userId, topic, String.valueOf(textId), "0", String.valueOf(pageCount)),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        GetRankInfoResponse response = (GetRankInfoResponse) bhr;
                        if (response != null && response.message.equals("Success")) {
                            //请求成功
                            if (isViewAttached()) {
                                getMvpView().setSwipeRefreshing(false);

                                List<SpeakRank> rankUsers = response.rankUsers;

                                SpeakRank rank = null;
                                if (!response.uid.equals("0")) {

                                    rank = new SpeakRank();
                                    rank.uid = Integer.valueOf(response.uid);
                                    rank.vip = response.vip;
                                    rank.count = Integer.valueOf(response.myCount);
                                    rank.score = Integer.valueOf(response.myScores);
                                    rank.ranking = Integer.valueOf(response.myRanking);
                                    rank.imgSrc = response.myImgSrc;
                                    if (TextUtils.isEmpty(response.myName)) {
                                        rank.name = String.valueOf(response.uid);
                                    } else {
                                        rank.name = response.myName;
                                    }
                                }

                                if (rankUsers.size() > 0) {
                                    getMvpView().onLatestLoaded(rankUsers, rank);
                                    getMvpView().setNoDataVisible(false);
                                    getMvpView().setRecyclerVisible(true);
                                    getMvpView().setRecyclerEndless(true);
                                } else {
                                    getMvpView().setRecyclerVisible(false);
                                    getMvpView().setNoDataVisible(true);
                                    getMvpView().setRecyclerEndless(false);
                                }
                            }
                        } else {
                            com.facebook.stetho.common.LogUtil.e("tag排名请求失败" + response.message);
                            if (isViewAttached()) {
                                getMvpView().setSwipeRefreshing(false);
                                getMvpView().setRecyclerEndless(oldEndlessState);
                                getMvpView().showMessage("获取数据时出错，请稍后再试!");
                            }
                        }

                    }

                    @Override
                    public void error() {
                        if (isViewAttached()) {
                            getMvpView().setSwipeRefreshing(false);
                            getMvpView().setRecyclerEndless(oldEndlessState);
                            getMvpView().showMessage("获取数据时出错，请稍后再试!");
                        }
                        com.facebook.stetho.common.LogUtil.e("tag排名请求失败2");
                    }
                });
    }

    public void loadMore(String userId, int textId, String start, int pageCount, String topic) {
        RxUtil.dispose(mDisposable);

        ExeProtocol.exe(
                new RankRequest(userId, topic, String.valueOf(textId), start, String.valueOf(pageCount)),
                new ProtocolResponse() {

                    @Override
                    public void finish(BaseHttpResponse bhr) {
                        GetRankInfoResponse response = (GetRankInfoResponse) bhr;
                        if (response != null && response.message.equals("Success")) {
                            //请求成功
                            if (isViewAttached()) {
                                getMvpView().setSwipeRefreshing(false);

                                List<SpeakRank> rankUsers = response.rankUsers;
                                if (rankUsers.size() > 0) {
                                    getMvpView().onMoreLoaded(rankUsers);
                                    getMvpView().setRecyclerEndless(true);
                                } else {
                                    getMvpView().showMessage("数据获取完毕!");
                                    getMvpView().setRecyclerEndless(false);
                                }
                            }
                        } else {
                            com.facebook.stetho.common.LogUtil.e("tag排名请求失败" + response.message);
                            getMvpView().setRecyclerEndless(true);
                            getMvpView().showMessage("获取数据时出错，请稍后再试!");
                        }

                    }

                    @Override
                    public void error() {
                        if (isViewAttached()) {
                            getMvpView().setRecyclerEndless(true);
                            getMvpView().showMessage("获取数据时出错，请稍后再试!");
                        }
                        com.facebook.stetho.common.LogUtil.e("tag排名请求失败3");
                    }
                });
//        mDisposable = mDataManager.getSpeakRank(userId, RangeType.TODAY, topicId, start, pageCount,topic)
//                .compose(RxUtil.<Pair<List<SpeakRank>, SpeakRank>>applySingleIoSchedulerWith(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (isViewAttached()) {
//                            getMvpView().setRecyclerEndless(false);
//                        }
//                    }
//                }))
//                .subscribe(new Consumer<Pair<List<SpeakRank>, SpeakRank>>() {
//                    @Override
//                    public void accept(Pair<List<SpeakRank>, SpeakRank> pair) throws Exception {
//                        if (isViewAttached()) {
//                            if (pair.first.size() > 0) {
//                                getMvpView().onMoreLoaded(pair.first);
//                                getMvpView().setRecyclerEndless(true);
//                            } else {
//                                getMvpView().showMessage("数据获取完毕!");
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().setRecyclerEndless(true);
//                            getMvpView().showMessage("获取数据时出错，请稍后再试!");
//                        }
//                    }
//                });
    }
}
