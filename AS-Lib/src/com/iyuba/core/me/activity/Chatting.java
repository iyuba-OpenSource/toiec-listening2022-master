package com.iyuba.core.me.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.base.BasisActivity;
import com.iyuba.core.common.base.CrashApplication;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.network.ClientSession;
import com.iyuba.core.common.network.IResponseReceiver;
import com.iyuba.core.common.protocol.BaseHttpRequest;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.protocol.message.RequestMessageLetterContentList;
import com.iyuba.core.common.protocol.message.RequestSendMessageLetter;
import com.iyuba.core.common.protocol.message.ResponseMessageLetterContentList;
import com.iyuba.core.common.protocol.message.ResponseSendMessageLetter;
import com.iyuba.core.common.widget.dialog.CustomDialog;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.common.widget.dialog.WaittingDialog;
import com.iyuba.core.me.adapter.ChattingAdapter;
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.me.sqlite.mode.MessageLetterContent;
import com.iyuba.core.R;

import personal.iyuba.personalhomelibrary.PersonalHome;

/**
 * 私信内容界面
 * 
 * @author chentong
 * @version 1.1
 * @para "currentname"； 当前用户名 "friendid" 好友id； "search" 是否从搜索而来 可不传； "auto send"
 *       为纠错特化 可不传
 * @修改内容 倒序显示会话内容 修复部分UI
 */
