package com.iyuba.core.me.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.me.sqlite.mode.Attention;
import com.iyuba.core.R;

/**
 * 关注适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class AttentionListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Attention> mList = new ArrayList<Attention>();
	private ViewHolder viewHolder;

	/**
	 * @param mContext
	 */
	public AttentionListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param mContext
	 * @param mList
	 */
	public AttentionListAdapter(Context mContext, ArrayList<Attention> mList) {
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

	public void setData(ArrayList<Attention> List) {
		this.mList = List;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final Attention curAttention = mList.get(position);
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.item_fans, null);
			viewHolder = new ViewHolder();
			viewHolder = new ViewHolder();
			viewHolder.username = (TextView) convertView
					.findViewById(R.id.username);
			viewHolder.message = (TextView) convertView
					.findViewById(R.id.content);
			viewHolder.userImageView = (ImageView) convertView
					.findViewById(R.id.pic);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.username.setText(curAttention.fusername);
		viewHolder.message.setText(curAttention.doing);
		GitHubImageLoader.Instace(mContext).setCirclePic(curAttention.followuid,
				viewHolder.userImageView);
		return convertView;
	}

	public class ViewHolder {
		TextView username;
		TextView message;// 当前状态
		ImageView userImageView;
	}
}
