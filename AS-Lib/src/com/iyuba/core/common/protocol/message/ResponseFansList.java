/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.VOABaseJsonResponse;
import com.iyuba.core.me.sqlite.mode.Fans;

/**
 * @author yao 粉丝列表
 */
public class ResponseFansList extends VOABaseJsonResponse {

	public String result;// 返回代码
	public String message;// 返回信息
	public Fans fan = new Fans();
	public JSONArray data;
	public ArrayList<Fans> fansList;
	public int num;

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
		fansList = new ArrayList<Fans>();
		try {
			result = jsonBody.getString("result");
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		try {
			message = jsonBody.getString("message");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result.equals("560")) {
			try {
				num = jsonBody.getInt("num");

			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			try {
				data = jsonBody.getJSONArray("data");
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if (data != null && data.length() != 0) {
				int size = data.length();
				Fans fans;
				JSONObject jsonObject;
				for (int i = 0; i < size; i++) {
					try {
						fans = new Fans();
						jsonObject = ((JSONObject) data.opt(i));
						fans.mutual = jsonObject.getString("mutual");
						fans.uid = jsonObject.getString("uid");
						fans.dateline = jsonObject.getString("dateline");
						fans.username = jsonObject.getString("username");
						fans.doing = jsonObject.getString("doing");
						fansList.add(fans);
					} catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();

					}
				}

			}

		}
		return true;
	}

}
