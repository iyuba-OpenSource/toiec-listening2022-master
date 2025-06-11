package com.iyuba.toeiclistening.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.ToeicApplication;
import com.iyuba.toeiclistening.entity.Text;
import com.iyuba.toeiclistening.widget.SelectWordTextView;

import java.util.List;

public class SentenceAdapter extends BaseQuickAdapter<Text, BaseViewHolder> {

    private int position = 0;

    private Callback callback;

    public SentenceAdapter(int layoutResId, @Nullable List<Text> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, Text item) {


        SelectWordTextView sentence_tv_en = helper.getView(R.id.sentence_tv_en);
        sentence_tv_en.setOnClickWordListener(new SelectWordTextView.OnClickWordListener() {
            @Override
            public void onClickWord(String word) {

                if (callback != null) {

                    callback.getWord(word);
                }
            }
        });

        sentence_tv_en.clearText();
        helper.setText(R.id.sentence_tv_en, item.Sentence);
        helper.setText(R.id.sentence_tv_cn, item.Sentence);

        if (helper.getBindingAdapterPosition() == position) {

            helper.setTextColor(R.id.sentence_tv_en, ToeicApplication.getApplication().getColor(R.color.colorPrimary));
            helper.setTextColor(R.id.sentence_tv_cn, ToeicApplication.getApplication().getColor(R.color.colorPrimary));
        } else {

            helper.setTextColor(R.id.sentence_tv_en, Color.BLACK);
            helper.setTextColor(R.id.sentence_tv_cn, Color.BLACK);
        }
    }


    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public interface Callback {

        //获取点击的单词
        void getWord(String word);
    }
}
