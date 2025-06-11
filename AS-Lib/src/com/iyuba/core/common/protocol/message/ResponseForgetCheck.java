/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author zh result:
 * 验证的结果，101为验证成功，102 没有注册过 不能通过。
 */
public class ResponseForgetCheck extends BaseJSONResponse {

	public String result;
	public String userName;
	public String userPhoto;

	@Override
	protected boolean extractBody(JSONObject headerElement, String bodyElement) {
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			if (jsonObjectRoot.has("result")) {
				result = jsonObjectRoot.getString("result");
			}
			if (result.equals("101")) {
				if (jsonObjectRoot.has("username")) {
					userName = jsonObjectRoot.getString("username");
				}
				if (jsonObjectRoot.has("imgSrc")) {
					userPhoto = jsonObjectRoot.getString("imgSrc");
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			Log.e("RequestForgetCode",e1.toString());
		}
		return true;
	}

}
