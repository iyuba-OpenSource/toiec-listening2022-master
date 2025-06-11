package com.iyuba.core.common.widget.circularimageview;

import android.content.Context;

import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.util.Constant;

public class GetPhotoPath {

    public static String getThisUserPhoto(Context context){
        String userId =AccountManagerLib.Instace(context).userId;
        return "http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
                + userId + "&size=middle";
    }
}
