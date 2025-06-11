package com.iyuba.core.microclass.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.iyuba.configation.ConfigManager;
import com.iyuba.configation.Constant;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.RollViewPager;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshListView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshListView.OnRefreshListener;
import com.iyuba.core.microclass.adapter.MobClassListAdapter;
import com.iyuba.core.microclass.adapter.MobClassListTypeAdapter;
import com.iyuba.core.microclass.protocol.CourseListRequest;
import com.iyuba.core.microclass.protocol.CourseListResponse;
import com.iyuba.core.microclass.protocol.CourseTypeListRequest;
import com.iyuba.core.microclass.protocol.CourseTypeListResponse;
import com.iyuba.core.microclass.protocol.SlideShowCourseListRequest;
import com.iyuba.core.microclass.protocol.SlideShowCourseListResponse;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;
import com.iyuba.core.microclass.sqlite.op.CoursePackOp;
import com.iyuba.core.microclass.sqlite.op.CoursePackTypeOp;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.RequestParameters.NativeAdAsset;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoAdAdapter;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

public class MicroClassListActivity extends Activity {
	private Context mContext;
	private RelativeLayout layout_roll_view;
	private List<SlideShowCourse> ssCourseList = new ArrayList<SlideShowCourse>();
	// 用于存放图片地址的集合
	private ArrayList<String> imageUrls = new ArrayList<String>();
	// 用于存放滚动点的集合
	private ArrayList<View> dot_list = new ArrayList<View>();
	// 轮播图片的布局
	private LinearLayout top_news_viewpager;
	// 轮播图片指引(圆点)的布局
	private LinearLayout dots_ll;
	private MobClassListAdapter mobClassListAdapter;
	private MobClassListTypeAdapter mobClassListTypeAdapter;
	private ArrayList<CoursePack> coursePackArrayList = new ArrayList<CoursePack>();
	private ArrayList<CoursePackType> coursePackTypes = new ArrayList<CoursePackType>();
	private ProgressDialog wettingDialog;
	private int curPackId;
	private double curPackPrice;
	private String reqPackId;
	private static String reqPackIdNew;
	private static String reqPackDesc;
	private String reqPackType;
	private PullToRefreshListView mobClassListView;
	private ProgressBar mobClassListWaitBar;
	private Spinner coursePackSpinner;
	private TextView titleTextView;
	private View backView;
	private View view;
	private IntentFilter mIntentFilter;
	private int index = 0;
	private static int OFFSET = 20;
	private int curPageNum = 1;
	private int reqPageNum;
	private Boolean isLastPage = false;
	private YouDaoAdAdapter mAdAdapter;
	// 课程信息的数据库操作帮助类
	private CoursePackOp coursePackOp;
	private CoursePackTypeOp coursePackTypeOp;
	private int startid = 0;
	private int GAP = 10;
	private int selectId = 0;
	private int maxBlogId;
	public String firstPage;
	public String prevPage;
	public String nextPage;
	public String lastPage;

	public int iFirstPage;
	public int iPrevPage;
	public int iNextPage;
	public int iLastPage = 0;

	// //轮播图相关控件和成员变量
	// private RelativeLayout slideShowView;
	// private RelativeLayout rrSlideShowView;
	// private LinearLayout dotLayout;
	//
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private final static String PIC_BASE_URL = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/dev/";
	// //轮播图图片数量
	private int IMAGE_COUNT;

