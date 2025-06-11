package com.iyuba.toeiclistening.api;

import java.util.List;

public class UpdateWordsRecordBeanList {

    public String DeviceId;
    public String mode = "2";
    public String appId = "224";
    public String appName = "ToeicListening";
    public String format = "json";
    public String lesson = "toeic";
    public String uid;
    public String sign;

//    private ArrayList<UpdateTestRecordInputBean> datalist;
//
//    public ArrayList getDatalist() {
//        return datalist;
//    }
//
//    public void setDatalist(ArrayList datalist) {
//        this.datalist=datalist;
//    }

    public List<UpdateWordsRecordTestBean> testList;

    public List<UpdateWordsRecordScoreBean> scoreList;
}
