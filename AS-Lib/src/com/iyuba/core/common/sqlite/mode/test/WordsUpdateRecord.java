package com.iyuba.core.common.sqlite.mode.test;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.network.Shareable;

import java.io.Serializable;

public class WordsUpdateRecord implements Serializable ,Shareable {

    @SerializedName("TestId")
    public String testId;
    @SerializedName("Score")
    public String score;
    @SerializedName("UserAnswer")
    public String userAnswer;
    @SerializedName("UpdateTime")
    public String updateTime;
    @SerializedName("Lesson")
    public String lesson;

    private String TestId = "";
    private String Score = "";
    private String UserAnswer = "";
    private String UpdateTime = "";
    private String Lesson = "";

    public WordsUpdateRecord() {

    }

    public String getTestId() {
        return testId;
    }

    public void setTestId(String testId) {
        this.testId=testId;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score=score;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer=userAnswer;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime=updateTime;
    }

    public String getLesson() {
        return lesson;
    }

    public void setLesson(String lesson) {
        this.lesson=lesson;
    }

    @Override
    public String getShareUrl(String appName) {
        return null;
    }

    @Override
    public String getShareImageUrl() {
        return null;
    }

    @Override
    public String getShareAudioUrl() {
        return null;
    }

    @Override
    public String getShareTitle() {
        return null;
    }

    @Override
    public String getShareLongText() {
        return null;
    }

    @Override
    public String getShareShortText() {
        return null;
    }
}
