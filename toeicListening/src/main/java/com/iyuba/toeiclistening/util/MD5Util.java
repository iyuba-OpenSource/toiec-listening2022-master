package com.iyuba.toeiclistening.util;

import java.security.MessageDigest;

public class MD5Util {

    public final static String MD5(String s) {

        char hexDigits[]={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};

        try {

            byte[] btInput = s.getBytes("utf-8");

            MessageDigest mdInst = MessageDigest.getInstance("MD5");

            mdInst.update(btInput);

            byte[] md = mdInst.digest();

            int j = md.length;

            char str[] = new char[j * 2];

            int k = 0;

            for (int i = 0; i < j; i++) {

                byte byte0 = md[i];

                str[k++] = hexDigits[byte0 >>> 4 & 0xf];

                str[k++] = hexDigits[byte0 & 0xf];

            }

            return new String(str).toLowerCase();

        } catch (Exception e) {

            return "";

        }

    }

}
