package com.iyuba.core.microclass.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.iyumooc.microclass.bean.CourseTypeListBean;
import com.iyuba.core.microclass.sqlite.mode.CoursePackType;
import com.iyuba.core.R;

public class MobClassListTypeAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CoursePackType> mList = new ArrayList<>();
	public boolean modeDelete = false;
	private String allPicUrl;
	
	public MobClassListTypeAdapter(Context context){
		mContext = context;
	}
	
	public MobClassListTypeAdapter(Context context, ArrayList<CoursePackType> list) {
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
	
	public void addList(List<CoursePackType> courseList){
		mList.addAll(courseList);
		
	}
	public void clearList(){
		mList.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		final CoursePackType cpt = mList.get(position);
		ViewHolder viewHolder;
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater)mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.microclass_item_common_mobclasstypelist, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.coursePackType_title);
			viewHolder.pic = (ImageView) convertView.findViewById(R.id.coursePackType_pic);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.title.setText(cpt.name);

		allPicUrl = Constant.MOB_CLASS_PACK_TYPE_IMAGE+cpt.id+"b.png";
		//使用ImageLoaderConfiguration初始�?
		GitHubImageLoader.Instace(mContext).setPic(allPicUrl, viewHolder.pic, R.drawable.nearby_no_icon);
		
		return convertView;
	}

	public class ViewHolder {
		TextView title;
		ImageView pic;
	}
}