@SuppressLint("NewApi")
public class Chatting extends BasisActivity {
	private ChattingAdapter adapter;
	private ArrayList<MessageLetterContent> list = new ArrayList<MessageLetterContent>();
	private ListView chatContent;
	private Button sendBtn, back, home;
	private EditText textEditor;
	private Button showBtn;
	private RelativeLayout rlShow;
	private CustomDialog waitingDialog;
	private TextView friendName;
	private GridView emotion_GridView;
	private String auto_send;
	private String sendStr;
	private String friendid, search, currentname = null;
	private Context mContext;
	private int page;
	private ClipboardManager clipboard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatting);
		 CrashApplication.addActivity(this);
		mContext = this;
		clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
		chatContent = (ListView) findViewById(R.id.chatting_history_lv);
		Intent intent = getIntent();
		friendid = intent.getStringExtra("friendid");
		search = intent.getStringExtra("search");
		currentname = intent.getStringExtra("currentname");
		auto_send = intent.getStringExtra("auto send");
		initWidget();
		waitingDialog = WaittingDialog.showDialog(mContext);
		initMessages();
	}

	private void initWidget() {
		// TODO Auto-generated method stub
		sendBtn = (Button) findViewById(R.id.send_button);
		showBtn = (Button) findViewById(R.id.show);
		back = (Button) findViewById(R.id.messageletterContent_back_btn);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBackPressed();
			}
		});
		home = (Button) findViewById(R.id.to_home);
		home.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, PersonalHome.class);
				SocialDataManager.Instance().userid = friendid;
				startActivity(intent);
			}
		});
		rlShow = (RelativeLayout) findViewById(R.id.rl_show);
		textEditor = (EditText) findViewById(R.id.text_editor);
		emotion_GridView = (GridView) rlShow.findViewById(R.id.grid_emotion);
		friendName = (TextView) findViewById(R.id.messagelettercontent_friendname);
		if (friendid != null) {
			if (search != null && search.equals("search")) {
				friendName
						.setText(SocialDataManager.Instance().searchItem.username);
			} else {
				friendName.setText(currentname);
			}
		} else {
			friendName.setText(SocialDataManager.Instance().letter.name);
		}
		sendBtn.setOnClickListener(l);
		showBtn.setOnClickListener(l);
		textEditor.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (rlShow.getVisibility() == View.VISIBLE) {
					rlShow.setVisibility(View.GONE);
				}
			}
		});

		textEditor.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View arg0) {
				// TODO Auto-generated method stub
				// 检查剪贴板是否有内容
				if (!clipboard.hasPrimaryClip()) {
					CustomToast.showToast(mContext, "剪贴板无内容");
				} else {
					StringBuffer resultString = new StringBuffer();
					ClipData clipData = clipboard.getPrimaryClip();
					int count = clipData.getItemCount();
					for (int i = 0; i < count; ++i) {
						ClipData.Item item = clipData.getItemAt(i);
						CharSequence str = item.coerceToText(mContext);
						resultString.append(str);
					}
					textEditor.setText(resultString.toString());
				}
				return true;
			}
		});
	}

	// 设置adapter
	private void setAdapterForThis() {
		adapter = new ChattingAdapter(this,
				AccountManagerLib.Instace(mContext).userId);
		chatContent.setAdapter(adapter);
		chatContent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ClipData clip = ClipData.newPlainText("chat message",
						adapter.getItem(arg2).message);
				clipboard.setPrimaryClip(clip);
				CustomToast.showToast(mContext, "内容已复制");
			}
		});
		chatContent.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
					// 判断滚动到顶部
					if (view.getFirstVisiblePosition() == 0) {
						handler.sendEmptyMessage(1);
					}
					break;
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub

			}
		});
	}

	// 为listView添加数据
	private void initMessages() {
		setAdapterForThis();
		if (auto_send != null) {
			textEditor.setText(auto_send);
		}
		handler.sendEmptyMessage(0);
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int jiFen = 0;
			super.handleMessage(msg);
			jiFen = msg.arg1;
			switch (msg.what) {
			case 0:
				list.clear();
				handler.sendEmptyMessage(1);
				handler.sendEmptyMessage(2);
				break;
			case 1:
				// 联网获取日志列表，滑到顶部点击更多进行加载
				if (friendid != null) {
					ClientSession.Instace().asynGetResponse(
							new RequestMessageLetterContentList(
									AccountManagerLib.Instace(mContext).userId,
									friendid, page), new IResponseReceiver() {
								@Override
								public void onResponse(
										BaseHttpResponse response,
										BaseHttpRequest request, int rspCookie) {
									// TODO 自动生成的方法存根
									handler.sendEmptyMessage(3);
									ResponseMessageLetterContentList res = (ResponseMessageLetterContentList) response;
									if (res.result.equals("631")) {
										Collections.reverse(res.list);
										list.addAll(0, res.list);
										adapter.setList(list);
										page++;
										handler.sendEmptyMessage(4);
									} else if (res.result.equals("632")) {
										handler.sendEmptyMessage(5);
									}
								}
							});
				} else {
					ClientSession
							.Instace()
							.asynGetResponse(
									new RequestMessageLetterContentList(
											AccountManagerLib.Instace(mContext).userId,
											SocialDataManager.Instance().letter.friendid,
											page), new IResponseReceiver() {
										@Override
										public void onResponse(
												BaseHttpResponse response,
												BaseHttpRequest request,
												int rspCookie) {
											// TODO 自动生成的方法存根
											handler.sendEmptyMessage(3);
											ResponseMessageLetterContentList res = (ResponseMessageLetterContentList) response;
											if (res.result.equals("631")) {
												Collections.reverse(res.list);
												list.addAll(res.list);
												adapter.setList(list);
												page++;
												handler.sendEmptyMessage(4);
											} else if (res.result.equals("632")) {
												handler.sendEmptyMessage(5);
											}
										}

									});
				}
				break;
			case 2:
				waitingDialog.show();
				break;
			case 3:
				waitingDialog.dismiss();
				break;
			case 4:
				adapter.notifyDataSetChanged();
				break;
			case 5:
				CustomToast.showToast(mContext,
						R.string.message_content_add_all);
				chatContent.setSelection(0);
				break;
			case 6:
				Toast.makeText(mContext, "+"+jiFen+"积分！", Toast.LENGTH_SHORT).show();
				break;
			case 10:
				// 发送私信
				if (search != null && search.equals("search")) {
					ClientSession
							.Instace()
							.asynGetResponse(
									new RequestSendMessageLetter(
											AccountManagerLib.Instace(mContext).userId,
											SocialDataManager.Instance().searchItem.username,
											sendStr), new IResponseReceiver() {
										@Override
										public void onResponse(
												BaseHttpResponse response,
												BaseHttpRequest request,
												int rspCookie) {
											// TODO 自动生成的方法存根
											handler.sendEmptyMessage(4);
											ResponseSendMessageLetter res = (ResponseSendMessageLetter) response;
											if(res.jiFen != null && Integer.parseInt(res.jiFen) > 0){
												Message msg = new Message();
												msg.what = 6;
												msg.arg1 = Integer.parseInt(res.jiFen);
												handler.sendMessage(msg);
											}
										}
									});
				} else if (currentname != null) {
					ClientSession.Instace().asynGetResponse(
							new RequestSendMessageLetter(
									AccountManagerLib.Instace(mContext).userId,
									currentname, sendStr),
							new IResponseReceiver() {
								@Override
								public void onResponse(
										BaseHttpResponse request,
										BaseHttpRequest response, int rspCookie) {
									// TODO Auto-generated method stub
									handler.sendEmptyMessage(4);
									ResponseSendMessageLetter res = (ResponseSendMessageLetter) request;
									if(res.jiFen != null && Integer.parseInt(res.jiFen) > 0){
										Message msg = new Message();
										msg.what = 6;
										msg.arg1 = Integer.parseInt(res.jiFen);
										handler.sendMessage(msg);
									}
								}

							});
				} else {
					ClientSession.Instace().asynGetResponse(
							new RequestSendMessageLetter(
									AccountManagerLib.Instace(mContext).userId,
									SocialDataManager.Instance().letter.name,
									sendStr), new IResponseReceiver() {
								@Override
								public void onResponse(
										BaseHttpResponse response,
										BaseHttpRequest request, int rspCookie) {
									// TODO 自动生成的方法存根
									handler.sendEmptyMessage(4);
									ResponseSendMessageLetter res = (ResponseSendMessageLetter) response;
									if(res.jiFen != null && Integer.parseInt(res.jiFen) > 0){
										Message msg = new Message();
										msg.what = 6;
										msg.arg1 = Integer.parseInt(res.jiFen);
										handler.sendMessage(msg);
									}
								}

							});
				}

				break;
			default:
				break;
			}
		}

	};
	/**
	 * 按键时监听
	 */
	private int[] imageIds = new int[30];
	private View.OnClickListener l = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == sendBtn.getId()) {
				String str = textEditor.getText().toString();
				if (str != null
						&& (sendStr = str.trim().replaceAll("\r", "")
								.replaceAll("\t", "").replaceAll("\n", "")
								.replaceAll("\f", "")) != "") {
					sendMessage(sendStr);
				}
				textEditor.setText("");
			}
			if (v.getId() == showBtn.getId()) {
				if (rlShow.getVisibility() == View.GONE) {
					((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
							.hideSoftInputFromWindow(Chatting.this
									.getCurrentFocus().getWindowToken(),
									InputMethodManager.HIDE_NOT_ALWAYS);
					rlShow.setVisibility(View.VISIBLE);
					initEmotion();
					emotion_GridView.setVisibility(View.VISIBLE);
					emotion_GridView
							.setOnItemClickListener(new OnItemClickListener() {

								@Override
								public void onItemClick(AdapterView<?> arg0,
										View arg1, int arg2, long arg3) {
									Bitmap bitmap = BitmapFactory
											.decodeResource(getResources(),
													imageIds[arg2
															% imageIds.length]);
									ImageSpan imageSpan = new ImageSpan(
											mContext, bitmap);
									String str = "image" + arg2;
									SpannableString spannableString = new SpannableString(
											str);
									String str1 = null;
									str1 = Emotion.express[arg2];
									SpannableString spannableString1 = new SpannableString(
											str1);
									if (str.length() == 6) {
										spannableString
												.setSpan(
														imageSpan,
														0,
														6,
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									} else if (str.length() == 7) {
										spannableString
												.setSpan(
														imageSpan,
														0,
														7,
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									} else {
										spannableString
												.setSpan(
														imageSpan,
														0,
														5,
														Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
									}
									textEditor.append(spannableString1);
								}
							});
				} else {
					rlShow.setVisibility(View.GONE);
				}
			}
		}

	};

	private void sendMessage(String sendStr) {
		handler.sendEmptyMessage(10);
		MessageLetterContent letterContent = new MessageLetterContent();
		letterContent.setMessage(sendStr);
		letterContent.setDirection(1);
		letterContent.setAuthorid(AccountManagerLib.Instace(mContext).userId);
		letterContent
				.setDateline(String.valueOf(System.currentTimeMillis() / 1000));
		list.add(letterContent);
		adapter.setList(list);
		rlShow.setVisibility(View.GONE);
		((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(Chatting.this.getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void initEmotion() {

		SimpleAdapter simpleAdapter = new SimpleAdapter(this,
				Emotion.initEmotion(),
				R.layout.team_layout_single_expression_cell,
				new String[] { "image" }, new int[] { R.id.image });
		emotion_GridView.setAdapter(simpleAdapter);
		emotion_GridView.setNumColumns(7);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	protected void onResume() {
		super.onResume();
		page = 1;
	}
}
