package com.iyuba.core.discover.adapter;

/**
 * 移动课堂列表适配器
 * 
 * @author 陈彤
 * @version 1.0
 */
import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.core.common.sqlite.mode.mob.CoursePack;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.R;

public class SimpleMobClassListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CoursePack> mList = new ArrayList<CoursePack>();
	public boolean modeDelete = false;
	private String allPicUrl;

	public SimpleMobClassListAdapter(Context context, ArrayList<CoursePack> list) {
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

	public void setList(ArrayList<CoursePack> courseList) {
		mList=courseList;

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final CoursePack cp = mList.get(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.item_common_mobclasslist, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView
					.findViewById(R.id.coursePack_title);
			viewHolder.tvclassNum = (TextView) convertView
					.findViewById(R.id.tv_courseNum1);
			viewHolder.courseNum = (ImageView) convertView
					.findViewById(R.id.coursePack_courseNum);
			viewHolder.content = (TextView) convertView
					.findViewById(R.id.coursePackDesc_content);
			viewHolder.pic = (ImageView) convertView
					.findViewById(R.id.coursePack_pic);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(cp.name);
		viewHolder.content.setText(cp.desc);
		viewHolder.tvclassNum.setText(cp.classNum + "");
		allPicUrl = "http://static1."+com.iyuba.core.util.Constant.IYBHttpHead+"/PClass/packIcon/" + cp.picUrl
				+ ".jpg";
		GitHubImageLoader.Instace(mContext).setPic(allPicUrl, viewHolder.pic,
				R.drawable.nearby_no_icon);
		return convertView;
	}

	public class ViewHolder {
		TextView title;
		TextView tvclassNum;
		ImageView courseNum;
		TextView content;
		ImageView pic;
	}
}
