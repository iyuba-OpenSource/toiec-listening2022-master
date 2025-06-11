package com.iyuba.core.me.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.me.sqlite.mode.MessageLetter;
import com.iyuba.core.R;

/**
 * 私信列表适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class MessageListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<MessageLetter> mList = new ArrayList<MessageLetter>();
	private ViewHolder viewHolder;

	/**
	 * @param mContext
	 */
	public MessageListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param mContext
	 * @param mList
	 */
	public MessageListAdapter(Context mContext, ArrayList<MessageLetter> mList) {
		this.mContext = mContext;
		this.mList = mList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub

		if (position == 0) {
			return 0;
		} else {
			return mList.get(position);
		}
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	public void addList(ArrayList<MessageLetter> List) {
		mList = List;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.item_message, null);
			viewHolder = new ViewHolder();
			viewHolder.messageletter_portrait = (ImageView) convertView
					.findViewById(R.id.messageletter_portrait);
			viewHolder.messageletter_username = (TextView) convertView
					.findViewById(R.id.messageletter_username);
			viewHolder.messageletter_pmnum = (TextView) convertView
					.findViewById(R.id.messageletter_pmnum);
			viewHolder.messageletter_lastmessage = (TextView) convertView
					.findViewById(R.id.messageletter_lastmessage);
			viewHolder.messageletter_dateline = (TextView) convertView
					.findViewById(R.id.messageletter_dateline);
			viewHolder.isNew = (ImageView) convertView.findViewById(R.id.isNew);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// 设置资源
		viewHolder.messageletter_lastmessage
				.setText(mList.get(position).lastmessage);
		viewHolder.messageletter_username.setText(mList.get(position).name);
		viewHolder.messageletter_dateline.setText(DateFormat.format(
				"yyyy-MM-dd kk:mm",
				Long.parseLong(mList.get(position).dateline) * 1000));
		viewHolder.messageletter_pmnum.setText("[ " + mList.get(position).pmnum
				+ " ]");
		GitHubImageLoader.Instace(mContext)
				.setCirclePic(mList.get(position).friendid,
						viewHolder.messageletter_portrait);
		if (mList.get(position).isnew.equals("1")) {// 未读
			viewHolder.isNew.setVisibility(View.VISIBLE);
		} else if (mList.get(position).isnew.equals("0")) {// 已读
			viewHolder.isNew.setVisibility(View.GONE);
		}
		return convertView;
	}

	public class ViewHolder {
		ImageView messageletter_portrait;
		TextView messageletter_username;
		TextView messageletter_pmnum;
		TextView messageletter_dateline;
		TextView messageletter_lastmessage;
		public ImageView isNew;
	}
}
