package com.iyuba.toeiclistening.api;

//忘记密码 请求验证码
public class YzPhoneResultForget {

    /**
     * expireTime : 1543219991
     * uid : 6036246
     * message : success
     * result : 101
     * Amount : 0
     * username : 18841603114zh
     * vipStatus : 0
     * email :
     * imgSrc : http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=6036246
     * money : 0
     * isteacher : 0
     * mobile : 18841603114
     */

    private int expireTime;
    private int uid;
    private String message;
    private String result;
    private String Amount;
    private String username;
    private String vipStatus;
    private String email;
    private String imgSrc;
    private String money;
    private String isteacher;
    private String mobile;

    public int getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(int expireTime) {
        this.expireTime = expireTime;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
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

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String Amount) {
        this.Amount = Amount;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getVipStatus() {
        return vipStatus;
    }

    public void setVipStatus(String vipStatus) {
        this.vipStatus = vipStatus;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImgSrc() {
        return imgSrc;
    }

    public void setImgSrc(String imgSrc) {
        this.imgSrc = imgSrc;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getIsteacher() {
        return isteacher;
    }

    public void setIsteacher(String isteacher) {
        this.isteacher = isteacher;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
