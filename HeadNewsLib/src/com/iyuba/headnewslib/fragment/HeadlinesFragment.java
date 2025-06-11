package com.iyuba.headnewslib.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iyuba.headnewslib.R;
import com.iyuba.headnewslib.ReadActivity;
import com.iyuba.headnewslib.adapter.ArticleListAdapter;
import com.iyuba.headnewslib.model.Article;
import com.iyuba.headnewslib.model.HeadlineTheme;
import com.iyuba.headnewslib.protocol.GetArticlesRequest;
import com.iyuba.headnewslib.protocol.GetArticlesResponse;
import com.iyuba.headnewslib.util.NetWorkState;
import com.iyuba.headnewslib.widget.CustomToast;
import com.iyuba.http.BaseHttpResponse;
import com.iyuba.http.toolbox.ExeProtocol;
import com.iyuba.http.toolbox.ProtocolResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoAdAdapter;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;

import java.util.EnumSet;
import java.util.List;

//import com.youdao.sdk.nativeads.RequestParameters;
//import com.youdao.sdk.nativeads.ViewBinder;
//import com.youdao.sdk.nativeads.YouDaoAdAdapter;
//import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
//import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
//import com.youdao.sdk.nativeads.RequestParameters.NativeAdAsset;
//import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning.Builder;

public class HeadlinesFragment extends BaseFragment {
	private static final String TAG = HeadlinesFragment.class.getSimpleName();
	private static final String YOUDAOADID = "badabcce3f8d6498161b2b7943224dca";
	
	private static final String CATEGORYID = "categoryId";
	private static final String THEME = "theme";
	private static final int PAGECOUNT = 10;

	private Context mContext;

	private PullToRefreshListView newsListView;
	private ArticleListAdapter mAdapter;
	private BaseAdapter workAdapter;

	private int categoryId;
	private List<Article> datalist;

	private boolean isPrepared;
	private boolean mHasLoadedOnce;
	
	private HeadlineTheme mTheme;

	public static HeadlinesFragment newInstance(int categoryId) {
		Bundle bundle = new Bundle();
		bundle.putInt(CATEGORYID, categoryId);
		HeadlinesFragment fragment = new HeadlinesFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
	
	public static HeadlinesFragment newInstance(int categoryId, HeadlineTheme theme) {
		Bundle bundle = new Bundle();
		bundle.putInt(CATEGORYID, categoryId);
		bundle.putParcelable(THEME, theme);
		HeadlinesFragment fragment = new HeadlinesFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mContext = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		categoryId = getArguments().getInt(CATEGORYID);
		mTheme = getArguments().getParcelable(THEME);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (newsListView == null) {
			newsListView = (PullToRefreshListView) inflater.inflate(R.layout.headline_fragment,
					container, false);
			isPrepared = true;
			lazyload();
		}
		ViewGroup parent = (ViewGroup) newsListView.getParent();
		if (parent != null) {
			parent.removeView(newsListView);
		}

		return newsListView;
	}

	@Override
	protected void lazyload() {
		if (!isPrepared || !isVisible || mHasLoadedOnce) {
			return;
		}
		ExeProtocol.exe(new GetArticlesRequest(PAGECOUNT, categoryId, 0), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				GetArticlesResponse response = (GetArticlesResponse) bhr;
				datalist = response.articles;
				handler.sendEmptyMessage(1);
			}

			@Override
			public void error() {
				handler.sendEmptyMessage(0);
			}
		});
		
