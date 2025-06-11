/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * @author yao result: 验证的结果，1为验证成功，0为验证失败。 checkResultCode:
 *         验证结果分类，1为验证成功，0为验证码错误，-1为验证码已过期 message: 返回验证结果提示信息
 */
public class ResponseCheckMessageCode extends BaseJSONResponse {

	public String result;
	public int checkResultCode;
	public String message;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			checkResultCode = jsonObjectRoot.getInt("checkResultCode");
			message = jsonObjectRoot.getString("message");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}

}
