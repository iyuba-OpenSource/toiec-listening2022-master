package com.iyuba.core.me.pay;

public class WeixinOrderInfo {
    public String mchkey;
    public String partnerId;
    public String prepayId;
    public String nonceStr;
    public String timeStamp;
    public String sign;

    public WeixinOrderInfo() {
    }

    public WeixinOrderInfo(String mchkey, String partnerId, String prepayId, String nonceStr, String timeStamp, String sign) {
        this.mchkey = mchkey;
        this.partnerId = partnerId;
        this.prepayId = prepayId;
        this.nonceStr = nonceStr;
        this.timeStamp = timeStamp;
        this.sign = sign;
    }
}
