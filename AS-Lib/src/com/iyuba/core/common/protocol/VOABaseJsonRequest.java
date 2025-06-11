package com.iyuba.core.common.protocol;

import android.util.Log;

import com.iyuba.core.util.Constant;

public abstract class VOABaseJsonRequest extends BaseJSONRequest {
	protected String platform = "android";
	protected String format = "json";
	protected String protocolCode = "";

	public VOABaseJsonRequest(String protocolCode) {
		this.protocolCode = protocolCode;
		setAbsoluteURI(getCurrAbsoluteURL());
	}

	/**
	 * 获取当前的路径
	 * 
	 * @return
	 */
	private String getCurrAbsoluteURL() {
		StringBuffer absoluteURL = new StringBuffer("");

		if (protocolCode.equals("0")) {
			// 升级vip协议
			absoluteURL.append("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/pay/payVipApi.jsp")
					.append("?").append("platform=").append(platform)
					.append("&").append("format=").append("xml");
		} else if (protocolCode.equals("1")) {
			// 获取短信验证码
			absoluteURL.append("http://m."+Constant.IYBHttpHead2+"/sendMessage.jsp")
					.append("?").append("format=").append("json");
		} else if (protocolCode.equals("2")) {
			// 验证短信验证码是否正确
			absoluteURL.append("http://m."+Constant.IYBHttpHead2+"/checkCode.jsp")
					.append("?").append("format=").append("json");
		} else {
			absoluteURL.append("http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba")
					.append("?").append("platform=").append(platform)
					.append("&").append("format=").append(format).append("&")
					.append("protocol=").append(this.protocolCode);
		}
		return absoluteURL.toString();
	}

	/**
	 * 设置参数
	 * 
	 * @param key
	 * @param value
	 */
	protected void setRequestParameter(String key, String value) {
		StringBuffer requestURLTemp = new StringBuffer(getAbsoluteURI());
		requestURLTemp.append("&").append(key).append("=").append(value);
		Log.e("requestURLTemp.toString()===", requestURLTemp.toString());
		setAbsoluteURI(requestURLTemp.toString());
	}

}
