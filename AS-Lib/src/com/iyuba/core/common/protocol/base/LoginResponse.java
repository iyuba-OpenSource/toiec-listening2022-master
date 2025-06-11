package com.iyuba.core.common.protocol.base;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

public class LoginResponse extends BaseXMLResponse {
    public String result, uid, username, imgsrc, vip, validity, amount, isteacher, money;

    public LoginResponse() {
    }

    public LoginResponse(String result, String uid, String username, String imgsrc, String vip, String validity, String amount, String isteacher, String money) {
        this.result = result;
        this.uid = uid;
        this.username = username;
        this.imgsrc = imgsrc;
        this.vip = vip;
        this.validity = validity;
        this.amount = amount;
        this.isteacher = isteacher;
        this.money = money;
    }

    @Override
    protected boolean extractBody(kXMLElement headerEleemnt,
                                  kXMLElement bodyElement) {
        // TODO Auto-generated method stub
        try {
            result = Utility.getSubTagContent(bodyElement, "result");
            uid = Utility.getSubTagContent(bodyElement, "uid");
            username = Utility.getSubTagContent(bodyElement, "username");
            imgsrc = Utility.getSubTagContent(bodyElement, "imgSrc");
            vip = Utility.getSubTagContent(bodyElement, "vipStatus");
            validity = Utility.getSubTagContent(bodyElement, "expireTime");
            amount = Utility.getSubTagContent(bodyElement, "Amount");
            isteacher = Utility.getSubTagContent(bodyElement, "isteacher");
            money = Utility.getSubTagContent(bodyElement, "money");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

}