		/*
		GetArticlesRequest rq = new GetArticlesRequest(PAGECOUNT, categoryId, new RequestCallBack() {

			@Override
			public void requestResult(Request request) {
				datalist = ((GetArticlesRequest) request).articles;
				handler.sendEmptyMessage(1);
			}
		});
		rq.setTag(requestQueue);
		requestQueue.add(rq);
		*/
	}

	private void setView() {
		mAdapter = new ArticleListAdapter(mContext, datalist);
		workAdapter = (categoryId == 119) ? buildWorkAdapter(mContext, mAdapter) : mAdapter;
		initListview(newsListView.getRefreshableView(), workAdapter);
		newsListView.getLoadingLayoutProxy(true, false).setPullLabel(
				mContext.getString(R.string.pulldown));
		newsListView.getLoadingLayoutProxy(true, false).setRefreshingLabel(
				mContext.getString(R.string.pulldown_refreshing));
		newsListView.getLoadingLayoutProxy(true, false).setReleaseLabel(
				mContext.getString(R.string.pulldown_release));
		newsListView.getLoadingLayoutProxy(false, true).setPullLabel(
				mContext.getString(R.string.pullup));
		newsListView.getLoadingLayoutProxy(false, true).setRefreshingLabel(
				mContext.getString(R.string.pullup_loading));
		newsListView.getLoadingLayoutProxy(false, true).setReleaseLabel(
				mContext.getString(R.string.pullup_release));
		newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

			@Override
			public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (NetWorkState.isConnectingToInternet(mContext)) {
					String label = DateUtils.formatDateTime(mContext,
							System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE
									| DateUtils.FORMAT_ABBREV_ALL);
					label = mContext.getString(R.string.lastupdatetime_label) + label;
					refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
					getNewData();
				} else {
					handler.sendEmptyMessageDelayed(4, 400);
				}
			}

			@Override
			public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
				if (NetWorkState.isConnectingToInternet(mContext)) {
					Article lastArticle = mAdapter.getLastItem();
					if(lastArticle.isNullObject()){
						getNewData();
					} else {
						loadMoreData(lastArticle);
					}
				} else {
					handler.sendEmptyMessageDelayed(4, 400);
				}
			}
		});
		/*
		if (!NetWorkState.isConnectingToInternet(mContext)) {
			newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

				@Override
				public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
					handler.sendEmptyMessageDelayed(2, 2000);
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					handler.sendEmptyMessageDelayed(6, 1000);
				}

			});
		} else {
			newsListView.setOnRefreshListener(new OnRefreshListener2<ListView>() {

				@Override
				public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
					String label = DateUtils.formatDateTime(mContext, System.currentTimeMillis(),
							DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
					label = mContext.getString(R.string.lastupdatetime_label) + label;
					refreshView.getLoadingLayoutProxy(true, false).setLastUpdatedLabel(label);
					getNewData();
				}

				@Override
				public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
					loadMoreData();
				}
			});
		}
		*/
		newsListView.getRefreshableView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(mContext, ReadActivity.class);
				Article item = (Article) workAdapter.getItem(position - 1);
				intent.putExtra("article", item);
				intent.putExtra(HeadlineTheme.TAG, mTheme);
				startActivity(intent);
			}
		});
	}

	BaseAdapter buildWorkAdapter(Context context, BaseAdapter sAdapter) {
		YouDaoNativeAdPositioning.Builder adBuilder = YouDaoNativeAdPositioning.newBuilder();
		adBuilder.addFixedPosition(3);
		return new YouDaoAdAdapter(mContext, sAdapter, adBuilder.build());
	}

	private void initListview(ListView lv, BaseAdapter adapter) {
		if (categoryId == 119 ) {
			final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(new ViewBinder.Builder(
					R.layout.headnewslib_native_ad_row).titleId(R.id.native_title)
					.mainImageId(R.id.native_main_image).build());
			((YouDaoAdAdapter) adapter).registerAdRenderer(adRenderer);
			lv.setAdapter(adapter);
			final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(RequestParameters.NativeAdAsset.TITLE,
					RequestParameters.NativeAdAsset.MAIN_IMAGE, RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
			RequestParameters mRequestParameters;
			mRequestParameters = RequestParameters.builder().location(null).keywords(null)
					.desiredAssets(desiredAssets).build();
			((YouDaoAdAdapter) adapter).loadAds(YOUDAOADID, mRequestParameters);
		} else {
			lv.setAdapter(adapter);
		}
	}
	
	private void getNewData() {
		ExeProtocol.exe(new GetArticlesRequest(PAGECOUNT, categoryId, 0), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				try {
					Thread.sleep(1500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				GetArticlesResponse response = (GetArticlesResponse) bhr;
				if (response.hasLegalData()) {
					if (response.articles.get(0).isNewerThan(mAdapter.getFirstItem())) {
						mAdapter.replaceData(response.articles);
						handler.sendEmptyMessage(3);
					} else {
						handler.sendEmptyMessage(2);
					}
				} else {
					handler.sendEmptyMessage(7);
				}
			}

			@Override
			public void error() {
				handler.sendEmptyMessage(7);
			}
		});
	}

	/*
	private void getNewData() {
		GetArticlesRequest rq = new GetArticlesRequest(PAGECOUNT, categoryId, new RequestCallBack() {

			@Override
			public void requestResult(Request request) {
				GetArticlesRequest res = (GetArticlesRequest) request;
				if (res.isRequestSuccessful()) {
					if (res.articles.get(0).isNewerThan(mAdapter.getFirstItem())) {
						mAdapter.replaceData(res.articles);
						handler.sendEmptyMessage(3);
					} else {
						handler.sendEmptyMessage(2);
					}
				} else {
					handler.sendEmptyMessage(7);
				}
			}
		});
		rq.setTag(requestQueue);
		requestQueue.add(rq);
	}
	*/
	
	private void loadMoreData(Article lastArticle) {
		ExeProtocol.exe(new GetArticlesRequest(PAGECOUNT, categoryId, lastArticle.getNewsId()),
				new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						try {
							Thread.sleep(1500);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						GetArticlesResponse response = (GetArticlesResponse) bhr;
						if (response.hasLegalData()) {
							mAdapter.addData(response.articles);
							handler.sendEmptyMessage(5);
						} else {
							handler.sendEmptyMessage(6);
						}
					}

					@Override
					public void error() {
						handler.sendEmptyMessage(7);
					}
				});
	}

	/*
	private void loadMoreData() {
		GetArticlesRequest rq = new GetArticlesRequest(PAGECOUNT, categoryId, currentPage + 1,
				new RequestCallBack() {

					@Override
					public void requestResult(Request request) {
						GetArticlesRequest res = (GetArticlesRequest) request;
						if (res.isRequestSuccessful()) {
							mAdapter.addData(res.articles);
							handler.sendEmptyMessage(5);
						} else {
							handler.sendEmptyMessage(6);
						}
					}
				});
		rq.setTag(requestQueue);
		requestQueue.add(rq);
	}
	*/

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				CustomToast.showToast(mContext, R.string.server_error);
				break;
			case 1: // initiate listview data
				setView();
				mHasLoadedOnce = true;
				break;
			case 2: // show toast when no more new data
				CustomToast.showToast(mContext, R.string.latestdata_hint);
				newsListView.onRefreshComplete();
				break;
			case 3: // after replace data
				mAdapter.notifyDataSetChanged();
				newsListView.onRefreshComplete();
				break;
			case 4:
				CustomToast.showToast(mContext, R.string.network_ungeilivable);
				newsListView.onRefreshComplete();
				break;
			case 5: // after add data
				mAdapter.notifyDataSetChanged();
				newsListView.onRefreshComplete();
				break;
			case 6: // show toast when no more old data
				CustomToast.showToast(mContext, R.string.nomoredata_hint);
				newsListView.onRefreshComplete();
				break;
			case 7: // show toast when network error happen
				CustomToast.showToast(mContext, R.string.server_error);
				newsListView.onRefreshComplete();
			default:
				break;
			}
		}

	};

}
