package com.iyuba.configation.util;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

public class ToastUtil {

    private static Toast toast;

    public static void showToast(Context context, String text) {
        try {
            if (toast == null) {
                //如果等于null，则创建
                toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            } else {
                //如果不等于空，则直接将text设置给toast
                toast.setText(text);
            }
            toast.show();
        }catch (Exception e){
            Looper.prepare();
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
            Looper.loop();
        }

    }

    public static void showLongToast(Context context, String text) {

        if (toast == null) {
            //如果等于null，则创建
            toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        } else {
            //如果不等于空，则直接将text设置给toast
            toast.setText(text);
        }
        toast.show();
    }


}
