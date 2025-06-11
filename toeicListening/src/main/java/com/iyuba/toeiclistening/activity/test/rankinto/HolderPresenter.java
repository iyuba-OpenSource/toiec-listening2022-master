package com.iyuba.toeiclistening.activity.test.rankinto;

import android.annotation.SuppressLint;

import com.iyuba.core.common.network.AgreeAgainstRequest;
import com.iyuba.core.common.network.AgreeAgainstResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.me.protocol.UserEvaluateRankRequest;
import com.iyuba.core.me.protocol.UserEvaluateRankResponse;
import com.iyuba.module.mvp.BasePresenter;
import com.iyuba.core.common.network.SpeakRankWork;

public class HolderPresenter extends BasePresenter<HolderMvpView> {
   // private final DataManager mDataManager;

    public HolderPresenter() {
        //mDataManager = DataManager.getInstance();
    }

    @SuppressLint("CheckResult")
    public void getVoaTitle(int voaId,String type) {
//        mDataManager.getVoaTitle(topicId,type)
//                .compose(RxUtil.<Voa>applySingleIoScheduler())
//                .subscribe(new Consumer<Voa>() {
//                    @Override
//                    public void accept(Voa voa) throws Exception {
//                        if (isViewAttached()) {
//                            getMvpView().onVoaTitleLoaded(voa);
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().showMessage("操作失败，请稍后再试!");
//                        }
//                    }
//                });
    }

    @SuppressLint("CheckResult")
    public void upvote(final SpeakRankWork work, final int userId) {


        ExeProtocol.exe(
                new AgreeAgainstRequest("61001", String.valueOf(work.id), work.shuoshuoType),
                new com.iyuba.core.common.listener.ProtocolResponse() {
                    @Override
                    public void finish(com.iyuba.core.common.protocol.BaseHttpResponse bhr) {
                        AgreeAgainstResponse tr = (AgreeAgainstResponse) bhr;
                        if (isViewAttached()) {
                            if (tr.result.equals("001")) {
                                getMvpView().showMessage("点赞成功~");
                                getMvpView().onUpvoteSuccess(work, userId);
                            } else {
                                getMvpView().showMessage("点赞未成功，请稍后再试!");
                            }
                        }
                    }

                    @Override
                    public void error() {
                        if (isViewAttached()) {
                            getMvpView().showMessage("点赞未成功，请稍后再试!");
                        }
                    }
                }
        );

//        mDataManager.upvoteComment(String.valueOf(work.id))
//                .compose(RxUtil.<Boolean>applySingleIoScheduler())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean result) throws Exception {
//                        if (isViewAttached()) {
//                            if (result) {
//                                getMvpView().showMessage("点赞成功~");
//                                getMvpView().onUpvoteSuccess(work, userId);
//                            } else {
//                                getMvpView().showMessage("点赞未成功，请稍后再试!");
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().showMessage("点赞未成功，请稍后再试!");
//                        }
//                    }
//                });
    }

    @SuppressLint("CheckResult")
    public void downvote(final SpeakRankWork work, final int userId) {

        ExeProtocol.exe(                                    //work.id 是说说ID！
                new AgreeAgainstRequest("61002",String.valueOf(work.id), work.shuoshuoType),
                new com.iyuba.core.common.listener.ProtocolResponse() {
                    @Override
                    public void finish(com.iyuba.core.common.protocol.BaseHttpResponse bhr) {
                        AgreeAgainstResponse tr = (AgreeAgainstResponse) bhr;
                        if (isViewAttached()) {
                            if (tr.result.equals("001")) {
                                getMvpView().showMessage("鄙视成功-_-");
                                getMvpView().onDownvoteSuccess(work, userId);
                            } else {
                                getMvpView().showMessage("鄙视未成功，请稍后再试!");
                            }
                        }
                    }

                    @Override
                    public void error() {
                        if (isViewAttached()) {
                            getMvpView().showMessage("鄙视未成功，请稍后再试!");
                        }
                    }
                }
        );
//        mDataManager.downvoteComment(String.valueOf(work.id))
//                .compose(RxUtil.<Boolean>applySingleIoScheduler())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean result) throws Exception {
//                        if (isViewAttached()) {
//                            if (result) {
//                                getMvpView().showMessage("鄙视成功-_-");
//                                getMvpView().onDownvoteSuccess(work, userId);
//                            } else {
//                                getMvpView().showMessage("鄙视未成功，请稍后再试!");
//                            }
//                        }
//                    }
//                }, new Consumer<Throwable>() {
//                    @Override
//                    public void accept(Throwable throwable) throws Exception {
//                        Timber.e(throwable);
//                        if (isViewAttached()) {
//                            getMvpView().showMessage("鄙视未成功，请稍后再试!");
//                        }
//                    }
//                });
    }
}
