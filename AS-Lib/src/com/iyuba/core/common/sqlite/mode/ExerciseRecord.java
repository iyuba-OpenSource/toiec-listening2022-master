package com.iyuba.core.common.sqlite.mode;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.network.Shareable;

import java.io.Serializable;

public class ExerciseRecord implements Serializable ,Shareable {

    @SerializedName("LessonId")
    public String lessonId;
    @SerializedName("TestNumber")
    public String testNumber;
    @SerializedName("UserAnswer")
    public String userAnswer;
    @SerializedName("AppName")
    public String appName;

    private String LessonId = "";
    private String TestNumber = "";
    private String UserAnswer = "";

    public ExerciseRecord() {

    }

    public String getLessonId() {
        return LessonId;
    }

    public void setLessonId(String lessonId) {
        LessonId=lessonId;
    }

    public String getTestNumber() {
        return TestNumber;
    }

    public void setTestNumber(String testNumber) {
        TestNumber=testNumber;
    }

    public String getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(String userAnswer) {
        this.userAnswer=userAnswer;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName=appName;
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
