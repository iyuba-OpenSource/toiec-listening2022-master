package com.iyuba.core.common.sqlite.mode;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.network.Shareable;

import java.io.Serializable;

public class ListeningTestRecord implements Serializable ,Shareable {

    @SerializedName("LessonId")
    public String lessonId;
    @SerializedName("TestNumber")
    public String testNumber;
    @SerializedName("TestWords")
    public String testWords;
    @SerializedName("EndFlg")
    public String endFlg;

    private String LessonId = "";
    private String TestNumber = "";
    private String EndFlg = "";
    private String TestWords = "";

    public ListeningTestRecord() {

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

    public String getEndFlg() {
        return EndFlg;
    }

    public void setEndFlg(String endFlg) {
        EndFlg=endFlg;
    }

    public String getTestWords() {
        return TestWords;
    }

    public void setTestWords(String testWords) {
        TestWords=testWords;
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
