package com.iyuba.core.microclass.fragment;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.MobManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.util.NetWorkState;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.microclass.adapter.MobClassCommentAdapter;
import com.iyuba.core.microclass.protocol.CommentRequest;
import com.iyuba.core.microclass.protocol.CommentResponse;
import com.iyuba.core.microclass.protocol.ExpressionRequest;
import com.iyuba.core.microclass.sqlite.mode.Comment;
import com.iyuba.core.R;

public class ContentCommentFragment extends Fragment{

	private Context mContext;
	private View root;
	private int PackId;
	private boolean isConnected;
//	private ProgressDialog wettingDialog;
	
	private Player mPlayer;
	private ListView commentList;
    private MobClassCommentAdapter commentAdapter;
    private ArrayList<Comment> commentsList = new ArrayList<Comment>();
    private int curCommentPage = 1;
 	private String expressWord;
 	private Button expressButton;
 	private EditText expressEditText;
 	private View commentLoadMoreLayout;
 	private LayoutInflater layoutInflater;
 	private View commentFooter;
 	private ListView listComment;
 	private boolean commentAll = false;
 	private boolean commentExist = false;
 	private TextView commentLoadMoreTextView;
 	private int commentMode = 0;// 0是文字评论，1是语音评论
 	private Drawable amps[] = new Drawable[8];
 	private Button btnScroing;
 	private RatingBar rb_curCourse;
    private float userScroing = -1f;
    private BigDecimal bd;  
    private float scroingStars = 4.0f;
    private float tempScroingStars;
    
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		root = inflater.inflate(R.layout.microclass_mobclass_contentcomment,container,false);
		initWidget();
		setComment();
		return root;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	private void initWidget(){
		
		isConnected = NetWorkState.isConnectingToInternet();
		PackId = MobManager.Instance().packid;
		mPlayer = new Player(mContext, null);
		rb_curCourse = (RatingBar) root.findViewById(R.id.rb_coursecomment);
		btnScroing = (Button) root.findViewById(R.id.btn_scoring);
		commentList = (ListView) root.findViewById(R.id.coursecomment_list);
		expressButton = (Button) root.findViewById(R.id.button_express);
		expressEditText = (EditText) root
				.findViewById(R.id.editText_express);
		commentFooter = getActivity().getLayoutInflater().inflate(R.layout.microclass_mobclass_contentcomment_footer,null);
		commentList.addFooterView(commentFooter);
		commentLoadMoreLayout = commentFooter
				.findViewById(R.id.comment_loadmore);
		commentLoadMoreTextView = (TextView) commentFooter
				.findViewById(R.id.comment_loadmore_text);
		expressButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
					if (commentMode == 0) {
						InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(
								expressEditText.getWindowToken(), 0);
						String expressionInput = expressEditText.getText()
								.toString();
						if (expressionInput.toString().equals("")) {
							CustomToast.showToast(mContext,
									R.string.study_input_comment);
						} else {
							expressWord = expressionInput;
							userScroing = rb_curCourse.getRating()*2;
							Log.d("expressWord:", expressionInput);
							Log.d("expressScroing:",userScroing+"");
							handler_comment.sendEmptyMessage(1);
							expressEditText.setText("");
						}
					}
				
				} else {
					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					startActivity(intent);
				}
			}
		});
		commentLoadMoreLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!isConnected) {
					commentLoadMoreTextView.setText(getResources().getString(
							R.string.check_network));
				} else if (!commentAll) {
					handler_comment.sendEmptyMessage(0);
				}
			}
		});
	}
	private void setComment(){
		
		handler_comment.sendEmptyMessage(0);
		
		Log.d("setComment中 调用构造函数之前：", commentsList.size()+"");
		commentAdapter = new MobClassCommentAdapter(mContext,commentsList, 0);
		Log.d("setComment中 调用构造函数之后：", commentsList.size()+"");
		commentList.setAdapter(commentAdapter);
		
		btnScroing.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				userScroing = rb_curCourse.getRating()*2;
				Toast.makeText(mContext, "感谢评分！", Toast.LENGTH_SHORT).show();
			}
		});
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
//				wettingDialog.show();
				break;
			case 1:
//				wettingDialog.dismiss();
				break;
			case 2:
				Toast.makeText(mContext, R.string.play_check_network, Toast.LENGTH_SHORT).show();
				break;
			case 3:
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			}
		}
	};
	
	private Handler handler_comment = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
