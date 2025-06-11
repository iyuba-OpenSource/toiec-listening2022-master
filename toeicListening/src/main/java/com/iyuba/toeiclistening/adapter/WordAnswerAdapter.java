package com.iyuba.toeiclistening.adapter;

import android.graphics.Color;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.iyuba.toeiclistening.R;
import com.iyuba.toeiclistening.entity.RememberWord;

import java.util.List;

/**
 * 单词页面的
 */
public class WordAnswerAdapter extends BaseQuickAdapter<RememberWord, BaseViewHolder> {

    private int type;

    private int choosePosition = -1;

    /**
     * 正确的位置
     */
    private int tPosition = -1;

    public WordAnswerAdapter(int layoutResId, @Nullable List<RememberWord> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, RememberWord item) {

        helper.setTextColor(R.id.answer_tv_content, Color.BLACK);
        if (type == 0) {

            helper.setText(R.id.answer_tv_content, item.word);
        } else {

            helper.setText(R.id.answer_tv_content, item.explain);
        }


        if (choosePosition == -1 && tPosition == -1) {

            helper.setBackgroundRes(R.id.answer_tv_content, R.drawable.shape_rctg_word_answer_common);
        } else {

            if (choosePosition == tPosition && tPosition == helper.getBindingAdapterPosition()) {

                helper.setTextColor(R.id.answer_tv_content, Color.WHITE);
                helper.setBackgroundRes(R.id.answer_tv_content, R.drawable.shape_rctg_word_answer_right);
            } else if (choosePosition != tPosition && choosePosition == helper.getBindingAdapterPosition()) {

                helper.setTextColor(R.id.answer_tv_content, Color.WHITE);
                helper.setBackgroundRes(R.id.answer_tv_content, R.drawable.shape_rctg_word_answer_red);
            } else if (choosePosition != tPosition && tPosition == helper.getBindingAdapterPosition()) {

                helper.setTextColor(R.id.answer_tv_content, Color.WHITE);
                helper.setBackgroundRes(R.id.answer_tv_content, R.drawable.shape_rctg_word_answer_right);
            } else {

                helper.setBackgroundRes(R.id.answer_tv_content, R.drawable.shape_rctg_word_answer_common);
            }
        }
        //显示正确答案的处理
/*        if (choosePosition == -1 && tPosition == -1) {

            helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
        } else {

            if (choosePosition == tPosition) {

                if (helper.getAdapterPosition() == tPosition) {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
                } else {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
                }
            } else {//位置不一致

                if (choosePosition == helper.getAdapterPosition()) {

                    helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_wrong);
                } else {

                    if (tPosition == helper.getAdapterPosition()) {

                        helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer_right);
                    } else {

                        helper.setBackgroundRes(R.id.answer_tv_content, R.mipmap.bg_answer);
                    }
                }
            }
        }*/
    }

    public int gettPosition() {
        return tPosition;
    }

    public void settPosition(int tPosition) {
        this.tPosition = tPosition;
    }

    public int getChoosePosition() {
        return choosePosition;
    }

    public void setChoosePosition(int choosePosition) {
        this.choosePosition = choosePosition;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
