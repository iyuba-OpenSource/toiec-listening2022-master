package com.iyuba.toeiclistening.activity.test.rankinto;

import com.iyuba.core.me.protocol.UserEvaluateRankRequest;
import com.iyuba.core.me.protocol.UserEvaluateRankResponse;
import com.iyuba.core.common.util.ExeProtocol;

import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.module.toolbox.RxUtil;

import io.reactivex.disposables.Disposable;

public class SpeakWorkPresenter extends BasePresenter<SpeakWorkMvpView> {


    private Disposable mDisposable;
    private Disposable mCreditDisposable;

    public SpeakWorkPresenter() {
    }

    @Override
    public void detachView() {
        super.detachView();
        RxUtil.dispose(mDisposable);
    }

    public void getUserWorks(int userId ,int voaId, String mCategory) {
        RxUtil.dispose(mDisposable);

        ExeProtocol.exe(//mCategory  toeic
                new UserEvaluateRankRequest(String.valueOf(userId), mCategory, String.valueOf(voaId)),
                new com.iyuba.core.common.listener.ProtocolResponse() {
                    @Override
                    public void finish(com.iyuba.core.common.protocol.BaseHttpResponse bhr) {
                        UserEvaluateRankResponse tr = (UserEvaluateRankResponse) bhr;
                        if (tr.result.equals("true")) {
                            getMvpView().onUserWorksLoaded(tr.comments);
                            getMvpView().setSwipeContainer(false);
                        } else {
                            if (isViewAttached()) {
                                getMvpView().setSwipeContainer(true);
                            }
                        }
                    }

                    @Override
                    public void error() {
                        if (isViewAttached()) {
                            getMvpView().setSwipeContainer(false);
                            getMvpView().showMessage("获取数据失败，请稍后再试!");
                        }
                    }
                }
        );
//        Single<List<SpeakRankWork>> rawSingle = mDataManager.getUserWorks(userId,
//                username,mCategory , topicId, shuoshuoType);//Constant.TOPIC IHeadlineManager.TOPIC
//        Single<List<SpeakRankWork>> workSingle;
//        if (topicId != 0) {
//            workSingle = Single.zip(rawSingle, buildDetailStuff(topicId,mCategory), new BiFunction<List<SpeakRankWork>, Pair<SparseIntArray, List<VoaDetail>>, List<SpeakRankWork>>() {
//                @Override
//                public List<SpeakRankWork> apply(List<SpeakRankWork> works, Pair<SparseIntArray, List<VoaDetail>> pair) throws Exception {
//                    SparseIntArray detailsParaIdIndex = pair.first;
//                    List<VoaDetail> details = pair.second;
//                    for (SpeakRankWork work : works) {
//                        if (work.shuoshuoType == UserWorkType.EVALUATION) {
//                            int order = detailsParaIdIndex.get(work.paraid) + work.idindex - 1;
//                            work.readText = details.get(order).sentence;
//                        }
//                    }
//                    return works;
//                }
//            });
//        } else {
//            workSingle = rawSingle;
//        }
//        mDisposable = workSingle
//                .compose(RxUtil.<List<SpeakRankWork>>applySingleIoSchedulerWith(new Consumer<Disposable>() {
//                    @Override
//                    public void accept(Disposable disposable) throws Exception {
//                        if (isViewAttached()) {
//                            getMvpView().setSwipeContainer(true);
//                        }
//                    }
//                }))
//                .subscribe(new Consumer<List<SpeakRankWork>>() {
//                    @Override
//                    public void accept(List<SpeakRankWork> works) throws Exception {
//                        if (isViewAttached()) {
//                            getMvpView().setSwipeContainer(false);
//                            getMvpView().onUserWorksLoaded(works);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().setSwipeContainer(false);
//                            getMvpView().showMessage("获取数据失败，请稍后再试!");
//                        }
//                    }
//                });
    }

//    private Single<Pair<SparseIntArray, List<VoaDetail>>> buildDetailStuff(int topicId,String type) {
//        String path=CommonConstant.DETAIL_URL;
//        return mDataManager.getDetail(path,topicId,CommonConstant.JSON,type)
//                .map(new Function<List<VoaDetail>, Pair<SparseIntArray, List<VoaDetail>>>() {
//                    @Override
//                    public Pair<SparseIntArray, List<VoaDetail>> apply(List<VoaDetail> details) throws Exception {
//                        int notFoundValue = -233;
//                        SparseIntArray detailsParaIdIndex = new SparseIntArray();
//                        for (int i = 0; i < details.size(); i += 1) {
//                            VoaDetail detail = details.get(i);
//                            int oldValue = detailsParaIdIndex.get(Integer.parseInt(detail.paraid), notFoundValue);
//                            if (oldValue == notFoundValue) {
//                                detailsParaIdIndex.put(Integer.parseInt(detail.paraid), i);
//                            }
//                        }
//                        return new Pair<>(detailsParaIdIndex, details);
//                    }
//                });
//    }

    public void addCredit(int userId, int voaId, int srId) {
        RxUtil.dispose(mCreditDisposable);
//        mCreditDisposable = mDataManager.addCredit(userId, topicId, srId)
//                .compose(RxUtil.<UpdateScoreInfo>applySingleIoScheduler())
//                .subscribe(new Consumer<UpdateScoreInfo>() {
//                    @Override
//                    public void accept(UpdateScoreInfo info) throws Exception {
//                        if (isViewAttached()) {
//                            getMvpView().onShareCreditAdded(info.creditChange, info.totalCredit);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        Timber.e("add credit failed.");
//                    }
//                });
    }
}
