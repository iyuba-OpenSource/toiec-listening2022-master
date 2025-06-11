/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.common.thread;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.iyuba.core.R;
import com.iyuba.core.util.Constant;
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
	public ImageLoader imageLoader = ImageLoader.getInstance();
	private static GitHubImageLoader instance;
	private DisplayImageOptions options;

	private GitHubImageLoader(Context mContext) {
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
	}

	public void setCirclePicGrild(String userid, ImageView pic,Context context,int drawable) {
		String url="http://api."+com.iyuba.configation.Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid=" + userid
				+ "&size=middle";
		setCircleImage(url,context,drawable,pic);
	}

	/**
	 * 加载圆形图片
	 * @param imageUrl
	 * @param context
	 * @param placeImage
	 * @param imageView
	 */
	public static void setCircleImage(String imageUrl, final Context context, int placeImage, final ImageView imageView) {
		Glide.with(context).asBitmap().load(imageUrl).centerCrop().placeholder(placeImage).error(placeImage).into(new BitmapImageViewTarget(imageView) {
			@Override
			protected void setResource(Bitmap resource) {
				RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context.getResources(), resource);
				circularBitmapDrawable.setCircular(true);
				imageView.setImageDrawable(circularBitmapDrawable);
			}
		});
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
	 * 从url下载图片在ImageView中显示
	 *
	 * @param url      图片地址
	 * @param pic      图片所在ImageView
	 * @param drawable 未加载时的默认图片
	 */
	public void setRawPic(String url, ImageView pic, int drawable) {
		options = new DisplayImageOptions.Builder().showImageOnLoading(drawable)
				.showImageForEmptyUri(drawable).delayBeforeLoading(1000)
				.showImageOnFail(drawable).cacheInMemory(true).cacheOnDisk(true)
				.build();
		imageLoader.displayImage(url, pic, options);
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
	
	public void exit(){
		imageLoader.stop();
		imageLoader.destroy();
	}
}
