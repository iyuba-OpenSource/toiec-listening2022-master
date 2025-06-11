/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.iyuba.configation.Constant;
import com.iyuba.core.R;

import cn.sharesdk.onekeyshare.OnekeyShare;

/**
 * 分享
 * 
 * @author 陈彤
 */
public class GroundShare {
	private Context mContext;

	public GroundShare(Context context) {
		mContext = context;
	}

	private void shareMessage(String imagePath, String text, String url,
			String title) {
		OnekeyShare oks = new OnekeyShare();
		// 分享时Notification的图标和文字
//		oks.setNotification(R.drawable.ic_launcher, Constant.APPName);
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle(title);
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		oks.setTitleUrl(url);
		// text是分享文本，所有平台都需要这个字段
		oks.setText(text);
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		oks.setImagePath(imagePath);
		oks.setImageUrl(url);
		oks.setUrl(url);
		oks.setComment("爱语吧的这款应用" + Constant.APP_NAME + "真的很不错啊~推荐！");
		oks.setSite(Constant.APP_NAME);
		oks.setSiteUrl(url);
		String location[] = GetLocation.getInstance(mContext).getLocation();
		if (!location[1].equals("0") && !location[0].equals("0")) {
			oks.setLatitude(Float.parseFloat(location[0]));
			oks.setLatitude(Float.parseFloat(location[1]));
		}
		oks.setSilent(false);
		oks.show(mContext);
	}
	
	public void prepareMessage2(int id, String title, String url, String content) {
		handler.sendEmptyMessage(0);
		String imagePath = "http://"+com.iyuba.core.util.Constant.IYBHttpHead+"/image/android/iyubaClient/icon.png";
		String text = mContext.getString(R.string.ground_info) + "《" + title
				+ "》[ " + url + " ]" + content;
		shareMessage(imagePath, text, url, title);
	}


	public void prepareMessage(int id, String title, String url, String content) {
		handler.sendEmptyMessage(0);
		String imagePath = Constant.screenShotAddr;
		if (url.equals("http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"")) {

		} else {
			url = url + id + ".html";
		}
		String text = mContext.getString(R.string.ground_info) + "《" + title
				+ "》[ " + url + " ]" + content;
		shareMessage(imagePath, text, url, title);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				ScreenShot.savePic(mContext, Constant.screenShotAddr);
				break;
			}
		}
	};
}
