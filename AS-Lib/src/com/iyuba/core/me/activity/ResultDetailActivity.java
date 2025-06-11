package com.iyuba.core.me.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.iyuba.configation.ConfigManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.INetStateReceiver;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.ErrorResponse;
import com.iyuba.core.me.adapter.LsDetailAdapter;
import com.iyuba.core.me.protocol.ListenDetailRequest;
import com.iyuba.core.me.protocol.ListenDetailResponse;
import com.iyuba.core.me.sqlite.mode.ListenWordDetail;
import com.iyuba.core.R;

public class ResultDetailActivity extends Activity {
	private Context mContext;
	private ListView lsDetailListView;
	private List<ListenWordDetail> mList = new ArrayList<ListenWordDetail>();
	private View lsDetailFooter;
	private LayoutInflater inflater;
	private int page = 1;
	LsDetailAdapter lsDetailAdapter;
	private String testMode = "1";
	private Button backBtn;
	String numPerPage = "20";
	private INetStateReceiver mNetStateReceiver = new INetStateReceiver() {

		@Override
		public void onStartConnect(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onConnected(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartSend(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSend(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSendFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStartRecv(BaseHttpRequest request, int rspCookie,
				int totalLen) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecv(BaseHttpRequest request, int rspCookie, int len) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onRecvFinish(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetError(BaseHttpRequest request, int rspCookie,
				ErrorResponse errorInfo) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onCancel(BaseHttpRequest request, int rspCookie) {
			// TODO Auto-generated method stub

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intel_listen_detail);
		inflater = getLayoutInflater();
		mContext = this;
		Intent intent = getIntent();
		testMode = intent.getStringExtra("testMode");
		lsDetailListView = (ListView) findViewById(R.id.detail_list);
		lsDetailFooter = inflater.inflate(R.layout.comment_footer, null);
		lsDetailAdapter = new LsDetailAdapter(mContext);
		lsDetailListView.addFooterView(lsDetailFooter);
		lsDetailListView.setAdapter(lsDetailAdapter);
		backBtn = (Button) findViewById(R.id.button_back);
		
		backBtn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		lsDetailListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
					// 判断滚动到底部
					if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
						// 当comment不为空且comment.size()不为0且没有完全加载
						mHandler.sendEmptyMessage(0);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

			}
		});
		new UpdateLsDetailThread().start();
	}

	private class UpdateLsDetailThread extends Thread {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			String uid = ConfigManager.Instance().loadString("userId");
			

			ClientSession.Instace().asynGetResponse(
					new ListenDetailRequest(uid, String.valueOf(page),
							numPerPage, testMode), new IResponseReceiver() {
						@Override
						public void onResponse(BaseHttpResponse response,
								BaseHttpRequest request, int rspCookie) {

							ListenDetailResponse tr = (ListenDetailResponse) response;

							if (tr != null && tr.result.equals("1")) {

								mList.clear();
								mList.addAll(tr.mList);
								binderAdapterDataHandler
										.post(binderAdapterDataRunnable);

							} else {

							}
						}
					}, null, mNetStateReceiver);

		}
	}

	private Handler binderAdapterDataHandler = new Handler();
	private Runnable binderAdapterDataRunnable = new Runnable() {
		public void run() {
			if (mList.size() == 0) {
				lsDetailFooter.setVisibility(View.INVISIBLE);
				mHandler.sendEmptyMessage(1);
			} else if (mList.size() < 20) {
				lsDetailAdapter.addList((ArrayList<ListenWordDetail>) mList);
				lsDetailAdapter.notifyDataSetChanged();
				lsDetailFooter.setVisibility(View.INVISIBLE);
			} else {
				lsDetailAdapter.addList((ArrayList<ListenWordDetail>) mList);
				lsDetailAdapter.notifyDataSetChanged();
				lsDetailFooter.setVisibility(View.VISIBLE);
			}
		}
	};

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				page = page + 1;
				new UpdateLsDetailThread().start();
				lsDetailFooter.setVisibility(View.GONE);
				break;
			case 1:
				Toast.makeText(mContext, "已经到底啦~~", 500).show();
				break;
			default:
				break;
			}
		};
	};

}
