package com.iyuba.toeiclistening.util;

import com.iyuba.configation.ConfigManager;

public class VipUtil {


    public static boolean isVip() {

        String expireTime = ConfigManager.Instance().loadString("expireTime");
        String vipStatus = ConfigManager.Instance().loadString("vipStatus");


        if (vipStatus.equals("") && !vipStatus.equals("0")) {

            return false;
        } else {

            if (expireTime.equals("") || expireTime.equals("0")) {

                return false;
            } else {

                if (expireTime.length() == 10) {

                    expireTime = expireTime + "000";
                }

                if (System.currentTimeMillis() < Long.parseLong(expireTime)) {

                    return true;
                } else {


                    return false;
                }
            }
        }
    }
}
