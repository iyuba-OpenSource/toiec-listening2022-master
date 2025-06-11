package com.iyuba.toeiclistening.api;

import java.util.ArrayList;
import java.util.List;

public class UpdateTestRecordInputBeanList {

    public String DeviceId;
    public String TestMode = "1";
    public String appId = "224";
    public String appName = "ToeicListening";
    public String format = "json";
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

    public List<UpdateTestRecordInputBean> datalist;
}
