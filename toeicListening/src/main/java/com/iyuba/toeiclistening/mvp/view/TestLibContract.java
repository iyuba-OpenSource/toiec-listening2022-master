package com.iyuba.toeiclistening.mvp.view;

import com.iyuba.toeiclistening.mvp.model.BaseModel;
import com.iyuba.toeiclistening.mvp.model.bean.AdEntryBean;
import com.iyuba.toeiclistening.mvp.presenter.IBasePresenter;

import java.util.List;

import io.reactivex.disposables.Disposable;

public interface TestLibContract {


    interface TestLibView extends LoadingView {

        void getAdEntryAllComplete(AdEntryBean adEntryBean);
    }

    interface TestLibPresenter extends IBasePresenter<TestLibView> {

        void getAdEntryAll(String appId, int flag, String uid);
    }

    interface TestLibModel extends BaseModel {

        Disposable getAdEntryAll(String appId, int flag, String uid, SplashContract.Callback callback);
    }

    interface Callback {

        void success(List<AdEntryBean> adEntryBeans);

        void error(Exception e);
    }
}
