
package com.iyuba.core.me.adapter;
/**
 * 心情适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
import java.util.ArrayList;

import android.content.Context;
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
import com.iyuba.core.me.sqlite.mode.DoingsInfo;
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.R;

public class DoingsListAdapter extends BaseAdapter {
	private Context mContext;
	public ArrayList<DoingsInfo> mList = new ArrayList<DoingsInfo>();
	private ViewHolder viewHolder;

	/**
	 * @param mContext
	 * @param mList
	 */
	public DoingsListAdapter(Context mContext, ArrayList<DoingsInfo> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	/**
	 * @param mContext
	 */
	public DoingsListAdapter(Context mContext) {
		this.mContext = mContext;
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

	public void setData(ArrayList<DoingsInfo> list) {
		mList = list;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final DoingsInfo curDoings = mList.get(position);
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_doings, null);
			viewHolder = new ViewHolder();
			viewHolder.replyNum = (TextView) convertView
					.findViewById(R.id.doingslist_replyNum);
			viewHolder.message = (TextView) convertView
					.findViewById(R.id.doingslist_message);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.doingslist_time);
			viewHolder.userImageView = (ImageView) convertView
					.findViewById(R.id.doingslist_userPortrait);
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.doingslist_username);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.replyNum.setText(curDoings.replynum);
		String zhengze = "image[0-9]{2}|image[0-9]";
		Emotion emotion = new Emotion();
		curDoings.message = emotion.replace(curDoings.message);
		try {
			SpannableString spannableString = Expression.getExpressionString(
					mContext, curDoings.message, zhengze);
			viewHolder.message.setText(spannableString);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		viewHolder.username.setText(curDoings.username);
		long time = Long.parseLong(curDoings.dateline) * 1000;
		viewHolder.time.setText(DateFormat.format("yyyy-MM-dd kk:mm", time));
		GitHubImageLoader.Instace(mContext).setCirclePic(mList.get(position).uid,
				viewHolder.userImageView);
		return convertView;
	}

	class ViewHolder {
		TextView username;
		TextView time;
		TextView replyNum;
		TextView message;
		ImageView userImageView;
	}
}
