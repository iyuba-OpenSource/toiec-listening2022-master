package com.iyuba.core.common.sqlite.mode;

import com.google.gson.annotations.SerializedName;
import com.iyuba.core.common.network.Shareable;

import java.io.Serializable;

public class EvaluateRecord implements Serializable ,Shareable {

    @SerializedName("sentence")
    public String sentence;
    @SerializedName("paraid")
    public String paraid;
    @SerializedName("score")
    public String score;
    @SerializedName("newsid")
    public String newsid;
    @SerializedName("idindex")
    public String idindex;
    @SerializedName("id")
    public String id;
    @SerializedName("createTime")
    public String createTime;
    @SerializedName("newstype")
    public String newstype;

    private String Sentence = "";
    private String Paraid = "";
    private String Score = "";
    private String Newsid = "";
    private String Idindex = "";
    private String Id = "";
    private String CreateTime = "";
    private String Newstype = "";


    public EvaluateRecord() {

    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence=sentence;
    }

    public String getParaid() {
        return paraid;
    }

    public void setParaid(String paraid) {
        this.paraid=paraid;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score=score;
    }

    public String getNewsid() {
        return newsid;
    }

    public void setNewsid(String newsid) {
        this.newsid=newsid;
    }

    public String getIdindex() {
        return idindex;
    }

    public void setIdindex(String idindex) {
        this.idindex=idindex;
    }

    public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id=id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime=createTime;
    }

    public String getNewstype() {
        return newstype;
    }

    public void setNewstype(String newstype) {
        this.newstype=newstype;
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
