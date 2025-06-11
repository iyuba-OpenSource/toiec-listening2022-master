package com.iyuba.core.util;

/**
 * Created by maoyujiao on 2019/3/14.
 */

public class LoginResponse {
    /**
     * Amount : 0
     * mobile : 0
     * message : success
     * result : 101
     * uid : 6271645
     * isteacher : 0
     * expireTime : 1562923094
     * money : 0
     * credits : 0
     * jiFen : 0
     * vipStatus : 1
     * imgSrc : http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=6271645
     * email : 18810305000@163.com
     * username : myj1002
     */

    private String Amount;
    private String mobile;
    private String message;
    private String result;
    private String uid;
    private String isteacher;
    private String expireTime;
    private String money;
    private int credits;
    private int jiFen;
    private String vipStatus;
    private String imgSrc;
    private String email;
    private String username;

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getCredits() {
        return credits;
    }

    public void setCredits(int credits) {
        this.credits = credits;
    }

    public int getJiFen() {
        return jiFen;
    }

    public void setJiFen(int jiFen) {
        this.jiFen = jiFen;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "{" +
                "Amount='" + Amount + '\'' +
                ", mobile='" + mobile + '\'' +
                ", message='" + message + '\'' +
                ", result='" + result + '\'' +
                ", uid=" + uid +
                ", isteacher='" + isteacher + '\'' +
                ", expireTime=" + expireTime +
                ", money='" + money + '\'' +
                ", credits=" + credits +
                ", jiFen=" + jiFen +
                ", vipStatus='" + vipStatus + '\'' +
                ", imgSrc='" + imgSrc + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                '}';
    }
}
