package com.iyuba.core.microclass.protocol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.MD5;

public class ContentListRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

	private String id,type;
	
	public ContentListRequest(String id,String type) {
		this.id=id;
		this.type=type;
		
//		http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?&protocol=10104&ocid=19&sign=7fc8764bd7c72f91946091a03b0f81ca
//		Sign =md5(10104class+ocid)
		
		setAbsoluteURI("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10104&ocid="+this.id+"&sign="+
				MD5.getMD5ofStr("10104class"+this.id));
		Log.w("ContentListRequest:","http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10104&ocid="+this.id+"&sign="+
				MD5.getMD5ofStr("10104class"+this.id));
		
//		setAbsoluteURI("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="+this.id+"&type="+this.type+"&sign="+
//		MD5.getMD5ofStr("10102class"+this.id));
//		System.out.println("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10102&id="+this.id+"&type="+this.type+"&sign="+
//				MD5.getMD5ofStr("10102class"+this.id));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ContentListResponse(id);
	}

}

