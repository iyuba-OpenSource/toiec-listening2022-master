package com.iyuba.core.common.network;

import com.google.gson.annotations.SerializedName;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.Constant;

public class SpeakRankWork implements Shareable {

    @SerializedName("id")
    public int id;
    @SerializedName("agreeCount")
    public int agreeCount = 0;
    @SerializedName("againstCount")
    public int againstCount = 0;
    @SerializedName("ShuoShuo")
    public String shuoshuo = "";
    @SerializedName("shuoshuotype")
    public int shuoshuoType;
    @SerializedName("CreateDate")
    public String createdate;
    @SerializedName("score")
    public int score = 0; // fuck
    @SerializedName("paraid")
    public int paraid = 0; // fuck
    @SerializedName("idIndex")
    public int idindex = 0; // fuck
    @SerializedName("TopicId")
    public int topicId;

    public String imgsrc = "";
    public String title = "";
    public String titleCn = "";
    public String description = "";

    public int userid;
    public String username = "none";

    public String readText = "";

    public boolean isAudioCommentPlaying = false;

    public String getShuoShuoUrl() {
        return "http://voa."+ WebConstant.CN_SUFFIX +"voa/" + shuoshuo;
    }

    public String getUsername() {
        return username;
    }

    public String getUpvoteCount() {
        return String.valueOf(agreeCount);
    }

    public String getDownvoteCount() {
        return String.valueOf(againstCount);
    }

    public String getScore() {
        return score + "分";
    }

    @Override
    public String getShareUrl(String appName) {
        if (Constant.BROADCAST_URL.lastIndexOf("?") == Constant.BROADCAST_URL.length() - 1) {
            return Constant.BROADCAST_URL + "id=" + topicId + "&shuoshuo=" + shuoshuo + "&apptype="
                    + "toeic";
        } else {
            return Constant.BROADCAST_URL + "&id=" + topicId + "&shuoshuo=" + shuoshuo+ "&apptype="
                    + "toeic";
        }
        //return "分享地址";
    }

    @Override
    public String getShareImageUrl() {
        return imgsrc;
    }

    @Override
    public String getShareAudioUrl() {
        return "";
    }

    @Override
    public String getShareTitle() {
        return "播音员:" + username + "。标题:" + titleCn;
    }

    @Override
    public String getShareLongText() {
        return getShareShortText();
    }

    @Override
    public String getShareShortText() {
        return "@爱语吧 " + username + "在语音评测中得了" + score + "分。标题:" + titleCn + title + ":" +
                description + " 下载地址:" + getShareUrl(Constant.AppName);
    }
}
