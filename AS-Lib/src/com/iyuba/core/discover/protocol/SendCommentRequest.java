package com.iyuba.core.discover.protocol;

import java.net.URLEncoder;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

public class SendCommentRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	
	public SendCommentRequest(String uid,String id,String type,String author,String 
			authorid,String message,String title){
		MD5 m=new MD5();
		
		//根据ID和Type取包的信息http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=31001&uid=928&sign=911d31a89b57ee074898720e819c4c6az
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=30005&uid="+uid+
			"&id="+id+"&type="+type+"&author="+URLEncoder.encode(author)+"&authorid="+authorid+"&title="
				+URLEncoder.encode(URLEncoder.encode( title))
			+"&message="+URLEncoder.encode(message)+	"&sign=" 
				+ MD5.getMD5ofStr("30005"+id+type+uid+author+authorid+"iyubaV2"));
		
		Log.e("iyuba","http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=30005&uid="+uid+
			"&id="+id+"&type="+type+"&author="+URLEncoder.encode(author)+"&authorid="+authorid+"&title="
				+URLEncoder.encode(URLEncoder.encode( title))
			+"&message="+URLEncoder.encode(message)+	"&sign=" 
				+ MD5.getMD5ofStr("30005"+id+type+uid+author+authorid+"iyubaV2"));
		
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new   SendCommentResponse();
	}

}

