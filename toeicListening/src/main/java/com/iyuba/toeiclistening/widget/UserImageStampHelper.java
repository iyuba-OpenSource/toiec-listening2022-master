package com.iyuba.toeiclistening.widget;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public final class UserImageStampHelper {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
    private static UserImageStampHelper sInstance = new UserImageStampHelper();

    private long currentUserModifyTime;

    public static UserImageStampHelper getInstance() {
        return sInstance;
    }

    private UserImageStampHelper() {
        currentUserModifyTime = System.currentTimeMillis();
    }

    public String getDefaultUserStamp() {
        return SDF.format(new Date());
    }

    public String getCurrentUserStamp() {
        return String.valueOf(currentUserModifyTime);
    }

    public void resetCurrentUserStamp() {
        currentUserModifyTime = System.currentTimeMillis();
    }
}
