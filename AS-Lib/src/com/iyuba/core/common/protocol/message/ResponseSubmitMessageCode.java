/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * @author yao result: 验证的结果，1为验证成功，0为验证失败。 checkResultCode:
 *         验证结果分类，1为验证成功，0为验证码错误，-1为验证码已过期 message: 返回验证结果提示信息
 */
public class ResponseSubmitMessageCode extends BaseJSONResponse {

	public String result;
	public int res_code;
	public String userphone;
	public String identifier;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			Log.e("jsonObjectRoot", ""+jsonObjectRoot);
			if (jsonObjectRoot != null && jsonObjectRoot.has("result")) {
				result = jsonObjectRoot.getString("result");
			}
			if (!result.equals("101")) {//101为注册过  result.equals("1")||
				if (jsonObjectRoot.has("res_code")) {
					res_code = jsonObjectRoot.getInt("res_code");
				}
				if (jsonObjectRoot.has("userphone")) {
					userphone = jsonObjectRoot.getString("userphone");
				}
				if (jsonObjectRoot.has("identifier")) {
					identifier = jsonObjectRoot.getString("identifier");
				}

				Log.e("res_code", res_code + "");
				Log.e("userphone", userphone + "");
				Log.e("identifier", identifier + "");
			} else {
				res_code = -1;
				userphone = "";
				identifier = "";
			}
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}

}
