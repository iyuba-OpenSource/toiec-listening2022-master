
package com.iyuba.core.discover.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.sqlite.db.Emotion;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.common.util.Expression;
import com.iyuba.core.common.util.ReadBitmap;
import com.iyuba.core.me.sqlite.mode.FindFriends;
import com.iyuba.core.R;

/**
 * 找朋友适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class FindFriendsListAdapter extends BaseAdapter {
	private Context mContext;
	public ArrayList<FindFriends> mList = new ArrayList<FindFriends>();
	private ViewHolder viewHolder;
	private int type;

	/**
	 * @param mContext
	 */
	public FindFriendsListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param mContext
	 * @param mList
	 */
	public FindFriendsListAdapter(Context mContext,
			ArrayList<FindFriends> mList, int type) {
		this.mContext = mContext;
		this.mList = mList;
		this.type = type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public FindFriends getItem(int position) {
		// TODO Auto-generated method stub
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void setData(ArrayList<FindFriends> list, int type) {
		mList = list;
		this.type = type;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 无convertView，需要new出各个控件
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.item_findfriends,
					null);
			viewHolder = new ViewHolder();
			viewHolder.pic = (ImageView) convertView.findViewById(R.id.pic);
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.username);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.lastLoginTime = (TextView) convertView
					.findViewById(R.id.last_login_time);
			viewHolder.gender = (ImageView) convertView
					.findViewById(R.id.gender);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.username.setText(mList.get(position).userName);
		if (mList.get(position).doing != null) {
			String zhengze = "image[0-9]{2}|image[0-9]";
			Emotion emotion = new Emotion();
			mList.get(position).doing = emotion
					.replace(mList.get(position).doing);
			try {
				SpannableString spannableString = Expression
						.getExpressionString(mContext,
								mList.get(position).doing, zhengze);
				viewHolder.content.setText(spannableString);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} else {
			viewHolder.content.setText(R.string.social_default_state);
		}
		if (mList.get(position).gender != null) {
			if (mList.get(position).gender.equals("2")) {
				viewHolder.gender.setImageBitmap(ReadBitmap.readBitmap(
						mContext, R.drawable.user_info_female));
			} else {
				viewHolder.gender.setImageBitmap(ReadBitmap.readBitmap(
						mContext, R.drawable.user_info_male));
			}
		} else {
			viewHolder.gender.setImageBitmap(ReadBitmap.readBitmap(mContext,
					R.drawable.user_info_male));
		}
		GitHubImageLoader.Instace(mContext).setCirclePic(mList.get(position).userid,
				viewHolder.pic);
		switch (type) {
			case 0:// 公共账号
				viewHolder.lastLoginTime.setVisibility(View.INVISIBLE);
				break;
			case 1:// 周边的人
				int distance = (int) mList.get(position).distance;
				viewHolder.content.setText(distance
						+ mContext.getString(R.string.social_distance));
				
				viewHolder.lastLoginTime.setVisibility(View.VISIBLE);
				
				viewHolder.lastLoginTime.setText(formatTime(1000*Long.parseLong(mList.get(position).signTime)));
				
				break;
				
			case 2:// 共同应用
				viewHolder.content.setText(mContext
						.getString(R.string.social_sameapp)
						+ mList.get(position).appName);
				viewHolder.lastLoginTime.setVisibility(View.INVISIBLE);
				break;
			case 3:// 好友推荐
				viewHolder.lastLoginTime.setVisibility(View.INVISIBLE);
				break;
			default:
				viewHolder.lastLoginTime.setVisibility(View.INVISIBLE);
				break;
		}
		return convertView;
	}
	
	private String formatTime(long time) {
		Date date = new Date(time);
		Date date2 = new Date(System.currentTimeMillis());
		Calendar calendar = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();

		calendar.setTime(date);
		calendar2.setTime(date2);

		if (calendar2.get(Calendar.DAY_OF_YEAR)
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

	class ViewHolder {
		protected ImageView pic;
		protected TextView username;
		protected TextView content;
		protected ImageView gender;
		protected TextView lastLoginTime;
	}
}
