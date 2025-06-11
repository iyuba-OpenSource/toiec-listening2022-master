/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * @author
 * 
 */
public class ResponseCancelAttention extends BaseJSONResponse {

	public String result;// 返回代码
	public String message;// 返回信息

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getString("result");

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
