package com.iyuba.core.me.pay;

import android.util.Log;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.facebook.stetho.common.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;

public class OrderGenerateWeiXinRequest extends BaseJsonObjectRequest {
    private static final String TAG = OrderGenerateWeiXinRequest.class.getSimpleName();
    private static final String newApi = "http://vip."+com.iyuba.core.util.Constant.IYBHttpHead+"/weixinPay.jsp?";
    public String partnerId;
    public String prepayId;
    public String nonceStr;
    public String timeStamp;
    public String mchKey;
    public String result;
    public WeixinOrderInfo orderInfo;

    public OrderGenerateWeiXinRequest(String wxkey, String appId, String userId,String money,
                                      String amount, String productId, String format,  String sign,
                                      final RequestCallBack rc) {
        super(Method.POST, newApi + "weixinApp=" + wxkey + "&appid="
                + appId + "&uid=" + userId + "&money=" + money
                + "&amount=" + amount + "&productid=" + productId + "&format=" + format
                + "&sign=" + sign );

        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    result = jsonObjectRoot.getString("retcode");
                    if (isRequestSuccessful()) {
                        partnerId = URLDecoder.decode(jsonObjectRoot.getString("mch_id"), "utf-8");
                        prepayId = URLDecoder.decode(jsonObjectRoot.getString("prepayid"), "utf-8");
                        nonceStr = URLDecoder.decode(jsonObjectRoot.getString("mch_id"), "utf-8");
                        timeStamp = URLDecoder.decode(jsonObjectRoot.getString("timestamp"), "utf-8");
                        mchKey = URLDecoder.decode(jsonObjectRoot.getString("mch_key"), "utf-8");
                        Log.d("partnerId"+partnerId,"");
                    }
                } catch (JSONException | UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateWeiXinRequest.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        return "0".equals(result);
    }

}
