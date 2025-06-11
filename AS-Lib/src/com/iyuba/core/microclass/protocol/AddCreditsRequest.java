package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.Base64Coder;

public class AddCreditsRequest extends BaseJSONRequest{
	public AddCreditsRequest(String uid,String idIndex){
		String  addIntegralUrl ="http://api."+com.iyuba.core.util.Constant.IYBHttpHead+"/credits/updateScore.jsp?srid=49";
		String url =addIntegralUrl+
				 "&uid="+uid+
				 "&idindex="+idIndex+
				 "&mobile=1"+
				 "&flag="+"1234567890"+Base64Coder.getTime();
		Log.e("AddCoinRequest",url);		    
		setAbsoluteURI(url);		
	}
	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
	}
	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new AddCreditsResponse();
	}
    
	
}
