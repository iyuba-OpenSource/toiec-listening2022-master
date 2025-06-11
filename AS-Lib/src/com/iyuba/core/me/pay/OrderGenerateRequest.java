package com.iyuba.core.me.pay;

import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

public class OrderGenerateRequest extends BaseJsonObjectRequest {
    private static final String TAG = OrderGenerateRequest.class.getSimpleName();
    //    public String productId;
    public String result;
    public String message;
    public String alipayTradeStr;

    public OrderGenerateRequest(int app_id, int userId, String code, String WIDtotal_fee, int amount,
                                int product_id, String WIDbody, String WIDsubject,
                                ErrorListener el, final RequestCallBack rc) {

        super(Request.Method.POST, "http://vip.iyuba.cn/alipay.jsp?"
                        + String.format(
                        "app_id=%s&userId=%s&code=%s&WIDtotal_fee=%s&amount=%s&product_id=%s&WIDbody=%s&WIDsubject=%s"
                        , app_id, userId, code, WIDtotal_fee, amount, product_id, WIDbody, WIDsubject
                )
                , el);


        setResListener(new Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObjectRoot) {
                try {
                    result = jsonObjectRoot.getString("result");
                    message = jsonObjectRoot.getString("message");
                    alipayTradeStr = jsonObjectRoot.getString("alipayTradeStr");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rc.requestResult(OrderGenerateRequest.this);
            }
        });
    }

    @Override
    public boolean isRequestSuccessful() {
        return "200".equals(result);
    }

    private static String generateCode(String userId) {
        String code = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        code = MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
        return code;
    }

}
