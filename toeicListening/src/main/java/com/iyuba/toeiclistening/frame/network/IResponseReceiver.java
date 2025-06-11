package com.iyuba.toeiclistening.frame.network;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.BaseHttpResponse;



public interface IResponseReceiver {
	void onResponse(BaseHttpResponse response, BaseHttpRequest request,
			int rspCookie);
}
