package com.iyuba.core.teacher.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.listener.ProtocolResponse;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.protocol.BaseHttpResponse;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.ExeProtocol;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.dialog.CustomToast;
import com.iyuba.core.iyumooc.teacher.bean.QuestionListBean;
import com.iyuba.core.teacher.activity.ShowLargePicActivity;
import com.iyuba.core.teacher.protocol.AgreeAgainstRequest;
import com.iyuba.core.teacher.sqlite.op.CommentAgreeOp;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class QuestionListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<QuestionListBean.QuestionDataBean> mList = new ArrayList<QuestionListBean.QuestionDataBean>();
	public	HashMap<String ,String> abilityTypeCatalog=new HashMap<String, String>();
	public	HashMap<String ,String> appTypeCatalog=new HashMap<String, String>();

	private Player mediaPlayer;

	public QuestionListAdapter(Context context, ArrayList<QuestionListBean.QuestionDataBean> quesList) {
		mContext = context;
		mList = quesList;
		setAbilityTypeCatalog();
		setAppTypeCatalog();
	}
	
	public QuestionListAdapter(Context context) {
		mContext = context;
		setAbilityTypeCatalog();
		setAppTypeCatalog();

	}

	// 查看此用户是否已点赞
	private int checkAgree(String commentId, String uid) {
		return new CommentAgreeOp(mContext).findDataByAll(commentId, uid);
	}

	public void setData(ArrayList<QuestionListBean.QuestionDataBean> quesList) {
		mList = quesList;
	}

	public void addList(ArrayList<QuestionListBean.QuestionDataBean> quesList) {
		mList.addAll(quesList);
	}
	
	public void clearList(){
		mList.clear();
	}

	public void pausePlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.pause();
		}
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public QuestionListBean.QuestionDataBean getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final QuestionListBean.QuestionDataBean ques = mList.get(position);
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(
					R.layout.lib_list_item_question, null);
			viewHolder = new ViewHolder();
			viewHolder.quesRecommend = (ImageView) convertView
					.findViewById(R.id.iv_ques_recommend);
			viewHolder.quesIcon = (ImageView) convertView
					.findViewById(R.id.ques_icon);
			viewHolder.quesName = (TextView) convertView
					.findViewById(R.id.ques_name);
			viewHolder.quesInfo = (TextView) convertView
					.findViewById(R.id.ques_info);
			viewHolder.answerIcon = (ImageView) convertView
					.findViewById(R.id.answer_icon);
			viewHolder.isAnswer = (TextView) convertView
					.findViewById(R.id.is_answer);
			viewHolder.answerName = (TextView) convertView
					.findViewById(R.id.answer_name);
			viewHolder.quesDisc = (TextView) convertView
					.findViewById(R.id.ques_disc);
			viewHolder.quesPic = (ImageView) convertView
					.findViewById(R.id.ques_pic);
			viewHolder.quesSource = (TextView) convertView
					.findViewById(R.id.ques_source);
			viewHolder.answerNum = (TextView) convertView
					.findViewById(R.id.answer_num);
			viewHolder.commentNum = (TextView) convertView
					.findViewById(R.id.comment_num);
			viewHolder.agreeNum = (TextView) convertView
					.findViewById(R.id.agree_num);
			viewHolder.user_inf = convertView.findViewById(R.id.user_inf);
			viewHolder.agree = (ImageView) convertView.findViewById(R.id.agree);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		GitHubImageLoader.Instace(mContext).setCirclePic(
				mList.get(position).getUid(), viewHolder.quesIcon);

		if(mList.get(position).getFlg() == 2){
			viewHolder.quesRecommend.setVisibility(View.VISIBLE);
		}else{
			viewHolder.quesRecommend.setVisibility(View.INVISIBLE);
		}

		if (checkAgree("q" + mList.get(position).getQuestionid(),
				AccountManagerLib.Instace(mContext).userId) == 1) {
			viewHolder.agree.setBackgroundResource(R.drawable.agree_press);
		} else {

			viewHolder.agree.setBackgroundResource(R.drawable.agree_normal);

		}

		viewHolder.agree.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (!AccountManagerLib.Instace(mContext).checkUserLogin()) {
					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}

				if (checkAgree("q" + mList.get(position).getQuestionid(),
						AccountManagerLib.Instace(mContext).userId) == 1) {
					handler.sendEmptyMessage(3);
				} else {

					ExeProtocol.exe(
							new AgreeAgainstRequest(AccountManagerLib
									.Instace(mContext).userId, AccountManagerLib
									.Instace(mContext).userName, "questionid",
									"" + mList.get(position).getQuestionid()),
							new ProtocolResponse() {

								@Override
								public void finish(BaseHttpResponse bhr) {
									handler.sendEmptyMessage(4);
								}

								@Override
								public void error() {

								}
							});

					new CommentAgreeOp(mContext).saveData(
							"q" + mList.get(position).getQuestionid(),
							AccountManagerLib.Instace(mContext).userId, "agree");
					mList.get(position).setAgreecount(mList.get(position).getAgreecount()+1);
					handler.sendEmptyMessage(0);
				}
			}
		});

		viewHolder.quesIcon.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				
				if(!AccountManagerLib.Instace(mContext).checkUserLogin()){
					
					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
					return;
				}
				
				SocialDataManager.Instance().userid = ques.getUid();
