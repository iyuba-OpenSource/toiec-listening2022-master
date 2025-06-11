package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;

/**
 * 发送包浏览量增加请求
 * 
 * @author ct
 * 
 */

public class ViewCountPackRequest extends BaseJSONRequest {
	String PackId = "0";

	public ViewCountPackRequest(String packid) {
		this.PackId = packid;

		setAbsoluteURI("http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10107&ocid="+ PackId);
		
		Log.d("ViewCountPackRequest:", "http://class."+com.iyuba.core.util.Constant.IYBHttpHead+"/getClass.iyuba?protocol=10107&ocid="+ PackId);

	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new ViewCountPackResponse();
	}
}
