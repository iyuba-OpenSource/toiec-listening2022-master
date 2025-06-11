package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.teacher.sqlite.mode.Teacher;
import com.iyuba.core.util.Constant;

public class UpLoadRequest extends BaseJSONRequest {
	private String format = "json";
	
	public UpLoadRequest(String uid ,String  username) {
		setAbsoluteURI("www."+Constant.IYBHttpHead+"/question/teacher/api/upLoad.jsp?from=attachment&format=json&uid="
	   +uid+"&username="+TextAttr.encode(TextAttr.encode(TextAttr.encode(username)))
		);
      Log.e("iyuba","www."+Constant.IYBHttpHead+"/question/teacher/api/upLoad.jsp?from=attachment&format=json&uid="
    		   +uid+"&username="+TextAttr.encode(TextAttr.encode(username))
				);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return  new  UpLoadResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
