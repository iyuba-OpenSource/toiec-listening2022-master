package com.iyuba.toeiclistening.frame.network;

import com.iyuba.toeiclistening.frame.protocol.BaseHttpRequest;
import com.iyuba.toeiclistening.frame.protocol.ErrorResponse;

/**
 * 
 * @author wuwei
 * 
 */
public interface IErrorReceiver {

	void onError(ErrorResponse errorResponse, BaseHttpRequest request,
			int rspCookie);
}
