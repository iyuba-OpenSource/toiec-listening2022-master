package com.iyuba.core.discover.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.sqlite.mode.CommonNews;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.R;

/**
 * 简版新闻列表适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
public class SimpleNewsAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CommonNews> mList = new ArrayList<CommonNews>();
	public boolean modeDelete = false;

	public SimpleNewsAdapter(Context context, ArrayList<CommonNews> list) {
		mContext = context;
		mList = list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mList.size();
	}

	@Override
	public CommonNews getItem(int arg0) {
		// TODO Auto-generated method stub
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		final CommonNews news = mList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_common_news, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.artical_title);
			viewHolder.time = (TextView) convertView
					.findViewById(R.id.artical_time);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.artical_content);
			viewHolder.pic = (ImageView) convertView
					.findViewById(R.id.news_pic);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(news.title);
		viewHolder.time.setText(news.time);
		viewHolder.content.setText(news.content);
		GitHubImageLoader.Instace(mContext).setPic(news.picUrl, viewHolder.pic,
				R.drawable.nearby_no_icon);
		return convertView;
	}

	public class ViewHolder {
		TextView title;
		TextView time;
		TextView content;
		ImageView pic;
	}
}
