package com.iyuba.core.microclass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.iyuba.core.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MobClassSlideHeaderViewPagerAdapter extends PagerAdapter{

	private List<ImageView> imageViewsList;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private Context mContext;
	static DisplayImageOptions options;
	
	public MobClassSlideHeaderViewPagerAdapter(Context mContext) {
		super();
		this.mContext = mContext;
		
		initImageLoader(mContext);
	}

	public MobClassSlideHeaderViewPagerAdapter(Context mContext,List<ImageView> ivList) {
		this.mContext = mContext;
		initImageLoader(mContext);
		imageViewsList = ivList;
	}
	
	public void addList(ArrayList<ImageView> IVsList){
		imageViewsList.addAll(IVsList);
		
	}
	public void clearList(){
		imageViewsList.clear();
	}

	@Override
	public void destroyItem(View container, int position, Object object) {
		// TODO Auto-generated method stub
//		super.destroyItem(container, position, object);
		((ViewPager)container).removeView(imageViewsList.get(position));
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
//		return super.instantiateItem(container, position);
		ImageView imageView = imageViewsList.get(position);

		if(imageView.getTag()!=null){
			imageLoader.displayImage(imageView.getTag() + "", imageView,options);
		}
//		else{
//			imageLoader.displayImage(imageView.getDrawable().toString(), imageView);
//		}
		
		Log.e("%%%%%%%%ImageView Tag:", imageView.getTag() + "");
		
//    	GitHubImageLoader.Instace(mContext).setPic(imageView.getTag() + "", imageView, R.drawable.iyumicroclass_slideshow_loading);
    	
		ViewGroup parent = (ViewGroup) imageView.getParent();
		if (parent != null) {
			parent.removeAllViews();
		} 
		container.addView(imageView);
		
//		((ViewPager) container).addView(imageView);
        return imageView;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return imageViewsList.size();//返回数据的个数
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	/**
	 * ImageLoader 图片组件初始化
	 * 
	 * @param context
	 */
	public static void initImageLoader(Context context) {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
//			.showImageOnLoading(R.drawable.iyumicroclass_slideshow_loading)
//			.showImageForEmptyUri(drawable)
//			.delayBeforeLoading(1000)
//			.showImageOnFail(drawable)
			.threadPriority(Thread.NORM_PRIORITY - 2)
			.denyCacheImageMultipleSizesInMemory()
			.discCacheFileNameGenerator(new Md5FileNameGenerator())
			.tasksProcessingOrder(QueueProcessingType.LIFO)
			.writeDebugLogs() // Remove
			.build();
			// Initialize ImageLoader with configuration.
		
			ImageLoader.getInstance().init(config);
			
			options  = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.iyumicroclass_slideshow_loading)
				.showImageForEmptyUri(R.drawable.iyumicroclass_slideshow)
				.delayBeforeLoading(1000)
				.showImageOnFail(R.drawable.iyumicroclass_slideshow)
				.cacheInMemory(true)
				.cacheOnDisk(true)
				.build();
			
		}
	
}
