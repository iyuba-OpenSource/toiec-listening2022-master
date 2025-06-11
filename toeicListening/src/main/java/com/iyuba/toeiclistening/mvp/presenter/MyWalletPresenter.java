package com.iyuba.toeiclistening.mvp.presenter;


import com.iyuba.toeiclistening.mvp.model.MyWalletModel;
import com.iyuba.toeiclistening.mvp.model.bean.RewardBean;
import com.iyuba.toeiclistening.mvp.view.MyWalletContract;

import io.reactivex.disposables.Disposable;

public class MyWalletPresenter extends BasePresenter<MyWalletContract.MyWalletView, MyWalletContract.MyWalletModel>
        implements MyWalletContract.MyWalletPresenter {


    @Override
    protected MyWalletContract.MyWalletModel initModel() {
        return new MyWalletModel();
    }

    @Override
    public void getUserActionRecord(int uid, int pages, int pageCount, String sign) {

        Disposable disposable = model.getUserActionRecord(uid, pages, pageCount, sign, new MyWalletContract.WalletCallback() {

            @Override
            public void success(RewardBean rewardBean) {

                if (rewardBean.getResult() == 200) {

                    view.wallet(pages, rewardBean.getData());
                }
            }

            @Override
            public void error(Exception e) {

                view.wallet(pages, null);
            }
        });
        addSubscribe(disposable);
    }
}
