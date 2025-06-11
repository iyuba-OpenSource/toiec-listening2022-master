/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.listener;

import com.iyuba.core.common.protocol.BaseHttpResponse;


/**
 * 协议操作完成回调
 * 
 * @author 陈彤
 */
public interface ProtocolResponse {
	
	public void finish(BaseHttpResponse bhr);

	public void error();
}
