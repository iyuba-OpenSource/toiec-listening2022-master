package com.iyuba.core.me.goldvip;

import android.content.Context;
import android.content.res.TypedArray;

public class ResourcesArrayUtil {

    public static int[] fromTypedArray(TypedArray ta) {
        return fromTypedArray(ta, 0);
    }

    public static int[] fromTypedArray(TypedArray ta, int defaultResId) {
        int[] result;
        int length = ta.length();
        if (length > 0) {
            result = new int[length];
            for (int i = 0; i < length; i += 1) {
                result[i] = ta.getResourceId(i, defaultResId);
            }
        } else {
            result = new int[]{};
        }
        ta.recycle();
        return result;
    }

    public static int[] fromTypedArray(Context context, int arrayId) {
        return fromTypedArray(context, arrayId, 0);
    }

    public static int[] fromTypedArray(Context context, int arrayId, int defaultResId) {
        TypedArray ta = context.getResources().obtainTypedArray(arrayId);
        return fromTypedArray(ta, defaultResId);
    }
}
