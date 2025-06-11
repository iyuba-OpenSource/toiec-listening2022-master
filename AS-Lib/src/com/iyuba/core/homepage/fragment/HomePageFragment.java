package com.iyuba.core.homepage.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.configation.RuntimeManager;
import com.iyuba.configation.WebConstant;
import com.iyuba.core.common.manager.CommonNewsDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IErrorReceiver;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.common.sqlite.mode.CommonNews;
import com.iyuba.core.discover.activity.news.SimpleNews;
import com.iyuba.core.homepage.adapter.NewsListAdapter;
import com.iyuba.core.homepage.entity.NewsInfo;
import com.iyuba.core.homepage.protocol.BBCNewsRequest;
import com.iyuba.core.homepage.protocol.BBCNewsResponse;
import com.iyuba.core.homepage.protocol.HeadNewsRequest;
import com.iyuba.core.homepage.protocol.HeadNewsResponse;
import com.iyuba.core.homepage.protocol.HeadNewsSearchRequest;
import com.iyuba.core.homepage.protocol.HeadNewsSearchResponse;
import com.iyuba.core.homepage.protocol.SongsRequest;
import com.iyuba.core.homepage.protocol.SongsResponse;
import com.iyuba.core.homepage.protocol.VOACNewsRequest;
import com.iyuba.core.homepage.protocol.VOACNewsResponse;
import com.iyuba.core.homepage.protocol.VOANNewsRequest;
import com.iyuba.core.homepage.protocol.VOANNewsResponse;
import com.iyuba.core.homepage.sqlite.HeadCourseContent;
import com.iyuba.core.homepage.widget.HomePageAudioPlayer;
import com.iyuba.core.homepage.widget.HomePageRollViewPager;
import com.iyuba.core.homepage.widget.ListViewNoScroll;
import com.iyuba.core.homepage.widget.PullToRefreshLayout;
import com.iyuba.core.microclass.protocol.SlideShowCourseListRequest;
import com.iyuba.core.microclass.protocol.SlideShowCourseListResponse;
import com.iyuba.core.microclass.sqlite.mode.SlideShowCourse;
import com.iyuba.headnewslib.HeadlineActivity;
import com.iyuba.headnewslib.ReadActivity;
import com.iyuba.headnewslib.model.Article;
import com.iyuba.headnewslib.model.HeadlineTheme;
import com.iyuba.core.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 作者：renzhy on 16/5/13 16:28
 * 邮箱：renzhongyigoo@gmail.com
 */
public class HomePageFragment extends Fragment {

	private Context mContext;
	private View root;

	private TextView titleDec;
	// 轮播图片的布局
	private LinearLayout top_news_viewpager;
	// 轮播图片指引(圆点)的布局
	private LinearLayout dots_ll;
	// 用于存放轮播图信息的集合
	private List<SlideShowCourse> ssCourseList = new ArrayList<>();
	// 用于存放图片地址的集合
	private ArrayList<String> imageUrls = new ArrayList<>();
	//用于存放轮播图说明文字
	private List<String> decList = new ArrayList<>();
	private ArrayList<CommonNews> newsArrayList = new ArrayList<>();
	private View voacMore, bbcMore, songMore, headNewsMore, voanMore;
	private String dt, headNewsId;
	private Article atcl;
	private boolean voacFinish, bbcFinish, songFinish, headFinish, voanFinish;

