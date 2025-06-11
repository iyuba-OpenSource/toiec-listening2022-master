package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

/**
 * 获取评论的Response
 * 
 */

public class ScroingStarResponse extends BaseJSONResponse {
	public String resultCode;
	public int starCounts;
	public int counts;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO 自动生成的方法存根
		JSONObject jsonObjectRoot;
		try {
			jsonObjectRoot = new JSONObject(bodyElement);
			resultCode = jsonObjectRoot.getString("ResultCode");
			if (resultCode != null && resultCode.equals("511")) {
				starCounts = jsonObjectRoot.getInt("starCounts");
				counts = jsonObjectRoot.getInt("Counts");
			} else if (resultCode != null && resultCode.equals("510")) {
			}
		} catch (JSONException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}

		return true;
	}
}
