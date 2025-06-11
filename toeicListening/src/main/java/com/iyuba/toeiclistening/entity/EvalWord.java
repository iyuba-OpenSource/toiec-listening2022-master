package com.iyuba.toeiclistening.entity;

/**
 *
 * 评测句子中的每一个单词
 * 包含：
 *      单词内容
 *      单词index
 *      单词评测得分
 */
public class EvalWord {
    private String content;
    private float score;
    private int index;
    private final int length;
    private String pron;  // 正确音标(全英文字母大写模式，用于纠音上传参数)
    private String userPron;  // 用户发音音标(全英文字母大写模式，用于纠音上传参数)
    private String pron2;  // 正确音标 (音标模式，用于前端展示)
    private String userPron2;  // 用户发音音标(音标模式，用于前端展示)
    private String insert;  // 用户多发出来的音的音标
    private String delete;  // 用户少发的音的音标

    public EvalWord(String content, float score, int index) {
        this.content = content;
        this.score = score;
        this.index = index;
        length = content.length();
        pron = "";
        userPron = "";
        pron2 = "";
        userPron2 = "";
        insert = "";
        delete = "";
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getLength() {
        return length;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getUserPron() {
        return userPron;
    }

    public void setUserPron(String userPron) {
        this.userPron = userPron;
    }

    public String getPron2() {
        return pron2;
    }

    public void setPron2(String pron2) {
        this.pron2 = pron2;
    }

    public String getUserPron2() {
        return userPron2;
    }

    public void setUserPron2(String userPron2) {
        this.userPron2 = userPron2;
    }

    public String getInsert() {
        return insert;
    }

    public void setInsert(String insert) {
        this.insert = insert;
    }

    public String getDelete() {
        return delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    @Override
    public String toString() {
        return "EvalWord{" +
                "content='" + content + '\'' +
                ", score=" + score +
                ", index=" + index +
                ", length=" + length +
                ", pron='" + pron + '\'' +
                ", userPron='" + userPron + '\'' +
                ", pron2='" + pron2 + '\'' +
                ", userPron2='" + userPron2 + '\'' +
                ", insert='" + insert + '\'' +
                ", delete='" + delete + '\'' +
                '}';
    }
}
