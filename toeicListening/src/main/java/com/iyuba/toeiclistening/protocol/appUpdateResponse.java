package com.iyuba.toeiclistening.protocol;

import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseJSONResponse;

public class appUpdateResponse extends BaseJSONResponse {
	public String result="";
	public String msg="";
	public String data="";
	@Override
	protected boolean extractBody(JSONObject headerEleemnt, String bodyElement) {
		// TODO Auto-generated method stub
		String[] body=bodyElement.split(",");
		if(body.length==3){
			result=body[0];
			msg=body[1];
			data=body[2];
		}else if(body.length==2){
			result=body[0];
			msg=body[1];
		}else if(body.length==1){
			result=body[0];
		}
//		result="OK";
//		data="1.2.3||http://down.gfan.com/gfan/product/a/gfanmobile/beta/GfanMobile.apk";
		Log.e("", bodyElement);
		return true;
	}

}
