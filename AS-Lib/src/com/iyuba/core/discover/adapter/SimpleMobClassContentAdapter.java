package com.iyuba.core.discover.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.core.common.sqlite.mode.mob.CourseContent;
import com.iyuba.core.R;

public class SimpleMobClassContentAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CourseContent> mList = new ArrayList<CourseContent>();
	
	private ViewHolder viewHolder;
	
	public SimpleMobClassContentAdapter(Context context, ArrayList<CourseContent> list) {
		mContext = context;
		mList = list;
		
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
		
		final CourseContent cc = mList.get(position);
		
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater)mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_common_mobclassactivity, null);
			viewHolder = new ViewHolder();
			
			findView(convertView);
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.titleName.setText(cc.titleName);
		
		viewHolder.rlBuy.setVisibility(View.VISIBLE);
		viewHolder.ivLock.setVisibility(View.VISIBLE);
		
		//控制购买标志是否显示，当前课程包中是否有购买记录；每个包中cost为0的课程可免费使用
		if (mList.get(position).IsFree == true || mList.get(position).cost == 0.0) {
			viewHolder.rlBuy.setVisibility(View.INVISIBLE);
			viewHolder.ivLock.setVisibility(View.GONE);
		}
		//控制播放标志
		if (viewHolder.rlBuy.getVisibility() == View.INVISIBLE) {
			Log.d("判断播放标志是否显示","已经购买课程，显示播放图标");
			viewHolder.rlPlay.setVisibility(View.VISIBLE);
		} else {
			Log.d("判断播放标志是否显示","未经购买课程，不显示播放图标");
			viewHolder.rlPlay.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	
	
	public void findView(View convertView) {
		// 获取testlib_in里面的控件
		viewHolder.titleName = (TextView) convertView.findViewById(R.id.courseCotent_title);
		viewHolder.rlBuy = (RelativeLayout)convertView.findViewById(R.id.RL_courseContent_buy_state_pic);
		viewHolder.rlPlay = (RelativeLayout)convertView.findViewById(R.id.RL_courseContent_play_state_pic);
		viewHolder.ivLock = (ImageView)convertView.findViewById(R.id.courseContent_lock_pic);
		viewHolder.rlItem = (RelativeLayout)convertView.findViewById(R.id.RL_courseContent_list);
	}

	public class ViewHolder {
		TextView titleName;
		RelativeLayout rlBuy;
		RelativeLayout rlPlay;
		ImageView ivLock;
		RelativeLayout rlItem;
	}
}
