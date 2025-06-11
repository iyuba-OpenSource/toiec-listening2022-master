package com.iyuba.core.microclass.protocol;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.BaseJSONRequest;

/**
 * 
 * 
 * @author chentong
 * @time 13.4.18 获取文章评论列表API
 */
public class CommentRequest extends BaseJSONRequest {

	String format = "json"; // 可选，默认为json格式
	String packid = "0";
	String pageNumber = "1";
	String pageCount = "15";

	public CommentRequest(String PackId, String pageNumber) {
		this.packid = PackId;
		
		//先用苹果的评论
		setAbsoluteURI("http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/appApi/UnicomApi?protocol=60001&platform=ios&format=json&voaid="
				+ packid
				+ "&pageNumber="
				+ pageNumber
				+ "&pageCounts="
				+ pageCount + "&appName=microclass");
		
		Log.d("Lib库中CommentRequest:","http://daxue."+com.iyuba.core.util.Constant.IYBHttpHead+"/appApi/UnicomApi?protocol=60001&platform=ios&format=json&voaid="
				+ packid
				+ "&pageNumber="
				+ pageNumber
				+ "&pageCounts="
				+ pageCount + "&appName=microclass");
	}

	@Override
	public BaseHttpResponse createResponse() {
		// TODO Auto-generated method stub
		return new CommentResponse();
	}

	@Override
	protected void fillBody(JSONObject jsonObject) throws JSONException {
		// TODO 自动生成的方法存根

	}

}
