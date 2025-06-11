/**
 * 
 */
package com.iyuba.core.common.protocol.message;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;
import com.iyuba.core.me.sqlite.mode.MutualAttention;

/**
 * @author 相互关注列表
 */
public class ResponseMutualAttentionList extends BaseJSONResponse {

	public String result;// 返回代码
	public String message;// 返回信息
	// public MutualAttention fan = new MutualAttention();
	public JSONArray data;
	public ArrayList<MutualAttention> list;
	public int num;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		list = new ArrayList<MutualAttention>();
		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = jsonObjectRootRoot.getString("result");
			message = jsonObjectRootRoot.getString("message");
			if (result.equals("570")) {
				num = jsonObjectRootRoot.getInt("num");
				data = jsonObjectRootRoot.getJSONArray("data");
				if (data != null && data.length() != 0) {
					int size = data.length();
					MutualAttention item;
					JSONObject jsonObject;
					for (int i = 0; i < size; i++) {
						item = new MutualAttention();
						jsonObject = ((JSONObject) data.opt(i));
						item.bkname = jsonObject.getString("bkname");
						item.dateline = jsonObject.getString("dateline");
						item.followuid = jsonObject.getString("followuid");
						item.fusername = jsonObject.getString("fusername");
						item.status = jsonObject.getString("status");
						list.add(item);
					}
				}
			}
		} catch (JSONException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return true;
	}

}
