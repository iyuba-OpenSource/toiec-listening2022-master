package com.iyuba.core.common.protocol.message;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class ResponseNewInfo extends BaseJSONResponse {
	// {"system":0,"letter":2,"notice":1,"follow":0}
	public int system = 0;
	public int letter = 0;
	public int notice = 0;
	public int follow = 0;
	public int allNotice;

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub

		try {
			JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			system = jsonObjectRootRoot.getInt("system");
			letter = jsonObjectRootRoot.getInt("letter");
			notice = jsonObjectRootRoot.getInt("notice");
			follow = jsonObjectRootRoot.getInt("follow");
			allNotice = jsonObjectRootRoot.getInt("allsystem")
					+ jsonObjectRootRoot.getInt("allfollow");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
