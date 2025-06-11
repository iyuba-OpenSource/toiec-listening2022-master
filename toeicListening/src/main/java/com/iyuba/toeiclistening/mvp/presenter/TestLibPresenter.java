package com.iyuba.toeiclistening.mvp.presenter;

import android.util.Log;

import com.iyuba.toeiclistening.mvp.model.TestLibModel;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.view.SplashContract;
import com.iyuba.toeiclistening.mvp.view.TestLibContract;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class TestLibPresenter extends BasePresenter<TestLibContract.TestLibView, TestLibContract.TestLibModel>
        implements TestLibContract.TestLibPresenter {


    @Override
    protected TestLibContract.TestLibModel initModel() {
        return new TestLibModel();
    }

    @Override
    public void getAdEntryAll(String appId, int flag, String uid) {

        Disposable disposable = model.getAdEntryAll(appId, flag, uid, new SplashContract.Callback() {
            @Override
            public void success(List<AdEntryBean> adEntryBeans) {

                if (adEntryBeans.size() != 0) {

                    AdEntryBean adEntryBean = adEntryBeans.get(0);
                    if (adEntryBean.getResult().equals("1")) {

                        view.getAdEntryAllComplete(adEntryBean);
                    }
                }
            }

            @Override
            public void error(Exception e) {

                Log.d("splash", e.toString());
            }
        });
        addSubscribe(disposable);
    }
}
