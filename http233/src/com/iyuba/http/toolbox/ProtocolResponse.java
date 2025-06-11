package com.iyuba.http.toolbox;

import com.iyuba.http.BaseHttpResponse;

/**
 * 协议操作完成回调
 * 
 * @author 陈彤
 */
public interface ProtocolResponse {
	public void finish(BaseHttpResponse bhr);

	public void error();
}
