package com.iyuba.toeiclistening.protocol;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;
import com.iyuba.toeiclistening.frame.protocol.BaseJSONRequest;
import com.iyuba.toeiclistening.util.MD5;




/**
 * 托业资讯
 * id=295451
 * @author yao
 * http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=200061&
 * id=295451&sign=80f18af1258564617afa4ca32c62d571&pageNumber=1&pageCounts=20
 */
public class BlogListRequest extends BaseJSONRequest {

	{
		requestId = 0;
	}

private String id,blogmaxId,pageCounts;
	
	public BlogListRequest(String id,String blogmaxId,String pageCounts) {
		this.id=id;
		this.blogmaxId=blogmaxId;
		this.pageCounts=pageCounts;
		MD5 m=new MD5();
		setAbsoluteURI("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=200061&id="+this.id+
				"&blogmaxId="+blogmaxId+"&sign="+m.getMD5ofStr("20006"+this.id+"iyubaV2")+"&blogcounts="+this.pageCounts);
		System.out.println("http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=200061&id="+this.id+"&blogmaxId="+
				blogmaxId+"&sign="+m.getMD5ofStr("20006"+this.id+"iyubaV2")+"&blogcounts=50");
		LogUtil.e("请求：资讯列表"+"http://api."+com.iyuba.toeiclistening.util.Constant.IYBHttpHead2+"/v2/api.iyuba?platform=android&format=json&protocol=200061&id="+this.id+"&blogmaxId="+
				blogmaxId+"&sign="+m.getMD5ofStr("20006"+this.id+"iyubaV2")+"&blogcounts=50");
		
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO Auto-generated method stub
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new BlogListResponse();
	}

}
