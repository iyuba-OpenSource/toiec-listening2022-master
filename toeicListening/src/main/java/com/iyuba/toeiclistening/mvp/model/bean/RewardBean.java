package com.iyuba.toeiclistening.mvp.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RewardBean {


    @SerializedName("result")
    private int result;
    @SerializedName("data")
    private List<DataDTO> data;
    @SerializedName("message")
    private String message;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public List<DataDTO> getData() {
        return data;
    }

    public void setData(List<DataDTO> data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DataDTO {
        @SerializedName("voaid")
        private String voaid;
        @SerializedName("score")
        private String score;
        @SerializedName("time")
        private String time;
        @SerializedName("type")
        private String type;
        @SerializedName("srid")
        private String srid;

        public String getVoaid() {
            return voaid;
        }

        public void setVoaid(String voaid) {
            this.voaid = voaid;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSrid() {
            return srid;
        }

        public void setSrid(String srid) {
            this.srid = srid;
        }
    }
}
