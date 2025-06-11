package com.iyuba.core.teacher.protocol;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;
import com.iyuba.core.common.util.TextAttr;
import com.iyuba.core.teacher.sqlite.mode.Teacher;

public class UpdateClassRequest extends BaseJSONRequest {
	private String format = "json";
	
	public UpdateClassRequest(Teacher teacher) {
		setAbsoluteURI("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/updateClass.jsp?format=json&uid="
	   +teacher.uid+"&username="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.username)))
	     +"&city="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.tcity)))
	      +"&endegree="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.endegree)))
	       +"&jpdegree="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.jpdegree)))
	      +"&part="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.category1)))
	       +"&positionName="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.category2)))
		);
      Log.e("iyuba","http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/teacher/api/updateClass.jsp?format=json&uid="
    		   +teacher.uid+"&username="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.username)))
    		     +"&city="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.tcity)))
    		      +"&endegree="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.endegree)))
    		       +"&jpdegree="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.jpdegree)))
    		      +"&part="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.category1)))
    		       +"&positionName="+TextAttr.encode(TextAttr.encode(TextAttr.encode(teacher.category2)))
				);
	}
	
	@Override
	public BaseHttpResponse createResponse() {
		return  new  UpdateBasicResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		
	}

}
