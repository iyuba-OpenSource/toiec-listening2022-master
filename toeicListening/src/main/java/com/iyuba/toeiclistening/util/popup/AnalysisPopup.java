package com.iyuba.toeiclistening.util.popup;

import android.content.Context;
import android.view.View;

import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.RememberWord;

import razerdp.basepopup.BasePopupWindow;


public class AnalysisPopup extends BasePopupWindow {

    private com.iyuba.toeiclistening.databinding.PopupAnalysisBinding popupAnalysisBinding;

    public AnalysisPopup(Context context) {
        super(context);
        View view = createPopupById(R.layout.popup_analysis);
        popupAnalysisBinding = com.iyuba.toeiclistening.databinding.PopupAnalysisBinding.bind(view);
        setContentView(view);

        initOperation();
    }

    private void initOperation() {

        popupAnalysisBinding.analysisIvX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
            }
        });
    }


    public void setJpWord(RememberWord word) {

        popupAnalysisBinding.analysisTvWord.setText(word.word);
        popupAnalysisBinding.analysisTvPron.setText("[" + word.pron + "]");
        popupAnalysisBinding.analysisTvWordch.setText(word.explain);
//        popupAnalysisBinding.analysisTvSentence.setText(word.getSentence());
//        popupAnalysisBinding.analysisTvSentencech.setText(word.getSentenceCh());
    }


}
