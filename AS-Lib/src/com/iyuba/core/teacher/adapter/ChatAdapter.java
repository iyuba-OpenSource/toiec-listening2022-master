package com.iyuba.core.teacher.adapter;

import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.stetho.common.LogUtil;
import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.OnPlayStateChangedListener;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.QuestionManager;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.teacher.activity.QuezActivity;
import com.iyuba.core.teacher.protocol.AgreeAgainstRequest;
import com.iyuba.core.teacher.sqlite.mode.AnswerInfo;
import com.iyuba.core.teacher.sqlite.mode.AnswerType;
import com.iyuba.core.teacher.sqlite.mode.Chat;
import com.iyuba.core.teacher.sqlite.op.CommentAgreeOp;
import com.iyuba.core.R;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

/**
 * expandableListView������
 *
 */
public class ChatAdapter extends BaseExpandableListAdapter {
	private Context mContext;
	private List<AnswerInfo> group;
	private List<List<Chat>> child;
	private Player mediaPlayer;
	private ImageView tempVoice;
	private boolean playingVoice = false;
	TextView t;
	private int voiceCount;
	AnswerType answerType;
	public ChatAdapter(Context context, List<AnswerInfo> group,
			List<List<Chat>> child,TextView tv,AnswerType a) {
		this.mContext = context;
		this.group = group;
		this.child = child;
		t=tv;
		answerType=a;
	}

	@Override
	public int getGroupCount() {
		return group.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return child.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return group.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return child.get(childPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}
	
	
	
	public void pausePlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}
	
	
	
