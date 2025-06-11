package com.iyuba.toeiclistening.mvp.model.bean;

import com.google.gson.annotations.SerializedName;

public class TestRecordBean {


    @SerializedName("result")
    private String result;
    @SerializedName("jiFen")
    private String jiFen;
    @SerializedName("message")
    private String message;

    private String reward;

    private String rewardMessage;


    public String getReward() {
        return reward;
    }

    public void setReward(String reward) {
        this.reward = reward;
    }

    public String getRewardMessage() {
        return rewardMessage;
    }

    public void setRewardMessage(String rewardMessage) {
        this.rewardMessage = rewardMessage;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getJiFen() {
        return jiFen;
    }

    public void setJiFen(String jiFen) {
        this.jiFen = jiFen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
