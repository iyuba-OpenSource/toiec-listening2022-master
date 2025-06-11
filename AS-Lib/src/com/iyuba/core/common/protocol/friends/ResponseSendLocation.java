/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.VOABaseJsonResponse;

/**
 * @author yao
 * 
 */
public class ResponseSendLocation extends VOABaseJsonResponse {
	public String result;// 返回代码
	public String message;// 返回信息

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		JSONObject jsonBody = null;
		try {
			jsonBody = new JSONObject(bodyElement);
		} catch (JSONException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}
		try {
			result = jsonBody.getString("result");
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			message = jsonBody.getString("message");
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}
}
