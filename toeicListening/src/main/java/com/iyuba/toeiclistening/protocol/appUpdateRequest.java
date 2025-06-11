package com.iyuba.toeiclistening.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONRequest;
import com.iyuba.toeiclistening.util.Constant;


/**
 * 鐢ㄦ埛鐧诲綍
 * 
 * @author lijingwei
 * 
 */
public class appUpdateRequest extends BaseJSONRequest {

	{
		//requestId = R.string.REQID_DEFAULT;
	}

	private int version;
	
	public appUpdateRequest(int version) {
		this.version=version;
//		"http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/mobile/android/jlpt1/islatest.plain?currver="
		setAbsoluteURI("http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/mobile/android/"+Constant.APP_TYPE+"/islatest.plain?currver="+this.version);
		Log.e("version", "http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/mobile/android/"+Constant.APP_TYPE+"/islatest.plain?currver="+this.version);
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new appUpdateResponse();
	}

}
