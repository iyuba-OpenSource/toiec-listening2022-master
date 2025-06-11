package com.iyuba.core.teacher.activity;

/**
 * 私信界面
 * 
 * @author chentong
 * @version 1.0
 */
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.teacher.adapter.QNoticeListAdapter;
import com.iyuba.core.teacher.protocol.NoticeRequest;
import com.iyuba.core.teacher.protocol.NoticeResponse;
import com.iyuba.core.teacher.protocol.ReadNoticeRequest;
import com.iyuba.core.teacher.sqlite.mode.Notice;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

public class QuestionNotice extends BasisActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private TextView title;
	private Button backButton;
	private Context mContext;
	private CustomDialog waitingDialog;
	private Boolean isLastPage = true;
	private Boolean isTopRefresh = false;
	private Boolean isFootRefresh = false;
	private int page = 1;
	private ListView messageList;// 新闻列表
	private PullToRefreshView refreshView;// 刷新列表
	private QNoticeListAdapter adapter;
	private ArrayList<Notice> letterList = new ArrayList<Notice>();
	public String noticeId="0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.question_notice);
		 CrashApplication.addActivity(this);
		mContext = this;
		initWidget();
		waitingDialog = WaittingDialog.showDialog(mContext);
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.question_title);
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				finish();
			}
		});
		 
		initLetterView();
	}

	private void initLetterView() {
		// TODO Auto-generated method stub
		messageList = (ListView) findViewById(R.id.list);
		refreshView = (PullToRefreshView) findViewById(R.id.listview);
		refreshView.setOnHeaderRefreshListener(this);
		refreshView.setOnFooterRefreshListener(this);
		adapter = new QNoticeListAdapter(mContext);
		messageList.setAdapter(adapter);
		handler.sendEmptyMessage(0);
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				page = 1;
				letterList.clear();
				handler.sendEmptyMessage(1);
				handler.sendEmptyMessage(2);
				break;
			case 1:
				ExeProtocol.exe(
						new NoticeRequest(AccountManagerLib.Instace(mContext).userId, 0,page),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								NoticeResponse res = (NoticeResponse) bhr;
								if (res.result.equals("1")) {
									letterList.addAll(res.list);
									adapter.addList(letterList);
									if (res.list==null||res.list.size()==0) {
										isLastPage = true;
									} else {
										page++;
										isLastPage = false;
									}
								} else {
								}
								handler.sendEmptyMessage(4);
							}

							@Override
							public void error() {
								// TODO Auto-generated method stub
								handler.sendEmptyMessage(3);
								handler.sendEmptyMessage(7);
							}
						});
				break;
			case 2:
				waitingDialog.show();
				break;
			case 3:
				waitingDialog.dismiss();
				break;
			case 4:
				handler.sendEmptyMessage(3);
				adapter.notifyDataSetChanged();
				if (isTopRefresh) {
					isTopRefresh = false;
					refreshView.onHeaderRefreshComplete();
				} else if (isFootRefresh) {
					isFootRefresh = false;
					refreshView.onFooterRefreshComplete();
				}
				setListener();
				break;
			case 6:
				adapter.notifyDataSetChanged();
				// 设置成已读
				ClientSession.Instace().asynGetResponse(
						new ReadNoticeRequest(noticeId),
						new IResponseReceiver() {
							@Override
							public void onResponse(BaseHttpResponse response,
									BaseHttpRequest request, int rspCookie) {
								// TODO Auto-generated method stub
							}
						});
				break;
			case 7:
				CustomToast.showToast(mContext, R.string.check_network);
				break;
			case 8:
				CustomToast.showToast(mContext, R.string.message_add_all);
				break;
			default:
				break;
			}
		}
	};

	private void setListener() {
		messageList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				//SocialDataManager.Instance().letterlist = letterList;
				
				letterList.get(position).isnew = "0";
				//ViewHolder viewholder = (ViewHolder) arg1.getTag();
				//viewholder.isNew.setVisibility(View.GONE);
			    handler.sendEmptyMessage(6);// 进入问题详情
				  noticeId=letterList.get(position).id;
				Intent intent = new Intent();
				intent.putExtra("qid",letterList.get(position).from_id+"");
				intent.setClass(mContext, QuesDetailActivity.class);
				startActivity(intent);
				
				
			}
		});
	}

	@Override
	public void onFooterRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		if (!isLastPage) {
			handler.sendEmptyMessage(1);
			isFootRefresh = true;
		} else {
			handler.sendEmptyMessage(8);
			refreshView.onFooterRefreshComplete();
		}
	}

	@Override
	public void onHeaderRefresh(PullToRefreshView view) {
		// TODO Auto-generated method stub
		handler.sendEmptyMessage(0);
		refreshView.setLastUpdated(ExeRefreshTime
				.lastRefreshTime("MessageCenter"));
		isTopRefresh = true;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
}
