package com.iyuba.core.microclass.adapter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.configation.Constant;
import com.iyuba.core.common.thread.GitHubImageLoader;
import com.iyuba.core.iyumooc.microclass.bean.CoursePackListBean;
import com.iyuba.core.microclass.sqlite.mode.CoursePack;
import com.iyuba.core.R;

public class MobClassListAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<CoursePack> mList = new ArrayList<>();
	public boolean modeDelete = false;
	private String allPicUrl;
	
	public MobClassListAdapter(Context context){
		mContext = context;
	}
	
	public MobClassListAdapter(Context context, ArrayList<CoursePack> list) {
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
	
	public void addList(List<CoursePack> courseList){
		mList.addAll(courseList);
	}
	public void clearList(){
		mList.clear();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		final CoursePack cp = mList.get(position);
		ViewHolder viewHolder;
		if(convertView == null){
			LayoutInflater vi = (LayoutInflater)mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = vi.inflate(R.layout.lib_microclass_item_common_mobclasslist, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.coursePack_title);
			viewHolder.tvclassNum = (TextView) convertView.findViewById(R.id.tv_courseNum1);
			viewHolder.courseNum = (ImageView) convertView.findViewById(R.id.coursePack_courseNum);
			viewHolder.content = (TextView) convertView.findViewById(R.id.coursePackDesc_content);
			viewHolder.pic = (ImageView) convertView.findViewById(R.id.coursePack_pic);
			
			viewHolder.oldPrice = (TextView) convertView.findViewById(R.id.tv_coursePack_oldprice);
			viewHolder.newPrice = (TextView) convertView.findViewById(R.id.tv_coursePack_newprice);
			viewHolder.viewCount = (TextView) convertView.findViewById(R.id.tv_coursePack_viewCount);
			
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.oldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);

		viewHolder.title.setText(cp.name);
		viewHolder.content.setText(cp.desc);
		viewHolder.tvclassNum.setText(cp.classNum+"");

		viewHolder.oldPrice.setText("原:"+cp.realprice);
		viewHolder.newPrice.setText("现:"+cp.price+"爱语币");
		
		DecimalFormat df=new java.text.DecimalFormat("#.0");

		if(cp.viewCount>10000){
			viewHolder.viewCount.setText(df.format((float)(cp.viewCount/10000.0))+"万");
		}else{
			viewHolder.viewCount.setText(cp.viewCount+"");
		}

		allPicUrl = Constant.MOB_CLASS_PACK_IMAGE+cp.picUrl+".jpg";
		//使用ImageLoaderConfiguration初始�?
		GitHubImageLoader.Instace(mContext).setPic(allPicUrl, viewHolder.pic, R.drawable.nearby_no_icon);
		
		//GitHubImageLoader.Instace(mContext).setPic(allPicUrl, viewHolder.pic,R.drawable.nearby_no_icon);
		return convertView;
	}

	public class ViewHolder {
		TextView title;
		TextView tvclassNum;
		ImageView courseNum;
		TextView content;
		ImageView pic;
		
		TextView oldPrice;
		TextView newPrice;
		TextView viewCount;
	}
}