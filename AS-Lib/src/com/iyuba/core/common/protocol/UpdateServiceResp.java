package com.iyuba.core.common.protocol;

import com.google.gson.annotations.SerializedName;

public class UpdateServiceResp {

    @SerializedName("result")
    private int result;
    @SerializedName("updateflg")
    private int updateflg;
    @SerializedName("short1")
    private String short1;
    @SerializedName("short2")
    private String short2;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getUpdateflg() {
        return updateflg;
    }

    public void setUpdateflg(int updateflg) {
        this.updateflg = updateflg;
    }

    public String getShort1() {
        return short1;
    }

    public void setShort1(String short1) {
        this.short1 = short1;
    }

    public String getShort2() {
        return short2;
    }

    public void setShort2(String short2) {
        this.short2 = short2;
    }
}
