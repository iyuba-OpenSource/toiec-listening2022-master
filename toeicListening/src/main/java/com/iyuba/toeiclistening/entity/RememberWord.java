package com.iyuba.toeiclistening.entity;


import com.iyuba.core.common.sqlite.mode.Word;

import java.io.Serializable;

public class RememberWord implements Serializable {

    public String word;
    public String pron;
    public String explain;
    public String explainEn;
    public String sentence;
    public String sentenceCn;
    public String sentenceSound;
    public String sound;//？

    public boolean isCollect;//0 未收藏 1 收藏了
    public int statues;//单词的状态，0:没学过 1:学了没记住 2：学了记住啦
    public int stage;//属于那一个分组
    public int flag;//预留的

    public boolean isChange = false;

    /**
     * 答题状态
     * 0：未答题
     * 1：回答正确
     * 2：回答错误
     */
    public int answer_status;


    public int getAnswer_status() {
        return answer_status;
    }

    public void setAnswer_status(int answer_status) {
        this.answer_status = answer_status;
    }

    public Word getWord(String uId) {
        Word word1 = new Word();
        word1.userid = uId;
        word1.key = word; // 关键??
        word1.lang = "";
        word1.audioUrl = sound; // 多媒体网络路??
        word1.pron = pron; // 音标
        word1.def = explain; // 解释
        word1.examples = sentence; // 例句，多条以??”分??
        word1.createDate = ""; // 创建时间
        return word1;
    }




}
