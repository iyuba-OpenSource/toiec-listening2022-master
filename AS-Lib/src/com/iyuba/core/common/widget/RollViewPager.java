package com.iyuba.core.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.iyuba.core.R;
import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class RollViewPager extends ViewPager {
	private Context context;
	private List<View> dot_List;

	private List<SlideShowCourse> ssCourseList;

	//轮播图显示文字对应集合
	private List<String> titleList;
	//轮播图显示文字的控件
	private TextView top_news_title;
	//轮播图片链接地址对应集合
	private List<String> imgUrlList;
	private MyPagerAdapter myPagerAdapter;
	private List<Bitmap> bitmaps = new ArrayList<Bitmap>();
	//当前viewpager指向的轮播图片的索引
	private int currentPosition = 0;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			//让viewpager指向currentPosition索引指向界面
			RollViewPager.this.setCurrentItem(currentPosition);

			//循环滚动，继续发消息
			startRoll();
		}

		;
	};
	private RunnableTask runnableTask;
	private int downX;
	private int downY;
	private OnViewClickListener onViewClickListener;

	class RunnableTask implements Runnable {
		@Override
		public void run() {
			//切换指向图片操作
			if (imgUrlList.size() != 0) {
				currentPosition = (currentPosition + 1) % imgUrlList.size();
			}
			//发送消息
			//发送一个空消息
			handler.obtainMessage().sendToTarget();
		}
	}

	public interface OnViewClickListener {
		//		public void viewClick(String url);
		public void viewClick(SlideShowCourse ssCourse);
	}

	//当前view移除界面的时候，调用的方法
	@Override
	protected void onDetachedFromWindow() {
		//不在发送消息，将维护的任务也移除掉
		handler.removeCallbacksAndMessages(null);
		super.onDetachedFromWindow();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downX = (int) ev.getX();
				downY = (int) ev.getY();
				//当前viewpager对应的夫控件不能去拦截事件
				getParent().requestDisallowInterceptTouchEvent(true);
				break;
			case MotionEvent.ACTION_MOVE:
				int moveX = (int) ev.getX();
				int moveY = (int) ev.getY();

				if (Math.abs(moveY - downY) > Math.abs(moveX - downX)) {
					//刷新操作
					getParent().requestDisallowInterceptTouchEvent(false);
				} else {
					//滑动内部viewpager指向的item，夫控件不拦截事件
					//滑动整个模块，指向下一个模块,夫控件要去拦截事件

					// moveX-downX>0 有左向右滑动
					// moveX-downX<0 由右向左滑动
					int diff = moveX - downX;

					//由右边向左边滑动，并且在viewpager最后一个页面的时候
					if (diff < 0 && getCurrentItem() == getAdapter().getCount() - 1) {
						//夫控件拦截事件
						getParent().requestDisallowInterceptTouchEvent(false);
					} else if (diff < 0 && getCurrentItem() > 0) {
						getParent().requestDisallowInterceptTouchEvent(true);
					} else if (diff > 0 && getCurrentItem() == 0) {
						getParent().requestDisallowInterceptTouchEvent(false);
					} else if (diff > 0 && getCurrentItem() < getAdapter().getCount()) {
						getParent().requestDisallowInterceptTouchEvent(true);
					}
				}
				break;
		}
		return super.dispatchTouchEvent(ev);
	}

	public RollViewPager(Context context, final List<View> dot_List, OnViewClickListener onViewClickListener) {
		super(context);
		this.context = context;
		this.dot_List = dot_List;
		this.onViewClickListener = onViewClickListener;
		runnableTask = new RunnableTask();
		this.setOnPageChangeListener(
				new OnPageChangeListener() {
					@Override
					public void onPageSelected(int arg0) {
						//文字切换
						//top_news_title.setText(titleList.get(arg0));
						//点也需要去切换
						for(int i = 0;i<dot_List.size();i++){
							if(i == arg0){
								dot_List.get(arg0).setBackgroundResource(R.drawable.dot_focus);
							}else{
								dot_List.get(i).setBackgroundResource(R.drawable.dot_blur);
							}
						}
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {

					}

					@Override
					public void onPageScrollStateChanged(int arg0) {

					}
				}

		);

	}

	//传递显示文字的方法
	public void initTitle(List<String> titleList, TextView top_news_title) {
		if (null != titleList && null != top_news_title && titleList.size() > 0) {
			top_news_title.setText(titleList.get(0));
		}
		this.titleList = titleList;
		this.top_news_title = top_news_title;
	}

	public void initImgUrl(List<String> imgUrlList) {
		this.imgUrlList = imgUrlList;
	}

	public void initSlideShowCourseList(List<SlideShowCourse> ssCourseList) {
		this.ssCourseList = ssCourseList;
	}

	public void startRoll() {
		//1,给viewpager去设置数据适配器
		if (myPagerAdapter == null) {
			myPagerAdapter = new MyPagerAdapter();
			this.setAdapter(myPagerAdapter);
		} else {
			myPagerAdapter.notifyDataSetChanged();
		}
		//2,滚动轮播图片,定时器，handler机制
		handler.postDelayed(runnableTask, 5000);
	}

	class MyPagerAdapter extends PagerAdapter {
		@Override
		public int getCount() {
			return imgUrlList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			//给viewpager添加view(ImageView)的操作
			View view = View.inflate(getContext(), R.layout.lib_course_list_slide, null);
			ImageView imageView = (ImageView) view.findViewById(R.id.lib_iv_ac_detail_slide);
			//下载后图片放置的控件，下载图片的链接地址
//			GitHubImageLoader.Instace(context).setPic(imgUrlList.get(position),
//					imageView, R.drawable.slideshow_loading);
			ImageLoader.getInstance().displayImage(imgUrlList.get(position),
					imageView);
			//ViewPager和内部view的事件的分发处理过程
			//1,点下操作Action_down先传递给viewpager，然后传递给viewpager内部的view，view做响应
			//2，滑动触发Action_move事件，先传递给viewpager，然后传递给内部的view，view去响应当前action_move,
			//当手指移动的距离达到一定值，不再view上去做响应，view上转而action_cancel方法
			//3，当内部的view不响应事件的时候，外侧的viewpager响应后续的事件(viewpager，响应action_move,action_up)
			view.setOnTouchListener(new OnTouchListener() {
				private int downX;
				private int upX;
				private long downTime;
				private long upTime;

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					switch (event.getAction()) {
						case MotionEvent.ACTION_DOWN:
							downX = (int) event.getX();
							downTime = System.currentTimeMillis();
							//handler维护的任务移除掉
							handler.removeCallbacksAndMessages(null);
							break;
						case MotionEvent.ACTION_UP:
							upX = (int) event.getX();
							upTime = System.currentTimeMillis();
							if (downX == upX && upTime - downTime < 500) {
								//触发点击操作，回调(定义一个接口，定义一个未实现点击事件触发的方法，谁用谁实现，在合适的地方调用一下当前方法)
//							onViewClickListener.viewClick(imgUrlList.get(position));
								onViewClickListener.viewClick(ssCourseList.get(position));
							}
							startRoll();
							break;
						case MotionEvent.ACTION_CANCEL:
							//抓住view不响应事件的临界点去解决bug
							startRoll();
							break;
					}
					return true;
				}
			});
			container.addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

}