//				new Thread(new commentThread()).start();
				ExeProtocol.exe(new CommentRequest(String.valueOf(PackId),String.valueOf(curCommentPage)), 
						new ProtocolResponse(){

						@Override
						public void finish(BaseHttpResponse bhr) {
							// TODO Auto-generated method stub
							Looper.prepare();

							CommentResponse res = (CommentResponse) bhr;

							if (res.resultCode.equals("511")) {

								commentsList.addAll(res.Comments);
								
								//******************************************************************
								//删除重复的评论，并将顺序保持
//								Set set = new HashSet();
//								List newList = new ArrayList();
//								for (Iterator iter = res.Comments.iterator(); iter.hasNext();) {
//								Object element = iter.next();
//									if (set.add(element)){
//									   newList.add(element);
//									}
//								}
//								commentsList.clear();
//								commentsList.addAll(newList);
								//删除重复的评论，并将顺序保持
								//******************************************************************
								
								
								if (commentsList.size() != 0) {
									commentExist = true;
									if (!res.firstPage
											.equals(res.nextPage)) {
										curCommentPage += 1;
										Log.d("CommentResponse中：11111", commentsList.size()+"");
										handler_comment.sendEmptyMessage(5);
									} else {
										Log.d("CommentResponse中：22222", commentsList.size()+"");
										handler_comment.sendEmptyMessage(5);
										handler_comment.sendEmptyMessage(6);
									}
								} else {
									Log.d("CommentResponse中：33333", commentsList.size()+"");
									handler_comment.sendEmptyMessage(6);
								}
							} else {
								Log.d("CommentResponse中：44444", commentsList.size()+"");
								handler_comment.sendEmptyMessage(6);
							}
							Looper.loop();
						}
						@Override
						public void error() {
							// TODO Auto-generated method stub
							
						}
						
					});
				break;
			case 1:
				String uid = AccountManagerLib.Instace(mContext).userId;
				
				ExeProtocol.exe(new ExpressionRequest(uid,PackId+"",expressWord,((int)userScroing)+""), new ProtocolResponse() {
					
					@Override
					public void finish(BaseHttpResponse bhr) {
						// TODO Auto-generated method stub
						handler_comment.sendEmptyMessage(4);
					}
					
					@Override
					public void error() {
						// TODO Auto-generated method stub
						
					}
				});
				
//				ClientSession.Instace().asynGetResponse(
//						new ExpressionRequest(uid,
//								String.valueOf(voaTemp.voaid), expressWord,
//								username), new IResponseReceiver() {
//							@Override
//							public void onResponse(BaseHttpResponse response,
//									BaseHttpRequest request, int rspCookie) {
//								handler_comment.sendEmptyMessage(4);
//							}
//						});

				break;
			case 3:
//				commentLoadMoreTextView.setText(getResources().getString(
//						R.string.study_load_more));
//				commentAll = false;
				handler_comment.sendEmptyMessage(0);
				break;
			case 4:
				curCommentPage = 1;
				commentsList.clear();
				handler_comment.sendEmptyMessage(3);
				break;
			case 5:
				
				if (commentAdapter == null) {
					Log.d("handler 5", "yyyyyyyyyyyyyyy");
					commentAdapter = new MobClassCommentAdapter(mContext,commentsList, 0);

					commentList.setAdapter(commentAdapter);
				} else {
					Log.d("handler 5", "nnnnnnnnnnnnnnn");
					commentAdapter.notifyDataSetChanged();
				}
				break;
			case 6:
				if (commentExist) {
					commentLoadMoreTextView.setText(getResources().getString(
							R.string.study_all_loaded));
					commentAll = true;
				} else if (commentsList == null) {
					commentLoadMoreTextView.setText(getResources().getString(
							R.string.check_network));
				} else {
					commentLoadMoreTextView.setText(getResources().getString(
							R.string.study_no_comment));
					commentAll = true;
				}
				break;
			case 14:
				Dialog dialog1 = new AlertDialog.Builder(mContext)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("提示")
				.setMessage("请对课程打分后提交评论！")
				.setPositiveButton("OK",null).create();
				dialog1.show();// 如果要显示对话框，一定要加上这句
				break;
			default:
				break;
			}
		}
	};
}
