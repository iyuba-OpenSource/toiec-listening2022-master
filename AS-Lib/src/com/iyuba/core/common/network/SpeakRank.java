package com.iyuba.core.common.network;

import android.util.Pair;

import com.google.gson.annotations.SerializedName;

public class SpeakRank implements IRankInfo {
    @SerializedName("uid")
    public int uid;
    @SerializedName("vip")
    public String vip;
    @SerializedName("sort")
    public int sort;
    @SerializedName("count")
    public int count;
    @SerializedName("scores")
    public int score;
    @SerializedName("imgSrc")
    public String imgSrc;
    @SerializedName("name")
    public String name = "";
    @SerializedName("ranking")
    public int ranking;

    public boolean isVip() {
        return !vip.equals("0");
    }

    public int getAverageScore() {
        if (count <= 0) {
            return 0;
        } else {
            return score / count;
        }
    }

    @Override
    public Pair<String, String> getDescriptionInfo() {
        String description = "用户" + name + "口语情况：\n句子数：" + count +
                " 总分：" + score + " 平均分:" + getAverageScore() + " 排名：" + ranking;
        return new Pair<>(description, imgSrc);
    }
}
