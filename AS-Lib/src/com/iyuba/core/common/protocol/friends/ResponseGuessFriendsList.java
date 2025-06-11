/**
 * 
 */
package com.iyuba.core.common.protocol.friends;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.VOABaseJsonResponse;
import com.iyuba.core.me.sqlite.mode.FindFriends;

/**
 * @author yao 猜你认识好友列表
 */
public class ResponseGuessFriendsList extends VOABaseJsonResponse {

	public String result;// 返回代码
	public String message;// 返回信息
	public JSONArray data;
	public ArrayList<FindFriends> list;
	public Boolean isLastPage = false;

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
		list = new ArrayList<FindFriends>();
		try {
			result = jsonBody.getString("result");
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		if (result.equals("591")) {
			try {
				data = jsonBody.getJSONArray("data");
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			if (data != null && data.length() != 0) {
				int size = data.length();
				FindFriends item;
				JSONObject jsonObject;
				for (int i = 0; i < size; i++) {
					try {
						item = new FindFriends();
						jsonObject = ((JSONObject) data.opt(i));
						item.userid = jsonObject.getString("uid");
						item.vip = jsonObject.getString("vip");
						item.doing = jsonObject.getString("doing");
						item.userName = jsonObject.getString("username");
						item.gender = jsonObject.getString("gender");
						list.add(item);
					} catch (JSONException e) {
						// TODO: handle exception
						e.printStackTrace();
					}
				}
			}
		} else if (result.equals("592")) {
			isLastPage = true;
		}
		return true;
	}
}
