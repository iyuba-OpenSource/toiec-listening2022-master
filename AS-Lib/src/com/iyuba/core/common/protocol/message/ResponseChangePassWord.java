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
 * 验证的结果，131为验证成功，或者101
 */
public class ResponseChangePassWord extends BaseJSONResponse {

	public String result;

	@Override
	protected boolean extractBody(JSONObject headerElement, String bodyElement) {
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			if (jsonObjectRoot.has("result")) {
				result = jsonObjectRoot.getString("result");
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
			Log.e("RequestForgetCode",e1.toString());
		}
		return true;
	}

}