	// 播放语音
		private void playVoice(String url) {
			stopVoice();
			if (mediaPlayer == null) {
				mediaPlayer = new Player(mContext,
						new OnPlayStateChangedListener() {

							@Override
							public void playFaild() {
								// TODO Auto-generated method stub

							}

							@Override
							public void playCompletion() {
								// TODO Auto-generated method stub
								playingVoice = false;
								handlerv.removeMessages(1, tempVoice);
								tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);
							}
						});
			}
			mediaPlayer.playUrl(url);
		}
	
	
	
	/**
	 * ��ʾ��group
	 */
	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		final GroupViewHolder holder;
		final AnswerInfo info = group.get(groupPosition);
		//用于判断此用户是否有回复
		 answerType.hasanswer=1;
		
		//用于判断此老师是否回复过这个问题，回复过则调用追问接口，否则调用回复接口
		if(AccountManagerLib.Instace(mContext).userId.equals(info.uid+"")
				&&AccountManagerLib.Instace(mContext).isteacher.equals("1")){
			answerType.isanswer=1;
		}
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.lib_group_item_chatting, null);
			holder = new GroupViewHolder();
			holder.answerImage = (ImageView) convertView.findViewById(R.id.answer_image);
			holder.answerName = (TextView) convertView.findViewById(R.id.answer_name);
			holder.answerInfo = (TextView) convertView.findViewById(R.id.answer_info);
			
			convertView.setTag(holder);
		} else {
			holder = (GroupViewHolder) convertView.getTag();
		}
		//TODO Image
		
		GitHubImageLoader.Instace(mContext).setCirclePic(info.timg+ "", holder.answerImage, R.drawable.noavatar_small);



		if (String.valueOf(info.uid) != null &&
				!"".equals(info.uid) &&
				!String.valueOf(info.uid).equals("0")
				&& info.uid < 50000000) {
			holder.answerName.setText(info.username);

		} else {//临时账户
			holder.answerName.setText(String.valueOf(info.uid) );
		}
		LogUtil.e("用户名  "+info.username);
		LogUtil.e("用户id  "+info.uid);


		if(info.time == null || "null".equals(info.time)) {
			info.time = "";
		}
		if(info.location == null || "null".equals(info.location)) {
			info.location = "";
		}
		info.time=info.time.substring(0, 19);
		holder.answerInfo.setText(info.time + " " + info.location);
		convertView.setClickable(true);
		return convertView;

	}
	
	/**
	 * ��ʾ��child
	 */
	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		final ChildViewHolder holder;
		final Chat chat = child.get(groupPosition).get(childPosition);
		final AnswerInfo info = group.get(groupPosition);
		if (convertView == null) {
//			if(info.uid==chat.fromid) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.lib_chatting_item_msg_text_left, null);
//			} else {
//				convertView = LayoutInflater.from(mContext).inflate(
//						R.layout.lib_chatting_item_msg_text_r, null);
//			}
				
				
			
			holder = new ChildViewHolder();
			holder.textContent = (TextView) convertView.findViewById(R.id.text_content);
			holder.otherLayout = convertView.findViewById(R.id.other_layout);
			holder.otherContent = (ImageView) convertView.findViewById(R.id.other_content);
			holder.timeText = (TextView) convertView.findViewById(R.id.time_text);
			holder.bottomView = convertView.findViewById(R.id.bottom_view);
			holder.questing = convertView.findViewById(R.id.questing);
			holder.quesContent= ((TextView)convertView.findViewById(R.id.questing_text));
			holder.agree_icon=(ImageView) convertView.findViewById(R.id.agree_icon);
			if(QuestionManager.getInstance().question.getUid().equals(AccountManagerLib.Instace(mContext).userId))
				{
				
				holder.quesContent.setText(" 追问问题");
					info.type=1;
				}else{
					holder.quesContent.setText(" 向他提问");
					info.type=2;
				}
			if(AccountManagerLib.Instace(mContext).userId.equals(info.uid+"")){
				info.type=3;
				holder.quesContent.setText(" 回复问题");
				
			}
			if(!AccountManagerLib.Instace(mContext).userId.equals(info.uid+"")&&(AccountManagerLib.Instace(mContext).isteacher.equals("1"))){
				//holder.questing.setVisibility(View.GONE);
			}
			
			holder.attention = convertView.findViewById(R.id.attention);
			holder.agree = convertView.findViewById(R.id.agree);
			holder.attentionText = (TextView) convertView.findViewById(R.id.attention_text);
			holder.agreeText = (TextView) convertView.findViewById(R.id.agree_text);
			holder.agreeText.setText(""+info.agreeCount+"");
			convertView.setTag(holder);
			
		} else {
			holder = (ChildViewHolder) convertView.getTag();
		} 
		
		if(chat.type == 1) {
			holder.textContent.setVisibility(View.VISIBLE);
			holder.otherLayout.setVisibility(View.GONE);
			holder.textContent.setText(chat.content);
			
			if(info.uid==chat.fromid) {
				holder.textContent.setBackgroundDrawable(
						mContext.getResources().getDrawable(R.drawable.chatfrom_bg));
			} else {
				
				RelativeLayout.LayoutParams para=new RelativeLayout.LayoutParams(
			            LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT );
				para.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			    holder.textContent.setLayoutParams(para);
				
				holder.textContent.setBackgroundDrawable(
						mContext.getResources().getDrawable(R.drawable.chatto_bg));
			}
		} else {
			holder.textContent.setVisibility(View.GONE);
			holder.otherLayout.setVisibility(View.VISIBLE);
			
			holder.otherContent.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO 自动生成的方法存根
					playingVoice = true;//
					
					if (tempVoice != null) {// 播放之前先停止其他的播放
						handlerv.removeMessages(1);
						tempVoice.setImageResource(R.drawable.chatfrom_voice_playing);
					}
					tempVoice=holder.otherContent;
					playVoice("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/" + chat.content);// 播放
					voiceAnimation(v);// 播放的动画
				}
			});
			
			
			
			
		}
		
		if(childPosition == child.get(groupPosition).size() - 1) {
			holder.bottomView.setVisibility(View.VISIBLE);
		} else {
			holder.bottomView.setVisibility(View.GONE);
		}
		
		holder.questing.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(info.type==1){
					answerType.aid=info.answerid;
					t.setHint("向"+info.username+"追问");
					answerType.sub=2;//追问
					  InputMethodManager inputMethodManager=(InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		                inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}else if(info.type==2){	
				Intent intent = new Intent();
				intent.putExtra("askuid",info.uid+"");//向某人提问的uid
				intent.setClass(mContext, QuezActivity.class);
				t.setHint("讨论");
				 InputMethodManager inputMethodManager=(InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
	              inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				mContext.startActivity(intent);
				
				}else if(info.type==3){	
					answerType.aid=info.answerid;
					answerType.sub=2;//同样是追问
					t.setHint("回复问题"+"");
					  InputMethodManager inputMethodManager=(InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
		              inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				}
				
			
				
			}
		});
		
		holder.attention.setOnClickListener(new  OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				SocialDataManager.Instance().userid =info.uid+"";
//				Intent intent = new Intent();
//				intent.setClass(mContext, PersonalHome.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//			    mContext.startActivity(intent);
				mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
						Integer.valueOf(SocialDataManager.Instance().userid),
						SocialDataManager.Instance().userName, 0));
			}
		});
		
		
		
		if (checkAgree("a"+info.answerid, AccountManagerLib.Instace(mContext).userId) == 1) {
			holder.agree_icon.setBackgroundResource(R.drawable.agree_press);
			holder.agreeText.setText(""+info.agreeCount+"");
		}else{
			
			holder.agree_icon.setBackgroundResource(R.drawable.agree_normal);
			holder.agreeText.setText(""+info.agreeCount+"");
		}
		
		//点赞
		holder.agree.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				if(!AccountManagerLib.Instace(mContext).checkUserLogin()) {
					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
				    mContext.startActivity(intent);
				    return;
				}
				
				if (checkAgree("a"+info.answerid, AccountManagerLib.Instace(mContext).userId) == 1) {
					handler.sendEmptyMessage(3);
				}else{
					info.agreeCount=info.agreeCount+1;
					handler.sendEmptyMessage(0);
				ExeProtocol.exe(new AgreeAgainstRequest(AccountManagerLib.Instace(mContext).userId,AccountManagerLib.Instace(mContext).userName,"answerid",""+info.answerid), new ProtocolResponse() {

					@Override
					public void finish(BaseHttpResponse bhr) {
						handler.sendEmptyMessage(4);
					}

					@Override
					public void error() {
						
					}});
				new CommentAgreeOp(mContext).saveData(
						"a"+info.answerid, AccountManagerLib.Instace(mContext).userId, "agree");
				
				}
				
			}
		});
		
		return convertView;
	}

	class GroupViewHolder {
		ImageView answerImage;
		TextView answerName;
		TextView answerInfo;
	}
	
	class ChildViewHolder {
		TextView textContent;
		View otherLayout;
		ImageView otherContent;
		TextView timeText;
		View bottomView;
		View questing;
		TextView quesContent;
		View attention;
		View agree;
		ImageView agree_icon;
		TextView attentionText;
		TextView agreeText;
	
		
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true ;
	}
	//查看此用户是否已点赞
			private int checkAgree(String commentId, String uid) {
				return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
			}
			
			
			
			
			Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						notifyDataSetChanged();
						break;
						
					case 1:
						notifyDataSetChanged();
						break;
					case 3:
						CustomToast.showToast(mContext, R.string.agree_already);
						break;
					case 4:
						CustomToast.showToast(mContext, R.string.comment_agree);
						break;
						
					}
					
				}
			};
