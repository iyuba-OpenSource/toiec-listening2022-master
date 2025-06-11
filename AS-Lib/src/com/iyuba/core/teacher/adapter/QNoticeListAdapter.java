package com.iyuba.core.teacher.adapter;

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
import com.iyuba.core.me.sqlite.mode.Emotion;
import com.iyuba.core.me.sqlite.mode.MessageLetter;
import com.iyuba.core.teacher.sqlite.mode.Notice;
import com.iyuba.core.R;

/**
 * 私信列表适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class QNoticeListAdapter extends BaseAdapter {

	private Context mContext;
	private ArrayList<Notice> mList = new ArrayList<Notice>();
	private ViewHolder viewHolder;

	/**
	 * @param mContext
	 */
	public QNoticeListAdapter(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * @param mContext
	 * @param mList
	 */
	public QNoticeListAdapter(Context mContext, ArrayList<Notice> mList) {
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

	public void addList(ArrayList<Notice> List) {
		mList = List;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.item_qnotice, null);
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
			
			
			
			
			viewHolder.messageletter_username.setText(mList.get(position).author);
			viewHolder.messageletter_lastmessage.setText(mList.get(position).note);
			viewHolder.messageletter_dateline.setText(DateFormat.format(
					"yyyy-MM-dd kk:mm",
					Long.parseLong(mList.get(position).time) * 1000));
			GitHubImageLoader.Instace(mContext)
					.setCirclePic(mList.get(position).authorid,
							viewHolder.messageletter_portrait);
			if (mList.get(position).isnew.equals("1")) {// 未读
				viewHolder.isNew.setVisibility(View.GONE);
			} else if (mList.get(position).isnew.equals("0")) {// 已读
				viewHolder.isNew.setVisibility(View.GONE);
			}
			return convertView;
			
			
			
			
			
			
			
			
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
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
