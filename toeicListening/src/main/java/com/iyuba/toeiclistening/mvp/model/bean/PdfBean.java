package com.iyuba.toeiclistening.mvp.model.bean;

import com.google.gson.annotations.SerializedName;

public class PdfBean {


    @SerializedName("exists")
    private String exists;
    @SerializedName("path")
    private String path;

    public String getExists() {
        return exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
