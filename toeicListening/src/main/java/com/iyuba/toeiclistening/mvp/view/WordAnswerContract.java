package com.iyuba.toeiclistening.mvp.view;


import com.iyuba.toeiclistening.mvp.model.BaseModel;
import com.iyuba.toeiclistening.mvp.presenter.IBasePresenter;

public interface WordAnswerContract {


    interface WordAnswerView extends LoadingView {

    }

    interface WordAnswerPresenter extends IBasePresenter<WordAnswerView> {


    }


    interface WordAnswerModel extends BaseModel {

    }
}