	// //自动轮播的时间间隔
	// private final static int TIME_INTERVAL = 5;
	// //自动轮播启用开关
	// private final static boolean isAutoPlay = true;
	// //自定义轮播图的资源
	// private ArrayList<String> imageUrlsList = new ArrayList<String>();
	// //放轮播图片的ImageView 的list
	// private ArrayList<ImageView> imageViewsList = new ArrayList<ImageView>();
	// private ArrayList<ImageView> defaultImageViewsList = new
	// ArrayList<ImageView>();
	// //放圆点的View的list
	// private ArrayList<View> dotViewsList = new ArrayList<View>();
	//
	// private ViewPager slideHeaderViewPager;
	// //当前轮播页
	// private int currentItem = 0;
	// //定时任务
	// private ScheduledExecutorService scheduledExecutorService;
	//
	// private MobClassSlideHeaderViewPagerAdapter slideHeaderViewPagerAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.microclass_course_list);
		Constant.download = 1;
		mContext = this;
		if (!Constant.APPID.equals("205")) {
			reqPackIdNew = Constant.MicroClassReqPackId;
			reqPackDesc = Constant.reqPackDesc;
		} else {// 否则请求所有的课程信息
			reqPackIdNew = "-2";
			reqPackDesc = "class.all";
		}
		coursePackOp = new CoursePackOp(mContext);
		coursePackTypeOp = new CoursePackTypeOp(mContext);
		wettingDialog = new ProgressDialog(mContext);
		coursePackArrayList = coursePackOp.findDataByAll();
		coursePackTypes = coursePackTypeOp.findDataByAll();
		initView();
		MobManager.Instance().courseList = coursePackArrayList;
		MobManager.Instance().courseTypeList = coursePackTypes;
		long lastTime = ConfigManager.Instance().loadLong("time");
		ConfigManager.Instance().putLong("time", System.currentTimeMillis());
		if (System.currentTimeMillis() - lastTime > 86400) {
			// 获取课程包的分类信息
			if (coursePackTypes.size() == 0) {
				handler.sendEmptyMessage(0);
				// reqPackId = "-2";
				// AppID非218时，按照对应的包ID进行请求
				if (!Constant.APPID.equals("205")) {
					reqPackIdNew = Constant.MicroClassReqPackId;
					reqPackDesc = Constant.reqPackDesc;
				} else {// 否则请求所有的课程信息
					reqPackIdNew = "-2";
					reqPackDesc = "class.all";
				}
				reqPackType = "-2";
			}
			if (coursePackArrayList.size() == 0) {
				handler.sendEmptyMessage(1);
			}
		}
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// stopPlay();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		coursePackSpinner.setSelection(13);
		Log.e("MicroClassListActivity", "onResume");
	}

	public void initView() {
		backView = findViewById(R.id.backlayout);
		backView.setBackgroundColor(Color.WHITE);

		coursePackSpinner = (Spinner) findViewById(R.id.titleSpinner);
		titleTextView = (TextView) findViewById(R.id.titleTextView);
		if (Constant.APPID.equals("205")) {
			coursePackSpinner.setVisibility(View.VISIBLE);
			titleTextView.setVisibility(View.INVISIBLE);
		} else {
			coursePackSpinner.setVisibility(View.INVISIBLE);
			titleTextView.setVisibility(View.VISIBLE);
		}
		layout_roll_view = (RelativeLayout) View.inflate(mContext,
				R.layout.layout_roll_view, null);
		// 用于将轮播图添加进去
		top_news_viewpager = (LinearLayout) layout_roll_view
				.findViewById(R.id.top_sliding_viewpager);
		dots_ll = (LinearLayout) layout_roll_view
				.findViewById(R.id.dots_ll_ongoing);
		mobClassListWaitBar = (ProgressBar) findViewById(R.id.courselist_waitbar);
		mobClassListAdapter = new MobClassListAdapter(mContext,
				coursePackArrayList);
		mobClassListTypeAdapter = new MobClassListTypeAdapter(mContext,
				coursePackTypes);
		mAdAdapter = new YouDaoAdAdapter(mContext, mobClassListAdapter,
				YouDaoNativeAdPositioning.newBuilder().addFixedPosition(2)
						.enableRepeatingPositions(10).build());

		// 设定广告样式，代理listview的adapter
		final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
				new ViewBinder.Builder(R.layout.lib_native_ad_row)
						.titleId(R.id.native_title).textId(R.id.native_text)
						.mainImageId(R.id.native_main_image).build());

		mAdAdapter.registerAdRenderer(adRenderer);

		final EnumSet<NativeAdAsset> desiredAssets = EnumSet.of(
				NativeAdAsset.TITLE, NativeAdAsset.TEXT,
				NativeAdAsset.MAIN_IMAGE, NativeAdAsset.CALL_TO_ACTION_TEXT);
		// 指定请求资源
		RequestParameters mRequestParameters = RequestParameters.builder()
				// .location(location)
				// .keywords(keywords)
				.desiredAssets(desiredAssets).build();

		// AD_UNIT_ID为申请的广告位ID。
		mAdAdapter.loadAds("b932187c3ec9f01c9ef45ad523510edd",
				mRequestParameters);

		mobClassListView = (PullToRefreshListView) findViewById(R.id.course_list);
		mobClassListView.setOnRefreshListener(onRefreshListener);
		mobClassListView.setOnScrollListener(onScrollListener);
		mobClassListView.setonRefreshListener(orfl);
		mobClassListView.setOnItemClickListener(oItemClickListener);
		// $$$$$$$$$$$$$$$$$使用ViewPager轮播图片$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

		// LayoutInflater inflateor = LayoutInflater.from(mContext);
		// slideShowView = (RelativeLayout)
		// inflateor.inflate(R.layout.header_slideshow, mobClassListView,
		// false);
		//
		// slideHeaderViewPager =
		// (ViewPager)slideShowView.findViewById(R.id.headerSlideshowView);
		// dotLayout = (LinearLayout)
		// slideShowView.findViewById(R.id.dotLayout);
		// dotLayout.removeAllViews();
		//
		// slideHeaderViewPagerAdapter = new
		// MobClassSlideHeaderViewPagerAdapter(mContext,imageViewsList);
		//
		// mobClassListView.addHeaderView(slideShowView);
		//
		// if (!NetWorkState.isConnectingToInternet()) {
		// Toast.makeText(mContext, "无网络！！！", Toast.LENGTH_SHORT).show();
		//
		// if( imageViewsList != null &&imageViewsList.size() != 0 ){
		// slideHeaderViewPager.setFocusable(true);
		//
		// slideHeaderViewPager.setFocusableInTouchMode(true);
		//
		// slideHeaderViewPager.setAdapter(slideHeaderViewPagerAdapter);
		//
		// slideHeaderViewPager.setOnPageChangeListener(new
		// SlideShowViewPageChangeListener());
		//
		// }
		// else{
		//
		// ImageView view1 = new ImageView(mContext);
		// ImageView view2 = new ImageView(mContext);
		// ImageView view3 = new ImageView(mContext);
		//
		// view1.setBackgroundResource(R.drawable.slideshow_jlpt);
		// view2.setBackgroundResource(R.drawable.slideshow_cet4);
		// view3.setBackgroundResource(R.drawable.slideshow_toefl);
		//
		// imageViewsList.add(view1);
		// imageViewsList.add(view2);
		// imageViewsList.add(view3);
		//
		// // 热点个数与图片特殊相等
		// for (int i = 0; i < imageViewsList.size(); i++) {
		//
		// ImageView dotView = new ImageView(mContext);
		// LinearLayout.LayoutParams params = new
		// LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
		// params.leftMargin = 4;
		// params.rightMargin = 4;
		// dotLayout.addView(dotView, params);
		// dotViewsList.add(dotView);
		// }
		//
		// slideHeaderViewPagerAdapter.notifyDataSetChanged();
		// }
		// }
		//
		// slideHeaderViewPager.setAdapter(slideHeaderViewPagerAdapter);
		// slideHeaderViewPager.setOnPageChangeListener(new
		// SlideShowViewPageChangeListener());
		// slideHeaderViewPager.setFocusable(true);

		// $$$$$$$$$$$$$$$$$使用ViewPager轮播图片$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

		mobClassListView.addHeaderView(layout_roll_view);

		if (mobClassListTypeAdapter != null) {
			coursePackSpinner.setAdapter(mobClassListTypeAdapter);
		}
		if (mobClassListAdapter != null) {
			mobClassListView.setAdapter(mobClassListAdapter);
			mobClassListView.setAdapter(mAdAdapter);
			// actualListView.setAdapter(mAdAdapter);
			mAdAdapter.refreshAds(mobClassListView,
					"b932187c3ec9f01c9ef45ad523510edd", mRequestParameters);
		}
		mobClassListWaitBar.setVisibility(View.GONE);
		coursePackSpinner
				.setOnItemSelectedListener(new OnItemSelectedListener() {
					@Override
					public void onItemSelected(AdapterView<?> parent,
											   View view, int position, long id) {
						// TODO Auto-generated method stub
						// reqPackId = coursePackTypes.get(position).id+"";
						if (Constant.APPID.equals("205")) {
							reqPackIdNew = coursePackTypes.get(position).id
									+ "";
							reqPackType = coursePackTypes.get(position).type
									+ "";
							reqPackDesc = coursePackTypes.get(position).desc; // 对应的是轮播图片对应的请求字段，如"class.all"
							handler.sendEmptyMessage(3);
						}
					}

					@Override
					public void onNothingSelected(AdapterView<?> parent) {
					}

				});
	}

	OnScrollListener onScrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			// TODO Auto-generated method stub
			switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						Log.d("请求列表的页码222", "判断滚动到底部时");
						// 分页数先向上取整
						Log.d("当前本地总的记录数为：",
								MobManager.Instance().courseList.size() + "");
						reqPageNum = (int) Math
								.ceil((double) MobManager.Instance().courseList
										.size() / 20) + 1;
						curPageNum = reqPageNum;
						Message msg = new Message();
						msg.arg1 = reqPageNum;
						msg.what = 5;
						handler.sendMessage(msg);
					}
					break;
			}
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
							 int visibleItemCount, int totalItemCount) {
		}
	};
	public String getAppId(int ownerid) {
		// TODO Auto-generated method stub
		String appId = "207";
		// if(ownerid == 1){
		// appId = "205";
		// }else if(ownerid == 5){
		// appId = "206";
		// }else if(ownerid == 6){
		// appId = "203";
		// }else if(ownerid == 28){
		// appId = "28";
		// }else{
		// appId = "299";
		// }
		return appId;
	}

	private OnRefreshListener onRefreshListener = new OnRefreshListener() {
		@Override
		public void onRefresh() {

			// maxBlogId = blogHelper.findMaxBlogId();
			// TODO Auto-generated method stub
			if (NetWorkState.isConnectingToInternet()) {// 开始刷新

				handler.sendEmptyMessage(1);
			} else {// 刷新失败

				// mobClassListView.onRefreshFail();
			}
		}
	};
	Handler handler = new Handler() {
		int reqPageNumber;

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case 0:
					wettingDialog.show();
					ClientSession.Instace().asynGetResponse(
							// APPID pageNumber pageCounts
							// 获取所有课程的列表
							new CourseTypeListRequest(), new IResponseReceiver() {
								@Override
								public void onResponse(BaseHttpResponse response,
													   BaseHttpRequest request, int rspCookie) {
									CourseTypeListResponse res = (CourseTypeListResponse) response;
									if (res.result.equals("1")) {
										if (res.courseTypeList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
											// 则证明没有新的资讯类容，
											// 无需刷新
											// 以后可更改接口实现高效刷新
											int flag = 0;
											if (MobManager.Instance().courseTypeList
													.size() == 0) {
												flag = 1;
											} else {
												// if
												// (!(res.courseTypeList.get(0).id
												// == MobManager
												// .Instance().courseTypeList.get(0).id))
												// {
												// flag = 1;
												// }
												if (res.courseTypeList.size() > MobManager
														.Instance().courseTypeList
														.size()) {
													flag = 1;
												}
											}
											if (flag == 1) {
												coursePackTypes.clear();
												coursePackTypes
														.addAll(res.courseTypeList);
												coursePackTypeOp
														.deleteCoursePackTypeData();
												coursePackTypeOp
														.insertCoursePackType(coursePackTypes);
												mobClassListTypeAdapter.clearList();// 清除原来的记录
												mobClassListTypeAdapter
														.addList(res.courseTypeList);
												handler.sendEmptyMessage(8);
												handlerRefreshList
														.sendEmptyMessage(3);
											}
										}
									}
									// handler.sendEmptyMessage(3);
									wettingDialog.dismiss();
								}
							}, new IErrorReceiver() {

								@Override
								public void onError(ErrorResponse errorResponse,
													BaseHttpRequest request, int rspCookie) {
									// TODO Auto-generated method stub
									// handlerRefreshList.sendEmptyMessage(4);
									// handlerRefreshList.sendEmptyMessage(9);
									// handlerRefreshList.sendEmptyMessage(13);
								}

							}, null);
					break;

				case 1:
					wettingDialog.show();
					ClientSession.Instace().asynGetResponse(
							// APPID pageNumber pageCounts

							// 获取所有课程的列表
							new CourseListRequest(reqPackIdNew, reqPackType, "1"),
							new IResponseReceiver() {

								@Override
								public void onResponse(BaseHttpResponse response,
													   BaseHttpRequest request, int rspCookie) {
									CourseListResponse res = (CourseListResponse) response;
									if (res.result.equals("1")) {

										iLastPage = Integer.parseInt(res.lastPage);

										if (res.courseList.size() > 0) {// 第一条记录如果和数据路里面的存储的记录相同
											// 则证明没有新的资讯类容，
											// 无需刷新
											// 以后可更改接口实现高效刷新
											int flag = 1;
										/*
										 * int flag = 0; if
										 * (MobManager.Instance().courseList
										 * .size() == 0) { flag = 1; } else { //
										 * if (!(res.courseList.get(0).id ==
										 * MobManager //
										 * .Instance().courseList.get(0).id)) {
										 * // flag = 1; // } //
										 * 如果返回的courseList中的记录数大于等于本地的全部课程的记录数或者返回的记录的最后一条记录与本地的最后一条记录不同则刷新
										 * // if (res.courseList.size() >=
										 * MobManager
										 * .Instance().courseList.size()){ if
										 * (res.courseList.size() >
										 * MobManager.Instance
										 * ().courseList.size() ||
										 * !(res.courseList
										 * .get(res.courseList.size()-1).id ==
										 * MobManager
										 * .Instance().courseList.get(MobManager
										 * .Instance().courseList.size()-1).id))
										 * { flag = 1; } }
										 */
											if (flag == 1) {
												coursePackArrayList.clear();
												coursePackArrayList
														.addAll(res.courseList);
												mobClassListAdapter.clearList();// 清除原来的记录
												mobClassListAdapter
														.addList(res.courseList);

												if (reqPackIdNew.equals("-2")) {
													coursePackOp
															.deleteCoursePackData();
												}
												coursePackOp
														.insertCoursePacks(coursePackArrayList);
											}
										}
									}
									handler.sendEmptyMessage(2);
									wettingDialog.dismiss();

								}
							}, null, null);
					break;
				case 2:
					mobClassListAdapter.notifyDataSetChanged();
					mobClassListView.onRefreshComplete();
					mobClassListWaitBar.setVisibility(View.GONE);
					break;

				// 分类获取也联网获取
				case 3:
					if ((!reqPackIdNew.equals("-2"))
							&& (!reqPackIdNew.equals("-1"))) {
						// 按课程的分类获取包（日语一级、VOA应用、英语四级等）
						// 首先判断本地是否有该类别的课程对应的包
						coursePackArrayList.clear();
						coursePackArrayList = coursePackOp
								.findDataByOwnerId(reqPackIdNew);

						// 如果当前课程对应的包在本地有记录，则加载本地的
						if (coursePackArrayList.size() != 0) {
							mobClassListAdapter.clearList();// 清除原来的记录
							mobClassListAdapter.addList(coursePackArrayList);
							handlerRefreshList.sendEmptyMessage(3);
						}// 否则，联网取数据，加载到本地
						else {
							handler.sendEmptyMessage(4);
							// handlerRefreshList.sendEmptyMessage(3);

							// if (NetWorkState.isConnectingToInternet()) {// 开始刷新
							//
							// handler.sendEmptyMessage(1);
							// handlerRefreshList.sendEmptyMessage(3);
							//
							// } else {// 刷新失败
							// handlerRefreshList.sendEmptyMessage(4);
							// handlerRefreshList.sendEmptyMessage(9);
							// handlerRefreshList.sendEmptyMessage(13);
							// }
						}

						// }else if(reqPackId.equals("-1")){ //获取最新课程包列表
					} else if (reqPackIdNew.equals("-1")) { // 获取最新课程包列表
						reqPackType = "1";
						// reqPackId = "-1";
						reqPackIdNew = "-1";
						handler.sendEmptyMessage(4);
						handlerRefreshList.sendEmptyMessage(3);
						// }else if(reqPackId.equals("-2")){ //获取全部课程包列表
					} else if (reqPackIdNew.equals("-2")) { // 获取全部课程包列表
						coursePackArrayList.clear();
						coursePackArrayList = coursePackOp.findDataByAll();
						if (coursePackArrayList.size() != 0) {
							mobClassListAdapter.clearList();// 清除原来的记录
							mobClassListAdapter.addList(coursePackArrayList);
							handlerRefreshList.sendEmptyMessage(3);
						} else {
							if (NetWorkState.isConnectingToInternet()) {// 开始刷新

								handler.sendEmptyMessage(1);
								handlerRefreshList.sendEmptyMessage(3);

							} else {// 刷新失败
								handlerRefreshList.sendEmptyMessage(4);
								handlerRefreshList.sendEmptyMessage(9);
								handlerRefreshList.sendEmptyMessage(13);
							}

							handlerRefreshList.sendEmptyMessage(3);
						}
					}
					initSlideShowViewPicData();
					break;
				// 获取最新课程的请求
				case 4:
					wettingDialog.show();
					Log.d("获取最新课程：", "此处为最新课程的返回结果");
					ClientSession.Instace().asynGetResponse(
							// APPID pageNumber pageCounts

							// 获取所有课程的列表
							new CourseListRequest(reqPackIdNew, reqPackType, "1"),
							new IResponseReceiver() {
								@Override
								public void onResponse(BaseHttpResponse response,
													   BaseHttpRequest request, int rspCookie) {
									CourseListResponse res = (CourseListResponse) response;
									if (res.result.equals("1")) {
										if (res.courseList.size() > 0) {

											coursePackArrayList.clear();
											coursePackArrayList
													.addAll(res.courseList);
											mobClassListAdapter.clearList();// 清除原来的记录
											mobClassListAdapter
													.addList(res.courseList);
											if (!reqPackIdNew.equals("-1")) {
												try {
													coursePackOp
															.insertCoursePacks(res.courseList);
												} catch (Exception e) {
													// TODO Auto-generated catch
													// block
													e.printStackTrace();
												}
											}

										}
									}
									handler.sendEmptyMessage(2);
									wettingDialog.dismiss();
								}
							}, null, null);
					break;
				// 获取上拉加载课程的请求
				case 5:
					reqPageNumber = msg.arg1;
					Log.d("请求列表的页码", reqPageNumber + "");
					wettingDialog.show();
					Log.d("获取上拉加载课程：", "此处为上拉加载课程的返回结果");
					ClientSession.Instace().asynGetResponse(
							// APPID pageNumber pageCounts
							// 获取上拉加载课程的列表,将当前获取的课程列表与上拉加载返回的课程列表做比较，
							new CourseListRequest(reqPackIdNew, reqPackType,
									reqPageNumber + ""), new IResponseReceiver() {
								@Override
								public void onResponse(BaseHttpResponse response,
													   BaseHttpRequest request, int rspCookie) {
									CourseListResponse res = (CourseListResponse) response;
									if (res.result.equals("1")) {
										if (res.courseList.size() > 0) {
										/*
										 * boolean hasCoursePack = false;
										 * for(int i =
										 * 0;i<res.courseList.size();i++){
										 * for(int j =
										 * 0;j<coursePackArrayList.size();j++){
										 * if(coursePackArrayList.get(j).id ==
										 * res.courseList.get(i).id){
										 * hasCoursePack = true; } }
										 * if(hasCoursePack == false){
										 * coursePackArrayList
										 * .add(res.courseList.get(i)); } }
										 */
											mobClassListAdapter.clearList();// 清除原来的记录
											coursePackArrayList
													.addAll(res.courseList);
											mobClassListAdapter
													.addList(coursePackArrayList);
											//
											try {
												// coursePackOp.deleteCoursePackData();
												coursePackOp
														.insertCoursePacks(coursePackArrayList);
												// 这里保证插到数据库中的加载数据放到之前的数据后边
												// coursePackOp.insertCoursePacks(res.courseList);
											} catch (Exception e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
									}
									handlerRefreshList.sendEmptyMessage(3);
									wettingDialog.dismiss();
								}
							}, null, null);
					break;
				case 6:
					initSlideShowViewPicData();

					break;
				case 7:
					// initSlideShowViewUI(mContext);
					break;
				case 8:
					mobClassListTypeAdapter.notifyDataSetChanged();
					coursePackSpinner.setSelection(13);
					break;
				default:
					break;
			}
		}
	};
	Handler handlerRefreshList = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
				case 0: // 从网络获取最新数�?

					break;
				case 1:
					// CrashApplication.getInstance().getCostTimeExecutors().execute(r);
					// CrashApplication.getInstance().getCostTimeExecutors().execute(selectDatabaseOp);
					break;
				case 2:

					break;
				case 3:
					mobClassListView.setVisibility(View.VISIBLE);
					mobClassListWaitBar.setVisibility(View.GONE);
					if (mobClassListAdapter == null) {
						mobClassListAdapter = new MobClassListAdapter(mContext,
								coursePackArrayList);
						mobClassListView.setAdapter(mobClassListAdapter);
					} else {
						mobClassListAdapter.notifyDataSetChanged();
					}
					break;
				case 4:
					mobClassListView.onRefreshComplete();
					// handlerDownload.postDelayed(runnable, 1000);
					break;
				case 5:

					break;
				case 6: // 搜索执行
					// searchCurrPages = 1;
					// sla.clearList();
					// if (currCategory == 100)
					// handler.sendEmptyMessage(10);
					// else
					// handler.sendEmptyMessage(15);
					break;
				case 7:
					// handler.sendEmptyMessage(9);
					// if(isDetached() )
					// {
					// return;
					// }
					// Intent intent = new Intent();
					// intent.setClass(mContext, StudyActivity.class);
					// intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
					// intent.putExtra("source", currCategory);
					// startActivity(intent);
					// getActivity().overridePendingTransition(R.anim.popup_enter,R.anim.popup_exit);
					break;
				case 8:
					// if(isDetached())
					// {
					// return;
					// }
					// wettingDialog.show();
					break;
				case 9:
					wettingDialog.dismiss();
					break;
				case 10: // 搜索listView适配
					// SearchRequest request1 = new SearchRequest(searchWord,
					// searchCurrPages
					// , new RequestCallBack() {
					// @Override
					// public void requestResult(Request result) {
					// // TODO 自动生成的方法存�?
					// SearchRequest sr = (SearchRequest)result;
					// if (sla != null) {
					// if (sr.voasTemps != null
					// && sr.voasTemps.size() != 0) {
					// for (int i = 0; i < sr.voasTemps.size(); i++) {
					// try {
					// sr.voasTemps.get(i).favourite = voaOp
					// .findDataById(sr.voasTemps
					// .get(i).voaid).favourite;
					// } catch (Exception e) {
					// sr.voasTemps.get(i).favourite =false;
					// }
					// }
					// sla.addList(sr.voasTemps);
					// } else {
					// handler.sendEmptyMessage(12);
					// }
					// }
					// searchCurrPages += 1;
					// handler.sendEmptyMessage(11);
					// }
					// });
					// CrashApplication.getInstance().getQueue().add(request1);
					break;
				case 11:
					// sla.notifyDataSetChanged();
					break;
				case 12:
					// textViewSearchInfo.setText(R.string.category_no_search_result);
					break;
				case 13:
					CustomToast.showToast(mContext, R.string.check_network);
					break;
				case 14:
					// CustomToast.showToast(mContext, R.string.newpost_no_new_art,
					// 1000);
					break;
				case 15:
					break;
				case 16:
					// CustomToast.showToast(mContext,
					// R.string.setting_wakeup_notdownload, 2000);
					break;
				case 17:
					// CustomToast.showToast(mContext,
					// R.string.setting_wakeup_bellok, 2000);
					break;
				// case 18:
				// CrashApplication.getInstance().getCostTimeExecutors().execute(selectDatabaseOp);
				// break;
			}
		}
	};
	private OnItemClickListener oItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
			// TODO Auto-generated method stub
			Intent intent = new Intent();
