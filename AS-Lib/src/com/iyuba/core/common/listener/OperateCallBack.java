/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.listener;

/**
 * 类操作完成回调
 * 
 * @author 陈彤
 */
public interface OperateCallBack {
	public void success(String message);

	public void fail(String message);
}