//以下是关于语音播放的代码
			// 停止播放动画
			private void voiceStopAnimation(View v) {
				handlerv.removeMessages(1, v);
			}
			//播放动画
			private void voiceAnimation(View v) {
				voiceStopAnimation(v);
				voiceCount = 0;
				tempVoice = (ImageView) v;
				handlerv.obtainMessage(1, tempVoice).sendToTarget();
			}
			// 播放语音评论之前先在这里reset播放器
			private void stopVoice() {
				if (mediaPlayer != null&&playingVoice) {
					mediaPlayer.reset();
				}
			}
			Handler handlerv = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					// TODO Auto-generated method stub
					super.handleMessage(msg);
					switch (msg.what) {
					case 0:
						notifyDataSetChanged();
						break;
					case 1:
						// 通过不断切换图片来表示动画
						if (voiceCount % 3 == 0) {
							((ImageView) msg.obj)
									.setImageResource(R.drawable.chatfrom_voice_playing_f1);
						} else if (voiceCount % 3 == 1) {
							((ImageView) msg.obj)
									.setImageResource(R.drawable.chatfrom_voice_playing_f2);
						} else if (voiceCount % 3 == 2) {
							((ImageView) msg.obj)
									.setImageResource(R.drawable.chatfrom_voice_playing_f3);
						}
						voiceCount++;
						handlerv.sendMessageDelayed(handlerv.obtainMessage(1, msg.obj),
								500);
						break;
					case 2:
						CustomToast.showToast(mContext, R.string.check_network);
						break;
					case 3:
						CustomToast.showToast(mContext, R.string.comment_already);
						break;
					case 4:
						CustomToast.showToast(mContext, R.string.comment_agree);
						break;
					case 5:
						CustomToast.showToast(mContext, R.string.comment_disagree);
						break;
					}
				}
			};	
			
}
