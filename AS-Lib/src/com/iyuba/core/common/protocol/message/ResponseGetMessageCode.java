/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * @author yao result: 更新结果，1为成功，0为失败(一般是手机号格式错误)。 res_code: 短信发送结果 0为成功，1为失败
 *         userphone: 接收验证码的手机号码 Identifier: 短信版本号（标识符）
 */
public class ResponseGetMessageCode extends BaseJSONResponse {

	public String result;
	public int res_code;
	public String userphone;
	public String identifier;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRoot = new JSONObject(bodyElement);
			result = jsonObjectRoot.getString("result");
			res_code = jsonObjectRoot.getInt("res_code");
			userphone = jsonObjectRoot.getString("userphone");
			identifier = jsonObjectRoot.getString("identifier");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return true;
	}
	/*
	 * @Override protected boolean resolveBodyJson(JSONObject jsonBody) { //
	 * TODO Auto-generated method stub try {
	 * result=jsonBody.getString("result"); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } try {
	 * res_code=jsonBody.getInt("res_code"); } catch (JSONException e) { // TODO
	 * Auto-generated catch block e.printStackTrace(); } try {
	 * userphone=jsonBody.getString("userphone"); } catch (JSONException e) { //
	 * TODO Auto-generated catch block e.printStackTrace(); } try {
	 * identifier=jsonBody.getString("identifier"); } catch (JSONException e) {
	 * // TODO Auto-generated catch block e.printStackTrace(); } return false; }
	 */

}
