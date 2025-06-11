package com.iyuba.toeiclistening.mvp.presenter;


import android.util.Log;

import com.iyuba.toeiclistening.mvp.model.SplashModel;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.view.SplashContract;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class SplashPresenter extends BasePresenter<SplashContract.SplashView, SplashContract.SplashModel>
        implements SplashContract.SplashPresenter {


    @Override
    protected SplashContract.SplashModel initModel() {
        return new SplashModel();
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
