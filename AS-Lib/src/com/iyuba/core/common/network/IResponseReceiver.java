package com.iyuba.core.common.network;

import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;

public interface IResponseReceiver {
	void onResponse(BaseHttpResponse response, BaseHttpRequest request,
			int rspCookie);
}