	private ArrayList<View> dot_list = new ArrayList<>();
	private String reqPackDesc = "class.first";
	private final static String PIC_BASE_URL = "http://app."+com.iyuba.core.util.Constant.IYBHttpHead+"/dev/";
	private ListViewNoScroll BBCListView, voaSListView, voaNListView, headNewsListView, songListView;
	private List<NewsInfo> BBCList = new ArrayList<>();
	private List<NewsInfo> voaNList = new ArrayList<>();
	private List<NewsInfo> voaSList = new ArrayList<>();
	private List<NewsInfo> headNewsList = new ArrayList<>();
	private List<NewsInfo> songList = new ArrayList<>();
	private List<Article> articles = new ArrayList<Article>();
	private HeadCourseContent hcc;
	private int itemNum = 5;
	private MyListener ml;
	private static PullToRefreshLayout ptrl;
	private boolean netState = true;
	//监听器
	private NetState receiver = new NetState();
	private IntentFilter filter = new IntentFilter();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		RuntimeManager.setApplication(getActivity().getApplication());
		RuntimeManager.setApplicationContext(getActivity().getApplicationContext());
		RuntimeManager.setDisplayMetrics(getActivity());
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(R.layout.lib_home_page,container,false);
		return root;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initWidget();
	}

	public void initWidget() {
//		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(mContext)
//				.memoryCache(new LruMemoryCache(5 * 1024 * 1024))
//				.memoryCacheSize(5 * 1024 * 1024)
//				.diskCacheSize(50 * 1024 * 1024)
//				.diskCacheFileCount(100).build();
//		ImageLoader.getInstance().init(config);

		ml = new MyListener();
		atcl = new Article();
		((PullToRefreshLayout) root.findViewById(R.id.refresh_view))
				.setOnRefreshListener(ml);

		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		getActivity().registerReceiver(receiver, filter);
		receiver.onReceive(mContext, null);

		top_news_viewpager = (LinearLayout) root.findViewById(R.id.top_sliding_viewpager1);
		dots_ll = (LinearLayout) root.findViewById(R.id.dots_ll_ongoing1);
		BBCListView = (ListViewNoScroll) root.findViewById(R.id.bbc_list);
		voaSListView = (ListViewNoScroll) root.findViewById(R.id.voac_list);
		voaNListView = (ListViewNoScroll) root.findViewById(R.id.voan_list);
		headNewsListView = (ListViewNoScroll) root.findViewById(R.id.head_news_list);
		songListView = (ListViewNoScroll) root.findViewById(R.id.song_list);
		titleDec = (TextView) root.findViewById(R.id.top_news_title);
		voacMore = root.findViewById(R.id.voac_more);
		bbcMore = root.findViewById(R.id.bbc_more);
		songMore = root.findViewById(R.id.song_more);
		headNewsMore = root.findViewById(R.id.head_news_more);
		voanMore = root.findViewById(R.id.voan_more);
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMM");
		Date date = new Date();
		dt = dateFormater.format(date);

		voacMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SimpleNews.class);
				intent.putExtra("type", mContext.getString(R.string.voa_speical));
				startActivity(intent);
			}
		});

		bbcMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SimpleNews.class);
				intent.putExtra("type", mContext.getString(R.string.voa_bbc6));
				startActivity(intent);
			}
		});

		songMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SimpleNews.class);
				intent.putExtra("type", mContext.getString(R.string.voa_music));
				startActivity(intent);
			}
		});

		headNewsMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, HeadlineActivity.class);
				startActivity(intent);
			}
		});

		voanMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, SimpleNews.class);
				intent.putExtra("type", mContext.getString(R.string.voa_cs));
				startActivity(intent);
			}
		});

		voaSListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isNetworkAvailable(getActivity())) {
					Intent intent = new Intent();
					newsArrayList.clear();
					newsArrayList.addAll(voaSList);
					String lesson = "VOA慢速英语";
					String shareUrl = "http://voa."+com.iyuba.core.util.Constant.IYBHttpHead+"/audioitem_special_";
					CommonNewsDataManager.Instace().voasTemp = newsArrayList;
					CommonNewsDataManager.Instace().position = position;
					CommonNewsDataManager.Instace().lesson = lesson;
					CommonNewsDataManager.Instace().url = shareUrl;
					intent.setClass(mContext, HomePageAudioPlayer.class);
					intent.putExtra("source", 0);
					startActivity(intent);
				} else {
					slideHandler.sendEmptyMessage(8);
				}
			}
		});

		BBCListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isNetworkAvailable(getActivity())) {
					Intent intent = new Intent();
					newsArrayList.clear();
					newsArrayList.addAll(BBCList);
					String lesson = "BBC英语";
					String shareUrl = "http://bbc."+com.iyuba.core.util.Constant.IYBHttpHead+"/BBCItem_1_";
					CommonNewsDataManager.Instace().voasTemp = newsArrayList;
					CommonNewsDataManager.Instace().position = position;
					CommonNewsDataManager.Instace().lesson = lesson;
					CommonNewsDataManager.Instace().url = shareUrl;
					intent.setClass(mContext, HomePageAudioPlayer.class);
					intent.putExtra("source", 1);
					startActivity(intent);
				} else {
					slideHandler.sendEmptyMessage(8);
				}
			}
		});

		songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isNetworkAvailable(getActivity())) {
					Intent intent = new Intent();
					newsArrayList.clear();
					newsArrayList.addAll(songList);
					String lesson = "听歌学英语";
					String shareUrl = "http://music."+com.iyuba.core.util.Constant.IYBHttpHead+"/play.jsp?SongId=";
					CommonNewsDataManager.Instace().voasTemp = newsArrayList;
					CommonNewsDataManager.Instace().position = position;
					CommonNewsDataManager.Instace().lesson = lesson;
					CommonNewsDataManager.Instace().url = shareUrl;
					intent.setClass(mContext, HomePageAudioPlayer.class);
					intent.putExtra("source", 2);
					startActivity(intent);
				} else {
					slideHandler.sendEmptyMessage(8);
				}
			}
		});

		headNewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				articles.clear();
				if (isNetworkAvailable(getActivity())) {
					for (int i = 0; i < headNewsList.size(); i++) {
						Article at = new Article();
						at.setCreatTime(headNewsList.get(i).time);
						at.setDescCn(headNewsList.get(i).content);
						at.setNewsId(headNewsList.get(i).id);
						at.setPic(headNewsList.get(i).picUrl);
						at.setReadCount(Integer.parseInt(headNewsList.get(i).readTimes));
						at.setTitle(headNewsList.get(i).titleEn);
						at.setTitle_cn(headNewsList.get(i).title);
						at.setSound(headNewsList.get(i).musicUrl);
						at.setSource(headNewsList.get(i).source);
						at.setCategory(Integer.valueOf(headNewsList.get(i).category));
						at.setFlag(Integer.parseInt(headNewsList.get(i).flag));
						at.setWordCount(Integer.parseInt(headNewsList.get(i).wordCount));
						at.setTopicId(Integer.parseInt(headNewsList.get(i).topicId));
						at.setHardWeight(Double.parseDouble(headNewsList.get(i).hardWeight));
						articles.add(at);
					}

					HeadlineTheme mTheme = getActivity().getIntent().getParcelableExtra(HeadlineTheme.TAG);
					if (mTheme == null)
						mTheme = HeadlineTheme.DEFAULT_THEME;
					Intent intent = new Intent(mContext, ReadActivity.class);
					Article item = articles.get(position);
					item.setPic(item.getPic().substring(38));
					intent.putExtra("article", item);
					intent.putExtra(HeadlineTheme.TAG, mTheme);
					startActivity(intent);
				} else {
					slideHandler.sendEmptyMessage(8);
				}
			}
		});

		voaNListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (isNetworkAvailable(getActivity())) {
					Intent intent = new Intent();
					newsArrayList.clear();
					newsArrayList.addAll(voaNList);
					String lesson = "VOA常速英语";
					String shareUrl = "http://voa."+com.iyuba.core.util.Constant.IYBHttpHead+"/audioitem_stardard_";
					CommonNewsDataManager.Instace().voasTemp = newsArrayList;
					CommonNewsDataManager.Instace().position = position;
					CommonNewsDataManager.Instace().lesson = lesson;
					CommonNewsDataManager.Instace().url = shareUrl;
					intent.setClass(mContext, HomePageAudioPlayer.class);
					intent.putExtra("source", 0);
					startActivity(intent);
				} else {
					slideHandler.sendEmptyMessage(8);
				}
			}
		});

		hcc = new HeadCourseContent(mContext);


		if (isNetworkAvailable(getActivity())) {
			initSlideShowViewPicData();
			hcc.deleteHeadCourseContentData();
			new GetBBCNews().start();
			new GetVOACNews().start();
			new GetVOANNews().start();
			new GetSongs().start();
			new GetHeadNews().start();
		} else
			LoadLocalNews();
	}

	/**
	 * 初始化相关Data
	 */
	private void initSlideShowViewPicData() {
		// 一步任务获取图片
		Log.e("准备发送一次轮播图片的请求：", "准备发送！！！");
		new GetSlidePicListTask().execute("");
	}

	/**
	 * 异步任务,获取数据
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
										ssCourseList.clear();
										ssCourseList.addAll(res.ssCourseList);
										for (int i = 0; i < ssCourseList
												.size(); i++) {
											if (ssCourseList.get(i).name.equals("英语头条")) {
												headNewsId = String.valueOf(ssCourseList.get(i).id);
												new SearchHeadNews().start();
											}
										}
										hcc.deleteHeadImage();
										hcc.insertSlideShowCourse(ssCourseList);

									}
									//添加底部的点、为轮播图添加点击处理事件
									slideHandler.sendEmptyMessage(0);

								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated method stub
								if (ssCourseList.size() == 0) {
									Log.e("执行轮播图片请求异常：", "异常中！！！");
									ssCourseList = hcc.findDataByAllHeadImage();
									slideHandler.sendEmptyMessage(0);
								}
							}

						}, new INetStateReceiver() {
							@Override
							public void onStartConnect(BaseHttpRequest request, int rspCookie) {

							}

							@Override
							public void onConnected(BaseHttpRequest request, int rspCookie) {

							}

							@Override
							public void onStartSend(BaseHttpRequest request, int rspCookie, int totalLen) {

							}

							@Override
							public void onSend(BaseHttpRequest request, int rspCookie, int len) {

							}

							@Override
							public void onSendFinish(BaseHttpRequest request, int rspCookie) {

							}

							@Override
							public void onStartRecv(BaseHttpRequest request, int rspCookie, int totalLen) {

							}

							@Override
							public void onRecv(BaseHttpRequest request, int rspCookie, int len) {

							}

							@Override
							public void onRecvFinish(BaseHttpRequest request, int rspCookie) {

							}

							@Override
							public void onNetError(BaseHttpRequest request, int rspCookie, ErrorResponse errorInfo) {
								if (ssCourseList.size() == 0) {
									ssCourseList = hcc.findDataByAllHeadImage();
									slideHandler.sendEmptyMessage(0);
								}
							}

							@Override
							public void onCancel(BaseHttpRequest request, int rspCookie) {

							}
						});

				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
	}

	private void initDot() {
		// 滚动的个数应该和图片的个数相等
		// 清空点所在集合
		dot_list.clear();
		dots_ll.removeAllViews();
		for (int i = 0; i < ssCourseList.size(); i++) {
			View view = new View(mContext);
			if (i == 0) {
				// 红色
				view.setBackgroundResource(R.drawable.dot_focus);
			} else {
				view.setBackgroundResource(R.drawable.dot_blur);
			}
			// 指定点的大小
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					7, 7);
			// 指定点的间距
			layoutParams.setMargins(5, 0, 5, 0);
			// 添加到线性布局中
			dots_ll.addView(view, layoutParams);
			// 添加到集合中去
			dot_list.add(view);
		}
	}

	Handler slideHandler = new Handler() {

		@Override
		public void dispatchMessage(Message msg) {
			// TODO Auto-generated method stub
			super.dispatchMessage(msg);
			switch (msg.what) {
				case 0:
					// 初始划滚动点
					initDot();
					// 创建轮播图
					if (ssCourseList != null && ssCourseList.size() != 0) {
						HomePageRollViewPager rollViewPager = new HomePageRollViewPager(mContext,
								dot_list, new HomePageRollViewPager.OnViewClickListener() {
							// 用于处理点击图片的逻辑
							public void viewClick(SlideShowCourse ssCourse) {
								if (isNetworkAvailable(getActivity())) {
									Intent intent = new Intent();
									CommonNews cn = new CommonNews();
									switch (ssCourse.name) {
										case "VOA慢速英语":
											newsArrayList.clear();
											cn.picUrl = WebConstant.HTTP_STATIC+com.iyuba.core.util.Constant.IYBHttpHead+"/images/voa/" + ssCourse.id + ".jpg";
											cn.title = ssCourse.desc1;
											cn.id = ssCourse.id;
											cn.musicUrl = "/" + dt + "/" + ssCourse.id + ".mp3";
											Log.e("musicUrl", cn.musicUrl);
											newsArrayList.add(cn);
											String lesson = "VOA慢速英语";
											String shareUrl = "http://voa."+com.iyuba.core.util.Constant.IYBHttpHead+"/audioitem_special_";
											CommonNewsDataManager.Instace().voasTemp = newsArrayList;
											CommonNewsDataManager.Instace().position = 0;
											CommonNewsDataManager.Instace().lesson = lesson;
											CommonNewsDataManager.Instace().url = shareUrl;
											intent.setClass(mContext, HomePageAudioPlayer.class);
											intent.putExtra("source", 0);
											startActivity(intent);

											break;
										case "BBC英语":
											newsArrayList.clear();
											cn.picUrl = WebConstant.HTTP_STATIC+com.iyuba.core.util.Constant.IYBHttpHead+"/images/minutes/" + ssCourse.id + ".jpg";
											cn.title = ssCourse.desc1;
											cn.id = ssCourse.id;
											cn.musicUrl = ssCourse.id + ".mp3";
											newsArrayList.add(cn);
											lesson = "BBC英语";
											shareUrl = "http://bbc."+com.iyuba.core.util.Constant.IYBHttpHead+"/BBCItem_1_";
											CommonNewsDataManager.Instace().voasTemp = newsArrayList;
											CommonNewsDataManager.Instace().position = 0;
											CommonNewsDataManager.Instace().lesson = lesson;
											CommonNewsDataManager.Instace().url = shareUrl;
											intent.setClass(mContext, HomePageAudioPlayer.class);
											intent.putExtra("source", 1);
											startActivity(intent);
											break;
										case "听歌学英语":
											newsArrayList.clear();
											cn.picUrl = ssCourse.id + ".jpg";
											cn.title = ssCourse.desc1;
											cn.id = ssCourse.id;
											cn.musicUrl = ssCourse.id + "S.mp3";
											newsArrayList.add(cn);
											lesson = "听歌学英语";
											shareUrl = "http://music."+com.iyuba.core.util.Constant.IYBHttpHead+"/play.jsp?SongId=";
											CommonNewsDataManager.Instace().voasTemp = newsArrayList;
											CommonNewsDataManager.Instace().position = 0;
											CommonNewsDataManager.Instace().lesson = lesson;
											CommonNewsDataManager.Instace().url = shareUrl;
											intent.setClass(mContext, HomePageAudioPlayer.class);
											intent.putExtra("source", 2);
											startActivity(intent);
											break;
										case "英语头条":
											if (atcl.getNewsId() != 0) {
												HeadlineTheme mTheme = getActivity().getIntent().getParcelableExtra(HeadlineTheme.TAG);
												if (mTheme == null)
													mTheme = HeadlineTheme.DEFAULT_THEME;
												intent = new Intent(mContext, ReadActivity.class);
												intent.putExtra("article", atcl);
												intent.putExtra(HeadlineTheme.TAG, mTheme);
												startActivity(intent);
											}
											break;
										case "VOA常速英语":
											newsArrayList.clear();
											cn.picUrl = WebConstant.HTTP_STATIC+com.iyuba.core.util.Constant.IYBHttpHead+"/images/voa/" + ssCourse.id + ".jpg";
											cn.title = ssCourse.desc1;
											cn.id = ssCourse.id;
											cn.musicUrl = "/" + dt + "/" + ssCourse.id + ".mp3";
											Log.e("musicUrl", cn.musicUrl);
											newsArrayList.add(cn);
											lesson = "VOA常速英语";
											shareUrl = "http://voa."+com.iyuba.core.util.Constant.IYBHttpHead+"/audioitem_stardard_";
											CommonNewsDataManager.Instace().voasTemp = newsArrayList;
											CommonNewsDataManager.Instace().position = 0;
											CommonNewsDataManager.Instace().lesson = lesson;
											CommonNewsDataManager.Instace().url = shareUrl;
											intent.setClass(mContext, HomePageAudioPlayer.class);
											intent.putExtra("source", 0);
											startActivity(intent);
											break;
										default:
											break;
									}
									// 之前是position-1，现在因为添加了ListView的Header，所以改成了position-2
//                                curPackId = ssCourse.id;
//                                curPackPrice = ssCourse.price;
//                                MobManager.Instance().packid = curPackId;
//                                MobManager.Instance().ownerid = ssCourse.ownerid;
//                                MobManager.Instance().appId = Constant.APPID;
//                                MobManager.Instance().desc = ssCourse.desc1;
//                                MobManager.Instance().curPackPrice = curPackPrice;
//                                intent.putExtra("packname", ssCourse.name);
//                                intent.setClass(mContext,
//                                        MobileClassActivity.class);
//                                if(curPackId != 0){
//                                    startActivity(intent);
//                                }

								} else {
									slideHandler.sendEmptyMessage(8);
								}
							}
						});

						imageUrls.clear();
						decList.clear();
						for (int i = 0; i < ssCourseList
								.size(); i++) {
							if (!imageUrls
									.contains(PIC_BASE_URL
											+ ssCourseList
											.get(i).pic)) {
								imageUrls.add(PIC_BASE_URL
										+ ssCourseList
										.get(i).pic);
							}
							decList.add(ssCourseList.get(i).desc1);
						}

						// 将图片地址添加到轮播图中
						rollViewPager.initSlideShowCourseList(ssCourseList);
						rollViewPager.initImgUrl(imageUrls);
						rollViewPager.initTitle(decList, titleDec);
						rollViewPager.startRoll();
						top_news_viewpager.removeAllViews();
						top_news_viewpager.addView(rollViewPager);
					} else if (imageUrls != null && imageUrls.size() != 0) {
						HomePageRollViewPager rollViewPager = new HomePageRollViewPager(mContext,
								dot_list, new HomePageRollViewPager.OnViewClickListener() {
							// 用于处理点击图片的逻辑
							public void viewClick(SlideShowCourse ssCourse) {

							}
						});
						// 将图片地址添加到轮播图中
						rollViewPager.initImgUrl(imageUrls);
						rollViewPager.startRoll();
						top_news_viewpager.removeAllViews();
						top_news_viewpager.addView(rollViewPager);
					}
					break;
				case 1:
					NewsListAdapter nlaB = new NewsListAdapter(mContext, BBCList);
					BBCListView.setAdapter(nlaB);
					break;
				case 2:
					NewsListAdapter nlaVc = new NewsListAdapter(mContext, voaSList);
					voaSListView.setAdapter(nlaVc);
					break;
				case 3:
					NewsListAdapter nlaVn = new NewsListAdapter(mContext, voaNList);
					voaNListView.setAdapter(nlaVn);
					break;
				case 4:
					NewsListAdapter nlaSo = new NewsListAdapter(mContext, songList);
					songListView.setAdapter(nlaSo);
					break;
				case 5:
					NewsListAdapter nlaHd = new NewsListAdapter(mContext, headNewsList);
					headNewsListView.setAdapter(nlaHd);
					break;
				case 6:
					ptrl.refreshFinish(PullToRefreshLayout.SUCCEED);
					break;
				case 7:
					new CheckProgress(ptrl).start();
					break;
				case 8:
					Toast.makeText(mContext, "网络已断开！", Toast.LENGTH_SHORT).show();
					break;
				case 9:
					Toast.makeText(mContext, "网络已连接，正在为您刷新！", Toast.LENGTH_SHORT).show();
				default:
					break;
			}
		}

	};

	class GetBBCNews extends Thread {
		public void run() {
			try {
				ClientSession.Instace().asynGetResponse(
						// 获取所有课程轮播图片的列表
						new BBCNewsRequest(itemNum),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								BBCNewsResponse res = (BBCNewsResponse) response;
								if (!res.mList.isEmpty()) {
									BBCList.clear();
									BBCList.addAll(res.mList);
									hcc.insertNewsInfo(BBCList);
									slideHandler.sendEmptyMessage(1);
									bbcFinish = true;
								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated methoxd stub
							}
						}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetVOACNews extends Thread {
		public void run() {
			try {
				ClientSession.Instace().asynGetResponse(
						// 获取所有课程轮播图片的列表
						new VOACNewsRequest(itemNum),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								VOACNewsResponse res = (VOACNewsResponse) response;
								if (!res.mList.isEmpty()) {
//                                String time = res.mList.get(0).date.substring(0, res.mList.get(0).date.indexOf(" "));
//                                voaSList.add(res.mList.get(0));
//                                for (int i = 1; i < res.mList.size(); i++) {
//                                    if (time.equals(res.mList.get(i).date.substring(0, res.mList.get(i).date.indexOf(" "))))
//                                        voaSList.add(res.mList.get(i));
//                                }
									voaSList.clear();
									voaSList.addAll(res.mList);
									hcc.insertNewsInfo(voaSList);
									slideHandler.sendEmptyMessage(2);
									voacFinish = true;
								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated method stub
							}
						}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetVOANNews extends Thread {
		public void run() {
			ClientSession.Instace().asynGetResponse(
					// 获取所有课程轮播图片的列表
					new VOANNewsRequest(itemNum),
					new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
											   BaseHttpRequest request, int rspCookie) {
							VOANNewsResponse res = (VOANNewsResponse) response;
							if (!res.mList.isEmpty()) {
//                                String time = res.mList.get(0).date.substring(0, res.mList.get(0).date.indexOf(" "));
//                                voaNList.add(res.mList.get(0));
//                                for (int i = 1; i < res.mList.size(); i++) {
//                                    if (time.equals(res.mList.get(i).date.substring(0, res.mList.get(i).date.indexOf(" "))))
//                                        voaNList.add(res.mList.get(i));
//                                }
								voaNList.clear();
								voaNList.addAll(res.mList);
								hcc.insertNewsInfo(voaNList);
								slideHandler.sendEmptyMessage(3);
								voanFinish = true;
							}
						}
					},
					new IErrorReceiver() {

						@Override
						public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
											int rspCookie) {
							// TODO Auto-generated method stub
							Log.e("voan", "出错啦");
						}
					}, null);
		}
	}


	class GetSongs extends Thread {
		public void run() {
			try {
				ClientSession.Instace().asynGetResponse(
						// 获取所有课程轮播图片的列表
						new SongsRequest(itemNum),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								SongsResponse res = (SongsResponse) response;
								if (!res.mList.isEmpty()) {
//                                String time = res.mList.get(0).date.substring(0, res.mList.get(0).date.indexOf(" "));
//                                voaNList.add(res.mList.get(0));
//                                for(int i = 1;i<res.mList.size();i++){
//                                    if(time.equals(res.mList.get(i).date.substring(0, res.mList.get(i).date.indexOf(" "))))
//                                        voaNList.add(res.mList.get(i));
//                                }
									songList.clear();
									songList.addAll(res.mList);
									hcc.insertNewsInfo(songList);
									slideHandler.sendEmptyMessage(4);
									songFinish = true;
								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated method stub
							}
						}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class GetHeadNews extends Thread {
		public void run() {
			try {
				ClientSession.Instace().asynGetResponse(
						// 获取所有课程轮播图片的列表
						new HeadNewsRequest(itemNum),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								HeadNewsResponse res = (HeadNewsResponse) response;
								if (!res.mList.isEmpty()) {
//                                String time = res.mList.get(0).date.substring(0, res.mList.get(0).date.indexOf(" "));
//                                voaNList.add(res.mList.get(0));
//                                for(int i = 1;i<res.mList.size();i++){
//                                    if(time.equals(res.mList.get(i).date.substring(0, res.mList.get(i).date.indexOf(" "))))
//                                        voaNList.add(res.mList.get(i));
//                                }
									headNewsList.clear();
									headNewsList.addAll(res.mList);
									hcc.insertNewsInfo(headNewsList);
									slideHandler.sendEmptyMessage(5);
									headFinish = true;
								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated method stub
							}
						}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class SearchHeadNews extends Thread {
		public void run() {
			try {
				ClientSession.Instace().asynGetResponse(
						// 获取所有课程轮播图片的列表
						new HeadNewsSearchRequest(headNewsId),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
												   BaseHttpRequest request, int rspCookie) {
								HeadNewsSearchResponse res = (HeadNewsSearchResponse) response;
								if (!res.mList.isEmpty()) {
//                                String time = res.mList.get(0).date.substring(0, res.mList.get(0).date.indexOf(" "));
//                                voaNList.add(res.mList.get(0));
//                                for(int i = 1;i<res.mList.size();i++){
//                                    if(time.equals(res.mList.get(i).date.substring(0, res.mList.get(i).date.indexOf(" "))))
//                                        voaNList.add(res.mList.get(i));
//                                }
									atcl = res.mList.get(0);
								}
							}
						},
						new IErrorReceiver() {

							@Override
							public void onError(ErrorResponse errorResponse, BaseHttpRequest request,
												int rspCookie) {
								// TODO Auto-generated method stub
							}
						}, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	class CheckProgress extends Thread {
		PullToRefreshLayout pullToRefreshLayout;

		public CheckProgress(PullToRefreshLayout pullToRefreshLayout) {
			super();
			this.pullToRefreshLayout = pullToRefreshLayout;
		}

		public void run() {
			if (voacFinish && bbcFinish && songFinish && headFinish && voanFinish) {
//                pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
				Log.e("comp", "finish");
				slideHandler.sendEmptyMessage(6);
			} else {
				slideHandler.sendEmptyMessageDelayed(7, 500);
//                Looper.prepare();
//                new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//                        // 千万别忘了告诉控件刷新完毕了哦！
////                Toast.makeText(mContext, "可以了", 2000).show();
//                        new CheckProgress(pullToRefreshLayout).start();
//                    }
//                }.sendEmptyMessageDelayed(0, 100);
			}
		}
	}


	/**
	 * 检查当前网络是否可用
	 *
	 * @param
	 * @return
	 */

	public boolean isNetworkAvailable(Activity activity) {
		Context context = activity.getApplicationContext();
		// 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivityManager == null) {
			return false;
		} else {
			// 获取NetworkInfo对象
			NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

			if (networkInfo != null && networkInfo.length > 0) {
				for (int i = 0; i < networkInfo.length; i++) {
					System.out.println(i + "===状态===" + networkInfo[i].getState());
					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
					// 判断当前网络状态是否为连接状态
					if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void LoadLocalNews() {
		ssCourseList = hcc.findDataByAllHeadImage();
		ArrayList<NewsInfo> all;
		all = hcc.findDataByAll();
		for (int i = 0; i < all.size(); i++) {
			NewsInfo ni = all.get(i);
			switch (ni.id) {
				case 1:
					voaSList.add(ni);
					break;
				case 2:
					BBCList.add(ni);
					break;
				case 3:
					songList.add(ni);
					break;
				case 4:
					headNewsList.add(ni);
					break;
				case 5:
					voaNList.add(ni);
					break;
			}
		}
		slideHandler.sendEmptyMessage(0);
		slideHandler.sendEmptyMessage(1);
		slideHandler.sendEmptyMessage(2);
		slideHandler.sendEmptyMessage(3);
		slideHandler.sendEmptyMessage(4);
		slideHandler.sendEmptyMessage(5);
	}


	class MyListener implements PullToRefreshLayout.OnRefreshListener {

		public MyListener() {
			super();
		}

		@SuppressLint("ShowToast")
		@Override
		public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
			// 下拉刷新操作
			ptrl = pullToRefreshLayout;
			if (isNetworkAvailable(getActivity())) {
				voanFinish = false;
				bbcFinish = false;
				songFinish = false;
				headFinish = false;
				voacFinish = false;
				hcc.deleteHeadCourseContentData();
				initSlideShowViewPicData();
				new GetBBCNews().start();
				new GetVOACNews().start();
				new GetVOANNews().start();
				new GetSongs().start();
				new GetHeadNews().start();//要加完成判断
				new CheckProgress(pullToRefreshLayout).start();
			} else {
				slideHandler.sendEmptyMessage(8);
				pullToRefreshLayout.refreshFinish(PullToRefreshLayout.FAIL);
			}
		}

		@Override
		public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
			// 加载操作
		}

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().unregisterReceiver(receiver);
	}

	//    一个广播接收类
	private class NetState extends BroadcastReceiver {
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			ConnectivityManager manager = (ConnectivityManager) getActivity()
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo gprs = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
			NetworkInfo wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (!gprs.isConnected() && !wifi.isConnected()) {
				slideHandler.sendEmptyMessage(8);
				netState = false;
			} else {
				if (netState == false) {
					slideHandler.sendEmptyMessage(9);
					hcc.deleteHeadCourseContentData();
					initSlideShowViewPicData();
					new GetBBCNews().start();
					new GetVOACNews().start();
					new GetVOANNews().start();
					new GetSongs().start();
					new GetHeadNews().start();
					netState = true;
				}
			}


		}

	}
}
