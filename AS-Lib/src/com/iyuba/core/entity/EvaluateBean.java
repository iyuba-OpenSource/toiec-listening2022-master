package com.iyuba.core.entity;



import com.google.gson.annotations.SerializedName;
import com.iyuba.configation.WebConstant;

import java.io.Serializable;
import java.util.List;


/**
 * evaluating 评测成功数据
 */
public class EvaluateBean {

    /**
     * sentence : Hello Banin,
     * total_score : 2.5
     * word_count : 1
     * URL : wav/voa/580497974067406.wav
     * words : [{"content":"HELLO","index":0,"score":2.5}]
     */

    private int position;
    private String sentence;
    public String total_score;//总分
    private String paraId;
    public String IdIndex;
    public String voaId;
    private int word_count;
    public String URL;
    private int time;
    private String nLocalMP3Path;
    public int textNum;
    private List<WordsBean> words;

    public String textScore;

    public String getTextScore() {
        return textScore;
    }

    public void setTextScore(String textScore) {
        this.textScore = textScore;
    }

    public String getnLocalMP3Path() {
        return nLocalMP3Path;
    }

    public void setnLocalMP3Path(String nLocalMP3Path) {
        this.nLocalMP3Path = nLocalMP3Path;
    }

    public String getVoaId() {
        return voaId;
    }

    public void setVoaId(String voaId) {
        this.voaId = voaId;
    }

    public String getIdIndex() {
        return IdIndex;
    }

    public void setIdIndex(String idIndex) {
        IdIndex = idIndex;
    }

    public String getParaId() {
        return paraId;
    }

    public void setParaId(String paraId) {
        this.paraId = paraId;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getTotal_score() {
        return total_score;
    }

    public void setTotal_score(String total_score) {
        this.total_score = total_score;
    }

    public int getWord_count() {
        return word_count;
    }

    public void setWord_count(int word_count) {
        this.word_count = word_count;
    }

    public String getURL() {
        return "http://voa."+ WebConstant.CN_SUFFIX +"voa/" +URL;
    }

    public String getSendUrl(){
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public List<WordsBean> getWords() {
        return words;
    }

    public void setWords(List<WordsBean> words) {
        this.words = words;
    }

    public static class WordsBean implements Serializable{
        /**
         * content : HELLO
         * index : 0
         * score : 2.5
         */

        private String content;
        private int index;
        private double score;
        private String pron;  // 正确音标(全英文字母大写模式，用于纠音上传参数)
        @SerializedName("user_pron")
        private String userPron;  // 用户发音音标(全英文字母大写模式，用于纠音上传参数)
        @SerializedName("pron2")
        private String pron2;  // 正确音标 (音标模式，用于前端展示)
        @SerializedName("user_pron2")
        private String userPron2;  // 用户发音音标(音标模式，用于前端展示)
        private String insert;  // 用户多发出来的音的音标
        private String delete;  // 用户少发的音的音标
        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
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
    }
}
