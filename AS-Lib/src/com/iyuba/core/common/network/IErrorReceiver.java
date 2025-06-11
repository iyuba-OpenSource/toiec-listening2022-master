package com.iyuba.core.common.network;

import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.ErrorResponse;

/**
 * 
 * @author wuwei
 * 
 */
public interface IErrorReceiver {

	void onError(ErrorResponse errorResponse, BaseHttpRequest request,
			int rspCookie);
}
