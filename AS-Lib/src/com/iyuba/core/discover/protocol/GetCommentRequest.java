package com.iyuba.core.discover.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;
import com.iyuba.core.util.Constant;

public class GetCommentRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	
	public GetCommentRequest(String id,String type,int pageNum){		
		MD5 m=new MD5();
		
		//根据ID和Type取包的信息http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=31001&uid=928&sign=911d31a89b57ee074898720e819c4c6az
		setAbsoluteURI("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=20007&id="+id+
				"&type="+type+"&ascc=0&pageNum="+pageNum+"&pageCounts=50"+"&sign=" + MD5.getMD5ofStr("20007"+id+type+"iyubaV2"));
		
		Log.d("BlogRequest","http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=20007&blogid="+id+
				"&sign=" + MD5.getMD5ofStr("20007"+id+type+"iyubaV2"));
		
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new GetCommentResponse();
	}

}

