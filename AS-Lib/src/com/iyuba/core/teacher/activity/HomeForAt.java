/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.core.teacher.activity;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.listener.ResultIntCallBack;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.ExeRefreshTime;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.ContextMenu;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.teacher.adapter.QuestionListAdapter;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesRequest;
import com.iyuba.core.teacher.protocol.DeleteAnswerQuesResponse;
import com.iyuba.core.teacher.protocol.GetQuesListRequest;
import com.iyuba.core.teacher.protocol.GetQuesListResponse;
import com.iyuba.core.R;

/**
 * 类名
 * 
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class HomeForAt extends Activity{
	private Context mContext;
	private TextView tvSelectQuesType, tvTitle;
	private ImageView btnEditQues;
	private PullToRefreshListView refreshView;// 刷新列表
	private ListView quesListview;
	private QuestionListAdapter quesAdapter;
	private ArrayList<QuestionListBean.QuestionDataBean> quesList = new ArrayList<QuestionListBean.QuestionDataBean>();
	public int pageNum = 1;
	boolean isLast = false;
	ContextMenu contextMenu;
	
	private CustomDialog waitDialog;

	private static final String[] question_app_type_arr = { "全部", "VOA", "BBC",
			"听歌", "CET4", "CET6", "托福", "N1", "N2", "微课", "雅思", "初中", "高中",
			"考研", "新概念", "走遍美国" };

	private static final String[] question_ability_type_arr = { "全部", "口语",
			"听力", "阅读", "写作", "翻译", "单词", "语法", "其他" };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		 CrashApplication.addActivity(this);
		setContentView(R.layout.lib_ques_list);

		mContext = this;
		
		waitDialog = WaittingDialog.showDialog(mContext);
		
		if (AccountManagerLib.Instace(mContext).userId == null) {
			AccountManagerLib.Instace(mContext).userId = "0";
		}

		initWidget();

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void startActivityForResult(Intent intent, int requestCode) {
		// TODO Auto-generated method stub
		super.startActivityForResult(intent, requestCode);

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		getHeaderData();
	}

	// 顶部今日头条事件监听器
	private OnClickListener SelectQuesOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// 跳转到筛选Activity
			Intent intent = new Intent();
			intent.setClass(mContext, SelectQuestionType.class);
			startActivityForResult(intent, 0);
		}
	};

	public void initWidget() {
		tvSelectQuesType = (TextView) findViewById(R.id.tv_select_ques_type);
		tvTitle = (TextView) findViewById(R.id.tv_teacher_title);
		btnEditQues = (ImageView) findViewById(R.id.tinsert);
		contextMenu = (ContextMenu) findViewById(R.id.context_menu);

//		quesListview = (ListView) findViewById(R.id.lv_home_ques_list);
//		refreshView = (PullToRefreshView) findViewById(R.id.ll_queslist_content);
//		refreshView.setOnHeaderRefreshListener(this);
//		refreshView.setOnFooterRefreshListener(this);

		refreshView = (PullToRefreshListView) findViewById(R.id.ptr_question_list);
		quesListview = refreshView.getRefreshableView();
		refreshView.setMode(Mode.BOTH);
		refreshView.setOnRefreshListener(orfl);
		
		tvSelectQuesType.setOnClickListener(SelectQuesOnClickListener);

		btnEditQues.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// 如果没登录则跳转登录
				if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {

					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					startActivity(intent);
					return;
				}

				Intent intent = new Intent();
				intent.setClass(mContext, QuezActivity.class);
				startActivity(intent);
			}
		});

		quesAdapter = new QuestionListAdapter(mContext);

		quesAdapter.clearList();
		quesListview.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {

				QuestionManager.getInstance().question = quesList.get(arg2);

				if (AccountManagerLib.Instace(mContext).userId != null
						&& quesList.get(arg2).getUid().equals(AccountManagerLib
								.Instace(mContext).userId)) {

					final int theqid = quesList.get(arg2).getQuestionid();
					final int num = arg2;
					contextMenu.setText(mContext.getResources().getStringArray(
							R.array.choose_delete));
					contextMenu.setCallback(new ResultIntCallBack() {

						@Override
						public void setResult(int result) {
							// TODO Auto-generated method stub
							switch (result) {
							case 0:
								delAlertDialog(theqid + "", num);
								break;
							case 1:
								Intent intent = new Intent();
								intent.setClass(mContext,
										QuesDetailActivity.class);
								intent.putExtra("qid", theqid + "");
								startActivity(intent);
								break;
							default:
								break;
							}
						}
					});
					contextMenu.show();
					return true;
				} else {
					return false;

				}

			}
		});

		if (quesAdapter != null) {
			quesListview.setAdapter(quesAdapter);
		}

		quesListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				QuestionManager.getInstance().question = quesList.get(arg2);
				Intent intent = new Intent();
				intent.setClass(mContext, QuesDetailActivity.class);
				intent.putExtra("qid", quesList.get(arg2).getQuestionid() + "");
				startActivity(intent);
			}
		});

		getHeaderData();
	}
	
	private OnRefreshListener2<ListView> orfl = new OnRefreshListener2<ListView>() {

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			
			if (NetWorkState.isConnectingToInternet()) {// 开始刷新

				new GetHeaderDataTask().execute();
				
			} else {// 刷新失败
				
			}
			
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			// TODO Auto-generated method stub
			
			if (NetWorkState.isConnectingToInternet()) {// 开始刷新

				new GetFooterDataTask().execute();
				
			} else {// 刷新失败
				
			}
			
		}
	};

	private void delAlertDialog(final String id, final int num) {
		AlertDialog alert = new AlertDialog.Builder(mContext).create();
		alert.setTitle(R.string.alert_title);
		alert.setIcon(android.R.drawable.ic_dialog_alert);
		alert.setMessage(mContext.getString(R.string.alert_delete));
		alert.setButton(AlertDialog.BUTTON_POSITIVE,
				getResources().getString(R.string.alert_btn_ok),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, int which) {

						ExeProtocol.exe(new DeleteAnswerQuesRequest("0", id,
								AccountManagerLib.Instace(mContext).userId),
								new ProtocolResponse() {

									@Override
									public void finish(BaseHttpResponse bhr) {
										// TODO Auto-generated method stub
										DeleteAnswerQuesResponse tr = (DeleteAnswerQuesResponse) bhr;
										if (tr.result.equals("1")) {
											quesList.remove(num);

											// handler.sendEmptyMessage(1);
											binderAdapterDataHandler
													.post(binderAdapterDataRunnable);
											handler.sendEmptyMessage(8);

										} else {
											quesList.remove(num);

											// handler.sendEmptyMessage(1);
											binderAdapterDataHandler
													.post(binderAdapterDataRunnable);
											handler.sendEmptyMessage(8);
										}
									}

									@Override
									public void error() {
									}
								});

					}
				});
		alert.setButton(AlertDialog.BUTTON_NEGATIVE,
				getResources().getString(R.string.alert_btn_cancel),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, int which) {
					}
				});
		alert.show();
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				break;
			case 1:
				quesAdapter.notifyDataSetChanged();
				break;
			case 2:
				waitDialog.show();
				break;
			case 3:
				waitDialog.dismiss();
				break;
			case 4:
				refreshView.onRefreshComplete();
				break;
			case 6:
				CustomToast.showToast(mContext, R.string.no_data);
				break;
			case 7:
				CustomToast.showToast(mContext, "已是最后一页");
				break;
			case 8:
				CustomToast.showToast(mContext, "删除成功!");
				break;
			}
		}
	};

	private Handler binderAdapterDataHandler = new Handler();
	private Runnable binderAdapterDataRunnable = new Runnable() {
		public void run() {
			quesAdapter.clearList();
			quesAdapter.addList(quesList);
			quesAdapter.notifyDataSetChanged();
		}
	};

	public void getHeaderData() {

		handler.sendEmptyMessage(2);
		ExeProtocol.exe(new GetQuesListRequest(1), new ProtocolResponse() {

			@Override
			public void finish(BaseHttpResponse bhr) {
				// TODO Auto-generated method stub
				GetQuesListResponse tr = (GetQuesListResponse) bhr;
				if (tr.list != null && tr.list.size() != 0) {

					quesList.clear();
					quesList.addAll(tr.list);
					binderAdapterDataHandler.post(binderAdapterDataRunnable);

					pageNum = 2;
					// handler.sendEmptyMessage(1);
					handler.sendEmptyMessage(3);
					handler.sendEmptyMessage(4);
					if (tr.list.size() < 20)
						isLast = true;
					else
						isLast = false;
				} else {
					handler.sendEmptyMessage(3);
					handler.sendEmptyMessage(6);
					handler.sendEmptyMessage(4);
				}
			}

			@Override
			public void error() {
				// TODO Auto-generated method stub
				handler.sendEmptyMessage(3);
				handler.sendEmptyMessage(6);
				handler.sendEmptyMessage(4);
			}
		});
	}

	public void getFooterData() {
		if (isLast) {
			handler.sendEmptyMessage(4);
			handler.sendEmptyMessage(7);
			return;
		}
		handler.sendEmptyMessage(2);
		ExeProtocol.exe(new GetQuesListRequest(pageNum),
				new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						GetQuesListResponse tr = (GetQuesListResponse) bhr;
						if (tr.list != null && tr.list.size() != 0) {
							quesList.addAll(tr.list);
							pageNum++;
							binderAdapterDataHandler
									.post(binderAdapterDataRunnable);

							// handler.sendEmptyMessage(1);
							handler.sendEmptyMessage(3);
							handler.sendEmptyMessage(4);
							if (tr.list.size() < 20)
								isLast = true;
							else
								isLast = false;
						} else {
							// handler.sendEmptyMessage(1);
							binderAdapterDataHandler
									.post(binderAdapterDataRunnable);
						}
					}

					@Override
					public void error() {
						// TODO Auto-generated method stub
						// handler.sendEmptyMessage(1);
						binderAdapterDataHandler
								.post(binderAdapterDataRunnable);
						handler.sendEmptyMessage(4);
					}
				});
	}

	private class GetHeaderDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
			getHeaderData();
			return null;
		}
	}

	private class GetFooterDataTask extends AsyncTask<Void, Void, String[]> {

		@Override
		protected String[] doInBackground(Void... params) {
			ExeRefreshTime.lastRefreshTime("NewPostListUpdateTime");
			getFooterData();
			return null;
		}
	}

//	@Override
//	public void onFooterRefresh(PullToRefreshView view) {
//		// TODO Auto-generated method stub
//		new GetFooterDataTask().execute();
//	}
//
//	@Override
//	public void onHeaderRefresh(PullToRefreshView view) {
//		// TODO Auto-generated method stub
//		Log.e("onHeaderRefresh", "onHeaderRefresh");
//		refreshView.setLastUpdated(ExeRefreshTime
//				.lastRefreshTime("NewPostListUpdateTime"));
//		new GetHeaderDataTask().execute();
//	}

}
