package com.iyuba.core.discover.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

public class BlogRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	
	public BlogRequest(String blogid) {
		
		MD5 m=new MD5();
		
		//根据ID和Type取包的信息http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=31001&uid=928&sign=911d31a89b57ee074898720e819c4c6az
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=20008&blogid="+blogid+
				"&sign=" + MD5.getMD5ofStr("20008"+blogid+"iyubaV2"));
		
		Log.d("BlogRequest","http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=20008&blogid="+blogid+
				"&sign=" + MD5.getMD5ofStr("20008"+blogid+"iyubaV2"));
		
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new BlogResponse();
	}

}