//			Log.d("MobileClassList22222222222222222222:", position + "");
			// curPackId = coursePackArrayList.get(position-2).id;
			// curPackPrice = coursePackArrayList.get(position-2).price;

			// curPackId = MobManager.Instance().courseList.get(position-2).id;
			// curPackPrice =
			// MobManager.Instance().courseList.get(position-2).price;
			if (position > 1) {
				mAdAdapter.getItem(position - 2);
				// 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2

				curPackId = ((CoursePack) mAdAdapter.getItem(position - 2)).id;
				curPackPrice = ((CoursePack) mAdAdapter.getItem(position - 2)).price;

				MobManager.Instance().packid = curPackId;
				MobManager.Instance().ownerid = ((CoursePack) mAdAdapter
						.getItem(position - 2)).ownerid;
				MobManager.Instance().appId = Constant.APPID;
				MobManager.Instance().desc = ((CoursePack) mAdAdapter
						.getItem(position - 2)).desc;
				MobManager.Instance().curPackPrice = curPackPrice;

				MobManager.Instance().CourseNum = ((CoursePack) mAdAdapter
						.getItem(position - 2)).classNum;

				intent.putExtra("packname",
						((CoursePack) mAdAdapter.getItem(position - 2)).name);
				intent.putExtra("position", position);
				intent.putExtra(
						"coursenum",
						((CoursePack) mAdAdapter.getItem(position - 2)).classNum);
				// 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2
				/*
				 * curPackId = ((CoursePack)mAdAdapter.getItem(position-2)).id;
				 * curPackPrice =
				 * ((CoursePack)mAdAdapter.getItem(position-2)).price;
				 * MobManager.Instance().packid =
				 * MobManager.Instance().courseList .get(position - 2).id;
				 * MobManager.Instance().ownerid =
				 * MobManager.Instance().courseList .get(position - 2).ownerid;
				 * // MobManager.Instace().appId = //
				 * getAppId(MobManager.Instace().ownerid);
				 * MobManager.Instance().appId = Constant.APPID;
				 * MobManager.Instance().desc = MobManager.Instance().courseList
				 * .get(position - 2).desc; MobManager.Instance().curPackPrice =
				 * MobManager.Instance().courseList .get(position - 2).price;
				 *
				 * MobManager.Instance().CourseNum =
				 * MobManager.Instance().courseList .get(position - 2).classNum;
				 *
				 * //
				 * intent.putExtra("packname",coursePackArrayList.get(position
				 * -2).name); // intent.putExtra("position", position); //
				 * intent.putExtra("coursenum", //
				 * coursePackArrayList.get(position-2).classNum);
				 *
				 * intent.putExtra("packname",
				 * MobManager.Instance().courseList.get(position - 2).name);
				 * intent.putExtra("position", position);
				 * intent.putExtra("coursenum",
				 * MobManager.Instance().courseList.get(position - 2).classNum);
				 */

				// Log.d("packid:", MobManager.Instace().packid+"");
				// Log.d("ownerid:", MobManager.Instace().ownerid+"");
				// Log.d("appId:", MobManager.Instace().appId);
				// Log.d("desc:", MobManager.Instace().desc);
				// Log.d("curPackPrice:", MobManager.Instace().curPackPrice+"");
				// Log.d("position:", position+"");

				intent.setClass(mContext, MobileClassActivity.class);
				startActivity(intent);
			}
		}
	};
	private OnRefreshListener orfl = new OnRefreshListener() {
		public void onRefresh() {
			// Do work to refresh the list here.
			// handlerDownload.removeCallbacks(runnable);
			if (NetWorkState.isConnectingToInternet()) {// 开始刷新
				handler.sendEmptyMessage(1);
				GetDataTask();
			} else {// 刷新失败
				handlerRefreshList.sendEmptyMessage(4);
				handlerRefreshList.sendEmptyMessage(9);
				handlerRefreshList.sendEmptyMessage(13);
			}
		}




	};

	private void GetDataTask() {
		// handlerRefreshList.sendEmptyMessage(8);
		handler.sendEmptyMessage(0);
		handler.sendEmptyMessage(1); // 做成手动加载
		mobClassListAdapter.notifyDataSetChanged();
	}

	private GetSlidePicListTask getSlidePicListTask;
	private StringBuilder sb;
	/**
	 * 初始化相关Data
	 */
	private void initSlideShowViewPicData() {
		String url = ConfigManager.Instance().loadString(reqPackDesc + "weike");
		if (url.length() == 0) {
			sb = new StringBuilder();
			Log.e("准备发送一次轮播图片的请求：", "准备发送！！！");
			getSlidePicListTask = new GetSlidePicListTask();
			getSlidePicListTask.execute();
		} else {
			slideHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * 异步任务,获取数据
	 *
	 */
	class GetSlidePicListTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				// 这里一般调用服务端接口获取一组轮播图片，下面是从百度找的几个图片

				ClientSession.Instace().asynGetResponse(

						// 获取所有课程轮播图片的列表
						new SlideShowCourseListRequest(reqPackDesc),
						new IResponseReceiver() {

							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								SlideShowCourseListResponse res = (SlideShowCourseListResponse) response;
								if (res.result.equals("1")) {
									if (res.ssCourseList.size() > 0) {
										IMAGE_COUNT = res.ssCourseList.size();
										ssCourseList.clear();
										ssCourseList.addAll(res.ssCourseList);
										imageUrls.clear();
										for (int i = 0; i < res.ssCourseList
												.size(); i++) {
											if (!imageUrls
													.contains(PIC_BASE_URL
															+ res.ssCourseList
															.get(i).pic)) {
												imageUrls.add(PIC_BASE_URL
														+ res.ssCourseList
														.get(i).pic);
												sb.append(PIC_BASE_URL
														+ res.ssCourseList
														.get(i).pic + "-");
											}
										}
										String allUrl = sb.toString();
										ConfigManager.Instance().removeKey(reqPackDesc + "weike");
										ConfigManager.Instance().putString(reqPackDesc + "weike", allUrl);
										try {
											ConfigManager.Instance().removeKey(reqPackDesc );
											ConfigManager.Instance().putString(reqPackDesc, ssCourseList);
											ArrayList o = (ArrayList) ConfigManager.Instance().loadObject(reqPackDesc);
										} catch (Exception e) {
											e.printStackTrace();
										}

									}
									slideHandler.sendEmptyMessage(0);
								}
							}
						}, null, null);

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (result) {
				// Log.e("完成轮播图片地址请求的发送和解析，图片个数：",imageUrlsList.size()+"");
				// initSlideShowViewUI(mContext);
			}
		}
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
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs() // Remove
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	// private void initDot() {
	// //滚动的个数应该和图片的个数相等
	// //清空点所在集合
	// dot_list.clear();
	// dots_ll.removeAllViews();
	// for(int i=0;i<imageUrls.size();i++){
	// View view = new View(mContext);
	// if(i == 0){
	// //红色
	// view.setBackgroundResource(R.drawable.dot_focus);
	// }else{
	// view.setBackgroundResource(R.drawable.dot_blur);
	// }
	// //指定点的大小
	// LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
	// 10,10);
	// //指定点的间距
	// layoutParams.setMargins(8, 0, 8, 0);
	// //添加到线性布局中
	// dots_ll.addView(view,layoutParams);
	// //添加到集合中去
	// dot_list.add(view);
	// }
	// }

	Handler slideHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
				case 0:
					String urls = ConfigManager.Instance().loadString(reqPackDesc + "weike");
					String[] arrUrls = urls.split("-");
					dot_list.clear();
					dots_ll.removeAllViews();
					top_news_viewpager.removeAllViews();
					for (int i = 0; i < arrUrls.length; i++) {
						View view = new View(mContext);
						if (i == 0) {
							// 红色
							view.setBackgroundResource(R.drawable.dot_focus);
						} else {
							view.setBackgroundResource(R.drawable.dot_blur);
						}
						// 指定点的大小
						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
								10, 10);
						// 指定点的间距
						layoutParams.setMargins(8, 0, 8, 0);
						// 添加到线性布局中
						dots_ll.addView(view, layoutParams);
						// 添加到集合中去
						dot_list.add(view);
					}
					// 初始划滚动点
					// initDot();
					// 创建轮播图
					Log.e("MicroClassListActivity ", "同步代码快");
					RollViewPager rollViewPager = new RollViewPager(mContext,
							dot_list, new RollViewPager.OnViewClickListener() {
						// 用于处理点击图片的逻辑
						@Override
						public void viewClick(SlideShowCourse ssCourse) {
							Intent intent = new Intent();
							// 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2
							curPackId = ssCourse.id;
							curPackPrice = ssCourse.price;
							MobManager.Instance().packid = curPackId;
							MobManager.Instance().ownerid = ssCourse.ownerid;
							MobManager.Instance().appId = Constant.APPID;
							MobManager.Instance().desc = ssCourse.desc1;
							MobManager.Instance().curPackPrice = curPackPrice;
							// MobManager.Instance().CourseNum =
							// coursePackArrayList.get(position-2).classNum;
							intent.putExtra("packname", ssCourse.name);
							// intent.putExtra("position", position);
							// intent.putExtra("coursenum",
							// coursePackArrayList.get(position-2).classNum);

							intent.setClass(mContext,
									MobileClassActivity.class);
							startActivity(intent);
						}
					});
					// 将图片地址添加到轮播图中
					try {
						List<SlideShowCourse> ssCourseList = (List<SlideShowCourse>) ConfigManager.Instance().loadObject(reqPackDesc);
						rollViewPager.initSlideShowCourseList(ssCourseList);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
					List<String> imgUrlList = Arrays.asList(arrUrls);
					rollViewPager.initImgUrl(imgUrlList);
					rollViewPager.startRoll();
					top_news_viewpager.addView(rollViewPager);
					break;
				default:
					break;
			}
		}

	};
}
