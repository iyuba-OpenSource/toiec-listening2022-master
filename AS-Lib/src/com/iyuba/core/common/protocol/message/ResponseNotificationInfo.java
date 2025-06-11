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
import com.iyuba.core.me.sqlite.mode.MessageLetter;

/**
 * @author yao 通知
 */
public class ResponseNotificationInfo extends VOABaseJsonResponse {

	public String result;// 返回代码
	public String message;// 返回信息
	public String uid;
	public MessageLetter letter = new MessageLetter();
	public JSONArray data;
	public ArrayList<Fans> list;
	public int firstPage;
	public int prevPage;
	public int nextPage;
	public int lastPage;

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
		list = new ArrayList<Fans>();
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
		if (result.equals("632")) {

		} else {
			try {
				prevPage = jsonBody.getInt("prevPage");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				firstPage = jsonBody.getInt("firstPage");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				nextPage = jsonBody.getInt("nextPage");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				lastPage = jsonBody.getInt("lastPage");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				uid = jsonBody.getString("uid");
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (result.equals("631")) {
				try {
					data = jsonBody.getJSONArray("data");
				} catch (JSONException e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				if (data != null && data.length() != 0) {
					int size = data.length();
					Fans item;
					JSONObject jsonObject;
					for (int i = 0; i < size; i++) {
						try {
							jsonObject = ((JSONObject) data.opt(i));
							item = new Fans();
							item.uid = jsonObject.getString("authorid");
							item.isnew = jsonObject.getString("new");
							item.doing = jsonObject.getString("note");
							item.username = jsonObject.getString("author");
							item.dateline = jsonObject.getString("dateline");
							list.add(item);
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		return true;
	}

}
