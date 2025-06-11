package com.iyuba.core.microclass.protocol;

import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class appUpdateResponse extends BaseJSONResponse {
	public String result = "";
	public String msg = "";
	public String data = "";

	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		String[] body = bodyElement.split(",");
		if (body.length == 3) {
			result = body[0];
			msg = body[1];
			data = body[2];
		} else if (body.length == 2) {
			result = body[0];
			msg = body[1];
		}
		return true;
	}

}
