/**
 *
 */
package com.iyuba.core.common.util;

import android.content.Context;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.core.R;

/**
 * 根据积分判断等级
 *
 * @author 陈彤
 */
public class CheckGrade {

    public static int Check(String points) {
        int i = 0, j = 0;
        i = Integer.parseInt(points);
        if (i >= 0 && i < 51) {
            j = 1;
        } else if (i > 50 && i < 201) {
            j = 2;
        } else if (i > 200 && i < 501) {
            j = 3;
        } else if (i > 500 && i < 1001) {
            j = 4;
        } else if (i > 1000 && i < 2001) {
            j = 5;
        } else if (i > 2000 && i < 4001) {
            j = 6;
        } else if (i > 4000 && i < 7001) {
            j = 7;
        } else if (i > 7000 && i < 12001) {
            j = 8;
        } else if (i > 12000 && i < 20001) {
            j = 9;
        } else if (i > 20001) {
            j = 10;
        } else {
            j = 1;
        }
        return j;
    }

    public static String CheckLevelName(String points) {
        String[] name = RuntimeManager.getContext().getResources()
                .getStringArray(R.array.level);
        return name[Check(points) - 1];
    }

    public static String CheckLevelName(Context context, String points) {
        String[] name = context.getResources()
                .getStringArray(R.array.level);
        return name[Check(points) - 1];
    }
}
