package com.iyuba.toeiclistening.mvp.model;

import com.iyuba.toeiclistening.Constant;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.view.SplashContract;
import com.iyuba.toeiclistening.mvp.view.TestLibContract;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class TestLibModel implements TestLibContract.TestLibModel {


    @Override
    public Disposable getAdEntryAll(String appId, int flag, String uid, SplashContract.Callback callback) {

        return NetWorkManager
                .getRequest()
                .getAdEntryAll(Constant.GET_AD_ENTRY_ALL,appId, flag, uid)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<AdEntryBean>>() {
                    @Override
                    public void accept(List<AdEntryBean> adEntryBeans) throws Exception {

                        callback.success(adEntryBeans);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        callback.error((Exception) throwable);
                    }
                });
    }
}
