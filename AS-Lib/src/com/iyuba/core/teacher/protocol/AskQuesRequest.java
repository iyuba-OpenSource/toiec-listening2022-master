package com.iyuba.core.teacher.protocol;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

public class AskQuesRequest extends BaseJSONRequest {
	private String format = "json";
	
	public AskQuesRequest(String uid, String username, String desc, int type,String askuid) {
		String uri;
		uri = "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/askQuestion.jsp?"
				+ "&format=" + format
				+ "&uid=" + uid
				+ "&username=" + TextAttr.encode(TextAttr.encode(TextAttr.encode(username)))
				+ "&question=" + desc
				+ "&category1=" + type;
		
		if(!askuid.equals("")) 
			uri = uri +"&tuid="+askuid;
		 
		Log.e("iyuba", uri);
		setAbsoluteURI(uri);
	}
	
	public AskQuesRequest(String uid, String username, String desc, int type,int appType,String askuid) {
		String uri = null;
		uri = "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/askQuestion.jsp?"
				+ "&format=" + format
				+ "&uid=" + uid
				+ "&username=" + TextAttr.encode(TextAttr.encode(TextAttr.encode(username)))
				+ "&question=" + desc
				+ "&category1=" + type
				+ "&category2=" + appType;
			
		if(!askuid.equals("")) 
			uri = uri +"&tuid="+askuid;
		 
	
		Log.e("iyuba", uri);
		setAbsoluteURI(uri);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return new AskQuesResponse ();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
