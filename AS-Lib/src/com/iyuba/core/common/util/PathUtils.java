package com.iyuba.core.common.util;

import android.content.Context;

public class PathUtils {

    public static String getGlideCache(Context context) {
        return context.getExternalCacheDir() + "/glide/";
    }

    public static String getAppUpdatePath(Context context) {
        return context.getExternalFilesDir(null) + "/appUpdate/";
    }

    public static String getRecordPath(Context context) {
        return context.getExternalFilesDir(null) + "/record/";
    }

    public static String getAdPath(Context context) {
        return context.getExternalFilesDir(null) + "/ad/";
    }

    public static String getAudioPath(Context context) {
        return context.getExternalFilesDir(null) + "/audio/";
    }

    public static String getAvatarPath(Context context) {
        return context.getExternalFilesDir(null) + "/avatar/";
    }

    public static String getSharePath(Context context) {
        return context.getExternalFilesDir(null) + "/share/";
    }

    public static String getExtraPath(Context context) {
        return context.getExternalFilesDir(null) + "/extra/";
    }
}
