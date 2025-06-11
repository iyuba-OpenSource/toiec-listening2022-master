/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.protocol.news;

import java.util.Vector;

import com.iyuba.core.common.network.xml.Utility;
import com.iyuba.core.common.network.xml.kXMLElement;
import com.iyuba.core.common.protocol.BaseXMLResponse;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class ReadCountResponse extends BaseXMLResponse {
	public String[] counts = new String[10];

	@Override
	protected boolean extractBody(kXMLElement headerEleemnt,
			kXMLElement bodyElement) {
		// TODO Auto-generated method stub
		int total = Integer.parseInt(Utility.getSubTagContent(bodyElement,
				"total"));
		Vector rankVector = bodyElement.getChildren();
		int size = rankVector.size();
		kXMLElement ranKXMLElement;
		for (int i = 0; i < size; i++) {
			ranKXMLElement = (kXMLElement) rankVector.elementAt(i);
			if (ranKXMLElement.getTagName().equals("Row")) {
				try {
					counts[total + 1 - i] = Utility.getSubTagContent(
							ranKXMLElement, "ReadCount");
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		}
		return true;
	}
}
