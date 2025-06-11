package com.iyuba.toeiclistening.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONRequest;
import com.iyuba.toeiclistening.util.MD5;

public class BlogContentRequest extends BaseJSONRequest {

	public BlogContentRequest(String blogid) {
		// TODO 鑷姩鐢熸垚鐨勬瀯閫犲嚱鏁板瓨鏍?
		setAbsoluteURI("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=200062"
				+"&blogId="+blogid
				+"&format=json&sign="+MD5.getMD5ofStr("20006" + blogid + "iyubaV2"));
		Log.d("BlogContentRequest url", "http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=200062"
				+"&blogId="+blogid
				+"&format=json&sign="+MD5.getMD5ofStr("20006" + blogid + "iyubaV2"));
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍?

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍?
		return new BlogContentResponse();
	}

}
