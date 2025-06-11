package com.iyuba.toeiclistening.mvp.presenter;


import com.iyuba.toeiclistening.mvp.model.WordAnswerModel;
import com.iyuba.toeiclistening.mvp.view.WordAnswerContract;

public class WordAnswerPresenter extends BasePresenter<WordAnswerContract.WordAnswerView, WordAnswerContract.WordAnswerModel>
        implements WordAnswerContract.WordAnswerPresenter {

    @Override
    protected WordAnswerContract.WordAnswerModel initModel() {
        return new WordAnswerModel();
    }
}