//				Intent intent = new Intent();
//				intent.setClass(mContext, PersonalHome.class);
//				intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				mContext.startActivity(intent);

				mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
						Integer.valueOf(SocialDataManager.Instance().userid),
						SocialDataManager.Instance().userName, 0));
			}
		});

		viewHolder.quesName.setText(ques.getUsername());

		if (ques.getCreatetime() == null || "null".equals(ques.getCreatetime())) {
			ques.setCreatetime("");
		}

		if (ques.getLocation() == null || "null".equals(ques.getLocation())) {
			ques.setLocation("");
		}
		ques.setCreatetime(ques.getCreatetime().substring(0,19));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			long time = sdf.parse(ques.getCreatetime()).getTime();
			viewHolder.quesInfo.setText(formatTime(time));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// viewHolder.quesInfo.setText(ques.time);

		if (ques.getAnswercount() == 0) {
			viewHolder.answerIcon.setVisibility(View.INVISIBLE);
			viewHolder.isAnswer.setVisibility(View.INVISIBLE);
		} else {
			viewHolder.answerIcon.setVisibility(View.VISIBLE);
			viewHolder.isAnswer.setVisibility(View.VISIBLE);
		}

		if (ques.getQuestion() != null && !ques.getQuestion().trim().equals("")) {
			viewHolder.quesDisc.setText(ques.getQuestion());
			viewHolder.quesDisc.setVisibility(View.VISIBLE);
		} else {
			viewHolder.quesDisc.setVisibility(View.INVISIBLE);
		}

		if (ques.getImg() != null && !ques.getImg().trim().equals("")) {
			
			
//			int screenWidth = getScreenWidth(this);
//			ViewGroup.LayoutParams lp = testImage.getLayoutParams();
//			lp.width = screenWidth;
//			lp.height = LayoutParams.WRAP_CONTENT;
//			testImage.setLayoutParams(lp);
//
//			testImage.setMaxWidth(screenWidth);
//			testImage.setMaxHeight(screenWidth * 5); 这里其实可以根据需求而定，我这里测试为最大宽度的5倍
			
			viewHolder.quesPic.setVisibility(View.VISIBLE);
			viewHolder.quesPic.setAdjustViewBounds(true);
			viewHolder.quesPic.setMaxHeight(360);
			viewHolder.quesPic.setMaxWidth(240);

			ImageLoader.getInstance().displayImage("http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"
					+ ques.getImg().replaceAll("_b.jpg", "_s.jpg"), viewHolder.quesPic);
			

			viewHolder.quesPic.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					Intent intent = new Intent();
					intent.setClass(mContext, ShowLargePicActivity.class);
					intent.putExtra("pic", "http://www."+com.iyuba.core.util.Constant.IYBHttpHead+"/question/"
							+ ques.getImg());
					mContext.startActivity(intent);
				}
			});
		} else {
			viewHolder.quesPic.setVisibility(View.GONE);
		}

		viewHolder.quesSource.setText(appTypeCatalog.get(ques.getCategory2())
				+ " " + abilityTypeCatalog.get(ques.getCategory1()));

		viewHolder.answerNum.setText(ques.getAnswercount() + "");
		viewHolder.commentNum.setText(ques.getCommentcount() + "");
		viewHolder.agreeNum.setText(ques.getAgreecount() + "");

		return convertView;
	}

	private String formatTime(long time) {
		Date date = new Date(time);
		Date date2 = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar.setTime(date);
		calendar2.setTime(date2);

		if(calendar2.get(Calendar.YEAR)
				- calendar.get(Calendar.YEAR) > 0){
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}else if (calendar2.get(Calendar.DAY_OF_YEAR)
				- calendar.get(Calendar.DAY_OF_YEAR) > 0) {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		} else if (System.currentTimeMillis() - time > 60 * 60 * 1000) {
			return (System.currentTimeMillis() - time) / (60 * 60 * 1000)
					+ "小时之前";
		} else if (System.currentTimeMillis() - time > 60 * 1000) {
			return (System.currentTimeMillis() - time) / (60 * 1000) + "分钟之前";
		} else if(System.currentTimeMillis() - time > 60){
			
			return (System.currentTimeMillis() - time) / (1000) + "秒之前";
			
		}else if(System.currentTimeMillis() - time == 0){
			return "1秒之前";
		}else{
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			return format.format(date);
		}

	}

	public  void setAbilityTypeCatalog(){
		abilityTypeCatalog=new HashMap<String, String>();
		abilityTypeCatalog.put("0", "其他");
		abilityTypeCatalog.put("1", "口语");
		abilityTypeCatalog.put("2", "听力");
		abilityTypeCatalog.put("3", "阅读");
		abilityTypeCatalog.put("4", "写作");
		abilityTypeCatalog.put("5", "翻译");
		abilityTypeCatalog.put("6", "单词");
		abilityTypeCatalog.put("7", "语法");
		abilityTypeCatalog.put("8", "其他");
	}

	public  void setAppTypeCatalog(){
		appTypeCatalog=new HashMap<String, String>();
		appTypeCatalog.put("0", "其他");
		appTypeCatalog.put("101", "VOA");
		appTypeCatalog.put("102", "BBC");
		appTypeCatalog.put("103", "听歌");
		appTypeCatalog.put("104", "CET4");
		appTypeCatalog.put("105", "CET6");
		appTypeCatalog.put("106", "托福");
		appTypeCatalog.put("107", "N1");
		appTypeCatalog.put("108", "N2");
		appTypeCatalog.put("109", "N3");
		appTypeCatalog.put("110", "微课");
		appTypeCatalog.put("111", "雅思");
		appTypeCatalog.put("112", "初中");
		appTypeCatalog.put("113", "高中");
		appTypeCatalog.put("114", "考研");
		appTypeCatalog.put("115", "新概念");
		appTypeCatalog.put("116", "走遍美国");
		appTypeCatalog.put("117", "英语头条");
	}

	public class ViewHolder {
		View user_inf;

		ImageView quesRecommend;
		ImageView quesIcon;
		TextView quesName;
		TextView quesInfo;
		ImageView answerIcon;
		TextView isAnswer;
		TextView answerName;
		TextView quesDisc;
		ImageView quesPic;
		TextView quesSource;
		TextView answerNum;
		TextView commentNum;
		TextView agreeNum;
		ImageView agree;
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

}
