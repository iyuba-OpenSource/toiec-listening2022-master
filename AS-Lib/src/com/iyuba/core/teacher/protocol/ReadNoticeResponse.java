/**
 * 
 */
package com.iyuba.core.teacher.protocol;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.MessageLetter;
import com.iyuba.core.me.sqlite.mode.MessageLetterContent;

/**
 * @author yao
 * 
 */
public class ReadNoticeResponse extends BaseJSONResponse {

	public String result;// 返回代码
	public String message;// 返回信息

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getString("result");
			message = jsonObjectRootRoot.getString("message");
			if (result.equals("1")) {
				System.out.println("设置成已读");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		// return false;
	}

}
