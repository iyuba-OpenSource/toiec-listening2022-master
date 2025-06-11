package com.iyuba.core.me.adapter;
/**
 * 心情评论适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.Expression;
import com.iyuba.core.me.sqlite.mode.DoingsCommentInfo;
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.R;

import personal.iyuba.personalhomelibrary.PersonalHome;

public class DoingsCommentAdapter extends BaseAdapter {
	private Context mContext;
	public ArrayList<DoingsCommentInfo> mList = new ArrayList<DoingsCommentInfo>();
	private ViewHolder viewHolder;
	DoingsCommentInfo item = new DoingsCommentInfo();

	/**
	 * @param mContext
	 * @param mList
	 */
	public DoingsCommentAdapter(Context mContext,
			ArrayList<DoingsCommentInfo> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	/**
	 * @param mContext
	 */
	public DoingsCommentAdapter(Context mContext) {
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	public void setData(ArrayList<DoingsCommentInfo> doingsCommentList) {
		mList = doingsCommentList;
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

	public void clearList() {
		mList.clear();
		handler.sendEmptyMessage(0);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_doings, null);
			viewHolder = new ViewHolder();
			viewHolder.message = (TextView) convertView
					.findViewById(R.id.doingslist_message);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.doingslist_time);
			viewHolder.userImageView = (ImageView) convertView
					.findViewById(R.id.doingslist_userPortrait);
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.doingslist_username);
			viewHolder.replyCommentNum = (TextView) convertView
					.findViewById(R.id.doingslist_replyNum);
			convertView.setTag(viewHolder);

		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (mList.get(position).message.equals("")
				|| mList.get(position).message == null) {
			viewHolder.message.setText("");
		} else {
			String zhengze = "image[0-9]{2}|image[0-9]";
			Emotion emotion = new Emotion();
			mList.get(position).message = emotion
					.replace(mList.get(position).message);
			try {
				SpannableString spannableString = Expression
						.getExpressionString(mContext,
								mList.get(position).message, zhengze);
				viewHolder.message.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		viewHolder.username.setText(mList.get(position).username);
		if (mList.get(position).userBitmap != null) {
			viewHolder.userImageView
					.setImageBitmap(mList.get(position).userBitmap);
		} else {
			viewHolder.userImageView.setImageResource(R.drawable.defaultavatar);
		}
		long time = Long.parseLong(mList.get(position).dateline) * 1000;
		viewHolder.time.setText(DateFormat.format("yyyy-MM-dd kk:mm", time));
		viewHolder.userImageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mContext, PersonalHome.class);
				intent.putExtra("fanuid", mList.get(position).uid);
				mContext.startActivity(intent);
			}

		});
		GitHubImageLoader.Instace(mContext).setCirclePic(mList.get(position).uid,
				viewHolder.userImageView);
		return convertView;
	}

	class ViewHolder {
		TextView username;
		TextView time;
		TextView message;
		ImageView userImageView;
		TextView replyCommentNum;
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
			}
		}
	};
}
