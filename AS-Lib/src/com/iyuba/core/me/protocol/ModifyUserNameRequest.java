package com.iyuba.core.me.protocol;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/11/2.
 */

public class ModifyUserNameRequest extends BaseJSONRequest {
    private String protocol = "10012";
    private String sign;

    public ModifyUserNameRequest(String uid, String oldUserName, String userName) {
        sign = protocol + uid + "iyubaV2";

//        if (oldUserName.trim().equals(""))
//            oldUserName = "";
//        String para1[] = oldUserName.split(" ");
//        StringBuffer keyBuffer = new StringBuffer();
//        for (int i = 0; i < para1.length - 1; i++) {
//            try {
//                keyBuffer.append(URLEncoder.encode(para1[i], "UTF-8")).append("%20");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            keyBuffer.append(URLEncoder.encode(para1[para1.length - 1], "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        oldUserName=keyBuffer.toString();
//
//
//        if (userName.trim().equals(""))
//            userName = "";
//        String para2[] = userName.split(" ");
//        keyBuffer = new StringBuffer();
//        for (int i = 0; i < para2.length - 1; i++) {
//            try {
//                keyBuffer.append(URLEncoder.encode(para2[i], "UTF-8")).append("%20");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            keyBuffer.append(URLEncoder.encode(para2[para2.length - 1], "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        userName=keyBuffer.toString();

        try {
            setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?format=json&protocol=" +
                    protocol +
                    "&uid=" +
                    uid +
                    "&oldUsername=" +
                    URLEncoder.encode(oldUserName, "UTF-8") +
                    "&username=" +
                    URLEncoder.encode(userName, "UTF-8") +
                    "&sign=" +
                    MD5.getMD5ofStr(sign));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
//        setAbsoluteURI("");

        try {
            Log.e("modify", "http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?format=json&protocol=" +
                    protocol +
                    "&uid=" +
                    uid +
                    "&oldUsername=" +
                    URLEncoder.encode(oldUserName, "UTF-8") +
                    "&username=" +
                    URLEncoder.encode(userName, "UTF-8") +
                    "&sign=" +
                    MD5.getMD5ofStr(sign));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseHttpResponse createResponse() {
        return new ModifyUserNameResponse();
    }

    @Override
    protected void fillBody(JSONObject jsonObject) throws JSONException {

    }
}
