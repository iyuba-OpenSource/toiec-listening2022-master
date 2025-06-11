package com.iyuba.core.util;

import android.content.Context;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AdBlocker {
    private static final long TWO_DAYS = 1000 * 60 * 60 * 24 * 2L;
    private static SimpleDateFormat GLOBAL_SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    private static final String COMPILE_DATETIME = "2019-05-31 12:35:44";

    public static boolean shouldBlockAd(Context appContext) {
        Date now = new Date();
        Date target;
        try {
            target = getBlockDate(appContext);
        } catch (ParseException e) {
            e.printStackTrace();
            target = now;
        }
        return now.before(target);
    }

    //true 0  false 9
    private static Date getBlockDate(Context appContext) throws ParseException {
        if (appContext == null) {
            return GLOBAL_SDF.parse("1970-01-01 01:00:00");
        } else {
            Date compileDate = GLOBAL_SDF.parse(COMPILE_DATETIME);
            return new Date(compileDate.getTime() + TWO_DAYS);
        }
    }
}
