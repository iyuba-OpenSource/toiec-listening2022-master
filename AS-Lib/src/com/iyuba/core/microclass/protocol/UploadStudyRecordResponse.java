package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseJSONResponse;

public class UploadStudyRecordResponse extends BaseJSONResponse{

	public String result;
	public String message;
	private String responseString;
	
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		try {
			responseString=bodyElement.toString().trim();
			JSONObject jsonObjectRootRoot = new JSONObject(responseString.substring(
					responseString.indexOf("{", 2), responseString.lastIndexOf("}") + 1));
			//JSONObject jsonObjectRootRoot = new JSONObject(bodyElement);
			result = bodyElement.charAt(10)+"";
			message = jsonObjectRootRoot.getString("message");
			//Log.d("result", result+"");
			//Log.d("message", message+"");
		if (result.equals("1")) {
			Log.d("��ȡ���ݳɹ�", "�ɹ�");
		}
		else {
			Log.d("��ȡ���ݳɹ�", "ʧ��");
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

}
