/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.thread;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.iyuba.core.util.Constant;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 下载图片
 * 
 * @author 作者 陈彤
 * @time 2014.4.30
 */
public class GitHubImageLoader {
	public ImageLoader imageLoader;
	private static GitHubImageLoader instance;
	private DisplayImageOptions options;

	private GitHubImageLoader(Context mContext) {
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(mContext));
	}

	/**
	 * 单例初始化
	 */
	public static synchronized GitHubImageLoader Instace(Context mContext) {
		if (instance == null) {
			instance = new GitHubImageLoader(mContext);
		}
		return instance;
	}

	/**
	 * 对头像的下载（头像大小为中等，需传入userid）
	 */
	@Deprecated
	public void setPic(String userid, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(15)).build();
		imageLoader.displayImage(
				"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
						+ userid + "&size=middle", pic, options);
	}

	/**
	 * 对头像的下载（头像大小为中等,圆形，需传入userid）
	 */
	public void setCirclePic(String userid, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(90)).build();
		imageLoader.displayImage(
				"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
						+ userid + "&size=middle", pic, options);
		Log.e("GithubImageLoader",""+"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
				+ userid + "&size=middle");
	}

	/**
	 * 对头像的下载（需传入userid和mode（big，middle，small））
	 */
	@Deprecated
	public void setPic(String userid, String size, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(15)).build();
		imageLoader.displayImage(
				"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
						+ userid + "&size=" + size, pic, options);

	}

	public void setCireclePic(String userid, String size, ImageView pic) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.defaultavatar)
				.showImageForEmptyUri(R.drawable.defaultavatar)
				.showImageOnFail(R.drawable.defaultavatar).cacheInMemory(true)
				.delayBeforeLoading(1000).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(90)).build();
		imageLoader.displayImage(
				"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
						+ userid + "&size=" + size, pic, options);

	}

	/**
	 * 对新闻图片的下载（需指定默认图片drawable）
	 */
	public void setPic(String url, ImageView pic, int drawable) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable).showImageForEmptyUri(drawable)
				.delayBeforeLoading(1000).showImageOnFail(drawable)
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(15)).build();
		imageLoader.displayImage(url, pic, options);
	}

	/**
	 * 对新闻图片的下载（需指定默认图片drawable，指定圆角角度degree）
	 */
	public void setPic(String url, ImageView pic, int drawable, int degree) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable).showImageForEmptyUri(drawable)
				.delayBeforeLoading(1000).showImageOnFail(drawable)
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(degree)).build();
		imageLoader.displayImage(url, pic, options);
	}

	/**
	 * 对新闻图片的下载,圆形（需指定默认图片drawable）
	 */
	public void setCirclePic(String url, ImageView pic, int drawable) {
		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(drawable).showImageForEmptyUri(drawable)
				.delayBeforeLoading(1000).showImageOnFail(drawable)
				.cacheInMemory(true).cacheOnDisk(true)
				.displayer(new RoundedBitmapDisplayer(90)).build();
		imageLoader.displayImage(url, pic, options);
	}

	public void clearCache() {
		// TODO Auto-generated method stub
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
	}

	public void clearMemoryCache() {
		// TODO Auto-generated method stub
		imageLoader.clearMemoryCache();
	}

	public void exit() {
		imageLoader.destroy();
	}
}
