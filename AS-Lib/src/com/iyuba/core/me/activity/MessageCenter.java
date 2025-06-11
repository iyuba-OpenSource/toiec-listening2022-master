package com.iyuba.core.me.activity;

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
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestMessageLetterList;
import com.iyuba.core.common.protocol.message.RequestSetMessageLetterRead;
import com.iyuba.core.common.protocol.message.ResponseMessageLetterList;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnFooterRefreshListener;
import com.iyuba.core.common.widget.pulltorefresh.PullToRefreshView.OnHeaderRefreshListener;
import com.iyuba.core.discover.activity.FindFriendListActivity;
import com.iyuba.core.me.adapter.MessageListAdapter;
import com.iyuba.core.me.adapter.MessageListAdapter.ViewHolder;
import com.iyuba.core.me.sqlite.mode.MessageLetter;
import com.iyuba.core.R;
import com.umeng.analytics.MobclickAgent;

public class MessageCenter extends BasisActivity implements
		OnHeaderRefreshListener, OnFooterRefreshListener {
	private TextView title;
	private Button backButton, writeButton;
	private Context mContext;
	private CustomDialog waitingDialog;
	private Boolean isLastPage = true;
	private Boolean isTopRefresh = false;
	private Boolean isFootRefresh = false;
	private int page = 1;
	private ListView messageList;// 新闻列表
	private PullToRefreshView refreshView;// 刷新列表
	private MessageListAdapter adapter;
	private ArrayList<MessageLetter> letterList = new ArrayList<MessageLetter>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_center);
		 CrashApplication.addActivity(this);
		mContext = this;
		initWidget();
		waitingDialog = WaittingDialog.showDialog(mContext);
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.message_title);
		backButton = (Button) findViewById(R.id.button_back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO 自动生成的方法存根
				finish();
			}
		});
		writeButton = (Button) findViewById(R.id.write_mail);
		writeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(mContext, FindFriendListActivity.class);
				startActivity(intent);
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
		adapter = new MessageListAdapter(mContext);
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
						new RequestMessageLetterList(AccountManagerLib
								.Instace(mContext).userId, page),
						new ProtocolResponse() {

							@Override
							public void finish(BaseHttpResponse bhr) {
								// TODO Auto-generated method stub
								ResponseMessageLetterList res = (ResponseMessageLetterList) bhr;
								if (res.result.equals("601")) {
									letterList.addAll(res.list);
									adapter.addList(letterList);
									if (res.firstPage == res.nextPage) {
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
			case 5:
				handler.sendEmptyMessage(3);
				Intent intent = new Intent();
				intent.putExtra("friendid",
						SocialDataManager.Instance().letter.friendid);
				intent.putExtra("currentname",
						SocialDataManager.Instance().letter.name);
				intent.setClass(mContext, Chatting.class);
				startActivity(intent);
				break;
			case 6:
				// 设置是否已读
				ClientSession.Instace().asynGetResponse(
						new RequestSetMessageLetterRead(
								AccountManagerLib.Instace(mContext).userId,
								SocialDataManager.Instance().letter.plid),
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
				SocialDataManager.Instance().letterlist = letterList;
				SocialDataManager.Instance().letter = letterList.get(position);
				letterList.get(position).isnew = "0";
				ViewHolder viewholder = (ViewHolder) arg1.getTag();
				viewholder.isNew.setVisibility(View.GONE);
				handler.sendEmptyMessage(6);// 进入私信聊天界面
				handler.sendEmptyMessage(5);
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
