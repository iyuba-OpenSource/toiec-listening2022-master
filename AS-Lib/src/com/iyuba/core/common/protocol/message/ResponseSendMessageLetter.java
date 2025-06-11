/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.MessageLetterContent;

/**
 * @author 私信内容
 */
public class ResponseSendMessageLetter extends BaseJSONResponse {
	public String result;// 返回代码
	public String message;// 返回信息
	public String jiFen;
	public MessageLetterContent letter = new MessageLetterContent();
	public JSONArray data;
	public ArrayList<MessageLetterContent> list;
	public int num;
	public String plid;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		list = new ArrayList<MessageLetterContent>();

		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getString("result");
			message = jsonObjectRootRoot.getString("message");
			try {
				jiFen = jsonObjectRootRoot.getString("jiFen");
			} catch (JSONException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
}
