package com.iyuba.core.microclass.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.iyuba.core.common.activity.LoginActivity;
import com.iyuba.core.common.manager.AccountManagerLib;
import com.iyuba.core.common.manager.SocialDataManager;
import com.iyuba.core.common.widget.Player;
import com.iyuba.core.common.widget.circularimageview.CircularImageView;
import com.iyuba.core.microclass.sqlite.mode.Comment;
import com.iyuba.core.util.Constant;
import com.iyuba.core.R;
import com.nostra13.universalimageloader.core.ImageLoader;

import personal.iyuba.personalhomelibrary.ui.home.PersonalHomeActivity;

public class MobClassCommentAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<Comment> mList = new ArrayList<Comment>();
	private boolean playingVoice = false;
	private Player mediaPlayer;
	private ImageView tempVoice;
	private int voiceCount;
	private String voiceId;
	private String uid;
	private int type;
	
	
	
	public MobClassCommentAdapter(Context mContext, int type) {
		this.mContext = mContext;
		if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
			uid = AccountManagerLib.Instace(mContext).userId;
		} else {
			uid = "0";
		}
		this.type = type;
	}
	
	

	public MobClassCommentAdapter(Context mContext, ArrayList<Comment> mList,
			int type) {
		this.mContext = mContext;
		this.mList = mList;
		if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
			uid = AccountManagerLib.Instace(mContext).userId;
		} else {
			uid = "0";
		}
		this.type = type;
	}

	public void setData(ArrayList<Comment> Comments) {
		// TODO Auto-generated method stub
		mList = Comments;
	}

	public void addList(ArrayList<Comment> Comments) {
		// TODO Auto-generated method stub
		mList.addAll(Comments);
	}
	
	public void clearList() {
		mList.clear();
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Comment curItem = mList.get(position);
		final float scroingStar = (float) curItem.star/2;
		final ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.lib_microclass_item_contentcomment, null);
			viewHolder = new ViewHolder();
			viewHolder.image = (CircularImageView) convertView
					.findViewById(R.id.comment_image);
			viewHolder.body = (TextView) convertView
					.findViewById(R.id.comment_content);
			viewHolder.name = (TextView) convertView
					.findViewById(R.id.user_name);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.comment_time);
//			viewHolder.reply = (Button) convertView.findViewById(R.id.reply);
			viewHolder.comment = convertView;
			viewHolder.ratingBar = (RatingBar) convertView.findViewById(R.id.rb_userscoring);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.name.setText(curItem.userName);
		viewHolder.time.setText(curItem.CreateDate);
		viewHolder.body.setText(curItem.ShuoShuo);
		viewHolder.ratingBar.setRating(scroingStar);
//		viewHolder.reply.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// TODO 自动生成的方法存根
//				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
//					Intent intent = new Intent("toreply");
//					intent.putExtra("username", curItem.userName);
//					mContext.sendBroadcast(intent);
//				} else {
//					Intent intent = new Intent();
//					intent.setClass(mContext, Login.class);
//					mContext.startActivity(intent);
//				}
//			}
//		});
//		if (type == 1) {
//			viewHolder.reply.setVisibility(View.GONE);
//		}
		
		ImageLoader.getInstance().displayImage(
		"http://api."+Constant.IYBHttpHead2+"/v2/api.iyuba?protocol=10005&uid="
		+ mList.get(position).Userid + "&size=middle", viewHolder.image);
		
//		GitHubImageLoader.Instace(mContext).setCireclePic(mList.get(position).Userid+"",
//				"middle",viewHolder.image);
		
		viewHolder.image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (AccountManagerLib.Instace(mContext).checkUserLogin()) {
					SocialDataManager.Instance().userid = curItem.Userid+"";
//					Intent intent = new Intent();
//					intent.setClass(mContext, PersonalHome.class);
//					mContext.startActivity(intent);
					mContext.startActivity(PersonalHomeActivity.buildIntent (mContext,
							Integer.valueOf(SocialDataManager.Instance().userid),
							SocialDataManager.Instance().userName, 0));
				} else {
					Intent intent = new Intent();
					intent.setClass(mContext, LoginActivity.class);
					mContext.startActivity(intent);
				}
			}
		});
		
		return convertView;
	}
	
	public class ViewHolder {
		CircularImageView image;// 头像图片
		TextView name; // 用户名
		TextView time; // 发布时间
		TextView body; // 评论体
//		Button reply; // 回复按钮
		View comment; // 整体
		RatingBar ratingBar;//评分
	}

}
